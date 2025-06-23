/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isNullOrUndefined} from '@liferay/layout-js-components-web';

import {EditableValue} from '../../types/editables/EditableValue';
import {config} from '../config/index';

export function getEditableLocalizedValue(
	editableValue: EditableValue | undefined,
	languageId: Liferay.Language.Locale | null = null,
	defaultValue: string = ''
) {
	let content;

	if (languageId && !isNullOrUndefined(editableValue?.[languageId])) {
		content = editableValue?.[languageId];
	}
	else if (!isNullOrUndefined(editableValue?.[config.defaultLanguageId])) {
		content = editableValue?.[config.defaultLanguageId];
	}
	else if (!isNullOrUndefined(editableValue?.defaultValue)) {
		content = editableValue?.defaultValue;
	}
	else if (typeof editableValue === typeof defaultValue) {
		content = editableValue;
	}
	else {
		content = defaultValue;
	}

	return content;
}
