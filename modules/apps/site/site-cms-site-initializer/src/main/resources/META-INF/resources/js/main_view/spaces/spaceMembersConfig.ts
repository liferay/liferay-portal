/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MembersConfig} from 'frontend-js-components-web';

export const SPACE_MEMBERS_CONFIG: MembersConfig = {
	defaultRoleExternalReferenceCode: 'L_ASSET_LIBRARY_MEMBER',
	excludedRoleExternalReferenceCodes: [
		'L_ASSET_LIBRARY_CONNECTED_SITE_MEMBER',
		'L_ASSET_LIBRARY_OWNER',
		'L_DESIGN_LIBRARY_ADMINISTRATOR',
		'L_DESIGN_LIBRARY_CONTENT_REVIEWER',
		'L_DESIGN_LIBRARY_MEMBER',
		'L_DESIGN_LIBRARY_OWNER',
	],
	messages: {
		addGroupError: Liferay.Language.get('failed-to-add-group-x-to-space'),
		addGroupSuccess: Liferay.Language.get(
			'group-x-successfully-added-to-space'
		),
		addUserError: Liferay.Language.get('failed-to-add-user-x-to-space'),
		addUserSuccess: Liferay.Language.get(
			'user-x-successfully-added-to-space'
		),
		removeGroupError: Liferay.Language.get(
			'unable-to-remove-group-x-from-space'
		),
		removeGroupSuccess: Liferay.Language.get(
			'group-x-successfully-removed-from-space'
		),
		removeUserError: Liferay.Language.get(
			'unable-to-remove-user-x-from-space'
		),
		removeUserSuccess: Liferay.Language.get(
			'user-x-successfully-removed-from-space'
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
		L_ASSET_LIBRARY_ADMINISTRATOR: Liferay.Language.get(
			'space-administrator'
		),
		L_ASSET_LIBRARY_CONTENT_REVIEWER: Liferay.Language.get(
			'space-content-reviewer'
		),
		L_ASSET_LIBRARY_MEMBER: Liferay.Language.get('space-member'),
	},
};
