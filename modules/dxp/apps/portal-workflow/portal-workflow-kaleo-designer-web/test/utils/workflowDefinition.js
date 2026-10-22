/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import path from 'path';

const REVIEW_TASK_DEFINITION = fs.readFileSync(
	path.join(__dirname, '../dependencies/review-task-workflow-definition.xml'),
	'utf8'
);

function getXMLTag(name, value) {
	if (value === undefined) {
		return '';
	}

	return `<${name}>${value}</${name}>`;
}

export function getReviewTaskDefinition({
	assignment,
	reassignment,
	recipient,
	timerRecipient,
}) {
	return REVIEW_TASK_DEFINITION.replace('[$ASSIGNMENT$]', assignment)
		.replace('[$RECIPIENT$]', recipient)
		.replace('[$REASSIGNMENT$]', reassignment)
		.replace('[$TIMER_RECIPIENT$]', timerRecipient);
}

export function getReviewTaskDefinitionWithReference(reference) {
	return getReviewTaskDefinition({
		assignment: reference,
		reassignment: reference,
		recipient: reference,
		timerRecipient: reference,
	});
}

export function getRolesXML(roles) {
	const rolesXML = roles.map(({externalReferenceCode, id, name, type}) =>
		[
			'<role>',
			getXMLTag('role-external-reference-code', externalReferenceCode),
			getXMLTag('role-type', type),
			getXMLTag('role-id', id),
			getXMLTag('name', name),
			'</role>',
		].join('')
	);

	return `<roles>${rolesXML.join('')}</roles>`;
}

export function getUsersXML(users) {
	const usersXML = users.map(({emailAddress, externalReferenceCode, id}) =>
		[
			'<user>',
			getXMLTag('user-external-reference-code', externalReferenceCode),
			getXMLTag('email-address', emailAddress),
			getXMLTag('user-id', id),
			'</user>',
		].join('')
	);

	return usersXML.join('');
}
