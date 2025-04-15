/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorView;
import com.liferay.layout.item.selector.criterion.LayoutItemSelectorCriterion;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.servlet.ServletContext;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(property = "view=private", service = ItemSelectorView.class)
public class PrivateLayoutsItemSelectorView
	extends BaseLayoutsItemSelectorView {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public String getTitle(Locale locale) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			Group group = _groupLocalService.fetchGroup(
				serviceContext.getScopeGroupId());

			if (!group.isPrivateLayoutsEnabled() &&
				group.isLayoutSetPrototype()) {

				return ResourceBundleUtil.getString(
					_portal.getResourceBundle(locale), "pages");
			}
		}

		return ResourceBundleUtil.getString(
			_portal.getResourceBundle(locale), "private-pages");
	}

	@Override
	public boolean isPrivateLayout() {
		return true;
	}

	@Override
	public boolean isVisible(
		LayoutItemSelectorCriterion layoutItemSelectorCriterion,
		ThemeDisplay themeDisplay) {

		Group group = themeDisplay.getScopeGroup();

		if (!group.isLayoutSetPrototype() && !group.isPrivateLayoutsEnabled()) {
			return false;
		}

		return true;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.item.selector.web)"
	)
	private ServletContext _servletContext;

}