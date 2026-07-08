/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.template;

import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(
	property = "type=" + TemplateContextContributor.TYPE_GLOBAL,
	service = TemplateContextContributor.class
)
public class CommerceOrderHttpHelperTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		contextObjects.put("commerceOrderHttpHelper", _commerceOrderHttpHelper);
		contextObjects.put(
			"commerceReturnsEnabled",
			FeatureFlagManagerUtil.isEnabled(
				_getCompanyId(httpServletRequest), "LPD-10562"));
	}

	private long _getCompanyId(HttpServletRequest httpServletRequest) {
		if (httpServletRequest == null) {
			return CompanyThreadLocal.getCompanyId();
		}

		return _portal.getCompanyId(httpServletRequest);
	}

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private Portal _portal;

}