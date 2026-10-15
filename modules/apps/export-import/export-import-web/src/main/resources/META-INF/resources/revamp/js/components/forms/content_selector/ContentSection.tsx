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
import {ExportImportProcess} from '../../../types/exportImportProcess';
import {PortletDataHandlerSection as PortletDataHandlerSectionType} from '../../../types/portletDataHandler';
import {
	PortletDataHandlerSelection,
	SECTION_KEYS_COMPACT,
	SECTION_KEYS_SCROLLABLE,
	SECTION_KEY_CONTENT,
	SECTION_KEY_CONTENT_AND_DATA,
	getSectionSelection,
	getSelectionSummary,
	isSelected,
	toPortletDataHandlerBooleans,
	updateSelection,
} from '../../../utils/contentSelection';
import {PageTreeModalConfiguration} from '../../PageTreeModal';
import CollapsibleGroup from './CollapsibleGroup';
import PortletDataControl from './PortletDataControl';
import SectionFooter from './SectionFooter';
import SectionTags from './SectionTags';

const COMMENTS_AND_RATINGS_SUBTITLES: Record<ExportImportProcess, string> = {
	export: Liferay.Language.get(
		'for-each-of-the-selected-content-types,-export-their'
	),
	import: Liferay.Language.get(
		'for-each-of-the-selected-content-types,-import-their'
	),
	publish: Liferay.Language.get(
		'for-each-of-the-selected-content-types,-publish-their'
	),
};

export type SectionSelection = Record<string, PortletDataHandlerSelection>;

interface ContentSectionProps {
	commentsAndRatingsEnabled?: boolean;
	onChange: (value: SectionSelection | undefined) => void;
	pageTreeModalConfiguration?: PageTreeModalConfiguration;
	portletDataHandlerSection: PortletDataHandlerSectionType;
	process?: ExportImportProcess;
	sectionSelection: SectionSelection | undefined;
	showDeletions?: boolean;
}

export default function ContentSection({
	commentsAndRatingsEnabled = false,
	onChange,
	pageTreeModalConfiguration,
	portletDataHandlerSection,
	process = 'export',
	sectionSelection = {},
	showDeletions,
}: ContentSectionProps) {
	const bodyRef = useRef<HTMLDivElement>(null);
	const checkboxId = useId();
	const [overflowing, setOverflowing] = useState(false);

	const compact = SECTION_KEYS_COMPACT.includes(
		portletDataHandlerSection.key
	);
	const scrollable = SECTION_KEYS_SCROLLABLE.includes(
		portletDataHandlerSection.key
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
	}, [scrollable, portletDataHandlerSection]);

	const portletDataHandlerBooleans = toPortletDataHandlerBooleans(
		portletDataHandlerSection
	);

	const allSelected = portletDataHandlerBooleans.every(
		(portletDataHandlerBoolean) =>
			isSelected(
				sectionSelection[portletDataHandlerBoolean.name],
				portletDataHandlerBoolean
			)
	);

	const anySelected = portletDataHandlerBooleans.some(
		(portletDataHandlerBoolean) =>
			sectionSelection[portletDataHandlerBoolean.name] !== undefined
	);

	const sectionFooters = [
		{
			applies:
				commentsAndRatingsEnabled &&
				(portletDataHandlerSection.key === SECTION_KEY_CONTENT ||
					portletDataHandlerSection.key ===
						SECTION_KEY_CONTENT_AND_DATA) &&
				anySelected,
			fields: [
				{key: 'comments', label: Liferay.Language.get('comments')},
				{key: 'ratings', label: Liferay.Language.get('ratings')},
			],
			name: 'commentsAndRatings',
			subtitle: COMMENTS_AND_RATINGS_SUBTITLES[process],
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
										portletDataHandlerSection.label
									)
								: sub(
										Liferay.Language.get('expand-x'),
										portletDataHandlerSection.label
									)
						}
						className="text-secondary"
						displayType="unstyled"
						symbol={expanded ? 'angle-down' : 'angle-right'}
					/>
				)}
				indeterminate={!allSelected && anySelected}
				label={portletDataHandlerSection.label}
				labelClassName="font-weight-bold text-6"
				onToggle={() =>
					onChange(
						allSelected
							? undefined
							: getSectionSelection(portletDataHandlerSection, {
									commentsAndRatingsEnabled,
								})
					)
				}
				selected={allSelected}
				summary={getSelectionSummary(
					portletDataHandlerBooleans,
					sectionSelection
				)}
				tags={
					<SectionTags
						additionCount={portletDataHandlerSection.additionCount}
						deletionCount={
							showDeletions
								? portletDataHandlerSection.deletionCount
								: undefined
						}
					/>
				}
			>
				{portletDataHandlerBooleans.map((portletDataHandlerBoolean) => (
					<PortletDataControl
						compact={compact}
						key={portletDataHandlerBoolean.name}
						onChange={(portletDataHandlerSelection) =>
							onChange(
								updateSelection(
									sectionSelection,
									portletDataHandlerBoolean.name,
									portletDataHandlerSelection
								)
							)
						}
						pageTreeModalConfiguration={pageTreeModalConfiguration}
						portletDataHandlerControl={portletDataHandlerBoolean}
						portletDataHandlerSelection={
							sectionSelection[portletDataHandlerBoolean.name]
						}
						showDeletions={showDeletions}
						topLevel
					/>
				))}

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
