/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal, {useModal} from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

export function InstallFragmentModalBody() {
	return (
		<>
			<ClayLoadingIndicator
				className="mb-4 mt-2"
				displayType="primary"
				shape="squares"
				size="md"
			/>

			<div className="d-flex flex-column ml-4 mr-4 text-center text-secondary">
				<span>
					{Liferay.Language.get(
						'the-installation-process-is-ongoing-and-may-take-some-time'
					)}
				</span>

				<span>
					{Liferay.Language.get(
						'closing-the-window-will-not-cancel-the-process'
					)}
				</span>
			</div>
		</>
	);
}

interface InstallFragmentModalProps {
	name: string;
	onCloseModal?: () => void;
}

export default function InstallFragmentModal({
	name,
	onCloseModal = () => {},
}: InstallFragmentModalProps) {
	const [visible, setVisible] = useState(true);

	const {observer} = useModal({
		onClose: () => {
			setVisible(false);
			onCloseModal();
		},
	});

	return visible ? (
		<ClayModal className="modal-dialog-centered" observer={observer}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{sub(Liferay.Language.get('installing-x'), name)}
			</ClayModal.Header>

			<ClayModal.Body>
				<InstallFragmentModalBody />
			</ClayModal.Body>
		</ClayModal>
	) : null;
}
