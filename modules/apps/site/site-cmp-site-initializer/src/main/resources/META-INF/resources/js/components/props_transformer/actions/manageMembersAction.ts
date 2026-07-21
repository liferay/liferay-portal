/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openCMPModal} from '../../../utils/openCMPModal';
import ProjectMembersModal from '../../members/ProjectMembersModal';

export interface ManageMembersData {
	assetLibraryCreatorUserId: string;
	externalReferenceCode: string;
	filter?: string;
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
		filter,
		hasAssignMembersPermission,
		title,
	} = data;

	openCMPModal({
		contentComponent: () =>
			ProjectMembersModal({
				assetLibraryCreatorUserId,
				externalReferenceCode,
				filter,
				hasAssignMembersPermission,
			}),
		onClose: loadData,
		size: 'md',
		title,
	});
}
