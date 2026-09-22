/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {useResource} from '@clayui/data-provider';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import AssetVocabularyCategoriesSelector from '../../src/main/resources/META-INF/resources/js/asset_categories_selector/AssetVocabularyCategoriesSelector';

const DEFAULT_PROPS = {
	eventName: 'selectCategory',
	groupIds: [],
	inputName: '',
	portletURL: '',
};

jest.mock('@clayui/multi-select', () => ({
	__esModule: true,
	default: ({
		'aria-labelledby': ariaLabelledBy,
		id,
		items,
		onItemsChange,
		sourceItems,
	}) => (
		<div>
			<input
				aria-labelledby={ariaLabelledBy}
				id={id}
				readOnly
				role="combobox"
			/>

			<ul role="listbox">
				{(sourceItems ?? []).map((item) => (
					<li
						key={item.value}
						onClick={() =>
							onItemsChange([
								...items,
								{label: item.label, value: item.value},
							])
						}
						role="option"
					>
						{item.label}
					</li>
				))}
			</ul>
		</div>
	),
}));

jest.mock('@clayui/data-provider', () => {
	const originalModule = jest.requireActual('@clayui/data-provider');

	return {
		__esModule: true,
		...originalModule,
		useResource: jest.fn().mockImplementation(() => ({
			refetch: jest.fn(),
			resource: [],
		})),
	};
});

describe('AssetVocabularyCategoriesSelector', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('refetch is not called in the first component render', () => {
		render(<AssetVocabularyCategoriesSelector {...DEFAULT_PROPS} />);

		const {refetch} = useResource();

		expect(refetch).not.toHaveBeenCalled();
	});

	it('gives the combobox an accessible name via aria-labelledby', () => {
		const {container} = render(
			<AssetVocabularyCategoriesSelector
				{...DEFAULT_PROPS}
				inputName="assetCategoryIds_42"
				label="My Vocabulary"
			/>
		);

		const labelId = 'assetCategoryIds_42_MultiSelectLabel';

		expect(screen.getByRole('combobox')).toHaveAttribute(
			'aria-labelledby',
			labelId
		);

		const label = container.querySelector(`#${labelId}`);

		expect(label).toBeInTheDocument();
		expect(label.tagName).toBe('LABEL');
		expect(label).toHaveAttribute('for', 'assetCategoryIds_42_MultiSelect');
		expect(label).toHaveTextContent('My Vocabulary');
	});

	it('does not set aria-labelledby when there is no label', () => {
		render(<AssetVocabularyCategoriesSelector {...DEFAULT_PROPS} />);

		expect(screen.getByRole('combobox')).not.toHaveAttribute(
			'aria-labelledby'
		);
	});

	it('replaces the existing selection when singleSelect is true and a new valid category is added via autocomplete', async () => {
		const user = userEvent.setup();

		useResource.mockReturnValue({
			refetch: jest.fn(),
			resource: [{categoryId: '2', titleCurrentValue: 'Clothing'}],
		});

		const onSelectedItemsChange = jest.fn();

		render(
			<AssetVocabularyCategoriesSelector
				{...DEFAULT_PROPS}
				onSelectedItemsChange={onSelectedItemsChange}
				selectedItems={[{label: 'Electronics', value: '1'}]}
				singleSelect={true}
			/>
		);

		await user.click(screen.getByRole('option', {name: 'Clothing'}));

		expect(onSelectedItemsChange).toHaveBeenCalledWith([
			{label: 'Clothing', value: '2'},
		]);
	});

	it('keeps all selections when singleSelect is false and a new valid category is added via autocomplete', async () => {
		const user = userEvent.setup();

		useResource.mockReturnValue({
			refetch: jest.fn(),
			resource: [{categoryId: '2', titleCurrentValue: 'Clothing'}],
		});

		const onSelectedItemsChange = jest.fn();

		render(
			<AssetVocabularyCategoriesSelector
				{...DEFAULT_PROPS}
				onSelectedItemsChange={onSelectedItemsChange}
				selectedItems={[{label: 'Electronics', value: '1'}]}
				singleSelect={false}
			/>
		);

		await user.click(screen.getByRole('option', {name: 'Clothing'}));

		expect(onSelectedItemsChange).toHaveBeenCalledWith([
			{label: 'Electronics', value: '1'},
			{label: 'Clothing', value: '2'},
		]);
	});
});
