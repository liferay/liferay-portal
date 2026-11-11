/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.item.selector.web.internal;

import com.liferay.fragment.item.selector.FragmentEntryItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.ServletContext;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "item.selector.view.order:Integer=200",
	service = ItemSelectorView.class
)
public class GlobalFragmentEntryItemSelectorView
	extends BaseFragmentEntryItemSelectorView {

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "global");
	}

	@Override
	public boolean isVisible(
		FragmentEntryItemSelectorCriterion itemSelectorCriterion,
		ThemeDisplay themeDisplay) {

		return !Objects.equals(
			themeDisplay.getScopeGroupId(), themeDisplay.getCompanyGroupId());
	}

	@Override
	protected long[] getGroupIds(ThemeDisplay themeDisplay) {
		return new long[] {themeDisplay.getCompanyGroupId()};
	}

	@Override
	protected Portal getPortal() {
		return _portal;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.fragment.item.selector.web)"
	)
	private ServletContext _servletContext;

}