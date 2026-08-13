/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {serializeFDSConfig} from '@liferay/frontend-data-set-web';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {
	EXPIRING_SOON_THRESHOLD_DAYS,
	FDS_FILTER_ID,
	WORKFLOW_STATUS,
} from '../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import {GovernanceContext} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceContext';
import {NeedsReview} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/NeedsReview';
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
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService',
	() => ({
		__esModule: true,
		default: {getSearchURL: jest.fn(() => '/o/search')},
	})
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/getDashboardAssetListFDSProps',
	() => ({__esModule: true, default: jest.fn(() => ({}))})
);

const ADDITIONAL_PROPS = {
	allSectionFDSName: 'allSection',
	expiringSoonFDSName: 'expiringSoon',
	expiringSoonFilterString: 'expiringSoonFilter',
	fdsActionDropdownItems: [{data: {id: 'update-review-date'}}],
	upcomingReviewsFDSName: 'upcomingReviews',
	upcomingReviewsFilterString: 'upcomingReviewsFilter',
} as any;

const LANGUAGE_KEYS: Record<string, string> = {
	'expiring-soon': 'Expiring Soon',
	'upcoming-reviews': 'Upcoming Reviews',
	'view-x': 'View {0}',
};

function getSerializedConfigs() {
	return (serializeFDSConfig as jest.Mock).mock.calls.map(
		([config]) => config
	);
}

function getFilters(filterId: string) {
	const config = getSerializedConfigs().find(({filters}) =>
		filters?.some(({id}: {id: string}) => id === filterId)
	);

	return config?.filters;
}

function getWindowInDays({from, to}: any) {
	const toUTC = ({day, month, year}: any) =>
		Date.UTC(year, month - 1, day) / (1000 * 60 * 60 * 24);

	return toUTC(to) - toUTC(from);
}

function renderNeedsReview(
	space: {label?: string; siteId?: number; value: string} = {value: 'all'}
) {
	return render(
		<GovernanceContext.Provider value={{space} as any}>
			<NeedsReview additionalProps={ADDITIONAL_PROPS} />
		</GovernanceContext.Provider>
	);
}

describe('[CMS Dashboard] NeedsReview', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		jest.spyOn(Liferay.Language, 'get').mockImplementation(
			(key: string) => LANGUAGE_KEYS[key] ?? key
		);
	});

	it('names each view all link after the card it belongs to', () => {
		renderNeedsReview();

		expect(
			screen.getByRole('link', {name: 'View Upcoming Reviews'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('link', {name: 'View Expiring Soon'})
		).toBeInTheDocument();
	});

	it('shows the next month of reviews sorted by soonest in the upcoming reviews link', () => {
		renderNeedsReview();

		expect(
			screen.getByRole('link', {name: 'View Upcoming Reviews'})
		).toHaveAttribute(
			'href',
			'/web/cms/all?allSection_fdsConfig=SERIALIZED_CONFIG'
		);

		const upcomingReviewsConfig = getSerializedConfigs().find(
			({sorts}) => sorts
		);

		expect(upcomingReviewsConfig.sorts).toEqual([
			{direction: 'asc', key: FDS_FILTER_ID.DATE_REVIEW},
		]);

		const dateReviewFilter = upcomingReviewsConfig.filters.find(
			({id}: {id: string}) => id === FDS_FILTER_ID.DATE_REVIEW
		);

		const {from, to} = dateReviewFilter.selectedData;

		expect(to.day).toBe(from.day);
		expect((to.year - from.year) * 12 + (to.month - from.month)).toBe(1);
	});

	it('shows only approved content in the expiring soon link', () => {
		renderNeedsReview();

		const expiringSoonFilters = getFilters(FDS_FILTER_ID.DATE_EXPIRATION);

		expect(expiringSoonFilters).toHaveLength(2);

		const statusFilter = expiringSoonFilters.find(
			({id}: {id: string}) => id === FDS_FILTER_ID.STATUS
		);

		expect(statusFilter.selectedData.selectedItems).toEqual([
			expect.objectContaining({value: WORKFLOW_STATUS.APPROVED}),
		]);
	});

	it('ends the expiring soon expiration window seven days from today', () => {
		renderNeedsReview();

		const {selectedData} = getFilters(FDS_FILTER_ID.DATE_EXPIRATION).find(
			({id}: {id: string}) => id === FDS_FILTER_ID.DATE_EXPIRATION
		);

		expect(getWindowInDays(selectedData)).toBe(
			EXPIRING_SOON_THRESHOLD_DAYS
		);
	});

	it('filters both view all links by the selected space', () => {
		renderNeedsReview({label: 'My Space', siteId: 12345, value: '999'});

		const configs = getSerializedConfigs();

		expect(configs).toHaveLength(2);

		for (const config of configs) {
			const spaceFilter = config.filters.find(
				({id}: {id: string}) => id === FDS_FILTER_ID.SCOPE_GROUP_ID
			);

			expect(spaceFilter.selectedData).toEqual({
				exclude: false,
				selectedItems: [{label: 'My Space', value: 12345}],
			});
		}
	});

	it('leaves the view all links unscoped when all spaces is selected', () => {
		renderNeedsReview();

		for (const config of getSerializedConfigs()) {
			expect(
				config.filters?.some(
					({id}: {id: string}) => id === FDS_FILTER_ID.SCOPE_GROUP_ID
				)
			).toBeFalsy();
		}
	});

	it('renders the review date on one card and the status on the other', () => {
		renderNeedsReview();

		const [upcomingReviews, expiringSoon] = (
			getDashboardAssetListFDSProps as jest.Mock
		).mock.calls.map(([{renderSubtitle}]) => renderSubtitle);

		expect(
			upcomingReviews({embedded: {reviewDate: '2099-12-31T10:00:00Z'}})
		).toBe('Dec 31, 2099');
		expect(upcomingReviews({embedded: {}})).toBe('--');
		expect(expiringSoon({embedded: {}})).toBe('--');
	});
});
