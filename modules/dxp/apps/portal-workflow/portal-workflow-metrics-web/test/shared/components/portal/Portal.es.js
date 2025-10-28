/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import React from 'react';

import Portal from '../../../../src/main/resources/META-INF/resources/js/shared/components/portal/Portal.es';

import '@testing-library/jest-dom';

describe('The Portal component should', () => {
	let containerWrapper;

	afterEach(cleanup);

	beforeAll(() => {
		const body = document.createElement('div');

		body.innerHTML = `<div id="workflow" data-testid="workflow"><span>Portal Metrics</span></div>`;

		document.body.appendChild(body);

		containerWrapper = document.getElementById('workflow');
	});

	test('Not render component without container prop', () => {
		const {container} = render(
			<Portal>
				<span>Portal Metrics</span>
			</Portal>
		);

		expect(container.innerHTML).toBe('');
	});

	test('Render component without replace children', () => {
		const {getByTestId} = render(
			<Portal container={containerWrapper}>
				<button>Voltar</button>
			</Portal>
		);

		const container = getByTestId('workflow');

		expect(container.children.length).toEqual(2);
		expect(container.children[0]).toHaveTextContent('Portal Metrics');
		expect(container.children[1]).toHaveTextContent('Voltar');
	});

	test('Render component with replace children', () => {
		const {getByTestId} = render(
			<Portal container={containerWrapper} replace>
				<button>Voltar</button>
			</Portal>
		);

		const container = getByTestId('workflow');

		expect(container.children.length).toEqual(1);
		expect(container.children[0]).toHaveTextContent('Voltar');
	});
});
