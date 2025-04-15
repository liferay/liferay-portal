/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info.list.renderer;

import com.liferay.commerce.product.content.web.internal.info.item.renderer.CPDefinitionLinkInfoItemRenderer;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.info.item.renderer.InfoItemRendererRegistry;
import com.liferay.info.list.renderer.DefaultInfoListRendererContext;
import com.liferay.info.list.renderer.InfoListRendererContext;
import com.liferay.info.taglib.list.renderer.BasicInfoListRenderer;
import com.liferay.info.taglib.servlet.taglib.InfoListBasicListTag;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 * @author Alessio Antonio Rendina
 */
public abstract class BaseCPDefinitionLinkBasicInfoListRenderer
	implements BasicInfoListRenderer<CPDefinition> {

	@Override
	public List<InfoItemRenderer<?>> getAvailableInfoItemRenderers() {
		return infoItemRendererRegistry.getInfoItemRenderers(
			CPDefinition.class.getName());
	}

	@Override
	public void render(
		List<CPDefinition> cpDefinitions, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		render(
			cpDefinitions,
			new DefaultInfoListRendererContext(
				httpServletRequest, httpServletResponse));
	}

	@Override
	public void render(
		List<CPDefinition> cpDefinitions,
		InfoListRendererContext infoListRendererContext) {

		InfoListBasicListTag infoListBasicListTag = new InfoListBasicListTag();

		infoListBasicListTag.setInfoListObjects(cpDefinitions);

		String listItemRendererKey =
			infoListRendererContext.getListItemRendererKey();

		if (Validator.isNotNull(listItemRendererKey)) {
			infoListBasicListTag.setItemRendererKey(listItemRendererKey);
		}
		else {
			infoListBasicListTag.setItemRendererKey(
				CPDefinitionLinkInfoItemRenderer.class.getName());
		}

		infoListBasicListTag.setListStyleKey(getListStyle());

		try {
			infoListBasicListTag.doTag(
				infoListRendererContext.getHttpServletRequest(),
				infoListRendererContext.getHttpServletResponse());
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Reference
	protected InfoItemRendererRegistry infoItemRendererRegistry;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseCPDefinitionLinkBasicInfoListRenderer.class);

}