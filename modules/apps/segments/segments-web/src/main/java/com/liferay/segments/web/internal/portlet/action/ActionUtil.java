/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.portlet.action;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;

import jakarta.portlet.PortletRequest;

import java.util.List;

/**
 * @author Eduardo García
 */
public class ActionUtil {

	public static Criteria getCriteria(
		PortletRequest portletRequest,
		List<SegmentsCriteriaContributor> segmentsCriteriaContributors) {

		Criteria criteria = new Criteria();

		for (SegmentsCriteriaContributor segmentsCriteriaContributor :
				segmentsCriteriaContributors) {

			String filterString = ParamUtil.getString(
				portletRequest,
				"criterionFilter" + segmentsCriteriaContributor.getKey());

			if (Validator.isNull(filterString)) {
				continue;
			}

			String conjunctionString = ParamUtil.getString(
				portletRequest,
				"criterionConjunction" + segmentsCriteriaContributor.getKey(),
				Criteria.Conjunction.AND.getValue());

			segmentsCriteriaContributor.contribute(
				criteria, filterString,
				Criteria.Conjunction.parse(conjunctionString));
		}

		return criteria;
	}

}