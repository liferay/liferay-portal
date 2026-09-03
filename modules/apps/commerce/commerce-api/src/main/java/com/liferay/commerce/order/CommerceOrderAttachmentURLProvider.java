/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Balazs Breier
 */
public interface CommerceOrderAttachmentURLProvider {

	public String getDownloadURL(
		long commerceOrderAttachmentId, HttpServletRequest httpServletRequest);

}