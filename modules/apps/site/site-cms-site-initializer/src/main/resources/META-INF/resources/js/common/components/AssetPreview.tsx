/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import {replaceTokens} from '@liferay/frontend-data-set-web';
import React from 'react';

import {ISearchAssetObjectEntry} from '../types/AssetType';
import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../utils/constants';
import ContentPreview from './ContentPreview';
import FilePreview from './FilePreview';
import FolderPreview from './FolderPreview';
import VideoPreview from './VideoPreview';

interface AssetPreviewProps {
	item: ISearchAssetObjectEntry;
	showContentPreview?: boolean;
	url: string;
}

export default function AssetPreview(props: AssetPreviewProps) {
	const {item, showContentPreview = true, url} = props;

	if (item.embedded?.file) {
		return <FilePreview file={item.embedded?.file} />;
	}

	if (item.embedded?.videoURL) {
		return <VideoPreview previewURL={item.embedded?.videoURL} />;
	}

	if (item.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME) {
		return (
			<FolderPreview
				filesLength={item.embedded?.numberOfObjectEntries ?? 0}
				name={item.embedded?.title ?? ''}
				subfoldersLength={
					item.embedded?.numberOfObjectEntryFolders ?? 0
				}
			/>
		);
	}

	if (showContentPreview) {
		return <ContentPreview url={replaceTokens(url, item)} />;
	}

	return (
		<div className="bg-light d-flex h-100 justify-content-center w-100">
			<ClayEmptyState
				description={Liferay.Language.get(
					'hmm-looks-like-this-item-does-not-have-a-preview-we-can-show-you'
				)}
				imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/cms_empty_state_preview.svg`}
				title={Liferay.Language.get('no-preview-available')}
			/>
		</div>
	);
}
