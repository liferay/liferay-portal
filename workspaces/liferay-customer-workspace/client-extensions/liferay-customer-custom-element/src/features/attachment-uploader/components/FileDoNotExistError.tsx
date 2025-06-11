/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useLocation} from 'react-router-dom';
import i18n from '~/utils/I18n';
import routerPath from '~/utils/routerPath';

import AttachmentMessages from './AttachmentMessages';

const FileDoNotExistError = () => {
	const {state} = useLocation();
	const pageRoutes = routerPath();

	return (
		<AttachmentMessages
			icon="warning-full"
			state={state}
			subtitle="the-file-may-have-been-deleted"
			title="file-to-download-doesnt-exist-anymore"
		>
			<a
				className="btn btn-primary"
				href={`${pageRoutes.project(state?.uploadAccountKey)}/attachments`}
			>
				{i18n.translate('return-to-attachments')}
			</a>
		</AttachmentMessages>
	);
};

export default FileDoNotExistError;
