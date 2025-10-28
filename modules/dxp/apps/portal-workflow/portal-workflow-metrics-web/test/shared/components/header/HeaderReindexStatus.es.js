/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import HeaderReindexStatus from '../../../../src/main/resources/META-INF/resources/js/shared/components/header/HeaderReindexStatus.es';
import ToasterProvider from '../../../../src/main/resources/META-INF/resources/js/shared/components/toaster/ToasterProvider.es';
import {MockRouter} from '../../../mock/MockRouter.es';

describe('The HeaderTitle component should', () => {
	let containerWrapper;

	afterEach(cleanup);

	beforeEach(() => {
		document.body.innerHTML = '';

		const body = document.createElement('div');

		body.innerHTML =
			'<div data-testid="wrapper"><div id="workflow"></div></div>';

		document.body.appendChild(body);

		containerWrapper = document.getElementById('workflow');
	});

	test('Not render without statuses', () => {
		const {getByTestId} = render(
			<MockRouter>
				<ToasterProvider>
					<HeaderReindexStatus container={containerWrapper} />
				</ToasterProvider>
			</MockRouter>
		);

		const wrapper = getByTestId('wrapper');

		expect(wrapper.children.length).toBe(1);
	});

	test('Render with statuses and show loading status', () => {
		const {getByTestId, getByTitle} = render(
			<MockRouter
				initialReindexStatuses={[{completionPercentage: 0, key: 'All'}]}
			>
				<ToasterProvider>
					<HeaderReindexStatus container={containerWrapper} />
				</ToasterProvider>
			</MockRouter>
		);

		const wrapper = getByTestId('wrapper');
		const statusLoading = getByTitle(
			'the-workflow-metrics-data-is-currently-reindexing'
		);

		expect(wrapper.children.length).toBe(2);
		expect(statusLoading).toBeTruthy();
	});
});
