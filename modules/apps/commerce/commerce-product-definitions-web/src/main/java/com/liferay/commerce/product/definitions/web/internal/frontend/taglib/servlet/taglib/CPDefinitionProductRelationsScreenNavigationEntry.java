/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.product.definitions.web.internal.display.context.CPDefinitionLinkDisplayContext;
import com.liferay.commerce.product.links.CPDefinitionLinkTypeRegistry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CPDefinitionLinkService;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class CPDefinitionProductRelationsScreenNavigationEntry
	extends CPDefinitionProductRelationsScreenNavigationCategory
	implements ScreenNavigationEntry<CPDefinition> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, CPDefinition cpDefinition) {
		if (cpDefinition == null) {
			return false;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		CPType cpType = _cpTypeRegistry.getCPType(
			cpDefinition.getProductTypeName());

		try {
			if (_commerceCatalogModelResourcePermission.contains(
					permissionChecker, cpDefinition.getCommerceCatalog(),
					ActionKeys.VIEW) &&
				cpType.isProductRelationsEnabled()) {

				return true;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		CPDefinitionLinkDisplayContext cpDefinitionLinkDisplayContext =
			new CPDefinitionLinkDisplayContext(
				_actionHelper, httpServletRequest, _cpDefinitionLinkService,
				_cpDefinitionLinkTypeRegistry, _itemSelector,
				_workflowDefinitionLinkLocalService);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, cpDefinitionLinkDisplayContext);

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse, "/definition_links.jsp");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionProductRelationsScreenNavigationEntry.class);

	@Reference
	private ActionHelper _actionHelper;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceCatalog)"
	)
	private ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;

	@Reference
	private CPDefinitionLinkService _cpDefinitionLinkService;

	@Reference
	private CPDefinitionLinkTypeRegistry _cpDefinitionLinkTypeRegistry;

	@Reference
	private CPTypeRegistry _cpTypeRegistry;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}