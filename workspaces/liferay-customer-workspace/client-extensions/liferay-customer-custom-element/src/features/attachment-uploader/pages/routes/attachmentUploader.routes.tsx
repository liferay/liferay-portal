/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {HashRouter, Route, Routes} from 'react-router-dom';

import Layout from '../../components/Layout';
import AttachmentOutlet from './Outlets/AttachmentOutlet';

const AttachmentUploaderRoutes = () => {
	return (
		<HashRouter>
			<Routes>
				<Route element={<Layout />} path="/:ticketId">
					<Route element={<AttachmentOutlet />} index />
				</Route>
			</Routes>
		</HashRouter>
	);
};

export default AttachmentUploaderRoutes;
