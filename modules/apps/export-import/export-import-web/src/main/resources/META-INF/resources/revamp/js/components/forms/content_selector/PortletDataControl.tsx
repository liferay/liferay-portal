/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import classnames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {ReactNode, useId} from 'react';

import {PortletDataHandlerControl} from '../../../types/portletDataHandler';
import {
	PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
	PortletDataHandlerSelection,
	getNestedPortletDataHandlerControls,
	getPortletDataHandlerSelection,
	getSelectionSummary,
	isSelected,
	updateSelection,
} from '../../../utils/contentSelection';
import {PageTreeModalConfiguration} from '../../PageTreeModal';
import CollapsibleGroup from './CollapsibleGroup';
import ControlRow from './ControlRow';
import LayoutSetControl from './LayoutSetControl';
import PortletDataControlChoice from './PortletDataControlChoice';
import SectionTags from './SectionTags';

export default function PortletDataControl({
	compact = false,
	onChange,
	pageTreeModalConfiguration,
	portletDataHandlerControl,
	portletDataHandlerSelection,
	showDeletions,
	topLevel = false,
}: {
	compact?: boolean;
	onChange: (value: PortletDataHandlerSelection | undefined) => void;
	pageTreeModalConfiguration?: PageTreeModalConfiguration;
	portletDataHandlerControl: PortletDataHandlerControl;
	portletDataHandlerSelection: PortletDataHandlerSelection | undefined;
	showDeletions?: boolean;
	topLevel?: boolean;
}) {
	const checkboxId = useId();

	if (
		portletDataHandlerControl.name ===
			PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS &&
		pageTreeModalConfiguration
	) {
		return (
			<LayoutSetControl
				additionCount={
					portletDataHandlerControl.type === 'Boolean'
						? portletDataHandlerControl.additionCount
						: undefined
				}
				deletionCount={
					portletDataHandlerControl.type === 'Boolean' &&
					showDeletions
						? portletDataHandlerControl.deletionCount
						: undefined
				}
				label={portletDataHandlerControl.label ?? ''}
				onChange={onChange}
				pageTreeModalConfiguration={pageTreeModalConfiguration}
				portletDataHandlerSelection={portletDataHandlerSelection}
			/>
		);
	}

	if (portletDataHandlerControl.type === 'Choice') {
		return (
			<PortletDataControlChoice
				onChange={onChange}
				portletDataHandlerChoice={portletDataHandlerControl}
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
		portletDataHandlerControl
	);
	const portletDataHandlerSelections =
		typeof portletDataHandlerSelection === 'object'
			? (portletDataHandlerSelection as Record<
					string,
					PortletDataHandlerSelection
				>)
			: {};
	const portletDataHandlerControls = getNestedPortletDataHandlerControls(
		portletDataHandlerControl
	);

	const additionCount =
		portletDataHandlerControl.type === 'Boolean'
			? portletDataHandlerControl.additionCount
			: undefined;
	const deletionCount =
		portletDataHandlerControl.type === 'Boolean' && showDeletions
			? portletDataHandlerControl.deletionCount
			: undefined;
	const description =
		topLevel && portletDataHandlerControl.type === 'Boolean'
			? portletDataHandlerControl.description
			: undefined;
	const tag =
		topLevel && portletDataHandlerControl.type === 'Boolean'
			? portletDataHandlerControl.tag
			: undefined;

	const rowProps = {
		checkboxId,
		description,
		indeterminate: !!portletDataHandlerSelection && !selected,
		label: portletDataHandlerControl.label ?? '',
		labelClassName: topLevel
			? 'font-weight-semi-bold'
			: 'font-weight-normal',
		onToggle: () =>
			onChange(
				selected
					? undefined
					: getPortletDataHandlerSelection(portletDataHandlerControl)
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

	const body = portletDataHandlerControls
		.filter(
			(nestedPortletDataHandlerControl) =>
				nestedPortletDataHandlerControl.type !== 'Choice' ||
				!!portletDataHandlerSelection
		)
		.map((nestedPortletDataHandlerControl) => (
			<PortletDataControl
				key={nestedPortletDataHandlerControl.name}
				onChange={(nestedPortletDataHandlerSelection) =>
					onChange(
						updateSelection(
							portletDataHandlerSelections,
							nestedPortletDataHandlerControl.name,
							nestedPortletDataHandlerSelection
						)
					)
				}
				pageTreeModalConfiguration={pageTreeModalConfiguration}
				portletDataHandlerControl={nestedPortletDataHandlerControl}
				portletDataHandlerSelection={
					portletDataHandlerSelections[
						nestedPortletDataHandlerControl.name
					]
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
				portletDataHandlerControls={portletDataHandlerControls}
				portletDataHandlerSelections={portletDataHandlerSelections}
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
	portletDataHandlerControls,
	portletDataHandlerSelections,
	rowProps,
}: {
	bodyChildren: ReactNode;
	compact?: boolean;
	expandable: boolean;
	portletDataHandlerControls: PortletDataHandlerControl[];
	portletDataHandlerSelections: Record<string, PortletDataHandlerSelection>;
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
						portletDataHandlerControls,
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
