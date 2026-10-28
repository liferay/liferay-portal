/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {selectAndExpectToHaveValue} from '../../../utils/selectAndExpectToHaveValue';
import {waitForAlert} from '../../../utils/waitForAlert';
import {faroConfig} from '../main/faro.config';
import {
	ACPage,
	navigateToACPageViaURL,
	navigateToACSettingsViaURL,
} from '../main/utils/navigation';
import {CardSelectors} from '../main/utils/selectors';
import {signInToAnalyticsCloud} from '../main/utils/signInToAnalyticsCloud';
import {changeTimeFilter} from '../main/utils/time-filter';

const test = mergeTests(
	apiHelpersTest,
	isolatedChannelTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

async function createCustomEvent({
	apiHelpers,
	attributes = [],
	channelId,
	eventName,
}: {
	apiHelpers: any;
	attributes?: Array<{dataType: string; name: string; value: string}>;
	channelId: string;
	eventName: string;
}) {
	await apiHelpers.jsonWebServicesOSBAsah.createEvents([
		{
			applicationId: 'CustomEvent',
			canonicalUrl: 'https://www.liferay.com',
			channelId,
			eventDate: new Date().toISOString(),
			eventId: eventName,
			properties: attributes.map(({name, value}) => ({name, value})),
			title: 'Liferay',
			userId: '1',
		},
	]);

	await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
		{
			applicationId: 'CustomEvent',
			displayName: eventName,
			eventAttributeDefinitions: attributes.map(({dataType, name}) => ({
				dataType,
				displayName: name,
				name,
				type: 'LOCAL',
			})),
			name: eventName,
			type: 'CUSTOM',
		},
	]);
}

async function openCustomEventEditor({
	eventName,
	page,
	projectGroupId,
}: {
	eventName: string;
	page: Page;
	projectGroupId: string;
}) {
	await navigateToACSettingsViaURL({
		acPage: ACPage.definitionsEventsCustomPage,
		page,
		projectID: projectGroupId,
	});

	await page.getByRole('link', {name: eventName}).click();

	await page.getByRole('button', {name: 'Edit'}).click();
}

async function saveCustomEventEditor(page: Page) {

	// Click Save and wait for the edit mutation to persist before navigating
	// away; navigating too early aborts the in-flight request.

	await Promise.all([
		page.waitForResponse(
			(response) =>
				response.url().includes('opname=UpdateEventDefinition') &&
				response.ok()
		),
		page.getByRole('button', {name: 'Save'}).click(),
	]);
}

async function saveAttributeEditor(page: Page) {

	// The attribute editor persists through a different mutation than the event
	// editor; wait for it before navigating away.

	await Promise.all([
		page.waitForResponse(
			(response) =>
				response
					.url()
					.includes('opname=UpdateEventAttributeDefinition') &&
				response.ok()
		),
		page.getByRole('button', {name: 'Save'}).click(),
	]);
}

async function resetSearchQueriesPreference(page: Page, projectID: string) {
	const response = await page.request.post(
		`${faroConfig.environment.baseUrl}/o/cerebro/graphql?opname=Preference&projectGroupId=${projectID}`,
		{
			data: {
				operationName: 'Preference',
				query: 'mutation Preference($key: String!, $value: String!) { preference(key: $key, value: $value) { key value } }',
				variables: {
					key: 'search-query-strings',
					value: '[]',
				},
			},
		}
	);

	expect(response.ok()).toBe(true);
}

test.beforeEach(async ({page, project}) => {
	await resetSearchQueriesPreference(page, project.groupId);
});

test(
	'Add, edit, and delete a search query parameter',
	{tag: ['@LRAC-8779', '@LRAC-8780', '@LRAC-8781']},
	async ({page, project}) => {
		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsSearchPage,
			page,
			projectID: project.groupId,
		});

		// Add a search query parameter

		await page.getByRole('button', {name: 'Add'}).click();

		await page
			.locator('input[name="queryStringList.0"]')
			.fill('Test Query');

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'Search query definition has been saved', {
			autoClose: false,
		});

		// Verify the parameter persists after reload

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsSearchPage,
			page,
			projectID: project.groupId,
		});

		await expect(
			page.locator('input[name="queryStringList.0"]')
		).toHaveValue('Test Query');

		// Edit the parameter

		await page.locator('input[name="queryStringList.0"]').fill('Test Edit');

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'Search query definition has been saved', {
			autoClose: false,
		});

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsSearchPage,
			page,
			projectID: project.groupId,
		});

		await expect(
			page.locator('input[name="queryStringList.0"]')
		).toHaveValue('Test Edit');

		// Add a second parameter so deleting the first leaves a valid form

		await page.getByRole('button', {name: 'Add'}).click();

		await page.locator('input[name="queryStringList.1"]').fill('Keep This');

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'Search query definition has been saved', {
			autoClose: false,
		});

		// Delete the first parameter

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsSearchPage,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('button', {name: 'Delete'}).first().click();

		// Re-fill the remaining input and blur to force Formik to re-run
		// validation after the FieldArray remove; otherwise Save stays disabled.

		await page.locator('input[name="queryStringList.0"]').fill('Keep This');

		await page.keyboard.press('Tab');

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'Search query definition has been saved', {
			autoClose: false,
		});

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsSearchPage,
			page,
			projectID: project.groupId,
		});

		await expect(
			page.locator('input[name="queryStringList.0"]')
		).toHaveValue('Keep This');

		await expect(
			page.locator('input[name="queryStringList.1"]')
		).toHaveCount(0);
	}
);

test(
	'Connected DXP data source appears as the source of every default individual attribute',
	{tag: '@Legacy'},
	async ({apiHelpers, page, project}) => {
		const connectionToken =
			await apiHelpers.jsonWebServicesOSBFaro.fetchDataSourceConnectionToken(
				project.groupId
			);

		await apiHelpers.analyticsSettingsRest.postDataSource(connectionToken);

		try {
			await navigateToACSettingsViaURL({
				acPage: ACPage.dataSourcePage,
				page,
				projectID: project.groupId,
			});

			const connectedRow = page
				.locator('table tbody tr')
				.filter({hasNotText: 'DISCONNECTED'})
				.filter({hasText: 'CONNECTED'});

			const dataSourceName =
				(await connectedRow.locator('td').first().textContent()) || '';

			expect(dataSourceName.trim()).not.toBe('');

			await navigateToACSettingsViaURL({
				acPage: ACPage.definitionsIndividualAttributesPage,
				page,
				projectID: project.groupId,
			});

			// Open the 'email' attribute and verify its source is the
			// freshly-connected DXP data source

			await page.getByRole('button', {name: 'email'}).click();

			await expect(
				page.getByRole('cell', {name: dataSourceName}).first()
			).toBeVisible();
		}
		finally {
			await apiHelpers.analyticsSettingsRest.deleteDataSource();
		}
	}
);

test(
	'Add button is hidden after reaching the 5-query limit',
	{tag: '@LRAC-8782'},
	async ({page, project}) => {
		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsSearchPage,
			page,
			projectID: project.groupId,
		});

		// Add 5 query strings

		for (let i = 0; i < 5; i++) {
			await page.getByRole('button', {name: 'Add'}).click();

			await page
				.locator(`input[name="queryStringList.${i}"]`)
				.fill(`Test Query ${i + 1}`);
		}

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'Search query definition has been saved', {
			autoClose: false,
		});

		// Assert no Add button is rendered when the limit is reached

		await expect(page.getByRole('button', {name: 'Add'})).not.toBeVisible();
	}
);

test(
	'A customized search query string parameter collects searched terms in the Search Terms report',
	{tag: '@LRAC-14680'},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const queryParameter = 'query';

		const searchTerm = 'test parameter';

		// Define a customized search query string parameter

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsSearchPage,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('button', {name: 'Add'}).click();

		await page
			.locator('input[name="queryStringList.0"]')
			.fill(queryParameter);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'Search query definition has been saved', {
			autoClose: false,
		});

		// Seed a search event carrying the customized keyword parameter

		const date = new Date();

		const searchUrl = `https://www.liferay.com/search?${queryParameter}=${encodeURIComponent(
			searchTerm
		)}`;

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Page',
				canonicalUrl: searchUrl,
				channelId: channel.id,
				eventDate: date.toISOString(),
				eventId: 'pageViewed',
				title: searchTerm,
				url: searchUrl,
				userId: 'searchUser',
			},
		]);

		// The searched term surfaces in the Site overview Search Terms report

		await navigateToACPageViaURL({
			acPage: ACPage.sitePage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await changeTimeFilter({
			cardSelector: CardSelectors.SearchTerms,
			page,
			timeFilterPeriod: 'Last 24 hours',
		});

		await expect(
			page
				.locator(CardSelectors.SearchTerms)
				.getByText(searchTerm)
				.first()
		).toBeVisible();
	}
);

test(
	'A custom event description can be set and cleared, the event renamed, and a name over 255 characters is rejected',
	{tag: '@LRAC-9865'},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const customEventName = 'event' + getRandomString();

		await createCustomEvent({
			apiHelpers,
			channelId: channel.id,
			eventName: customEventName,
		});

		// The custom event is listed

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsEventsCustomPage,
			page,
			projectID: project.groupId,
		});

		await expect(
			page.getByRole('link', {name: customEventName})
		).toBeVisible();

		// A description can be set and it appears in the list

		const description = `${customEventName} Description`;

		await openCustomEventEditor({
			eventName: customEventName,
			page,
			projectGroupId: project.groupId,
		});

		await page.getByLabel('Description').fill(description);

		await saveCustomEventEditor(page);

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsEventsCustomPage,
			page,
			projectID: project.groupId,
		});

		await expect(page.getByText(description)).toBeVisible();

		// The description can be cleared

		await openCustomEventEditor({
			eventName: customEventName,
			page,
			projectGroupId: project.groupId,
		});

		await page.getByLabel('Description').fill('');

		await saveCustomEventEditor(page);

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsEventsCustomPage,
			page,
			projectID: project.groupId,
		});

		await expect(page.getByText(description)).toHaveCount(0);

		// The display name can be renamed

		const displayName = `${customEventName} Display Name`;

		await openCustomEventEditor({
			eventName: customEventName,
			page,
			projectGroupId: project.groupId,
		});

		await page.getByLabel('Display Name').fill(displayName);

		await saveCustomEventEditor(page);

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsEventsCustomPage,
			page,
			projectID: project.groupId,
		});

		await expect(page.getByText(displayName)).toBeVisible();

		// A display name longer than 255 characters is rejected. The row link
		// keeps showing the event name, so reopen the editor by that name.

		await openCustomEventEditor({
			eventName: customEventName,
			page,
			projectGroupId: project.groupId,
		});

		await page.getByLabel('Display Name').fill('a'.repeat(256));

		// Blur so Formik runs validation and surfaces the field error

		await page.getByLabel('Display Name').blur();

		await expect(page.getByText('Exceeds maximum length.')).toBeVisible();
	}
);

test(
	'An event attribute data type can be changed to Number, Date, Boolean and Duration',
	{
		tag: ['@LRAC-10200', '@LRAC-10201', '@LRAC-10203', '@LRAC-10205'],
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await createCustomEvent({
			apiHelpers,
			attributes: [
				{dataType: 'STRING', name: 'animal', value: 'dog'},
				{dataType: 'STRING', name: 'phone', value: '87900809'},
				{dataType: 'STRING', name: 'job', value: 'QA'},
				{dataType: 'STRING', name: 'portal', value: 'ac'},
			],
			channelId: channel.id,
			eventName: 'event' + getRandomString(),
		});

		async function searchAttribute(attributeName: string) {
			await navigateToACSettingsViaURL({
				acPage: ACPage.eventAttributesPage,
				page,
				projectID: project.groupId,
			});

			await page
				.getByRole('link', {exact: true, name: 'Attributes'})
				.click();

			await page.getByPlaceholder('Search').fill(attributeName);

			await page.keyboard.press('Enter');
		}

		const dataTypeByAttribute: Array<[string, string]> = [
			['animal', 'NUMBER'],
			['phone', 'DATE'],
			['job', 'BOOLEAN'],
			['portal', 'DURATION'],
		];

		for (const [attributeName, dataType] of dataTypeByAttribute) {
			await searchAttribute(attributeName);

			await page
				.getByRole('link', {exact: true, name: attributeName})
				.click();

			await page.getByRole('button', {name: 'Edit'}).click();

			await selectAndExpectToHaveValue({
				optionValue: dataType,
				select: page.getByLabel('Default Data Typecast'),
			});

			await saveAttributeEditor(page);

			// The new data type is reflected in the attributes list

			await searchAttribute(attributeName);

			await expect(page.getByText(dataType)).toBeVisible();
		}
	}
);

test(
	'A custom event attribute shows its sample data and data type, and a display name over 255 characters is rejected',
	{tag: '@LRAC-10011'},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const attributes = [
			{dataType: 'STRING', name: 'category', value: 'wetsuit'},
			{dataType: 'NUMBER', name: 'price', value: '259.95'},
			{
				dataType: 'DATE',
				name: 'birthdate',
				value: '2021-11-25T14:36:30.685Z',
			},
			{dataType: 'BOOLEAN', name: 'like', value: 'true'},
		];

		await createCustomEvent({
			apiHelpers,
			attributes,
			channelId: channel.id,
			eventName: 'event' + getRandomString(),
		});

		async function openAttribute(attributeName: string) {
			await navigateToACSettingsViaURL({
				acPage: ACPage.eventAttributesPage,
				page,
				projectID: project.groupId,
			});

			await page
				.getByRole('link', {exact: true, name: 'Attributes'})
				.click();

			await page.getByPlaceholder('Search').fill(attributeName);

			await page.keyboard.press('Enter');

			await page
				.getByRole('link', {exact: true, name: attributeName})
				.click();
		}

		// Each attribute shows its ingested sample value and its data type

		for (const {dataType, name, value} of attributes) {
			await openAttribute(name);

			await expect(page.getByText(value, {exact: true})).toBeVisible();

			await expect(page.getByText(dataType, {exact: true})).toBeVisible();
		}

		// A display name longer than 255 characters is rejected

		await openAttribute('category');

		await page.getByRole('button', {name: 'Edit'}).click();

		await page.getByLabel('Display Name').fill('a'.repeat(256));

		await page.getByLabel('Display Name').blur();

		await expect(page.getByText('Exceeds maximum length.')).toBeVisible();
	}
);

test(
	'Global attributes can be searched and an unknown name yields no results',
	{tag: '@LRAC-10217'},
	async ({page, project}) => {
		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsEventAttributesGlobalPage,
			page,
			projectID: project.groupId,
		});

		// Every default global attribute is found by its name

		for (const globalAttribute of [
			'canonicalUrl',
			'pageDescription',
			'pageTitle',
		]) {
			await page.getByPlaceholder('Search').fill(globalAttribute);

			await page.keyboard.press('Enter');

			await expect(
				page.getByRole('link', {exact: true, name: globalAttribute})
			).toBeVisible();
		}

		// A name that matches no global attribute yields the empty state

		await page.getByPlaceholder('Search').fill('acqa');

		await page.keyboard.press('Enter');

		await expect(
			page.getByText('No results were found.')
		).toBeVisible();
	}
);

test(
	'A global attribute description can be set and its display name renamed',
	{tag: '@LRAC-10212'},
	async ({page, project}) => {
		const globalAttributeName = 'pageTitle';

		async function openGlobalAttribute() {
			await navigateToACSettingsViaURL({
				acPage: ACPage.definitionsEventAttributesGlobalPage,
				page,
				projectID: project.groupId,
			});

			await page
				.getByRole('link', {exact: true, name: globalAttributeName})
				.click();

			await page.getByRole('button', {name: 'Edit'}).click();
		}

		try {

			// The default global attributes are listed

			await navigateToACSettingsViaURL({
				acPage: ACPage.definitionsEventAttributesGlobalPage,
				page,
				projectID: project.groupId,
			});

			for (const defaultGlobalAttribute of [
				'canonicalUrl',
				'pageDescription',
				'pageKeywords',
				'pageTitle',
				'referrer',
				'url',
			]) {
				await expect(
					page.getByRole('link', {
						exact: true,
						name: defaultGlobalAttribute,
					})
				).toBeVisible();
			}

			// A description can be set and it appears in the list

			const description = `${globalAttributeName} Description`;

			await openGlobalAttribute();

			await page.getByLabel('Description').fill(description);

			await saveAttributeEditor(page);

			await navigateToACSettingsViaURL({
				acPage: ACPage.definitionsEventAttributesGlobalPage,
				page,
				projectID: project.groupId,
			});

			await expect(page.getByText(description)).toBeVisible();

			// The display name can be renamed

			const displayName = `${globalAttributeName} Display Name`;

			await openGlobalAttribute();

			await page.getByLabel('Display Name').fill(displayName);

			await saveAttributeEditor(page);

			await navigateToACSettingsViaURL({
				acPage: ACPage.definitionsEventAttributesGlobalPage,
				page,
				projectID: project.groupId,
			});

			await expect(page.getByText(displayName)).toBeVisible();
		}
		finally {

			// Restore the shared project-level attribute to its defaults

			await openGlobalAttribute();

			await page.getByLabel('Display Name').fill(globalAttributeName);

			await page.getByLabel('Description').fill('');

			await saveAttributeEditor(page);
		}
	}
);

test(
	'Certain default events are hidden by default',
	{tag: '@LRAC-10222'},
	async ({page, project}) => {
		await page.goto(
			`${faroConfig.environment.baseUrl}/workspace/${project.groupId}/settings/definitions/events/default`
		);

		// Events that ship hidden expose a "Set to Show" toggle

		for (const defaultEventName of [
			'assetDepthReached',
			'blogDepthReached',
		]) {
			const row = page
				.locator('tbody tr')
				.filter({hasText: defaultEventName});

			await row.hover();

			await expect(
				row.getByRole('button', {name: 'Set to Show'})
			).toBeVisible();
		}
	}
);

test(
	'A custom event display name cannot be renamed to an existing display name',
	{tag: '@LRAC-10006'},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const existingEventName = 'event' + getRandomString();
		const renamedEventName = 'event' + getRandomString();

		await createCustomEvent({
			apiHelpers,
			channelId: channel.id,
			eventName: existingEventName,
		});

		await createCustomEvent({
			apiHelpers,
			channelId: channel.id,
			eventName: renamedEventName,
		});

		await openCustomEventEditor({
			eventName: renamedEventName,
			page,
			projectGroupId: project.groupId,
		});

		// Renaming the display name to one that already exists is rejected

		await page.getByLabel('Display Name').fill(existingEventName);

		const [response] = await Promise.all([
			page.waitForResponse((response) =>
				response.url().includes('opname=UpdateEventDefinition')
			),
			page.getByRole('button', {name: 'Save'}).click(),
		]);

		expect(await response.text()).toContain(
			`Display name ${existingEventName} is already used`
		);
	}
);

test(
	'A hidden default event can still be opened and shows its attributes',
	{tag: '@LRAC-10223'},
	async ({page, project}) => {
		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsEventsDefaultPage,
			page,
			projectID: project.groupId,
		});

		// The default event is hidden (exposes a Set to Show quick action)

		await page.getByRole('row', {name: 'assetDepthReached'}).hover();

		await expect(
			page
				.getByRole('row', {name: 'assetDepthReached'})
				.getByRole('button', {name: 'Set to Show'})
		).toBeVisible();

		// The hidden event can still be opened and shows its attribute list

		await page.getByRole('link', {name: 'assetDepthReached'}).click();

		await expect(
			page.getByRole('heading', {name: 'assetDepthReached'})
		).toBeVisible();

		await expect(
			page.getByRole('link', {exact: true, name: 'canonicalUrl'})
		).toBeVisible();
	}
);

async function searchCustomEventList(page: Page, query: string) {
	await page.getByPlaceholder('Search').fill(query);

	await page.keyboard.press('Enter');
}

test(
	'Custom events can be blocked and unblocked individually and in bulk',
	{tag: '@LRAC-10147'},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const eventNames = [
			'blockA' + getRandomString(),
			'blockB' + getRandomString(),
			'blockC' + getRandomString(),
		];

		for (const eventName of eventNames) {
			await createCustomEvent({
				apiHelpers,
				channelId: channel.id,
				eventName,
			});
		}

		const goToCustomEvents = () =>
			navigateToACSettingsViaURL({
				acPage: ACPage.definitionsEventsCustomPage,
				page,
				projectID: project.groupId,
			});

		const goToBlockList = () =>
			navigateToACSettingsViaURL({
				acPage: ACPage.definitionsEventsBlockListPage,
				page,
				projectID: project.groupId,
			});

		// Block the first event individually

		await goToCustomEvents();

		await searchCustomEventList(page, eventNames[0]);

		await page.getByRole('row', {name: eventNames[0]}).hover();

		await page
			.getByRole('row', {name: eventNames[0]})
			.getByRole('button', {name: 'Block Event'})
			.click();

		await page
			.locator('.confirmation-modal-root')
			.getByRole('button', {name: 'Block'})
			.click();

		// It leaves the custom events list

		await searchCustomEventList(page, eventNames[0]);

		await expect(page.getByRole('link', {name: eventNames[0]})).toHaveCount(
			0
		);

		// It appears in the block list

		await goToBlockList();

		await searchCustomEventList(page, eventNames[0]);

		await expect(
			page.getByRole('cell', {name: eventNames[0]})
		).toBeVisible();

		// Block the other two events in bulk by selecting their rows

		await goToCustomEvents();

		for (const eventName of [eventNames[1], eventNames[2]]) {
			await searchCustomEventList(page, eventName);

			await page
				.getByRole('row', {name: eventName})
				.getByRole('checkbox')
				.check();
		}

		await searchCustomEventList(page, 'block');

		await page.getByRole('button', {name: 'Block Events'}).click();

		await page
			.locator('.confirmation-modal-root')
			.getByRole('button', {name: 'Block'})
			.click();

		// Both bulk-blocked events appear in the block list

		await goToBlockList();

		for (const eventName of [eventNames[1], eventNames[2]]) {
			await searchCustomEventList(page, eventName);

			await expect(
				page.getByRole('cell', {name: eventName})
			).toBeVisible();
		}

		// Unblock the first event and confirm it returns to the custom list

		await searchCustomEventList(page, eventNames[0]);

		await page.getByRole('row', {name: eventNames[0]}).hover();

		await page
			.getByRole('row', {name: eventNames[0]})
			.getByRole('button', {name: 'Unblock Event'})
			.click();

		await goToCustomEvents();

		await searchCustomEventList(page, eventNames[0]);

		await expect(
			page.getByRole('link', {name: eventNames[0]})
		).toBeVisible();
	}
);

test(
	'A non-admin user cannot block or hide custom events',
	{tag: ['@LRAC-10150', '@LRAC-10234']},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const eventName = 'nonAdmin' + getRandomString();

		await createCustomEvent({apiHelpers, channelId: channel.id, eventName});

		// Sign in as a member (non-admin) user

		await signInToAnalyticsCloud(page, 'corbin.murakami@faro.io');

		try {
			await navigateToACSettingsViaURL({
				acPage: ACPage.definitionsEventsCustomPage,
				page,
				projectID: project.groupId,
			});

			await page.getByPlaceholder('Search').fill(eventName);

			await page.keyboard.press('Enter');

			// The event is listed but no admin controls are available

			await expect(
				page.getByRole('link', {name: eventName})
			).toBeVisible();

			await expect(page.getByTestId('select-all-checkbox')).toHaveCount(
				0
			);

			await page.getByRole('row', {name: eventName}).hover();

			await expect(
				page.getByRole('button', {name: 'Block Event'})
			).toHaveCount(0);

			await expect(
				page.getByRole('button', {name: 'Set to Hide'})
			).toHaveCount(0);
		}
		finally {
			await signInToAnalyticsCloud(page, faroConfig.user.login);
		}
	}
);

test(
	'The blocked events list can be sorted by Event Name and Last Seen',
	{tag: '@LRAC-10360'},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const prefix = 'sortBlocked' + getRandomString();

		const eventNames = [prefix + 'C', prefix + 'A', prefix + 'B'];

		for (const eventName of eventNames) {
			await createCustomEvent({
				apiHelpers,
				channelId: channel.id,
				eventName,
			});
		}

		// Block each seeded event individually

		for (const eventName of eventNames) {
			await navigateToACSettingsViaURL({
				acPage: ACPage.definitionsEventsCustomPage,
				page,
				projectID: project.groupId,
			});

			await page.getByPlaceholder('Search').fill(eventName);

			await page.keyboard.press('Enter');

			await page.getByRole('row', {name: eventName}).hover();

			await page
				.getByRole('row', {name: eventName})
				.getByRole('button', {name: 'Block Event'})
				.click();

			await page
				.locator('.confirmation-modal-root')
				.getByRole('button', {name: 'Block'})
				.click();
		}

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsEventsBlockListPage,
			page,
			projectID: project.groupId,
		});

		// Sorting by either column keeps the three blocked events listed

		for (const columnName of ['Event Name', 'Last Seen']) {
			await page.getByPlaceholder('Search').fill(prefix);

			await page.keyboard.press('Enter');

			await page.getByText(columnName, {exact: true}).click();

			await expect(page.getByRole('cell', {name: prefix})).toHaveCount(3);
		}
	}
);

test(
	'A hidden custom event is still found in the custom events list',
	{tag: '@LRAC-10235'},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const eventName = 'hidden' + getRandomString();

		await createCustomEvent({apiHelpers, channelId: channel.id, eventName});

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsEventsCustomPage,
			page,
			projectID: project.groupId,
		});

		// Hide the custom event from its row quick action

		await searchCustomEventList(page, eventName);

		await page.getByRole('row', {name: eventName}).hover();

		await page
			.getByRole('row', {name: eventName})
			.getByRole('button', {name: 'Set to Hide'})
			.click();

		await page
			.locator('.confirmation-modal-root')
			.getByRole('button', {exact: true, name: 'Hide'})
			.click();

		// Searching for the hidden event still finds it, now exposing the
		// Set to Show action that marks it as hidden

		await searchCustomEventList(page, eventName);

		await page.getByRole('row', {name: eventName}).hover();

		await expect(
			page
				.getByRole('row', {name: eventName})
				.getByRole('button', {name: 'Set to Show'})
		).toBeVisible();
	}
);
