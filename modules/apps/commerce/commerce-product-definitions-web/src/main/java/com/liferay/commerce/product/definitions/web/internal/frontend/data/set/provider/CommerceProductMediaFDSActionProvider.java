/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.frontend.data.set.provider;

import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.definitions.web.internal.constants.CommerceProductFDSNames;
import com.liferay.commerce.product.definitions.web.internal.model.ProductMedia;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"fds.data.provider.key=" + CommerceProductFDSNames.PRODUCT_ATTACHMENTS,
		"fds.data.provider.key=" + CommerceProductFDSNames.PRODUCT_IMAGES
	},
	service = FDSActionProvider.class
)
public class CommerceProductMediaFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		ProductMedia productMedia = (ProductMedia)model;

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpAttachmentFileEntryService.getCPAttachmentFileEntry(
				productMedia.getCPAttachmentFileEntryId());

		return DropdownItemListBuilder.add(
			() -> _hasManagePermission(
				cpAttachmentFileEntry,
				PermissionThreadLocal.getPermissionChecker()),
			dropdownItem -> {
				dropdownItem.setHref(
					_getProductMediaEditURL(
						cpAttachmentFileEntry, httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
				dropdownItem.setTarget("sidePanel");
			}
		).add(
			() -> _hasManagePermission(
				cpAttachmentFileEntry,
				PermissionThreadLocal.getPermissionChecker()),
			dropdownItem -> {
				dropdownItem.setHref(
					_getProductMediaDeleteURL(
						cpAttachmentFileEntry.getCPAttachmentFileEntryId(),
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "delete"));
			}
		).build();
	}

	private PortletURL _getProductMediaDeleteURL(
			long cpAttachmentFileEntryId, HttpServletRequest httpServletRequest)
		throws PortalException {

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				_portal.getOriginalServletRequest(httpServletRequest),
				CPPortletKeys.CP_DEFINITIONS, PortletRequest.ACTION_PHASE)
		).setActionName(
			"/cp_definitions/edit_cp_attachment_file_entry"
		).setCMD(
			Constants.DELETE
		).buildPortletURL();

		String redirect = ParamUtil.getString(
			httpServletRequest, "currentUrl",
			_portal.getCurrentURL(httpServletRequest));

		portletURL.setParameter("redirect", redirect);

		portletURL.setParameter(
			"cpAttachmentFileEntryId", String.valueOf(cpAttachmentFileEntryId));

		return portletURL;
	}

	private PortletURL _getProductMediaEditURL(
			CPAttachmentFileEntry cpAttachmentFileEntry,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		PortletURL portletURL = PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CPDefinition.class.getName(),
				PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_attachment_file_entry"
		).setParameter(
			"cpAttachmentFileEntryId",
			cpAttachmentFileEntry.getCPAttachmentFileEntryId()
		).setParameter(
			"cpDefinitionId", cpAttachmentFileEntry.getClassPK()
		).setParameter(
			"type", cpAttachmentFileEntry.getType()
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return portletURL;
	}

	private boolean _hasManagePermission(
		CPAttachmentFileEntry cpAttachmentFileEntry,
		PermissionChecker permissionChecker) {

		int type = cpAttachmentFileEntry.getType();

		if (type == CPAttachmentFileEntryConstants.TYPE_IMAGE) {
			return _portletResourcePermission.contains(
				permissionChecker, cpAttachmentFileEntry.getGroupId(),
				CPActionKeys.MANAGE_COMMERCE_PRODUCT_IMAGES);
		}
		else if (type == CPAttachmentFileEntryConstants.TYPE_OTHER) {
			return _portletResourcePermission.contains(
				permissionChecker, cpAttachmentFileEntry.getGroupId(),
				CPActionKeys.MANAGE_COMMERCE_PRODUCT_ATTACHMENTS);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceProductMediaFDSActionProvider.class);

	@Reference
	private CPAttachmentFileEntryService _cpAttachmentFileEntryService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + CPConstants.RESOURCE_NAME_PRODUCT + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}