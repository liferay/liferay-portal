/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.social;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.social.kernel.model.BaseSocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityInterpreter;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageResource;
import com.liferay.wiki.service.WikiPageLocalService;
import com.liferay.wiki.service.WikiPageResourceLocalService;
import com.liferay.wiki.social.WikiActivityKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Kong
 * @author Ryan Park
 * @author Zsolt Berentey
 */
@Component(
	property = "jakarta.portlet.name=" + WikiPortletKeys.WIKI,
	service = SocialActivityInterpreter.class
)
public class WikiActivityInterpreter extends BaseSocialActivityInterpreter {

	@Override
	public String[] getClassNames() {
		return _CLASS_NAMES;
	}

	@Override
	protected String getPath(
		SocialActivity activity, ServiceContext serviceContext) {

		return "/wiki/find_page?pageResourcePrimKey=" + activity.getClassPK();
	}

	@Override
	protected Object[] getTitleArguments(
			String groupName, SocialActivity activity, String link,
			String title, ServiceContext serviceContext)
		throws Exception {

		WikiPageResource pageResource =
			_wikiPageResourceLocalService.fetchWikiPageResource(
				activity.getClassPK());

		if (pageResource == null) {
			return null;
		}

		String creatorUserName = getUserName(
			activity.getUserId(), serviceContext);

		title = wrapLink(link, title);

		return new Object[] {
			groupName, creatorUserName, title,
			_getAttachmentTitle(activity, pageResource, serviceContext)
		};
	}

	@Override
	protected String getTitlePattern(
		String groupName, SocialActivity activity) {

		int activityType = activity.getType();

		if ((activityType == WikiActivityKeys.ADD_COMMENT) ||
			(activityType == SocialActivityConstants.TYPE_ADD_COMMENT)) {

			if (Validator.isNull(groupName)) {
				return "activity-wiki-page-add-comment";
			}

			return "activity-wiki-page-add-comment-in";
		}
		else if (activityType == WikiActivityKeys.ADD_PAGE) {
			if (Validator.isNull(groupName)) {
				return "activity-wiki-page-add-page";
			}

			return "activity-wiki-page-add-page-in";
		}
		else if (activityType == SocialActivityConstants.TYPE_ADD_ATTACHMENT) {
			if (Validator.isNull(groupName)) {
				return "activity-wiki-page-add-attachment";
			}

			return "activity-wiki-page-add-attachment-in";
		}
		else if (activityType ==
					SocialActivityConstants.TYPE_MOVE_ATTACHMENT_TO_TRASH) {

			if (Validator.isNull(groupName)) {
				return "activity-wiki-page-remove-attachment";
			}

			return "activity-wiki-page-remove-attachment-in";
		}
		else if (activityType ==
					SocialActivityConstants.
						TYPE_RESTORE_ATTACHMENT_FROM_TRASH) {

			if (Validator.isNull(groupName)) {
				return "activity-wiki-page-restore-attachment";
			}

			return "activity-wiki-page-restore-attachment-in";
		}
		else if (activityType == SocialActivityConstants.TYPE_MOVE_TO_TRASH) {
			if (Validator.isNull(groupName)) {
				return "activity-wiki-page-move-to-trash";
			}

			return "activity-wiki-page-move-to-trash-in";
		}
		else if (activityType ==
					SocialActivityConstants.TYPE_RESTORE_FROM_TRASH) {

			if (Validator.isNull(groupName)) {
				return "activity-wiki-page-restore-from-trash";
			}

			return "activity-wiki-page-restore-from-trash-in";
		}
		else if (activityType == WikiActivityKeys.UPDATE_PAGE) {
			if (Validator.isNull(groupName)) {
				return "activity-wiki-page-update-page";
			}

			return "activity-wiki-page-update-page-in";
		}

		return null;
	}

	@Override
	protected boolean hasPermissions(
			PermissionChecker permissionChecker, SocialActivity activity,
			String actionId, ServiceContext serviceContext)
		throws Exception {

		if (!_wikiPageModelResourcePermission.contains(
				permissionChecker, activity.getClassPK(), ActionKeys.VIEW)) {

			return false;
		}

		int activityType = activity.getType();

		if (activityType == WikiActivityKeys.UPDATE_PAGE) {
			WikiPageResource pageResource =
				_wikiPageResourceLocalService.getPageResource(
					activity.getClassPK());

			double version = GetterUtil.getDouble(
				activity.getExtraDataValue("version"));

			WikiPage page = _wikiPageLocalService.getPage(
				pageResource.getNodeId(), pageResource.getTitle(), version);

			if (!page.isApproved() &&
				!_wikiPageModelResourcePermission.contains(
					permissionChecker, activity.getClassPK(),
					ActionKeys.UPDATE)) {

				return false;
			}
		}

		return true;
	}

	private String _getAttachmentTitle(
			SocialActivity activity, WikiPageResource pageResource,
			ServiceContext serviceContext)
		throws Exception {

		int activityType = activity.getType();

		if ((activityType == SocialActivityConstants.TYPE_ADD_ATTACHMENT) ||
			(activityType ==
				SocialActivityConstants.TYPE_MOVE_ATTACHMENT_TO_TRASH) ||
			(activityType ==
				SocialActivityConstants.TYPE_RESTORE_ATTACHMENT_FROM_TRASH)) {

			String link = null;

			FileEntry fileEntry = null;

			try {
				long fileEntryId = GetterUtil.getLong(
					activity.getExtraDataValue("fileEntryId"));

				fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(
					fileEntryId);
			}
			catch (NoSuchModelException noSuchModelException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchModelException);
				}
			}

			String fileEntryTitle = activity.getExtraDataValue(
				"fileEntryTitle");

			if ((fileEntry != null) && !fileEntry.isInTrash()) {
				link = StringBundler.concat(
					serviceContext.getPathMain(),
					"/wiki/get_page_attachment?p_l_id=",
					serviceContext.getPlid(), "&nodeId=",
					pageResource.getNodeId(), "&title=",
					URLCodec.encodeURL(pageResource.getTitle()), "&fileName=",
					fileEntryTitle);
			}

			return wrapLink(link, fileEntryTitle);
		}

		return StringPool.BLANK;
	}

	private static final String[] _CLASS_NAMES = {WikiPage.class.getName()};

	private static final Log _log = LogFactoryUtil.getLog(
		WikiActivityInterpreter.class);

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

	@Reference(target = "(model.class.name=com.liferay.wiki.model.WikiPage)")
	private ModelResourcePermission<WikiPage> _wikiPageModelResourcePermission;

	@Reference
	private WikiPageResourceLocalService _wikiPageResourceLocalService;

}