/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {searchAdminPageTest} from '../../../fixtures/searchAdminPageTest';
import {serverAdministrationPageTest} from '../../../fixtures/serverAdministrationPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {
	TCustomField,
	TInputField,
} from '../../../helpers/CustomFieldTypesHelper';
import {
	DEFAULT_IDP_CONNECTION_VALUES,
	DEFAULT_SP_CONNECTION_VALUES,
	TIdpConnection,
	TSpConnection,
} from '../../../helpers/SamlProviderConnectionHelper';
import {EActions} from '../../../helpers/ServerAdministrationHelper';
import {liferayConfig} from '../../../liferay.config';
import {InstanceSettingsPage} from '../../../pages/configuration-admin-web/InstanceSettingsPage';
import {SystemSettingsPage} from '../../../pages/configuration-admin-web/SystemSettingsPage';
import {GeneralPage} from '../../../pages/instance-configuration-web/GeneralPage';
import {PagesAdminPage} from '../../../pages/layout-admin-web/PagesAdminPage';
import {ApplicationsMenuPage} from '../../../pages/product-navigation-applications-menu/ApplicationsMenuPage';
import {
	AttributeMapping,
	IdentityProviderConnectionsPage,
} from '../../../pages/saml-web/IdentityProviderConnectionsPage';
import {SamlAdminPage} from '../../../pages/saml-web/SamlAdminPage';
import {ServiceProviderConnectionsPage} from '../../../pages/saml-web/ServiceProviderConnectionsPage';
import {ServerAdministrationPage} from '../../../pages/server-admin-web/ServerAdministrationPage';
import {SiteSettingsPage} from '../../../pages/site-admin-web/SiteSettingsPage';
import {EditUserPage} from '../../../pages/users-admin-web/EditUserPage';
import {UserGroupsPage} from '../../../pages/users-admin-web/UserGroupsPage';
import {UsersAndOrganizationsPage} from '../../../pages/users-admin-web/UsersAndOrganizationsPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {performLogout} from '../../../utils/performLogin';
import {reloadUntilVisible} from '../../../utils/reloadUntilVisible';
import {waitForAlert} from '../../../utils/waitForAlert';
import {waitForLoading} from '../../osb-faro-web/main/utils/loading';
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
	createIdentityBrokerVirtualInstance,
	createIdentityProviderVirtualInstance,
	createServiceProviderVirtualInstance,
	createUser,
	deleteAfterTestProviderConnections,
	deleteAfterTestVirtualInstances,
	deleteVirtualInstance,
	performSamlSafeLogin,
	resetSamlConfiguration,
	resetSamlKeystoreManagerTarget,
	setupSamlInstances,
	updateRuntimeMetadataRefreshInterval,
	updateSamlKeystoreManagerTarget,
} from './utils/samlVirtualInstanceUtil';

export const test = mergeTests(
	applicationsMenuPageTest,
	loginTest(),
	searchAdminPageTest,
	usersAndOrganizationsPagesTest,
	serverAdministrationPageTest,
	virtualInstancesPagesTest
);

const resetAfterTestGeneralPage = new Set<string>();

let resetSystemSettings = false;

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

	for (const instanceName of resetAfterTestGeneralPage) {
		liferayConfig.environment.baseUrl = `http://${instanceName}:8080`;

		// Reset general tab

		const newPage = await performSamlSafeLogin(browser, instanceName);

		const instanceSettingsPage = new InstanceSettingsPage(newPage);

		await instanceSettingsPage.goToInstanceSetting(
			'Instance Configuration',
			'General',
			false
		);

		const generalPage = new GeneralPage(instanceSettingsPage.page);

		await generalPage.resetNavigationFields();

		await newPage.close();
	}

	for (const instanceName of deleteAfterTestProviderConnections) {
		liferayConfig.environment.baseUrl = `http://${instanceName}:8080`;

		// Reset general tab

		const newPage = await performSamlSafeLogin(browser, instanceName);

		const samlAdminPage = new SamlAdminPage(newPage);

		await samlAdminPage.configureSAML(false);

		// Delete all connections

		if ((await samlAdminPage.samlRoleField.inputValue()) !== 'idp') {
			const identityProviderConnectionsPage =
				new IdentityProviderConnectionsPage(samlAdminPage.page);

			await identityProviderConnectionsPage.goTo();

			await identityProviderConnectionsPage.deleteIdentityProviderConnections();

			await configureServiceProvider(newPage);

			await samlAdminPage.applicationsMenuPage.goToSamlAdmin();
		}

		if ((await samlAdminPage.samlRoleField.inputValue()) !== 'sp') {
			const serviceProviderConnectionsPage =
				new ServiceProviderConnectionsPage(samlAdminPage.page);

			await serviceProviderConnectionsPage.goTo();

			await serviceProviderConnectionsPage.deleteServiceProviderConnections();

			await configureIdentityProvider(newPage);
		}

		await newPage.close();
	}

	liferayConfig.environment.baseUrl = defaultBaseUrl;

	if (resetSystemSettings) {
		const newPage = await browser.newPage();

		await performLogin(newPage, 'test');

		const systemSettingsPage = new SystemSettingsPage(newPage);

		await systemSettingsPage.goToSystemSetting('Login', 'Login');

		await waitForLoading(systemSettingsPage.page);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: systemSettingsPage.page.getByRole('menuitem', {
				name: 'Reset Default Values',
			}),
			trigger: systemSettingsPage.page.getByRole('button', {
				name: 'Actions',
			}),
		});

		await waitForAlert(systemSettingsPage.page);

		resetSystemSettings = false;
	}
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

test('LPD-32187 AC1 TC1: Verify IdP initiated SSO with provided RelayState parameter redirects the user to designated RelayState.', async ({
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

	// Create a new page on the SP Instance

	const pagesAdminPage = new PagesAdminPage(spAdminPage);

	await pagesAdminPage.goto();

	const pageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: pageTitle,
	});

	const spNewPagePath = '/web/guest/' + pageTitle;

	// Create new user in IdP instance

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Execute IdP initiated SSO

	const idpInstancePage = await performIdpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_IDP_URL,
		spNewPagePath,
		DEFAULT_SP_NAME
	);

	// Assert authentication and SP redirection

	await expect(
		await idpInstancePage.getByTitle('User Profile Menu')
	).toBeVisible();

	await expect(await idpInstancePage.url()).toContain(
		DEFAULT_SP_URL + spNewPagePath
	);
});

test('LPD-32187 AC2 TC2: Verify unsuccessful IdP initiated SSO with provided RelayState parameter redirects the user to the login page.', async ({
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

	// Create a new page on the SP Instance

	const pagesAdminPage = new PagesAdminPage(spAdminPage);

	await pagesAdminPage.goto();

	const pageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: pageTitle,
	});

	const spNewPagePath = '/web/guest/' + pageTitle;

	// Execute unsuccessful IdP initiated SSO

	const idpInstancePage = await browser.newPage({
		baseURL: DEFAULT_IDP_URL,
	});

	await idpInstancePage.goto(
		`${DEFAULT_IDP_URL}/c/portal/saml/sso?entityId=${DEFAULT_SP_NAME}&RelayState=${spNewPagePath}`
	);

	await idpInstancePage
		.getByLabel('Email Address')
		.waitFor({timeout: 30 * 1000});

	// Attempt authentication, but provide invalid email address

	await idpInstancePage.waitForTimeout(1000);
	await idpInstancePage
		.getByLabel('Email Address')
		.fill('invalidEmail@liferay.com');
	await idpInstancePage.getByLabel('Password').fill('test');
	await idpInstancePage.getByRole('button', {name: 'Sign In'}).click();

	// Assert failed authentication and still on IdP login page

	await expect(await idpInstancePage.getByText('Error:')).toBeVisible();

	await expect(
		await idpInstancePage.getByLabel('Email Address')
	).toBeVisible();

	await expect(await idpInstancePage.url()).toContain(DEFAULT_IDP_URL);
});

test('LPD-32189 AC1 TC1: Verify IdP initiated SLO redirects user to c/portal/saml/slo_logout where all SP instances are logged out.  After, user should be redirected back to Default Logout Page configuration value.', async ({
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

	// Create new page on IdP Instance

	const pagesAdminPage = new PagesAdminPage(idpAdminPage);

	await pagesAdminPage.goto();

	const pageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: pageTitle,
	});

	const idpNewPagePath = '/web/guest/' + pageTitle;

	// Configure new page as the Default Logout Page

	const instanceSettingsPage = new InstanceSettingsPage(idpAdminPage);

	await instanceSettingsPage.goToInstanceSetting(
		'Instance Configuration',
		'General',
		false
	);

	const generalPage = new GeneralPage(instanceSettingsPage.page);

	await generalPage.editDefaultLogoutPage(idpNewPagePath);

	resetAfterTestGeneralPage.add(DEFAULT_IDP_NAME);

	// Create IdP User

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// SP initiated SSO

	const newPage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL
	);

	// Navigate to Idp and initiate SLO

	await newPage.goto(DEFAULT_IDP_URL);

	await newPage.getByTitle('User Profile Menu').click();

	await newPage.getByRole('menuitem', {name: 'Sign Out'}).click();

	await newPage.waitForTimeout(3000);

	// Expect to redirect to IdP slo_logout page

	expect(await newPage.url()).toContain(
		`${DEFAULT_IDP_URL}/c/portal/saml/slo_logout`
	);

	// Expect to be redirected back to Default Landing Page configuration value

	await newPage.waitForTimeout(5000);

	expect(await newPage.url()).toContain(DEFAULT_IDP_URL + idpNewPagePath);
});

test('LPD-32208 AC1 TC2 and TC3: Verify SP initiated SSO with RelayState and invalid credentials redirects/maintains the user to the IdP login page, and then successful auth redirects user back to provided RelayState.', async ({
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

	// Create new user in IdP instance

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Create a new page on the SP Instance

	const pagesAdminPage = new PagesAdminPage(spAdminPage);

	await pagesAdminPage.goto();

	const pageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: pageTitle,
	});

	const spNewPageUrl = DEFAULT_SP_URL + '/web/guest/' + pageTitle;

	// Perform SP initiated SSO from the new page with invalid credentials

	const spInstancePage = await performSpInitiatedSSO(
		browser,
		'invalid@test.com',
		spNewPageUrl,
		false
	);

	// Verify unsuccessful authentication, and user remains on IdP login page

	await expect(await spInstancePage.getByText('Error:')).toBeVisible();

	await expect(
		await spInstancePage.getByRole('button', {name: 'Sign In'})
	).toBeVisible();

	expect(await spInstancePage.url()).toContain(DEFAULT_IDP_URL);

	// TC2 end, TC3 begin: from the same page, correctly authenticate user

	await spInstancePage
		.getByLabel('Email Address')
		.fill(userAccount.emailAddress);
	await spInstancePage.getByLabel('Password').fill('test');
	await spInstancePage.getByRole('button', {name: 'Sign In'}).click();

	// Assert authentication and SP redirection

	await expect(
		await spInstancePage.getByTitle('User Profile Menu')
	).toBeVisible();

	await expect(await spInstancePage.url()).toContain(spNewPageUrl);
});

test('LPD-32208 AC1 TC4: Verify SP initiated SSO with RelayState redirects user back to RelayState, but display error message and user is not authenticated if IdP auth is successful but SP auth is not.', async ({
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

	// Create a new page on the SP Instance

	const pagesAdminPage = new PagesAdminPage(spAdminPage);

	await pagesAdminPage.goto();

	const pageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: pageTitle,
	});

	const spNewPageUrl = DEFAULT_SP_URL + '/web/guest/' + pageTitle;

	// Perform SP initiated SSO from the new page

	const spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		spNewPageUrl,
		false
	);

	// Verify unsuccessful SP auth

	await expect(
		await spInstancePage.getByText(
			`Your user ${userAccount.emailAddress} could not be logged in`
		)
	).toBeVisible();

	await expect(
		await spInstancePage.getByRole('button', {name: 'Sign In'})
	).toBeVisible();

	// Verify user is taken back to RelayState

	expect(await spInstancePage.url()).toContain(spNewPageUrl);

	// Go to IdP instance and verify IdP auth was successful

	await spInstancePage.goto(DEFAULT_IDP_URL);

	await expect(
		await spInstancePage.getByTitle('User Profile Menu')
	).toBeVisible();
});

test('LPD-32210 AC1 TC1: Verify IdP initiated SSO with same-site page redirect parameter redirects the user to designated page.', async ({
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

	// Create a new page on the IdP Instance

	const pagesAdminPage = new PagesAdminPage(idpAdminPage);

	await pagesAdminPage.goto();

	const pageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: pageTitle,
	});

	// Create new user in IdP instance

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Execute IdP initiated SSO with redirect parameter

	const pagePath = '/web/guest/' + pageTitle;

	const newPage = await performSamlSafeLogin(
		browser,
		DEFAULT_IDP_NAME,
		'?p_p_id=com_liferay_login_web_portlet_LoginPortlet&' +
			'p_p_state=maximized&' +
			'_com_liferay_login_web_portlet_LoginPortlet_redirect=' +
			pagePath.replace('/', '%2F'),
		'@liferay.com',
		undefined,
		userAccount.alternateName
	);

	// Verify we have been redirected and logged in

	expect(await newPage.url()).toContain(DEFAULT_IDP_URL + pagePath);

	await expect(await newPage.getByTitle('User Profile Menu')).toBeVisible();
});

test('LPD-32210 AC1 TC2: Verify IdP initiated SSO with different instance redirect parameter redirects the user to designated instance.', async ({
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

	// Create new user in IdP instance

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Execute IdP initiated SSO with redirect parameter

	const newPage = await browser.newPage();

	const escapedSpUrl = DEFAULT_SP_URL.replace('/', '%2F').replace(':', '%3A');

	await newPage.goto(
		DEFAULT_IDP_URL +
			'?p_p_id=com_liferay_login_web_portlet_LoginPortlet&' +
			'p_p_state=maximized&' +
			'_com_liferay_login_web_portlet_LoginPortlet_redirect=' +
			escapedSpUrl
	);

	await newPage.getByLabel('Email Address').fill(userAccount.emailAddress);
	await newPage.getByLabel('Password').fill('test');
	await newPage.getByRole('button', {name: 'Sign In'}).click();
	await newPage.waitForTimeout(5000);

	// Verify we have been redirected to SP instance

	expect(await newPage.url()).toContain(DEFAULT_SP_URL);
});

test('LPD-32210 AC1 TC3: Verify IdP initiated SSO with a configured Default Landing Page redirects user properly.', async ({
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

	// Create new page on IdP Instance

	const pagesAdminPage = new PagesAdminPage(idpAdminPage);

	await pagesAdminPage.goto();

	const pageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: pageTitle,
	});

	const idpNewPagePath = '/web/guest/' + pageTitle;

	// Configure new page as the Default Landing Page

	const instanceSettingsPage = new InstanceSettingsPage(idpAdminPage);

	await instanceSettingsPage.goToInstanceSetting(
		'Instance Configuration',
		'General',
		false
	);

	const generalPage = new GeneralPage(instanceSettingsPage.page);

	await generalPage.editDefaultLandingPage(idpNewPagePath);

	resetAfterTestGeneralPage.add(DEFAULT_IDP_NAME);

	// Create IdP User

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// IdP initiated SSO

	const newPage = await browser.newPage();

	await performLogin(newPage, userAccount.alternateName, DEFAULT_IDP_URL);

	await newPage.getByTitle('User Profile Menu').waitFor({timeout: 30 * 1000});

	// Expect to be redirected back to Default Landing Page configuration value

	await newPage.waitForTimeout(5000);

	expect(await newPage.url()).toContain(DEFAULT_IDP_URL + idpNewPagePath);
});

test('LPD-32210 AC1 TC4: Verify IdP initiated SSO with no redirection params/configs applied redirects user to the page they initiated SSO from.', async ({
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

	// Create new page on IdP Instance

	const pagesAdminPage = new PagesAdminPage(idpAdminPage);

	await pagesAdminPage.goto();

	const pageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: pageTitle,
	});

	const idpNewPageUrl = DEFAULT_IDP_URL + '/web/guest/' + pageTitle;

	// Create IdP User

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// IdP initiated SSO from new page

	const newPage = await browser.newPage();

	await performLogin(newPage, userAccount.alternateName, idpNewPageUrl);

	await newPage.waitForTimeout(5000);

	// Verify user is logged in

	expect(await newPage.getByTitle('User Profile Menu')).toBeVisible();

	// Expect to be redirected back to page SSO was initiated from

	expect(await newPage.url()).toContain(idpNewPageUrl);
});

test('LPD-32210 AC1 TC5: Verify unsuccessful IdP initiated SSO with any redirection params/configs applied redirects user to current login page.', async ({
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

	// Create new page on IdP Instance

	const pagesAdminPage = new PagesAdminPage(idpAdminPage);

	await pagesAdminPage.goto();

	const pageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: pageTitle,
	});

	const idpNewPagePath = '/web/guest/' + pageTitle;

	// Configure new page as the Default Landing Page

	const instanceSettingsPage = new InstanceSettingsPage(idpAdminPage);

	await instanceSettingsPage.goToInstanceSetting(
		'Instance Configuration',
		'General',
		false
	);

	const generalPage = new GeneralPage(instanceSettingsPage.page);

	await generalPage.editDefaultLandingPage(idpNewPagePath);

	resetAfterTestGeneralPage.add(DEFAULT_IDP_NAME);

	// Dynamically retrieve home URL

	const newPage = await browser.newPage();

	await newPage.goto(DEFAULT_IDP_URL + '/c/portal/layout');

	const homeUrl = await newPage.url();

	// Set new page as login redirect parameter

	const loginPageParams =
		'?p_p_id=com_liferay_login_web_portlet_LoginPortlet&' +
		'p_p_state=maximized&' +
		'_com_liferay_login_web_portlet_LoginPortlet_redirect=%2F' +
		pageTitle;

	// Execute unsuccessful IdP initiated SSO

	await newPage.goto(homeUrl + loginPageParams);

	await newPage.getByLabel('Email Address').fill('invalid@liferay.com');
	await newPage.getByLabel('Password').fill('invalid');
	await newPage.getByRole('button', {name: 'Sign In'}).click();
	await newPage.waitForTimeout(5000);

	// Verify unsuccessful authentication

	await expect(await newPage.getByText('Error:')).toBeVisible();

	// Verify user is not logged in and still on login portlet page

	await expect(await newPage.getByLabel('Email Address')).toBeVisible();

	await expect(await newPage.url()).toContain(homeUrl);
});

test('LPD-32213 AC1 TC1 and TC5: Verify SP initiated SSO from a restricted resource with prompt enabled redirects user back to resource after authentication.', async ({
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

	// Create a user on the IdP instance

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Create a new page on the SP Instance

	const pagesAdminPage = new PagesAdminPage(spAdminPage);

	await pagesAdminPage.goto();

	const pageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: pageTitle,
	});

	// Remove guest view permission from new page

	await pagesAdminPage.goto();

	await pagesAdminPage.changePagesPermissions(
		[pageTitle],
		['guest_ACTION_VIEW']
	);

	const spNewPageUrl = DEFAULT_SP_URL + '/web/guest/' + pageTitle;

	// Enable Prompt Enabled option

	const siteSettingsPage = new SiteSettingsPage(spAdminPage);

	await siteSettingsPage.goToSiteSetting('Login', 'Login');

	await waitForLoading(siteSettingsPage.page);

	await siteSettingsPage.page.getByLabel('Prompt Enabled').setChecked(true);

	if (
		await siteSettingsPage.page
			.getByRole('button', {name: 'Save'})
			.isVisible()
	) {
		await siteSettingsPage.page.getByRole('button', {name: 'Save'}).click();
	}
	else {
		await siteSettingsPage.page
			.getByRole('button', {name: 'Update'})
			.click();
	}

	await waitForAlert(siteSettingsPage.page);

	// Attempt to access resource as unauthenticated user

	const newPage = await browser.newPage({
		baseURL: DEFAULT_SP_URL,
	});

	await newPage.goto(spNewPageUrl);

	// Verify redirected to IdP for authentication

	await expect(await newPage.getByLabel('Email Address')).toBeVisible();

	expect(await newPage.url()).toContain(DEFAULT_IDP_URL);

	// Provide invalid credentials to test LPD-32213 TC4

	await newPage.getByLabel('Email Address').fill(userAccount.emailAddress);
	await newPage.getByLabel('Password').fill('invalid');
	await newPage.getByRole('button', {name: 'Sign In'}).click();
	await newPage.waitForTimeout(2000);

	// Expect to remain unauthenticated on IdP

	await expect(await newPage.getByLabel('Email Address')).toBeVisible();

	expect(await newPage.url()).toContain(DEFAULT_IDP_URL);

	// End TC4.  Successfully authenticate on IdP to finish SSO

	await newPage.getByLabel('Email Address').fill(userAccount.emailAddress);
	await newPage.getByLabel('Password').fill('test');
	await newPage.getByRole('button', {name: 'Sign In'}).click();

	// Verify user is logged in

	await newPage.getByTitle('User Profile Menu').waitFor({timeout: 30 * 1000});

	// Verify user is redirected back to restricted resource

	expect(await newPage.url()).toContain(spNewPageUrl);

	// Clear Prompt Enabled

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: siteSettingsPage.page.getByRole('menuitem', {
			name: 'Reset Default Values',
		}),
		trigger: siteSettingsPage.page.getByRole('button', {
			name: 'Actions',
		}),
	});

	await waitForAlert(siteSettingsPage.page);
});

test('LPD-32213 AC1 TC2: Verify after successful SP initiated SSO with auth.forward.by.last.path=true, the user is redirected to the page SSO was initiated from, regardless of Default Landing Page or Home Url.', async ({
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

	// Create a user on the IdP instance

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Create a new page on the SP Instance to initiate SSO from

	const pagesAdminPage = new PagesAdminPage(spAdminPage);

	await pagesAdminPage.goto();

	const pageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: pageTitle,
	});

	const spSsoPageUrl = DEFAULT_SP_URL + '/web/guest/' + pageTitle;

	// Configure Default Landing Page on SP instance

	await pagesAdminPage.goto();

	const defaultLandingPageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: defaultLandingPageTitle,
	});

	const defaultLandingPagePath = '/web/guest/' + defaultLandingPageTitle;

	// Configure Home Url on SP instance

	await pagesAdminPage.goto();

	const homeUrlPageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: homeUrlPageTitle,
	});

	const homeUrlPagePath = '/web/guest/' + homeUrlPageTitle;

	// Configure Default Landing Page and Home Url

	const instanceSettingsPage = new InstanceSettingsPage(spAdminPage);

	await instanceSettingsPage.goToInstanceSetting(
		'Instance Configuration',
		'General',
		false
	);

	const generalPage = new GeneralPage(instanceSettingsPage.page);

	await generalPage.editDefaultLandingPage(defaultLandingPagePath);
	await generalPage.editHomeUrl(homeUrlPagePath);

	resetAfterTestGeneralPage.add(DEFAULT_SP_NAME);

	// SP initiated SSO from specified page

	const newPage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		spSsoPageUrl
	);

	// Expect to be redirected back to page SSO was initiated from

	expect(await newPage.url()).toContain(spSsoPageUrl);
});

test('LPD-32214 AC1 TC1: Verify SP initiated SLO logs user out of IdP and SP, then redirects back to Default Logout Page configuration value of SP.', async ({
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

	// Create new page on SP Instance

	const pagesAdminPage = new PagesAdminPage(spAdminPage);

	await pagesAdminPage.goto();

	const pageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: pageTitle,
	});

	const defaultLogoutPagePath = '/web/guest/' + pageTitle;

	// Configure new page as the Default Logout Page

	const instanceSettingsPage = new InstanceSettingsPage(spAdminPage);

	await instanceSettingsPage.goToInstanceSetting(
		'Instance Configuration',
		'General',
		false
	);

	const generalPage = new GeneralPage(instanceSettingsPage.page);

	await generalPage.editDefaultLogoutPage(defaultLogoutPagePath);

	resetAfterTestGeneralPage.add(DEFAULT_IDP_NAME);

	// Create IdP User

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// SP initiated SSO

	const newPage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL
	);

	// SP initiated SLO

	await performLogout(newPage);

	// Expect to be redirected back to Default Logout Page configuration value

	await newPage.waitForTimeout(5000);

	expect(await newPage.url()).toContain(
		DEFAULT_SP_URL + defaultLogoutPagePath
	);

	// Verify the IdP was also logged out

	await newPage.goto(DEFAULT_IDP_URL);

	expect(await newPage.getByRole('button', {name: 'Sign In'})).toBeVisible();
});

test('LPD-57886: Verify SP initiated SSO redirects to the IdP from a staged site', async ({
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

	// Create new site in SP instance

	const defaultBaseUrl = liferayConfig.environment.baseUrl;

	liferayConfig.environment.baseUrl = DEFAULT_SP_URL;

	const apiHelpers = new ApiHelpers(spAdminPage);

	liferayConfig.environment.baseUrl = defaultBaseUrl;

	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
		templateKey: 'com.liferay.site.initializer.welcome',
		templateType: 'site-initializer',
	});

	// Enable local live staging from the SP instance

	liferayConfig.environment.baseUrl = DEFAULT_SP_URL;

	await apiHelpers.jsonWebServicesStaging.enableLocalStaging({
		groupId: site.id,
	});

	liferayConfig.environment.baseUrl = defaultBaseUrl;

	// Verify the staging site page is valid by visiting it as an admin

	const stagingSitePageUrl =
		DEFAULT_SP_URL + '/web' + site.friendlyUrlPath + '-staging';

	await spAdminPage.goto(stagingSitePageUrl);

	// Enabling local staging takes time, so reload the page until finished

	await reloadUntilVisible({
		maxAttempts: 10,
		myLocator: spAdminPage.getByText('You are viewing the staged version'),
		page: spAdminPage,
	});

	await expect(
		await spAdminPage.getByText('You are viewing the staged version')
	).toBeVisible();

	// Go to staging site and expect auto

	const spStagingSitePage = await browser.newPage();

	await spStagingSitePage.goto(stagingSitePageUrl);

	await spStagingSitePage.waitForTimeout(1000);

	// Perform SP initiated SSO

	await clickSignInButton(spStagingSitePage);

	await spStagingSitePage.waitForTimeout(2000);

	await expect(await spStagingSitePage.url()).toContain(DEFAULT_IDP_URL);
});

test('LPD-56043 and LPD-56046: Verify User and User Group Provisioning source is stored as an Expando Value after IdP import', async ({
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

	// Update SP Connection to include userGroups in attribute list

	const spConnection: TSpConnection = {
		entityId: DEFAULT_SP_NAME,
		idpName: DEFAULT_IDP_NAME,
		spDomain: `http://${DEFAULT_SP_NAME}:8080`,
		spName: DEFAULT_SP_NAME,
		...DEFAULT_SP_CONNECTION_VALUES,
	};

	spConnection.attributes = spConnection.attributes + '\nuserGroups';

	await editServiceProviderConnection(idpAdminPage, spConnection);

	// Create a user on the IdP instance

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Create a new User Group and assign the user to it

	const idpApiHelpers = new ApiHelpers(idpAdminPage, DEFAULT_IDP_URL);

	const userGroup = await idpApiHelpers.headlessAdminUser.postUserGroup();

	const userGroupsPage = await new UserGroupsPage(idpAdminPage);

	await userGroupsPage.goto(false);

	await (
		await userGroupsPage.userGroupsTableRowActions(userGroup.name)
	).click();
	await userGroupsPage.assignMembersMenuItem.click();

	await userGroupsPage.newUserButton.click();

	await (
		await userGroupsPage.addUsersTable.rowCheckbox(userAccount.name)
	).check();

	await userGroupsPage.addUsersIFrameAddButton.click();

	await waitForAlert(userGroupsPage.page);

	// Perform SP initiated SSO

	const spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL
	);

	expect(await spInstancePage.url()).toContain(DEFAULT_SP_URL);

	// Run Groovy script to verify Expando Value was added for provisioned User

	const localhostAdminPage = await browser.newPage();

	await performLogin(localhostAdminPage, 'test');

	const applicationsMenuPage = new ApplicationsMenuPage(localhostAdminPage);

	await applicationsMenuPage.goToServerAdministration();

	const spApiHelpers = new ApiHelpers(spAdminPage, DEFAULT_SP_URL);

	const spCompany =
		await spApiHelpers.jsonWebServicesCompany.getCompanyByWebId(
			DEFAULT_SP_NAME
		);

	const spUserAccount =
		await spApiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			userAccount.emailAddress
		);

	let script = `
		import com.liferay.expando.kernel.service.ExpandoValueLocalServiceUtil;
		import com.liferay.portal.kernel.model.User;
		
		out.println(
			ExpandoValueLocalServiceUtil.getValue(
				${spCompany.companyId}, User.class.getName(), "CUSTOM_FIELDS",
						"samlIdpEntityId", ${spUserAccount.id}));
		`;

	const serverAdministrationPage = new ServerAdministrationPage(
		localhostAdminPage
	);

	await serverAdministrationPage.executeScript(script);

	await expect(
		await localhostAdminPage.getByText(DEFAULT_IDP_NAME)
	).toBeVisible();

	// Do the same for the User Group

	const spUserGroup = await spApiHelpers.headlessAdminUser.getUserGroupByName(
		userGroup.name
	);

	script = `
		import com.liferay.expando.kernel.service.ExpandoValueLocalServiceUtil;
		import com.liferay.portal.kernel.model.UserGroup;
		
		out.println(
			ExpandoValueLocalServiceUtil.getValue(
				${spCompany.companyId}, UserGroup.class.getName(),
				"CUSTOM_FIELDS", "samlIdpEntityId", ${spUserGroup.id}));
		`;

	await serverAdministrationPage.executeScript(script);

	await expect(
		await localhostAdminPage.getByText(DEFAULT_IDP_NAME)
	).toBeVisible();
});

test('LPD-56047: Verify User Group membership deletions from the IdP only apply to the SP if the provisioning IdP is the same', async ({
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

	// Create an additional IdP virtual instance and connect it to the SP

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

	// Update SP Connection to include userGroups in attribute list

	const spConnection: TSpConnection = {
		entityId: DEFAULT_SP_NAME,
		idpName: DEFAULT_IDP_NAME,
		spDomain: `http://${DEFAULT_SP_NAME}:8080`,
		spName: DEFAULT_SP_NAME,
		...DEFAULT_SP_CONNECTION_VALUES,
	};

	spConnection.attributes = spConnection.attributes + '\nuserGroups';

	await editServiceProviderConnection(idpAdminPage, spConnection);

	spConnection.idpName = SECONDARY_IDP_NAME;

	await editServiceProviderConnection(secondaryIdpAdminPage, spConnection);

	// Create a user on both IdPs, with identical information

	const userId = getRandomInt();

	const secondaryUserAccount = await createUser(
		secondaryIdpAdminPage,
		SECONDARY_IDP_NAME,
		userId
	);
	const userAccount = await createUser(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		userId
	);

	// Create a new User Group on the IdP and assign the user to it

	const idpApiHelpers = new ApiHelpers(idpAdminPage, DEFAULT_IDP_URL);

	const userGroup = await idpApiHelpers.headlessAdminUser.postUserGroup();

	const userGroupsPage = await new UserGroupsPage(idpAdminPage);

	await userGroupsPage.goto(false);

	await (
		await userGroupsPage.userGroupsTableRowActions(userGroup.name)
	).click();
	await userGroupsPage.assignMembersMenuItem.click();

	await userGroupsPage.newUserButton.click();

	await (
		await userGroupsPage.addUsersTable.rowCheckbox(userAccount.name)
	).check();

	await userGroupsPage.addUsersIFrameAddButton.click();

	await waitForAlert(userGroupsPage.page);

	// Do the same for the secondary IdP

	const secondaryIdpApiHelpers = new ApiHelpers(
		secondaryIdpAdminPage,
		SECONDARY_IDP_URL
	);

	const secondaryUserGroup =
		await secondaryIdpApiHelpers.headlessAdminUser.postUserGroup();

	const secondaryUserGroupsPage = await new UserGroupsPage(
		secondaryIdpAdminPage
	);

	await secondaryUserGroupsPage.goto(false);

	await (
		await secondaryUserGroupsPage.userGroupsTableRowActions(
			secondaryUserGroup.name
		)
	).click();
	await secondaryUserGroupsPage.assignMembersMenuItem.click();

	await secondaryUserGroupsPage.newUserButton.click();

	await (
		await secondaryUserGroupsPage.addUsersTable.rowCheckbox(
			secondaryUserAccount.name
		)
	).check();

	await secondaryUserGroupsPage.addUsersIFrameAddButton.click();

	await waitForAlert(secondaryUserGroupsPage.page);

	// Perform SP initiated SSO from the default IdP

	let spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL,
		true,
		DEFAULT_IDP_NAME
	);

	expect(await spInstancePage.url()).toContain(DEFAULT_SP_URL);

	await performLogout(spInstancePage);

	// Do the same from the secondary IdP

	spInstancePage = await performSpInitiatedSSO(
		browser,
		secondaryUserAccount.emailAddress,
		DEFAULT_SP_URL,
		true,
		SECONDARY_IDP_NAME
	);

	expect(await spInstancePage.url()).toContain(DEFAULT_SP_URL);

	await performLogout(spInstancePage);

	const applicationsMenuPage = new ApplicationsMenuPage(localhostAdminPage);

	await applicationsMenuPage.goToServerAdministration();

	const spApiHelpers = new ApiHelpers(spAdminPage, DEFAULT_SP_URL);

	const spCompany =
		await spApiHelpers.jsonWebServicesCompany.getCompanyByWebId(
			DEFAULT_SP_NAME
		);

	const secondarySpUserGroup =
		await spApiHelpers.headlessAdminUser.getUserGroupByName(
			secondaryUserGroup.name
		);

	let script = `
			import com.liferay.expando.kernel.service.ExpandoValueLocalServiceUtil;
			import com.liferay.portal.kernel.model.UserGroup;
			
			out.println(
				ExpandoValueLocalServiceUtil.getValue(
					${spCompany.companyId}, UserGroup.class.getName(),
					"CUSTOM_FIELDS", "samlIdpEntityId", ${secondarySpUserGroup.id}));
			`;

	const serverAdministrationPage = new ServerAdministrationPage(
		localhostAdminPage
	);

	await test.step('Verify Expando Values were added for both User Groups', async () => {
		await serverAdministrationPage.executeScript(script);

		await expect(
			await localhostAdminPage.getByText(SECONDARY_IDP_NAME)
		).toBeVisible();

		const spUserGroup =
			await spApiHelpers.headlessAdminUser.getUserGroupByName(
				userGroup.name
			);

		script = `
			import com.liferay.expando.kernel.service.ExpandoValueLocalServiceUtil;
			import com.liferay.portal.kernel.model.UserGroup;
			
			out.println(
				ExpandoValueLocalServiceUtil.getValue(
					${spCompany.companyId}, UserGroup.class.getName(),
					"CUSTOM_FIELDS", "samlIdpEntityId", ${spUserGroup.id}));
			`;

		await serverAdministrationPage.executeScript(script);

		await expect(
			await localhostAdminPage.getByText(DEFAULT_IDP_NAME)
		).toBeVisible();
	});

	await test.step('Remove the Default IdP User Group Membership', async () => {
		await expect(
			userGroupsPage.userGroupUsersTable.cell(userAccount.name)
		).toBeVisible();

		await (
			await userGroupsPage.userGroupUsersTable.rowActions(
				userAccount.name
			)
		).click();
		await userGroupsPage.removeUserMenuItem.click();

		await waitForAlert(userGroupsPage.page);

		await expect(userGroupsPage.noUsersMessage).toBeVisible();
	});

	await test.step('Perform SP initiated SSO using the Secondary IdP', async () => {
		spInstancePage = await performSpInitiatedSSO(
			browser,
			secondaryUserAccount.emailAddress,
			DEFAULT_SP_URL,
			true,
			SECONDARY_IDP_NAME
		);

		expect(await spInstancePage.url()).toContain(DEFAULT_SP_URL);

		await performLogout(spInstancePage);
	});

	await test.step('LPD-56047 AC1 TC2: Verify the User Group membership from the Default IdP was not removed', async () => {
		await serverAdministrationPage.executeScript(script);

		await expect(
			await localhostAdminPage.getByText(DEFAULT_IDP_NAME)
		).toBeVisible();
	});

	await test.step('Perform SP initiated SSO using the Default IdP', async () => {
		spInstancePage = await performSpInitiatedSSO(
			browser,
			secondaryUserAccount.emailAddress,
			DEFAULT_SP_URL,
			true,
			DEFAULT_IDP_NAME
		);

		expect(await spInstancePage.url()).toContain(DEFAULT_SP_URL);

		await performLogout(spInstancePage);
	});

	await test.step('LPD-56047 AC1 TC1: Verify the User Group membership was removed', async () => {
		await serverAdministrationPage.executeScript(script);

		await expect(
			await localhostAdminPage.getByText(DEFAULT_IDP_NAME)
		).toBeHidden();
	});
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

test('Verify during SP initiated SSO, RelayState is correct and present regardless of VM cache.  Also covers LPD-32208 AC1 TC1.', async ({
	applicationsMenuPage,
	browser,
	serverAdministrationPage,
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

	// Create a user on the IdP instance

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	// Create a new page on the SP Instance

	const pagesAdminPage = new PagesAdminPage(spAdminPage);

	await pagesAdminPage.goto();

	const pageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: pageTitle,
	});

	const spNewPageUrl = DEFAULT_SP_URL + '/web/guest/' + pageTitle;

	// Perform Sp initiated SSO with the new user from the new page

	const spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		spNewPageUrl
	);

	// Assert user is redirected back to new page after SSO

	expect(await spInstancePage.url()).toEqual(spNewPageUrl);

	await performLogout(spInstancePage);

	await spInstancePage.waitForTimeout(8000);

	// Perform Sp to IdP SSO redirection only, no authentication yet

	await spInstancePage.goto(spNewPageUrl);

	await clickSignInButton(spInstancePage);

	// Reset VM cache to clear relayState cache (not relevant for LPD-32208)

	await applicationsMenuPage.goToServerAdministration();

	await serverAdministrationPage.executeAction(EActions.CLEAR_VM_CACHE);

	// Authenticate on IdP to finish SSO

	await spInstancePage
		.getByLabel('Email Address')
		.fill(userAccount.emailAddress);
	await spInstancePage.getByLabel('Password').fill('test');
	await spInstancePage.getByLabel('Remember Me').check();
	await spInstancePage.getByRole('button', {name: 'Sign In'}).click();

	await spInstancePage
		.getByTitle('User Profile Menu')
		.waitFor({timeout: 30 * 1000});

	// Assert we are redirected to correct URL, even without relayState cache

	expect(await spInstancePage.url()).toEqual(spNewPageUrl);
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
		undefined,
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

	const site1 = await apiHelpers.headlessSite.createSite({
		name: site1Name,
		templateKey: 'com.liferay.site.initializer.welcome',
		templateType: 'site-initializer',
	});

	const site2 = await apiHelpers.headlessSite.createSite({
		name: site2Name,
		templateKey: 'com.liferay.site.initializer.welcome',
		templateType: 'site-initializer',
	});

	await secondarySpAdminPage.goto(`/web/${site1Name}`);

	await secondarySpAdminPage.waitForTimeout(1000);

	let siteSettingsPage = new SiteSettingsPage(secondarySpAdminPage);

	await siteSettingsPage.goToSiteSetting(
		'Site Configuration',
		'Site URL',
		site1.friendlyUrlPath
	);

	const site1VirtualHostName = 'www.easy.com';

	await siteSettingsPage.page
		.getByLabel('Virtual Host')
		.fill(site1VirtualHostName);

	await siteSettingsPage.page.getByRole('button', {name: 'Save'}).click();

	await waitForAlert(siteSettingsPage.page);

	await secondarySpAdminPage.goto(`/web/${site2Name}`);

	await secondarySpAdminPage.waitForTimeout(1000);

	siteSettingsPage = new SiteSettingsPage(secondarySpAdminPage);

	await siteSettingsPage.goToSiteSetting(
		'Site Configuration',
		'Site URL',
		site2.friendlyUrlPath
	);

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

	await siteSettingsPage.goToSiteSetting(
		'Site Configuration',
		'Site URL',
		site.friendlyUrlPath
	);

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

test('LPD-37323 AC1 TC1: Liferay as both IdP and SP handles the SSO flow by triggering an SP-initiated SSO to the correct external IdP', async ({
	browser,
}) => {
	const localhostAdminPage = await browser.newPage();

	await performLogin(localhostAdminPage, 'test');

	const ibAdminPage = await createIdentityBrokerVirtualInstance(
		browser,
		localhostAdminPage,
		SECONDARY_IDP_NAME
	);

	const idpAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_IDP_NAME,
		'Identity Provider'
	);

	// Clear default connections and make new ones with the both IdP and SP instance

	const serviceProviderConnectionsPage = new ServiceProviderConnectionsPage(
		idpAdminPage
	);

	await serviceProviderConnectionsPage.goTo();

	await serviceProviderConnectionsPage.deleteServiceProviderConnections();

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		ibAdminPage,
		SECONDARY_IDP_NAME
	);

	const spAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_SP_NAME,
		'Service Provider'
	);

	const identityProviderConnectionsPage = new IdentityProviderConnectionsPage(
		spAdminPage
	);

	await identityProviderConnectionsPage.goTo();

	await identityProviderConnectionsPage.deleteIdentityProviderConnections();

	await connectSpAndIdp(
		ibAdminPage,
		SECONDARY_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	const spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL
	);

	expect(await spInstancePage.url()).toContain(DEFAULT_SP_URL);

	await expect(
		await spInstancePage.getByTitle('User Profile Menu')
	).toBeVisible();
});

test('LPD-37323 AC2/AC4 TC2: User switches between apps. When already logged in an app, if the User is redirected to another app that needs login, Liferay as both IdP and SP handles the connection', async ({
	browser,
}) => {
	const localhostAdminPage = await browser.newPage();

	await performLogin(localhostAdminPage, 'test');

	// Enable Prompt Enabled option

	const systemSettingsPage = new SystemSettingsPage(localhostAdminPage);

	await systemSettingsPage.goToSystemSetting('Login', 'Login');

	await waitForLoading(systemSettingsPage.page);

	await systemSettingsPage.page.getByLabel('Prompt Enabled').setChecked(true);

	await systemSettingsPage.page
		.getByRole('button', {name: /save|update/i})
		.click();

	await waitForAlert(systemSettingsPage.page);

	resetSystemSettings = true;

	const ibAdminPage = await createIdentityBrokerVirtualInstance(
		browser,
		localhostAdminPage,
		SECONDARY_IDP_NAME
	);

	const secondarySpAdminPage = await createServiceProviderVirtualInstance(
		browser,
		SECONDARY_SP_NAME,
		SECONDARY_SP_NAME,
		localhostAdminPage
	);

	await connectSpAndIdp(
		ibAdminPage,
		SECONDARY_IDP_NAME,
		secondarySpAdminPage,
		SECONDARY_SP_NAME
	);

	// Create a new page on the secondary SP Instance

	const pagesAdminPage = new PagesAdminPage(secondarySpAdminPage);

	await pagesAdminPage.goto();

	const pageTitle = getRandomString();

	await pagesAdminPage.createNewPage({
		name: pageTitle,
	});

	const spNewPageUrl = SECONDARY_SP_URL + '/web/guest/' + pageTitle;

	// Remove guest view permission from new page

	await pagesAdminPage.goto();

	await pagesAdminPage.changePagesPermissions(
		[pageTitle],
		['guest_ACTION_VIEW']
	);

	const idpAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_IDP_NAME,
		'Identity Provider'
	);

	// Clear default connections and make new ones with the both IdP and SP instance

	const serviceProviderConnectionsPage = new ServiceProviderConnectionsPage(
		idpAdminPage
	);

	await serviceProviderConnectionsPage.goTo();

	await serviceProviderConnectionsPage.deleteServiceProviderConnections();

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		ibAdminPage,
		SECONDARY_IDP_NAME
	);

	const spAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_SP_NAME,
		'Service Provider'
	);

	const identityProviderConnectionsPage = new IdentityProviderConnectionsPage(
		spAdminPage
	);

	await identityProviderConnectionsPage.goTo();

	await identityProviderConnectionsPage.deleteIdentityProviderConnections();

	await connectSpAndIdp(
		ibAdminPage,
		SECONDARY_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	const spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL
	);

	expect(await spInstancePage.url()).toContain(DEFAULT_SP_URL);

	await expect(
		await spInstancePage.getByTitle('User Profile Menu')
	).toBeVisible();

	await spInstancePage.reload();

	await spInstancePage.goto(spNewPageUrl);

	// Verify user is logged in

	await spInstancePage
		.getByTitle('User Profile Menu')
		.waitFor({timeout: 30 * 1000});

	// Verify user is redirected back to restricted resource

	expect(await spInstancePage.url()).toContain(spNewPageUrl);
});

test('LPD-62689: IdP initiated SLO is propagated correctly from Identity Brokers', async ({
	browser,
}) => {
	const localhostAdminPage = await browser.newPage();

	await performLogin(localhostAdminPage, 'test');

	const ibAdminPage = await createIdentityBrokerVirtualInstance(
		browser,
		localhostAdminPage,
		SECONDARY_IDP_NAME
	);

	const idpAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_IDP_NAME,
		'Identity Provider'
	);

	// Clear default connections and make new ones with the both IdP and SP instance

	const serviceProviderConnectionsPage = new ServiceProviderConnectionsPage(
		idpAdminPage
	);

	await serviceProviderConnectionsPage.goTo();

	await serviceProviderConnectionsPage.deleteServiceProviderConnections();

	await connectSpAndIdp(
		idpAdminPage,
		DEFAULT_IDP_NAME,
		ibAdminPage,
		SECONDARY_IDP_NAME
	);

	const spAdminPage = await configureVirtualInstanceForSaml(
		browser,
		DEFAULT_SP_NAME,
		'Service Provider'
	);

	const identityProviderConnectionsPage = new IdentityProviderConnectionsPage(
		spAdminPage
	);

	await identityProviderConnectionsPage.goTo();

	await identityProviderConnectionsPage.deleteIdentityProviderConnections();

	await connectSpAndIdp(
		ibAdminPage,
		SECONDARY_IDP_NAME,
		spAdminPage,
		DEFAULT_SP_NAME
	);

	const secondarySpAdminPage = await createServiceProviderVirtualInstance(
		browser,
		SECONDARY_SP_NAME,
		SECONDARY_SP_NAME,
		localhostAdminPage
	);

	await connectSpAndIdp(
		ibAdminPage,
		SECONDARY_IDP_NAME,
		secondarySpAdminPage,
		SECONDARY_SP_NAME
	);

	const userAccount = await createUser(idpAdminPage, DEFAULT_IDP_NAME);

	const spInstancePage = await performSpInitiatedSSO(
		browser,
		userAccount.emailAddress,
		DEFAULT_SP_URL
	);

	expect(await spInstancePage.url()).toContain(DEFAULT_SP_URL);

	await expect(
		await spInstancePage.getByTitle('User Profile Menu')
	).toBeVisible();

	await spInstancePage.goto(SECONDARY_SP_URL);

	await clickSignInButton(spInstancePage);

	await spInstancePage
		.getByTitle('User Profile Menu')
		.waitFor({timeout: 30 * 1000});

	// IdP initiated SLO

	await spInstancePage.goto(DEFAULT_IDP_URL);

	await spInstancePage.getByTitle('User Profile Menu').click();

	await spInstancePage.getByRole('menuitem', {name: 'Sign Out'}).click();

	await spInstancePage.waitForTimeout(8000);

	// Both SPs should also be logged out after IdP initiated SLO

	for (const spUrl of [DEFAULT_SP_URL, SECONDARY_SP_URL]) {
		await spInstancePage.goto(spUrl);

		const signInButton = await spInstancePage.getByRole('button', {
			name: 'Sign In',
		});

		expect(await signInButton).toBeVisible();
	}

	// Delete newly created virtual instance, and remove from afterAll deletion

	await deleteVirtualInstance(SECONDARY_SP_NAME, localhostAdminPage);

	await deleteAfterTestProviderConnections.delete(SECONDARY_SP_NAME);

	await deleteAfterTestVirtualInstances.delete(SECONDARY_SP_NAME);
});
