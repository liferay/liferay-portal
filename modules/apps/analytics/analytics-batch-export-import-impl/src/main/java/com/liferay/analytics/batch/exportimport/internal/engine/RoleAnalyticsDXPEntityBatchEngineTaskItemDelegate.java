/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.batch.exportimport.internal.engine;

import com.liferay.analytics.batch.exportimport.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.analytics.batch.exportimport.internal.engine.util.DTOConverterUtil;
import com.liferay.analytics.dxp.entity.rest.dto.v1_0.DXPEntity;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(
	property = "batch.engine.task.item.delegate.name=role-analytics-dxp-entities",
	service = BatchEngineTaskItemDelegate.class
)
public class RoleAnalyticsDXPEntityBatchEngineTaskItemDelegate
	extends BaseAnalyticsDXPEntityBatchEngineTaskItemDelegate<DXPEntity> {

	@Override
	protected Page<DXPEntity> doRead(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		if (!_analyticsSettingsManager.syncedContactSettingsEnabled(
				contextCompany.getCompanyId())) {

			return Page.of(
				Collections.emptyList(),
				Pagination.of(pagination.getPage(), pagination.getPageSize()),
				0);
		}

		DynamicQuery dynamicQuery = _buildDynamicQuery(
			contextCompany.getCompanyId(), parameters);

		return Page.of(
			DTOConverterUtil.toDTOs(
				roleLocalService.dynamicQuery(
					dynamicQuery, pagination.getStartPosition(),
					pagination.getEndPosition()),
				_dxpEntityDTOConverter),
			pagination, roleLocalService.dynamicQueryCount(dynamicQuery));
	}

	private DynamicQuery _buildDynamicQuery(
		long companyId, Map<String, Serializable> parameters) {

		DynamicQuery dynamicQuery = roleLocalService.dynamicQuery();

		Property nameProperty = PropertyFactoryUtil.forName("name");

		dynamicQuery.add(
			nameProperty.ne(RoleConstants.ANALYTICS_ADMINISTRATOR));

		Property typeProperty = PropertyFactoryUtil.forName("type");

		dynamicQuery.add(typeProperty.eq(RoleConstants.TYPE_REGULAR));

		return buildDynamicQuery(companyId, dynamicQuery, parameters);
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference(target = DTOConverterConstants.DXP_ENTITY_DTO_CONVERTER)
	private DTOConverter<BaseModel<?>, DXPEntity> _dxpEntityDTOConverter;

}