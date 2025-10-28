/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayForm, {ClayInput} from '@clayui/form';
import classNames from 'classnames';
import {FieldFeedback, RequiredMask} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

export const TYPE_BOOLEAN = 'TYPE_BOOLEAN';
export const TYPE_STRING = 'TYPE_STRING';

const BOOLEAN_VALUE_ITEMS = [
	{label: Liferay.Language.get('true'), value: true},
	{label: Liferay.Language.get('false'), value: false},
];

const TYPE_ITEMS = [
	{label: Liferay.Language.get('string'), value: TYPE_STRING},
	{label: Liferay.Language.get('boolean'), value: TYPE_BOOLEAN},
];

interface IProps {
	disabled?: boolean;
	id: string;
	index: number;
	name: string;
	onAttributeChange: (index: number, updatedValue: Object) => void;
	onErrorChange: (id: string, error: boolean) => void;
	onRemoveClick: (index: number) => void;
	portletNamespace: string;
	type: string;
	value: string | boolean;
}

export default function AttributeFields({
	disabled,
	id,
	index,
	name,
	onAttributeChange,
	onErrorChange,
	onRemoveClick,
	portletNamespace,
	type,
	value,
}: IProps) {
	const nameId = `${portletNamespace}name_${index}`;
	const typeId = `${portletNamespace}type_${index}`;
	const valueId = `${portletNamespace}value_${index}`;
	const fieldFeedbackId = `${portletNamespace}fieldFeedback_${index}`;

	const [errorMessage, setErrorMessage] = useState<string | null>(null);

	const handleErrorChange = (error: string | null) => {
		onErrorChange(id, !!error);
		setErrorMessage(error);
	};

	const validateAttributeName = (value: string) => {
		if (value.toLowerCase() === 'src') {
			handleErrorChange(
				Liferay.Language.get('use-the-javascript-url-field')
			);
		}
		else if (!value.length) {
			handleErrorChange(
				Liferay.Language.get('attribute-field-is-required')
			);
		}
		else {
			handleErrorChange(null);
		}
	};

	return (
		<ClayForm.Group>
			<ClayInput.Group>
				<ClayInput.GroupItem
					className={classNames({'has-error': errorMessage})}
				>
					<label
						className={disabled ? 'disabled' : ''}
						htmlFor={nameId}
					>
						{Liferay.Language.get('attribute')}

						<span className="sr-only">
							{Liferay.Language.get('spaces-are-not-allowed')}
						</span>

						<RequiredMask />
					</label>

					<ClayInput
						aria-describedby={fieldFeedbackId}
						aria-required={true}
						data-testid={`testId_${index}`}
						defaultValue={name}
						disabled={disabled}
						id={nameId}
						onBlur={(event) =>
							validateAttributeName(event.target.value)
						}
						onChange={(event) => {
							const value = event.target.value
								.split(/\s/)
								.join('');

							validateAttributeName(value);

							onAttributeChange(index, {
								name: value,
							});
						}}
						type="text"
						value={name}
					/>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem>
					<label
						className={disabled ? 'disabled' : ''}
						htmlFor={typeId}
					>
						{Liferay.Language.get('type')}
					</label>

					<Picker
						aria-labelledby="picker-label"
						defaultSelectedKey={type}
						disabled={disabled}
						id={typeId}
						items={TYPE_ITEMS}
						messages={{
							itemDescribedby: Liferay.Language.get(
								'you-are-currently-on-a-text-element,-inside-of-a-list-box'
							),
							itemSelected: Liferay.Language.get('x-selected'),
							scrollToBottomAriaLabel:
								Liferay.Language.get('scroll-to-bottom'),
							scrollToTopAriaLabel:
								Liferay.Language.get('scroll-to-top'),
						}}

						// @ts-ignore

						onSelectionChange={(type) =>
							onAttributeChange(index, {
								type,
								value:
									type !== TYPE_BOOLEAN
										? ''
										: BOOLEAN_VALUE_ITEMS[0].value,
							})
						}
					>
						{(item) => (
							<Option key={item.value}>{item.label}</Option>
						)}
					</Picker>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem>
					<label
						className={disabled ? 'disabled' : ''}
						htmlFor={valueId}
					>
						{Liferay.Language.get('value')}
					</label>

					{type !== TYPE_BOOLEAN ? (
						<ClayInput
							disabled={disabled}
							id={valueId}
							onChange={({target}) =>
								onAttributeChange(index, {value: target.value})
							}
							type="text"
							value={value.toString()}
						/>
					) : (
						<Picker
							aria-labelledby="picker-label"
							defaultSelectedKey={value.toString()}
							disabled={disabled}
							id={valueId}
							items={BOOLEAN_VALUE_ITEMS}
							onSelectionChange={(value: any) =>
								onAttributeChange(index, {

									// @ts-ignore

									value: JSON.parse(value),
								})
							}
						>
							{(item) => (
								<Option key={item.value.toString()}>
									{item.label}
								</Option>
							)}
						</Picker>
					)}
				</ClayInput.GroupItem>

				<ClayInput.GroupItem shrink>
					<ClayButtonWithIcon
						aria-label={sub(
							Liferay.Language.get('remove-attribute-x'),
							name
						)}
						borderless
						className="align-self-end"
						disabled={disabled}
						displayType="secondary"
						monospaced
						onClick={() => onRemoveClick(index)}
						symbol="trash"
						title={Liferay.Language.get('remove')}
						type="button"
					/>
				</ClayInput.GroupItem>
			</ClayInput.Group>

			{errorMessage && (
				<FieldFeedback
					className={classNames({'has-error': errorMessage})}
					errorMessage={errorMessage}
					id={fieldFeedbackId}
				/>
			)}
		</ClayForm.Group>
	);
}
