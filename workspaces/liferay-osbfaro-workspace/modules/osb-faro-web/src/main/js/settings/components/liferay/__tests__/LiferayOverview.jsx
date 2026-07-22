import * as data from 'test/data';
import LiferayOverview from '../LiferayOverview';
import mockStore from 'test/mock-store';
import React from 'react';
import {DataSource} from 'shared/util/records';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: () => ({
		isAdmin: () => true
	})
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: () => ({
		data: {
			channelsCount: 5,
			groupsCount: 10,
			individualsCount: 100
		},
		loading: false
	})
}));

const defaultProps = {
	dataSource: new DataSource(data.mockLiferayDataSource())
};

const WrappedComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={['/workspace/23/settings/data-source/test']}
		>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider>
							<LiferayOverview {...defaultProps} {...props} />
						</MockedProvider>
					}
					path='/workspace/:groupId/settings/data-source/:id/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('LiferayOverview', () => {
	it('should render', async () => {
		const {container} = render(<WrappedComponent />);

		expect(container).toMatchSnapshot();
	});
});
