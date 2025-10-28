/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal from '@clayui/modal';
import {Observer, Size, Status} from '@clayui/modal/lib/types';
import {ReactElement, ReactNode} from 'react';

type ModalProps = {
	children: ReactNode;
	className?: string;
	disableAutoClose?: boolean;
	first?: ReactElement;
	last?: ReactElement;
	observer: Observer;
	size: Size;
	status?: Status;
	subtitle?: string;
	title?: string;
	visible: boolean;
};

const Modal = ({
	children,
	className,
	disableAutoClose = false,
	first,
	last,
	observer,
	size,
	status,
	subtitle,
	title,
	visible,
}: ModalProps) => {
	if (!visible) {
		return null;
	}

	return (
		<ClayModal
			center
			className={className}
			disableAutoClose={disableAutoClose}
			observer={observer}
			size={size}
			status={status}
		>
			{title && (
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{title}
				</ClayModal.Header>
			)}

			{subtitle && (
				<ClayModal.SubtitleSection>
					<ClayModal.Subtitle className="legend-text">
						{subtitle}
					</ClayModal.Subtitle>
				</ClayModal.SubtitleSection>
			)}

			<ClayModal.Body>{children}</ClayModal.Body>

			{(first || last) && <ClayModal.Footer first={first} last={last} />}
		</ClayModal>
	);
};

export default Modal;
