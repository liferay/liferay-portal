/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.theme;

import com.liferay.layout.utility.page.kernel.LayoutUtilityPageEntryTypeUtil;
import com.liferay.layout.utility.page.kernel.provider.util.LayoutUtilityPageEntryLayoutProviderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspWriter;

/**
 * @author Brian Wing Shun Chan
 */
public class MetaTagsTag extends IncludeTag {

	public static void doTag(
			ServletContext servletContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		doTag(_PAGE, servletContext, httpServletRequest, httpServletResponse);
	}

	public static void doTag(
			String page, ServletContext servletContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(page);

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected int processEndTag() throws Exception {
		HttpServletResponse httpServletResponse =
			(HttpServletResponse)pageContext.getResponse();

		String layoutUtilityPageEntryType =
			LayoutUtilityPageEntryTypeUtil.getStatusLayoutUtilityPageEntryType(
				httpServletResponse.getStatus());

		if (Validator.isNotNull(layoutUtilityPageEntryType)) {
			return _setLayoutUtilityPageEntryMetaDescription(
				layoutUtilityPageEntryType);
		}

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return EVAL_PAGE;
		}

		Layout layout = themeDisplay.getLayout();

		if (layout == null) {
			return EVAL_PAGE;
		}

		String currentLanguageId = LanguageUtil.getLanguageId(
			httpServletRequest);
		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		String w3cCurrentLanguageId = LocaleUtil.toW3cLanguageId(
			currentLanguageId);
		String w3cDefaultLanguageId = LocaleUtil.toW3cLanguageId(
			defaultLanguageId);

		String metaRobots = (String)httpServletRequest.getAttribute(
			WebKeys.PAGE_ROBOTS);

		if (Validator.isNull(metaRobots)) {
			metaRobots = layout.getRobots(themeDisplay.getLanguageId(), false);

			if (Validator.isNull(metaRobots)) {
				metaRobots = layout.getRobots(defaultLanguageId);
			}
		}

		if (Validator.isNotNull(metaRobots)) {
			_writeMeta(HtmlUtil.escape(metaRobots), StringPool.BLANK, "robots");
		}

		String metaDescription = layout.getDescription(
			themeDisplay.getLanguageId(), false);
		String metaDescriptionLanguageId = w3cCurrentLanguageId;

		if (Validator.isNull(metaDescription)) {
			metaDescription = layout.getDescription(defaultLanguageId);

			if (Validator.isNotNull(metaDescription)) {
				metaDescriptionLanguageId = w3cDefaultLanguageId;
			}
		}

		ListMergeable<String> pageDescriptionListMergeable =
			(ListMergeable<String>)httpServletRequest.getAttribute(
				WebKeys.PAGE_DESCRIPTION);

		if (pageDescriptionListMergeable != null) {
			if (Validator.isNotNull(metaDescription)) {
				metaDescription = StringBundler.concat(
					pageDescriptionListMergeable.mergeToString(
						StringPool.SPACE),
					StringPool.PERIOD, StringPool.SPACE, metaDescription);
			}
			else {
				metaDescription = pageDescriptionListMergeable.mergeToString(
					StringPool.SPACE);
			}
		}

		if (Validator.isNotNull(metaDescription)) {
			_writeMeta(
				HtmlUtil.escape(metaDescription), metaDescriptionLanguageId,
				"description");
		}

		String metaKeywords = layout.getKeywords(
			themeDisplay.getLanguageId(), false);
		String metaKeywordsLanguageId = w3cCurrentLanguageId;

		if (Validator.isNull(metaKeywords)) {
			metaKeywords = layout.getKeywords(defaultLanguageId);

			if (Validator.isNotNull(metaKeywords)) {
				metaKeywordsLanguageId = w3cDefaultLanguageId;
			}
		}

		ListMergeable<String> pageKeywordsListMergeable =
			(ListMergeable<String>)httpServletRequest.getAttribute(
				WebKeys.PAGE_KEYWORDS);

		if (pageKeywordsListMergeable != null) {
			String pageKeywords = pageKeywordsListMergeable.mergeToString(
				StringPool.COMMA);

			if (Validator.isNotNull(pageKeywords) &&
				Validator.isNotNull(metaKeywords)) {

				metaKeywords = StringBundler.concat(
					pageKeywords, StringPool.COMMA, StringPool.SPACE,
					metaKeywords);
			}
			else if (Validator.isNull(metaKeywords)) {
				metaKeywords = pageKeywords;
			}
		}

		if (Validator.isNotNull(metaKeywords)) {
			_writeMeta(
				HtmlUtil.escape(metaKeywords), metaKeywordsLanguageId,
				"keywords");
		}

		return EVAL_PAGE;
	}

	private int _setLayoutUtilityPageEntryMetaDescription(
			String layoutUtilityPageEntryType)
		throws Exception {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return EVAL_PAGE;
		}

		Layout layout =
			LayoutUtilityPageEntryLayoutProviderUtil.
				getDefaultLayoutUtilityPageEntryLayout(
					themeDisplay.getScopeGroupId(), layoutUtilityPageEntryType);

		if (layout == null) {
			return EVAL_PAGE;
		}

		String languageId = LanguageUtil.getLanguageId(httpServletRequest);

		String metaDescription = layout.getDescription(languageId, false);

		if (Validator.isNull(metaDescription)) {
			languageId = LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault());

			metaDescription = layout.getDescription(languageId);
		}

		if (Validator.isNotNull(metaDescription)) {
			_writeMeta(
				HtmlUtil.escape(metaDescription),
				LocaleUtil.toW3cLanguageId(languageId), "description");
		}

		return EVAL_PAGE;
	}

	private void _writeMeta(String content, String lang, String name)
		throws Exception {

		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("<meta content=\"");
		jspWriter.write(content);

		if (!lang.equals("")) {
			jspWriter.write("\" lang=\"");
			jspWriter.write(lang);
		}

		jspWriter.write("\" name=\"");
		jspWriter.write(name);
		jspWriter.write("\" />");
	}

	private static final String _PAGE = "/html/taglib/theme/meta_tags/page.jsp";

}