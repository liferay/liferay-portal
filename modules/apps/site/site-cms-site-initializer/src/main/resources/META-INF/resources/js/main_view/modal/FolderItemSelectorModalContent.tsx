/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Alert from '@clayui/alert';
import ClayLink from '@clayui/link';
import {useModal} from '@clayui/modal';
import {IFrontendDataSetProps, IView} from '@liferay/frontend-data-set-web';
import {ItemSelectorModal} from '@liferay/frontend-js-item-selector-web';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import ApiHelper, {RequestResult} from '../../common/services/ApiHelper';
import FolderService from '../../common/services/FolderService';
import {AssetLibrary} from '../../common/types/AssetLibrary';
import {ISearchAssetObjectEntry} from '../../common/types/AssetType';
import {IBulkActionFDSData} from '../../common/types/BulkActionTask';
import {
	ITEM_SELECTOR_ITEM_TYPE,
	ItemSelectorItemType,
	OBJECT_ENTRY_FOLDER_CLASS_NAME,
	isRootFolderERC,
} from '../../common/utils/constants';
import {openCMSModal} from '../../common/utils/openCMSModal';
import {displayErrorToast} from '../../common/utils/toastUtil';
import {triggerAssetBulkAction} from '../props_transformer/actions/triggerAssetBulkAction';
import {isContentStructureMoveInvalid} from '../utils/ContentStructureUtil';
import DuplicatedAssetFolderNamesModalContent, {
	Option,
} from './DuplicatedAssetFolderNamesModalContent';

export type TFolderItemSelectorModalContent = {
	action: FolderAction;
	apiURL?: string;
	assetLibraries: AssetLibrary[];
	dataSetId?: string;
	isBulk?: boolean;
	itemData: ItemData;
	loadData: () => {};
	objectEntryFolderExternalReferenceCode: string | undefined;
	rootObjectEntryFolderExternalReferenceCode: string;
	selectedData: IBulkActionFDSData;
};

export type FolderAction = 'copy' | 'move';

type FolderNode = {
	id: number;
	title: string;
};

type Folder = FolderNode & {
	scopeId: string;
};

type Space = {
	name: string;
	scopeId: number;
};

const SPACES_URL = `${window.location.origin}/o/headless-asset-library/v1.0/asset-libraries?filter=type eq 'Space'`;

const isModifiedClick = (event: React.MouseEvent) =>
	event.metaKey ||
	event.ctrlKey ||
	event.shiftKey ||
	event.altKey ||
	event.button !== 0;

const SUCCESS_MESSAGES = {
	copy: Liferay.Language.get('x-was-successfully-copied-to-x'),
	move: Liferay.Language.get('x-was-successfully-moved-to-x'),
};

const FDS_DEFAULT_PROPS: Partial<IFrontendDataSetProps> = {
	pagination: {
		deltas: [{label: 20}, {label: 40}, {label: 60}],
		initialDelta: 20,
	},
	selectionType: 'single',
};

const getDuplicateItemCheckPromise = (item: ItemData, folder: Folder) => {
	const isFolder = item.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME;

	if (isFolder) {
		return FolderService.searchFolder(
			item.embedded.scopeId,
			item.title,
			folder.id
		);
	}
	else {
		const folderURL = item.actions['get-by-scope'].href.replace(
			String(item.embedded.scopeId),
			folder.scopeId
		);

		return ApiHelper.get(
			`${folderURL}?filter=title eq '${item.title}' and folderId eq ${folder.id}`
		);
	}
};

const getSpaceFoldersURL = (cmsSection: string, scopeId: number) => {
	return `${window.location.origin}/o/search/v1.0/search?emptySearch=true&entryClassNames=${OBJECT_ENTRY_FOLDER_CLASS_NAME}&filter=((title eq '${cmsSection}' and folderId eq 0) or (cmsRoot eq true and cmsSection eq '${cmsSection}' and rootDescendantNode eq false)) and (status in (0, 2, 3))&nestedFields=description,embedded,file.thumbnailURL&scope=${scopeId}`;
};

const getChildFoldersURL = (folderId: number, scopeId: number) => {
	return `${window.location.origin}/o/search/v1.0/search?emptySearch=true&entryClassNames=${OBJECT_ENTRY_FOLDER_CLASS_NAME}&filter=folderId eq ${folderId} and (status in (0, 2, 3))&nestedFields=description,embedded,file.thumbnailURL&scope=${scopeId}`;
};

const displayInfoToast = (
	action: FolderAction,
	folder: Folder,
	itemData: ItemData
) => {
	openToast({
		message: sub(
			action === 'copy'
				? Liferay.Language.get('copying-x-to-x')
				: Liferay.Language.get('moving-x-to-x'),
			`${Liferay.Util.escapeHTML(itemData.embedded.title)}`,
			`<strong>${Liferay.Util.escapeHTML(folder.title)}</strong>`
		),
		type: 'info',
	});
};

const displaySuccessToast = (message: string, ...args: string[]) => {
	openToast({
		message: sub(message, args),
		type: 'success',
	});
};

const displayToast = (
	action: FolderAction,
	error: any,
	folder: Folder,
	itemData: ItemData,
	message: string
) => {
	if (error) {
		let errorMessage = error;

		if (error?.status === 'BAD_REQUEST') {
			errorMessage = sub(
				action === 'copy'
					? Liferay.Language.get(
							'x-could-not-be-copied.-please-ensure-the-structure-it-is-using-exists-in-the-destination-space'
						)
					: Liferay.Language.get(
							'x-could-not-be-moved.-please-ensure-the-structure-it-is-using-exists-in-the-destination-space'
						),
				Liferay.Util.escapeHTML(itemData.title)
			);
		}

		displayErrorToast(errorMessage);
	}
	else {
		displaySuccessToast(
			message,
			`${Liferay.Util.escapeHTML(itemData.embedded.title)}`,
			`<strong>${Liferay.Util.escapeHTML(folder.title)}</strong>`
		);
	}
};

function executeBulkCopyOrMoveAction({
	apiURL,
	dataSetId,
	folder,
	onClose,
	selectedData,
	targetName,
	type,
}: {
	apiURL: string | undefined;
	dataSetId?: string;
	folder: Folder;
	onClose: () => void;
	selectedData: any;
	targetName?: string;
	type: 'CopyObjectBulkSelectionAction' | 'MoveObjectBulkSelectionAction';
}) {
	triggerAssetBulkAction({
		additionalData: {
			targetName: targetName ?? folder.title,
		},
		apiURL,
		dataSetId,
		keyValues: {
			objectEntryFolderId: folder.id,
		},
		onCreateSuccess: onClose,
		selectedData,
		type,
	});
}

function executeAction({
	action,
	folder,
	itemData,
	loadData,
	replace = false,
}: {
	action: FolderAction;
	folder: Folder;
	itemData: ItemData;
	loadData: () => {};
	replace?: boolean;
}) {
	displayInfoToast(action, folder, itemData);

	const isFolder = itemData.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME;

	let promise: Promise<RequestResult<unknown>>;

	if (isFolder) {
		if (action === 'copy') {
			promise = replace
				? FolderService.copyReplaceFolder(
						itemData.embedded.id,
						folder.id
					)
				: FolderService.copyFolder(itemData.embedded.id, folder.id);
		}
		else {
			promise = replace
				? FolderService.moveReplaceFolder(
						itemData.embedded.id,
						folder.id
					)
				: FolderService.moveFolder(itemData.embedded.id, folder.id);
		}
	}
	else {
		const actionKey = `${action}${
			replace ? '-replace' : ''
		}` as keyof ItemData['actions'];

		promise = ApiHelper.post<any>(
			itemData.actions[actionKey].href.replace(
				'{objectEntryFolderId}',
				String(folder.id)
			)
		);
	}

	promise.then((result: any) => {
		if (!result.error) {
			loadData();
		}

		displayToast(
			action,
			result.error,
			folder,
			itemData,
			SUCCESS_MESSAGES[action]
		);
	});
}

function openDuplicatedAssetFolderNamesModal(
	action: FolderAction,
	itemData: ItemData,
	onContinueClick: (operation: Option) => void
) {
	openCMSModal({
		contentComponent: ({closeModal}: {closeModal: () => void}) =>
			DuplicatedAssetFolderNamesModalContent({
				action,
				closeModal,
				itemData,
				onContinueClick,
			}),
		size: 'md',
	});
}

function FolderItemSelectorModalContent({
	action,
	apiURL,
	assetLibraries,
	dataSetId,
	isBulk = false,
	itemData,
	loadData,
	rootObjectEntryFolderExternalReferenceCode,
	selectedData,
}: TFolderItemSelectorModalContent) {
	const isCopy = action === 'copy';

	const [selectedItemType, setSelectedItemType] =
		useState<ItemSelectorItemType>(ITEM_SELECTOR_ITEM_TYPE.SPACE);

	const objectFolderExternalReferenceCode =
		itemData.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME
			? ''
			: itemData.embedded.systemProperties?.objectDefinitionBrief
					?.objectFolderExternalReferenceCode;

	const cmsSection =
		objectFolderExternalReferenceCode === 'L_CMS_CONTENT_STRUCTURES' ||
		rootObjectEntryFolderExternalReferenceCode === 'L_CONTENTS'
			? 'contents'
			: 'files';

	const [url, setURL] = useState<string>(SPACES_URL);
	const [schemaKey, setSchemaKey] = useState(0);
	const [currentSpace, setCurrentSpace] = useState<Space | undefined>();
	const [folderStructure, setFolderStructure] = useState<FolderNode[]>([]);
	const [rootFolder, setRootFolder] = useState<Folder | null>(null);

	const {observer, onOpenChange, open} = useModal();

	const abortControllerRef = useRef<AbortController | null>(null);

	const excludedERCs = useMemo(() => {
		const rootExcluded = rootObjectEntryFolderExternalReferenceCode
			? [rootObjectEntryFolderExternalReferenceCode]
			: [];

		if (isBulk && selectedData?.items) {
			return [
				...rootExcluded,
				...selectedData.items
					.filter(
						(item: any) =>
							item.entryClassName ===
							OBJECT_ENTRY_FOLDER_CLASS_NAME
					)
					.map((item: any) => item.embedded.externalReferenceCode),
			];
		}

		if (
			itemData?.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME &&
			itemData.embedded?.externalReferenceCode
		) {
			return [...rootExcluded, itemData.embedded.externalReferenceCode];
		}

		return rootExcluded;
	}, [
		isBulk,
		itemData,
		rootObjectEntryFolderExternalReferenceCode,
		selectedData,
	]);

	const handleSpaceClick = useCallback(
		async (space: Space) => {
			abortControllerRef.current?.abort();

			const controller = new AbortController();
			abortControllerRef.current = controller;

			setRootFolder(null);
			setCurrentSpace(space);
			setFolderStructure([]);
			setSelectedItemType(ITEM_SELECTOR_ITEM_TYPE.FOLDER);
			setURL(getSpaceFoldersURL(cmsSection, space.scopeId));
			setSchemaKey((prev) => prev + 1);

			if (rootObjectEntryFolderExternalReferenceCode) {
				const {data} = await ApiHelper.get<any>(
					`/o/search/v1.0/search?emptySearch=true&entryClassNames=${OBJECT_ENTRY_FOLDER_CLASS_NAME}&filter=title eq '${cmsSection}' and folderId eq 0 and status in (0, 2, 3)&nestedFields=embedded&scope=${space.scopeId}&pageSize=1`,
					controller.signal
				);

				if (controller.signal.aborted) {
					return;
				}

				if (data?.items?.length) {
					const folder = data.items[0];

					setRootFolder({
						id: folder.embedded.id,
						scopeId: String(space.scopeId),
						title: space.name,
					});
				}
			}
		},
		[cmsSection, rootObjectEntryFolderExternalReferenceCode]
	);

	const navigateToFolders = useCallback(
		(folders: FolderNode[]) => {
			if (!currentSpace) {
				return;
			}

			setFolderStructure(folders);
			setSchemaKey((prev) => prev + 1);
			setURL(
				!folders.length
					? getSpaceFoldersURL(cmsSection, currentSpace.scopeId)
					: getChildFoldersURL(
							folders[folders.length - 1].id,
							currentSpace.scopeId
						)
			);
		},
		[cmsSection, currentSpace]
	);

	const handleChildFolderClick = useCallback(
		(folder: FolderNode) => {
			navigateToFolders([...folderStructure, folder]);
		},
		[folderStructure, navigateToFolders]
	);

	const setItemComponentProps = useCallback(
		({item, props}: {item: any; props: any}) => {
			if (item.type === ITEM_SELECTOR_ITEM_TYPE.SPACE) {
				const assetLibrary = assetLibraries.find(
					(assetLibrary) =>
						assetLibrary.externalReferenceCode ===
						item.externalReferenceCode
				);

				return {
					...props,
					onClick: () => {
						if (!assetLibrary) {
							return;
						}

						handleSpaceClick({
							name: assetLibrary.name,
							scopeId: assetLibrary.groupId,
						});
					},
					onSelectChange: null,
				};
			}

			if (selectedItemType !== ITEM_SELECTOR_ITEM_TYPE.FOLDER) {
				return {
					...props,
					symbol: 'folder',
				};
			}

			const folderItem = item as ISearchAssetObjectEntry;
			const erc = folderItem.embedded?.externalReferenceCode;

			if (erc && excludedERCs.includes(erc)) {
				return {
					...props,
					className: `${props.className || ''} hidden-item-selector-item`,
					onSelectChange: null,
				};
			}

			const folderId = folderItem.embedded?.id;

			const isDrillable =
				!isRootFolderERC(erc) &&
				folderItem.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME &&
				folderId !== undefined;

			if (!isDrillable) {
				return {
					...props,
					symbol: 'folder',
				};
			}

			const originalOnClick = props.onClick;

			return {
				...props,
				href: '#',
				onClick: (event: React.MouseEvent) => {
					const target = event.nativeEvent.target as HTMLElement;
					const anchor =
						target.tagName === 'A' ? target : target.closest('a');

					if (anchor) {
						if (isModifiedClick(event)) {
							event.preventDefault();

							return;
						}

						event.preventDefault();
						handleChildFolderClick({
							id: folderId,
							title: folderItem.title ?? '',
						});

						return;
					}

					originalOnClick?.(event);
				},
				symbol: 'folder',
			};
		},
		[
			assetLibraries,
			excludedERCs,
			handleChildFolderClick,
			handleSpaceClick,
			selectedItemType,
		]
	);

	const customRenderers = useMemo(
		() => ({
			tableCell: [
				{
					component: ({
						itemData,
						value,
					}: {
						itemData: ISearchAssetObjectEntry;
						value: string;
					}) => {
						const erc = itemData.embedded?.externalReferenceCode;
						const folderId = itemData.embedded?.id;

						if (
							isRootFolderERC(erc) ||
							folderId === undefined ||
							itemData.entryClassName !==
								OBJECT_ENTRY_FOLDER_CLASS_NAME
						) {
							return <>{value}</>;
						}

						return (
							<div className="table-list-title">
								<ClayLink
									aria-label={value}
									data-senna-off
									href="#"
									onClick={(event: React.MouseEvent) => {
										if (isModifiedClick(event)) {
											event.preventDefault();

											return;
										}

										event.preventDefault();
										handleChildFolderClick({
											id: folderId,
											title: itemData.title ?? '',
										});
									}}
								>
									{value}
								</ClayLink>
							</div>
						);
					},
					name: 'folderTitleCellRenderer',
					type: 'internal' as const,
				},
			],
		}),
		[handleChildFolderClick]
	);

	const handleOnItemsChange = async (folder: Folder, targetName?: string) => {
		const invalidContentActionMessage = isCopy
			? Liferay.Language.get(
					'the-asset-cannot-be-copied-because-its-content-type-is-not-available-in-the-destination-space'
				)
			: Liferay.Language.get(
					'the-asset-cannot-be-moved-because-its-content-type-is-not-available-in-the-destination-space'
				);

		if (isBulk && selectedData.items) {
			const invalidMovesPromises = selectedData.items.map(
				async (item: any) =>
					await isContentStructureMoveInvalid(
						item.embedded,
						currentSpace?.scopeId,
						assetLibraries
					)
			);

			const invalidMovesResults = await Promise.all(invalidMovesPromises);
			const hasContentStructureInDifferentSpace =
				invalidMovesResults.some(Boolean);

			if (hasContentStructureInDifferentSpace) {
				displayErrorToast(invalidContentActionMessage);
				onOpenChange(false);

				return;
			}

			const actionType = isCopy
				? 'CopyObjectBulkSelectionAction'
				: 'MoveObjectBulkSelectionAction';

			const duplicateCheckPromises = selectedData.items.map(
				(selectedItem: any) =>
					getDuplicateItemCheckPromise(selectedItem, folder).then(
						(result: any) => ({
							data: result.data,
							error: result.error,
							item: selectedItem,
						})
					)
			);

			Promise.all(duplicateCheckPromises).then((results) => {
				const duplicatedItemTitles: string[] = [];
				let hasError = false;

				results.forEach(({data, error, item}) => {
					if (error) {
						if (!hasError) {
							displayErrorToast(error);
							hasError = true;
						}
					}
					else if (data?.items.length > 0) {
						duplicatedItemTitles.push(item.title);
					}
				});

				if (hasError || duplicatedItemTitles.length) {
					if (!hasError) {
						displayErrorToast(
							Liferay.Language.get(
								'assets-could-not-be-moved.-please-ensure-the-name-is-unique-in-the-destination'
							)
						);
					}
					onOpenChange(false);

					return;
				}

				executeBulkCopyOrMoveAction({
					apiURL,
					dataSetId,
					folder,
					onClose: () => onOpenChange(false),
					selectedData,
					targetName,
					type: actionType,
				});
			});

			return;
		}

		const isInvalidSingleMove = await isContentStructureMoveInvalid(
			itemData.embedded,
			currentSpace?.scopeId,
			assetLibraries
		);

		if (isInvalidSingleMove) {
			displayErrorToast(invalidContentActionMessage);
			onOpenChange(false);

			return;
		}

		getDuplicateItemCheckPromise(itemData, folder).then(
			({data, error}: any) => {
				if (error) {
					displayErrorToast(error);

					return;
				}

				if (data?.items.length > 0) {
					openDuplicatedAssetFolderNamesModal(
						action,
						itemData,
						(operation: Option) => {
							executeAction({
								action,
								folder,
								itemData,
								loadData,
								replace: operation === 'replace',
							});
						}
					);
				}
				else {
					executeAction({action, folder, itemData, loadData});
				}
			}
		);
	};

	useEffect(() => {
		onOpenChange(true);
	}, [onOpenChange]);

	useEffect(() => {
		return () => {
			abortControllerRef.current?.abort();
		};
	}, []);

	return (
		<>
			{open && (
				<ItemSelectorModal<Folder>
					allowEmptySelection={
						selectedItemType === ITEM_SELECTOR_ITEM_TYPE.FOLDER &&
						!!rootFolder
					}
					apiURL={url}
					breadcrumbs={[
						{
							label: Liferay.Language.get('spaces'),
							onClick: () => {
								setCurrentSpace(undefined);
								setFolderStructure([]);
								setSchemaKey((prev) => prev + 1);
								setSelectedItemType(
									ITEM_SELECTOR_ITEM_TYPE.SPACE
								);
								setURL(SPACES_URL);
							},
						},
						...(currentSpace
							? [
									{
										label: currentSpace.name,
										onClick: () => {
											if (!folderStructure.length) {
												return;
											}

											navigateToFolders([]);
										},
									},
								]
							: []),
						...folderStructure.map((folder, index) => ({
							label: folder.title,
							onClick: () => {
								if (index === folderStructure.length - 1) {
									return;
								}

								navigateToFolders(
									folderStructure.slice(0, index + 1)
								);
							},
						})),
					]}
					breadcrumbsLabel={false}
					emptySelectionLabel={currentSpace?.name}
					fdsProps={{
						...FDS_DEFAULT_PROPS,
						customRenderers,
						id: `itemSelectorModal-users-${
							selectedItemType === ITEM_SELECTOR_ITEM_TYPE.FOLDER
								? itemData.embedded.id
								: itemData.id
						}`,

						// eslint-disable-next-line react-compiler/react-compiler
						views: [
							{
								contentRenderer: 'cards',
								label: Liferay.Language.get('cards'),
								name: 'cards',
								schema:
									selectedItemType ===
									ITEM_SELECTOR_ITEM_TYPE.FOLDER
										? {
												description: 'description',
												title: 'title',
											}
										: {
												description: 'description',
												title: 'name',
											},
								setItemComponentProps,
								thumbnail: 'cards2',
							},
							{
								contentRenderer: 'table',
								label: Liferay.Language.get('table'),
								name: 'table',
								schema: {
									fields: [
										selectedItemType ===
										ITEM_SELECTOR_ITEM_TYPE.FOLDER
											? {
													contentRenderer:
														'folderTitleCellRenderer',
													fieldName: 'title',
													label: Liferay.Language.get(
														'title'
													),
													sortable: false,
												}
											: {
													fieldName: 'name',
													label: Liferay.Language.get(
														'title'
													),
													sortable: false,
												},
										{
											fieldName: 'description',
											label: Liferay.Language.get(
												'description'
											),
											sortable: false,
										},
									],
								},
								setItemComponentProps,
								thumbnail: 'table',
							},
						] as IView[],
					}}
					items={[]}
					key={schemaKey}
					locator={
						selectedItemType === ITEM_SELECTOR_ITEM_TYPE.FOLDER
							? {
									id: 'embedded.id',
									label: 'title',
									value: 'embedded.id',
								}
							: {
									id: 'id',
									label: 'name',
									value: 'id',
								}
					}
					message={
						<Alert
							className="alert-dismissible alert-fluid p-3"
							displayType="warning"
							title="Warning"
						>
							{action === 'copy'
								? Liferay.Language.get(
										'only-categories-and-tags-also-available-in-the-destination-will-be-copied'
									)
								: Liferay.Language.get(
										'only-categories-and-tags-also-available-in-the-destination-will-be-retained'
									)}
						</Alert>
					}
					observer={observer}
					onItemsChange={(items: any[]) => {
						if (items.length) {
							const item = items[0];
							const isFolder =
								selectedItemType ===
								ITEM_SELECTOR_ITEM_TYPE.FOLDER;

							let name = item.title;

							if (isFolder) {
								const hasNoParent =
									!item.embedded.parentObjectEntryFolderId ||
									item.embedded.parentObjectEntryFolderId ===
										null;

								if (hasNoParent) {
									name = currentSpace?.name || item.title;
								}
							}

							handleOnItemsChange(
								{
									id: isFolder ? item.embedded.id : item.id,
									scopeId: isFolder
										? item.embedded.scopeId
										: item.scopeId,
									title: name,
								},
								name
							);
						}
						else if (
							selectedItemType ===
								ITEM_SELECTOR_ITEM_TYPE.FOLDER &&
							rootFolder
						) {
							handleOnItemsChange(rootFolder, rootFolder.title);
						}
					}}
					onOpenChange={onOpenChange}
					open={open}
					title={
						action === 'copy'
							? sub(
									Liferay.Language.get('copy-x-to'),
									itemData.title
								)
							: sub(
									Liferay.Language.get('move-x-to'),
									itemData.title
								)
					}
				/>
			)}
		</>
	);
}

export default FolderItemSelectorModalContent;
