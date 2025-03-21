/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.locked.layouts.web.internal.portlet.action;

import com.liferay.layout.locked.layouts.web.internal.constants.LockedLayoutsPortletKeys;
import com.liferay.locked.items.constants.LockedItemsPortletKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LockedItemsPortletKeys.LOCKED_ITEMS,
		"jakarta.portlet.name=" + LockedLayoutsPortletKeys.LOCKED_LAYOUTS_PORTLET,
		"mvc.command.name=/layout_locked_layouts/unlock_layouts",
		"mvc.command.name=/locked_items/unlock_layouts"
	},
	service = MVCActionCommand.class
)
public class UnlockLayoutsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		GroupPermissionUtil.check(
			themeDisplay.getPermissionChecker(), themeDisplay.getScopeGroup(),
			ActionKeys.UPDATE);

		long[] plids;

		long plid1 = ParamUtil.getLong(actionRequest, "plid");

		if (plid1 > 0) {
			plids = new long[] {plid1};
		}
		else {
			plids = ParamUtil.getLongValues(actionRequest, "rowIds");
		}

		for (long plid2 : plids) {
			_lockManager.unlock(Layout.class.getName(), plid2);
		}

		hideDefaultSuccessMessage(actionRequest);

		SessionMessages.add(
			actionRequest, "unlockLayoutsRequestProcessed",
			_language.format(
				themeDisplay.getLocale(), "x-pages-were-successfully-unlocked",
				new String[] {String.valueOf(plids.length)}));
	}

	@Reference
	private Language _language;

	@Reference
	private LockManager _lockManager;

}