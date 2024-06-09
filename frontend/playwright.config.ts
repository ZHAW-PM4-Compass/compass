import { defineConfig, devices } from '@playwright/test';

const PORT = process.env.PORT || 3000;
const baseURL = `http://localhost:${PORT}`;
process.env.ENVIRONMENT_URL = baseURL;

export default defineConfig({
  testDir: './tests',
  timeout: 60 * 1000,
  fullyParallel: !!process.env.CI,
  forbidOnly: !!process.env.CI,
  retries: 1,
  workers: 1,
  maxFailures: 2,
  reporter: process.env.CI ? [['github'], ['list'], ['html', { outputFolder: 'test-results' }]] : [['list']],

  webServer: {
    command: process.env.CI ? 'npm run dev' : 'npm run dev',
    url: baseURL,
    timeout: 2 * 60 * 1000,
    reuseExistingServer: !process.env.CI,
  },

  use: {
    baseURL,
    trace: 'on-first-retry',
  },

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    ...(process.env.CI
      ? [
          {
            name: 'firefox',
            use: { ...devices['Desktop Firefox'] },
          },
          {
            name: 'webkit',
            use: { ...devices['Desktop Safari'] },
          },
        ]
      : []),
  ],
});
