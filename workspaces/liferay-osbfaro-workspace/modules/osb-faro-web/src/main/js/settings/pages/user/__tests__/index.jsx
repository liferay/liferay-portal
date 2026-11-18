import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import {BrowserRouter} from 'react-router-dom';
import {cleanup, render} from '@testing-library/react';
import {Provider} from 'react-redux';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {User} from 'shared/util/records';
import {User as UserRoutes} from '../index';

jest.unmock('react-dom');

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn()
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<BrowserRouter>
			<UserRoutes {...props} />
		</BrowserRouter>
	</Provider>
);

describe('UserRoutes', () => {
	afterEach(cleanup);

	it('should render', () => {
		useCurrentUser.mockImplementation(() => ({
			isAdmin: () => true
		}));

		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});

	it('if the user is AC Admin, then the tabs for toggling between users and user requests should render', () => {
		useCurrentUser.mockImplementation(() => ({
			isAdmin: () => true
		}));

		const {queryAllByText, queryByText} = render(<DefaultComponent />);

		expect(queryAllByText(/^manage users$/i)).toBeTruthy();
		expect(queryByText(/^requests$/i)).toBeTruthy();
	});

	it('if the user is NOT an AC Admin, then the tabs for toggling between users and user requests should NOT render', () => {
		useCurrentUser.mockImplementation(() => ({
			isAdmin: () => false
		}));

		const {queryByText} = render(
			<DefaultComponent
				currentUser={data.getImmutableMock(User, data.mockMemberUser)}
			/>
		);

		expect(queryByText('Manager Users')).toBeNull();
		expect(queryByText(/^requests$/i)).toBeNull();
	});
});
