/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import React, {useEffect, useState} from 'react';

export default function ConfigureAIModal({portletNamespace}) {
	const [showModal, setShowModal] = useState();

	const handleOnClose = () => {
		setShowModal(false);
	};

	const {observer, onClose} = useModal({
		onClose: handleOnClose,
	});

	useEffect(() => {
		const bridgeComponentId = `${portletNamespace}ConfigueAIModal`;

		if (!Liferay.component(bridgeComponentId)) {
			Liferay.component(
				bridgeComponentId,
				{
					open: () => {
						setShowModal(true);
					},
				},
				{
					destroyOnNavigate: true,
				}
			);
		}

		return () => {
			Liferay.destroyComponent(bridgeComponentId);
		};
	}, [portletNamespace]);

	return (
		<>
			{showModal && (
				<ClayModal observer={observer} status="info">
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('configure-openai')}
					</ClayModal.Header>

					<ClayModal.Body>
						<p className="text-secondary">
							{Liferay.Language.get(
								'authentication-is-needed-to-use-this-feature.-contact-your-administrator-to-add-an-api-key-in-instance-or-site-settings'
							)}
						</p>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton displayType="primary" onClick={onClose}>
								{Liferay.Language.get('ok')}
							</ClayButton>
						}
					/>
				</ClayModal>
			)}
		</>
	);
}
