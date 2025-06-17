/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useState} from 'react';
import {Liferay} from '~/services/liferay';

interface IParams {
	ticketAttachmentId: string;
}

interface IProps {
	deleteAttachment: (params: IParams) => Promise<void>;
	error: Error | null;
	loading: boolean;
}

const useTicketAttachmentsDelete = (): IProps => {
	const [error, setError] = useState<Error | null>(null);
	const [loading, setLoading] = useState(false);

	const deleteAttachment = useCallback(async (params: IParams) => {
		setError(null);
		setLoading(true);

		const {ticketAttachmentId} = params;

		try {
			const response =
				await Liferay.OAuth2Client.FromUserAgentApplication(
					'liferay-customer-etc-spring-boot-oaua'
				).fetch(`/ticket-attachments/${ticketAttachmentId}`, {
					method: 'DELETE',
				});

			if (!response.ok) {
				throw new Error(
					`Failed to delete attachment: ${response.text()}`
				);
			}
		}
		catch (deleteError) {
			console.error('Delete attachment error:', deleteError);
			setError(
				deleteError instanceof Error
					? deleteError
					: new Error(String(deleteError))
			);
		}
		finally {
			setLoading(false);
		}
	}, []);

	return {deleteAttachment, error, loading};
};

export default useTicketAttachmentsDelete;
