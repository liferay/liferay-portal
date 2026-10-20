/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	CONTROL_NAME_PAGES,
	CONTROL_NAME_VISIBILITY,
	PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/contentSelection';
import {
	toPortletDataHandlerTreeSelection,
	toPortletDataHandlers,
} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/toPortletDataHandlers';

import type {PortletDataHandlerSection} from '../../../../src/main/resources/META-INF/resources/revamp/js/types/portletDataHandler';

const PORTLET_DATA_HANDLER_SECTIONS = [
	{
		key: 'category.content',
		label: 'Content',
		portletDataHandlers: [
			{
				label: 'Web Content',
				name: 'PORTLET_DATA_com_liferay_journal',
				portletDataHandlerControls: [
					{
						label: 'Web Content',
						name: 'web-content',
						portletDataHandlerControls: [
							{
								choices: [
									{label: 'Always', name: 'include-always'},
									{
										label: 'If Modified',
										name: 'include-if-modified',
									},
								],
								label: 'Behavior',
								name: 'behavior',
								type: 'Choice',
							},
							{
								label: 'Version History',
								name: 'version-history',
								type: 'Boolean',
							},
						],
						type: 'Boolean',
					},
					{label: 'Structures', name: 'structures', type: 'Boolean'},
				],
			},
			{
				label: 'Blogs',
				name: 'PORTLET_DATA_com_liferay_blogs',
			},
		],
	},
	{
		key: 'category.site_administration.build',
		label: 'Site Builder',
		portletDataHandlers: [
			{
				label: 'Pages',
				name: PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
				portletDataHandlerControls: [
					{
						choices: [
							{label: 'Public Pages', name: 'public-pages'},
							{label: 'Private Pages', name: 'private-pages'},
						],
						label: 'Visibility',
						name: CONTROL_NAME_VISIBILITY,
						portletDataHandlerControls: [
							{
								label: 'Pages',
								name: CONTROL_NAME_PAGES,
								type: 'TreeSelection',
							},
						],
						type: 'Choice',
					},
				],
			},
		],
	},
] as PortletDataHandlerSection[];

describe('toPortletDataHandlers', () => {
	it('sends every control of a selected handler with its state', () => {
		expect(
			toPortletDataHandlers(PORTLET_DATA_HANDLER_SECTIONS, {
				'category.content': {
					PORTLET_DATA_com_liferay_blogs: true,
					PORTLET_DATA_com_liferay_journal: {
						'web-content': {behavior: 'include-if-modified'},
					},
				},
			})
		).toEqual([
			{
				name: 'PORTLET_DATA_com_liferay_journal',
				portletDataHandlerControls: [
					{
						name: 'web-content',
						portletDataHandlerControls: [
							{
								choice: 'include-if-modified',
								name: 'behavior',
								type: 'Choice',
							},
							{
								name: 'version-history',
								selected: false,
								type: 'Boolean',
							},
						],
						selected: true,
						type: 'Boolean',
					},
					{name: 'structures', selected: false, type: 'Boolean'},
				],
			},
			{name: 'PORTLET_DATA_com_liferay_blogs'},
		]);
	});

	it('leaves out the handlers that are not selected', () => {
		expect(
			toPortletDataHandlers(PORTLET_DATA_HANDLER_SECTIONS, {
				'category.content': {PORTLET_DATA_com_liferay_blogs: true},
			})
		).toEqual([{name: 'PORTLET_DATA_com_liferay_blogs'}]);

		expect(
			toPortletDataHandlers(PORTLET_DATA_HANDLER_SECTIONS, {})
		).toEqual([]);
	});

	it('sends the pages as a selection of the pages control', () => {
		expect(
			toPortletDataHandlers(PORTLET_DATA_HANDLER_SECTIONS, {
				'category.site_administration.build': {
					[PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS]: {
						all: true,
						excludedSubtrees: ['archive'],
						items: ['news'],
						privateLayout: true,
					},
				},
			})
		).toEqual([
			{
				name: PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
				portletDataHandlerControls: [
					{
						choice: 'private-pages',
						name: CONTROL_NAME_VISIBILITY,
						portletDataHandlerControls: [
							{
								excludedSubtreeExternalReferenceCodes: [
									'archive',
								],
								itemExternalReferenceCodes: ['news'],
								name: CONTROL_NAME_PAGES,
								selectAll: true,
								selected: true,
								type: 'TreeSelection',
							},
						],
						type: 'Choice',
					},
				],
			},
		]);
	});

	it('sends every page without a selection when all pages are selected', () => {
		expect(
			toPortletDataHandlers(PORTLET_DATA_HANDLER_SECTIONS, {
				'category.site_administration.build': {
					[PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS]: {
						all: true,
						privateLayout: false,
					},
				},
			})
		).toEqual([
			{
				name: PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
				portletDataHandlerControls: [
					{
						choice: 'public-pages',
						name: CONTROL_NAME_VISIBILITY,
						portletDataHandlerControls: [
							{
								name: CONTROL_NAME_PAGES,
								selected: true,
								type: 'TreeSelection',
							},
						],
						type: 'Choice',
					},
				],
			},
		]);
	});
});

describe('toPortletDataHandlerTreeSelection', () => {
	it('maps the selected subtrees and items to lists', () => {
		expect(
			toPortletDataHandlerTreeSelection({
				excludedItems: ['home'],
				privateLayout: false,
				subtrees: ['news'],
			})
		).toEqual({
			excludedItemExternalReferenceCodes: ['home'],
			selectAll: false,
			subtreeExternalReferenceCodes: ['news'],
		});
	});

	it('returns no lists when every page is selected', () => {
		expect(
			toPortletDataHandlerTreeSelection({all: true, privateLayout: true})
		).toEqual({});
	});
});
