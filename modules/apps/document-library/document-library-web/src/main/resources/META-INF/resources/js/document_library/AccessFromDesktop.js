/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal from '@clayui/modal';
import React from 'react';

import WebdavURLCopyButton from './WebdavURLCopyButton';

const AccessFromDesktop = ({
	learnMessage,
	learnURL,
	portletNamespace,
	webDavURL,
}) => {
	const id = `${portletNamespace}webDavURL`;

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('access-from-desktop')}
			</ClayModal.Header>

			<ClayModal.Body>
				<div className="portlet-document-library">
					<p className="mb-4">
						{Liferay.Language.get('webdav-help')}

						<a href={learnURL} target="_blank">
							{learnMessage}
						</a>
					</p>

					<div className="form-group input-resource-wrapper">
						<label className="control-label" htmlFor={id}>
							{Liferay.Language.get('web-dav-url')}
						</label>

						<WebdavURLCopyButton id={id} url={webDavURL} />
					</div>
				</div>
			</ClayModal.Body>
		</>
	);
};

export default AccessFromDesktop;
