/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {customFieldsPagesTest} from '../../../fixtures/customFieldsPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../liferay.config';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {waitForAlert} from '../../../utils/waitForAlert';
import {waitForLoading} from '../../analytics-reports-js-components-web/main/utils/loading';
import {syncAnalyticsCloud} from '../../analytics-settings-web/main/utils/analytics-settings';
import {faroConfig} from '../main/faro.config';
import {ACPage, navigateToACPageViaURL} from '../main/utils/navigation';
import {
	addSegmentField,
	createBatchSegment,
	editCriteriaAttributeValue,
	selectOperator,
} from '../main/utils/segments';
import {SegmentConditions} from '../main/utils/selectors';
import {viewNameOnTableList} from '../main/utils/utils';

const test = mergeTests(
	apiHelpersTest,
	customFieldsPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedChannelTest,
	isolatedSiteTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

async function waitForCustomFieldAttribute({
	apiHelpers,
	attributeName,
	groupId,
	ownerType = 'individual',
}: {
	apiHelpers: ApiHelpers;
	attributeName: string;
	groupId: string;
	ownerType?: string;
}) {
	await expect(async () => {
		const response = await apiHelpers.page.request.get(
			`${faroConfig.environment.baseUrl}/o/faro/contacts/${groupId}` +
				`/field_mapping?channelId=&context=custom&cur=1&delta=500` +
				`&ownerType=${ownerType}&query=`
		);

		const {items} = await response.json();

		expect(
			(items ?? []).some(
				(item: {name: string}) => item.name === attributeName
			)
		).toBe(true);
	}).toPass({timeout: 60000});
}

async function waitForKnownIndividual({
	apiHelpers,
	channelId,
	groupId,
	name,
}: {
	apiHelpers: ApiHelpers;
	channelId: string;
	groupId: string;
	name: string;
}) {
	await expect(async () => {
		const response = await apiHelpers.page.request.post(
			`${faroConfig.environment.baseUrl}/o/faro/contacts/${groupId}` +
				`/individual/search`,
			{
				form: {channelId, cur: 1, delta: 500, query: name},
				headers: await apiHelpers.getCSRFTokenHeader(),
			}
		);

		const {items} = await response.json();

		expect(
			(items ?? []).some((item: {name: string}) => item.name === name)
		).toBe(true);
	}).toPass({timeout: 60000});
}

test(
	'Batch segment can be created with an individual text custom field criterion',
	{
		tag: '@LRAC-8569',
	},
	async ({
		addCustomFieldPage,
		analyticsChannel: channel,
		apiHelpers,
		page,
		project,
		site,
		viewAttributesPage,
	}) => {
		test.setTimeout(180000);

		const runId = getRandomString().replace(/-/g, '');
		const customFieldName = `userCustomField${runId}`;

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		try {
			await addCustomFieldPage.addCustomField({
				fieldName: customFieldName,
				fieldType: 'inputField',
				fieldValues: {
					dataType: 'Text',
				},
				resource: 'User',
			});

			await page.goto(
				'/group/control_panel/manage?p_p_id=com_liferay_users_admin_web_portlet_UsersAdminPortlet'
			);

			await clickAndExpectToBeVisible({
				target: page.getByText(customFieldName),
				trigger: page.getByRole('link', {
					exact: true,
					name: user.alternateName,
				}),
			});

			await page.getByLabel(customFieldName).fill('VIP AC');

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(page);

			await syncAnalyticsCloud({
				apiHelpers,
				channel,
				page,
				project,
				siteName: site.name,
			});

			await page.goto(`${PORTLET_URLS.analyticsCloudConnection}`);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page
					.getByRole('listitem')
					.filter({hasText: 'People'})
					.getByRole('button'),
				trigger: page.getByRole('menuitem', {name: 'Attributes'}),
			});

			const dialog = page.getByRole('dialog', {
				name: 'Sync People Attributes',
			});

			await dialog
				.getByRole('textbox', {name: 'Search'})
				.fill(customFieldName);

			await clickAndExpectToBeVisible({
				target: dialog.getByRole('row', {name: customFieldName}),
				trigger: dialog.getByRole('button', {name: 'Search'}),
			});

			await dialog
				.getByRole('row', {name: customFieldName})
				.getByRole('checkbox')
				.check();

			await page.getByRole('button', {exact: true, name: 'Sync'}).click();

			await waitForAlert(page, 'Success:Attributes have been saved.');

			await waitForCustomFieldAttribute({
				apiHelpers,
				attributeName: customFieldName,
				groupId: project.groupId,
			});

			await waitForKnownIndividual({
				apiHelpers,
				channelId: channel.id,
				groupId: project.groupId,
				name: `${user.givenName} ${user.familyName}`,
			});

			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await expect(
				page.getByRole('heading', {name: 'Segments'}).locator('span')
			).toBeVisible();

			await waitForLoading(page);

			await createBatchSegment(page);

			await addSegmentField({
				criterionName: customFieldName,
				criterionType: 'Individual',
				page,
			});

			await selectOperator({
				operator: 'contains',
				operatorField: SegmentConditions.criteriaCondition,
				page,
			});

			await editCriteriaAttributeValue({attributeValue: 'VIP AC', page});

			// Open the in-editor View Members preview and assert the user
			// appears

			await clickAndExpectToBeVisible({
				target: page.getByText('Known Segment Members'),
				trigger: page.getByRole('button', {name: 'View Members'}),
			});

			await viewNameOnTableList({
				itemNames: `${user.givenName} ${user.familyName}`,
				page,
			});
		}
		finally {
			await page.goto(liferayConfig.environment.baseUrl);

			await apiHelpers.headlessAdminUser.deleteUserAccount(
				Number(user.id)
			);

			await viewAttributesPage.deleteCustomField(customFieldName, 'User');
		}
	}
);

test(
	'Batch segment can be created with an organization custom field criterion',
	{
		tag: '@LRAC-8570',
	},
	async ({
		addCustomFieldPage,
		analyticsChannel: channel,
		apiHelpers,
		page,
		project,
		site,
		viewAttributesPage,
	}) => {
		test.setTimeout(180000);

		const runId = getRandomString().replace(/-/g, '');
		const customFieldName = `orgCustomField${runId}`;

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		const organization =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization CF ${runId}`,
			});

		try {
			await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
				organization.id,
				user.emailAddress
			);

			await addCustomFieldPage.addCustomField({
				fieldName: `orgCustomField${runId}`,
				fieldType: 'inputField',
				fieldValues: {
					dataType: 'Text',
					startingValue: 'Hey AC Team',
				},
				resource: 'Organization',
			});

			await page.goto(
				'/group/control_panel/manage?p_p_id=com_liferay_users_admin_web_portlet_UsersAdminPortlet&_com_liferay_users_admin_web_portlet_UsersAdminPortlet_screenNavigationCategoryKey=organizations'
			);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'Edit'}),
				trigger: page
					.getByRole('row', {name: organization.name})
					.getByLabel('Show Actions'),
			});

			await page.getByLabel(customFieldName).fill('Hey AC Team');

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(page);

			await syncAnalyticsCloud({
				apiHelpers,
				channel,
				organizationName: organization.name,
				page,
				project,
				siteName: site.name,
			});

			await waitForCustomFieldAttribute({
				apiHelpers,
				attributeName: customFieldName,
				groupId: project.groupId,
				ownerType: 'organization',
			});

			await waitForKnownIndividual({
				apiHelpers,
				channelId: channel.id,
				groupId: project.groupId,
				name: `${user.givenName} ${user.familyName}`,
			});

			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await expect(
				page.getByRole('heading', {name: 'Segments'}).locator('span')
			).toBeVisible();

			await waitForLoading(page);

			await createBatchSegment(page);

			await addSegmentField({
				criterionName: customFieldName,
				criterionType: 'Organization',
				page,
			});

			await editCriteriaAttributeValue({
				attributeValue: 'Hey AC Team',
				page,
			});

			// Preview the segment and verify the user matches the organization
			// criterion

			await clickAndExpectToBeVisible({
				target: page.getByText('Known Segment Members'),
				trigger: page.getByRole('button', {name: 'View Members'}),
			});

			await viewNameOnTableList({
				itemNames: `${user.givenName} ${user.familyName}`,
				page,
			});
		}
		finally {
			await page.goto(liferayConfig.environment.baseUrl);

			await apiHelpers.headlessAdminUser.deleteUserAccount(
				Number(user.id)
			);

			await apiHelpers.headlessAdminUser.deleteOrganization(
				organization.id
			);

			await viewAttributesPage.deleteCustomFields('Organization');
		}
	}
);
