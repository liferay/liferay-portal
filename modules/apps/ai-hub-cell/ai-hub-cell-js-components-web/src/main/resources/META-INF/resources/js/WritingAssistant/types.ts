/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export enum EActionType {
	CHANGE_TONE = 'L_CHANGE_TONE',
	FIX_SPELLING_AND_GRAMMAR = 'L_FIX_SPELLING_AND_GRAMMAR',
	GENERATE_BASED_ON_TITLE = 'L_GENERATE_BASED_ON_TITLE',
	IMPROVE_WRITING = 'L_IMPROVE_WRITING',
	MAKE_LONGER = 'L_MAKE_LONGER',
	MAKE_SHORTER = 'L_MAKE_SHORTER',
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
