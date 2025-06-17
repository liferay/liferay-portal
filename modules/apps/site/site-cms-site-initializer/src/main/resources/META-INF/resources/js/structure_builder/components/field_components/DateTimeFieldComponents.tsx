/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {useId} from 'frontend-js-components-web';
import React from 'react';

import {useSelector, useStateDispatch} from '../../contexts/StateContext';
import selectPublishedFields from '../../selectors/selectPublishedFields';
import {DateTimeField, Field} from '../../utils/field';

const TIME_STORAGE_OPTIONS = [
	{
		label: Liferay.Language.get('convert-to-utc'),
		value: 'convertToUTC',
	},
	{
		label: Liferay.Language.get('use-input-as-entered'),
		value: 'useInputAsEntered',
	},
];

export default function getDateTimeFieldComponents(): {
	FirstSectionComponent?: React.FC<{disabled?: boolean; field: Field}>;
	SecondSectionComponent?: React.FC<{disabled?: boolean; field: Field}>;
} {
	return {
		FirstSectionComponent,
	};
}

function FirstSectionComponent({
	disabled,
	field,
}: {
	disabled?: boolean;
	field: Field;
}) {
	const dateTimeField = field as DateTimeField;

	const dispatch = useStateDispatch();
	const publishedFields = useSelector(selectPublishedFields);

	const isPublished = publishedFields.has(field.uuid);

	const id = useId();

	return (
		<ClayForm.Group className="mb-3">
			<label htmlFor={id}>
				{Liferay.Language.get('time-storage')}

				<ClayIcon
					className="ml-1 reference-mark"
					focusable="false"
					role="presentation"
					symbol="asterisk"
				/>

				<ClayIcon
					className="lfr-portal-tooltip ml-1 text-secondary"
					data-title={Liferay.Language.get(
						'store-the-time-in-utc-for-time-zone-conversion-or-keep-it-as-entered'
					)}
					focusable="false"
					symbol="question-circle"
				/>
			</label>

			<Picker
				aria-label={Liferay.Language.get('time-storage')}
				disabled={disabled || isPublished}
				id={id}
				items={TIME_STORAGE_OPTIONS}
				onSelectionChange={(timeStorage: React.Key) => {
					dispatch({
						settings: {
							timeStorage,
						},
						type: 'update-field',
						uuid: field.uuid,
					});
				}}
				selectedKey={dateTimeField.settings.timeStorage}
			>
				{(item) => <Option key={item.value}>{item.label}</Option>}
			</Picker>
		</ClayForm.Group>
	);
}
