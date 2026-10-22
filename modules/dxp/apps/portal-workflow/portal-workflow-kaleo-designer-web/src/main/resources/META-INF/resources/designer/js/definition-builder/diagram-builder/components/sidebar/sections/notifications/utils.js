/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const getRecipientType = (assignments) => {
	if (!assignments || !assignments.assignmentType) {
		return 'assetCreator';
	}

	return assignments.assignmentType?.[0] === 'user' &&
		!Object.keys(assignments).includes('emailAddress') &&
		!Object.keys(assignments).includes('userId')
		? 'assetCreator'
		: assignments.assignmentType?.[0] === 'roleId'
			? 'role'
			: assignments.assignmentType?.[0];
};

export {getRecipientType};
