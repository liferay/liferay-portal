import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import {ClearData} from '../ClearData';
import {DataSource} from 'shared/util/records';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const defaultProps = {
	addAlert: jest.fn(),
	dataSource: new DataSource(data.mockLiferayDataSource()),
	entitiesCount: 10,
	groupId: '23',
	history: {push: jest.fn()},
	id: '26'
};

const WrappedComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={[
				'/workspace/23/settings/data-source/26/clear-data'
			]}
		>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider>
							<ClearData {...defaultProps} {...props} />
						</MockedProvider>
					}
					path='/workspace/:groupId/settings/data-source/:id/clear-data/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('ClearData', () => {
	it('should render', async () => {
		const {container} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
