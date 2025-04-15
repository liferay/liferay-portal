/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.portlet.template;

import com.liferay.commerce.product.content.helper.CPCompareContentHelper;
import com.liferay.commerce.product.content.helper.CPContentHelper;
import com.liferay.portal.kernel.template.TemplateContextContributor;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "type=" + TemplateContextContributor.TYPE_GLOBAL,
	service = TemplateContextContributor.class
)
public class CPCompareContentTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		contextObjects.put("cpCompareContentHelper", _cpCompareContentHelper);
		contextObjects.put("cpContentHelper", _cpContentHelper);
	}

	@Reference
	private CPCompareContentHelper _cpCompareContentHelper;

	@Reference
	private CPContentHelper _cpContentHelper;

}