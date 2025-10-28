/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React, {useRef} from 'react';
import {flushSync} from 'react-dom';

import {EVENT_VALIDATE_FORM} from './ContentEditorToolbar';
import ScheduleField, {dateConfig, toServerISOFormat} from './ScheduleField';

type HTMLScheduleField = {
	validate: () => boolean;
};

export default function SchedulePublicationModal({
	date,
	formId,
	hasWorkflow,
	onCloseModal,
	onUpdateDate,
	type,
}: {
	date: string;
	formId: string;
	hasWorkflow: boolean;
	onCloseModal: () => void;
	onUpdateDate: (date: string) => void;
	type: string;
}) {
	const fieldRef = useRef<HTMLScheduleField>(null);
	const {observer, onClose} = useModal({
		onClose: () => onCloseModal(),
	});

	const onCancel = () => {
		onUpdateDate('');
		onClose();
	};

	const onSchedule = (event: React.MouseEvent<HTMLButtonElement>) => {
		const isValid = fieldRef.current?.validate();

		if (!isValid) {
			event.preventDefault();

			return;
		}

		flushSync(() => {
			onUpdateDate(toServerISOFormat(date));
		});

		onClose();

		Liferay.fire(EVENT_VALIDATE_FORM, {event});
	};

	const onUpdate = ({error, value}: {error: string; value: string}) => {
		if (!error) {
			onUpdateDate(toServerISOFormat(value));
		}
	};

	return (
		<ClayModal observer={observer}>
			<ClayModal.Header>
				{hasWorkflow
					? Liferay.Language.get(
							'schedule-publication-and-submit-for-workflow'
						)
					: Liferay.Language.get('schedule-publication')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p className="text-secondary">
					{hasWorkflow
						? sub(
								Liferay.Language.get(
									'set-the-date-and-time-for-publishing-the-x-and-submit-it-for-workflow'
								),
								type
							)
						: sub(
								Liferay.Language.get(
									'set-the-date-and-time-for-publishing-the-x'
								),
								type
							)}
				</p>

				<ScheduleField
					date={date}
					dateConfig={dateConfig}
					label={Liferay.Language.get('date-and-time')}
					name="displayDate"
					ref={fieldRef}
					required
					updateFieldData={onUpdate}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onCancel}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							form={formId}
							onClick={onSchedule}
							type="submit"
						>
							{hasWorkflow
								? Liferay.Language.get('submit-for-workflow')
								: Liferay.Language.get('schedule')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
