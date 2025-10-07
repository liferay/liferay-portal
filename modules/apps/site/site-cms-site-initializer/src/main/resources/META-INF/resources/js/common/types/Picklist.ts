/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type Option = {
	erc: string;
	key: string;
	name: Liferay.Language.LocalizedValue<string>;
};

export type Options = Map<string, Partial<Option>>;

export type Picklist = {
	externalReferenceCode: string;
	id: number;
	listTypeEntries: {
		externalReferenceCode: string;
		key: string;
		name_i18n: Liferay.Language.LocalizedValue<string>;
	}[];
	name: string;
	name_i18n: Liferay.Language.LocalizedValue<string>;
};
