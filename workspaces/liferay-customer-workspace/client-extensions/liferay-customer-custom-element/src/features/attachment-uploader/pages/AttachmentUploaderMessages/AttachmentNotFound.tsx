/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import i18n from '~/utils/I18n';
import routerPath from '~/utils/routerPath';

import AttachmentMessage from '../../components/AttachmentMessage/AttachmentMessage';

interface IProps {
	uploadAccountKey: string;
}

const AttachmentNotFound = ({uploadAccountKey}: IProps) => {
	const pageRoutes = routerPath();

	return (
		<AttachmentMessage
			icon="warning-full"
			subtitle="the-file-may-have-been-deleted"
			title="file-to-download-doesnt-exist-anymore"
		>
			{uploadAccountKey && (
				<a
					className="btn btn-primary"
					href={`${pageRoutes.project(uploadAccountKey)}/attachments`}
				>
					{i18n.translate('return-to-attachments')}
				</a>
			)}
		</AttachmentMessage>
	);
};

export default AttachmentNotFound;
