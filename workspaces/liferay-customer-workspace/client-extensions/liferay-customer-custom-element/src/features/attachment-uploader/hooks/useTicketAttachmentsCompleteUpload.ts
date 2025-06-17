/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useState} from 'react';
import {Liferay} from '~/services/liferay';

interface IParams {
	comment: string;
	ticketAttachmentId: string;
}

interface IProps {
	completeUpload: (params: IParams) => Promise<void>;
	error: Error | null;
	loading: boolean;
}

const useTicketAttachmentsCompleteUpload = (): IProps => {
	const [loading, setLoading] = useState(false);
	const [error, setError] = useState<Error | null>(null);

	const completeUpload = useCallback(async (params: IParams) => {
		setLoading(true);
		setError(null);

		const {comment, ticketAttachmentId} = params;

		try {
			const response: Response =
				(await Liferay.OAuth2Client.FromUserAgentApplication(
					'liferay-customer-etc-spring-boot-oaua'
				).fetch(
					`/ticket-attachments/${ticketAttachmentId}/complete-upload`,
					{
						body: JSON.stringify({
							zendeskTicketCommentBody: comment,
						}),
						method: 'POST',
					}
				)) as unknown as Response;

			if (!response.ok) {
				throw new Error(
					`Failed to complete upload: ${response.text()}`
				);
			}

			sessionStorage.removeItem('gcsSessionURL');
		}
		catch (uploadError) {
			console.error('Complete upload error:', uploadError);
			setError(
				uploadError instanceof Error
					? uploadError
					: new Error(String(uploadError))
			);
		}
		finally {
			setLoading(false);
		}
	}, []);

	return {completeUpload, error, loading};
};

export default useTicketAttachmentsCompleteUpload;
