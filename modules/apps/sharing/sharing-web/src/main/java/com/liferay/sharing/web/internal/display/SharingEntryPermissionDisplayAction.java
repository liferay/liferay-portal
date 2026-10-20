/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.display;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.sharing.security.permission.SharingEntryAction;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Sergio González
 */
public enum SharingEntryPermissionDisplayAction {

	COMMENTS(
		"COMMENTS", "comment[verb]", "collaborators-can-comment-on-the-item[",
		"comment[verb]", SharingEntryAction.ADD_DISCUSSION,
		SharingEntryAction.VIEW),
	UPDATE(
		"UPDATE", "update", "collaborators-can-view-comment-update-the-item[",
		"update", SharingEntryAction.ADD_DISCUSSION, SharingEntryAction.UPDATE,
		SharingEntryAction.VIEW),
	VIEW(
		"VIEW", "view", "collaborators-can-only-view-the-item[", "view",
		SharingEntryAction.VIEW);

	public static SharingEntryPermissionDisplayAction parseFromActionId(
		String actionId) {

		if (Objects.equals(COMMENTS.getActionId(), actionId)) {
			return COMMENTS;
		}
		else if (Objects.equals(UPDATE.getActionId(), actionId)) {
			return UPDATE;
		}
		else if (Objects.equals(VIEW.getActionId(), actionId)) {
			return VIEW;
		}

		throw new IllegalArgumentException("Invalid action ID " + actionId);
	}

	public String getActionId() {
		return _actionId;
	}

	public String getDescription(
		String className, ResourceBundle resourceBundle) {

		return LanguageUtil.get(
			resourceBundle,
			StringBundler.concat(_descriptionKey, className, "]"));
	}

	public List<SharingEntryAction> getSharingEntryActions() {
		return _sharingEntryActions;
	}

	public String getTitleKey() {
		return _titleKey;
	}

	public String getVerbKey() {
		return _verbKey;
	}

	private SharingEntryPermissionDisplayAction(
		String actionId, String titleKey, String descriptionKey, String verbKey,
		SharingEntryAction... sharingEntryActions) {

		_actionId = actionId;
		_titleKey = titleKey;
		_descriptionKey = descriptionKey;
		_verbKey = verbKey;

		_sharingEntryActions = Arrays.asList(sharingEntryActions);
	}

	private final String _actionId;
	private final String _descriptionKey;
	private final List<SharingEntryAction> _sharingEntryActions;
	private final String _titleKey;
	private final String _verbKey;

}