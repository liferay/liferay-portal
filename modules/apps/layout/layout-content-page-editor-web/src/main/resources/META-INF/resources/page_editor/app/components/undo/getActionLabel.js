/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {SELECT_SEGMENTS_EXPERIENCE} from '../../../plugins/experience/actions';
import {
	ADD_FRAGMENT_ENTRY_LINKS,
	ADD_ITEM,
	ADD_RULE,
	ADD_STEPPER,
	CHANGE_MASTER_LAYOUT,
	DELETE_ITEM,
	DELETE_RULE,
	DUPLICATE_ITEM,
	MOVE_ITEM,
	MOVE_STEPPER,
	PASTE_ITEM,
	REMOVE_FORM_STEP,
	SWAP_FRAGMENT,
	SWITCH_VIEWPORT_SIZE,
	TOGGLE_FRAGMENT_HIGHLIGHTED,
	TOGGLE_WIDGET_HIGHLIGHTED,
	UPDATE_COLLECTION_DISPLAY_COLLECTION,
	UPDATE_COL_SIZE,
	UPDATE_EDITABLE_VALUES,
	UPDATE_FORM_ITEM_CONFIG,
	UPDATE_FRAGMENT_ENTRY_LINK_CONFIGURATION,
	UPDATE_ITEM_CONFIG,
	UPDATE_LANGUAGE_ID,
	UPDATE_ROW_COLUMNS,
	UPDATE_RULE,
	UPDATE_RULES,
} from '../../actions/types';
import {UNDO_TYPES} from '../../config/constants/undoTypes';
import {config} from '../../config/index';
import getSegmentsExperienceName from '../../utils/getSegmentsExperienceName';
import isSameMasterLayout from '../../utils/isSameMasterLayout';

function getMasterLayoutName(
	masterLayoutPageTemplateEntryERC,
	masterLayoutPageTemplateEntryScopeERC
) {
	const masterLayout = config.masterLayouts.find((masterLayout) =>
		isSameMasterLayout(masterLayout, {
			masterLayoutPageTemplateEntryERC,
			masterLayoutPageTemplateEntryScopeERC,
		})
	);

	return masterLayout.name;
}

export default function getActionLabel(
	action,
	type,
	{availableSegmentsExperiences}
) {
	switch (action.originalType || action.type) {
		case ADD_FRAGMENT_ENTRY_LINKS:
		case ADD_ITEM:
		case ADD_RULE:
		case ADD_STEPPER:
			return sub(Liferay.Language.get('add-x'), action.itemName);
		case CHANGE_MASTER_LAYOUT:
			return type === UNDO_TYPES.undo
				? sub(
						Liferay.Language.get('select-x-master-layout'),
						getMasterLayoutName(
							action.nextMasterLayoutPageTemplateEntryERC,
							action.nextMasterLayoutPageTemplateEntryScopeERC
						)
					)
				: sub(
						Liferay.Language.get('select-x-master-layout'),
						getMasterLayoutName(
							action.masterLayoutPageTemplateEntryERC,
							action.masterLayoutPageTemplateEntryScopeERC
						)
					);

		case DELETE_ITEM:
		case DELETE_RULE:
			return sub(Liferay.Language.get('delete-x'), action.itemName);
		case DUPLICATE_ITEM:
			return sub(Liferay.Language.get('duplicate-x'), action.itemName);
		case PASTE_ITEM:
			return sub(Liferay.Language.get('paste-x'), action.itemName);
		case MOVE_ITEM:
		case MOVE_STEPPER:
			return sub(Liferay.Language.get('move-x'), action.itemName);
		case REMOVE_FORM_STEP:
			return Liferay.Language.get('remove-step');
		case SELECT_SEGMENTS_EXPERIENCE:
			return type === UNDO_TYPES.undo
				? sub(
						Liferay.Language.get('select-x-experience'),
						getSegmentsExperienceName(
							action.nextSegmentsExperienceId,
							availableSegmentsExperiences
						)
					)
				: sub(
						Liferay.Language.get('select-x-experience'),
						getSegmentsExperienceName(
							action.segmentsExperienceId,
							availableSegmentsExperiences
						)
					);
		case SWAP_FRAGMENT:
			return Liferay.Language.get('swap-fragment');
		case SWITCH_VIEWPORT_SIZE:
			return type === UNDO_TYPES.undo
				? sub(
						Liferay.Language.get('select-x-viewport'),
						config.availableViewportSizes[action.nextSize].label
					)
				: sub(
						Liferay.Language.get('select-x-viewport'),
						config.availableViewportSizes[action.size].label
					);

		case TOGGLE_FRAGMENT_HIGHLIGHTED:
			return action.initiallyHighlighted
				? Liferay.Language.get('add-fragment-to-favorites')
				: Liferay.Language.get('remove-fragment-from-favorites');

		case TOGGLE_WIDGET_HIGHLIGHTED:
			return action.initiallyHighlighted
				? Liferay.Language.get('add-widget-to-favorites')
				: Liferay.Language.get('remove-widget-from-favorites');

		case UPDATE_COL_SIZE:
			return Liferay.Language.get('update-column-size');
		case UPDATE_COLLECTION_DISPLAY_COLLECTION:
		case UPDATE_FRAGMENT_ENTRY_LINK_CONFIGURATION:
		case UPDATE_FORM_ITEM_CONFIG:
		case UPDATE_ITEM_CONFIG:
		case UPDATE_ROW_COLUMNS:
			return sub(
				Liferay.Language.get('update-x-configuration'),
				action.itemName
			);
		case UPDATE_EDITABLE_VALUES:
			return sub(
				Liferay.Language.get('update-x-editable-values'),
				action.itemName
			);
		case UPDATE_LANGUAGE_ID:
			return type === UNDO_TYPES.undo
				? sub(
						Liferay.Language.get('select-x-language'),
						action.nextLanguageId
					)
				: sub(
						Liferay.Language.get('select-x-language'),
						action.languageId
					);
		case UPDATE_RULE:
			return sub(Liferay.Language.get('update-x'), action.itemName);
		case UPDATE_RULES:
			return Liferay.Language.get('reorder-rule');
		default:
			return;
	}
}
