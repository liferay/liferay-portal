/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.display.context;

import com.liferay.portal.kernel.display.context.BaseDisplayContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

/**
 * @author Iván Zaera
 */
public abstract class BaseDLDisplayContext<T extends DLDisplayContext>
	extends BaseDisplayContext<T> implements DLDisplayContext {

	public BaseDLDisplayContext(
		UUID uuid, T parentDLDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		super(
			uuid, parentDLDisplayContext, httpServletRequest,
			httpServletResponse);
	}

}