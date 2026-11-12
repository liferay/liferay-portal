/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const definition = (type, model) => `
	<workflow-definition xmlns="urn:liferay.com:liferay-workflow_7.4.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
		<name>definition</name>
		<version>1</version>
		<${type}>
			<name>${type}</name>
			${model}
		</${type}>
	</workflow-definition>
`;

describe('Deserializing the model of an AI node', () => {
	let DeserializeUtil;

	const getNode = (type, model) => {
		const deserializeUtil = new DeserializeUtil(definition(type, model));

		return deserializeUtil
			.getElements()
			.find((element) => element.type === type);
	};

	beforeAll(() => {
		Liferay.FeatureFlags['LPD-62272'] = true;
		Liferay.PropsValues = {
			...Liferay.PropsValues,
			ENTERPRISE_PRODUCT_AI_HUB_ENABLED: true,
		};

		DeserializeUtil =
			require('../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/source-builder/deserializeUtil').default;
	});

	afterAll(() => {
		Liferay.FeatureFlags['LPD-62272'] = false;

		delete Liferay.PropsValues.ENTERPRISE_PRODUCT_AI_HUB_ENABLED;
	});

	it.each(['ai-decision', 'llm'])(
		'Reads the model name and model provider of an %s node',
		(type) => {
			const {data} = getNode(
				type,
				'<model-name>anthropic.claude-sonnet-4-5</model-name><model-provider>providerERC</model-provider>'
			);

			expect(data.modelName).toBe('anthropic.claude-sonnet-4-5');
			expect(data.modelProvider).toBe('providerERC');
		}
	);

	it.each(['ai-decision', 'llm'])(
		'Reads an %s node without a model as empty values',
		(type) => {
			const {data} = getNode(type, '');

			expect(data.modelName).toBe('');
			expect(data.modelProvider).toBe('');
		}
	);
});
