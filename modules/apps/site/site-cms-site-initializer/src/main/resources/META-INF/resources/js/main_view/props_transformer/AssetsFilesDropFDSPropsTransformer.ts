/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IBulkActionItem, IView} from '@liferay/frontend-data-set-web';

import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../common/utils/constants';
import {allFDSAtom} from '../quick_filters/atoms';
import AssetsFDSPropsTransformer, {
	AdditionalProps,
} from './AssetsFDSPropsTransformer';
import fileDropAction from './actions/fileDropAction';
import {MultipleFileUploaderData} from './actions/multipleFilesUploadAction';

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

	const isCreationMenuEmpty = !creationMenu?.primaryItems?.length;

	const fileDropSettings = {
		enabled: !isCreationMenuEmpty,
		isDropTarget: ({item}: {item: any}) => {
			return item.entryClassName.includes(OBJECT_ENTRY_FOLDER_CLASS_NAME);
		},
		onFileDrop: (droppedFiles: any, dropTarget?: any) => {
			if (isCreationMenuEmpty) {
				return;
			}

			return fileDropAction(additionalProps, droppedFiles, dropTarget);
		},
	};

	return {
		...assetsData,
		atom: allFDSAtom,
		bulkActions: (assetsData.bulkActions || []).map(
			(action: IBulkActionItem) => {
				if (action?.data?.id !== 'find-and-replace') {
					return action;
				}

				return {
					...action,
					isVisible: ({
						selectedItems,
					}: {
						selectedItems?: Array<any>;
					} = {}): boolean => {
						return (
							selectedItems?.every(
								(item: any) => item?.actions?.update
							) ?? false
						);
					},
				};
			}
		),
		fileDropSettings,
		snapshotsEnabled: true,

		// Custom views do not receive the top-level drop settings, so they are
		// attached to the gallery view.

		views: (assetsData.views || []).map((view: IView) =>
			view.name === 'gallery' ? {...view, fileDropSettings} : view
		),
	};
}
