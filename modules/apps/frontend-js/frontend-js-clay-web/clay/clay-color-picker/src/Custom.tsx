/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Icon from '@clayui/icon';
import React, {useRef} from 'react';
import tinycolor from 'tinycolor2';

import Splotch from './Splotch';
import {findColorIndex, getCSSVariableColor, isComputedColor} from './util';

type Props = {
	color: tinycolor.Instance;

	/**
	 * List of colors that will display as a color splotch
	 * these can be either hex values, color names or
	 * css variables.
	 */
	colors: Array<string>;

	editorActive: boolean;

	/**
	 * Label describing the set of colors provided
	 */
	label?: string;

	/**
	 * Messages for the Color Picker.
	 */
	messages?: Partial<{
		close: string;
		customColor: string;
	}>;

	/**
	 * Callback for when a color is changed
	 */
	onChange: (color: tinycolor.Instance, value: string) => void;

	/**
	 * Callback for when the list of colors is changed
	 */
	onColorsChange: (hex: string, index: number) => void;

	onEditorActiveChange: (value: boolean) => void;

	onSplotchChange: (value: number) => void;

	/**
	 * Flag for showing and disabling the palette of colors
	 */
	showPalette?: boolean;

	splotch?: number;

	/**
	 * Path to the location of the spritemap resource.
	 */
	spritemap?: string;
};

const DEFAULT_SPLOTCH_COLOR = 'FFFFFF';

const defaultMessages = {
	close: 'close',
	customColor: 'custom color',
};

function ClayColorPickerCustom({
	color,
	colors,
	editorActive,
	label,
	messages = defaultMessages,
	onChange,
	onColorsChange,
	onEditorActiveChange,
	onSplotchChange,
	showPalette,
	splotch,
	spritemap,
}: Props) {
	const previousColorRef = useRef(color);

	return (
		<div>
			{label && (
				<div className="clay-color-header">
					<span className="component-title">{label}</span>

					{showPalette && (
						<button
							aria-label={
								editorActive
									? messages.close
									: messages.customColor
							}
							className={`${editorActive ? 'close' : ''} component-action`}
							onClick={() => onEditorActiveChange(!editorActive)}
							type="button"
						>
							<Icon
								spritemap={spritemap}
								symbol={editorActive ? 'times' : 'drop'}
							/>
						</button>
					)}
				</div>
			)}

			{showPalette && (
				<div className="clay-color-swatch">
					{colors.map((hex, index) => (
						<div className="clay-color-swatch-item" key={index}>
							<Splotch
								active={index === splotch}
								onClick={() => {
									if (splotch !== index) {
										onSplotchChange(index);
									}

									// The hexadecimal color `#FFFFFF` is treated as an empty
									// slot so when the user enters a color that doesn't exist in
									// the custom, clicking on an empty slot will replace that
									// slot with the new color if don't have an active slot
									// being edited.

									if (hex === DEFAULT_SPLOTCH_COLOR) {
										onEditorActiveChange(true);

										// Replaces the slot color with the color entered in the
										// input if it does not have an active slot being edited.

										if (
											previousColorRef.current !==
												tinycolor(
													DEFAULT_SPLOTCH_COLOR
												) &&
											findColorIndex(colors, color) ===
												-1 &&
											typeof splotch === 'undefined'
										) {
											onColorsChange(
												color.toHex(),
												index
											);
											onChange(
												color,
												color.getOriginalInput() as string
											);
										}
										else {
											const newColor = tinycolor(hex);
											onColorsChange(hex, index);
											onChange(newColor, hex);
										}
									}
									else {
										const newColor = isComputedColor(hex!)
											? getCSSVariableColor(hex!)
											: tinycolor(hex);
										previousColorRef.current = newColor;
										onChange(newColor, hex);
									}
								}}
								value={hex}
							/>
						</div>
					))}
				</div>
			)}
		</div>
	);
}

export default ClayColorPickerCustom;
