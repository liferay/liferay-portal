/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export enum AssetType {
	BasicContent = 'BASIC_CONTENT',
	Blog = 'BLOG',
	CustomStructure = 'CUSTOM_STRUCTURE',
	FileDefault = 'FILE_DEFAULT',
	FileCompressed = 'FILE_COMPRESSED',
	FileSpreadsheet = 'FILE_SPREADSHEET',
	FileImage = 'FILE_IMAGE',
	FileAudioVideo = 'FILE_AUDIO_VIDEO',
	FileVectorial = 'FILE_VECTORIAL',
	FileText = 'FILE_TEXT',
	FileCode = 'FILE_CODE',
}

export const AssetTypeIcons: Record<
	AssetType,
	{color: string; symbol: string}
> = {
	[AssetType.BasicContent]: {color: '#6B6C7E', symbol: 'forms'},
	[AssetType.Blog]: {color: '#458613', symbol: 'blogs'},
	[AssetType.CustomStructure]: {color: '#338FFF', symbol: 'web-content'},
	[AssetType.FileDefault]: {color: '#6B6C7E', symbol: 'document-default'},
	[AssetType.FileCompressed]: {
		color: '#6B6C7E',
		symbol: 'document-compressed',
	},
	[AssetType.FileSpreadsheet]: {color: '#24A892', symbol: 'document-table'},
	[AssetType.FileImage]: {color: '#FF4D4D', symbol: 'document-image'},
	[AssetType.FileAudioVideo]: {
		color: '#FF4D4D',
		symbol: 'document-multimedia',
	},
	[AssetType.FileVectorial]: {color: '#BF66FF', symbol: 'document-vectorial'},
	[AssetType.FileText]: {color: '#0099E6', symbol: 'document-text'},
	[AssetType.FileCode]: {color: '#FF4DB2', symbol: 'document-code'},
};
