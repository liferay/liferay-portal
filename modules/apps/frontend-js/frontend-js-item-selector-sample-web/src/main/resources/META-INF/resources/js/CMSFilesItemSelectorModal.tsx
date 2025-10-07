/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {IView} from '@liferay/frontend-data-set-web';
import {
	IItemSelectorModalProps,
	ItemSelectorModal,
} from '@liferay/frontend-js-item-selector-web';
import React, {useState} from 'react';
import {v4 as uuidv4} from 'uuid';

const OBJECT_ENTRY_FOLDER_CLASS_NAME =
	'com.liferay.object.model.ObjectEntryFolder';

const ROOT_URL = `${window.location.origin}${Liferay.ThemeDisplay.getPathContext()}/o/search/v1.0/search`;

const BASE_SEARCH_PARAMS = {
	currentURL: '/web/cms/files',
	emptySearch: 'true',
	nestedFields: 'description,embedded,file.thumbnailURL',
};

const CMS_ROOT_FILES_URL = `${ROOT_URL}?${new URLSearchParams({
	...BASE_SEARCH_PARAMS,
	filter: "cmsRoot eq true and cmsSection eq 'files' and status in (0, 2, 3)",
}).toString()}`;

function getCMSChildFolderURL(folderId: string) {
	return `${ROOT_URL}?${new URLSearchParams({
		...BASE_SEARCH_PARAMS,
		filter: `folderId eq ${folderId}`,
	}).toString()}`;
}

type CMSFile = {
	id: number;
	title: string;
};

function CMSFilesItemSelectorModal({
	fdsProps,
	...otherProps
}: Omit<
	IItemSelectorModalProps<CMSFile>,
	'itemTypeLabel' | 'fdsProps' | 'apiURL'
> & {
	fdsProps?: IItemSelectorModalProps<CMSFile>['fdsProps'];
}) {
	const [folderStructure, setFolderStructure] = useState<
		{folderId: string; folderName: string}[]
	>([]);
	const [url, setURL] = useState(CMS_ROOT_FILES_URL);

	function onChildFolderClick({
		folderId,
		folderName,
	}: {
		folderId: string;
		folderName: string;
	}) {
		setFolderStructure((prevStructure) => [
			...prevStructure,
			{folderId, folderName},
		]);

		setURL(getCMSChildFolderURL(folderId));
	}

	return (
		<ItemSelectorModal
			{...otherProps}
			apiURL={url}
			breadcrumbs={
				folderStructure.length
					? [
							{
								label: Liferay.Language.get('default'),
								onClick: () => {
									setURL(CMS_ROOT_FILES_URL);
									setFolderStructure([]);
								},
							},
							...folderStructure.map(
								({folderId, folderName}, index) => ({
									label: folderName,
									onClick: () => {
										setFolderStructure(
											(prevFolderStructure) =>
												prevFolderStructure.slice(
													0,
													index + 1
												)
										);

										setURL(getCMSChildFolderURL(folderId));
									},
								})
							),
						]
					: undefined
			}
			fdsProps={{
				pagination: {
					deltas: [{label: 20}, {label: 40}, {label: 60}],
					initialDelta: 20,
				},
				...fdsProps,
				customRenderers: {
					tableCell: [
						{
							component: ({itemData, value}) => {
								const {embedded, entryClassName} = itemData;

								return entryClassName ===
									OBJECT_ENTRY_FOLDER_CLASS_NAME ? (
									<ClayButton
										className="c-p-0"
										displayType="link"
										onClick={() => {
											onChildFolderClick({
												folderId: embedded.id,
												folderName: embedded.title,
											});
										}}
									>
										{value}
									</ClayButton>
								) : (
									value
								);
							},
							name: 'cmsFilesTitleCellRenderer',
							type: 'internal',
						},
					],
				},
				filters: [
					{
						apiURL: '/o/headless-asset-library/v1.0/asset-libraries',
						entityFieldType: 'collection',
						id: 'groupIds',
						itemKey: 'siteId',
						itemLabel: 'name',
						label: Liferay.Language.get('space'),
						multiple: true,
						type: 'selection',
					},
				],
				id: `itemSelectorModal-cms-${uuidv4()}`,
				views: [
					{
						contentRenderer: 'cards',
						label: Liferay.Language.get('cards'),
						name: 'cards',
						schema: {
							description: 'embedded.description',
							image: 'embedded.file.thumbnailURL',
							title: 'embedded.title',
						},
						setItemComponentProps: ({
							item,
							props,
						}: {
							item: any;
							props: any;
						}) => {
							if (
								item.entryClassName ===
								OBJECT_ENTRY_FOLDER_CLASS_NAME
							) {
								return {
									...props,
									onClick: () => {
										onChildFolderClick({
											folderId: item.embedded.id,
											folderName: item.embedded.title,
										});
									},
									onSelectChange: null,
									symbol: 'folder',
								};
							}

							const stickerProps = {
								className: 'file-icon-color-5',
								displayType: 'unstyled',
							};

							if (
								!item.embedded.file.mimeType.startsWith('image')
							) {
								return {
									...props,
									imgProps: null,
									stickerProps,
								};
							}

							return {
								...props,
								stickerProps,
							};
						},
						thumbnail: 'cards2',
					},
					{
						contentRenderer: 'table',
						label: Liferay.Language.get('table'),
						name: 'table',
						schema: {
							fields: [
								{
									contentRenderer:
										'cmsFilesTitleCellRenderer',
									fieldName: 'embedded.title',
									label: Liferay.Language.get('title'),
									sortable: false,
								},
								{
									fieldName: 'embedded.description',
									label: Liferay.Language.get('description'),
									sortable: false,
								},
								{
									fieldName: 'embedded.file.name',
									label: Liferay.Language.get('file-name'),
									sortable: false,
								},
								{
									fieldName: 'embedded.file.mimeType',
									label: Liferay.Language.get('type'),
									sortable: false,
								},
							],
						},
						thumbnail: 'table',
					},
				] as IView[],
			}}
			itemTypeLabel={Liferay.Language.get('files')}
			locator={{
				id: 'embedded.id',
				label: 'embedded.title',
				value: 'embedded.id',
			}}
		/>
	);
}

export default CMSFilesItemSelectorModal;
