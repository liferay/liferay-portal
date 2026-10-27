import * as API from 'shared/api';
import * as data from 'test/data';
import mockStore, {mockStoreDataLDP} from 'test/mock-store';
import React from 'react';
import WorkspaceLayout from '../WorkspaceLayout';
import {ChannelProvider} from 'shared/context/channel';
import {fireEvent, render, screen} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes} from 'react-router-dom';
import {mockDataSourcesReq} from 'test/graphql-data';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

jest.mock('route-middleware/BundleRouter', () => () => {
	const {Link, useLocation} = jest.requireActual('react-router-dom');

	const {pathname} = useLocation();

	return (
		<div data-testid="page">
			{pathname}

			<Link to="/workspace/23/1/event-analysis/create">
				{'Create Analysis'}
			</Link>

			<Link to="/workspace/23/1/event-analysis">{'Back to List'}</Link>
		</div>
	);
});

const CHANNELS = [
	data.mockChannel(1, 0, {id: '1'}),
	data.mockChannel(2, 0, {id: '2'}),
];

const renderWorkspaceLayout = (path: string) =>
	render(
		<Provider store={mockStore(mockStoreDataLDP)}>
			<ChannelProvider>
				<MockedProvider
					cache={new InMemoryCache({addTypename: false})}
					mocks={[mockDataSourcesReq()]}
				>
					<MemoryRouter initialEntries={[path]}>
						<Routes>
							<Route
								element={<WorkspaceLayout />}
								path="workspace/:groupId/*"
							/>
						</Routes>
					</MemoryRouter>
				</MockedProvider>
			</ChannelProvider>
		</Provider>
	);

describe('WorkspaceLayout', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(API.channels.fetchAll as jest.Mock).mockReturnValue(
			Promise.resolve({items: CHANNELS})
		);
	});

	it('renders the toolbar above the workspace pages', async () => {
		renderWorkspaceLayout('/workspace/23/1/sites');

		expect(await screen.findByTestId('page')).toBeTruthy();

		expect(screen.getByTitle(/language/i)).toBeTruthy();
	});

	it('does not render the toolbar on the settings pages', async () => {
		renderWorkspaceLayout('/workspace/23/settings/data-source');

		expect(await screen.findByTestId('page')).toBeTruthy();

		expect(screen.queryByTitle(/language/i)).toBeNull();
	});

	it('does not render the toolbar on the edit pages', async () => {
		renderWorkspaceLayout('/workspace/23/1/event-analysis/create');

		expect(await screen.findByTestId('page')).toBeTruthy();

		expect(screen.queryByTitle(/language/i)).toBeNull();
	});

	it('keeps the workspace loaded when opening an editor from a list', async () => {
		renderWorkspaceLayout('/workspace/23/1/event-analysis');

		fireEvent.click(await screen.findByText('Create Analysis'));

		expect(
			screen.getByText('/workspace/23/1/event-analysis/create')
		).toBeInTheDocument();
		expect(screen.queryByTitle(/language/i)).toBeNull();

		expect(API.channels.fetchAll).toHaveBeenCalledTimes(1);
		expect(API.projects.fetch).toHaveBeenCalledTimes(1);
	});

	it('keeps the workspace modals loaded when going back to a list', async () => {
		renderWorkspaceLayout('/workspace/23/1/event-analysis');

		fireEvent.click(await screen.findByText('Create Analysis'));
		fireEvent.click(screen.getByText('Back to List'));

		expect(
			screen.getByText('/workspace/23/1/event-analysis')
		).toBeInTheDocument();
		expect(screen.getByTitle(/language/i)).toBeTruthy();

		expect(API.individualSegment.searchUnassigned).toHaveBeenCalledTimes(1);
	});

	it('renders the error page for a page without a channel', async () => {
		renderWorkspaceLayout('/workspace/23/event-analysis/create');

		expect(
			await screen.findByText(
				'The page you are looking for does not exist.'
			)
		).toBeInTheDocument();
		expect(screen.queryByTestId('page')).toBeNull();
	});

	it('keeps the channel of a missing page selected', async () => {
		renderWorkspaceLayout('/workspace/23/2/unknown-page');

		expect(
			await screen.findByText(
				'The page you are looking for does not exist.'
			)
		).toBeInTheDocument();
		expect(
			screen.getByRole('combobox', {name: 'Property'})
		).toHaveTextContent('Channel 2');
	});

	it('renders the error page for an unknown channel', async () => {
		renderWorkspaceLayout('/workspace/23/999/event-analysis');

		expect(
			await screen.findByText(
				'The page you are looking for does not exist.'
			)
		).toBeInTheDocument();
	});
});
