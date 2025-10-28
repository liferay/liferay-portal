/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayCheckbox} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {createPortletURL, fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

export default function ChangeTrackingNotifications({
	portletNamespace,
	saveDisplayPreferenceURL,
}) {
	const [popoverCheckbox, setPopoverCheckbox] = useState(true);
	const [visible, setVisible] = useState(false);

	const componentId = `${portletNamespace}CTNotifications`;

	const {observer, onClose} = useModal({
		onClose: () => {
			setVisible(false);
		},
	});

	const savePortalPreferences = (key, value) => {
		const portletURL = createPortletURL(saveDisplayPreferenceURL, {
			key,
			value,
		});

		fetch(portletURL).then((response) => {
			if (response.ok) {
				window.location.reload();
			}
		});
	};

	useEffect(() => {
		Liferay.component(
			componentId,
			{
				open: () => {
					setVisible(true);
				},
			},
			{
				destroyOnNavigate: true,
			}
		);

		return () => Liferay.destroyComponent(componentId);
	}, [componentId, setVisible]);

	return visible ? (
		<div>
			<ClayModal observer={observer}>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{Liferay.Language.get('notifications')}
				</ClayModal.Header>

				<ClayModal.Body>
					<ClayCheckbox
						checked={popoverCheckbox}
						label={Liferay.Language.get(
							'hide-warning-when-changing-contexts'
						)}
						onChange={() => {
							setPopoverCheckbox(!popoverCheckbox);
						}}
						style={{marginLeft: '10px'}}
					/>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={() => {
									onClose(false);
								}}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								onClick={() => {
									if (!popoverCheckbox) {
										savePortalPreferences(
											'hideContextChangeWarningDuration',
											0
										);
									}
								}}
							>
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayModal>
		</div>
	) : null;
}
