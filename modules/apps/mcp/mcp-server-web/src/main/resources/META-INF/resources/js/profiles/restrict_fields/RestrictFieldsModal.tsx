/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import React from 'react';

interface RestrictFieldsModalProps {
	onClose: () => void;
	toolName: string;
}

export default function RestrictFieldsModal({
	onClose,
	toolName,
}: RestrictFieldsModalProps) {
	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Util.sub(
					Liferay.Language.get('x-colon-y'),
					Liferay.Language.get('restrict-fields'),
					toolName
				)}
			</ClayModal.Header>

			<ClayModal.Body />

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton displayType="primary" onClick={onClose}>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
