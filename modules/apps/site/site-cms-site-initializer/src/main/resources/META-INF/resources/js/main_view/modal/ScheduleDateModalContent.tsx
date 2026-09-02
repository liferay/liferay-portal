/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import moment from 'moment';
import React, {useRef, useState} from 'react';

import ScheduleField, {
	dateConfig,
	isPastDate,
	toPickerDate,
	toUTCISOFormat,
} from '../../content_editor/components/ScheduleField';

interface ScheduleFieldRef {
	validate: () => boolean;
}

interface UpdateFieldData {
	error?: string;
	name: string;
	neverCheckbox?: boolean;
	value: string;
}

export default function ScheduleDateModalContent({
	closeModal,
	date = '',
	fieldLabel,
	fieldName,
	neverLabel,
	onSave,
	saveRequirementLabel,
	title,
}: {
	closeModal: () => void;
	date?: string;
	fieldLabel: string;
	fieldName: string;
	neverLabel: string;
	onSave: (date: string) => Promise<boolean>;
	saveRequirementLabel: string;
	title: string;
}) {
	const fieldRef = useRef<ScheduleFieldRef>(null);

	const [field, setField] = useState({
		error: '',
		never: false,
		value: toPickerDate(date),
	});
	const [saving, setSaving] = useState(false);

	const canSave = field.never || (Boolean(field.value) && !field.error);

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		if (!field.never) {
			fieldRef.current?.validate();

			const isDateInvalid =
				!moment(field.value, dateConfig.momentFormat, true).isValid() ||
				isPastDate(field.value);

			if (isDateInvalid) {
				return;
			}
		}

		setSaving(true);

		const success = await onSave(
			field.never ? '' : toUTCISOFormat(field.value)
		);

		setSaving(false);

		if (success) {
			closeModal();
		}
	};

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}

				{!canSave && (
					<span className="sr-only">{saveRequirementLabel}</span>
				)}
			</ClayModal.Header>

			<ClayModal.Body>
				<ScheduleField
					date={field.value}
					dateConfig={dateConfig}
					error={field.error}
					label={fieldLabel}
					name={fieldName}
					neverCheckbox={{
						label: neverLabel,
						value: field.never,
					}}
					ref={fieldRef}
					updateFieldData={({
						error,
						neverCheckbox,
						value,
					}: UpdateFieldData) =>
						setField((previousField) => ({
							error: error ?? '',
							never: neverCheckbox ?? previousField.never,
							value,
						}))
					}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={saving || !canSave}
							displayType="primary"
							type="submit"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
}
