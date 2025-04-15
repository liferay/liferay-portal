/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.display.context;

import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.sharing.configuration.SharingConfigurationFactory;
import com.liferay.sharing.display.context.util.SharingDropdownItemFactory;
import com.liferay.sharing.interpreter.SharingEntryInterpreterProvider;
import com.liferay.sharing.security.permission.SharingPermission;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.sharing.web.internal.filter.SharedAssetsFilterItemRegistry;
import com.liferay.sharing.web.internal.servlet.taglib.ui.SharingEntryDropdownItemContributorRegistry;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = ViewSharedAssetsDisplayContextFactory.class)
public class ViewSharedAssetsDisplayContextFactory {

	public ViewSharedAssetsDisplayContext getViewSharedAssetsDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		return new ViewSharedAssetsDisplayContext(
			_groupLocalService, _itemSelector,
			_portal.getLiferayPortletRequest(renderRequest),
			_portal.getLiferayPortletResponse(renderResponse),
			_sharedAssetsFilterItemRegistry, _sharingConfigurationFactory,
			_sharingDropdownItemFactory,
			_sharingEntryDropdownItemContributorRegistry,
			_sharingEntryInterpreterProvider::getSharingEntryInterpreter,
			_sharingEntryLocalService, _sharingPermission);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

	@Reference
	private SharedAssetsFilterItemRegistry _sharedAssetsFilterItemRegistry;

	@Reference
	private SharingConfigurationFactory _sharingConfigurationFactory;

	@Reference
	private SharingDropdownItemFactory _sharingDropdownItemFactory;

	@Reference
	private SharingEntryDropdownItemContributorRegistry
		_sharingEntryDropdownItemContributorRegistry;

	@Reference
	private SharingEntryInterpreterProvider _sharingEntryInterpreterProvider;

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

	@Reference
	private SharingPermission _sharingPermission;

}