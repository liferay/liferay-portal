import * as API from 'shared/api';
import * as data from 'test/data';
import mockStore from 'test/mock-store';
import ModalRenderer from 'shared/components/ModalRenderer';
import React from 'react';
import View from '../View';
import {
	cleanup,
	fireEvent,
	getByText,
	queryByText,
	render,
	screen
} from '@testing-library/react';
import {Provider} from 'react-redux';
import {StaticRouter} from 'react-router';
import {User} from 'shared/util/records';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const defaultProps = {
	currentUser: new User(data.mockUser()),
	groupId: '23'
};

const DefaultComponent = props => {
	API.preferences.fetchEmailReport = jest.fn(() => Promise.resolve());

	return (
		<Provider store={mockStore()}>
			<ModalRenderer />

			<StaticRouter>
				<View {...defaultProps} {...props} />
			</StaticRouter>
		</Provider>
	);
};

describe('View Channel', () => {
	afterEach(() => {
		cleanup();

		jest.clearAllMocks();
	});

	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render w/ Select Users view', async () => {
		API.channels.fetch.mockReturnValueOnce(
			Promise.resolve(data.mockChannel(1, 1))
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByLabelText('Select Users').checked).toBeTrue();
		expect(container.querySelector('.table-root')).toBeTruthy();
	});

	it('should not edit or delete property when the user is not an admin', () => {
		API.user.fetchCurrentUser.mockReturnValueOnce(
			Promise.resolve(data.mockMemberUser())
		);

		const {queryByLabelText, queryByText} = render(<DefaultComponent />);

		expect(queryByText('Delete')).toBeNull();
		expect(queryByLabelText('Edit')).toBeNull();
	});

	it('should check if DELETE and CLEAR DATA buttons are displayed', async () => {
		API.user.fetchCurrentUser.mockReturnValueOnce(
			Promise.resolve(data.mockUser())
		);

		const {container, queryByText} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(queryByText('Delete')).toBeInTheDocument();
		expect(queryByText('Clear Data')).toBeInTheDocument();
	});

	it('should check error modal message and hyperlink on deleting property that has CHANNELS synced', async () => {
		API.user.fetchCurrentUser.mockReturnValueOnce(
			Promise.resolve(data.mockUser())
		);

		API.channels.fetch.mockReturnValueOnce(
			Promise.resolve(
				data.mockChannel(1, 1, {
					commerceChannelsCount: 5,
					groupsCount: 0
				})
			)
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(
			screen.getByText(
				'There are 0 sites and 5 channels synced to this property.'
			)
		).toBeInTheDocument();

		const deleteBtn = screen.getByTestId('delete');

		fireEvent.click(deleteBtn);

		expect(
			container.querySelector('div.modal-container')
		).toBeInTheDocument();

		expect(container.querySelector('div.modal-container')).toHaveClass(
			'show'
		);

		expect(
			screen.getByText('Unable to Delete Property')
		).toBeInTheDocument();

		const modalText = screen.getByText((content, node) => {
			const hasText = node =>
				node.textContent ===
				'Ensure no sites and channels are assigned to it before deleting a property. To disconnect them from a property, navigate to Instance Settings > Analytics Cloud > Properties and select the properties with synchronizations that you wish to undo. Access our documentation to learn more.(Opens a new window)';
			const nodeHasText = hasText(node);
			const childrenDontHaveText = Array.from(node.children).every(
				child => !hasText(child)
			);

			return nodeHasText && childrenDontHaveText;
		});

		expect(modalText).toBeTruthy();

		expect(
			screen.getByText('Access our documentation to learn more.')
		).toBeInTheDocument();

		expect(
			screen.getByText('Access our documentation to learn more.')
		).toHaveAttribute(
			'href',
			'https://learn.liferay.com/w/dxp/personalization/analytics-cloud/workspace-settings/managing-properties#adding-and-removing-users-to-a-property'
		);
	});

	it('should check error modal message and hyperlink on deleting property that has SITES synced', async () => {
		API.user.fetchCurrentUser.mockReturnValueOnce(
			Promise.resolve(data.mockUser())
		);

		API.channels.fetch.mockReturnValueOnce(
			Promise.resolve(
				data.mockChannel(1, 1, {
					commerceChannelsCount: 0,
					groupsCount: 5
				})
			)
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(
			screen.getByText(
				'There are 5 sites and 0 channels synced to this property.'
			)
		).toBeInTheDocument();

		const deleteBtn = screen.getByTestId('delete');

		fireEvent.click(deleteBtn);

		expect(
			container.querySelector('div.modal-container')
		).toBeInTheDocument();

		expect(container.querySelector('div.modal-container')).toHaveClass(
			'show'
		);

		expect(
			screen.getByText('Unable to Delete Property')
		).toBeInTheDocument();

		expect(
			screen.getByText('Access our documentation to learn more.')
		).toBeInTheDocument();

		expect(
			screen.getByText('Access our documentation to learn more.')
		).toHaveAttribute(
			'href',
			'https://learn.liferay.com/w/dxp/personalization/analytics-cloud/workspace-settings/managing-properties#adding-and-removing-users-to-a-property'
		);
	});

	it('should check error modal message and hyperlink on deleting property that has SITES and CHANNELS NOT synced', async () => {
		API.user.fetchCurrentUser.mockReturnValueOnce(
			Promise.resolve(data.mockUser())
		);

		API.channels.fetch.mockReturnValueOnce(
			Promise.resolve(
				data.mockChannel(1, 1, {
					commerceChannelsCount: 0,
					groupsCount: 0
				})
			)
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(
			screen.getByText(
				'There are 0 sites and 0 channels synced to this property.'
			)
		).toBeInTheDocument();

		const deleteBtn = screen.getByTestId('delete');

		fireEvent.click(deleteBtn);

		expect(
			container.querySelector('div.modal-container')
		).toBeInTheDocument();

		expect(container.querySelector('div.modal-container')).toHaveClass(
			'show'
		);

		expect(screen.getByText('Delete Channel 1?')).toBeInTheDocument();

		expect(
			screen.getByText(
				'To delete Channel 1, copy the sentence below to confirm your intention to delete property.'
			)
		).toBeInTheDocument();
	});

	it('should render a warning modal when the user toggles from All User to Select User property permissions', async () => {
		const {container} = render(<DefaultComponent />);
		const modalContainer = container.querySelector('.modal-renderer-root');
		const customMatcher = content => content === 'Permissions Change';

		await waitForLoadingToBeRemoved(container);

		expect(queryByText(modalContainer, customMatcher)).toBeNull();

		fireEvent.click(screen.getByLabelText('Select Users'));

		expect(getByText(modalContainer, customMatcher)).toBeTruthy();
	});
});
