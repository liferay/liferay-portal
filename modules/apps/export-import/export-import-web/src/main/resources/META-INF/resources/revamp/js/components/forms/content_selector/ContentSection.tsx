/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayLayout from '@clayui/layout';
import classnames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useEffect, useId, useRef, useState} from 'react';

import '../../../../css/utilities.scss';
import {PageTreeModalConfiguration} from '../../../pages/export/components/PageTreeModal';
import {ExportImportProcess} from '../../../types/exportImportProcess';
import {PreviewPortletDataHandlerSection as PortletDataHandlerSectionType} from '../../../types/portletDataHandler';
import {
	COMPACT_SECTION_NAMES,
	PortletDataHandlerSelection,
	SCROLLABLE_SECTION_NAMES,
	SECTION_KEY_CONTENT,
	SECTION_KEY_CONTENT_AND_DATA,
	getSectionPreviewPortletDataHandlers,
	getSectionSelection,
	getSelectionSummary,
	isSelected,
	updateSelection,
} from '../../../utils/contentSelection';
import CollapsibleGroup from './CollapsibleGroup';
import PortletDataControl from './PortletDataControl';
import SectionFooter from './SectionFooter';
import SectionTags from './SectionTags';

export type SectionSelection = Record<string, PortletDataHandlerSelection>;

interface ContentSectionProps {
	commentsAndRatingsEnabled?: boolean;
	lookAndFeelEnabled?: boolean;
	onChange: (value: SectionSelection | undefined) => void;
	pageTreeModalConfiguration?: PageTreeModalConfiguration;
	previewPortletDataHandlerSection: PortletDataHandlerSectionType;
	process?: ExportImportProcess;
	sectionSelection: SectionSelection | undefined;
	showDeletions?: boolean;
}

export default function ContentSection({
	commentsAndRatingsEnabled = false,
	lookAndFeelEnabled = false,
	onChange,
	pageTreeModalConfiguration,
	previewPortletDataHandlerSection,
	process = 'export',
	sectionSelection = {},
	showDeletions,
}: ContentSectionProps) {
	const bodyRef = useRef<HTMLDivElement>(null);
	const checkboxId = useId();
	const [overflowing, setOverflowing] = useState(false);

	const compact = COMPACT_SECTION_NAMES.includes(
		previewPortletDataHandlerSection.name
	);
	const scrollable = SCROLLABLE_SECTION_NAMES.includes(
		previewPortletDataHandlerSection.name
	);

	useEffect(() => {
		const element = bodyRef.current;

		if (!scrollable || !element || typeof ResizeObserver === 'undefined') {
			return;
		}

		const resizeObserver = new ResizeObserver(() =>
			setOverflowing(element.scrollHeight > element.clientHeight)
		);

		resizeObserver.observe(element);

		for (const child of Array.from(element.children)) {
			resizeObserver.observe(child);
		}

		return () => resizeObserver.disconnect();
	}, [scrollable, previewPortletDataHandlerSection]);

	const allPreviewPortletDataHandlers = getSectionPreviewPortletDataHandlers(
		previewPortletDataHandlerSection,
		{lookAndFeelEnabled}
	);

	const allSelected = allPreviewPortletDataHandlers.every(
		(previewPortletDataHandler) =>
			isSelected(
				sectionSelection[previewPortletDataHandler.name],
				previewPortletDataHandler
			)
	);

	const anySelected = allPreviewPortletDataHandlers.some(
		(previewPortletDataHandler) =>
			sectionSelection[previewPortletDataHandler.name] !== undefined
	);

	const sectionFooters = [
		{
			applies:
				commentsAndRatingsEnabled &&
				(previewPortletDataHandlerSection.name ===
					SECTION_KEY_CONTENT ||
					previewPortletDataHandlerSection.name ===
						SECTION_KEY_CONTENT_AND_DATA) &&
				anySelected,
			fields: [
				{key: 'comments', label: Liferay.Language.get('comments')},
				{key: 'ratings', label: Liferay.Language.get('ratings')},
			],
			name: 'commentsAndRatings',
			subtitle:
				process === 'import'
					? Liferay.Language.get(
							'for-each-of-the-selected-content-types,-import-their'
						)
					: Liferay.Language.get(
							'for-each-of-the-selected-content-types,-export-their'
						),
			title: Liferay.Language.get('comments-and-ratings'),
		},
	].filter(({applies}) => applies);

	return (
		<ClayLayout.Sheet className="mt-0">
			<CollapsibleGroup
				bodyClassName={classnames('mt-2 pl-2', {
					'border rounded': overflowing,
					'content-section-scroll': scrollable,
				})}
				bodyRef={bodyRef}
				checkboxId={checkboxId}
				disclosure={({expanded, ...disclosureProps}) => (
					<ClayButtonWithIcon
						{...disclosureProps}
						aria-label={
							expanded
								? sub(
										Liferay.Language.get('collapse-x'),
										previewPortletDataHandlerSection.label
									)
								: sub(
										Liferay.Language.get('expand-x'),
										previewPortletDataHandlerSection.label
									)
						}
						className="text-secondary"
						displayType="unstyled"
						symbol={expanded ? 'angle-down' : 'angle-right'}
					/>
				)}
				indeterminate={
					!allSelected &&
					allPreviewPortletDataHandlers.some(
						(previewPortletDataHandler) =>
							sectionSelection[previewPortletDataHandler.name] !==
							undefined
					)
				}
				label={previewPortletDataHandlerSection.label}
				labelClassName="font-weight-bold text-6"
				onToggle={() =>
					onChange(
						allSelected
							? undefined
							: getSectionSelection(
									previewPortletDataHandlerSection,
									{
										commentsAndRatingsEnabled,
										lookAndFeelEnabled,
									}
								)
					)
				}
				selected={allSelected}
				summary={getSelectionSummary(
					allPreviewPortletDataHandlers,
					sectionSelection
				)}
				tags={
					<SectionTags
						additionCount={
							previewPortletDataHandlerSection.additionCount
						}
						deletionCount={
							showDeletions
								? previewPortletDataHandlerSection.deletionCount
								: undefined
						}
					/>
				}
			>
				{allPreviewPortletDataHandlers.map(
					(previewPortletDataHandler) => (
						<PortletDataControl
							compact={compact}
							key={previewPortletDataHandler.name}
							onChange={(portletDataHandlerSelection) =>
								onChange(
									updateSelection(
										sectionSelection,
										previewPortletDataHandler.name,
										portletDataHandlerSelection
									)
								)
							}
							pageTreeModalConfiguration={
								pageTreeModalConfiguration
							}
							portletDataHandlerSelection={
								sectionSelection[previewPortletDataHandler.name]
							}
							previewPortletDataHandlerControl={
								previewPortletDataHandler
							}
							showDeletions={showDeletions}
							topLevel
						/>
					)
				)}

				{sectionFooters.map((sectionFooter) => (
					<SectionFooter
						fields={sectionFooter.fields}
						key={sectionFooter.name}
						name={sectionFooter.name}
						onChange={(portletDataHandlerSelection) =>
							onChange(
								updateSelection(
									sectionSelection,
									sectionFooter.name,
									portletDataHandlerSelection
								)
							)
						}
						portletDataHandlerSelection={
							sectionSelection[sectionFooter.name]
						}
						subtitle={sectionFooter.subtitle}
						title={sectionFooter.title}
					/>
				))}
			</CollapsibleGroup>
		</ClayLayout.Sheet>
	);
}
