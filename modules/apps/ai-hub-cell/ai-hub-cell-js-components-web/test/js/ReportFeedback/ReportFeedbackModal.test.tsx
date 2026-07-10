/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import ReportFeedbackModal from '../../../src/main/resources/META-INF/resources/js/ReportFeedback/ReportFeedbackModal';
import {postAIIssueReport} from '../../../src/main/resources/META-INF/resources/js/ReportFeedback/api';

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/ReportFeedback/api'
);

const mockPostAIIssueReport = postAIIssueReport as jest.MockedFunction<
	typeof postAIIssueReport
>;

const openToast = jest.fn();

function renderModal(
	props: Partial<{
		onClose: () => void;
	}> = {}
) {
	return render(
		<ReportFeedbackModal
			agentDefinitionExternalReferenceCodes={['agent-1']}
			onClose={jest.fn()}
			surface="aiAssistant"
			{...props}
		/>
	);
}

describe('ReportFeedbackModal', () => {
	beforeEach(() => {
		mockPostAIIssueReport.mockReset();
		openToast.mockReset();

		global.Liferay = {
			...global.Liferay,
			Util: {
				...global.Liferay?.Util,
				openToast,
			},
		};
	});

	it('renders the title and every reason option', async () => {
		renderModal();

		expect(await screen.findByText('send-feedback')).toBeInTheDocument();
		expect(
			screen.getByText('incorrect-or-inaccurate-response')
		).toBeInTheDocument();
		expect(
			screen.getByText('exposure-of-personal-or-sensitive-data-pii')
		).toBeInTheDocument();
		expect(
			screen.getByText('agent-error-or-malfunction')
		).toBeInTheDocument();
	});

	it('keeps Send disabled until a reason is chosen', async () => {
		renderModal();

		const send = await screen.findByRole('button', {name: 'send'});

		expect(send).toBeDisabled();

		fireEvent.change(screen.getByRole('combobox'), {
			target: {value: 'other'},
		});

		expect(send).toBeEnabled();
	});

	it('submits the report and fires a success toast', async () => {
		mockPostAIIssueReport.mockResolvedValue({id: 'report-1'});

		renderModal();

		fireEvent.change(await screen.findByRole('combobox'), {
			target: {value: 'inaccurateResponse'},
		});
		fireEvent.click(screen.getByRole('button', {name: 'send'}));

		await waitFor(() =>
			expect(openToast).toHaveBeenCalledWith({
				message: 'thank-you-for-your-feedback',
				type: 'success',
			})
		);

		expect(mockPostAIIssueReport).toHaveBeenCalled();
	});

	it('shows an inline error and stays open on failure', async () => {
		mockPostAIIssueReport.mockRejectedValue(new Error('submit failed'));

		renderModal();

		fireEvent.change(await screen.findByRole('combobox'), {
			target: {value: 'other'},
		});
		fireEvent.click(screen.getByRole('button', {name: 'send'}));

		expect(await screen.findByText('submit failed')).toBeInTheDocument();
		expect(openToast).not.toHaveBeenCalled();
	});

	it('does not propagate key events to surrounding components', async () => {
		const onAncestorKeyDown = jest.fn();

		render(
			<div onKeyDown={onAncestorKeyDown}>
				<ReportFeedbackModal
					agentDefinitionExternalReferenceCodes={['agent-1']}
					onClose={jest.fn()}
					surface="aiAssistant"
				/>
			</div>
		);

		fireEvent.keyDown(await screen.findByRole('combobox'), {key: 'Tab'});

		expect(onAncestorKeyDown).not.toHaveBeenCalled();
	});
});
