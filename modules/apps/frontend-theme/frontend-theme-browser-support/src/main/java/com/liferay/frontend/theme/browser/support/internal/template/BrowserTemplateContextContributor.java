/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.theme.browser.support.internal.template;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.servlet.BrowserSnifferUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Chema Balsas
 */
@Component(
	property = "type=" + TemplateContextContributor.TYPE_THEME,
	service = TemplateContextContributor.class
)
public class BrowserTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		StringBundler sb = new StringBundler(4);

		sb.append(GetterUtil.getString(contextObjects.get("bodyCssClass")));
		sb.append(StringPool.SPACE);
		sb.append(BrowserSnifferUtil.getBrowserId(httpServletRequest));

		if (BrowserSnifferUtil.isMobile(httpServletRequest)) {
			sb.append(" mobile");
		}

		contextObjects.put("bodyCssClass", sb.toString());
	}

}