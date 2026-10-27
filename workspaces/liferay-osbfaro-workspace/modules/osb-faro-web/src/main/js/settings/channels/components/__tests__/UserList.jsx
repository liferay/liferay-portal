import * as API from 'shared/api';
import {isModalOpen} from 'test/mock-searchable-table-modal';
import * as pedantic from 'test/pedantic';
import mockStore from 'test/mock-store';
import React from 'react';
import UserList from '../UserList';
import {ClayIconSpriteContext} from '@clayui/icon';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {Provider} from 'react-redux';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('shared/actions/alerts', () => ({
	actionTypes: {},
	addAlert: jest.fn(() => ({meta: {}, payload: {}, type: 'addAlert'}))
}));

// Mock NameCell to avoid potential complex dependencies or meta property issues
jest.mock('shared/components/table/cell-components/index', () => {
	const Original = jest.requireActual(
		'shared/components/table/cell-components/index'
	);
	return {
		...Original,
		NameCell: ({data}) => <td data-testid='name-cell'>{data.name}</td>
	};
});

const defaultProps = {
	authorized: true,
	groupId: '23',
	id: 'channel-1',
	propertyName: 'Test Property'
};

const mockUsers = {
	items: [
		{
			emailAddress: 'test1@liferay.com',
			id: 'user-1',
			name: 'User 1',
			userId: 'user-1'
		},
		{
			emailAddress: 'test2@liferay.com',
			id: 'user-2',
			name: 'User 2',
			userId: 'user-2'
		}
	],
	total: 2
};

const Wrapper = ({children, store = mockStore()}) => (
	<Provider store={store}>
		<MemoryRouter>
			<ClayIconSpriteContext.Provider value='/o/osb-faro-web/dist/sprite.svg'>
				{children}
			</ClayIconSpriteContext.Provider>
		</MemoryRouter>
	</Provider>
);

describe('ChannelUserList', () => {
	beforeEach(() => {
		pedantic.disable();
		API.channels.fetchUsers.mockReturnValue(Promise.resolve(mockUsers));
	});

	afterEach(() => {
		pedantic.enable();
		cleanup();
	});

	it('should render', async () => {
		const {container} = render(
			<Wrapper>
				<UserList {...defaultProps} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText('User 1')).toBeInTheDocument();
		expect(screen.getByText('test1@liferay.com')).toBeInTheDocument();
		expect(screen.getByText('User 2')).toBeInTheDocument();
		expect(screen.getByText('test2@liferay.com')).toBeInTheDocument();

		expect(screen.getByText('Add User')).toBeInTheDocument();
	});

	it('should render without checkboxes if user is not an AC admin', async () => {
		const {container} = render(
			<Wrapper>
				<UserList {...defaultProps} authorized={false} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('input[type=checkbox]')).toBeNull();
		expect(screen.queryByText('Add User')).toBeNull();
	});

	it('should open a modal to add users', async () => {
		render(
			<Wrapper>
				<UserList {...defaultProps} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved();

		expect(isModalOpen()).toBe(false);

		fireEvent.click(screen.getByText('Add User'));

		expect(isModalOpen()).toBe(true);
	});

	it('should open a modal to remove users', async () => {
		render(
			<Wrapper>
				<UserList {...defaultProps} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved();

		const deleteButtons = screen.getAllByTestId('delete-user');
		fireEvent.click(deleteButtons[0]);

		expect(await screen.findByText('Remove User')).toBeInTheDocument();
	});
});
