import * as API from 'shared/api';
import ManageSegmentNotificationsModal from '../ManageSegmentNotificationsModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {Provider} from 'react-redux';
import {useModal} from '@clayui/modal';

jest.unmock('react-dom');

const DefaultComponent = ({
	onClose = jest.fn(),
}: {
	onClose?: () => void;
} = {}) => {
	const {observer} = useModal({defaultOpen: true});

	return (
		<Provider store={mockStore()}>
			<ManageSegmentNotificationsModal
				groupId="23"
				observer={observer}
				onClose={onClose}
				segmentId="1"
			/>
		</Provider>
	);
};

describe('ManageSegmentNotificationsModal', () => {
	beforeEach(() => {
		(API.preferences.fetchSegmentNotifications as jest.Mock).mockClear();
		(API.preferences.updateSegmentNotification as jest.Mock).mockClear();
	});

	it('renders the backend defaults when nothing has been saved', async () => {
		render(<DefaultComponent />);

		expect(
			await screen.findByText('Manage Segment Notifications')
		).toBeInTheDocument();

		expect(
			await screen.findByLabelText('New Member Added to the Segment')
		).not.toBeChecked();

		expect(screen.getByLabelText('Email Frequency')).toHaveValue('monthly');
	});

	it('renders the saved notification settings', async () => {
		(
			API.preferences.fetchSegmentNotifications as jest.Mock
		).mockResolvedValueOnce({
			1: {emailFrequency: 'weekly', newMemberAdded: true},
		});

		render(<DefaultComponent />);

		expect(
			await screen.findByLabelText('New Member Added to the Segment')
		).toBeChecked();

		expect(screen.getByLabelText('Email Frequency')).toHaveValue('weekly');

		expect(API.preferences.fetchSegmentNotifications).toHaveBeenCalledWith({
			groupId: '23',
			segmentId: '1',
		});
	});

	it('closes without saving when Cancel is clicked', async () => {
		const onClose = jest.fn();

		render(<DefaultComponent onClose={onClose} />);

		fireEvent.click(await screen.findByText('Cancel'));

		expect(onClose).toHaveBeenCalled();
	});

	it('saves the current values and closes when Save is clicked', async () => {
		const onClose = jest.fn();

		render(<DefaultComponent onClose={onClose} />);

		fireEvent.click(
			await screen.findByLabelText('New Member Added to the Segment')
		);

		fireEvent.click(screen.getByText('Save'));

		await waitFor(() => expect(onClose).toHaveBeenCalled());

		expect(API.preferences.updateSegmentNotification).toHaveBeenCalledWith({
			emailFrequency: 'monthly',
			groupId: '23',
			newMemberAdded: true,
			segmentId: '1',
		});
	});
});
