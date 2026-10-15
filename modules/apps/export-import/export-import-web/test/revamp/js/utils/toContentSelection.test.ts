/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	CONTROL_NAME_PAGES,
	CONTROL_NAME_VISIBILITY,
	PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/contentSelection';
import {toContentSelection} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/toContentSelection';

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
						choice: 'mirror',
						choices: [
							{label: 'Mirror', name: 'mirror'},
							{label: 'Copy', name: 'copy'},
						],
						label: 'Referenced Content Behavior',
						name: 'referencedContentBehavior',
						type: 'Choice',
					},
					{
						label: 'Version History',
						name: 'versionHistory',
						selected: true,
						type: 'Boolean',
					},
					{
						label: 'Feeds',
						name: 'feeds',
						selected: false,
						type: 'Boolean',
					},
				],
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
						choice: 'public-pages',
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
								selected: true,
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

describe('toContentSelection', () => {
	it('selects the portlet data handlers present in the request', () => {
		expect(
			toContentSelection(PORTLET_DATA_HANDLER_SECTIONS, {
				name: 'Weekly',
				portletDataHandlers: [
					{
						name: 'PORTLET_DATA_com_liferay_journal',
						portletDataHandlerControls: [
							{
								choice: 'copy',
								name: 'referencedContentBehavior',
								type: 'Choice',
							},
							{
								name: 'versionHistory',
								selected: true,
								type: 'Boolean',
							},
							{name: 'feeds', selected: true, type: 'Boolean'},
						],
					},
				],
			})
		).toEqual({
			'category.content': {
				PORTLET_DATA_com_liferay_journal: {
					feeds: true,
					referencedContentBehavior: 'copy',
					versionHistory: true,
				},
			},
		});
	});

	it('leaves the controls left out of the request unselected', () => {
		expect(
			toContentSelection(PORTLET_DATA_HANDLER_SECTIONS, {
				name: 'Weekly',
				portletDataHandlers: [
					{name: 'PORTLET_DATA_com_liferay_journal'},
				],
			})
		).toEqual({
			'category.content': {
				PORTLET_DATA_com_liferay_journal: {
					referencedContentBehavior: 'mirror',
				},
			},
		});
	});

	it('skips the handlers sent as not selected', () => {
		expect(
			toContentSelection(PORTLET_DATA_HANDLER_SECTIONS, {
				name: 'Weekly',
				portletDataHandlers: [
					{
						name: 'PORTLET_DATA_com_liferay_journal',
						selected: false,
					},
				],
			})
		).toBeUndefined();
	});

	it('maps the pages control to a layout set selection', () => {
		expect(
			toContentSelection(PORTLET_DATA_HANDLER_SECTIONS, {
				name: 'Weekly',
				portletDataHandlers: [
					{
						name: PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
						portletDataHandlerControls: [
							{
								choice: 'private-pages',
								name: CONTROL_NAME_VISIBILITY,
								portletDataHandlerControls: [
									{
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
				],
			})
		).toEqual({
			'category.site_administration.build': {
				[PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS]: {privateLayout: true},
			},
		});

		expect(
			toContentSelection(PORTLET_DATA_HANDLER_SECTIONS, {
				name: 'Weekly',
				portletDataHandlers: [
					{name: PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS},
				],
			})
		).toEqual({
			'category.site_administration.build': {
				[PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS]: {privateLayout: false},
			},
		});
	});

	it('selects the comments and ratings present in the request', () => {
		expect(
			toContentSelection(
				PORTLET_DATA_HANDLER_SECTIONS,
				{
					comments: true,
					name: 'Weekly',
					portletDataHandlers: [
						{
							name: 'PORTLET_DATA_com_liferay_journal',
							portletDataHandlerControls: [
								{
									name: 'versionHistory',
									selected: false,
									type: 'Boolean',
								},
							],
						},
					],
					ratings: false,
				},
				{commentsAndRatingsEnabled: true}
			)
		).toEqual({
			'category.content': {
				PORTLET_DATA_com_liferay_journal: {
					referencedContentBehavior: 'mirror',
				},
				commentsAndRatings: {
					comments: true,
				},
			},
		});
	});

	it('returns undefined when nothing is selected', () => {
		expect(
			toContentSelection(PORTLET_DATA_HANDLER_SECTIONS, {
				deletions: false,
				name: 'Weekly',
			})
		).toBeUndefined();
	});
});
