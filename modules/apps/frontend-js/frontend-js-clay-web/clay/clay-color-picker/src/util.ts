/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import tinycolor, {Instance as ColorInstance} from 'tinycolor2';

/**
 * Utility function for getting x & y coordinates for gradient
 */ export function colorToXY(color: tinycolor.Instance, node: HTMLElement) {
	const rect = node.getBoundingClientRect();

	const {s, v} = color.toHsv();

	const x = Math.round((s * 100 * rect.width) / 100);
	const y = Math.round(((v * 100 - 100) * -1 * rect.height) / 100);

	return {x, y};
}

/**
 * Utility function for getting the x position from hue
 */ export function hueToX(hue: number, node: HTMLElement) {
	if (Number.isNaN(hue)) {
		return 0;
	}

	return (hue / 360) * node.getBoundingClientRect().width;
}

/**
 * Utility function for getting hue from the x position
 */ export function xToHue(x: number, node: HTMLElement) {
	if (Number.isNaN(x)) {
		return 0;
	}

	return (x / node.getBoundingClientRect().width) * 360;
}

/**
 * Utility function for getting saturation from the x position
 */
export function xToSaturation(x: number, node: HTMLElement) {
	return Math.round((x * 100) / node.getBoundingClientRect().width);
}

/**
 * Utility function for getting visibility from the y position
 */
export function yToVisibility(y: number, node: HTMLElement) {
	return Math.round(-((y * 100) / node.getBoundingClientRect().height) + 100);
}
export function findColorIndex(
	colors: Array<string>,
	color: tinycolor.Instance
) {
	return colors.findIndex((currentColor) =>
		tinycolor.equals(
			isComputedColor(currentColor)
				? getCSSVariableColor(currentColor)
				: tinycolor(currentColor),
			color
		)
	);
}

export function internalToHex(color: ColorInstance) {
	if (color.getAlpha() < 1) {
		return color.toHex8().toUpperCase();
	}

	return color.toHex().toUpperCase();
}

export function isHexFormat(color: ColorInstance) {
	const format = color.getFormat();

	return format === 'hex' || format === 'hex8';
}

export function isComputedColor(value: string) {
	const lowerCaseValue = value.toLowerCase();

	return (
		lowerCaseValue.includes('var(') ||
		lowerCaseValue.includes('light-dark(')
	);
}

export function getCSSVariableColor(value: string) {
	const element = document.createElement('div');

	element.setAttribute('style', `background: ${value};`);

	document.body.appendChild(element);

	const color = tinycolor(getComputedStyle(element).backgroundColor);

	document.body.removeChild(element);

	return color;
}

export function parseColor(value: string) {
	return tinycolor(value);
}

export function toHexColorString({
	isHex,
	value = '',
}: {
	isHex: boolean;
	value?: string;
}) {
	return `${isHex ? '#' : ''}${value}`;
}
