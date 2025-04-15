/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.grouped.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.type.grouped.service.CPDefinitionGroupedEntryService;
import com.liferay.commerce.product.type.grouped.web.internal.display.context.CPDefinitionGroupedEntriesDisplayContext;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author João Victor Alves
 */
@Component(
	property = "screen.navigation.entry.order:Integer=20",
	service = ScreenNavigationEntry.class
)
public class CPDefinitionCPTypeGroupedScreenNavigationEntry
	extends CPDefinitionCPTypeGroupedScreenNavigationCategory
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

		String productTypeName = cpDefinition.getProductTypeName();

		return productTypeName.equals(getCategoryKey());
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			CPDefinitionGroupedEntriesDisplayContext
				cpDefinitionGroupedEntriesDisplayContext =
					new CPDefinitionGroupedEntriesDisplayContext(
						_actionHelper, httpServletRequest,
						_cpDefinitionGroupedEntryService, _itemSelector);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				cpDefinitionGroupedEntriesDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		_jspRenderer.renderJSP(
			_setServletContext, httpServletRequest, httpServletResponse,
			"/view_cp_definition_grouped_entries.jsp");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionCPTypeGroupedScreenNavigationEntry.class);

	@Reference
	private ActionHelper _actionHelper;

	@Reference
	private CPDefinitionGroupedEntryService _cpDefinitionGroupedEntryService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.type.grouped.web)"
	)
	private ServletContext _setServletContext;

}