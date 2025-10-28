/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import React from 'react';

import HeaderBackButton from '../../../../src/main/resources/META-INF/resources/js/shared/components/header/HeaderBackButton.es';
import {MockRouter} from '../../../mock/MockRouter.es';

import '@testing-library/jest-dom';

describe('The HeaderBackButton component should', () => {
	let containerWrapper;

	afterEach(cleanup);

	beforeEach(() => {
		const body = document.createElement('div');

		document.body.innerHTML = '';

		body.innerHTML =
			'<div id="workflow" data-testid="workflow"><button>Close Sidebar</button></div>';

		document.body.appendChild(body);

		containerWrapper = document.getElementById('workflow');
	});

	test('Not render when pathname is equal to basePath', () => {
		const {getByTestId} = render(
			<MockRouter initialPath="/">
				<HeaderBackButton basePath="/" container={containerWrapper} />
			</MockRouter>
		);

		const container = getByTestId('workflow');

		expect(container.children.length).toEqual(1);
		expect(container.children[0]).toHaveTextContent('Close Sidebar');
	});

	test('Render when pathname is different to basePath', () => {
		const {getByTestId} = render(
			<MockRouter>
				<HeaderBackButton basePath="/" container={containerWrapper} />
			</MockRouter>
		);

		const {children} = getByTestId('workflow');

		const link = children[1].children[0];
		const {classList} = link.children[0];
		const href = link.getAttribute('href');

		expect(children.length).toEqual(2);
		expect(children[0]).toHaveTextContent('Close Sidebar');
		expect(classList.contains('lexicon-icon-angle-left')).toBe(true);
		expect(href).toBe('/');
	});
});
