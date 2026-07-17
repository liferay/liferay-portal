/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ElementVariation,
	State,
	createElementVariation,
	createInitialState,
	reducer,
} from '../../../../src/main/resources/META-INF/resources/page_editor/plugins/element_variations/elementVariationsReducer';

function buildElementVariation(
	properties: Partial<ElementVariation> = {}
): ElementVariation {
	return {
		active: true,
		audienceEntryERCs: [],
		externalReferenceCode: '',
		hide: false,
		html: {},
		js: {},
		key: 'key-1',
		name: '',
		segmentsExperienceERC: 'experience-1',
		targetElement: '',
		...properties,
	};
}

function buildState(properties: Partial<State> = {}): State {
	return {
		defaultLanguageId: 'en_US',
		draftElementVariation: null,
		editableElementOptions: [],
		elementVariations: [],
		experienceKey: '',
		highlightedTargetElement: null,
		languageId: 'en_US',
		...properties,
	};
}

describe('elementVariationsReducer', () => {
	describe('createElementVariation', () => {
		it('creates an empty variation for the given experience with a unique key', () => {
			const elementVariation = createElementVariation('experience-1');

			expect(elementVariation.segmentsExperienceERC).toBe('experience-1');
			expect(elementVariation.active).toBe(true);
			expect(elementVariation.name).toBe('');
			expect(elementVariation.hide).toBe(false);
			expect(elementVariation.key).toBeTruthy();

			expect(createElementVariation('experience-1').key).not.toBe(
				elementVariation.key
			);
		});
	});

	describe('createInitialState', () => {
		it('starts on the default language with no draft and no variations when none are loaded', () => {
			expect(
				createInitialState({
					defaultLanguageId: 'en_US',
					elementVariations: [],
					experiences: [],
					selectedSegmentsExperienceId: 0,
				})
			).toEqual({
				defaultLanguageId: 'en_US',
				draftElementVariation: null,
				editableElementOptions: [],
				elementVariations: [],
				experienceKey: '',
				highlightedTargetElement: null,
				languageId: 'en_US',
			});
		});

		it('assigns a key and derives the hide flag from the hide value', () => {
			const {draftElementVariation, elementVariations} =
				createInitialState({
					defaultLanguageId: 'en_US',
					elementVariations: [
						{
							active: true,
							audienceEntryERCs: [],
							externalReferenceCode: 'erc-1',
							hide: 'true',
							html: {},
							js: {},
							name: 'Variation 1',
							segmentsExperienceERC: 'experience-1',
							targetElement: '#main',
						},
					],
					experiences: [],
					selectedSegmentsExperienceId: 0,
				});

			expect(draftElementVariation).toBeNull();
			expect(elementVariations).toHaveLength(1);
			expect(elementVariations[0].name).toBe('Variation 1');
			expect(elementVariations[0].key).toBeTruthy();
			expect(elementVariations[0].hide).toBe(true);
		});
	});

	describe('reducer', () => {
		it('sets the draft on CREATE_ELEMENT_VARIATION_DRAFT', () => {
			const draftElementVariation = buildElementVariation();

			const state = reducer(buildState(), {
				draftElementVariation,
				type: 'CREATE_ELEMENT_VARIATION_DRAFT',
			});

			expect(state.draftElementVariation).toBe(draftElementVariation);
		});

		it('merges properties into the draft on UPDATE_ELEMENT_VARIATION_DRAFT', () => {
			const state = reducer(
				buildState({draftElementVariation: buildElementVariation()}),
				{
					properties: {hide: true, name: 'Renamed'},
					type: 'UPDATE_ELEMENT_VARIATION_DRAFT',
				}
			);

			expect(state.draftElementVariation?.name).toBe('Renamed');
			expect(state.draftElementVariation?.hide).toBe(true);
		});

		it('sets the language on SET_LANGUAGE_ID', () => {
			const state = reducer(buildState(), {
				languageId: 'es_ES',
				type: 'SET_LANGUAGE_ID',
			});

			expect(state.languageId).toBe('es_ES');
		});

		it('sets the editable element options on SET_EDITABLE_ELEMENT_OPTIONS', () => {
			const editableElementOptions = [
				{label: 'Heading (element-text)', value: '.selector'},
			];

			const state = reducer(buildState(), {
				editableElementOptions,
				type: 'SET_EDITABLE_ELEMENT_OPTIONS',
			});

			expect(state.editableElementOptions).toBe(editableElementOptions);
		});

		it('sets the experience key on SET_EXPERIENCE_KEY', () => {
			const state = reducer(buildState(), {
				experienceKey: 'experience-2',
				type: 'SET_EXPERIENCE_KEY',
			});

			expect(state.experienceKey).toBe('experience-2');
		});

		it('sets the highlighted target element on SET_HIGHLIGHTED_TARGET_ELEMENT', () => {
			const state = reducer(buildState(), {
				highlightedTargetElement: '.selector',
				type: 'SET_HIGHLIGHTED_TARGET_ELEMENT',
			});

			expect(state.highlightedTargetElement).toBe('.selector');
		});

		it('appends a new draft and clears it on SAVE_ELEMENT_VARIATION_DRAFT', () => {
			const draftElementVariation = buildElementVariation({key: 'new'});

			const state = reducer(buildState({draftElementVariation}), {
				type: 'SAVE_ELEMENT_VARIATION_DRAFT',
			});

			expect(state.draftElementVariation).toBeNull();
			expect(state.elementVariations).toEqual([draftElementVariation]);
		});

		it('updates an existing variation in place on SAVE_ELEMENT_VARIATION_DRAFT', () => {
			const existingElementVariation = buildElementVariation({
				key: 'key-1',
				name: 'Old',
			});

			const state = reducer(
				buildState({
					draftElementVariation: {
						...existingElementVariation,
						name: 'New',
					},
					elementVariations: [existingElementVariation],
				}),
				{type: 'SAVE_ELEMENT_VARIATION_DRAFT'}
			);

			expect(state.elementVariations).toHaveLength(1);
			expect(state.elementVariations[0].name).toBe('New');
		});

		it('resets the language to the default on SAVE_ELEMENT_VARIATION_DRAFT', () => {
			const state = reducer(
				buildState({
					draftElementVariation: buildElementVariation({key: 'new'}),
					languageId: 'es_ES',
				}),
				{type: 'SAVE_ELEMENT_VARIATION_DRAFT'}
			);

			expect(state.languageId).toBe('en_US');
		});

		it('clears the highlighted target element on SAVE_ELEMENT_VARIATION_DRAFT', () => {
			const state = reducer(
				buildState({
					draftElementVariation: buildElementVariation({key: 'new'}),
					highlightedTargetElement: '.selector',
				}),
				{type: 'SAVE_ELEMENT_VARIATION_DRAFT'}
			);

			expect(state.highlightedTargetElement).toBeNull();
		});

		it('loads a variation into the draft on EDIT_ELEMENT_VARIATION', () => {
			const elementVariation = buildElementVariation({key: 'key-1'});

			const state = reducer(
				buildState({elementVariations: [elementVariation]}),
				{key: 'key-1', type: 'EDIT_ELEMENT_VARIATION'}
			);

			expect(state.draftElementVariation).toEqual(elementVariation);
		});

		it('removes a variation on DELETE_ELEMENT_VARIATION', () => {
			const elementVariation = buildElementVariation({key: 'key-1'});

			const state = reducer(
				buildState({elementVariations: [elementVariation]}),
				{key: 'key-1', type: 'DELETE_ELEMENT_VARIATION'}
			);

			expect(state.elementVariations).toEqual([]);
		});

		it('updates the active flag on UPDATE_ELEMENT_VARIATION', () => {
			const elementVariation = buildElementVariation({
				active: true,
				key: 'key-1',
			});

			const state = reducer(
				buildState({elementVariations: [elementVariation]}),
				{
					active: false,
					key: 'key-1',
					type: 'UPDATE_ELEMENT_VARIATION',
				}
			);

			expect(state.elementVariations[0].active).toBe(false);
		});

		it('clears the draft and resets the language on CANCEL_ELEMENT_VARIATION_DRAFT', () => {
			const state = reducer(
				buildState({
					draftElementVariation: buildElementVariation(),
					languageId: 'es_ES',
				}),
				{type: 'CANCEL_ELEMENT_VARIATION_DRAFT'}
			);

			expect(state.draftElementVariation).toBeNull();
			expect(state.languageId).toBe('en_US');
		});

		it('clears the highlighted target element on CANCEL_ELEMENT_VARIATION_DRAFT', () => {
			const state = reducer(
				buildState({
					draftElementVariation: buildElementVariation(),
					highlightedTargetElement: '.selector',
				}),
				{type: 'CANCEL_ELEMENT_VARIATION_DRAFT'}
			);

			expect(state.highlightedTargetElement).toBeNull();
		});
	});
});
