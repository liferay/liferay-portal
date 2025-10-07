/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {instanceSettingsPagesTest} from '../../../../fixtures/instanceSettingsPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {taxCategoriesPageTest} from '../../../../fixtures/taxCategoriesPageTest';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {checkSameDate} from '../../utils/date';

export const test = mergeTests(
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-20379': {enabled: true},
	}),
	instanceSettingsPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	taxCategoriesPageTest
);

test(
	'Can map order item information',
	{tag: '@LPD-30855'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		commerceAdminProductPage,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		await apiHelpers.headlessCommerceAdminAccount.postAddress(account.id, {
			phoneNumber: '12345',
			regionISOCode: 'LA',
		});

		const option = await apiHelpers.headlessCommerceAdminCatalog.postOption(
			'select',
			'color',
			'Color',
			1
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const linkedProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
				productOptions: [
					{
						fieldType: 'select',
						key: option.key,
						name: option.name,
						optionId: option.id,
						priceType: 'static',
						priority: 1,
						productOptionValues: [
							{
								deltaPrice: 10.0,
								key: 'black',
								name: {
									en_US: 'Black',
								},
								priority: 1,
								quantity: 1,
								skuId: linkedProduct.skus[0].id,
							},
						],
						skuContributor: true,
					},
				],
			});

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);
		await commerceAdminProductPage.generateSkus();

		await expect(
			page.getByText('Showing 1 to 2 of 2 entries.')
		).toBeVisible();

		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.productId)
			.then((product) => {
				return product.skus;
			});

		const sku = productSkus.find((sku) => sku.sku === 'BLACK');

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Order',
			name: displayPageTemplateName,
		});
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment(
			'Content Display',
			'Collection Display'
		);
		await pageEditorPage.selectFragment(
			await pageEditorPage.getFragmentId('Collection Display')
		);

		await page.getByText('Select a collection to display.').click();

		await pageEditorPage.chooseCollectionDisplayCollection(
			'Related Items Collection Providers',
			'Order Items'
		);
		await pageEditorPage.waitForChangesSaved();
		await pageEditorPage.addFragment(
			'Basic Components',
			'Heading',
			page.locator('.page-editor__collection-item.empty').first()
		);

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						options: `[{key: ${option.key}, value: 'black'}]`,
						quantity: 1,
						skuId: sku.id,
					},
				],
			},
			channel.id
		);

		await commerceLayoutsPage.selectDisplayPageTemplatePreviewItem(
			cart.id.toString()
		);

		const headingId = await pageEditorPage.getFragmentId('Heading');

		await pageEditorPage.selectEditable(headingId, 'element-text');

		await commerceLayoutsPage.labelField.selectOption('Order ID');

		await expect(
			commerceLayoutsPage.pageEditorText(cart.id.toString()).first()
		).toBeVisible();

		await commerceLayoutsPage.labelField.selectOption('Author Name');

		await expect(
			commerceLayoutsPage.pageEditorText(cart.author).first()
		).toBeVisible();

		await commerceLayoutsPage.labelField.selectOption('Create Date');

		await expect(
			page
				.getByLabel('Configuration Panel', {exact: true})
				.getByLabel('Mapping')
		).toContainText('Field Type: Date');

		const pageEditorCreateDate = await commerceLayoutsPage
			.pageEditorElement('h1')
			.first()
			.innerText();

		expect(checkSameDate(cart.createDate, pageEditorCreateDate)).toBe(true);

		await commerceLayoutsPage.labelField.selectOption('Modified Date');

		await expect(
			page
				.getByLabel('Configuration Panel', {exact: true})
				.getByLabel('Mapping')
		).toContainText('Field Type: Date');

		const pageEditorModifiedDate = await commerceLayoutsPage
			.pageEditorElement('h1')
			.first()
			.innerText();

		expect(checkSameDate(cart.modifiedDate, pageEditorModifiedDate)).toBe(
			true
		);

		await commerceLayoutsPage.labelField.selectOption('Order Item ID');

		await expect(
			commerceLayoutsPage
				.pageEditorText(cart.cartItems[0].id.toString())
				.first()
		).toBeVisible();

		await commerceLayoutsPage.labelField.selectOption('Unit Price');

		await expect(
			commerceLayoutsPage
				.pageEditorText(
					cart.cartItems[0].price.priceFormatted.toString()
				)
				.first()
		).toBeVisible();

		if (cart.cartItems[0].price.promoPriceFormatted) {
			await commerceLayoutsPage.labelField.selectOption('Promo Price');

			await expect(
				commerceLayoutsPage
					.pageEditorText(
						cart.cartItems[0].price.promoPriceFormatted.toString()
					)
					.first()
			).toBeVisible();
		}

		if (cart.cartItems[0].price.discountFormatted) {
			await commerceLayoutsPage.labelField.selectOption('Discount');

			await expect(
				commerceLayoutsPage
					.pageEditorText(
						cart.cartItems[0].price.discountFormatted.toString()
					)
					.first()
			).toBeVisible();
		}

		await commerceLayoutsPage.labelField.selectOption('Total Price');

		await expect(
			commerceLayoutsPage
				.pageEditorText(
					cart.cartItems[0].price.finalPriceFormatted.toString()
				)
				.first()
		).toBeVisible();

		await commerceLayoutsPage.labelField.selectOption('Options');

		await expect(commerceLayoutsPage.pageEditorText('Black')).toBeVisible();

		await displayPageTemplatesPage.publishTemplate();

		await displayPageTemplatesPage.deleteTemplate(displayPageTemplateName);

		await expect(
			page.getByText(displayPageTemplateName, {exact: true})
		).not.toBeVisible();
	}
);

test('LPD-41395 Can map display page and schedule fields to fragments', async ({
	apiHelpers,
	commerceLayoutsPage,
	displayPageTemplatesPage,
	page,
	pageEditorPage,
	site,
}) => {
	await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'person',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['demo.unprivileged@liferay.com']
	);

	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
		return role.name === 'Buyer';
	});

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.unprivileged@liferay.com'
		);

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account.externalReferenceCode,
		accountRoleBuyer[0].id,
		user.emailAddress
	);
	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		site.id,
		user.id
	);

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		neverExpire: false,
	});

	const className = await apiHelpers.jsonWebServicesClassName.fetchClassName(
		'com.liferay.commerce.product.model.CPDefinition'
	);

	const displayPageTemplateName = getRandomString();

	const displayPage =
		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
			{
				classNameId: className.classNameId,
				groupId: site.id,
				name: displayPageTemplateName,
			}
		);

	await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
		{
			layoutPageTemplateEntryId: displayPage.layoutPageTemplateEntryId,
		}
	);

	await displayPageTemplatesPage.goto(site.friendlyUrlPath);
	await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

	await pageEditorPage.addFragment('Basic Components', 'Heading');
	await pageEditorPage.addFragment('Basic Components', 'Heading');
	await pageEditorPage.addFragment('Basic Components', 'Heading');

	const heading1 = await pageEditorPage.getFragmentId('Heading');

	await pageEditorPage.selectEditable(heading1, 'element-text');

	await commerceLayoutsPage.labelField.selectOption('Default');

	const heading2 = await pageEditorPage.getFragmentId('Heading', 1);

	await pageEditorPage.selectEditable(heading2, 'element-text');

	await commerceLayoutsPage.labelField.selectOption('Display Date');

	const heading3 = await pageEditorPage.getFragmentId('Heading', 2);

	await pageEditorPage.selectEditable(heading3, 'element-text');

	await commerceLayoutsPage.labelField.selectOption('Expiration Date');

	await pageEditorPage.waitForChangesSaved();

	await displayPageTemplatesPage.publishTemplate();

	await performLogout(page);

	await performLoginViaApi({page, screenName: 'demo.unprivileged'});

	await page.goto(
		`/web${site.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${product.id}`
	);

	const productDefaultExpected = `${liferayConfig.environment.baseUrl}/web/${site.name}/p/${product.name.en_US}`;

	await expect(
		page.getByRole('heading', {name: productDefaultExpected})
	).toBeVisible();

	const productCreateDate = await pageEditorPage
		.getFragment(heading2)
		.innerText();

	expect(checkSameDate(product.createDate, productCreateDate)).toBe(true);

	const productExpirationDate = await pageEditorPage
		.getFragment(heading3)
		.innerText();

	expect(checkSameDate(product.expirationDate, productExpirationDate)).toBe(
		true
	);
});

test('LPD-41395 Can map detailed information fields to fragments', async ({
	apiHelpers,
	commerceLayoutsPage,
	displayPageTemplatesPage,
	newTaxCategoryPage,
	page,
	pageEditorPage,
	site,
	taxCategoriesPage,
}) => {
	let taxCategory;
	try {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['demo.unprivileged@liferay.com']
		);

		const rolesResponse =
			await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

		const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
			return role.name === 'Buyer';
		});

		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'demo.unprivileged@liferay.com'
			);

		const siteRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignAccountRoles(
			account.externalReferenceCode,
			accountRoleBuyer[0].id,
			user.emailAddress
		);
		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteRole.id,
			site.id,
			user.id
		);

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		await taxCategoriesPage.goto(site.name);

		await taxCategoriesPage.newButton.click();

		await newTaxCategoryPage.externalReferenceCodeInput.fill(
			getRandomString()
		);
		await newTaxCategoryPage.nameInput.fill(getRandomString());
		await newTaxCategoryPage.descriptionInput.fill(getRandomString());
		await newTaxCategoryPage.saveButton.click();

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				neverExpire: false,
			});

		taxCategory = (
			await apiHelpers.headlessCommerceAdminChannel.getTaxCategories()
		).items[0];

		await apiHelpers.headlessCommerceAdminCatalog.patchProductTaxConfiguration(
			product.productId,
			{
				id: taxCategory.id,
				taxable: true,
			}
		);

		const className =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.commerce.product.model.CPDefinition'
			);

		const displayPageTemplateName = getRandomString();

		const displayPage =
			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
				{
					classNameId: className.classNameId,
					groupId: site.id,
					name: displayPageTemplateName,
				}
			);

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
			{
				layoutPageTemplateEntryId:
					displayPage.layoutPageTemplateEntryId,
			}
		);

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Basic Components', 'Heading');
		await pageEditorPage.addFragment('Basic Components', 'Heading');
		await pageEditorPage.addFragment('Basic Components', 'Heading');
		await pageEditorPage.addFragment('Basic Components', 'Heading');
		await pageEditorPage.addFragment('Basic Components', 'Heading');
		await pageEditorPage.addFragment('Basic Components', 'Heading');
		await pageEditorPage.addFragment('Basic Components', 'Heading');

		const heading1 = await pageEditorPage.getFragmentId('Heading');

		await pageEditorPage.selectEditable(heading1, 'element-text');

		await commerceLayoutsPage.labelField.selectOption(
			'Account Group Filter Enabled'
		);

		const heading2 = await pageEditorPage.getFragmentId('Heading', 1);

		await pageEditorPage.selectEditable(heading2, 'element-text');

		await commerceLayoutsPage.labelField.selectOption(
			'Channel Filter Enabled'
		);

		const heading3 = await pageEditorPage.getFragmentId('Heading', 2);

		await pageEditorPage.selectEditable(heading3, 'element-text');

		await commerceLayoutsPage.labelField.selectOption('Create Date');

		const heading4 = await pageEditorPage.getFragmentId('Heading', 3);

		await pageEditorPage.selectEditable(heading4, 'element-text');

		await commerceLayoutsPage.labelField.selectOption('Status');

		const heading5 = await pageEditorPage.getFragmentId('Heading', 4);

		await pageEditorPage.selectEditable(heading5, 'element-text');

		await commerceLayoutsPage.labelField.selectOption('Tax Exempt');

		const heading6 = await pageEditorPage.getFragmentId('Heading', 5);

		await pageEditorPage.selectEditable(heading6, 'element-text');

		await commerceLayoutsPage.labelField.selectOption(
			'Telco or Electronics'
		);

		const heading7 = await pageEditorPage.getFragmentId('Heading', 6);

		await pageEditorPage.selectEditable(heading7, 'element-text');

		await commerceLayoutsPage.labelField.selectOption('Version');

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

		await performLogout(page);

		await performLoginViaApi({page, screenName: 'demo.unprivileged'});

		await page.goto(
			`/web${site.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${product.id}`
		);

		await expect(pageEditorPage.getFragment(heading1)).toContainText(
			product.productAccountGroupFilter.toString()
		);

		await expect(pageEditorPage.getFragment(heading2)).toContainText(
			product.productChannelFilter.toString()
		);

		const productCreateDate = await pageEditorPage
			.getFragment(heading3)
			.innerText();

		expect(checkSameDate(product.createDate, productCreateDate)).toBe(true);

		await expect(pageEditorPage.getFragment(heading4)).toContainText(
			product.productStatus.toString()
		);

		await expect(pageEditorPage.getFragment(heading5)).toContainText(
			'false'
		);

		await expect(pageEditorPage.getFragment(heading6)).toContainText(
			'false'
		);

		await expect(pageEditorPage.getFragment(heading7)).toContainText(
			product.version.toString()
		);
	}
	finally {
		await performLogout(page);

		await performLoginViaApi({page, screenName: 'test'});

		page.on('dialog', (dialog) => {
			dialog.accept();
		});

		await taxCategoriesPage.goto(site.name);

		await (
			await taxCategoriesPage.taxCategoriesTableRowActions(
				taxCategory.name.en_US
			)
		).click();
		await taxCategoriesPage.deleteMenuItem.click();

		await waitForAlert(page);
	}
});
