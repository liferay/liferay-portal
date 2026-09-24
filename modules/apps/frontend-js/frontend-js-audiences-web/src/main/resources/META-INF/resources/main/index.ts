/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	clear,
	clearHandlers,
	get,
	getPriority,
	on,
	runDetection,
	runHandlers,
	setLogEnabled,
} from './implementation';

// JSON API

export interface AudiencesDefinition {
	audiences: Audience[];
}

export interface Audience {
	conjunction: Conjunction;
	id: AudienceId;
	rules: Rule[];
	scope: string[];
}

export type AudienceId = string;

export type Conjunction = 'AND' | 'OR';

export type Rule = LeafRule | RuleGroup;

export interface LeafRule {
	attribute: Attribute;
	operator: Operator;
	value: any;
}

export interface RuleGroup {
	conjunction: Conjunction;
	rules: Rule[];
}

export type Attribute =
	| 'batch_segments'
	| 'browser_name'
	| 'browser_version'
	| 'cookies'
	| `custom:${string}`
	| 'device_type'
	| 'hostname'
	| 'language'
	| 'local_date'
	| 'local_hour'
	| 'pathname'
	| 'real_time_segments'
	| 'referrer'
	| `request_parameters`
	| 'timezone'
	| 'url'
	| 'user_agent';
export type Operator =
	| 'eq'
	| 'gt'
	| 'gte'
	| 'includes'
	| 'lt'
	| 'lte'
	| 'not_eq'
	| 'not_includes';

// JavaScript API

export interface CustomAttribute<T> {
	name: string | undefined;
	(): Promise<T> | T;
}

export interface Handler {
	name: string | undefined;
	(): Promise<void> | void;
}

export interface RunDetectionOptions {
	filterAudiences?: (audience: Audience) => boolean;
	timeout?: number;
}

export interface AudiencesAPI {
	clear(): void;
	clearHandlers(): void;
	get(): Set<AudienceId>;
	getPriority(audienceId: AudienceId): number;
	on(audienceId: AudienceId, handler: Handler): void;

	/**
	 * Detect audiences based on a given audiences definition URL.
	 *
	 * This method clears all previously defined audiences before running the
	 * detection.
	 *
	 * When `options.filterAudiences` is given, only the audiences it accepts
	 * take part in the detection, and the rest are ignored as if they were
	 * absent from the definition.
	 *
	 * This method tries to do its best but it never rejects, just logs the
	 * errors/timeouts.
	 * @param audiencesDefinitionURL
	 * @param options
	 */
	runDetection(
		audiencesDefinitionURL: string,
		options?: RunDetectionOptions
	): Promise<void>;

	/**
	 * Run handlers based on currently detected audiences.
	 *
	 * This method clears all registered handlers after running.
	 *
	 * This method tries to do its best but it never rejects, just logs the
	 * errors.
	 */
	runHandlers(): Promise<void>;

	setLogEnabled(enabled: boolean): void;
}

export const audiences: AudiencesAPI = {
	clear,
	clearHandlers,
	get,
	getPriority,
	on,
	runDetection,
	runHandlers,
	setLogEnabled,
};
