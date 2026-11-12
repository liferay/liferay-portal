/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const METADATA = {description: '', name: 'definition', version: 1};

const XML_NAMESPACE = {
	'xmlns': 'urn:liferay.com:liferay-workflow_7.4.0',
	'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
};

describe('Serializing the model of an AI node', () => {
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
		'Writes the model name and model provider of an %s node in schema order',
		(type) => {
			const xmlDefinition = serializeNode(type, {
				modelName: 'anthropic.claude-sonnet-4-5',
				modelProvider: 'providerERC',
			});

			expect(xmlDefinition).toMatch(
				/<\/input-variables>\s*<model-name>anthropic\.claude-sonnet-4-5<\/model-name>\s*<model-provider>providerERC<\/model-provider>\s*<output-variables>/
			);
		}
	);

	it.each(['ai-decision', 'llm'])(
		'Omits the model of an %s node without one',
		(type) => {
			const xmlDefinition = serializeNode(type, {});

			expect(xmlDefinition).not.toContain('<model-name>');
			expect(xmlDefinition).not.toContain('<model-provider>');
		}
	);
});
