/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.workflow;

import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;

import jakarta.portlet.PortletRequest;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Farache
 * @author Marcellus Tavares
 * @author Juan Fernández
 * @author Julio Camarero
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalArticle",
	service = WorkflowHandler.class
)
public class JournalArticleWorkflowHandler
	extends BaseWorkflowHandler<JournalArticle> {

	@Override
	public String getClassName() {
		return JournalArticle.class.getName();
	}

	@Override
	public String getType(Locale locale) {
		return ResourceActionsUtil.getModelResource(locale, getClassName());
	}

	@Override
	public String getURLViewInContext(
		long classPK, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		JournalArticle article = _journalArticleLocalService.fetchArticle(
			classPK);

		if (article == null) {
			article = _journalArticleLocalService.fetchLatestArticle(classPK);
		}

		if (article == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to find article " + classPK);
			}

			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (!_isShowDisplayPage(
					classPK, themeDisplay.getScopeGroupId(), article)) {

				return _getHitLayoutURL(
					article, liferayPortletRequest, themeDisplay);
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return super.getURLViewInContext(
			classPK, liferayPortletRequest, liferayPortletResponse,
			noSuchEntryRedirect);
	}

	@Override
	public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long companyId, long groupId, long classPK)
		throws PortalException {

		JournalArticle article = _journalArticleLocalService.getArticle(
			classPK);

		long folderId = _journalFolderLocalService.getInheritedWorkflowFolderId(
			article.getFolderId());

		WorkflowDefinitionLink workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
				companyId, groupId, JournalFolder.class.getName(), folderId,
				article.getDDMStructureId(), true);

		if (workflowDefinitionLink == null) {
			workflowDefinitionLink =
				_workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
					companyId, groupId, JournalFolder.class.getName(), folderId,
					JournalArticleConstants.DDM_STRUCTURE_ID_ALL, true);
		}

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
		}

		if (folderId == 0) {
			return super.getWorkflowDefinitionLink(companyId, groupId, classPK);
		}

		JournalFolder folder = _journalFolderLocalService.fetchFolder(folderId);

		if ((folder != null) &&
			(folder.getRestrictionType() ==
				JournalFolderConstants.RESTRICTION_TYPE_INHERIT)) {

			return super.getWorkflowDefinitionLink(companyId, groupId, classPK);
		}

		return null;
	}

	@Override
	public boolean isVisible() {
		return _VISIBLE;
	}

	@Override
	public JournalArticle updateStatus(
			int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		long userId = GetterUtil.getLong(
			(String)workflowContext.get(WorkflowConstants.CONTEXT_USER_ID));

		long classPK = GetterUtil.getLong(
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));

		JournalArticle article = _journalArticleLocalService.getArticle(
			classPK);

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			"serviceContext");

		String articleURL = _portal.getControlPanelFullURL(
			serviceContext.getScopeGroupId(),
			PortletProviderUtil.getPortletId(
				JournalArticle.class.getName(), PortletProvider.Action.EDIT),
			null);

		return _journalArticleLocalService.updateStatus(
			userId, article, status, articleURL, serviceContext,
			workflowContext);
	}

	private String _getHitLayoutURL(
			JournalArticle article, LiferayPortletRequest liferayPortletRequest,
			ThemeDisplay themeDisplay)
		throws PortalException {

		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.getLayoutClassedModelUsages(
				_portal.getClassNameId(JournalArticle.class),
				article.getResourcePrimKey());

		for (LayoutClassedModelUsage layoutClassedModelUsage :
				layoutClassedModelUsages) {

			Layout layout = _layoutLocalService.fetchLayout(
				layoutClassedModelUsage.getPlid());

			if ((layout != null) && !layout.isSystem() &&
				_layoutPermission.contains(
					themeDisplay.getPermissionChecker(), layout,
					ActionKeys.VIEW)) {

				return PortletURLBuilder.create(
					PortletURLFactoryUtil.create(
						liferayPortletRequest,
						layoutClassedModelUsage.getContainerKey(),
						layoutClassedModelUsage.getPlid(),
						PortletRequest.RENDER_PHASE)
				).setParameter(
					"previewClassNameId",
					layoutClassedModelUsage.getClassNameId()
				).setParameter(
					"previewClassPK", layoutClassedModelUsage.getClassPK()
				).setParameter(
					"previewType", AssetRendererFactory.TYPE_LATEST
				).setParameter(
					"previewVersion", InfoItemIdentifier.VERSION_LATEST
				).buildString();
			}
		}

		return null;
	}

	private boolean _isShowDisplayPage(
			long classPK, long groupId, JournalArticle article)
		throws PortalException {

		AssetRendererFactory<JournalArticle> assetRendererFactory =
			getAssetRendererFactory();

		AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
			getClassName(), classPK);

		if (Validator.isNull(article.getLayoutUuid()) &&
			Validator.isNull(assetEntry.getLayoutUuid()) &&
			!AssetDisplayPageUtil.hasAssetDisplayPage(
				groupId,
				assetRendererFactory.getAssetEntry(
					JournalArticle.class.getName(),
					article.getResourcePrimKey()))) {

			return false;
		}

		return true;
	}

	private static final boolean _VISIBLE = true;

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleWorkflowHandler.class);

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalFolderLocalService _journalFolderLocalService;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private Portal _portal;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}