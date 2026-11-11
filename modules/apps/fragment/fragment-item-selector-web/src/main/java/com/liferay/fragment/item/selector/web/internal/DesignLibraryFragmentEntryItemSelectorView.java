/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.item.selector.web.internal;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.fragment.item.selector.FragmentEntryItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.ServletContext;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "item.selector.view.order:Integer=400",
	service = ItemSelectorView.class
)
public class DesignLibraryFragmentEntryItemSelectorView
	extends BaseFragmentEntryItemSelectorView {

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "design-libraries");
	}

	@Override
	public boolean isVisible(
		FragmentEntryItemSelectorCriterion fragmentEntryItemSelectorCriterion,
		ThemeDisplay themeDisplay) {

		return ArrayUtil.isNotEmpty(getGroupIds(themeDisplay));
	}

	@Override
	protected long[] getGroupIds(ThemeDisplay themeDisplay) {
		return DesignLibraryUtil.fetchConnectedDesignLibraryGroupIds(
			themeDisplay.getScopeGroupId());
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