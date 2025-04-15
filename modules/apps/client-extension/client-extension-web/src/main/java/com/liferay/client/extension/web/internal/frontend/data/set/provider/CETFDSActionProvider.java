/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.frontend.data.set.provider;

import com.liferay.client.extension.web.internal.constants.ClientExtensionAdminFDSNames;
import com.liferay.client.extension.web.internal.frontend.data.set.model.CETFDSEntry;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Basto
 */
@Component(
	property = "fds.data.provider.key=" + ClientExtensionAdminFDSNames.CLIENT_EXTENSION_TYPES,
	service = FDSActionProvider.class
)
public class CETFDSActionProvider implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		CETFDSEntry cetFDSEntry = (CETFDSEntry)model;

		if (cetFDSEntry.isReadOnly()) {
			return DropdownItemListBuilder.add(
				dropdownItem -> _buildViewClientExtensionEntryAction(
					cetFDSEntry, dropdownItem, httpServletRequest)
			).build();
		}

		return DropdownItemListBuilder.add(
			dropdownItem -> _buildEditClientExtensionEntryAction(
				cetFDSEntry, dropdownItem, httpServletRequest)
		).add(
			dropdownItem -> _buildDeleteClientExtensionEntryAction(
				cetFDSEntry, dropdownItem, httpServletRequest)
		).add(
			dropdownItem -> _buildExportClientExtensionEntryAction(
				cetFDSEntry, dropdownItem, httpServletRequest)
		).build();
	}

	private void _buildDeleteClientExtensionEntryAction(
		CETFDSEntry cetFDSEntry, DropdownItem dropdownItem,
		HttpServletRequest httpServletRequest) {

		dropdownItem.setData(
			HashMapBuilder.<String, Object>put(
				"confirmationMessage",
				_language.format(
					httpServletRequest,
					"are-you-sure-you-want-to-delete-x-client-extension",
					HtmlUtil.escape(cetFDSEntry.getName()))
			).put(
				"status", "warning"
			).put(
				"title",
				_language.get(httpServletRequest, "delete-client-extension")
			).build());
		dropdownItem.setHref(
			PortletURLBuilder.create(
				_getActionURL(httpServletRequest)
			).setActionName(
				"/client_extension_admin/delete_client_extension_entry"
			).setParameter(
				"externalReferenceCode", cetFDSEntry.getExternalReferenceCode()
			).buildString());
		dropdownItem.setIcon("trash");
		dropdownItem.setLabel(_getMessage(httpServletRequest, "delete"));
		dropdownItem.setTarget("async");
	}

	private void _buildEditClientExtensionEntryAction(
		CETFDSEntry cetFDSEntry, DropdownItem dropdownItem,
		HttpServletRequest httpServletRequest) {

		dropdownItem.setHref(
			PortletURLBuilder.create(
				_getRenderURL(httpServletRequest)
			).setMVCRenderCommandName(
				"/client_extension_admin/edit_client_extension_entry"
			).setRedirect(
				ParamUtil.getString(
					httpServletRequest, "currentURL",
					_portal.getCurrentURL(httpServletRequest))
			).setParameter(
				"externalReferenceCode", cetFDSEntry.getExternalReferenceCode()
			).buildPortletURL());
		dropdownItem.setIcon("pencil");
		dropdownItem.setLabel(_getMessage(httpServletRequest, "edit"));
	}

	private void _buildExportClientExtensionEntryAction(
		CETFDSEntry cetFDSEntry, DropdownItem dropdownItem,
		HttpServletRequest httpServletRequest) {

		dropdownItem.setHref(
			ResourceURLBuilder.createResourceURL(
				_getResourceURL(httpServletRequest)
			).setParameter(
				"externalReferenceCode", cetFDSEntry.getExternalReferenceCode()
			).setResourceID(
				"/client_extension_admin/export_client_extension_entry"
			).buildString());
		dropdownItem.setIcon("export");
		dropdownItem.setLabel(
			_getMessage(httpServletRequest, "export-as-json"));
	}

	private void _buildViewClientExtensionEntryAction(
		CETFDSEntry cetFDSEntry, DropdownItem dropdownItem,
		HttpServletRequest httpServletRequest) {

		dropdownItem.setHref(
			PortletURLBuilder.create(
				_getRenderURL(httpServletRequest)
			).setMVCRenderCommandName(
				"/client_extension_admin/view_client_extension_entry"
			).setRedirect(
				ParamUtil.getString(
					httpServletRequest, "currentURL",
					_portal.getCurrentURL(httpServletRequest))
			).setParameter(
				"externalReferenceCode", cetFDSEntry.getExternalReferenceCode()
			).buildPortletURL());
		dropdownItem.setIcon("view");
		dropdownItem.setLabel(_getMessage(httpServletRequest, "view"));
	}

	private PortletURL _getActionURL(HttpServletRequest httpServletRequest) {
		String portletId = _getPortletId(httpServletRequest);

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest);

		return requestBackedPortletURLFactory.createActionURL(portletId);
	}

	private String _getMessage(
		HttpServletRequest httpServletRequest, String key) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", themeDisplay.getLocale(), getClass());

		return _language.get(resourceBundle, key);
	}

	private String _getPortletId(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return portletDisplay.getId();
	}

	private PortletURL _getRenderURL(HttpServletRequest httpServletRequest) {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest);

		return requestBackedPortletURLFactory.createRenderURL(
			_getPortletId(httpServletRequest));
	}

	private ResourceURL _getResourceURL(HttpServletRequest httpServletRequest) {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest);

		return (ResourceURL)requestBackedPortletURLFactory.createResourceURL(
			_getPortletId(httpServletRequest));
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}