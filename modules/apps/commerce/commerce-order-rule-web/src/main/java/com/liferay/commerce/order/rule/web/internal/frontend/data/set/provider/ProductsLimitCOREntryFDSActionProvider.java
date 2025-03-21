/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.web.internal.frontend.data.set.provider;

import com.liferay.commerce.order.rule.constants.COREntryPortletKeys;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.web.internal.constants.COREntryFDSNames;
import com.liferay.commerce.order.rule.web.internal.frontend.model.Product;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + COREntryFDSNames.COR_ENTRY_PRODUCTS_LIMITS,
	service = FDSActionProvider.class
)
public class ProductsLimitCOREntryFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		long corEntryId = ParamUtil.getLong(httpServletRequest, "corEntryId");

		Product product = (Product)model;

		return DropdownItemListBuilder.add(
			() -> _corEntryModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), corEntryId,
				ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getConfigurableProductDeleteURL(
						corEntryId, product.getCProductId(),
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "delete"));
			}
		).build();
	}

	private String _getConfigurableProductDeleteURL(
		long corEntryId, long cProductId,
		HttpServletRequest httpServletRequest) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, COREntryPortletKeys.COR_ENTRY,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/cor_entry/edit_cor_entry"
		).setCMD(
			"deleteProduct"
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"corEntryId", corEntryId
		).setParameter(
			"cProductId", cProductId
		).buildString();
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.order.rule.model.COREntry)"
	)
	private ModelResourcePermission<COREntry> _corEntryModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}