/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.punchout.web.internal.theme.template;

import com.liferay.commerce.punchout.web.internal.helper.PunchOutSessionHelper;
import com.liferay.portal.kernel.template.TemplateContextContributor;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jaclyn Ong
 */
@Component(
	property = "type=" + TemplateContextContributor.TYPE_THEME,
	service = TemplateContextContributor.class
)
public class CommercePunchOutThemeTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		contextObjects.put(
			"commercePunchOutThemeHttpHelper",
			_commercePunchOutThemeHttpHelper);
	}

	public class CommercePunchOutThemeHttpHelper {

		public boolean punchOutSession(HttpServletRequest httpServletRequest) {
			if (_punchOutSessionHelper.punchOutEnabled(httpServletRequest) &&
				_punchOutSessionHelper.punchOutAllowed(httpServletRequest)) {

				return true;
			}

			return false;
		}

	}

	@Activate
	protected void activate() {
		_commercePunchOutThemeHttpHelper =
			new CommercePunchOutThemeHttpHelper();
	}

	private CommercePunchOutThemeHttpHelper _commercePunchOutThemeHttpHelper;

	@Reference
	private PunchOutSessionHelper _punchOutSessionHelper;

}