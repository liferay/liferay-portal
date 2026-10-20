/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {Editor} from '@ckeditor/ckeditor5-core';

const getItemSelectorGroupId = (editor: Editor): number | undefined => {
	const groupId = Number(editor.config.get('groupId'));

	if (groupId) {
		return groupId;
	}

	if (editor.config.get('itemSelectorAskForGroupId')) {
		return undefined;
	}

	return Liferay.ThemeDisplay.getScopeGroupId();
};

export default getItemSelectorGroupId;
