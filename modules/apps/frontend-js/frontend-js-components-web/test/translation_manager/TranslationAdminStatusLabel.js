/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import TranslationAdminStatusLabel from '../../src/main/resources/META-INF/resources/translation_manager/TranslationAdminStatusLabel';

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	sub: jest.fn((key, ...args) =>
		args.flat().reduce((key, arg) => key.replace('x', arg), key)
	),
}));

const renderComponent = ({
	languageId = 'ar_AS',
	languageName = 'Arabic',
	localeValue = null,
	translationProgress = null,
} = {}) =>
	render(
		<TranslationAdminStatusLabel
			defaultLanguageId="en_US"
			languageId={languageId}
			languageName={languageName}
			localeValue={localeValue}
			translationProgress={translationProgress}
		/>
	);

describe('TranslationAdminStatusLabel', () => {
	it('renders default status', () => {
		renderComponent({languageId: 'en_US', languageName: 'English'});

		expect(
			screen.getByText('English-language-default')
		).toBeInTheDocument();
	});

	it('renders not translated status', () => {
		renderComponent();

		expect(
			screen.getByText('Arabic-language-not-translated')
		).toBeInTheDocument();
	});

	it('renders translated status', () => {
		renderComponent({localeValue: 'ar_AS'});

		expect(
			screen.getByText('Arabic-language-translated')
		).toBeInTheDocument();
	});

	it('renders translating status', () => {
		renderComponent({
			translationProgress: {
				totalItems: 3,
				translatedItems: {ar_AS: 1},
			},
		});

		expect(
			screen.getByText('Arabic-language-translating-1-3')
		).toBeInTheDocument();
	});
});
