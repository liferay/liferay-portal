/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const definition = (type, parameters) => `
	<workflow-definition xmlns="urn:liferay.com:liferay-workflow_7.4.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
		<name>definition</name>
		<version>1</version>
		<${type}>
			<name>${type}</name>
			${parameters}
		</${type}>
	</workflow-definition>
`;

describe('Deserializing the parameters of an AI node', () => {
	let DeserializeUtil;

	const getNode = (type, parameters) => {
		const deserializeUtil = new DeserializeUtil(
			definition(type, parameters)
		);

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
		'Reads the parameters of an %s node',
		(type) => {
			const {data} = getNode(
				type,
				'<parameters><![CDATA[[{"name": "maxOutputTokens", "value": "512"}]]]></parameters>'
			);

			expect(data.parameters).toEqual([
				{name: 'maxOutputTokens', value: '512'},
			]);
		}
	);

	it.each(['ai-decision', 'llm'])(
		'Reads an %s node without parameters as an empty list',
		(type) => {
			const {data} = getNode(type, '');

			expect(data.parameters).toEqual([]);
		}
	);
});
