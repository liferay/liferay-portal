import React from 'react';
import {render} from '@testing-library/react';
import {Tooltip} from '../Tooltip';

jest.unmock('react-dom');

const payload = {
	data_control: 10,
	data_control_traffic_split: 50,
	data_variant: 12,
	data_variant_traffic_split: 50,
	key: '2026-06-10T00:00:00.000Z',
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

		expect(getByText('Test Traffic | Jun 10, 2026')).toBeInTheDocument();
	});
});
