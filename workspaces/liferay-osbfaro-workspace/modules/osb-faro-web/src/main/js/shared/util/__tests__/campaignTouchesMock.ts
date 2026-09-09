import {CAMPAIGNS_PER_PAGE} from 'shared/queries/CampaignTouchesByDayQuery';
import {mockCampaignTouchesByDay} from '../campaignTouchesMock';

describe('mockCampaignTouchesByDay', () => {
	it('answers with a spread of days, some of them without campaigns', () => {
		const days = mockCampaignTouchesByDay({rangeEnd: '2026-07-16'});

		expect(Object.keys(days)).toEqual([
			'2026-07-16',
			'2026-07-14',
			'2026-07-13',
		]);
	});

	it('holds back the campaigns a day pager has yet to ask for', () => {
		const [day] = Object.values(
			mockCampaignTouchesByDay({rangeEnd: '2026-07-16'})
		);

		expect(day.campaignsCount).toBe(9);
		expect(day.campaigns).toHaveLength(CAMPAIGNS_PER_PAGE);
		expect(day.page).toBe(1);
	});

	it('answers a single day when one is asked for', () => {
		const days = mockCampaignTouchesByDay({date: '2026-07-16', page: 2});

		expect(Object.keys(days)).toEqual(['2026-07-16']);
		expect(days['2026-07-16'].campaigns).toHaveLength(1);
		expect(days['2026-07-16'].page).toBe(2);
	});

	it('counts every touch of the day, not just the page', () => {
		const [day] = Object.values(
			mockCampaignTouchesByDay({rangeEnd: '2026-07-16'})
		);

		expect(day.touchesCount).toBeGreaterThan(day.campaigns.length);
	});
});
