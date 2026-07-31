/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const AI_HUB_NODE_TYPES = [
	'ai-decision',
	'ai-hub-agent',
	'http-request',
	'llm',
	'service',
];

describe('AI Hub node gating on an AI Hub instance', () => {
	let colTypesField;
	let contents;
	let nodeTypes;

	beforeAll(() => {
		Liferay.FeatureFlags['LPD-62272'] = true;
		Liferay.PropsValues = {
			...Liferay.PropsValues,
			ENTERPRISE_PRODUCT_AI_HUB_ENABLED: true,
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

	it('offers every AI Hub node type', () => {
		AI_HUB_NODE_TYPES.forEach((nodeType) => {
			expect(colTypesField).toContain(nodeType);
			expect(contents).toHaveProperty(nodeType);
			expect(nodeTypes).toHaveProperty(nodeType);
		});
	});
});
