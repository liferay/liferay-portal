import * as API from 'shared/api';
import DeleteChannelModal from '../DeleteChannelModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {noop} from 'lodash';
import {Provider} from 'react-redux';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const DefaultWrapper = ({children}) => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={['/']}>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider
							cache={
								new InMemoryCache({
									addTypename: false,
									freezeResults: false
								})
							}
						>
							{children}
						</MockedProvider>
					}
					path='/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('DeleteChannelModal', () => {
	afterEach(cleanup);

	it('renders without data source alert message', async () => {
		API.dataSource.fetchChannels.mockReturnValueOnce(
			Promise.resolve({items: [], total: 0})
		);

		const {container} = render(
			<DefaultWrapper>
				<DeleteChannelModal onClose={noop} onSubmit={noop} />
			</DefaultWrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render with data source alert message', async () => {
		API.dataSource.fetchChannels.mockReturnValueOnce(
			Promise.resolve({items: [{id: '1', name: 'Test Source'}], total: 1})
		);

		const {container, getByText} = render(
			<DefaultWrapper>
				<DeleteChannelModal onClose={noop} onSubmit={noop} />
			</DefaultWrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(
			getByText(/To reconnect to Analytics Cloud with Test Source/)
		).toBeTruthy();
	});
});
