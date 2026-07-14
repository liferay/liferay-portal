/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import createAssetAction from './createAssetAction';
import createFolderAction from './createFolderAction';
import generateContentWithAIAction from './generateContentWithAIAction';
import generateImageWithAIAction from './generateImageWithAIAction';
import importTranslationAction from './importTranslationAction';
import multipleFilesUploadAction from './multipleFilesUploadAction';
import selectAssetsAction from './selectAssetsAction';

const ACTIONS = {
	createAsset: createAssetAction,
	createFolder: createFolderAction,
	generateContentWithAI: generateContentWithAIAction,
	generateImageWithAI: generateImageWithAIAction,
	importTranslation: importTranslationAction,
	selectAssets: selectAssetsAction,
	uploadMultipleFiles: multipleFilesUploadAction,
};

export default ACTIONS;
