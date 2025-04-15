/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.content.web.internal.portlet.action;

import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.type.virtual.order.constants.CommerceVirtualOrderPortletKeys;
import com.liferay.commerce.product.type.virtual.order.content.web.internal.display.context.CommerceVirtualOrderItemContentDisplayContext;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemLocalService;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingLocalService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;

import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "jakarta.portlet.name=" + CommerceVirtualOrderPortletKeys.COMMERCE_VIRTUAL_ORDER_ITEM_CONTENT,
	service = ConfigurationAction.class
)
public class CommerceVirtualOrderItemContentConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/configuration.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			CommerceVirtualOrderItemContentDisplayContext
				commerceVirtualOrderItemContentDisplayContext =
					new CommerceVirtualOrderItemContentDisplayContext(
						_commerceChannelLocalService,
						_commerceVirtualOrderItemLocalService,
						_commerceVirtualOrderItemFileEntryModelResourcePermission,
						_cpDefinitionHelper,
						_cpDefinitionVirtualSettingLocalService,
						_cpInstanceHelper, _groupLocalService,
						httpServletRequest);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceVirtualOrderItemContentDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceVirtualOrderItemContentConfigurationAction.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry)"
	)
	private ModelResourcePermission<CommerceVirtualOrderItemFileEntry>
		_commerceVirtualOrderItemFileEntryModelResourcePermission;

	@Reference
	private CommerceVirtualOrderItemLocalService
		_commerceVirtualOrderItemLocalService;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPDefinitionVirtualSettingLocalService
		_cpDefinitionVirtualSettingLocalService;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private GroupLocalService _groupLocalService;

}