/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {performLogout} from '../../../utils/performLogin';
import {classicCommerceSetUp, guestCheckoutSetUp} from '../utils/commerce';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-10562': {enabled: true},
		'LPD-20379': {enabled: true},
		'LPD-35678': {enabled: true},
	}),
	loginTest()
);

test('LPD-35678 Guest can directly checkout a new order in B2B channel site', async ({
	apiHelpers,
	checkoutPage,
	commerceAdminChannelDetailsPage,
	commerceAdminChannelsPage,
	commerceMiniCartPage,
	page,
}) => {
	test.setTimeout(180000);

	const {channel, site} = await classicCommerceSetUp(
		apiHelpers,
		`B2B_${getRandomString()}`
	);

	await guestCheckoutSetUp(
		channel,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		page,
		site
	);

	const addToCartButton = page
		.locator('.cp-renderer', {hasText: 'U-Joint'})
		.getByRole('button', {name: 'Add to Cart'});

	await addToCartButton.click();

	await commerceMiniCartPage.miniCartButton.click();

	await commerceMiniCartPage.proceedAsGuest.click();

	await checkoutPage.performCheckout({
		shippingAddress: {
			asGuest: true,
			city: 'testCity',
			countryLabel: 'United States',
			name: 'John Doe Guest',
			regionLabel: 'Florida',
			street: 'testStreet',
			zip: '12345',
		},
	});
});

test('LPD-35678 Guest can checkout a new order on sign-in in B2B channel site', async ({
	apiHelpers,
	checkoutPage,
	commerceAdminChannelDetailsPage,
	commerceAdminChannelsPage,
	commerceMiniCartPage,
	page,
}) => {
	test.setTimeout(180000);

	const {channel, site} = await classicCommerceSetUp(
		apiHelpers,
		`B2B_${getRandomString()}`
	);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	apiHelpers.data.push({id: account.id, type: 'account'});

	await guestCheckoutSetUp(
		channel,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		page,
		site
	);

	const addToCartButton = page
		.locator('.cp-renderer', {hasText: 'U-Joint'})
		.getByRole('button', {name: 'Add to Cart'});

	await addToCartButton.click();

	await commerceMiniCartPage.miniCartButton.click();

	await commerceMiniCartPage.signInToCheckoutButton.click();

	const signInToCheckoutModal = page.locator('#guest-sign-in-modal');

	await expect(signInToCheckoutModal).toBeVisible();

	const emailAddressInput = signInToCheckoutModal.locator(
		'input[id*="LoginPortlet_login"]'
	);
	const passInput = signInToCheckoutModal.locator(
		'input[id*="LoginPortlet_pass"]'
	);
	const signInButton = signInToCheckoutModal.getByRole('button', {
		name: 'Sign In',
	});

	await emailAddressInput.fill('test@liferay.com');
	await passInput.fill('test');

	await signInButton.click();

	await expect(
		page.locator('.btn-account-selector', {hasText: account.name})
	).toBeVisible();

	await commerceMiniCartPage.miniCartButton.click();

	await expect(commerceMiniCartPage.miniCartItem('U-Joint')).toBeVisible();

	await commerceMiniCartPage.miniCartButtonClose.click();

	await checkoutPage.performCheckout({
		shippingAddress: {
			city: 'testCity',
			countryLabel: 'United States',
			name: `Guest to ${account.name}`,
			regionLabel: 'Florida',
			street: 'testStreet',
			zip: '12345',
		},
	});
});

test('LPD-35678 Guest can checkout a new order on sign-in with multiple accounts in B2B channel site', async ({
	apiHelpers,
	checkoutPage,
	commerceAdminChannelDetailsPage,
	commerceAdminChannelsPage,
	commerceMiniCartPage,
	page,
}) => {
	test.setTimeout(180000);

	const {channel, site} = await classicCommerceSetUp(
		apiHelpers,
		`B2B_${getRandomString()}`
	);

	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	apiHelpers.data.push({id: account1.id, type: 'account'});
	apiHelpers.data.push({id: account2.id, type: 'account'});

	await guestCheckoutSetUp(
		channel,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		page,
		site
	);

	const addToCartButton = page
		.locator('.cp-renderer', {hasText: 'U-Joint'})
		.getByRole('button', {name: 'Add to Cart'});

	await addToCartButton.click();

	await commerceMiniCartPage.miniCartButton.click();

	await commerceMiniCartPage.signInToCheckoutButton.click();

	const signInToCheckoutModal = page.locator('#guest-sign-in-modal');

	await expect(signInToCheckoutModal).toBeVisible();

	const emailAddressInput = signInToCheckoutModal.locator(
		'input[id*="LoginPortlet_login"]'
	);
	const passInput = signInToCheckoutModal.locator(
		'input[id*="LoginPortlet_pass"]'
	);
	const signInButton = signInToCheckoutModal.getByRole('button', {
		name: 'Sign In',
	});

	await emailAddressInput.fill('test@liferay.com');
	await passInput.fill('test');

	await signInButton.click();

	const accountSelectionModal = page.locator('#account-selection-modal');

	await expect(accountSelectionModal).toBeVisible();

	await accountSelectionModal
		.locator('#available-accounts-list')
		.selectOption(account2.name);

	await accountSelectionModal.getByRole('button', {name: 'Continue'}).click();

	await expect(
		page.locator('.btn-account-selector', {hasText: account2.name})
	).toBeVisible();

	await commerceMiniCartPage.miniCartButton.click();

	await expect(commerceMiniCartPage.miniCartItem('U-Joint')).toBeVisible();

	await commerceMiniCartPage.miniCartButtonClose.click();

	await checkoutPage.performCheckout({
		shippingAddress: {
			city: 'testCity',
			countryLabel: 'United States',
			name: `Guest to ${account2.name}`,
			regionLabel: 'Florida',
			street: 'testStreet',
			zip: '12345',
		},
	});
});

test('LPD-35678 Guest can checkout a new order on sign-up in B2B channel site', async ({
	apiHelpers,
	checkoutPage,
	commerceAdminChannelDetailsPage,
	commerceAdminChannelsPage,
	commerceMiniCartPage,
	page,
}) => {
	test.setTimeout(180000);

	await page.goto(
		'/group/control_panel/manage?p_p_id=com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_mvcRenderCommandName=%2Fconfiguration_admin%2Fedit_configuration&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_factoryPid=com.liferay.captcha.configuration.CaptchaConfiguration&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_pid=com.liferay.captcha.configuration.CaptchaConfiguration'
	);

	const captchaCheckbox = page
		.locator('input[name*="$createAccountCaptchaEnabled$"]')
		.first();

	await captchaCheckbox.click();

	await expect(captchaCheckbox).not.toBeChecked();

	await page.getByTestId('submitConfiguration').click();

	try {
		const {channel, site} = await classicCommerceSetUp(
			apiHelpers,
			`B2B_${getRandomString()}`
		);

		await guestCheckoutSetUp(
			channel,
			commerceAdminChannelDetailsPage,
			commerceAdminChannelsPage,
			page,
			site
		);

		const addToCartButton = page
			.locator('.cp-renderer', {hasText: 'U-Joint'})
			.getByRole('button', {name: 'Add to Cart'});

		await addToCartButton.click();

		await commerceMiniCartPage.miniCartButton.click();

		await commerceMiniCartPage.signInToCheckoutButton.click();

		const signInToCheckoutModal = page.locator('#guest-sign-in-modal');

		await expect(signInToCheckoutModal).toBeVisible();

		const emailAddressInput = signInToCheckoutModal.locator(
			'input[id*="LoginPortlet_login"]'
		);
		const passInput = signInToCheckoutModal.locator(
			'input[id*="LoginPortlet_pass"]'
		);
		const signInButton = signInToCheckoutModal.getByRole('button', {
			name: 'Sign In',
		});
		const signUpButton = signInToCheckoutModal.getByRole('button', {
			name: 'Sign Up',
		});

		await signUpButton.click();

		const iframe = signInToCheckoutModal.frameLocator(
			'.sign-up-modal-view iframe'
		);

		const userScreenNameInput = iframe.locator('input[id*="_screenName"]');
		const userEmailAddressInput = iframe.locator(
			'input[id*="_emailAddress"]'
		);
		const userFirstNameInput = iframe.locator('input[id*="_firstName"]');
		const userLastNameInput = iframe.locator('input[id*="_lastName"]');
		const userPass1Input = iframe.locator('input[id*="_password1"]');
		const userPass2Input = iframe.locator('input[id*="_password2"]');

		const accountNameInput = signInToCheckoutModal.locator(
			'input[name="accountName"]'
		);

		const ACCOUNT_NAME = getRandomString();
		const EMAIL_ADDRESS = `${ACCOUNT_NAME}@liferay.com`;

		await userScreenNameInput.fill(ACCOUNT_NAME);
		await userEmailAddressInput.fill(EMAIL_ADDRESS);
		await userFirstNameInput.fill(ACCOUNT_NAME);
		await userLastNameInput.fill(ACCOUNT_NAME);
		await userPass1Input.fill(ACCOUNT_NAME);
		await userPass2Input.fill(ACCOUNT_NAME);
		await accountNameInput.fill(ACCOUNT_NAME);

		await signInToCheckoutModal.getByRole('button', {name: 'Done'}).click();

		await expect(signInToCheckoutModal.getByRole('alert')).toBeVisible();

		await emailAddressInput.fill(EMAIL_ADDRESS);
		await passInput.fill(ACCOUNT_NAME);

		await signInButton.click();

		await expect(
			page.locator('.btn-account-selector', {hasText: ACCOUNT_NAME})
		).toBeVisible();

		await commerceMiniCartPage.miniCartButton.click();

		await expect(
			commerceMiniCartPage.miniCartItem('U-Joint')
		).toBeVisible();

		await commerceMiniCartPage.miniCartButtonClose.click();

		await checkoutPage.performCheckout({
			shippingAddress: {
				city: 'testCity',
				countryLabel: 'United States',
				name: `Guest to ${ACCOUNT_NAME}`,
				regionLabel: 'Florida',
				street: 'testStreet',
				zip: '12345',
			},
		});
	}
	finally {
		await performLogout(page);

		await performLogin(page, 'test');

		await page.goto(
			'/group/control_panel/manage?p_p_id=com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_mvcRenderCommandName=%2Fconfiguration_admin%2Fedit_configuration&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_factoryPid=com.liferay.captcha.configuration.CaptchaConfiguration&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_pid=com.liferay.captcha.configuration.CaptchaConfiguration'
		);

		await captchaCheckbox.click();

		await expect(captchaCheckbox).toBeChecked();
	}
});
