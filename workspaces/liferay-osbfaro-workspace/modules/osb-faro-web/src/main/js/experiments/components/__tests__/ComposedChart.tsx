import React from 'react';
import {ComposedChart} from '../ComposedChart';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('recharts', () => {
	const OriginalModule = jest.requireActual('recharts');

	return {
		...OriginalModule,
		ResponsiveContainer: ({children}: {children: React.ReactNode}) => (
			<OriginalModule.ResponsiveContainer height={320} width={800}>
				{children}
			</OriginalModule.ResponsiveContainer>
		),
	};
});

const keys = ['2026-06-10T00:00:00.000Z', '2026-06-11T00:00:00.000Z'];

describe('ComposedChart', () => {
	it('renders the day of each data point on the x axis', () => {
		const {container} = render(
			<ComposedChart
				data={{
					controlLabel: 'Control',
					data: keys.map((key, index) => ({
						data_control: index,
						key,
					})),
					format: String,
					intervals: keys,
				}}
				Tooltip={() => null}
			/>
		);

		const ticks = container.querySelectorAll(
			'.recharts-xAxis .recharts-cartesian-axis-tick text'
		);

		expect(ticks[0]).toHaveTextContent('Jun 10');
		expect(ticks[1]).toHaveTextContent('Jun 11');
	});
});
