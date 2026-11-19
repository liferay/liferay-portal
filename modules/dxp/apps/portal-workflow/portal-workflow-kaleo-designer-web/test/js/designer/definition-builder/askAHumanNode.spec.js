/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import {isEdge, isNode} from 'react-flow-renderer';

import AskAHumanNode from '../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/nodes/AskAHumanNode';
import MockDefinitionBuilderContext from '../../../mock/MockDefinitionBuilderContext';
import MockDiagramBuilderContext from '../../../mock/MockDiagramBuilderContext';

const INPUT_VARIABLES = [{name: 'description', type: 'string'}];

const METADATA = {description: '', name: 'definition', version: 1};

const OUTPUT_VARIABLES = [{name: 'style', type: 'string'}];

const QUESTION = {
	options: [
		{
			label_i18n: {en_US: 'Digital Art'},
			output: {style: 'Digital Art'},
		},
		{
			label_i18n: {en_US: 'Watercolor'},
			output: {style: 'Watercolor'},
		},
	],
	title_i18n: {en_US: 'Which style?'},
	type: 'select',
};

const XML_DEFINITION = `
	<workflow-definition xmlns="urn:liferay.com:liferay-workflow_7.4.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
		<name>definition</name>
		<version>1</version>
		<ask-a-human>
			<name>requestImageStyle</name>
			<input-variables><![CDATA[${JSON.stringify(INPUT_VARIABLES)}]]></input-variables>
			<labels>
				<label language-id="en_US">Request Image Style</label>
			</labels>
			<output-variables><![CDATA[${JSON.stringify(OUTPUT_VARIABLES)}]]></output-variables>
			<question><![CDATA[${JSON.stringify(QUESTION)}]]></question>
			<transitions>
				<transition>
					<name>done</name>
					<target>done</target>
					<default>true</default>
				</transition>
			</transitions>
		</ask-a-human>
		<state>
			<name>done</name>
		</state>
	</workflow-definition>
`;

const XML_NAMESPACE = {
	'xmlns': 'urn:liferay.com:liferay-workflow_7.4.0',
	'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
};

describe('Ask a Human node', () => {
	let DeserializeUtil;
	let Sidebar;
	let serializeDefinition;

	const getAskAHumanElement = (xmlDefinition) =>
		xmlDefinition.match(/<ask-a-human>[\s\S]*?<\/ask-a-human>/)[0];

	const getJSON = (askAHumanElement, tagName) =>
		JSON.parse(
			askAHumanElement.match(
				new RegExp(
					`<${tagName}>\\s*<!\\[CDATA\\[([\\s\\S]*?)\\]\\]>\\s*</${tagName}>`
				)
			)[1]
		);

	beforeAll(() => {
		Liferay.FeatureFlags['LPD-115774'] = true;
		Liferay.FeatureFlags['LPD-62272'] = true;
		Liferay.PropsValues = {
			...Liferay.PropsValues,
			ENTERPRISE_PRODUCT_AI_HUB_ENABLED: true,
		};

		DeserializeUtil =
			require('../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/source-builder/deserializeUtil').default;
		Sidebar =
			require('../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/sidebar/Sidebar').default;
		serializeDefinition =
			require('../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/source-builder/serializeUtil').serializeDefinition;
	});

	afterAll(() => {
		Liferay.FeatureFlags['LPD-115774'] = false;
		Liferay.FeatureFlags['LPD-62272'] = false;

		delete Liferay.PropsValues.ENTERPRISE_PRODUCT_AI_HUB_ENABLED;
	});

	it('Keeps the question and variables of an Ask a Human node when the definition is saved again', () => {
		const elements = new DeserializeUtil(XML_DEFINITION).getElements();

		const askAHumanElement = getAskAHumanElement(
			serializeDefinition(
				XML_NAMESPACE,
				METADATA,
				elements.filter(isNode),
				elements.filter(isEdge)
			)
		);

		expect(getJSON(askAHumanElement, 'input-variables')).toStrictEqual(
			INPUT_VARIABLES
		);
		expect(getJSON(askAHumanElement, 'output-variables')).toStrictEqual(
			OUTPUT_VARIABLES
		);
		expect(getJSON(askAHumanElement, 'question')).toStrictEqual(QUESTION);

		// The definition schema requires this order inside an Ask a Human node

		const tagNames = [
			'<input-variables>',
			'<labels>',
			'<output-variables>',
			'<question>',
			'<transitions>',
		];

		const positions = tagNames.map((tagName) =>
			askAHumanElement.indexOf(tagName)
		);

		expect(positions).not.toContain(-1);
		expect(positions).toStrictEqual([...positions].sort((a, b) => a - b));
	});

	it('Shows the question and variables of an Ask a Human node when it is selected', async () => {
		render(
			<MockDefinitionBuilderContext>
				<MockDiagramBuilderContext>
					<AskAHumanNode
						data={{
							inputVariables: INPUT_VARIABLES,
							label: {en_US: 'Request Image Style'},
							outputVariables: OUTPUT_VARIABLES,
							question: QUESTION,
						}}
						id="requestImageStyle"
					/>

					<Sidebar />
				</MockDiagramBuilderContext>
			</MockDefinitionBuilderContext>
		);

		await userEvent.click(screen.getByText('Request Image Style'));

		expect(screen.getByLabelText('input-variables')).toHaveValue(
			JSON.stringify(INPUT_VARIABLES, null, 2)
		);
		expect(screen.getByLabelText('output-variables')).toHaveValue(
			JSON.stringify(OUTPUT_VARIABLES, null, 2)
		);
		expect(screen.getByLabelText('question')).toHaveValue(
			JSON.stringify(QUESTION, null, 2)
		);
	});
});
