import React from 'react';
import {SummaryNoWinnerCard} from '../SummaryNoWinnerCard';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const variants = [
	{
		changes: 0,
		control: true,
		dxpVariantId: 'DEFAULT',
		dxpVariantName: 'Control',
		trafficSplit: 50,
		uniqueVisitors: 10,
	},
	{
		changes: 1,
		control: false,
		dxpVariantId: 'VARIANT_1',
		dxpVariantName: 'Variant 1',
		trafficSplit: 50,
		uniqueVisitors: 12,
	},
];

const experiment = {
	dxpVariants: variants,
	id: '123',
	metrics: {
		completion: 50,
		elapsedDays: 3,
		variantMetrics: variants,
	},
	sessions: 100,
	startedDate: '2026-06-10T01:30:00Z',
	status: 'FINISHED_NO_WINNER',
};

describe('SummaryNoWinnerCard', () => {
	it('renders the started date in the given time zone', () => {
		const {getByText} = render(
			<SummaryNoWinnerCard
				experiment={experiment}
				timeZoneId="America/Recife"
			/>
		);

		expect(getByText('Started: Jun 9, 2026')).toBeInTheDocument();
	});
});
