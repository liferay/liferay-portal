/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';
import {Liferay} from '~/services/liferay';

type ErrorCode =
	| 'FORBIDDEN_ACCESS'
	| 'INVALID_TICKET_ATTACHMENT'
	| 'INVALID_TICKET_NUMBER'
	| 'JIRA_ORGANIZATION_ERROR'
	| 'TICKET_IS_CLOSED'
	| 'UNEXPECTED_ERROR'
	| 'UNKNOWN';

interface IResponse {
	errorCode: ErrorCode | null;
	hasAccess: boolean | null;
	loading: boolean;
}

export default function useCheckAttachmentAccess(): IResponse {
	const {ticketAttachmentERC, ticketAttachmentId, ticketId} = useParams();

	const currentTicketId = ticketId;
	const currentTicketAttachmentId = ticketAttachmentId;
	const currentTicketAttachmentERC = ticketAttachmentERC;

	const isDownload = !!(
		currentTicketAttachmentId || currentTicketAttachmentERC
	);

	const [loading, setLoading] = useState(true);
	const [hasAccess, setHasAccess] = useState<boolean | null>(null);
	const [errorCode, setErrorCode] = useState<ErrorCode | null>(null);

	useEffect(() => {
		if (!isDownload && !currentTicketId) {
			setLoading(false);
			setHasAccess(false);
			setErrorCode('INVALID_TICKET_NUMBER');

			return;
		}

		const controller = new AbortController();

		const fetchAccess = async () => {
			try {
				let jiraIssueKey = currentTicketId;

				if (isDownload) {
					const ticketAttachmentEndpoint = currentTicketAttachmentId
						? `/o/c/ticketattachments/${currentTicketAttachmentId}`
						: `/o/c/ticketattachments/by-external-reference-code/${currentTicketAttachmentERC}`;

					const ticketAttachmentResponse = await Liferay.Util.fetch(
						ticketAttachmentEndpoint,
						{
							headers: {
								'Content-Type': 'application/json',
							},
							method: 'GET',
							signal: controller.signal,
						}
					);

					if (ticketAttachmentResponse.ok) {
						const data = await ticketAttachmentResponse.json();

						jiraIssueKey = data.jiraIssueKey;
					}
					else {
						setErrorCode('INVALID_TICKET_ATTACHMENT');
						setHasAccess(false);
						setLoading(false);

						return;
					}
				}

				const endpoint = `/tickets/${jiraIssueKey}/ticket-attachments/upload-access-check`;

				const response =
					await Liferay.OAuth2Client.FromUserAgentApplication(
						'liferay-customer-etc-spring-boot-oaua'
					).fetch(endpoint, {
						headers: {
							'Content-Type': 'application/json',
						},
						method: 'GET',
						signal: controller.signal,
					});

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
						case 'INVALID_TICKET_ATTACHMENT':
						case 'INVALID_TICKET_NUMBER':
						case 'JIRA_ORGANIZATION_ERROR':
						case 'TICKET_IS_CLOSED':
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
	}, [
		currentTicketId,
		currentTicketAttachmentId,
		currentTicketAttachmentERC,
		isDownload,
	]);

	return {errorCode, hasAccess, loading};
}
