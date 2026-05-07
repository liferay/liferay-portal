/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useIsMounted} from '@liferay/frontend-js-react-web';
import {useCallback, useEffect, useRef, useState} from 'react';

import {
	AnalyticsFilters,
	IAnalyticsUserFilter,
	TAnalyticsFilter,
	TDateRangeAnalyticsFilterValue,
	TRoomAnalyticsFilterValue,
} from '../../main_view/analytics/types';
import {toFilters} from '../../main_view/analytics/utils';
import AnalyticsService from '../services/AnalyticsService';
import useIsInViewport from './useIsInViewport';

function toRequestParams(
	filters: TAnalyticsFilter,
	variables: Record<string, unknown>
) {
	const roomFilterValue = filters[AnalyticsFilters.ROOM]
		.value as TRoomAnalyticsFilterValue;
	const dateRangeFilterValue = filters[AnalyticsFilters.DATE_RANGE]
		.value as TDateRangeAnalyticsFilterValue;
	const userFilter = filters[AnalyticsFilters.USER] as IAnalyticsUserFilter;

	return {
		...variables,
		emailAddresses: userFilter.value,
		groupIds: roomFilterValue.room?.siteId
			? [roomFilterValue.room?.siteId]
			: [],
		rangeEnd: dateRangeFilterValue.to,
		rangeStart: dateRangeFilterValue.from,
	};
}

type TAnalyticsQueryPath = {
	key: string;
	path: string;
	variables?: Record<string, unknown>;
};

export default function useAnalyticsQuery({
	element,
	query,
	settings = {checkViewportVisibility: true},
	variables,
}: {
	element: HTMLElement | null;
	query: {paths: TAnalyticsQueryPath[]};
	settings?: {
		checkViewportVisibility: boolean;
	};
	variables: Record<string, unknown>;
}) {
	const [isLoading, setIsLoading] = useState(true);
	const [response, setResponse] = useState<Record<string, any> | null>(null);
	const [filters, setFilters] = useState<TAnalyticsFilter>(toFilters(null));

	const isMounted = useIsMounted();
	const isVisible = useIsInViewport(element);

	const queryRef = useRef(query);
	const settingsRef = useRef(settings);
	const variablesRef = useRef(variables);

	useEffect(() => {
		queryRef.current = query;
		settingsRef.current = settings;
		variablesRef.current = variables;
	});

	const sendRequest = useCallback(
		async (activeFilters: TAnalyticsFilter) => {
			const currentSettings = settingsRef.current;

			if (currentSettings.checkViewportVisibility && !isVisible) {
				return;
			}

			setIsLoading(true);

			try {
				const entries = await Promise.all(
					queryRef.current.paths.map(
						async ({key, path, variables: overrides}) => {
							const params = toRequestParams(activeFilters, {
								...variablesRef.current,
								...(overrides ?? {}),
							});

							const result = await AnalyticsService.get(
								path,
								params
							);

							return [key, result] as const;
						}
					)
				);

				if (isMounted()) {
					setResponse(Object.fromEntries(entries) as any);
				}
			}
			catch (_ignore) {
				if (isMounted()) {
					setResponse(null);
				}
			}

			if (isMounted()) {
				setIsLoading(false);
			}
		},
		[isVisible, isMounted]
	);

	useEffect(() => {
		const handleFiltersUpdate = ({
			filters: incoming,
		}: {
			filters: TAnalyticsFilter;
		}) => {
			setFilters((current) =>
				JSON.stringify(current) === JSON.stringify(incoming)
					? current
					: incoming
			);
		};

		if (isMounted()) {
			Liferay.on('dsr-filters-updated', handleFiltersUpdate);
		}

		return () => {
			if (isMounted()) {
				Liferay.detach('dsr-filters-updated', handleFiltersUpdate);
			}
		};
	}, [isMounted]);

	useEffect(() => {
		sendRequest(filters);
	}, [filters, isVisible, sendRequest]);

	return {isLoading, response, sendRequest};
}
