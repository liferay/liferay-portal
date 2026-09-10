/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.odata.search;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.FilterParser;

import java.util.Locale;

/**
 * @author Eduardo García
 */
public interface ODataSearchAdapter {

	public Hits search(
			long companyId, FilterParser filterParser, String filterString,
			String className, EntityModel entityModel, Locale locale, int start,
			int end)
		throws PortalException;

	public int searchCount(
			long companyId, FilterParser filterParser, String filterString,
			String className, EntityModel entityModel, Locale locale)
		throws PortalException;

	public default long[] searchPrimaryKeys(
			long companyId, FilterParser filterParser, String filterString,
			String className, EntityModel entityModel, Locale locale,
			String primaryKeyFieldName, int start, int end)
		throws PortalException {

		Hits hits = search(
			companyId, filterParser, filterString, className, entityModel,
			locale, start, end);

		Document[] documents = hits.getDocs();

		long[] primaryKeys = new long[documents.length];

		for (int i = 0; i < documents.length; i++) {
			primaryKeys[i] = GetterUtil.getLong(
				documents[i].get(primaryKeyFieldName));
		}

		return primaryKeys;
	}

}