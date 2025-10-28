/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import ChangeDefaultLanguage from '../../src/main/resources/META-INF/resources/js/ChangeDefaultLanguage.es';

const defaultStrings = {
	ca_ES: 'Catalan (ES)',
	en_US: 'English (US)',
	es_ES: 'Spanish (ES)',
};

const defaultLanguages = [
	{icon: 'en-US', label: 'en_US'},
	{icon: 'es-ES', label: 'es_ES'},
	{icon: 'ca-ES', label: 'ca_ES'},
];

function _renderChangeDefaultLanguageComponent({
	defaultLanguage = 'en_US',
	languages = defaultLanguages,
	strings = defaultStrings,
} = {}) {
	return render(
		<ChangeDefaultLanguage
			defaultLanguage={defaultLanguage}
			languages={languages}
			strings={strings}
		/>,
		{
			baseElement: document.body,
		}
	);
}

describe('ChangeDefaultLanguage', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('render', () => {
		Liferay.FeatureFlags['LPD-11228'] = true;

		const {getByText} = _renderChangeDefaultLanguageComponent();

		expect(
			getByText(
				"changing-the-default-language-will-reset-the-article's-history-making-previous-changes-untrackable"
			)
		).toBeInTheDocument();

		expect(getByText('change')).toBeTruthy();

		Liferay.FeatureFlags['LPD-11228'] = false;
	});

	it('render the default language', () => {
		const {getByText} = _renderChangeDefaultLanguageComponent({
			defaultLanguage: 'es_ES',
		});

		expect(getByText('Spanish (ES)')).toBeTruthy();
	});

	it('change default language', async () => {
		const {findByText, getByText, getByTitle} =
			_renderChangeDefaultLanguageComponent();

		fireEvent.click(getByTitle('es_ES'));

		await findByText('Spanish (ES)');

		expect(getByText('Spanish (ES)')).toBeTruthy();
	});

	it('to fire default locale changed event', () => {
		const {getByTitle} = _renderChangeDefaultLanguageComponent();

		const button = getByTitle('es_ES');

		fireEvent.click(button);

		expect(Liferay.fire).toHaveBeenCalled();

		expect(Liferay.fire.mock.calls[0][0]).toBe(
			'inputLocalized:defaultLocaleChanged'
		);

		expect(Liferay.fire.mock.calls[0][1].item.dataset.value).toBe('es_ES');
	});
});
