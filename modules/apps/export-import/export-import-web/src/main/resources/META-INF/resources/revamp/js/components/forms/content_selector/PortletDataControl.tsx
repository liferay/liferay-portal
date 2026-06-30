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
	HandlerSelection,
	LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY,
	getHandlerSelection,
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
	control,
	onChange,
	pageTreeModalConfiguration,
	showDeletions,
	topLevel = false,
	value,
}: {
	compact?: boolean;
	control: PreviewPortletDataHandlerControl;
	onChange: (value: HandlerSelection | undefined) => void;
	pageTreeModalConfiguration?: PageTreeModalConfiguration;
	showDeletions?: boolean;
	topLevel?: boolean;
	value: HandlerSelection | undefined;
}) {
	const checkboxId = useId();

	if (
		control.name === LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY &&
		pageTreeModalConfiguration
	) {
		return (
			<LayoutSetControl
				additionCount={
					control.type === 'Boolean'
						? control.additionCount
						: undefined
				}
				deletionCount={
					control.type === 'Boolean' && showDeletions
						? control.deletionCount
						: undefined
				}
				label={control.label}
				onChange={onChange}
				pageTreeModalConfiguration={pageTreeModalConfiguration}
				value={value}
			/>
		);
	}

	if (control.type === 'Choice') {
		return (
			<PortletDataControlChoice
				control={control}
				onChange={onChange}
				value={typeof value === 'string' ? value : ''}
			/>
		);
	}

	const selected = isSelected(value, control);
	const currentSelection =
		typeof value === 'object'
			? (value as Record<string, HandlerSelection>)
			: {};
	const nestedControls = control.previewPortletDataHandlerControls ?? [];

	const additionCount =
		control.type === 'Boolean' ? control.additionCount : undefined;
	const deletionCount =
		control.type === 'Boolean' && showDeletions
			? control.deletionCount
			: undefined;
	const description =
		topLevel && control.type === 'Boolean'
			? control.description
			: undefined;
	const tag =
		topLevel && control.type === 'Boolean' ? control.tag : undefined;

	const rowProps = {
		checkboxId,
		description,
		indeterminate: !!value && !selected,
		label: control.label,
		labelClassName: topLevel
			? 'font-weight-semi-bold'
			: 'font-weight-normal',
		onToggle: () =>
			onChange(selected ? undefined : getHandlerSelection(control)),
		selected,
		tags: (
			<SectionTags
				additionCount={additionCount}
				deletionCount={deletionCount}
				tag={tag}
			/>
		),
	};

	const body = nestedControls
		.filter((nestedControl) => nestedControl.type !== 'Choice' || !!value)
		.map((nestedControl) => (
			<PortletDataControl
				control={nestedControl}
				key={nestedControl.name}
				onChange={(controlValue) =>
					onChange(
						updateSelection(
							currentSelection,
							nestedControl.name,
							controlValue
						)
					)
				}
				pageTreeModalConfiguration={pageTreeModalConfiguration}
				value={currentSelection[nestedControl.name]}
			/>
		));

	const expandable = !!body.length;

	if (topLevel) {
		return (
			<PortletDataHandlerPanel
				bodyChildren={body}
				compact={compact}
				currentSelection={currentSelection}
				expandable={expandable}
				nestedControls={nestedControls}
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
	currentSelection,
	expandable,
	nestedControls,
	rowProps,
}: {
	bodyChildren: ReactNode;
	compact?: boolean;
	currentSelection: Record<string, HandlerSelection>;
	expandable: boolean;
	nestedControls: PreviewPortletDataHandlerControl[];
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
						nestedControls,
						currentSelection
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
