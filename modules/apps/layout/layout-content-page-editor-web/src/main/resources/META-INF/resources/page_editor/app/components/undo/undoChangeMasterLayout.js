/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import changeMasterLayout from '../../../app/thunks/changeMasterLayout';
import {config} from '../../config/index';
import isSameMasterLayout from '../../utils/isSameMasterLayout';

function undoAction({action}) {
	return changeMasterLayout({
		masterLayoutPageTemplateEntryERC:
			action.masterLayoutPageTemplateEntryERC,
		masterLayoutPageTemplateEntryScopeERC:
			action.masterLayoutPageTemplateEntryScopeERC,
	});
}

function getDerivedStateForUndo({action, state}) {
	const masterLayoutExists = config.masterLayouts.some((masterLayout) =>
		isSameMasterLayout(masterLayout, state.masterLayout)
	);

	if (!masterLayoutExists) {
		return null;
	}

	return {
		masterLayoutPageTemplateEntryERC:
			state.masterLayout.masterLayoutPageTemplateEntryERC,
		masterLayoutPageTemplateEntryScopeERC:
			state.masterLayout.masterLayoutPageTemplateEntryScopeERC,
		nextMasterLayoutPageTemplateEntryERC:
			action.masterLayoutPageTemplateEntryERC,
		nextMasterLayoutPageTemplateEntryScopeERC:
			action.masterLayoutPageTemplateEntryScopeERC,
	};
}

export {undoAction, getDerivedStateForUndo};
