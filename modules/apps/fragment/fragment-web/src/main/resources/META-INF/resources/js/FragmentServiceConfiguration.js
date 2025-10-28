/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayCheckbox} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {fetch, objectToFormData} from 'frontend-js-web';
import React, {useState} from 'react';

const FEEDBACK_MESSAGES = {
	error: {
		cssClass: 'has-error',
		icon: 'exclamation-full',
		text: Liferay.Language.get(
			'the-changes-could-not-be-propagated,-please-try-again'
		),
	},
	success: {
		cssClass: 'has-success',
		icon: 'check-circle-full',
		text: Liferay.Language.get('all-changes-were-propagated'),
	},
};

export default function FragmentServiceConfiguration({
	alreadyPropagateContributedFragmentChanges,
	namespace,
	propagateChanges,
	propagateContributedFragmentChanges,
	propagateContributedFragmentEntriesChangesURL,
}) {
	const [disablePropagateChangesButton, setDisablePropagateChangesButton] =
		useState(alreadyPropagateContributedFragmentChanges);

	const [
		propagateContributedFragmentChangesChecked,
		setPropagateContributedFragmentChangesChecked,
	] = useState(propagateContributedFragmentChanges);

	const [propagateChangesChecked, setPropagateChangesChecked] =
		useState(propagateChanges);

	const [feedbackMessage, setFeedbackMessage] = useState(
		disablePropagateChangesButton ? FEEDBACK_MESSAGES.success : null
	);

	const [warningModalVisible, setWarningModalVisible] = useState(false);

	const {observer, onClose} = useModal({
		onClose: () => setWarningModalVisible(false),
	});

	const handleSubmit = () => {
		setFeedbackMessage(null);

		fetch(propagateContributedFragmentEntriesChangesURL, {
			body: objectToFormData({
				[`${namespace}propagateChanges`]: propagateChangesChecked,
				[`${namespace}propagateContributedFragmentChanges`]:
					propagateContributedFragmentChangesChecked,
			}),
			method: 'POST',
		})
			.then((response) => response.json())
			.then(({success}) => {
				if (success) {
					openToast({
						message: Liferay.Language.get(
							'the-changes-in-the-contributed-fragments-have-been-propagated-successfully'
						),
						title: Liferay.Language.get('success'),
						type: 'success',
					});

					setDisablePropagateChangesButton(true);
					setFeedbackMessage(FEEDBACK_MESSAGES.success);
				}
				else {
					openToast({
						message: Liferay.Language.get(
							'something-went-wrong-and-the-changes-in-the-contributed-fragments-could-not-be-propagated.-please-try-again-later'
						),
						title: Liferay.Language.get('error'),
						type: 'danger',
					});

					setFeedbackMessage(FEEDBACK_MESSAGES.error);
				}

				setWarningModalVisible(false);
			});
	};

	return (
		<>
			<h3 className="sheet-subtitle">
				{Liferay.Language.get('default-fragments')}
			</h3>

			<p className="text-secondary">
				{Liferay.Language.get(
					'default-fragments-are-provided-by-liferay-and-they-are-part-of-the-product-code.-define-their-behavior'
				)}
			</p>

			<ClayCheckbox
				defaultChecked={propagateContributedFragmentChanges}
				label={Liferay.Language.get(
					'propagate-contributed-fragment-changes-automatically'
				)}
				name={`${namespace}propagateContributedFragmentChanges`}
				onChange={(event) =>
					setPropagateContributedFragmentChangesChecked(
						event.target.checked
					)
				}
			/>

			<div aria-hidden="true" className="form-feedback-group mb-3">
				<div className="form-text text-weight-normal">
					{Liferay.Language.get(
						'propagate-contributed-fragment-changes-automatically-description'
					)}
				</div>
			</div>

			{!propagateContributedFragmentChangesChecked && (
				<div className="align-items-center d-flex">
					<ClayButton
						className="mr-3"
						disabled={disablePropagateChangesButton}
						displayType="secondary"
						onClick={() => setWarningModalVisible(true)}
					>
						{Liferay.Language.get('propagate-changes')}
					</ClayButton>

					{feedbackMessage && (
						<ClayForm.Group
							className={`mb-0 ${feedbackMessage.cssClass}`}
						>
							<ClayForm.FeedbackGroup>
								<ClayForm.FeedbackItem>
									<ClayForm.FeedbackIndicator
										symbol={feedbackMessage.icon}
									/>

									{feedbackMessage.text}
								</ClayForm.FeedbackItem>
							</ClayForm.FeedbackGroup>
						</ClayForm.Group>
					)}
				</div>
			)}

			<h3 className="mt-3 sheet-subtitle">
				{Liferay.Language.get('custom-fragments')}
			</h3>

			<p className="text-secondary">
				{Liferay.Language.get(
					'custom-fragments-are-those-that-are-created-by-the-user.-define-their-behavior'
				)}
			</p>

			<ClayCheckbox
				defaultChecked={propagateChanges}
				label={Liferay.Language.get(
					'propagate-fragment-changes-automatically'
				)}
				name={`${namespace}propagateChanges`}
				onChange={(event) =>
					setPropagateChangesChecked(event.target.checked)
				}
			/>

			<div aria-hidden="true" className="form-feedback-group">
				<div className="form-text text-weight-normal">
					{Liferay.Language.get(
						'propagate-fragment-changes-automatically-description'
					)}
				</div>
			</div>

			{warningModalVisible && (
				<ClayModal
					observer={observer}
					role="alertdialog"
					size="md"
					status="warning"
				>
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('propagate-changes')}
					</ClayModal.Header>

					<ClayModal.Body>
						<p className="text-secondary">
							{Liferay.Language.get(
								'please-be-aware-that-if-any-content-creator-is-editing-a-page,-some-changes-may-not-be-saved.-performance-issues-can-also-result-from-this-action'
							)}
						</p>

						<p className="text-secondary">
							{Liferay.Language.get(
								'are-you-sure-you-want-to-continue'
							)}
						</p>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									displayType="secondary"
									onClick={onClose}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton
									displayType="warning"
									onClick={handleSubmit}
								>
									{Liferay.Language.get('continue')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayModal>
			)}
		</>
	);
}
