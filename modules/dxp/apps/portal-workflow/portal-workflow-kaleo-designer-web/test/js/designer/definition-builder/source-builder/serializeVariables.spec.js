/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const METADATA = {description: '', name: 'definition', version: 1};

const XML_NAMESPACE = {
	'xmlns': 'urn:liferay.com:liferay-workflow_7.4.0',
	'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
};

describe('Serializing a node with untouched variables', () => {
	let serializeDefinition;

	beforeAll(() => {
		Liferay.FeatureFlags['LPD-62272'] = true;
		Liferay.PropsValues = {
			...Liferay.PropsValues,
			ENTERPRISE_PRODUCT_AI_HUB_ENABLED: true,
		};

		serializeDefinition =
			require('../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/source-builder/serializeUtil').serializeDefinition;
	});

	afterAll(() => {
		Liferay.FeatureFlags['LPD-62272'] = false;

		delete Liferay.PropsValues.ENTERPRISE_PRODUCT_AI_HUB_ENABLED;
	});

	it('Writes empty arrays for an HTTP request node', () => {
		const xmlDefinition = serializeDefinition(
			XML_NAMESPACE,
			METADATA,
			[
				{
					data: {label: {en_US: 'HTTP Request'}, newNode: true},
					id: 'httpNode',
					position: {x: 0, y: 0},
					type: 'http-request',
				},
			],
			[]
		);

		expect(xmlDefinition).not.toContain('undefined');
		expect(xmlDefinition).toContain('<input-variables>');
		expect(xmlDefinition).toContain('<output-variables>');
	});

	it('Writes empty arrays for an LLM node', () => {
		const xmlDefinition = serializeDefinition(
			XML_NAMESPACE,
			METADATA,
			[
				{
					data: {label: {en_US: 'LLM'}, newNode: true},
					id: 'llmNode',
					position: {x: 0, y: 0},
					type: 'llm',
				},
			],
			[]
		);

		expect(xmlDefinition).not.toContain('undefined');
		expect(xmlDefinition).toMatch(/<rag>[\s\S]*?\{\}[\s\S]*?<\/rag>/);
		expect(xmlDefinition).toMatch(/<tools>[\s\S]*?\[\][\s\S]*?<\/tools>/);
	});
});
