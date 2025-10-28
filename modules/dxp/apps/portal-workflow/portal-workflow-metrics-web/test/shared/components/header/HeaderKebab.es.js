/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import HeaderKebab from '../../../../src/main/resources/META-INF/resources/js/shared/components/header/HeaderKebab.es';
import {MockRouter} from '../../../mock/MockRouter.es';

import '@testing-library/jest-dom';

describe('The HeaderKebab component should', () => {
	afterEach(cleanup);

	beforeAll(() => {
		const body = document.createElement('div');

		body.className = 'user-control-group';

		body.innerHTML = `<ul class="control-menu-nav"><li></li></ul>`;

		document.body.appendChild(body);
	});

	test('Not render when has no kebabItems prop', () => {
		const {container} = render(<HeaderKebab />);

		expect(container.innerHTML).toBe('');
	});

	test('Render with kebabItems prop', () => {
		const mockAction = jest.fn();
		const kebabItems = [
			{
				action: mockAction,
				label: 'test',
			},
			{
				label: 'test1',
				link: '/',
			},
		];

		render(
			<MockRouter>
				<HeaderKebab kebabItems={kebabItems} />
			</MockRouter>
		);

		const button =
			document.getElementById('headerKebab').children[0].children[0]
				.children[0];

		fireEvent.click(button);

		const dropDownItems = document.querySelectorAll('.dropdown-item');

		expect(dropDownItems[0]).toHaveTextContent('test');

		expect(dropDownItems[1]).toHaveTextContent('test1');

		fireEvent.click(dropDownItems[0]);

		expect(mockAction).toHaveBeenCalled();
	});
});
