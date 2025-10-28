/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import React, {useEffect, useState} from 'react';

interface ModalDeletionNotAllowedProps {
	content: React.ReactNode;
	onModalClose: () => void;
}

function ModalDeletionNotAllowed({
	content,
	onModalClose,
}: ModalDeletionNotAllowedProps) {
	const [bodyContent, setBodyContent] = useState<React.ReactNode>(content);
	const [visible, setVisible] = useState(false);

	const {observer, onClose} = useModal({
		onClose: () => {
			onModalClose ? onModalClose() : setVisible(false);
		},
	});

	useEffect(() => {
		const openModal = ({contentLiferayFire = <></>}) => {
			setVisible(true);
			setBodyContent(contentLiferayFire);
		};

		Liferay.on('openModalDeletionNotAllowed', openModal);

		return () =>
			Liferay.detach(
				'openModalDeletionNotAllowed',
				openModal as () => void
			);
	}, []);

	return (
		<>
			{(visible || !!content) && (
				<ClayModal center observer={observer} status="warning">
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('deletion-not-allowed')}
					</ClayModal.Header>

					<ClayModal.Body>{bodyContent}</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group key={1} spaced>
								<ClayButton
									displayType="warning"
									onClick={() => onClose()}
								>
									{Liferay.Language.get('done')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayModal>
			)}
		</>
	);
}

export default ModalDeletionNotAllowed;
