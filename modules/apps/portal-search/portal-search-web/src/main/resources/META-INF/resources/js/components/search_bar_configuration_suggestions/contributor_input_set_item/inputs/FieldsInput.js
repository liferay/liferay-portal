/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayMultiSelect from '@clayui/multi-select';
import {ClayTooltipProvider} from '@clayui/tooltip';
import getCN from 'classnames';
import React, {useEffect, useState} from 'react';

function FieldsInput({
	index,
	onChange,
	isRequired = false,
	fields = [],
	touched,
	onBlur,
}) {
	const [value, setValue] = useState('');
	const [items, setItems] = useState(
		fields.map((field) => ({
			label: field,
			value: field,
		}))
	);

	const _handleBlur = () => {
		if (value) {
			setItems([
				...items,
				{
					label: value,
					value,
				},
			]);

			setValue('');
		}

		onBlur();
	};

	/**
	 * Apply useEffect to perform `onChange` because `attributes` might not be
	 * the most up-to-date inside `onChange` when it is passed into a function
	 * for `onItemsChange`.
	 */
	useEffect(() => {
		onChange(items.map((item) => item.value));
	}, [items]); //eslint-disable-line

	return (
		<ClayInput.GroupItem
			className={getCN({
				'has-error': isRequired && !items.length && touched,
			})}
		>
			<label htmlFor={`fields${index}`}>
				{Liferay.Language.get('fields')}

				<ClayTooltipProvider>
					<span
						className="c-ml-2"
						data-tooltip-align="top"
						tabIndex={0}
						title={Liferay.Language.get('fields-suggestion-help')}
					>
						<ClayIcon symbol="question-circle-full" />
					</span>
				</ClayTooltipProvider>
			</label>

			<ClayMultiSelect
				aria-label={Liferay.Language.get('fields')}
				id={`fields${index}`}
				items={items}
				onBlur={_handleBlur}
				onChange={setValue}
				onItemsChange={setItems}
				value={value}
			/>
		</ClayInput.GroupItem>
	);
}

export default FieldsInput;
