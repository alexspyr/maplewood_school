import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import { 
  AppBar, 
  Toolbar, 
  Typography, 
  Button, 
  Container, 
  Box,
  CssBaseline,
  ThemeProvider,
  createTheme
} from '@mui/material';
import AdminSchedulePage from './pages/AdminSchedulePage';
import StudentPlanPage from './pages/StudentPlanPage';

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
    background: {
      default: '#f5f5f5',
      paper: '#ffffff',
    },
    text: {
      primary: 'rgba(0, 0, 0, 0.87)',
      secondary: 'rgba(0, 0, 0, 0.6)',
    },
  },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundColor: '#ffffff',
          color: 'rgba(0, 0, 0, 0.87)',
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundColor: '#ffffff',
          color: 'rgba(0, 0, 0, 0.87)',
        },
      },
    },
    MuiTable: {
      styleOverrides: {
        root: {
          backgroundColor: '#ffffff',
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          color: 'rgba(0, 0, 0, 0.87)',
          borderColor: 'rgba(224, 224, 224, 1)',
        },
        head: {
          backgroundColor: '#f5f5f5',
          fontWeight: 600,
        },
      },
    },
    MuiSelect: {
      styleOverrides: {
        root: {
          backgroundColor: '#ffffff',
        },
      },
    },
    MuiInputLabel: {
      styleOverrides: {
        root: {
          color: 'rgba(0, 0, 0, 0.6)',
        },
      },
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
          <AppBar position="static" sx={{ backgroundColor: '#1976d2' }}>
            <Toolbar>
              <Typography variant="h6" component="div" sx={{ flexGrow: 1, color: '#ffffff' }}>
                🏫 Maplewood High School - Scheduling System
              </Typography>
              <Button 
                color="inherit" 
                component={Link} 
                to="/admin/schedule"
                sx={{ color: '#ffffff', mr: 1 }}
              >
                Admin
              </Button>
              <Button 
                color="inherit" 
                component={Link} 
                to="/students/1/plan"
                sx={{ color: '#ffffff' }}
              >
                Student Planner
              </Button>
            </Toolbar>
          </AppBar>

          <Box 
            component="main" 
            sx={{ 
              flexGrow: 1, 
              display: 'flex', 
              flexDirection: 'column',
              backgroundColor: '#f5f5f5',
              py: 4
            }}
          >
            <Container maxWidth="xl" sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
              <Routes>
                <Route path="/admin/schedule" element={<AdminSchedulePage />} />
                <Route path="/students/:studentId/plan" element={<StudentPlanPage />} />
              </Routes>
            </Container>
          </Box>

          <Box 
            component="footer" 
            sx={{ 
              backgroundColor: '#1976d2', 
              color: '#ffffff', 
              py: 2, 
              mt: 'auto',
              textAlign: 'center'
            }}
          >
            <Typography variant="body2">
              © 2024 Maplewood High School - Scheduling System
            </Typography>
          </Box>
        </Box>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
