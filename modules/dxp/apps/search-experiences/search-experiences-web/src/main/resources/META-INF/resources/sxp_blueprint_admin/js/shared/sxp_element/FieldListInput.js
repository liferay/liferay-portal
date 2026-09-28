/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import React from 'react';

import FieldRow from './FieldRow';

function FieldListInput({
	disabled,
	id,
	indexFields,
	name,
	setFieldTouched,
	setFieldValue,
	showBoost,
	value,
}) {
	const _handleBlur = () => {
		setFieldTouched(name);
	};

	const _handleChange = (index) => (newValue) => {
		setFieldValue(`${name}[${index}]`, {...value[index], ...newValue});
	};

	const _handleFieldRowAdd = () => {
		setFieldValue(name, [...value, {field: ''}]);
	};

	const _handleFieldRowDelete = (index) => () => {
		_handleBlur();

		setFieldValue(
			name,
			value.filter((_, i) => index !== i)
		);
	};

	return (
		<div className="field">
			{value?.map((item, index) => (
				<FieldRow
					boost={item.boost}
					disabled={disabled}
					field={item.field}
					id={index === 0 ? id : `${id}_${index}`}
					index={index}
					indexFields={indexFields}
					key={index}
					languageIdPosition={item.languageIdPosition}
					locale={item.locale}
					onBlur={_handleBlur}
					onChange={_handleChange(index)}
					onDelete={_handleFieldRowDelete(index)}
					showBoost={showBoost}
				/>
			))}

			<ClayForm.Group className="add-remove-field">
				<ClayButton.Group spaced>
					<ClayButton
						aria-label={Liferay.Language.get('add-field')}
						disabled={disabled}
						displayType="secondary"
						monospaced
						onClick={_handleFieldRowAdd}
						size="sm"
					>
						<ClayIcon symbol="plus" />
					</ClayButton>
				</ClayButton.Group>
			</ClayForm.Group>
		</div>
	);
}

export default React.memo(FieldListInput);
