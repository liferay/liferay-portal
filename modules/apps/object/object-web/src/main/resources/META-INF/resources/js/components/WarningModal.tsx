/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {Observer} from '@clayui/modal/lib/types';
import React from 'react';

export default function WarningModal({
	children,
	observer,
	onClose,
	title,
}: IProps) {
	return (
		<ClayModal center observer={observer} status="warning">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body>{children}</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="warning" onClick={onClose}>
							{Liferay.Language.get('done')}
						</ClayButton>
					</ClayButton.Group>
				}
			></ClayModal.Footer>
		</ClayModal>
	);
}

interface IProps {
	children?: React.ReactNode;
	observer: Observer;
	onClose: () => void;
	title: string;
}
