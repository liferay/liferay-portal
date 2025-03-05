/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {useMarketplaceConfiguration} from '@liferay/marketplace-js-components-web';
import {act, fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import {StoreAPIContextProvider} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import {setIn} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/setIn';
import FragmentsSidebar, {
	normalizeWidget,
} from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/FragmentsSidebar';
import TabsPanel from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/TabsPanel';

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			portletNamespace: 'FragmentSidebarPortlet',
		},
	})
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/TabsPanel',
	() => {
		return jest.fn(() => null);
	}
);

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	sub: jest.fn((key, arg) => key.replace('x', arg)),
}));

jest.mock('@liferay/marketplace-js-components-web', () => {
	const mockGetProducts = {
		getProducts: jest.fn(),
	};
	const mockMarketplaceRest = jest.fn(() => mockGetProducts);
	mockMarketplaceRest.getBaseResourceURL = jest.fn(() => 'mocked-base-url');

	return {
		MarketplaceRest: mockMarketplaceRest,
		useMarketplaceConfiguration: jest.fn(),
	};
});

const DEFAULT_WIDGETS = [
	{
		categories: [],
		path: 'widget-collection-1',
		portlets: [
			{
				instanceable: true,
				portletId: 'portlet-1',
				portletItems: [
					{
						instanceable: true,
						portletId: 'template-portlet-1',
						portletItemId: '40063',
						preview: '',
						title: 'Template Portlet 1',
						used: false,
					},
				],
				title: 'Portlet 1',
				used: false,
			},
		],
		title: 'Widget Collection 1',
	},
];

const NORMALIZED_PORTLET_ITEMS = [
	{
		data: {
			portletId: 'template-portlet-1',
			portletItemId: '40063',
		},
		disabled: false,
		icon: 'square-hole-multi',
		itemId: 'template-portlet-1',
		label: 'Template Portlet 1',
		portletItems: null,
		preview: '',
		type: 'fragment',
	},
];

const NORMALIZED_TABS = [
	{
		collections: [
			{
				children: [
					{
						data: {
							fragmentEntryKey: 'fragment-1',
							groupId: '0',
							type: 1,
						},
						icon: 'fragment-1-icon',
						itemId: 'fragment-1',
						label: 'Fragment 1',
						preview: '/fragment-1-image.png',
						type: 'fragment',
					},
					{
						data: {
							fragmentEntryKey: 'fragment-2',
							groupId: '0',
							type: 1,
						},
						icon: 'fragment-2-icon',
						itemId: 'fragment-2',
						label: 'Fragment 2',
						preview: '/fragment-2-image.png',
						type: 'fragment',
					},
					{
						data: {
							fragmentEntryKey: 'fragment-3',
							groupId: '0',
							type: 1,
						},
						icon: 'fragment-3-icon',
						itemId: 'fragment-3',
						label: 'Fragment 3',
						preview: '/fragment-3-image',
						type: 'fragment',
					},
				],
				collectionId: 'collection-1',
				label: 'Collection 1',
			},
		],
		id: 0,
		label: 'fragments',
	},
	{
		collections: [
			{
				children: [
					{
						data: {
							portletId: 'portlet-1',
							portletItemId: null,
						},
						disabled: false,
						icon: 'square-hole-multi',
						itemId: 'portlet-1',
						label: 'Portlet 1',
						portletItems: NORMALIZED_PORTLET_ITEMS,
						preview: '',
						type: 'fragment',
					},
				],
				collectionId: 'widget-collection-1',
				label: 'Widget Collection 1',
			},
		],
		id: 1,
		label: 'widgets',
	},
];

const renderComponent = (widgets = DEFAULT_WIDGETS) => {
	return render(
		<DndProvider backend={HTML5Backend}>
			<StoreAPIContextProvider
				dispatch={() => Promise.resolve({})}
				getState={() => ({
					fragmentEntryLinks: [],
					fragments: [
						{
							fragmentCollectionId: 'collection-1',
							fragmentEntries: [
								{
									fragmentEntryKey: 'fragment-1',
									groupId: '0',
									icon: 'fragment-1-icon',
									imagePreviewURL: '/fragment-1-image.png',
									label: 'Fragment 1',
									name: 'Fragment 1',
									type: 1,
								},
								{
									fragmentEntryKey: 'fragment-2',
									groupId: '0',
									icon: 'fragment-2-icon',
									imagePreviewURL: '/fragment-2-image.png',
									label: 'Fragment 2',
									name: 'Fragment 2',
									type: 1,
								},
								{
									fragmentEntryKey: 'fragment-3',
									groupId: '0',
									icon: 'fragment-3-icon',
									imagePreviewURL: '/fragment-3-image',
									label: 'Fragment 3',
									name: 'Fragment 3',
									type: 1,
								},
							],
							name: 'Collection 1',
						},
					],
					widgets,
				})}
			>
				<FragmentsSidebar />
			</StoreAPIContextProvider>
		</DndProvider>
	);
};

describe('FragmentsSidebar', () => {
	afterEach(() => {
		jest.useRealTimers();
		jest.clearAllMocks();
	});

	beforeEach(() => {
		TabsPanel.mockClear();
		jest.useFakeTimers();
		useMarketplaceConfiguration.mockReturnValue({authorized: false});
	});

	it('has a sidebar panel title', () => {
		renderComponent();

		expect(screen.getByText('components')).toBeInTheDocument();
	});

	it('normalizes fragments and widgets format', () => {
		renderComponent();

		expect(TabsPanel).toHaveBeenCalledWith(
			expect.objectContaining({
				displayStyle: 'list',
				tabs: NORMALIZED_TABS,
			}),
			{}
		);
	});

	it('filters fragments and widgets according to a input value', async () => {
		renderComponent();
		const input = screen.getByLabelText('search-fragments-and-widgets');

		await act(async () => {
			await userEvent.type(input, 't 1', {
				advanceTimers: jest.advanceTimersByTime,
			});

			jest.runAllTimers();
		});

		expect(screen.queryByText('Portlet 1')).toBeInTheDocument();
		expect(screen.queryByText('Fragment 1')).toBeInTheDocument();
		expect(screen.queryByText('Fragment 2')).not.toBeInTheDocument();
		expect(screen.queryByText('Fragment 3')).not.toBeInTheDocument();
	});

	it('filters collections according to a input value', async () => {
		renderComponent();
		const input = screen.getByLabelText('search-fragments-and-widgets');

		await act(async () => {
			await userEvent.type(input, 'Widget Collection 1', {
				advanceTimers: jest.advanceTimersByTime,
			});

			jest.runAllTimers();
		});

		expect(screen.queryByText('Widget Collection 1')).toBeInTheDocument();
		expect(screen.queryByText('Portlet 1')).toBeInTheDocument();
		expect(screen.queryByText('Fragment 1')).not.toBeInTheDocument();
		expect(screen.queryByText('Fragment 2')).not.toBeInTheDocument();
		expect(screen.queryByText('Fragment 3')).not.toBeInTheDocument();
	});

	it('filters widget template according to a input value', async () => {
		renderComponent();
		const input = screen.getByLabelText('search-fragments-and-widgets');

		await act(async () => {
			await userEvent.type(input, 'Template Portlet 1', {
				advanceTimers: jest.advanceTimersByTime,
			});

			jest.runAllTimers();
		});

		expect(screen.queryByText('Widget Collection 1')).toBeInTheDocument();
		expect(screen.queryByText('Portlet 1')).toBeInTheDocument();
		expect(screen.queryByText('Template Portlet 1')).toBeInTheDocument();
		expect(screen.queryByText('Fragment 1')).not.toBeInTheDocument();
		expect(screen.queryByText('Fragment 2')).not.toBeInTheDocument();
		expect(screen.queryByText('Fragment 3')).not.toBeInTheDocument();
	});

	it('sets square-hole icon when the widget is not instanceable', () => {
		const widget = {
			instanceable: false,
			portletId: 'portlet-1',
			portletItems: [
				{
					instanceable: false,
					portletId: 'template-portlet-1',
					portletItemId: '40063',
					preview: '',
					title: 'Template Portlet 1',
					used: false,
				},
			],
			title: 'Portlet 1',
			used: false,
		};

		expect(normalizeWidget(widget)).toEqual(
			expect.objectContaining({
				icon: 'square-hole',
				portletItems: [expect.objectContaining({icon: 'square-hole'})],
			})
		);
	});

	it('disables a widget when it is not instanceable and it is used', () => {
		const widget = {
			instanceable: false,
			portletId: 'portlet-1',
			portletItems: [
				{
					instanceable: false,
					portletId: 'template-portlet-1',
					portletItemId: 'template-portlet-item-id-1',
					preview: '',
					title: 'Template Portlet 1',
					used: true,
				},
			],
			title: 'Portlet 1',
			used: true,
		};

		expect(normalizeWidget(widget)).toEqual(
			expect.objectContaining({
				disabled: true,
				portletItems: [expect.objectContaining({disabled: true})],
			})
		);
	});

	it('disables a widget when it is not instanceable and it is embedded', () => {
		const widget = {
			embedded: true,
			instanceable: false,
			portletId: 'portlet-1',
			portletItems: [
				{
					embedded: true,
					instanceable: false,
					portletId: 'template-portlet-1',
					portletItemId: 'template-portlet-item-id-1',
					preview: '',
					title: 'Template Portlet 1',
					used: false,
				},
			],
			title: 'Portlet 1',
			used: false,
		};

		expect(normalizeWidget(widget)).toEqual(
			expect.objectContaining({
				disabled: true,
				portletItems: [expect.objectContaining({disabled: true})],
			})
		);
	});

	it('normalizes collection with portlets items', () => {
		const widgets = [
			{
				categories: [],
				path: 'widget-collection-1',
				portlets: [
					{
						instanceable: true,
						portletId: 'portlet-1',
						portletItems: [
							{
								instanceable: true,
								portletId: 'portlet-item-1',
								title: 'Portlet Item 1',
								used: false,
							},
						],
						title: 'Portlet 1',
						used: false,
					},
				],
				title: 'Widget Collection 1',
			},
		];

		renderComponent(widgets);

		expect(TabsPanel).toHaveBeenCalledWith(
			expect.objectContaining({
				displayStyle: 'list',
				tabs: setIn(
					NORMALIZED_TABS,
					[1, 'collections', 0, 'children', 0, 'portletItems'],
					[
						{
							data: {
								portletId: 'portlet-item-1',
								portletItemId: null,
							},
							disabled: false,
							icon: 'square-hole-multi',
							itemId: 'portlet-item-1',
							label: 'Portlet Item 1',
							portletItems: null,
							preview: '',
							type: 'fragment',
						},
					]
				),
			}),
			{}
		);
	});

	it('normalizes collection with more collections inside', () => {
		const widgets = [
			{
				categories: [
					{
						categories: [
							{
								categories: [],
								path: 'collection-4',
								portlets: [
									{
										instanceable: true,
										portletId: 'collection-4-portlet',
										portletItems: [],
										title: 'Collection 4 Portlet',
										used: false,
									},
								],
								title: 'Widget Collection 3',
							},
						],
						path: 'widget-collection-2',
						portlets: [],
						title: 'Widget Collection 2',
					},
				],
				path: 'widget-collection-1',
				portlets: [
					{
						instanceable: true,
						portletId: 'portlet-1',
						portletItems: [
							{
								instanceable: true,
								portletId: 'template-portlet-1',
								portletItemId: '40063',
								preview: '',
								title: 'Template Portlet 1',
								used: false,
							},
						],
						title: 'Portlet 1',
						used: false,
					},
				],
				title: 'Widget Collection 1',
			},
		];

		renderComponent(widgets);

		expect(TabsPanel).toHaveBeenCalledWith(
			expect.objectContaining({
				displayStyle: 'list',
				tabs: setIn(
					NORMALIZED_TABS,
					[1, 'collections', 0, 'collections'],
					[
						{
							children: [],
							collectionId: 'widget-collection-2',
							collections: [
								{
									children: [
										{
											data: {
												portletId:
													'collection-4-portlet',
												portletItemId: null,
											},
											disabled: false,
											icon: 'square-hole-multi',
											itemId: 'collection-4-portlet',
											label: 'Collection 4 Portlet',
											portletItems: null,
											preview: '',
											type: 'fragment',
										},
									],
									collectionId: 'collection-4',
									label: 'Widget Collection 3',
								},
							],
							label: 'Widget Collection 2',
						},
					]
				),
			}),
			{}
		);
	});

	describe('Button to switch the display style', () => {
		const clickOnComponentsOptions = () => {
			const componentsOptions = screen.getByTitle('components-options');

			expect(componentsOptions).toBeInTheDocument();

			fireEvent.click(componentsOptions);
		};
		it('shows the card view when the display style is list', () => {
			renderComponent();

			clickOnComponentsOptions();

			expect(screen.getByText('switch-to-card-view')).toBeInTheDocument();
		});

		it('shows the list view when the display style is card', async () => {
			renderComponent();

			clickOnComponentsOptions();

			await userEvent.click(screen.getByText('switch-to-card-view'), {
				advanceTimers: jest.advanceTimersByTime,
			});

			clickOnComponentsOptions();

			expect(
				screen.getByText('switch-to-list[noun]-view')
			).toBeInTheDocument();
		});
	});
});
