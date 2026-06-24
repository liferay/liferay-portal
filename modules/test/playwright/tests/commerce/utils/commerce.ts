/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {TPermission} from '../../../helpers/HeadlessAdminUserApiHelper';
import {CommerceAdminChannelDetailsPage} from '../../../pages/commerce/commerce-channel-web/commerceAdminChannelDetailsPage';
import {CommerceAdminChannelsPage} from '../../../pages/commerce/commerce-channel-web/commerceAdminChannelsPage';
import getRandomString from '../../../utils/getRandomString';
import {performLogout, userData} from '../../../utils/performLogin';
import {openProductMenu} from '../../../utils/productMenu';
import {waitForAlert} from '../../../utils/waitForAlert';
import {TAccount} from '../../workspaces/liferay-partner-workspace/main/types/account';
import {ORDER_WORKFLOW_STATUS_CODE} from '../../workspaces/liferay-workspace-marketplace/main/utils/constants';

export async function classicCommerceSetUp(
	apiHelpers: DataApiHelpers,
	siteName?: string
) {
	return initializerSetUp(
		apiHelpers,
		'com.liferay.commerce.site.initializer',
		siteName,
		siteName,
		siteName
	);
}

export async function commerceReturnSetUp(
	apiHelpers: DataApiHelpers,
	amount?: number,
	authorized?: number,
	received?: number,
	quantity?: number,
	returnStatus?: string
) {
	const {
		account,
		address,
		catalog,
		channel,
		order,
		orderItem,
		payment,
		site,
		sku,
	} = await completedVirtualOrderItemSetUp(apiHelpers, 1);

	amount = amount || 10;
	authorized = authorized || 1;
	received = received || 1;
	quantity = quantity || 1;
	returnStatus = returnStatus || 'processing';

	let commerceReturn = null;

	if (returnStatus === 'draft') {
		commerceReturn =
			await apiHelpers.headlessCommerceReturn.postCommerceReturn({
				channelId: channel.id,
				commerceReturnToCommerceReturnItems: [
					{
						amount,
						authorized,
						quantity,
						r_accountToCommerceReturnItems_accountEntryId:
							account.id,
						r_commerceOrderItemToCommerceReturnItems_commerceOrderItemId:
							orderItem.id,
						r_commerceOrderToCommerceReturns_commerceOrderId:
							order.id,
						received,
						returnReason: {
							key: 'changeOfMind',
						},
						returnResolutionMethod: {
							key: 'refund',
						},
					},
				],
				r_accountToCommerceReturns_accountEntryId: account.id,
				r_commerceOrderToCommerceReturns_commerceOrderId: order.id,
				returnStatus: {
					key: returnStatus,
				},
			});
	}
	else {
		commerceReturn =
			await apiHelpers.headlessCommerceReturn.postCommerceReturn({
				channelId: channel.id,
				commerceReturnToCommerceReturnItems: [
					{
						amount,
						authorized,
						quantity,
						r_accountToCommerceReturnItems_accountEntryId:
							account.id,
						r_commerceOrderItemToCommerceReturnItems_commerceOrderItemId:
							orderItem.id,
						r_commerceOrderToCommerceReturns_commerceOrderId:
							order.id,
						received,
						returnItemStatus: {
							key: 'toBeProccessed',
						},
						returnReason: {
							key: 'changeOfMind',
						},
						returnResolutionMethod: {
							key: 'refund',
						},
					},
				],
				r_accountToCommerceReturns_accountEntryId: account.id,
				r_commerceOrderToCommerceReturns_commerceOrderId: order.id,
				returnStatus: {
					key: returnStatus,
				},
			});
	}

	return {
		account,
		address,
		catalog,
		channel,
		commerceReturn,
		order,
		orderItem,
		payment,
		site,
		sku,
	};
}

export async function configureBuyerUserForSite(
	account: TAccount,
	apiHelpers: DataApiHelpers,
	site: Site,
	userEmail: any
) {
	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			userEmail
		);

	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
		return role.name === 'Buyer';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account.externalReferenceCode,
		accountRoleBuyer[0].id,
		user.emailAddress
	);

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		site.id,
		user.id
	);
	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[user.emailAddress]
	);

	return user;
}

export async function configureOrderManagerUserForSite(
	account: TAccount,
	apiHelpers: DataApiHelpers,
	isOrderAdministrator: boolean,
	site: Site,
	userEmail: any
) {
	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			userEmail
		);

	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
		return role.name === 'Order Manager';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account.externalReferenceCode,
		accountRoleBuyer[0].id,
		user.emailAddress
	);

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		site.id,
		user.id
	);
	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[user.emailAddress]
	);

	if (isOrderAdministrator) {
		const orderAdministratorRole =
			await apiHelpers.headlessAdminUser.getRoleByName(
				'Order Administrator'
			);

		await apiHelpers.headlessAdminUser.assignUserToRole(
			orderAdministratorRole.externalReferenceCode,
			user.id
		);
	}

	return user;
}

export async function configureOperationsManagerUserForSite(
	account: TAccount,
	apiHelpers: DataApiHelpers,
	companyId: string,
	site: Site,
	additionalPermissions?: TPermission[]
) {
	const operationsManagerUser =
		await apiHelpers.headlessAdminUser.postUserAccount();

	userData[operationsManagerUser.alternateName] = {
		name: operationsManagerUser.givenName,
		password: 'test',
		surname: operationsManagerUser.familyName,
	};

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: 'Test Role ' + getRandomString(),
		rolePermissions: [
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_commerce_inventory_web_internal_portlet_CommerceInventoryPortlet',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_commerce_shipment_web_internal_portlet_CommerceShipmentPortlet',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_commerce_subscription_web_internal_portlet_CommerceSubscriptionEntryPortlet',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName: 'com.liferay.account.model.AccountEntry',
				scope: 1,
			},
			{
				actionIds: ['VIEW_INVENTORIES'],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.inventory',
				scope: 1,
			},
			{
				actionIds: ['DELETE', 'PERMISSIONS', 'UPDATE', 'VIEW'],
				primaryKey: companyId,
				resourceName:
					'com.liferay.commerce.inventory.model.CommerceInventoryWarehouse',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.model.CommerceOrder',
				scope: 1,
			},
			{
				actionIds: [
					'MANAGE_COMMERCE_ORDER_PAYMENT_TERMS',
					'MANAGE_COMMERCE_ORDERS',
					'MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES',
					'MANAGE_COMMERCE_ORDER_DELIVERY_TERMS',
				],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.order',
				scope: 1,
			},
			{
				actionIds: [
					'ADD_COMMERCE_PRODUCT_MEASUREMENT_UNIT',
					'VIEW_COMMERCE_PRODUCT_MEASUREMENT_UNITS',
				],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.product',
				scope: 1,
			},
			{
				actionIds: [
					'ADD_COMMERCE_SHIPMENT',
					'MANAGE_ALL_ACCOUNTS',
					'VIEW_COMMERCE_SHIPMENTS',
				],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.shipment',
				scope: 1,
			},
			{
				actionIds: ['MANAGE_COMMERCE_SUBSCRIPTIONS'],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.subscription',
				scope: 1,
			},
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName:
					'com.liferay.commerce.product.model.CommerceChannel',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName:
					'com.liferay.commerce.product.model.CPMeasurementUnit',
				scope: 1,
			},
			...additionalPermissions,
		],
	});

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		operationsManagerUser.id
	);

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		site.id,
		operationsManagerUser.id
	);
	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[operationsManagerUser.emailAddress]
	);

	return operationsManagerUser;
}

export async function completedVirtualOrderItemSetUp(
	apiHelpers: DataApiHelpers,
	orderItemQuantity: number
) {
	const site = await apiHelpers.headlessAdminSite.postSite({
		name: getRandomString(),
	});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: getRandomString(),
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		productType: 'virtual',
	});

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product.productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'person',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['test@liferay.com']
	);

	const address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account.id,
		{phoneNumber: '1234567890', regionISOCode: 'AL'}
	);

	const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account.id,
		billingAddressId: address.id,
		channelId: channel.id,
		orderItems: [
			{
				quantity: orderItemQuantity,
				skuId: sku.id,
			},
		],
		shippingAddressId: address.id,
	});

	const payment =
		await apiHelpers.headlessCommerceAdminPaymentApiHelper.postPayment({
			amount: 10,
			relatedItemId: order.id,
		});

	await apiHelpers.headlessCommerceAdminPaymentApiHelper.patchPayment(
		{
			paymentStatus: 0,
			relatedItemId: payment.relatedItemId,
		},
		payment.id
	);

	await apiHelpers.headlessCommerceAdminOrder.patchOrder(order.id, {
		orderStatus: ORDER_WORKFLOW_STATUS_CODE.PROCESSING,
	});

	await apiHelpers.headlessCommerceAdminOrder.patchOrder(order.id, {
		orderStatus: ORDER_WORKFLOW_STATUS_CODE.COMPLETED,
	});

	const orderItem = order.orderItems[0];

	return {
		account,
		address,
		catalog,
		channel,
		order,
		orderItem,
		payment,
		site,
		sku,
	};
}

export async function initializerSetUp(
	apiHelpers: DataApiHelpers,
	templateKey: string,
	catalogName?: string,
	channelName?: string,
	siteName?: string
) {
	siteName = siteName || getRandomString();

	catalogName = catalogName || siteName;
	channelName = channelName || siteName;

	const site = await apiHelpers.headlessAdminSite.postSite({
		name: siteName,
		templateKey,
		templateType: 'site-initializer',
	});

	const channels =
		await apiHelpers.headlessCommerceAdminChannel.getChannelsPage(
			channelName
		);

	apiHelpers.data.push({id: channels.items.at(-1).id, type: 'channel'});

	const catalogs =
		await apiHelpers.headlessCommerceAdminCatalog.getCatalogsPage(
			catalogName
		);

	if (catalogs.items?.length) {
		apiHelpers.data.push({id: catalogs.items[0].id, type: 'catalog'});

		const products =
			await apiHelpers.headlessCommerceAdminCatalog.getProductsPage(
				100,
				''
			);

		for (let i = 0; i < products.totalCount; i++) {
			if (products.items[i].catalogId === catalogs.items[0].id) {
				apiHelpers.data.push({
					id: products.items[i].productId,
					type: 'product',
				});
			}
		}
	}

	const options = await apiHelpers.headlessCommerceAdminCatalog.getOptions();

	for (let i = 0; i < options.totalCount; i++) {
		apiHelpers.data.push({
			id: options.items[i].id,
			type: 'option',
		});
	}

	const optionCategories =
		await apiHelpers.headlessCommerceAdminCatalog.getOptionCategories();

	for (let i = 0; i < optionCategories.totalCount; i++) {
		apiHelpers.data.push({
			id: optionCategories.items[i].id,
			type: 'optionCategory',
		});
	}

	const specifications =
		await apiHelpers.headlessCommerceAdminCatalog.getSpecifications();

	for (let i = 0; i < specifications.totalCount; i++) {
		apiHelpers.data.push({
			id: specifications.items[i].id,
			type: 'specification',
		});
	}

	const warehouses =
		await apiHelpers.headlessCommerceAdminInventoryApiHelper.getWarehousesPage();

	for (let i = 0; i < warehouses.totalCount; i++) {
		apiHelpers.data.push({
			id: warehouses.items[i].id,
			type: 'warehouse',
		});
	}

	return {catalog: catalogs.items[0], channel: channels.items[0], site};
}

export async function enableGuestPageView(
	page: Page,
	site: Site,
	childPages?: Array<{pageName: string; parentPageName: string}>
): Promise<void> {
	const siteURL = `/web${site.friendlyUrlPath}`;

	await page.goto(siteURL);

	await openProductMenu(page);

	const productMenuSiteBuilderButton = page.getByRole('menuitem', {
		name: 'Site Builder',
	});

	await productMenuSiteBuilderButton.click();

	const productMenuPagesButton = page.getByRole('menuitem', {
		name: 'Pages',
	});

	await productMenuPagesButton.click();
	await page
		.getByRole('checkbox', {name: 'Select All Items on the Page'})
		.click();
	await page.getByRole('button', {name: 'Permissions'}).click();

	const guestActionViewCheckbox = page
		.frameLocator('iframe[title="Permissions"]')
		.locator('#guest_ACTION_VIEW');

	await expect(guestActionViewCheckbox).toBeVisible();

	await guestActionViewCheckbox.click({clickCount: 2});

	const savePagePermissionsButton = page
		.frameLocator('iframe[title="Permissions"]')
		.getByRole('button', {name: 'Save'});

	await savePagePermissionsButton.click();

	await waitForAlert(
		page.frameLocator('iframe[title="Permissions"]'),
		'success'
	);

	if (childPages && childPages.length) {
		const permissionsDialog = page.getByRole('dialog', {
			name: 'Permissions',
		});

		await permissionsDialog.getByRole('button', {name: 'Close'}).click();

		await expect(permissionsDialog).toBeHidden();

		for (const {pageName, parentPageName} of childPages) {
			await page
				.getByRole('menuitem', {
					exact: true,
					name: `${parentPageName} Content Page`,
				})
				.click({force: true});

			await page
				.getByRole('checkbox', {
					exact: true,
					name: `Select ${pageName}`,
				})
				.click();

			await page.getByRole('button', {name: 'Permissions'}).click();

			const permissionsFrame = page.frameLocator(
				'iframe[title="Permissions"]'
			);

			const childGuestActionViewCheckbox =
				permissionsFrame.locator('#guest_ACTION_VIEW');

			await expect(childGuestActionViewCheckbox).toBeVisible();

			await childGuestActionViewCheckbox.check();

			await permissionsFrame.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(permissionsFrame, 'success');

			await permissionsDialog
				.getByRole('button', {name: 'Close'})
				.click();

			await expect(permissionsDialog).toBeHidden();
		}
	}

	await page.reload();
}

export async function guestCheckoutSetUp(
	channel: any,
	commerceAdminChannelDetailsPage: CommerceAdminChannelDetailsPage,
	commerceAdminChannelsPage: CommerceAdminChannelsPage,
	page: Page,
	site: Site,
	childPages?: Array<{pageName: string; parentPageName: string}>
): Promise<void> {
	await enableGuestPageView(page, site, childPages);

	await commerceAdminChannelsPage.goto();

	await (
		await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
	).click();

	await commerceAdminChannelDetailsPage.guestCheckoutToggle.setChecked(true);

	await expect(
		commerceAdminChannelDetailsPage.guestCheckoutToggle
	).toBeChecked();

	await commerceAdminChannelDetailsPage.saveButton.click();

	await waitForAlert(page, 'success');

	await performLogout(page);

	await page.goto(`/web${site.friendlyUrlPath}`, {waitUntil: 'networkidle'});

	await expect(page.locator('.btn-account-selector')).not.toBeVisible();
}

export async function miniumSetUp(
	apiHelpers: DataApiHelpers,
	siteName?: string
) {
	return initializerSetUp(
		apiHelpers,
		'minium-initializer',
		null,
		null,
		siteName
	);
}

export async function speedwellSetUp(
	apiHelpers: DataApiHelpers,
	siteName?: string
) {
	return initializerSetUp(
		apiHelpers,
		'speedwell-initializer',
		null,
		null,
		siteName
	);
}

export async function createAccountWithBuyerUser(
	apiHelpers: DataApiHelpers,
	siteId: number | string,
	options?: {
		accountName?: string;
		userEmailAddress?: string;
		userFirstName?: string;
		userLastName?: string;
		userScreenName?: string;
	}
) {
	const randomSuffix = getRandomString();
	const accountName =
		options?.accountName || `Commerce Account ${randomSuffix}`;
	const userScreenName = options?.userScreenName || `buyer${randomSuffix}`;
	const userEmailAddress =
		options?.userEmailAddress || `${userScreenName}@liferay.com`;
	const userFirstName = options?.userFirstName || `Buyer${randomSuffix}`;
	const userLastName = options?.userLastName || 'User';

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: accountName,
		type: 'business',
	});

	const buyerUser = await apiHelpers.headlessAdminUser.postUserAccount({
		alternateName: userScreenName,
		emailAddress: userEmailAddress,
		familyName: userLastName,
		givenName: userFirstName,
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[buyerUser.emailAddress]
	);

	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const buyerRole = rolesResponse?.items?.find(
		(role: {name: string}) => role.name === 'Buyer'
	);

	if (buyerRole) {
		await apiHelpers.headlessAdminUser.assignAccountRoles(
			account.externalReferenceCode,
			buyerRole.id,
			buyerUser.emailAddress
		);
	}

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		siteId,
		buyerUser.id
	);

	userData[buyerUser.alternateName] = {
		name: buyerUser.givenName,
		password: 'test',
		surname: buyerUser.familyName,
	};

	return {account, buyerUser};
}

export async function createAccountWithSupplierUser(
	apiHelpers: DataApiHelpers,
	siteId: number | string,
	options?: {
		accountName?: string;
		userEmailAddress?: string;
		userFirstName?: string;
		userLastName?: string;
		userScreenName?: string;
	}
) {
	const randomSuffix = getRandomString();
	const accountName =
		options?.accountName || `Supplier Account ${randomSuffix}`;
	const userScreenName = options?.userScreenName || `supplier${randomSuffix}`;
	const userEmailAddress =
		options?.userEmailAddress || `${userScreenName}@liferay.com`;
	const userFirstName = options?.userFirstName || `Supplier${randomSuffix}`;
	const userLastName = options?.userLastName || 'User';

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: accountName,
		type: 'supplier',
	});

	const supplierUser = await apiHelpers.headlessAdminUser.postUserAccount({
		alternateName: userScreenName,
		emailAddress: userEmailAddress,
		familyName: userLastName,
		givenName: userFirstName,
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[supplierUser.emailAddress]
	);

	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const supplierRole = rolesResponse?.items?.find(
		(role: {name: string}) => role.name === 'Account Supplier'
	);

	if (supplierRole) {
		await apiHelpers.headlessAdminUser.assignAccountRoles(
			account.externalReferenceCode,
			supplierRole.id,
			supplierUser.emailAddress
		);
	}

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		siteId,
		supplierUser.id
	);

	userData[supplierUser.alternateName] = {
		name: supplierUser.givenName,
		password: 'test',
		surname: supplierUser.familyName,
	};

	return {account, supplierUser};
}

export async function createChannelAccountManagerUser(
	apiHelpers: DataApiHelpers,
	{
		accountEntryActionIds = [],
		companyId,
		organizationActionIds = [],
		siteId,
	}: {
		accountEntryActionIds?: string[];
		companyId: string;
		organizationActionIds?: string[];
		siteId: number | string;
	}
) {
	const rolePermissions = [];

	if (accountEntryActionIds.length) {
		rolePermissions.push({
			actionIds: accountEntryActionIds,
			primaryKey: companyId,
			resourceName: 'com.liferay.account.model.AccountEntry',
			scope: 1,
		});
	}

	if (organizationActionIds.length) {
		rolePermissions.push({
			actionIds: organizationActionIds,
			primaryKey: companyId,
			resourceName: 'com.liferay.portal.kernel.model.Organization',
			scope: 1,
		});
	}

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: 'Test Channel Account Manager ' + getRandomString(),
		rolePermissions,
	});

	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	await apiHelpers.headlessAdminUser.assignUserToRole(
		role.externalReferenceCode,
		user.id
	);

	const siteMemberRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteMemberRole.id,
		siteId,
		user.id
	);

	return {role, user};
}

export async function createSalesAgentUser(
	apiHelpers: DataApiHelpers,
	{
		accountId,
		siteId,
	}: {
		accountId?: number;
		siteId: number | string;
	}
) {
	const salesAgentRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Sales Agent');

	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	await apiHelpers.headlessAdminUser.assignUserToRole(
		salesAgentRole.externalReferenceCode,
		user.id
	);

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		siteId,
		user.id
	);

	if (accountId) {
		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			accountId,
			[user.emailAddress]
		);
	}

	return user;
}
