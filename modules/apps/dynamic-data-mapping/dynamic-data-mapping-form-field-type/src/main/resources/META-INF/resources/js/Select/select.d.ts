/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locale, LocalizedValue} from '../types';

interface Item {
	_key?: string;
	active: boolean;
	checked: boolean;
	label: string;
	reference: string;
	type: string;
	value: string;
}

export interface Option<T> {
	label: LocalizedValue<string>;
	value: T;
}

type MultiSelectItem = {
	label: string;
	reference: string | null;
	value: string;
};

interface MultipleSelectBaseProps<TValue> {
	defaultLanguageId: Locale;
	displayErrors?: boolean;
	errorMessage: string;
	fieldName: string;
	fixedOptions?: Option<string>[];
	id?: string;
	label: string;
	loading?: boolean;
	localizedObjectField?: boolean;
	name: string;
	onBlur?: any;
	onChange: any;
	onFocus?: any;
	onLoadMore?: () => Promise<Option>;
	options: any[];
	placeholder?: string;
	predefinedValue?: string[] | string;
	readOnly: boolean;
	required: boolean;
	tip?: string;
	valid?: boolean;
	value: TValue;
}

interface SelectMainProps extends MultipleSelectBaseProps<string | string[]> {
	className?: string;
	defaultSearch?: boolean;
	displayErrors?: boolean;
	editingLanguageId?: Locale;
	fixedOptions?: Option<string>[];
	localizedValue?: any;
	localizedValueEdited?: any;
	multiple?: boolean;
	onSelectionChange?: (value: React.Key) => void;
	selectedKey: string;
	showEmptyOption: boolean;
	valid?: boolean;
	visible?: boolean;
}
