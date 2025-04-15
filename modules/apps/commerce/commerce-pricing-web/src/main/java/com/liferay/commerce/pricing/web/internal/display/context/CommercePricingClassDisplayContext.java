/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.display.context;

import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.pricing.constants.CommercePricingClassActionKeys;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.service.CommercePricingClassService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.RenderURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Riccardo Alberti
 */
public class CommercePricingClassDisplayContext
	extends BasePricingDisplayContext {

	public CommercePricingClassDisplayContext(
		HttpServletRequest httpServletRequest,
		ModelResourcePermission<CommercePricingClass>
			commercePricingClassModelResourcePermission,
		CommercePricingClassService commercePricingClassService,
		Portal portal) {

		super(httpServletRequest);

		_commercePricingClassModelResourcePermission =
			commercePricingClassModelResourcePermission;
		_commercePricingClassService = commercePricingClassService;
		_portal = portal;
	}

	public String getAddCommercePricingClassRenderURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			commercePricingRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_pricing_classes/add_commerce_pricing_class"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public CommercePricingClass getCommercePricingClass()
		throws PortalException {

		long commercePricingClassId = ParamUtil.getLong(
			commercePricingRequestHelper.getRequest(),
			"commercePricingClassId");

		if (commercePricingClassId == 0) {
			return null;
		}

		return _commercePricingClassService.fetchCommercePricingClass(
			commercePricingClassId);
	}

	public long getCommercePricingClassId() throws PortalException {
		CommercePricingClass commercePricingClass = getCommercePricingClass();

		if (commercePricingClass == null) {
			return 0;
		}

		return commercePricingClass.getCommercePricingClassId();
	}

	public CreationMenu getCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (hasAddPermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getAddCommercePricingClassRenderURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							commercePricingRequestHelper.getRequest(),
							"add-product-group"));
					dropdownItem.setTarget("modal");
				});
		}

		return creationMenu;
	}

	public String getEditCommercePricingClassActionURL() throws Exception {
		CommercePricingClass commercePricingClass = getCommercePricingClass();

		if (commercePricingClass == null) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				commercePricingRequestHelper.getRequest(),
				CommercePricingPortletKeys.COMMERCE_PRICING_CLASSES,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/commerce_pricing_classes/edit_commerce_pricing_class"
		).setCMD(
			Constants.UPDATE
		).setParameter(
			"commercePricingClassId",
			commercePricingClass.getCommercePricingClassId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public PortletURL getEditCommercePricingClassRenderURL() {
		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				commercePricingRequestHelper.getRequest(),
				CommercePricingPortletKeys.COMMERCE_PRICING_CLASSES,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_pricing_classes/edit_commerce_pricing_class"
		).buildPortletURL();
	}

	public List<HeaderActionModel> getHeaderActionModels() throws Exception {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		RenderResponse renderResponse =
			commercePricingRequestHelper.getRenderResponse();

		RenderURL cancelURL = renderResponse.createRenderURL();

		HeaderActionModel cancelHeaderActionModel = new HeaderActionModel(
			null, cancelURL.toString(), null, "cancel");

		headerActionModels.add(cancelHeaderActionModel);

		if (hasPermission(ActionKeys.UPDATE)) {
			headerActionModels.add(
				new HeaderActionModel(
					"btn-primary", liferayPortletResponse.getNamespace() + "fm",
					getEditCommercePricingClassActionURL(), null, "save"));
		}

		return headerActionModels;
	}

	public boolean hasAddPermission() throws PortalException {
		PortletResourcePermission portletResourcePermission =
			_commercePricingClassModelResourcePermission.
				getPortletResourcePermission();

		return portletResourcePermission.contains(
			commercePricingRequestHelper.getPermissionChecker(), null,
			CommercePricingClassActionKeys.ADD_COMMERCE_PRICING_CLASS);
	}

	public boolean hasPermission(String actionId) throws PortalException {
		return _commercePricingClassModelResourcePermission.contains(
			commercePricingRequestHelper.getPermissionChecker(),
			getCommercePricingClassId(), actionId);
	}

	private final ModelResourcePermission<CommercePricingClass>
		_commercePricingClassModelResourcePermission;
	private final CommercePricingClassService _commercePricingClassService;
	private final Portal _portal;

}