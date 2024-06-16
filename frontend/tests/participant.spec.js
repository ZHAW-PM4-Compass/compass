const { test, expect } = require('playwright-test-coverage');

test.beforeEach('login charles leclerc (participant)', async ({ page }) => {
  await page.goto('http://localhost:3000/');
  await page.getByRole('button', { name: 'Login' }).click();
  await page.getByLabel('Email address*').click();
  await page.getByLabel('Email address*').fill('charles.leclerc@gmail.com');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.getByLabel('Password*').click();
  await page.getByLabel('Password*').fill('Test123$');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.waitForTimeout(3000);
});

test('testing crud for timestamp', async ({ page }) => {
  //create valid timestamp
  await page.goto('http://localhost:3000/working-hours');
  await page.waitForTimeout(3000);
  await page.locator('input[name="date"]').fill('2024-01-01');
  await expect(page.getByRole('cell', { name: 'Keine Daten erfasst' })).toBeVisible();
  await expect(page.getByRole('cell', { name: '0h 0min' })).toBeVisible();
  await page.locator('input[name="startTime"]').click();
  await page.locator('input[name="startTime"]').fill('08:00');
  await page.locator('input[name="endTime"]').click();
  await page.locator('input[name="endTime"]').fill('17:00');
  await page.getByRole('button', { name: 'Erfassen' }).click();
  await expect(page.getByText('Zeiteintrag erstellt')).toBeVisible();
  await expect(page.getByRole('cell', { name: '08:' })).toBeVisible();
  await expect(page.getByRole('cell', { name: '17:' })).toBeVisible();
  await expect(page.getByRole('cell', { name: '9h 0min' }).first()).toBeVisible();
  await expect(page.getByRole('cell', { name: '9h 0min' }).nth(1)).toBeVisible();
  await expect(page.getByRole('row', { name: ':00 17:00 9h 0min' }).getByRole('button').first()).toBeVisible();
  await expect(page.getByRole('row', { name: ':00 17:00 9h 0min' }).getByRole('button').nth(1)).toBeVisible();
  await page.waitForTimeout(3000);

  //create invalid timestamp
  await page.getByRole('button', { name: 'Erfassen' }).click();
  await expect(page.getByText('Zeiteintrag konnte nicht')).toBeVisible();
  await page.locator('input[name="startTime"]').click();
  await page.locator('input[name="startTime"]').fill('10:00');
  await page.locator('input[name="endTime"]').click();
  await page.locator('input[name="endTime"]').fill('12:00');
  await page.getByRole('button', { name: 'Erfassen' }).click();
  await expect(page.getByText('Zeiteintrag konnte nicht')).toBeVisible();
  await page.waitForTimeout(3000);

  //update timestamp
  await page.getByRole('row', { name: ':00 17:00 9h 0min' }).getByRole('button').nth(1).click();
  await expect(page.locator('h2')).toContainText('Zeiteintrag bearbeiten');
  await expect(page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="startTime"]')).toHaveValue('08:00');
  await expect(page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="endTime"]')).toHaveValue('17:00');
  await page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="startTime"]').click();
  await page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="startTime"]').fill('08:15');
  await page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="endTime"]').click();
  await page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="endTime"]').fill('17:30');
  await page.getByRole('button', { name: 'Speichern' }).click();
  await expect(page.locator('tbody')).toContainText('9h 15min');
  await expect(page.locator('tbody')).toContainText('9h 15min');
  await expect(page.locator('tbody')).toContainText('08:15');
  await expect(page.locator('tbody')).toContainText('17:30');
  await page.waitForTimeout(3000);

  //delete timestamp
  await page.getByRole('row', { name: ':15 17:30 9h 15min' }).getByRole('button').first().click();
});

test('testing crud for moods', async ({ page }) => {
  await page.getByText('Stimmung erfassen').click();
  await page.getByRole('textbox').fill('2024-01-01');
  await expect(page.locator('td')).toContainText('Keine Daten erfasst');
  await page.getByRole('button', { name: 'Bewertung erfassen' }).click();
  await expect(page.locator('h2')).toContainText('Bewertung abgeben');
  await expect(page.getByRole('button', { name: 'Speichern' })).toBeVisible();
  await page.locator('form div').filter({ hasText: 'Speichern' }).getByRole('button').click();
  await page.waitForTimeout(500);
  await page.getByRole('button', { name: 'Bestätigen' }).click();
});
