/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const config = {
	name: 'document-library-web.upgrade-documents-and-media-file-size-limit-73101',
	testDir:
		'tests/document-library-web/upgrade-documents-and-media-file-size-limit',
	timeout: 480 * 1000,
	use: {
		maxFileSize: '100 KB',
		testIdAttribute: 'data-qa-id',
	},
};
