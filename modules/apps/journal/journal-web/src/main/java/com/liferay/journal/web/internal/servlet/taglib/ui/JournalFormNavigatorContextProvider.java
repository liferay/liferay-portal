/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.servlet.taglib.ui;

import com.liferay.frontend.taglib.form.navigator.constants.FormNavigatorConstants;
import com.liferay.frontend.taglib.form.navigator.constants.FormNavigatorContextConstants;
import com.liferay.frontend.taglib.form.navigator.context.FormNavigatorContextProvider;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = FormNavigatorContextConstants.FORM_NAVIGATOR_ID + "=" + FormNavigatorConstants.FORM_NAVIGATOR_ID_JOURNAL,
	service = FormNavigatorContextProvider.class
)
public class JournalFormNavigatorContextProvider
	implements FormNavigatorContextProvider<JournalArticle> {

	@Override
	public String getContext(JournalArticle article) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		HttpServletRequest httpServletRequest = themeDisplay.getRequest();

		String toLanguageId = ParamUtil.getString(
			httpServletRequest, "toLanguageId");

		long classNameId = BeanParamUtil.getLong(
			article, httpServletRequest, "classNameId");

		if (Validator.isNotNull(toLanguageId)) {
			return "translate";
		}
		else if ((article != null) && (article.getId() > 0)) {
			return "update";
		}
		else if (classNameId > JournalArticleConstants.CLASS_NAME_ID_DEFAULT) {
			return "default.values";
		}

		return "add";
	}

}