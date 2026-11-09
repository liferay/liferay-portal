/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.catalog.web.internal.portlet.action;

import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.catalog.web.internal.display.context.CommerceCatalogDisplayContext;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.inventory.method.CommerceInventoryMethodRegistry;
import com.liferay.commerce.media.CommerceCatalogDefaultImage;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.product.configuration.AttachmentsConfiguration;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	configurationPid = "com.liferay.commerce.product.configuration.AttachmentsConfiguration",
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_CATALOGS,
		"mvc.command.name=/commerce_catalogs/edit_commerce_catalog_external_reference_code"
	},
	service = MVCRenderCommand.class
)
public class EditCommerceCatalogExternalReferenceCodeMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		CommerceCatalogDisplayContext commerceCatalogDisplayContext =
			new CommerceCatalogDisplayContext(
				_accountEntryService, _attachmentsConfiguration,
				_portal.getHttpServletRequest(renderRequest),
				_commerceCatalogDefaultImage, _commerceCatalogService,
				_commerceCatalogModelResourcePermission,
				_commerceCurrencyLocalService, _commerceInventoryMethodRegistry,
				_commercePriceListService, _configurationProvider,
				_dlAppService, _itemSelector, _portal);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, commerceCatalogDisplayContext);

		return "/commerce_catalog/edit_commerce_external_reference_code.jsp";
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_attachmentsConfiguration = ConfigurableUtil.createConfigurable(
			AttachmentsConfiguration.class, properties);
	}

	@Reference
	private AccountEntryService _accountEntryService;

	private volatile AttachmentsConfiguration _attachmentsConfiguration;

	@Reference
	private CommerceCatalogDefaultImage _commerceCatalogDefaultImage;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceCatalog)"
	)
	private ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;

	@Reference
	private CommerceCatalogService _commerceCatalogService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceInventoryMethodRegistry _commerceInventoryMethodRegistry;

	@Reference
	private CommercePriceListService _commercePriceListService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

}