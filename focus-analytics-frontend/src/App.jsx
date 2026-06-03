import "./App.css"
import { Box, Button, Card, CardContent, Chip, Container, Stack, Typography } from "@mui/material"
import { useContext, useEffect, useMemo } from "react"
import { AuthContext } from "react-oauth2-code-pkce"
import { useDispatch } from "react-redux";
import { BrowserRouter as Router, Navigate, Route, Routes } from "react-router"
import { logout, setCredentials } from "./store/authSlice";
import FocusSessionForm from "./components/FocusSessionForm";
import FocusSessionList from "./components/FocusSessionList";
import FocusSessionDetail from "./components/FocusSessionDetail";
import ProductivityInsightsPanel from "./components/ProductivityInsightsPanel";
import { clearAuthContext, setAuthContext } from "./services/api";

const FocusSessionsPage = () => {
  return (
    <Container maxWidth="xl" sx={{ py: { xs: 2, md: 4 } }}>
      <Box className="hero-panel">
        <Stack spacing={2}>
          <Chip label="AI Focus Analytics Platform" className="hero-chip" />
          <Typography variant="h3" component="h1" className="hero-title">
            Focus sessions, surfaced as a working dashboard.
          </Typography>
          <Typography variant="body1" className="hero-copy">
            Track session quality, review AI insights, and keep your next work block grounded in real usage data.
          </Typography>
        </Stack>
      </Box>

      <Box className="dashboard-grid">
        <Stack spacing={3}>
          <ProductivityInsightsPanel />
          <Card className="surface-card">
            <CardContent>
              <Typography variant="overline" className="section-label">
                Log Session
              </Typography>
              <Typography variant="h5" className="section-title" gutterBottom>
                Capture the work block you just finished.
              </Typography>
              <FocusSessionForm onSessionAdded={() => window.location.reload()} />
            </CardContent>
          </Card>
        </Stack>

        <Card className="surface-card">
          <CardContent>
            <Typography variant="overline" className="section-label">
              Recent Sessions
            </Typography>
            <Typography variant="h5" className="section-title" gutterBottom>
              Review the latest focus history.
            </Typography>
            <FocusSessionList />
          </CardContent>
        </Card>
      </Box>
    </Container>
  );
}

function App() {
  const { token, tokenData, logIn, logOut } 
      = useContext(AuthContext);
  const dispatch = useDispatch();

  useEffect(() => {
    if (token) {
      dispatch(setCredentials({token, user: tokenData}));
      setAuthContext({ token, userId: tokenData?.sub });
    } else {
      clearAuthContext();
    }
  }, [token, tokenData, dispatch]);

  const userName = useMemo(() => {
    return tokenData?.given_name || tokenData?.preferred_username || tokenData?.email || "Operator";
  }, [tokenData]);

  return (
    <Router>
      {!token ? (
        <Box className="auth-shell">
          <Card className="auth-card">
            <CardContent>
              <Chip label="Welcome" className="hero-chip" />
              <Typography variant="h4" component="h1" className="auth-title" gutterBottom>
                AI Focus Analytics Platform
              </Typography>
              <Typography variant="body1" className="auth-copy" paragraph>
                Sign in to log focus sessions, inspect productivity trends, and read AI-generated next-step insights.
              </Typography>
              <Button variant="contained" className="primary-action" onClick={() => logIn()}>
                Log in
              </Button>
            </CardContent>
          </Card>
        </Box>
      ) : (
        <Box className="app-shell">
          <Box className="topbar">
            <Box>
              <Typography variant="overline" className="topbar-kicker">
                Productivity dashboard
              </Typography>
              <Typography variant="h6" className="topbar-title">
                Hello, {userName}
              </Typography>
            </Box>
            <Button
              variant="outlined"
              className="ghost-action"
              onClick={() => {
                dispatch(logout());
                clearAuthContext();
                logOut();
              }}
            >
              Log out
            </Button>
          </Box>

          <Routes>
            <Route path="/focus-sessions" element={<FocusSessionsPage />}/>
            <Route path="/focus-sessions/:id" element={<FocusSessionDetail />}/>
            <Route path="/" element={<Navigate to="/focus-sessions" replace/>}/>
          </Routes>
        </Box>
      )}
    </Router>
  )
}

export default App
