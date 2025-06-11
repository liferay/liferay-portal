/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useLocation} from 'react-router-dom';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import i18n from '~/utils/I18n';
import routerPath from '~/utils/routerPath';

import AttachmentMessages from './AttachmentMessages';

const PostCommentError = () => {
	const {state} = useLocation();
	const pageRoutes = routerPath();
	const {helpCenterURL} = useAppPropertiesContext();

	return (
		<AttachmentMessages
			icon="warning-full"
			state={state}
			subtitle="please-check-again-later"
			title="your-attachment-is-uploaded-however-we-encountered-a-problem-posting-your-comment-the-system-is-automatically-retrying-to-send-it"
		>
			<a
				className="btn btn-secondary mr-2 uploader-secondary-button"
				href={`${pageRoutes.project(state?.uploadAccountKey)}/attachments`}
			>
				{i18n.translate('return-to-attachments')}
			</a>

			<a
				className="btn btn-primary button-rounded"
				href={`${helpCenterURL}/hc/requests/${state?.ticketId}`}
			>
				{i18n.translate('return-to-ticket')}
			</a>
		</AttachmentMessages>
	);
};

export default PostCommentError;
