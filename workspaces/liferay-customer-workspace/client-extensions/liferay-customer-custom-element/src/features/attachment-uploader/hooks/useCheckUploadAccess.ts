/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';
import {Liferay} from '~/services/liferay';

type ErrorCode =
	| 'FORBIDDEN_ACCESS'
	| 'INVALID_TICKET_NUMBER'
	| 'TICKET_IS_CLOSED'
	| 'UNEXPECTED_ERROR'
	| 'UNKNOWN'
	| 'ZENDESK_ORGANIZATION_ERROR';

interface IResponse {
	errorCode: ErrorCode | null;
	hasAccess: boolean | null;
	loading: boolean;
}

export default function useCheckUploadAccess(): IResponse {
	const {ticketId} = useParams();

	const [loading, setLoading] = useState(true);
	const [hasAccess, setHasAccess] = useState<boolean | null>(null);
	const [errorCode, setErrorCode] = useState<ErrorCode | null>(null);

	useEffect(() => {
		if (!ticketId) {
			setLoading(false);
			setHasAccess(false);
			setErrorCode('INVALID_TICKET_NUMBER');

			return;
		}

		const controller = new AbortController();

		const fetchAccess = async () => {
			try {
				const response =
					await Liferay.OAuth2Client.FromUserAgentApplication(
						'liferay-customer-etc-spring-boot-oaua'
					).fetch(
						`/tickets/${ticketId}/ticket-attachments/upload-access-check`,
						{
							headers: {
								'Content-Type': 'application/json',
							},
							method: 'GET',
							signal: controller.signal,
						}
					);

				if (response.ok) {
					setHasAccess(true);
				}
			}
			catch (error: any) {
				const errorCode = await error.text();

				setHasAccess(false);

				if (!controller.signal.aborted) {
					switch (errorCode) {
						case 'FORBIDDEN_ACCESS':
						case 'TICKET_IS_CLOSED':
						case 'INVALID_TICKET_NUMBER':
						case 'ZENDESK_ORGANIZATION_ERROR':
						case 'UNEXPECTED_ERROR':
							setErrorCode(errorCode);
							break;
						default:
							setErrorCode('UNEXPECTED_ERROR');
					}
				}
			}
			finally {
				setLoading(false);
			}
		};

		fetchAccess();

		return () => controller.abort();
	}, [ticketId]);

	return {errorCode, hasAccess, loading};
}
