/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.batch.exportimport.internal.engine;

import com.liferay.analytics.batch.exportimport.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.analytics.batch.exportimport.internal.engine.util.DTOConverterUtil;
import com.liferay.analytics.dxp.entity.rest.dto.v1_0.DXPEntity;
import com.liferay.analytics.message.storage.model.AnalyticsDeleteMessage;
import com.liferay.analytics.message.storage.service.AnalyticsDeleteMessageLocalService;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(
	property = "batch.engine.task.item.delegate.name=analytics-delete-message-analytics-dxp-entities",
	service = BatchEngineTaskItemDelegate.class
)
public class AnalyticsDeleteMessageAnalyticsDXPEntityBatchEngineTaskItemDelegate
	extends BaseAnalyticsDXPEntityBatchEngineTaskItemDelegate<DXPEntity> {

	@Override
	protected Page<DXPEntity> doRead(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		List<AnalyticsDeleteMessage> analyticsDeleteMessages = null;
		int totalCount = 0;

		Date modifiedDate = _getModifiedDate(filter);

		if (modifiedDate != null) {
			analyticsDeleteMessages =
				_analyticsDeleteMessageLocalService.getAnalyticsDeleteMessages(
					contextCompany.getCompanyId(), modifiedDate,
					pagination.getStartPosition(), pagination.getEndPosition());
			totalCount =
				_analyticsDeleteMessageLocalService.
					getAnalyticsDeleteMessagesCount(
						contextCompany.getCompanyId(), modifiedDate);
		}
		else {
			analyticsDeleteMessages =
				_analyticsDeleteMessageLocalService.getAnalyticsDeleteMessages(
					contextCompany.getCompanyId(),
					pagination.getStartPosition(), pagination.getEndPosition());
			totalCount =
				_analyticsDeleteMessageLocalService.
					getAnalyticsDeleteMessagesCount(
						contextCompany.getCompanyId());
		}

		if (ListUtil.isEmpty(analyticsDeleteMessages)) {
			return Page.of(Collections.emptyList());
		}

		return Page.of(
			DTOConverterUtil.toDTOs(
				analyticsDeleteMessages, _dxpEntityDTOConverter),
			pagination, totalCount);
	}

	private Date _getModifiedDate(Filter filter) {
		if (!(filter instanceof QueryFilter)) {
			return null;
		}

		QueryFilter queryFilter = (QueryFilter)filter;

		Query query = queryFilter.getQuery();

		if (!(query instanceof TermRangeQuery)) {
			return null;
		}

		TermRangeQuery termRangeQuery = (TermRangeQuery)query;

		if (!StringUtil.startsWith(termRangeQuery.getField(), "modified")) {
			return null;
		}

		String lowerTerm = termRangeQuery.getLowerTerm();

		return new Date(GetterUtil.getLong(lowerTerm));
	}

	@Reference
	private AnalyticsDeleteMessageLocalService
		_analyticsDeleteMessageLocalService;

	@Reference(target = DTOConverterConstants.DXP_ENTITY_DTO_CONVERTER)
	private DTOConverter<BaseModel<?>, DXPEntity> _dxpEntityDTOConverter;

}