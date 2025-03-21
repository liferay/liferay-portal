/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.taglib.internal.jaxrs.context.provider;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

/**
 * @author Marco Leo
 */
@Provider
public class SortContextProvider implements ContextProvider<Sort> {

	@Override
	public Sort createContext(Message message) {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)message.getContextualProperty("HTTP.REQUEST");

		String sortString = ParamUtil.getString(
			httpServletRequest, "sort.field");
		String sortDir = ParamUtil.getString(httpServletRequest, "sort.dir");

		if (Validator.isNotNull(sortString) && Validator.isNotNull(sortDir)) {
			return SortFactoryUtil.create(
				StringUtil.trim(sortString), sortDir.equals("desc"));
		}

		String sort = ParamUtil.getString(httpServletRequest, "sort");

		if (Validator.isNull(sort)) {
			return null;
		}

		String[] sortArray = StringUtil.split(sort, StringPool.COLON);

		return SortFactoryUtil.create(
			StringUtil.trim(sortArray[0]), sortArray[1].equals("desc"));
	}

}