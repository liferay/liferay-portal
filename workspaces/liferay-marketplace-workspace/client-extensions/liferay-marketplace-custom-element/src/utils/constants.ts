/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {KeyedMutator} from 'swr';

export type Action<T = any> = {
	disabled?: ((item: T) => boolean) | boolean;
	hidden?: ((item: T) => boolean) | boolean;
	icon?: string;
	name: ((item: T) => string) | string;
	onClick?: (item: T, mutate: KeyedMutator<APIResponse<T> | T>) => void;
};

export type SortDirection = keyof typeof SortOption;

export const PAGINATION_DELTA = [10, 20, 40, 80, 120];

export const PAGINATION = {
	delta: PAGINATION_DELTA,
	ellipsisBuffer: 3,
};

export enum SortOption {
	ASC = 'ASC',
	DESC = 'DESC',
}
