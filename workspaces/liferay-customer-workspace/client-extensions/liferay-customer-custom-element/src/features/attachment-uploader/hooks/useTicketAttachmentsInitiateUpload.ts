/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useRef, useState} from 'react';
import {Liferay} from '~/services/liferay';
import {IUpload} from '~/utils/types';

interface IParams {
	fileMd5: string;
	fileName: string;
	fileSize: string;
	ticketId: string;
}

interface IResponse {
	success: boolean;
	uploadProperties?: IUpload;
}

interface IProps {
	abort: () => void;
	gcsSessionURL: string;
	initiateUpload: (params: IParams) => Promise<IResponse | null>;
	loading: boolean;
	ticketAttachmentId: string;
}

const useTicketAttachmentsInitiateUpload = (): IProps => {
	const [loading, setLoading] = useState(false);
	const [gcsSessionURL, setGCSSessionURL] = useState('');
	const [ticketAttachmentId, setTicketAttachmentId] = useState('');

	const abortControllerRef = useRef<AbortController>();

	const abort = useCallback(() => {
		if (abortControllerRef.current) {
			abortControllerRef.current.abort();
		}
	}, []);

	const initiateUpload = useCallback(
		async (params: IParams): Promise<IResponse | null> => {
			setLoading(true);

			abortControllerRef.current = new AbortController();

			const {fileMd5, fileName, fileSize, ticketId} = params;

			try {
				const response: Response =
					(await Liferay.OAuth2Client.FromUserAgentApplication(
						'liferay-customer-etc-spring-boot-oaua'
					).fetch('/ticket-attachments/initiate-upload', {
						body: JSON.stringify({
							fileName,
							fileSize,
							gcsSessionURL: sessionStorage.getItem(
								`gcsSessionURL:${fileMd5}`
							),
							md5Checksum: fileMd5,
							ticketId,
						}),
						method: 'POST',
						signal: abortControllerRef.current.signal,
					})) as unknown as Response;

				const responseJSON = await response.json();

				sessionStorage.setItem(
					`gcsSessionURL:${fileMd5}`,
					responseJSON.gcsSessionURL
				);

				setGCSSessionURL(responseJSON.gcsSessionURL);
				setTicketAttachmentId(responseJSON.ticketAttachmentId);

				return {
					success: true,
					uploadProperties: {
						accountKey: responseJSON.accountKey,
						gcsSessionURL: responseJSON.gcsSessionURL,
						ticketAttachmentId: responseJSON.ticketAttachmentId,
					},
				};
			}
			catch (uploadError) {
				if ((uploadError as any).name === 'AbortError') {
					return {
						success: false,
					};
				}

				if ((uploadError as any).status === 409) {
					return {
						success: false,
						uploadProperties: {
							attachmentName: fileName,
							errorCode: 'ATTACHMENT_ALREADY_EXISTS',
							ticketId,
						},
					};
				}

				return {
					success: false,
					uploadProperties: {
						errorCode: 'UNEXPECTED_ERROR',
						errorMessage: String(uploadError),
					},
				};
			}
			finally {
				setLoading(false);
			}
		},
		[]
	);

	return {
		abort,
		gcsSessionURL,
		initiateUpload,
		loading,
		ticketAttachmentId,
	};
};

export default useTicketAttachmentsInitiateUpload;
