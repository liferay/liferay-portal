/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import useTranslationProgress, {
	fieldToTranslations,
} from '../../../src/main/resources/META-INF/resources/js/translation_manager/useTranslationProgress';

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
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
};

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
};

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

	it('attaches inputLocalized:localeChanged fire event on mount', () => {
		const renderComponent = () =>
			render(
				<>
					<div data-languageid="en_US" data-value="en_US" />

					<TranslationManager {...DEFAULT_PROPS} />
				</>
			);

		renderComponent();

		const item = document.createElement('div');
		item.dataset.value = 'en_US';
		item.dataset.languageid = 'en_US';

		expect(global.Liferay.fire).toHaveBeenCalledWith(
			'inputLocalized:localeChanged',
			{
				item,
			}
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
				else {
					input.setAttribute('data-translated', 'true');
					input.value = 'test';
				}
				document.body.appendChild(input);
			});
		});

		const expectedTranslations = [
			{fieldName: 'titleMapAsXML', languages: []},
			{fieldName: 'description', languages: ['ar_SA', 'ca_ES']},
			{fieldName: 'name', languages: ['ca_ES']},
		];

		const {result} = renderHook(() =>
			useTranslationProgress(DEFAULT_PROPS)
		);

		act(() => {
			result.current.updateTranslations();
		});

		expect(result.current.translations).toEqual(expectedTranslations);
	});
});
