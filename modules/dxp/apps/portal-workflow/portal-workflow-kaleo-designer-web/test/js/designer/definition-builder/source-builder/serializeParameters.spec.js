/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const METADATA = {description: '', name: 'definition', version: 1};

const XML_NAMESPACE = {
	'xmlns': 'urn:liferay.com:liferay-workflow_7.4.0',
	'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
};

describe('Serializing the parameters of an AI node', () => {
	let serializeDefinition;

	const serializeNode = (type, data) =>
		serializeDefinition(
			XML_NAMESPACE,
			METADATA,
			[
				{
					data: {label: {en_US: type}, newNode: true, ...data},
					id: 'aiNode',
					position: {x: 0, y: 0},
					type,
				},
			],
			[]
		);

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

	it.each(['ai-decision', 'llm'])(
		'Writes the parameters of an %s node in schema order',
		(type) => {
			const xmlDefinition = serializeNode(type, {
				parameters: [{name: 'maxOutputTokens', value: '512'}],
			});

			expect(xmlDefinition).toMatch(
				/<\/output-variables>\s*<parameters>\s*<!\[CDATA\[\s*\[\s*{\s*"name": "maxOutputTokens",\s*"value": "512"\s*}\s*\]\s*\]\]>\s*<\/parameters>\s*<prompt>/
			);
		}
	);

	it.each(['ai-decision', 'llm'])(
		'Omits the parameters of an %s node without any',
		(type) => {
			expect(serializeNode(type, {})).not.toContain('<parameters>');
			expect(serializeNode(type, {parameters: []})).not.toContain(
				'<parameters>'
			);
		}
	);
});
