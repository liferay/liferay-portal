/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ElementVariationForm from '../../../../src/main/resources/META-INF/resources/page_editor/plugins/element_variations/ElementVariationForm';

type ElementVariationProp = React.ComponentProps<
	typeof ElementVariationForm
>['elementVariation'];

const BASE_ELEMENT_VARIATION: ElementVariationProp = {
	active: true,
	audienceEntryERCs: [],
	hide: false,
	html: {},
	js: {},
	key: 'variation-1',
	name: 'My Variation',
	targetElement: '',
};

const EDITABLE_ELEMENT_OPTIONS = [
	{label: 'Title (title)', value: '.title'},
	{label: 'Body (body)', value: '.body'},
];

const LOCALES = [{id: 'en_US', label: 'English', symbol: 'en-us'}];

const TRANSLATING_PROPS = {
	languageId: 'es_ES',
	locales: [
		{id: 'en_US', label: 'English', symbol: 'en-us'},
		{id: 'es_ES', label: 'Spanish', symbol: 'es-es'},
	],
};

function renderForm(
	elementVariation: Partial<ElementVariationProp> = {},
	props: Partial<React.ComponentProps<typeof ElementVariationForm>> = {}
) {
	const onChange = jest.fn();

	return {
		onChange,
		...render(
			<ElementVariationForm
				audiences={[]}
				defaultLanguageId="en_US"
				dispatch={jest.fn()}
				editableElementOptions={EDITABLE_ELEMENT_OPTIONS}
				elementVariation={{
					...BASE_ELEMENT_VARIATION,
					...elementVariation,
				}}
				elementVariations={[]}
				languageId="en_US"
				locales={LOCALES}
				onCancel={jest.fn()}
				onChange={onChange}
				onLanguageIdChange={jest.fn()}
				onReloadPreview={jest.fn()}
				onSave={jest.fn()}
				{...props}
			/>
		),
	};
}

describe('ElementVariationForm', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('hides the audience selector, the toggle, and the html and js fields until a page element is selected', () => {
		renderForm({targetElement: ''});

		expect(screen.queryByLabelText('audience')).not.toBeInTheDocument();
		expect(
			screen.queryByLabelText('hide-page-element')
		).not.toBeInTheDocument();
		expect(screen.queryByLabelText('html')).not.toBeInTheDocument();
		expect(screen.queryByLabelText('javascript')).not.toBeInTheDocument();
	});

	it('shows the audience selector, the toggle, and the html and js fields once a page element is selected', () => {
		renderForm({targetElement: '.title'});

		expect(screen.getByLabelText('audience')).toBeInTheDocument();
		expect(screen.getByLabelText('hide-page-element')).toBeInTheDocument();
		expect(screen.getByLabelText('html')).toBeInTheDocument();
		expect(screen.getByLabelText('javascript')).toBeInTheDocument();
	});

	it('keeps the toggle but hides the html and js fields while the element is hidden', () => {
		renderForm({hide: true, targetElement: '.title'});

		expect(screen.getByLabelText('hide-page-element')).toBeChecked();
		expect(screen.queryByLabelText('html')).not.toBeInTheDocument();
		expect(screen.queryByLabelText('javascript')).not.toBeInTheDocument();
	});

	it('clears the html and js values when the element is hidden', async () => {
		const {onChange} = renderForm({
			html: {en_US: '<p>Hello</p>'},
			js: {en_US: 'console.log("hi");'},
			targetElement: '.title',
		});

		await userEvent.click(screen.getByLabelText('hide-page-element'));

		expect(onChange).toHaveBeenCalledWith({
			hide: true,
			html: {},
			js: {},
		});
	});

	it('preserves the html and js values when the element is shown again', async () => {
		const {onChange} = renderForm({
			hide: true,
			html: {en_US: '<p>Hello</p>'},
			js: {en_US: 'console.log("hi");'},
			targetElement: '.title',
		});

		await userEvent.click(screen.getByLabelText('hide-page-element'));

		expect(onChange).toHaveBeenCalledWith({hide: false});
	});

	it('disables the variation when the disable checkbox is checked', async () => {
		const {onChange} = renderForm({targetElement: '.title'});

		const disableCheckbox = screen.getByLabelText(
			'disable-element-variation'
		);

		expect(disableCheckbox).not.toBeChecked();

		await userEvent.click(disableCheckbox);

		expect(onChange).toHaveBeenCalledWith({active: false});
	});

	it('enables the variation when the disable checkbox is unchecked', async () => {
		const {onChange} = renderForm({active: false, targetElement: '.title'});

		const disableCheckbox = screen.getByLabelText(
			'disable-element-variation'
		);

		expect(disableCheckbox).toBeChecked();

		await userEvent.click(disableCheckbox);

		expect(onChange).toHaveBeenCalledWith({active: true});
	});

	it('marks the not-localizable fields as read-only when translating', () => {
		renderForm({targetElement: '.title'}, TRANSLATING_PROPS);

		expect(screen.getAllByText('(not-localizable)')).toHaveLength(5);
		expect(screen.getByDisplayValue('My Variation')).toHaveAttribute(
			'readonly'
		);
		expect(screen.getByLabelText('hide-page-element')).toBeDisabled();
	});

	it('does not mark fields as not-localizable on the default language', () => {
		renderForm({targetElement: '.title'});

		expect(screen.queryByText('(not-localizable)')).not.toBeInTheDocument();
	});

	it('shows the default language html and javascript values when translating', () => {
		renderForm(
			{
				html: {en_US: 'default html content'},
				js: {en_US: 'default js content'},
				targetElement: '.title',
			},
			TRANSLATING_PROPS
		);

		expect(screen.getByText('default html content')).toBeInTheDocument();
		expect(screen.getByText('default js content')).toBeInTheDocument();
	});

	it('shows a fallback message when translating a field with no default language value', () => {
		renderForm({targetElement: '.title'}, TRANSLATING_PROPS);

		expect(
			screen.getAllByText('there-is-no-default-value-to-localize')
		).toHaveLength(2);
	});

	it('shows a required error and blocks saving when no name is provided', async () => {
		const onSave = jest.fn();

		renderForm(
			{
				audienceEntryERCs: ['audience-1'],
				name: '',
				targetElement: '.title',
			},
			{onSave}
		);

		await userEvent.click(screen.getByText('save'));

		expect(screen.getByText('this-field-is-required')).toBeInTheDocument();
		expect(onSave).not.toHaveBeenCalled();
	});

	it('shows a required error and blocks saving when no page element is selected', async () => {
		const onSave = jest.fn();

		renderForm({targetElement: ''}, {onSave});

		await userEvent.click(screen.getByText('save'));

		expect(screen.getByText('this-field-is-required')).toBeInTheDocument();
		expect(onSave).not.toHaveBeenCalled();
	});

	it('shows a required error and blocks saving when no audience is selected', async () => {
		const onSave = jest.fn();

		renderForm({targetElement: '.title'}, {onSave});

		await userEvent.click(screen.getByText('save'));

		expect(screen.getByText('this-field-is-required')).toBeInTheDocument();
		expect(onSave).not.toHaveBeenCalled();
	});

	it('clears the required error when the offending field is updated', async () => {
		renderForm({
			audienceEntryERCs: ['audience-1'],
			name: '',
			targetElement: '.title',
		});

		await userEvent.click(screen.getByText('save'));

		expect(screen.getByText('this-field-is-required')).toBeInTheDocument();

		await userEvent.type(screen.getByLabelText('name'), 'New name');
		await userEvent.tab();

		expect(
			screen.queryByText('this-field-is-required')
		).not.toBeInTheDocument();
	});

	it('saves when an audience is selected', async () => {
		const onSave = jest.fn();

		renderForm(
			{audienceEntryERCs: ['audience-1'], targetElement: '.title'},
			{onSave}
		);

		await userEvent.click(screen.getByText('save'));

		expect(
			screen.queryByText('this-field-is-required')
		).not.toBeInTheDocument();
		expect(onSave).toHaveBeenCalledTimes(1);
	});

	it('has no accessibility violations', async () => {
		const {container} = renderForm({targetElement: '.title'});

		await checkAccessibility({context: container});
	});
});
