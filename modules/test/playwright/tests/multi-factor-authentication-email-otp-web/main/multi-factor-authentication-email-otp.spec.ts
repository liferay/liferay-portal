/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {multiFactorAuthenticationPagesTest} from '../../../fixtures/multiFactorAuthenticationPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import signInAndReachMFAChallenge from '../../../utils/signInAndReachMFAChallenge';

export const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	multiFactorAuthenticationPagesTest
);

test(
	'LPD-95294 keeps the resend countdown without flipping back to Send when an incorrect OTP is submitted',
	{tag: '@LPD-95294'},
	async ({
		apiHelpers,
		browser,
		multiFactorAuthenticationConfigurationPage,
	}) => {

		// Enable the email OTP checker for the instance. The admin browser
		// session stays authenticated, so the configuration can be reset later.

		await multiFactorAuthenticationConfigurationPage.goto();

		await multiFactorAuthenticationConfigurationPage.enable();

		// Create a user that will be challenged with email OTP when signing in

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		// Reach the email OTP challenge in a clean context, leaving the admin
		// signed in

		const {userContext, userPage} = await signInAndReachMFAChallenge(
			browser,
			user.emailAddress
		);

		try {

			// Request a one-time password so the send button starts its cooldown

			const sendEmailButton = userPage.locator('[id$="sendEmailButton"]');

			await expect(sendEmailButton).toHaveText('Send');

			await clickAndExpectToBeVisible({
				target: userPage.getByText(
					'Your one-time password has been sent by email.'
				),
				trigger: sendEmailButton,
			});

			// Submit an incorrect OTP. The send button is wired with type="submit"
			// inside a data-senna-off form, so this is a full-page server
			// re-render of the challenge (not an AJAX update).

			await userPage
				.getByLabel('Enter the one-time password from the email')
				.fill(getRandomString());

			await userPage.locator('[id$="submitEmailButton"]').click();

			await expect(
				userPage.getByText('Multi-factor authentication has failed.')
			).toBeVisible();

			// LPD-95294: on the re-render the cooldown must already be in place,
			// never reverting to "Send". The button is rendered server-side as a
			// disabled countdown (its label is the remaining seconds).

			await expect(sendEmailButton).toBeDisabled();

			await expect(sendEmailButton).not.toHaveText('Send');

			await expect(sendEmailButton).toHaveText(/^\s*\d+\s*$/);
		}
		finally {
			await userContext.close();

			await multiFactorAuthenticationConfigurationPage.goto();

			await multiFactorAuthenticationConfigurationPage.resetConfiguration();
		}
	}
);

test(
	'LPD-95735 keeps the send button disabled while the resend cooldown is still running after the retry lockout expires',
	{tag: '@LPD-95735'},
	async ({
		apiHelpers,
		browser,
		multiFactorAuthenticationConfigurationPage,
	}) => {
		test.setTimeout(60000);

		// Enable email OTP and configure a resend cooldown that outlasts the
		// failed-attempts retry lockout, so the two timers expire at clearly
		// different moments. A single failed attempt is enough to trigger the
		// lockout.

		await multiFactorAuthenticationConfigurationPage.goto();

		await multiFactorAuthenticationConfigurationPage.enable({
			failedAttemptsAllowed: 1,
			resendEmailTimeout: 12,
			retryTimeout: 4,
		});

		// Create a user that will be challenged with email OTP when signing in

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		// Reach the email OTP challenge in a clean context, leaving the admin
		// signed in

		const {userContext, userPage} = await signInAndReachMFAChallenge(
			browser,
			user.emailAddress
		);

		try {

			// Request a one-time password so the send button starts its cooldown

			const sendEmailButton = userPage.locator('[id$="sendEmailButton"]');

			const submitEmailButton = userPage.locator(
				'[id$="submitEmailButton"]'
			);

			await expect(sendEmailButton).toHaveText('Send');

			await clickAndExpectToBeVisible({
				target: userPage.getByText(
					'Your one-time password has been sent by email.'
				),
				trigger: sendEmailButton,
			});

			// Submit an incorrect OTP to exhaust the single allowed attempt. The
			// full-page re-render brings back the challenge with the retry
			// lockout active: both buttons are disabled and the submit button
			// shows the lockout countdown.

			await userPage
				.getByLabel('Enter the one-time password from the email')
				.fill(getRandomString());

			await submitEmailButton.click();

			await expect(
				userPage.getByText('Multi-factor authentication has failed.')
			).toBeVisible();

			await expect(submitEmailButton).toBeDisabled();

			await expect(sendEmailButton).toBeDisabled();

			// Once the retry lockout expires the submit button is re-enabled,
			// but the resend cooldown is still running. LPD-95735: the send
			// button must stay disabled and keep showing its countdown instead
			// of becoming clickable while the cooldown has not elapsed.

			await expect(submitEmailButton).toBeEnabled({timeout: 15000});

			await expect(sendEmailButton).toBeDisabled();

			await expect(sendEmailButton).not.toHaveText('Send');

			await expect(sendEmailButton).toHaveText(/^\s*\d+\s*$/);

			// Once the resend cooldown also elapses, the send button must
			// become fully usable again, not merely lose its disabled
			// attribute. A leftover server-rendered "disabled" CSS class would
			// keep it greyed out and unclickable.

			await expect(sendEmailButton).toBeEnabled({timeout: 15000});

			await expect(sendEmailButton).not.toHaveClass(/\bdisabled\b/);
		}
		finally {
			await userContext.close();

			await multiFactorAuthenticationConfigurationPage.goto();

			await multiFactorAuthenticationConfigurationPage.resetConfiguration();
		}
	}
);

test(
	'LPD-97358 keeps the send button independent from the failed-attempts lockout',
	{tag: ['@LPD-97358', '@LPP-64662']},
	async ({
		apiHelpers,
		browser,
		multiFactorAuthenticationConfigurationPage,
	}) => {

		// Enable email OTP with a resend cooldown that is much shorter than the
		// failed-attempts retry lockout, so the resend cooldown elapses before
		// the OTP is submitted. A single failed attempt triggers the lockout.

		await multiFactorAuthenticationConfigurationPage.goto();

		await multiFactorAuthenticationConfigurationPage.enable({
			failedAttemptsAllowed: 1,
			resendEmailTimeout: 2,
			retryTimeout: 30,
		});

		// Create a user that will be challenged with email OTP when signing in

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		// Reach the email OTP challenge in a clean context, leaving the admin
		// signed in

		const {userContext, userPage} = await signInAndReachMFAChallenge(
			browser,
			user.emailAddress
		);

		try {
			const sendEmailButton = userPage.locator('[id$="sendEmailButton"]');

			const submitEmailButton = userPage.locator(
				'[id$="submitEmailButton"]'
			);

			// Request a one-time password so the send button starts its cooldown,
			// then wait for that short cooldown to elapse so the resend timer is
			// no longer the reason the button could be disabled.

			await expect(sendEmailButton).toHaveText('Send');

			await clickAndExpectToBeVisible({
				target: userPage.getByText(
					'Your one-time password has been sent by email.'
				),
				trigger: sendEmailButton,
			});

			await expect(sendEmailButton).toBeEnabled({timeout: 15000});

			// Submit an incorrect OTP to exhaust the single allowed attempt. The
			// full-page re-render brings back the challenge with the retry
			// lockout active on the submit button.

			await userPage
				.getByLabel('Enter the one-time password from the email')
				.fill(getRandomString());

			await submitEmailButton.click();

			await expect(
				userPage.getByText('Multi-factor authentication has failed.')
			).toBeVisible();

			// LPD-97358: the lockout governs only the submit button. With the
			// resend cooldown already elapsed, the send button must stay usable
			// so a new OTP can be requested while the submit lockout runs.

			await expect(submitEmailButton).toBeDisabled();

			await expect(submitEmailButton).toHaveText(/^\s*\d+\s*$/);

			await expect(sendEmailButton).toBeEnabled();

			await expect(sendEmailButton).toHaveText('Send');

			await expect(sendEmailButton).not.toHaveClass(/\bdisabled\b/);
		}
		finally {
			await userContext.close();

			await multiFactorAuthenticationConfigurationPage.goto();

			await multiFactorAuthenticationConfigurationPage.resetConfiguration();
		}
	}
);

test(
	'LPD-96858 removes the sent-by-email message once the resend countdown finishes',
	{tag: '@LPD-96858'},
	async ({
		apiHelpers,
		browser,
		multiFactorAuthenticationConfigurationPage,
	}) => {
		test.setTimeout(60000);

		// Enable email OTP with a short resend cooldown so the countdown
		// finishes quickly within the test.

		await multiFactorAuthenticationConfigurationPage.goto();

		await multiFactorAuthenticationConfigurationPage.enable({
			resendEmailTimeout: 5,
		});

		// Create a user that will be challenged with email OTP when signing in

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		// Reach the email OTP challenge in a clean context, leaving the admin
		// signed in

		const {userContext, userPage} = await signInAndReachMFAChallenge(
			browser,
			user.emailAddress
		);

		try {
			const sendEmailButton = userPage.locator('[id$="sendEmailButton"]');

			const sentMessage = userPage.getByText(
				'Your one-time password has been sent by email.'
			);

			// Request a code: the confirmation message appears and the send
			// button starts its cooldown countdown.

			await clickAndExpectToBeVisible({
				target: sentMessage,
				trigger: sendEmailButton,
			});

			// When the cooldown ends the send button returns to "Send" and the
			// confirmation message must be removed entirely, not just shortened
			// to the version without the "please wait" notice.

			await expect(sendEmailButton).toBeEnabled({timeout: 15000});

			await expect(sentMessage).toBeHidden();
		}
		finally {
			await userContext.close();

			await multiFactorAuthenticationConfigurationPage.goto();

			await multiFactorAuthenticationConfigurationPage.resetConfiguration();
		}
	}
);
