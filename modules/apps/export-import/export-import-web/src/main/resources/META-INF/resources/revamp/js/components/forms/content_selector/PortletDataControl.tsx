/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import classnames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {ReactNode, useId} from 'react';

import {PageTreeModalConfiguration} from '../../../pages/export/components/PageTreeModal';
import {PreviewPortletDataHandlerControl} from '../../../types/portletDataHandler';
import {
	LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY,
	PortletDataHandlerSelection,
	getPortletDataHandlerSelection,
	getSelectionSummary,
	isSelected,
	updateSelection,
} from '../../../utils/contentSelection';
import CollapsibleGroup from './CollapsibleGroup';
import ControlRow from './ControlRow';
import LayoutSetControl from './LayoutSetControl';
import PortletDataControlChoice from './PortletDataControlChoice';
import SectionTags from './SectionTags';

export default function PortletDataControl({
	compact = false,
	onChange,
	pageTreeModalConfiguration,
	portletDataHandlerSelection,
	previewPortletDataHandlerControl,
	showDeletions,
	topLevel = false,
}: {
	compact?: boolean;
	onChange: (value: PortletDataHandlerSelection | undefined) => void;
	pageTreeModalConfiguration?: PageTreeModalConfiguration;
	portletDataHandlerSelection: PortletDataHandlerSelection | undefined;
	previewPortletDataHandlerControl: PreviewPortletDataHandlerControl;
	showDeletions?: boolean;
	topLevel?: boolean;
}) {
	const checkboxId = useId();

	if (
		previewPortletDataHandlerControl.name ===
			LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY &&
		pageTreeModalConfiguration
	) {
		return (
			<LayoutSetControl
				additionCount={
					previewPortletDataHandlerControl.type === 'Boolean'
						? previewPortletDataHandlerControl.additionCount
						: undefined
				}
				deletionCount={
					previewPortletDataHandlerControl.type === 'Boolean' &&
					showDeletions
						? previewPortletDataHandlerControl.deletionCount
						: undefined
				}
				label={previewPortletDataHandlerControl.label}
				onChange={onChange}
				pageTreeModalConfiguration={pageTreeModalConfiguration}
				portletDataHandlerSelection={portletDataHandlerSelection}
			/>
		);
	}

	if (previewPortletDataHandlerControl.type === 'Choice') {
		return (
			<PortletDataControlChoice
				onChange={onChange}
				previewPortletDataHandlerChoice={
					previewPortletDataHandlerControl
				}
				value={
					typeof portletDataHandlerSelection === 'string'
						? portletDataHandlerSelection
						: ''
				}
			/>
		);
	}

	const selected = isSelected(
		portletDataHandlerSelection,
		previewPortletDataHandlerControl
	);
	const portletDataHandlerSelections =
		typeof portletDataHandlerSelection === 'object'
			? (portletDataHandlerSelection as Record<
					string,
					PortletDataHandlerSelection
				>)
			: {};
	const previewPortletDataHandlerControls =
		previewPortletDataHandlerControl.previewPortletDataHandlerControls ??
		[];

	const additionCount =
		previewPortletDataHandlerControl.type === 'Boolean'
			? previewPortletDataHandlerControl.additionCount
			: undefined;
	const deletionCount =
		previewPortletDataHandlerControl.type === 'Boolean' && showDeletions
			? previewPortletDataHandlerControl.deletionCount
			: undefined;
	const description =
		topLevel && previewPortletDataHandlerControl.type === 'Boolean'
			? previewPortletDataHandlerControl.description
			: undefined;
	const tag =
		topLevel && previewPortletDataHandlerControl.type === 'Boolean'
			? previewPortletDataHandlerControl.tag
			: undefined;

	const rowProps = {
		checkboxId,
		description,
		indeterminate: !!portletDataHandlerSelection && !selected,
		label: previewPortletDataHandlerControl.label,
		labelClassName: topLevel
			? 'font-weight-semi-bold'
			: 'font-weight-normal',
		onToggle: () =>
			onChange(
				selected
					? undefined
					: getPortletDataHandlerSelection(
							previewPortletDataHandlerControl
						)
			),
		selected,
		tags: (
			<SectionTags
				additionCount={additionCount}
				deletionCount={deletionCount}
				tag={tag}
			/>
		),
	};

	const body = previewPortletDataHandlerControls
		.filter(
			(nestedPreviewPortletDataHandlerControl) =>
				nestedPreviewPortletDataHandlerControl.type !== 'Choice' ||
				!!portletDataHandlerSelection
		)
		.map((nestedPreviewPortletDataHandlerControl) => (
			<PortletDataControl
				key={nestedPreviewPortletDataHandlerControl.name}
				onChange={(nestedPortletDataHandlerSelection) =>
					onChange(
						updateSelection(
							portletDataHandlerSelections,
							nestedPreviewPortletDataHandlerControl.name,
							nestedPortletDataHandlerSelection
						)
					)
				}
				pageTreeModalConfiguration={pageTreeModalConfiguration}
				portletDataHandlerSelection={
					portletDataHandlerSelections[
						nestedPreviewPortletDataHandlerControl.name
					]
				}
				previewPortletDataHandlerControl={
					nestedPreviewPortletDataHandlerControl
				}
			/>
		));

	const expandable = !!body.length;

	if (topLevel) {
		return (
			<PortletDataHandlerPanel
				bodyChildren={body}
				compact={compact}
				expandable={expandable}
				portletDataHandlerSelections={portletDataHandlerSelections}
				previewPortletDataHandlerControls={
					previewPortletDataHandlerControls
				}
				rowProps={rowProps}
			/>
		);
	}

	return (
		<>
			<ControlRow {...rowProps} />

			{expandable && (
				<div className="c-gap-1 d-flex flex-column pl-4">{body}</div>
			)}
		</>
	);
}

function PortletDataHandlerPanel({
	bodyChildren,
	compact = false,
	expandable,
	portletDataHandlerSelections,
	previewPortletDataHandlerControls,
	rowProps,
}: {
	bodyChildren: ReactNode;
	compact?: boolean;
	expandable: boolean;
	portletDataHandlerSelections: Record<string, PortletDataHandlerSelection>;
	previewPortletDataHandlerControls: PreviewPortletDataHandlerControl[];
	rowProps: React.ComponentProps<typeof ControlRow>;
}) {
	return (
		<div
			className={classnames({
				[`p-3`]: !compact,
				[`py-2 py-3`]: compact,
			})}
		>
			{expandable ? (
				<CollapsibleGroup
					{...rowProps}
					bodyClassName="c-gap-1 mt-2 pl-4"
					bodyVisibleClassName="d-flex flex-column"
					disclosure={({expanded, ...disclosureProps}) => (
						<ClayButton
							{...disclosureProps}
							aria-label={
								expanded
									? sub(
											Liferay.Language.get('hide-all-x'),
											rowProps.label
										)
									: sub(
											Liferay.Language.get('show-all-x'),
											rowProps.label
										)
							}
							className="font-weight-semi-bold"
							displayType="link"
							size="sm"
						>
							{expanded
								? Liferay.Language.get('hide-all')
								: Liferay.Language.get('show-all')}

							<ClayIcon
								className="ml-1"
								symbol={expanded ? 'angle-down' : 'angle-right'}
							/>
						</ClayButton>
					)}
					summary={getSelectionSummary(
						previewPortletDataHandlerControls,
						portletDataHandlerSelections
					)}
				>
					{bodyChildren}
				</CollapsibleGroup>
			) : (
				<ControlRow {...rowProps} />
			)}
		</div>
	);
}
