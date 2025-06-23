/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '@liferay/frontend-js-state-web';

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/editableFragmentEntryProcessor';
import {LAYOUT_DATA_ITEM_TYPE_LABELS} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypeLabels';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {ControlsProvider} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import {KeyboardMovementContextProvider} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/KeyboardMovementContext';
import {ShortcutContextProvider} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ShortcutContext';
import {StoreAPIContextProvider} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import updateItemConfig from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig';
import {pageContentsAtom} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/usePageContents';
import PageStructureSidebar from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/PageStructureSidebar';
jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig',
	() => jest.fn()
);

jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/FormService',
	() => ({
		getFormFields: jest.fn(() => Promise.resolve({})),
	})
);

jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config',
	() => ({
		config: {
			formTypes: [
				{
					isRestricted: false,
					label: 'Form Type 1',
					value: '11111',
				},
				{
					isRestricted: true,
					label: 'Form Type 2',
					value: '22222',
				},
			],
		},
	})
);

const renderComponent = ({
	activeItemIds = [],
	formConfig,
	hasUpdatePermissions = true,
	lockedExperience = false,
	masterRootItemChildren = ['11-container'],
	restrictedItemIds = new Set(),
	rootItemChildren = ['01-container'],
	viewportSize = VIEWPORT_SIZES.desktop,
} = {}) => {
	const mockDispatch = jest.fn((a) => {
		if (typeof a === 'function') {
			return a(mockDispatch);
		}
	});

	return render(
		<DndProvider backend={HTML5Backend}>
			<ControlsProvider
				activeInitialState={{
					activationOrigin: 'layout',
					activeItemIds,
					activeItemType: null,
				}}
				hoverInitialState={{
					hoveredItemId: null,
				}}
			>
				<StoreAPIContextProvider
					dispatch={mockDispatch}
					getState={() => ({
						fragmentEntryLinks: {
							'001': {
								content:
									'<div>001<span data-lfr-editable-id="05-editable">editable</span><span data-lfr-editable-id="06-editable">editable</span></div>',
								editableTypes: {
									'05-editable': 'text',
									'06-editable': 'text',
								},
								editableValues: {
									[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {
										'05-editable': {
											defaultValue: 'defaultValue',
										},
										'06-editable': {
											classNameId: 'itemClassNameId',
											classPK: 'itemClassPK',
											classTypeId: 'itemClassTypeId',
											defaultValue: 'defaultValue',
											fieldId: 'text-field-1',
										},
									},
								},
								fragmentEntryLinkId: '001',
								name: 'Fragment 1',
							},
						},

						layoutData: {
							items: {
								'00-main': {
									children: rootItemChildren,
									config: {},
									itemId: '00-main',
									parentId: null,
									type: LAYOUT_DATA_ITEM_TYPES.root,
								},
								'01-container': {
									children: ['02-row'],
									config: {},
									itemId: '01-container',
									parentId: '00-main',
									type: LAYOUT_DATA_ITEM_TYPES.container,
								},
								'02-row': {
									children: ['03-column'],
									config: {
										numberOfColumns: 1,
									},
									itemId: '02-row',
									parentId: '01-container',
									type: LAYOUT_DATA_ITEM_TYPES.row,
								},
								'03-column': {
									children: ['04-fragment'],
									config: {},
									itemId: '03-column',
									parentId: '02-row',
									type: LAYOUT_DATA_ITEM_TYPES.column,
								},
								'04-fragment': {
									children: [],
									config: {
										fragmentEntryLinkId: '001',
									},
									itemId: '04-fragment',
									parentId: '03-column',
									type: LAYOUT_DATA_ITEM_TYPES.fragment,
								},
								'05-row': {
									children: [],
									config: {
										fragmentEntryLinkId: '001',
									},
									itemId: '05-row',
									parentId: '03-column',
									type: LAYOUT_DATA_ITEM_TYPES.fragment,
								},
								'06-form': {
									children: ['07-row'],
									config: {
										classNameId: '11111',
										classTypeId: '0',
										...formConfig,
									},
									itemId: '06-form',
									parentId: '00-main',
									type: LAYOUT_DATA_ITEM_TYPES.form,
								},
								'07-row': {
									children: [],
									config: {
										fragmentEntryLinkId: '001',
									},
									itemId: '07-row',
									parentId: '06-form',
									type: LAYOUT_DATA_ITEM_TYPES.fragment,
								},
							},

							rootItems: {main: '00-main'},
							version: 1,
						},

						mappingFields: {
							'itemClassNameId-itemClassTypeId': [
								{
									fields: [
										{
											key: 'text-field-1',
											label: 'Text Field 1',
											type: 'text',
										},
									],
								},
							],
						},

						masterLayout: {
							masterLayoutData: {
								items: {
									'10-main': {
										children: masterRootItemChildren,
										config: {},
										itemId: '10-main',
										parentId: null,
										type: LAYOUT_DATA_ITEM_TYPES.root,
									},
									'11-container': {
										children: ['12-dropzone'],
										config: {},
										itemId: '11-container',
										parentId: '10-main',
										type: LAYOUT_DATA_ITEM_TYPES.container,
									},
									'12-dropzone': {
										children: [],
										config: {},
										itemId: '12-dropzone',
										parentId: '11-container',
										type: LAYOUT_DATA_ITEM_TYPES.dropZone,
									},
								},

								rootItems: {
									dropZone: '12-dropzone',
									main: '10-main',
								},
								version: 1,
							},
							masterLayoutPlid: '0',
						},

						pageContents: [
							{
								classNameId: 'itemClassNameId',
								classPK: 'itemClassPK',
								classTypeId: 'itemClassTypeId',
							},
						],

						permissions: {
							LOCKED_SEGMENTS_EXPERIMENT: lockedExperience,
							UPDATE: hasUpdatePermissions,
						},

						restrictedItemIds,

						selectedViewportSize: viewportSize,
					})}
				>
					<KeyboardMovementContextProvider>
						<ShortcutContextProvider>
							<PageStructureSidebar />
						</ShortcutContextProvider>
					</KeyboardMovementContextProvider>
				</StoreAPIContextProvider>
			</ControlsProvider>
		</DndProvider>
	);
};

describe('PageStructureSidebar', () => {
	beforeAll(() => {
		State.writeAtom(pageContentsAtom, {
			data: [],
			status: 'saved',
		});
		window.HTMLElement.prototype.scrollIntoView = jest.fn;
	});

	it('has a warning message when there is no content', () => {
		renderComponent({
			masterRootItemChildren: [],
			rootItemChildren: [],
		});

		expect(
			screen.getByText('there-is-no-content-on-this-page')
		).toBeInTheDocument();
	});

	it('uses fragments names as labels', () => {
		renderComponent({
			activeItemIds: ['04-fragment'],
		});

		expect(
			screen.getByText('Fragment 1', {selector: 'span'})
		).toBeInTheDocument();
	});

	it('uses default labels for containers, columns, rows', () => {
		renderComponent({
			activeItemIds: ['03-column'],
			rootItemChildren: ['01-container', '02-row', '03-column'],
		});
		['container', 'row', 'column'].forEach((itemLabel) =>
			screen
				.getAllByText(LAYOUT_DATA_ITEM_TYPE_LABELS[itemLabel])
				.forEach((element) => expect(element).toBeInTheDocument())
		);
	});

	it('sets activeItemIds as selected item', () => {
		const {baseElement} = renderComponent({
			activeItemIds: ['04-fragment'],
		});
		expect(
			baseElement.querySelector('[aria-controls="04-fragment"]')
		).toHaveAttribute('aria-expanded', 'true');
	});

	it('disables items that are in masterLayout', () => {
		renderComponent();
		const button = screen.getByLabelText('select-container');
		expect(button.parentElement).toHaveAttribute('aria-disabled', 'true');
	});

	it('scans fragments editables', () => {
		renderComponent({
			activeItemIds: ['04-fragment'],
			rootItemChildren: ['04-fragment'],
		});
		expect(
			screen.queryByLabelText('select-05-editable')
		).toBeInTheDocument();
		expect(screen.queryByLabelText('remove-05-editable')).toBe(null);
	});

	it('sets element as active item', async () => {
		renderComponent({
			activeItemIds: ['03-column'],
		});
		const button = screen.getByLabelText('select-grid');
		await userEvent.click(button);
		expect(button.parentElement).toHaveAttribute('aria-selected', 'true');
	});

	it('sets element as active item when it is a fragment', async () => {
		renderComponent({
			activeItemIds: ['03-column'],
		});
		const button = screen.getByLabelText('select-Fragment 1');
		await userEvent.click(button);
		expect(button.parentElement).toHaveAttribute('aria-selected', 'true');
	});

	it('sets element as active item when it is a column', async () => {
		renderComponent({
			activeItemIds: ['02-row'],
		});
		const button = screen.getByLabelText('select-module');
		await userEvent.click(button);
		expect(button.parentElement).toHaveAttribute('aria-selected', 'true');
	});

	it('does not allow removing items if user has no permissions', () => {
		renderComponent({
			hasUpdatePermissions: false,
			rootItemChildren: ['01-container', '02-row', '04-fragment'],
		});
		expect(screen.queryByLabelText('remove-container')).toBe(null);
		expect(screen.queryByLabelText('remove-grid')).toBe(null);
		expect(screen.queryByLabelText('remove-Fragment 1')).toBe(null);
	});

	it('does not allow removing items if viewport is not desktop', () => {
		renderComponent({
			activeItemIds: ['11-container'],
			rootItemChildren: ['01-container', '02-row', '04-fragment'],
			viewportSize: VIEWPORT_SIZES.portraitMobile,
		});
		expect(screen.queryByLabelText('remove-container')).toBe(null);
		expect(screen.queryByLabelText('remove-grid')).toBe(null);
		expect(screen.queryByLabelText('remove-Fragment 1')).toBe(null);
	});

	it('uses field label for mapped editables', () => {
		renderComponent({
			activeItemIds: ['04-fragment'],
			rootItemChildren: ['04-fragment'],
		});
		expect(
			screen.getByText('Fragment 1', {selector: 'span'})
		).toBeInTheDocument();
	});

	it('render custom fragment names as labels', () => {
		renderComponent({
			activeItemIds: ['04-fragment'],
			rootItemChildren: ['04-fragment'],
		});
		expect(
			screen.getByText('Fragment 1', {selector: 'span'})
		).toBeInTheDocument();
	});

	it('allow changing fragment name', async () => {
		const {baseElement} = renderComponent({
			activeItemIds: ['04-fragment'],
			rootItemChildren: ['04-fragment'],
		});
		await userEvent.dblClick(screen.getByLabelText('select-Fragment 1'));
		const input = baseElement.querySelector('input');
		expect(input).toBeInTheDocument();

		await userEvent.clear(input);
		await userEvent.type(input, 'Custom Fragment Name');
		fireEvent.blur(input);
		expect(screen.getByText('Custom Fragment Name')).toBeInTheDocument();
		expect(updateItemConfig).toBeCalledWith(
			expect.objectContaining({
				itemConfig: {name: 'Custom Fragment Name'},
			})
		);
		updateItemConfig.mockClear();
	});

	describe('Form container without permissions', () => {
		it('shows the form normally when it is mapped to an element with permissions', () => {
			renderComponent({
				activeItemIds: ['04-fragment'],
				rootItemChildren: ['06-form'],
			});

			expect(
				screen.getByText('form-container', {selector: 'span'})
			).toBeInTheDocument();

			expect(
				screen.queryByText(
					'this-content-cannot-be-displayed-due-to-permission-restrictions'
				)
			).not.toBeInTheDocument();
		});

		it('shows a permission restriction message when the form is mapped to an element without permissions and their children are not listed', () => {
			const {baseElement} = renderComponent({
				activeItemIds: ['06-form'],
				formConfig: {
					classNameId: '22222',
					classTypeId: '0',
				},
				rootItemChildren: ['06-form'],
			});
			expect(
				screen.getByText('form-container', {selector: 'span'})
			).toBeInTheDocument();
			expect(
				screen.getByText(
					'this-content-cannot-be-displayed-due-to-permission-restrictions'
				)
			).toBeInTheDocument();
			expect(
				baseElement.querySelector('[aria-controls="06-form"]')
			).not.toBeInTheDocument();
		});

		it('shows a permission restriction message when the fragment is restricted', () => {
			renderComponent({
				activeItemIds: ['04-fragment'],
				restrictedItemIds: new Set(['04-fragment']),
				rootItemChildren: ['04-fragment'],
			});
			expect(
				screen.getByText('Fragment 1', {selector: 'span'})
			).toBeInTheDocument();
			expect(
				screen.getByText(
					'this-content-cannot-be-displayed-due-to-permission-restrictions'
				)
			).toBeInTheDocument();
		});
	});
});
