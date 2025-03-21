/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context;

import com.liferay.commerce.model.CommerceReturn;
import com.liferay.commerce.order.web.internal.display.context.helper.CommerceReturnRequestHelper;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
import com.liferay.frontend.data.set.model.FDSSortItemList;
import com.liferay.frontend.data.set.model.FDSSortItemListBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import java.util.List;

/**
 * @author Stefano Motta
 */
public class CommerceReturnListDisplayContext {

	public CommerceReturnListDisplayContext(
		ObjectDefinitionLocalService objectDefinitionLocalService,
		RenderRequest renderRequest) {

		_objectDefinitionLocalService = objectDefinitionLocalService;

		_commerceReturnRequestHelper = new CommerceReturnRequestHelper(
			renderRequest);

		_keywords = ParamUtil.getString(renderRequest, "keywords");
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						_commerceReturnRequestHelper.getRequest(),
						CommerceReturn.class.getName(),
						PortletProvider.Action.MANAGE)
				).setMVCRenderCommandName(
					"/commerce_return/edit_commerce_return"
				).setParameter(
					"commerceReturnId", "{id}"
				).buildString(),
				"view", "view",
				LanguageUtil.get(
					_commerceReturnRequestHelper.getRequest(), "view"),
				"get", null, null),
			new FDSActionDropdownItem(
				_getPermissionsURL(), "password-policies", "permissions",
				LanguageUtil.get(
					_commerceReturnRequestHelper.getRequest(), "permissions"),
				"get", "permissions", "modal-permissions"),
			new FDSActionDropdownItem(
				null, "trash", "delete",
				LanguageUtil.get(
					_commerceReturnRequestHelper.getRequest(), "delete"),
				"delete", "delete", null));
	}

	public FDSSortItemList getFDSSortItemList() {
		return FDSSortItemListBuilder.add(
			FDSSortItemBuilder.setDirection(
				"desc"
			).setKey(
				"dateCreated"
			).build()
		).build();
	}

	public ObjectDefinition getObjectDefinition() {
		if (_objectDefinition != null) {
			return _objectDefinition;
		}

		_objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_COMMERCE_RETURN",
					_commerceReturnRequestHelper.getCompanyId());

		return _objectDefinition;
	}

	public PortletURL getPortletURL() {
		return getSearchURL();
	}

	public PortletURL getSearchURL() {
		LiferayPortletResponse liferayPortletResponse =
			_commerceReturnRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		if (Validator.isNotNull(_keywords)) {
			portletURL.setParameter("keywords", _keywords);
		}

		return portletURL;
	}

	private String _getPermissionsURL() {
		ObjectDefinition objectDefinition = getObjectDefinition();

		if (objectDefinition == null) {
			return null;
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_commerceReturnRequestHelper.getRequest(),
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setRedirect(
			_commerceReturnRequestHelper.getCurrentURL()
		).setParameter(
			"modelResource", objectDefinition.getClassName()
		).setParameter(
			"modelResourceDescription",
			objectDefinition.getLabel(_commerceReturnRequestHelper.getLocale())
		).setParameter(
			"resourcePrimKey", "{id}"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private final CommerceReturnRequestHelper _commerceReturnRequestHelper;
	private final String _keywords;
	private ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;

}