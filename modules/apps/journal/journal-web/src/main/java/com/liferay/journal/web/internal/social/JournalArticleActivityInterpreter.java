/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.social;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.journal.constants.JournalActivityKeys;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.util.JournalHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.social.kernel.model.BaseSocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityInterpreter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 * @author Zsolt Berentey
 */
@Component(
	property = "jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
	service = SocialActivityInterpreter.class
)
public class JournalArticleActivityInterpreter
	extends BaseSocialActivityInterpreter {

	@Override
	public String[] getClassNames() {
		return _CLASS_NAMES;
	}

	@Override
	protected String getPath(
			SocialActivity activity, ServiceContext serviceContext)
		throws Exception {

		try {
			LiferayPortletRequest liferayPortletRequest =
				serviceContext.getLiferayPortletRequest();

			LiferayPortletResponse liferayPortletResponse =
				serviceContext.getLiferayPortletResponse();

			if ((liferayPortletRequest != null) &&
				(liferayPortletResponse != null)) {

				AssetRendererFactory<?> journalArticleAssetRendererFactory =
					AssetRendererFactoryRegistryUtil.
						getAssetRendererFactoryByClass(JournalArticle.class);

				AssetRenderer<?> journalArticleAssetRenderer =
					journalArticleAssetRendererFactory.getAssetRenderer(
						activity.getClassPK());

				return journalArticleAssetRenderer.getURLViewInContext(
					serviceContext.getLiferayPortletRequest(),
					serviceContext.getLiferayPortletResponse(), null);
			}

			JournalArticle article =
				_journalArticleLocalService.getLatestArticle(
					activity.getClassPK());

			Layout layout = article.getLayout();

			if (layout == null) {
				return null;
			}

			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			return _journalHelper.createURLPattern(
				article, themeDisplay.getLocale(), layout.isPrivateLayout(),
				JournalArticleConstants.CANONICAL_URL_SEPARATOR, themeDisplay);
		}
		catch (NoSuchArticleException noSuchArticleException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchArticleException);
			}

			return null;
		}
	}

	@Override
	protected Object[] getTitleArguments(
			String groupName, SocialActivity activity, String link,
			String title, ServiceContext serviceContext)
		throws Exception {

		if (activity.getType() == SocialActivityConstants.TYPE_ADD_COMMENT) {
			String creatorUserName = getUserName(
				activity.getUserId(), serviceContext);
			String receiverUserName = getUserName(
				activity.getReceiverUserId(), serviceContext);

			return new Object[] {
				groupName, creatorUserName, receiverUserName,
				wrapLink(link, title)
			};
		}

		return super.getTitleArguments(
			groupName, activity, link, title, serviceContext);
	}

	@Override
	protected String getTitlePattern(
		String groupName, SocialActivity activity) {

		int activityType = activity.getType();

		if (activityType == JournalActivityKeys.ADD_ARTICLE) {
			if (Validator.isNull(groupName)) {
				return "activity-journal-article-add-web-content";
			}

			return "activity-journal-article-add-web-content-in";
		}
		else if (activityType == JournalActivityKeys.UPDATE_ARTICLE) {
			if (Validator.isNull(groupName)) {
				return "activity-journal-article-update-web-content";
			}

			return "activity-journal-article-update-web-content-in";
		}
		else if (activityType == SocialActivityConstants.TYPE_ADD_COMMENT) {
			if (Validator.isNull(groupName)) {
				return "activity-journal-article-add-comment";
			}

			return "activity-journal-article-add-comment-in";
		}
		else if (activityType == SocialActivityConstants.TYPE_MOVE_TO_TRASH) {
			if (Validator.isNull(groupName)) {
				return "activity-journal-article-move-to-trash";
			}

			return "activity-journal-article-move-to-trash-in";
		}
		else if (activityType ==
					SocialActivityConstants.TYPE_RESTORE_FROM_TRASH) {

			if (Validator.isNull(groupName)) {
				return "activity-journal-article-restore-from-trash";
			}

			return "activity-journal-article-restore-from-trash-in";
		}

		return null;
	}

	@Override
	protected boolean hasPermissions(
			PermissionChecker permissionChecker, SocialActivity activity,
			String actionId, ServiceContext serviceContext)
		throws Exception {

		int activityType = activity.getType();

		if (activityType == JournalActivityKeys.ADD_ARTICLE) {
			JournalArticle article =
				_journalArticleLocalService.getLatestArticle(
					activity.getClassPK());

			return ModelResourcePermissionUtil.contains(
				_journalFolderModelResourcePermission, permissionChecker,
				article.getGroupId(), article.getFolderId(),
				ActionKeys.ADD_ARTICLE);
		}
		else if (activityType == JournalActivityKeys.UPDATE_ARTICLE) {
			return _journalArticleModelResourcePermission.contains(
				permissionChecker, activity.getClassPK(), ActionKeys.UPDATE);
		}

		return _journalArticleModelResourcePermission.contains(
			permissionChecker, activity.getClassPK(), actionId);
	}

	private static final String[] _CLASS_NAMES = {
		JournalArticle.class.getName()
	};

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleActivityInterpreter.class);

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private ModelResourcePermission<JournalArticle>
		_journalArticleModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalFolder)"
	)
	private ModelResourcePermission<JournalFolder>
		_journalFolderModelResourcePermission;

	@Reference
	private JournalHelper _journalHelper;

}