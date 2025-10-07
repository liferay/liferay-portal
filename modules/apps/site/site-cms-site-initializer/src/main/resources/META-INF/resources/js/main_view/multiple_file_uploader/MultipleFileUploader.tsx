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
import {useDropzone} from 'react-dropzone';

import {FieldPicker} from '../../common/components/forms';
import DragZoneBackground from './DragZoneBackground';
import {LoadingMessage} from './LoadingMessage';

import '../../../css/components/MultipleFileUploader.scss';
import {required, validate} from '../../common/components/forms/validations';
import {AssetLibrary} from '../../common/types/AssetLibrary';
import FailedFiles from './FailedFiles';
export interface FileData {
	errorMessage?: string;
	failed?: boolean;
	file: File;
	name: string;
	size: number;
}

export default function MultipleFileUploader({
	assetLibraries,
	filesToUpload: initialFilesToUpload,
	onModalClose,
	onUploadComplete,
	uploadRequest,
}: {
	assetLibraries: AssetLibrary[];
	filesToUpload?: FileData[];
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
	uploadRequest: ({
		fileData,
		groupId,
	}: {
		fileData: FileData;
		groupId: string;
	}) => Promise<any>;
}) {
	const [filesToUpload, setFilesToUpload] = useState<FileData[]>(
		initialFilesToUpload || []
	);
	const [failedFiles, setFiledFiles] = useState<FileData[]>([]);
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

	const findAssetLibrary = (groupId: string) =>
		assetLibraries.find(
			(assetLibrary) => assetLibrary.groupId.toString() === groupId
		);

	const handleRemoveFile = (fileNameToRemove: string) => {
		setFilesToUpload((prevFilesToUpload) =>
			prevFilesToUpload.filter((file) => file.name !== fileNameToRemove)
		);
	};

	const {errors, handleSubmit, setFieldValue, touched, values} = useFormik({
		initialValues: {
			groupId:
				assetLibraries.length === 1 ? assetLibraries[0].groupId : 0,
		},
		onSubmit: async (values) => {
			setIsLoading(true);

			const failedFiles: FileData[] = [];
			const uploadedFiles: string[] = [];

			Promise.allSettled(
				filesToUpload.map(async (fileData: FileData) => {
					const {error} = await uploadRequest({
						fileData,
						groupId: String(values.groupId),
					});

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

				setFilesToUpload([]);
				setFiledFiles(failedFiles);

				if (onUploadComplete) {
					onUploadComplete({
						assetLibrary:
							findAssetLibrary(String(values.groupId)) ||
							assetLibraries[0],
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
					<FailedFiles failedFiles={failedFiles} />
				) : (
					<>
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
							<input aria-hidden="true" {...getInputProps()} />

							<DragZoneBackground />
						</div>

						{assetLibraries.length > 1 && (
							<div className="mt-4">
								<FieldPicker
									errorMessage={
										touched.groupId
											? errors.groupId
											: undefined
									}
									helpMessage={Liferay.Language.get(
										'select-the-space-to-upload-the-file'
									)}
									items={assetLibraries.map(
										({groupId, name}) => ({
											label: name,
											value: groupId,
										})
									)}
									label={Liferay.Language.get('space')}
									name="groupId"
									onSelectionChange={(value: string) => {
										setFieldValue('groupId', value);
									}}
									placeholder={`--${Liferay.Language.get('not-selected')}--`}
									required
									selectedKey={values.groupId}
									title={Liferay.Language.get(
										'select-the-space-to-upload-the-file'
									)}
								/>
							</div>
						)}

						{!!filesToUpload.length && (
							<div
								className={classNames('mt-4', {
									invisible: isLoading,
								})}
							>
								<h2 className="font-weight-semi-bold mb-3 text-3 text-secondary text-uppercase">
									{Liferay.Language.get('files-to-upload')}
								</h2>

								{filesToUpload.map((fileData, index) => (
									<>
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
											key={fileData.name}
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
									</>
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
								{sub(
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
								{Liferay.Language.get('upload-another-file')}
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
