/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {RequiredMask} from 'frontend-js-components-web';
import React from 'react';

import {ReportFeedbackReason, ReportFeedbackSurface} from './api';
import {REASON_OPTIONS} from './constants';
import showThanksForFeedbackToast from './showThanksForFeedbackToast';
import useReportFeedback from './useReportFeedback';

import './ReportFeedbackModal.scss';

interface ReportFeedbackModalProps {
	agentDefinitionExternalReferenceCodes: string[];
	onClose: () => void;
	onSubmitted?: () => void;
	surface: ReportFeedbackSurface;
}

const ReportFeedbackModal: React.FC<ReportFeedbackModalProps> = ({
	agentDefinitionExternalReferenceCodes,
	onClose,
	onSubmitted,
	surface,
}) => {
	const {observer, onClose: closeModal} = useModal({onClose});

	const {
		canSubmit,
		error,
		reason,
		setReason,
		setUserMessage,
		submit,
		submitting,
		userMessage,
	} = useReportFeedback({
		agentDefinitionExternalReferenceCodes,
		surface,
	});

	async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
		event.preventDefault();

		if (await submit()) {
			showThanksForFeedbackToast();

			onSubmitted?.();
			closeModal();
		}
	}

	return (
		<ClayModal
			observer={observer}
			onKeyDown={(event) => event.stopPropagation()}
		>
			<ClayForm onSubmit={handleSubmit}>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{Liferay.Language.get('send-feedback')}
				</ClayModal.Header>

				<ClayModal.Body>
					{error && (
						<ClayAlert displayType="danger">{error}</ClayAlert>
					)}

					<ClayForm.Group>
						<label htmlFor="reportFeedbackReason">
							{Liferay.Language.get('reason')}

							<RequiredMask />
						</label>

						<ClaySelectWithOption
							aria-required="true"
							disabled={submitting}
							id="reportFeedbackReason"
							onChange={(event) =>
								setReason(
									event.target.value as ReportFeedbackReason
								)
							}
							options={REASON_OPTIONS}
							required
							value={reason}
						/>
					</ClayForm.Group>

					<ClayForm.Group>
						<label htmlFor="reportFeedbackUserMessage">
							{Liferay.Language.get('comment-optional')}
						</label>

						<ClayInput
							className="report-feedback-modal__comment"
							component="textarea"
							disabled={submitting}
							id="reportFeedbackUserMessage"
							onChange={(event) =>
								setUserMessage(event.target.value)
							}
							value={userMessage}
						/>
					</ClayForm.Group>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								disabled={submitting}
								displayType="secondary"
								onClick={() => closeModal()}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								disabled={!canSubmit}
								displayType="primary"
								type="submit"
							>
								{Liferay.Language.get('send')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</ClayModal>
	);
};

export default ReportFeedbackModal;
