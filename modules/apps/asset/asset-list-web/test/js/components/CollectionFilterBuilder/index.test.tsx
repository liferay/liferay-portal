/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '@liferay/frontend-js-state-web';
import {render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {invalidFiltersAtom} from '../../../../src/main/resources/META-INF/resources/js/atoms/invalidFiltersAtom';
import CollectionFilterBuilder from '../../../../src/main/resources/META-INF/resources/js/components/CollectionFilterBuilder/index';

const PROPERTIES = [
	{
		items: [
			{
				label: 'Price',
				name: 'price',
				objectDefinitionExternalReferenceCode: 'PRODUCT',
				type: 'integer' as const,
			},
		],
		label: 'Structure',
	},
];

function renderCollectionFilterBuilder(value: string[]) {
	const {container, unmount} = render(
		<form>
			<CollectionFilterBuilder
				initialConditions={[
					{
						objectDefinitionExternalReferenceCode: 'PRODUCT',
						operatorName: 'between',
						propertyName: 'price',
						value,
					},
				]}
				namespace="_test_"
				properties={PROPERTIES}
			/>
		</form>
	);

	return {form: container.querySelector('form')!, unmount};
}

function getSubmittedFilters(form: HTMLFormElement) {
	const input = form.elements.namedItem(
		'_test_TypeSettingsProperties--filters--'
	) as HTMLInputElement;

	return JSON.parse(input.value);
}

function submitForm(form: HTMLFormElement, action?: string) {
	const event = {
		action,
		form: {getDOM: () => form},
		preventDefault: jest.fn(),
	};

	(Liferay.on as jest.Mock).mock.calls
		.filter(([eventName]) => eventName === 'submitForm')
		.forEach(([, handler]) => handler(event));

	return event;
}

describe('CollectionFilterBuilder', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('prevents submitting the form with an invalid range', () => {
		const {form} = renderCollectionFilterBuilder(['10', '2']);

		expect(submitForm(form).preventDefault).toHaveBeenCalled();
	});

	it('allows submitting the form to another action with an invalid range', () => {
		const {form} = renderCollectionFilterBuilder(['10', '2']);

		expect(
			submitForm(form, '/add_asset_list_entry_variation').preventDefault
		).not.toHaveBeenCalled();
	});

	it('allows submitting the form with a valid range', () => {
		const {form} = renderCollectionFilterBuilder(['2', '10']);

		expect(submitForm(form).preventDefault).not.toHaveBeenCalled();
	});

	it('flags the filters as invalid while a range is invalid', () => {
		const {unmount} = renderCollectionFilterBuilder(['10', '2']);

		expect(State.read(invalidFiltersAtom)).toBe(true);

		unmount();

		expect(State.read(invalidFiltersAtom)).toBe(false);
	});

	it('does not flag the filters as invalid with a valid range', () => {
		renderCollectionFilterBuilder(['2', '10']);

		expect(State.read(invalidFiltersAtom)).toBe(false);
	});

	it('does not submit a condition with an invalid range', () => {
		const {form} = renderCollectionFilterBuilder(['10', '2']);

		expect(getSubmittedFilters(form)).toEqual([]);
	});

	it('submits a condition with a valid range', () => {
		const {form} = renderCollectionFilterBuilder(['2', '10']);

		expect(getSubmittedFilters(form)).toEqual([
			expect.objectContaining({
				propertyName: 'price',
				value: ['2', '10'],
			}),
		]);
	});
});
