/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.search;

import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBTemplate;
import com.liferay.knowledge.base.web.internal.KBUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

/**
 * @author Peter Shin
 * @author Brian Wing Shun Chan
 */
public class KBTemplateSearch extends SearchContainer<KBTemplate> {

	public static final String EMPTY_RESULTS_MESSAGE =
		"no-templates-were-found";

	public KBTemplateSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest, null, null, DEFAULT_CUR_PARAM, DEFAULT_DELTA,
			iteratorURL, null, EMPTY_RESULTS_MESSAGE);

		try {
			String orderByCol = SearchOrderByUtil.getOrderByCol(
				portletRequest, KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
				"kb-templates-order-by-col", "modified-date");

			setOrderByCol(orderByCol);

			String orderByType = SearchOrderByUtil.getOrderByType(
				portletRequest, KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
				"kb-templates-order-by-type", "desc");

			setOrderByComparator(
				KBUtil.getKBTemplateOrderByComparator(orderByCol, orderByType));
			setOrderByType(orderByType);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to initialize knowledge base template search",
				exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KBTemplateSearch.class);

}