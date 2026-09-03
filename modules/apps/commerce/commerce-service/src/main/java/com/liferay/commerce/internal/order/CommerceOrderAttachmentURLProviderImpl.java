/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.order;

import com.liferay.commerce.order.CommerceOrderAttachmentURLProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Balazs Breier
 */
@Component(service = CommerceOrderAttachmentURLProvider.class)
public class CommerceOrderAttachmentURLProviderImpl
	implements CommerceOrderAttachmentURLProvider {

	@Override
	public String getDownloadURL(
		long commerceOrderAttachmentId, HttpServletRequest httpServletRequest) {

		return StringBundler.concat(
			_portal.getPortalURL(httpServletRequest), _portal.getPathModule(),
			"/commerce-order-attachment/", commerceOrderAttachmentId);
	}

	@Reference
	private Portal _portal;

}