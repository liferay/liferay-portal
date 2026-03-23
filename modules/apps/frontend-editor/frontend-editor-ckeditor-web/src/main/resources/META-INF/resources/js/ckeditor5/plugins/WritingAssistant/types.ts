/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export enum EActionType {
	CHANGE_TONE = 'Change Tone',
	FIX_SPELLING_AND_GRAMMAR = 'Fix Spelling and Grammar',
	GENERATE_BASED_ON_TITLE = 'Generate Based On Title',
	IMPROVE_WRITING = 'Improve Writing',
	MAKE_LONGER = 'Make Longer',
	MAKE_SHORTER = 'Make Shorter',
	TRANSLATE_TO = 'Translate To',
}

export interface IAction {
	disabled?: boolean;
	name: string;
	symbolLeft?: string;
	symbolRight?: string;
	type: EActionType;
}

export interface IActionGroup {
	children: IAction[];
	name: string;
	type?: 'divider';
}
