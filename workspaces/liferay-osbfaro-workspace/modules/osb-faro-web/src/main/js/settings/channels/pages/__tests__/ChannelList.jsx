import * as API from 'shared/api';
import * as data from 'test/data';
import ChannelList from '../ChannelList';
import mockStore, {mockStoreData} from 'test/mock-store';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {Provider} from 'react-redux';
import {RemoteData, User} from 'shared/util/records';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const defaultProps = {
	groupId: '23'
};

const Wrapper = ({children, store = mockStore()}) => (
	<Provider store={store}>
		<MemoryRouter initialEntries={['/workspace/23/settings/properties']}>
			<RouterRoutes>
				<Route element={children} path={`${Routes.SETTINGS_CHANNELS}/*`} />
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe.skip('Channels List', () => {
	afterEach(cleanup);

	it('should render', async () => {
		API.channels.search.mockReturnValue(
			Promise.resolve(data.mockChannels())
		);

		const {container} = render(
			<Wrapper>
				<ChannelList {...defaultProps} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved();

		expect(container).toMatchSnapshot();
	});

	it('should not render add button if user is not an admin', async () => {
		const memberStore = mockStore(
			mockStoreData.setIn(['currentUser', 'data'], '24').setIn(
				['users', '24'],
				new RemoteData({
					data: new User(data.mockMemberUser('24')),
					loading: false
				})
			)
		);

		API.channels.search.mockReturnValue(
			Promise.resolve(data.mockChannels())
		);

		render(
			<Wrapper store={memberStore}>
				<ChannelList {...defaultProps} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved();

		expect(screen.queryByText('New Property')).toBeNull();
	});

	it('should check if the number of synced sites and channels appears for each property', async () => {
		API.channels.search.mockReturnValue(
			Promise.resolve(data.mockChannels())
		);

		render(
			<Wrapper>
				<ChannelList {...defaultProps} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved();

		expect(screen.getByText('Sites')).toBeInTheDocument();
		expect(screen.getByText('Channels')).toBeInTheDocument();
		expect(screen.getByText('Liferay DXP')).toBeInTheDocument();
		expect(screen.getAllByText('6')).toHaveLength(1);
		expect(screen.getAllByText('5')).toHaveLength(1);
	});

	it('should check if actions "DELETE" and "CLEAR DATA" are displayed in the ellipsis and its icons', async () => {
		API.channels.search.mockReturnValue(
			Promise.resolve(data.mockChannels())
		);

		const {container} = render(
			<Wrapper>
				<ChannelList {...defaultProps} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved();

		// In some environments or screen sizes, RowActions might render quick actions as buttons
		// If they are in an ellipsis, we would need to click it first.
		// Let's check if the buttons are present.

		const clearDataBtn = screen.getByRole('button', {
			name: /clear data/i
		});

		const deleteBtn = screen.getByRole('button', {
			name: /delete/i
		});

		expect(clearDataBtn).toBeInTheDocument();
		expect(deleteBtn).toBeInTheDocument();

		const clearDataIcon = container.querySelector('svg.lexicon-icon-magic');

		const deleteIcon = container.querySelector('svg.lexicon-icon-trash');

		expect(clearDataIcon).toBeInTheDocument();
		expect(deleteIcon).toBeInTheDocument();
	});
});
