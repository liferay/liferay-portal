/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.asset.display.page.portlet;

import com.liferay.asset.display.page.portlet.BaseAssetDisplayPageFriendlyURLResolver;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.commerce.constants.CommerceOrderWebKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutFriendlyURLComposite;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = FriendlyURLResolver.class)
public class CommerceOrderAssetDisplayPageFriendlyURLResolver
	extends BaseAssetDisplayPageFriendlyURLResolver {

	@Override
	public String getActualURL(
			long companyId, long groupId, boolean privateLayout,
			String mainPath, String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException {

		String urlSeparator = getURLSeparator();

		long commerceOrderId = Long.valueOf(
			friendlyURL.substring(urlSeparator.length()));

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.fetchCommerceOrder(commerceOrderId);

		if (commerceOrder == null) {
			return null;
		}

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)requestContext.get("request");

		if (commerceOrder.isOpen()) {
			_setCurrentCommerceOrder(commerceOrder, httpServletRequest);
		}

		httpServletRequest.setAttribute(
			CommerceWebKeys.COMMERCE_ORDER, commerceOrder);

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			_getLayoutDisplayPageObjectProvider(commerceOrder);

		if ((layoutDisplayPageObjectProvider == null) ||
			!AssetDisplayPageUtil.hasAssetDisplayPage(
				groupId, layoutDisplayPageObjectProvider.getClassNameId(),
				layoutDisplayPageObjectProvider.getClassPK(),
				layoutDisplayPageObjectProvider.getClassTypeId())) {

			return null;
		}

		return super.getActualURL(
			companyId, groupId, privateLayout, mainPath, friendlyURL, params,
			requestContext);
	}

	@Override
	public String getDefaultURLSeparator() {
		return FriendlyURLResolverConstants.URL_SEPARATOR_COMMERCE_ORDER;
	}

	@Override
	public String getKey() {
		return CommerceOrder.class.getName();
	}

	@Override
	public LayoutFriendlyURLComposite getLayoutFriendlyURLComposite(
			long companyId, long groupId, boolean privateLayout,
			String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException {

		String urlSeparator = getURLSeparator();

		long commerceOrderId = Long.valueOf(
			friendlyURL.substring(urlSeparator.length()));

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.fetchCommerceOrder(commerceOrderId);

		if (commerceOrder == null) {
			return null;
		}

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			_getLayoutDisplayPageObjectProvider(commerceOrder);

		if ((layoutDisplayPageObjectProvider != null) &&
			AssetDisplayPageUtil.hasAssetDisplayPage(
				groupId, layoutDisplayPageObjectProvider.getClassNameId(),
				layoutDisplayPageObjectProvider.getClassPK(),
				layoutDisplayPageObjectProvider.getClassTypeId())) {

			return super.getLayoutFriendlyURLComposite(
				companyId, groupId, privateLayout, friendlyURL, params,
				requestContext);
		}

		return new LayoutFriendlyURLComposite(
			null, getURLSeparator() + commerceOrder.getCommerceOrderId(),
			false);
	}

	@Override
	public boolean isURLSeparatorConfigurable() {
		return true;
	}

	private LayoutDisplayPageObjectProvider<?>
		_getLayoutDisplayPageObjectProvider(CommerceOrder commerceOrder) {

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					CommerceOrder.class.getName());

		InfoItemReference infoItemReference = new InfoItemReference(
			CommerceOrder.class.getName(), commerceOrder.getCommerceOrderId());

		return layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
			infoItemReference);
	}

	private void _setCurrentCommerceOrder(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest)
		throws PortalException {

		httpServletRequest.setAttribute(
			CommerceOrderWebKeys.MERGE_GUEST_ORDER, Boolean.FALSE);

		CommerceContext commerceContext = _commerceContextFactory.create(
			httpServletRequest);

		httpServletRequest.setAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT, commerceContext);

		CommerceOrder currentCommerceOrder =
			_commerceOrderHttpHelper.getCurrentCommerceOrder(
				httpServletRequest);

		if ((currentCommerceOrder == null) ||
			(commerceOrder.getCommerceOrderId() !=
				currentCommerceOrder.getCommerceOrderId())) {

			_commerceOrderHttpHelper.setCurrentCommerceOrder(
				httpServletRequest, commerceOrder);
		}
	}

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

}