import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router';
import { Card, CardContent, Chip, Grid, Stack, Typography } from '@mui/material';
import { getActivities } from '../services/api';

const FocusSessionList = () => {

  const [activities, setActivities] = useState([]);
  const navigate = useNavigate();

  const fetchActivities = async () => {
    try {
      const response = await getActivities();
      setActivities(response.data);
    } catch (error) {
      console.error(error);
    }
  }

  useEffect(() => {
    fetchActivities();
  }, []);

  return (
    <Grid container spacing={2}>
      {activities.map((activity) => (
        <Grid item xs={12} md={6} key={activity.id}>
          <Card
            className="session-card"
            onClick={() => navigate(`/focus-sessions/${activity.id}`)}
          >
            <CardContent>
              <Stack spacing={1.25}>
                <Chip size="small" label={activity.type} className="session-chip" />
                <Typography variant='h6' className="session-title">
                  {activity.additionalMetrics?.notes || 'Untitled focus block'}
                </Typography>
                <Typography className="session-meta">
                  Duration: {activity.duration} minutes
                </Typography>
                <Typography className="session-meta">
                  Focus Score: {activity.focusScore}
                </Typography>
              </Stack>
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  )
}

export default FocusSessionList
