/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openCMSModal} from '../../../common/utils/openCMSModal';
import SpaceMembersModal from '../../spaces/SpaceMembersModal';

export interface ManageMembersData {
	assetLibraryCreatorUserId: string;
	cmpProjectObjectEntryId?: number;
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

	openCMSModal({
		contentComponent: () =>
			SpaceMembersModal({
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
