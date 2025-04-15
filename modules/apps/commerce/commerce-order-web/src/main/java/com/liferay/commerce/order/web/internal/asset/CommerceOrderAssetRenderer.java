/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.asset;

import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.web.internal.security.permission.resource.CommerceOrderPermission;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Andrea Di Giorgi
 * @author Marco Leo
 */
public class CommerceOrderAssetRenderer
	extends BaseJSPAssetRenderer<CommerceOrder> {

	public CommerceOrderAssetRenderer(
		CommerceChannelLocalService commerceChannelLocalService,
		CommerceOrder commerceOrder) {

		_commerceChannelLocalService = commerceChannelLocalService;
		_commerceOrder = commerceOrder;
	}

	@Override
	public CommerceOrder getAssetObject() {
		return _commerceOrder;
	}

	@Override
	public String getClassName() {
		return CommerceOrder.class.getName();
	}

	@Override
	public long getClassPK() {
		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	public long getGroupId() {
		return _commerceOrder.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		return "/asset/full_content.jsp";
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		ResourceBundleLoader resourceBundleLoader =
			acquireResourceBundleLoader();

		ResourceBundle resourceBundle = resourceBundleLoader.loadResourceBundle(
			locale);

		return LanguageUtil.format(
			resourceBundle, "order-x", _commerceOrder.getCommerceOrderId());
	}

	@Override
	public String getURLViewInContext(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String noSuchEntryRedirect)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				_commerceOrder.getGroupId());

		String commerceChannelType = commerceChannel.getType();

		if (!commerceChannelType.equals(
				CommerceChannelConstants.CHANNEL_TYPE_SITE)) {

			return null;
		}

		Group group = GroupLocalServiceUtil.getGroup(
			commerceChannel.getSiteGroupId());

		PortletURL portletURL = PortletProviderUtil.getPortletURL(
			liferayPortletRequest, group, CommerceOrder.class.getName(),
			PortletProvider.Action.VIEW);

		String orderRenderCommand =
			"/commerce_order_content/view_commerce_order_details";

		if (_commerceOrder.isOpen()) {
			portletURL = PortletProviderUtil.getPortletURL(
				liferayPortletRequest, group, CommerceOrder.class.getName(),
				PortletProvider.Action.EDIT);

			orderRenderCommand = "/commerce_order/edit_commerce_order";
		}

		portletURL.setParameter("mvcRenderCommandName", orderRenderCommand);
		portletURL.setParameter(
			"commerceOrderId",
			String.valueOf(_commerceOrder.getCommerceOrderId()));
		portletURL.setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "backURL",
			PortalUtil.getCurrentURL(liferayPortletRequest));

		return portletURL.toString();
	}

	@Override
	public long getUserId() {
		return _commerceOrder.getUserId();
	}

	@Override
	public String getUserName() {
		return _commerceOrder.getUserName();
	}

	@Override
	public String getUuid() {
		return _commerceOrder.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return CommerceOrderPermission.contains(
			permissionChecker, _commerceOrder, ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return CommerceOrderPermission.contains(
			permissionChecker, _commerceOrder, ActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			CommerceOrderConstants.COMMERCE_ORDER, _commerceOrder);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isPreviewInContext() {
		return true;
	}

	private final CommerceChannelLocalService _commerceChannelLocalService;
	private final CommerceOrder _commerceOrder;

}