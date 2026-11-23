/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.tab.item;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

/**
 * @author Lino Alves
 */
public class DDMFormAdminFieldSetTabItem extends DDMFormAdminTabItem {

	public DDMFormAdminFieldSetTabItem(Language language) {
		super(language);
	}

	@Override
	public String getTitle(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		return language.get(
			liferayPortletRequest.getHttpServletRequest(), "element-sets");
	}

	@Override
	public String getURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return PortletURLBuilder.create(
			getPortletURL(liferayPortletRequest)
		).setParameter(
			"currentTab", "element-set"
		).buildString();
	}

}