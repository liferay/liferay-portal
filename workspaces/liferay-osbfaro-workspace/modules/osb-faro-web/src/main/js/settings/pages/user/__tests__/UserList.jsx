import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import UserList from '../UserList';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {Routes} from 'shared/util/router';
import {User} from 'shared/util/records';
import {UserRoleNames} from 'shared/util/constants';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const defaultProps = {
	currentUser: new User(data.mockUser()),
	groupId: '23'
};

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={['/workspace/23/settings/users']}>
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
							<UserList {...defaultProps} {...props} />
						</MockedProvider>
					}
					path={`${Routes.SETTINGS_USERS}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('UserList', () => {
	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it("should render rows as disabled without row actions, invite members button, or checkboxes if the current user's role is member", async () => {
		const {container, queryByTestId, queryByText} = render(
			<DefaultComponent
				currentUser={
					new User(data.mockUser(0, {roleName: UserRoleNames.Member}))
				}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(
			container.querySelector(
				'.table > tbody:nth-of-type(1) > tr.disabled'
			)
		).not.toBeNull();

		expect(queryByTestId('select-all-checkbox')).not.toBeInTheDocument();

		expect(queryByText('Invite Users')).not.toBeInTheDocument();
	});
});
