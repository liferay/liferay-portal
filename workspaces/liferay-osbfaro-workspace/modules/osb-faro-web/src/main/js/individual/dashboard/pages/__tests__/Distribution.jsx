import * as data from 'test/data';
import * as useDataSources from 'shared/context/dataSources';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {IndividualsDistribution} from '../Distribution';
import {mockEmptyState, mockSuccessState} from 'test/__mocks__/mock-objects';
import {Provider} from 'react-redux';
import {MemoryRouter} from 'react-router-dom';
import {User} from 'shared/util/records';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '123'
	})
}));

const mockUseDataSource = useDataSources;

const defaultProps = {
	currentUser: new User(data.mockUser()),
	groupId: '123'
};

const WrappedComponent = () => (
	<Provider store={mockStore()}>
		<MemoryRouter>
			<IndividualsDistribution {...defaultProps} />
		</MemoryRouter>
	</Provider>
);

describe('Individuals Dashboard Distribution', () => {
	afterEach(cleanup);

	it('renders', () => {
		mockUseDataSource.useDataSources = jest.fn(() => mockSuccessState);

		const {container} = render(<WrappedComponent />);

		expect(
			container.querySelector('.individuals-dashboard-distribution-root')
		).toBeInTheDocument();
	});

	it('renders with data source empty state', () => {
		mockUseDataSource.useDataSources = jest.fn(() => mockEmptyState);

		const {getByText} = render(<WrappedComponent />);

		expect(getByText('No Data Sources Connected')).toBeInTheDocument();
	});
});
