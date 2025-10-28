/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import Tabs from '../../../../src/main/resources/META-INF/resources/js/shared/components/tabs/Tabs.es';
import {MockRouter} from '../../../mock/MockRouter.es';

describe('The Tabs component should', () => {
	let container;
	let renderResult;
	let tabButtons;
	const setCurrentTab = jest.fn();

	beforeAll(() => {
		const tabs = [
			{name: 'Tab 1', tabKey: 'tab1'},
			{name: 'Tab 2', tabKey: 'tab2'},
			{name: 'Tab 3', tabKey: 'tab3'},
		];

		renderResult = render(
			<MockRouter>
				<Tabs
					currentTab="tab3"
					setCurrentTab={setCurrentTab}
					tabs={tabs}
				/>
			</MockRouter>
		);

		container = renderResult.container;
		tabButtons = container.querySelectorAll('button');
	});

	test('Render tabs correctly', () => {
		expect(tabButtons.length).toBe(3);
		expect(tabButtons[0]).toHaveTextContent('Tab 1');
		expect(tabButtons[1]).toHaveTextContent('Tab 2');
		expect(tabButtons[2]).toHaveTextContent('Tab 3');
		expect(tabButtons[2].classList).toContain('active');
	});

	test('Call setCurrentTab with correct key when respective tab is clicked', () => {
		fireEvent.click(tabButtons[0]);

		expect(setCurrentTab).toHaveBeenCalledWith('tab1');

		fireEvent.click(tabButtons[1]);

		expect(setCurrentTab).toHaveBeenCalledWith('tab2');

		fireEvent.click(tabButtons[2]);

		expect(setCurrentTab).toHaveBeenCalledWith('tab3');
	});
});
