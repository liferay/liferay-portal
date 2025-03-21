/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.asset;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portlet.asset.model.impl.AssetEntryImpl;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = "jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER,
	service = AssetRendererFactory.class
)
public class CommerceOrderAssetRendererFactory
	extends BaseAssetRendererFactory<CommerceOrder> {

	public static final String TYPE = "commerce-order";

	public CommerceOrderAssetRendererFactory() {
		setCategorizable(false);
		setClassName(CommerceOrder.class.getName());
		setPortletId(CommercePortletKeys.COMMERCE_ORDER);
		setSelectable(false);
	}

	@Override
	public AssetEntry getAssetEntry(String className, long classPK)
		throws PortalException {

		AssetEntry assetEntry = new AssetEntryImpl();

		assetEntry.setClassName(className);
		assetEntry.setClassPK(classPK);

		return assetEntry;
	}

	@Override
	public AssetRenderer<CommerceOrder> getAssetRenderer(long classPK, int type)
		throws PortalException {

		CommerceOrderAssetRenderer commerceOrderAssetRenderer =
			new CommerceOrderAssetRenderer(
				_commerceChannelLocalService,
				_commerceOrderLocalService.getCommerceOrder(classPK));

		commerceOrderAssetRenderer.setAssetRendererType(type);
		commerceOrderAssetRenderer.setServletContext(_servletContext);

		return commerceOrderAssetRenderer;
	}

	@Override
	public String getIconCssClass() {
		return "list";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.order.web)"
	)
	private ServletContext _servletContext;

}