/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {FieldBase} from 'frontend-js-components-web';
import React, {useState} from 'react';

import {deleteProfileDataMask} from '../services/deleteProfileDataMask';
import {patchProfileDataMask} from '../services/patchProfileDataMask';
import {ProfileDataMaskRow} from '../types';
import {openErrorToast, openSuccessToast} from '../utils';

interface RemoveDataMaskModalProps {
	onClose: () => void;
	onRemoved: () => void;
	row: ProfileDataMaskRow;
}

export default function RemoveDataMaskModal({
	onClose,
	onRemoved,
	row,
}: RemoveDataMaskModalProps) {
	const [reason, setReason] = useState('');
	const [saving, setSaving] = useState(false);

	const {observer} = useModal({onClose});

	const remove = async () => {
		setSaving(true);

		const {error: patchError} = await patchProfileDataMask(row.id, {
			deleteReason: reason,
		});

		if (patchError) {
			setSaving(false);

			openErrorToast(patchError);

			return;
		}

		const {error: deleteError} = await deleteProfileDataMask(row.id);

		setSaving(false);

		if (deleteError) {
			openErrorToast(deleteError);

			return;
		}

		openSuccessToast(
			Liferay.Util.sub(
				Liferay.Language.get('x-was-removed-successfully'),
				row.name
			)
		);

		onRemoved();
		onClose();
	};

	return (
		<ClayModal observer={observer} status="danger">
			<ClayModal.Header>
				{Liferay.Language.get('remove-data-mask')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p id="removeDataMaskReasonInstruction">
					{Liferay.Language.get(
						'provide-a-reason-for-removing-this-mask-from-the-profile'
					)}
				</p>

				<FieldBase
					id="removeDataMaskReason"
					label={Liferay.Language.get('delete-reason')}
					required
				>
					<ClayInput
						aria-describedby="removeDataMaskReasonInstruction"
						component="textarea"
						id="removeDataMaskReason"
						onChange={(event) => setReason(event.target.value)}
						required
						value={reason}
					/>
				</FieldBase>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={onClose}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={!reason.trim() || saving}
							displayType="danger"
							onClick={remove}
							type="button"
						>
							{Liferay.Language.get('remove')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
