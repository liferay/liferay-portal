/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Cache} from '../cache';
import {log} from '../log';
import {TimeoutError} from '../timeout_error';
import {formatError, indent, waitForAbort} from '../util';
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
import {getSegments} from './attributes/segments';
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
	private readonly _audiencesDefinition: AudiencesDefinition;

	constructor(audiencesDefinition: AudiencesDefinition) {
		check(audiencesDefinition);

		this._audiencesDefinition = audiencesDefinition;
	}

	/**
	 * Run a detection over the definitions given in the constructor.
	 *
	 * Note that if timeout occurs, the `matches` parameter will contain only
	 * the audiences that were detected before the timeout.
	 *
	 * @param cache
	 * @param signal
	 * @param matches output parameter to hold detected audiences
	 * @throws TimeoutError on timeout
	 * @throws Error if anything fails
	 */
	async run(
		cache: Cache,
		signal: AbortSignal,
		matches: AudienceId[]
	): Promise<void> {
		await Promise.race([
			waitForAbort(signal),
			this._matchAudiences(cache, signal, matches),
		]);

		if (signal.aborted) {
			throw new TimeoutError('Detection of audiences');
		}
	}

	/**
	 * Match audiences in parallel.
	 *
	 * Note that if timeout occurs, the `matches` parameter will contain only
	 * the audiences that were detected before the timeout.
	 *
	 * Never rejects, just logs errors.
	 *
	 * @param matches output parameter to hold detected audiences
	 * @private
	 */
	private async _matchAudiences(
		cache: Cache,
		signal: AbortSignal,
		matches: AudienceId[]
	): Promise<void> {
		const promises = this._audiencesDefinition.audiences.map(
			async (audience) => {
				const {conjunction, id, rules} = audience;

				log(`Checking rules for audience '${id}'...`);

				try {
					const matched = await this._evaluateGroup(
						cache,
						signal,
						conjunction,
						rules
					);

					if (matched) {
						log(`Audience '${id}' is matched`);

						matches.push(id);
					}
				}
				catch (error: any) {
					log(
						`Audience '${id}' is not matched because its evaluation failed with error:\n${indent(2, true, formatError(error))}`
					);
				}
			}
		);

		await Promise.allSettled(promises);
	}

	private async _evaluateGroup(
		cache: Cache,
		signal: AbortSignal,
		conjunction: Conjunction,
		rules: Rule[]
	): Promise<boolean> {
		const results = await Promise.all(
			rules.map((rule) => this._evaluateRule(cache, signal, rule))
		);

		return conjunction === 'AND'
			? results.every(Boolean)
			: results.some(Boolean);
	}

	private async _evaluateRule(
		cache: Cache,
		signal: AbortSignal,
		rule: Rule
	): Promise<boolean> {
		if ('conjunction' in rule) {
			return this._evaluateGroup(
				cache,
				signal,
				rule.conjunction,
				rule.rules
			);
		}

		const ruleDescription = `('${rule.attribute}' ${rule.operator} '${rule.value}')`;

		try {
			const operator = this._getOperator(rule.operator);

			const attribute = await this._getAttribute(rule.attribute, cache);

			if (signal.aborted) {
				throw new TimeoutError('Rule evaluation');
			}

			const result = operator(attribute, rule.value);

			log(`Rule ${ruleDescription} evaluates to ${result}`);

			return result;
		}
		catch (error: any) {
			throw new Error(
				`An error was thrown when evaluating rule ${ruleDescription}`,
				{cause: error}
			);
		}
	}

	private async _getAttribute(
		attr: Attribute,
		cache: Cache
	): Promise<AttributeValue> {
		if (attr === 'browser_name') {
			return getBrowserName(cache);
		}
		else if (attr === 'browser_version') {
			return getBrowserVersion(cache);
		}
		else if (attr === 'cookies') {
			return getCookies();
		}
		else if (attr.startsWith('custom:')) {
			return getCustom(attr.slice(7));
		}
		else if (attr === 'device_type') {
			return getDeviceType(cache);
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
		else if (attr === 'segments') {
			return getSegments(cache);
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
