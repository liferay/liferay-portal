/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet;

import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.knowledge.base.exception.KBArticleContentException;
import com.liferay.knowledge.base.exception.KBArticlePriorityException;
import com.liferay.knowledge.base.exception.KBArticleTitleException;
import com.liferay.knowledge.base.exception.KBCommentContentException;
import com.liferay.knowledge.base.exception.NoSuchArticleException;
import com.liferay.knowledge.base.exception.NoSuchCommentException;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.service.KBCommentLocalService;
import com.liferay.knowledge.base.service.KBCommentService;
import com.liferay.knowledge.base.service.KBFolderService;
import com.liferay.knowledge.base.service.KBTemplateService;
import com.liferay.knowledge.base.util.AdminHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.rss.util.RSSUtil;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.util.TrashWebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseKBPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);

		if (Validator.isNotNull(cmd) && cmd.equals("compareVersions")) {
			_compareVersions(renderRequest);
		}

		renderRequest.setAttribute(TrashWebKeys.TRASH_HELPER, trashHelper);

		doRender(renderRequest, renderResponse);

		super.render(renderRequest, renderResponse);
	}

	public void serveKBArticleRSS(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		PortletPreferences portletPreferences =
			resourceRequest.getPreferences();

		boolean enableRss = GetterUtil.getBoolean(
			portletPreferences.getValue("enableRss", null), true);

		if (!portal.isRSSFeedsEnabled() || !enableRss) {
			portal.sendRSSFeedsDisabledError(resourceRequest, resourceResponse);

			return;
		}

		long resourcePrimKey = ParamUtil.getLong(
			resourceRequest, "resourcePrimKey");

		int max = ParamUtil.getInteger(
			resourceRequest, "max", SearchContainer.DEFAULT_DELTA);
		String type = ParamUtil.getString(
			resourceRequest, "type", RSSUtil.FORMAT_DEFAULT);
		double version = ParamUtil.getDouble(
			resourceRequest, "version", RSSUtil.VERSION_DEFAULT);
		String displayStyle = ParamUtil.getString(
			resourceRequest, "displayStyle", RSSUtil.DISPLAY_STYLE_DEFAULT);
		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String rss = kbArticleService.getKBArticleRSS(
			resourcePrimKey, WorkflowConstants.STATUS_APPROVED, max, type,
			version, displayStyle, themeDisplay);

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, null,
			rss.getBytes(StringPool.UTF8), ContentTypes.TEXT_XML_UTF8, null,
			HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		try {
			String resourceID = resourceRequest.getResourceID();

			if (resourceID.equals("compareVersions")) {
				try {
					StringBundler sb = new StringBundler(3);

					long resourcePrimKey = ParamUtil.getLong(
						resourceRequest, "resourcePrimKey");
					double sourceVersion = ParamUtil.getDouble(
						resourceRequest, "filterSourceVersion");
					double targetVersion = ParamUtil.getDouble(
						resourceRequest, "filterTargetVersion");

					String diffHtmlResults = adminHelper.getKBArticleDiff(
						resourcePrimKey, GetterUtil.getInteger(sourceVersion),
						GetterUtil.getInteger(targetVersion), "content");

					if (Validator.isNotNull(diffHtmlResults)) {
						sb.append("<div class=\"taglib-diff-html\">");
						sb.append(diffHtmlResults);
						sb.append("</div>");
					}
					else {
						sb.append("<div class=\"alert alert-info\">");
						sb.append(
							language.get(
								portal.getHttpServletRequest(resourceRequest),
								"these-versions-are-not-comparable"));
						sb.append("</div>");
					}

					ServletResponseUtil.write(
						portal.getHttpServletResponse(resourceResponse),
						sb.toString());
				}
				catch (Exception exception) {
					PortalUtil.sendError(
						exception,
						PortalUtil.getHttpServletRequest(resourceRequest),
						PortalUtil.getHttpServletResponse(resourceResponse));
				}
			}
			else if (resourceID.equals("kbArticleRSS")) {
				serveKBArticleRSS(resourceRequest, resourceResponse);
			}
		}
		catch (IOException | PortletException exception) {
			throw exception;
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	protected abstract void doRender(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException;

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof AssetCategoryException ||
			throwable instanceof AssetTagException ||
			throwable instanceof FileNameException ||
			throwable instanceof FileSizeException ||
			throwable instanceof KBArticleContentException ||
			throwable instanceof KBArticlePriorityException ||
			throwable instanceof KBArticleTitleException ||
			throwable instanceof KBCommentContentException ||
			throwable instanceof NoSuchArticleException ||
			throwable instanceof NoSuchCommentException ||
			throwable instanceof NoSuchFileException ||
			throwable instanceof PrincipalException ||
			super.isSessionErrorException(throwable)) {

			return true;
		}

		return false;
	}

	@Reference
	protected AdminHelper adminHelper;

	@Reference
	protected KBArticleService kbArticleService;

	@Reference
	protected KBCommentLocalService kbCommentLocalService;

	@Reference
	protected KBCommentService kbCommentService;

	@Reference
	protected KBFolderService kbFolderService;

	@Reference
	protected KBTemplateService kbTemplateService;

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	@Reference
	protected TrashHelper trashHelper;

	private void _compareVersions(RenderRequest renderRequest)
		throws PortletException {

		long resourcePrimKey = ParamUtil.getLong(
			renderRequest, "resourcePrimKey");
		double sourceVersion = ParamUtil.getDouble(
			renderRequest, "sourceVersion");
		double targetVersion = ParamUtil.getDouble(
			renderRequest, "targetVersion");

		String diffHtmlResults = null;

		try {
			diffHtmlResults = adminHelper.getKBArticleDiff(
				resourcePrimKey, GetterUtil.getInteger(sourceVersion),
				GetterUtil.getInteger(targetVersion), "content");
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		renderRequest.setAttribute(WebKeys.DIFF_HTML_RESULTS, diffHtmlResults);
	}

}