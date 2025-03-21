/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.display.context;

import com.liferay.portal.kernel.display.context.DisplayContextFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Iván Zaera
 */
public interface MBDisplayContextFactory extends DisplayContextFactory {

	public MBAdminListDisplayContext getMBAdminListDisplayContext(
		MBAdminListDisplayContext parentMBAdminListDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, long categoryId);

	public MBHomeDisplayContext getMBHomeDisplayContext(
		MBHomeDisplayContext parentMBHomeDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public MBListDisplayContext getMBListDisplayContext(
		MBListDisplayContext parentMBListDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, long categoryId);

}