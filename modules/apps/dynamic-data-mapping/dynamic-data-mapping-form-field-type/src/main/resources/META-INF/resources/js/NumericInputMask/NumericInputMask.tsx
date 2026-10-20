/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import React, {FocusEventHandler, useEffect, useMemo, useState} from 'react';

// @ts-ignore

import Radio from '../Radio/Radio.es';

// @ts-ignore

import Select from '../Select/Select';

// @ts-ignore

import Text from '../Text/Text.es';
import {limitValue} from '../util/numericalOperations';

import type {FieldChangeEventHandler, Locale, LocalizedValue} from '../types';

type DecimalSymbol = ',' | '.';
type ThousandsSeparator = DecimalSymbol | ' ' | "'" | 'none';

const DEFAULT_DECIMAL_PLACES = 2;
const MAX_DECIMAL_PLACES = 10;
const MIN_DECIMAL_PLACES = 1;

interface INumericInputMaskValue {
	append?: string;
	appendType?: 'prefix' | 'suffix';
	decimalSymbol?: DecimalSymbol[];
	symbols: LocalizedValue<ISymbols>;
	thousandsSeparator?: ThousandsSeparator[];
}
interface IProps {
	append?: string;
	appendType?: 'prefix' | 'suffix';
	decimalPlaces: number;
	decimalSymbol: DecimalSymbol[] | DecimalSymbol;
	decimalSymbols: ISelectProps<DecimalSymbol>[];
	defaultLanguageId: Locale;
	editingLanguageId: Locale;
	errorMessage: string;
	ffDecimalPlacesSettingsEnabled: boolean;
	fieldName: string;
	onBlur: FocusEventHandler<HTMLInputElement>;
	onChange: FieldChangeEventHandler<unknown>;
	onFocus: FocusEventHandler<HTMLInputElement>;
	readOnly: boolean;
	required: boolean;
	thousandsSeparator: ThousandsSeparator[] | ThousandsSeparator;
	thousandsSeparators: ISelectProps<ThousandsSeparator>[];
	value: INumericInputMaskValue;
	visible: boolean;
}

export interface ISymbols {
	decimalSymbol: DecimalSymbol;
	thousandsSeparator?: ThousandsSeparator | null;
}

interface ISelectProps<T> {
	disabled: boolean;
	label: LocalizedValue<string>;
	reference: string;
	value: T;
}

const NumericInputMask: React.FC<
	{children?: React.ReactNode | undefined} & IProps
> = ({
	append: appendInitial,
	appendType: appendTypeInitial,
	decimalPlaces: decimalPlacesInitial,
	decimalSymbol: decimalSymbolInitial,
	decimalSymbols: decimalSymbolsProp,
	editingLanguageId,
	errorMessage,
	fieldName,
	onBlur,
	onChange,
	onFocus,
	readOnly,
	required,
	thousandsSeparator: thousandsSeparatorInitial,
	thousandsSeparators: thousandsSeparatorsProp,
	value,
	visible,
}) => {
	const [thousandsSeparator, setThousandsSeparator] = useState(
		thousandsSeparatorInitial
	);
	const [currentDecimalPlaces, setCurrentDecimalPlaces] =
		useState(decimalPlacesInitial);
	const [decimalPlaces, setDecimalPlaces] = useState(decimalPlacesInitial);
	const [decimalSymbol, setDecimalSymbol] = useState(decimalSymbolInitial);
	const [append, setAppend] = useState(appendInitial);
	const [appendType, setAppendType] = useState(appendTypeInitial);

	const decimalSymbols = useMemo(() => {
		return decimalSymbolsProp.map((item) => {
			return {
				...item,
				disabled: item.reference === thousandsSeparator?.[0],
			};
		});
	}, [decimalSymbolsProp, thousandsSeparator]);

	const thousandsSeparators = useMemo(() => {
		return thousandsSeparatorsProp.map((item) => {
			return {
				...item,
				disabled: item.reference === decimalSymbol?.[0],
				label:
					item.label?.[Liferay.ThemeDisplay.getLanguageId()] ??
					item.label,
			};
		});
	}, [decimalSymbol, thousandsSeparatorsProp]);

	useEffect(() => {
		const newValue =
			typeof value === 'string' ? JSON.parse(value) : {...value};

		setAppend(newValue.append ?? append);

		setAppendType(newValue.appendType ?? appendType);

		setDecimalPlaces(newValue.decimalPlaces ?? decimalPlaces);

		const symbols = newValue.symbols;

		setDecimalSymbol(symbols?.decimalSymbol ?? decimalSymbol);

		setThousandsSeparator(
			symbols?.thousandsSeparator ?? thousandsSeparator
		);
	}, [
		append,
		appendType,
		decimalPlaces,
		decimalSymbol,
		editingLanguageId,
		thousandsSeparator,
		value,
	]);

	const handleChange = (key: string, value: string | number | ISymbols) => {
		onChange({
			target: {
				value: {
					append,
					appendType,
					decimalPlaces,
					symbols: {
						decimalSymbol,
						thousandsSeparator,
					},

					// eslint-disable-next-line sort-keys
					[key]: value,
				},
			},
		});
	};

	return (
		<>
			<div className="align-items-end d-flex position-relative">
				<div className="pr-2 w-50">
					<Select
						defaultLanguageId={editingLanguageId}
						errorMessage={errorMessage}
						fieldName={fieldName}
						label={Liferay.Language.get('thousands-separator')}
						name="thousandsSeparator"
						onChange={(_: any, value: ThousandsSeparator[]) => {
							handleChange('symbols', {
								decimalSymbol: (
									decimalSymbol as DecimalSymbol[]
								)?.[0],
								thousandsSeparator: value[0],
							});

							setThousandsSeparator(value[0]);
						}}
						options={thousandsSeparators}
						readOnly={readOnly}
						required={required}
						selectedKey={
							thousandsSeparator === '.'
								? '$.2'
								: (thousandsSeparator as string)
						}
						showEmptyOption={false}
						value={thousandsSeparator}
						visible={visible}
					/>
				</div>

				<div className="pl-2 w-50">
					<Select
						defaultLanguageId={editingLanguageId}
						errorMessage={errorMessage}
						fieldName={fieldName}
						label={Liferay.Language.get('decimal-separator')}
						name="decimalSymbol"
						onChange={(_: any, value: DecimalSymbol[]) => {
							handleChange('symbols', {
								decimalSymbol: value[0],
								thousandsSeparator:
									(thousandsSeparator?.includes('none')
										? 'none'
										: thousandsSeparator[0]) as ThousandsSeparator,
							});

							setDecimalSymbol(value[0]);
						}}
						options={decimalSymbols}
						readOnly={readOnly}
						required={required}
						selectedKey={
							decimalSymbol === '.'
								? '$.0'
								: (decimalSymbol as string)
						}
						showEmptyOption={false}
						value={[]}
						visible={visible}
					/>
				</div>
			</div>
			{visible && (
				<div>
					<div className="form-group">
						<label htmlFor="decimal_places ">
							{Liferay.Language.get('decimal-places')}
						</label>

						<ClayInput
							className="ddm-field-text"
							disabled={readOnly}
							id="decimal_places"
							max={MAX_DECIMAL_PLACES}
							min={MIN_DECIMAL_PLACES}
							name="decimal_places"
							onBlur={(event: any) => {
								let {value: newValue} = event.target;

								newValue = limitValue({
									defaultValue: DEFAULT_DECIMAL_PLACES,
									max: MAX_DECIMAL_PLACES,
									min: MIN_DECIMAL_PLACES,
									value: newValue,
								});

								setDecimalPlaces(newValue);
								setCurrentDecimalPlaces(newValue);
								handleChange(
									'decimalPlaces',
									parseInt(newValue, 10)
								);
							}}
							onChange={(event: any) => {
								let {value: newValue} = event.target;
								newValue = newValue.includes('-')
									? newValue.replace('-', '')
									: newValue;

								if (
									newValue.length > 2 ||
									newValue > MAX_DECIMAL_PLACES ||
									newValue === '0'
								) {
									return;
								}

								if (newValue === '') {
									setDecimalPlaces(DEFAULT_DECIMAL_PLACES);

									handleChange(
										'decimalPlaces',
										DEFAULT_DECIMAL_PLACES
									);
								}
								else {
									newValue = parseInt(newValue, 10);

									setDecimalPlaces(newValue);

									handleChange('decimalPlaces', newValue);
								}

								setCurrentDecimalPlaces(newValue);
							}}
							type="number"
							value={currentDecimalPlaces}
						/>
					</div>
				</div>
			)}
			<Text
				label={Liferay.Language.get('prefix-or-suffix')}
				locale={editingLanguageId}
				maxLength={10}
				name="append"
				onBlur={onBlur}
				onChange={(event: any) => {
					handleChange('append', event.target.value);

					setAppend(event.target.value);
				}}
				onFocus={onFocus}
				placeholder={Liferay.Language.get(
					'input-mask-append-placeholder'
				)}
				readOnly={readOnly}
				required={false}
				tip={Liferay.Language.get(
					'the-maximum-length-is-10-characters'
				)}
				value={append}
				visible={visible}
			/>
			{append !== '' && (
				<Radio
					editingLanguageId={editingLanguageId}
					inline={false}
					name="appendType"
					onBlur={onBlur}
					onChange={(event: any) => {
						handleChange('appendType', event.target.value);

						setAppendType(event.target.value);
					}}
					onFocus={onFocus}
					options={[
						{
							label: Liferay.Language.get('prefix[string]'),
							value: 'prefix',
						},
						{
							label: Liferay.Language.get('suffix[value]'),
							value: 'suffix',
						},
					]}
					readOnly={readOnly}
					value={appendType}
					visible={visible}
				/>
			)}
		</>
	);
};

export default NumericInputMask;
