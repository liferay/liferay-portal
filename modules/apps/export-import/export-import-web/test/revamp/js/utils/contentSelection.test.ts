/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {PreviewPortletDataHandlerSection} from '../../../../src/main/resources/META-INF/resources/revamp/js/types/portletDataHandler';
import {
	LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY,
	PRIVATE_PAGES_CONTROL_NAME,
	PUBLIC_PAGES_CONTROL_NAME,
	SECTION_KEY_SITE_BUILDER,
	getLayoutSetCount,
	getSelectedDeletionCount,
	getSelectedItemsCount,
	isPrivateLayoutSelected,
	withSelectedLayoutSetCount,
} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/contentSelection';

function getSections(
	publicAdditionCount: number,
	privateAdditionCount: number,
	publicDeletionCount = 0,
	privateDeletionCount = 0
): PreviewPortletDataHandlerSection[] {
	return [
		{
			additionCount: publicAdditionCount,
			deletionCount: publicDeletionCount,
			label: 'Site Builder',
			name: SECTION_KEY_SITE_BUILDER,
			previewPortletDataHandlers: [
				{
					additionCount: publicAdditionCount,
					deletionCount: publicDeletionCount,
					label: 'Static Pages',
					name: LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY,
					previewPortletDataHandlerControls: [
						{
							choices: [
								{
									additionCount: publicAdditionCount,
									deletionCount: publicDeletionCount,
									label: 'Public Pages',
									name: PUBLIC_PAGES_CONTROL_NAME,
								},
								{
									additionCount: privateAdditionCount,
									deletionCount: privateDeletionCount,
									label: 'Private Pages',
									name: PRIVATE_PAGES_CONTROL_NAME,
								},
							],
							label: 'Static Pages',
							name: 'layoutSet',
							type: 'Choice',
						},
					],
				},
			],
		},
	];
}

const privateSelection = {
	[SECTION_KEY_SITE_BUILDER]: {
		[LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY]: {privateLayout: true},
	},
};

const publicSelection = {
	[SECTION_KEY_SITE_BUILDER]: {
		[LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY]: {privateLayout: false},
	},
};

describe('contentSelection layout set counts', () => {
	it('reads the public and private addition counts from the choices', () => {
		const sections = getSections(2, 5);

		expect(getLayoutSetCount(sections, false)).toBe(2);
		expect(getLayoutSetCount(sections, true)).toBe(5);
	});

	it('reads the public and private deletion counts from the choices', () => {
		const sections = getSections(2, 5, 1, 3);

		expect(getLayoutSetCount(sections, false, 'deletionCount')).toBe(1);
		expect(getLayoutSetCount(sections, true, 'deletionCount')).toBe(3);
	});

	it('detects whether the private layout set is selected', () => {
		expect(isPrivateLayoutSelected(undefined)).toBe(false);
		expect(isPrivateLayoutSelected(publicSelection)).toBe(false);
		expect(isPrivateLayoutSelected(privateSelection)).toBe(true);
	});

	it('keeps the public counts in the totals when the public set is selected', () => {
		const sections = getSections(2, 5, 1, 3);

		expect(getSelectedItemsCount(10, sections, publicSelection)).toBe(10);
		expect(getSelectedDeletionCount(4, sections, publicSelection)).toBe(4);
	});

	it('swaps in the private counts in the totals when private is selected', () => {
		const sections = getSections(2, 5, 1, 3);

		expect(getSelectedItemsCount(10, sections, privateSelection)).toBe(13);
		expect(getSelectedDeletionCount(4, sections, privateSelection)).toBe(6);
	});

	it('leaves the sections untouched when the public set is selected', () => {
		const sections = getSections(2, 5, 1, 3);

		expect(withSelectedLayoutSetCount(sections, publicSelection)).toBe(
			sections
		);
	});

	it('adjusts the section and handler counts when private is selected', () => {
		const sections = getSections(2, 5, 1, 3);

		const [section] = withSelectedLayoutSetCount(
			sections,
			privateSelection
		);

		expect(section.additionCount).toBe(5);
		expect(section.deletionCount).toBe(3);

		const handler = section.previewPortletDataHandlers.find(
			({name}) => name === LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY
		);

		expect(handler?.additionCount).toBe(5);
		expect(handler?.deletionCount).toBe(3);
	});
});
