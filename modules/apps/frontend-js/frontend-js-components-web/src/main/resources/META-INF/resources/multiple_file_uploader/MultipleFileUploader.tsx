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
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';
import {Accept, useDropzone} from 'react-dropzone';

import DragZoneBackground from './DragZoneBackground';
import FailedFiles from './FailedFiles';
import {LoadingMessage} from './LoadingMessage';
import {
	FaildFile,
	FileData,
	UploadMessages,
	UploadRequestCallback,
} from './types';

import '../css/components/MultipleFileUploader.scss';

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

interface MultipleFileUploaderProps {
	buttonLabel?: string;

	description?: string;

	filesToUpload?: FileData[];

	formValidation: () => Promise<boolean>;

	messages?: Partial<UploadMessages>;

	onModalClose: () => void;

	onUploadComplete: ({
		failedFiles,
		successFiles,
	}: {
		failedFiles: string[];
		successFiles: string[];
	}) => void;

	scopeSelectorElement?: JSX.Element;

	uploadRequest: UploadRequestCallback;

	validExtensions?: string;
}

export default function MultipleFileUploader({
	buttonLabel,
	description,
	filesToUpload: initialFilesToUpload,
	formValidation,
	messages,
	onModalClose,
	onUploadComplete,
	scopeSelectorElement,
	uploadRequest,
	validExtensions = '*',
}: MultipleFileUploaderProps) {
	const [filesToUpload, setFilesToUpload] = useState<FileData[]>(
		initialFilesToUpload || []
	);
	const [failedFiles, setFiledFiles] = useState<FaildFile[]>([]);
	const [isLoading, setIsLoading] = useState(false);

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

	const handleRemoveFile = (fileNameToRemove: string) => {
		setFilesToUpload((prevFilesToUpload) =>
			prevFilesToUpload.filter((file) => file.name !== fileNameToRemove)
		);
	};

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		const isValid = await formValidation();

		if (!isValid) {
			return;
		}

		setIsLoading(true);

		const failedFiles: FaildFile[] = [];
		const uploadedFiles: string[] = [];

		Promise.allSettled(
			filesToUpload.map(async (fileData: FileData) => {
				const response = await uploadRequest({fileData});

				if ('error' in response) {
					failedFiles.push({
						...fileData,
						errorMessage: response.error,
						failed: true,
					});
				}
				else if ('multipleErrors' in response) {
					response.errors.map((item) => {
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
					failedFiles: failedFiles.map((file) => file.name),
					successFiles: uploadedFiles,
				});
			}
		});
	};

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

						{scopeSelectorElement}

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
