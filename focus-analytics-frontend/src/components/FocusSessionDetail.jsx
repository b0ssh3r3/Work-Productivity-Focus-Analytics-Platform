import { Box, Card, CardContent, Chip, Divider, Stack, Typography } from '@mui/material'
import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router';
import { getActivityDetail } from '../services/api';

const FocusSessionDetail = () => {
  const { id } = useParams();
  const [activity, setActivity] = useState(null);
  const [recommendation, setRecommendation] = useState(null);

  useEffect(() => {
    const fetchActivityDetail = async () => {
      try {
        const response = await getActivityDetail(id);
        setActivity(response.data);
        setRecommendation(response.data.recommendation || response.data.insight);
      } catch (error) {
        console.error(error);
      }
    }

    fetchActivityDetail();
  }, [id]);

  if (!activity) {
    return <Typography className="detail-loading">Loading...</Typography>
  }

  return (
    <Box className="detail-shell">
      <Card className="surface-card">
        <CardContent>
          <Stack spacing={1.5}>
            <Chip label={activity.type} className="session-chip" />
            <Typography variant="h4" className="detail-title">
              Focus Session Details
            </Typography>
            <Typography className="session-meta">
              Duration: {activity.duration} minutes
            </Typography>
            <Typography className="session-meta">
              Focus Score: {activity.focusScore}
            </Typography>
            <Typography className="session-meta">
              Date: {new Date(activity.createdAt).toLocaleString()}
            </Typography>
            {activity.additionalMetrics?.notes && (
              <Typography className="detail-notes">
                Notes: {activity.additionalMetrics.notes}
              </Typography>
            )}
          </Stack>
        </CardContent>
      </Card>

      {recommendation && (
        <Card className="surface-card">
          <CardContent>
            <Stack spacing={2}>
              <Typography variant="h5" className="section-title">
                AI Insight
              </Typography>
              <Typography className="detail-summary">
                {recommendation}
              </Typography>

              <Divider />

              <Box>
                <Typography variant="h6" gutterBottom>
                  Improvement Areas
                </Typography>
                <Stack spacing={1}>
                  {(activity?.improvements || []).map((improvement, index) => (
                    <Typography key={index} className="detail-list-item">
                      - {improvement}
                    </Typography>
                  ))}
                </Stack>
              </Box>

              <Divider />

              <Box>
                <Typography variant="h6" gutterBottom>
                  Next Steps
                </Typography>
                <Stack spacing={1}>
                  {(activity?.suggestions || []).map((suggestion, index) => (
                    <Typography key={index} className="detail-list-item">
                      - {suggestion}
                    </Typography>
                  ))}
                </Stack>
              </Box>

              <Divider />

              <Box>
                <Typography variant="h6" gutterBottom>
                  Focus Guidelines
                </Typography>
                <Stack spacing={1}>
                  {(activity?.safety || []).map((safety, index) => (
                    <Typography key={index} className="detail-list-item">
                      - {safety}
                    </Typography>
                  ))}
                </Stack>
              </Box>
            </Stack>
          </CardContent>
        </Card>
      )}
    </Box>
  )
}

export default FocusSessionDetail
