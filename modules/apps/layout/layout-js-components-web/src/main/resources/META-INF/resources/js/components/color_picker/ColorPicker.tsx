/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {isComputedColor} from '@clayui/color-picker';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {useIsFirstRender} from '@clayui/shared';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import React, {
	KeyboardEvent,
	useEffect,
	useMemo,
	useRef,
	useState,
} from 'react';

import {
	useDeleteStyleError,
	useSetStyleError,
	useStyleErrors,
} from '../../contexts/StyleErrorsContext';
import {Color, ColorCategoryMap, Field, Token} from '../../types/ColorPicker';
import ColorPickerField from './ColorPickerField';
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
	const deleteStyleError = useDeleteStyleError();
	const dropdownColorPickerRef = useRef<HTMLButtonElement>(null);
	const isFirstRender = useIsFirstRender();
	const setStyleError = useSetStyleError();
	const styleErrors = useStyleErrors();

	const [activeColorPicker, setActiveColorPicker] = useState(false);
	const [clearedValue, setClearedValue] = useState(false);
	const [color, setColor] = usePropsFirst(
		tokenValues[value]?.value || value || defaultTokenValue,
		{forceProp: clearedValue}
	);
	const [customColors, setCustomColors] = useState([value || '']);
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

		const hexValue = normalizeHexColor(value);

		if (hexValue.toLowerCase() === target.value.toLowerCase()) {
			return;
		}

		if (!target.value) {
			setColor(hexValue);

			return;
		}

		if (target.value !== hexValue) {
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

	const onChangeInput = (color: string) => {
		if (error.value) {
			setError({label: null, value: null});
			deleteStyleError(field.name, activeItemId);
		}

		setColor(isComputedColor(color) ? color : `#${color}`);
	};

	const onKeyDownInput = (event: KeyboardEvent<HTMLInputElement>) => {
		if (event.key === 'Enter') {
			onBlurInput({target: event.currentTarget});
		}
	};

	useEffect(() => {
		if (!isFirstRender && tokenLabel) {
			dropdownColorPickerRef.current?.focus();
		}
	}, [tokenLabel, isFirstRender]);

	return (
		<ClayForm.Group aria-label={field.label} small>
			<label className={classNames({'sr-only': !showLabel})}>
				{field.label}
			</label>

			<div
				className={classNames('layout__color-picker rounded', {
					'custom': !tokenLabel,
					'has-error': error.value,
					'hovered': activeColorPicker,
				})}
			>
				<ColorPickerField
					active={activeColorPicker}
					colors={customColors}
					colorsFromStylebook={colors}
					inherited={!value && !!field.inherited}
					onActiveChange={setActiveColorPicker}
					onBlurInput={onBlurInput}
					onChange={onChangeInput}
					onClickColorPalette={({label, name, value}) => {
						onSetValue({label, name, value});

						if (error.value) {
							setError({
								label: null,
								value: null,
							});

							deleteStyleError(field.name);
						}
					}}
					onColorChangeEditor={(color: string) => {
						debouncedOnValueSelect(field.name, color);
					}}
					onColorsChange={setCustomColors}
					onKeyDown={onKeyDownInput}
					tokenLabel={tokenLabel}
					tokenValue={color || defaultTokenValue}
					value={error.value || normalizeHexColor(color)}
				/>

				{tokenLabel && canDetachTokenValues && (
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('detach-style')}
						className="border-0 flex-shrink-0 layout__color-picker__action-button mb-0 ml-1"
						displayType="secondary"
						onClick={() => {
							if (tokenValues[value]) {
								setCustomColors([
									tokenValues[value].value.replace('#', ''),
								]);

								onSetValue({
									value: tokenValues[value].value,
								});
							}
							else {
								setCustomColors(
									defaultTokenValue ? [defaultTokenValue] : []
								);

								onSetValue({value: defaultTokenValue});
							}
						}}
						size="sm"
						symbol="chain-broken"
						title={Liferay.Language.get('detach-style')}
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

function normalizeHexColor(color: string) {
	return color?.startsWith('#')
		? color.replace('#', '').toUpperCase()
		: color;
}
