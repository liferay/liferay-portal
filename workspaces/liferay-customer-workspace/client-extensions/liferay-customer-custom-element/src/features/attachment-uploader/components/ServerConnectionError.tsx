/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useLocation} from 'react-router-dom';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import i18n from '~/utils/I18n';
import routerPath from '~/utils/routerPath';

import AttachmentMessages from './AttachmentMessages';

interface ServerConnectionErrorProps {
	isUpload?: boolean;
}

const ServerConnectionError: React.FC<ServerConnectionErrorProps> = ({
	isUpload = false,
}) => {
	const {state} = useLocation();
	const pageRoutes = routerPath();

	const {helpCenterURL} = useAppPropertiesContext();

	return (
		<AttachmentMessages
			icon="warning-full"
			subtitle="try-again-later"
			title="unable-to-connect-to-file-server"
		>
			{isUpload ? (
				<>
					<a
						className="btn btn-secondary mr-2 uploader-secondary-button"
						href={`${pageRoutes.project(state?.uploadAccountKey)}/attachments`}
					>
						{i18n.translate('return-to-attachments')}
					</a>

					<a
						className="btn btn-primary uploader-primary-button"
						href={`${helpCenterURL}/hc/requests/${state?.ticketId}`}
					>
						{i18n.translate('return-to-ticket')}
					</a>
				</>
			) : (
				<a
					className="btn btn-primary uploader-primary-button"
					href={`${pageRoutes.project(state?.uploadAccountKey)}/attachments`}
				>
					{i18n.translate('return-to-attachments')}
				</a>
			)}
		</AttachmentMessages>
	);
};

export default ServerConnectionError;
