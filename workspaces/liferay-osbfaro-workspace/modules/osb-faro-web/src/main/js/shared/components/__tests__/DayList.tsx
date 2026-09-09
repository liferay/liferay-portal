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

	it('renders nothing when there are no days', () => {
		const {container} = render(<DayList timeZoneId={TIME_ZONE_ID} />);

		expect(container.querySelector('.date-header')).not.toBeInTheDocument();
	});
});
