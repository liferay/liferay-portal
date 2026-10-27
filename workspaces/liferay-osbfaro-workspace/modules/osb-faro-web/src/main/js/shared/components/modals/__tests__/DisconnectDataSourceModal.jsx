import DisconnectDataSourceModal from '../DisconnectDataSourceModal';
import React from 'react';
import {Alert} from 'shared/types';
import {cleanup, fireEvent, screen, waitFor} from '@testing-library/react';
import {renderModal} from 'test/render-modal';

jest.unmock('react-dom');

const renderDisconnect = (props) =>
	renderModal((observer) => (
		<DisconnectDataSourceModal
			addAlert={jest.fn()}
			errorMessage="the error"
			message="Stop syncing?"
			observer={observer}
			onClose={jest.fn()}
			onSubmit={jest.fn(() => Promise.resolve())}
			{...props}
		/>
	));

describe('DisconnectDataSourceModal', () => {
	afterEach(cleanup);

	it('renders as a warning with the message', async () => {
		await renderDisconnect();

		expect(document.querySelector('.modal-warning')).toBeTruthy();

		expect(screen.getByText('Stop syncing?')).toBeInTheDocument();
	});

	it('runs the submit of the caller, shows the success alert and closes', async () => {
		const addAlert = jest.fn();
		const onClose = jest.fn();
		const onSubmit = jest.fn(() => Promise.resolve());

		await renderDisconnect({addAlert, onClose, onSubmit});

		fireEvent.click(screen.getByRole('button', {name: 'Disconnect'}));

		await waitFor(() => expect(onClose).toHaveBeenCalled());

		expect(onSubmit).toHaveBeenCalledTimes(1);

		expect(addAlert).toHaveBeenCalledWith({
			alertType: Alert.Types.Success,
			message: Liferay.Language.get('data-source-disconnected'),
		});
	});

	it('shows the error message of the caller when the submit fails', async () => {
		const addAlert = jest.fn();

		await renderDisconnect({
			addAlert,
			onSubmit: jest.fn(() => Promise.reject(new Error('boom'))),
		});

		fireEvent.click(screen.getByRole('button', {name: 'Disconnect'}));

		await waitFor(() =>
			expect(addAlert).toHaveBeenCalledWith({
				alertType: Alert.Types.Error,
				message: 'the error',
			})
		);

		expect(addAlert).not.toHaveBeenCalledWith(
			expect.objectContaining({alertType: Alert.Types.Success})
		);
	});

	it('closes when cancel is clicked', async () => {
		const onClose = jest.fn();

		await renderDisconnect({onClose});

		fireEvent.click(screen.getByText('Cancel'));

		expect(onClose).toHaveBeenCalledTimes(1);
	});
});
