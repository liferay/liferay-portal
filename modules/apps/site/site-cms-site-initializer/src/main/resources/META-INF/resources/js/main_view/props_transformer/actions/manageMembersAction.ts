/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';

import SpaceMembersModal from '../../spaces/SpaceMembersModal';

export interface ManageMembersData {
	assetLibraryCreatorUserId: string;
	externalReferenceCode: string;
	hasAssignMembersPermission: boolean;
	title: string;
}

export default function manageMembersAction(
	data: ManageMembersData,
	loadData?: () => void
) {
	const {
		assetLibraryCreatorUserId,
		externalReferenceCode,
		hasAssignMembersPermission,
		title,
	} = data;

	openModal({
		center: true,
		contentComponent: () =>
			SpaceMembersModal({
				assetLibraryCreatorUserId,
				externalReferenceCode,
				hasAssignMembersPermission,
			}),
		onClose: loadData,
		size: 'md',
		title,
	});
}
