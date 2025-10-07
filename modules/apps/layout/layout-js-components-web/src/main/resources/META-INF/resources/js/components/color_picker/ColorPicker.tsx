/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayColorPicker from '@clayui/color-picker';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import {useId} from 'frontend-js-components-web';
import React, {KeyboardEvent, useMemo, useRef, useState} from 'react';

import {
	useDeleteStyleError,
	useSetStyleError,
	useStyleErrors,
} from '../../contexts/StyleErrorsContext';
import {
	Color,
	ColorCategoryMap,
	DropdownColorPicker,
} from './DropdownColorPicker';
import {parseColorValue} from './parseColorValue';

import './ColorPicker.scss';

export const DEFAULT_TOKEN_LABEL = Liferay.Language.get('inherited');

function usePropsFirst<T>(value: T, {forceProp = false}) {
	const [nextValue, setNextValue] = useState(value);
	const [previousValue, setPreviousValue] = useState(value);

	if (value !== previousValue || (forceProp && nextValue !== value)) {
		setNextValue(value);
		setPreviousValue(value);
	}

	return [nextValue, setNextValue] as const;
}

export interface Token {
	editorType: string;
	label: string;
	name: string;
	tokenCategoryLabel: string;
	tokenSetLabel: string;
	value: string;
}

export interface Field {
	cssProperty?: string;
	dataType?: string;
	defaultValue?: string;
	description?: string;
	icon?: string;
	inherited?: boolean;
	label: string;
	name: string;
	type?: string;
	typeOptions?: {
		showLengthField?: boolean;
	};
	value?: string;
}

interface Props {
	activeItemId?: string;
	canDetachTokenValues?: boolean;
	defaultTokenLabel?: string;
	defaultTokenValue?: string;
	editedTokenValues: Record<string, Token>;
	field: Field;
	onValueSelect: (fieldName: string, value: string) => void;
	restoreButtonLabel?: string;
	showLabel?: boolean;
	tokenValues: Record<string, Token>;
	value: string;
}

export default function ColorPicker({
	activeItemId = '',
	canDetachTokenValues = true,
	defaultTokenLabel = DEFAULT_TOKEN_LABEL,
	defaultTokenValue = '',
	editedTokenValues,
	field,
	onValueSelect,
	showLabel = true,
	tokenValues,
	restoreButtonLabel = Liferay.Language.get('clear-selection'),
	value,
}: Props) {
	const colors: ColorCategoryMap = {};
	const inputId = useId();
	const deleteStyleError = useDeleteStyleError();
	const setStyleError = useSetStyleError();
	const styleErrors = useStyleErrors();

	const [activeDropdownColorPicker, setActiveDropdownColorPicker] =
		useState(false);
	const [activeColorPicker, setActiveColorPicker] = useState(false);
	const [clearedValue, setClearedValue] = useState(false);
	const [color, setColor] = usePropsFirst(
		tokenValues[value]?.value || value || defaultTokenValue,
		{forceProp: clearedValue}
	);
	const colorButtonRef = useRef(null);
	const [customColors, setCustomColors] = useState([value || '']);
	const inputRef = useRef<HTMLInputElement>(null);
	const isMounted = useIsMounted();

	const debouncedOnValueSelect = useMemo(() => {
		let timeoutId: NodeJS.Timeout;

		return (fieldName: string, value: string) => {
			clearTimeout(timeoutId);

			timeoutId = setTimeout(() => {
				if (isMounted()) {
					onValueSelect(fieldName, value);
				}
			}, 300);
		};
	}, [isMounted, onValueSelect]);

	const [error, setError] = useState<{
		label: string | null;
		value: string | null;
	}>({
		label: styleErrors[activeItemId]?.[field.name]?.error,
		value: styleErrors[activeItemId]?.[field.name]?.value,
	});

	const [tokenLabel, setTokenLabel] = usePropsFirst(
		value
			? tokenValues[value]?.label
			: field.inherited
				? defaultTokenLabel
				: null,
		{forceProp: clearedValue}
	);

	const tokenColorValues = Object.values(tokenValues)
		.filter((token) => token.editorType === 'ColorPicker')
		.map((token) => ({
			...token,
			disabled:
				token.name === field.name ||
				editedTokenValues?.[token.name]?.name === field.name,
		}));

	tokenColorValues.forEach(
		({
			disabled,
			label,
			name,
			tokenCategoryLabel: category,
			tokenSetLabel: tokenSet,
			value,
		}) => {
			const color: Color = {disabled, label, name, value};

			if (Object.keys(colors).includes(category)) {
				if (Object.keys(colors[category]).includes(tokenSet)) {
					colors[category][tokenSet].push(color);
				}
				else {
					colors[category][tokenSet] = [color];
				}
			}
			else {
				colors[category] = {[tokenSet]: [color]};
			}
		}
	);

	const onSetValue = ({
		isReset,
		label = null,
		name = null,
		value,
	}: {
		isReset?: boolean;
		label?: string | null;
		name?: string | null;
		value: string;
	}) => {
		setColor(value);
		setTokenLabel(label);
		onValueSelect(field.name, name ?? value);

		if (value === '' && !isReset) {
			setClearedValue(true);
		}
		else {
			setClearedValue(false);
		}
	};

	const onBlurInput = ({target}: {target: HTMLInputElement}) => {
		if (!value) {
			value = defaultTokenValue;
		}

		if (value.toLowerCase() === target.value.toLowerCase()) {
			return;
		}

		if (!target.value) {
			setColor(value);

			return;
		}

		if (target.value !== value) {
			const token = tokenColorValues.find(
				(token) =>
					token.label.toLowerCase() === target.value.toLowerCase()
			);

			const nextValue = parseColorValue({
				editedTokenValues,
				field,
				token,
				value: target.value,
			});

			if ('error' in nextValue) {
				setError({label: nextValue.error, value: target.value});
				setCustomColors(['FFFFFF']);
				setStyleError(
					field.name,
					{
						error: nextValue.error,
						value: target.value,
					},
					activeItemId
				);

				return;
			}

			if ('value' in nextValue) {
				onValueSelect(field.name, nextValue.value || '');
			}

			if ('label' in nextValue) {
				setTokenLabel(nextValue.label || '');
			}

			if ('pickerColor' in nextValue) {
				setCustomColors([nextValue.pickerColor]);
			}

			setColor(nextValue?.color || nextValue?.value || value);
		}
	};

	const onChangeInput = ({target: {value}}: {target: HTMLInputElement}) => {
		if (error.value) {
			setError({label: null, value: null});
			deleteStyleError(field.name, activeItemId);
		}

		setColor(value);
	};

	const onKeyDownInput = (event: KeyboardEvent<HTMLInputElement>) => {
		if (event.key === 'Enter') {
			onBlurInput({target: event.currentTarget});
		}
	};

	return (
		<ClayForm.Group aria-label={field.label} small>
			<label className={classNames({'sr-only': !showLabel})}>
				{field.label}
			</label>

			<div
				className={classNames('layout__color-picker', {
					'custom': !tokenLabel,
					'has-error': error.value,
					'hovered': activeColorPicker || activeDropdownColorPicker,
				})}
			>
				{tokenLabel ? (
					<DropdownColorPicker
						active={activeDropdownColorPicker}
						colors={colors}
						fieldLabel={showLabel ? null : field.label}
						inherited={!value && field.inherited}
						label={tokenLabel}
						onSetActive={setActiveDropdownColorPicker}
						onValueChange={({label, name, value}) =>
							onSetValue({label, name, value})
						}
						small
						value={color || defaultTokenValue}
					/>
				) : (
					<ClayInput.Group>
						<ClayInput.GroupItem
							prepend
							ref={colorButtonRef}
							shrink
						>
							<ClayColorPicker
								active={activeColorPicker}
								colors={customColors}
								dropDownContainerProps={{
									className: 'cadmin',
								}}
								onActiveChange={setActiveColorPicker}
								onChange={(color: String) => {
									debouncedOnValueSelect(
										field.name,
										`#${color}`
									);
									setColor(`#${color}`);

									if (error.value) {
										setError({
											label: null,
											value: null,
										});
										deleteStyleError(field.name);
									}
								}}
								onColorsChange={setCustomColors}
								showHex={false}
								showPalette={false}
								value={
									error.value ? '' : color?.replace('#', '')
								}
							/>
						</ClayInput.GroupItem>

						<ClayInput.GroupItem append>
							<ClayInput
								aria-invalid={Boolean(error.label)}
								aria-label={Liferay.Language.get('color')}
								className="layout__color-picker__input"
								id={inputId}
								onBlur={onBlurInput}
								onChange={onChangeInput}
								onKeyDown={onKeyDownInput}
								ref={inputRef}
								sizing="sm"
								value={
									error.value ||
									(color?.startsWith('#')
										? color.toUpperCase()
										: color)
								}
							/>
						</ClayInput.GroupItem>
					</ClayInput.Group>
				)}

				{tokenLabel ? (
					canDetachTokenValues && (
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('detach-style')}
							className="border-0 flex-shrink-0 layout__color-picker__action-button mb-0 ml-2"
							displayType="secondary"
							onClick={() => {
								if (tokenValues[value]) {
									setCustomColors([
										tokenValues[value].value.replace(
											'#',
											''
										),
									]);

									onSetValue({
										value: tokenValues[value].value,
									});
								}
								else {
									setCustomColors(
										defaultTokenValue
											? [defaultTokenValue]
											: []
									);

									onSetValue({value: defaultTokenValue});
								}
							}}
							size="sm"
							symbol="chain-broken"
							title={Liferay.Language.get('detach-style')}
						/>
					)
				) : (
					<DropdownColorPicker
						active={activeDropdownColorPicker}
						colors={colors}
						fieldLabel={showLabel ? null : field.label}
						onSetActive={setActiveDropdownColorPicker}
						onValueChange={({label, name, value}) => {
							onSetValue({label, name, value});

							if (error.value) {
								setError({
									label: null,
									value: null,
								});
								deleteStyleError(field.name);
							}
						}}
						showSelector={false}
						small
						value={color}
					/>
				)}

				{value ? (
					<ClayButtonWithIcon
						aria-label={restoreButtonLabel}
						className="border-0 flex-shrink-0 layout__color-picker__action-button ml-2"
						displayType="secondary"
						onClick={() => {
							if (
								value.toLowerCase() ===
									field.defaultValue?.toLowerCase() &&
								!error.value
							) {
								return;
							}

							setError({label: null, value: null});

							onSetValue({
								isReset: true,
								label: field.defaultValue
									? null
									: defaultTokenValue,
								value: field.defaultValue ?? '',
							});
						}}
						size="sm"
						symbol="restore"
						title={restoreButtonLabel}
					/>
				) : null}
			</div>

			{error.label ? (
				<div className="mt-2 small text-danger">
					<ClayIcon symbol="exclamation-full" />

					<span className="ml-2">{error.label}</span>
				</div>
			) : null}
		</ClayForm.Group>
	);
}
