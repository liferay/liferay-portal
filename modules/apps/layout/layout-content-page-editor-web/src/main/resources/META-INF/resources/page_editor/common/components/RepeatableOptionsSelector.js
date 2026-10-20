/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

import {SelectField} from '../../app/components/fragment_configuration_fields/SelectField';
import {TextField} from '../../app/components/fragment_configuration_fields/TextField';
import getSelectedField from '../../app/utils/getSelectedField';

const ALL_OPTION = 'all';
const FIRST_OPTION = 'first';
const SPECIFIC_NUMBER_OPTION = 'iteration-number';
const LAST_OPTION = 'last';

const REPEATABLE_ITERATION_TYPE_OPTIONS = [
	{
		label: Liferay.Language.get('all'),
		value: ALL_OPTION,
	},
	{
		label: Liferay.Language.get('first[item]'),
		value: FIRST_OPTION,
	},
	{
		label: Liferay.Language.get('specific-number'),
		value: SPECIFIC_NUMBER_OPTION,
	},
	{
		label: Liferay.Language.get('last'),
		value: LAST_OPTION,
	},
];

export default function RepeatableOptionsSelector({
	fieldName,
	fields,
	onOptionsSelect,
	options,
}) {
	const field = getSelectedField({fields, value: fieldName});

	if (!field || !field.repeatable) {
		return null;
	}

	const {iterationNumber, iterationType} = options || {};

	return (
		<>
			<SelectField
				field={{
					label: Liferay.Language.get('iteration-to-display'),
					name: 'iterationType',
					typeOptions: {
						validValues: REPEATABLE_ITERATION_TYPE_OPTIONS,
					},
				}}
				onValueSelect={(_name, iterationType) =>
					onOptionsSelect(
						iterationType === SPECIFIC_NUMBER_OPTION
							? {iterationNumber: 1, iterationType}
							: {iterationType}
					)
				}
				value={iterationType || FIRST_OPTION}
			/>

			{iterationType === SPECIFIC_NUMBER_OPTION ? (
				<>
					<TextField
						field={{
							label: Liferay.Language.get('iteration-number'),
							typeOptions: {
								validation: {
									min: 1,
									type: 'number',
								},
							},
						}}
						onValueSelect={(_, iterationNumber) =>
							onOptionsSelect({
								iterationNumber,
								iterationType,
							})
						}
						value={iterationNumber || 1}
					/>

					<p className="d-flex text-info">
						<div
							style={{
								minWidth: 'auto',
							}}
						>
							<ClayIcon
								className="mr-2"
								symbol="info-circle-open"
							/>
						</div>

						<span className="text-3">
							{Liferay.Language.get(
								'the-field-to-be-mapped-will-be-empty-if-the-selected-iteration-number-does-not-exist'
							)}
						</span>
					</p>
				</>
			) : null}
		</>
	);
}
