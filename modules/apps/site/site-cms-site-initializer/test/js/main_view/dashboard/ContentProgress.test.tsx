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
	FDS_FILTER_ID,
	WORKFLOW_STATUS,
} from '../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import toDatePart from '../../../../src/main/resources/META-INF/resources/js/common/utils/toDatePart';
import {GovernanceContext} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceContext';
import GovernanceService, {
	MILLISECONDS_PER_DAY,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService';
import {ContentProgress} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/ContentProgress';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	sub: (key: string, ...values: unknown[]) =>
		[key, ...values.flat()].join(' '),
}));

jest.mock('@liferay/frontend-data-set-web', () => ({
	getConfigParamName: (fdsName: string) => `${fdsName}_fdsConfig`,
	serializeFDSConfig: jest.fn(() => 'SERIALIZED_CONFIG'),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService',
	() => ({
		...(jest.requireActual(
			'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService'
		) as object),
		__esModule: true,
		default: {getContentProgress: jest.fn()},
	})
);

const NOW = Date.UTC(2026, 9, 21, 12);

const ADDITIONAL_PROPS = {
	allSectionFDSName: 'allSection',
	contentProgressFilter: 'contentProgressFilter',
} as any;

// The search facet returns the raw status code as displayName.

const BUCKETS = [
	{displayName: '0', frequency: 5, term: '0'},
	{displayName: '1', frequency: 2, term: '1'},
	{displayName: '2', frequency: 3, term: '2'},
	{displayName: '3', frequency: 1, term: '3'},
	{displayName: '7', frequency: 4, term: '7'},
];

function getCreatedInLastDaysFilter(days: number) {
	return {
		id: FDS_FILTER_ID.DATE_CREATED,
		selectedData: {
			exclude: false,
			from: toDatePart(new Date(NOW - days * MILLISECONDS_PER_DAY)),
			to: null,
		},
	};
}

function mockContentProgress(buckets: object[]) {
	(GovernanceService.getContentProgress as jest.Mock).mockResolvedValue({
		data: {searchFacets: {statusFacet: buckets}},
		error: null,
	});
}

function getGroupIds(space: {siteId?: number}) {
	return space.siteId ? [space.siteId] : [];
}

function renderContentProgress(
	space: {label?: string; siteId?: number; value: string} = {value: 'all'}
) {
	return render(
		<GovernanceContext.Provider
			value={{groupIds: getGroupIds(space), space} as any}
		>
			<ContentProgress additionalProps={ADDITIONAL_PROPS} />
		</GovernanceContext.Provider>
	);
}

describe('ContentProgress', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	beforeEach(() => {
		jest.clearAllMocks();

		jest.spyOn(Date, 'now').mockReturnValue(NOW);
	});

	it('renders one segment per status from the largest to the smallest', async () => {
		mockContentProgress(BUCKETS);

		renderContentProgress();

		const segments = await screen.findAllByRole('link');

		expect(
			segments.map((segment) => segment.getAttribute('aria-label'))
		).toEqual([
			'approved: 5',
			'scheduled: 4',
			'draft: 3',
			'pending: 2',
			'others: 1',
		]);
	});

	it('omits statuses without content', async () => {
		mockContentProgress([
			{displayName: '0', frequency: 5, term: '0'},
			{displayName: '2', frequency: 3, term: '2'},
		]);

		renderContentProgress();

		const segments = await screen.findAllByRole('link');

		expect(
			segments.map((segment) => segment.getAttribute('aria-label'))
		).toEqual(['approved: 5', 'draft: 3']);
	});

	it('shows the share of each status in the legend', async () => {
		mockContentProgress(BUCKETS);

		renderContentProgress();

		await screen.findAllByRole('link');

		['20.0%', '13.3%', '33.3%', '26.7%', '6.7%'].forEach((share) => {
			expect(screen.getByText(share)).toBeInTheDocument();
		});
	});

	it('links each status segment to the All section filtered by that status', async () => {
		mockContentProgress(BUCKETS);

		renderContentProgress();

		const segments = await screen.findAllByRole('link');

		segments.forEach((segment) => {
			expect(segment).toHaveAttribute(
				'href',
				'/web/cms/all?allSection_fdsConfig=SERIALIZED_CONFIG'
			);
		});

		const configs = (serializeFDSConfig as jest.Mock).mock.calls.map(
			([config]) => config
		);

		expect(configs).toContainEqual({
			filters: [
				{
					id: FDS_FILTER_ID.STATUS,
					selectedData: {
						exclude: false,
						selectedItems: [
							{label: 'draft', value: WORKFLOW_STATUS.DRAFT},
						],
					},
				},
				getCreatedInLastDaysFilter(7),
			],
		});
	});

	it('links the others segment to the statuses it aggregates', async () => {
		mockContentProgress(BUCKETS);

		renderContentProgress();

		expect(
			await screen.findByRole('link', {name: 'others: 1'})
		).toBeInTheDocument();

		const configs = (serializeFDSConfig as jest.Mock).mock.calls.map(
			([config]) => config
		);

		expect(configs).toContainEqual({
			filters: [
				{
					id: FDS_FILTER_ID.STATUS,
					selectedData: {
						exclude: false,
						selectedItems: [
							{label: 'expired', value: WORKFLOW_STATUS.EXPIRED},
						],
					},
				},
				getCreatedInLastDaysFilter(7),
			],
		});
	});

	it('ignores the statuses the facet reports with no content', async () => {
		mockContentProgress([
			...BUCKETS,
			{displayName: '8', frequency: 0, term: '8'},
		]);

		renderContentProgress();

		const segments = await screen.findAllByRole('link');

		expect(
			segments.map((segment) => segment.getAttribute('aria-label'))
		).toEqual([
			'approved: 5',
			'scheduled: 4',
			'draft: 3',
			'pending: 2',
			'others: 1',
		]);

		const configs = (serializeFDSConfig as jest.Mock).mock.calls.map(
			([config]) => config
		);

		expect(configs).toContainEqual({
			filters: [
				{
					id: FDS_FILTER_ID.STATUS,
					selectedData: {
						exclude: false,
						selectedItems: [
							{label: 'expired', value: WORKFLOW_STATUS.EXPIRED},
						],
					},
				},
				getCreatedInLastDaysFilter(7),
			],
		});
	});

	it('scopes the segment links to the selected space', async () => {
		mockContentProgress(BUCKETS);

		renderContentProgress({label: 'My Space', siteId: 123, value: '456'});

		await screen.findAllByRole('link');

		const configs = (serializeFDSConfig as jest.Mock).mock.calls.map(
			([config]) => config
		);

		expect(configs).toContainEqual({
			filters: [
				{
					id: FDS_FILTER_ID.STATUS,
					selectedData: {
						exclude: false,
						selectedItems: [
							{label: 'draft', value: WORKFLOW_STATUS.DRAFT},
						],
					},
				},
				getCreatedInLastDaysFilter(7),
				{
					id: FDS_FILTER_ID.SCOPE_GROUP_ID,
					selectedData: {
						exclude: false,
						selectedItems: [{label: 'My Space', value: 123}],
					},
				},
			],
		});
	});

	it('scopes the request to the selected space', async () => {
		mockContentProgress(BUCKETS);

		renderContentProgress({siteId: 123, value: '456'});

		await screen.findAllByRole('link');

		expect(GovernanceService.getContentProgress).toHaveBeenCalledWith(
			'contentProgressFilter',
			[123],
			7
		);
	});

	it('requests the content again for the selected time range', async () => {
		mockContentProgress(BUCKETS);

		renderContentProgress();

		await screen.findAllByRole('link');

		await userEvent.click(
			screen.getByRole('combobox', {name: 'time-range'})
		);
		await userEvent.click(
			screen.getByRole('option', {name: 'last-x-days 90'})
		);

		expect(GovernanceService.getContentProgress).toHaveBeenLastCalledWith(
			'contentProgressFilter',
			[],
			90
		);
	});

	it('filters the segment links by the selected time range', async () => {
		mockContentProgress(BUCKETS);

		renderContentProgress();

		await screen.findAllByRole('link');

		(serializeFDSConfig as jest.Mock).mockClear();

		await userEvent.click(
			screen.getByRole('combobox', {name: 'time-range'})
		);
		await userEvent.click(
			screen.getByRole('option', {name: 'last-x-days 90'})
		);

		await screen.findAllByRole('link');

		const configs = (serializeFDSConfig as jest.Mock).mock.calls.map(
			([config]) => config
		);

		expect(configs).not.toContainEqual(
			expect.objectContaining({
				filters: expect.arrayContaining([
					getCreatedInLastDaysFilter(7),
				]),
			})
		);
		expect(configs).toContainEqual({
			filters: [
				{
					id: FDS_FILTER_ID.STATUS,
					selectedData: {
						exclude: false,
						selectedItems: [
							{label: 'draft', value: WORKFLOW_STATUS.DRAFT},
						],
					},
				},
				getCreatedInLastDaysFilter(90),
			],
		});
	});

	it('shows the empty state when the scope has no content', async () => {
		mockContentProgress([]);

		renderContentProgress();

		expect(
			await screen.findByText('nothing-to-track-yet')
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'there-is-no-content-movement-to-measure-progress-against'
			)
		).toBeInTheDocument();
		expect(screen.queryByRole('link')).not.toBeInTheDocument();
	});

	it('has no accessibility violations', async () => {
		mockContentProgress(BUCKETS);

		const {container} = renderContentProgress();

		await screen.findAllByRole('link');

		await checkAccessibility({bestPractices: true, context: container});
	});
});
