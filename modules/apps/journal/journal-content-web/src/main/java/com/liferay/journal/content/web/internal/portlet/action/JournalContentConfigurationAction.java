/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.portlet.action;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.item.selector.ItemSelector;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalWebKeys;
import com.liferay.journal.content.web.internal.display.context.JournalContentDisplayContext;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.util.JournalContent;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Douglas Wong
 * @author Raymond Augé
 */
@Component(
	property = "jakarta.portlet.name=" + JournalContentPortletKeys.JOURNAL_CONTENT,
	service = ConfigurationAction.class
)
public class JournalContentConfigurationAction
	extends DefaultConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/configuration.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		httpServletRequest.setAttribute(
			ItemSelector.class.getName(), _itemSelector);
		httpServletRequest.setAttribute(
			JournalWebKeys.JOURNAL_CONTENT, _journalContent);

		try {
			JournalContentDisplayContext.create(
				portletRequest, portletResponse, _ddmTemplateLocalService,
				_ddmTemplateModelResourcePermission, _itemSelector, _portal,
				_trashHelper);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		setPreference(
			actionRequest, "articleExternalReferenceCode",
			_getArticleExternalReferenceCode(actionRequest));

		String[] contentMetadataAssetAddonEntryKeys =
			ParamUtil.getParameterValues(
				actionRequest, "contentMetadataAssetAddonEntryKeys");

		setPreference(
			actionRequest, "contentMetadataAssetAddonEntryKeys",
			StringUtil.merge(contentMetadataAssetAddonEntryKeys));

		String ddmTemplateKey = ParamUtil.getString(
			actionRequest, "ddmTemplateKey");

		String ddmTemplateExternalReferenceCode = StringPool.BLANK;

		if (Validator.isNotNull(ddmTemplateKey)) {
			DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
				themeDisplay.getScopeGroupId(),
				_portal.getClassNameId(DDMStructure.class), ddmTemplateKey,
				true);

			if (ddmTemplate != null) {
				ddmTemplateExternalReferenceCode =
					ddmTemplate.getExternalReferenceCode();
			}
		}

		setPreference(
			actionRequest, "ddmTemplateExternalReferenceCode",
			ddmTemplateExternalReferenceCode);

		long groupId = _getArticleGroupId(actionRequest);

		if (groupId > 0) {
			Group group = _groupLocalService.fetchGroup(groupId);

			if (group != null) {
				setPreference(
					actionRequest, "groupExternalReferenceCode",
					group.getExternalReferenceCode());
			}
		}

		String[] userToolAssetAddonEntryKeys = ParamUtil.getParameterValues(
			actionRequest, "userToolAssetAddonEntryKeys");

		setPreference(
			actionRequest, "userToolAssetAddonEntryKeys",
			StringUtil.merge(userToolAssetAddonEntryKeys));

		_addDDMTemplateLinks(actionRequest);

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	private void _addDDMTemplateLinks(ActionRequest actionRequest)
		throws Exception {

		JournalArticle journalArticle =
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					_getArticleGroupId(actionRequest),
					_getArticleExternalReferenceCode(actionRequest),
					WorkflowConstants.STATUS_APPROVED, true);

		if (journalArticle == null) {
			return;
		}

		String compositeClassName = ResourceActionsUtil.getCompositeModelName(
			JournalArticle.class.getName(), DDMTemplate.class.getName());

		_ddmTemplateLinkLocalService.deleteTemplateLink(
			_portal.getClassNameId(compositeClassName), journalArticle.getId());

		long ddmTemplateId = _getDDMTemplateId(actionRequest);

		if (ddmTemplateId == 0) {
			return;
		}

		_ddmTemplateLinkLocalService.addTemplateLink(
			_portal.getClassNameId(compositeClassName), journalArticle.getId(),
			ddmTemplateId);
	}

	private String _getArticleExternalReferenceCode(
			PortletRequest portletRequest)
		throws Exception {

		long assetEntryId = GetterUtil.getLong(
			getParameter(portletRequest, "assetEntryId"));

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			assetEntryId);

		if (assetEntry == null) {
			return StringPool.BLANK;
		}

		AssetRendererFactory<JournalArticle> articleAssetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				JournalArticle.class);

		if (articleAssetRendererFactory == null) {
			return StringPool.BLANK;
		}

		AssetRenderer<JournalArticle> articleAssetRenderer =
			articleAssetRendererFactory.getAssetRenderer(
				assetEntry.getClassPK());

		if (articleAssetRenderer == null) {
			return StringPool.BLANK;
		}

		JournalArticle article = articleAssetRenderer.getAssetObject();

		return article.getExternalReferenceCode();
	}

	private long _getArticleGroupId(PortletRequest portletRequest) {
		long assetEntryId = GetterUtil.getLong(
			getParameter(portletRequest, "assetEntryId"));

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			assetEntryId);

		if (assetEntry == null) {
			return 0;
		}

		return assetEntry.getGroupId();
	}

	private long _getDDMTemplateId(PortletRequest portletRequest) {
		String ddmTemplateExternalReferenceCode = getParameter(
			portletRequest, "ddmTemplateExternalReferenceCode");

		if (Validator.isNull(ddmTemplateExternalReferenceCode)) {
			return 0;
		}

		DDMTemplate ddmTemplate =
			_ddmTemplateLocalService.fetchDDMTemplateByExternalReferenceCode(
				ddmTemplateExternalReferenceCode,
				_getArticleGroupId(portletRequest), true);

		if (ddmTemplate == null) {
			return 0;
		}

		return ddmTemplate.getTemplateId();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalContentConfigurationAction.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private DDMTemplateLinkLocalService _ddmTemplateLinkLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMTemplate)"
	)
	private ModelResourcePermission<DDMTemplate>
		_ddmTemplateModelResourcePermission;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalContent _journalContent;

	@Reference
	private Portal _portal;

	@Reference
	private TrashHelper _trashHelper;

}