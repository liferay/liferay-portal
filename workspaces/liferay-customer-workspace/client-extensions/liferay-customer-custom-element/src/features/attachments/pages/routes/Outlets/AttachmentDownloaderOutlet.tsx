/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';
import useCheckAttachmentAccess from '~/features/attachments/hooks/useCheckAttachmentAccess';
import {Liferay} from '~/services/liferay';
import i18n from '~/utils/I18n';

import AttachmentDownloader from '../../AttachmentDownloader';
import {
	AttachmentNotFound,
	ForbiddenAccessDownload,
	InvalidTicketAttachment,
	UnexpectedError,
} from '../../AttachmentMessages';

const renderErrorComponent = (errorCode: string | null) => {
	switch (errorCode) {
		case 'FORBIDDEN_ACCESS':
			return <ForbiddenAccessDownload />;
		case 'INVALID_TICKET_ATTACHMENT':
			return <InvalidTicketAttachment />;
		case 'JIRA_ORGANIZATION_ERROR':
			return <AttachmentNotFound />;
		default:
			return <UnexpectedError uploadErrorMessage="try-again-later" />;
	}
};

const AttachmentDownloaderOutlet = () => {
	const [downloadError, setDownloadError] = useState<string | null>(null);
	const [downloadUrl, setDownloadUrl] = useState<string | null>(null);
	const [downloadUrlLoading, setDownloadUrlLoading] = useState(false);
	const {errorCode, hasAccess, loading} = useCheckAttachmentAccess();
	const {ticketAttachmentERC, ticketAttachmentId} = useParams();

	useEffect(() => {
		const fetchDownloadURL = async () => {
			setDownloadUrlLoading(true);

			try {
				const endpoint = ticketAttachmentId
					? `/ticket-attachments/by-id/${ticketAttachmentId}/download`
					: `/ticket-attachments/by-external-reference-code/${ticketAttachmentERC}/download`;

				const response =
					await Liferay.OAuth2Client.FromUserAgentApplication(
						'liferay-customer-etc-spring-boot-oaua'
					).fetch(endpoint);

				if (response.ok) {
					setDownloadUrl(await response.text());
				}
				else {
					setDownloadError(
						i18n.translate('error-downloading-attachment')
					);
				}
			}
			catch (error) {
				setDownloadError(
					i18n.translate('error-downloading-attachment')
				);
			}
			finally {
				setDownloadUrlLoading(false);
			}
		};

		if (hasAccess && (ticketAttachmentERC || ticketAttachmentId)) {
			fetchDownloadURL();
		}
	}, [hasAccess, ticketAttachmentERC, ticketAttachmentId]);

	if (loading || downloadUrlLoading) {
		return (
			<div className="mx-auto">
				<ClayLoadingIndicator size="sm" />
			</div>
		);
	}

	if (downloadError) {
		return renderErrorComponent(downloadError);
	}

	if (hasAccess && downloadUrl) {
		return <AttachmentDownloader downloadUrl={downloadUrl} />;
	}

	return renderErrorComponent(errorCode);
};

export default AttachmentDownloaderOutlet;
