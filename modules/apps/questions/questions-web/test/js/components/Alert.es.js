/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup} from '@testing-library/react';
import React from 'react';

import Alert from '../../../src/main/resources/META-INF/resources/js/components/Alert.es';
import {renderComponent} from '../../helpers.es';

import '@testing-library/jest-dom';

describe('Alert', () => {
	afterEach(cleanup);

	it('renders alert with its title and message', () => {
		const info = {
			message: 'Info message',
			title: 'Info title',
		};
		const {getByText} = renderComponent({
			ui: <Alert info={info} />,
		});

		expect(getByText(info.title)).toBeInTheDocument();

		expect(getByText(info.message)).toBeInTheDocument();
	});
});
