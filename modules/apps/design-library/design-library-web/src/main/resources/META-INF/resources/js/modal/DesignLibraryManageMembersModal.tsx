/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ManageMembersModal, MembersConfig} from 'frontend-js-components-web';
import React from 'react';

import AddMembersInput from '../components/members/AddMembersInput';

const CONFIG: MembersConfig = {
	defaultRoleExternalReferenceCode: 'L_DESIGN_LIBRARY_MEMBER',
	excludedRoleExternalReferenceCodes: [
		'L_ASSET_LIBRARY_ADMINISTRATOR',
		'L_ASSET_LIBRARY_CONNECTED_SITE_MEMBER',
		'L_ASSET_LIBRARY_CONTENT_REVIEWER',
		'L_ASSET_LIBRARY_MEMBER',
		'L_ASSET_LIBRARY_OWNER',
		'L_DESIGN_LIBRARY_OWNER',
	],
	messages: {
		addGroupError: Liferay.Language.get(
			'failed-to-add-group-x-to-design-library'
		),
		addGroupSuccess: Liferay.Language.get(
			'group-x-successfully-added-to-design-library'
		),
		addUserError: Liferay.Language.get(
			'failed-to-add-user-x-to-design-library'
		),
		addUserSuccess: Liferay.Language.get(
			'user-x-successfully-added-to-design-library'
		),
		removeGroupError: Liferay.Language.get(
			'unable-to-remove-group-x-from-design-library'
		),
		removeGroupSuccess: Liferay.Language.get(
			'group-x-successfully-removed-from-design-library'
		),
		removeUserError: Liferay.Language.get(
			'unable-to-remove-user-x-from-design-library'
		),
		removeUserSuccess: Liferay.Language.get(
			'user-x-successfully-removed-from-design-library'
		),
		updateGroupError: Liferay.Language.get(
			'unable-to-update-roles-for-group-x'
		),
		updateSuccess: Liferay.Language.get('x-role-was-successfully-updated'),
		updateUserError: Liferay.Language.get(
			'unable-to-update-roles-for-user-x'
		),
	},
	roleNames: {
		L_DESIGN_LIBRARY_ADMINISTRATOR: Liferay.Language.get(
			'design-library-administrator'
		),
		L_DESIGN_LIBRARY_CONTENT_REVIEWER: Liferay.Language.get(
			'design-library-content-reviewer'
		),
		L_DESIGN_LIBRARY_MEMBER: Liferay.Language.get('design-library-member'),
	},
};

export default function DesignLibraryManageMembersModal({
	externalReferenceCode,
	hasAssignMembersPermission,
	headerTitle,
	onChange,
	ownerId,
}: {
	externalReferenceCode: string;
	hasAssignMembersPermission: boolean;
	headerTitle: string;
	onChange?: () => void;
	ownerId: string;
}) {
	return (
		<ManageMembersModal
			config={CONFIG}
			emptyStateDescription={Liferay.Language.get(
				'add-members-to-this-design-library'
			)}
			externalReferenceCode={externalReferenceCode}
			hasAssignMembersPermission={hasAssignMembersPermission}
			headerTitle={headerTitle}
			onChange={onChange}
			ownerId={ownerId}
			renderAddMembersInput={(api) => <AddMembersInput {...api} />}
		/>
	);
}
