/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, queryByText, render} from '@testing-library/react';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import {ContainerWithControls} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {
	ControlsProvider,
	useSelectItem,
} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import getLayoutDataItemClassName from '../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/getLayoutDataItemClassName';
import getLayoutDataItemTopperUniqueClassName from '../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/getLayoutDataItemTopperUniqueClassName';
import getLayoutDataItemUniqueClassName from '../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/getLayoutDataItemUniqueClassName';

const CONTAINER_ID = 'CONTAINER_ID';

const renderContainer = ({
	activeItemId = 'container',
	containerConfig = {styles: {}},
	hasUpdatePermissions = true,
	lockedExperience = false,
} = {}) => {
	const container = {
		children: [],
		config: containerConfig,
		itemId: CONTAINER_ID,
		parentId: null,
		type: LAYOUT_DATA_ITEM_TYPES.container,
	};

	const layoutData = {
		items: {[container.itemId]: container},
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
						fragmentEntryLinks: {},
						layoutData,
						permissions: {
							LOCKED_SEGMENTS_EXPERIMENT: lockedExperience,
							UPDATE: hasUpdatePermissions,
						},
						selectedViewportSize: VIEWPORT_SIZES.desktop,
					})}
				>
					<AutoSelect />

					<ContainerWithControls
						item={container}
						layoutData={layoutData}
					/>
				</StoreAPIContextProvider>
			</ControlsProvider>
		</DndProvider>
	);
};

describe('ContainerWithControls', () => {
	afterEach(cleanup);

	it('does not add container class if user has no permissions', () => {
		const {baseElement} = renderContainer({hasUpdatePermissions: false});

		expect(baseElement.querySelector('.page-editor__container')).toBe(null);
	});

	it('does not allow deleting or duplicating the container if user has no permissions', () => {
		const {baseElement} = renderContainer({hasUpdatePermissions: false});

		expect(queryByText(baseElement, 'delete')).not.toBeInTheDocument();
		expect(queryByText(baseElement, 'duplicate')).not.toBeInTheDocument();
	});

	it('set classes for referencing the item', () => {
		const {baseElement} = renderContainer();

		const classes = [
			getLayoutDataItemClassName(LAYOUT_DATA_ITEM_TYPES.container),
			getLayoutDataItemTopperUniqueClassName(CONTAINER_ID),
			getLayoutDataItemUniqueClassName(CONTAINER_ID),
		];

		classes.forEach((className) => {
			const item = baseElement.querySelector(`.${className}`);

			expect(item).toBeVisible();
		});
	});
});
