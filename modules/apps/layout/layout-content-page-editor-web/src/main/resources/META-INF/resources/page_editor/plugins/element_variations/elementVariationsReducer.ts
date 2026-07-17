/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {v4 as uuidv4} from 'uuid';

import {EditableElementOption} from './getEditableElementOptions';

export interface ElementVariation {
	active: boolean;
	audienceEntryERCs: string[];
	externalReferenceCode: string;
	hide: boolean;
	html: Record<string, string>;
	js: Record<string, string>;
	key: string;
	name: string;
	segmentsExperienceERC: string;
	targetElement: string;
}

export interface State {
	defaultLanguageId: string;
	draftElementVariation: ElementVariation | null;
	editableElementOptions: EditableElementOption[];
	elementVariations: ElementVariation[];
	experienceKey: string;
	highlightedTargetElement: string | null;
	languageId: string;
}

export type Action =
	| {
			draftElementVariation: ElementVariation;
			type: 'CREATE_ELEMENT_VARIATION_DRAFT';
	  }
	| {key: string; type: 'DELETE_ELEMENT_VARIATION'}
	| {key: string; type: 'EDIT_ELEMENT_VARIATION'}
	| {
			editableElementOptions: EditableElementOption[];
			type: 'SET_EDITABLE_ELEMENT_OPTIONS';
	  }
	| {experienceKey: string; type: 'SET_EXPERIENCE_KEY'}
	| {
			highlightedTargetElement: string | null;
			type: 'SET_HIGHLIGHTED_TARGET_ELEMENT';
	  }
	| {languageId: string; type: 'SET_LANGUAGE_ID'}
	| {active: boolean; key: string; type: 'UPDATE_ELEMENT_VARIATION'}
	| {
			properties: Partial<ElementVariation>;
			type: 'UPDATE_ELEMENT_VARIATION_DRAFT';
	  }
	| {type: 'CANCEL_ELEMENT_VARIATION_DRAFT' | 'SAVE_ELEMENT_VARIATION_DRAFT'};

export function createElementVariation(
	segmentsExperienceERC: string
): ElementVariation {
	return {
		active: true,
		audienceEntryERCs: [],
		externalReferenceCode: uuidv4(),
		hide: false,
		html: {},
		js: {},
		key: uuidv4(),
		name: '',
		segmentsExperienceERC,
		targetElement: '',
	};
}

export type LoadedElementVariation = Omit<ElementVariation, 'hide' | 'key'> & {
	hide: string;
};

export function createInitialState({
	defaultLanguageId,
	elementVariations,
	experiences,
	selectedSegmentsExperienceId,
}: {
	defaultLanguageId: string;
	elementVariations: LoadedElementVariation[];
	experiences: Array<{
		label: string;
		segmentsExperienceERC: string;
		segmentsExperienceId: number;
	}>;
	selectedSegmentsExperienceId: number;
}): State {
	const selectedExperience = experiences.find(
		(experience) =>
			experience.segmentsExperienceId === selectedSegmentsExperienceId
	);

	return {
		defaultLanguageId,
		draftElementVariation: null,
		editableElementOptions: [],
		elementVariations: elementVariations.map((elementVariation) => ({
			...elementVariation,
			hide: elementVariation.hide === 'true',
			key: uuidv4(),
		})),
		experienceKey:
			selectedExperience?.segmentsExperienceERC ??
			experiences[0]?.segmentsExperienceERC ??
			'',
		highlightedTargetElement: null,
		languageId: defaultLanguageId,
	};
}

export function reducer(state: State, action: Action): State {
	switch (action.type) {
		case 'CANCEL_ELEMENT_VARIATION_DRAFT':
			return {
				...state,
				draftElementVariation: null,
				highlightedTargetElement: null,
				languageId: state.defaultLanguageId,
			};

		case 'DELETE_ELEMENT_VARIATION':
			return {
				...state,
				elementVariations: state.elementVariations.filter(
					(elementVariation) => elementVariation.key !== action.key
				),
			};

		case 'EDIT_ELEMENT_VARIATION': {
			const elementVariation = state.elementVariations.find(
				(elementVariation) => elementVariation.key === action.key
			);

			if (!elementVariation) {
				return state;
			}

			return {...state, draftElementVariation: {...elementVariation}};
		}

		case 'SAVE_ELEMENT_VARIATION_DRAFT': {
			const {draftElementVariation, elementVariations} = state;

			if (!draftElementVariation) {
				return state;
			}

			const existing = elementVariations.some(
				(elementVariation) =>
					elementVariation.key === draftElementVariation.key
			);

			return {
				...state,
				draftElementVariation: null,
				elementVariations: existing
					? elementVariations.map((elementVariation) =>
							elementVariation.key === draftElementVariation.key
								? draftElementVariation
								: elementVariation
						)
					: [...elementVariations, draftElementVariation],
				highlightedTargetElement: null,
				languageId: state.defaultLanguageId,
			};
		}

		case 'CREATE_ELEMENT_VARIATION_DRAFT':
			return {
				...state,
				draftElementVariation: action.draftElementVariation,
			};

		case 'SET_EDITABLE_ELEMENT_OPTIONS':
			return {
				...state,
				editableElementOptions: action.editableElementOptions,
			};

		case 'SET_EXPERIENCE_KEY':
			return {...state, experienceKey: action.experienceKey};

		case 'SET_HIGHLIGHTED_TARGET_ELEMENT':
			return {
				...state,
				highlightedTargetElement: action.highlightedTargetElement,
			};

		case 'SET_LANGUAGE_ID':
			return {...state, languageId: action.languageId};

		case 'UPDATE_ELEMENT_VARIATION':
			return {
				...state,
				elementVariations: state.elementVariations.map(
					(elementVariation) =>
						elementVariation.key === action.key
							? {...elementVariation, active: action.active}
							: elementVariation
				),
			};

		case 'UPDATE_ELEMENT_VARIATION_DRAFT':
			return {
				...state,
				draftElementVariation: state.draftElementVariation
					? {...state.draftElementVariation, ...action.properties}
					: null,
			};

		default:
			return state;
	}
}
