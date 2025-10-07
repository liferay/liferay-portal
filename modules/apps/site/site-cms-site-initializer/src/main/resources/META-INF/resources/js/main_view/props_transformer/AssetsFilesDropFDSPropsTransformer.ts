/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IView} from '@liferay/frontend-data-set-web';

import AssetsFDSPropsTransformer, {
	AdditionalProps,
} from './AssetsFDSPropsTransformer';
import fileDropAction from './actions/fileDropAction';
import {MultipleFileUploaderData} from './actions/multipleFilesUploadAction';

const OBJECT_ENTRY_FOLDER_CLASS_NAME =
	'com.liferay.object.model.ObjectEntryFolder';

export default function AssetsFilesDropFDSPropsTransformer({
	additionalProps,
	creationMenu,
	itemsActions = [],
	views,
	...otherProps
}: {
	additionalProps: AdditionalProps & MultipleFileUploaderData;
	creationMenu: any;
	itemsActions?: any[];
	otherProps: any;
	views: IView[];
}) {
	const assetsData = AssetsFDSPropsTransformer({
		additionalProps,
		creationMenu,
		itemsActions,
		...otherProps,
		views,
	});

	return {
		...assetsData,
		fileDropSettings: {
			enabled: true,
			isDropTarget: ({item}: {item: any}) => {
				return item.entryClassName.includes(
					OBJECT_ENTRY_FOLDER_CLASS_NAME
				);
			},
			onFileDrop: (droppedFiles: any, dropTarget?: any) =>
				fileDropAction(additionalProps, droppedFiles, dropTarget),
		},
	};
}
