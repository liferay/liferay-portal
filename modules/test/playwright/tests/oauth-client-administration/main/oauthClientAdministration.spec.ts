/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {customFieldsPagesTest} from '../../../fixtures/customFieldsPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {oauthClientAdminPagesTest} from '../../../fixtures/oauthClientAdminPagesTest';
import {TCustomField} from '../../../helpers/CustomFieldTypesHelper';
import getRandomString from '../../../utils/getRandomString';

let authServerLocalMetadataCreated = false;
let oauthClientsCreated = false;
let protectedResourceLocalMetadataCreated = false;
let userCustomFieldsCreated = false;

const test = mergeTests(
	customFieldsPagesTest,
	oauthClientAdminPagesTest,
	featureFlagsTest({
		'LPD-49855': {enabled: true},
		'LPD-63415': {enabled: true},
	}),
	loginTest()
);

test.afterEach(
	async ({
		authServerLocalMetadatasPage,
		oauthClientClientsPage,
		protectedResourceLocalMetadatasPage,
		viewAttributesPage,
	}) => {
		if (authServerLocalMetadataCreated) {
			await authServerLocalMetadatasPage.goTo();

			await authServerLocalMetadatasPage.deleteAuthServerLocalMetadata();

			authServerLocalMetadataCreated = false;
		}

		if (userCustomFieldsCreated) {
			await viewAttributesPage.deleteCustomFields('User');
		}

		if (oauthClientsCreated) {
			await oauthClientClientsPage.goTo();

			await oauthClientClientsPage.deleteOAuthClientEntries();

			oauthClientsCreated = false;
		}

		if (protectedResourceLocalMetadataCreated) {
			await protectedResourceLocalMetadatasPage.goTo();

			await protectedResourceLocalMetadatasPage.deleteProtectedResourceLocalMetadata();

			protectedResourceLocalMetadataCreated = false;
		}
	}
);

test.describe('Enable Configuration of oauth-authorization-server Well-Known URIs in the OAuthClient Portlet', () => {
	test(
		'Create an oauth-authorization-server and validate',
		{tag: '@LPD-67473'},
		async ({authServerLocalMetadatasPage}) => {
			const issuer = 'https://localhost.com';
			const supportedScopes = `${getRandomString()},${getRandomString()}`;
			const wellKnownURI = `${issuer}/o/.well-known/oauth-authorization-server`;

			await authServerLocalMetadatasPage.goTo();

			await authServerLocalMetadatasPage.addAuthServerLocalMetadata('', {
				expectedMessage: 'The Issuer field is required.',
			});

			await authServerLocalMetadatasPage.addAuthServerLocalMetadata(
				issuer,
				{
					supportedScopes,
				}
			);

			authServerLocalMetadataCreated = true;

			await authServerLocalMetadatasPage.addAuthServerLocalMetadata(
				issuer,
				{
					expectedMessage: 'Duplicate',
				}
			);

			if (
				await authServerLocalMetadatasPage.oAuthAuthorizatoinServerTab.isHidden()
			) {
				await authServerLocalMetadatasPage.globalMenuPage.goToControlPanel(
					'OAuth Client Administration'
				);
			}

			await authServerLocalMetadatasPage.authServerLocalMetadataTab.click();
			await authServerLocalMetadatasPage.oAuthAuthorizatoinServerTab.click();
			await expect(
				await authServerLocalMetadatasPage.page.getByRole('link', {
					name: wellKnownURI,
				})
			).toBeVisible();

			await authServerLocalMetadatasPage.openIdConfigurationTab.click();

			await expect(
				await authServerLocalMetadatasPage.page.getByRole('link', {
					name: `${issuer}/.well-known/openid-configuration/1B2M2Y8AsgTpgAmY7PhCfg**/local`,
				})
			).toBeVisible();

			await authServerLocalMetadatasPage.oAuthAuthorizatoinServerTab.click();

			// Reopen the entry and verify the supported scopes round-trip with
			// the same comma delimiter they were saved with

			await authServerLocalMetadatasPage.openAuthServerLocalMetadata(
				wellKnownURI
			);

			await expect(
				authServerLocalMetadatasPage.supportedScopes
			).toHaveValue(supportedScopes);
		}
	);
});

test.describe('Enable Configuration of oauth-protected-resource Well-Known URIs in the OAuthClient Portlet', () => {
	test(
		'Create an oauth-protected-resource and validate',
		{tag: '@LPD-91289'},
		async ({protectedResourceLocalMetadatasPage}) => {
			await protectedResourceLocalMetadatasPage.goTo();

			await protectedResourceLocalMetadatasPage.addProtectedResourceLocalMetadata(
				'',
				'https://localhost.com',
				'The Resource field is required.'
			);

			await protectedResourceLocalMetadatasPage.addProtectedResourceLocalMetadata(
				'http://example.com/o/mcp',
				'https://localhost.com',
				'Invalid HTTPS Resource URL: http://example.com/o/mcp'
			);

			protectedResourceLocalMetadataCreated = true;

			await protectedResourceLocalMetadatasPage.addProtectedResourceLocalMetadata(
				'https://localhost.com/o/mcp',
				'https://localhost.com'
			);

			await protectedResourceLocalMetadatasPage.addProtectedResourceLocalMetadata(
				'https://localhost.com/o/mcp',
				'https://localhost.com',
				'Duplicate'
			);

			await expect(
				protectedResourceLocalMetadatasPage.page.getByText(
					'https://localhost.com/.well-known/oauth-protected-resource/o/mcp'
				)
			).toBeVisible();
		}
	);
});

test.describe('oAuth Client Admin Clients', () => {
	test(
		'Configure OIDC custom claims and matcher field',
		{tag: '@LPD-67470'},
		async ({addCustomFieldPage, oauthClientClientsPage}) => {
			const customField: TCustomField = {
				fieldName: getRandomString(),
				fieldType: 'inputField',
				resource: 'User',
			};

			await addCustomFieldPage.addCustomField(customField);

			userCustomFieldsCreated = true;

			await oauthClientClientsPage.goTo();

			const customClaim: TCustomClaim = {
				expandoColumnName: customField.fieldName,
				oidcProviderCustomClaim: getRandomString(),
			};

			const infoJSON: TInfoJSON = {
				client_id: getRandomString(),
				client_secret: getRandomString(),
			};

			const oauthClientEntry: TOAuthClientEntry = {
				authServerWellKnownURI:
					'https://accounts.google.com/.well-known/openid-configuration',
				customClaims: [customClaim],
				infoJSON,
				matcherField: 'screenName',
			};

			expect(
				await oauthClientClientsPage.addOAuthClientEntry(
					oauthClientEntry,
					'Missing screenName value at OpenId Connect User Information Mapper JSON'
				)
			).toBeNull();

			const user: TOIDCUser = {
				screenName: getRandomString(),
			};

			const oidcUserInfoMapperJSON: TOIDCUserInfoMapperJSON = {
				user,
			};

			oauthClientEntry.oidcUserInfoMapperJSON = oidcUserInfoMapperJSON;

			await oauthClientClientsPage.goTo();

			const newOAuthClientEntry =
				await oauthClientClientsPage.addOAuthClientEntry(
					oauthClientEntry
				);

			oauthClientsCreated = true;

			await oauthClientClientsPage.goToEntry(oauthClientEntry);

			await expect(oauthClientClientsPage.matcherField).toHaveValue(
				'screenName'
			);

			if (newOAuthClientEntry?.customClaims) {
				for (const customClaim of newOAuthClientEntry.customClaims) {
					await oauthClientClientsPage.checkCustomClaimValues(
						customClaim
					);
				}
			}
		}
	);
});
