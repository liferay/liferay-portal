/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useState} from 'react';
import useCheckUploadAccess from '~/features/attachment-uploader/hooks/useCheckUploadAccess';
import {IUpload} from '~/utils/types';

import AttachmentUploader from '../../AttachmentUploader';
import {
	AttachmentAlreadyExists,
	AttachmentNotFound,
	ForbiddenAccess,
	InvalidTicketNumber,
	UnexpectedError,
} from '../../AttachmentUploaderMessages';
import TicketIsClosed from '../../AttachmentUploaderMessages/TicketIsClosed';

const renderErrorComponent = (
	errorCode: string | null,
	uploadStateData: IUpload | null
) => {
	switch (errorCode) {
		case 'FORBIDDEN_ACCESS':
			return <ForbiddenAccess />;
		case 'TICKET_IS_CLOSED':
			return <TicketIsClosed />;
		case 'INVALID_TICKET_NUMBER':
			return <InvalidTicketNumber />;
		case 'ZENDESK_ORGANIZATION_ERROR': {
			if (!uploadStateData?.uploadAccountKey) {
				return <UnexpectedError uploadErrorMessage="try-again-later" />;
			}

			return (
				<AttachmentNotFound
					uploadAccountKey={uploadStateData.uploadAccountKey}
				/>
			);
		}
		default:
			return (
				<UnexpectedError
					uploadErrorMessage={
						uploadStateData?.errorMessage ?? 'try-again-later'
					}
				/>
			);
	}
};

const AttachmentOutlet = () => {
	const {errorCode, hasAccess, loading} = useCheckUploadAccess();
	const [uploadStateData, setUploadStateData] = useState<IUpload | null>(
		null
	);

	if (loading) {
		return (
			<div className="mx-auto">
				<ClayLoadingIndicator size="sm" />
			</div>
		);
	}

	if (uploadStateData?.errorCode === 'ATTACHMENT_ALREADY_EXISTS') {
		return <AttachmentAlreadyExists />;
	}

	if (hasAccess) {
		return (
			<AttachmentUploader
				setUploadStateData={setUploadStateData}
				uploadStateData={uploadStateData}
			/>
		);
	}

	return renderErrorComponent(errorCode, uploadStateData);
};

export default AttachmentOutlet;
