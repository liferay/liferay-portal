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
	getUsersXML,
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

describe('Serializing roles and users that carry an external reference code', () => {
	it('Writes every reference back with its external reference code and without the fields the code replaces', () => {
		const xmlDefinition = serializeElements(
			getReviewTaskDefinition({
				assignment: getUsersXML([
					{
						externalReferenceCode: 'USER_EXTERNAL_REFERENCE_CODE_1',
						id: 1,
					},
					{id: 10},
					{
						emailAddress: 'user@liferay.com',
						externalReferenceCode: 'USER_EXTERNAL_REFERENCE_CODE_2',
					},
				]),
				reassignment: getRolesXML([{name: 'Portal Content Reviewer'}]),
				recipient: getRolesXML([
					{
						externalReferenceCode: 'ROLE_EXTERNAL_REFERENCE_CODE_1',
						name: 'Portal Content Publisher',
						type: 'site',
					},
				]),
				timerRecipient: getRolesXML([
					{
						externalReferenceCode: 'ROLE_EXTERNAL_REFERENCE_CODE_2',
						id: 2,
						type: 'depot',
					},
				]),
			})
		);

		expect(xmlDefinition).toContain(
			'<assignments><user><user-external-reference-code>USER_EXTERNAL_REFERENCE_CODE_1</user-external-reference-code><user-id>1</user-id></user><user><user-id>10</user-id></user><user><user-external-reference-code>USER_EXTERNAL_REFERENCE_CODE_2</user-external-reference-code></user></assignments>'
		);
		expect(xmlDefinition).toContain(
			'<reassignments><roles><role><role-type>regular</role-type><name>PortalContentReviewer</name></role></roles></reassignments>'
		);
		expect(xmlDefinition).toContain(
			'<recipients><roles><role><role-external-reference-code>ROLE_EXTERNAL_REFERENCE_CODE_1</role-external-reference-code><role-type>site</role-type></role></roles></recipients><execution-type>'
		);
		expect(xmlDefinition).toContain(
			'<recipients><roles><role><role-external-reference-code>ROLE_EXTERNAL_REFERENCE_CODE_2</role-external-reference-code><role-type>depot</role-type><role-id>2</role-id></role></roles></recipients></timer-notification>'
		);
	});
});
