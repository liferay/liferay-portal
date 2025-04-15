/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.info.list.renderer;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.content.web.internal.info.item.renderer.CommerceOrderItemInfoItemRenderer;
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
 * @author Alessio Antonio Rendina
 */
public abstract class BaseCommerceOrderItemBasicInfoListRenderer
	implements BasicInfoListRenderer<CommerceOrderItem> {

	@Override
	public List<InfoItemRenderer<?>> getAvailableInfoItemRenderers() {
		return infoItemRendererRegistry.getInfoItemRenderers(
			CommerceOrderItem.class.getName());
	}

	@Override
	public void render(
		List<CommerceOrderItem> commerceOrderItems,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		render(
			commerceOrderItems,
			new DefaultInfoListRendererContext(
				httpServletRequest, httpServletResponse));
	}

	@Override
	public void render(
		List<CommerceOrderItem> commerceOrderItems,
		InfoListRendererContext infoListRendererContext) {

		InfoListBasicListTag infoListBasicListTag = new InfoListBasicListTag();

		infoListBasicListTag.setInfoListObjects(commerceOrderItems);

		String listItemRendererKey =
			infoListRendererContext.getListItemRendererKey();

		if (Validator.isNotNull(listItemRendererKey)) {
			infoListBasicListTag.setItemRendererKey(listItemRendererKey);
		}
		else {
			infoListBasicListTag.setItemRendererKey(
				CommerceOrderItemInfoItemRenderer.class.getName());
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
		BaseCommerceOrderItemBasicInfoListRenderer.class);

}