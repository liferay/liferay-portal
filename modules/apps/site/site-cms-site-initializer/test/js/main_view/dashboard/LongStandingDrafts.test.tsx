/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	FrontendDataSet,
	serializeFDSConfig,
} from '@liferay/frontend-data-set-web';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {
	FDS_FILTER_ID,
	WORKFLOW_STATUS,
} from '../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import toDatePart from '../../../../src/main/resources/META-INF/resources/js/common/utils/toDatePart';
import {GovernanceContext} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceContext';
import {Operations} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/Operations';
import getDashboardAssetListFDSProps from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/getDashboardAssetListFDSProps';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	sub: (template: string, ...values: string[]) =>
		values.reduce(
			(result, value, index) => result.replace(`{${index}}`, value),
			template
		),
}));

jest.mock('@liferay/frontend-data-set-web', () => ({
	FrontendDataSet: jest.fn(() => null),
	getConfigParamName: (fdsName: string) => `${fdsName}_fdsConfig`,
	serializeFDSConfig: jest.fn(() => 'SERIALIZED_CONFIG'),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/ContentProgress',
	() => ({ContentProgress: () => null})
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/ContributorConcentration',
	() => ({ContributorConcentration: () => null})
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/getDashboardAssetListFDSProps',
	() => ({__esModule: true, default: jest.fn(() => ({}))})
);

const ADDITIONAL_PROPS = {
	allSectionFDSName: 'allSection',
	contentProgressFilter: 'contentProgressFilter',
	fdsActionDropdownItems: [
		{data: {id: 'actionLink'}},
		{data: {id: 'delete'}},
		{data: {id: 'view-content'}},
		{data: {id: 'view-file'}},
		{data: {id: 'share'}},
		{data: {id: 'update-review-date'}},
	],
} as any;

const LANGUAGE_KEYS: Record<string, string> = {
	'long-standing-drafts': 'Long-Standing Drafts',
	'showing-x-of-x-items': 'Showing {0} of {1} Items',
	'view-all-x': 'View All {0}',
	'x-days-in-draft': '{0} days in draft',
	'x-of-x-assets': '{0} of {1} Assets',
};

const MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

const NOW = Date.UTC(2026, 9, 8, 12, 0);

const reloadStatistics = jest.fn();

function getGroupIds(space: {siteId?: number}) {
	return space.siteId ? [space.siteId] : [];
}

function renderOperations({
	space = {value: 'all'},
	statistics = {longStandingDraftsCount: 9, totalCount: 350},
}: {
	space?: {label?: string; siteId?: number; value: string};
	statistics?: {longStandingDraftsCount: number; totalCount: number};
} = {}) {
	return render(
		<GovernanceContext.Provider
			value={
				{
					groupIds: getGroupIds(space),
					loadingStatistics: false,
					reloadStatistics,
					space,
					statistics,
				} as any
			}
		>
			<Operations additionalProps={ADDITIONAL_PROPS} />
		</GovernanceContext.Provider>
	);
}

function getCard() {
	return screen.getByRole('button', {name: /Long-Standing Drafts/});
}

async function expandList() {
	await userEvent.click(getCard());
}

function getFDSPropsArguments() {
	return (getDashboardAssetListFDSProps as jest.Mock).mock.calls[0][0];
}

function getViewAllConfig() {
	return (serializeFDSConfig as jest.Mock).mock.calls[0][0];
}

describe('[CMS Dashboard] LongStandingDrafts', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	beforeEach(() => {
		jest.clearAllMocks();

		jest.spyOn(Date, 'now').mockReturnValue(NOW);

		jest.spyOn(Liferay.Language, 'get').mockImplementation(
			(key: string) => LANGUAGE_KEYS[key] ?? key
		);
	});

	it('shows the long-standing drafts count and its share of all assets', () => {
		renderOperations();

		expect(getCard()).toHaveTextContent('9');
		expect(getCard()).toHaveTextContent('2.6% of 350 Assets');
	});

	it('toggles the list of long-standing drafts when clicking the card', async () => {
		renderOperations();

		expect(getCard()).toHaveAttribute('aria-expanded', 'false');
		expect(
			screen.queryByRole('region', {name: 'Long-Standing Drafts'})
		).not.toBeInTheDocument();

		await expandList();

		expect(getCard()).toHaveAttribute('aria-expanded', 'true');
		expect(
			screen.getByRole('region', {name: 'Long-Standing Drafts'})
		).toBeInTheDocument();

		await expandList();

		expect(getCard()).toHaveAttribute('aria-expanded', 'false');
		expect(
			screen.queryByRole('region', {name: 'Long-Standing Drafts'})
		).not.toBeInTheDocument();
	});

	it('lists the drafts not modified in the last 30 days, oldest first', async () => {
		renderOperations();

		await expandList();

		const {apiURL} = getFDSPropsArguments();

		const searchParams = new URL(apiURL, 'http://localhost').searchParams;

		expect(searchParams.get('filter')).toMatch(
			new RegExp(
				`^contentProgressFilter and status eq ${WORKFLOW_STATUS.DRAFT} and dateModified lt \\S+$`
			)
		);
		expect(searchParams.get('pageSize')).toBe('8');
		expect(searchParams.get('sort')).toBe('dateModified:asc');
	});

	it('lists only the drafts of the selected space', async () => {
		renderOperations({
			space: {label: 'My Space', siteId: 12345, value: '999'},
		});

		await expandList();

		const {apiURL} = getFDSPropsArguments();

		expect(
			new URL(apiURL, 'http://localhost').searchParams.get('filter')
		).toContain('groupIds/any(g:g eq 12345)');
	});

	it('shows the author and the days in draft of each draft', async () => {
		renderOperations();

		await expandList();

		const {renderSubtitle} = getFDSPropsArguments();

		expect(
			renderSubtitle({
				dateModified: new Date(
					NOW - 45 * MILLISECONDS_PER_DAY
				).toISOString(),
				embedded: {creator: {name: 'Iris Chen'}},
			})
		).toBe('Iris Chen · 45 days in draft');
	});

	it('shows only the days in draft when the draft has no author', async () => {
		renderOperations();

		await expandList();

		const {renderSubtitle} = getFDSPropsArguments();

		expect(
			renderSubtitle({
				dateModified: new Date(
					NOW - 45 * MILLISECONDS_PER_DAY
				).toISOString(),
				embedded: {},
			})
		).toBe('45 days in draft');
	});

	it('only offers the edit, view and delete actions', async () => {
		renderOperations();

		await expandList();

		const {itemsActions} = getFDSPropsArguments();

		expect(
			itemsActions.map(({data}: {data: {id: string}}) => data.id)
		).toEqual(['actionLink', 'view-content', 'view-file', 'delete']);
	});

	it('reloads the statistics when the list data changes', async () => {
		renderOperations();

		await expandList();

		const {onDataChange} = getFDSPropsArguments();

		onDataChange();

		expect(reloadStatistics).toHaveBeenCalledTimes(1);
	});

	it('opens the all section filtered by draft status and the modified date threshold', async () => {
		renderOperations({
			space: {label: 'My Space', siteId: 12345, value: '999'},
		});

		await expandList();

		expect(
			screen.getByRole('link', {name: 'View All Long-Standing Drafts'})
		).toHaveAttribute(
			'href',
			'/web/cms/all?allSection_fdsConfig=SERIALIZED_CONFIG'
		);

		const {filters, sorts} = getViewAllConfig();

		expect(filters).toEqual([
			{
				id: FDS_FILTER_ID.STATUS,
				selectedData: expect.objectContaining({
					selectedItems: [
						expect.objectContaining({value: WORKFLOW_STATUS.DRAFT}),
					],
				}),
			},
			{
				id: FDS_FILTER_ID.DATE_MODIFIED,
				selectedData: {
					exclude: false,
					from: null,
					to: toDatePart(new Date(NOW - 30 * MILLISECONDS_PER_DAY)),
				},
			},
			{
				id: FDS_FILTER_ID.SCOPE_GROUP_ID,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'My Space', value: 12345}],
				},
			},
		]);
		expect(sorts).toEqual([
			{direction: 'asc', key: FDS_FILTER_ID.DATE_MODIFIED},
		]);
	});

	it('shows how many of the long-standing drafts are listed', async () => {
		renderOperations();

		await expandList();

		expect(screen.getByText('Showing 8 of 9 Items')).toBeInTheDocument();
	});

	it('describes the empty state when there are no long-standing drafts', async () => {
		renderOperations({
			statistics: {longStandingDraftsCount: 0, totalCount: 350},
		});

		await expandList();

		const [{emptyState}] = (FrontendDataSet as jest.Mock).mock.calls.at(-1);

		expect(emptyState).toEqual(
			expect.objectContaining({
				description:
					'no-content-has-stayed-in-draft-longer-than-expected',
				title: 'no-long-standing-drafts',
			})
		);
		expect(screen.queryByText(/^Showing/)).not.toBeInTheDocument();
	});

	it('has no accessibility violations with the list expanded', async () => {
		const {container} = renderOperations();

		await expandList();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
