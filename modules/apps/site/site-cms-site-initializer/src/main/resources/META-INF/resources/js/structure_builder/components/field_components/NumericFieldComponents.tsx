/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayCheckbox} from '@clayui/form';
import React from 'react';

import {useSelector, useStateDispatch} from '../../contexts/StateContext';
import selectPublishedFields from '../../selectors/selectPublishedFields';
import {Field, NumericField} from '../../utils/field';

export default function getNumericFieldComponents(): {
	FirstSectionComponent?: React.FC<{disabled?: boolean; field: Field}>;
	SecondSectionComponent?: React.FC<{disabled?: boolean; field: Field}>;
} {
	return {
		SecondSectionComponent,
	};
}

function SecondSectionComponent({
	disabled,
	field,
}: {
	disabled?: boolean;
	field: Field;
}) {
	const numericField = field as NumericField;

	const dispatch = useStateDispatch();
	const publishedFields = useSelector(selectPublishedFields);

	const isPublished = publishedFields.has(field.uuid);

	return (
		<ClayForm.Group className="mb-3">
			<ClayCheckbox
				checked={numericField.settings.uniqueValues || false}
				disabled={disabled || isPublished}
				label={Liferay.Language.get('accept-unique-values-only')}
				onChange={(event) => {
					dispatch({
						settings: {
							...numericField.settings,
							uniqueValues: event.target.checked,
						},
						type: 'update-field',
						uuid: field.uuid,
					});
				}}
			/>
		</ClayForm.Group>
	);
}
