/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import React, {useState} from 'react';

import PermissionsOptions from '../PermissionsOptions';
import ScheduleOptions from '../ScheduleOptions';

export default function PublishModal({
	actionButton,
	articleId,
	displayDate: defaultDisplayDate,
	onCloseModal,
	onPublishButtonClick,
	permissionsURL,
	portletNamespace,
	showPermissionsOptions,
	timeZone,
	workflowEnabled,
}) {
	const formId = `${portletNamespace}fm1`;

	const {observer, onClose} = useModal({
		onClose: () => {
			onCloseModal();
		},
	});

	const {button, description, heading} = getLabels({
		actionButton,
		articleId,
		workflowEnabled,
	});

	const [displayDate, setDisplayDate] = useState(defaultDisplayDate);
	const [dateError, setDateError] = useState('');
	const [showErrorAlert, setShowErrorAlert] = useState(false);
	const [isSubmitting, setIsSubmitting] = useState(false);

	const handleButtonClick = () => {
		if (isSubmitting) {
			return;
		}

		if (!displayDate && actionButton === 'schedule') {
			setDateError(Liferay.Language.get('please-enter-a-valid-date'));
			setShowErrorAlert(true);

			return;
		}

		if (dateError) {
			setShowErrorAlert(true);

			return;
		}

		setIsSubmitting(true);
		onPublishButtonClick(actionButton);
	};

	return (
		<ClayModal className="m-0" observer={observer} size="md">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{heading}
			</ClayModal.Header>

			<ClayModal.Body className="m-0">
				{showErrorAlert && dateError ? (
					<ClayAlert
						displayType="danger"
						onClose={() => setShowErrorAlert(false)}
						title={`${Liferay.Language.get('error')}:`}
					>
						{dateError}
					</ClayAlert>
				) : null}

				<p className="text-secondary">{description}</p>

				{actionButton === 'schedule' ? (
					<ScheduleOptions
						displayDate={displayDate}
						error={dateError}
						formId={formId}
						portletNamespace={portletNamespace}
						setDisplayDate={setDisplayDate}
						setError={setDateError}
						timeZone={timeZone}
					/>
				) : null}

				{(!articleId || Liferay.FeatureFlags['LPD-11228']) &&
					showPermissionsOptions && (
						<div className="mt-3">
							<PermissionsOptions
								formId={formId}
								permissionsURL={permissionsURL}
							/>
						</div>
					)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							form={formId}
							onClick={handleButtonClick}
							type={
								dateError ||
								(!displayDate && actionButton === 'schedule')
									? 'button'
									: 'submit'
							}
						>
							{button}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}

function getLabels({actionButton, articleId, workflowEnabled}) {
	if (actionButton === 'publish') {
		return {
			button: workflowEnabled
				? Liferay.Language.get('submit-for-workflow')
				: Liferay.Language.get('publish'),
			description: Liferay.Language.get(
				'confirm-the-web-content-visibility-before-publishing'
			),
			heading: workflowEnabled
				? articleId
					? Liferay.Language.get('submit-for-workflow')
					: Liferay.Language.get(
							'submit-for-workflow-with-permissions'
						)
				: Liferay.Language.get('publish-with-permissions'),
		};
	}
	else if (actionButton === 'schedule') {
		return {
			button: workflowEnabled
				? Liferay.Language.get('submit-for-workflow')
				: Liferay.Language.get('schedule[verb]'),
			description: articleId
				? workflowEnabled
					? Liferay.Language.get(
							'set-the-date-and-time-for-publishing-the-web-content-and-submit-it-for-workflow'
						)
					: Liferay.Language.get(
							'set-the-date-and-time-for-publishing-the-web-content'
						)
				: workflowEnabled
					? Liferay.Language.get(
							'set-the-publishing-date-and-time-for-the-web-content-confirm-the-visibility-and-submit-it-for-workflow'
						)
					: Liferay.Language.get(
							'set-the-date-and-time-for-publishing-the-web-content-and-confirm-the-visibility-before-scheduling'
						),
			heading: workflowEnabled
				? Liferay.Language.get(
						'schedule-publication-and-submit-for-workflow'
					)
				: Liferay.Language.get('schedule-publication'),
		};
	}
	else {
		return {
			button: Liferay.Language.get('save-as-draft'),
			description: Liferay.Language.get(
				'confirm-the-web-content-visibility-before-saving-as-draft'
			),
			heading: Liferay.Language.get('save-as-draft-with-permissions'),
		};
	}
}
