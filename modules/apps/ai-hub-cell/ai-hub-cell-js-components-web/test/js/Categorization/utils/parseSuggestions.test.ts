/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ECategorizationAgent} from '../../../../src/main/resources/META-INF/resources/js/Categorization/types';
import {parseSuggestions} from '../../../../src/main/resources/META-INF/resources/js/Categorization/utils/parseSuggestions';

const CANDIDATES = [
	{id: 39001, name: 'International', vocabulary: 'Travel'},
	{id: 39002, name: 'Roadtrip', vocabulary: 'Travel'},
	{id: 39003, name: 'Culture', vocabulary: 'Topic'},
];

describe('parseSuggestions', () => {
	describe('categories', () => {
		it('parses fenced JSON and keeps only candidate ids', () => {
			const data =
				'```json\n{"suggestions":[{"id":39001,"name":"International","confidence":0.9},{"id":99999,"name":"Bogus","confidence":0.8}]}\n```';

			expect(
				parseSuggestions(ECategorizationAgent.AUTO_CATEGORIZE, data, {
					candidateCategories: CANDIDATES,
					content: 'x',
				})
			).toEqual([{id: 39001, name: 'International'}]);
		});

		it('parses raw unfenced JSON', () => {
			const data =
				'{"suggestions":[{"id":39002,"name":"Roadtrip","confidence":0.7}]}';

			expect(
				parseSuggestions(ECategorizationAgent.AUTO_CATEGORIZE, data, {
					candidateCategories: CANDIDATES,
					content: 'x',
				})
			).toEqual([{id: 39002, name: 'Roadtrip'}]);
		});

		it('takes the category name from the candidate set not the model', () => {
			const data =
				'{"suggestions":[{"id":39001,"name":"WRONG","confidence":0.9}]}';

			expect(
				parseSuggestions(ECategorizationAgent.AUTO_CATEGORIZE, data, {
					candidateCategories: CANDIDATES,
					content: 'x',
				})
			).toEqual([{id: 39001, name: 'International'}]);
		});

		it('coerces string ids and dedupes', () => {
			const data =
				'{"suggestions":[{"id":"39001","confidence":0.9},{"id":39001,"confidence":0.5}]}';

			expect(
				parseSuggestions(ECategorizationAgent.AUTO_CATEGORIZE, data, {
					candidateCategories: CANDIDATES,
					content: 'x',
				})
			).toEqual([{id: 39001, name: 'International'}]);
		});

		it('sorts by confidence and clamps to count', () => {
			const data =
				'{"suggestions":[{"id":39001,"confidence":0.2},{"id":39002,"confidence":0.9},{"id":39003,"confidence":0.5}]}';

			expect(
				parseSuggestions(ECategorizationAgent.AUTO_CATEGORIZE, data, {
					candidateCategories: CANDIDATES,
					content: 'x',
					count: 2,
				})
			).toEqual([
				{id: 39002, name: 'Roadtrip'},
				{id: 39003, name: 'Culture'},
			]);
		});

		it('returns an empty array for malformed JSON', () => {
			expect(
				parseSuggestions(
					ECategorizationAgent.AUTO_CATEGORIZE,
					'not json',
					{candidateCategories: CANDIDATES, content: 'x'}
				)
			).toEqual([]);
		});

		it('returns an empty array when suggestions is missing or empty', () => {
			expect(
				parseSuggestions(
					ECategorizationAgent.AUTO_CATEGORIZE,
					'{"foo":1}',
					{candidateCategories: CANDIDATES, content: 'x'}
				)
			).toEqual([]);

			expect(
				parseSuggestions(
					ECategorizationAgent.AUTO_CATEGORIZE,
					'{"suggestions":[]}',
					{candidateCategories: CANDIDATES, content: 'x'}
				)
			).toEqual([]);
		});
	});

	describe('tags', () => {
		it('keeps name and isNew and dedupes case-insensitively', () => {
			const data =
				'{"suggestions":[{"name":"Japan","isNew":false,"confidence":0.9},{"name":"japan","isNew":true,"confidence":0.4},{"name":"Culture","isNew":true,"confidence":0.7}]}';

			expect(
				parseSuggestions(ECategorizationAgent.GENERATE_TAGS, data, {
					content: 'x',
					existingTags: ['Japan'],
				})
			).toEqual([
				{isNew: false, name: 'Japan'},
				{isNew: true, name: 'Culture'},
			]);
		});

		it('coerces a missing isNew to false and trims names', () => {
			const data =
				'{"suggestions":[{"name":"  Travel  ","confidence":0.8}]}';

			expect(
				parseSuggestions(ECategorizationAgent.GENERATE_TAGS, data, {
					content: 'x',
				})
			).toEqual([{isNew: false, name: 'Travel'}]);
		});

		it('defaults to three suggestions when count is unset', () => {
			const data =
				'{"suggestions":[{"name":"a","confidence":0.9},{"name":"b","confidence":0.8},{"name":"c","confidence":0.7},{"name":"d","confidence":0.6}]}';

			const result = parseSuggestions(
				ECategorizationAgent.GENERATE_TAGS,
				data,
				{content: 'x'}
			);

			expect(result).toHaveLength(3);
			expect(result.map((suggestion) => suggestion.name)).toEqual([
				'a',
				'b',
				'c',
			]);
		});

		it('strips fences for tags too', () => {
			const data =
				'```\n{"suggestions":[{"name":"Japan","isNew":false}]}\n```';

			expect(
				parseSuggestions(ECategorizationAgent.GENERATE_TAGS, data, {
					content: 'x',
				})
			).toEqual([{isNew: false, name: 'Japan'}]);
		});
	});
});
