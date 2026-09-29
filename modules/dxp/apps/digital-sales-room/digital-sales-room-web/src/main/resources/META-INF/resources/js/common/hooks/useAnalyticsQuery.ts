/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useIsMounted} from '@liferay/frontend-js-react-web';

// @ts-ignore - Check possibility to install package in ts format

import sha256 from 'hash.js/lib/hash/sha/256';
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

export function toRequestParams(
	filters: TAnalyticsFilter,
	variables: Record<string, unknown>
) {
	const roomFilterValue = filters[AnalyticsFilters.ROOM]
		.value as TRoomAnalyticsFilterValue;
	const dateRangeFilterValue = filters[AnalyticsFilters.DATE_RANGE]
		.value as TDateRangeAnalyticsFilterValue;
	const userFilter = filters[AnalyticsFilters.USER] as IAnalyticsUserFilter;

	const emailAddress = userFilter?.value?.[0];

	return {
		...variables,
		emailAddresses: userFilter?.value ?? [],
		entityId: emailAddress
			? sha256().update(emailAddress.toLowerCase().trim()).digest('hex')
			: '',
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
	settings = {
		checkViewportVisibility: true,
		isAnalyticsEnabled: false,
	},
	variables,
}: {
	element: HTMLElement | null;
	query: {paths: TAnalyticsQueryPath[]};
	settings?: {
		checkViewportVisibility?: boolean;
		isAnalyticsEnabled?: boolean;
	};
	variables: Record<string, unknown>;
}) {
	const [isLoading, setIsLoading] = useState(true);
	const [response, setResponse] = useState<Record<string, any> | null>(null);
	const [filters, setFilters] = useState<TAnalyticsFilter>(toFilters(null));

	const isMounted = useIsMounted();
	const isVisible = useIsInViewport(element);

	const lastFetchedFiltersRef = useRef<string | null>(null);
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

			if (
				(currentSettings.checkViewportVisibility && !isVisible) ||
				!currentSettings.isAnalyticsEnabled
			) {
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
		if (settingsRef.current.checkViewportVisibility && !isVisible) {
			return;
		}

		const serializedFilters = JSON.stringify(filters);

		if (lastFetchedFiltersRef.current === serializedFilters) {
			return;
		}

		lastFetchedFiltersRef.current = serializedFilters;

		sendRequest(filters);
	}, [filters, isVisible, sendRequest]);

	return {isLoading, response, sendRequest};
}
