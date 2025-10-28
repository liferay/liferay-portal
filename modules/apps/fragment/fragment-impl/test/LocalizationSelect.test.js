/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';

import '@testing-library/jest-dom';
import React from 'react';

import {LocalizationSelect} from '../src/main/resources/META-INF/resources/js/api/LocalizationSelect';

const locales = [
	{
		displayName: 'English (United States)',
		id: 'en_US',
		label: 'en-US',
		symbol: 'us-us',
	},
	{
		displayName: 'Español (España)',
		id: 'es_ES',
		label: 'es-ES',
		symbol: 'es-es',
	},
];

describe('LocalizationSelect', () => {
	it('renders with default props', () => {
		render(
			<LocalizationSelect
				defaultLanguageId="en_US"
				editMode={false}
				hideLanguageLabel={false}
				locales={locales}
			/>
		);

		expect(screen.getByText('en-US')).toBeInTheDocument();
	});

	it('changes selected locale when clicking an option', () => {
		render(
			<LocalizationSelect
				defaultLanguageId="en_US"
				editMode={false}
				hideLanguageLabel={false}
				locales={locales}
			/>
		);

		fireEvent.click(screen.getByRole('combobox'));
		fireEvent.click(screen.getByText('es-ES'));

		expect(window.Liferay.fire).toHaveBeenCalledWith(
			'localizationSelect:localeChanged',
			{languageId: 'es_ES'}
		);
	});

	it('hides language label when hideLanguageLabel is true', () => {
		render(
			<LocalizationSelect
				defaultLanguageId="en_US"
				editMode={false}
				hideLanguageLabel={true}
				locales={locales}
			/>
		);

		expect(screen.queryByText('en-US')).not.toBeInTheDocument();
	});

	it('disables picker when in edit mode', () => {
		render(
			<LocalizationSelect
				defaultLanguageId="en_US"
				editMode={true}
				hideLanguageLabel={false}
				locales={locales}
			/>
		);

		fireEvent.click(screen.getByRole('combobox'));

		expect(screen.queryByText('Español (España)')).not.toBeInTheDocument();
	});
});
