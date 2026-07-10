/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
} from '../../../../utils/performLogin';
import {
	classicCommerceSetUp,
	enableGuestPageView,
	guestCheckoutSetUp,
	speedwellSetUp,
} from '../../utils/commerce';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-10562': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest
);

test(
	'Guest can directly checkout a new order in B2B channel site',
	{tag: ['@LPD-35678', '@LPD-84664', '@LPD-93817']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		commerceThemeClassicCatalogPage,
		page,
	}) => {
		test.setTimeout(90000);

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

		try {
			await test.step('Add an item to the cart and open the mini cart', async () => {
				await commerceThemeClassicCatalogPage
					.productCardAddToCartButton('Wear Sensors')
					.click();

				await page.waitForLoadState('networkidle');

				await expect(commerceMiniCartPage.miniCartButton).toHaveClass(
					'has-badge mini-cart-opener'
				);

				await commerceMiniCartPage.miniCartButton.click();
			});

			await test.step('Proceed as guest from the mini cart and verify the checkout survives a page reload', async () => {
				await commerceMiniCartPage.proceedAsGuest.click();

				await expect(checkoutPage.activeCheckoutStep).toBeVisible();

				await page.reload();

				await expect(checkoutPage.activeCheckoutStep).toBeVisible();
			});

			await test.step('Complete the checkout flow', async () => {
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
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			if (orders.items[0]) {
				apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
			}
		}
	}
);

test(
	'Guest can checkout a new order on sign-in in B2B channel site',
	{tag: '@LPD-35678'},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		commerceThemeClassicCatalogPage,
		page,
	}) => {
		test.setTimeout(90000);

		let account;

		const {channel, site} =
			await test.step('Set up classic commerce B2B site, create an account', async () => {
				const setUp = await classicCommerceSetUp(
					apiHelpers,
					`B2B_${getRandomString()}`
				);

				account = await apiHelpers.headlessAdminUser.postAccount({
					name: getRandomString(),
					type: 'business',
				});

				return setUp;
			});

		await test.step('Enable guest checkout on the channel', async () => {
			await guestCheckoutSetUp(
				channel,
				commerceAdminChannelDetailsPage,
				commerceAdminChannelsPage,
				page,
				site
			);
		});

		try {
			await test.step('Add a product to the cart as guest', async () => {
				await commerceThemeClassicCatalogPage
					.productCardAddToCartButton('Wear Sensors')
					.click();

				await page.waitForLoadState('networkidle');

				await expect(commerceMiniCartPage.miniCartButton).toHaveClass(
					'has-badge mini-cart-opener'
				);
			});

			await test.step('Sign in to checkout from the mini cart', async () => {
				await commerceMiniCartPage.miniCartButton.click();

				await commerceMiniCartPage.signInToCheckoutButton.click();

				const signInToCheckoutModal = page.locator(
					'#guest-sign-in-modal'
				);

				await expect(signInToCheckoutModal).toBeVisible();

				await signInToCheckoutModal
					.locator('input[id*="LoginPortlet_login"]')
					.fill('test@liferay.com');
				await signInToCheckoutModal
					.locator('input[id*="LoginPortlet_pass"]')
					.fill('test');
				await signInToCheckoutModal
					.getByRole('button', {name: 'Sign In'})
					.click();
			});

			await test.step('Verify the account is selected and the cart still has the product', async () => {
				await expect(
					page.locator('.btn-account-selector', {
						hasText: account.name,
					})
				).toBeVisible();

				await commerceMiniCartPage.miniCartButton.click();

				await expect(
					commerceMiniCartPage.miniCartItem('Wear Sensors')
				).toBeVisible();

				await commerceMiniCartPage.miniCartButtonClose.click();
			});

			await test.step('Complete the checkout flow', async () => {
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
		}
		finally {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			for (const order of orders.items) {
				await apiHelpers.headlessCommerceAdminOrder.deleteOrder(
					order.id
				);
			}
		}
	}
);

test('LPD-35678 Guest can checkout a new order on sign-in with multiple accounts in B2B channel site', async ({
	apiHelpers,
	checkoutPage,
	commerceAdminChannelDetailsPage,
	commerceAdminChannelsPage,
	commerceMiniCartPage,
	commerceThemeClassicCatalogPage,
	page,
}) => {
	test.setTimeout(90000);

	const {channel, site} = await classicCommerceSetUp(
		apiHelpers,
		`B2B_${getRandomString()}`
	);

	await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});
	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await guestCheckoutSetUp(
		channel,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		page,
		site
	);

	try {
		await commerceThemeClassicCatalogPage
			.productCardAddToCartButton('Wear Sensors')
			.click();

		await page.waitForLoadState('networkidle');

		await expect(commerceMiniCartPage.miniCartButton).toHaveClass(
			'has-badge mini-cart-opener'
		);

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

		await accountSelectionModal
			.getByRole('button', {name: 'Continue'})
			.click();

		await expect(
			page.locator('.btn-account-selector', {hasText: account2.name})
		).toBeVisible();

		await commerceMiniCartPage.miniCartButton.click();

		await expect(
			commerceMiniCartPage.miniCartItem('Wear Sensors')
		).toBeVisible();

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
	}
	finally {
		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const orders =
			await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

		if (orders.items[0]) {
			apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
		}
	}
});

test('LPD-35678 Guest can checkout a new order on sign-up in B2B channel site', async ({
	apiHelpers,
	checkoutPage,
	commerceAdminChannelDetailsPage,
	commerceAdminChannelsPage,
	commerceMiniCartPage,
	commerceThemeClassicCatalogPage,
	page,
}) => {
	test.setTimeout(180000);

	await page.goto(
		'/group/control_panel/manage?p_p_id=com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_mvcRenderCommandName=%2Fconfiguration_admin%2Fedit_configuration&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_factoryPid=com.liferay.captcha.configuration.CaptchaConfiguration&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_pid=com.liferay.captcha.configuration.CaptchaConfiguration'
	);

	const captchaCheckbox = page
		.locator('input[name*="$createAccountCaptchaEnabled$"]')
		.first();

	await expect(async () => {
		await captchaCheckbox.click();

		await expect(captchaCheckbox).not.toBeChecked();

		await page.getByTestId('submitConfiguration').click();
	}).toPass();

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

		await commerceThemeClassicCatalogPage
			.productCardAddToCartButton('Wear Sensors')
			.click();

		await page.waitForLoadState('networkidle');

		await expect(commerceMiniCartPage.miniCartButton).toHaveClass(
			'has-badge mini-cart-opener'
		);

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
			commerceMiniCartPage.miniCartItem('Wear Sensors')
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
		await performLoginViaApi({page, screenName: 'test'});

		await page.goto(
			'/group/control_panel/manage?p_p_id=com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_mvcRenderCommandName=%2Fconfiguration_admin%2Fedit_configuration&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_factoryPid=com.liferay.captcha.configuration.CaptchaConfiguration&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_pid=com.liferay.captcha.configuration.CaptchaConfiguration',
			{waitUntil: 'networkidle'}
		);

		await captchaCheckbox.click();

		await expect(captchaCheckbox).toBeChecked();

		const orders =
			await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

		if (orders.items[0]) {
			apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
		}
	}
});

test(
	'Guest order cookie is removed if it is manipulated',
	{tag: ['@LPD-68662']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		commerceThemeClassicCatalogPage,
		page,
	}) => {
		test.setTimeout(120000);

		await test.step('Initialize a site with guest checkout enabled', async () => {
			const {channel, site} = await classicCommerceSetUp(
				apiHelpers,
				`B2B_${getRandomString()}`
			);

			await apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			});

			await guestCheckoutSetUp(
				channel,
				commerceAdminChannelDetailsPage,
				commerceAdminChannelsPage,
				page,
				site
			);
		});

		try {
			await test.step('A product is added to cart', async () => {
				await page.waitForLoadState('networkidle');

				await commerceThemeClassicCatalogPage
					.productCardAddToCartButton('Wear Sensors')
					.click();

				await page.waitForLoadState('networkidle');

				await expect(commerceMiniCartPage.miniCartButton).toHaveClass(
					'has-badge mini-cart-opener'
				);

				await page.reload();
				await page.waitForLoadState('networkidle');
			});

			await test.step('The guest order cookie is modified', async () => {
				const context = await page.context();

				const cookies = await context.cookies();

				await context.clearCookies();
				await context.addCookies(
					cookies.map((cookie) => {
						if (
							cookie.name.startsWith(
								'com.liferay.commerce.model.CommerceOrder#'
							)
						) {
							return {
								...cookie,
								value: `${cookie.value}modified`,
							};
						}

						return cookie;
					})
				);
			});

			await test.step('When the guest user signs in', async () => {
				await commerceMiniCartPage.miniCartButton.click();
				await commerceMiniCartPage.signInToCheckoutButton.click();

				const signInToCheckoutModal = page.locator(
					'#guest-sign-in-modal'
				);

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
			});

			await test.step('Then the cookie is removed and the order is not re-conciliated', async () => {
				const context = await page.context();

				const cookies = await context.cookies();

				await expect(
					await cookies.find((cookie) =>
						cookie.name.startsWith(
							'com.liferay.commerce.model.CommerceOrder#'
						)
					)
				).toBeUndefined();

				await commerceMiniCartPage.miniCartButton.click();

				await expect(
					commerceMiniCartPage.miniCartItem('Wear Sensors')
				).toHaveCount(0);
			});
		}
		finally {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			if (orders.items[0]) {
				apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
			}
		}
	}
);

test(
	'Guest users do not see restricted storefront tabs or the wish list CTA',
	{tag: ['@LPD-92440', '@LPD-93812']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceThemeClassicCatalogPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
	}) => {
		const productName = 'Wear Sensors';

		const {channel, site} = await classicCommerceSetUp(
			apiHelpers,
			`B2B_${getRandomString()}`
		);

		await guestCheckoutSetUp(
			channel,
			commerceAdminChannelDetailsPage,
			commerceAdminChannelsPage,
			page,
			site,
			[{pageName: 'Product Detail', parentPageName: 'Catalog'}]
		);

		await test.step('Verify the storefront navigation bar is not shown to guests', async () => {
			await expect(page.locator('.navbar-site')).toHaveCount(0);
		});

		await test.step('Verify the wish list CTA is hidden from the guest on the listing page', async () => {
			await expect(
				commerceThemeClassicCatalogPage.productCardAddToCartButton(
					productName
				)
			).toBeVisible();

			await expect(
				commerceThemeClassicCatalogPage.productCardAddToWishListButton(
					productName
				)
			).toHaveCount(0);
		});

		await test.step('Verify the wish list CTA is hidden from the guest on the product detail page', async () => {
			await commerceThemeClassicCatalogPage
				.productCardLink(productName)
				.click();

			await expect(
				page.getByRole('heading', {name: productName})
			).toBeVisible();

			await expect(page.locator('.add-to-wish-list')).toHaveCount(0);
		});

		let productCardDisplayPageURL: string;

		await test.step('Create a product display page template with the product card fragment', async () => {
			await performLoginViaApi({page, screenName: 'test'});

			const className =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					'com.liferay.commerce.product.model.CPDefinition'
				);

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
					productName
				);

			const displayPageTemplateName = getRandomString();

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
				{
					classNameId: className.classNameId,
					groupId: String(site.id),
					name: displayPageTemplateName,
				}
			);

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);
			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			await pageEditorPage.addFragment('Product', 'Product Card');

			await displayPageTemplatesPage.publishTemplate();

			productCardDisplayPageURL = `/web${site.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${product.id}`;
		});

		await test.step('Verify the wish list CTA is hidden from the guest on the product card fragment', async () => {
			await performLogout(page);

			await page.goto(productCardDisplayPageURL);

			await expect(page.locator('.product-card')).toBeVisible();

			await expect(page.locator('.add-to-wish-list')).toHaveCount(0);
		});
	}
);

test(
	'Guest cannot use the mini cart quick add when guest checkout is disabled in B2B channel site',
	{tag: '@LPD-94001'},
	async ({apiHelpers, commerceMiniCartPage, page}) => {
		test.setTimeout(90000);

		const {site} = await classicCommerceSetUp(
			apiHelpers,
			`B2B_${getRandomString()}`
		);

		await enableGuestPageView(page, site);

		try {
			await test.step('Open the mini cart as a guest', async () => {
				await performLogout(page);

				await page.goto(`/web${site.friendlyUrlPath}`);

				await commerceMiniCartPage.miniCartButton.click();
			});

			await test.step('Verify the quick add is disabled', async () => {
				await expect(
					commerceMiniCartPage.searchProductsInput
				).toBeDisabled();

				await expect(
					commerceMiniCartPage.quickAddToCartButton
				).toBeDisabled();
			});
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});
		}
	}
);

test(
	'Guest checkout survives sign-in after the order in the URL is merged away',
	{tag: '@LPD-95478'},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		page,
	}) => {
		test.setTimeout(120000);

		const {channel, site} = await speedwellSetUp(
			apiHelpers,
			`Speedwell_${getRandomString()}`
		);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		await guestCheckoutSetUp(
			channel,
			commerceAdminChannelDetailsPage,
			commerceAdminChannelsPage,
			page,
			site
		);

		let order;

		await test.step('Provision the full-page authentication layout via the channel health check', async () => {
			await performLoginViaApi({page, screenName: 'test'});

			await commerceAdminChannelsPage.goto();

			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					channel.name
				)
			).click();

			await (
				await commerceAdminChannelDetailsPage.commerceChannelHealthChecksTableRowAction(
					'Fix Issue',
					'Guest Checkout Authentication'
				)
			).click();

			await page.waitForLoadState('networkidle');
		});

		await test.step('Give the user an open order so the guest order is merged into it on sign-in', async () => {
			const product =
				await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
					'Calipers'
				);

			const sku = product.skus[0];

			order = await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [{quantity: 1, skuId: sku.id}],
				},
				channel.id
			);

			await performLogout(page);
		});

		await test.step('As a guest, proceed to checkout and continue to the authentication page', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/catalog`, {
				waitUntil: 'networkidle',
			});

			const productName = 'Wear Sensors';

			await commerceThemeMiniumCatalogPage.catalogSearch.fill(
				productName
			);

			await commerceThemeMiniumCatalogPage.catalogSearch.press('Enter');

			await page.waitForLoadState('networkidle');

			await commerceThemeMiniumCatalogPage
				.productCardAddToCartButton(productName)
				.click();

			await page.waitForLoadState('networkidle');

			await commerceMiniCartPage.miniCartButton.click();

			await expect(
				commerceMiniCartPage.miniCartItem(productName)
			).toBeVisible();

			await commerceMiniCartPage.proceedAsGuest.click();

			await page.waitForLoadState('networkidle');
		});

		await test.step('Sign in on the authentication page and verify the checkout renders against the merged order', async () => {
			await page
				.locator('input[id*="LoginPortlet_login"]')
				.fill('test@liferay.com');
			await page.locator('input[id*="LoginPortlet_pass"]').fill('test');
			await page.getByRole('button', {name: 'Sign In'}).last().click();

			await page.waitForLoadState('networkidle');

			await expect(page.locator('.alert-danger')).toHaveCount(0);

			const cartItems =
				await apiHelpers.headlessCommerceDeliveryCart.getCartItems(
					order.id
				);

			expect(cartItems.items).toHaveLength(2);
		});
	}
);
