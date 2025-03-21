/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.provider.search;

import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSKeywordsFactory;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Marco Leo
 */
public class FDSKeywordsFactoryImpl implements FDSKeywordsFactory {

	@Override
	public FDSKeywords create(HttpServletRequest httpServletRequest) {
		DefaultFDSKeywordsImpl defaultFDSKeywordsImpl =
			new DefaultFDSKeywordsImpl();

		defaultFDSKeywordsImpl.setKeywords(
			ParamUtil.getString(httpServletRequest, "search"));

		return defaultFDSKeywordsImpl;
	}

}