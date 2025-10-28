/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import NavbarTabs from '../../../../src/main/resources/META-INF/resources/js/shared/components/navbar-tabs/NavbarTabs.es';
import {MockRouter} from '../../../mock/MockRouter.es';

describe('The NavbarTabs component should', () => {
	let container;
	let renderResult;

	const tabs = [
		{
			key: 'Tab 1',
			name: 'Lorem Ipsum 1',
			params: {
				processId: 35135,
			},
			path: '/test/tab1/:processId',
		},
		{
			key: 'Tab 2',
			name: 'Lorem Ipsum 2',
			params: {
				processId: 35136,
			},
			path: '/test/tab2/:processId',
		},
		{
			key: 'Tab 3',
			name: 'Lorem Ipsum 3',
			params: {
				processId: 35137,
			},
			path: '/test/tab3/:processId',
		},
		{
			key: 'Tab 4',
			name: 'Lorem Ipsum 4',
			params: {
				processId: 35138,
			},
			path: '/test/tab4/:processId',
		},
	];

	beforeAll(() => {
		renderResult = render(
			<MockRouter>
				<NavbarTabs tabs={tabs} />
			</MockRouter>
		);

		container = renderResult.container;
	});

	test('Render components with correct link', () => {
		const tabLinks = container.querySelectorAll('a.nav-link');

		expect(tabLinks.length).toBe(4);
		expect(tabLinks[0]).toHaveTextContent('Lorem Ipsum 1');
		expect(tabLinks[1]).toHaveTextContent('Lorem Ipsum 2');
		expect(tabLinks[2]).toHaveTextContent('Lorem Ipsum 3');
		expect(tabLinks[3]).toHaveTextContent('Lorem Ipsum 4');
		expect(tabLinks[0].getAttribute('href')).toContain('/test/tab1/35135');
		expect(tabLinks[1].getAttribute('href')).toContain('/test/tab2/35136');
		expect(tabLinks[2].getAttribute('href')).toContain('/test/tab3/35137');
		expect(tabLinks[3].getAttribute('href')).toContain('/test/tab4/35138');
	});
});
