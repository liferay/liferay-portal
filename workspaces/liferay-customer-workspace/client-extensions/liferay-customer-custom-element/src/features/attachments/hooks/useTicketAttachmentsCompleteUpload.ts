/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useState} from 'react';
import {Liferay} from '~/services/liferay';

interface IParams {
	comment: string;
	fileMd5: string;
	ticketAttachmentId: string;
}

interface IProps {
	completeUpload: (params: IParams) => Promise<void>;
	loading: boolean;
}

const useTicketAttachmentsCompleteUpload = (): IProps => {
	const [loading, setLoading] = useState(false);

	const completeUpload = useCallback(async (params: IParams) => {
		setLoading(true);

		const {comment, fileMd5, ticketAttachmentId} = params;

		try {
			const response: Response =
				(await Liferay.OAuth2Client.FromUserAgentApplication(
					'liferay-customer-etc-spring-boot-oaua'
				).fetch(
					`/ticket-attachments/${ticketAttachmentId}/complete-upload`,
					{
						body: JSON.stringify({
							commentBody: comment,
						}),
						method: 'POST',
					}
				)) as unknown as Response;

			if (!response.ok) {
				throw new Error(
					`Failed to complete upload: ${response.text()}`
				);
			}

			sessionStorage.removeItem(`gcsSessionURL:${fileMd5}`);
		}
		catch (uploadError) {
			console.error('Complete upload error:', uploadError);

			throw uploadError;
		}
		finally {
			setLoading(false);
		}
	}, []);

	return {completeUpload, loading};
};

export default useTicketAttachmentsCompleteUpload;
