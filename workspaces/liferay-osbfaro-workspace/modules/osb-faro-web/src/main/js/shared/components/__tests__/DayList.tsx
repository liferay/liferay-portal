import DayList from '../DayList';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('shared/util/feature-flags', () => ({
	...jest.requireActual('shared/util/feature-flags'),
	ENABLE_DAY_LEVEL_ACTIVITY: true,
}));

const featureFlags = jest.requireMock('shared/util/feature-flags');

const TIME_ZONE_ID = 'UTC';

const buildDay = (
	title: string,
	totalEvents: number,
	individualName: string,
	date = '2026-07-16'
) =>
	({
		date,
		header: {header: true, title, totalEvents},
		items: [
			{
				individual: true,
				individualName,
				isAnonymous: false,
			},
		],
	}) as any;

const CAMPAIGN = {
	campaignId: 'c1',
	campaignName: 'Q3 Manufacturing ABM',
	dataSourceType: 'salesforce',
	touches: [
		{
			individualId: null,
			individualName: 'Michelle de Rue',
			jobTitle: 'VP of Operations',
			status: 'Registered',
		},
	],
};

const buildCampaignDays = (date: string) => ({
	[date]: {
		campaigns: [CAMPAIGN],
		campaignsCount: 1,
		delta: 8,
		page: 1,
		touchesCount: 4,
	},
});

describe('DayList', () => {
	afterEach(cleanup);

	beforeEach(() => {
		featureFlags.ENABLE_DAY_LEVEL_ACTIVITY = true;
	});

	it('renders a date header per day, most recent first', () => {
		const {container} = render(
			<DayList
				items={[
					buildDay('Jul 16', 3, 'Ada Lovelace'),
					buildDay('Jul 15', 2, 'Grace Hopper'),
				]}
				timeZoneId={TIME_ZONE_ID}
			/>
		);

		const headers = Array.from(
			container.querySelectorAll('.date-header .title')
		);

		expect(headers.map((header) => header.textContent)).toEqual([
			'Jul 16',
			'Jul 15',
		]);
	});

	it('renders each day rows beneath its own header', () => {
		render(
			<DayList
				items={[buildDay('Jul 16', 3, 'Ada Lovelace')]}
				timeZoneId={TIME_ZONE_ID}
			/>
		);

		expect(screen.getByText('Jul 16')).toBeInTheDocument();
		expect(screen.getByText('Ada Lovelace')).toBeInTheDocument();
	});

	it('gives a day only the cards that hold something', () => {
		render(
			<DayList
				campaignDays={buildCampaignDays('2026-07-16')}
				items={[
					buildDay('Jul 16', 3, 'Ada Lovelace', '2026-07-16'),
					buildDay('Jul 15', 2, 'Grace Hopper', '2026-07-15'),
				]}
				timeZoneId={TIME_ZONE_ID}
			/>
		);

		expect(screen.getAllByText(/day.level/i)).toHaveLength(1);
		expect(screen.getAllByText(/timed.activity/i)).toHaveLength(2);
	});

	it('keeps the day-level card when the day has no sessions', () => {
		const campaignsOnly = buildDay(
			'Jul 16',
			3,
			'Ada Lovelace',
			'2026-07-16'
		);

		campaignsOnly.items = [];

		render(
			<DayList
				campaignDays={buildCampaignDays('2026-07-16')}
				items={[campaignsOnly]}
				timeZoneId={TIME_ZONE_ID}
			/>
		);

		expect(screen.getByText('Q3 Manufacturing ABM')).toBeInTheDocument();
		expect(screen.queryByText(/timed.activity/i)).not.toBeInTheDocument();
	});

	it('leaves the day-level card out when only sessions exist', () => {
		render(
			<DayList
				items={[buildDay('Jul 15', 2, 'Grace Hopper')]}
				timeZoneId={TIME_ZONE_ID}
			/>
		);

		expect(screen.getByText('Grace Hopper')).toBeInTheDocument();
		expect(screen.queryByText(/day.level/i)).not.toBeInTheDocument();
		expect(screen.getByText(/timed.activity/i)).toBeInTheDocument();
	});

	it('matches a day to its campaigns by day, not by instant', () => {
		render(
			<DayList
				campaignDays={buildCampaignDays('2026-07-16')}
				items={[
					buildDay(
						'Jul 16',
						3,
						'Ada Lovelace',
						'2026-07-16T00:00:00.000Z'
					),
				]}
				timeZoneId={TIME_ZONE_ID}
			/>
		);

		expect(screen.getByText('Q3 Manufacturing ABM')).toBeInTheDocument();
	});

	it('counts the day touches beside its events on the header', () => {
		const {container} = render(
			<DayList
				campaignDays={buildCampaignDays('2026-07-16')}
				items={[buildDay('Jul 16', 3, 'Ada Lovelace', '2026-07-16')]}
				timeZoneId={TIME_ZONE_ID}
			/>
		);

		const counts = Array.from(
			container.querySelectorAll('.date-header .event-count-pill')
		);

		expect(counts.map((count) => count.textContent)).toEqual(['3', '4']);
	});

	it('leaves the touch count off a day the campaigns did not reach', () => {
		const {container} = render(
			<DayList
				items={[buildDay('Jul 16', 3, 'Ada Lovelace', '2026-07-16')]}
				timeZoneId={TIME_ZONE_ID}
			/>
		);

		expect(
			container.querySelectorAll('.date-header .event-count-pill')
		).toHaveLength(1);
	});

	it('leaves the day-level card out while the feature is off', () => {
		featureFlags.ENABLE_DAY_LEVEL_ACTIVITY = false;

		render(
			<DayList
				campaignDays={buildCampaignDays('2026-07-16')}
				items={[buildDay('Jul 16', 3, 'Ada Lovelace', '2026-07-16')]}
				timeZoneId={TIME_ZONE_ID}
			/>
		);

		expect(screen.queryByText(/day.level/i)).not.toBeInTheDocument();
		expect(screen.getByText(/timed.activity/i)).toBeInTheDocument();
		expect(
			screen.queryByText('Q3 Manufacturing ABM')
		).not.toBeInTheDocument();
	});

	it('renders nothing when there are no days', () => {
		const {container} = render(<DayList timeZoneId={TIME_ZONE_ID} />);

		expect(container.querySelector('.date-header')).not.toBeInTheDocument();
	});
});
