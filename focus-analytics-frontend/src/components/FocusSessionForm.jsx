import { Box, Button, FormControl, InputLabel, MenuItem, Select, Stack, TextField } from '@mui/material'
import React, { useState } from 'react'
import { addActivity } from '../services/api';

const FocusSessionForm = ({ onSessionAdded }) => {

  const [activity, setActivity] = useState({
    type: "OTHER", duration: '', focusScore: '',
    additionalMetrics: { notes: "" }
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await addActivity(activity);
      onSessionAdded();
      setActivity({
        type: "OTHER",
        duration: '',
        focusScore: '',
        additionalMetrics: { notes: "" }
      });
    } catch (error) {
      console.error(error);
    }
  }

  return (
    <Box component="form" onSubmit={handleSubmit}>
      <Stack spacing={2}>
        <FormControl fullWidth>
          <InputLabel>Session Type</InputLabel>
          <Select
            value={activity.type}
            label="Session Type"
            onChange={(e) => setActivity({...activity, type: e.target.value})}>
            <MenuItem value="OFFICE_WORK">OFFICE_WORK</MenuItem>
            <MenuItem value="CASUAL_CODING">CASUAL_CODING</MenuItem>
            <MenuItem value="GAMING">GAMING</MenuItem>
            <MenuItem value="OTHER">OTHER</MenuItem>
          </Select>
        </FormControl>
        <TextField
          fullWidth
          label="Duration (Minutes)"
          type="number"
          value={activity.duration}
          onChange={(e) => setActivity({...activity, duration: Number(e.target.value)})}
        />
        <TextField
          fullWidth
          label="Focus Score"
          type="number"
          value={activity.focusScore}
          onChange={(e) => setActivity({...activity, focusScore: Number(e.target.value)})}
        />
        <TextField
          fullWidth
          label="Task Notes"
          placeholder="What were you working on?"
          value={activity.additionalMetrics.notes}
          onChange={(e) => setActivity({
            ...activity,
            additionalMetrics: { ...activity.additionalMetrics, notes: e.target.value }
          })}
        />
        <Button type="submit" variant="contained" className="primary-action">
          Log Session
        </Button>
      </Stack>
    </Box>
  )
}

export default FocusSessionForm
