/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import Sidebar, {
	contents,
} from '../../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/sidebar/Sidebar';
import MockDefinitionBuilderContext from '../../../../../../mock/MockDefinitionBuilderContext';
import MockDiagramBuilderContext from '../../../../../../mock/MockDiagramBuilderContext';

const nodeTypes = [
	'condition',
	'end',
	'fork',
	'join',
	'join-xor',
	'start',
	'state',
	'task',
];

describe('The Sidebar component should', () => {
	let container;

	it('Be rendered with correct title and all node types', () => {
		const renderResult = render(
			<MockDefinitionBuilderContext>
				<MockDiagramBuilderContext>
					<Sidebar />
				</MockDiagramBuilderContext>
			</MockDefinitionBuilderContext>
		);

		container = renderResult.container;

		const title = container.querySelector('span.title');

		expect(title).toHaveTextContent('nodes');

		const nodes = container.querySelectorAll('div.node');

		expect(nodes.length).toBe(7);
		expect(nodes[0].classList).toContain('end-node');
		expect(nodes[1].classList).toContain('fork-node');
		expect(nodes[2].classList).toContain('join-node');
		expect(nodes[3].classList).toContain('join-xor-node');
		expect(nodes[4].classList).toContain('start-node');
		expect(nodes[5].classList).toContain('state-node');
		expect(nodes[6].classList).toContain('task-node');
	});

	it('Be rendered with selected node info when a node is selected', () => {
		const renderResult = render(
			<MockDefinitionBuilderContext>
				<MockDiagramBuilderContext
					mockSelectedNode={{
						data: {label: {en_US: 'start node'}},
						id: 'node_0',
						type: 'start',
					}}
				>
					<Sidebar />
				</MockDiagramBuilderContext>
			</MockDefinitionBuilderContext>
		);

		container = renderResult.container;

		const title = container.querySelector('span.title');
		const panel = container.querySelector('div.panel');

		expect(title).toHaveTextContent('start');
		expect(panel).toHaveTextContent('information');

		const labels = container.querySelectorAll('label');

		expect(labels.length).toBe(3);
		expect(labels[0]).toHaveTextContent('label*');
		expect(labels[1]).toHaveTextContent('node name*');
		expect(labels[2]).toHaveTextContent('description');

		const inputLabel = container.querySelector(
			'input#workflowDefinitionBaseNodeLabel'
		);
		const inputName = container.querySelector(
			'input#workflowDefinitionBaseNodeName'
		);
		const textareaDescription = container.querySelector(
			'textarea#workflowDefinitionBaseNodeDescription'
		);

		expect(inputLabel).toHaveValue('start node');
		expect(inputName).toHaveValue('node_0');
		expect(textareaDescription).toHaveValue('');
	});

	it('Have the correct sections according to each node type', () => {
		nodeTypes.forEach((type) => {
			const {sections} = contents[type];

			if (type === 'task') {
				expect(sections[0]).toContain('nodeInformation');
				expect(sections[1]).toContain('assignmentsSummary');
				expect(sections[2]).toContain('notificationsSummary');
				expect(sections[3]).toContain('actionsSummary');
				expect(sections[4]).toContain('timersSummary');

				return;
			}

			expect(sections[0]).toContain('nodeInformation');
			expect(sections[1]).toContain('notificationsSummary');
			expect(sections[2]).toContain('actionsSummary');
		});
	});
});
