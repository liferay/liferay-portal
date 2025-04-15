/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.catalog.web.internal.frontend.data.set.provider;

import com.liferay.commerce.catalog.web.internal.constants.CommerceCatalogFDSNames;
import com.liferay.commerce.catalog.web.internal.model.Catalog;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gianmarco Brunialti Masera
 */
@Component(
	property = "fds.data.provider.key=" + CommerceCatalogFDSNames.CATALOGS,
	service = FDSActionProvider.class
)
public class CommerceCatalogFDSActionProvider implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		Catalog catalog = (Catalog)model;

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return DropdownItemListBuilder.add(
			() -> _commerceCatalogModelResourcePermission.contains(
				permissionChecker, catalog.getCatalogId(), ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getCatalogEditURL(
						catalog.getCatalogId(), httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, Constants.EDIT));
			}
		).add(
			() -> _commerceCatalogModelResourcePermission.contains(
				permissionChecker, catalog.getCatalogId(),
				ActionKeys.PERMISSIONS),
			dropdownItem -> {
				dropdownItem.setHref(
					_getManageCatalogPermissionsURL(
						catalog, httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "permissions"));
				dropdownItem.setTarget("modal-permissions");
			}
		).add(
			() -> _commerceCatalogModelResourcePermission.contains(
				permissionChecker, catalog.getCatalogId(), ActionKeys.DELETE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getCatalogDeleteURL(
						catalog.getCatalogId(), httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, Constants.DELETE));
			}
		).build();
	}

	private PortletURL _getCatalogDeleteURL(
		long catalogId, HttpServletRequest httpServletRequest) {

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, CPPortletKeys.COMMERCE_CATALOGS,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/commerce_catalogs/edit_commerce_catalog"
		).setCMD(
			Constants.DELETE
		).buildPortletURL();

		String redirect = ParamUtil.getString(
			httpServletRequest, "currentUrl",
			_portal.getCurrentURL(httpServletRequest));

		portletURL.setParameter("redirect", redirect);

		portletURL.setParameter("commerceCatalogId", String.valueOf(catalogId));

		return portletURL;
	}

	private PortletURL _getCatalogEditURL(
		long catalogId, HttpServletRequest httpServletRequest) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, CPPortletKeys.COMMERCE_CATALOGS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_catalogs/edit_commerce_catalog"
		).setParameter(
			"commerceCatalogId", catalogId
		).buildPortletURL();
	}

	private PortletURL _getManageCatalogPermissionsURL(
			Catalog catalog, HttpServletRequest httpServletRequest)
		throws PortalException {

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "backURL",
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"modelResource", CommerceCatalog.class.getName()
		).setParameter(
			"modelResourceDescription", catalog.getName()
		).setParameter(
			"resourcePrimKey", catalog.getCatalogId()
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			throw new PortalException(windowStateException);
		}

		return portletURL;
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceCatalog)"
	)
	private ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}