/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Button as ClayButton} from '@clayui/core';
import {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {useCallback, useState} from 'react';
import useJiraTicketURL from '~/hooks/useJiraTicketURL';
import i18n from '~/utils/I18n';

import './AttachmentUploader.css';

import {useParams} from 'react-router-dom';
import {IUpload} from '~/utils/types';

import DropzoneUpload from '../../components/DropzoneUpload';
import FileList from '../../components/FileList';
import useCheckUploadAccess from '../../hooks/useCheckUploadAccess';
import useGCSUploadFile from '../../hooks/useGCSUploadFile';
import useGenerateFileMd5 from '../../hooks/useGenerateFileMd5';
import useTicketAttachmentsDelete from '../../hooks/useTicketAttachmentsDelete';
import useTicketAttachmentsInitiateUpload from '../../hooks/useTicketAttachmentsInitiateUpload';
import {
	CommentPostFailed,
	ServerUnavailable,
	UploadConfirmation,
} from '../AttachmentUploaderMessages';

interface IProps {
	setUploadStateData: React.Dispatch<React.SetStateAction<IUpload | null>>;
	uploadStateData: IUpload | null;
}

const AttachmentUploader = ({setUploadStateData, uploadStateData}: IProps) => {
	const {loading: accessCheckLoading} = useCheckUploadAccess();

	const [comment, setComment] = useState<string>('');
	const [file, setFile] = useState<File>();
	const [hasPersonalData, setHasPersonalData] = useState<boolean>(false);

	const [uploadResult, setUploadResult] = useState<
		'IDLE' | 'SUCCESS' | 'COMMENT_ERROR' | 'SERVER_ERROR'
	>('IDLE');

	const {ticketId} = useParams();

	const ticketURL = useJiraTicketURL(ticketId ?? '');

	const {deleteAttachment} = useTicketAttachmentsDelete();

	const {
		abort: abortInitiateUpload,
		gcsSessionURL: initiatedGCSSessionURL,
		initiateUpload,
		loading: ticketAttachmentInitiateUploadLoading,
		ticketAttachmentId: initiatedTicketAttachmentId,
	} = useTicketAttachmentsInitiateUpload();

	const {
		abortGenerateMd5,
		generateMd5,
		loading: generateMd5Loading,
	} = useGenerateFileMd5();

	const {
		abortUpload: abortGCSUpload,
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

		const calculatedMd5 = await generateMd5({file, ticketId});

		if (!calculatedMd5.success || !calculatedMd5.hash) {
			setUploadStateData(calculatedMd5.uploadProperties ?? null);
			setUploadResult('SERVER_ERROR');

			return;
		}

		const initiationResult = await initiateUpload({
			fileMd5: calculatedMd5.hash,
			fileName: file.name,
			fileSize: file.size.toString(),
			ticketId: ticketId as string,
		});

		if (!initiationResult?.success) {
			setUploadStateData(initiationResult?.uploadProperties ?? null);

			if (
				initiationResult?.uploadProperties?.errorCode ===
				'ATTACHMENT_ALREADY_EXISTS'
			) {
				return;
			}

			setUploadResult('SERVER_ERROR');

			return;
		}

		const uploadResponse = await uploadFile({
			accountKey: initiationResult.uploadProperties?.accountKey ?? '',
			comment,
			file,
			fileMd5: calculatedMd5.hash,
			gcsSessionURL:
				initiationResult.uploadProperties?.gcsSessionURL ?? '',
			ticketAttachmentId:
				initiationResult.uploadProperties?.ticketAttachmentId ?? '',
			ticketId,
		});

		if (uploadResponse.success) {
			setFile(undefined);
			setComment('');
			setHasPersonalData(false);

			setUploadStateData(uploadResponse.uploadProperties ?? null);
			setUploadResult('SUCCESS');
		}
		else {
			setUploadResult('SERVER_ERROR');
			setUploadStateData(uploadResponse.uploadProperties ?? null);
		}
	}, [
		comment,
		file,
		generateMd5,
		initiateUpload,
		setComment,
		setFile,
		setHasPersonalData,
		ticketId,
		uploadFile,
		setUploadStateData,
	]);

	const _handleCancelUpload = useCallback(async () => {
		abortGCSUpload();
		abortGenerateMd5();
		abortInitiateUpload();

		if (initiatedGCSSessionURL && initiatedTicketAttachmentId) {
			await deleteAttachment({
				gcsSessionURL: initiatedGCSSessionURL,
				ticketAttachmentId: initiatedTicketAttachmentId,
			});
		}

		setComment('');
		setFile(undefined);
		setHasPersonalData(false);
	}, [
		abortGCSUpload,
		abortGenerateMd5,
		abortInitiateUpload,
		deleteAttachment,
		initiatedGCSSessionURL,
		initiatedTicketAttachmentId,
		setComment,
		setFile,
		setHasPersonalData,
	]);

	const _handleRemoveFileFromList = () => {
		setFile(undefined);
	};

	if (accessCheckLoading) {
		return null;
	}

	if (uploadResult === 'SUCCESS' && uploadStateData) {
		return (
			<UploadConfirmation
				attachmentName={uploadStateData.attachmentName ?? ''}
				ticketURL={ticketURL ?? ''}
				uploadAccountKey={uploadStateData.uploadAccountKey ?? ''}
			/>
		);
	}

	if (uploadResult === 'COMMENT_ERROR' && uploadStateData) {
		return (
			<CommentPostFailed
				ticketURL={ticketURL ?? ''}
				uploadAccountKey={uploadStateData.uploadAccountKey ?? ''}
			/>
		);
	}

	if (uploadResult === 'SERVER_ERROR' && uploadStateData) {
		return (
			<ServerUnavailable
				ticketURL={ticketURL ?? ''}
				uploadAccountKey={uploadStateData.uploadAccountKey ?? ''}
			/>
		);
	}

	return (
		<div className="attachment-uploader mt-4">
			<div className="attachment-uploader-container">
				<div className="d-flex text-neutral-10">
					<div className="h2">
						<p
							dangerouslySetInnerHTML={{
								__html: i18n.sub('attach-file-to-ticket-x', [
									'<a href="' +
										ticketURL +
										'">' +
										ticketId +
										'</a>',
								]),
							}}
						/>
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

					<div className="attachment-input mb-4">
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
