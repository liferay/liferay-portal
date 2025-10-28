/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, queryByText, render} from '@testing-library/react';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import FragmentWithControls from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items/FragmentWithControls';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {
	ControlsProvider,
	useSelectItem,
} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import {EditableProcessorContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/EditableProcessorContext';
import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import getLayoutDataItemTopperUniqueClassName from '../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/getLayoutDataItemTopperUniqueClassName';
import getLayoutDataItemUniqueClassName from '../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/getLayoutDataItemUniqueClassName';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/services/serviceFetch',
	() => jest.fn(() => Promise.resolve({}))
);

const FRAGMENT_ID = 'FRAGMENT_ID';

const FRAGMENT_CLASS_NAME = 'FRAGMENT_CLASS_NAME';

const renderFragment = ({
	activeItemId = FRAGMENT_ID,
	editableValues = {},
	fragmentConfig = {styles: {}},
	hasUpdatePermissions = true,
	lockedExperience = false,
} = {}) => {
	const fragmentEntryLink = {
		cssClass: FRAGMENT_CLASS_NAME,
		editableValues,
		fragmentEntryLinkId: 'fragmentEntryLink',
	};

	const fragment = {
		children: [],
		config: {
			...fragmentConfig,
			fragmentEntryLinkId: fragmentEntryLink.fragmentEntryLinkId,
		},
		itemId: FRAGMENT_ID,
		parentId: null,
		type: LAYOUT_DATA_ITEM_TYPES.fragment,
	};

	const layoutData = {
		items: {[fragment.itemId]: fragment},
	};

	const AutoSelect = () => {
		useSelectItem()(activeItemId);

		return null;
	};

	return render(
		<DndProvider backend={HTML5Backend}>
			<ControlsProvider>
				<StoreAPIContextProvider
					getState={() => ({
						fragmentEntryLinks: {
							[fragmentEntryLink.fragmentEntryLinkId]:
								fragmentEntryLink,
						},
						layoutData,
						permissions: {
							LOCKED_SEGMENTS_EXPERIMENT: lockedExperience,
							UPDATE: hasUpdatePermissions,
						},
						selectedViewportSize: VIEWPORT_SIZES.desktop,
					})}
				>
					<EditableProcessorContextProvider>
						<AutoSelect />

						<FragmentWithControls
							item={fragment}
							layoutData={layoutData}
						/>
					</EditableProcessorContextProvider>
				</StoreAPIContextProvider>
			</ControlsProvider>
		</DndProvider>
	);
};

describe('FragmentWithControls', () => {
	beforeEach(() => {
		const wrapper = document.createElement('div');

		wrapper.id = 'wrapper';

		document.body.appendChild(wrapper);
	});

	it('does not allow deleting or duplicating the fragment if user has no permissions', async () => {
		await act(async () => {
			renderFragment({hasUpdatePermissions: false});
		});

		expect(queryByText(document.body, 'delete')).not.toBeInTheDocument();
		expect(queryByText(document.body, 'duplicate')).not.toBeInTheDocument();
	});

	it('set classes for referencing the item', async () => {
		await act(async () => {
			renderFragment();
		});

		const classes = [
			FRAGMENT_CLASS_NAME,
			getLayoutDataItemTopperUniqueClassName(FRAGMENT_ID),
			getLayoutDataItemUniqueClassName(FRAGMENT_ID),
		];

		classes.forEach((className) => {
			const item = document.querySelector(`.${className}`);

			expect(item).toBeVisible();
		});
	});

	it('does not set unique classNames when it has inner common styles', async () => {
		await act(async () => {
			renderFragment({
				editableValues: {
					['com.liferay.fragment.entry.processor.styles.StylesFragmentEntryProcessor']:
						{
							hasCommonStyles: true,
						},
				},
			});
		});

		const classes = [
			getLayoutDataItemTopperUniqueClassName(FRAGMENT_ID),
			getLayoutDataItemUniqueClassName(FRAGMENT_ID),
		];

		classes.forEach((className) => {
			const item = document.querySelector(`.${className}`);

			expect(item).toBeNull();
		});
	});
});
