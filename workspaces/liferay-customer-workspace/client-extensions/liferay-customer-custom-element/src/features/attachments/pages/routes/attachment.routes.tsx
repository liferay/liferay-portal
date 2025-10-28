/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {HashRouter, Route, Routes} from 'react-router-dom';

import Layout from '../../components/Layout';
import AttachmentDownloaderOutlet from './Outlets/AttachmentDownloaderOutlet';
import AttachmentUploaderOutlet from './Outlets/AttachmentUploaderOutlet';

const AttachmentRoutes = () => {
	return (
		<HashRouter>
			<Routes>
				<Route element={<Layout />}>
					<Route
						element={<AttachmentUploaderOutlet />}
						path="/:ticketId"
					/>
					<Route
						element={<AttachmentDownloaderOutlet />}
						path="/erc/:ticketAttachmentERC"
					/>
					<Route
						element={<AttachmentDownloaderOutlet />}
						path="/id/:ticketAttachmentId"
					/>
				</Route>
			</Routes>
		</HashRouter>
	);
};

export default AttachmentRoutes;
