/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.internal.social;

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.social.BookmarksActivityKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.social.kernel.model.BaseSocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityInterpreter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juan Fernández
 * @author Zsolt Berentey
 */
@Component(
	property = {
		"jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
		"model.class.name=com.liferay.bookmarks.model.BookmarksEntry"
	},
	service = SocialActivityInterpreter.class
)
public class BookmarksEntryActivityInterpreter
	extends BaseSocialActivityInterpreter {

	@Override
	public String[] getClassNames() {
		return _CLASS_NAMES;
	}

	@Override
	protected String getPath(
		SocialActivity activity, ServiceContext serviceContext) {

		return "/bookmarks/find_entry?entryId=" + activity.getClassPK();
	}

	@Override
	protected String getTitlePattern(
		String groupName, SocialActivity activity) {

		int activityType = activity.getType();

		if (activityType == BookmarksActivityKeys.ADD_ENTRY) {
			if (Validator.isNull(groupName)) {
				return "activity-bookmarks-entry-add-entry";
			}

			return "activity-bookmarks-entry-add-entry-in";
		}
		else if (activityType == BookmarksActivityKeys.UPDATE_ENTRY) {
			if (Validator.isNull(groupName)) {
				return "activity-bookmarks-entry-update-entry";
			}

			return "activity-bookmarks-entry-update-entry-in";
		}
		else if (activityType == SocialActivityConstants.TYPE_MOVE_TO_TRASH) {
			if (Validator.isNull(groupName)) {
				return "activity-bookmarks-entry-move-to-trash";
			}

			return "activity-bookmarks-entry-move-to-trash-in";
		}
		else if (activityType ==
					SocialActivityConstants.TYPE_RESTORE_FROM_TRASH) {

			if (Validator.isNull(groupName)) {
				return "activity-bookmarks-entry-restore-from-trash";
			}

			return "activity-bookmarks-entry-restore-from-trash-in";
		}

		return null;
	}

	@Override
	protected boolean hasPermissions(
			PermissionChecker permissionChecker, SocialActivity activity,
			String actionId, ServiceContext serviceContext)
		throws Exception {

		return _bookmarksEntryModelResourcePermission.contains(
			permissionChecker, activity.getClassPK(), actionId);
	}

	private static final String[] _CLASS_NAMES = {
		BookmarksEntry.class.getName()
	};

	@Reference(
		target = "(model.class.name=com.liferay.bookmarks.model.BookmarksEntry)"
	)
	private ModelResourcePermission<BookmarksEntry>
		_bookmarksEntryModelResourcePermission;

}