/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import React from 'react';

import LoadingState from '../../../../src/main/resources/META-INF/resources/js/shared/components/loading/LoadingState.es';

import '@testing-library/jest-dom';

describe('The LoadingState component should', () => {
	afterEach(cleanup);

	test('Be render with default props', () => {
		const {container} = render(<LoadingState />);

		const loading = container.querySelector('span.loading-animation');

		expect(loading).toBeTruthy();
	});

	test('Be render with loading message', () => {
		const {getByText} = render(<LoadingState message="fetching data..." />);

		const loadingMessage = getByText('fetching data...');

		expect(loadingMessage).toBeTruthy();
	});
});
