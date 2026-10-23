/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';
import {
	convertToFormData,
	makeFetch,
	useConfig,
} from 'data-engine-js-components-web';
import {openSelectionModal} from 'frontend-js-components-web';
import {fetch, formatStorage, getFileAsBase64} from 'frontend-js-web';
import React, {ChangeEventHandler, useEffect, useRef, useState} from 'react';

import FileContainer from './FileContainer';
import CMSFilesItemSelectorModal, {
	CMSFile,
} from './util/CMSFilesItemSelectorModal';
import {validateFileExtension, validateFileSize} from './util/attachment';

import './Attachment.scss';

import type {LocalizedValue} from 'dynamic-data-mapping-form-field-type';

type Space = {
	externalReferenceCode: string;
};

export type AttachmentFile = {
	contentURL: string;
	fileEntryId: string;
	size?: string;
	source?: 'dm' | 'cms';
	storageDepotGroup?: string;
	title: string;
};

type DMUploadedFile = {
	contentURL: string;
	fileEntryId: string;
	readOnly: boolean;
	size?: string;
	title: string;
};

type CMSUploadResponse = {
	contentURL: string;
	file: {
		id: number;
		link: {
			href: string;
		};
		size?: string;
	};
	id: number;
	title: string;
};

type FolderEntry = {
	externalReferenceCode: string;
	id: number;
	title: string;
};

type FolderRef =
	| {objectEntryFolderExternalReferenceCode: string}
	| {objectEntryFolderId: number};

/**
 * Formats a raw byte count (40255) the way the server formats attachment
 * sizes (39 KB), with the user's decimal separator and translated suffixes.
 */
function formatFileSize(bytes: number) {
	const decimalSeparator =
		new Intl.NumberFormat(Liferay.ThemeDisplay.getBCP47LanguageId())
			.formatToParts(1.5)
			.find((part) => part.type === 'decimal')?.value ?? '.';

	return formatStorage(bytes, {
		addSpaceBeforeSuffix: true,
		decimalSeparator,
		suffixGB: Liferay.Language.get('storage.size.suffix.gb'),
		suffixKB: Liferay.Language.get('storage.size.suffix.kb'),
		suffixMB: Liferay.Language.get('storage.size.suffix.mb'),
	});
}

export interface AttachmentBaseProps<TValue> {
	acceptedFileExtensions: string;
	attachment: AttachmentFile | null;
	error: {} | {displayErrors: boolean; errorMessage: string; valid: boolean};
	fileSource: string;
	handleDelete: () => void;
	maximumFileSize: number;
	onAttachmentChange: (attachment: AttachmentFile, id: string) => void;
	overallMaximumUploadRequestSize: number;
	readOnly: boolean;
	setError: React.Dispatch<React.SetStateAction<{}>>;
	storageDLFolderPath?: string;
	storageDepotGroup?: string;
	tip: string;
	url: string;
	value: TValue;
}

export default function AttachmentBase({
	acceptedFileExtensions,
	attachment,
	fileSource,
	handleDelete,
	maximumFileSize,
	onAttachmentChange,
	overallMaximumUploadRequestSize,
	readOnly,
	setError,
	storageDLFolderPath,
	storageDepotGroup,
	url,
}: AttachmentBaseProps<string | LocalizedValue<string>>) {
	const {portletNamespace} = useConfig();

	const inputRef = useRef<HTMLInputElement>(null);

	const [cmsFiles, setCMSFiles] = useState<CMSFile[]>([]);
	const [isLoading, setLoading] = useState(false);
	const [spaces, setSpaces] = useState<Space[]>([]);

	const {
		observer: spaceItemSelectorObserver,
		onOpenChange: onSpaceItemSelectorOpenChange,
		open: spaceItemSelectorOpen,
	} = useModal();

	const DEFAULT_FOLDER_ERC = 'L_FILES';

	const HIDDEN_FOLDER_PATH = 'HIDDEN_FILES';

	const isCMSBasicDocument = fileSource === 'CMSBasicDocument';

	const isDocumentsAndMedia = fileSource === 'documentsAndMedia';

	const isUserComputerToCMSBasicDocument =
		fileSource === 'userComputerToCMSBasicDocument';

	const isUserComputerToDocumentsAndMedia =
		fileSource === 'userComputerToDocumentsAndMedia';

	const hasLibraryStorage =
		isUserComputerToCMSBasicDocument || isUserComputerToDocumentsAndMedia;

	const handleCMSItemsChange = (items: CMSFile[]) => {
		setCMSFiles(items);

		if (!items.length) {
			return;
		}

		const selectedItem = items[0];

		onAttachmentChange(
			{
				contentURL: selectedItem.embedded.file.fileURL,
				fileEntryId: String(selectedItem.embedded.id),
				size: selectedItem.embedded.file.size,
				title: selectedItem.title,
			},
			String(selectedItem.embedded.file.id)
		);
	};

	const handleSelectedItem = (selectedItem: any) => {
		if (!selectedItem) {
			return;
		}

		const selectedItemValue = JSON.parse(selectedItem.value);

		const error =
			validateFileExtension(
				acceptedFileExtensions,
				selectedItemValue.extension
			) ??
			validateFileSize(
				Number(selectedItemValue.size),
				Number(maximumFileSize),
				Number(overallMaximumUploadRequestSize)
			);

		if (error) {
			setError(error);
		}
		else {
			onAttachmentChange(
				{
					contentURL: selectedItemValue.url,
					fileEntryId: selectedItemValue.fileEntryId,
					size: formatFileSize(Number(selectedItemValue.size)),
					title: selectedItemValue.title,
				},
				selectedItemValue.fileEntryId
			);
		}
	};

	useEffect(() => {
		fetch(
			"/o/headless-asset-library/v1.0/asset-libraries?filter=type eq 'Space'"
		)
			.then((response) => response.json())
			.then((data) => {
				setSpaces(data.items ?? []);
			});
	}, []);

	useEffect(() => {
		if (!attachment) {
			setCMSFiles([]);
		}
	}, [attachment]);

	const findOrCreateFolder = async (
		folderName: string,
		parentFolderERC: string,
		parentFolderId: number | undefined,
		isVisible: boolean,
		spaceERC: string
	): Promise<FolderEntry> => {
		const searchParams = new URLSearchParams({
			currentURL: '/web/cms/files',
			emptySearch: 'true',
			nestedFields: 'embedded,scope',
			pageSize: '30',
			search: folderName,
		});

		if (parentFolderId) {
			searchParams.set('filter', `folderId eq ${parentFolderId}`);
		}
		else if (isVisible) {
			searchParams.set(
				'filter',
				"cmsRoot eq true and cmsSection eq 'files' and rootDescendantNode eq false and status in (0)"
			);
		}

		const searchResponse = await fetch(
			`/o/search/v1.0/search?${searchParams.toString()}`
		);

		if (searchResponse.ok) {
			const {items = []} = await searchResponse.json();

			const match = items.find((item: any) => {
				const data = item.embedded ?? item;

				return (
					data.title === folderName &&
					data.scope?.externalReferenceCode === spaceERC
				);
			});

			const folder: FolderEntry | null = match?.embedded ?? match ?? null;

			if (folder) {
				return folder;
			}
		}

		const createResponse = await fetch(
			`/o/headless-object/v1.0/scopes/${spaceERC}/object-entry-folders`,
			{
				body: JSON.stringify({
					parentObjectEntryFolderExternalReferenceCode:
						parentFolderERC,
					title: folderName,
				}),
				headers: {'Content-Type': 'application/json'},
				method: 'POST',
			}
		);

		if (!createResponse.ok) {
			throw new Error(`Unable to create folder ${folderName}`);
		}

		return createResponse.json();
	};

	const resolveFolderId = async (
		isVisible: boolean,
		spaceERC: string,
		storageDLFolderPath?: string
	): Promise<FolderRef> => {
		const folderNames = (storageDLFolderPath || '')
			.split('/')
			.map((name) => name.trim())
			.filter(Boolean);

		if (!folderNames.length) {
			return {objectEntryFolderExternalReferenceCode: DEFAULT_FOLDER_ERC};
		}

		let parentFolderERC = DEFAULT_FOLDER_ERC;
		let parentFolderId: number | undefined;

		for (const folderName of folderNames) {
			const folder = await findOrCreateFolder(
				folderName,
				parentFolderERC,
				parentFolderId,
				isVisible,
				spaceERC
			);

			parentFolderId = folder.id;
			parentFolderERC = folder.externalReferenceCode;
		}

		return {objectEntryFolderId: parentFolderId!};
	};

	const uploadToCMS = async (
		file: File,
		storageDLFolderPath: string | undefined,
		storageDepotGroup: string | undefined
	): Promise<AttachmentFile & {id: string}> => {
		const spaceERC =
			storageDepotGroup || spaces[0]?.externalReferenceCode || '';

		if (!spaceERC) {
			throw new Error(
				Liferay.Language.get('unable-to-upload-the-selected-file')
			);
		}

		const fileBase64 = await getFileAsBase64(file);

		const isVisible = !!storageDepotGroup;

		const folder = await resolveFolderId(
			isVisible,
			spaceERC,
			storageDLFolderPath || HIDDEN_FOLDER_PATH
		);

		const body = {
			...folder,
			file: {
				fileBase64,
				name: file.name,
			},
			title: file.name,
		};

		const response = (await makeFetch({
			body: JSON.stringify(body),
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json',
			} as {Accept: string} & Record<string, string>,
			method: 'POST',
			url: `/o/cms/basic-documents/scopes/${spaceERC}`,
		})) as CMSUploadResponse & {status?: string; title?: string};

		const fileUrlPath = response.contentURL || response.file?.link?.href;

		const previewUrl = new URL(fileUrlPath, window.location.origin);

		previewUrl.searchParams.delete('download');

		return {
			contentURL: previewUrl.toString(),
			fileEntryId: String(response.id),
			id: String(response.file?.id),
			size: response.file?.size,
			source: 'cms',
			title: response.title,
		};
	};

	const uploadToDM = async (
		file: File,
		url: string,
		portletNamespace: string
	): Promise<AttachmentFile & {id: string}> => {
		const {error, file: uploadedFile} = (await makeFetch({
			body: convertToFormData({
				[`${portletNamespace}file`]: file,
			}),
			method: 'POST',
			url,
		})) as {
			error?: {message: string};
			file: DMUploadedFile;
		};

		if (error) {
			throw new Error(error.message);
		}

		return {
			...uploadedFile,
			id: uploadedFile.fileEntryId,
		};
	};

	const handleUpload: ChangeEventHandler<HTMLInputElement> = async ({
		target: {files},
	}) => {
		const selectedFile = files?.[0];

		if (!selectedFile) {
			return;
		}

		const fileSizeError = validateFileSize(
			Number(selectedFile.size),
			Number(maximumFileSize),
			Number(overallMaximumUploadRequestSize)
		);

		if (fileSizeError) {
			setError(fileSizeError);

			return;
		}

		setError({});
		setLoading(true);

		try {
			let result: AttachmentFile & {id: string};

			if (isUserComputerToCMSBasicDocument) {
				result = await uploadToCMS(
					selectedFile,
					storageDLFolderPath,
					storageDepotGroup
				);
			}
			else {
				result = await uploadToDM(selectedFile, url, portletNamespace);
			}

			const {id, ...attachmentData} = result;

			onAttachmentChange(attachmentData, id);
		}
		catch (error: any) {
			setError({
				displayErrors: true,
				errorMessage:
					error?.message ||
					Liferay.Language.get('unable-to-upload-the-selected-file'),
				valid: false,
			});
		}
		finally {
			setLoading(false);
		}
	};

	return (
		<>
			<div className="inline-item lfr-objects__attachment">
				{!readOnly && (
					<ClayButton
						className="lfr-objects__attachment-button"
						displayType="secondary"
						onClick={() => {
							setError({});

							if (isDocumentsAndMedia) {
								openSelectionModal({
									onSelect: handleSelectedItem,
									selectEventName: `${portletNamespace}selectAttachmentEntry`,
									title: Liferay.Language.get('select-file'),
									url,
								});
							}
							else if (hasLibraryStorage) {
								const filePicker = inputRef.current;

								if (filePicker) {
									filePicker.value = '';
									filePicker.click();
								}
							}
							else if (isCMSBasicDocument) {
								onSpaceItemSelectorOpenChange(true);
							}
						}}
					>
						{Liferay.Language.get('select-file')}
					</ClayButton>
				)}

				<FileContainer
					attachment={attachment}
					loading={isLoading}
					onDelete={handleDelete}
					readOnly={readOnly}
				/>
			</div>

			{isCMSBasicDocument && (
				<CMSFilesItemSelectorModal
					items={cmsFiles}
					observer={spaceItemSelectorObserver}
					onItemsChange={handleCMSItemsChange}
					onOpenChange={onSpaceItemSelectorOpenChange}
					open={spaceItemSelectorOpen}
				/>
			)}

			<input
				accept={acceptedFileExtensions
					.split(',')
					.map((extension) => `.${extension.trim()}`)
					.join(',')}
				onChange={handleUpload}
				ref={inputRef}
				style={{display: 'none'}}
				type="file"
			/>
		</>
	);
}
