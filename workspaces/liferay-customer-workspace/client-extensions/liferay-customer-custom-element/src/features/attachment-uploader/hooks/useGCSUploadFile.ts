/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useState} from 'react';
import {IUpload} from '~/utils/types';

import useGCSGetUploadOffset from './useGCSGetUploadOffset';
import useTicketAttachmentsCompleteUpload from './useTicketAttachmentsCompleteUpload';

interface IParams {
	accountKey: string;
	comment: string;
	file: File;
	gcsSessionURL: string;
	ticketAttachmentId: string;
	ticketId: string;
}

interface IResponse {
	success: boolean;
	uploadProperties?: IUpload;
}

interface IProps {
	abortUpload: () => void;
	loading: boolean;
	progress: number;
	uploadFile: (params: IParams) => Promise<IResponse>;
}

const useGCSUploadFile = (): IProps => {
	const [abortController, setAbortController] =
		useState<AbortController | null>(null);
	const [loading, setLoading] = useState(false);
	const [progress, setProgress] = useState(0);

	const {
		error: gcsGetUploadOffsetError,
		getUploadOffset,
		loading: gcsGetUploadOffsetLoading,
	} = useGCSGetUploadOffset();

	const {
		completeUpload,
		error: completeUploadError,
		loading: completeUploadLoading,
	} = useTicketAttachmentsCompleteUpload();

	const uploadFile = useCallback(
		async (params: IParams) => {
			const {
				accountKey,
				comment,
				file,
				gcsSessionURL,
				ticketAttachmentId,
				ticketId,
			} = params;

			setLoading(true);
			setProgress(0);

			let uploadFailed = false;
			const maxRetries = 5;
			const retryDelay = (attempt: number) => 500 * Math.pow(2, attempt);
			const controller = new AbortController();
			setAbortController(controller);

			try {
				if (!file) {
					throw new Error('Attachment file is missing.');
				}

				const chunkSize = 25 * 1024 * 1024;
				const totalSize = file.size;

				const offset = await getUploadOffset({
					gcsSessionURL,
					totalSize,
				});

				if (gcsGetUploadOffsetError) {
					throw gcsGetUploadOffsetError;
				}

				let chunkStart = offset;

				if (chunkStart >= totalSize && totalSize > 0) {
					setProgress(100);
				}
				else if (totalSize === 0) {
					setProgress(100);
				}

				while (chunkStart < totalSize) {
					if (controller.signal.aborted) {
						uploadFailed = true;

						break;
					}

					const chunkEnd = Math.min(
						chunkStart + chunkSize,
						totalSize
					);
					const chunk = file.slice(chunkStart, chunkEnd);
					const contentRange = `bytes ${chunkStart}-${chunkEnd - 1}/${totalSize}`;

					let successInChunkUpload = false;
					let attempt = 0;

					while (!successInChunkUpload && attempt < maxRetries) {
						if (controller.signal.aborted) {
							uploadFailed = true;
							break;
						}
						try {
							const response = await fetch(gcsSessionURL, {
								body: chunk,
								headers: {
									'Content-Length': chunk.size.toString(),
									'Content-Range': contentRange,
								},
								method: 'PUT',
								signal: controller.signal,
							});

							if (response.ok || response.status === 308) {
								successInChunkUpload = true;
								chunkStart = chunkEnd;

								const uploadPercentage = Math.round(
									(chunkStart / totalSize) * 100
								);
								setProgress(uploadPercentage);
							}
							else {
								throw new Error(
									`Chunk upload failed: ${response.status} ${response.statusText}`
								);
							}
						}
						catch (chunkError) {
							if (controller.signal.aborted) {
								uploadFailed = true;

								break;
							}

							console.error(
								`Upload attempt ${attempt + 1} for chunk ${chunkStart}-${chunkEnd - 1} failed:`,
								chunkError
							);
							attempt++;

							if (attempt >= maxRetries) {
								uploadFailed = true;
								if (chunkError instanceof Error) {
									throw chunkError;
								}
								throw new Error(
									'Max retries reached for chunk upload.'
								);
							}
							else {
								await new Promise((resolve) =>
									setTimeout(resolve, retryDelay(attempt))
								);
							}
						}
					}

					if (!successInChunkUpload || uploadFailed) {
						uploadFailed = true;
						break;
					}
				}

				setAbortController(null);

				if (uploadFailed || controller.signal.aborted) {
					return {success: false};
				}

				await completeUpload({
					comment,
					ticketAttachmentId: String(ticketAttachmentId),
				});

				if (completeUploadError) {
					throw completeUploadError;
				}

				if (!gcsGetUploadOffsetLoading && !completeUploadLoading) {
					return {
						success: true,
						uploadProperties: {
							attachmentName: file.name,
							ticketId,
							uploadAccountKey: accountKey,
						},
					};
				}
				else {
					return {
						success: false,
						uploadProperties: {
							errorCode: 'UNEXPECTED_ERROR',
						},
					};
				}
			}
			catch (uploadError) {
				setProgress(0);

				return {
					success: false,
					uploadProperties: {
						errorCode: 'UNEXPECTED_ERROR',
						errorMessage: String(uploadError),
						ticketId,
						uploadAccountKey: accountKey,
					},
				};
			}
			finally {
				setLoading(false);
				setAbortController(null);
			}
		},
		[
			completeUpload,
			completeUploadError,
			completeUploadLoading,
			gcsGetUploadOffsetError,
			getUploadOffset,
			gcsGetUploadOffsetLoading,
		]
	);

	const abortUpload = useCallback(() => {
		if (abortController) {
			abortController.abort();

			sessionStorage.removeItem('gcsSessionURL');
			setLoading(false);
			setProgress(0);
		}
	}, [abortController]);

	return {abortUpload, loading, progress, uploadFile};
};

export default useGCSUploadFile;
