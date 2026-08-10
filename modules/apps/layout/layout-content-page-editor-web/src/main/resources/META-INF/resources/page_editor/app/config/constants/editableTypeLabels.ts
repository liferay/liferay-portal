/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EDITABLE_TYPES} from './editableTypes';

export const EDITABLE_TYPE_LABELS = {
	[EDITABLE_TYPES.action]: Liferay.Language.get('action'),
	[EDITABLE_TYPES.backgroundImage]: Liferay.Language.get('background-image'),
	[EDITABLE_TYPES.html]: Liferay.Language.get('html'),
	[EDITABLE_TYPES.image]: Liferay.Language.get('image'),
	[EDITABLE_TYPES.link]: Liferay.Language.get('link'),
	[EDITABLE_TYPES['rich-text']]: Liferay.Language.get('rich-text'),
	[EDITABLE_TYPES.text]: Liferay.Language.get('text'),
};
