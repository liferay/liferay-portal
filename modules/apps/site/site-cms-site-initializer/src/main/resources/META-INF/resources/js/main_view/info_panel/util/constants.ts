/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const ASSET_TYPE = {
	BLOGS: 'blogs',
	CONTENTS: 'basic-web-contents',
	FILES: 'files',
	FOLDER: 'folder',
};

export const ASSET_TYPE_ERC = {
	BASIC_DOCUMENT: 'L_CMS_BASIC_DOCUMENT',
	BASIC_WEB_CONTENT: 'L_CMS_BASIC_WEB_CONTENT',
	BLOG: 'L_CMS_BLOG',
	EXTERNAL_VIDEO: 'L_CMS_EXTERNAL_VIDEO',
};

export const COPY = 'copy';
export const DELETE_VERSION = 'delete';
export const EXPIRE = 'expire';
export const L_CONTENTS = 'L_CONTENTS';
export const L_FILES = 'L_FILES';
export const RESTORE = 'restore';

export const VIEW_CONTENT_VERSION_URL = `${Liferay.ThemeDisplay.getPortalURL()}${Liferay.ThemeDisplay.getPathMain()}/cms`;
