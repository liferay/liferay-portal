/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.taglib.aui.base.BaseModelContextTag;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 */
public class ModelContextTag extends BaseModelContextTag {

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		Class<?> model = getModel();

		if (model != null) {
			pageContext.setAttribute("aui:model-context:bean", getBean());
			pageContext.setAttribute(
				"aui:model-context:defaultLanguageId", getDefaultLanguageId());
			pageContext.setAttribute("aui:model-context:model", model);
		}
		else {
			pageContext.removeAttribute("aui:model-context:bean");
			pageContext.removeAttribute("aui:model-context:defaultLanguageId");
			pageContext.removeAttribute("aui:model-context:model");
		}
	}

}