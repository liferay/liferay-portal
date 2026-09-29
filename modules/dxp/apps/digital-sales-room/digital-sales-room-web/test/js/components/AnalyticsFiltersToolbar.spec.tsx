/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen} from '@testing-library/react';
import React from 'react';

import AnalyticsFiltersToolbar from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/AnalyticsFiltersToolbar';
import {
	AnalyticsFilters,
	TAnalyticsFilter,
} from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/types';

const filters: TAnalyticsFilter = {
	[AnalyticsFilters.DATE_RANGE]: {
		active: true,
		component: () => <div>date-range-filter</div>,
		value: {from: '2025-01-01', to: '2025-01-07'},
	},
	[AnalyticsFilters.ROOM]: {
		active: true,
		component: () => <div>room-filter</div>,
		value: {room: null},
	},
	[AnalyticsFilters.USER]: {
		active: true,
		component: () => <div>user-filter</div>,
		value: [],
	},
};

describe('AnalyticsFiltersToolbar', () => {
	afterEach(cleanup);

	it('renders nothing when not interactable', () => {
		const {container} = render(
			<AnalyticsFiltersToolbar
				filters={filters}
				filtersJSONString={null}
				interactable={false}
				persisted={false}
				setValue={jest.fn()}
			/>
		);

		expect(
			container.querySelector('.analytics-filters-toolbar')
		).not.toBeInTheDocument();
	});

	it('mounts the active non-room filters when interactable', () => {
		const {container} = render(
			<AnalyticsFiltersToolbar
				filters={filters}
				filtersJSONString={null}
				interactable={true}
				persisted={false}
				setValue={jest.fn()}
			/>
		);

		expect(
			container.querySelector('.analytics-filters-toolbar')
		).toBeInTheDocument();

		expect(screen.getByText('date-range-filter')).toBeInTheDocument();
		expect(screen.getByText('user-filter')).toBeInTheDocument();
		expect(screen.queryByText('room-filter')).not.toBeInTheDocument();
	});

	it('skips inactive filters', () => {
		render(
			<AnalyticsFiltersToolbar
				filters={{
					...filters,
					[AnalyticsFilters.USER]: {
						...filters[AnalyticsFilters.USER],
						active: false,
					},
				}}
				filtersJSONString={null}
				interactable={true}
				persisted={false}
				setValue={jest.fn()}
			/>
		);

		expect(screen.getByText('date-range-filter')).toBeInTheDocument();
		expect(screen.queryByText('user-filter')).not.toBeInTheDocument();
	});
});
