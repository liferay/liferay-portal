/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const getJiraIconImage = (issueType: string) => {
	const baseUrl = 'https://liferay.atlassian.net/';

	if (issueType === 'IMPEDIBUG') {
		return `${baseUrl}rest/api/2/universal_avatar/view/type/issuetype/avatar/10321?size=small`;
	}
	else if (issueType === 'INITIATIVE') {
		return `${baseUrl}rest/api/2/universal_avatar/view/type/issuetype/avatar/10308?size=medium`;
	}
	else if (issueType === 'TECHNICALTASK') {
		return `${baseUrl}rest/api/2/universal_avatar/view/type/issuetype/avatar/10300?size=medium`;
	}

	return `${baseUrl}images/icons/issuetypes/${issueType.toLowerCase()}.svg`;
};

export default getJiraIconImage;
