const { test, expect } = require('playwright-test-coverage');

test.beforeEach('login charles leclerc (participant)', async ({ page }) => {

  // Log only failed network requests (status codes 4xx and 5xx)
  page.on('response', response => {
    if (response.status() >= 400) {
      console.log(`Failed response: ${response.url()} - ${response.status()}`);
      response.text().then(text => {
        console.log(`Response body: ${text}`);
      }).catch(err => {
        console.error(`Error reading response body: ${err}`);
      });
    }
  });

  // log failed requests as well
  page.on('requestfailed', request => {
    console.log(`Failed request: ${request.url()} - ${request.failure().errorText}`);
  });
  
  await page.goto('http://localhost:3000/');
  await page.getByRole('button', { name: 'Login' }).click();
  await page.getByLabel('Email address*').click();
  await page.getByLabel('Email address*').fill('charles.leclerc@gmail.com');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.getByLabel('Password*').click();
  await page.getByLabel('Password*').fill('Test123$');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.waitForTimeout(6000);
});

test('testing crud for timestamp', async ({ page }) => {
  // Create valid timestamp
  await page.goto('http://localhost:3000/working-hours');
  await page.waitForTimeout(6000);
  await page.locator('input[name="date"]').fill('2024-01-01');
  await expect(page.getByRole('cell', { name: 'Keine Daten erfasst' })).toBeVisible();
  await expect(page.getByRole('cell', { name: '0h 0min' })).toBeVisible();
  await page.locator('input[name="startTime"]').click();
  await page.locator('input[name="startTime"]').fill('08:00');
  await page.locator('input[name="endTime"]').click();
  await page.locator('input[name="endTime"]').fill('17:00');
  await page.getByRole('button', { name: 'Erfassen' }).click();
  await page.waitForTimeout(6000); // Add wait to allow data to be saved
  await expect(page.getByRole('cell', { name: '08:' })).toBeVisible({ timeout: 25000 });
  await expect(page.getByRole('cell', { name: '17:' })).toBeVisible({ timeout: 25000 });
  await expect(page.getByRole('cell', { name: '9h 0min' }).first()).toBeVisible({ timeout: 25000 });
  await expect(page.getByRole('cell', { name: '9h 0min' }).nth(1)).toBeVisible({ timeout: 25000 });
  await expect(page.getByRole('row', { name: ':00 17:00 9h 0min' }).getByRole('button').first()).toBeVisible({ timeout: 25000 });
  await expect(page.getByRole('row', { name: ':00 17:00 9h 0min' }).getByRole('button').nth(1)).toBeVisible({ timeout: 25000 });
  await page.waitForTimeout(6000);

  // Create invalid timestamp
  await page.getByRole('button', { name: 'Erfassen' }).click();
  await expect(page.getByText('Zeiteintrag konnte nicht')).toBeVisible({ timeout: 25000 });
  await page.locator('input[name="startTime"]').click();
  await page.locator('input[name="startTime"]').fill('10:00');
  await page.locator('input[name="endTime"]').click();
  await page.locator('input[name="endTime"]').fill('12:00');
  await page.getByRole('button', { name: 'Erfassen' }).click();
  await expect(page.getByText('Zeiteintrag konnte nicht')).toBeVisible({ timeout: 25000 });
  await page.waitForTimeout(6000);

  // Update timestamp
  await page.getByRole('row', { name: ':00 17:00 9h 0min' }).getByRole('button').nth(1).click();
  await expect(page.locator('h2')).toContainText('Zeiteintrag bearbeiten', { timeout: 25000 });
  await expect(page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="startTime"]')).toHaveValue('08:00', { timeout: 25000 });
  await expect(page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="endTime"]')).toHaveValue('17:00', { timeout: 25000 });
  await page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="startTime"]').click();
  await page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="startTime"]').fill('08:15');
  await page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="endTime"]').click();
  await page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="endTime"]').fill('17:30');
  await page.getByRole('button', { name: 'Speichern' }).click();
  await page.waitForTimeout(6000); // Add wait to allow data to be saved
  await expect(page.locator('tbody')).toContainText('9h 15min', { timeout: 25000 });
  await expect(page.locator('tbody')).toContainText('9h 15min', { timeout: 25000 });
  await expect(page.locator('tbody')).toContainText('08:15', { timeout: 25000 });
  await expect(page.locator('tbody')).toContainText('17:30', { timeout: 25000 });
  await page.waitForTimeout(6000);

  // Delete timestamp
  await page.getByRole('row', { name: ':15 17:30 9h 15min' }).getByRole('button').first().click();
});

test('testing crud for moods', async ({ page }) => {
  await page.getByText('Stimmung erfassen').click();
  await page.getByRole('textbox').fill('2024-01-01');
  await expect(page.locator('td')).toContainText('Keine Daten erfasst', { timeout: 25000 });
  await page.getByRole('button', { name: 'Bewertung erfassen' }).click();
  await expect(page.locator('h2')).toContainText('Bewertung abgeben', { timeout: 25000 });
  await expect(page.getByRole('button', { name: 'Speichern' })).toBeVisible({ timeout: 25000 });
  await page.locator('form div').filter({ hasText: 'Speichern' }).getByRole('button').click();
  await page.waitForTimeout(500);
  await page.getByRole('button', { name: 'Bestätigen' }).click();
});
