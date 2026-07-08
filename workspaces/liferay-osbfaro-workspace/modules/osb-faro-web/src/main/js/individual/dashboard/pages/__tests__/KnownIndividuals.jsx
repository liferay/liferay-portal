import * as API from 'shared/api';
import * as useDataSources from 'shared/context/dataSources';
import KnownIndividuals from '../KnownIndividuals';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter, Route} from 'react-router-dom';
import {mockEmptyState, mockSuccessState} from 'test/__mocks__/mock-objects';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const mockUseDataSource = useDataSources;

const WrappedComponent = () => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={[
				'/workspace/23/321321/contacts/individuals/known-individuals'
			]}
		>
			<Route path={Routes.CONTACTS_INDIVIDUALS_KNOWN_INDIVIDUALS}>
				<KnownIndividuals />
			</Route>
		</MemoryRouter>
	</Provider>
);

describe('Individuals Dashboard KnownIndividuals List', () => {
	afterEach(cleanup);

	it('renders', async () => {
		mockUseDataSource.useDataSources = jest.fn(() => mockSuccessState);

		const {container} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(container);

		jest.runAllTimers();

		expect(
			container.querySelector(
				'.individuals-dashboard-known-individuals-root'
			)
		).toBeInTheDocument();
	});

	it('renders with data source empty state', () => {
		mockUseDataSource.useDataSources = jest.fn(() => mockEmptyState);

		const {getByText} = render(<WrappedComponent />);

		expect(getByText('No Data Sources Connected')).toBeInTheDocument();
	});

	it('searches with the last year range key', async () => {
		mockUseDataSource.useDataSources = jest.fn(() => mockSuccessState);

		const {container} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(container);

		jest.runAllTimers();

		expect(API.individuals.search).toHaveBeenCalledWith(
			expect.objectContaining({
				rangeKey: RangeKeyTimeRanges.LastYear,
			})
		);
	});
});
