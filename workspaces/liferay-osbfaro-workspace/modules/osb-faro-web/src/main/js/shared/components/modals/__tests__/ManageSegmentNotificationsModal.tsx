import ManageSegmentNotificationsModal from '../ManageSegmentNotificationsModal';
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
		<ManageSegmentNotificationsModal onClose={onClose} />
	</Provider>
);

describe('ManageSegmentNotificationsModal', () => {
	it('renders the notification settings with their defaults', () => {
		render(<DefaultComponent />);

		expect(
			screen.getByText('Manage Segment Notifications')
		).toBeInTheDocument();

		expect(
			screen.getByLabelText('New Member Added to the Segment')
		).toBeChecked();

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
