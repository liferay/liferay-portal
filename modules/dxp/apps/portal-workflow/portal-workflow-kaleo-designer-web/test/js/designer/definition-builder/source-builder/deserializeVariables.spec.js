/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const definition = (rag) => `
	<workflow-definition xmlns="urn:liferay.com:liferay-workflow_7.4.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
		<name>definition</name>
		<version>1</version>
		<llm>
			<name>LLM</name>
			<rag><![CDATA[${rag}]]></rag>
		</llm>
	</workflow-definition>
`;

describe('Deserializing a node with an empty RAG configuration', () => {
	let DeserializeUtil;

	const getLLMNode = (rag) => {
		const deserializeUtil = new DeserializeUtil(definition(rag));

		return deserializeUtil
			.getElements()
			.find((element) => element.type === 'llm');
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

	it('Reads a definition saved with an undefined value as an object', () => {
		expect(getLLMNode('undefined').data.rag).toStrictEqual({});
	});

	it('Reads a definition saved with an empty array as an object', () => {
		expect(getLLMNode('[]').data.rag).toStrictEqual({});
	});

	it('Keeps a RAG configuration stored as a non empty array', () => {
		expect(
			getLLMNode('[{"contentRetriever":{"key":"liferay"}}]').data.rag
		).toStrictEqual([{contentRetriever: {key: 'liferay'}}]);
	});

	it('Keeps a RAG configuration that is already an object', () => {
		expect(
			getLLMNode('{"contentRetriever":{"key":"liferay"}}').data.rag
		).toStrictEqual({contentRetriever: {key: 'liferay'}});
	});
});
