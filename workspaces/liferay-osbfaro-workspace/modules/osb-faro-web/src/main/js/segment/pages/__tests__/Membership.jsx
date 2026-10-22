import * as data from 'test/data';
import * as Growth from 'segment/components/Growth';
import Membership, {MembershipChart} from '../Membership';
import mockStore from 'test/mock-store';
import React from 'react';
import {Provider} from 'react-redux';
import {render, screen} from '@testing-library/react';
import {Segment} from 'shared/util/records';
import {SegmentTypes} from 'shared/util/constants';
import {MemoryRouter} from 'react-router';
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
			<MemoryRouter>
				<Membership {...defaultProps} {...props} />
			</MemoryRouter>
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

	it.each([
		[true, null],
		[false, 1700000000000]
	])(
		'should pass membershipProcessing=%p to the growth chart when the last membership update date is %p',
		async (membershipProcessing, lastMembershipUpdateDate) => {
			const growthSpy = jest.spyOn(Growth, 'default');

			const {container} = render(
				<WrappedComponent
					segment={data.getImmutableMock(
						Segment,
						data.mockSegment,
						0,
						{lastMembershipUpdateDate}
					)}
				/>
			);

			jest.runAllTimers();

			await waitForLoadingToBeRemoved(container);

			expect(growthSpy).toHaveBeenLastCalledWith(
				expect.objectContaining({membershipProcessing}),
				expect.anything()
			);

			growthSpy.mockRestore();
		}
	);
});

describe('MembershipChart', () => {
	const WrappedComponent = props => (
		<MemoryRouter>
			<MembershipChart {...defaultProps} {...props} />
		</MemoryRouter>
	);

	it('should render', async () => {
		const {container} = render(<WrappedComponent />);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText(/^members$/i)).toBeInTheDocument();
	});
});
