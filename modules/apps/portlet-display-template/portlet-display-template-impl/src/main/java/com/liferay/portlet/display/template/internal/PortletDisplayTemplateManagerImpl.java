/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.display.template.internal;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portletdisplaytemplate.PortletDisplayTemplateManager;
import com.liferay.portlet.display.template.PortletDisplayTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(service = PortletDisplayTemplateManager.class)
public class PortletDisplayTemplateManagerImpl
	implements PortletDisplayTemplateManager {

	@Override
	public String renderDDMTemplate(
			long classNameId, Map<String, Object> contextObjects,
			String ddmTemplateKey, List<?> entries, long groupId,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, boolean useDefault)
		throws Exception {

		DDMTemplate ddmTemplate =
			_portletDisplayTemplate.getPortletDisplayTemplateDDMTemplate(
				groupId, classNameId, DISPLAY_STYLE_PREFIX + ddmTemplateKey,
				useDefault);

		if (ddmTemplate == null) {
			return StringPool.BLANK;
		}

		return _portletDisplayTemplate.renderDDMTemplate(
			httpServletRequest, httpServletResponse,
			ddmTemplate.getTemplateId(), entries, contextObjects);
	}

	@Reference
	private PortletDisplayTemplate _portletDisplayTemplate;

}