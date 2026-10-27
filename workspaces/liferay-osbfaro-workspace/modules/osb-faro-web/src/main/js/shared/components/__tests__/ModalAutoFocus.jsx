import ClayModal from '@clayui/modal';
import ModalAutoFocus from '../ModalAutoFocus';
import React from 'react';
import {act, cleanup, screen} from '@testing-library/react';
import {renderModal} from 'test/render-modal';

jest.unmock('react-dom');

const renderFocusModal = () =>
	renderModal(observer => (
		<ClayModal observer={observer}>
			<ClayModal.Header>{'Title'}</ClayModal.Header>

			<ClayModal.Body>
				<ModalAutoFocus selector="input[name=field]" />

				<input aria-label="field" name="field" />

				<input aria-label="other" name="other" />
			</ClayModal.Body>
		</ClayModal>
	));

describe('ModalAutoFocus', () => {
	afterEach(cleanup);

	it('focuses the field after Clay moves the focus to the title', async () => {
		await renderFocusModal();

		act(() => {
			jest.runOnlyPendingTimers();
		});

		expect(screen.getByLabelText('field')).toHaveFocus();
	});

	it('does not focus a field that does not match the selector', async () => {
		await renderFocusModal();

		act(() => {
			jest.runOnlyPendingTimers();
		});

		expect(screen.getByLabelText('other')).not.toHaveFocus();
	});

	it('does not blur the field before it is focused', async () => {
		const onBlur = jest.fn();

		await renderModal(observer => (
			<ClayModal observer={observer}>
				<ClayModal.Header>{'Title'}</ClayModal.Header>

				<ClayModal.Body>
					<ModalAutoFocus selector="input[name=field]" />

					<input
						aria-label="field"
						name="field"
						onBlur={onBlur}
					/>
				</ClayModal.Body>
			</ClayModal>
		));

		act(() => {
			jest.runOnlyPendingTimers();
		});

		expect(onBlur).not.toHaveBeenCalled();
	});
});
