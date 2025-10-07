/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import React from 'react';

import '../../../css/components/AssetIcon.scss';

export enum MimeTypes {
	BasicWebContent = 'basic-web-content',
	Blog = 'blog',
	CustomStructure = 'custom-structure',
	DocumentCode = 'document-code',
	DocumentCompressed = 'document-compressed',
	DocumentDefault = 'document-default',
	DocumentImage = 'document-image',
	DocumentMultimedia = 'document-multimedia',
	DocumentPresentation = 'document-presentation',
	DocumentTable = 'document-table',
	DocumentText = 'document-text',
	DocumentVector = 'document-vector',
	Folder = 'folder',
	KnowledgeBase = 'knowledge-base',
}

function getAssetIcon(mimeType: MimeTypes) {
	switch (mimeType) {
		case MimeTypes.DocumentCode:
			return {
				className: 'cms-asset-icon-document-code',
				icon: 'code',
			};
		case MimeTypes.DocumentCompressed:
			return {
				className: 'cms-asset-icon-document-compressed',
				icon: 'document-compressed',
			};
		case MimeTypes.DocumentPresentation:
			return {
				className: 'cms-asset-icon-document-presentation',
				icon: 'document-presentation',
			};
		case MimeTypes.DocumentTable:
			return {
				className: 'cms-asset-icon-document-table',
				icon: 'document-table',
			};
		case MimeTypes.DocumentText:
			return {
				className: 'cms-asset-icon-document-text',
				icon: 'document-text',
			};
		case MimeTypes.DocumentVector:
			return {
				className: 'cms-asset-icon-document-vector',
				icon: 'document-vector',
			};
		case MimeTypes.DocumentImage:
			return {
				className: 'cms-asset-icon-document-image',
				icon: 'document-image',
			};
		case MimeTypes.DocumentMultimedia:
			return {
				className: 'cms-asset-icon-document-multimedia',
				icon: 'document-multimedia',
			};
		case MimeTypes.DocumentDefault:
			return {
				className: 'cms-asset-icon-document-default',
				icon: 'document-default',
			};
		case MimeTypes.BasicWebContent:
			return {
				className: 'cms-asset-icon-basic-content',
				icon: 'forms',
			};
		case MimeTypes.Blog:
			return {
				className: 'cms-asset-icon-blog',
				icon: 'blogs',
			};
		case MimeTypes.KnowledgeBase:
			return {
				className: 'cms-asset-icon-knowledge-base',
				icon: 'wiki',
			};
		case MimeTypes.CustomStructure:
			return {
				className: 'cms-asset-icon-custom-structure',
				icon: 'web-content',
			};
		case MimeTypes.Folder: {
			return {
				className: 'cms-asset-icon-folder',
				icon: 'folder',
			};
		}
		default:
			return {
				className: 'file-icon-color-0',
				icon: 'document-default',
			};
	}
}

interface IAssetIconProps {
	mimeType: MimeTypes;
}

const AssetIcon: React.FC<IAssetIconProps> = ({mimeType}) => {
	const icon = getAssetIcon(mimeType as MimeTypes);

	return (
		<ClaySticker className={icon.className} displayType="dark">
			<ClayIcon symbol={icon.icon} />
		</ClaySticker>
	);
};

export {AssetIcon};
