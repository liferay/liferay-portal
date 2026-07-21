/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ManageMembersModal} from 'frontend-js-components-web';
import React from 'react';

import AddMembersInput from '../../common/components/AddMembersInput';
import {SPACE_MEMBERS_CONFIG} from './spaceMembersConfig';

export default function SpaceMembersModal({
	assetLibraryCreatorUserId,
	externalReferenceCode,
	filter,
	hasAssignMembersPermission,
}: {
	assetLibraryCreatorUserId: string;
	externalReferenceCode: string;
	filter?: string;
	hasAssignMembersPermission: boolean;
}) {
	return (
		<ManageMembersModal
			config={SPACE_MEMBERS_CONFIG}
			emptyStateDescription={Liferay.Language.get(
				'add-members-to-this-space'
			)}
			externalReferenceCode={externalReferenceCode}
			filter={filter}
			hasAssignMembersPermission={hasAssignMembersPermission}
			headerTitle={Liferay.Language.get('all-members')}
			ownerId={assetLibraryCreatorUserId}
			renderAddMembersInput={(api) => <AddMembersInput {...api} />}
		/>
	);
}
