/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {useModal} from '@clayui/modal';
import ClaySticker from '@clayui/sticker';
import {
	IInlineNotificationComponent,
	TSort,
} from '@liferay/frontend-data-set-web';
import {useBrowserTabVisibility} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import {fetch, mimeTypeUtils} from 'frontend-js-web';
import React, {
	createContext,
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useRef,
	useState,
} from 'react';

import ItemSelectorModal, {FilesUploaderComponent} from './ItemSelectorModal';
import {
	FolderCrumb,
	getLastBreadcrumbFolders,
	setLastBreadcrumbFolders,
} from './lastBreadcrumbFoldersMemory';
import {TDetachedItemSelectorModal} from './types';

import '../css/DetachedCMSFilesItemSelectorModal.scss';

const OBJECT_ENTRY_FOLDER_CLASS_NAME =
	'com.liferay.object.model.ObjectEntryFolder';

const OBJECT_ENTRY_FOLDERS_API_URL =
	'/o/headless-object/v1.0/object-entry-folders';

const DL_FOLDER_CLASS_NAME =
	'com.liferay.document.library.kernel.model.DLFolder';

const STATUS_IN_TRASH = 8;

function isFolder(item?: {entryClassName: string}): boolean {
	return (
		item?.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME ||
		item?.entryClassName === DL_FOLDER_CLASS_NAME
	);
}

function getFolderLabel(item: any): string {
	return item?.embedded?.title ?? item?.embedded?.name ?? item?.title ?? '';
}

async function checkNewCMSFiles(
	cmsRootFilesURL: string,
	lastRequestTime: string
) {
	const url = new URL(cmsRootFilesURL, window.location.origin);
	const existingFilter = url.searchParams.get('filter') ?? '';

	url.searchParams.set(
		'filter',
		existingFilter
			? `(${existingFilter}) and dateCreated gt ${lastRequestTime}`
			: `dateCreated gt ${lastRequestTime}`
	);

	const response = await fetch(url.toString());

	if (!response.ok) {
		return {totalCount: 0};
	}

	return (await response.json()) as {totalCount: number};
}

type NotificationContextValue = {
	newItemsCount: number;
	setShowInlineNotification: (show: boolean) => void;
	showInlineNotification: boolean;
};

const NotificationContext = createContext<NotificationContextValue>({
	newItemsCount: 0,
	setShowInlineNotification: () => {},
	showInlineNotification: false,
});

const NewItemsNotificationComponent = ({
	context,
}: {
	context: IInlineNotificationComponent['context'];
}) => {
	const {newItemsCount, setShowInlineNotification, showInlineNotification} =
		useContext(NotificationContext);

	if (!showInlineNotification) {
		return null;
	}

	return (
		<ClayAlert
			className="detached-cms-files-alert mx-n3 pl-5 pr-1"
			displayType="info"
			onClose={() => setShowInlineNotification(false)}
			title={Liferay.Language.get('info')}
			variant="stripe"
		>
			{Liferay.Util.sub(
				Liferay.Language.get(
					'x-new-items-are-not-visible-in-this-view'
				),
				[newItemsCount]
			)}

			<ClayButton.Group className="pl-3" spaced>
				<ClayButton
					displayType="info"
					onClick={() => {
						const updatedSorts: TSort[] = (context?.sorts || [])
							.filter((sort) => sort.key !== 'dateCreated')
							.map((sort) => ({...sort, active: false}));

						updatedSorts.push({
							active: true,
							direction: 'desc',
							key: 'dateCreated',
							label: Liferay.Language.get('by-creation-date'),
						});

						context?.onClearResultsBar();
						context?.forceSortsUpdate(updatedSorts);

						setShowInlineNotification(false);
					}}
					size="sm"
				>
					{Liferay.Language.get('reload')}
				</ClayButton>

				<ClayButton
					alert
					onClick={() => setShowInlineNotification(false)}
					size="sm"
				>
					{Liferay.Language.get('dismiss')}
				</ClayButton>
			</ClayButton.Group>
		</ClayAlert>
	);
};

type DetachedCMSFilesItemSelectorModalProps<T extends Record<string, any>> =
	TDetachedItemSelectorModal<T> & {
		buildApiURL: (folderId: number | null) => string;
		folderMemoryKey?: string;
		resetKey?: string;
		rootLabel?: string;
	};

function getRootBreadcrumbFolders(rootLabel?: string): FolderCrumb[] {
	return [
		{
			id: null,
			label: rootLabel ?? Liferay.Language.get('files'),
		},
	];
}

async function isFolderAvailable(folder: FolderCrumb): Promise<boolean> {

	// The root ("Files") has no backing object entry folder to validate, so it
	// is always available.

	if (folder.id === null) {
		return true;
	}

	try {
		const response = await fetch(
			`${OBJECT_ENTRY_FOLDERS_API_URL}/${folder.id}`
		);

		// Only a confirmed deletion (the folder is gone or in the recycle bin)
		// should drop the selector back to the root. A transient failure
		// (network error, server error, missing permission) is inconclusive,
		// so the remembered folder is kept rather than wiped.

		if (response.status === 404) {
			return false;
		}

		if (!response.ok) {
			return true;
		}

		const objectEntryFolder = await response.json();

		return objectEntryFolder?.status?.code !== STATUS_IN_TRASH;
	}
	catch (error) {
		return true;
	}
}

const DetachedCMSFilesItemSelectorModal = <T extends Record<string, any>>(
	props: DetachedCMSFilesItemSelectorModalProps<T>
) => {
	const {
		buildApiURL,
		folderMemoryKey,
		onItemsChange,
		resetKey,
		rootLabel,
		...restProps
	} = props;

	const {observer, onOpenChange, open} = useModal();
	const [newItemsCount, setNewItemsCount] = useState(0);
	const [showInlineNotification, setShowInlineNotification] = useState(false);
	const [breadcrumbFolders, setBreadcrumbFolders] = useState<FolderCrumb[]>(
		() => {
			const restoredFolders = folderMemoryKey
				? getLastBreadcrumbFolders(folderMemoryKey)
				: null;

			return restoredFolders ?? getRootBreadcrumbFolders(rootLabel);
		}
	);

	const isBrowserTabVisible = useBrowserTabVisibility();
	const lastRequestTimeRef = useRef(new Date().toISOString());
	const resetKeyRef = useRef(resetKey);
	const rootLabelRef = useRef(rootLabel);

	useEffect(() => {
		rootLabelRef.current = rootLabel;
	});

	useEffect(() => {
		onOpenChange(true);
	}, [onOpenChange]);

	useEffect(() => {
		if (!folderMemoryKey) {
			return;
		}

		const restoredFolders = getLastBreadcrumbFolders(folderMemoryKey);

		if (!restoredFolders || restoredFolders.length <= 1) {
			return;
		}

		isFolderAvailable(restoredFolders[restoredFolders.length - 1]).then(
			(available) => {
				if (!available) {
					setLastBreadcrumbFolders(folderMemoryKey, null);

					setBreadcrumbFolders(
						getRootBreadcrumbFolders(rootLabelRef.current)
					);
				}
			}
		);
	}, [folderMemoryKey]);

	useEffect(() => {
		if (resetKeyRef.current === resetKey) {
			return;
		}

		resetKeyRef.current = resetKey;

		setBreadcrumbFolders(getRootBreadcrumbFolders(rootLabel));
	}, [resetKey, rootLabel]);

	const activeFolderId = breadcrumbFolders.at(-1)!.id;
	const activeScopeId = breadcrumbFolders.at(-1)!.scopeId ?? null;

	const apiURL = useMemo(
		() => buildApiURL(activeFolderId),
		[activeFolderId, buildApiURL]
	);

	useEffect(() => {
		if (isBrowserTabVisible && open) {
			checkNewCMSFiles(apiURL, lastRequestTimeRef.current).then(
				(response) => {
					if (response.totalCount > 0) {
						setNewItemsCount(response.totalCount);
						setShowInlineNotification(true);

						lastRequestTimeRef.current = new Date().toISOString();
					}
				}
			);
		}
	}, [isBrowserTabVisible, open, apiURL]);

	const breadcrumbs = useMemo(
		() =>
			breadcrumbFolders.map((crumb, index) => {
				const isLast = index === breadcrumbFolders.length - 1;

				return {
					active: isLast,
					label: crumb.label,
					onClick: isLast
						? undefined
						: (event: React.SyntheticEvent) => {
								event.preventDefault();

								setBreadcrumbFolders((path) =>
									path.slice(0, index + 1)
								);
							},
				};
			}),
		[breadcrumbFolders]
	);

	const navigateToFolder = useCallback(
		(folder: {id: number; label: string; scopeId?: number | null}) => {
			setBreadcrumbFolders((path) => [...path, folder]);
		},
		[]
	);

	const handleItemsChange = useCallback(
		(items: T[]) => {
			if (folderMemoryKey) {
				setLastBreadcrumbFolders(folderMemoryKey, breadcrumbFolders);
			}

			onItemsChange(items);
		},
		[breadcrumbFolders, folderMemoryKey, onItemsChange]
	);

	const customRenderers = useMemo(
		() => ({
			...restProps.fdsProps.customRenderers,
			tableCell: [
				...(restProps.fdsProps.customRenderers?.tableCell ?? []),
				{
					component: ({
						itemData,
						value,
					}: {
						itemData: any;
						value: any;
					}) => {
						if (isFolder(itemData)) {
							return (
								<div className="align-items-center d-flex">
									<ClaySticker className="c-mr-2 file-icon-color-0 flex-shrink-0 inline-item inline-item-before">
										<ClayIcon symbol="folder" />
									</ClaySticker>

									<div className="table-list-title">
										<ClayLink
											className="text-decoration-underline"
											data-senna-off
											href="#"
											onClick={(
												event: React.MouseEvent
											) => {
												event.preventDefault();
												event.stopPropagation();

												navigateToFolder({
													id:
														itemData?.embedded.id ??
														0,
													label: getFolderLabel(
														itemData
													),
													scopeId:
														itemData?.embedded
															.scopeId ?? 0,
												});
											}}
										>
											{value}
										</ClayLink>
									</div>
								</div>
							);
						}

						const fileMimeType =
							itemData?.embedded?.file?.mimeType ??
							itemData?.embedded?.encodingFormat;

						let stickerClassName = 'file-icon-color-5';
						let iconSymbol = 'document-default';

						if (fileMimeType) {
							stickerClassName =
								mimeTypeUtils.getClassNameFromMimeType(
									fileMimeType
								);
							iconSymbol =
								mimeTypeUtils.getIconFromMimeType(fileMimeType);
						}
						else if (itemData?.embedded?.videoURL) {
							stickerClassName = 'file-icon-color-3';
							iconSymbol = 'document-multimedia';
						}

						return (
							<div className="align-items-center d-flex">
								<ClaySticker
									className={classNames(
										'c-mr-2 flex-shrink-0 inline-item inline-item-before',
										stickerClassName
									)}
								>
									<ClayIcon symbol={iconSymbol} />
								</ClaySticker>

								<span>{value || itemData?.title}</span>
							</div>
						);
					},
					name: 'cmsFilesTitleCellRenderer',
					type: 'internal' as const,
				},
				{
					component: ({
						itemData,
						value,
					}: {
						itemData: any;
						value: any;
					}) => (isFolder(itemData) ? '--' : value),
					name: 'cmsFilesFallbackCellRenderer',
					type: 'internal' as const,
				},
			],
		}),
		[navigateToFolder, restProps.fdsProps.customRenderers]
	);

	const views = useMemo(
		() =>
			(restProps.fdsProps.views ?? []).map((view: any) => {
				if (
					view.contentRenderer !== 'cards' &&
					view.contentRenderer !== 'table'
				) {
					return view;
				}

				const original = view.setItemComponentProps;

				return {
					...view,
					setItemComponentProps: ({
						item,
						props,
					}: {
						item: any;
						props: any;
					}) => {
						const effectiveProps = original
							? original({item, props})
							: props;

						if (isFolder(item)) {
							return {
								...effectiveProps,
								onClick: () =>
									navigateToFolder({
										id: item?.embedded?.id ?? 0,
										label: getFolderLabel(item),
										scopeId: item?.embedded?.scopeId ?? 0,
									}),
								...(view.contentRenderer === 'cards'
									? {onSelectChange: null, symbol: 'folder'}
									: {
											className: classNames(
												effectiveProps.className,
												'cms-item-selector-folder-row'
											),
										}),
							};
						}

						return effectiveProps;
					},
				};
			}),
		[navigateToFolder, restProps.fdsProps.views]
	);

	const fdsProps = useMemo(
		() => ({
			...restProps.fdsProps,
			customRenderers,
			inlineNotificationComponent: NewItemsNotificationComponent,
			views,
		}),
		[customRenderers, restProps.fdsProps, views]
	);

	const filesUploaderComponent = useMemo(() => {
		const InitialFileUploaderComponent =
			restProps.filesUploaderComponent as
				| FilesUploaderComponent<{
						parentObjectEntryFolderId?: number | null;
				  }>
				| undefined;

		if (!InitialFileUploaderComponent) {
			return undefined;
		}

		return (
			uploaderProps: React.ComponentProps<
				typeof InitialFileUploaderComponent
			>
		) => (
			<InitialFileUploaderComponent
				{...uploaderProps}
				groupId={activeScopeId ?? uploaderProps.groupId}
				parentObjectEntryFolderId={activeFolderId}
			/>
		);
	}, [activeFolderId, activeScopeId, restProps.filesUploaderComponent]);

	return (
		<NotificationContext.Provider
			value={{
				newItemsCount,
				setShowInlineNotification,
				showInlineNotification,
			}}
		>
			{open && (
				<ItemSelectorModal
					{...restProps}
					apiURL={apiURL}
					breadcrumbs={breadcrumbs}
					breadcrumbsLabel={false}
					fdsProps={fdsProps}
					filesUploaderComponent={filesUploaderComponent}
					observer={observer}
					onItemsChange={handleItemsChange}
					onOpenChange={onOpenChange}
					open={open}
				/>
			)}
		</NotificationContext.Provider>
	);
};

export default DetachedCMSFilesItemSelectorModal;
