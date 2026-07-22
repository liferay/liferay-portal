import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import {addAlert} from 'shared/actions/alerts';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {Project, User} from 'shared/util/records';
import {Provider} from 'react-redux';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {Workspace} from '../Workspace';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useBlocker: () => ({state: 'unblocked'}),
}));

jest.mock('shared/actions/alerts', () => ({
	actionTypes: {},
	addAlert: jest.fn(() => ({meta: {}, payload: {}, type: 'addAlert'}))
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn()
}));

const defaultProps = {
	addAlert,
	emailAddressDomains: ['liferay.com'],
	groupId: '23',
	project: new Project(data.mockProject())
};

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={['/workspace/23/settings/workspace']}>
			<RouterRoutes>
				<Route
					element={
						<Workspace {...defaultProps} {...props} />
					}
					path='/workspace/:groupId/settings/workspace/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('Workspace Settting', () => {
	afterEach(cleanup);

	it('should render', () => {
		useCurrentUser.mockImplementation(() => ({
			isAdmin: () => true
		}));

		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should render as disabled if the user is not an AC admin', () => {
		useCurrentUser.mockImplementation(() => ({
			isAdmin: () => false
		}));

		const {container, getByDisplayValue, getByLabelText} = render(
			<DefaultComponent
				currentUser={data.getImmutableMock(User, data.mockMemberUser)}
			/>
		);

		expect(getByLabelText('Workspace Name')).toBeDisabled();
		expect(getByDisplayValue('Oregon, USA')).toBeDisabled();
		expect(
			container.querySelector('.input-list-root input')
		).toBeDisabled();
	});
});
