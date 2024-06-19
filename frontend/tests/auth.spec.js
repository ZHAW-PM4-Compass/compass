const { test, expect } = require('playwright-test-coverage');

test('Framework Verification Test', async ({ page }) => {
  await expect(true).toBeTruthy();
});

test('LandingPage Login Button Test', async ({ page }) => {
  await page.goto('http://localhost:3000/');
  const loginButton = await page.getByRole('button', { name: 'Login' });
  await expect(loginButton).toBeTruthy();
  await loginButton.click();
});