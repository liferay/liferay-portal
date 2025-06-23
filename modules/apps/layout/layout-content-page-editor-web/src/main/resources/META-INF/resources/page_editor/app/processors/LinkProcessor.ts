/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isNullOrUndefined} from '@liferay/layout-js-components-web';

import {EditableConfig} from '../../types/editables/EditableValue';
import {getAlloyEditorProcessor} from '../js-index';
import {getEditableLinkValue} from '../utils/getEditableLinkValue';
import getCKEditorProcessor from './getCKEditorProcessor';
import {getLinkableEditableEditorWrapper} from './getLinkableEditableEditorWrapper';

const processor = Liferay.FeatureFlags['LPD-11235']
	? getCKEditorProcessor
	: getAlloyEditorProcessor;

export default processor(
	'text',
	getLinkableEditableEditorWrapper,
	(
		element: HTMLElement,
		value: string,
		editableConfig: EditableConfig,
		languageId: Liferay.Language.Locale
	) => {
		const anchor =
			element instanceof HTMLAnchorElement
				? element
				: element.querySelector('a');

		const link = getEditableLinkValue(editableConfig, languageId);

		if (anchor) {
			const href = link.href
				? `${editableConfig.prefix || ''}${link.href}`
				: '#';

			anchor.href = href;
			anchor.rel = link.rel;
			anchor.target = link.target;

			if (!isNullOrUndefined(value)) {
				anchor.innerHTML = value;
			}
		}
	}
);
