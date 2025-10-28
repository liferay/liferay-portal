/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {VIEWPORT_SIZES} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {ResizeContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ResizeContext';
import {StoreAPIContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import updateItemConfig from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig';
import updateRowColumns from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateRowColumns';
import {RowGeneralPanel} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/item_configuration_panels/RowGeneralPanel';

const ITEM_CONFIG = {
	gutters: true,
	modulesPerRow: 2,
	numberOfColumns: 2,
	verticalAlignment: 'top',
};

const RESIZE_CONTEXT_STATE = {
	customRow: false,
	resizing: false,
	setResizing: () => null,
	setUpdatedLayoutData: () => null,
	updatedLayoutData: null,
};

const STATE = {
	layoutData: {
		items: {
			'item-1': {
				config: {
					size: 6,
				},
			},
			'item-2': {
				config: {
					size: 6,
				},
			},
		},
	},
	permissions: {UPDATE: true},
	segmentsExperienceId: '0',
	selectedViewportSize: 'desktop',
};

const renderComponent = ({
	config = ITEM_CONFIG,
	state = STATE,
	contextState = RESIZE_CONTEXT_STATE,
	dispatch = () => {},
} = {}) =>
	render(
		<StoreAPIContextProvider
			dispatch={dispatch}
			getState={() => ({...STATE, ...state})}
		>
			<ResizeContextProvider
				value={{...RESIZE_CONTEXT_STATE, ...contextState}}
			>
				<RowGeneralPanel
					item={{
						children: ['item-1', 'item-2'],
						config: {...ITEM_CONFIG, ...config},
						itemId: '0',
						parentId: '',
						type: '',
					}}
				/>
			</ResizeContextProvider>
		</StoreAPIContextProvider>
	);

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig',
	() => jest.fn()
);

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateRowColumns',
	() => jest.fn()
);

describe('RowGeneralPanel', () => {
	afterEach(() => {
		updateItemConfig.mockClear();
		updateRowColumns.mockClear();
	});

	it('allows changing the number of modules of a grid', async () => {
		renderComponent();

		const input = screen.getByLabelText('number-of-modules');

		fireEvent.change(input, {
			target: {value: '6'},
		});

		expect(updateRowColumns).toHaveBeenCalledWith({
			itemId: '0',
			numberOfColumns: 6,
		});
	});

	it('allows changing the gutter', async () => {
		renderComponent();

		const input = screen.getByLabelText('show-gutter');

		fireEvent.click(input);

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: {gutters: false},
			itemIds: ['0'],
		});
	});

	it('allows changing the modules per row', async () => {
		renderComponent();

		const input = screen.getByLabelText('layout');

		fireEvent.change(input, {
			target: {value: '2'},
		});

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: {
				modulesPerRow: 2,
			},
			itemIds: ['0'],
		});
	});

	it('allows custom value in modules per row when row is customized', async () => {
		renderComponent();

		const input = screen.getByLabelText('layout');

		expect(input).toHaveValue('2');
	});

	it('change label to custom when the column configuration is customized', async () => {
		renderComponent({
			state: {
				layoutData: {
					items: {
						'item-1': {
							config: {
								size: 5,
							},
						},
						'item-2': {
							config: {
								size: 7,
							},
						},
					},
				},
			},
		});

		const input = screen.getByLabelText('layout');

		expect(input).toHaveValue('custom');
	});

	it('allows changing the vertical alignment', async () => {
		renderComponent();

		const input = screen.getByLabelText('vertical-alignment');

		fireEvent.change(input, {
			target: {value: 'middle'},
		});

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: {
				verticalAlignment: 'middle',
			},
			itemIds: ['0'],
		});
	});

	it('allows inverse order when number of modules is 2 and modules per row is 1', async () => {
		renderComponent({
			config: {
				modulesPerRow: 1,
			},
			state: {
				layoutData: {
					items: {
						'item-1': {
							config: {
								size: 12,
							},
						},
						'item-2': {
							config: {
								size: 12,
							},
						},
					},
				},
			},
		});

		const input = screen.getByLabelText('inverse-order');

		fireEvent.click(input);

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: {
				reverseOrder: true,
			},
			itemIds: ['0'],
		});
	});

	it('allows changing layout for a given viewport', async () => {
		renderComponent({
			state: {
				selectedViewportSize: 'tablet',
			},
		});
		const input = screen.getByLabelText('layout');

		fireEvent.change(input, {
			target: {value: '1'},
		});

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: {
				tablet: {modulesPerRow: 1},
			},
			itemIds: ['0'],
		});
	});

	test.each([
		[VIEWPORT_SIZES.desktop],
		[VIEWPORT_SIZES.tablet],
		[VIEWPORT_SIZES.landscapeMobile],
		[VIEWPORT_SIZES.portraitMobile],
	])(
		'checks that the Number of Modules field is not visible for the %p viewport',
		(viewport) => {
			renderComponent({
				state: {
					selectedViewportSize: viewport,
				},
			});

			const select = screen.queryByLabelText('number-of-modules');

			if (viewport === VIEWPORT_SIZES.desktop) {
				expect(select).toBeInTheDocument();
			}
			else {
				expect(select).not.toBeInTheDocument();
			}
		}
	);
});
