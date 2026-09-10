import CampaignTouchesByDayQuery, {
	CAMPAIGNS_PER_PAGE,
	CampaignTouchesByDayData,
	CampaignTouchesByDayVariables,
} from 'shared/queries/CampaignTouchesByDayQuery';
import {CampaignTouch, toDayKey} from 'shared/util/activities';
import {useCallback, useEffect, useMemo, useRef, useState} from 'react';
import {ENABLE_DAY_LEVEL_MOCK_DATA} from 'shared/util/feature-flags';
import {mockCampaignTouchesByDay} from 'shared/util/campaignTouchesMock';
import {useLazyQuery, useQuery} from '@apollo/client';

export type CampaignDay = {
	campaigns: CampaignTouch[];
	campaignsCount: number;
	delta: number;
	page: number;
	touchesCount: number;
};

export type CampaignDays = Record<string, CampaignDay>;

// Shared so the empty result keeps one identity: a fresh {} on every range or
// keyword change re-renders the card and re-runs the callers' day formatting.

const EMPTY_DAYS: CampaignDays = {};

type ICampaignTouchesVariables = Omit<
	CampaignTouchesByDayVariables,
	'date' | 'page' | 'size'
>;

export const useCampaignTouchesByDay = (
	variables: ICampaignTouchesVariables,
	{skip}: {skip?: boolean} = {}
) => {
	const [days, setDays] = useState<CampaignDays>({});

	const _requestIdsRef = useRef<Record<string, number>>({});

	const {entityId, keywords, rangeEnd, rangeKey, rangeStart} = variables;

	const {data, error, loading} = useQuery<
		CampaignTouchesByDayData,
		CampaignTouchesByDayVariables
	>(CampaignTouchesByDayQuery, {
		skip: skip || ENABLE_DAY_LEVEL_MOCK_DATA,
		variables: {
			...variables,
			date: null,
			page: 0,
			size: CAMPAIGNS_PER_PAGE,
		},
	});

	const [fetchDay] = useLazyQuery<
		CampaignTouchesByDayData,
		CampaignTouchesByDayVariables
	>(CampaignTouchesByDayQuery, {fetchPolicy: 'network-only'});

	useEffect(() => {
		_requestIdsRef.current = {};

		setDays(
			skip || !ENABLE_DAY_LEVEL_MOCK_DATA
				? EMPTY_DAYS
				: mockCampaignTouchesByDay({rangeEnd})
		);
	}, [entityId, keywords, rangeEnd, rangeKey, rangeStart, skip]);

	useEffect(() => {
		if (!data) {
			return;
		}

		setDays(
			data.campaignTouchesByDay.reduce<CampaignDays>(
				(allDays, {campaignsCount, date, items, touchesCount}) => ({
					...allDays,
					[toDayKey(date)]: {
						campaigns: items,
						campaignsCount,
						delta: CAMPAIGNS_PER_PAGE,
						page: 1,
						touchesCount,
					},
				}),
				{}
			)
		);
	}, [data]);

	const loadDay = useCallback(
		(date: string, page: number, delta: number) => {
			if (ENABLE_DAY_LEVEL_MOCK_DATA) {
				setDays((allDays) => ({
					...allDays,
					...mockCampaignTouchesByDay({date, delta, page}),
				}));

				return;
			}

			const dayKey = toDayKey(date);

			const requestId = (_requestIdsRef.current[dayKey] ?? 0) + 1;

			_requestIdsRef.current[dayKey] = requestId;

			fetchDay({
				variables: {...variables, date, page: page - 1, size: delta},
			}).then(({data}) => {
				if (_requestIdsRef.current[dayKey] !== requestId) {
					return;
				}

				const [day] = data?.campaignTouchesByDay ?? [];

				setDays((allDays) => ({
					...allDays,
					[dayKey]: {
						campaigns: day?.items ?? [],
						campaignsCount: day?.campaignsCount ?? 0,
						delta,
						page,
						touchesCount: day?.touchesCount ?? 0,
					},
				}));
			});
		},
		[fetchDay, variables]
	);

	return useMemo(
		() => ({
			days,
			error,
			loading,
			onCampaignDeltaChange: (date: string, delta: number) =>
				loadDay(date, 1, delta),
			onCampaignPageChange: (date: string, page: number) =>
				loadDay(
					date,
					page,
					days[toDayKey(date)]?.delta ?? CAMPAIGNS_PER_PAGE
				),
		}),
		[days, error, loading, loadDay]
	);
};
