import * as data from 'test/data';
import React from 'react';
import SegmentProfileCard from '../ProfileCard';
import {render} from '@testing-library/react';
import {Segment} from 'shared/util/records';
import {MemoryRouter} from 'react-router-dom';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('recharts', () => {
	const OriginalModule = jest.requireActual('recharts');

	return {
		...OriginalModule,
		ResponsiveContainer: ({children}) => (
			<OriginalModule.ResponsiveContainer height={350} width={800}>
				{children}
			</OriginalModule.ResponsiveContainer>
		)
	};
});

const DefaultComponent = props => (
	<MemoryRouter>
		<SegmentProfileCard
			channelId='123'
			groupId='23'
			id='3'
			segment={data.getImmutableMock(Segment, data.mockSegment, '3')}
			{...props}
		/>
	</MemoryRouter>
);

describe('SegmentProfileCard membership count', () => {
	it('shows the member count as processing while membership is calculated', async () => {
		const {container} = render(
			<DefaultComponent
				segment={data.getImmutableMock(Segment, data.mockSegment, '3', {
					knownIndividualCount: 5,
					lastMembershipUpdateDate: null
				})}
			/>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		const counts = container.querySelectorAll('.legend-text-color b');

		expect(counts).toHaveLength(2);

		counts.forEach(count => expect(count).toHaveTextContent('Processing'));
	});

	it('shows a zero member count once membership is calculated', async () => {
		const {container} = render(
			<DefaultComponent
				segment={data.getImmutableMock(Segment, data.mockSegment, '3', {
					knownIndividualCount: 0,
					lastMembershipUpdateDate: data.getTimestamp()
				})}
			/>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		const counts = container.querySelectorAll('.legend-text-color b');

		expect(counts).toHaveLength(2);

		counts.forEach(count => expect(count).toHaveTextContent('0'));
	});

	it('shows the member count once membership is calculated', async () => {
		const {container} = render(
			<DefaultComponent
				segment={data.getImmutableMock(Segment, data.mockSegment, '3', {
					knownIndividualCount: 1500,
					lastMembershipUpdateDate: data.getTimestamp()
				})}
			/>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		const counts = container.querySelectorAll('.legend-text-color b');

		expect(counts[0]).toHaveTextContent('1,500');
		expect(counts[1]).toHaveTextContent('1,500');
	});
});
