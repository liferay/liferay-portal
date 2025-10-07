/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const HEX_LENGTH_6 = 6;
const MAX_HEX_LENGTH = 8;
const REGEX_HEX = /^([0-9A-F]{3}|[0-9A-F]{4}|[0-9A-F]{6}|[0-9A-F]{8})$/i;

export default function getValidHexColor(value: string) {
	let hexColor = value.replace('#', '').substring(0, MAX_HEX_LENGTH);

	if (hexColor.length === 7) {
		hexColor = hexColor.substring(0, HEX_LENGTH_6);
	}

	const isValid = REGEX_HEX.test(hexColor);

	if (isValid) {
		return `#${
			value.length < HEX_LENGTH_6
				? hexColor
						.split('')
						.map((hex) => hex + hex)
						.join('')
				: hexColor
		}`;
	}

	return '';
}
