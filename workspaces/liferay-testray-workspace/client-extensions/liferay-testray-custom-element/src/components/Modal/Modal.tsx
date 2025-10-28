/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal from '@clayui/modal';
import React, {ReactElement, ReactNode} from 'react';

type ModalProps = {
	children: ReactNode;
	className?: string;
	first?: ReactElement;
	last?: ReactElement;
	observer: any;
	size?: 'full-screen' | 'lg' | 'sm';
	subtitle?: string;
	title?: string;
	visible: boolean;
};

const Modal: React.FC<ModalProps> = ({
	children,
	first,
	last,
	observer,
	size,
	subtitle,
	title,
	visible,
}) => {
	if (!visible) {
		return null;
	}

	return (
		<ClayModal observer={observer} size={size}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				<ClayModal.Title>{title}</ClayModal.Title>
			</ClayModal.Header>

			{subtitle && (
				<ClayModal.SubtitleSection>
					<ClayModal.Subtitle className="legend-text">
						{subtitle}
					</ClayModal.Subtitle>
				</ClayModal.SubtitleSection>
			)}

			<ClayModal.Body>{children}</ClayModal.Body>

			{first || (last && <ClayModal.Footer first={first} last={last} />)}
		</ClayModal>
	);
};

export default Modal;
