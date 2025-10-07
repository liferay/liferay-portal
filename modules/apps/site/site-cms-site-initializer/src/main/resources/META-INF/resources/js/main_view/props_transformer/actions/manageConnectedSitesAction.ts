/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';

import SpaceConnectedSitesModal from '../../spaces/SpaceConnectedSitesModal';

export interface ManageConnectedSitesData {
	externalReferenceCode: string;
	hasConnectSitesPermission: boolean;
}

export default function manageConnectedSitesAction(
	data: ManageConnectedSitesData,
	loadData?: () => void
) {
	openModal({
		contentComponent: () => SpaceConnectedSitesModal(data),
		onClose: loadData,
		size: 'md',
	});
}
