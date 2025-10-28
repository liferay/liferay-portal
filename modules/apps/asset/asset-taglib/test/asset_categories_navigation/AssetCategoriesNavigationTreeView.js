/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import AssetCategoriesNavigationTreeView from '../../src/main/resources/META-INF/resources/js/asset_categories_navigation/AssetCategoriesNavigationTreeView';

const DEFAULT_PROPS = {
	selectedCategoryId: '',
	vocabularies: [
		{
			children: [
				{
					categoryId: 42348,
					children: [],
					icon: 'categories',
					id: '42348',
					name: 'category',
					url: 'http://localhost:8080/home/-/categories/42348?p_r_p_categoryId=42348',
					vocabularyId: '42347',
				},
			],
			disabled: true,
			icon: 'vocabulary',
			id: '42347',
			name: 'vocabulary',
		},
	],
};

describe('AssetCategoriesNavigationTreeView', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('tree is expanded when clicked on the vocabulary name', () => {
		const vocabularyName = DEFAULT_PROPS.vocabularies[0].name;
		const categoryName = DEFAULT_PROPS.vocabularies[0].children[0].name;

		render(<AssetCategoriesNavigationTreeView {...DEFAULT_PROPS} />);

		const vocabularyItem = screen.getByText(vocabularyName);

		expect(vocabularyItem).toBeInTheDocument();
		expect(screen.queryByText(categoryName)).not.toBeInTheDocument();

		fireEvent.click(vocabularyItem);

		expect(vocabularyItem).toBeInTheDocument();
		expect(screen.getByText(categoryName)).toBeInTheDocument();
	});
});
