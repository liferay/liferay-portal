/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal from '@clayui/modal';
import {Observer, Size} from '@clayui/modal/lib/types';
import React, {ReactNode} from 'react';

type BaseModalProps = {
	children: ReactNode;
	observer: Observer;
	open: boolean;
	size?: Size;
	title?: string;
};

function BaseModal({
	children,
	observer,
	open,
	size = 'full-screen',
	title = Liferay.Language.get('add-from-marketplace'),
}: BaseModalProps) {
	if (!open) {
		return null;
	}

	return (
		<ClayModal
			center
			className="marketplace-modal"
			observer={observer}
			size={size}
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body className="m-0 p-0">{children}</ClayModal.Body>
		</ClayModal>
	);
}

export default BaseModal;
