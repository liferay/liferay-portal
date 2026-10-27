import React from 'react';
import {render, waitFor} from '@testing-library/react';
import {useModal} from '@clayui/modal';

const ModalHarness = ({children}) => {
	const {observer} = useModal({defaultOpen: true});

	return children(observer);
};

export const waitForModal = () =>
	waitFor(() =>
		expect(document.querySelector('.modal-header, .modal-body')).not.toBeNull()
	);

/**
 * Renders a Clay modal that is open from the start. The modal draws in a
 * portal and Clay shows its content a moment after it mounts, so this waits
 * for the header or body and returns once the content can be queried on `document`.
 *
 * @param {function} renderModal Receives the `observer` and returns the modal.
 */
export const renderModal = async (renderModal, options) => {
	const result = render(
		<ModalHarness>{renderModal}</ModalHarness>,
		options
	);

	await waitForModal();

	return result;
};
