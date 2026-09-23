import React from 'react';
import {render} from '@testing-library/react';
import {Tooltip} from '../Tooltip';

jest.unmock('react-dom');

const values = {high: '12', improvement: undefined, low: '8', median: '10'};

const payload = {
	key: '2026-06-10T00:00:00.000Z',
	tooltip: {control: values, variant: values},
};

describe('Tooltip', () => {
	it('renders the day of the data point in the header', () => {
		const {getByText} = render(
			<Tooltip
				dataPoint={[
					{color: '#000', name: 'Control', payload},
					{color: '#fff', name: 'Variant 1', payload},
				]}
			/>
		);

		expect(getByText('Variants | Jun 10, 2026')).toBeInTheDocument();
	});
});
