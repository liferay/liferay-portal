/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {openModal} from 'frontend-js-components-web';
import React from 'react';

import RoomShare from './RoomShare';

function RoomShareButton({
	canAssignAllRoles,
	readOnly = false,
	roomId,
}: {
	canAssignAllRoles: boolean;
	readOnly?: boolean;
	roomId: number;
}) {
	return (
		<ClayButtonWithIcon
			aria-label={Liferay.Language.get('share')}
			displayType="link"
			onClick={() =>
				openModal({
					containerProps: {
						className: '',
					},
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						RoomShare({
							canAssignAllRoles,
							closeModal,
							readOnly,
							roomId,
						}),
					size: 'lg',
				})
			}
			size="xs"
			symbol="share"
		/>
	);
}

export default RoomShareButton;
