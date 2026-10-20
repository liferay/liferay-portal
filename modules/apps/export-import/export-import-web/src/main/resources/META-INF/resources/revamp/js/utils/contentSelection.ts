/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {
	PortletDataHandler,
	PortletDataHandlerBoolean,
	PortletDataHandlerControl,
	PortletDataHandlerSection,
} from '../types/portletDataHandler';

import type {SitePageTreeSelection} from '@liferay/layout-js-components-web';

import type {ContentSelection} from '../components/forms/content_selector/ContentSelector';

export type PortletDataHandlerSelection =
	| {
			[key: string]: PortletDataHandlerSelection | boolean | string[];
	  }
	| string
	| true;

export type LayoutSetSelection = Omit<SitePageTreeSelection, 'privateLayout'> &
	Partial<Pick<SitePageTreeSelection, 'privateLayout'>>;

export const CHOICE_NAME_PRIVATE_PAGES = 'private-pages';

export const CHOICE_NAME_PUBLIC_PAGES = 'public-pages';

export const CONTROL_NAME_PAGES = '_layout_set_pages';

export const CONTROL_NAME_VISIBILITY = '_layout_set_visibility';

export const PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS =
	'PORTLET_DATA_com_liferay_layout_admin_web_portlet_LayoutSetLayoutsPortlet';

export const SECTION_KEY_CONTENT = 'category.content';

export const SECTION_KEY_CONTENT_AND_DATA =
	'category.site_administration.content';

export const SECTION_KEYS_COMPACT = ['category.control_panel.users', 'objects'];

export const SECTION_KEYS_SCROLLABLE = ['objects'];

export function isAllLayoutsSelected(
	portletDataHandlerSelection: PortletDataHandlerSelection | undefined
): boolean {
	if (
		typeof portletDataHandlerSelection !== 'object' ||
		!portletDataHandlerSelection.all
	) {
		return false;
	}

	const {excludedItems = [], excludedSubtrees = []} =
		portletDataHandlerSelection as LayoutSetSelection;

	return !excludedItems.length && !excludedSubtrees.length;
}

export function getNestedPortletDataHandlerControls(
	portletDataHandlerControl: PortletDataHandlerControl
): PortletDataHandlerControl[] {
	if (portletDataHandlerControl.type === 'TreeSelection') {
		return [];
	}

	return portletDataHandlerControl.portletDataHandlerControls ?? [];
}

export function isSelected(
	portletDataHandlerSelection: PortletDataHandlerSelection | undefined,
	portletDataHandlerControl: PortletDataHandlerControl
): boolean {
	if (!portletDataHandlerSelection) {
		return false;
	}

	if (
		portletDataHandlerControl.name === PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS
	) {
		return isAllLayoutsSelected(portletDataHandlerSelection);
	}

	if (portletDataHandlerControl.type === 'Choice') {
		return true;
	}

	const nestedPortletDataHandlerControls =
		getNestedPortletDataHandlerControls(portletDataHandlerControl);

	if (
		!nestedPortletDataHandlerControls.length ||
		typeof portletDataHandlerSelection !== 'object'
	) {
		return true;
	}

	return nestedPortletDataHandlerControls.every(
		(nestedPortletDataHandlerControl) =>
			isSelected(
				portletDataHandlerSelection[
					nestedPortletDataHandlerControl.name
				] as PortletDataHandlerSelection,
				nestedPortletDataHandlerControl
			)
	);
}

function isPrivateLayoutDefault(
	portletDataHandlerControl: PortletDataHandlerControl
): boolean {
	const visibilityPortletDataHandlerControl =
		getNestedPortletDataHandlerControls(portletDataHandlerControl).find(
			(nestedPortletDataHandlerControl) =>
				nestedPortletDataHandlerControl.name === CONTROL_NAME_VISIBILITY
		);

	return (
		visibilityPortletDataHandlerControl?.type === 'Choice' &&
		visibilityPortletDataHandlerControl.choice === CHOICE_NAME_PRIVATE_PAGES
	);
}

export function getPortletDataHandlerSelection(
	portletDataHandlerControl: PortletDataHandlerControl
): PortletDataHandlerSelection {
	if (
		portletDataHandlerControl.name === PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS
	) {
		return {
			all: true,
			privateLayout: isPrivateLayoutDefault(portletDataHandlerControl),
		};
	}

	if (portletDataHandlerControl.type === 'Choice') {
		return (
			portletDataHandlerControl.choice ??
			portletDataHandlerControl.choices?.[0]?.name ??
			''
		);
	}

	const nestedPortletDataHandlerControls =
		getNestedPortletDataHandlerControls(portletDataHandlerControl);

	if (!nestedPortletDataHandlerControls.length) {
		return true;
	}

	return getPortletDataHandlerSelections(nestedPortletDataHandlerControls);
}

export function getPortletDataHandlerSelections(
	portletDataHandlerControls: PortletDataHandlerControl[]
): Record<string, PortletDataHandlerSelection> {
	return Object.fromEntries(
		portletDataHandlerControls.map((portletDataHandlerControl) => [
			portletDataHandlerControl.name,
			getPortletDataHandlerSelection(portletDataHandlerControl),
		])
	);
}

export function toPortletDataHandlerSections(
	portletDataHandlers: PortletDataHandler[]
): PortletDataHandlerSection[] {
	const portletDataHandlerSections: PortletDataHandlerSection[] = [];

	for (const portletDataHandler of portletDataHandlers) {
		const {sectionKey = '', sectionLabel = ''} = portletDataHandler;

		let portletDataHandlerSection = portletDataHandlerSections.find(
			({key}) => key === sectionKey
		);

		if (!portletDataHandlerSection) {
			portletDataHandlerSection = {
				additionCount: 0,
				deletionCount: 0,
				key: sectionKey,
				label: sectionLabel,
				portletDataHandlers: [],
			};

			portletDataHandlerSections.push(portletDataHandlerSection);
		}

		portletDataHandlerSection.additionCount! +=
			portletDataHandler.additionCount ?? 0;
		portletDataHandlerSection.deletionCount! +=
			portletDataHandler.deletionCount ?? 0;
		portletDataHandlerSection.portletDataHandlers.push(portletDataHandler);
	}

	return portletDataHandlerSections;
}

export function toPortletDataHandlerBooleans(
	portletDataHandlerSection: PortletDataHandlerSection
): PortletDataHandlerBoolean[] {
	return portletDataHandlerSection.portletDataHandlers.map<PortletDataHandlerBoolean>(
		(portletDataHandler) => ({
			...portletDataHandler,
			type: 'Boolean',
		})
	);
}

export function getSectionSelection(
	portletDataHandlerSection: PortletDataHandlerSection,
	{
		commentsAndRatingsEnabled = false,
	}: {commentsAndRatingsEnabled?: boolean} = {}
): Record<string, PortletDataHandlerSelection> {
	const portletDataHandlerSelections = getPortletDataHandlerSelections(
		toPortletDataHandlerBooleans(portletDataHandlerSection)
	);

	if (
		commentsAndRatingsEnabled &&
		(portletDataHandlerSection.key === SECTION_KEY_CONTENT ||
			portletDataHandlerSection.key === SECTION_KEY_CONTENT_AND_DATA)
	) {
		portletDataHandlerSelections.commentsAndRatings = {
			comments: true,
			ratings: true,
		};
	}

	return portletDataHandlerSelections;
}

export function getFullDataSelection(
	portletDataHandlerSections: PortletDataHandlerSection[],
	{
		commentsAndRatingsEnabled = false,
		showDeletions = false,
	}: {
		commentsAndRatingsEnabled?: boolean;
		showDeletions?: boolean;
	} = {}
): ContentSelection {
	return Object.fromEntries(
		getVisibleSections(portletDataHandlerSections, {showDeletions}).map(
			(portletDataHandlerSection) => [
				portletDataHandlerSection.key,
				getSectionSelection(portletDataHandlerSection, {
					commentsAndRatingsEnabled,
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
	portletDataHandlerControls: {label?: string; name: string}[],
	portletDataHandlerSelections: Record<string, PortletDataHandlerSelection>
): string {
	const selectedLabels = portletDataHandlerControls
		.filter(
			(portletDataHandlerControl) =>
				portletDataHandlerSelections[portletDataHandlerControl.name] !==
				undefined
		)
		.map(
			(portletDataHandlerControl) => portletDataHandlerControl.label ?? ''
		);

	if (selectedLabels.length) {
		return sub(
			Liferay.Language.get('selected-x'),
			selectedLabels.join(', ')
		);
	}

	const labels = portletDataHandlerControls.map(
		(portletDataHandlerControl) => portletDataHandlerControl.label ?? ''
	);

	if (labels.length) {
		return sub(Liferay.Language.get('select-x'), labels.join(', '));
	}

	return '';
}

function isDeletionOnlySection(
	portletDataHandlerSection: PortletDataHandlerSection
): boolean {
	return (
		!portletDataHandlerSection.additionCount &&
		!!portletDataHandlerSection.deletionCount
	);
}

export function getVisibleSections(
	portletDataHandlerSections: PortletDataHandlerSection[],
	{showDeletions = false}: {showDeletions?: boolean} = {}
): PortletDataHandlerSection[] {
	return portletDataHandlerSections.filter(
		(portletDataHandlerSection) =>
			showDeletions || !isDeletionOnlySection(portletDataHandlerSection)
	);
}

export function toProcessRequestFlags(
	contentSelection: ContentSelection | null | undefined
) {
	const commentsAndRatings = (contentSelection?.[SECTION_KEY_CONTENT]
		?.commentsAndRatings ??
		contentSelection?.[SECTION_KEY_CONTENT_AND_DATA]?.commentsAndRatings ??
		{}) as Record<string, boolean>;

	return {
		comments: !!commentsAndRatings.comments,
		ratings: !!commentsAndRatings.ratings,
	};
}

function getLayoutSetPortletDataHandler(
	portletDataHandlerSections: PortletDataHandlerSection[]
): PortletDataHandler | undefined {
	for (const portletDataHandlerSection of portletDataHandlerSections) {
		const portletDataHandler =
			portletDataHandlerSection.portletDataHandlers.find(
				({name}) => name === PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS
			);

		if (portletDataHandler) {
			return portletDataHandler;
		}
	}

	return undefined;
}

export function getLayoutSetCount(
	portletDataHandlerSections: PortletDataHandlerSection[],
	privateLayout: boolean,
	key: 'additionCount' | 'deletionCount' = 'additionCount'
): number | undefined {
	const portletDataHandler = getLayoutSetPortletDataHandler(
		portletDataHandlerSections
	);

	if (!portletDataHandler) {
		return undefined;
	}

	const choiceControl = portletDataHandler.portletDataHandlerControls?.find(
		(portletDataHandlerControl) =>
			portletDataHandlerControl.type === 'Choice'
	);

	if (choiceControl?.type === 'Choice') {
		const choiceName = privateLayout
			? CHOICE_NAME_PRIVATE_PAGES
			: CHOICE_NAME_PUBLIC_PAGES;

		const choice = choiceControl.choices?.find(
			({name}) => name === choiceName
		);

		if (choice) {
			return choice[key];
		}
	}

	return portletDataHandler[key];
}

function getLayoutSetSelection(
	contentSelection: ContentSelection | null | undefined
): LayoutSetSelection | true | undefined {
	if (!contentSelection) {
		return undefined;
	}

	for (const sectionSelection of Object.values(contentSelection)) {
		const portletDataHandlerSelection = sectionSelection?.[
			PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS
		] as LayoutSetSelection | true | undefined;

		if (portletDataHandlerSelection) {
			return portletDataHandlerSelection;
		}
	}

	return undefined;
}

export function isPrivateLayoutSelected(
	contentSelection: ContentSelection | null | undefined
): boolean {
	const layoutSetSelection = getLayoutSetSelection(contentSelection);

	if (!layoutSetSelection || layoutSetSelection === true) {
		return false;
	}

	return !!layoutSetSelection.privateLayout;
}

export function getSelectedItemsCount(
	additionCount: number | undefined,
	portletDataHandlerSections: PortletDataHandlerSection[],
	contentSelection: ContentSelection | null | undefined
): number | undefined {
	if (additionCount === undefined) {
		return undefined;
	}

	return (
		additionCount +
		getLayoutSetCountDelta(
			portletDataHandlerSections,
			contentSelection,
			'additionCount'
		)
	);
}

export function getSelectedDeletionCount(
	deletionCount: number | undefined,
	portletDataHandlerSections: PortletDataHandlerSection[],
	contentSelection: ContentSelection | null | undefined
): number | undefined {
	if (deletionCount === undefined) {
		return undefined;
	}

	return (
		deletionCount +
		getLayoutSetCountDelta(
			portletDataHandlerSections,
			contentSelection,
			'deletionCount'
		)
	);
}

export function getLayoutSetCountDelta(
	portletDataHandlerSections: PortletDataHandlerSection[],
	contentSelection: ContentSelection | null | undefined,
	key: 'additionCount' | 'deletionCount' = 'additionCount'
): number {
	const privateLayout = isPrivateLayoutSelected(contentSelection);

	const publicCount =
		getLayoutSetCount(portletDataHandlerSections, false, key) ?? 0;
	const selectedCount =
		getLayoutSetCount(portletDataHandlerSections, privateLayout, key) ?? 0;

	return selectedCount - publicCount;
}

export function withSelectedLayoutSetCount(
	portletDataHandlerSections: PortletDataHandlerSection[],
	contentSelection: ContentSelection | null | undefined
): PortletDataHandlerSection[] {
	const additionCountDelta = getLayoutSetCountDelta(
		portletDataHandlerSections,
		contentSelection,
		'additionCount'
	);
	const deletionCountDelta = getLayoutSetCountDelta(
		portletDataHandlerSections,
		contentSelection,
		'deletionCount'
	);

	if (!additionCountDelta && !deletionCountDelta) {
		return portletDataHandlerSections;
	}

	const privateLayout = isPrivateLayoutSelected(contentSelection);

	const selectedAdditionCount = getLayoutSetCount(
		portletDataHandlerSections,
		privateLayout,
		'additionCount'
	);
	const selectedDeletionCount = getLayoutSetCount(
		portletDataHandlerSections,
		privateLayout,
		'deletionCount'
	);

	return portletDataHandlerSections.map((portletDataHandlerSection) => {
		if (
			!portletDataHandlerSection.portletDataHandlers.some(
				(portletDataHandler) =>
					portletDataHandler.name ===
					PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS
			)
		) {
			return portletDataHandlerSection;
		}

		return {
			...portletDataHandlerSection,
			additionCount:
				(portletDataHandlerSection.additionCount ?? 0) +
				additionCountDelta,
			deletionCount:
				(portletDataHandlerSection.deletionCount ?? 0) +
				deletionCountDelta,
			portletDataHandlers:
				portletDataHandlerSection.portletDataHandlers.map(
					(portletDataHandler) =>
						portletDataHandler.name ===
						PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS
							? {
									...portletDataHandler,
									additionCount: selectedAdditionCount,
									deletionCount: selectedDeletionCount,
								}
							: portletDataHandler
				),
		};
	});
}
