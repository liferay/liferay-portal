/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import Row from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items/Row';
import TopperEmpty from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/topper/TopperEmpty';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {ControlsProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';

const renderTopperEmpty = ({
	itemType = LAYOUT_DATA_ITEM_TYPES.row,
	hasUpdatePermissions = true,
	lockedExperience = false,
} = {}) => {
	const row = {
		children: [],
		config: {
			styles: {},
		},
		itemId: 'itemId',
		parentId: null,
		type: itemType,
	};

	const layoutData = {
		items: {row},
	};

	return render(
		<DndProvider backend={HTML5Backend}>
			<ControlsProvider activeInitialState={{activeItemIds: ['itemId']}}>
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
					<TopperEmpty item={row} layoutData={layoutData}>
						<Row item={row} layoutData={layoutData}></Row>
					</TopperEmpty>
				</StoreAPIContextProvider>
			</ControlsProvider>
		</DndProvider>
	);
};

describe('TopperEmpty', () => {
	afterEach(cleanup);

	it('does not render TopperEmpty if user has no permissions', () => {
		const {baseElement} = renderTopperEmpty({hasUpdatePermissions: false});

		expect(baseElement.querySelector('.page-editor__topper')).toBe(null);
	});

	it('renders empty TopperEmpty if user has permissions', () => {
		const {baseElement} = renderTopperEmpty({});

		expect(
			baseElement.querySelector('.page-editor__topper')
		).toBeInTheDocument();
	});

	it('renders topper label with topper empty', () => {
		renderTopperEmpty({
			itemType: LAYOUT_DATA_ITEM_TYPES.column,
		});

		expect(screen.getByText('module')).toBeInTheDocument();
	});

	it('renders paste options', async () => {
		renderTopperEmpty({
			itemType: LAYOUT_DATA_ITEM_TYPES.column,
		});

		await userEvent.click(screen.getByLabelText('options'));

		expect(screen.getByText('paste')).toBeInTheDocument();
	});
});
