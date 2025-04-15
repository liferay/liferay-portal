/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.social;

import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.social.BlogsActivityKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.social.kernel.model.BaseSocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityInterpreter;

import java.text.Format;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Ryan Park
 * @author Zsolt Berentey
 */
@Component(
	property = "jakarta.portlet.name=" + BlogsPortletKeys.BLOGS,
	service = SocialActivityInterpreter.class
)
public class BlogsActivityInterpreter extends BaseSocialActivityInterpreter {

	@Override
	public String[] getClassNames() {
		return _CLASS_NAMES;
	}

	@Override
	protected String getPath(
		SocialActivity activity, ServiceContext serviceContext) {

		return "/blogs/find_entry?entryId=" + activity.getClassPK();
	}

	@Override
	protected Object[] getTitleArguments(
			String groupName, SocialActivity activity, String link,
			String title, ServiceContext serviceContext)
		throws Exception {

		String creatorUserName = getUserName(
			activity.getUserId(), serviceContext);
		String receiverUserName = getUserName(
			activity.getReceiverUserId(), serviceContext);

		BlogsEntry entry = _blogsEntryLocalService.getEntry(
			activity.getClassPK());

		String displayDate = StringPool.BLANK;

		if ((activity.getType() == BlogsActivityKeys.ADD_ENTRY) &&
			(entry.getStatus() == WorkflowConstants.STATUS_SCHEDULED)) {

			link = null;

			Format dateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
				"MMMM d", serviceContext.getLocale(),
				serviceContext.getTimeZone());

			displayDate = dateFormat.format(entry.getDisplayDate());
		}

		return new Object[] {
			groupName, creatorUserName, receiverUserName, wrapLink(link, title),
			displayDate
		};
	}

	@Override
	protected String getTitlePattern(String groupName, SocialActivity activity)
		throws Exception {

		int activityType = activity.getType();

		if ((activityType == BlogsActivityKeys.ADD_COMMENT) ||
			(activityType == SocialActivityConstants.TYPE_ADD_COMMENT)) {

			if (Validator.isNull(groupName)) {
				return "activity-blogs-entry-add-comment";
			}

			return "activity-blogs-entry-add-comment-in";
		}
		else if (activityType == BlogsActivityKeys.ADD_ENTRY) {
			BlogsEntry entry = _blogsEntryLocalService.getEntry(
				activity.getClassPK());

			if (entry.getStatus() == WorkflowConstants.STATUS_SCHEDULED) {
				if (Validator.isNull(groupName)) {
					return "activity-blogs-entry-schedule-entry";
				}

				return "activity-blogs-entry-schedule-entry-in";
			}

			if (Validator.isNull(groupName)) {
				return "activity-blogs-entry-add-entry";
			}

			return "activity-blogs-entry-add-entry-in";
		}
		else if (activityType == SocialActivityConstants.TYPE_MOVE_TO_TRASH) {
			if (Validator.isNull(groupName)) {
				return "activity-blogs-entry-move-to-trash";
			}

			return "activity-blogs-entry-move-to-trash-in";
		}
		else if (activityType ==
					SocialActivityConstants.TYPE_RESTORE_FROM_TRASH) {

			if (Validator.isNull(groupName)) {
				return "activity-blogs-entry-restore-from-trash";
			}

			return "activity-blogs-entry-restore-from-trash-in";
		}
		else if (activityType == BlogsActivityKeys.UPDATE_ENTRY) {
			if (Validator.isNull(groupName)) {
				return "activity-blogs-entry-update-entry";
			}

			return "activity-blogs-entry-update-entry-in";
		}

		return null;
	}

	@Override
	protected boolean hasPermissions(
			PermissionChecker permissionChecker, SocialActivity activity,
			String actionId, ServiceContext serviceContext)
		throws Exception {

		return _blogsEntryModelResourcePermission.contains(
			permissionChecker, activity.getClassPK(), actionId);
	}

	private static final String[] _CLASS_NAMES = {BlogsEntry.class.getName()};

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference(target = "(model.class.name=com.liferay.blogs.model.BlogsEntry)")
	private ModelResourcePermission<BlogsEntry>
		_blogsEntryModelResourcePermission;

}