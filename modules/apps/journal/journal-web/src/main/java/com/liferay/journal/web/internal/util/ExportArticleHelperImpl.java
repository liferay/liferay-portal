/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.util;

import com.liferay.document.library.kernel.document.conversion.DocumentConversionUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.util.ExportArticleHelper;
import com.liferay.journal.util.JournalContent;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ExportArticleHelper.class)
public class ExportArticleHelperImpl implements ExportArticleHelper {

	@Override
	public void sendFile(
			String targetExtension, PortletRequest portletRequest,
			PortletResponse portletResponse)
		throws IOException {

		if (Validator.isNull(targetExtension)) {
			return;
		}

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			portletRequest);
		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(portletResponse);

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(portletRequest, "groupId");
		String articleId = ParamUtil.getString(portletRequest, "articleId");
		String languageId = _language.getLanguageId(portletRequest);
		PortletRequestModel portletRequestModel = new PortletRequestModel(
			portletRequest, portletResponse);

		JournalArticle article = _journalArticleLocalService.fetchLatestArticle(
			groupId, articleId, WorkflowConstants.STATUS_APPROVED);

		JournalArticleDisplay articleDisplay = _journalContent.getDisplay(
			groupId, articleId, article.getVersion(), null, "export",
			languageId, 1, portletRequestModel, themeDisplay);

		int pages = articleDisplay.getNumberOfPages();

		StringBundler sb = new StringBundler(pages + 12);

		sb.append("<html>");

		sb.append("<head>");
		sb.append("<meta content=\"");
		sb.append(ContentTypes.TEXT_HTML_UTF8);
		sb.append("\" http-equiv=\"content-type\" />");
		sb.append("<base href=\"");
		sb.append(themeDisplay.getPortalURL());
		sb.append("\" />");
		sb.append("</head>");

		sb.append("<body>");

		sb.append(articleDisplay.getContent());

		for (int i = 2; i <= pages; i++) {
			articleDisplay = _journalContent.getDisplay(
				groupId, articleId, "export", languageId, i, themeDisplay);

			sb.append(articleDisplay.getContent());
		}

		sb.append("</body>");
		sb.append("</html>");

		String s = sb.toString();

		InputStream inputStream = new UnsyncByteArrayInputStream(
			s.getBytes(StringPool.UTF8));

		String title = articleDisplay.getTitle();
		String sourceExtension = "html";

		String fileName = StringBundler.concat(
			title, StringPool.PERIOD, sourceExtension);

		String contentType = ContentTypes.TEXT_HTML;

		sb = new StringBundler(3);

		sb.append(PrincipalThreadLocal.getUserId());
		sb.append(StringPool.UNDERLINE);
		sb.append(
			DLUtil.getTempFileId(
				articleDisplay.getId(),
				String.valueOf(articleDisplay.getVersion()), languageId));

		File convertedFile = DocumentConversionUtil.convert(
			sb.toString(), inputStream, sourceExtension, targetExtension);

		if (convertedFile != null) {
			targetExtension = StringUtil.toLowerCase(targetExtension);

			fileName = StringBundler.concat(
				title, StringPool.PERIOD, targetExtension);

			contentType = MimeTypesUtil.getContentType(fileName);

			inputStream = new FileInputStream(convertedFile);
		}

		ServletResponseUtil.sendFile(
			httpServletRequest, httpServletResponse, fileName, inputStream,
			contentType);
	}

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalContent _journalContent;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}