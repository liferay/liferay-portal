/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import CurrentNodes from '../../src/main/resources/META-INF/resources/js/components/CurrentNodes';

jest.mock('react-flow-renderer', () => ({
	useStore: () => ({
		getState: () => ({
			nodes: [
				{data: {label: 'Create'}, id: 'create'},
				{data: {label: 'Review'}, id: 'review'},
				{data: {label: 'Update'}, id: 'update'},
				{data: {label: 'Forward'}, id: 'forward'},
				{data: {label: 'Finish'}, id: 'finish'},
			],
		}),
	}),
	useZoomPanHelper: () => ({
		setCenter: jest.fn(),
	}),
}));

describe('The CurrentNodes component should', () => {
	let container;
	let getByText;

	beforeEach(() => {
		const result = render(
			<CurrentNodes
				nodesNames={['create', 'review', 'update', 'forward', 'finish']}
			/>
		);

		container = result.container;
		getByText = result.getByText;
	});

	it('Be rendered with nodes and showing "more..." option', () => {
		expect(container.querySelector('.current-node')).toBeTruthy();
		expect(
			container.querySelector('.current-node-link.more-link')
		).toBeTruthy();
	});

	it('Be rendered dropdown options', () => {
		const moreOption = container.querySelector(
			'.current-node-link.more-link'
		);
		const dropdownMenuShow = '.dropdown-menu.show';

		expect(getByText('Forward')).toBeTruthy();
		expect(getByText('Finish')).toBeTruthy();

		expect(document.querySelector(dropdownMenuShow)).toBeNull();

		fireEvent.click(moreOption);

		expect(document.querySelector(dropdownMenuShow)).toBeTruthy();
	});
});
