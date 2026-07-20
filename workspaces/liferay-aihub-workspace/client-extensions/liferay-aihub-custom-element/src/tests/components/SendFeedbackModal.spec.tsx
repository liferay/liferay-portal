/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';
import {beforeEach, describe, expect, it, vi} from 'vitest';

import {postAIIssueReport} from '../../api';
import SendFeedbackModal from '../../components/SendFeedbackModal';

vi.mock('../../api', () => ({postAIIssueReport: vi.fn()}));

// ClayModal defers rendering its children until a CSS transition completes,
// which never fires under jsdom. The open/close animation and dialog a11y are
// Clay's concern (and are covered by the browser tests); here the modal is
// mocked to a passthrough so the real form behavior renders synchronously and
// can be exercised.

vi.mock('@clayui/modal', () => {
	const ClayModal = ({children}: {children: React.ReactNode}) => (
		<div>{children}</div>
	);

	ClayModal.Header = ({children}: {children: React.ReactNode}) => (
		<div>{children}</div>
	);
	ClayModal.Body = ({children}: {children: React.ReactNode}) => (
		<div>{children}</div>
	);
	ClayModal.Footer = ({last}: {last: React.ReactNode}) => <div>{last}</div>;

	return {
		__esModule: true,
		default: ClayModal,
		useModal: ({onClose}: {onClose: () => void}) => ({
			observer: {},
			onClose,
		}),
	};
});

const mockedPost = vi.mocked(postAIIssueReport);

function renderModal(
	overrides: Partial<{onClose: () => void; onSubmitted: () => void}> = {}
) {
	const props = {
		agentDefinitionExternalReferenceCodes: ['agent-1'],
		chatbotExternalReferenceCode: 'chatbot-1',
		onClose: vi.fn(),
		onSubmitted: vi.fn(),
		...overrides,
	};

	render(<SendFeedbackModal {...props} />);

	return props;
}

describe('SendFeedbackModal', () => {
	beforeEach(() => {
		mockedPost.mockReset();
	});

	it('closes the modal when Cancel is clicked', () => {
		const {onClose} = renderModal();

		fireEvent.click(screen.getByRole('button', {name: 'Cancel'}));

		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('keeps Send disabled until a reason is chosen', () => {
		renderModal();

		const send = screen.getByRole('button', {name: 'Send'});

		expect(send).toHaveAttribute('form', 'aihub-feedback-form');
		expect(send).toBeDisabled();

		fireEvent.change(screen.getByLabelText(/Reason/), {
			target: {value: 'other'},
		});

		expect(send).toBeEnabled();
	});

	it('renders the title and every reason option', () => {
		renderModal();

		expect(screen.getByText('Send Feedback')).toBeInTheDocument();
		expect(
			screen.getByText('Incorrect or Inaccurate Response')
		).toBeInTheDocument();
		expect(
			screen.getByText('Exposure of Personal or Sensitive Data (PII)')
		).toBeInTheDocument();
		expect(
			screen.getByText('Agent Error or Malfunction')
		).toBeInTheDocument();
	});

	it('shows an inline error and keeps the modal open on failure', async () => {
		mockedPost.mockRejectedValue(new Error('nope'));

		const {onSubmitted} = renderModal();

		fireEvent.change(screen.getByLabelText(/Reason/), {
			target: {value: 'other'},
		});
		fireEvent.submit(
			document.getElementById('aihub-feedback-form') as HTMLFormElement
		);

		await waitFor(() =>
			expect(screen.getByText('nope')).toBeInTheDocument()
		);

		expect(onSubmitted).not.toHaveBeenCalled();
	});

	it('submits and calls onSubmitted on success', async () => {
		mockedPost.mockResolvedValue({id: 'mock-1'});

		const {onSubmitted} = renderModal();

		fireEvent.change(screen.getByLabelText(/Reason/), {
			target: {value: 'inaccurateResponse'},
		});
		fireEvent.submit(
			document.getElementById('aihub-feedback-form') as HTMLFormElement
		);

		await waitFor(() => expect(onSubmitted).toHaveBeenCalledTimes(1));

		expect(mockedPost).toHaveBeenCalledWith(
			expect.objectContaining({
				reason: 'inaccurateResponse',
				surface: 'clickToChat',
			})
		);
	});
});
