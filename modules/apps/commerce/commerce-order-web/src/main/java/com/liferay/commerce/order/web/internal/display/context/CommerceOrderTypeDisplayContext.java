/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context;

import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.order.web.internal.display.context.helper.CommerceOrderRequestHelper;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.CustomAttributesUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Riccardo Alberti
 */
public class CommerceOrderTypeDisplayContext {

	public CommerceOrderTypeDisplayContext(
		HttpServletRequest httpServletRequest,
		ModelResourcePermission<CommerceOrderType>
			commerceOrderTypeModelResourcePermission,
		CommerceOrderTypeService commerceOrderTypeService, Portal portal) {

		this.httpServletRequest = httpServletRequest;
		_commerceOrderTypeModelResourcePermission =
			commerceOrderTypeModelResourcePermission;
		_commerceOrderTypeService = commerceOrderTypeService;
		_portal = portal;

		commerceOrderRequestHelper = new CommerceOrderRequestHelper(
			httpServletRequest);
	}

	public String getAddCommerceOrderTypeRenderURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			commerceOrderRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_order_type/add_commerce_order_type"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public CommerceOrderType getCommerceOrderType() throws PortalException {
		long commerceOrderTypeId = ParamUtil.getLong(
			commerceOrderRequestHelper.getRequest(), "commerceOrderTypeId");

		if (commerceOrderTypeId == 0) {
			return null;
		}

		return _commerceOrderTypeService.fetchCommerceOrderType(
			commerceOrderTypeId);
	}

	public List<FDSActionDropdownItem>
			getCommerceOrderTypeFDSActionDropdownItems()
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						httpServletRequest, CommerceOrderType.class.getName(),
						PortletProvider.Action.MANAGE)
				).setMVCRenderCommandName(
					"/commerce_order_type/edit_commerce_order_type"
				).setRedirect(
					commerceOrderRequestHelper.getCurrentURL()
				).setParameter(
					"commerceOrderTypeId", "{id}"
				).buildString(),
				"pencil", "edit", LanguageUtil.get(httpServletRequest, "edit"),
				"get", null, null),
			new FDSActionDropdownItem(
				null, "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "headless"),
			new FDSActionDropdownItem(
				_getManagePermissionsURL(), null, "permissions",
				LanguageUtil.get(httpServletRequest, "permissions"), "get",
				"permissions", "modal-permissions"));
	}

	public long getCommerceOrderTypeId() throws PortalException {
		CommerceOrderType commerceOrderType = getCommerceOrderType();

		if (commerceOrderType == null) {
			return 0;
		}

		return commerceOrderType.getCommerceOrderTypeId();
	}

	public CreationMenu getCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (hasAddPermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getAddCommerceOrderTypeRenderURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							commerceOrderRequestHelper.getRequest(),
							"add-order-type"));
					dropdownItem.setTarget("modal");
				});
		}

		return creationMenu;
	}

	public String getEditCommerceOrderTypeActionURL() throws Exception {
		CommerceOrderType commerceOrderType = getCommerceOrderType();

		if (commerceOrderType == null) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				commerceOrderRequestHelper.getRequest(),
				CommercePortletKeys.COMMERCE_ORDER_TYPE,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/commerce_order_type/edit_commerce_order_type"
		).setCMD(
			Constants.UPDATE
		).setParameter(
			"commerceOrderTypeId", commerceOrderType.getCommerceOrderTypeId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public PortletURL getEditCommerceOrderTypeRenderURL() {
		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				commerceOrderRequestHelper.getRequest(),
				CommercePortletKeys.COMMERCE_ORDER_TYPE,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_order_type/edit_commerce_order_type"
		).buildPortletURL();
	}

	public List<HeaderActionModel> getHeaderActionModels() throws Exception {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		LiferayPortletResponse liferayPortletResponse =
			commerceOrderRequestHelper.getLiferayPortletResponse();

		String saveButtonLabel = "save";

		CommerceOrderType commerceOrderType = getCommerceOrderType();

		if ((commerceOrderType == null) || commerceOrderType.isDraft() ||
			commerceOrderType.isApproved() || commerceOrderType.isExpired() ||
			commerceOrderType.isScheduled()) {

			saveButtonLabel = "save-as-draft";
		}

		HeaderActionModel saveAsDraftHeaderActionModel = new HeaderActionModel(
			null, liferayPortletResponse.getNamespace() + "fm",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/commerce_order_type/edit_commerce_order_type"
			).buildString(),
			null, saveButtonLabel);

		headerActionModels.add(saveAsDraftHeaderActionModel);

		String publishButtonLabel = "publish";

		if (WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(
				commerceOrderRequestHelper.getCompanyId(),
				commerceOrderRequestHelper.getScopeGroupId(),
				CommerceOrderType.class.getName())) {

			publishButtonLabel = "submit-for-workflow";
		}

		String additionalClasses = "btn-primary";

		if ((commerceOrderType != null) && commerceOrderType.isPending()) {
			additionalClasses = additionalClasses + " disabled";
		}

		HeaderActionModel publishHeaderActionModel = new HeaderActionModel(
			additionalClasses, liferayPortletResponse.getNamespace() + "fm",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/commerce_order_type/edit_commerce_order_type"
			).buildString(),
			liferayPortletResponse.getNamespace() + "publishButton",
			publishButtonLabel);

		headerActionModels.add(publishHeaderActionModel);

		return headerActionModels;
	}

	public PortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			commerceOrderRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		long commerceOrderTypeId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderTypeId");

		if (commerceOrderTypeId > 0) {
			portletURL.setParameter(
				"commerceOrderTypeId", String.valueOf(commerceOrderTypeId));
		}

		return portletURL;
	}

	public boolean hasAddPermission() throws PortalException {
		PortletResourcePermission portletResourcePermission =
			_commerceOrderTypeModelResourcePermission.
				getPortletResourcePermission();

		return portletResourcePermission.contains(
			commerceOrderRequestHelper.getPermissionChecker(), null,
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER_TYPE);
	}

	public boolean hasCustomAttributesAvailable() throws Exception {
		return CustomAttributesUtil.hasCustomAttributes(
			commerceOrderRequestHelper.getCompanyId(),
			CommerceOrderType.class.getName(), getCommerceOrderTypeId(), null);
	}

	public boolean hasPermission(String actionId) throws PortalException {
		return _commerceOrderTypeModelResourcePermission.contains(
			commerceOrderRequestHelper.getPermissionChecker(),
			getCommerceOrderTypeId(), actionId);
	}

	protected final CommerceOrderRequestHelper commerceOrderRequestHelper;
	protected final HttpServletRequest httpServletRequest;

	private String _getManagePermissionsURL() throws PortalException {
		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setRedirect(
			commerceOrderRequestHelper.getCurrentURL()
		).setParameter(
			"modelResource", CommerceOrderType.class.getName()
		).setParameter(
			"modelResourceDescription", "{name}"
		).setParameter(
			"resourcePrimKey", "{id}"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private final ModelResourcePermission<CommerceOrderType>
		_commerceOrderTypeModelResourcePermission;
	private final CommerceOrderTypeService _commerceOrderTypeService;
	private final Portal _portal;

}