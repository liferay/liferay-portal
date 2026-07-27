/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useState} from 'react';

import EditCategoryGeneralInfoTab from '../../../../../../src/main/resources/META-INF/resources/js/main_view/categorization/categories/components/EditCategoryGeneralInfoTab';

const DEFAULT_LANGUAGE_ID = 'en_US';

const LOCALES = [
	{
		id: 'en_US',
		label: 'en-US',
		name: 'English (United States)',
		symbol: 'en-us',
	},
];

function renderComponent(category: Partial<TaxonomyCategory> = {}) {
	const EditCategoryGeneralInfoTabWrapper = () => {
		const [currentCategory, setCurrentCategory] =
			useState<TaxonomyCategory>({
				name: '',
				name_i18n: {'en-US': ''},
				...category,
			});

		return (
			<EditCategoryGeneralInfoTab
				category={currentCategory}
				defaultLanguageId={DEFAULT_LANGUAGE_ID}
				locales={LOCALES}
				nameInputError=""
				setCategory={setCurrentCategory}
				setCategoryPermissions={jest.fn()}
				setNameInputError={jest.fn()}
				showPermissions={false}
				spritemap="/sprite.svg"
			/>
		);
	};

	return render(<EditCategoryGeneralInfoTabWrapper />);
}

describe('EditCategoryGeneralInfoTab', () => {
	it('shows the slug of the category being edited', () => {
		renderComponent({
			friendlyUrlPath: 'sports',
			friendlyUrlPath_i18n: {'en-US': 'sports'},
			name: 'Sports',
			name_i18n: {'en-US': 'Sports'},
		});

		expect(screen.getByRole('textbox', {name: /slug/})).toHaveValue(
			'sports'
		);
	});

	it('leaves the slug empty for a new category', () => {
		renderComponent();

		expect(screen.getByRole('textbox', {name: /slug/})).toHaveValue('');
	});

	it('updates the slug of the default language when it is edited', async () => {
		renderComponent({
			friendlyUrlPath: 'sports',
			friendlyUrlPath_i18n: {'en-US': 'sports'},
			name: 'Sports',
			name_i18n: {'en-US': 'Sports'},
		});

		const slugInput = screen.getByRole('textbox', {name: /slug/});

		await userEvent.clear(slugInput);
		await userEvent.type(slugInput, 'team-sports');

		expect(slugInput).toHaveValue('team-sports');
	});

	it('disables the slug of a system category', () => {
		renderComponent({
			friendlyUrlPath: 'sports',
			friendlyUrlPath_i18n: {'en-US': 'sports'},
			name: 'Sports',
			name_i18n: {'en-US': 'Sports'},
			system: true,
		});

		expect(screen.getByRole('textbox', {name: /slug/})).toBeDisabled();
	});

	it('has no accessibility violations', async () => {
		const {container} = renderComponent({
			friendlyUrlPath: 'sports',
			friendlyUrlPath_i18n: {'en-US': 'sports'},
			name: 'Sports',
			name_i18n: {'en-US': 'Sports'},
		});

		await checkAccessibility({context: container});
	});
});
