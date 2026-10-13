/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const IMAGES_PATH = Liferay.ThemeDisplay.getPathThemeImages();

export const EMPTY_STATE_SYMBOL = 'empty-state';

export const ERROR_MESSAGE = Liferay.Language.get(
	'an-unexpected-system-error-occurred'
);

export const MAX_LENGTH = 65;

export const MIN_LENGTH = 3;

export const NOT_FOUND_SYMBOL = 'search-state';

export const REFETCH_JOB_INTERVAL = 3000;

export const SPRITEMAP = IMAGES_PATH + '/clay/icons.svg';
