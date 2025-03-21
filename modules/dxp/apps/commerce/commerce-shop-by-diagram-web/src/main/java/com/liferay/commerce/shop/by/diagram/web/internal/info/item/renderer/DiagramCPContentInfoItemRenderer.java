/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.web.internal.info.item.renderer;

import com.liferay.commerce.product.content.info.item.renderer.CPContentInfoItemRenderer;
import com.liferay.commerce.shop.by.diagram.constants.CSDiagramWebKeys;
import com.liferay.commerce.shop.by.diagram.util.CSDiagramCPTypeHelper;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"commerce.product.content.info.item.renderer.key=" + DiagramCPContentInfoItemRenderer.KEY,
		"commerce.product.content.info.item.renderer.order=400"
	},
	service = CPContentInfoItemRenderer.class
)
public class DiagramCPContentInfoItemRenderer
	implements CPContentInfoItemRenderer {

	public static final String KEY = "diagram";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(resourceBundle, KEY);
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(
			CSDiagramWebKeys.CS_DIAGRAM_CP_TYPE_HELPER, _csDiagramCPTypeHelper);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/info/item/renderer/diagram_card/page.jsp");
	}

	@Reference
	private CSDiagramCPTypeHelper _csDiagramCPTypeHelper;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.shop.by.diagram.web)"
	)
	private ServletContext _servletContext;

}