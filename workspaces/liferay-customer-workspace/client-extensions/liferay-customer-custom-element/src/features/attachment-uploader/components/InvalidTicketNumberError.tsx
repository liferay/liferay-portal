/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AttachmentMessages from './AttachmentMessages';

const InvalidTicketNumberError = () => {
	return (
		<AttachmentMessages
			icon="warning-full"
			subtitle="make-sure-the-ticket-number-is-correct"
			title="invalid-or-non-existent-ticket-number"
		/>
	);
};

export default InvalidTicketNumberError;
