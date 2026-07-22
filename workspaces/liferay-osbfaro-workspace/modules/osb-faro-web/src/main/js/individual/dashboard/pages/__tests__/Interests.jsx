import * as useDataSources from 'shared/context/dataSources';
import Interests from '../Interests';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockEmptyState, mockSuccessState} from 'test/__mocks__/mock-objects';
import {mockIndividualInterestsReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const mockUseDataSource = useDataSources;

const WrappedComponent = () => (
	<MockedProvider
		mocks={[
			mockIndividualInterestsReq(defaultVars => ({
				...defaultVars,
				id: undefined,
				keywords: '',
				size: 2
			}))
		]}
	>
		<Provider store={mockStore()}>
			<MemoryRouter
				initialEntries={[
					'/workspace/123/456/contacts/individuals/interests'
				]}
			>
				<RouterRoutes>
					<Route
						element={
							<Interests />
						}
						path={`${Routes.CONTACTS_INDIVIDUALS_INTERESTS}/*`}
					/>
				</RouterRoutes>
			</MemoryRouter>
		</Provider>
	</MockedProvider>
);

describe('Individuals Dashboard Individuals Interests', () => {
	afterEach(cleanup);

	it('renders', async () => {
		mockUseDataSource.useDataSources = jest.fn(() => mockSuccessState);

		const {getByText} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(document.body);

		jest.runAllTimers();

		expect(getByText('Interest Topics')).toBeInTheDocument();
	});

	it('renders with data source empty state', () => {
		mockUseDataSource.useDataSources = jest.fn(() => mockEmptyState);

		const {getByText} = render(<WrappedComponent />);

		expect(
			getByText('No Sites Synced from Data Sources')
		).toBeInTheDocument();
	});
});
