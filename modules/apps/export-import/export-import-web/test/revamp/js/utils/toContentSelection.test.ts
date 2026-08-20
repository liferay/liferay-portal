/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/contentSelection';
import {toContentSelection} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/toContentSelection';

import type {PreviewPortletDataHandlerSection} from '../../../../src/main/resources/META-INF/resources/revamp/js/types/portletDataHandler';

const PREVIEW_PORTLET_DATA_HANDLER_SECTIONS = [
	{
		label: 'Content',
		name: 'category.content',
		previewPortletDataHandlers: [
			{
				label: 'Web Content',
				name: 'PORTLET_DATA_com_liferay_journal',
				previewPortletDataHandlerControls: [
					{
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
						type: 'Boolean',
					},
				],
			},
		],
	},
	{
		label: 'Site Builder',
		name: 'category.site_administration.build',
		previewPortletDataHandlers: [
			{
				label: 'Pages',
				name: LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY,
			},
		],
	},
] as PreviewPortletDataHandlerSection[];

describe('toContentSelection', () => {
	it('selects the portlet data handlers present in the parameters', () => {
		expect(
			toContentSelection(PREVIEW_PORTLET_DATA_HANDLER_SECTIONS, {
				PORTLET_DATA_com_liferay_journal: ['true'],
				referencedContentBehavior: ['copy'],
				versionHistory: ['true'],
			})
		).toEqual({
			'category.content': {
				PORTLET_DATA_com_liferay_journal: {
					referencedContentBehavior: 'copy',
					versionHistory: true,
				},
			},
		});
	});

	it('maps the layout set parameters to a layout set selection', () => {
		expect(
			toContentSelection(PREVIEW_PORTLET_DATA_HANDLER_SECTIONS, {
				[LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY]: ['true'],
				privateLayoutPages: ['10', '11'],
			})
		).toEqual({
			'category.site_administration.build': {
				[LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY]: {
					layoutIds: [10, 11],
					privateLayout: true,
				},
			},
		});

		expect(
			toContentSelection(PREVIEW_PORTLET_DATA_HANDLER_SECTIONS, {
				[LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY]: ['true'],
				publicLayoutPages: ['true'],
			})
		).toEqual({
			'category.site_administration.build': {
				[LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY]: {
					privateLayout: false,
				},
			},
		});
	});

	it('selects the look and feel settings present in the parameters', () => {
		expect(
			toContentSelection(
				PREVIEW_PORTLET_DATA_HANDLER_SECTIONS,
				{
					LAYOUT_SET_PROTOTYPE_SETTINGS: ['false'],
					LAYOUT_SET_SETTINGS: ['true'],
					LOGO: ['false'],
					THEME_REFERENCE: ['true'],
				},
				{lookAndFeelEnabled: true}
			)
		).toEqual({
			'category.site_administration.build': {
				lookAndFeel: {
					sitePagesSettings: true,
					themeSettings: true,
				},
			},
		});
	});

	it('ignores the look and feel settings when they are disabled', () => {
		expect(
			toContentSelection(PREVIEW_PORTLET_DATA_HANDLER_SECTIONS, {
				THEME_REFERENCE: ['true'],
			})
		).toBeUndefined();
	});

	it('selects the comments and ratings present in the parameters', () => {
		expect(
			toContentSelection(
				PREVIEW_PORTLET_DATA_HANDLER_SECTIONS,
				{
					COMMENTS: ['true'],
					PORTLET_DATA_com_liferay_journal: ['true'],
					RATINGS: ['false'],
				},
				{commentsAndRatingsEnabled: true}
			)
		).toEqual({
			'category.content': {
				PORTLET_DATA_com_liferay_journal: {},
				commentsAndRatings: {
					comments: true,
				},
			},
		});
	});

	it('returns undefined when nothing is selected', () => {
		expect(
			toContentSelection(PREVIEW_PORTLET_DATA_HANDLER_SECTIONS, {
				DELETIONS: ['false'],
			})
		).toBeUndefined();
	});
});
