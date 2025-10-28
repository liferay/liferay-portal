/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

type AssetType = {
	required: boolean;
	subtype?:
		| 'AllAssetSubtypes'
		| 'Basic Web Content'
		| 'Basic Document'
		| 'External Video Shortcut'
		| 'Google Drive Shortcut';
	type:
		| 'AllAssetTypes'
		| 'Web Content Feed'
		| 'WikiPage'
		| 'Product'
		| 'Product Link'
		| 'Microblogs Entry'
		| 'BlogPosting'
		| 'WebSite'
		| 'Bookmarks Entry'
		| 'StructuredContent'
		| 'Calendar Event'
		| 'Document'
		| 'UserAccount'
		| 'Translation'
		| 'WebPage'
		| 'Message Boards Message'
		| 'KnowledgeBaseArticle';
};
