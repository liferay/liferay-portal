/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.SitePage;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Lourdes Fernández Besada
 */
public class SitePageTypeUtil {

	public static SitePage.Type toExternalType(String internalType) {
		Set<SitePage.Type> externalTypes =
			_externalToInternalValuesMap.keySet();

		for (SitePage.Type externalType : externalTypes) {
			if (Objects.equals(
					internalType,
					_externalToInternalValuesMap.get(externalType))) {

				return externalType;
			}
		}

		throw new UnsupportedOperationException();
	}

	public static String toInternalType(SitePage.Type externalType) {
		String internalType = _externalToInternalValuesMap.get(externalType);

		if (internalType != null) {
			return internalType;
		}

		throw new UnsupportedOperationException();
	}

	private static final Map<SitePage.Type, String>
		_externalToInternalValuesMap = HashMapBuilder.put(
			SitePage.Type.CONTENT_PAGE, LayoutConstants.TYPE_CONTENT
		).put(
			SitePage.Type.EMBEDDED_PAGE, LayoutConstants.TYPE_EMBEDDED
		).put(
			SitePage.Type.LINK_TO_PAGE_PAGE, LayoutConstants.TYPE_LINK_TO_LAYOUT
		).put(
			SitePage.Type.LINK_TO_URL_PAGE, LayoutConstants.TYPE_URL
		).put(
			SitePage.Type.PAGE_SET_PAGE, LayoutConstants.TYPE_NODE
		).put(
			SitePage.Type.WIDGET_PAGE, LayoutConstants.TYPE_PORTLET
		).build();

}