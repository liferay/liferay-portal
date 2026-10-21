/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {serializeFDSConfig} from '@liferay/frontend-data-set-web';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {
	FDS_EVENT_UPDATE_DISPLAY,
	FDS_FILTER_ID,
	WORKFLOW_STATUS,
} from '../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import toDatePart from '../../../../src/main/resources/META-INF/resources/js/common/utils/toDatePart';
import {GovernanceContext} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceContext';
import {Freshness} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/Freshness';
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
	FrontendDataSet: () => null,
	getConfigParamName: (fdsName: string) => `${fdsName}_fdsConfig`,
	serializeFDSConfig: jest.fn(() => 'SERIALIZED_CONFIG'),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/ContentAgeDistribution',
	() => ({ContentAgeDistribution: () => null})
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
		{data: {id: 'update-expiration-date'}},
		{data: {id: 'update-review-date'}},
	],
} as any;

const LANGUAGE_KEYS: Record<string, string> = {
	'expired-on-x': 'Expired on {0}',
	'expires-on-x': 'Expires on {0}',
	'review-due-on-x': 'Review due on {0}',
	'showing-x-of-x-items': 'Showing {0} of {1} Items',
	'view-all-x': 'View All {0}',
	'x-of-x-assets': '{0} of {1} Assets',
};

const MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

const NOW = Date.UTC(2026, 9, 8, 12, 0);

const SPACE = {label: 'My Space', siteId: 12345, value: '999'};

const reloadStatistics = jest.fn();

const STATISTICS = {
	expiredCount: 8,
	expiringSoonCount: 9,
	reviewDateOverdueCount: 5,
	totalCount: 350,
	upcomingReviewCount: 7,
};

function renderFreshness({
	space = {value: 'all'},
	statistics = STATISTICS,
}: {
	space?: {label?: string; siteId?: number; value: string};
	statistics?: Partial<typeof STATISTICS>;
} = {}) {
	return render(
		<GovernanceContext.Provider
			value={
				{
					loadingStatistics: false,
					reloadStatistics,
					space,
					statistics,
				} as any
			}
		>
			<Freshness additionalProps={ADDITIONAL_PROPS} />
		</GovernanceContext.Provider>
	);
}

function getCard(key: string) {
	return screen.getByRole('button', {name: new RegExp(`^${key} `)});
}

async function expandList(key: string) {
	await userEvent
		.setup({advanceTimers: jest.advanceTimersByTime})
		.click(getCard(key));
}

function getFDSPropsArguments() {
	return (getDashboardAssetListFDSProps as jest.Mock).mock.calls[0][0];
}

function getSearchParams() {
	return new URL(getFDSPropsArguments().apiURL, 'http://localhost')
		.searchParams;
}

function getViewAllLink(name: string) {
	return screen.getByRole('link', {name: `View All ${name}`});
}

describe('[CMS Dashboard] Freshness', () => {
	afterEach(() => {
		jest.restoreAllMocks();
		jest.useRealTimers();
	});

	beforeEach(() => {
		jest.clearAllMocks();

		jest.useFakeTimers().setSystemTime(NOW);

		jest.spyOn(Liferay.Language, 'get').mockImplementation(
			(key: string) => LANGUAGE_KEYS[key] ?? key
		);
	});

	it('shows the count of each card and its share of all assets', () => {
		renderFreshness();

		expect(getCard('expired')).toHaveTextContent('8');
		expect(getCard('expired')).toHaveTextContent('2.3% of 350 Assets');

		expect(getCard('expiring-soon')).toHaveTextContent('9');
		expect(getCard('expiring-soon')).toHaveTextContent(
			'2.6% of 350 Assets'
		);

		expect(getCard('overdue-reviews')).toHaveTextContent('5');
		expect(getCard('overdue-reviews')).toHaveTextContent(
			'1.4% of 350 Assets'
		);

		expect(getCard('upcoming-reviews')).toHaveTextContent('7');
		expect(getCard('upcoming-reviews')).toHaveTextContent(
			'2% of 350 Assets'
		);
	});

	it('expands the list of the clicked card and collapses it on the second click', async () => {
		renderFreshness();

		expect(screen.queryByRole('region')).not.toBeInTheDocument();

		await expandList('expired');

		expect(getCard('expired')).toHaveAttribute('aria-expanded', 'true');
		expect(
			screen.getByRole('region', {name: 'expired-assets'})
		).toBeInTheDocument();

		await expandList('expired');

		expect(getCard('expired')).toHaveAttribute('aria-expanded', 'false');
		expect(screen.queryByRole('region')).not.toBeInTheDocument();
	});

	it('shows only one list at a time', async () => {
		renderFreshness();

		await expandList('expired');
		await expandList('upcoming-reviews');

		expect(getCard('expired')).toHaveAttribute('aria-expanded', 'false');
		expect(getCard('upcoming-reviews')).toHaveAttribute(
			'aria-expanded',
			'true'
		);
		expect(screen.getAllByRole('region')).toHaveLength(1);
		expect(
			screen.getByRole('region', {name: 'upcoming-reviews'})
		).toBeInTheDocument();
	});

	it('lists the expired assets, the first to expire first', async () => {
		renderFreshness();

		await expandList('expired');

		const searchParams = getSearchParams();

		expect(searchParams.get('filter')).toBe(
			`contentProgressFilter and status eq ${WORKFLOW_STATUS.EXPIRED}`
		);
		expect(searchParams.get('pageSize')).toBe('8');
		expect(searchParams.get('sort')).toBe('dateExpiration:asc');
	});

	it('lists the approved assets expiring in the next seven days', async () => {
		renderFreshness();

		await expandList('expiring-soon');

		const searchParams = getSearchParams();

		expect(searchParams.get('filter')).toBe(
			`contentProgressFilter and status eq ${WORKFLOW_STATUS.APPROVED} and dateExpiration le ${new Date(NOW + 7 * MILLISECONDS_PER_DAY).toISOString()}`
		);
		expect(searchParams.get('sort')).toBe('dateExpiration:asc');
	});

	it('lists the assets whose review date has passed', async () => {
		renderFreshness();

		await expandList('overdue-reviews');

		const searchParams = getSearchParams();

		expect(searchParams.get('filter')).toBe(
			`contentProgressFilter and dateReview lt ${new Date(NOW).toISOString()}`
		);
		expect(searchParams.get('sort')).toBe('dateReview:asc');
	});

	it('lists the assets to review in the next seven days', async () => {
		renderFreshness();

		await expandList('upcoming-reviews');

		const searchParams = getSearchParams();

		expect(searchParams.get('filter')).toBe(
			`contentProgressFilter and dateReview gt ${new Date(NOW).toISOString()} and dateReview le ${new Date(NOW + 7 * MILLISECONDS_PER_DAY).toISOString()}`
		);
		expect(searchParams.get('sort')).toBe('dateReview:asc');
	});

	it('lists only the assets of the selected space', async () => {
		renderFreshness({space: SPACE});

		await expandList('expired');

		expect(getSearchParams().get('filter')).toContain(
			'groupIds/any(g:g eq 12345)'
		);
	});

	it('shows the expiration date of each expired and expiring asset', async () => {
		renderFreshness();

		await expandList('expired');

		expect(
			getFDSPropsArguments().renderSubtitle({
				embedded: {expirationDate: '2026-10-02T10:00:00Z'},
			})
		).toBe('Expired on Oct 2, 2026');

		jest.clearAllMocks();

		await expandList('expiring-soon');

		expect(
			getFDSPropsArguments().renderSubtitle({
				embedded: {expirationDate: '2026-10-12T10:00:00Z'},
			})
		).toBe('Expires on Oct 12, 2026');
	});

	it('shows the review date of each asset to review', async () => {
		renderFreshness();

		await expandList('overdue-reviews');

		expect(
			getFDSPropsArguments().renderSubtitle({
				embedded: {reviewDate: '2026-09-30T10:00:00Z'},
			})
		).toBe('Review due on Sep 30, 2026');
	});

	it('shows a placeholder when the asset has no date', async () => {
		renderFreshness();

		await expandList('expired');

		expect(getFDSPropsArguments().renderSubtitle({embedded: {}})).toBe(
			'Expired on --'
		);
	});

	it('offers the update expiration date action first on the expiration lists', async () => {
		renderFreshness();

		await expandList('expiring-soon');

		expect(
			getFDSPropsArguments().itemsActions.map(
				({data}: {data: {id: string}}) => data.id
			)
		).toEqual([
			'update-expiration-date',
			'actionLink',
			'view-content',
			'view-file',
			'delete',
		]);
	});

	it('offers the update review date action first on the review lists', async () => {
		renderFreshness();

		await expandList('overdue-reviews');

		expect(
			getFDSPropsArguments().itemsActions.map(
				({data}: {data: {id: string}}) => data.id
			)
		).toEqual([
			'update-review-date',
			'actionLink',
			'view-content',
			'view-file',
			'delete',
		]);
	});

	it('opens the all section filtered by the expired status', async () => {
		renderFreshness({space: SPACE});

		await expandList('expired');

		expect(getViewAllLink('expired-assets')).toHaveAttribute(
			'href',
			'/web/cms/all?allSection_fdsConfig=SERIALIZED_CONFIG'
		);

		const {filters, sorts} = (serializeFDSConfig as jest.Mock).mock
			.calls[0][0];

		expect(filters).toEqual([
			{
				id: FDS_FILTER_ID.STATUS,
				selectedData: expect.objectContaining({
					selectedItems: [
						expect.objectContaining({
							value: WORKFLOW_STATUS.EXPIRED,
						}),
					],
				}),
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
			{direction: 'asc', key: FDS_FILTER_ID.DATE_EXPIRATION},
		]);
	});

	it('opens the all section filtered by the review dates already passed', async () => {
		renderFreshness();

		await expandList('overdue-reviews');

		expect(getViewAllLink('overdue-reviews')).toHaveAttribute(
			'href',
			'/web/cms/all?allSection_fdsConfig=SERIALIZED_CONFIG'
		);

		const {filters, sorts} = (serializeFDSConfig as jest.Mock).mock
			.calls[0][0];

		expect(filters).toEqual([
			{
				id: FDS_FILTER_ID.DATE_REVIEW,
				selectedData: {
					exclude: false,
					from: null,
					to: toDatePart(new Date(NOW)),
				},
			},
		]);
		expect(sorts).toEqual([
			{direction: 'asc', key: FDS_FILTER_ID.DATE_REVIEW},
		]);
	});

	it('opens the all section filtered by the expiring soon criteria', async () => {
		renderFreshness({space: SPACE});

		await expandList('expiring-soon');

		expect(getViewAllLink('expiring-soon')).toHaveAttribute(
			'href',
			'/web/cms/all?allSection_fdsConfig=SERIALIZED_CONFIG'
		);

		const {filters, sorts} = (serializeFDSConfig as jest.Mock).mock
			.calls[0][0];

		expect(filters).toEqual([
			{
				id: FDS_FILTER_ID.DATE_EXPIRATION,
				selectedData: {
					exclude: false,
					from: toDatePart(new Date(NOW)),
					to: toDatePart(new Date(NOW + 7 * MILLISECONDS_PER_DAY)),
				},
			},
			{
				id: FDS_FILTER_ID.STATUS,
				selectedData: expect.objectContaining({
					selectedItems: [
						expect.objectContaining({
							value: WORKFLOW_STATUS.APPROVED,
						}),
					],
				}),
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
			{direction: 'asc', key: FDS_FILTER_ID.DATE_EXPIRATION},
		]);
	});

	it('opens the all section filtered by the next seven days of review dates', async () => {
		renderFreshness({space: SPACE});

		await expandList('upcoming-reviews');

		expect(getViewAllLink('upcoming-reviews')).toHaveAttribute(
			'href',
			'/web/cms/all?allSection_fdsConfig=SERIALIZED_CONFIG'
		);

		const {filters, sorts} = (serializeFDSConfig as jest.Mock).mock
			.calls[0][0];

		expect(filters).toEqual([
			{
				id: FDS_FILTER_ID.DATE_REVIEW,
				selectedData: {
					exclude: false,
					from: toDatePart(new Date(NOW)),
					to: toDatePart(new Date(NOW + 7 * MILLISECONDS_PER_DAY)),
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
			{direction: 'asc', key: FDS_FILTER_ID.DATE_REVIEW},
		]);
	});

	it('reloads the statistics when a bulk action refreshes the open list', async () => {
		const handlers: Record<string, (event?: {id?: string}) => void> = {};

		jest.spyOn(Liferay, 'on').mockImplementation(((
			name: string,
			handler: (event?: {id?: string}) => void
		) => {
			handlers[name] = handler;
		}) as any);

		renderFreshness();

		await expandList('expired');

		handlers[FDS_EVENT_UPDATE_DISPLAY]({id: 'upcomingReviews'});

		expect(reloadStatistics).not.toHaveBeenCalled();

		handlers[FDS_EVENT_UPDATE_DISPLAY]({id: 'expiredAssets'});

		expect(reloadStatistics).toHaveBeenCalledTimes(1);
	});

	it('shows how many of the assets are listed', async () => {
		renderFreshness();

		await expandList('expiring-soon');

		expect(screen.getByText('Showing 8 of 9 Items')).toBeInTheDocument();
	});

	it('has no accessibility violations with a list expanded', async () => {
		const {container} = renderFreshness();

		await expandList('expired');

		jest.useRealTimers();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
