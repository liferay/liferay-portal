/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.social;

import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBComment;
import com.liferay.knowledge.base.model.KBTemplate;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBCommentLocalService;
import com.liferay.knowledge.base.service.KBTemplateLocalService;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.social.kernel.model.BaseSocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityInterpreter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Shin
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
	service = SocialActivityInterpreter.class
)
public class KBActivityInterpreter extends BaseSocialActivityInterpreter {

	@Override
	public String[] getClassNames() {
		return _CLASS_NAMES;
	}

	@Override
	protected String getEntryTitle(
			SocialActivity activity, ServiceContext serviceContext)
		throws Exception {

		String title = StringPool.BLANK;

		String className = activity.getClassName();

		if (className.equals(KBArticle.class.getName())) {
			KBArticle kbArticle = _kbArticleLocalService.getLatestKBArticle(
				activity.getClassPK(), WorkflowConstants.STATUS_APPROVED);

			title = kbArticle.getTitle();
		}
		else if (className.equals(KBComment.class.getName())) {
			KBComment kbComment = _kbCommentLocalService.getKBComment(
				activity.getClassPK());

			String kbCommentClassName = kbComment.getClassName();

			if (kbCommentClassName.equals(KBArticle.class.getName())) {
				KBArticle kbArticle = _kbArticleLocalService.getLatestKBArticle(
					kbComment.getClassPK(), WorkflowConstants.STATUS_APPROVED);

				title = kbArticle.getTitle();
			}
			else if (kbCommentClassName.equals(KBTemplate.class.getName())) {
				KBTemplate kbTemplate = _kbTemplateLocalService.getKBTemplate(
					kbComment.getClassPK());

				title = kbTemplate.getTitle();
			}
		}
		else if (className.equals(KBTemplate.class.getName())) {
			KBTemplate kbTemplate = _kbTemplateLocalService.getKBTemplate(
				activity.getClassPK());

			title = kbTemplate.getTitle();
		}

		return getJSONValue(activity.getExtraData(), "title", title);
	}

	@Override
	protected String getLink(
			SocialActivity activity, ServiceContext serviceContext)
		throws Exception {

		String className = activity.getClassName();

		if (className.equals(KBArticle.class.getName())) {
			KBArticle kbArticle = _kbArticleLocalService.getLatestKBArticle(
				activity.getClassPK(), WorkflowConstants.STATUS_APPROVED);

			return KnowledgeBaseUtil.getKBArticleURL(
				serviceContext.getPlid(), kbArticle.getResourcePrimKey(),
				kbArticle.getStatus(), serviceContext.getPortalURL(), false);
		}
		else if (className.equals(KBComment.class.getName())) {
			KBComment kbComment = _kbCommentLocalService.getKBComment(
				activity.getClassPK());

			String kbCommentClassName = kbComment.getClassName();

			if (kbCommentClassName.equals(KBArticle.class.getName())) {
				KBArticle kbArticle = _kbArticleLocalService.getLatestKBArticle(
					kbComment.getClassPK(), WorkflowConstants.STATUS_APPROVED);

				return KnowledgeBaseUtil.getKBArticleURL(
					serviceContext.getPlid(), kbArticle.getResourcePrimKey(),
					kbArticle.getStatus(), serviceContext.getPortalURL(),
					false);
			}
		}

		return StringPool.BLANK;
	}

	@Override
	protected String getTitlePattern(
		String groupName, SocialActivity activity) {

		String className = activity.getClassName();

		if (className.equals(KBArticle.class.getName())) {
			if (activity.getType() == KBActivityKeys.ADD_KB_ARTICLE) {
				if (Validator.isNull(groupName)) {
					return "activity-knowledge-base-admin-add-kb-article";
				}

				return "activity-knowledge-base-admin-add-kb-article-in";
			}
			else if (activity.getType() == KBActivityKeys.MOVE_KB_ARTICLE) {
				if (Validator.isNull(groupName)) {
					return "activity-knowledge-base-admin-move-kb-article";
				}

				return "activity-knowledge-base-admin-move-kb-article-in";
			}
			else if (activity.getType() == KBActivityKeys.UPDATE_KB_ARTICLE) {
				if (Validator.isNull(groupName)) {
					return "activity-knowledge-base-admin-update-kb-article";
				}

				return "activity-knowledge-base-admin-update-kb-article-in";
			}
		}
		else if (className.equals(KBComment.class.getName())) {
			if (activity.getType() == KBActivityKeys.ADD_KB_COMMENT) {
				if (Validator.isNull(groupName)) {
					return "activity-knowledge-base-admin-add-kb-comment";
				}

				return "activity-knowledge-base-admin-add-kb-comment-in";
			}
			else if (activity.getType() == KBActivityKeys.UPDATE_KB_COMMENT) {
				if (Validator.isNull(groupName)) {
					return "activity-knowledge-base-admin-update-kb-comment";
				}

				return "activity-knowledge-base-admin-update-kb-comment-in";
			}
		}
		else if (className.equals(KBTemplate.class.getName())) {
			if (activity.getType() == KBActivityKeys.ADD_KB_TEMPLATE) {
				if (Validator.isNull(groupName)) {
					return "activity-knowledge-base-admin-add-kb-template";
				}

				return "activity-knowledge-base-admin-add-kb-template-in";
			}
			else if (activity.getType() == KBActivityKeys.UPDATE_KB_TEMPLATE) {
				if (Validator.isNull(groupName)) {
					return "activity-knowledge-base-admin-update-kb-template";
				}

				return "activity-knowledge-base-admin-update-kb-template-in";
			}
		}

		return StringPool.BLANK;
	}

	@Override
	protected boolean hasPermissions(
			PermissionChecker permissionChecker, SocialActivity activity,
			String actionId, ServiceContext serviceContext)
		throws Exception {

		String className = activity.getClassName();

		if (className.equals(KBArticle.class.getName())) {
			KBArticle kbArticle = _kbArticleLocalService.getLatestKBArticle(
				activity.getClassPK(), WorkflowConstants.STATUS_APPROVED);

			return _kbArticleModelResourcePermission.contains(
				permissionChecker, kbArticle, KBActionKeys.VIEW);
		}
		else if (className.equals(KBComment.class.getName())) {
			return true;
		}
		else if (className.equals(KBTemplate.class.getName())) {
			return _kbTemplateModelResourcePermission.contains(
				permissionChecker,
				_kbTemplateLocalService.getKBTemplate(activity.getClassPK()),
				KBActionKeys.VIEW);
		}

		return false;
	}

	private static final String[] _CLASS_NAMES = {
		KBArticle.class.getName(), KBComment.class.getName(),
		KBTemplate.class.getName()
	};

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBArticle)"
	)
	private ModelResourcePermission<KBArticle>
		_kbArticleModelResourcePermission;

	@Reference
	private KBCommentLocalService _kbCommentLocalService;

	@Reference
	private KBTemplateLocalService _kbTemplateLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBTemplate)"
	)
	private ModelResourcePermission<KBTemplate>
		_kbTemplateModelResourcePermission;

}