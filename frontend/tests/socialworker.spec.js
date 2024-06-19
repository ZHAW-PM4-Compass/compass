const { test, expect } = require('playwright-test-coverage');

test.beforeEach('login max verstappen (social worker)', async ({ page }) => {
  await page.waitForTimeout(6000);
  await page.goto('http://localhost:3000/');
  await page.getByRole('button', { name: 'Login' }).click();
  await page.getByLabel('Email address*').click();
  await page.getByLabel('Email address*').fill('max.verstappen@gmail.com');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.getByLabel('Password*').click();
  await page.getByLabel('Password*').fill('Test123$');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.waitForTimeout(6000);
});

test('testing crud for timestamp', async ({ page }) => {
  // Create timestamp
  await page.getByText('Arbeitszeit erfassen').click();
  await page.locator('input[name="date"]').fill('2024-01-01');
  await expect(page.locator('tbody')).toContainText('Keine Daten erfasst', { timeout: 25000 });
  await page.locator('input[name="startTime"]').click();
  await page.locator('input[name="startTime"]').fill('08:00');
  await page.locator('input[name="endTime"]').click();
  await page.locator('input[name="endTime"]').fill('16:00');
  await page.getByRole('button', { name: 'Erfassen' }).click();
  await page.waitForTimeout(6000);
  await expect(page.getByRole('status')).toContainText('Zeiteintrag erstellt', { timeout: 25000 });
  await expect(page.locator('tbody')).toContainText('08:00', { timeout: 25000 });
  await expect(page.locator('tbody')).toContainText('16:00', { timeout: 25000 });
  await expect(page.locator('tbody')).toContainText('8h 0min', { timeout: 25000 });
  await expect(page.locator('tbody')).toContainText('8h 0min', { timeout: 25000 });
  await expect(page.getByRole('row', { name: ':00 16:00 8h 0min' }).getByRole('button').first()).toBeVisible({ timeout: 25000 });
  await expect(page.getByRole('row', { name: ':00 16:00 8h 0min' }).getByRole('button').nth(1)).toBeVisible({ timeout: 25000 });
  await page.waitForTimeout(6000);

  // Create invalid timestamp without changes
  await page.getByRole('button', { name: 'Erfassen' }).click();
  await page.waitForTimeout(6000);

  // Create invalid timestamp with changes
  await page.locator('input[name="startTime"]').click();
  await page.locator('input[name="startTime"]').fill('10:00');
  await page.locator('input[name="endTime"]').click();
  await page.locator('input[name="endTime"]').fill('14:00');
  await page.getByRole('button', { name: 'Erfassen' }).click();
  await page.waitForTimeout(6000);

  // Update timestamp
  await page.getByRole('row', { name: ':00 16:00 8h 0min' }).getByRole('button').nth(1).click();
  await page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="startTime"]').click();
  await page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="startTime"]').fill('08:15');
  await page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="endTime"]').click();
  await page.locator('form').filter({ hasText: 'Zeiteintrag' }).locator('input[name="endTime"]').fill('17:30');
  await page.getByRole('button', { name: 'Speichern' }).click();
  await page.waitForTimeout(6000);
  await expect(page.getByRole('status')).toContainText('Zeiteintrag wurde aktualisiert', { timeout: 25000 });
  await expect(page.locator('tbody')).toContainText('08:15', { timeout: 25000 });
  await expect(page.locator('tbody')).toContainText('17:30', { timeout: 25000 });
  await expect(page.locator('tbody')).toContainText('9h 15min', { timeout: 25000 });
  await page.waitForTimeout(6000);

  // Delete timestamp
  await page.getByRole('row', { name: ':15 17:30 9h 15min' }).getByRole('button').first().click();
  await page.waitForTimeout(6000);
});

test('testing crud for incidents', async ({ page }) => {
  // Create incident
  await page.locator('div:nth-child(5) > .w-5').click();
  await page.getByRole('button', { name: 'Erstellen' }).click();
  await page.getByPlaceholder('Datum').fill('2024-01-01');
  await page.getByPlaceholder('Titel').click();
  await page.getByPlaceholder('Titel').fill('Schlafen am Arbeitsplatz');
  await page.getByPlaceholder('Beschreibung').click();
  await page.getByPlaceholder('Beschreibung').fill('Charles Leclerc hat sein DRS nicht verwendet.');
  await page.waitForTimeout(6000);
  await page.getByRole('button', { name: 'Speichern' }).click();
  await expect(page.getByRole('status')).toContainText('Vorfall erstellt', { timeout: 25000 });
  await expect(page.locator('tbody')).toContainText('01. Jan. 2024', { timeout: 25000 });
  await expect(page.locator('tbody')).toContainText('Schlafen am Arbeitsplatz', { timeout: 25000 });
  await expect(page.getByRole('button', { name: 'Löschen' })).toBeVisible({ timeout: 25000 });
  await expect(page.getByRole('button').nth(2)).toBeVisible({ timeout: 25000 });
  await page.waitForTimeout(6000);

  // Update incident
  await page.getByRole('button').nth(2).click();
  await page.getByPlaceholder('Titel').click();
  await page.getByPlaceholder('Titel').press('ControlOrMeta+a');
  await page.getByPlaceholder('Titel').fill('DRS auf Gerade nicht genutzt');
  await page.getByRole('button', { name: 'Speichern' }).click();
  await page.waitForTimeout(6000);
  await expect(page.locator('tbody')).toContainText('DRS auf Gerade nicht genutzt', { timeout: 25000 });
  await page.waitForTimeout(6000);

  // Delete incident
  await page.getByRole('button', { name: 'Löschen' }).click();
  await page.waitForTimeout(6000);
  await expect(page.getByRole('heading', { name: 'Vorfall löschen' })).toBeVisible({ timeout: 25000 });
  await page.getByRole('button', { name: 'Bestätigen' }).click();
  await expect(page.getByRole('status')).toContainText('Vorfall gelöscht', { timeout: 25000 });
  await expect(page.locator('td')).toContainText('Keine Daten erfasst', { timeout: 25000 });
});

test('testing confirm daysheet', async ({ page }) => {
  await page.locator('div:nth-child(6) > .w-5').click();
  await expect(page.getByRole('cell', { name: 'Jan. 2024' })).toBeVisible({ timeout: 25000 });
  await page.getByRole('row', { name: '01. Jan. 2024 0h 0min charles' }).getByRole('button').nth(1).click();
  await page.waitForTimeout(6000);
  await page.locator('input[name="startTime"]').click();
  await page.locator('input[name="startTime"]').fill('05:05');
  await page.locator('input[name="endTime"]').click();
  await page.locator('input[name="endTime"]').fill('15:15');
  await page.getByRole('button', { name: 'Erfassen' }).click();
  await page.waitForTimeout(6000);
  await expect(page.getByText('Zeiteintrag erstellt')).toBeVisible({ timeout: 25000 });
  await page.waitForTimeout(6000);
  await page.getByRole('button', { name: 'Bestätigen' }).click();
  await page.waitForTimeout(6000);
  await expect(page.getByRole('heading', { name: 'Arbeitszeit bestätigen' })).toBeVisible({ timeout: 25000 });
  await page.getByRole('button', { name: 'Bestätigen' }).first().click();
  await page.waitForTimeout(6000);
  await expect(page.getByText('DaySheet wurde bestätigt')).toBeVisible({ timeout: 25000 });
});

test('testing daily overview', async ({ page }) => {
  await page.locator('div:nth-child(7) > .w-5').click();
  await page.waitForTimeout(6000);
  await page.getByRole('combobox').nth(3).selectOption('charles.leclerc@gmail.com');
  await page.getByRole('combobox').first().selectOption('01');
  await page.waitForTimeout(6000);

  // Update notes
  await page.getByRole('button', { name: 'Notizen' }).click();
  await page.waitForTimeout(6000);
  await page.getByPlaceholder('Notizen').click();
  await page.getByPlaceholder('Notizen').fill('Charles Leclerc wurde von Max Vestappen überholt.');
  await page.getByRole('button', { name: 'Speichern' }).click();
  await page.waitForTimeout(6000);
  await expect(page.getByText('Notizen aktualisiert')).toBeVisible({ timeout: 25000 });

  // Unconfirm daysheet
  await page.getByRole('button').nth(1).click();
  await page.waitForTimeout(6000);
  await page.getByRole('button', { name: 'Bestätigen' }).click();
  await page.waitForTimeout(6000);
  await expect(page.getByText('Bestätigung wurde zurü')).toBeVisible({ timeout: 25000 });
  await page.getByRole('cell', { name: 'Nein' }).locator('div').click();
  await page.waitForTimeout(6000);
  await expect(page.getByRole('row', { name: '01. Jan. 2024 10h 10min Nein' }).locator('div')).toBeVisible({ timeout: 25000 });
});

test('testing monthly overview', async ({ page }) => {
  await page.locator('div:nth-child(8) > .w-5').click();
  await page.waitForTimeout(6000);
  await expect(page.locator('body')).toContainText('Teilnehmercharles.leclerc@gmail.com', { timeout: 25000 });
  await expect(page.getByRole('button', { name: 'Rapport' })).toBeVisible({ timeout: 25000 });
});