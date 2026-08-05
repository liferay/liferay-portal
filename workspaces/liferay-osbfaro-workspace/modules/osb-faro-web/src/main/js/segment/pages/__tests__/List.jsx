import * as API from 'shared/api';
import * as data from 'test/data';
import List from '../List';
import mockStore from 'test/mock-store';
import React from 'react';
import {act} from '@testing-library/react';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, render, screen, within} from '@testing-library/react';
import {MemoryRouter, Route} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';
import {SegmentCategories, SegmentTypes} from 'shared/util/constants';
import {UnassignedSegmentsContext} from 'shared/context/unassignedSegments';
import {User} from 'shared/util/records';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('shared/util/feature-flags', () => ({
	...jest.requireActual('shared/util/feature-flags'),
	ENABLE_REAL_TIME_SEGMENTS: false
}));

const featureFlags = jest.requireMock('shared/util/feature-flags');

const MOCK_UNASSIGNED_SEGMENTS_CONTEXT = {
	showUnassignedAlert: false,
	unassignedSegments: [],
	unassignedSegmentsDispatch: jest.fn()
};

const store = mockStore();

const DefaultComponent = ({queryString = '', ...otherProps}) => (
	<Provider store={store}>
		<MemoryRouter
			initialEntries={[
				`/workspace/23/123/contacts/segments${queryString}`
			]}
		>
			<Route path={Routes.CONTACTS_LIST_SEGMENT}>
				<UnassignedSegmentsContext.Provider
					value={MOCK_UNASSIGNED_SEGMENTS_CONTEXT}
				>
					<ChannelContext.Provider value={mockChannelContext()}>
						<List
							channelId='123'
							currentUser={data.getImmutableMock(
								User,
								data.mockUser
							)}
							groupId='23'
							{...otherProps}
						/>
					</ChannelContext.Provider>
				</UnassignedSegmentsContext.Provider>
			</Route>
		</MemoryRouter>
	</Provider>
);

describe('List', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		jest.useFakeTimers();

		featureFlags.ENABLE_REAL_TIME_SEGMENTS = true;
	});

	afterEach(() => {
		cleanup();

		jest.useRealTimers();
	});

	it('should enable every segment option regardless of the segment count', async () => {
		API.individualSegment.search.mockReturnValue(
			Promise.resolve(data.mockSearch(data.mockSegment, 20))
		);

		render(<DefaultComponent />);

		await act(async () => {
			jest.runAllTimers();
		});

		const batchOption = screen.getByTestId('batch-segment-dropdown-item');
		expect(batchOption.closest('a')).not.toHaveClass('disabled');

		const realTimeOption = screen.getByTestId(
			'real-time-segment-dropdown-item'
		);
		expect(realTimeOption.closest('a')).not.toHaveClass('disabled');

		expect(
			screen.getByText('New Segment').closest('button')
		).not.toBeDisabled();

		expect(API.projects.fetchFeatureUsages).not.toHaveBeenCalled();
	});

	it('should render', async () => {
		render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(document.body);

		expect(screen.getByText('Segments')).toBeInTheDocument();
	});

	it('should show the sequential info icon for real time sequential segments', async () => {
		API.individualSegment.search.mockReturnValue(
			Promise.resolve(
				data.mockSearch(data.mockSegment, 1, {
					segmentType: SegmentTypes.RealTime,
					sequential: true
				})
			)
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(document.body);

		expect(container.querySelector('.sticker-info')).toBeInTheDocument();
	});

	it('should not show the sequential info icon for real time non-sequential segments', async () => {
		API.individualSegment.search.mockReturnValue(
			Promise.resolve(
				data.mockSearch(data.mockSegment, 1, {
					segmentType: SegmentTypes.RealTime,
					sequential: false
				})
			)
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(document.body);

		expect(
			container.querySelector('.sticker-info')
		).not.toBeInTheDocument();
	});

	it('should not show the sequential info icon for batch segments', async () => {
		API.individualSegment.search.mockReturnValue(
			Promise.resolve(
				data.mockSearch(data.mockSegment, 1, {
					segmentType: SegmentTypes.Batch,
					sequential: true
				})
			)
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(document.body);

		expect(
			container.querySelector('.sticker-info')
		).not.toBeInTheDocument();
	});

	describe('when real time segments are disabled', () => {
		beforeEach(() => {
			featureFlags.ENABLE_REAL_TIME_SEGMENTS = false;
		});

		it('hides the real time segment option', async () => {
			render(<DefaultComponent />);

			await act(async () => {
				jest.runAllTimers();
			});

			expect(
				screen.getByTestId('account-batch-segment-dropdown-item')
			).toBeInTheDocument();
			expect(
				screen.getByTestId('batch-segment-dropdown-item')
			).toBeInTheDocument();

			expect(
				screen.queryByTestId('real-time-segment-dropdown-item')
			).not.toBeInTheDocument();
		});
	});

	it('shows the segment type dropdown when real time segments are enabled', async () => {
		render(<DefaultComponent />);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(
			screen.getByTestId('account-batch-segment-dropdown-item')
		).toBeInTheDocument();
		expect(
			screen.getByTestId('batch-segment-dropdown-item')
		).toBeInTheDocument();
		expect(
			screen.getByTestId('real-time-segment-dropdown-item')
		).toBeInTheDocument();

		const accountOption = screen.getByTestId(
			'account-batch-segment-dropdown-item'
		);

		const [accountGroup, individualGroup] = accountOption
			.closest('.dropdown-menu')
			.querySelectorAll('.dropdown-subheader');

		expect(accountGroup).toHaveTextContent('Account');
		expect(individualGroup).toHaveTextContent('Individual');
	});

	it('shows the account count for account segments', async () => {
		API.projects.fetchFeatureUsages.mockResolvedValueOnce([]);
		API.individualSegment.search.mockReturnValue(
			Promise.resolve(
				data.mockSearch(data.mockSegment, 1, {
					accountsCount: 1800,
					individualCount: 2300,
					segmentCategory: SegmentCategories.Account
				})
			)
		);

		render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(document.body);

		const row = screen.getByText('Seattle0').closest('tr');

		expect(within(row).getByText('Account')).toBeInTheDocument();
		expect(within(row).getByText('1.8K accounts')).toBeInTheDocument();
	});

	it('shows the individual count for individual segments', async () => {
		API.projects.fetchFeatureUsages.mockResolvedValueOnce([]);
		API.individualSegment.search.mockReturnValue(
			Promise.resolve(
				data.mockSearch(data.mockSegment, 1, {
					accountsCount: 1800,
					individualCount: 2300,
					segmentCategory: SegmentCategories.Individual
				})
			)
		);

		render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(document.body);

		const row = screen.getByText('Seattle0').closest('tr');

		expect(within(row).getByText('Individual')).toBeInTheDocument();
		expect(within(row).getByText('2.3K individuals')).toBeInTheDocument();
	});
});
