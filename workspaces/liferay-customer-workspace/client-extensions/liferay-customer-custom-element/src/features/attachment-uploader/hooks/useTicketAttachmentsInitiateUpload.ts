/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useState} from 'react';
import {Liferay} from '~/services/liferay';

interface IParams {
	fileMd5: string;
	fileName: string;
	fileSize: string;
	ticketId: string;
}

interface IResponse {
	accountKey: string;
	gcsSessionURL: string;
	ticketAttachmentId: string;
}

interface IProps {
	error: Error | null;
	initiateUpload: (params: IParams) => Promise<IResponse | null>;
	loading: boolean;
	ticketAttachmentId: string;
}

const useTicketAttachmentsInitiateUpload = (): IProps => {
	const [error, setError] = useState<Error | null>(null);
	const [loading, setLoading] = useState(false);
	const [ticketAttachmentId, setTicketAttachmentId] = useState('');

	const initiateUpload = useCallback(
		async (params: IParams): Promise<IResponse | null> => {
			setError(null);
			setLoading(true);

			const {fileMd5, fileName, fileSize, ticketId} = params;

			try {
				const response: Response =
					(await Liferay.OAuth2Client.FromUserAgentApplication(
						'liferay-customer-etc-spring-boot-oaua'
					).fetch('/ticket-attachments/initiate-upload', {
						body: JSON.stringify({
							fileName,
							fileSize,
							gcsSessionURL:
								sessionStorage.getItem('gcsSessionURL'),
							md5Checksum: fileMd5,
							zendeskTicketId: ticketId,
						}),
						method: 'POST',
					})) as unknown as Response;

				if (!response.ok) {
					throw new Error(
						`Failed to initiate upload: ${response.text()}`
					);
				}

				const responseJSON = await response.json();

				setTicketAttachmentId(responseJSON.ticketAttachmentId);

				return {
					accountKey: responseJSON.accountKey,
					gcsSessionURL: responseJSON.gcsSessionURL,
					ticketAttachmentId: responseJSON.ticketAttachmentId,
				};
			}
			catch (uploadError) {
				console.error('Initiate upload error:', uploadError);
				setError(
					uploadError instanceof Error
						? uploadError
						: new Error(String(uploadError))
				);

				return null;
			}
			finally {
				setLoading(false);
			}
		},
		[]
	);

	return {
		error,
		initiateUpload,
		loading,
		ticketAttachmentId,
	};
};

export default useTicketAttachmentsInitiateUpload;
