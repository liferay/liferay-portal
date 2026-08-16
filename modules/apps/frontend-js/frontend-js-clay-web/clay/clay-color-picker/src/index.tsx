/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDropDown from '@clayui/drop-down';
import {ClayInput} from '@clayui/form';
import {
	FocusScope,
	InternalDispatch,
	sub,
	useControlledState,
} from '@clayui/shared';
import React, {useEffect, useMemo, useRef, useState} from 'react';
import tinycolor from 'tinycolor2';

import Basic from './Basic';
import Custom from './Custom';
import {Editor, useEditor} from './Editor';
import Splotch from './Splotch';
import {findColorIndex, getCSSVariableColor} from './util';

const DEFAULT_COLORS = [
	'000000',
	'5F5F5F',
	'9A9A9A',
	'CBCBCB',
	'E1E1E1',
	'FFFFFF',
	'FF0D0D',
	'FF8A1C',
	'2BA676',
	'006EF8',
	'7F26FF',
	'FF21A0',
	'FF5F5F',
	'FFB46E',
	'50D2A0',
	'4B9BFF',
	'AF78FF',
	'FF73C3',
	'FFB1B1',
	'FFDEC0',
	'91E3C3',
	'9DC8FF',
	'DFCAFF',
	'FFC5E6',
	'FFD9D9',
	'FFF3E8',
	'B1EBD5',
	'C5DFFF',
	'F8F2FF',
	'FFEDF7',
];

const BLANK_COLORS = Array(12).fill('FFFFFF');

const DEFAULT_ARIA_LABELS = {
	saturationAndBrightness: 'Saturation and brightness',
	saturationAndBrightnessIs: 'Saturation {0}%, brightness {1}%',
	selectColor: 'Select a color',
	selectionIs: 'Color selection is {0}',
};

interface IProps
	extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'onChange'> {

	/**
	 * Property to define whether the DropDown menu is expanded or not
	 * (controlled).
	 */
	active?: boolean;

	/**
	 * Labels for the aria attributes
	 */
	ariaLabels?: {
		saturationAndBrightness?: string;
		saturationAndBrightnessIs?: string;
		selectColor: string;
		selectionIs: string;
	};

	/**
	 * List of color hex values
	 */
	colors?: Array<string>;

	/**
	 * Property to set the default value of `active` (uncontrolled).
	 */
	defaultActive?: boolean;

	/**
	 * Property to set the default value (uncontrolled).
	 */
	defaultValue?: string;

	/**
	 * Flag for adding ColorPicker in disabled state
	 */
	disabled?: boolean;

	/**
	 * Props to add to the DropDown container.
	 */
	dropDownContainerProps?: React.ComponentProps<
		typeof ClayDropDown.Menu
	>['containerProps'];

	/**
	 * The label describing the collection of colors in the menu
	 */
	label?: string;

	/**
	 * Messages for the Color Picker.
	 */
	messages?: {
		close: string;
		customColor: string;
	};

	/**
	 * The input attribute for name
	 */
	name?: string;

	/**
	 * Callback function for when active state changes (controlled).
	 */
	onActiveChange?: InternalDispatch<boolean>;

	/**
	 * Callback that is called when the value changes (controlled).
	 */
	onChange?: InternalDispatch<string>;

	/**
	 * Callback for when the list of colors change
	 */
	onColorsChange?: (val: Array<string>) => void;

	/**
	 * Callback for when the selected color changes
	 * @deprecated since v3.51.0 - use `onChange` instead.
	 */
	onValueChange?: (val: string) => void;

	predefinedColors?: Array<string>;

	/**
	 * Determines if the hex input should render
	 */
	showHex?: boolean;

	/**
	 * Flag for showing and disabling the palette of colors.
	 * This defaults to true
	 */
	showPalette?: boolean;

	showPredefinedColorsWithCustom?: boolean;

	/**
	 * Flag to indicate if `input-group-sm` class should
	 * be applied to `ClayInput.Group`
	 */
	small?: boolean;

	/**
	 * The title of the Main Splotch component
	 */
	splotchTitle?: string;

	/**
	 * Path to the location of the spritemap resource.
	 */
	spritemap?: string;

	/**
	 * Title to describe the color picker form element
	 */
	title?: string;

	/**
	 * Determines if the native color picker should be used
	 */
	useNative?: boolean;

	/**
	 * The value property sets the current value (controlled).
	 */
	value?: string;
}

function ColorPicker({
	active,
	ariaLabels = DEFAULT_ARIA_LABELS,
	colors,
	defaultActive = false,
	defaultValue = 'FFFFFF',
	disabled,
	dropDownContainerProps,
	label,
	messages,
	name,
	onActiveChange,
	onChange,
	onColorsChange,
	onValueChange,
	predefinedColors,
	showHex = true,
	showPalette = true,
	showPredefinedColorsWithCustom = false,
	small,
	splotchTitle,
	spritemap,
	title,
	useNative = false,
	value,
	...otherProps
}: IProps) {
	const [internalValue, setValue] = useControlledState({
		defaultName: 'defaultValue',
		defaultValue: defaultValue
			? normalizeValueHex(defaultValue)
			: undefined,
		handleName: 'onChange',
		name: 'value',
		onChange: onChange ?? onValueChange,
		value,
	});
	const color = useMemo(
		() =>
			internalValue!.includes('var(')
				? getCSSVariableColor(internalValue!)
				: tinycolor(internalValue),
		[internalValue]
	);
	const customColors = colors
		? colors.concat(BLANK_COLORS).slice(0, 12)
		: BLANK_COLORS;
	const [state, dispatch] = useEditor(internalValue, color, customColors);
	const [customEditorActive, setCustomEditorActive] = useState(!showPalette);
	const isHex = tinycolor(internalValue).getFormat() === 'hex';
	const internalToHex = (color: any) => {
		if (color.getAlpha() < 1) {
			return color.toHex8().toUpperCase();
		}

		return color.toHex().toUpperCase();
	};
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
	const triggerElementRef = useRef<HTMLDivElement>(null);
	const dropdownContainerRef = useRef<HTMLDivElement>(null);
	const inputRef = useRef<HTMLInputElement>(null);
	const valueInputRef = useRef<HTMLInputElement>(null);
	const splotchRef = useRef<HTMLButtonElement>(null);
	const [internalActive, setInternalActive] = useControlledState({
		defaultName: 'defaultActive',
		defaultValue: defaultActive,
		handleName: 'onActiveChange',
		name: 'active',
		onChange: onActiveChange,
		value: active,
	});
	useEffect(() => {
		if (!internalActive) {
			setCustomEditorActive(false);
		}
	}, [internalActive]);

	return (
		<FocusScope arrowKeysUpDown={false}>
			<div className="clay-color-picker">
				{title && <label>{title}</label>}

				<ClayInput.Group
					className="clay-color"
					ref={triggerElementRef}
					small={small}
				>
					<ClayInput.GroupItem prepend={showHex} shrink>
						{name && (
							<input
								name={name}
								onChange={(event) =>
									useNative
										? setValue(event.target.value)
										: null
								}
								ref={valueInputRef}
								style={{
									height: 0,
									position: 'absolute',
									visibility: 'hidden',
									width: 0,
								}}
								tabIndex={-1}
								type={useNative ? 'color' : 'text'}
								value={
									internalValue
										? `${isHex ? '#' : ''}${internalValue}`
										: ''
								}
							/>
						)}

						<ClayInput.GroupText>
							<Splotch
								aria-label={ariaLabels.selectColor}
								className="dropdown-toggle"
								disabled={disabled}
								onClick={() => {
									{
										if (
											useNative &&
											valueInputRef.current
										) {
											valueInputRef.current.click();
										}
										else {
											setInternalActive(!internalActive);
											if (!showPalette) {
												setCustomEditorActive(
													!customEditorActive
												);
											}
										}
									}
								}}
								ref={splotchRef}
								title={splotchTitle}
								value={internalValue}
							/>
						</ClayInput.GroupText>
					</ClayInput.GroupItem>

					<ClayDropDown.Menu
						active={internalActive}
						alignElementRef={triggerElementRef}
						className="clay-color-dropdown-menu"
						containerProps={dropDownContainerProps}
						deps={[internalActive]}
						onActiveChange={setInternalActive}
						ref={dropdownContainerRef}
						triggerRef={splotchRef}
					>
						{(!onColorsChange ||
							(showPredefinedColorsWithCustom &&
								!customEditorActive)) && (
							<Basic
								colors={
									(showPredefinedColorsWithCustom
										? predefinedColors
										: colors) || DEFAULT_COLORS
								}
								label={label}
								onChange={(newVal) => {
									setValue(newVal);
									setInternalActive(!internalActive);
									if (splotchRef.current) {
										splotchRef.current.focus();
									}
								}}
							/>
						)}

						{onColorsChange && (
							<Custom
								color={color}
								colors={customColors}
								editorActive={customEditorActive}
								label={label}
								messages={messages}
								onChange={(color, hex) => {
									dispatch({
										hex: internalToHex(color),
										hue: color.toHsv().h,
									});
									setValue(hex);
								}}
								onColorsChange={(hex, index) => {
									const newColors = [...customColors];
									newColors[index] = hex;
									onColorsChange(newColors);
								}}
								onEditorActiveChange={setCustomEditorActive}
								onSplotchChange={(splotch) =>
									dispatch({splotch})
								}
								showPalette={showPalette}
								splotch={state.splotch}
								spritemap={spritemap}
							/>
						)}

						{onColorsChange && customEditorActive && (
							<Editor
								ariaLabels={{
									...DEFAULT_ARIA_LABELS,
									...ariaLabels,
								}}
								color={color}
								colors={customColors}
								hex={state.hex}
								hue={state.hue}
								internalToHex={internalToHex}
								onChange={(color, active) => {
									const hex = internalToHex(color);
									if (active) {
										const newColors = [...customColors];
										newColors[state.splotch!] = hex;
										onColorsChange(newColors);
									}
									else {
										dispatch({splotch: undefined});
									}
									setValue(hex);
								}}
								onColorChange={(color) => {
									const hex = internalToHex(color);
									const newColors = [...customColors];
									newColors[state.splotch!] = hex;
									onColorsChange(newColors);
									setValue(hex);
									dispatch({hex});
								}}
								onHexChange={(hex) => dispatch({hex})}
								onHueChange={(hue) => dispatch({hue})}
							/>
						)}
					</ClayDropDown.Menu>

					{showHex && (
						<ClayInput.GroupItem
							append
							className="input-group-item-focusable"
						>
							<ClayInput
								{...otherProps}
								aria-label={sub(ariaLabels.selectionIs, [
									internalValue,
								])}
								disabled={disabled}
								insetBefore
								onBlur={(event) => {
									let value = event.target.value;
									if (otherProps.onBlur) {
										otherProps.onBlur(event);
									}
									if (event.defaultPrevented) {
										return;
									}
									const newColor = tinycolor(value);
									if (newColor.isValid()) {
										const colorFormat =
											newColor.getFormat();
										if (
											colorFormat === 'hex' ||
											colorFormat === 'hex8'
										) {
											value = internalToHex(newColor);
										}
										else if (
											newColor.toString() !== value
										) {
											value = newColor.toString();
										}
									}
									else if (!value.includes('var(')) {
										value = '';
									}
									if (onColorsChange && !internalActive) {
										const index = customColors.findIndex(
											(color) =>
												color.toUpperCase() ===
												value.toUpperCase()
										);
										dispatch({
											splotch:
												index !== -1
													? index
													: undefined,
										});
									}
									setValue(value);
								}}
								onChange={(event) => {
									const value = normalizeValueHex(
										event.target.value
									);
									const color = value.includes('var(')
										? getCSSVariableColor(value)
										: tinycolor(value);
									if (
										onColorsChange &&
										(color.isValid() ||
											value.includes('var('))
									) {
										dispatch({
											hex: internalToHex(color),
											hue: color.toHsv().h,
										});
										if (internalActive) {
											const newColors = [...customColors];
											newColors[state.splotch!] =
												internalToHex(color);
											onColorsChange(newColors);
										}
										else {
											const colorIndex = findColorIndex(
												customColors,
												value!.includes('var(')
													? getCSSVariableColor(
															value!
														)
													: tinycolor(value)
											);
											if (colorIndex === -1) {
												dispatch({splotch: undefined});
											}
										}
									}
									setValue(value);
								}}
								ref={inputRef}
								type="text"
								value={internalValue}
							/>

							<ClayInput.GroupInsetItem before tag="label">
								{isHex ? '#' : ''}
							</ClayInput.GroupInsetItem>
						</ClayInput.GroupItem>
					)}
				</ClayInput.Group>
			</div>
		</FocusScope>
	);
}

function normalizeValueHex(value: string) {
	const isHex = tinycolor(value).getFormat() === 'hex';

	if (isHex && value.indexOf('#') === 0) {
		value = value.slice(1);
	}

	return value;
}

export default ColorPicker;
