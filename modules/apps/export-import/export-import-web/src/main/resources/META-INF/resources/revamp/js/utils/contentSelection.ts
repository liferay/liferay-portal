/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {
	PreviewPortletDataHandler,
	PreviewPortletDataHandlerBoolean,
	PreviewPortletDataHandlerControl,
	PreviewPortletDataHandlerSection,
} from '../types/portletDataHandler';

import type {ContentSelection} from '../components/forms/content_selector/ContentSelector';

export type PortletDataHandlerSelection =
	| {
			[key: string]: PortletDataHandlerSelection | boolean | number[];
	  }
	| string
	| true;

export interface LayoutSetSelection {
	layoutIds?: number[];
	privateLayout?: boolean;
}

export const COMPACT_SECTION_NAMES = [
	'category.control_panel.users',
	'objects',
];

export const LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY =
	'PORTLET_DATA_com_liferay_layout_admin_web_portlet_LayoutSetLayoutsPortlet';

export const PRIVATE_PAGES_CONTROL_NAME = 'privateLayoutPages';

export const PUBLIC_PAGES_CONTROL_NAME = 'publicLayoutPages';

export const SCROLLABLE_SECTION_NAMES = ['objects'];

export const SECTION_KEY_CONTENT = 'category.content';

export const SECTION_KEY_CONTENT_AND_DATA =
	'category.site_administration.content';

export const SECTION_KEY_SITE_BUILDER = 'category.site_administration.build';

export function isAllLayoutsSelected(
	portletDataHandlerSelection: PortletDataHandlerSelection | undefined
): boolean {
	return (
		typeof portletDataHandlerSelection === 'object' &&
		!portletDataHandlerSelection.layoutIds
	);
}

export function isSelected(
	portletDataHandlerSelection: PortletDataHandlerSelection | undefined,
	previewPortletDataHandlerControl: PreviewPortletDataHandlerControl
): boolean {
	if (!portletDataHandlerSelection) {
		return false;
	}

	if (
		previewPortletDataHandlerControl.name ===
		LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY
	) {
		return isAllLayoutsSelected(portletDataHandlerSelection);
	}

	if (previewPortletDataHandlerControl.type === 'Choice') {
		return true;
	}

	if (
		!previewPortletDataHandlerControl.previewPortletDataHandlerControls
			?.length ||
		typeof portletDataHandlerSelection !== 'object'
	) {
		return true;
	}

	return previewPortletDataHandlerControl.previewPortletDataHandlerControls.every(
		(previewPortletDataHandlerControl) =>
			isSelected(
				portletDataHandlerSelection[
					previewPortletDataHandlerControl.name
				] as PortletDataHandlerSelection,
				previewPortletDataHandlerControl
			)
	);
}

export function getPortletDataHandlerSelection(
	previewPortletDataHandlerControl: PreviewPortletDataHandlerControl
): PortletDataHandlerSelection {
	if (
		previewPortletDataHandlerControl.name ===
		LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY
	) {
		return {privateLayout: false};
	}

	if (previewPortletDataHandlerControl.type === 'Choice') {
		return previewPortletDataHandlerControl.choices[0].name;
	}

	if (
		!previewPortletDataHandlerControl.previewPortletDataHandlerControls
			?.length
	) {
		return true;
	}

	return getPortletDataHandlerSelections(
		previewPortletDataHandlerControl.previewPortletDataHandlerControls
	);
}

export function getPortletDataHandlerSelections(
	previewPortletDataHandlerControls: PreviewPortletDataHandlerControl[]
): Record<string, PortletDataHandlerSelection> {
	return Object.fromEntries(
		previewPortletDataHandlerControls.map(
			(previewPortletDataHandlerControl) => [
				previewPortletDataHandlerControl.name,
				getPortletDataHandlerSelection(
					previewPortletDataHandlerControl
				),
			]
		)
	);
}

export function getSectionPreviewPortletDataHandlers(
	previewPortletDataHandlerSection: PreviewPortletDataHandlerSection,
	{lookAndFeelEnabled = false}: {lookAndFeelEnabled?: boolean} = {}
): PreviewPortletDataHandlerBoolean[] {
	const previewPortletDataHandlers =
		previewPortletDataHandlerSection.previewPortletDataHandlers.map<PreviewPortletDataHandlerBoolean>(
			(previewPortletDataHandler) => ({
				...previewPortletDataHandler,
				type: 'Boolean',
			})
		);

	if (
		!(
			lookAndFeelEnabled &&
			previewPortletDataHandlerSection.name === SECTION_KEY_SITE_BUILDER
		)
	) {
		return previewPortletDataHandlers;
	}

	return [
		...previewPortletDataHandlers,
		{
			label: Liferay.Language.get('look-and-feel'),
			name: 'lookAndFeel',
			previewPortletDataHandlerControls: [
				{
					label: Liferay.Language.get('theme-settings'),
					name: 'themeSettings',
					type: 'Boolean',
				},
				{
					label: Liferay.Language.get('logo'),
					name: 'logo',
					type: 'Boolean',
				},
				{
					label: Liferay.Language.get('site-pages-settings'),
					name: 'sitePagesSettings',
					type: 'Boolean',
				},
				{
					label: Liferay.Language.get('site-template-settings'),
					name: 'siteTemplateSettings',
					type: 'Boolean',
				},
			],
			type: 'Boolean',
		},
	];
}

export function getSectionSelection(
	previewPortletDataHandlerSection: PreviewPortletDataHandlerSection,
	{
		commentsAndRatingsEnabled = false,
		lookAndFeelEnabled = false,
	}: {commentsAndRatingsEnabled?: boolean; lookAndFeelEnabled?: boolean} = {}
): Record<string, PortletDataHandlerSelection> {
	const portletDataHandlerSelections = getPortletDataHandlerSelections(
		getSectionPreviewPortletDataHandlers(previewPortletDataHandlerSection, {
			lookAndFeelEnabled,
		})
	);

	if (
		commentsAndRatingsEnabled &&
		(previewPortletDataHandlerSection.name === SECTION_KEY_CONTENT ||
			previewPortletDataHandlerSection.name ===
				SECTION_KEY_CONTENT_AND_DATA)
	) {
		portletDataHandlerSelections.commentsAndRatings = {
			comments: true,
			ratings: true,
		};
	}

	return portletDataHandlerSelections;
}

export function getFullDataSelection(
	previewPortletDataHandlerSections: PreviewPortletDataHandlerSection[],
	{
		commentsAndRatingsEnabled = false,
		lookAndFeelEnabled = false,
		showDeletions = false,
	}: {
		commentsAndRatingsEnabled?: boolean;
		lookAndFeelEnabled?: boolean;
		showDeletions?: boolean;
	} = {}
): ContentSelection {
	return Object.fromEntries(
		getVisibleSections(previewPortletDataHandlerSections, {
			lookAndFeelEnabled,
			showDeletions,
		}).map((previewPortletDataHandlerSection) => [
			previewPortletDataHandlerSection.name,
			getSectionSelection(previewPortletDataHandlerSection, {
				commentsAndRatingsEnabled,
				lookAndFeelEnabled,
			}),
		])
	);
}

export function updateSelection<V>(
	current: Record<string, V>,
	key: string,
	value: V | undefined
): Record<string, V> | undefined {
	const {[key]: _, ...rest} = current;
	const next: Record<string, V> = value ? {...rest, [key]: value} : rest;

	return Object.keys(next).length ? next : undefined;
}

export function getSelectionSummary(
	previewPortletDataHandlerControls: {label: string; name: string}[],
	portletDataHandlerSelections: Record<string, PortletDataHandlerSelection>
): string {
	const selectedLabels = previewPortletDataHandlerControls
		.filter(
			(previewPortletDataHandlerControl) =>
				portletDataHandlerSelections[
					previewPortletDataHandlerControl.name
				] !== undefined
		)
		.map(
			(previewPortletDataHandlerControl) =>
				previewPortletDataHandlerControl.label
		);

	if (selectedLabels.length) {
		return sub(
			Liferay.Language.get('selected-x'),
			selectedLabels.join(', ')
		);
	}

	const labels = previewPortletDataHandlerControls.map(
		(previewPortletDataHandlerControl) =>
			previewPortletDataHandlerControl.label
	);

	if (labels.length) {
		return sub(Liferay.Language.get('select-x'), labels.join(', '));
	}

	return '';
}

export function withSiteBuilderSection(
	previewPortletDataHandlerSections: PreviewPortletDataHandlerSection[],
	label = ''
): PreviewPortletDataHandlerSection[] {
	if (
		previewPortletDataHandlerSections.some(
			(previewPortletDataHandlerSection) =>
				previewPortletDataHandlerSection.name ===
				SECTION_KEY_SITE_BUILDER
		)
	) {
		return previewPortletDataHandlerSections;
	}

	return [
		...previewPortletDataHandlerSections,
		{
			label,
			name: SECTION_KEY_SITE_BUILDER,
			previewPortletDataHandlers: [],
		},
	];
}

function isDeletionOnlySection(
	previewPortletDataHandlerSection: PreviewPortletDataHandlerSection
): boolean {
	return (
		!previewPortletDataHandlerSection.additionCount &&
		!!previewPortletDataHandlerSection.deletionCount
	);
}

export function getVisibleSections(
	previewPortletDataHandlerSections: PreviewPortletDataHandlerSection[],
	{
		lookAndFeelEnabled = false,
		showDeletions = false,
	}: {lookAndFeelEnabled?: boolean; showDeletions?: boolean} = {}
): PreviewPortletDataHandlerSection[] {
	const filteredSections = previewPortletDataHandlerSections.filter(
		(previewPortletDataHandlerSection) =>
			showDeletions ||
			!isDeletionOnlySection(previewPortletDataHandlerSection)
	);

	return lookAndFeelEnabled
		? withSiteBuilderSection(
				filteredSections,
				Liferay.Language.get('category.site_administration.build')
			)
		: filteredSections;
}

export function toProcessRequestFlags(
	contentSelection: ContentSelection | undefined
) {
	const commentsAndRatings = (contentSelection?.[SECTION_KEY_CONTENT]
		?.commentsAndRatings ??
		contentSelection?.[SECTION_KEY_CONTENT_AND_DATA]?.commentsAndRatings ??
		{}) as Record<string, boolean>;
	const lookAndFeel = (contentSelection?.[SECTION_KEY_SITE_BUILDER]
		?.lookAndFeel ?? {}) as Record<string, boolean>;

	return {
		comments: !!commentsAndRatings.comments,
		logo: !!lookAndFeel.logo,
		ratings: !!commentsAndRatings.ratings,
		sitePagesSettings: !!lookAndFeel.sitePagesSettings,
		siteTemplateSettings: !!lookAndFeel.siteTemplateSettings,
		themeSettings: !!lookAndFeel.themeSettings,
	};
}

export function getLayoutSetPreviewPortletDataHandler(
	previewPortletDataHandlerSections: PreviewPortletDataHandlerSection[]
): PreviewPortletDataHandler | undefined {
	for (const previewPortletDataHandlerSection of previewPortletDataHandlerSections) {
		const previewPortletDataHandler =
			previewPortletDataHandlerSection.previewPortletDataHandlers?.find(
				(previewPortletDataHandler) =>
					previewPortletDataHandler.name ===
					LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY
			);

		if (previewPortletDataHandler) {
			return previewPortletDataHandler;
		}
	}

	return undefined;
}

export function getLayoutSetCount(
	previewPortletDataHandlerSections: PreviewPortletDataHandlerSection[],
	privateLayout: boolean,
	key: 'additionCount' | 'deletionCount' = 'additionCount'
): number | undefined {
	const previewPortletDataHandler = getLayoutSetPreviewPortletDataHandler(
		previewPortletDataHandlerSections
	);

	if (!previewPortletDataHandler) {
		return undefined;
	}

	const choiceControl =
		previewPortletDataHandler.previewPortletDataHandlerControls?.find(
			(previewPortletDataHandlerControl) =>
				previewPortletDataHandlerControl.type === 'Choice'
		);

	if (choiceControl?.type === 'Choice') {
		const choiceName = privateLayout
			? PRIVATE_PAGES_CONTROL_NAME
			: PUBLIC_PAGES_CONTROL_NAME;

		const choice = choiceControl.choices.find(
			({name}) => name === choiceName
		);

		if (choice) {
			return choice[key];
		}
	}

	return previewPortletDataHandler[key];
}

export function isPrivateLayoutSelected(
	contentSelection: ContentSelection | undefined
): boolean {
	if (!contentSelection) {
		return false;
	}

	for (const sectionSelection of Object.values(contentSelection)) {
		const portletDataHandlerSelection = sectionSelection?.[
			LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY
		] as LayoutSetSelection | undefined;

		if (portletDataHandlerSelection) {
			return !!portletDataHandlerSelection.privateLayout;
		}
	}

	return false;
}

export function getSelectedItemsCount(
	additionCount: number | undefined,
	previewPortletDataHandlerSections: PreviewPortletDataHandlerSection[],
	contentSelection: ContentSelection | undefined
): number | undefined {
	if (additionCount === undefined) {
		return undefined;
	}

	return (
		additionCount +
		getLayoutSetCountDelta(
			previewPortletDataHandlerSections,
			contentSelection,
			'additionCount'
		)
	);
}

export function getSelectedDeletionCount(
	deletionCount: number | undefined,
	previewPortletDataHandlerSections: PreviewPortletDataHandlerSection[],
	contentSelection: ContentSelection | undefined
): number | undefined {
	if (deletionCount === undefined) {
		return undefined;
	}

	return (
		deletionCount +
		getLayoutSetCountDelta(
			previewPortletDataHandlerSections,
			contentSelection,
			'deletionCount'
		)
	);
}

export function getLayoutSetCountDelta(
	previewPortletDataHandlerSections: PreviewPortletDataHandlerSection[],
	contentSelection: ContentSelection | undefined,
	key: 'additionCount' | 'deletionCount' = 'additionCount'
): number {
	const privateLayout = isPrivateLayoutSelected(contentSelection);

	const publicCount =
		getLayoutSetCount(previewPortletDataHandlerSections, false, key) ?? 0;
	const selectedCount =
		getLayoutSetCount(
			previewPortletDataHandlerSections,
			privateLayout,
			key
		) ?? 0;

	return selectedCount - publicCount;
}

export function withSelectedLayoutSetCount(
	previewPortletDataHandlerSections: PreviewPortletDataHandlerSection[],
	contentSelection: ContentSelection | undefined
): PreviewPortletDataHandlerSection[] {
	const additionCountDelta = getLayoutSetCountDelta(
		previewPortletDataHandlerSections,
		contentSelection,
		'additionCount'
	);
	const deletionCountDelta = getLayoutSetCountDelta(
		previewPortletDataHandlerSections,
		contentSelection,
		'deletionCount'
	);

	if (!additionCountDelta && !deletionCountDelta) {
		return previewPortletDataHandlerSections;
	}

	const privateLayout = isPrivateLayoutSelected(contentSelection);

	const selectedAdditionCount = getLayoutSetCount(
		previewPortletDataHandlerSections,
		privateLayout,
		'additionCount'
	);
	const selectedDeletionCount = getLayoutSetCount(
		previewPortletDataHandlerSections,
		privateLayout,
		'deletionCount'
	);

	return previewPortletDataHandlerSections.map(
		(previewPortletDataHandlerSection) => {
			if (
				!previewPortletDataHandlerSection.previewPortletDataHandlers?.some(
					(previewPortletDataHandler) =>
						previewPortletDataHandler.name ===
						LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY
				)
			) {
				return previewPortletDataHandlerSection;
			}

			return {
				...previewPortletDataHandlerSection,
				additionCount:
					(previewPortletDataHandlerSection.additionCount ?? 0) +
					additionCountDelta,
				deletionCount:
					(previewPortletDataHandlerSection.deletionCount ?? 0) +
					deletionCountDelta,
				previewPortletDataHandlers:
					previewPortletDataHandlerSection.previewPortletDataHandlers.map(
						(previewPortletDataHandler) =>
							previewPortletDataHandler.name ===
							LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY
								? {
										...previewPortletDataHandler,
										additionCount: selectedAdditionCount,
										deletionCount: selectedDeletionCount,
									}
								: previewPortletDataHandler
					),
			};
		}
	);
}
