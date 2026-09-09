import CampaignTouchesByDayQuery, {
	CAMPAIGNS_PER_PAGE,
	CampaignTouchesByDayData,
	CampaignTouchesByDayVariables,
} from 'shared/queries/CampaignTouchesByDayQuery';
import {CampaignTouch, toDayKey} from 'shared/util/activities';
import {useCallback, useEffect, useMemo, useState} from 'react';
import {useLazyQuery, useQuery} from '@apollo/client';

export type CampaignDay = {
	campaigns: CampaignTouch[];
	campaignsCount: number;
	delta: number;
	page: number;
};

export type CampaignDays = Record<string, CampaignDay>;

type ICampaignTouchesVariables = Omit<
	CampaignTouchesByDayVariables,
	'date' | 'page' | 'size'
>;

export const useCampaignTouchesByDay = (
	variables: ICampaignTouchesVariables,
	{skip}: {skip?: boolean} = {}
) => {
	const [days, setDays] = useState<CampaignDays>({});

	const {entityId, keywords, rangeEnd, rangeKey, rangeStart} = variables;

	const {data, error, loading} = useQuery<
		CampaignTouchesByDayData,
		CampaignTouchesByDayVariables
	>(CampaignTouchesByDayQuery, {
		skip,
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
		setDays({});
	}, [entityId, keywords, rangeEnd, rangeKey, rangeStart]);

	useEffect(() => {
		if (!data) {
			return;
		}

		setDays(
			data.campaignTouchesByDay.reduce<CampaignDays>(
				(allDays, {campaignsCount, date, items}) => ({
					...allDays,
					[toDayKey(date)]: {
						campaigns: items,
						campaignsCount,
						delta: CAMPAIGNS_PER_PAGE,
						page: 1,
					},
				}),
				{}
			)
		);
	}, [data]);

	const loadDay = useCallback(
		(date: string, page: number, delta: number) =>
			fetchDay({
				variables: {...variables, date, page: page - 1, size: delta},
			}).then(({data}) => {
				const [day] = data?.campaignTouchesByDay ?? [];

				setDays((allDays) => ({
					...allDays,
					[toDayKey(date)]: {
						campaigns: day?.items ?? [],
						campaignsCount: day?.campaignsCount ?? 0,
						delta,
						page,
					},
				}));
			}),
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
