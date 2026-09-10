/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.odata.retriever;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.List;
import java.util.Locale;

/**
 * @author David Arques
 */
public interface ODataRetriever<T extends BaseModel<?>> {

	public default long[] getResultPrimaryKeys(
			long companyId, String filterString, Locale locale, int start,
			int end)
		throws PortalException {

		return TransformUtil.transformToLongArray(
			getResults(companyId, filterString, locale, start, end),
			baseModel -> (Long)baseModel.getPrimaryKeyObj());
	}

	public List<T> getResults(
			long companyId, String filterString, Locale locale, int start,
			int end)
		throws PortalException;

	public int getResultsCount(
			long companyId, String filterString, Locale locale)
		throws PortalException;

}