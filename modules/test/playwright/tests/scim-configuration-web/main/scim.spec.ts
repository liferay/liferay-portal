/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {customFieldsPagesTest} from '../../../fixtures/customFieldsPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {serverAdministrationPageTest} from '../../../fixtures/serverAdministrationPageTest';
import {userGroupsPageTest} from '../../../fixtures/userGroupsPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../liferay.config';
import {VirtualInstancesPage} from '../../../pages/portal-instances-web/VirtualInstancesPage';
import {ApplicationsMenuPage} from '../../../pages/product-navigation-applications-menu/ApplicationsMenuPage';
import {SCIMConfigurationPage} from '../../../pages/scim-configuraiton-web/SCIMConfigurationPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import performLogin, {performLogout} from '../../../utils/performLogin';
import {newScimUser} from './utils/newScimUserUtil';

export const featureFlagDisabledtest = mergeTests(
	featureFlagsTest({
		'LPD-56434': {enabled: false},
	}),

	loginTest(),
	customFieldsPagesTest,
	usersAndOrganizationsPagesTest
);

export const test = mergeTests(
	featureFlagsTest({
		'LPD-56434': {enabled: true},
	}),

	loginTest(),
	applicationsMenuPageTest,
	serverAdministrationPageTest,
	userGroupsPageTest,
	usersAndOrganizationsPagesTest
);

const DEFAULT_VIRTUAL_INSTANCE_NAME = 'www.able.com';

const RESET_SCIM_HELP_TEXT =
	'All SCIM Client related data and generated OAuth 2 tokens will be ' +
	'removed. This is necessary to configure a new SCIM Client.';

featureFlagDisabledtest(
	'LPD-60870 Verify LPD-56434 is behind feature flag',
	async ({
		editUserPage,
		page,
		usersAndOrganizationsPage,
		viewAttributesPage,
	}) => {

		// If the SCIM custom fields exist, remove them so we can test properly

		await viewAttributesPage.goto('User');

		await viewAttributesPage.addCustomFieldButton.waitFor();

		const customFieldAttributes = [
			'scimDisplayName',
			'scimEntitlements',
			'scimNickName',
			'scimPhotos',
			'scimPreferredLanguage',
			'scimUserType',
			'scimX509Certificates',
		];

		for (const customFieldAttribute of customFieldAttributes) {
			if (
				await viewAttributesPage.page
					.getByRole('row')
					.filter({hasText: customFieldAttribute})
					.isVisible()
			) {
				await viewAttributesPage.deleteCustomField(
					customFieldAttribute,
					'User'
				);
			}
		}

		const scimConfigurationPage = new SCIMConfigurationPage(page);

		await scimConfigurationPage.goTo();

		await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

		await scimConfigurationPage.generateToken();

		const accessToken =
			await scimConfigurationPage.accessTokenField.inputValue();

		const randomNumber = getRandomInt();

		const newUser = await newScimUser(randomNumber);

		const apiHelper = new ApiHelpers(page);

		await apiHelper.scim.postUserWithOAuth(newUser, accessToken);

		const response = await (
			await apiHelper.scim.getUsersWithOAuth(accessToken)
		).text();

		expect(response).toContain('"totalResults":1');

		await usersAndOrganizationsPage.goto(false);

		await usersAndOrganizationsPage.goToUser(newUser.userName);

		await editUserPage.emailAddressInput.waitFor();

		await expect(editUserPage.emailAddressInput).toHaveValue(
			`able${randomNumber}@liferay.com`
		);

		await expect(editUserPage.firstNameInput).toHaveValue(
			newUser.name.givenName
		);

		await expect(editUserPage.lastNameInput).toHaveValue(
			newUser.name.familyName
		);

		await expect(editUserPage.middleNameInput).toHaveValue(
			newUser.name.middleName
		);

		await expect(editUserPage.prefixInput).toHaveValue('');

		await expect(editUserPage.suffixInput).toHaveValue('');

		for (const customFieldAttribute of customFieldAttributes) {
			await expect(
				await editUserPage.customField(customFieldAttribute)
			).not.toBeVisible();
		}

		await editUserPage.rolesLink.click();

		await editUserPage.page
			.getByText('Regular Roles', {exact: true})
			.waitFor();

		await expect(
			editUserPage.page.getByText(
				'This user is not assigned any regular roles.'
			)
		).toBeVisible();

		await editUserPage.contactLink.click();

		await expect(
			editUserPage.page.getByText(
				'This user does not have any addresses.'
			)
		).toBeVisible();

		await editUserPage.contactInformationLink.click();

		await expect(
			editUserPage.page.getByText(
				'This user does not have any additional email addresses.'
			)
		).toBeVisible();

		await expect(await editUserPage.jabberInput).toBeEmpty();

		await expect(await editUserPage.skypeInput).toBeEmpty();

		await expect(
			editUserPage.page.getByText(
				'This user does not have any phone numbers.'
			)
		).toBeVisible();

		await expect(
			editUserPage.page.getByText('This user does not have any websites.')
		).toBeVisible();

		await editUserPage.preferencesLink.click();

		await editUserPage.displaySettingsLink.click();

		await editUserPage.timeZoneInput.waitFor();

		await expect(await editUserPage.timeZoneInput).toHaveValue('UTC');

		await scimConfigurationPage.goTo();

		await scimConfigurationPage.resetClientData();
	}
);

test('smoke: test SCIM configuration options', async ({page}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'test');

	await scimConfigurationPage.generateToken();

	await scimConfigurationPage.revokeToken();

	await scimConfigurationPage.resetClientData();
});

test('LPD-23255 AC1 TC1: Reset SCIM Client provisioning data button is present', async ({
	page,
}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	await scimConfigurationPage.resetClientData();
});

test('LPD-23255 AC2 TC2: Reset SCIM Client provisioning data button description is present', async ({
	page,
}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	const tooltip = page.getByLabel(RESET_SCIM_HELP_TEXT).locator('use');

	expect(await tooltip).toBeDefined();

	await scimConfigurationPage.resetClientData();
});

test('LPD-23255 AC3 TC3: Verify that clicking the “Reset SCIM Client provisioning data“ button clears the information in the SCIM page', async ({
	page,
}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	await scimConfigurationPage.resetClientData();

	await expect(scimConfigurationPage.accessTokenField).toBeHidden();

	await expect(scimConfigurationPage.oAuth2ApplicationNameField).toBeEmpty();

	await expect(scimConfigurationPage.matcherField).toHaveValue('');
});

test('LPD-23255 AC3 TC4: Verify that clicking the “Reset SCIM Client provisioning data“ button revokes the generated OAuth2 token and deletes the OAuth2 Application.', async ({
	page,
}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	await scimConfigurationPage.generateToken();

	const accessToken =
		await scimConfigurationPage.accessTokenField.inputValue();

	const apiHelper = new ApiHelpers(page);

	const authorizedResponse =
		await apiHelper.scim.getUsersWithOAuth(accessToken);
	expect(authorizedResponse.status()).toBe(200);

	const applicationsMenuPage = new ApplicationsMenuPage(page);

	await applicationsMenuPage.goToOauth2Administration();
	await page.waitForTimeout(1000);

	const scimOAuthClientRow = await page.getByRole('cell', {
		exact: true,
		name: 'Test SCIM Client',
	});
	expect(await scimOAuthClientRow).toBeVisible();

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.resetClientData();

	const unauthorizedResponse =
		await apiHelper.scim.getUsersWithOAuth(accessToken);
	expect(unauthorizedResponse.status()).toBe(401);

	await applicationsMenuPage.goToOauth2Administration();
	await page.waitForTimeout(1000);

	expect(await scimOAuthClientRow).not.toBeVisible();
});

test('LPD-23255 AC3 TC5: Verify that clicking the “Reset SCIM Client provisioning data“ button unbinds users', async ({
	page,
}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	const newUser = await newScimUser();

	const apiHelper = new ApiHelpers(page);

	await apiHelper.scim.postUser(newUser);

	const response = await (await apiHelper.scim.getUsers()).text();

	expect(response).toContain('"totalResults":1');

	await scimConfigurationPage.resetClientData();

	const emptyResponse = await (await apiHelper.scim.getUsers()).text();

	expect(emptyResponse).toContain('"totalResults":0');
});

test('LPD-23255 AC3 TC6: Verify that clicking the “Reset SCIM Client provisioning data“ button unbinds user groups.', async ({
	page,
}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	const randomNumber = getRandomInt();

	const newGroup = {
		displayName: `Foo${randomNumber}`,
	};

	const apiHelper = new ApiHelpers(page);

	await apiHelper.scim.postGroup(newGroup);

	const response = await (await apiHelper.scim.getGroups()).text();

	expect(response).toContain('"totalResults":1');

	await scimConfigurationPage.resetClientData();

	const emptyResponse = await (await apiHelper.scim.getGroups()).text();

	expect(emptyResponse).toContain('"totalResults":0');
});

test('LPD-23255 AC4 TC7: Verify that Name field is disabled when SCIM is configured', async ({
	page,
}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	expect(scimConfigurationPage.oAuth2ApplicationNameField).toBeEditable();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	expect(scimConfigurationPage.oAuth2ApplicationNameField).not.toBeEditable();

	await scimConfigurationPage.resetClientData();
});

test('LPD-23255 AC5 TC8: Verify that the Name field is enabled when scim client data is reset', async ({
	page,
}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	expect(scimConfigurationPage.oAuth2ApplicationNameField).not.toBeEditable();

	await scimConfigurationPage.resetClientData();

	expect(scimConfigurationPage.oAuth2ApplicationNameField).toBeEditable();
});

test('LPD-33284 verify that post and get users requests work with oauth token', async ({
	page,
}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	await scimConfigurationPage.generateToken();

	const accessToken =
		await scimConfigurationPage.accessTokenField.inputValue();

	const newUser = await newScimUser();

	const apiHelper = new ApiHelpers(page);

	await apiHelper.scim.postUserWithOAuth(newUser, accessToken);

	const response = await (
		await apiHelper.scim.getUsersWithOAuth(accessToken)
	).text();

	expect(response).toContain('"totalResults":1');

	await scimConfigurationPage.resetClientData();
});

test('LPD-33284 verify that post and get groups requests work with oauth token', async ({
	page,
}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	await scimConfigurationPage.generateToken();

	const accessToken =
		await scimConfigurationPage.accessTokenField.inputValue();

	const randomNumber = getRandomInt();

	const newGroup = {
		displayName: `Foo${randomNumber}`,
	};

	const apiHelper = new ApiHelpers(page);

	await apiHelper.scim.postGroupWithOAuth(newGroup, accessToken);

	const response = await (
		await apiHelper.scim.getGroupsWithOAuth(accessToken)
	).text();

	expect(response).toContain('"totalResults":1');

	await scimConfigurationPage.resetClientData();
});

test('LPS-190119 (TC-2 & TC-5). Admin User can Generate and Revoke SCIM Access Tokens on a new Virtual Instance.', async ({
	browser,
	page,
}) => {
	const virtualInstancesPage = new VirtualInstancesPage(page);

	await virtualInstancesPage.addNewVirtualInstance(
		DEFAULT_VIRTUAL_INSTANCE_NAME
	);

	const defaultBaseUrl = liferayConfig.environment.baseUrl;

	liferayConfig.environment.baseUrl = `http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:8080`;

	const newPage = await browser.newPage({
		baseURL: `http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:8080`,
	});

	await performLogin(
		newPage,
		'test',
		'?p_p_id=com_liferay_login_web_portlet_LoginPortlet&' +
			'p_p_state=maximized',
		`@${DEFAULT_VIRTUAL_INSTANCE_NAME}.com`
	);

	const scimConfigurationPage = new SCIMConfigurationPage(newPage);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	await scimConfigurationPage.generateToken();

	await scimConfigurationPage.revokeToken();

	await performLogout(newPage);

	liferayConfig.environment.baseUrl = defaultBaseUrl;

	await virtualInstancesPage.deleteVirtualInstance(
		DEFAULT_VIRTUAL_INSTANCE_NAME
	);
});

test('LPD-34644: Check if the token expiration warning message appears in the SCIM configuration UI.', async ({
	applicationsMenuPage,
	page,
	serverAdministrationPage,
}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	await scimConfigurationPage.generateToken();

	// Execute script to change the expiration date of the SCIM client access token to 10 days from current day

	await applicationsMenuPage.goToServerAdministration();

	const script = `
		import com.liferay.portal.kernel.dao.orm.QueryUtil;
		import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
		import com.liferay.portal.kernel.util.Time;
		import com.liferay.oauth2.provider.model.OAuth2Application;
		import com.liferay.oauth2.provider.model.OAuth2Authorization;
		import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalServiceUtil;
		import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalServiceUtil;
		import java.util.Date;
		import java.util.List;
		OAuth2Application oAuth2Application =
			OAuth2ApplicationLocalServiceUtil.getOAuth2Application(
			CompanyThreadLocal.getCompanyId(), "SCIM_test-scim-client");
		List<OAuth2Authorization> oAuth2Authorizations =
			OAuth2AuthorizationLocalServiceUtil.getOAuth2Authorizations(
			oAuth2Application.getOAuth2ApplicationId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		OAuth2Authorization oAuth2Authorization = oAuth2Authorizations.get(0);
		oAuth2Authorization.setAccessTokenExpirationDate(new Date(System.currentTimeMillis() + (Time.DAY * 10)));
		OAuth2AuthorizationLocalServiceUtil.updateOAuth2Authorization(oAuth2Authorization);
	`;

	await serverAdministrationPage.executeScript(script);

	await scimConfigurationPage.goTo();

	await expect(scimConfigurationPage.alertMessage).toBeVisible();

	await scimConfigurationPage.resetClientData();
});

test('LPD-37452 verify expando field is not visible for user added to SCIM', async ({
	editUserPage,
	page,
	usersAndOrganizationsPage,
}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	const newUser = await newScimUser();

	const apiHelper = new ApiHelpers(page);

	await apiHelper.scim.postUser(newUser);

	await usersAndOrganizationsPage.goToUsers(true);

	await (
		await usersAndOrganizationsPage.usersTableRowLink(newUser.userName)
	).click();

	await expect(
		await editUserPage.customField('scimClientId')
	).not.toBeVisible();

	await scimConfigurationPage.goTo();
	await scimConfigurationPage.resetClientData();
});

test('LPD-37452 verify expando field is not visible for group added to SCIM', async ({
	page,
	userGroupsPage,
}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	const randomNumber = getRandomInt();

	const newGroup = {
		displayName: `Foo${randomNumber}`,
	};

	const apiHelper = new ApiHelpers(page);

	await apiHelper.scim.postGroup(newGroup);

	await userGroupsPage.goto(true);

	await (
		await userGroupsPage.userGroupsTableRowActions(newGroup.displayName)
	).click();

	await userGroupsPage.editUserGroupMenuItem.click();

	await expect(await page.getByLabel('Scimclientid')).not.toBeVisible();

	await scimConfigurationPage.goTo();
	await scimConfigurationPage.resetClientData();
});

test('LPD-56434 Verify SCIM user attributes are properly imported during provisioning', async ({
	editUserPage,
	page,
	usersAndOrganizationsPage,
}) => {
	const scimConfigurationPage = new SCIMConfigurationPage(page);

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.configureSCIM('email', 'Test SCIM Client');

	await scimConfigurationPage.generateToken();

	const accessToken =
		await scimConfigurationPage.accessTokenField.inputValue();

	const randomNumber = getRandomInt();

	const newUser = await newScimUser(randomNumber);

	const apiHelper = new ApiHelpers(page);

	await apiHelper.scim.postUserWithOAuth(newUser, accessToken);

	const response = await (
		await apiHelper.scim.getUsersWithOAuth(accessToken)
	).text();

	expect(response).toContain('"totalResults":1');

	await test.step('Verify custom field related attributes are provisioned correctly', async () => {
		await usersAndOrganizationsPage.goto(false);

		await usersAndOrganizationsPage.goToUser(newUser.userName);

		await expect(
			await editUserPage.customField('scimDisplayName')
		).toHaveValue(newUser.displayName, {timeout: 30 * 1000});

		await expect(
			await editUserPage.customField('scimEntitlements')
		).toHaveValue(
			newUser.entitlements[0].value +
				'\n' +
				newUser.entitlements[1].value,
			{timeout: 30 * 1000}
		);

		await expect(
			await editUserPage.customField('scimNickName')
		).toHaveValue(newUser.nickName, {timeout: 30 * 1000});

		await expect(await editUserPage.customField('scimPhotos')).toHaveValue(
			newUser.photos[0].value + '\n' + newUser.photos[1].value,
			{timeout: 30 * 1000}
		);

		await expect(
			await editUserPage.customField('scimPreferredLanguage')
		).toHaveValue(newUser.preferredLanguage, {timeout: 30 * 1000});

		await expect(
			await editUserPage.customField('scimUserType')
		).toHaveValue(newUser.userType, {timeout: 30 * 1000});

		await expect(
			await editUserPage.customField('scimX509Certificates')
		).toHaveValue(
			newUser.x509Certificates[0].value +
				'\n' +
				newUser.x509Certificates[1].value,
			{timeout: 30 * 1000}
		);
	});

	await test.step('Verify addresses attribute is provisioned correctly', async () => {
		await editUserPage.contactLink.click();

		await editUserPage.addressesLink.waitFor();

		for (const address of newUser.addresses) {
			const addressLines = address.formatted.split('\n');

			const li = await editUserPage.page
				.locator('li')
				.filter({hasText: addressLines[0]});

			await expect(li).toBeVisible();

			addressLines.forEach((value) => {
				expect(li.getByText(value)).toBeVisible();
			});

			if (address.type === 'business') {
				await expect(await li.getByText('Business')).toBeVisible();
			}
			else if (address.type === 'personal') {
				await expect(await li.getByText('Personal')).toBeVisible();
			}

			if (address.primary) {
				await expect(await li.getByText('Primary')).toBeVisible();
			}
			else {
				await expect(await li.getByText('Primary')).not.toBeVisible();
			}
		}
	});

	await test.step('Verify emails attribute works with provisioned SCIM user', async () => {
		await usersAndOrganizationsPage.goto(false);

		await usersAndOrganizationsPage.goToUser(newUser.userName);

		// Verify primary email is used as user's email, not necessarily the first

		await expect(editUserPage.emailAddressInput).toHaveValue(
			`able${randomNumber}@liferay.com`
		);

		await editUserPage.contactLink.click();

		await editUserPage.contactInformationLink.waitFor();

		await editUserPage.contactInformationLink.click();

		for (const email of newUser.emails) {
			const row = (
				await editUserPage.additionalEmailAddressesTableRow(
					0,
					email.value,
					true
				)
			).row;

			await expect(row).toBeVisible();

			if (email.primary) {
				await expect(await row.getByText('Primary')).toBeVisible();
			}
			else {
				await expect(await row.getByText('Primary')).not.toBeVisible();
			}
		}
	});

	await test.step('Verify ims works with provisioned SCIM user', async () => {
		await expect(await editUserPage.jabberInput).toHaveValue(
			newUser.ims[0].value
		);

		await expect(await editUserPage.skypeInput).toHaveValue(
			newUser.ims[1].value
		);
	});

	await test.step('Verify name attribute and subattributes work with provisioned SCIM user', async () => {
		await usersAndOrganizationsPage.goto(false);

		await usersAndOrganizationsPage.goToUser(newUser.userName);

		await editUserPage.emailAddressInput.waitFor();

		await expect(editUserPage.firstNameInput).toHaveValue(
			newUser.name.givenName
		);

		await expect(editUserPage.lastNameInput).toHaveValue(
			newUser.name.familyName
		);

		await expect(editUserPage.middleNameInput).toHaveValue(
			newUser.name.middleName
		);

		await expect(editUserPage.prefixInput).toHaveValue(
			newUser.name.honorificPrefix
		);

		await expect(editUserPage.suffixInput).toHaveValue(
			newUser.name.honorificSuffix
		);
	});

	await test.step('Verify phoneNumbers attribute works properly with SCIM user provisioning', async () => {
		await editUserPage.contactLink.click();

		await editUserPage.contactInformationLink.waitFor();

		await editUserPage.contactInformationLink.click();

		for (const phoneNumber of newUser.phoneNumbers) {
			const row = (
				await editUserPage.phoneNumbersTableRow(
					0,
					phoneNumber.value,
					true
				)
			).row;

			await expect(row).toBeVisible();

			if (phoneNumber.primary) {
				await expect(await row.getByText('Primary')).toBeVisible();
			}
			else {
				await expect(await row.getByText('Primary')).not.toBeVisible();
			}

			await expect(await row.getByText(phoneNumber.type)).toBeVisible();
		}
	});

	await test.step('Verify profileUrl works with provisioned SCIM user', async () => {
		const row = (
			await editUserPage.websitesTableRow(0, newUser.profileUrl, true)
		).row;

		await expect(row).toBeVisible();

		await expect(await row.getByText('Personal')).toBeVisible();

		await expect(await row.getByText('Primary')).toBeVisible();
	});

	await test.step('Verify roles attribute works with provisioned SCIM user', async () => {
		await usersAndOrganizationsPage.goto(false);

		await usersAndOrganizationsPage.goToUser(newUser.userName);

		await editUserPage.rolesLink.click();

		for (const role of newUser.roles) {
			if (role.value === 'Invalid Role') {
				await expect(
					await editUserPage.regularRoleCell(role.value)
				).not.toBeVisible();
			}
			else {
				await expect(
					await editUserPage.regularRoleCell(role.value)
				).toBeVisible();
			}
		}
	});

	await test.step('Verify timezone works with provisioned SCIM user', async () => {
		await editUserPage.preferencesLink.click();

		await editUserPage.displaySettingsLink.click();

		await editUserPage.timeZoneInput.waitFor();

		await expect(await editUserPage.timeZoneInput).toHaveValue(
			newUser.timezone
		);
	});

	await scimConfigurationPage.goTo();

	await scimConfigurationPage.resetClientData();
});
