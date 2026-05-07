/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect, useState} from 'react';

import {TAnalyticsFilter} from '../../main_view/analytics/types';
import {toFilters, toStoredFilters} from '../../main_view/analytics/utils';
import AnalyticsService from '../services/AnalyticsService';

type FiltersState = {
	filters: TAnalyticsFilter;
	source: 'init' | 'internal' | 'external';
};

export default function useAnalyticsFilters(
	filtersJSONString: string | null,
	persisted: boolean = false
) {
	const [{filters, source}, setState] = useState<FiltersState>({
		filters: toFilters(filtersJSONString),
		source: 'init',
	});

	const setFilter = useCallback((filter: TAnalyticsFilter) => {
		setState((current) => {
			const filterName = Object.keys(filter)[0] as keyof TAnalyticsFilter;

			const updatedFilterValue = JSON.stringify(filter[filterName]);
			const filterValue = JSON.stringify(current.filters[filterName]);

			if (filterValue !== updatedFilterValue) {
				return {
					filters: {
						...current.filters,
						[filterName]: filter[filterName],
					},
					source: 'internal',
				};
			}

			return current;
		});
	}, []);

	useEffect(() => {
		if (source !== 'internal') {
			return;
		}

		const shouldStore = persisted
			? () => AnalyticsService.storeFilters(toStoredFilters(filters))
			: () => Promise.resolve();

		shouldStore().then(() => {
			Liferay.fire('dsr-filters-updated', {filters});
		});
	}, [filters, persisted, source]);

	useEffect(() => {
		const handleFiltersUpdate = ({
			filters: incoming,
		}: {
			filters: TAnalyticsFilter;
		}) => {
			setState((current) => {
				if (
					JSON.stringify(current.filters) === JSON.stringify(incoming)
				) {
					return current;
				}

				return {filters: incoming, source: 'external'};
			});
		};

		Liferay.on('dsr-filters-updated', handleFiltersUpdate);

		return () => {
			Liferay.detach('dsr-filters-updated', handleFiltersUpdate);
		};
	}, []);

	return [filters, setFilter] as const;
}
