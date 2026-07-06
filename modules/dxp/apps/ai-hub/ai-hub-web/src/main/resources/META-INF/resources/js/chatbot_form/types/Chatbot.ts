/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type AvatarReference = {
	externalReferenceCode: string;
};

export type AvatarUpload = {
	fileBase64: string;
	mimeType?: string;
	name: string;
};

export type Chatbot = {
	active: boolean;
	avatar?: AvatarReference | AvatarUpload | null | number;
	avatarFileName?: string;
	description: string;
	disclaimerMessage_i18n?: {[key: string]: string};
	externalReferenceCode: string;
	introMessage_i18n: {[key: string]: string};
	notificationMessage_i18n: {[key: string]: string};
	placeholderMessage_i18n: {[key: string]: string};
	r_accountToAIHubChatbots_accountEntryERC: string;
	title_i18n: {[key: string]: string};
};
