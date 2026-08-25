/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {serializeFDSConfig} from '@liferay/frontend-data-set-web';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {
	FDS_FILTER_ID,
	WORKFLOW_STATUS,
} from '../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import {GovernanceContext} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceContext';
import GovernanceService from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService';
import {ContentProgress} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/ContentProgress';

jest.mock('@liferay/frontend-data-set-web', () => ({
	getConfigParamName: (fdsName: string) => `${fdsName}_fdsConfig`,
	serializeFDSConfig: jest.fn(() => 'SERIALIZED_CONFIG'),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService',
	() => ({
		__esModule: true,
		default: {getContentProgress: jest.fn()},
	})
);

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

function mockContentProgress(buckets: object[]) {
	(GovernanceService.getContentProgress as jest.Mock).mockResolvedValue({
		data: {searchFacets: {statusFacet: buckets}},
		error: null,
	});
}

function renderContentProgress(space: object = {value: 'all'}) {
	return render(
		<GovernanceContext.Provider value={{space} as any}>
			<ContentProgress additionalProps={ADDITIONAL_PROPS} />
		</GovernanceContext.Provider>
	);
}

describe('ContentProgress', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('renders one segment per status in the design order', async () => {
		mockContentProgress(BUCKETS);

		renderContentProgress();

		const segments = await screen.findAllByRole('link');

		expect(
			segments.map((segment) => segment.getAttribute('aria-label'))
		).toEqual([
			'draft: 3',
			'pending: 2',
			'approved: 5',
			'scheduled: 4',
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
		).toEqual(['draft: 3', 'approved: 5']);
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
			'draft: 3',
			'pending: 2',
			'approved: 5',
			'scheduled: 4',
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
			123
		);
	});

	it('shows the empty state when the scope has no content', async () => {
		mockContentProgress([]);

		renderContentProgress();

		expect(await screen.findByText('there-is-no-data')).toBeInTheDocument();
		expect(screen.queryByRole('img')).not.toBeInTheDocument();
		expect(screen.queryByRole('link')).not.toBeInTheDocument();
	});

	it('has no accessibility violations', async () => {
		mockContentProgress(BUCKETS);

		const {container} = renderContentProgress();

		await screen.findAllByRole('link');

		await checkAccessibility({bestPractices: true, context: container});
	});
});
