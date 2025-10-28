/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {HideFromSearchField} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/fragment_configuration_fields/HideFromSearchField';
import {VIEWPORT_SIZES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {useSelectItem} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import updateItemConfig from '../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig';

const DEFAULT_ITEM = {
	config: {},
	itemId: 'item-id',
	parentId: 'parent-id',
};

const DEFAULT_LAYOUT_DATA = {
	items: {},
};

const LAYOUT_DATA_WITH_HIDDEN_PARENT = {
	items: {
		'parent-id': {
			config: {
				indexed: false,
			},
			itemId: 'parent-id',
		},
	},
};

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig',
	() => jest.fn()
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext',
	() => {
		const selectItem = jest.fn();

		return {
			useSelectItem: () => selectItem,
		};
	}
);

const renderComponent = ({
	item = DEFAULT_ITEM,
	layoutData = DEFAULT_LAYOUT_DATA,
} = {}) =>
	render(
		<StoreAPIContextProvider
			dispatch={() => {}}
			getState={() => ({
				layoutData,
				selectedViewportSize: VIEWPORT_SIZES.desktop,
			})}
		>
			<HideFromSearchField item={item} layoutData={layoutData} />
		</StoreAPIContextProvider>
	);

describe('HideFromSearchField', () => {
	it('calls dispatch method with selected value for Hide From Search checkbox', async () => {
		renderComponent();

		const checkbox = screen.getByLabelText('hide-from-site-search-results');

		await act(async () => {
			fireEvent.click(checkbox);
		});

		expect(updateItemConfig).toBeCalledWith(
			expect.objectContaining({
				itemConfig: {
					indexed: false,
				},
			})
		);
	});

	it('renders checkbox disabled and checked when parent is hidden', async () => {
		renderComponent({layoutData: LAYOUT_DATA_WITH_HIDDEN_PARENT});

		const checkbox = screen.getByLabelText('hide-from-site-search-results');

		expect(checkbox).toBeDisabled();
		expect(checkbox).toBeChecked();
	});

	it('allows going to parent fragment when parent is hidden', async () => {
		renderComponent({layoutData: LAYOUT_DATA_WITH_HIDDEN_PARENT});

		const button = screen.getByText('go-to-parent-fragment-to-edit');
		const selectItem = useSelectItem();

		fireEvent.click(button);

		expect(selectItem).toBeCalledWith('parent-id');
	});
});
