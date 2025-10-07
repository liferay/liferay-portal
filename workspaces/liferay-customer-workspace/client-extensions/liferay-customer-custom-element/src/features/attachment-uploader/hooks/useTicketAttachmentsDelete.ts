/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useState} from 'react';
import {Liferay} from '~/services/liferay';

interface IParams {
	gcsSessionURL: string;
	ticketAttachmentId: string;
}

interface IProps {
	deleteAttachment: (params: IParams) => Promise<void>;
	loading: boolean;
}

const useTicketAttachmentsDelete = (): IProps => {
	const [loading, setLoading] = useState(false);

	const deleteAttachment = useCallback(async (params: IParams) => {
		setLoading(true);

		const {gcsSessionURL, ticketAttachmentId} = params;

		try {
			const ticketAttachmentResponse = await fetch(
				`${window.location.origin}/o/c/ticketattachments/${ticketAttachmentId}`,
				{
					headers: {
						'x-csrf-token': Liferay.authToken,
					},
					method: 'DELETE',
				}
			);

			if (!ticketAttachmentResponse.ok) {
				throw new Error(
					`Failed to delete ticket attachment: ${ticketAttachmentResponse.text()}`
				);
			}

			const gcpResponse = await fetch(gcsSessionURL, {
				headers: {
					'Content-Length': '0',
				},
				method: 'DELETE',
			});

			if (gcpResponse.status !== 499) {
				throw new Error(
					`Failed to delete gcp file: ${gcpResponse.text()}`
				);
			}
		}
		catch (deleteError) {
			console.error(deleteError);
		}
		finally {
			setLoading(false);
		}
	}, []);

	return {deleteAttachment, loading};
};

export default useTicketAttachmentsDelete;
