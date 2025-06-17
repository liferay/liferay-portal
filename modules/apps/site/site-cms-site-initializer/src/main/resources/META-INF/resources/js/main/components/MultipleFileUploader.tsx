/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayLayout from '@clayui/layout';
import ClayModal from '@clayui/modal';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';
import {useDropzone} from 'react-dropzone';

import ApiHelper from '../../services/ApiHelper';
import DragZoneBackground from './DragZoneBackground';
import {LoadingMessage} from './LoadingMessage';
import {FieldPicker} from './forms';

import '../../../css/components/MultipleFileUploader.scss';
import {AssetLibrary} from '../../types/AssetLibrary';

interface FileData {
	errorMessage: string;
	failed: boolean;
	file: File;
	name: string;
	size: number;
}

const getBase64 = (file: File): Promise<string> => {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();
		reader.onload = () => {
			if (typeof reader.result === 'string') {
				resolve(reader.result.split(',')[1]);
			}
			else {
				reject(new Error('FileReader did not return a string.'));
			}
		};
		reader.onerror = reject;
		reader.readAsDataURL(file);
	});
};

export default function MultipleFileUploader({
	assetLibraries,
	onModalClose,
	onUploadComplete,
	parentObjectEntryFolderExternalReferenceCode,
}: {
	assetLibraries: AssetLibrary[];
	onModalClose: () => void;
	onUploadComplete: ({
		assetLibrary,
		failedFiles,
		successFiles,
	}: {
		assetLibrary: AssetLibrary;
		failedFiles: string[];
		successFiles: string[];
	}) => void;
	parentObjectEntryFolderExternalReferenceCode: string;
}) {
	const [filesData, setFilesData] = useState<FileData[]>([]);
	const [groupId, setGroupId] = useState(
		assetLibraries.length === 1 ? assetLibraries[0].groupId : ''
	);
	const [isLoading, setIsLoading] = useState(false);

	const {getInputProps, getRootProps, isDragActive} = useDropzone({
		multiple: true,
		onDropAccepted: (acceptedFiles) => {
			const newFilesToUpload = acceptedFiles.map((file) => ({
				errorMessage: '',
				failed: false,
				file,
				name: file.name,
				size: file.size,
			}));

			setFilesData((prevFilesData) => {
				const currentIds = new Set(
					prevFilesData.map((fileData) => fileData.name)
				);
				const uniqueNewFiles = newFilesToUpload.filter(
					(nf) => !currentIds.has(nf.name)
				);

				return [...prevFilesData, ...uniqueNewFiles];
			});
		},
	});

	const findAssetLibrary = (groupId: string) =>
		assetLibraries.find(
			(assetLibrary) => assetLibrary.groupId.toString() === groupId
		);

	const handleRemoveFile = (fileNameToRemove: string) => {
		setFilesData((prevFilesData) =>
			prevFilesData.filter((file) => file.name !== fileNameToRemove)
		);
	};

	const handleButtonClick = () => {
		setIsLoading(true);

		const failedFiles: FileData[] = [];
		const uploadedFiles: string[] = [];

		Promise.allSettled(
			filesData.map(async (fileData: FileData) => {
				const fileBase64 = await getBase64(fileData.file);

				const {error} = await ApiHelper.post(
					`/o/cms/basic-documents/scopes/${groupId}`,
					{
						file: {
							fileBase64,
							name: fileData.name,
						},
						objectEntryFolderExternalReferenceCode:
							parentObjectEntryFolderExternalReferenceCode ||
							'L_FILES',
						title: fileData.name,
					}
				);

				if (error) {
					failedFiles.push({
						...fileData,
						errorMessage: error,
						failed: true,
					});
				}
				else {
					uploadedFiles.push(fileData.name);
				}

				return true;
			})
		).then(() => {
			setIsLoading(false);

			setFilesData(failedFiles);

			if (onUploadComplete) {
				onUploadComplete({
					assetLibrary:
						findAssetLibrary(String(groupId)) || assetLibraries[0],
					failedFiles: failedFiles.map((file) => file.name),
					successFiles: uploadedFiles,
				});
			}
		});
	};

	return (
		<form>
			<ClayModal.Body scrollable>
				{isLoading && (
					<div className="loading-message">
						<LoadingMessage />
					</div>
				)}

				<div
					{...getRootProps({
						className: classNames('dropzone', {
							'dropzone-drag-active': isDragActive,
						}),
					})}
				>
					<input {...getInputProps()} />

					<DragZoneBackground />
				</div>

				{assetLibraries.length > 1 && (
					<div className="mt-4">
						<FieldPicker
							helpMessage={Liferay.Language.get(
								'select-the-space-to-upload-the-file'
							)}
							items={assetLibraries.map(({groupId, name}) => ({
								label: name,
								value: groupId,
							}))}
							label={Liferay.Language.get('space')}
							name="groupId"
							onSelectionChange={(value: string) => {
								setGroupId(value);
							}}
							placeholder={Liferay.Language.get('select-a-space')}
							required
							selectedKey={groupId}
						/>
					</div>
				)}

				{!!filesData.length && (
					<div className={classNames('mt-4', {invisible: isLoading})}>
						<p className="text-3 text-secondary text-uppercase">
							{Liferay.Language.get('files-to-upload')}
						</p>

						{filesData.map((fileData, index) => (
							<>
								<ClayLayout.ContentRow
									className={classNames(
										'align-items-center',
										{
											'border-bottom':
												index < filesData.length - 1,
										}
									)}
									key={fileData.name}
									padded
								>
									<ClayLayout.ContentCol>
										<ClayButtonWithIcon
											displayType="secondary"
											size="sm"
											symbol="document"
										/>
									</ClayLayout.ContentCol>

									<ClayLayout.ContentCol
										className="text-3"
										expand
									>
										<span className="text-weight-semi-bold">
											{fileData.name}
										</span>

										<span className="text-secondary">
											{Liferay.Util.formatStorage(
												fileData.size,
												{
													addSpaceBeforeSuffix: true,
												}
											)}
										</span>
									</ClayLayout.ContentCol>

									<ClayLayout.ContentCol>
										<ClayButtonWithIcon
											aria-label={Liferay.Language.get(
												'remove-file'
											)}
											borderless
											displayType="secondary"
											onClick={() =>
												handleRemoveFile(fileData.name)
											}
											size="sm"
											symbol="times-circle"
										/>
									</ClayLayout.ContentCol>
								</ClayLayout.ContentRow>

								{fileData.errorMessage && (
									<span className="mt-2 text-danger">
										{fileData.errorMessage}
									</span>
								)}
							</>
						))}
					</div>
				)}
			</ClayModal.Body>

			{!!filesData.length && (
				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={onModalClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								disabled={isLoading}
								onClick={handleButtonClick}
							>
								{sub(
									Liferay.Language.get('upload-x'),
									`(${filesData.length})`
								)}
							</ClayButton>
						</ClayButton.Group>
					}
				></ClayModal.Footer>
			)}
		</form>
	);
}
