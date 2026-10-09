/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import path from 'path';
import {isEdge, isNode} from 'react-flow-renderer';

import DeserializeUtil from '../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/source-builder/deserializeUtil';
import {serializeDefinition} from '../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/source-builder/serializeUtil';

describe('Serializing roles that carry an external reference code', () => {
	it('Writes the external reference code of every role', () => {
		const deserializeUtil = new DeserializeUtil(
			fs.readFileSync(
				path.join(
					__dirname,
					'../../../../dependencies/roles-with-external-reference-codes-workflow-definition.xml'
				),
				'utf8'
			)
		);

		const elements = deserializeUtil.getElements();

		const xmlDefinition = serializeDefinition(
			{
				'xmlns': 'urn:liferay.com:liferay-workflow_7.4.0',
				'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
			},
			{description: '', name: 'definition', version: 1},
			elements.filter(isNode),
			elements.filter(isEdge)
		);

		expect(xmlDefinition).toContain(
			'<role-external-reference-code>ROLE1_ERC</role-external-reference-code>'
		);
		expect(xmlDefinition).toContain(
			'<role-external-reference-code>ROLE2_ERC</role-external-reference-code>'
		);
		expect(xmlDefinition).toContain(
			'<role-external-reference-code>ROLE3_ERC</role-external-reference-code>'
		);
		expect(xmlDefinition).toContain(
			'<role-external-reference-code>ROLE4_ERC</role-external-reference-code>'
		);
	});
});
