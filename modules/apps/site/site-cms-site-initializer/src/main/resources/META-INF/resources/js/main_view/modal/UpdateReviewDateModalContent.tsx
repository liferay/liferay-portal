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
	toMomentDate,
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

export default function UpdateReviewDateModalContent({
	closeModal,
	onSave,
	reviewDate = '',
}: {
	closeModal: () => void;
	onSave: (reviewDate: string) => Promise<boolean>;
	reviewDate?: string;
}) {
	const fieldRef = useRef<ScheduleFieldRef>(null);

	const [field, setField] = useState({
		error: '',
		neverReview: !reviewDate,
		value: toMomentDate(reviewDate),
	});
	const [saving, setSaving] = useState(false);

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		if (!field.neverReview) {
			fieldRef.current?.validate();

			const isReviewDateInvalid =
				!moment(field.value, dateConfig.momentFormat, true).isValid() ||
				isPastDate(field.value);

			if (isReviewDateInvalid) {
				return;
			}
		}

		setSaving(true);

		const success = await onSave(
			field.neverReview
				? ''
				: `${moment(field.value, dateConfig.momentFormat).format(
						'YYYY-MM-DDTHH:mm:ss'
					)}Z`
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
				{Liferay.Language.get('update-review-date')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ScheduleField
					date={field.value}
					dateConfig={dateConfig}
					error={field.error}
					label={Liferay.Language.get('review-date')}
					name="reviewDate"
					neverCheckbox={{
						label: Liferay.Language.get('never-review'),
						value: field.neverReview,
					}}
					ref={fieldRef}
					updateFieldData={({
						error,
						neverCheckbox,
						value,
					}: UpdateFieldData) =>
						setField((previousField) => ({
							error: error ?? '',
							neverReview:
								neverCheckbox ?? previousField.neverReview,
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
							disabled={
								saving ||
								(!field.neverReview && Boolean(field.error))
							}
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
