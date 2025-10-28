/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AttachmentMessage from '../../components/AttachmentMessage/AttachmentMessage';

const InvalidTicketAttachment = () => {
	return (
		<AttachmentMessage
			icon="warning-full"
			subtitle="make-sure-the-attachment-id-is-correct"
			title="invalid-or-non-existent-attachment-id"
		/>
	);
};

export default InvalidTicketAttachment;
