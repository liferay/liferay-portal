/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {UAParser} from 'ua-parser-js';

import {log} from '../log';
import {getBrowserName} from './attributes/browser_name';
import {getBrowserVersion} from './attributes/browser_version';
import {getCookies} from './attributes/cookies';
import {getCustom} from './attributes/custom';
import {getDeviceType} from './attributes/device_type';
import {getHostname} from './attributes/hostname';
import {getLanguage} from './attributes/language';
import {getLocalDate} from './attributes/local_date';
import {getLocalHour} from './attributes/local_hour';
import {getPathname} from './attributes/pathname';
import {getReferrer} from './attributes/referrer';
import {getRequestParameters} from './attributes/request_parameters';
import {getSegment} from './attributes/segment';
import {getTimezone} from './attributes/timezone';
import {getUrl} from './attributes/url';
import {getUserAgent} from './attributes/user_agent';
import {check} from './check';
import {eq} from './operators/eq';
import {gt} from './operators/gt';
import {gte} from './operators/gte';
import {includes} from './operators/includes';
import {lt} from './operators/lt';
import {lte} from './operators/lte';
import {notEq} from './operators/not_eq';
import {notIncludes} from './operators/not_includes';

import type {
	Attribute,
	AudienceId,
	AudiencesDefinition,
	Conjunction,
	Operator,
	Rule,
} from '../index';

type AttributeValue = Set<string> | boolean | number | string;

interface OperatorImpl {
	(actual: any, expected: any): boolean;
}

export class Detection {
	private _audiencesDefinition: AudiencesDefinition;
	private _acSegments: Set<string> | undefined;
	private _uaParser: UAParser;

	constructor(audiencesDefinition: AudiencesDefinition) {
		check(audiencesDefinition);

		this._audiencesDefinition = audiencesDefinition;
		this._uaParser = new UAParser(navigator.userAgent);
	}

	async run(): Promise<AudienceId[]> {
		const matches = [];

		for (const audience of this._audiencesDefinition.audiences) {
			const {conjunction, id, rules} = audience;

			log(`Checking rules for audience '${id}'...`);

			const matched = await this._evaluateGroup(conjunction, rules);

			if (matched) {
				log(`Matched audience: ${id}`);

				matches.push(id);
			}
		}

		return matches;
	}

	private async _getAttribute(attr: Attribute): Promise<AttributeValue> {
		if (attr === 'browser_name') {
			return getBrowserName(this._uaParser);
		}
		else if (attr === 'browser_version') {
			return getBrowserVersion(this._uaParser);
		}
		else if (attr === 'cookies') {
			return getCookies();
		}
		else if (attr.startsWith('custom:')) {
			return getCustom(attr.slice(7));
		}
		else if (attr === 'device_type') {
			return getDeviceType(this._uaParser);
		}
		else if (attr === 'hostname') {
			return getHostname();
		}
		else if (attr === 'language') {
			return getLanguage();
		}
		else if (attr === 'local_date') {
			return getLocalDate();
		}
		else if (attr === 'local_hour') {
			return getLocalHour();
		}
		else if (attr === 'pathname') {
			return getPathname();
		}
		else if (attr === 'referrer') {
			return getReferrer();
		}
		else if (attr === 'request_parameters') {
			return getRequestParameters();
		}
		else if (attr === 'segment') {
			return getSegment();
		}
		else if (attr === 'timezone') {
			return getTimezone();
		}
		else if (attr === 'url') {
			return getUrl();
		}
		else if (attr === 'user_agent') {
			return getUserAgent();
		}
		else {
			throw new Error(`Unsupported attribute: ${attr}`);
		}
	}

	private async _evaluateGroup(
		conjunction: Conjunction,
		rules: Rule[]
	): Promise<boolean> {
		const results = await Promise.all(
			rules.map((rule) => this._evaluateRule(rule))
		);

		return conjunction === 'AND'
			? results.every(Boolean)
			: results.some(Boolean);
	}

	private async _evaluateRule(rule: Rule): Promise<boolean> {
		if ('conjunction' in rule) {
			return this._evaluateGroup(rule.conjunction, rule.rules);
		}

		const ruleDescription = `('${rule.attribute}' ${rule.operator} '${rule.value}')`;

		try {
			const operator = this._getOperator(rule.operator);
			const attribute = await this._getAttribute(rule.attribute);

			const result = operator(attribute, rule.value);

			log(`Evaluation of rule ${ruleDescription}: ${result}`);

			return result;
		}
		catch (error: any) {
			throw new Error(
				`An error was thrown when evaluating rule ${ruleDescription}: ` +
					(error.message || error)
			);
		}
	}

	private _getOperator(operator: Operator): OperatorImpl {
		if (operator === 'eq') {
			return eq;
		}
		else if (operator === 'gt') {
			return gt;
		}
		else if (operator === 'gte') {
			return gte;
		}
		else if (operator === 'includes') {
			return includes;
		}
		else if (operator === 'lt') {
			return lt;
		}
		else if (operator === 'lte') {
			return lte;
		}
		else if (operator === 'not_eq') {
			return notEq;
		}
		else if (operator === 'not_includes') {
			return notIncludes;
		}
		else {
			throw new Error(`Unsupported operator: ${operator}`);
		}
	}
}
