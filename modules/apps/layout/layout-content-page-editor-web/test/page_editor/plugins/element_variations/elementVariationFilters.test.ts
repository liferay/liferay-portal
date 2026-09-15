/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	Filter,
	getFilterOptions,
	getFilterText,
	getFilteredVariations,
} from '../../../../src/main/resources/META-INF/resources/page_editor/plugins/element_variations/elementVariationFilters';
import {ElementVariation} from '../../../../src/main/resources/META-INF/resources/page_editor/plugins/element_variations/elementVariationsReducer';

const AUDIENCES = [
	{label: 'Audience A', value: 'audience-a'},
	{label: 'Audience B', value: 'audience-b'},
];

function createVariation(
	overrides: Partial<ElementVariation> = {}
): ElementVariation {
	return {
		active: true,
		audienceEntryERCs: [],
		externalReferenceCode: 'erc',
		hide: false,
		html: {},
		js: {},
		key: 'variation',
		name: 'Variation',
		segmentsExperienceERC: 'experience',
		targetElement: '.title',
		...overrides,
	};
}

describe('elementVariationFilters', () => {
	describe('getFilterOptions', () => {
		it('lists the audiences for the audience filter', () => {
			expect(getFilterOptions('audience', AUDIENCES)).toBe(AUDIENCES);
		});

		it('lists the enabled and disabled options for the status filter', () => {
			expect(
				getFilterOptions('status', AUDIENCES).map(({value}) => value)
			).toEqual(['enabled', 'disabled']);
		});

		it('lists the content options for the type filter', () => {
			expect(
				getFilterOptions('type', AUDIENCES).map(({value}) => value)
			).toEqual(['html', 'javascript', 'hide-element']);
		});
	});

	describe('getFilterText', () => {
		beforeEach(() => {
			(Liferay.Language.get as jest.Mock).mockImplementation(
				(key: string) => (key === 'exclude' ? 'Exclude' : key)
			);
		});

		it('joins the selected option labels', () => {
			const filter: Filter = {
				exclude: false,
				type: 'audience',
				values: ['audience-a', 'audience-b'],
			};

			expect(getFilterText(filter, AUDIENCES)).toEqual({
				hiddenCount: 0,
				label: 'Audience A, Audience B',
			});
		});

		it('marks the label as excluding when the filter excludes', () => {
			const filter: Filter = {
				exclude: true,
				type: 'audience',
				values: ['audience-a'],
			};

			expect(getFilterText(filter, AUDIENCES)).toEqual({
				hiddenCount: 0,
				label: '(Exclude) Audience A',
			});
		});

		it('previews three labels and counts the rest as hidden', () => {
			const filter: Filter = {
				exclude: false,
				type: 'type',
				values: ['html', 'javascript', 'hide-element'],
			};

			expect(getFilterText(filter, AUDIENCES)).toEqual({
				hiddenCount: 0,
				label: 'html, javascript, hide-element',
			});

			expect(
				getFilterText(
					{
						exclude: false,
						type: 'audience',
						values: ['a', 'b', 'c', 'd', 'e'],
					},
					[
						{label: 'A', value: 'a'},
						{label: 'B', value: 'b'},
						{label: 'C', value: 'c'},
						{label: 'D', value: 'd'},
						{label: 'E', value: 'e'},
					]
				)
			).toEqual({hiddenCount: 2, label: 'A, B, C'});
		});
	});

	describe('getFilteredVariations', () => {
		it('returns every variation when there are no filters', () => {
			const elementVariations = [createVariation()];

			expect(getFilteredVariations(elementVariations, [])).toEqual(
				elementVariations
			);
		});

		it('keeps the variations matching any of the selected audiences', () => {
			const matching = createVariation({
				audienceEntryERCs: ['audience-a'],
				key: 'matching',
			});

			const other = createVariation({
				audienceEntryERCs: ['audience-b'],
				key: 'other',
			});

			expect(
				getFilteredVariations(
					[matching, other],
					[{exclude: false, type: 'audience', values: ['audience-a']}]
				)
			).toEqual([matching]);
		});

		it('drops the variations matching the selected audiences when excluding', () => {
			const matching = createVariation({
				audienceEntryERCs: ['audience-a'],
				key: 'matching',
			});

			const other = createVariation({
				audienceEntryERCs: ['audience-b'],
				key: 'other',
			});

			expect(
				getFilteredVariations(
					[matching, other],
					[{exclude: true, type: 'audience', values: ['audience-a']}]
				)
			).toEqual([other]);
		});

		it('derives the status from the active flag', () => {
			const enabled = createVariation({key: 'enabled'});
			const disabled = createVariation({active: false, key: 'disabled'});

			expect(
				getFilteredVariations(
					[enabled, disabled],
					[{exclude: false, type: 'status', values: ['disabled']}]
				)
			).toEqual([disabled]);
		});

		it('derives the type from the html, javascript and hide values', () => {
			const html = createVariation({
				html: {en_US: '<p>Hello</p>'},
				key: 'html',
			});

			const javascript = createVariation({
				js: {en_US: 'console.log("hi");'},
				key: 'javascript',
			});

			const hidden = createVariation({hide: true, key: 'hidden'});

			expect(
				getFilteredVariations(
					[html, javascript, hidden],
					[
						{
							exclude: false,
							type: 'type',
							values: ['javascript', 'hide-element'],
						},
					]
				)
			).toEqual([javascript, hidden]);
		});

		it('ignores localized values that are empty', () => {
			const elementVariations = [createVariation({html: {en_US: ''}})];

			expect(
				getFilteredVariations(elementVariations, [
					{exclude: false, type: 'type', values: ['html']},
				])
			).toEqual([]);
		});

		it('requires every filter to match', () => {
			const matching = createVariation({
				audienceEntryERCs: ['audience-a'],
				key: 'matching',
			});

			const other = createVariation({
				active: false,
				audienceEntryERCs: ['audience-a'],
				key: 'other',
			});

			expect(
				getFilteredVariations(
					[matching, other],
					[
						{
							exclude: false,
							type: 'audience',
							values: ['audience-a'],
						},
						{exclude: false, type: 'status', values: ['enabled']},
					]
				)
			).toEqual([matching]);
		});
	});
});
