/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import {isEdge, isNode} from 'react-flow-renderer';

import ServiceNode from '../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/nodes/ServiceNode';
import MockDefinitionBuilderContext from '../../../mock/MockDefinitionBuilderContext';
import MockDiagramBuilderContext from '../../../mock/MockDiagramBuilderContext';

const INPUT_VARIABLES = [{name: 'projectId', type: 'string'}];

const METADATA = {description: '', name: 'definition', version: 1};

const OUTPUT_VARIABLES = [{name: 'spaceSelection', type: 'string'}];

const XML_DEFINITION = `
	<workflow-definition xmlns="urn:liferay.com:liferay-workflow_7.4.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
		<name>definition</name>
		<version>1</version>
		<service>
			<name>requestSpaceSelection</name>
			<input-variables><![CDATA[${JSON.stringify(INPUT_VARIABLES)}]]></input-variables>
			<java-delegate>javaDelegate#requestSpaceSelection</java-delegate>
			<labels>
				<label language-id="en_US">Request Space Selection</label>
			</labels>
			<output-variables><![CDATA[${JSON.stringify(OUTPUT_VARIABLES)}]]></output-variables>
			<transitions>
				<transition>
					<name>done</name>
					<target>done</target>
					<default>true</default>
				</transition>
			</transitions>
		</service>
		<state>
			<name>done</name>
		</state>
	</workflow-definition>
`;

const XML_NAMESPACE = {
	'xmlns': 'urn:liferay.com:liferay-workflow_7.4.0',
	'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
};

describe('Service node variables', () => {
	let DeserializeUtil;
	let Sidebar;
	let serializeDefinition;

	const getServiceElement = (xmlDefinition) =>
		xmlDefinition.match(/<service>[\s\S]*?<\/service>/)[0];

	const getVariables = (serviceElement, tagName) =>
		JSON.parse(
			serviceElement.match(
				new RegExp(
					`<${tagName}>\\s*<!\\[CDATA\\[([\\s\\S]*?)\\]\\]>\\s*</${tagName}>`
				)
			)[1]
		);

	beforeAll(() => {
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
		Liferay.FeatureFlags['LPD-62272'] = false;

		delete Liferay.PropsValues.ENTERPRISE_PRODUCT_AI_HUB_ENABLED;
	});

	it('Keeps the variables of a service node when the definition is saved again', () => {
		const elements = new DeserializeUtil(XML_DEFINITION).getElements();

		const serviceElement = getServiceElement(
			serializeDefinition(
				XML_NAMESPACE,
				METADATA,
				elements.filter(isNode),
				elements.filter(isEdge)
			)
		);

		expect(getVariables(serviceElement, 'input-variables')).toStrictEqual(
			INPUT_VARIABLES
		);
		expect(getVariables(serviceElement, 'output-variables')).toStrictEqual(
			OUTPUT_VARIABLES
		);

		// The definition schema requires this order inside a service node

		const tagNames = [
			'<input-variables>',
			'<java-delegate>',
			'<labels>',
			'<output-variables>',
			'<transitions>',
		];

		const positions = tagNames.map((tagName) =>
			serviceElement.indexOf(tagName)
		);

		expect(positions).not.toContain(-1);
		expect(positions).toStrictEqual([...positions].sort((a, b) => a - b));
	});

	it('Shows the input and output variables of a service node when it is selected', async () => {
		render(
			<MockDefinitionBuilderContext>
				<MockDiagramBuilderContext>
					<ServiceNode
						data={{
							inputVariables: INPUT_VARIABLES,
							javaDelegate: 'javaDelegate#requestSpaceSelection',
							label: {en_US: 'Request Space Selection'},
							outputVariables: OUTPUT_VARIABLES,
						}}
						id="requestSpaceSelection"
					/>

					<Sidebar />
				</MockDiagramBuilderContext>
			</MockDefinitionBuilderContext>
		);

		await userEvent.click(screen.getByText('Request Space Selection'));

		expect(screen.getByLabelText('input-variables')).toHaveValue(
			JSON.stringify(INPUT_VARIABLES, null, 2)
		);
		expect(screen.getByLabelText('output-variables')).toHaveValue(
			JSON.stringify(OUTPUT_VARIABLES, null, 2)
		);
	});
});
