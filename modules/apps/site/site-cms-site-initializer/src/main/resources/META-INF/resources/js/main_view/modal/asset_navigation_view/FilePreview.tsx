/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import {DocumentPreviewer} from 'document-library-preview-document';
import {ImagePreviewer} from 'document-library-preview-image';
import {DLVideoIframe} from 'document-library-video';
import React from 'react';

import {IAssetFile} from '../../../common/types/AssetType';

export default function FilePreview({
	file: {
		alternativeText,
		link,
		metadata: {numberOfPages} = {},
		mimeType,
		name,
		previewURL,
		thumbnailURL,
	},
}: {
	file: IAssetFile;
}) {
	const params = new URLSearchParams(thumbnailURL);
	const hasDocumentPreview = numberOfPages && previewURL;
	const baseDocumentImageURL = new URL(previewURL, window.location.href);
	const hasImagePreview = params.has('imageThumbnail');
	const isVideo = mimeType?.startsWith('video/') && previewURL;

	return (
		<>
			{hasDocumentPreview ? (
				<DocumentPreviewer
					alt={alternativeText}
					baseImageURL={baseDocumentImageURL.toString()}
					totalPages={numberOfPages}
				/>
			) : hasImagePreview ? (
				<ImagePreviewer alt={name} imageURL={link.href} />
			) : isVideo ? (
				<DLVideoIframe videoPreviewURL={previewURL} />
			) : (
				<div className="bg-light d-flex h-100 justify-content-center w-100">
					<ClayEmptyState
						description={Liferay.Language.get(
							'hmm-looks-like-this-item-does-not-have-a-preview-we-can-show-you'
						)}
						imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/cms_empty_state_preview.svg`}
						title={Liferay.Language.get('no-preview-available')}
					/>
				</div>
			)}
		</>
	);
}
