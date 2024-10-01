/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {TCustomField} from '../../../helpers/CustomFieldTypesHelper';
import {liferayConfig} from '../../../liferay.config';
import {SystemSettingsPage} from '../../../pages/configuration-admin-web/SystemSettingsPage';
import {AddCustomFieldPage} from '../../../pages/expando-web/AddCustomFieldPage';
import {VirtualInstancesPage} from '../../../pages/portal-instances-web/VirtualInstancesPage';
import {SamlAdminPage} from '../../../pages/saml-web/SamlAdminPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import performLogin, {userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {
	deleteAfterTestProviderConnections,
	deleteAfterTestVirtualInstances,
} from '../saml.spec';
import {connectSpAndIdp} from './samlProviderConnectionUtil';

export const DEFAULT_IDP_NAME = 'www.able.com';
export const DEFAULT_IDP_URL = `http://${DEFAULT_IDP_NAME}:8080`;
export const DEFAULT_SP_NAME = 'www.baker.com';
export const DEFAULT_SP_URL = `http://${DEFAULT_SP_NAME}:8080`;
export const SECONDARY_IDP_NAME = 'www.charlie.com';
export const SECONDARY_IDP_URL = `http://${SECONDARY_IDP_NAME}:8080`;
export const SECONDARY_SP_NAME = 'www.dog.com';
export const SECONDARY_SP_URL = `http://${SECONDARY_SP_NAME}:8080`;

export async function createCustomField(
	adminPage: Page,
	customField: TCustomField
) {
	const addCustomFieldPage = new AddCustomFieldPage(adminPage);

	await addCustomFieldPage.addCustomField(customField);
}

export async function createIdentityProviderVirtualInstance(
	browser,
	page: Page,
	name = DEFAULT_IDP_NAME,
	entityId = name
): Promise<Page> {
	return await createSamlVirtualInstance(
		browser,
		entityId,
		name,
		page,
		'Identity Provider'
	);
}

export async function createUser(
	adminPage: Page,
	instanceName: string,
	userId = getRandomInt()
) {
	const defaultBaseUrl = liferayConfig.environment.baseUrl;

	liferayConfig.environment.baseUrl = `http://${instanceName}:8080`;

	// Create apiHelper implementation for the given instance

	const apiHelpers = new ApiHelpers(adminPage);

	// Create user in given instance

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount(
		undefined,
		userId
	);

	// Add user info to userData const so we can authenticate via performLogin

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	liferayConfig.environment.baseUrl = defaultBaseUrl;

	return userAccount;
}

async function createSamlVirtualInstance(
	browser,
	entityId: string,
	name: string,
	page: Page,
	samlRole: string
): Promise<Page> {
	const virtualInstancesPage = new VirtualInstancesPage(page);

	await virtualInstancesPage.addNewVirtualInstance(name);

	deleteAfterTestVirtualInstances.add(name);

	return await configureVirtualInstanceForSaml(browser, entityId, samlRole);
}

export async function configureVirtualInstanceForSaml(
	browser,
	entityId: string,
	samlRole: string
): Promise<Page> {
	deleteAfterTestProviderConnections.add(entityId);

	const defaultBaseUrl = liferayConfig.environment.baseUrl;

	liferayConfig.environment.baseUrl = `http://${entityId}:8080`;

	const newPage = await performSamlSafeLogin(browser, entityId);

	const samlAdminPage = new SamlAdminPage(newPage);

	await samlAdminPage.configureSAML(true, entityId, samlRole);

	liferayConfig.environment.baseUrl = defaultBaseUrl;

	return newPage;
}

export async function createServiceProviderVirtualInstance(
	browser,
	entityId: string,
	name: string,
	page
): Promise<Page> {
	return await createSamlVirtualInstance(
		browser,
		entityId,
		name,
		page,
		'Service Provider'
	);
}

export async function deleteVirtualInstance(name: string, page: Page) {
	const virtualInstancesPage = new VirtualInstancesPage(page);

	await virtualInstancesPage.deleteVirtualInstance(name);
}

export async function performSamlSafeLogin(
	browser,
	domain: string,
	mailId = domain !== 'localhost' ? `@${domain}.com` : undefined,
	rememberMe = true,
	screenName = 'test'
) {
	const page = await browser.newPage({
		baseURL: `http://${domain}:8080`,
	});

	await performLogin(
		page,
		screenName,
		'?p_p_id=com_liferay_login_web_portlet_LoginPortlet&' +
			'p_p_state=maximized',
		mailId,
		rememberMe
	);

	return page;
}

export async function resetSamlConfiguration(page: Page) {
	const systemSettingsPage = new SystemSettingsPage(page);

	await systemSettingsPage.goToSystemSetting('SSO', 'SAML Configuration');

	await systemSettingsPage.page
		.getByText('Runtime Metadata Refresh Interval')
		.waitFor();

	if (
		await systemSettingsPage.page
			.getByRole('button', {name: 'Actions'})
			.isVisible()
	) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: systemSettingsPage.page.getByRole('link', {
				name: 'Reset Default Values',
			}),
			trigger: systemSettingsPage.page.getByRole('button', {
				name: 'Actions',
			}),
		});

		await waitForAlert(systemSettingsPage.page);
	}
}

export async function resetSamlKeystoreManagerTarget(page: Page) {
	const systemSettingsPage = new SystemSettingsPage(page);

	await systemSettingsPage.goToSystemSetting(
		'SSO',
		'SAML KeyStoreManager Implementation Configuration'
	);

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: systemSettingsPage.page.getByRole('link', {
			name: 'Reset Default Values',
		}),
		trigger: systemSettingsPage.page.getByRole('button', {name: 'Actions'}),
	});

	await waitForAlert(page);
}

export async function setupSamlInstances(
	browser,
	page: Page,
	idpInstanceName = DEFAULT_IDP_NAME,
	idpEntityId = idpInstanceName,
	spInstanceName = DEFAULT_SP_NAME,
	spEntityId = spInstanceName
) {

	// Create new idp virtual instance

	const idpAdminPage = await createIdentityProviderVirtualInstance(
		browser,
		page,
		idpInstanceName,
		idpEntityId
	);

	// Create new sp virtual instance

	const spAdminPage = await createServiceProviderVirtualInstance(
		browser,
		spEntityId,
		spInstanceName,
		page
	);

	// Add a new connection for each provider, of the opposite provider

	await connectSpAndIdp(
		idpAdminPage,
		idpInstanceName,
		spAdminPage,
		spInstanceName,
		idpEntityId,
		spEntityId
	);

	await idpAdminPage.close();
	await spAdminPage.close();
}

export async function updateRuntimeMetadataRefreshInterval(
	page: Page,
	value: string
) {
	const systemSettingsPage = new SystemSettingsPage(page);

	await systemSettingsPage.goToSystemSetting('SSO', 'SAML Configuration');

	await systemSettingsPage.page
		.getByText('Runtime Metadata Refresh Interval')
		.fill(value);

	let updateButton = await systemSettingsPage.page.getByRole('button', {
		name: 'Update',
	});

	if (!(await updateButton.isVisible())) {
		updateButton = await systemSettingsPage.page.getByRole('button', {
			name: 'Save',
		});
	}

	await updateButton.click();

	await waitForAlert(systemSettingsPage.page);
}

export async function updateSamlKeystoreManagerTarget(
	page: Page,
	target: string
) {
	const systemSettingsPage = new SystemSettingsPage(page);

	await systemSettingsPage.goToSystemSetting(
		'SSO',
		'SAML KeyStoreManager Implementation Configuration'
	);

	await systemSettingsPage.page.getByLabel('Keystore Manager Target').click();

	await systemSettingsPage.page.getByRole('option', {name: target}).click();

	let updateButton = await systemSettingsPage.page.getByRole('button', {
		name: 'Update',
	});

	if (!(await updateButton.isVisible())) {
		updateButton = await systemSettingsPage.page.getByRole('button', {
			name: 'Save',
		});
	}

	await updateButton.click();

	await waitForAlert(page);
}
