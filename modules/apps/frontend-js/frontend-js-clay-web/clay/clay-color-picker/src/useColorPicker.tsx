/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useControlledState} from '@clayui/shared';
import React, {useEffect, useMemo, useRef, useState} from 'react';
import tinycolor, {Instance} from 'tinycolor2';

import {useEditor} from './Editor';
import {findColorIndex, getCSSVariableColor, isComputedColor} from './util';

const BLANK_COLORS = Array(12).fill('FFFFFF');

export default function useColorPicker({
	active,
	colors,
	defaultActive = false,
	defaultValue = 'FFFFFF',
	externalOnBlur,
	onActiveChange,
	onChange,
	onColorsChange,
	onValueChange,
	showPalette = false,
	useNative = false,
	value,
}: {
	active?: boolean;
	colors?: Array<string>;
	defaultActive?: boolean;
	defaultValue?: string;
	externalOnBlur?: React.FocusEventHandler<HTMLInputElement>;
	onActiveChange?: (active: boolean) => void;
	onChange?: (val: string) => void;
	onColorsChange?: (val: Array<string>) => void;
	onValueChange?: (val: string) => void;
	showPalette?: boolean;
	useNative?: boolean;
	value?: string;
}) {
	const customColors = colors
		? colors.concat(BLANK_COLORS).slice(0, 12)
		: BLANK_COLORS;

	const [customEditorActive, setCustomEditorActive] =
		useState<boolean>(!showPalette);
	const [internalActive, setInternalActive] = useControlledState<boolean>({
		defaultName: 'defaultActive',
		defaultValue: defaultActive,
		handleName: 'onActiveChange',
		name: 'active',
		onChange: onActiveChange,
		value: active,
	});
	const [internalValue, setValue] = useControlledState<string>({
		defaultName: 'defaultValue',
		defaultValue: defaultValue
			? normalizeValueHex(defaultValue)
			: undefined,
		handleName: 'onChange',
		name: 'value',
		onChange: onChange ?? onValueChange,
		value,
	});

	useEffect(() => {
		if (!internalActive) {
			setCustomEditorActive(false);
		}
	}, [internalActive]);

	const color = useMemo(
		() =>
			internalValue && isComputedColor(internalValue)
				? getCSSVariableColor(internalValue)
				: tinycolor(internalValue),
		[internalValue]
	);

	const [state, dispatch] = useEditor(internalValue, color, customColors);

	const valueInputRef = useRef<HTMLInputElement>(null);

	const inputColorTypeSupport = useMemo(() => {
		if (typeof document !== 'undefined') {
			const input = document.createElement('input');
			input.setAttribute('type', 'color');

			return input.value !== '';
		}

		return true;
	}, []);
	if (!inputColorTypeSupport) {
		useNative = false;
	}

	const internalToHex = (color: any) => {
		if (color.getAlpha() < 1) {
			return color.toHex8().toUpperCase();
		}

		return color.toHex().toUpperCase();
	};

	const onChangeEditor = (color: Instance, active: boolean) => {
		const hex = internalToHex(color);

		if (active) {
			const newColors = [...customColors];

			newColors[state.splotch!] = hex;

			onColorsChange?.(newColors);
		}
		else {
			dispatch({splotch: undefined});
		}

		setValue(hex);
	};

	const onSplotchClick = () => {
		{
			if (useNative && valueInputRef.current) {
				valueInputRef.current.click();
			}
			else {
				setInternalActive(!internalActive);

				if (!showPalette) {
					setCustomEditorActive((active) => !active);
				}
			}
		}
	};

	const onColorChangeEditor = (color: Instance) => {
		const hex = internalToHex(color);
		const newColors = [...customColors];

		newColors[state.splotch!] = hex;

		onColorsChange?.(newColors);

		setValue(hex);

		dispatch({hex});
	};

	const onHexBlur = (event: React.FocusEvent<HTMLInputElement>) => {
		let value = event.target.value;

		if (externalOnBlur) {
			externalOnBlur(event);
		}
		if (event.defaultPrevented) {
			return;
		}
		const newColor = tinycolor(value);

		if (newColor.isValid()) {
			const colorFormat = newColor.getFormat();

			if (colorFormat === 'hex' || colorFormat === 'hex8') {
				value = internalToHex(newColor);
			}
			else if (newColor.toString() !== value) {
				value = newColor.toString();
			}
		}
		else if (!isComputedColor(value)) {
			value = '';
		}
		if (onColorsChange && !internalActive) {
			const index = customColors.findIndex(
				(color: string) => color.toUpperCase() === value.toUpperCase()
			);

			dispatch({
				splotch: index !== -1 ? index : undefined,
			});
		}

		setValue(value);
	};

	const onHexChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		const value = normalizeValueHex(event.target.value);
		const color = isComputedColor(value)
			? getCSSVariableColor(value)
			: tinycolor(value);

		if (onColorsChange && (color.isValid() || isComputedColor(value))) {
			dispatch({
				hex: internalToHex(color),
				hue: color.toHsv().h,
			});

			if (internalActive) {
				const newColors = [...customColors];

				newColors[state.splotch!] = internalToHex(color);

				onColorsChange(newColors);
			}
			else {
				const colorIndex = findColorIndex(
					customColors,
					isComputedColor(value!)
						? getCSSVariableColor(value!)
						: tinycolor(value)
				);

				if (colorIndex === -1) {
					dispatch({splotch: undefined});
				}
			}
		}
		setValue(value);
	};

	return {
		color,
		customColors,
		customEditorActive,
		dispatch,
		internalActive,
		internalToHex,
		internalValue,
		onChangeEditor,
		onColorChangeEditor,
		onHexBlur,
		onHexChange,
		onSplotchClick,
		setCustomEditorActive,
		setInternalActive,
		setValue,
		state,
		valueInputRef,
	};
}

function normalizeValueHex(value: string) {
	const isHex = tinycolor(value).getFormat() === 'hex';

	if (isHex && value.indexOf('#') === 0) {
		value = value.slice(1);
	}

	return value;
}
