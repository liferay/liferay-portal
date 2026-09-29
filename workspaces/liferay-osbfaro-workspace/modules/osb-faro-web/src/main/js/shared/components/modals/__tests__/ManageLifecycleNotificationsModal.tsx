import * as API from 'shared/api';
import ManageLifecycleNotificationsModal from '../ManageLifecycleNotificationsModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {close} from 'shared/actions/modals';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

const DefaultComponent = ({
	onClose = jest.fn(close),
}: {
	onClose?: typeof close;
} = {}) => (
	<Provider store={mockStore()}>
		<ManageLifecycleNotificationsModal
			groupId="23"
			lifecycleId="1"
			onClose={onClose}
		/>
	</Provider>
);

describe('ManageLifecycleNotificationsModal', () => {
	beforeEach(() => {
		(API.preferences.fetchLifecycleNotifications as jest.Mock).mockClear();
		(API.preferences.updateLifecycleNotification as jest.Mock).mockClear();
	});

	it('renders the backend defaults when nothing has been saved', async () => {
		render(<DefaultComponent />);

		expect(
			await screen.findByText('Manage Lifecycle Notifications')
		).toBeInTheDocument();

		expect(await screen.findByLabelText('New Accounts')).not.toBeChecked();
		expect(
			screen.getByLabelText('Account Stage Changes')
		).not.toBeChecked();
		expect(screen.getByLabelText('Email Frequency')).toHaveValue('monthly');
	});

	it('renders the saved notification settings', async () => {
		(
			API.preferences.fetchLifecycleNotifications as jest.Mock
		).mockResolvedValueOnce({
			1: {
				accountStageChanges: false,
				emailFrequency: 'weekly',
				newAccounts: true,
			},
		});

		render(<DefaultComponent />);

		expect(await screen.findByLabelText('New Accounts')).toBeChecked();
		expect(
			screen.getByLabelText('Account Stage Changes')
		).not.toBeChecked();
		expect(screen.getByLabelText('Email Frequency')).toHaveValue('weekly');

		expect(
			API.preferences.fetchLifecycleNotifications
		).toHaveBeenCalledWith({groupId: '23', lifecycleId: '1'});
	});

	it('closes without saving when Cancel is clicked', async () => {
		const onClose = jest.fn(close);

		render(<DefaultComponent onClose={onClose} />);

		fireEvent.click(await screen.findByText('Cancel'));

		expect(onClose).toHaveBeenCalled();
	});

	it('saves the current values, including unchecked ones, and closes when Save is clicked', async () => {
		const onClose = jest.fn(close);

		render(<DefaultComponent onClose={onClose} />);

		fireEvent.click(await screen.findByLabelText('New Accounts'));

		fireEvent.click(screen.getByText('Save'));

		await waitFor(() => expect(onClose).toHaveBeenCalled());

		expect(
			API.preferences.updateLifecycleNotification
		).toHaveBeenCalledWith({
			accountStageChanges: false,
			emailFrequency: 'monthly',
			groupId: '23',
			lifecycleId: '1',
			netNewPipelineAccounts: false,
			newAccounts: true,
			newAtRiskAccounts: false,
			newStalledAccounts: false,
		});
	});
});
