/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';

import ConnectPage from '../ConnectPage';

describe('ConnectPage', () => {
	it('renders page title', () => {
		const {getByText} = render(
			<ConnectPage title="Workspace-connection" />
		);

		const title = getByText('Workspace-connection');

		expect(title).toBeInTheDocument();
	});
});
