/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.util;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.util.JournalContent;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.util.UriInfoUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

import java.util.Locale;

/**
 * @author Javier Gamarra
 */
public class RenderedContentValueUtil {

	public static String renderTemplate(
			ClassNameLocalService classNameLocalService,
			DDMTemplateLocalService ddmTemplateLocalService,
			GroupLocalService groupLocalService,
			HttpServletRequest httpServletRequest,
			JournalArticleService journalArticleService,
			JournalContent journalContent, Locale locale,
			Long structuredContentId, String templateId, UriInfo uriInfo)
		throws Exception {

		JournalArticle journalArticle = journalArticleService.getLatestArticle(
			structuredContentId);

		DDMTemplate ddmTemplate = ddmTemplateLocalService.fetchTemplate(
			journalArticle.getGroupId(),
			classNameLocalService.getClassNameId(DDMStructure.class),
			templateId);

		if (ddmTemplate == null) {
			Group group = groupLocalService.getCompanyGroup(
				CompanyThreadLocal.getCompanyId());

			ddmTemplate = ddmTemplateLocalService.fetchTemplate(
				group.getGroupId(),
				classNameLocalService.getClassNameId(DDMStructure.class),
				templateId);
		}

		JournalArticleDisplay journalArticleDisplay = journalContent.getDisplay(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			ddmTemplate.getTemplateKey(), null, LocaleUtil.toLanguageId(locale),
			_getThemeDisplay(httpServletRequest, journalArticle));

		String content = journalArticleDisplay.getContent();

		UriBuilder uriBuilder = UriInfoUtil.getBaseUriBuilder(uriInfo);

		URI uri = uriBuilder.replacePath(
			"/"
		).build();

		content = content.replaceAll(
			" srcset=\"/o/", " srcset=\"" + uri + "o/");
		content = content.replaceAll(" src=\"/", " src=\"" + uri);

		return content.replaceAll("[\\t\\n]", "");
	}

	private static ThemeDisplay _getThemeDisplay(
			HttpServletRequest httpServletRequest,
			JournalArticle journalArticle)
		throws Exception {

		ServicePreAction servicePreAction = new ServicePreAction();

		HttpServletResponse httpServletResponse =
			new DummyHttpServletResponse();

		servicePreAction.servicePre(
			httpServletRequest, httpServletResponse, false);

		ThemeServicePreAction themeServicePreAction =
			new ThemeServicePreAction();

		themeServicePreAction.run(httpServletRequest, httpServletResponse);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setScopeGroupId(journalArticle.getGroupId());
		themeDisplay.setSiteGroupId(journalArticle.getGroupId());

		return themeDisplay;
	}

}