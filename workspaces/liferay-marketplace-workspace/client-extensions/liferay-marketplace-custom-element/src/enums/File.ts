/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const ALLOWED_MIME_TYPES = {
	ALL: {
		'application/octet-stream': [],
	},
	JAR: {
		'application/java-archive': ['.jar'],
	},
	WAR: {
		'application/x-java-archive': ['.war'],
	},
	ZIP: {
		'application/zip': ['.zip'],
	},
};

export const DOCUMENT_FOLDER_PERMISSIONS = {
	ANYONE: 'Anyone',
	OWNER: 'Owner',
	SITE_MEMBERS: 'Members',
};

export const PUBLISH_APP_UPLOAD_MAX_FILES = 10;

export const PUBLISH_APP_UPLOAD_MAX_SIZE = 500_000_000;

export const ACCEPT_FILE_TYPES = {
	'image/gif': ['.gif'],
	'image/jpeg': ['.jpeg'],
	'image/jpg': ['.jpg'],
	'image/png': ['.png'],
};
