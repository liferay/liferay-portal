/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AddMembersInput} from '@liferay/site-cms-site-initializer';
import {ManageMembersModal} from 'frontend-js-components-web';
import React from 'react';

import {PROJECT_MEMBERS_CONFIG} from './projectMembersConfig';

export default function ProjectMembersModal({
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
			config={PROJECT_MEMBERS_CONFIG}
			emptyStateDescription={Liferay.Language.get(
				'add-members-to-this-project'
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
