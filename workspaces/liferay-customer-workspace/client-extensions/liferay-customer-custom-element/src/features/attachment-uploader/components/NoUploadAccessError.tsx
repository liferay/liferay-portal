/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AttachmentMessages from './AttachmentMessages';

const NoUploadAccessError = () => {
	return (
		<AttachmentMessages
			icon="warning-full"
			subtitle="you-need-administrator-or-requester-role-on-this-project-to-upload-a-file."
			title="you-dont-have-access-to-upload-files"
		/>
	);
};

export default NoUploadAccessError;
