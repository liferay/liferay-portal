/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import React from 'react';

export type ModalStatus = 'warning' | 'danger';

interface ConfirmModalProps {
	body: string;
	confirmDisplayType?: 'primary' | 'danger';
	confirmLabel: string;
	onCancel: () => void;
	onConfirm: () => void;
	status: ModalStatus;
	title: string;
}

export function ConfirmModal({
	body,
	confirmDisplayType = 'primary',
	confirmLabel,
	onCancel,
	onConfirm,
	status,
	title,
}: ConfirmModalProps) {
	const {observer, onClose} = useModal({onClose: onCancel});

	return (
		<ClayModal observer={observer} status={status}>
			<ClayModal.Header>{title}</ClayModal.Header>

			<ClayModal.Body>
				<p className="mb-0">{body}</p>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType={confirmDisplayType}
							onClick={onConfirm}
						>
							{confirmLabel}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
