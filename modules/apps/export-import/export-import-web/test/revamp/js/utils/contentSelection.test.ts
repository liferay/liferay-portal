/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {PortletDataHandlerSection} from '../../../../src/main/resources/META-INF/resources/revamp/js/types/portletDataHandler';
import {
	CHOICE_NAME_PRIVATE_PAGES,
	CHOICE_NAME_PUBLIC_PAGES,
	CONTROL_NAME_VISIBILITY,
	PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
	getLayoutSetCount,
	getSelectedDeletionCount,
	getSelectedItemsCount,
	isPrivateLayoutSelected,
	toPortletDataHandlerSections,
	withSelectedLayoutSetCount,
} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/contentSelection';

function mockLayoutSetSections({
	privateAdditionCount = 5,
	privateDeletionCount = 0,
	publicAdditionCount = 2,
	publicDeletionCount = 0,
}: {
	privateAdditionCount?: number;
	privateDeletionCount?: number;
	publicAdditionCount?: number;
	publicDeletionCount?: number;
} = {}): PortletDataHandlerSection[] {
	return [
		{
			additionCount: publicAdditionCount,
			deletionCount: publicDeletionCount,
			key: 'category.site_administration.build',
			label: 'Site Builder',
			portletDataHandlers: [
				{
					additionCount: publicAdditionCount,
					deletionCount: publicDeletionCount,
					label: 'Static Pages',
					name: PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
					portletDataHandlerControls: [
						{
							choices: [
								{
									additionCount: publicAdditionCount,
									deletionCount: publicDeletionCount,
									label: 'Public Pages',
									name: CHOICE_NAME_PUBLIC_PAGES,
								},
								{
									additionCount: privateAdditionCount,
									deletionCount: privateDeletionCount,
									label: 'Private Pages',
									name: CHOICE_NAME_PRIVATE_PAGES,
								},
							],
							label: 'Visibility',
							name: CONTROL_NAME_VISIBILITY,
							type: 'Choice',
						},
					],
				},
			],
		},
	];
}

const privateSelection = {
	'category.site_administration.build': {
		[PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS]: {all: true, privateLayout: true},
	},
};

const publicSelection = {
	'category.site_administration.build': {
		[PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS]: {
			all: true,
			privateLayout: false,
		},
	},
};

describe('contentSelection layout set counts', () => {
	it('reads the public and private addition counts from the choices', () => {
		const portletDataHandlerSections = mockLayoutSetSections();

		expect(getLayoutSetCount(portletDataHandlerSections, false)).toBe(2);
		expect(getLayoutSetCount(portletDataHandlerSections, true)).toBe(5);
	});

	it('reads the public and private deletion counts from the choices', () => {
		const portletDataHandlerSections = mockLayoutSetSections({
			privateDeletionCount: 3,
			publicDeletionCount: 1,
		});

		expect(
			getLayoutSetCount(
				portletDataHandlerSections,
				false,
				'deletionCount'
			)
		).toBe(1);
		expect(
			getLayoutSetCount(portletDataHandlerSections, true, 'deletionCount')
		).toBe(3);
	});

	it('detects whether the private layout set is selected', () => {
		expect(isPrivateLayoutSelected(undefined)).toBe(false);
		expect(isPrivateLayoutSelected(publicSelection)).toBe(false);
		expect(isPrivateLayoutSelected(privateSelection)).toBe(true);
	});

	it('keeps the public counts in the totals when the public set is selected', () => {
		const portletDataHandlerSections = mockLayoutSetSections({
			privateDeletionCount: 3,
			publicDeletionCount: 1,
		});

		expect(
			getSelectedItemsCount(
				10,
				portletDataHandlerSections,
				publicSelection
			)
		).toBe(10);
		expect(
			getSelectedDeletionCount(
				4,
				portletDataHandlerSections,
				publicSelection
			)
		).toBe(4);
	});

	it('swaps in the private counts in the totals when private is selected', () => {
		const portletDataHandlerSections = mockLayoutSetSections({
			privateDeletionCount: 3,
			publicDeletionCount: 1,
		});

		expect(
			getSelectedItemsCount(
				10,
				portletDataHandlerSections,
				privateSelection
			)
		).toBe(13);
		expect(
			getSelectedDeletionCount(
				4,
				portletDataHandlerSections,
				privateSelection
			)
		).toBe(6);
	});

	it('leaves the sections untouched when the public set is selected', () => {
		const portletDataHandlerSections = mockLayoutSetSections({
			privateDeletionCount: 3,
			publicDeletionCount: 1,
		});

		expect(
			withSelectedLayoutSetCount(
				portletDataHandlerSections,
				publicSelection
			)
		).toBe(portletDataHandlerSections);
	});

	it('adjusts the section and handler counts when private is selected', () => {
		const portletDataHandlerSections = mockLayoutSetSections({
			privateDeletionCount: 3,
			publicDeletionCount: 1,
		});

		const [portletDataHandlerSection] = withSelectedLayoutSetCount(
			portletDataHandlerSections,
			privateSelection
		);

		expect(portletDataHandlerSection.additionCount).toBe(5);
		expect(portletDataHandlerSection.deletionCount).toBe(3);

		const portletDataHandler =
			portletDataHandlerSection.portletDataHandlers.find(
				({name}) => name === PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS
			);

		expect(portletDataHandler?.additionCount).toBe(5);
		expect(portletDataHandler?.deletionCount).toBe(3);
	});
});

describe('toPortletDataHandlerSections', () => {
	it('groups the handlers by section in order of first appearance and sums the counts', () => {
		expect(
			toPortletDataHandlerSections([
				{
					additionCount: 2,
					deletionCount: 1,
					name: 'a',
					sectionKey: 'category.content',
					sectionLabel: 'Content',
				},
				{
					additionCount: 3,
					name: 'b',
					sectionKey: 'category.site_administration.build',
					sectionLabel: 'Site Builder',
				},
				{
					additionCount: 4,
					deletionCount: 2,
					name: 'c',
					sectionKey: 'category.content',
					sectionLabel: 'Content',
				},
			])
		).toEqual([
			{
				additionCount: 6,
				deletionCount: 3,
				key: 'category.content',
				label: 'Content',
				portletDataHandlers: [
					expect.objectContaining({name: 'a'}),
					expect.objectContaining({name: 'c'}),
				],
			},
			{
				additionCount: 3,
				deletionCount: 0,
				key: 'category.site_administration.build',
				label: 'Site Builder',
				portletDataHandlers: [expect.objectContaining({name: 'b'})],
			},
		]);
	});
});
