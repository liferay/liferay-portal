/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import {FormStep} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items/FormStep';
import {FormStepContainer} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items/FormStepContainer';
import Row from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items/Row';
import Topper from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/topper/Topper';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {
	ControlsProvider,
	useSelectItem,
} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import {DragAndDropContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/drag_and_drop/useDragAndDrop';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext',
	() => {
		const selectItem = jest.fn();

		return {
			...jest.requireActual(
				'../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext'
			),
			useSelectItem: () => selectItem,
		};
	}
);

const LAYOUT_DATA = {
	items: {
		itemId: {
			children: [],
			config: {styles: {}},
			itemId: 'itemId',
			parentId: null,
			type: LAYOUT_DATA_ITEM_TYPES.row,
		},
	},
};

const renderTopper = ({
	Component = Row,
	activeItemIds = [],
	fragmentEntryLinks = {},
	hasUpdatePermissions = true,
	isActive = true,
	itemId = 'itemId',
	layoutData = LAYOUT_DATA,
	lockedExperience = false,
} = {}) => {
	const item = layoutData.items[itemId];

	return render(
		<DndProvider backend={HTML5Backend}>
			<ControlsProvider activeInitialState={{activeItemIds}}>
				<StoreAPIContextProvider
					getState={() => ({
						fragmentEntryLinks,
						layoutData,
						permissions: {
							LOCKED_SEGMENTS_EXPERIMENT: lockedExperience,
							UPDATE: hasUpdatePermissions,
						},
						selectedViewportSize: VIEWPORT_SIZES.desktop,
					})}
				>
					<DragAndDropContextProvider>
						<Topper
							isActive={isActive}
							item={item}
							layoutData={layoutData}
						>
							<Component item={item} layoutData={layoutData} />
						</Topper>
					</DragAndDropContextProvider>
				</StoreAPIContextProvider>
			</ControlsProvider>
		</DndProvider>
	);
};

describe('Topper', () => {
	it('does not render Topper if user has no permissions', () => {
		const {baseElement} = renderTopper({hasUpdatePermissions: false});

		expect(baseElement.querySelector('.page-editor__topper')).toBe(null);
	});

	it('renders Topper if user has permissions', () => {
		const {baseElement} = renderTopper();

		expect(
			baseElement.querySelector('.page-editor__topper')
		).toBeInTheDocument();
	});

	it('renders name of the fragment', () => {
		const {baseElement} = renderTopper();

		expect(
			baseElement.querySelector('[data-name="grid"]')
		).toBeInTheDocument();
	});

	it('renders custom name of the fragment', () => {
		const layoutData = {
			items: {
				itemId: {
					children: [],
					config: {name: 'customName'},
					itemId: 'itemId',
					parentId: null,
					type: LAYOUT_DATA_ITEM_TYPES.row,
				},
			},
		};

		const {baseElement} = renderTopper({layoutData});

		expect(
			baseElement.querySelector('[data-name="customName"]')
		).toBeInTheDocument();
	});

	it('disables options when multiple items are selected', () => {
		const layoutData = {
			items: {
				'item-1': {
					children: [],
					config: {name: 'Item 1'},
					itemId: 'item-1',
					parentId: null,
					type: LAYOUT_DATA_ITEM_TYPES.fragment,
				},
				'item-2': {
					children: [],
					config: {name: 'Item 2'},
					itemId: 'item-2',
					parentId: null,
					type: LAYOUT_DATA_ITEM_TYPES.fragment,
				},
				'item-3': {
					children: [],
					config: {styles: {}},
					itemId: 'item-3',
					parentId: null,
					type: LAYOUT_DATA_ITEM_TYPES.row,
				},
			},
		};

		renderTopper({
			activeItemIds: ['item-1', 'item-2'],
			isActive: true,
			itemId: 'item-3',
			layoutData,
		});

		expect(screen.getByLabelText('options')).toBeDisabled();
	});

	describe('Ensures that selectItem() is not called when the topper buttons are clicked', () => {
		const layoutData = {
			items: {
				fragment: {
					children: [],
					config: {
						fragmentEntryLinkId: 'fragment',
						name: 'customName',
					},
					itemId: 'fragment',
					parentId: null,
					type: LAYOUT_DATA_ITEM_TYPES.fragment,
				},
			},
		};

		const fragmentEntryLinks = {
			fragment: {editableValues: {}},
		};

		const params = {
			activeItemIds: ['item-1'],
			fragmentEntryLinks,
			isActive: true,
			itemId: 'fragment',
			layoutData,
		};

		it('clicks on options dropdown', async () => {
			renderTopper(params);

			const selectItem = useSelectItem();

			await userEvent.click(screen.getByLabelText('options'));

			expect(selectItem).not.toBeCalled();
		});

		it('clicks in an options action', async () => {
			renderTopper(params);

			const selectItem = useSelectItem();

			await userEvent.click(screen.getByText('duplicate'));

			expect(selectItem).not.toBeCalled();
		});

		it('clicks on comments button', async () => {
			renderTopper(params);

			const selectItem = useSelectItem();

			await userEvent.click(screen.getByLabelText('comments'));

			expect(selectItem).not.toBeCalled();
		});
	});

	describe('Form Step components', () => {
		it('renders step name correctly', () => {
			const layoutData = {
				items: {
					formStep1: {
						children: [],
						config: {},
						itemId: 'formStep1',
						parentId: 'formStepContainer',
						type: LAYOUT_DATA_ITEM_TYPES.formStep,
					},

					formStep2: {
						children: [],
						config: {},
						itemId: 'formStep2',
						parentId: 'formStepContainer',
						type: LAYOUT_DATA_ITEM_TYPES.formStep,
					},

					formStepContainer: {
						children: ['formStep1', 'formStep2'],
						config: {},
						itemId: 'formStepContainer',
						parentId: null,
						type: LAYOUT_DATA_ITEM_TYPES.formStepContainer,
					},
				},
			};

			renderTopper({
				Component: FormStep,
				itemId: 'formStep2',
				layoutData,
			});

			expect(screen.getByText('step-2')).toBeInTheDocument();
		});

		it('does not render actions in the form step container', () => {
			const layoutData = {
				items: {
					formStepContainer: {
						children: [],
						config: {},
						itemId: 'formStepContainer',
						parentId: null,
						type: LAYOUT_DATA_ITEM_TYPES.formStepContainer,
					},
				},
			};

			renderTopper({
				Component: FormStepContainer,
				itemId: 'formStepContainer',
				layoutData,
			});

			expect(screen.queryByText('options')).not.toBeInTheDocument();
		});
	});
});
