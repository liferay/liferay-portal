import * as data from 'test/data';
import Membership, {MembershipChart} from '../Membership';
import mockStore from 'test/mock-store';
import React from 'react';
import {Provider} from 'react-redux';
import {render, screen} from '@testing-library/react';
import {Segment} from 'shared/util/records';
import {SegmentTypes} from 'shared/util/constants';
import {StaticRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const defaultProps = {
	channelId: '123',
	groupId: '23',
	growthHistory: {data: []},
	id: '321',
	segment: data.getImmutableMock(Segment, data.mockSegment),
	segmentType: SegmentTypes.Batch,
	timeZoneId: 'UTC'
};

describe('Membership', () => {
	const WrappedComponent = props => (
		<Provider store={mockStore()}>
			<StaticRouter>
				<Membership {...defaultProps} {...props} />
			</StaticRouter>
		</Provider>
	);

	it('should render', async () => {
		const {container} = render(<WrappedComponent />);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(
			screen.getByText('Segment Membership Trend')
		).toBeInTheDocument();
	});
});

describe('MembershipChart', () => {
	const WrappedComponent = props => (
		<StaticRouter>
			<MembershipChart {...defaultProps} {...props} />
		</StaticRouter>
	);

	it('should render', async () => {
		const {container} = render(<WrappedComponent />);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText(/^members$/i)).toBeInTheDocument();
	});
});
