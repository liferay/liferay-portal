import * as data from 'test/data';
import React from 'react';
import SegmentProfileCard from '../ProfileCard';
import {render} from '@testing-library/react';
import {Segment} from 'shared/util/records';
import {MemoryRouter} from 'react-router-dom';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

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

describe('SegmentProfileCard', () => {
	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
