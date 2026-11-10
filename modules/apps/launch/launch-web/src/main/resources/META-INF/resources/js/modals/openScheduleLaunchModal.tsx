/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import React from 'react';

import ScheduleLaunchModal, {
	ScheduleLaunchModalProps,
} from '../components/ScheduleLaunchModal';

export default function openScheduleLaunchModal(
	props: Omit<ScheduleLaunchModalProps, 'closeModal'>
) {
	openModal({
		contentComponent: ({closeModal}: {closeModal: () => void}) => (
			<ScheduleLaunchModal {...props} closeModal={closeModal} />
		),
		disableAutoClose: true,
		size: 'md',
	});
}
