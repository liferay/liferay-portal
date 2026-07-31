/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const AI_HUB_ONLY_NODE_TYPES = [
	'ai-decision',
	'http-request',
	'llm',
	'service',
];

describe('AI Hub node gating on a client DXP instance', () => {
	let colTypesField;
	let contents;
	let nodeTypes;

	beforeAll(() => {
		Liferay.FeatureFlags['LPD-62272'] = true;
		Liferay.PropsValues = {
			...Liferay.PropsValues,
			ENTERPRISE_PRODUCT_AI_HUB_ENABLED: false,
		};

		colTypesField =
			require('../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/source-builder/constants').COL_TYPES_FIELD;
		contents =
			require('../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/sidebar/Sidebar').contents;
		nodeTypes =
			require('../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/nodes/utils').nodeTypes;
	});

	afterAll(() => {
		Liferay.FeatureFlags['LPD-62272'] = false;

		delete Liferay.PropsValues.ENTERPRISE_PRODUCT_AI_HUB_ENABLED;
	});

	it('offers the AI Hub Agent node, whose executor the portal deploys', () => {
		expect(colTypesField).toContain('ai-hub-agent');
		expect(contents).toHaveProperty('ai-hub-agent');
		expect(nodeTypes).toHaveProperty('ai-hub-agent');
	});

	it('withholds the nodes whose executors only the AI Hub deploys', () => {
		AI_HUB_ONLY_NODE_TYPES.forEach((nodeType) => {
			expect(colTypesField).not.toContain(nodeType);
			expect(contents).not.toHaveProperty(nodeType);
			expect(nodeTypes).not.toHaveProperty(nodeType);
		});
	});
});
