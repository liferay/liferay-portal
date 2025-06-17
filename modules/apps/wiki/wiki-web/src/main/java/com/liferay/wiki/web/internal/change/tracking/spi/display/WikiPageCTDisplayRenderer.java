/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.HorizontalCardTag;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.engine.WikiEngineRenderer;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalService;
import com.liferay.wiki.web.internal.frontend.taglib.clay.servlet.taglib.WikiPageAttachmentHorizontalCard;
import com.liferay.wiki.web.internal.util.WikiUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noor Najjar
 */
@Component(service = CTDisplayRenderer.class)
public class WikiPageCTDisplayRenderer extends BaseCTDisplayRenderer<WikiPage> {

	@Override
	public WikiPage fetchLatestVersionedModel(WikiPage wikiPage) {
		return _wikiPageLocalService.fetchLatestPage(
			wikiPage.getResourcePrimKey(), WorkflowConstants.STATUS_ANY, false);
	}

	@Override
	public String getEditURL(
		HttpServletRequest httpServletRequest, WikiPage wikiPage) {

		return String.valueOf(_getEditURL(httpServletRequest, wikiPage));
	}

	@Override
	public Class<WikiPage> getModelClass() {
		return WikiPage.class;
	}

	@Override
	public String getTitle(Locale locale, WikiPage wikiPage) {
		return StringBundler.concat(
			wikiPage.getTitle(), " (", wikiPage.getVersion(), ")");
	}

	@Override
	public String getVersionName(WikiPage wikiPage) {
		return String.valueOf(wikiPage.getVersion());
	}

	@Override
	public String renderPreview(DisplayContext<WikiPage> displayContext)
		throws Exception {

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		PortletURL viewPageURL = PortletURLBuilder.createRenderURL(
			_portal.getLiferayPortletResponse(portletResponse)
		).setMVCRenderCommandName(
			"/wiki/view_page"
		).buildPortletURL();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		WikiPage wikiPage = displayContext.getModel();

		String content = _wikiEngineRenderer.convert(
			wikiPage, viewPageURL, _getEditURL(httpServletRequest, wikiPage),
			WikiUtil.getAttachmentURLPrefix(
				themeDisplay.getPathMain(), themeDisplay.getPlid(),
				wikiPage.getNodeId(), wikiPage.getTitle()));

		List<FileEntry> fileEntries = wikiPage.getAttachmentsFileEntries();

		if (fileEntries.isEmpty()) {
			return content;
		}

		StringBundler sb = new StringBundler(content);

		sb.append("<div class=\"page-attachments\"><div class=\"h5\">");
		sb.append(_language.get(httpServletRequest, "attachments"));
		sb.append("</div><div class=\"row\">");

		for (FileEntry fileEntry : fileEntries) {
			sb.append("<div class=\"col-md-4\">");

			HorizontalCardTag horizontalCardTag = new HorizontalCardTag();

			horizontalCardTag.setHorizontalCard(
				new WikiPageAttachmentHorizontalCard(
					fileEntry, httpServletRequest));

			sb.append(
				horizontalCardTag.doTagAsString(
					httpServletRequest,
					displayContext.getHttpServletResponse()));

			sb.append("</div>");
		}

		sb.append("</div>");

		return sb.toString();
	}

	@Override
	public boolean showPreviewDiff() {
		return true;
	}

	@Override
	protected void buildDisplay(DisplayBuilder<WikiPage> displayBuilder) {
		WikiPage wikiPage = displayBuilder.getModel();

		displayBuilder.display(
			"name", wikiPage.getTitle()
		).display(
			"created-by",
			() -> {
				String userName = wikiPage.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", wikiPage.getCreateDate()
		).display(
			"last-modified", wikiPage.getModifiedDate()
		);
	}

	private PortletURL _getEditURL(
		HttpServletRequest httpServletRequest, WikiPage wikiPage) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, WikiPortletKeys.WIKI_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/wiki/edit_page"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"nodeId", wikiPage.getNodeId()
		).setParameter(
			"title", wikiPage.getTitle()
		).buildPortletURL();
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private WikiEngineRenderer _wikiEngineRenderer;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

}