import * as data from 'test/data';
import DataSourcesProvider from 'shared/context/dataSources';
import IndividualProfileRoutes from '../ProfileRoutes';
import mockStore from 'test/mock-store';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, render} from '@testing-library/react';
import {Individual} from 'shared/util/records';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

const ENTITY_URL =
	'/workspace/23/123/contacts/individuals/known-individuals/test/details';

const ENTITY_ROUTE = `${Routes.CONTACTS_INDIVIDUAL}/*`;

const defaultProps = {
	channelId: '123',
	groupId: '23',
	id: 'test',
	individual: data.getImmutableMock(Individual, data.mockIndividual)
};

jest.unmock('react-dom');

const renderProfileRoutes = (props = defaultProps) =>
	render(
		<Provider store={mockStore()}>
			<ChannelContext.Provider value={mockChannelContext()}>
				<DataSourcesProvider groupId={defaultProps.groupId}>
					<MemoryRouter initialEntries={[ENTITY_URL]}>
						<RouterRoutes>
							<Route
								element={<IndividualProfileRoutes {...props} />}
								path={ENTITY_ROUTE}
							/>
						</RouterRoutes>
					</MemoryRouter>
				</DataSourcesProvider>
			</ChannelContext.Provider>
		</Provider>
	);

describe('IndividualProfileRoutes', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {container} = renderProfileRoutes();

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('renders the individual name at the top of the page', () => {
		const individual = data.getImmutableMock(
			Individual,
			data.mockIndividual
		);

		const {container} = renderProfileRoutes({...defaultProps, individual});

		expect(container.querySelector('h1.title')).toHaveTextContent(
			individual.name
		);
	});
});
