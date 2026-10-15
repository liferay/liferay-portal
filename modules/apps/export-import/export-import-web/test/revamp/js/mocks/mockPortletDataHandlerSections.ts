/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	PortletDataHandler,
	PortletDataHandlerSection,
} from '../../../../src/main/resources/META-INF/resources/revamp/js/types/portletDataHandler';

export const mockPortletDataHandlerSections: PortletDataHandlerSection[] = [
	{
		key: 'category.site_administration.design',
		label: 'Design',
		portletDataHandlers: [
			{
				label: 'Theme Settings',
				name: 'THEME_REFERENCE',
			},
			{
				label: 'Logo',
				name: 'LOGO',
			},
			{
				label: 'Fragments',
				name: 'PORTLET_DATA_com_liferay_fragment_web_portlet_FragmentPortlet',
			},
		],
	},
	{
		key: 'category.site_administration.build',
		label: 'Site Builder',
		portletDataHandlers: [
			{
				label: 'Pages',
				name: 'PORTLET_DATA_com_liferay_layout_admin_web_portlet_GroupPagesPortlet',
			},
		],
	},
	{
		key: 'category.site_administration.content',
		label: 'Content & Data',
		portletDataHandlers: [
			{
				label: 'Web Content',
				name: 'PORTLET_DATA_com_liferay_journal_web_portlet_JournalPortlet',
				portletDataHandlerControls: [
					{
						label: 'Web Content',
						name: '_journal_web-content',
						type: 'Boolean',
					},
					{
						label: 'Referenced Content',
						name: '_journal_referenced-content',
						portletDataHandlerControls: [
							{
								choices: [
									{
										label: 'Include Always',
										name: 'include-always',
									},
									{
										label: 'Include If Modified',
										name: 'include-if-modified',
									},
								],
								label: 'Referenced Content Behavior',
								name: '_journal_referenced-content-behavior',
								type: 'Choice',
							},
						],
						type: 'Boolean',
					},
					{
						label: 'Version History',
						name: '_journal_version-history',
						type: 'Boolean',
					},
				],
			},
			{
				label: 'Documents and Media',
				name: 'PORTLET_DATA_com_liferay_document_library_web_portlet_DLAdminPortlet',
				portletDataHandlerControls: [
					{
						label: 'Repositories',
						name: '_document_library_repositories',
						type: 'Boolean',
					},
					{
						label: 'Folders',
						name: '_document_library_folders',
						type: 'Boolean',
					},
					{
						label: 'Documents',
						name: '_document_library_documents',
						portletDataHandlerControls: [
							{
								label: 'Previews and Thumbnails',
								name: '_document_library_previews-and-thumbnails',
								type: 'Boolean',
							},
							{
								label: 'Referenced Content',
								name: '_document_library_referenced-content',
								portletDataHandlerControls: [
									{
										choices: [
											{
												label: 'Include Always',
												name: 'include-always',
											},
											{
												label: 'Include If Modified',
												name: 'include-if-modified',
											},
										],
										label: 'Referenced Content Behavior',
										name: '_document_library_referenced-content-behavior',
										type: 'Choice',
									},
								],
								type: 'Boolean',
							},
						],
						type: 'Boolean',
					},
					{
						label: 'Document Types',
						name: '_document_library_document-types',
						type: 'Boolean',
					},
					{
						label: 'Shortcuts',
						name: '_document_library_shortcuts',
						type: 'Boolean',
					},
				],
			},
		],
	},
];

export function toMockPortletDataHandlers(
	portletDataHandlerSections: PortletDataHandlerSection[]
): PortletDataHandler[] {
	return portletDataHandlerSections.flatMap(
		({key, label, portletDataHandlers}) =>
			portletDataHandlers.map((portletDataHandler) => ({
				...portletDataHandler,
				sectionKey: key,
				sectionLabel: label,
			}))
	);
}
