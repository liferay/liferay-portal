/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../fixtures/loginTest';
import {searchAdminPageTest} from '../../fixtures/searchAdminPageTest';
import {usersAndOrganizationsPagesTest} from '../../fixtures/usersAndOrganizationsPagesTest';
import {virtualInstancesPagesTest} from '../../fixtures/virtualInstancesPagesTest';
import {ApiHelpers} from '../../helpers/ApiHelpers';
import {TCustomField, TInputField} from '../../helpers/CustomFieldTypesHelper';
import {
	DEFAULT_IDP_CONNECTION_VALUES,
	DEFAULT_SP_CONNECTION_VALUES,
	TIdpConnection,
	TSpConnection,
} from '../../helpers/SamlProviderConnectionHelper';
import {liferayConfig} from '../../liferay.config';
import {
	AttributeMapping,
	IdentityProviderConnectionsPage,
} from '../../pages/saml-web/IdentityProviderConnectionsPage';
import {SamlAdminPage} from '../../pages/saml-web/SamlAdminPage';
import {ServiceProviderConnectionsPage} from '../../pages/saml-web/ServiceProviderConnectionsPage';
import {SiteSettingsPage} from '../../pages/site-admin-web/SiteSettingsPage';
import {EditUserPage} from '../../pages/users-admin-web/EditUserPage';
import {UsersAndOrganizationsPage} from '../../pages/users-admin-web/UsersAndOrganizationsPage';
import {getRandomInt} from '../../utils/getRandomInt';
import getRandomString from '../../utils/getRandomString';
import performLogin, {performLogout} from '../../utils/performLogin';
import {reloadUntilVisible} from '../../utils/reloadUntilVisible';
import {waitForAlert} from '../../utils/waitForAlert';
import {
	TIdentityProvider,
	configureIdentityProvider,
} from './utils/IdentityProviderUtil';
import {
	TServiceProvider,
	configureServiceProvider,
} from './utils/ServiceProviderUtil';
import {
	clickSignInButton,
	performIdpInitiatedSSO,
	performSpInitiatedSSO,
} from './utils/samlAuthUtil';
import {
	connectSpAndIdp,
	editIdentityProviderConnection,
	editServiceProviderConnection,
} from './utils/samlProviderConnectionUtil';
import {
	DEFAULT_IDP_NAME,
	DEFAULT_IDP_URL,
	DEFAULT_SP_NAME,
	DEFAULT_SP_URL,
	SECONDARY_IDP_NAME,
	SECONDARY_IDP_URL,
	SECONDARY_SP_NAME,
	SECONDARY_SP_URL,
	configureVirtualInstanceForSaml,
	createCustomField,
	createIdentityProviderVirtualInstance,
	createServiceProviderVirtualInstance,
	createUser,
	deleteVirtualInstance,
	performSamlSafeLogin,
	resetSamlConfiguration,
	resetSamlKeystoreManagerTarget,
	setupSamlInstances,
	updateRuntimeMetadataRefreshInterval,
	updateSamlKeystoreManagerTarget,
} from './utils/samlVirtualInstanceUtil';

export const test = mergeTests(
	loginTest(),
	searchAdminPageTest,
	usersAndOrganizationsPagesTest,
	virtualInstancesPagesTest
);

export const deleteAfterTestProviderConnections = new Set<string>();
export const deleteAfterTestVirtualInstances = new Set<string>();

test.afterAll(async ({browser}) => {

	// Remove virtual instances

	const newPage = await browser.newPage();

	await performLogin(newPage, 'test');

	for (const virtualInstanceName of deleteAfterTestVirtualInstances) {
		await deleteVirtualInstance(virtualInstanceName, newPage);
	}

	await newPage.waitForTimeout(60 * 1000);

	// Reset saml configuration, in cases where test failed before doing so

	await resetSamlConfiguration(newPage);

	// Reset saml keystore

	await resetSamlKeystoreManagerTarget(newPage);
});

test.afterEach(async ({browser}) => {
	const defaultBaseUrl = liferayConfig.environment.baseUrl;

	for (const instanceName of deleteAfterTestProviderConnections) {
		liferayConfig.environment.baseUrl = `http://${instanceName}:8080`;

		// Reset general tab

		const newPage = await performSamlSafeLogin(browser, instanceName);

		const samlAdminPage = new SamlAdminPage(newPage);

		await samlAdminPage.configureSAML(false);

		// Delete all connections

		if ((await samlAdminPage.samlRoleField.inputValue()) === 'idp') {
			const serviceProviderConnectionsPage =
				new ServiceProviderConnectionsPage(samlAdminPage.page);

			await serviceProviderConnectionsPage.goTo();

			await serviceProviderConnectionsPage.deleteServiceProviderConnections();

			await configureIdentityProvider(newPage);
		}
		else {
			const identityProviderConnectionsPage =
				new IdentityProviderConnectionsPage(samlAdminPage.page);

			await identityProviderConnectionsPage.goTo();

			await identityProviderConnectionsPage.deleteIdentityProviderConnections();

			await configureServiceProvider(newPage);
		}

		await newPage.close();
	}

	liferayConfig.environment.baseUrl = defaultBaseUrl;
});

test.beforeAll(async ({browser}) => {

	// Set saml keystore

	const newPage = await browser.newPage();

	await performLogin(newPage, 'test');

	await updateSamlKeystoreManagerTarget(
		newPage,
		'Document Library Keystore Manager'
	);

	// Update Runtime Metadata Refresh Interval value to a low value, otherwise
	// the tests may update faster than the interval, causing errors.

	await updateRuntimeMetadataRefreshInterval(newPage, '4');

	// Create virtual instances

	await setupSamlInstances(browser, newPage);

	await newPage.close();
});

test('Create two virtual instances, one IdP and one SP, connect them, perform SP initiated SSO, perform SP initiated SLO', async ({
	browser,
}) => {
	const idpAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_IDP_NAME,
		'Identity Provider'
	);

	const spAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_SP_NAME,
		'Service Provider'
	);

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	// Create a user with identical credentials on each instance

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Perform SP initiated SSO

	const spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL
	);

	expect(await spInstancePage.url()).toContain(DEFAULT_SP_URL);

	// Verify user has been imported to SP and logged in

	await expect(
		await spInstancePage.getByTitle('User Profile Menu')
	).toBeVisible();

	// Perform SP initiated SLO

	await performLogout(spInstancePage);

	await spInstancePage.waitForTimeout(8000);

	// Verify user has been logged out of SP and IdP

	await expect(
		await spInstancePage.getByRole('button', {name: 'Sign In'})
	).toBeVisible();

	await spInstancePage.goto(DEFAULT_IDP_URL);

	await spInstancePage
		.getByRole('button', {name: 'Sign In'})
		.waitFor({timeout: 30 * 1000});
});

test('Create, edit, and delete a new virtual instance', async ({
	editVirtualInstancePage,
	searchAdminPage,
	virtualInstancesPage,
}) => {
	const name = getRandomString();

	await virtualInstancesPage.addNewVirtualInstance(name);

	const newName = getRandomString();

	await editVirtualInstancePage.editVirtualInstance(
		name,
		false,
		newName + '.com',
		'100',
		newName
	);

	// Reindex users so the correct number is present

	await searchAdminPage.goto();

	await searchAdminPage.goToIndexActionsTab();

	await searchAdminPage.reindexIndexActionsItem('User');

	await virtualInstancesPage.goto();

	expect(
		await virtualInstancesPage.page
			.getByRole('row')
			.getByText(name + ' ' + newName + ' ' + newName + '.com 1 100 No')
	).toBeVisible();

	await virtualInstancesPage.deleteVirtualInstance(name);
});

test('Create three virtual instances, set two to IdP and one SP, and verify Custom User Attributes', async ({
	browser,
	editUserPage,
	searchAdminPage,
	usersAndOrganizationsPage,
}) => {
	const idpAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_IDP_NAME,
		'Identity Provider'
	);

	const spAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_SP_NAME,
		'Service Provider'
	);

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	// Create an additional IdP virtual instance, and connect it to the SP

	const localhostAdminPage = await browser.newPage();

	await performLogin(localhostAdminPage, 'test');

	const secondaryIdpAdminPage = await createIdentityProviderVirtualInstance(
		browser,
		localhostAdminPage,
		SECONDARY_IDP_NAME
	);

	await connectSpAndIdp(
		secondaryIdpAdminPage,
		SECONDARY_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	// Create identical Custom Fields for all instances, except starting value

	const customFieldName = 'CustomField' + getRandomInt();

	const fieldValues: TInputField = {
		startingValue: 'ableStartingValue',
	};

	const customField: TCustomField = {
		fieldName: customFieldName,
		fieldType: 'inputField',
		fieldValues,
		resource: 'User',
	};

	await createCustomField(idpAdminPage, customField);

	fieldValues.startingValue = 'charlieStartingValue';

	customField.fieldValues = fieldValues;

	await createCustomField(secondaryIdpAdminPage, customField);

	fieldValues.startingValue = 'bakerStartingValue';

	customField.fieldValues = fieldValues;

	await createCustomField(spAdminPage, customField);

	// Edit IdP Connections to include User Custom Field attribute mapping

	const attributeMappings: AttributeMapping[] = [
		{
			attributeMappingType: 'User Custom Fields',
			samlAttribute: customFieldName,
			userFieldExpression: customFieldName,
		},
	];

	let idpConnection: TIdpConnection = {
		attributeMappings,
		entityId: DEFAULT_IDP_NAME,
		idpDomain: DEFAULT_IDP_URL,
		idpName: DEFAULT_IDP_NAME,
		spName: DEFAULT_SP_NAME,
		...DEFAULT_IDP_CONNECTION_VALUES,
	};

	await editIdentityProviderConnection(spAdminPage, idpConnection);

	idpConnection = {
		attributeMappings,
		entityId: SECONDARY_IDP_NAME,
		idpDomain: SECONDARY_IDP_URL,
		idpName: SECONDARY_IDP_NAME,
		spName: DEFAULT_SP_NAME,
		...DEFAULT_IDP_CONNECTION_VALUES,
	};

	await editIdentityProviderConnection(spAdminPage, idpConnection);

	// Edit SP Connection to include User Custom Field attribute

	const spConnection: TSpConnection = {
		entityId: DEFAULT_SP_NAME,
		idpName: DEFAULT_IDP_NAME,
		spDomain: DEFAULT_SP_URL,
		spName: DEFAULT_SP_NAME,
		...DEFAULT_SP_CONNECTION_VALUES,
	};

	spConnection.attributes =
		spConnection.attributes + `\nexpando:${customFieldName}`;

	await editServiceProviderConnection(idpAdminPage, spConnection);

	// Create a user on the IdP instances

	const userId = getRandomInt();

	const userAccount = await createUser(
		secondaryIdpAdminPage,
		SECONDARY_IDP_NAME,
		userId
	);

	await createUser(idpAdminPage, DEFAULT_IDP_NAME, userId);

	// Perform SP initiated SSO, using the secondary IdP

	let spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL,
		true,
		SECONDARY_IDP_NAME
	);

	await performLogout(spInstancePage);

	// Perform SP initiated SSO again, this time using www.able.com as the IdP

	spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL,
		true,
		DEFAULT_IDP_NAME
	);

	await performLogout(spInstancePage);

	// Perform reindex on User object

	await searchAdminPage.goto();

	await searchAdminPage.goToIndexActionsTab();

	await searchAdminPage.reindexIndexActionsItem('User');

	// Login to SP as admin, verify user custom field was imported properly

	const defaultBaseUrl = liferayConfig.environment.baseUrl;

	liferayConfig.environment.baseUrl = DEFAULT_SP_URL;

	spInstancePage = await performSamlSafeLogin(browser, DEFAULT_SP_NAME);

	usersAndOrganizationsPage = await new UsersAndOrganizationsPage(
		spInstancePage
	);

	await usersAndOrganizationsPage.goToUsers(false);

	await (
		await usersAndOrganizationsPage.usersTableRowLink(
			userAccount.alternateName
		)
	).click();

	editUserPage = await new EditUserPage(spInstancePage);

	await expect(await editUserPage.customField(customFieldName)).toHaveValue(
		'ableStartingValue',
		{timeout: 30 * 1000}
	);

	liferayConfig.environment.baseUrl = defaultBaseUrl;

	// Delete newly created virtual instance, and remove from afterAll deletion

	await deleteVirtualInstance(SECONDARY_IDP_NAME, localhostAdminPage);

	await deleteAfterTestProviderConnections.delete(SECONDARY_IDP_NAME);

	await deleteAfterTestVirtualInstances.delete(SECONDARY_IDP_NAME);
});

test('Create two virtual instances, one IdP and one SP, and verify Custom User Attributes', async ({
	browser,
	editUserPage,
	searchAdminPage,
	usersAndOrganizationsPage,
}) => {
	const idpAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_IDP_NAME,
		'Identity Provider'
	);

	const spAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_SP_NAME,
		'Service Provider'
	);

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	// Create identical Custom Fields for both instances, except starting value

	const customFieldName = 'CustomField' + getRandomInt();

	const fieldValues: TInputField = {
		startingValue: 'idpStartingValue',
	};

	const customField: TCustomField = {
		fieldName: customFieldName,
		fieldType: 'inputField',
		fieldValues,
		resource: 'User',
	};

	await createCustomField(idpAdminPage, customField);

	fieldValues.startingValue = 'spStartingValue';

	customField.fieldValues = fieldValues;

	await createCustomField(spAdminPage, customField);

	// Edit IdP Connection to include User Custom Field attribute mapping

	const attributeMappings: AttributeMapping[] = [
		{
			attributeMappingType: 'User Custom Fields',
			samlAttribute: customFieldName,
			userFieldExpression: customFieldName,
		},
	];

	const idpConnection: TIdpConnection = {
		attributeMappings,
		entityId: DEFAULT_IDP_NAME,
		idpDomain: `http://${DEFAULT_IDP_NAME}:8080`,
		idpName: DEFAULT_IDP_NAME,
		spName: DEFAULT_SP_NAME,
		...DEFAULT_IDP_CONNECTION_VALUES,
	};

	await editIdentityProviderConnection(spAdminPage, idpConnection);

	// Edit SP Connection to include User Custom Field attribute

	const spConnection: TSpConnection = {
		entityId: DEFAULT_SP_NAME,
		idpName: DEFAULT_IDP_NAME,
		spDomain: `http://${DEFAULT_SP_NAME}:8080`,
		spName: DEFAULT_SP_NAME,
		...DEFAULT_SP_CONNECTION_VALUES,
	};

	spConnection.attributes =
		spConnection.attributes + `\nexpando:${customFieldName}`;

	await editServiceProviderConnection(idpAdminPage, spConnection);

	// Create a user on the IdP instance

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Perform Sp initiated SSO with the new user

	let spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL
	);

	await performLogout(spInstancePage);

	// Perform reindex on User object

	await searchAdminPage.goto();

	await searchAdminPage.goToIndexActionsTab();

	await searchAdminPage.reindexIndexActionsItem('User');

	// Login to SP as admin, verify user custom field was imported properly

	const defaultBaseUrl = liferayConfig.environment.baseUrl;

	liferayConfig.environment.baseUrl = DEFAULT_SP_URL;

	spInstancePage = await performSamlSafeLogin(browser, DEFAULT_SP_NAME);

	usersAndOrganizationsPage = await new UsersAndOrganizationsPage(
		spInstancePage
	);

	await usersAndOrganizationsPage.goToUsers(false);

	await (
		await usersAndOrganizationsPage.usersTableRowLink(
			userAccount.alternateName
		)
	).click();

	editUserPage = await new EditUserPage(spInstancePage);

	await expect(await editUserPage.customField(customFieldName)).toHaveValue(
		'idpStartingValue'
	);

	liferayConfig.environment.baseUrl = defaultBaseUrl;
});

test('SAML connection cannot be saved if a custom field value is used more than once', async ({
	browser,
}) => {
	const idpAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_IDP_NAME,
		'Identity Provider'
	);

	const spAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_SP_NAME,
		'Service Provider'
	);

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	const customFieldName = 'CustomField' + getRandomInt();

	const customField: TCustomField = {
		fieldName: customFieldName,
		fieldType: 'inputField',
		resource: 'User',
	};

	await createCustomField(idpAdminPage, customField);

	await createCustomField(spAdminPage, customField);

	// Edit IdP Connection to include duplicate Custom Field attribute mappings

	const attributeMappings: AttributeMapping[] = [
		{
			attributeMappingType: 'User Custom Fields',
			samlAttribute: customFieldName,
			userFieldExpression: customFieldName,
		},
		{
			attributeMappingType: 'User Custom Fields',
			samlAttribute: customFieldName,
			userFieldExpression: customFieldName,
		},
	];

	const idpConnection: TIdpConnection = {
		attributeMappings,
		entityId: DEFAULT_IDP_NAME,
		idpDomain: `http://${DEFAULT_IDP_NAME}:8080`,
		idpName: DEFAULT_IDP_NAME,
		spName: DEFAULT_SP_NAME,
		...DEFAULT_IDP_CONNECTION_VALUES,
	};

	// IdP connection should display the following error message

	const errorMessage =
		'User Custom Fields: Each user field can only be mapped to one SAML attribute.';

	await editIdentityProviderConnection(
		spAdminPage,
		idpConnection,
		errorMessage
	);
});

test('Verify a Message context is not authenticated when Require Authn Request Signature and Sign Authn Requests are disabled.  Replaces SAML.AssertSSOWithSignAuthnRequests, see LPD-32545.', async ({
	browser,
}) => {
	const idpAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_IDP_NAME,
		'Identity Provider'
	);

	const spAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_SP_NAME,
		'Service Provider'
	);

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	// Disable auth and request signature required on IdP

	const identityProvider: TIdentityProvider = {
		requireAuthnRequestSignature: false,
	};

	await configureIdentityProvider(idpAdminPage, identityProvider);

	const localhostAdminPage = await browser.newPage();

	await performLogin(localhostAdminPage, 'test');

	await updateRuntimeMetadataRefreshInterval(localhostAdminPage, '2');

	// Create new user in IdP instance

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Execute IdP initiated SSO

	const idpInstancePage = await performIdpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_IDP_URL,
		DEFAULT_SP_URL,
		DEFAULT_SP_NAME
	);

	// Assert authentication and SP redirection

	expect(await idpInstancePage.getByTitle('User Profile Menu')).toBeVisible();

	expect(await idpInstancePage.url()).toContain(DEFAULT_SP_URL);

	// Reset IdP configuration settings

	await configureIdentityProvider(idpAdminPage);

	// Execute SP initiated SLO and assert logged out

	await idpInstancePage.getByTitle('User Profile Menu').click();

	await idpInstancePage.getByRole('menuitem', {name: 'Sign Out'}).click();

	await expect(
		await idpInstancePage.getByRole('button', {name: 'Sign In'})
	).toBeVisible();

	// Execute SP initiated SSO

	let spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL
	);

	// Assert authentication and SP redirection

	expect(await spInstancePage.getByTitle('User Profile Menu')).toBeVisible();

	expect(await spInstancePage.url()).toContain(DEFAULT_SP_URL);

	// Disable auth and request signature required on IdP

	await configureIdentityProvider(idpAdminPage, identityProvider);

	// Disable Sign Authn Requests on SP

	const serviceProvider: TServiceProvider = {
		signAuthnRequests: false,
	};

	await configureServiceProvider(spAdminPage, serviceProvider);

	// Execute SP initiated SLO and assert logged out

	await spInstancePage.getByTitle('User Profile Menu').click();

	await spInstancePage.getByRole('menuitem', {name: 'Sign Out'}).click();

	await expect(
		await spInstancePage.getByRole('button', {name: 'Sign In'})
	).toBeVisible();

	// Execute SP initiated SSO

	spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL
	);

	// Assert logged in

	expect(await spInstancePage.getByTitle('User Profile Menu')).toBeVisible();

	// Update Runtime Metadata Refresh Interval value to a high value

	await updateRuntimeMetadataRefreshInterval(localhostAdminPage, '9999');

	// Reset IdP configuration settings

	await configureIdentityProvider(idpAdminPage);

	// Execute SP initiated SLO

	await spInstancePage.getByTitle('User Profile Menu').click();

	await spInstancePage.getByRole('menuitem', {name: 'Sign Out'}).click();

	await spInstancePage
		.getByRole('button', {name: 'Sign In'})
		.waitFor({timeout: 30 * 1000});

	// Go to SP, click Sign in, and assert error message

	await spInstancePage
		.getByRole('button', {
			name: 'Sign In',
		})
		.click();

	// Assert the SAML Message context was not authenticated, because the IdP
	// requires Authn Request Signature, but the SP didn't have a chance to
	// refresh and pull the IdP configuration change

	await spInstancePage.waitForTimeout(2000);

	// Sometimes the error banner does not display, even if the message context
	// was not authenticated.  To make test less flaky, when the banner is not
	// present, verify user was not logged in and is still on SP instance.  This
	// result should still be considered as passing.

	if (
		await spInstancePage
			.getByRole('heading', {
				name: 'Unable to process SAML',
			})
			.isHidden()
	) {
		expect(
			await spInstancePage.getByRole('button', {name: 'Sign In'})
		).toBeVisible();
		expect(await spInstancePage.url()).toContain(DEFAULT_SP_URL);
	}

	await updateRuntimeMetadataRefreshInterval(localhostAdminPage, '4');
});

test('Verify Custom Fields can be used for user matching in SAML, see LPS-128600 and LPD-34973', async ({
	browser,
	searchAdminPage,
}) => {
	const idpAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_IDP_NAME,
		'Identity Provider'
	);

	const spAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_SP_NAME,
		'Service Provider'
	);

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	// Create Custom Field for SP instance only

	const customFieldName = 'CustomField' + getRandomInt();

	const fieldValues: TInputField = {
		startingValue: 'spStartingValue',
	};

	const customField: TCustomField = {
		fieldName: customFieldName,
		fieldType: 'inputField',
		fieldValues,
		resource: 'User',
	};

	await createCustomField(spAdminPage, customField);

	// Edit IdP Connection to include User Custom Field attribute mapping

	const attributeMappings: AttributeMapping[] = [
		{
			attributeMappingType: 'User Custom Fields',
			samlAttribute: customFieldName,
			useToMatchUsers: true,
			userFieldExpression: customFieldName,
		},
	];

	const idpConnection: TIdpConnection = {
		attributeMappings,
		entityId: DEFAULT_IDP_NAME,
		idpDomain: `http://${DEFAULT_IDP_NAME}:8080`,
		idpName: DEFAULT_IDP_NAME,
		spName: DEFAULT_SP_NAME,
		userResolution: 'attribute',
		...DEFAULT_IDP_CONNECTION_VALUES,
	};

	await editIdentityProviderConnection(spAdminPage, idpConnection);

	// Create a user on the IdP instance

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Perform Sp initiated SSO with the new user and verify unsuccessful

	let spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL,
		false
	);

	await expect(
		await spInstancePage.getByText(
			`Your user ${userAccount.emailAddress} could not be logged in`
		)
	).toBeVisible();

	// Add custom field into IdP instance

	fieldValues.startingValue = 'idpStartingValue';

	customField.fieldValues = fieldValues;

	await createCustomField(idpAdminPage, customField);

	// Update SP Connection to include custom field in attribute list

	const spConnection: TSpConnection = {
		entityId: DEFAULT_SP_NAME,
		idpName: DEFAULT_IDP_NAME,
		spDomain: `http://${DEFAULT_SP_NAME}:8080`,
		spName: DEFAULT_SP_NAME,
		...DEFAULT_SP_CONNECTION_VALUES,
	};

	spConnection.attributes =
		spConnection.attributes + `\nexpando:${customFieldName}`;

	await editServiceProviderConnection(idpAdminPage, spConnection);

	// Reattempt SP initiated SSO by just clicking the Sign In link

	await spInstancePage
		.getByRole('button', {
			name: 'Sign In',
		})
		.click();

	await spInstancePage.waitForTimeout(8000);

	// Verify redirected back to SP

	expect(await spInstancePage.url()).toContain(DEFAULT_SP_URL);

	// Verify user has been imported to SP and logged in

	await expect(
		await spInstancePage.getByTitle('User Profile Menu')
	).toBeVisible({timeout: 30 * 1000});

	// Perform SP initiated SLO

	await performLogout(spInstancePage);

	// Change the value of the user's custom field in the IdP Instance

	let usersAndOrganizationsPage = await new UsersAndOrganizationsPage(
		idpAdminPage
	);

	await usersAndOrganizationsPage.goToUsers(false);

	await (
		await usersAndOrganizationsPage.usersTableRowLink(
			userAccount.alternateName
		)
	).click();

	let editUserPage = await new EditUserPage(idpAdminPage);

	await (await editUserPage.customField(customFieldName)).fill('newValue');

	await editUserPage.saveButton.click();

	// Perform SP initiated SSO

	spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL,
		true
	);

	// Perform reindex on User object

	await searchAdminPage.goto();

	await searchAdminPage.goToIndexActionsTab();

	await searchAdminPage.reindexIndexActionsItem('User');

	await searchAdminPage.page.waitForTimeout(8000);

	// Verify user's custom field value has been updated in the SP instance

	usersAndOrganizationsPage = await new UsersAndOrganizationsPage(
		spAdminPage
	);

	await usersAndOrganizationsPage.goToUsers(false);

	await (
		await usersAndOrganizationsPage.usersTableRowLink(
			userAccount.alternateName
		)
	).click();

	editUserPage = await new EditUserPage(spAdminPage);

	await expect(await editUserPage.customField(customFieldName)).toHaveValue(
		'newValue'
	);
});

test('Verify IdP initiated SLO also logs out of authenticated SP when Require Authn Request Signature and Sign Metadata are enabled.  See LPS-128578.', async ({
	browser,
}) => {
	const idpAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_IDP_NAME,
		'Identity Provider'
	);

	const spAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_SP_NAME,
		'Service Provider'
	);

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	// Create IdP User

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Login to IdP.  The Remember Me checkbox must be disabled.

	const newPage = await performSamlSafeLogin(
		browser,
		DEFAULT_IDP_NAME,
		'@liferay.com',
		false,
		userAccount.alternateName
	);

	// Clicking Sign In button on SP page should automatically authenticate

	await newPage.goto(DEFAULT_SP_URL);

	await clickSignInButton(newPage);

	await newPage.getByTitle('User Profile Menu').waitFor({timeout: 30 * 1000});

	// Idp initiated SLO

	await newPage.goto(DEFAULT_IDP_URL);

	await newPage.getByTitle('User Profile Menu').click();

	await newPage.getByRole('menuitem', {name: 'Sign Out'}).click();

	await newPage.waitForTimeout(8000);

	// SP should also be logged out after IdP initiated SLO

	await newPage.goto(DEFAULT_SP_URL);

	const signInButton = await newPage.getByRole('button', {
		name: 'Sign In',
	});

	await reloadUntilVisible({
		myLocator: signInButton,
		page: newPage,
	});

	expect(await signInButton).toBeVisible();
});

test('Verify IdP initiated SLO logs out of multiple authenticated SPs.  See LPS-129934.', async ({
	browser,
}) => {

	// Create and configure secondary SP

	const localhostAdminPage = await browser.newPage();

	await performLogin(localhostAdminPage, 'test');

	const secondarySpAdminPage = await createServiceProviderVirtualInstance(
		browser,
		SECONDARY_SP_NAME,
		SECONDARY_SP_NAME,
		localhostAdminPage
	);

	// Configure the other virtual instances as usual

	const idpAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_IDP_NAME,
		'Identity Provider'
	);

	const spAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_SP_NAME,
		'Service Provider'
	);

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		secondarySpAdminPage,
		SECONDARY_SP_NAME
	);

	// Create IdP User

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// SP initiated SSO

	const spIntancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL
	);

	// Clicking Sign In button on other SP page should auto-login

	await spIntancePage.goto(SECONDARY_SP_URL);

	await clickSignInButton(spIntancePage);

	await spIntancePage
		.getByTitle('User Profile Menu')
		.waitFor({timeout: 30 * 1000});

	// Idp initiated SLO

	await spIntancePage.goto(DEFAULT_IDP_URL);

	await spIntancePage.getByTitle('User Profile Menu').click();

	await spIntancePage.getByRole('menuitem', {name: 'Sign Out'}).click();

	await spIntancePage.waitForTimeout(8000);

	// Both SPs should also be logged out after IdP initiated SLO

	for (const spUrl of [DEFAULT_SP_URL, SECONDARY_SP_URL]) {
		await spIntancePage.goto(spUrl);

		const signInButton = await spIntancePage.getByRole('button', {
			name: 'Sign In',
		});

		await reloadUntilVisible({
			myLocator: signInButton,
			page: spIntancePage,
		});

		expect(await signInButton).toBeVisible();
	}

	// Delete newly created virtual instance, and remove from afterAll deletion

	await deleteVirtualInstance(SECONDARY_SP_NAME, localhostAdminPage);

	await deleteAfterTestProviderConnections.delete(SECONDARY_SP_NAME);

	await deleteAfterTestVirtualInstances.delete(SECONDARY_SP_NAME);
});

test('Verify SSO login and logout mechanism works the same when having multiple sites configured as SP.  See LPS-170940.', async ({
	browser,
}) => {
	const idpAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_IDP_NAME,
		'Identity Provider'
	);

	// Create an additional IdP virtual instance

	const localhostAdminPage = await browser.newPage();

	await performLogin(localhostAdminPage, 'test');

	const secondaryIdpAdminPage = await createIdentityProviderVirtualInstance(
		browser,
		localhostAdminPage,
		SECONDARY_IDP_NAME
	);

	const spAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_SP_NAME,
		'Service Provider'
	);

	// Create an additional SP virtual instance

	const secondarySpAdminPage = await createServiceProviderVirtualInstance(
		browser,
		SECONDARY_SP_NAME,
		SECONDARY_SP_NAME,
		localhostAdminPage
	);

	// Connect all IdPs and SPs

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		secondarySpAdminPage,
		SECONDARY_SP_NAME
	);

	await connectSpAndIdp(
		secondaryIdpAdminPage,
		SECONDARY_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	await connectSpAndIdp(
		secondaryIdpAdminPage,
		SECONDARY_IDP_NAME,
		secondarySpAdminPage,
		SECONDARY_SP_NAME
	);

	// In Secondary SP, create two sites with virtual hostnames

	const site1Name = getRandomString();
	const site2Name = getRandomString();

	const defaultBaseUrl = liferayConfig.environment.baseUrl;

	liferayConfig.environment.baseUrl = SECONDARY_SP_URL;

	const apiHelpers = new ApiHelpers(secondarySpAdminPage);

	liferayConfig.environment.baseUrl = defaultBaseUrl;

	await apiHelpers.headlessSite.createSite({
		name: site1Name,
		templateKey: 'com.liferay.site.initializer.welcome',
		templateType: 'site-initializer',
	});

	await apiHelpers.headlessSite.createSite({
		name: site2Name,
		templateKey: 'com.liferay.site.initializer.welcome',
		templateType: 'site-initializer',
	});

	await secondarySpAdminPage.goto(`/web/${site1Name}`);

	await secondarySpAdminPage.waitForTimeout(1000);

	let siteSettingsPage = new SiteSettingsPage(secondarySpAdminPage);

	await siteSettingsPage.goToSiteSetting('Site Configuration', 'Site URL');

	const site1VirtualHostName = 'www.easy.com';

	await siteSettingsPage.page
		.getByLabel('Virtual Host')
		.fill(site1VirtualHostName);

	await siteSettingsPage.page.getByRole('button', {name: 'Save'}).click();

	await waitForAlert(siteSettingsPage.page);

	await secondarySpAdminPage.goto(`/web/${site2Name}`);

	await secondarySpAdminPage.waitForTimeout(1000);

	siteSettingsPage = new SiteSettingsPage(secondarySpAdminPage);

	await siteSettingsPage.goToSiteSetting('Site Configuration', 'Site URL');

	const site2VirtualHostName = 'www.fox.com';

	await siteSettingsPage.page
		.getByLabel('Virtual Host')
		.fill(site2VirtualHostName);

	await siteSettingsPage.page.getByRole('button', {name: 'Save'}).click();

	await waitForAlert(siteSettingsPage.page);

	// Create users for both IdP virtual instances

	const idp1User = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	const idp2User = await createUser(
		secondaryIdpAdminPage,
		SECONDARY_IDP_NAME
	);

	// Verify SP1 initiated SSO works on IdP1

	const idp1SpPages = await performSpInitiatedSSO(
		browser,
		idp1User.emailAddress,
		DEFAULT_SP_URL,
		true,
		DEFAULT_IDP_NAME
	);

	// Verify clicking sign-in button and selecting IdP1 works from SP2 site1

	await idp1SpPages.goto(`http://${site1VirtualHostName}:8080`);

	await clickSignInButton(idp1SpPages, DEFAULT_IDP_NAME);

	// Assert authenticated

	await expect(await idp1SpPages.getByTitle('User Profile Menu')).toBeVisible(
		{
			timeout: 30 * 1000,
		}
	);

	// Verify SP2 initiated SSO works on IdP2

	const idp2SpPages = await performSpInitiatedSSO(
		browser,
		idp2User.emailAddress,
		SECONDARY_SP_URL,
		true,
		SECONDARY_IDP_NAME
	);

	// Verify clicking sign-in button and selecting IdP2 works from SP2 site2

	await idp2SpPages.goto(`http://${site2VirtualHostName}:8080`);

	await clickSignInButton(idp2SpPages, SECONDARY_IDP_NAME);

	// Assert authenticated

	await expect(await idp2SpPages.getByTitle('User Profile Menu')).toBeVisible(
		{
			timeout: 30 * 1000,
		}
	);

	// Perform SP2 Site 2 initiated SLO

	await performLogout(idp2SpPages);

	// Verify IdP2 user is logged out from SP2 Site 2

	expect(
		await idp2SpPages.getByRole('button', {name: 'Sign In'})
	).toBeVisible();

	// Go to SP2 instance and verify logged out

	await idp2SpPages.goto(DEFAULT_SP_URL);

	await expect(
		await idp2SpPages.getByRole('button', {name: 'Sign In'})
	).toBeVisible();

	// Verify IdP1 user is not affected, and still authenticated on both sites

	await idp1SpPages.reload();

	await idp1SpPages.waitForTimeout(1000);

	await expect(await idp1SpPages.getByTitle('User Profile Menu')).toBeVisible(
		{
			timeout: 30 * 1000,
		}
	);

	await idp1SpPages.goto(`http://${site1VirtualHostName}:8080`);

	await idp1SpPages.waitForTimeout(1000);

	expect(await idp1SpPages.getByTitle('User Profile Menu')).toBeVisible({
		timeout: 30 * 1000,
	});

	// Delete newly created virtual instances, and remove from afterAll deletion

	await deleteVirtualInstance(SECONDARY_IDP_NAME, localhostAdminPage);

	await deleteAfterTestProviderConnections.delete(SECONDARY_IDP_NAME);

	await deleteAfterTestVirtualInstances.delete(SECONDARY_IDP_NAME);

	await deleteVirtualInstance(SECONDARY_SP_NAME, localhostAdminPage);

	await deleteAfterTestProviderConnections.delete(SECONDARY_SP_NAME);

	await deleteAfterTestVirtualInstances.delete(SECONDARY_SP_NAME);
});

test('View single logout and force auth with multiple SPs.  See LRQA-31886.', async ({
	browser,
}) => {

	// Create and configure Secondary SP

	const localhostAdminPage = await browser.newPage();

	await performLogin(localhostAdminPage, 'test');

	const secondarySpAdminPage = await createServiceProviderVirtualInstance(
		browser,
		SECONDARY_SP_NAME,
		SECONDARY_SP_NAME,
		localhostAdminPage
	);

	// Configure the other virtual instances as usual

	const idpAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_IDP_NAME,
		'Identity Provider'
	);

	const spAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_SP_NAME,
		'Service Provider'
	);

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		secondarySpAdminPage,
		SECONDARY_SP_NAME
	);

	// Create IdP User

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Configure IdP connections to require force authentication

	let idpConnection: TIdpConnection = {
		entityId: DEFAULT_IDP_NAME,
		forceAuthn: true,
		idpDomain: DEFAULT_IDP_URL,
		idpName: DEFAULT_IDP_NAME,
		spName: DEFAULT_SP_NAME,
		...DEFAULT_IDP_CONNECTION_VALUES,
	};

	await editIdentityProviderConnection(spAdminPage, idpConnection);

	idpConnection = {
		entityId: DEFAULT_IDP_NAME,
		forceAuthn: true,
		idpDomain: DEFAULT_IDP_URL,
		idpName: DEFAULT_IDP_NAME,
		spName: SECONDARY_SP_NAME,
		...DEFAULT_IDP_CONNECTION_VALUES,
	};

	await editIdentityProviderConnection(secondarySpAdminPage, idpConnection);

	// SP initiated SSO

	const spIntancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL
	);

	// Assert clicking Sign In button on other SP page does not auto-login

	await spIntancePage.goto(SECONDARY_SP_URL);

	await clickSignInButton(spIntancePage);

	await spIntancePage.waitForTimeout(2000);

	await expect(await spIntancePage.getByLabel('Email Address')).toBeVisible();

	// Delete newly created virtual instances, and remove from afterAll deletion

	await deleteVirtualInstance(SECONDARY_SP_NAME, localhostAdminPage);

	await deleteAfterTestProviderConnections.delete(SECONDARY_SP_NAME);

	await deleteAfterTestVirtualInstances.delete(SECONDARY_SP_NAME);
});

test('Verify the SAML configuration is not applied to the sites when ACS is disabled.  See LPS-170940.', async ({
	browser,
}) => {
	const idpAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_IDP_NAME,
		'Identity Provider'
	);

	const spAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_SP_NAME,
		'Service Provider'
	);

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	// Add site to SP

	const defaultBaseUrl = liferayConfig.environment.baseUrl;

	liferayConfig.environment.baseUrl = DEFAULT_SP_URL;

	const apiHelpers = new ApiHelpers(spAdminPage);

	liferayConfig.environment.baseUrl = defaultBaseUrl;

	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
		templateKey: 'com.liferay.site.initializer.welcome',
		templateType: 'site-initializer',
	});

	await spAdminPage.goto(`/web/${site.name}`);

	await spAdminPage.waitForTimeout(1000);

	// Configure site virtual hostname

	const siteSettingsPage = new SiteSettingsPage(spAdminPage);

	await siteSettingsPage.goToSiteSetting('Site Configuration', 'Site URL');

	const siteVirtualHostName = 'www.easy.com';

	await siteSettingsPage.page
		.getByLabel('Virtual Host')
		.fill(siteVirtualHostName);

	await siteSettingsPage.page.getByRole('button', {name: 'Save'}).click();

	await waitForAlert(siteSettingsPage.page);

	// Create IdP user

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Disable ACS on IdP

	const identityProvider: TIdentityProvider = {
		authnRequestSigningAllowsDynamicAcsUrl: false,
	};

	await configureIdentityProvider(idpAdminPage, identityProvider);

	// Assert SP initiated SSO from default SP virtual hostname works

	let spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL
	);

	await performLogout(spInstancePage);

	// Assert SP initiated SSO from site virtual hostname does not work

	spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		`http://${siteVirtualHostName}:8080`,
		false
	);

	await expect(
		await spInstancePage.getByRole('button', {name: 'Sign In'})
	).toBeVisible();

	// Remove site from SP instance

	await apiHelpers.headlessSite.deleteSite(String(site.id));
});
