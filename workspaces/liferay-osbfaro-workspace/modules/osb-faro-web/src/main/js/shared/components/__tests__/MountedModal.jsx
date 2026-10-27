import ClayModal from '@clayui/modal';
import MountedModal from '../MountedModal';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';

jest.unmock('react-dom');

const renderMountedModal = onClose =>
	render(
		<MountedModal onClose={onClose}>
			{(observer, close) => (
				<ClayModal observer={observer}>
					<ClayModal.Header>{'Mounted title'}</ClayModal.Header>

					<ClayModal.Body>
						<button onClick={close} type="button">
							{'close it'}
						</button>
					</ClayModal.Body>
				</ClayModal>
			)}
		</MountedModal>
	);

describe('MountedModal', () => {
	afterEach(cleanup);

	it('opens the modal as soon as it is mounted', async () => {
		renderMountedModal(jest.fn());

		expect(await screen.findByText('Mounted title')).toBeInTheDocument();
	});

	it('does not call onClose while the modal is open', async () => {
		const onClose = jest.fn();

		renderMountedModal(onClose);

		await screen.findByText('Mounted title');

		expect(onClose).not.toHaveBeenCalled();
	});

	it('calls onClose once after the close given to the children', async () => {
		const onClose = jest.fn();

		renderMountedModal(onClose);

		fireEvent.click(await screen.findByText('close it'));

		await waitFor(() => expect(onClose).toHaveBeenCalledTimes(1));
	});

	it('calls onClose when the modal is dismissed with Esc', async () => {
		const onClose = jest.fn();

		renderMountedModal(onClose);

		await screen.findByText('Mounted title');

		fireEvent.keyDown(document, {key: 'Escape'});

		await waitFor(() => expect(onClose).toHaveBeenCalledTimes(1));
	});
});
