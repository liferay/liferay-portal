/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MembersConfig} from 'frontend-js-components-web';

export const PROJECT_MEMBERS_CONFIG: MembersConfig = {
	defaultRoleExternalReferenceCode: 'L_PROJECT_MEMBER',
	excludedRoleExternalReferenceCodes: [
		'L_ASSET_LIBRARY_ADMINISTRATOR',
		'L_ASSET_LIBRARY_CONNECTED_SITE_MEMBER',
		'L_ASSET_LIBRARY_CONTENT_REVIEWER',
		'L_ASSET_LIBRARY_MEMBER',
		'L_ASSET_LIBRARY_OWNER',
		'L_DESIGN_LIBRARY_ADMINISTRATOR',
		'L_DESIGN_LIBRARY_CONTENT_REVIEWER',
		'L_DESIGN_LIBRARY_MEMBER',
		'L_DESIGN_LIBRARY_OWNER',
	],
	messages: {
		addGroupError: Liferay.Language.get('failed-to-add-group-x-to-project'),
		addGroupSuccess: Liferay.Language.get(
			'group-x-successfully-added-to-project'
		),
		addUserError: Liferay.Language.get('failed-to-add-user-x-to-project'),
		addUserSuccess: Liferay.Language.get(
			'user-x-successfully-added-to-project'
		),
		removeGroupError: Liferay.Language.get(
			'unable-to-remove-group-x-from-project'
		),
		removeGroupSuccess: Liferay.Language.get(
			'group-x-successfully-removed-from-project'
		),
		removeUserError: Liferay.Language.get(
			'unable-to-remove-user-x-from-project'
		),
		removeUserSuccess: Liferay.Language.get(
			'user-x-successfully-removed-from-project'
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
		L_PROJECT_CONTRIBUTOR: Liferay.Language.get('project-contributor'),
		L_PROJECT_MANAGER: Liferay.Language.get('project-manager'),
		L_PROJECT_MEMBER: Liferay.Language.get('project-member'),
	},
};
