/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {checkTypes} from './util';

import type {
	Attribute,
	Audience,
	AudiencesDefinition,
	Conjunction,
	LeafRule,
	Operator,
	Rule,
	RuleGroup,
} from '../index';

const ATTRIBUTES: Attribute[] = [
	'browser_name',
	'browser_version',
	'cookies',
	'custom:*',
	'device_type',
	'hostname',
	'language',
	'local_date',
	'local_hour',
	'pathname',
	'referrer',
	'request_parameters',
	'segments',
	'timezone',
	'url',
	'user_agent',
];

const CONJUNCTIONS: Conjunction[] = ['AND', 'OR'];

const OPERATORS: Operator[] = [
	'eq',
	'gt',
	'gte',
	'includes',
	'lt',
	'lte',
	'not_eq',
	'not_includes',
];

const OPERATOR_VALUE_TYPES: {[operator in Operator]: string[]} = {
	eq: ['boolean', 'number', 'string'],
	gt: ['number', 'string'],
	gte: ['number', 'string'],
	includes: ['string'],
	lt: ['number', 'string'],
	lte: ['number', 'string'],
	not_eq: ['boolean', 'number', 'string'],
	not_includes: ['string'],
};

export function check(audiencesDefinition: AudiencesDefinition) {
	const what = 'Audiences definition';

	checkObject(audiencesDefinition, what);
	checkKeys(audiencesDefinition, ['audiences'], what);
	checkArray(audiencesDefinition.audiences, `${what} field 'audiences'`);

	const ids = new Set<string>();

	for (let i = 0; i < audiencesDefinition.audiences.length; i++) {
		const audience = audiencesDefinition.audiences[i];

		checkAudience(audience, i);

		if (ids.has(audience.id)) {
			throw new Error(
				`${what} has more than one audience with id '${audience.id}'`
			);
		}

		ids.add(audience.id);
	}
}

function checkAudience(audience: Audience, index: number) {
	const what = `Audience '${audience?.id ?? `#${index + 1}`}'`;

	checkObject(audience, what);
	checkKeys(audience, ['conjunction', 'id', 'rules'], what);
	checkOneOf(
		audience.conjunction,
		CONJUNCTIONS,
		`${what} field 'conjunction'`
	);
	checkString(audience.id, `${what} field 'id'`);
	checkRules(audience.rules, `${what} field 'rules'`);
}

function checkRules(rules: Rule[], what: string) {
	checkArray(rules, what);

	for (let i = 0; i < rules.length; i++) {
		checkRule(rules[i], `${what}[${i}]`);
	}
}

function checkRule(rule: Rule, what: string) {
	checkObject(rule, what);

	if ('conjunction' in rule) {
		checkRuleGroup(rule, what);
	}
	else {
		checkLeafRule(rule as LeafRule, what);
	}
}

function checkRuleGroup(ruleGroup: RuleGroup, what: string) {
	checkKeys(ruleGroup, ['conjunction', 'rules'], what);
	checkOneOf(
		ruleGroup.conjunction,
		CONJUNCTIONS,
		`${what} field 'conjunction'`
	);
	checkRules(ruleGroup.rules, `${what} field 'rules'`);
}

function checkLeafRule(leafRule: LeafRule, what: string) {
	checkKeys(leafRule, ['attribute', 'operator', 'value'], what);
	checkOneOf(leafRule.attribute, ATTRIBUTES, `${what} field 'attribute'`);
	checkOneOf(leafRule.operator, OPERATORS, `${what} field 'operator'`);
	checkTypes(
		leafRule.value,
		OPERATOR_VALUE_TYPES[leafRule.operator],
		`${what} field 'value'`
	);
}

function checkArray(thing: any, what: string) {
	if (!Array.isArray(thing)) {
		throw new Error(`${what} must be an array`);
	}
}

function checkKeys(thing: any, expectedKeys: string[], what: string) {
	for (const actualKey of Object.keys(thing)) {
		if (!expectedKeys.includes(actualKey)) {
			throw new Error(`${what} has unexpected field '${actualKey}'`);
		}
	}

	for (const expectedKey of expectedKeys) {
		if (!(expectedKey in thing)) {
			throw new Error(`${what} is missing field '${expectedKey}'`);
		}
	}
}

function checkObject(thing: any, what: string) {
	if (thing === null || typeof thing !== 'object' || Array.isArray(thing)) {
		throw new Error(`${what} must be an object`);
	}
}

function checkOneOf<T extends string>(value: any, alloweds: T[], what: string) {
	for (const allowed of alloweds) {
		if (allowed === value) {
			return;
		}

		if (
			allowed.endsWith('*') &&
			value.startsWith(allowed.substring(0, allowed.length - 1))
		) {
			return;
		}
	}

	throw new Error(
		`${what} must be one of: ${alloweds.map((v) => `'${v}'`).join(', ')}`
	);
}

function checkString(thing: any, what: string) {
	if (typeof thing !== 'string') {
		throw new Error(`${what} must be a string`);
	}
}
