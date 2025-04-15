/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.account.admin.web.internal.constants.AccountWebKeys;
import com.liferay.account.admin.web.internal.display.AccountEntryDisplayFactoryUtil;
import com.liferay.account.model.AccountEntry;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Albert Lee
 * @author Alessio Antonio Rendina
 */
public abstract class BaseAccountEntryScreenNavigationEntry
	extends BaseAccountEntryScreenNavigationCategory
	implements ScreenNavigationEntry<AccountEntry> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	public abstract String getJspPath();

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		long accountEntryId = ParamUtil.getLong(
			httpServletRequest, "accountEntryId");

		httpServletRequest.setAttribute(
			AccountWebKeys.ACCOUNT_ENTRY_DISPLAY,
			AccountEntryDisplayFactoryUtil.create(
				accountEntryId, httpServletRequest));

		jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse, getJspPath());
	}

	@Reference
	protected JSPRenderer jspRenderer;

}