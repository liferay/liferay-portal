/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayModal from '@clayui/modal';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import {useFormik} from 'formik';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';
import {Accept, useDropzone} from 'react-dropzone';
import {v4 as uuidv4} from 'uuid';

import DragZoneBackground from './DragZoneBackground';
import {LoadingMessage} from './LoadingMessage';

import '../../../css/components/MultipleFileUploader.scss';

import {FieldBase} from 'frontend-js-components-web';

import SpaceSelector from '../../common/components/SpaceSelector';
import {required, validate} from '../../common/components/forms/validations';
import {AssetLibrary} from '../../common/types/AssetLibrary';
import {Space} from '../../common/types/Space';
import FailedFiles from './FailedFiles';
export interface FileData {
	errorMessage?: string;
	failed?: boolean;
	file: File;
	name: string;
	size: number;
}
export interface UploadMessages {
	anotherFileButton: string;
	filesToUpload: string;
	loadingMessageDescription: string;
	loadingMessageTitle: string;
	xFilesNotUploaded: string;
}

const DEFAULT_MESSAGES: UploadMessages = {
	anotherFileButton: Liferay.Language.get('upload-another-file'),
	filesToUpload: Liferay.Language.get('files-to-upload'),
	loadingMessageDescription: Liferay.Language.get(
		'closing-the-window-will-cancel-the-upload-process'
	),
	loadingMessageTitle: Liferay.Language.get(
		'the-upload-process-may-take-some-time'
	),
	xFilesNotUploaded: Liferay.Language.get('x-files-could-not-be-uploaded'),
};
export default function MultipleFileUploader({
	assetLibraries,
	buttonLabel,
	description,
	filesToUpload: initialFilesToUpload,
	groupId: initialGroupId,
	messages,
	onModalClose,
	onUploadComplete,
	uploadRequest,
	validExtensions = '*',
}: {
	assetLibraries?: AssetLibrary[];
	buttonLabel?: string;
	description?: string;
	filesToUpload?: FileData[];
	groupId?: number;
	messages?: Partial<UploadMessages>;
	onModalClose: () => void;
	onUploadComplete: ({
		assetLibrary,
		failedFiles,
		successFiles,
	}: {
		assetLibrary: AssetLibrary | null;
		failedFiles: string[];
		successFiles: string[];
	}) => void;
	uploadRequest: ({
		fileData,
		groupId,
	}: {
		fileData: FileData;
		groupId: string;
	}) => Promise<any>;
	validExtensions?: string;
}) {
	const [filesToUpload, setFilesToUpload] = useState<FileData[]>(
		initialFilesToUpload || []
	);
	const [failedFiles, setFiledFiles] = useState<FileData[]>([]);
	const [isLoading, setIsLoading] = useState(false);
	const [space, setSpace] = useState<Space>();

	const groupIdInputId = `${uuidv4()}groupId`;

	const {getInputProps, getRootProps, isDragActive} = useDropzone({
		accept:
			validExtensions === '*'
				? undefined
				: (validExtensions as unknown as Accept),
		multiple: true,
		noKeyboard: true,
		onDropAccepted: (acceptedFiles) => {
			const newFilesToUpload = acceptedFiles.map((file) => ({
				errorMessage: '',
				failed: false,
				file,
				name: file.name,
				size: file.size,
			}));

			setFilesToUpload((prevFilesToUpload) => {
				const currentIds = new Set(
					prevFilesToUpload.map((fileData) => fileData.name)
				);
				const uniqueNewFiles = newFilesToUpload.filter(
					(nf) => !currentIds.has(nf.name)
				);

				return [...prevFilesToUpload, ...uniqueNewFiles];
			});
		},
	});

	const mergedMessages = {...DEFAULT_MESSAGES, ...messages};

	const findAssetLibrary = (groupId: string) =>
		assetLibraries?.find(
			(assetLibrary) => assetLibrary.groupId.toString() === groupId
		);

	const handleRemoveFile = (fileNameToRemove: string) => {
		setFilesToUpload((prevFilesToUpload) =>
			prevFilesToUpload.filter((file) => file.name !== fileNameToRemove)
		);
	};

	const {errors, handleSubmit, setFieldValue, touched} = useFormik({
		initialValues: {
			groupId:
				initialGroupId ||
				(assetLibraries?.length === 1
					? assetLibraries?.[0].groupId
					: 0),
		},
		onSubmit: async (values) => {
			setIsLoading(true);

			const failedFiles: FileData[] = [];
			const uploadedFiles: string[] = [];

			Promise.allSettled(
				filesToUpload.map(async (fileData: FileData) => {
					const response = await uploadRequest({
						fileData,
						groupId: String(values.groupId),
					});

					const {error} = response;

					if (error) {
						failedFiles.push({
							...fileData,
							errorMessage: error,
							failed: true,
						});
					}
					else if (response.multipleErrors) {
						response.errors.map((item: any) => {
							failedFiles.push({
								...item,
								failed: true,
							});
						});
					}
					else {
						uploadedFiles.push(fileData.name);
					}

					return true;
				})
			).then(() => {
				setIsLoading(false);

				setFilesToUpload([]);
				setFiledFiles(failedFiles);

				if (onUploadComplete) {
					onUploadComplete({
						assetLibrary: assetLibraries
							? findAssetLibrary(String(values.groupId)) ||
								assetLibraries?.[0]
							: null,
						failedFiles: failedFiles.map((file) => file.name),
						successFiles: uploadedFiles,
					});
				}
			});
		},
		validate: (values) =>
			validate(
				{
					groupId: [required],
				},
				values
			),
	});

	return (
		<form className="multiple-file-uploader" onSubmit={handleSubmit}>
			<ClayModal.Body scrollable>
				{failedFiles.length ? (
					<FailedFiles
						errorMessage={mergedMessages.xFilesNotUploaded}
						failedFiles={failedFiles}
					/>
				) : (
					<>
						{isLoading && (
							<LoadingMessage
								description={
									mergedMessages.loadingMessageDescription
								}
								title={mergedMessages.loadingMessageTitle}
							/>
						)}

						{description && (
							<div className="mb-1">
								<p className="text-secondary">{description}</p>

								<span className="font-weight-semi-bold text-3">
									{Liferay.Language.get('file')}
								</span>
							</div>
						)}

						<div
							{...getRootProps({
								className: classNames('dropzone', {
									'dropzone-drag-active': isDragActive,
								}),
							})}
						>
							<input aria-hidden="true" {...getInputProps()} />

							<DragZoneBackground />
						</div>

						{assetLibraries && assetLibraries.length > 1 && (
							<div className="mt-4">
								<FieldBase
									errorMessage={
										touched.groupId
											? errors.groupId
											: undefined
									}
									helpMessage={Liferay.Language.get(
										'select-the-space-to-upload-the-file'
									)}
									id={groupIdInputId}
									label={Liferay.Language.get('space')}
									required
								>
									<SpaceSelector
										id={groupIdInputId}
										onSpaceChange={(space) => {
											setFieldValue(
												'groupId',
												space ? space.siteId : null
											);
											setSpace(space);
										}}
										space={space}
									/>
								</FieldBase>
							</div>
						)}

						{!!filesToUpload.length && (
							<div
								className={classNames('mt-4', {
									invisible: isLoading,
								})}
							>
								<h2 className="font-weight-semi-bold mb-3 text-3 text-secondary text-uppercase">
									{mergedMessages.filesToUpload}
								</h2>

								{filesToUpload.map((fileData, index) => (
									<div key={fileData.name}>
										<ClayLayout.ContentRow
											className={classNames(
												'align-items-center',
												{
													'border-bottom':
														index <
														filesToUpload.length -
															1,
												}
											)}
											padded
										>
											<ClayLayout.ContentCol>
												<ClaySticker
													className="sticker-border-secondary"
													displayType="secondary"
													size="lg"
												>
													<ClayIcon symbol="document" />
												</ClaySticker>
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
															addSpaceBeforeSuffix:
																true,
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
														handleRemoveFile(
															fileData.name
														)
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
									</div>
								))}
							</div>
						)}
					</>
				)}
			</ClayModal.Body>

			{!!filesToUpload.length && (
				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={onModalClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton disabled={isLoading} type="submit">
								{buttonLabel ||
									sub(
										Liferay.Language.get('upload-x'),
										`(${filesToUpload.length})`
									)}
							</ClayButton>
						</ClayButton.Group>
					}
				></ClayModal.Footer>
			)}

			{!!failedFiles.length && (
				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={() => setFiledFiles([])}
							>
								{mergedMessages.anotherFileButton}
							</ClayButton>

							<ClayButton onClick={onModalClose}>
								{Liferay.Language.get('done')}
							</ClayButton>
						</ClayButton.Group>
					}
				></ClayModal.Footer>
			)}
		</form>
	);
}
