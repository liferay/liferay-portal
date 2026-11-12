/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useContext} from 'react';
import {ReactFlowProvider} from 'react-flow-renderer';

import '@testing-library/jest-dom';

import {DiagramBuilderContext} from '../../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/DiagramBuilderContext';
import AIDecisionNode from '../../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/nodes/AIDecisionNode';
import LLMNode from '../../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/nodes/LLMNode';
import MockDefinitionBuilderContext from '../../../../../../mock/MockDefinitionBuilderContext';
import MockDiagramBuilderContext from '../../../../../../mock/MockDiagramBuilderContext';

const SelectedModel = () => {
	const {selectedItem} = useContext(DiagramBuilderContext);

	return (
		<output aria-label="selected-model">
			{selectedItem
				? `${selectedItem.data.modelProvider}/${selectedItem.data.modelName}`
				: ''}
		</output>
	);
};

describe('Selecting an AI node', () => {
	it.each([
		['ai-decision', AIDecisionNode],
		['llm', LLMNode],
	])('Keeps the model of an %s node', async (type, Node) => {
		render(
			<MockDefinitionBuilderContext>
				<MockDiagramBuilderContext>
					<ReactFlowProvider>
						<Node
							data={{
								label: {en_US: 'AI Node'},
								modelName: 'anthropic.claude-sonnet-4-5',
								modelProvider: 'providerERC',
							}}
							id="aiNode"
						/>

						<SelectedModel />
					</ReactFlowProvider>
				</MockDiagramBuilderContext>
			</MockDefinitionBuilderContext>
		);

		await userEvent.click(screen.getByText('AI Node'));

		expect(screen.getByLabelText('selected-model')).toHaveTextContent(
			'providerERC/anthropic.claude-sonnet-4-5'
		);
	});
});
