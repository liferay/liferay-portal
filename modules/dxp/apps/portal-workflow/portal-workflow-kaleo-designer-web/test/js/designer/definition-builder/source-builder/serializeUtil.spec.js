/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isEdge, isNode} from 'react-flow-renderer';

import DeserializeUtil from '../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/source-builder/deserializeUtil';
import {serializeDefinition} from '../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/source-builder/serializeUtil';
import {
	getReviewTaskDefinition,
	getRolesXML,
} from '../../../../utils/workflowDefinition';

const serializeElements = (xmlDefinition) => {
	const deserializeUtil = new DeserializeUtil(xmlDefinition);

	const elements = deserializeUtil.getElements();

	return serializeDefinition(
		{
			'xmlns': 'urn:liferay.com:liferay-workflow_7.4.0',
			'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
		},
		{description: '', name: 'definition', version: 1},
		elements.filter(isNode),
		elements.filter(isEdge)
	).replace(/\s/g, '');
};

describe('Serializing roles that carry an external reference code', () => {
	it('Writes the external reference code, type, and ID of every role', () => {
		const xmlDefinition = serializeElements(
			getReviewTaskDefinition({
				assignment: getRolesXML([
					{
						externalReferenceCode: 'ROLE_EXTERNAL_REFERENCE_CODE_1',
						id: 1,
						type: 'regular',
					},
				]),
				reassignment: getRolesXML([
					{
						externalReferenceCode: 'ROLE_EXTERNAL_REFERENCE_CODE_3',
						id: 3,
						type: 'organization',
					},
				]),
				recipient: getRolesXML([
					{
						externalReferenceCode: 'ROLE_EXTERNAL_REFERENCE_CODE_2',
						id: 2,
						type: 'site',
					},
				]),
				timerRecipient: getRolesXML([
					{
						externalReferenceCode: 'ROLE_EXTERNAL_REFERENCE_CODE_4',
						id: 4,
						type: 'depot',
					},
				]),
			})
		);

		expect(xmlDefinition).toContain(
			'<role><role-external-reference-code>ROLE_EXTERNAL_REFERENCE_CODE_1</role-external-reference-code><role-type>regular</role-type><role-id>1</role-id></role>'
		);
		expect(xmlDefinition).toContain(
			'<role><role-external-reference-code>ROLE_EXTERNAL_REFERENCE_CODE_2</role-external-reference-code><role-type>site</role-type><role-id>2</role-id></role>'
		);
		expect(xmlDefinition).toContain(
			'<role><role-external-reference-code>ROLE_EXTERNAL_REFERENCE_CODE_3</role-external-reference-code><role-type>organization</role-type><role-id>3</role-id></role>'
		);
		expect(xmlDefinition).toContain(
			'<role><role-external-reference-code>ROLE_EXTERNAL_REFERENCE_CODE_4</role-external-reference-code><role-type>depot</role-type><role-id>4</role-id></role>'
		);
	});
});
