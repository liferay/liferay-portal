/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {parseIntent} from '../../../../src/main/resources/META-INF/resources/js/Categorization/utils/parseIntent';

const PASSTHROUGH = {actions: [], passthrough: true};

describe('parseIntent', () => {
	it('parses a categorize and tag request with categorize ordered first', () => {
		const verdict = parseIntent(
			JSON.stringify({
				actions: [
					{agent: 'tag', count: null, targets: []},
					{agent: 'categorize', count: null, targets: []},
				],
				passthrough: false,
			})
		);

		expect(verdict.passthrough).toBe(false);
		expect(verdict.actions.map((action) => action.agent)).toEqual([
			'categorize',
			'tag',
		]);
	});

	it('strips markdown fences before parsing', () => {
		const verdict = parseIntent(
			'```json\n{"actions":[{"agent":"tag","count":2,"targets":["kayaking"]}],"passthrough":false}\n```'
		);

		expect(verdict.actions).toEqual([
			{agent: 'tag', count: 2, targets: ['kayaking']},
		]);
	});

	it('clamps a zero count to one and defaults a missing count to three', () => {
		const verdict = parseIntent(
			JSON.stringify({
				actions: [
					{agent: 'categorize', count: 0, targets: []},
					{agent: 'tag', targets: []},
				],
				passthrough: false,
			})
		);

		expect(verdict.actions[0].count).toBe(1);
		expect(verdict.actions[1].count).toBe(3);
	});

	it('caps the count at ten', () => {
		const verdict = parseIntent(
			JSON.stringify({
				actions: [{agent: 'tag', count: 99, targets: []}],
				passthrough: false,
			})
		);

		expect(verdict.actions[0].count).toBe(10);
	});

	it('drops unknown agents', () => {
		const verdict = parseIntent(
			JSON.stringify({
				actions: [
					{agent: 'delete', targets: []},
					{agent: 'tag', targets: []},
				],
				passthrough: false,
			})
		);

		expect(verdict.actions.map((action) => action.agent)).toEqual(['tag']);
	});

	it('trims target names and drops blank ones', () => {
		const verdict = parseIntent(
			JSON.stringify({
				actions: [
					{
						agent: 'categorize',
						targets: ['  Travel  ', '', 'Fishing'],
					},
				],
				passthrough: false,
			})
		);

		expect(verdict.actions[0].targets).toEqual(['Travel', 'Fishing']);
	});

	it('keeps only the first action when an agent repeats', () => {
		const verdict = parseIntent(
			JSON.stringify({
				actions: [
					{agent: 'tag', targets: ['a']},
					{agent: 'tag', targets: ['b']},
				],
				passthrough: false,
			})
		);

		expect(verdict.actions).toHaveLength(1);
		expect(verdict.actions[0].targets).toEqual(['a']);
	});

	it('passes through when the model requests it', () => {
		expect(
			parseIntent(
				JSON.stringify({
					actions: [{agent: 'tag', targets: []}],
					passthrough: true,
				})
			)
		).toEqual(PASSTHROUGH);
	});

	it('passes through when no valid action remains', () => {
		expect(
			parseIntent(
				JSON.stringify({
					actions: [{agent: 'delete'}],
					passthrough: false,
				})
			)
		).toEqual(PASSTHROUGH);
	});

	it('passes through on malformed input', () => {
		expect(parseIntent('not json')).toEqual(PASSTHROUGH);
	});
});
