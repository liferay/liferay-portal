/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Button as ClayButton} from '@clayui/core';
import {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {useCallback, useState} from 'react';
import i18n from '~/utils/I18n';

import './AttachmentUploader.css';

import {useNavigate, useParams} from 'react-router-dom';
import {Liferay} from '~/services/liferay';

import useGCSUploadFile from '../../hooks/useGCSUploadFile';
import useGenerateFileMd5 from '../../hooks/useGenerateFileMd5';
import useTicketAttachmentsDelete from '../../hooks/useTicketAttachmentsDelete';
import useTicketAttachmentsInitiateUpload from '../../hooks/useTicketAttachmentsInitiateUpload';
import DropzoneUpload from './components/DropzoneUpload';
import FileList from './components/FileList';

export function isMd5HashEqual(localMd5: string, gcpMd5Hash: string): boolean {
	const localBase24Md5 = new Uint8Array(
		localMd5.match(/.{1,2}/g)!.map((byte) => parseInt(byte, 16))
	);

	return btoa(String.fromCharCode(...localBase24Md5)) === gcpMd5Hash;
}

const AttachmentUploader = () => {
	const [comment, setComment] = useState<string>('');
	const [file, setFile] = useState<File>();
	const [hasPersonalData, setHasPersonalData] = useState<boolean>(false);

	const navigate = useNavigate();
	const {ticketId} = useParams();

	const {deleteAttachment} = useTicketAttachmentsDelete();

	const {
		error: ticketAttachmentInitiateUploadError,
		initiateUpload,
		loading: ticketAttachmentInitiateUploadLoading,
		ticketAttachmentId: initiatedTicketAttachmentId,
	} = useTicketAttachmentsInitiateUpload();

	const {
		abortGenerateMd5,
		error: generateMd5Error,
		generateMd5,
		loading: generateMd5Loading,
	} = useGenerateFileMd5();

	const {
		abortUpload: abortGCSUpload,
		error: gcsUploadError,
		loading: gcsUploadFileLoading,
		progress: gcsUploadProgress,
		uploadFile,
	} = useGCSUploadFile();

	const isLoading =
		gcsUploadFileLoading ||
		generateMd5Loading ||
		ticketAttachmentInitiateUploadLoading;

	const _handleCloseOnClick = () => {
		if (window.history.length > 1) {
			window.history.back();
		}
		else {
			window.location.href = window.location.origin;
		}
	};

	const _handleDropzoneOnDropAccepted = useCallback(
		(acceptedFile: File) => {
			setFile(acceptedFile);
		},
		[setFile]
	);

	const _handleUploadOnClick = useCallback(async () => {
		if (!file || !ticketId) {
			return;
		}

		const calculatedMd5 = await generateMd5({file});

		if (!calculatedMd5 || generateMd5Error) {
			Liferay.Util.openToast({
				message: i18n.translate(
					'md5-hash-generation-failed-please-try-again'
				),
				title: i18n.translate('error'),
				type: 'danger',
			});

			return;
		}

		const initiationResult = await initiateUpload({
			fileMd5: calculatedMd5,
			fileName: file.name,
			fileSize: file.size.toString(),
			ticketId: ticketId as string,
		});

		if (!initiationResult || ticketAttachmentInitiateUploadError) {
			Liferay.Util.openToast({
				message: i18n.translate(
					'failed-to-initiate-upload-please-try-again'
				),
				title: i18n.translate('error'),
				type: 'danger',
			});

			return;
		}

		await uploadFile({
			accountKey: initiationResult.accountKey,
			comment,
			file,
			navigateFn: navigate,
			sessionURL: initiationResult.gcsSessionURL,
			ticketAttachmentId: initiationResult.ticketAttachmentId,
			ticketId: ticketId as string,
		});

		if (gcsUploadError) {
			Liferay.Util.openToast({
				message: i18n.translate('file-upload-failed-please-try-again'),
				title: i18n.translate('error'),
				type: 'danger',
			});

			return;
		}

		setFile(undefined);
		setComment('');
		setHasPersonalData(false);
	}, [
		comment,
		file,
		gcsUploadError,
		generateMd5,
		generateMd5Error,
		initiateUpload,
		navigate,
		setComment,
		setFile,
		setHasPersonalData,
		ticketAttachmentInitiateUploadError,
		ticketId,
		uploadFile,
	]);

	const _handleCancelUpload = useCallback(async () => {
		abortGenerateMd5();
		abortGCSUpload();

		const currentTicketAttachmentId = initiatedTicketAttachmentId;

		if (currentTicketAttachmentId) {
			try {
				await deleteAttachment({
					ticketAttachmentId: currentTicketAttachmentId,
				});
			}
			catch (error) {
				console.error(
					'Failed to delete attachment during cancel:',
					error
				);
			}
		}

		setComment('');
		setFile(undefined);
		setHasPersonalData(false);
	}, [
		abortGCSUpload,
		abortGenerateMd5,
		deleteAttachment,
		initiatedTicketAttachmentId,
		setComment,
		setFile,
		setHasPersonalData,
	]);

	const _handleRemoveFileFromList = () => {
		setFile(undefined);
	};

	return (
		<div className="attachment-container mt-4">
			<div className="attachment-uploader">
				<div className="d-flex text-neutral-10">
					<div className="h2">
						{i18n.sub('attach-file-to-ticket-x', [ticketId || ''])}
					</div>
				</div>

				<div className="mt-4">
					<div>
						<div className="attachment-title h5 text-neutral-9">
							{i18n.translate('attachment')}

							<span className="inline-item-after reference-mark text-warning">
								<ClayIcon symbol="asterisk" />
							</span>
						</div>

						<span className="text-neutral-8">
							{i18n.translate(
								'select-a-local-file-to-upload-only-one-file-can-be-attached-at-a-time'
							)}
						</span>
					</div>

					{!file && (
						<div className="dropzone-upload">
							<DropzoneUpload
								buttonText={i18n.translate('select-a-file')}
								onDropAccepted={_handleDropzoneOnDropAccepted}
								title={i18n.translate(
									'drag-and-drop-to-upload-or'
								)}
							/>
						</div>
					)}

					{!!file && (
						<div className="file-list-item">
							<FileList
								file={file}
								isInitializing={generateMd5Loading}
								isUploading={isLoading}
								onDelete={
									isLoading
										? _handleCancelUpload
										: _handleRemoveFileFromList
								}
								progress={gcsUploadProgress}
							/>
						</div>
					)}
					<div className="h5 text-neutral-9">
						{i18n.translate('leave-a-comment')}
					</div>

					<div className="attach-input mb-4">
						<ClayInput
							component="textarea"
							disabled={isLoading}
							onChange={(event) => setComment(event.target.value)}
							placeholder={i18n.translate(
								'add-a-description-of-the-file-related-to-this-ticket'
							)}
							type="text"
							value={comment}
						/>
					</div>

					<div className="attachment-uploader-support-text ml-2">
						<ClayCheckbox
							checked={hasPersonalData || false}
							disabled={isLoading}
							label={i18n.translate(
								'please-check-this-box-if-the-file-you-upload-does-not-contain-any-personal-data-and-therefore-can-be-uploaded-to-and-accessed-from-any-liferay-support-location-globally'
							)}
							onChange={(event) =>
								setHasPersonalData(event.target.checked)
							}
						/>
					</div>

					<div className="d-flex my-4">
						<ClayButton
							aria-label="Close"
							className="ml-auto mt-2"
							disabled={isLoading}
							displayType="secondary"
							onClick={_handleCloseOnClick}
						>
							{i18n.translate('close')}
						</ClayButton>

						<ClayButton
							aria-label="Upload"
							className="ml-3 mt-2"
							disabled={!file || !hasPersonalData || isLoading}
							displayType="primary"
							onClick={_handleUploadOnClick}
						>
							{i18n.translate('upload')}
						</ClayButton>
					</div>
				</div>
			</div>
		</div>
	);
};

export default AttachmentUploader;
