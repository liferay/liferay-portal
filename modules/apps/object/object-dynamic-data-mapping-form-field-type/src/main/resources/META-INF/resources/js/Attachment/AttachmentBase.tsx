/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {
	convertToFormData,
	makeFetch,
	useConfig,
} from 'data-engine-js-components-web';
import {openSelectionModal} from 'frontend-js-components-web';
import React, {ChangeEventHandler, useRef, useState} from 'react';

import FileContainer from './FileContainer';
import {validateFileExtension, validateFileSize} from './util/attachment';

import './Attachment.scss';

import type {LocalizedValue} from 'dynamic-data-mapping-form-field-type';

export type AttachmentFile = {
	contentURL: string;
	fileEntryId: string;
	title: string;
};

type File = {
	contentURL: string;
	fileEntryId: string;
	readOnly: boolean;
	title: string;
};

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
	url,
}: AttachmentBaseProps<string | LocalizedValue<string>>) {
	const {portletNamespace} = useConfig();

	const inputRef = useRef<HTMLInputElement>(null);

	const [isLoading, setLoading] = useState(false);

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
					title: selectedItemValue.title,
				},
				selectedItemValue.fileEntryId
			);
		}
	};

	const handleUpload: ChangeEventHandler<HTMLInputElement> = async ({
		target: {files},
	}) => {
		const selectedFile = files?.[0];
		if (selectedFile) {
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
				const {error, file} = (await makeFetch({
					body: convertToFormData({
						[`${portletNamespace}file`]: files[0],
					}),
					method: 'POST',
					url,
				})) as {error: {message: string}; file: File; success: boolean};

				if (error) {
					setError({
						displayErrors: true,
						errorMessage: error.message,
						valid: false,
					});
				}
				else {
					onAttachmentChange(
						{
							contentURL: file.contentURL,
							fileEntryId: file.fileEntryId,
							title: file.title,
						},
						file.fileEntryId
					);
				}
			}
			finally {
				setLoading(false);
			}
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

							if (fileSource === 'documentsAndMedia') {
								openSelectionModal({
									onSelect: handleSelectedItem,
									selectEventName: `${portletNamespace}selectAttachmentEntry`,
									title: Liferay.Language.get('select-file'),
									url,
								});
							}
							else if (fileSource === 'userComputer') {
								const filePicker = inputRef.current;
								if (filePicker) {
									filePicker.value = '';
									filePicker.click();
								}
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
