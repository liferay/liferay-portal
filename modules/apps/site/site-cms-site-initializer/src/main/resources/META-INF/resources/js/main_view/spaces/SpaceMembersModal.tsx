/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal from '@clayui/modal';
import React from 'react';

import {SpaceMembersWithList} from './SpaceMembersWithList';

export default function SpaceMembersModal({
	assetLibraryCreatorUserId,
	externalReferenceCode,
	hasAssignMembersPermission,
}: {
	assetLibraryCreatorUserId: string;
	externalReferenceCode: string;
	hasAssignMembersPermission: boolean;
}) {
	return (
		<div>
			<ClayModal.Header>
				{Liferay.Language.get('all-members')}
			</ClayModal.Header>

			<ClayModal.Body>
				<SpaceMembersWithList
					assetLibraryCreatorUserId={assetLibraryCreatorUserId}
					externalReferenceCode={externalReferenceCode}
					hasAssignMembersPermission={hasAssignMembersPermission}
				/>
			</ClayModal.Body>
		</div>
	);
}
