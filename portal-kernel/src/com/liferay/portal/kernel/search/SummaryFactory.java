/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.search;

import com.liferay.portal.kernel.exception.PortalException;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;

/**
 * @author André de Oliveira
 */
public interface SummaryFactory {

	public Summary getSummary(
			Document document, String className, long classPK, Locale locale,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortalException;

	public Summary getSummary(String className, long classPK, Locale locale)
		throws PortalException;

}