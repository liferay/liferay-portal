/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import DiagramBuilder from '../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/DiagramBuilder';
import MockDefinitionBuilderContext from '../../../../mock/MockDefinitionBuilderContext';

describe('The DiagramBuilder component should', () => {
	let container;

	beforeAll(() => {
		const renderResult = render(
			<MockDefinitionBuilderContext>
				<DiagramBuilder workflowDefinitionVersions={[]} />
			</MockDefinitionBuilderContext>
		);

		container = renderResult.container;
	});

	it('Be rendered with control buttons, default nodes and sidebar', () => {
		const controlButtons = container.querySelector(
			'div.react-flow__controls'
		);
		const startNode = container.querySelector('div.start-node');
		const endNode = container.querySelector('div.end-node');
		const sidebar = container.querySelector('div.sidebar');

		expect(controlButtons).toBeTruthy();
		expect(startNode).toBeTruthy();
		expect(endNode).toBeTruthy();
		expect(sidebar).toBeTruthy();
	});
});
