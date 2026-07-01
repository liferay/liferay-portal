/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const BUFFER_ATTR = [null, '="', null, '" '];
const BUFFER_CLOSE_NODE = ['</', null, '>'];
const BUFFER_OPEN_NODE = ['<', null, null, '>'];
const COL_TYPES_ASSIGNMENT = [
	'address',
	'receptionType',
	'resourceActions',
	'roleId',
	'roleType',
	'scriptedAssignment',
	'scriptedRecipient',
	'taskAssignees',
	'user',
	'userId',
];
const COL_TYPES_FIELD = [
	'condition',
	'fork',
	'join',
	'join-xor',
	'state',
	'task',
];

if (Liferay.FeatureFlags['LPD-62272'] === true) {
	COL_TYPES_FIELD.splice(0, 0, 'ai-hub-agent');
	COL_TYPES_FIELD.splice(1, 0, 'ai-decision');
	COL_TYPES_FIELD.splice(5, 0, 'llm');
	COL_TYPES_FIELD.splice(6, 0, 'http-request');
	COL_TYPES_FIELD.splice(7, 0, 'service');
}

const DEFAULT_LANGUAGE = 'groovy';
const STR_BLANK = '';
const STR_CDATA_CLOSE = ']]>';
const STR_CDATA_OPEN = '<![CDATA[';
const STR_CHAR_CRLF = '\r\n';
const STR_CHAR_CR_LF_CRLF = /\r\n|\r|\n/;
const STR_CHAR_TAB = '\t';
const STR_DASH = '-';
const STR_METADATA = '<metadata';
const STR_SPACE = ' ';

const xmlNamespace = {
	'xmlns': 'urn:liferay.com:liferay-workflow_7.4.0',
	'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
	'xsi:schemaLocation':
		'urn:liferay.com:liferay-workflow_7.4.0 http://www.liferay.com/dtd/liferay-workflow-definition_7_4_0.xsd',
};

export {
	BUFFER_ATTR,
	BUFFER_CLOSE_NODE,
	BUFFER_OPEN_NODE,
	COL_TYPES_ASSIGNMENT,
	COL_TYPES_FIELD,
	DEFAULT_LANGUAGE,
	STR_BLANK,
	STR_CDATA_CLOSE,
	STR_CDATA_OPEN,
	STR_CHAR_CRLF,
	STR_CHAR_CR_LF_CRLF,
	STR_CHAR_TAB,
	STR_DASH,
	STR_METADATA,
	STR_SPACE,
	xmlNamespace,
};
