/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBFolderService;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.knowledge.base.util.comparator.KBArticlePriorityComparator;
import com.liferay.knowledge.base.web.internal.KBUtil;
import com.liferay.knowledge.base.web.internal.constants.KBWebKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.io.IOException;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_DISPLAY,
		"mvc.command.name=/knowledge_base/update_root_kb_folder_id"
	},
	service = MVCActionCommand.class
)
public class UpdateRootKBFolderIdMVCActionCommand extends BaseMVCActionCommand {

	@Override
	public void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		try {
			_updateRootKBFolderId(actionRequest, actionResponse);

			SessionMessages.add(
				actionRequest,
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);
		}
		catch (IOException | PortalException exception) {
			throw new PortletException(exception);
		}
	}

	private KBArticle _findClosestMatchingKBArticle(
			long groupId, String oldKBFolderURLTitle, long newKBFolderId,
			String urlTitle)
		throws PortalException {

		KBArticle oldKBArticle =
			_kbArticleLocalService.fetchKBArticleByUrlTitle(
				groupId, oldKBFolderURLTitle, urlTitle);

		KBArticle kbArticle = null;

		while ((kbArticle == null) && (oldKBArticle != null)) {
			kbArticle = _kbArticleLocalService.fetchKBArticleByUrlTitle(
				groupId, newKBFolderId, oldKBArticle.getUrlTitle());

			if (kbArticle == null) {
				oldKBArticle = oldKBArticle.getParentKBArticle();
			}
		}

		if (kbArticle == null) {
			List<KBArticle> kbArticles = _kbArticleLocalService.getKBArticles(
				groupId, newKBFolderId, WorkflowConstants.STATUS_APPROVED, 0, 1,
				KBArticlePriorityComparator.getInstance(true));

			if (!kbArticles.isEmpty()) {
				kbArticle = kbArticles.get(0);
			}
		}

		return kbArticle;
	}

	private void _updateRootKBFolderId(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortalException {

		long kbFolderId = ParamUtil.getLong(actionRequest, "rootKBFolderId");

		long kbFolderGroupId = _portal.getScopeGroupId(actionRequest);
		String kbFolderURLTitle = StringPool.BLANK;

		if (kbFolderId != KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			KBFolder kbFolder = _kbFolderService.getKBFolder(kbFolderId);

			kbFolderGroupId = kbFolder.getGroupId();
			kbFolderURLTitle = kbFolder.getUrlTitle();
		}

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				_portal.getLiferayPortletRequest(actionRequest));

		PortletPreferences portletPreferences = actionRequest.getPreferences();

		String contentRootPrefix = GetterUtil.getString(
			portletPreferences.getValue("contentRootPrefix", null));

		String previousPreferredKBFolderURLTitle =
			KBUtil.getPreferredKBFolderURLTitle(
				portalPreferences, contentRootPrefix);

		KnowledgeBaseUtil.setPreferredKBFolderURLTitle(
			portalPreferences, contentRootPrefix, kbFolderURLTitle);

		String urlTitle = ParamUtil.getString(actionRequest, "urlTitle");

		KBArticle kbArticle = null;

		if (Validator.isNotNull(urlTitle)) {
			kbArticle = _kbArticleLocalService.fetchKBArticleByUrlTitle(
				kbFolderGroupId, kbFolderURLTitle, urlTitle);

			if ((kbArticle == null) &&
				Validator.isNotNull(previousPreferredKBFolderURLTitle)) {

				kbArticle = _findClosestMatchingKBArticle(
					kbFolderGroupId, previousPreferredKBFolderURLTitle,
					kbFolderId, urlTitle);
			}
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			KBWebKeys.THEME_DISPLAY);

		if ((kbArticle != null) &&
			!_kbArticleModelResourcePermission.contains(
				themeDisplay.getPermissionChecker(), kbArticle,
				KBActionKeys.VIEW)) {

			kbArticle = null;
		}

		PortletURL redirectURL = PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				actionRequest, KBPortletKeys.KNOWLEDGE_BASE_DISPLAY,
				PortletRequest.RENDER_PHASE)
		).setParameter(
			"kbFolderId", kbFolderId
		).setParameter(
			"kbFolderUrlTitle", kbFolderURLTitle
		).buildPortletURL();

		if (kbArticle != null) {
			redirectURL.setParameter("urlTitle", kbArticle.getUrlTitle());
		}

		actionResponse.sendRedirect(redirectURL.toString());
	}

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBArticle)"
	)
	private ModelResourcePermission<KBArticle>
		_kbArticleModelResourcePermission;

	@Reference
	private KBFolderService _kbFolderService;

	@Reference
	private Portal _portal;

}