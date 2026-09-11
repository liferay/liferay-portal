import CampaignTouchesByDayQuery, {
	CAMPAIGNS_PER_PAGE,
} from 'shared/queries/CampaignTouchesByDayQuery';
import React from 'react';
import {act, renderHook, waitFor} from '@testing-library/react';
import {MockedProvider} from '@apollo/client/testing';
import {SessionEntityTypes} from 'shared/util/constants';
import {useCampaignTouchesByDay} from '../useCampaignTouchesByDay';

jest.unmock('react-dom');

const VARIABLES = {
	accountId: 'account-1',
	channelId: 'channel-1',
	entityId: '',
	entityType: SessionEntityTypes.Individual,
	keywords: '',
	rangeEnd: '2026-07-16T23:59:59Z',
	rangeKey: 30,
	rangeStart: '2026-06-16T00:00:00Z',
};

const buildTouch = (campaignId: string) => ({
	campaignId,
	campaignName: 'Q3 Manufacturing ABM',
	origin: 'SALESFORCE',
	touches: [
		{
			individualId: null,
			individualName: 'Michelle de Rue',
			jobTitle: 'VP of Operations',
			status: 'Registered',
		},
	],
	touchesCount: 1,
});

const buildRequest = (variables: Record<string, unknown>, days: unknown[]) => ({
	request: {query: CampaignTouchesByDayQuery, variables},
	result: {data: {campaignTouchesByDay: days}},
});

const renderCampaignTouches = (mocks: unknown[] = [], skip = false) =>
	renderHook(() => useCampaignTouchesByDay(VARIABLES, {skip}), {
		wrapper: ({children}) => (
			<MockedProvider addTypename={false} mocks={mocks as never}>
				{children as never}
			</MockedProvider>
		),
	});

describe('useCampaignTouchesByDay', () => {
	it('holds nothing while the caller skips it', () => {
		const {result} = renderCampaignTouches([], true);

		expect(result.current.days).toEqual({});
	});

	it('keys each answered day by its day, holding the day totals', async () => {
		const {result} = renderCampaignTouches([
			buildRequest(
				{
					...VARIABLES,
					date: null,
					page: 0,
					size: CAMPAIGNS_PER_PAGE,
				},
				[
					{
						campaignsCount: 9,
						date: '2026-07-16',
						items: [buildTouch('c1')],
						touchesCount: 12,
					},
				]
			),
		]);

		await waitFor(() =>
			expect(result.current.days['2026-07-16']).toBeDefined()
		);

		expect(result.current.days['2026-07-16']).toMatchObject({
			campaignsCount: 9,
			page: 1,
			touchesCount: 12,
		});
	});

	it('replaces a single day when its own pager moves', async () => {
		const {result} = renderCampaignTouches([
			buildRequest(
				{
					...VARIABLES,
					date: null,
					page: 0,
					size: CAMPAIGNS_PER_PAGE,
				},
				[
					{
						campaignsCount: 9,
						date: '2026-07-16',
						items: [buildTouch('c1')],
						touchesCount: 12,
					},
				]
			),
			buildRequest(
				{
					...VARIABLES,
					date: '2026-07-16',
					page: 1,
					size: CAMPAIGNS_PER_PAGE,
				},
				[
					{
						campaignsCount: 9,
						date: '2026-07-16',
						items: [buildTouch('c9')],
						touchesCount: 12,
					},
				]
			),
		]);

		await waitFor(() =>
			expect(result.current.days['2026-07-16']).toBeDefined()
		);

		act(() => result.current.onCampaignPageChange('2026-07-16', 2));

		await waitFor(() =>
			expect(result.current.days['2026-07-16'].page).toBe(2)
		);

		expect(result.current.days['2026-07-16'].campaigns[0].campaignId).toBe(
			'c9'
		);
	});
});
