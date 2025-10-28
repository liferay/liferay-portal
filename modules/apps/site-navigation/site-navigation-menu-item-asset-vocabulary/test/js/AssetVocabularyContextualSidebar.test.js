/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {AssetVocabularyContextualSidebar} from '../../src/main/resources/META-INF/resources/js/index';

const DEFAULT_LANGUAGE_ID = 'en_US';

const LOCALES = [
	{
		id: 'en_US',
		label: 'en-US',
		symbol: 'en-us',
	},
	{
		id: 'es_ES',
		label: 'es-ES',
		symbol: 'es-es',
	},
];

const renderContextualSidebar = ({
	assetVocabulary = {},
	localizedNames = {},
	numberOfCategories = 1,
	useCustomName = false,
} = {}) => {
	return render(
		<AssetVocabularyContextualSidebar
			assetVocabulary={assetVocabulary}
			chooseAssetVocabularyProps={{}}
			defaultLanguageId={DEFAULT_LANGUAGE_ID}
			locales={LOCALES}
			localizedNames={localizedNames}
			namespace="namespace"
			numberOfCategories={numberOfCategories}
			showAssetVocabularyLevel={false}
			siteName="my-site"
			useCustomName={useCustomName}
		/>
	);
};

describe('AssetVocabularyContextualSidebar', () => {
	it('renders name input disabled if custom name is disabled', () => {
		renderContextualSidebar({useCustomName: false});

		const nameInput = screen.getByLabelText('name');

		expect(nameInput).toHaveAttribute('disabled');
	});

	it('renders name input enabled if custom name is enabled', () => {
		renderContextualSidebar({useCustomName: true});

		const nameInput = screen.getByLabelText('name');

		expect(nameInput).not.toHaveAttribute('disabled');
	});

	it('renders name input with translated custom name for the default language when custom name is enabled', () => {
		const localizedNames = {
			[DEFAULT_LANGUAGE_ID]: 'localized-name',
		};

		renderContextualSidebar({localizedNames, useCustomName: true});

		const nameInput = screen.getByLabelText('name');

		expect(nameInput).toHaveValue('localized-name');
	});

	it('renders number of categories', () => {
		renderContextualSidebar();

		expect(screen.getByText('categories')).toBeInTheDocument();
	});

	it('renders site name', () => {
		renderContextualSidebar();

		expect(screen.getByText('my-site')).toBeInTheDocument();
	});

	it('renders feedback message when vocabulary does not have any category', () => {
		renderContextualSidebar({numberOfCategories: 0});

		expect(screen.getByRole('alert')).toBeInTheDocument();
	});
});
