/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type LocalizedValue = {
	[key in Liferay.Language.Locale]?: string;
};

export type EditableConfig = Record<string, unknown>;

export type EditableValue = {
	config?: EditableConfig;
	defaultValue?: string;
} & LocalizedValue;
