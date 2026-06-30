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

export type HandlerSelection =
	| {
			[key: string]: HandlerSelection | boolean | number[];
	  }
	| string
	| true;

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
	value: HandlerSelection | undefined
): boolean {
	return typeof value === 'object' && !value.layoutIds;
}

export function isSelected(
	value: HandlerSelection | undefined,
	entry: PreviewPortletDataHandlerControl
): boolean {
	if (!value) {
		return false;
	}

	if (entry.name === LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY) {
		return isAllLayoutsSelected(value);
	}

	if (entry.type === 'Choice') {
		return true;
	}

	if (
		!entry.previewPortletDataHandlerControls?.length ||
		typeof value !== 'object'
	) {
		return true;
	}

	return entry.previewPortletDataHandlerControls.every((control) =>
		isSelected(value[control.name] as HandlerSelection, control)
	);
}

export function getHandlerSelection(
	entry: PreviewPortletDataHandlerControl
): HandlerSelection {
	if (entry.name === LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY) {
		return {privateLayout: false};
	}

	if (entry.type === 'Choice') {
		return entry.choices[0].name;
	}

	if (!entry.previewPortletDataHandlerControls?.length) {
		return true;
	}

	return getHandlerSelections(entry.previewPortletDataHandlerControls);
}

export function getHandlerSelections(
	controls: PreviewPortletDataHandlerControl[]
): Record<string, HandlerSelection> {
	return Object.fromEntries(
		controls.map((control) => [control.name, getHandlerSelection(control)])
	);
}

export function getSectionPreviewPortletDataHandlers(
	section: PreviewPortletDataHandlerSection,
	{lookAndFeelEnabled = false}: {lookAndFeelEnabled?: boolean} = {}
): PreviewPortletDataHandlerBoolean[] {
	const previewPortletDataHandlers =
		section.previewPortletDataHandlers.map<PreviewPortletDataHandlerBoolean>(
			(handler) => ({...handler, type: 'Boolean'})
		);

	if (!(lookAndFeelEnabled && section.name === SECTION_KEY_SITE_BUILDER)) {
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
	section: PreviewPortletDataHandlerSection,
	{
		commentsAndRatingsEnabled = false,
		lookAndFeelEnabled = false,
	}: {commentsAndRatingsEnabled?: boolean; lookAndFeelEnabled?: boolean} = {}
): Record<string, HandlerSelection> {
	const selection = getHandlerSelections(
		getSectionPreviewPortletDataHandlers(section, {lookAndFeelEnabled})
	);

	if (
		commentsAndRatingsEnabled &&
		(section.name === SECTION_KEY_CONTENT ||
			section.name === SECTION_KEY_CONTENT_AND_DATA)
	) {
		selection.commentsAndRatings = {comments: true, ratings: true};
	}

	return selection;
}

export function getFullDataSelection(
	sections: PreviewPortletDataHandlerSection[],
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
		getVisibleSections(sections, {lookAndFeelEnabled, showDeletions}).map(
			(section) => [
				section.name,
				getSectionSelection(section, {
					commentsAndRatingsEnabled,
					lookAndFeelEnabled,
				}),
			]
		)
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
	controls: {label: string; name: string}[],
	selection: Record<string, HandlerSelection>
): string {
	const selectedLabels = controls
		.filter((control) => selection[control.name] !== undefined)
		.map((control) => control.label);

	if (selectedLabels.length) {
		return sub(
			Liferay.Language.get('selected-x'),
			selectedLabels.join(', ')
		);
	}

	const labels = controls.map((control) => control.label);

	if (labels.length) {
		return sub(Liferay.Language.get('select-x'), labels.join(', '));
	}

	return '';
}

export function withSiteBuilderSection(
	sections: PreviewPortletDataHandlerSection[],
	label = ''
): PreviewPortletDataHandlerSection[] {
	if (sections.some((section) => section.name === SECTION_KEY_SITE_BUILDER)) {
		return sections;
	}

	return [
		...sections,
		{
			label,
			name: SECTION_KEY_SITE_BUILDER,
			previewPortletDataHandlers: [],
		},
	];
}

export function getVisibleSections(
	sections: PreviewPortletDataHandlerSection[],
	{
		lookAndFeelEnabled = false,
		showDeletions = false,
	}: {lookAndFeelEnabled?: boolean; showDeletions?: boolean} = {}
): PreviewPortletDataHandlerSection[] {
	const filteredSections = sections.filter(
		(section) =>
			showDeletions || !!section.additionCount || !section.deletionCount
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

export function getLayoutSetHandler(
	sections: PreviewPortletDataHandlerSection[]
): PreviewPortletDataHandler | undefined {
	for (const section of sections) {
		const handler = section.previewPortletDataHandlers?.find(
			(previewPortletDataHandler) =>
				previewPortletDataHandler.name ===
				LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY
		);

		if (handler) {
			return handler;
		}
	}

	return undefined;
}

export function getLayoutSetCount(
	sections: PreviewPortletDataHandlerSection[],
	privateLayout: boolean,
	key: 'additionCount' | 'deletionCount' = 'additionCount'
): number | undefined {
	const handler = getLayoutSetHandler(sections);

	if (!handler) {
		return undefined;
	}

	const choiceControl = handler.previewPortletDataHandlerControls?.find(
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

	return handler[key];
}

export function isPrivateLayoutSelected(
	contentSelection: ContentSelection | undefined
): boolean {
	if (!contentSelection) {
		return false;
	}

	for (const sectionSelection of Object.values(contentSelection)) {
		const selection = sectionSelection?.[
			LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY
		] as {privateLayout?: boolean} | undefined;

		if (selection && typeof selection === 'object') {
			return selection.privateLayout === true;
		}
	}

	return false;
}

export function getSelectedItemsCount(
	additionCount: number | undefined,
	sections: PreviewPortletDataHandlerSection[],
	contentSelection: ContentSelection | undefined
): number | undefined {
	if (additionCount === undefined) {
		return undefined;
	}

	return (
		additionCount +
		getLayoutSetCountDelta(sections, contentSelection, 'additionCount')
	);
}

export function getSelectedDeletionCount(
	deletionCount: number | undefined,
	sections: PreviewPortletDataHandlerSection[],
	contentSelection: ContentSelection | undefined
): number | undefined {
	if (deletionCount === undefined) {
		return undefined;
	}

	return (
		deletionCount +
		getLayoutSetCountDelta(sections, contentSelection, 'deletionCount')
	);
}

export function getLayoutSetCountDelta(
	sections: PreviewPortletDataHandlerSection[],
	contentSelection: ContentSelection | undefined,
	key: 'additionCount' | 'deletionCount' = 'additionCount'
): number {
	const privateLayout = isPrivateLayoutSelected(contentSelection);

	const publicCount = getLayoutSetCount(sections, false, key) ?? 0;
	const selectedCount = getLayoutSetCount(sections, privateLayout, key) ?? 0;

	return selectedCount - publicCount;
}

export function withSelectedLayoutSetCount(
	sections: PreviewPortletDataHandlerSection[],
	contentSelection: ContentSelection | undefined
): PreviewPortletDataHandlerSection[] {
	const additionCountDelta = getLayoutSetCountDelta(
		sections,
		contentSelection,
		'additionCount'
	);
	const deletionCountDelta = getLayoutSetCountDelta(
		sections,
		contentSelection,
		'deletionCount'
	);

	if (!additionCountDelta && !deletionCountDelta) {
		return sections;
	}

	const privateLayout = isPrivateLayoutSelected(contentSelection);

	const selectedAdditionCount = getLayoutSetCount(
		sections,
		privateLayout,
		'additionCount'
	);
	const selectedDeletionCount = getLayoutSetCount(
		sections,
		privateLayout,
		'deletionCount'
	);

	return sections.map((section) => {
		if (
			!section.previewPortletDataHandlers?.some(
				(handler) =>
					handler.name === LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY
			)
		) {
			return section;
		}

		return {
			...section,
			additionCount: (section.additionCount ?? 0) + additionCountDelta,
			deletionCount: (section.deletionCount ?? 0) + deletionCountDelta,
			previewPortletDataHandlers: section.previewPortletDataHandlers.map(
				(handler) =>
					handler.name === LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY
						? {
								...handler,
								additionCount: selectedAdditionCount,
								deletionCount: selectedDeletionCount,
							}
						: handler
			),
		};
	});
}
