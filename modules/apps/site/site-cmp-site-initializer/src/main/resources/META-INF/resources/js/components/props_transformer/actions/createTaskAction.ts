/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {navigate} from 'frontend-js-web';

import {openCMPModal} from '../../../utils/openCMPModal';
import SelectProjectModalContent from '../../modal/SelectProjectModalContent';

export default function createTaskAction({
	addProjectURL,
	addTaskURL,
	projectObjectDefinitionId,
	redirect,
}: {
	addProjectURL: string;
	addTaskURL: string;
	projectObjectDefinitionId: number;
	redirect?: string;
}) {
	if (redirect) {
		const url = new URL(redirect);

		navigate(url.pathname + url.search);

		return;
	}

	openCMPModal({
		center: true,
		contentComponent: ({closeModal}: {closeModal: () => void}) =>
			SelectProjectModalContent({
				addProjectURL,
				addTaskURL,
				closeModal,
				projectObjectDefinitionId,
			}),
		size: 'md',
	});
}
