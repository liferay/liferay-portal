/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {check} from '../src/main/resources/META-INF/resources/main/detection/check';

function leafRule(overrides: {[key: string]: any} = {}): any {
	return {
		attribute: 'language',
		operator: 'eq',
		value: 'en-US',
		...overrides,
	};
}

function ruleGroup(overrides: {[key: string]: any} = {}): any {
	return {
		conjunction: 'OR',
		rules: [leafRule()],
		...overrides,
	};
}

function audience(overrides: {[key: string]: any} = {}): any {
	return {
		conjunction: 'AND',
		id: 'the_audience',
		rules: [leafRule()],
		...overrides,
	};
}

function audiencesDefinition(overrides: {[key: string]: any} = {}): any {
	return {
		audiences: [audience()],
		...overrides,
	};
}

describe('check', () => {
	describe('valid', () => {
		it('accepts a definition with leaf rules and a nested rule group', () => {
			expect(() =>
				check(
					audiencesDefinition({
						audiences: [
							audience({
								rules: [leafRule(), ruleGroup()],
							}),
						],
					})
				)
			).not.toThrow();
		});

		it('accepts a number value for a comparison operator', () => {
			expect(() =>
				check(
					audiencesDefinition({
						audiences: [
							audience({
								rules: [
									leafRule({
										attribute: 'local_hour',
										operator: 'eq',
										value: 10,
									}),
								],
							}),
						],
					})
				)
			).not.toThrow();
		});

		it('accepts a boolean value for an equality operator', () => {
			expect(() =>
				check(
					audiencesDefinition({
						audiences: [
							audience({
								rules: [
									leafRule({
										attribute: 'custom:logged_in',
										operator: 'eq',
										value: true,
									}),
									leafRule({
										attribute: 'custom:logged_in',
										operator: 'not_eq',
										value: false,
									}),
								],
							}),
						],
					})
				)
			).not.toThrow();
		});

		it('accepts audiences with distinct ids', () => {
			expect(() =>
				check(
					audiencesDefinition({
						audiences: [
							audience({id: 'audience-1'}),
							audience({id: 'audience-2'}),
						],
					})
				)
			).not.toThrow();
		});
	});

	describe('audience id uniqueness', () => {
		it('rejects audiences with duplicate ids', () => {
			expect(() =>
				check(
					audiencesDefinition({
						audiences: [
							audience({id: 'duplicate'}),
							audience({id: 'duplicate'}),
						],
					})
				)
			).toThrow(
				"Audiences definition has more than one audience with id 'duplicate'"
			);
		});
	});

	describe('check', () => {
		it('rejects null', () => {
			expect(() => check(null as any)).toThrow(
				'Audiences definition must be an object'
			);
		});

		it('rejects an array', () => {
			expect(() => check([] as any)).toThrow(
				'Audiences definition must be an object'
			);
		});

		it('rejects a missing audiences field', () => {
			expect(() => check({} as any)).toThrow(
				"Audiences definition is missing field 'audiences'"
			);
		});

		it('rejects an unexpected field', () => {
			expect(() => check(audiencesDefinition({extra: true}))).toThrow(
				"Audiences definition has unexpected field 'extra'"
			);
		});

		it('rejects a non-array audiences field', () => {
			expect(() => check(audiencesDefinition({audiences: {}}))).toThrow(
				"Audiences definition field 'audiences' must be an array"
			);
		});
	});

	describe('check audience', () => {
		it('rejects a non-object, naming it by index', () => {
			expect(() =>
				check(audiencesDefinition({audiences: [null]}))
			).toThrow("Audience '#1' must be an object");
		});

		it('rejects a missing field', () => {
			const {rules: _rules, ...withoutRules} = audience();

			expect(() =>
				check(audiencesDefinition({audiences: [withoutRules]}))
			).toThrow("Audience 'the_audience' is missing field 'rules'");
		});

		it('rejects an unexpected field', () => {
			expect(() =>
				check(
					audiencesDefinition({audiences: [audience({extra: true})]})
				)
			).toThrow("Audience 'the_audience' has unexpected field 'extra'");
		});

		it('rejects an invalid conjunction', () => {
			expect(() =>
				check(
					audiencesDefinition({
						audiences: [audience({conjunction: 'XOR'})],
					})
				)
			).toThrow(
				"Audience 'the_audience' field 'conjunction' must be one of: 'AND', 'OR'"
			);
		});

		it('rejects a non-string id', () => {
			expect(() =>
				check(audiencesDefinition({audiences: [audience({id: 123})]}))
			).toThrow("field 'id' must be a string");
		});
	});

	describe('leaf rule', () => {
		function withRule(rule: any) {
			return audiencesDefinition({
				audiences: [audience({rules: [rule]})],
			});
		}

		it('rejects a non-object rule', () => {
			expect(() => check(withRule(null))).toThrow(
				"Audience 'the_audience' field 'rules'[0] must be an object"
			);
		});

		it('rejects an unexpected field', () => {
			expect(() => check(withRule(leafRule({extra: true})))).toThrow(
				"field 'rules'[0] has unexpected field 'extra'"
			);
		});

		it('rejects a missing field', () => {
			const {value: _value, ...withoutValue} = leafRule();

			expect(() => check(withRule(withoutValue))).toThrow(
				"field 'rules'[0] is missing field 'value'"
			);
		});

		it('rejects an invalid attribute', () => {
			expect(() =>
				check(withRule(leafRule({attribute: 'nope'})))
			).toThrow("field 'rules'[0] field 'attribute' must be one of");
		});

		it('rejects an invalid operator', () => {
			expect(() => check(withRule(leafRule({operator: 'nope'})))).toThrow(
				"field 'rules'[0] field 'operator' must be one of"
			);
		});

		it('rejects a number value for an includes operator', () => {
			expect(() =>
				check(
					withRule(
						leafRule({
							attribute: 'segments',
							operator: 'includes',
							value: 123,
						})
					)
				)
			).toThrow(
				"field 'rules'[0] field 'value' must be of type 'string' but got: number"
			);
		});

		it('rejects a non-scalar value for a comparison operator', () => {
			expect(() =>
				check(withRule(leafRule({operator: 'eq', value: {}})))
			).toThrow(
				"field 'rules'[0] field 'value' must be of type " +
					"'boolean' or 'number' or 'string' but got: object"
			);
		});

		it('treats a rule with a conjunction key as a rule group', () => {
			expect(() =>
				check(withRule(leafRule({conjunction: 'AND'})))
			).toThrow("field 'rules'[0] has unexpected field 'attribute'");
		});
	});

	describe('rule group', () => {
		function withRule(rule: any) {
			return audiencesDefinition({
				audiences: [audience({rules: [rule]})],
			});
		}

		it('accepts a valid nested rule group', () => {
			expect(() => check(withRule(ruleGroup()))).not.toThrow();
		});

		it('rejects an invalid conjunction', () => {
			expect(() =>
				check(withRule(ruleGroup({conjunction: 'XOR'})))
			).toThrow(
				"field 'rules'[0] field 'conjunction' must be one of: 'AND', 'OR'"
			);
		});

		it('rejects an unexpected field', () => {
			expect(() => check(withRule(ruleGroup({extra: true})))).toThrow(
				"field 'rules'[0] has unexpected field 'extra'"
			);
		});

		it('recurses into nested rules', () => {
			expect(() =>
				check(
					withRule(
						ruleGroup({rules: [leafRule({attribute: 'nope'})]})
					)
				)
			).toThrow(
				"field 'rules'[0] field 'rules'[0] field 'attribute' must be one of"
			);
		});
	});
});
