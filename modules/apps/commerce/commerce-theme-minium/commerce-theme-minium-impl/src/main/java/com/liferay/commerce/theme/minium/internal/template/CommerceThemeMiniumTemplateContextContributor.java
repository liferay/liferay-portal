/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.theme.minium.internal.template;

import com.liferay.application.list.PanelAppRegistry;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.theme.minium.internal.helper.CommerceThemeMiniumHttpHelper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "type=" + TemplateContextContributor.TYPE_THEME,
	service = TemplateContextContributor.class
)
public class CommerceThemeMiniumTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		contextObjects.put(
			"commerceThemeMiniumHttpHelper", _commerceThemeMiniumHttpHelper);
	}

	@Activate
	protected void activate() {
		_commerceThemeMiniumHttpHelper = new CommerceThemeMiniumHttpHelper(
			_commerceAccountHelper, _language, _panelAppRegistry, _portal);
	}

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	private CommerceThemeMiniumHttpHelper _commerceThemeMiniumHttpHelper;

	@Reference
	private Language _language;

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	@Reference
	private Portal _portal;

}