/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import TranslationFilter from '../../../src/main/resources/META-INF/resources/js/translation_manager/TranslationFilter';
import TranslationOptions from '../../../src/main/resources/META-INF/resources/js/translation_manager/TranslationOptions';

const DEFAULT_FIELDS = {
	descriptionMapAsXML: {},
	friendlyURL: {},
	titleMapAsXML: {},
};

const DEFAULT_PROPS = {
	defaultLanguageId: 'en_US',
	fields: DEFAULT_FIELDS,
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
	namespace: '_com_liferay_journal_web_portlet_JournalPortlet_',
	selectedLanguageId: 'en_US',
};

const renderDefaultComponent = () =>
	render(

		// @ts-ignore

		<TranslationOptions {...DEFAULT_PROPS} />
	);
describe('TranslationOptions', () => {
	it('translations options ellipsis not rendered when default language is selected', () => {
		renderDefaultComponent();

		const resetTranslationsButton = screen.queryByTitle(
			'translation-options'
		);

		expect(resetTranslationsButton).not.toBeInTheDocument();
	});

	describe('Reset Translations Button', () => {
		it('reset translations button is disabled when default language is selected', () => {
			render(

				// @ts-ignore

				<TranslationOptions
					{...DEFAULT_PROPS}
					selectedLanguageId="ca_ES"
				/>
			);

			const resetTranslationsButton =
				screen.getByText('reset-translation');

			expect(resetTranslationsButton.closest('button')).toBeDisabled();
		});
	});

	describe('Translation Filter Picker', () => {
		it('all fields option is selected by default', () => {
			render(

				// @ts-ignore

				<TranslationFilter
					{...DEFAULT_PROPS}
					selectedLanguageId="ca_ES"
				/>
			);

			const resetTranslationsButton = screen.getByText('all-fields');

			expect(resetTranslationsButton).toBeInTheDocument();
		});
	});
});
