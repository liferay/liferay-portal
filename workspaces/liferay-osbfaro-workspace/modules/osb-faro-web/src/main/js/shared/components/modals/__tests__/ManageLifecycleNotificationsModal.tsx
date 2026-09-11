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
		<ManageLifecycleNotificationsModal onClose={onClose} />
	</Provider>
);

describe('ManageLifecycleNotificationsModal', () => {
	it('renders the notification settings with their defaults', () => {
		render(<DefaultComponent />);

		expect(
			screen.getByText('Manage Lifecycle Notifications')
		).toBeInTheDocument();

		[
			'New Accounts',
			'Account Stage Changes',
			'Net New Pipeline Accounts',
			'New Stalled Accounts',
			'New At Risk Accounts',
		].forEach((label) => {
			expect(screen.getByLabelText(label)).toBeChecked();
		});

		expect(screen.getByLabelText('Email Frequency')).toHaveValue('daily');
	});

	it('closes without saving when Cancel is clicked', () => {
		const onClose = jest.fn(close);

		render(<DefaultComponent onClose={onClose} />);

		fireEvent.click(screen.getByText('Cancel'));

		expect(onClose).toHaveBeenCalled();
	});

	it('closes and saves when Save is clicked', async () => {
		const onClose = jest.fn(close);

		render(<DefaultComponent onClose={onClose} />);

		fireEvent.click(screen.getByText('Save'));

		await waitFor(() => expect(onClose).toHaveBeenCalled());
	});
});
