/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.web.internal.display.context;

import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.term.constants.CommerceTermEntryActionKeys;
import com.liferay.commerce.term.constants.CommerceTermEntryPortletKeys;
import com.liferay.commerce.term.entry.type.CommerceTermEntryType;
import com.liferay.commerce.term.entry.type.CommerceTermEntryTypeRegistry;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryService;
import com.liferay.commerce.term.web.internal.display.context.helper.CommerceTermEntryRequestHelper;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
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
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceTermEntryDisplayContext {

	public CommerceTermEntryDisplayContext(
		ModelResourcePermission<CommerceTermEntry>
			commerceTermEntryModelResourcePermission,
		CommerceTermEntryTypeRegistry commerceTermEntryTypeRegistry,
		CommerceTermEntryService commerceTermEntryService,
		HttpServletRequest httpServletRequest, Portal portal) {

		_commerceTermEntryModelResourcePermission =
			commerceTermEntryModelResourcePermission;
		_commerceTermEntryTypeRegistry = commerceTermEntryTypeRegistry;
		_commerceTermEntryService = commerceTermEntryService;
		this.httpServletRequest = httpServletRequest;
		this.portal = portal;

		commerceTermEntryRequestHelper = new CommerceTermEntryRequestHelper(
			httpServletRequest);
	}

	public String getAddCommerceTermEntryRenderURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			commerceTermEntryRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_term_entry/add_commerce_term_entry"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public CommerceTermEntry getCommerceTermEntry() throws PortalException {
		long commerceTermEntryId = ParamUtil.getLong(
			commerceTermEntryRequestHelper.getRequest(), "commerceTermEntryId");

		if (commerceTermEntryId == 0) {
			return null;
		}

		return _commerceTermEntryService.fetchCommerceTermEntry(
			commerceTermEntryId);
	}

	public List<FDSActionDropdownItem>
			getCommerceTermEntryFDSActionDropdownItems()
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						httpServletRequest, CommerceTermEntry.class.getName(),
						PortletProvider.Action.MANAGE)
				).setMVCRenderCommandName(
					"/commerce_term_entry/edit_commerce_term_entry"
				).setRedirect(
					commerceTermEntryRequestHelper.getCurrentURL()
				).setParameter(
					"commerceTermEntryId", "{id}"
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

	public long getCommerceTermEntryId() throws PortalException {
		CommerceTermEntry commerceTermEntry = getCommerceTermEntry();

		if (commerceTermEntry == null) {
			return 0;
		}

		return commerceTermEntry.getCommerceTermEntryId();
	}

	public List<CommerceTermEntryType> getCommerceTermEntryTypes() {
		return _commerceTermEntryTypeRegistry.getCommerceTermEntryTypes();
	}

	public CreationMenu getCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (hasAddPermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getAddCommerceTermEntryRenderURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							commerceTermEntryRequestHelper.getRequest(),
							"add-term"));
					dropdownItem.setTarget("modal");
				});
		}

		return creationMenu;
	}

	public PortletURL getEditCommerceTermEntryRenderURL() {
		return PortletURLBuilder.create(
			portal.getControlPanelPortletURL(
				commerceTermEntryRequestHelper.getRequest(),
				CommerceTermEntryPortletKeys.COMMERCE_TERM_ENTRY,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_term_entry/edit_commerce_term_entry"
		).buildPortletURL();
	}

	public List<HeaderActionModel> getHeaderActionModels() throws Exception {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		LiferayPortletResponse liferayPortletResponse =
			commerceTermEntryRequestHelper.getLiferayPortletResponse();

		String saveButtonLabel = "save";

		CommerceTermEntry commerceTermEntry = getCommerceTermEntry();

		if ((commerceTermEntry == null) || commerceTermEntry.isDraft() ||
			commerceTermEntry.isApproved() || commerceTermEntry.isExpired() ||
			commerceTermEntry.isScheduled()) {

			saveButtonLabel = "save-as-draft";
		}

		HeaderActionModel saveAsDraftHeaderActionModel = new HeaderActionModel(
			null, liferayPortletResponse.getNamespace() + "fm",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/commerce_term_entry/edit_commerce_term_entry"
			).buildString(),
			null, saveButtonLabel);

		headerActionModels.add(saveAsDraftHeaderActionModel);

		String publishButtonLabel = "publish";

		if (WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(
				commerceTermEntryRequestHelper.getCompanyId(),
				commerceTermEntryRequestHelper.getScopeGroupId(),
				CommerceTermEntry.class.getName())) {

			publishButtonLabel = "submit-for-workflow";
		}

		String additionalClasses = "btn-primary";

		if ((commerceTermEntry != null) && commerceTermEntry.isPending()) {
			additionalClasses = additionalClasses + " disabled";
		}

		HeaderActionModel publishHeaderActionModel = new HeaderActionModel(
			additionalClasses, liferayPortletResponse.getNamespace() + "fm",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/commerce_term_entry/edit_commerce_term_entry"
			).buildString(),
			liferayPortletResponse.getNamespace() + "publishButton",
			publishButtonLabel);

		headerActionModels.add(publishHeaderActionModel);

		return headerActionModels;
	}

	public PortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			commerceTermEntryRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		long commerceTermEntryId = ParamUtil.getLong(
			httpServletRequest, "commerceTermEntryId");

		if (commerceTermEntryId > 0) {
			portletURL.setParameter(
				"commerceTermEntryId", String.valueOf(commerceTermEntryId));
		}

		return portletURL;
	}

	public boolean hasAddPermission() throws PortalException {
		PortletResourcePermission portletResourcePermission =
			_commerceTermEntryModelResourcePermission.
				getPortletResourcePermission();

		return portletResourcePermission.contains(
			commerceTermEntryRequestHelper.getPermissionChecker(), null,
			CommerceTermEntryActionKeys.ADD_COMMERCE_TERM_ENTRY);
	}

	public boolean hasPermission(String actionId) throws PortalException {
		return _commerceTermEntryModelResourcePermission.contains(
			commerceTermEntryRequestHelper.getPermissionChecker(),
			getCommerceTermEntryId(), actionId);
	}

	protected final CommerceTermEntryRequestHelper
		commerceTermEntryRequestHelper;
	protected final HttpServletRequest httpServletRequest;
	protected final Portal portal;

	private String _getManagePermissionsURL() throws PortalException {
		return PortletURLBuilder.create(
			portal.getControlPanelPortletURL(
				httpServletRequest,
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setRedirect(
			commerceTermEntryRequestHelper.getCurrentURL()
		).setParameter(
			"modelResource", CommerceTermEntry.class.getName()
		).setParameter(
			"modelResourceDescription", "{name}"
		).setParameter(
			"resourcePrimKey", "{id}"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private final ModelResourcePermission<CommerceTermEntry>
		_commerceTermEntryModelResourcePermission;
	private final CommerceTermEntryService _commerceTermEntryService;
	private final CommerceTermEntryTypeRegistry _commerceTermEntryTypeRegistry;

}