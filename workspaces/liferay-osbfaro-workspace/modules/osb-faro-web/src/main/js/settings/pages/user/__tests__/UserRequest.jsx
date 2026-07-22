import mockStore from 'test/mock-store';
import React from 'react';
import UserRequest from '../UserRequest';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {noop} from 'lodash';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={['/workspace/23/settings/users/requests']}
		>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider freezeResults={false}>
							<UserRequest {...props} onSetUserRequest={noop} />
						</MockedProvider>
					}
					path={`${Routes.SETTINGS_USERS_REQUESTS}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('UserRequest', () => {
	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
