/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type TTrendOptions = {
	color?: string;
	icon: string;
	label: string;
	percentage: number;
	status: number;
	useSpritemap?: boolean;
};

export const ROOM_TREND_OPTIONS: TTrendOptions[] = [
	{
		color: '#4B9FFF',
		icon: 'snow',
		label: Liferay.Language.get('cold'),
		percentage: 12.5,
		status: 0,
		useSpritemap: true,
	},
	{
		color: '#FFBB00',
		icon: 'sun',
		label: Liferay.Language.get('warming-up'),
		percentage: 37.5,
		status: 1,
	},
	{
		color: '#FF8133',
		icon: 'heating',
		label: Liferay.Language.get('heating-up'),
		percentage: 50,
		status: 2,
		useSpritemap: true,
	},
	{
		color: '#6CE0CC',
		icon: 'comments',
		label: Liferay.Language.get('engaged'),
		percentage: 62.5,
		status: 3,
	},
	{
		color: '#FF4F45',
		icon: 'hot',
		label: Liferay.Language.get('hot'),
		percentage: 75,
		status: 4,
		useSpritemap: true,
	},
	{
		color: '#5ACA75',
		icon: 'shield-check',
		label: Liferay.Language.get('ready-to-close'),
		percentage: 87.5,
		status: 5,
	},
	{
		color: '#AA33FF',
		icon: 'champion-cup',
		label: Liferay.Language.get('closed-won'),
		percentage: 100,
		status: 6,
		useSpritemap: true,
	},
	{
		color: '#DA1414',
		icon: 'times-circle-full',
		label: Liferay.Language.get('closed-lost'),
		percentage: 0,
		status: 7,
	},
	{
		icon: 'reload',
		label: Liferay.Language.get('reignited'),
		percentage: 25,
		status: 8,
		useSpritemap: true,
	},
];

export function getImage(filename: string) {
	return `${Liferay.ThemeDisplay.getPortalURL()}${Liferay.ThemeDisplay.getPathContext()}/o/digital-sales-room-web/images/${filename}`;
}

export function getRoomTrendOption(status: number) {
	return (
		ROOM_TREND_OPTIONS.find((option) => option.status === status) ||
		ROOM_TREND_OPTIONS[0]
	);
}
