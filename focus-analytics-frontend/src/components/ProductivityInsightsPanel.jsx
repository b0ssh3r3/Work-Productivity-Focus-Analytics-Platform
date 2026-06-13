import { Card, CardContent, Chip, Grid, Stack, Typography } from "@mui/material"
import { useEffect, useMemo, useState } from "react"
import { getActivities } from "../services/api"

const distractionWords = ["distract", "notification", "interrupt", "phone", "email", "meeting", "slack"]

const extractTags = (notes = "") => {
  const matches = notes.match(/#[\w-]+/g) || []
  return matches.map((tag) => tag.replace("#", ""))
}

const getTaskLabel = (notes = "") => {
  const cleaned = notes.replace(/#[\w-]+/g, "").trim()
  return cleaned || "Unlabeled task"
}

const ProductivityInsightsPanel = () => {
  const [activities, setActivities] = useState([])

  useEffect(() => {
    const load = async () => {
      try {
        const response = await getActivities()
        setActivities(response.data || [])
      } catch (error) {
        console.error(error)
      }
    }

    load()
  }, [])

  const derived = useMemo(() => {
    const now = new Date()
    const weekAgo = new Date(now)
    weekAgo.setDate(now.getDate() - 7)

    const weekly = activities.filter((activity) => {
      if (!activity.createdAt) return true
      return new Date(activity.createdAt) >= weekAgo
    })

    const totalDuration = weekly.reduce((sum, activity) => sum + (Number(activity.duration) || 0), 0)
    const totalScore = weekly.reduce((sum, activity) => sum + (Number(activity.focusScore) || 0), 0)
    const averageScore = weekly.length ? Math.round(totalScore / weekly.length) : 0
    const tags = [...new Set(weekly.flatMap((activity) => extractTags(activity.additionalMetrics?.notes || "")))]
    const distractionCount = weekly.filter((activity) => {
      const notes = (activity.additionalMetrics?.notes || "").toLowerCase()
      return distractionWords.some((word) => notes.includes(word))
    }).length

    const byHour = new Map()
    weekly.forEach((activity) => {
      if (!activity.startTime) return
      const hour = new Date(activity.startTime).getHours()
      const bucket = byHour.get(hour) || { count: 0, score: 0 }
      bucket.count += 1
      bucket.score += Number(activity.focusScore) || 0
      byHour.set(hour, bucket)
    })

    let bestHour = null
    let bestHourScore = -1
    for (const [hour, bucket] of byHour.entries()) {
      const avg = bucket.score / bucket.count
      if (avg > bestHourScore) {
        bestHourScore = avg
        bestHour = hour
      }
    }

    const ranked = [...weekly].sort((a, b) => (Number(b.focusScore) || 0) - (Number(a.focusScore) || 0))
    const nextDayPlan = ranked.slice(0, 3).map((activity) => getTaskLabel(activity.additionalMetrics?.notes || ""))

    return {
      sessionCount: weekly.length,
      totalDuration,
      averageScore,
      tags,
      distractionCount,
      bestHour,
      nextDayPlan
    }
  }, [activities])

  return (
    <Card className="surface-card">
      <CardContent>
        <Stack spacing={2}>
          <Typography variant="overline" className="section-label">
            Productivity intelligence
          </Typography>
          <Typography variant="h5" className="section-title">
            Weekly productivity summary
          </Typography>

          <Grid container spacing={2}>
            <Grid item xs={12} md={3}>
              <Typography className="metric-label">Sessions</Typography>
              <Typography variant="h4" className="metric-value">{derived.sessionCount}</Typography>
            </Grid>
            <Grid item xs={12} md={3}>
              <Typography className="metric-label">Minutes</Typography>
              <Typography variant="h4" className="metric-value">{derived.totalDuration}</Typography>
            </Grid>
            <Grid item xs={12} md={3}>
              <Typography className="metric-label">Avg. score</Typography>
              <Typography variant="h4" className="metric-value">{derived.averageScore}</Typography>
            </Grid>
            <Grid item xs={12} md={3}>
              <Typography className="metric-label">Distraction flags</Typography>
              <Typography variant="h4" className="metric-value">{derived.distractionCount}</Typography>
            </Grid>
          </Grid>

          <Stack spacing={1}>
            <Typography variant="h6">Project / task tags</Typography>
            <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
              {derived.tags.length > 0 ? derived.tags.map((tag) => (
                <Chip key={tag} label={`#${tag}`} className="session-chip" />
              )) : (
                <Typography className="session-meta">Add tags inside session notes using `#tag`.</Typography>
              )}
            </Stack>
          </Stack>

          <Stack spacing={1}>
            <Typography variant="h6">Best working hours</Typography>
            <Typography className="detail-summary">
              {derived.bestHour === null
                ? "Not enough start-time data yet."
                : `Your strongest average focus appears around ${derived.bestHour}:00.`}
            </Typography>
          </Stack>

          <Stack spacing={1}>
            <Typography variant="h6">Next-day planning</Typography>
            {derived.nextDayPlan.length > 0 ? (
              derived.nextDayPlan.map((item, index) => (
                <Typography key={index} className="detail-list-item">
                  - Schedule a follow-up block for {item}
                </Typography>
              ))
            ) : (
              <Typography className="detail-summary">
                Log a few sessions and the planner will surface your next best priorities.
              </Typography>
            )}
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  )
}

export default ProductivityInsightsPanel
