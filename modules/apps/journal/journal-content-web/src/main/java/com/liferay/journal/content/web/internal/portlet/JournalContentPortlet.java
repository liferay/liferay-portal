/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.portlet;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalWebKeys;
import com.liferay.journal.content.web.internal.display.context.JournalContentDisplayContext;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.util.ExportArticleHelper;
import com.liferay.journal.util.JournalContent;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.service.TrashEntryService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-journal-content",
		"com.liferay.portlet.display-category=category.cms",
		"com.liferay.portlet.display-category=category.highlighted",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.icon=/icons/journal_content.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Web Content Display",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + JournalContentPortletKeys.JOURNAL_CONTENT,
		"jakarta.portlet.portlet-mode=application/vnd.wap.xhtml+xml;view",
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class JournalContentPortlet extends MVCPortlet {

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletPreferences portletPreferences = renderRequest.getPreferences();

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long articleGroupId = 0;

		String articleGroupExternalReferenceCode = PrefsParamUtil.getString(
			portletPreferences, renderRequest, "groupExternalReferenceCode");

		if (Validator.isNotNull(articleGroupExternalReferenceCode)) {
			Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
				articleGroupExternalReferenceCode, themeDisplay.getCompanyId());

			if (group != null) {
				articleGroupId = group.getGroupId();
			}
		}

		if (articleGroupId == 0) {
			articleGroupId = themeDisplay.getScopeGroupId();
		}

		String articleExternalReferenceCode = PrefsParamUtil.getString(
			portletPreferences, renderRequest, "articleExternalReferenceCode");

		JournalArticle article = null;
		JournalArticleDisplay articleDisplay = null;

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		JournalContentDisplayContext journalContentDisplayContext =
			(JournalContentDisplayContext)renderRequest.getAttribute(
				JournalContentDisplayContext.getRequestAttributeName(
					portletDisplay.getId()));

		if (journalContentDisplayContext != null) {
			try {
				article = journalContentDisplayContext.getArticle();
				articleDisplay =
					journalContentDisplayContext.getArticleDisplay();
			}
			catch (PortalException portalException) {
				_log.error("Unable to get journal article", portalException);
			}
		}
		else if ((articleGroupId > 0) &&
				 Validator.isNotNull(articleExternalReferenceCode)) {

			String viewMode = ParamUtil.getString(renderRequest, "viewMode");
			String languageId = _language.getLanguageId(renderRequest);
			int page = ParamUtil.getInteger(renderRequest, "page", 1);

			article =
				_journalArticleLocalService.
					fetchLatestArticleByExternalReferenceCode(
						articleGroupId, articleExternalReferenceCode,
						WorkflowConstants.STATUS_APPROVED, false);

			try {
				if (article == null) {
					article =
						_journalArticleLocalService.
							getLatestArticleByExternalReferenceCode(
								articleGroupId, articleExternalReferenceCode,
								WorkflowConstants.STATUS_ANY, false);
				}

				String ddmTemplateKey = null;

				String ddmTemplateExternalReferenceCode =
					PrefsParamUtil.getString(
						portletPreferences, renderRequest,
						"ddmTemplateExternalReferenceCode");

				DDMTemplate ddmTemplate =
					_ddmTemplateLocalService.
						fetchDDMTemplateByExternalReferenceCode(
							ddmTemplateExternalReferenceCode,
							article.getGroupId(), true);

				if (ddmTemplate != null) {
					ddmTemplateKey = ddmTemplate.getTemplateKey();
				}

				if (Validator.isNull(ddmTemplateKey)) {
					ddmTemplateKey = article.getDDMTemplateKey();
				}

				articleDisplay = _journalContent.getDisplay(
					article, ddmTemplateKey, viewMode, languageId, page,
					new PortletRequestModel(renderRequest, renderResponse),
					themeDisplay);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				renderRequest.removeAttribute(WebKeys.JOURNAL_ARTICLE);
			}
		}

		if (article != null) {
			renderRequest.setAttribute(WebKeys.JOURNAL_ARTICLE, article);
		}

		if (articleDisplay != null) {
			renderRequest.setAttribute(
				WebKeys.JOURNAL_ARTICLE_DISPLAY, articleDisplay);
		}
		else {
			renderRequest.removeAttribute(WebKeys.JOURNAL_ARTICLE_DISPLAY);
		}

		super.doView(renderRequest, renderResponse);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			JournalWebKeys.JOURNAL_CONTENT, _journalContent);

		try {
			JournalContentDisplayContext.create(
				renderRequest, renderResponse, _ddmTemplateLocalService,
				_ddmTemplateModelResourcePermission, _itemSelector, _portal,
				_trashHelper);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		super.render(renderRequest, renderResponse);
	}

	public void restoreJournalArticle(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long classPK = ParamUtil.getLong(actionRequest, "classPK");

		_trashEntryService.restoreEntry(
			JournalArticle.class.getName(), classPK);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		String resourceID = GetterUtil.getString(
			resourceRequest.getResourceID());

		if (resourceID.equals("exportArticle")) {
			String targetExtension = ParamUtil.getString(
				resourceRequest, "targetExtension");

			targetExtension = StringUtil.toUpperCase(targetExtension);

			PortletPreferences portletPreferences =
				resourceRequest.getPreferences();

			String[] allowedExtensions = StringUtil.split(
				portletPreferences.getValue(
					"userToolAssetAddonEntryKeys", null));

			if (ArrayUtil.contains(
					allowedExtensions,
					"enable" + StringUtil.toUpperCase(targetExtension))) {

				_exportArticleHelper.sendFile(
					targetExtension, resourceRequest, resourceResponse);
			}
		}
		else {
			resourceRequest.setAttribute(
				JournalWebKeys.JOURNAL_CONTENT, _journalContent);

			try {
				JournalContentDisplayContext.create(
					resourceRequest, resourceResponse, _ddmTemplateLocalService,
					_ddmTemplateModelResourcePermission, _itemSelector, _portal,
					_trashHelper);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}

			super.serveResource(resourceRequest, resourceResponse);
		}
	}

	@Activate
	protected void activate() {
		_portletRegistry.registerAlias(
			_ALIAS, JournalContentPortletKeys.JOURNAL_CONTENT);
	}

	@Deactivate
	protected void deactivate() {
		_portletRegistry.unregisterAlias(_ALIAS);
	}

	private static final String _ALIAS = "web-content";

	private static final Log _log = LogFactoryUtil.getLog(
		JournalContentPortlet.class);

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMTemplate)"
	)
	private ModelResourcePermission<DDMTemplate>
		_ddmTemplateModelResourcePermission;

	@Reference
	private ExportArticleHelper _exportArticleHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalContent _journalContent;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortletRegistry _portletRegistry;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.journal.content.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

	@Reference
	private TrashEntryService _trashEntryService;

	@Reference
	private TrashHelper _trashHelper;

}