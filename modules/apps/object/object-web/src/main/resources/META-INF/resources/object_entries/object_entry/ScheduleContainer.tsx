/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayPanel from '@clayui/panel';
import React, {useState} from 'react';

import {convertToUTC} from '../../js/utils/convertToUTC';
import ModalSchedulePublication from './ModalSchedulePublication';
import ScheduleField from './ScheduleField';

import './ScheduleContainer.scss';

type DateProperties = {
	expirationDate: {
		checked: boolean;
		value: string;
	};
	reviewDate: {
		checked: boolean;
		value: string;
	};
};

type HiddenValue = {
	[key in 'expirationDate' | 'reviewDate' | 'displayDate']: string | null;
};

interface ContainerProperties {
	portletNamespace: string;
	scheduleProperties: ScheduleProperties;
	submitRef: string;
}

interface FieldProperties {
	checkboxLabel: string;
	customValidation?: (date: string) => string;
	dateLabel: string;
	schedulePropertyKey: 'expirationDate' | 'reviewDate';
}

export interface ScheduleProperties extends DateProperties {
	displayDate: {
		value: string;
	};
}

export default function ScheduleContainer({
	portletNamespace,
	scheduleProperties,
	submitRef,
}: ContainerProperties) {
	const [displayedScheduleValues, setDisplayedScheduleValues] =
		useState<DateProperties>({
			expirationDate: {
				...scheduleProperties.expirationDate,
				value: scheduleProperties.expirationDate.value ?? '',
			},
			reviewDate: {
				...scheduleProperties.reviewDate,
				value: scheduleProperties.reviewDate.value ?? '',
			},
		});

	const [hiddenScheduleValues, setHiddenScheduleValues] =
		useState<HiddenValue>({
			displayDate: convertToUTC(scheduleProperties.displayDate?.value),
			expirationDate: convertToUTC(
				scheduleProperties.expirationDate.value
			),
			reviewDate: convertToUTC(scheduleProperties.reviewDate.value),
		});

	const handleCheckboxChange = ({
		event,
		property,
	}: {
		event: React.ChangeEvent<HTMLInputElement>;
		property: 'expirationDate' | 'reviewDate';
	}) => {
		const checked = event.target.checked;

		const value = displayedScheduleValues[property].value;

		setHiddenScheduleValues((prev) => ({
			...prev,
			[property]: checked ? null : value ? convertToUTC(value) : '',
		}));
	};

	const scheduleFieldProps: FieldProperties[] = [
		{
			checkboxLabel: Liferay.Language.get('never-expire'),
			customValidation: (date: string) => {
				const currentDateTime = new Date();
				const dateTime = new Date(date);

				if (currentDateTime >= dateTime) {
					return Liferay.Language.get(
						'the-date-entered-is-in-the-past'
					);
				}

				return '';
			},
			dateLabel: Liferay.Language.get('expiration-date'),
			schedulePropertyKey: 'expirationDate',
		},
		{
			checkboxLabel: Liferay.Language.get('never-review'),
			dateLabel: Liferay.Language.get('review-date'),
			schedulePropertyKey: 'reviewDate',
		},
	];

	return (
		<>
			<ClayPanel
				collapsable
				defaultExpanded
				displayTitle={Liferay.Language.get('schedule')}
				displayType="secondary"
			>
				<div className="lfr-object__entries-schedule-panel-description">
					<Text size={3}>
						{Liferay.Language.get(
							'set-expiration-and-review-dates-for-the-object-entry'
						)}
					</Text>
				</div>

				<ClayPanel.Body className="lfr-object__entries-schedule-panel-content">
					<div className="row">
						{scheduleFieldProps.map(
							({
								checkboxLabel,
								customValidation,
								dateLabel,
								schedulePropertyKey,
							}) => (
								<ScheduleField
									checkboxLabel={checkboxLabel}
									customValidation={customValidation}
									dateLabel={dateLabel}
									id={`${portletNamespace}${schedulePropertyKey}`}
									isChecked={
										displayedScheduleValues[
											schedulePropertyKey
										].checked
									}
									key={schedulePropertyKey}
									onCheckboxChange={(
										event: React.ChangeEvent<HTMLInputElement>
									) => {
										handleCheckboxChange({
											event,
											property: schedulePropertyKey,
										});
									}}
									onDateChange={(value: string) => {
										setDisplayedScheduleValues({
											...displayedScheduleValues,
											[schedulePropertyKey]: {
												...scheduleProperties[
													schedulePropertyKey
												],
												value,
											},
										});
										setHiddenScheduleValues((prev) => ({
											...prev,
											[schedulePropertyKey]:
												convertToUTC(value),
										}));
									}}
									value={
										displayedScheduleValues[
											schedulePropertyKey
										].value
									}
								/>
							)
						)}

						<input
							id={portletNamespace + 'scheduleContainer'}
							type="hidden"
							value={JSON.stringify(hiddenScheduleValues)}
						/>
					</div>
				</ClayPanel.Body>
			</ClayPanel>

			<ModalSchedulePublication
				hiddenScheduleValues={hiddenScheduleValues}
				portletNamespace={portletNamespace}
				submitRef={submitRef}
				value={scheduleProperties.displayDate?.value}
			/>
		</>
	);
}
