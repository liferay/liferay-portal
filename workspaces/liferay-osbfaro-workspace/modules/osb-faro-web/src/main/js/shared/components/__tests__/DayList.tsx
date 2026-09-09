import DayList from '../DayList';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const TIME_ZONE_ID = 'UTC';

const buildDay = (title: string, totalEvents: number, individualName: string) =>
	({
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

describe('DayList', () => {
	afterEach(cleanup);

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

	it('gives every day both cards, whichever one holds the data', () => {
		const withCampaigns = buildDay('Jul 16', 3, 'Ada Lovelace');

		withCampaigns.campaigns = [CAMPAIGN];

		render(
			<DayList
				emptyState={<div>{'Nothing here'}</div>}
				items={[withCampaigns, buildDay('Jul 15', 2, 'Grace Hopper')]}
				timeZoneId={TIME_ZONE_ID}
			/>
		);

		expect(screen.getAllByText(/day.level/i)).toHaveLength(2);
		expect(screen.getAllByText(/timed.activity/i)).toHaveLength(2);
	});

	it('fills the two cards independently', () => {
		const campaignsOnly = buildDay('Jul 16', 3, 'Ada Lovelace');

		campaignsOnly.campaigns = [CAMPAIGN];
		campaignsOnly.items = [];

		render(
			<DayList
				emptyState={<div>{'Nothing here'}</div>}
				items={[campaignsOnly]}
				timeZoneId={TIME_ZONE_ID}
			/>
		);

		expect(screen.getByText('Q3 Manufacturing ABM')).toBeInTheDocument();
		expect(screen.getByText('Nothing here')).toBeInTheDocument();
	});

	it('shows the empty state on the day-level card when only sessions exist', () => {
		render(
			<DayList
				emptyState={<div>{'Nothing here'}</div>}
				items={[buildDay('Jul 15', 2, 'Grace Hopper')]}
				timeZoneId={TIME_ZONE_ID}
			/>
		);

		expect(screen.getByText('Grace Hopper')).toBeInTheDocument();
		expect(screen.getByText('Nothing here')).toBeInTheDocument();
	});

	it('renders nothing when there are no days', () => {
		const {container} = render(<DayList timeZoneId={TIME_ZONE_ID} />);

		expect(container.querySelector('.date-header')).not.toBeInTheDocument();
	});
});
