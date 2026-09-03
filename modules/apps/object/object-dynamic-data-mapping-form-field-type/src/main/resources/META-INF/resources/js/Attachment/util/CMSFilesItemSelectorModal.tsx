/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {IView} from '@liferay/frontend-data-set-web';
import {
	CMSFileUploaderComponent,
	IItemSelectorModalProps,
	ItemSelectorModal,
	getCMSItemSelectorFilters,
	getCMSItemSelectorGroupedFilters,
} from '@liferay/frontend-js-item-selector-web';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';
import {v4 as uuidv4} from 'uuid';

import {getSpaceAvatarSrc} from './getSpaceAvatarSrc';

const BASE_SEARCH_PARAMS = {
	currentURL: '/web/cms/files',
	emptySearch: 'true',
	nestedFields:
		'description,embedded,file.thumbnailURL,file.mimeType,file.name',
};

const OBJECT_ENTRY_FOLDER_CLASS_NAME =
	'com.liferay.object.model.ObjectEntryFolder';

const ROOT_URL = `${window.location.origin}${Liferay.ThemeDisplay.getPathContext()}/o/search/v1.0/search`;

const SPACES_API_URL = '/o/headless-asset-library/v1.0/asset-libraries';

const getSpaceId = (item: any) => {
	return item.siteId || item.groupId || item.group?.id || item.id;
};

function getNotExpiredFilter() {
	return `(dateExpiration eq null or dateExpiration gt ${new Date().toISOString()})`;
}

function getSpaceRootFilesURL(groupId: string) {
	if (!groupId || groupId === 'undefined') {
		return '';
	}

	return `${ROOT_URL}?${new URLSearchParams({
		...BASE_SEARCH_PARAMS,
		filter: `cmsRoot eq true and cmsSection eq 'files' and rootDescendantNode eq false and status in (0) and scopeGroupId eq ${groupId} and ${getNotExpiredFilter()}`,
	}).toString()}`;
}

function getCMSChildFolderURL(folderId: string) {
	return `${ROOT_URL}?${new URLSearchParams({
		...BASE_SEARCH_PARAMS,
		filter: `folderId eq ${folderId} and status in (0) and ${getNotExpiredFilter()}`,
	}).toString()}`;
}

export type CMSFile = {
	embedded: {
		description?: string;
		file: {
			fileURL: string;
			id: number;
			mimeType?: string;
			name?: string;
			thumbnailURL?: string;
		};
		id: number;
		title: string;
	};
	entryClassName?: string;
	id: number;
	title: string;
};

type Space = {
	id: string;
	name: string;
};

function CMSFilesItemSelectorModal({
	fdsProps,
	items,
	open,
	...otherProps
}: Omit<
	IItemSelectorModalProps<CMSFile>,
	| 'itemTypeLabel'
	| 'fdsProps'
	| 'apiURL'
	| 'filesUploaderComponent'
	| 'groupId'
> & {
	fdsProps?: IItemSelectorModalProps<any>['fdsProps'];
}) {
	const wasOpenRef = useRef(open);

	const [folderStructure, setFolderStructure] = useState<
		{folderId: string; folderName: string}[]
	>([]);
	const [selectedSpace, setSelectedSpace] = useState<Space | null>(null);
	const [url, setURL] = useState(SPACES_API_URL);

	const isSpaceMode = !selectedSpace;

	const modalId = useMemo(
		() =>
			`itemSelectorModal-cms-${isSpaceMode ? 'space' : 'files'}-${uuidv4()}`,
		[isSpaceMode]
	);

	useEffect(() => {
		const wasOpen = wasOpenRef.current;

		wasOpenRef.current = open;

		if (!open || wasOpen) {
			return;
		}

		if (!items.length) {
			setSelectedSpace(null);
			setFolderStructure([]);
			setURL(SPACES_API_URL);
		}
	}, [open, items]);

	const onChildFolderClick = useCallback(
		({folderId, folderName}: {folderId: string; folderName: string}) => {
			setFolderStructure((prevStructure) => [
				...prevStructure,
				{folderId, folderName},
			]);

			setURL(getCMSChildFolderURL(folderId));
		},
		[]
	);

	const onResetToSpaces = useCallback(() => {
		setSelectedSpace(null);
		setFolderStructure([]);
		setURL(SPACES_API_URL);
	}, []);

	const onSpaceClick = useCallback((space: Space) => {
		setSelectedSpace(space);
		setURL(getSpaceRootFilesURL(space.id));
		setFolderStructure([]);
	}, []);

	const breadcrumbs = useMemo(() => {
		if (!selectedSpace) {
			return undefined;
		}

		const crumbs: Array<{
			active?: boolean;
			label: string;
			onClick?: () => void;
		}> = [
			{
				label: Liferay.Language.get('spaces'),
				onClick: onResetToSpaces,
			},
		];

		if (folderStructure.length) {
			crumbs.push({
				label: selectedSpace.name,
				onClick: () => {
					setURL(getSpaceRootFilesURL(selectedSpace.id));
					setFolderStructure([]);
				},
			});
		}
		else {
			crumbs.push({
				active: true,
				label: selectedSpace.name,
			});
		}

		folderStructure.forEach(({folderId, folderName}, index) => {
			const isLast = index === folderStructure.length - 1;

			crumbs.push({
				active: isLast,
				label: folderName,
				onClick: isLast
					? undefined
					: () => {
							setFolderStructure((prev) =>
								prev.slice(0, index + 1)
							);
							setURL(getCMSChildFolderURL(folderId));
						},
			});
		});

		return crumbs;
	}, [folderStructure, onResetToSpaces, selectedSpace]);

	const currentViews = useMemo(
		() =>
			(!selectedSpace
				? [
						{
							contentRenderer: 'cards',
							label: Liferay.Language.get('cards'),
							name: 'cards',
							schema: {
								description: 'description',
								title: 'name',
							},
							setItemComponentProps: ({item, props}: any) => ({
								...props,
								flushHorizontal: true,
								flushVertical: true,
								imgProps: {
									alt: item.name,
									src: getSpaceAvatarSrc(
										item.name?.charAt(0).toUpperCase() ??
											'',
										item.settings?.logoColor || 'outline-0'
									),
								},
								onClick: () => {
									onSpaceClick({
										id: getSpaceId(item),
										name: item.name,
									});
								},
								onSelectChange: null,
							}),
							thumbnail: 'cards2',
						},
					]
				: [
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
									!item.embedded?.file?.mimeType?.startsWith(
										'image'
									)
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
										label: Liferay.Language.get(
											'description'
										),
										sortable: false,
									},
									{
										fieldName: 'embedded.file.name',
										label: Liferay.Language.get(
											'file-name'
										),
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
					]) as IView[],
		[onChildFolderClick, onSpaceClick, selectedSpace]
	);

	return (
		<ItemSelectorModal
			{...otherProps}
			apiURL={url}
			breadcrumbs={breadcrumbs}
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
								if (selectedSpace) {
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
								}

								return (
									<ClayButton
										className="c-p-0"
										displayType="link"
										onClick={() => {
											onSpaceClick({
												id: getSpaceId(itemData),
												name: itemData.name,
											});
										}}
									>
										{value}
									</ClayButton>
								);
							},
							name: 'cmsFilesTitleCellRenderer',
							type: 'internal',
						},
					],
				},
				emptyState: {
					description: Liferay.Language.get(
						'drag-and-drop-your-files-or'
					),
				},
				...(selectedSpace
					? {
							filters: getCMSItemSelectorFilters(
								Liferay.ThemeDisplay.getSiteGroupId()
							),
							groupedFilters: getCMSItemSelectorGroupedFilters(),
						}
					: {}),
				id: modalId,
				views: currentViews,
			}}
			filesUploaderComponent={CMSFileUploaderComponent}
			groupId={selectedSpace ? Number(selectedSpace.id) : undefined}
			itemTypeLabel={Liferay.Language.get(
				selectedSpace ? 'files' : 'space'
			)}
			items={items}
			key={modalId}
			locator={{
				id: selectedSpace ? 'embedded.id' : 'id',
				label: selectedSpace ? 'embedded.title' : 'name',
				value: selectedSpace ? 'embedded.id' : 'id',
			}}
			multiSelect={false}
			open={open}
		/>
	);
}

export default CMSFilesItemSelectorModal;
