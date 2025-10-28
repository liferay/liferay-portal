/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker, Text} from '@clayui/core';
import Form from '@clayui/form';
import ClayLabel from '@clayui/label';
import Layout from '@clayui/layout';
import classNames from 'classnames';
import {useId} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';
import React from 'react';

import SegmentExperience from '../../types/SegmentExperience';

import './ExperienceSelector.scss';

interface BaseProps {
	displayType?: 'light' | 'dark';
	selectedItem?: SegmentExperience;
}

interface ExperienceSelectorProps extends BaseProps {
	className?: string;
	disabled?: boolean;
	label?: string;
	onChangeExperience?: (key: React.Key) => void;
	segmentsExperiences: SegmentExperience[];
	selectedSegmentsExperience: SegmentExperience;
}

const TriggerLabel = React.forwardRef(
	(
		{displayType, selectedItem, ...otherProps}: BaseProps,
		ref: React.LegacyRef<HTMLButtonElement>
	) => {
		if (!selectedItem) {
			return null;
		}

		return (
			<button
				{...otherProps}
				className={classNames(
					'btn btn-block btn-sm c-ml-1 c-py-1 form-control-select layout__experience-selector',
					{'btn-secondary': displayType === 'light'}
				)}
				ref={ref}
			>
				<Layout.ContentRow verticalAlign="center">
					<Layout.ContentCol className="c-mr-2 text-truncate" expand>
						<Text color="secondary" size={4} weight="normal">
							{selectedItem.segmentsExperienceName}
						</Text>
					</Layout.ContentCol>

					<Layout.ContentCol>
						<ClayLabel
							className="bg-transparent c-m-0"
							displayType={
								selectedItem.active ? 'success' : 'secondary'
							}
						>
							{selectedItem.statusLabel}
						</ClayLabel>
					</Layout.ContentCol>
				</Layout.ContentRow>
			</button>
		);
	}
);

export default function ExperienceSelector({
	disabled = false,
	displayType = 'light',
	label,
	onChangeExperience,
	segmentsExperiences,
	selectedSegmentsExperience,
	...otherProps
}: ExperienceSelectorProps) {
	const selectorId = useId();

	const handleExperienceChange = (key: React.Key) => {
		const newSelectedExperience = segmentsExperiences.find(
			(experience) => experience.segmentsExperienceId === key
		);

		if (newSelectedExperience && newSelectedExperience.url) {
			navigate(newSelectedExperience.url);
		}
	};

	return (
		<Form.Group {...otherProps}>
			{label ? <label htmlFor={selectorId}>{label}</label> : null}

			<Picker
				UNSAFE_menuClassName="cadmin"
				aria-label={
					label || Liferay.Language.get('experience-selector')
				}
				as={TriggerLabel}
				disabled={disabled}
				displayType={displayType}
				id={selectorId}
				items={segmentsExperiences}
				messages={{
					itemDescribedby: Liferay.Language.get(
						'you-are-currently-on-a-text-element,-inside-of-a-list-box'
					),
					itemSelected: Liferay.Language.get('x-selected'),
					scrollToBottomAriaLabel:
						Liferay.Language.get('scroll-to-bottom'),
					scrollToTopAriaLabel: Liferay.Language.get('scroll-to-top'),
				}}
				onSelectionChange={onChangeExperience || handleExperienceChange}
				selectedItem={selectedSegmentsExperience}
				selectedKey={selectedSegmentsExperience.segmentsExperienceId}
			>
				{({
					active,
					segmentsEntryName: entryName,
					segmentsExperienceId: id,
					segmentsExperienceName: name,
					statusLabel,
				}) => (
					<Option key={id} textValue={name}>
						<Layout.ContentRow>
							<Layout.ContentCol className="c-pl-0" expand>
								<span className="sr-only">
									{`${name} ${Liferay.Language.get(
										'segment'
									)}: ${entryName} ${statusLabel}`}
								</span>

								<Text aria-hidden size={3} weight="semi-bold">
									{name}
								</Text>

								<Text aria-hidden color="secondary" size={3}>
									{`${Liferay.Language.get(
										'segment'
									)}: ${entryName}`}
								</Text>
							</Layout.ContentCol>

							<Layout.ContentCol className="c-pr-0">
								<ClayLabel
									aria-hidden
									className="c-mr-0"
									displayType={
										active ? 'success' : 'secondary'
									}
								>
									{statusLabel}
								</ClayLabel>
							</Layout.ContentCol>
						</Layout.ContentRow>
					</Option>
				)}
			</Picker>
		</Form.Group>
	);
}
