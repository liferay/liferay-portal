/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import useTranslationProgress, {
	fieldToTranslations,
} from '../../../src/main/resources/META-INF/resources/js/translation_manager/useTranslationProgress';

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen, within} from '@testing-library/react';
import {act, renderHook} from '@testing-library/react-hooks';
import userEvent from '@testing-library/user-event';
import React from 'react';

import TranslationManager from '../../../src/main/resources/META-INF/resources/js/translation_manager/TranslationManager';
import {TranslationManagerProps} from '../../../src/main/resources/META-INF/resources/js/translation_manager/Types';

const FIELDS = {
	description: {
		ar_SA: 'test',
		ca_ES: 'test',
	},
	name: {
		ca_ES: 'test',
		en_US: 'test',
	},
} as any;

const DEFAULT_PROPS: TranslationManagerProps = {
	defaultLanguageId: 'en_US',
	fields: FIELDS,
	locales: [
		{
			displayName: 'English',
			id: 'en_US',
			label: 'en-US',
			symbol: 'en-us',
		},
		{
			displayName: 'Arabic',
			id: 'ar_SA',
			label: 'ar-SA',
			symbol: 'ar-sa',
		},
		{
			displayName: 'Catalan',
			id: 'ca_ES',
			label: 'ca-ES',
			symbol: 'ca-es',
		},
	],
	namespace: 'test',
	selectedLanguageId: 'en_US',
} as any;

const renderComponent = () => render(<TranslationManager {...DEFAULT_PROPS} />);

describe('TranslationManager', () => {
	it('renders component', () => {
		renderComponent();

		expect(screen.getByText('en-US')).toBeInTheDocument();
	});

	it('converts field to translation', () => {
		renderComponent();

		expect(fieldToTranslations(FIELDS)).toStrictEqual([
			{
				fieldName: 'description',
				languages: ['ar_SA', 'ca_ES'],
			},
			{
				fieldName: 'name',
				languages: ['ca_ES', 'en_US'],
			},
		]);
	});

	it('attaches inputLocalized:updateTranslationStatus event when the button is clicked', () => {
		renderComponent();

		userEvent.click(screen.getByRole('combobox'));

		expect(global.Liferay.on).toHaveBeenCalledWith(
			'inputLocalized:updateTranslationStatus',
			expect.any(Function)
		);
	});

	it('detaches inputLocalized:updateTranslationStatus event on unmount', () => {
		const {unmount} = renderComponent();

		unmount();

		expect(global.Liferay.detach).toHaveBeenCalledWith(
			'inputLocalized:updateTranslationStatus',
			expect.any(Function)
		);
	});

	it('fires `inputLocalized:localeChanged` event when language is changed', async () => {
		const renderComponent = () =>
			render(
				<>
					<div data-languageid="ca_ES" data-value="ca_ES" />

					<TranslationManager {...DEFAULT_PROPS} />
				</>
			);

		renderComponent();

		userEvent.click(screen.getByRole('combobox'));

		const listbox = await screen.findByRole('listbox');
		fireEvent.click(within(listbox).getByText('ca-ES'));

		const item = document.querySelector(
			'[data-languageid="ca_ES"][data-value="ca_ES"]'
		);

		expect(global.Liferay.fire).toHaveBeenCalledWith(
			'inputLocalized:localeChanged',
			{
				item,
			}
		);
	});

	it('does not fire `inputLocalized:localeChanged` event when language is not changed', async () => {
		renderComponent();

		(global.Liferay.fire as jest.Mock).mockClear();

		userEvent.click(screen.getByRole('combobox'));

		const listbox = await screen.findByRole('listbox');
		fireEvent.click(within(listbox).getByText('en-US'));

		expect(global.Liferay.fire).not.toHaveBeenCalledWith(
			'inputLocalized:localeChanged',
			expect.anything()
		);
	});

	it('ignores hidden inputs with data-translated=false and no value', () => {
		Object.keys(DEFAULT_PROPS.fields).forEach((fieldName) => {
			const ddmField = document.createElement('input');
			ddmField.type = 'text';
			ddmField.setAttribute('data-ddm-localizable-field-id', '');
			ddmField.setAttribute('data-field-name', fieldName);
			document.body.appendChild(ddmField);

			Object.keys(DEFAULT_PROPS.fields[fieldName]).forEach((langId) => {
				const input = document.createElement('input');
				input.type = 'hidden';
				input.setAttribute('data-field-name', fieldName);
				input.setAttribute('data-languageid', langId);
				if (fieldName === 'name' && langId === 'en_US') {
					input.setAttribute('data-translated', 'false');
					input.value = '';
				}
				else if (fieldName === 'description' && langId === 'ca_ES') {
					input.setAttribute('data-translated', 'false');
					input.value = '   ';
				}
				else {
					input.setAttribute('data-translated', 'true');
					input.value = 'test';
				}
				document.body.appendChild(input);
			});
		});

		const {result} = renderHook(() =>
			useTranslationProgress(DEFAULT_PROPS)
		);

		act(() => {
			result.current.updateTranslations();
		});

		expect(result.current.translations).toEqual([
			{fieldName: 'titleMapAsXML', languages: []},
			{fieldName: 'description', languages: ['ar_SA']},
			{fieldName: 'name', languages: ['ca_ES']},
		]);
	});
});
