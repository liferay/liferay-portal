/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context.helper;

import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Marcellus Tavares
 */
public class DDMFormAdminRequestHelper extends BaseRequestHelper {

	public DDMFormAdminRequestHelper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

	public DDMFormAdminRequestHelper(RenderRequest renderRequest) {
		super(PortalUtil.getHttpServletRequest(renderRequest));
	}

	@Override
	public Locale getLocale() {
		String languageId = LanguageUtil.getLanguageId(getRequest());

		if (languageId != null) {
			return LocaleUtil.fromLanguageId(languageId);
		}

		return super.getLocale();
	}

}