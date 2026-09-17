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
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationTable;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(
	property = "batch.engine.task.item.delegate.name=organization-analytics-dxp-entities",
	service = BatchEngineTaskItemDelegate.class
)
public class OrganizationAnalyticsDXPEntityBatchEngineTaskItemDelegate
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

		return Page.of(
			DTOConverterUtil.toDTOs(
				_organizationLocalService.<List<Organization>>dslQuery(
					_createSelectDSLQuery(
						contextCompany.getCompanyId(), pagination, parameters)),
				_dxpEntityDTOConverter),
			Pagination.of(pagination.getPage(), pagination.getPageSize()),
			_organizationLocalService.dslQuery(
				_createCountDSLQuery(
					contextCompany.getCompanyId(), parameters)));
	}

	private DSLQuery _createCountDSLQuery(
		long companyId, Map<String, Serializable> parameters) {

		return DSLQueryFactoryUtil.count(
		).from(
			OrganizationTable.INSTANCE
		).where(
			buildPredicate(OrganizationTable.INSTANCE, companyId, parameters)
		);
	}

	private DSLQuery _createSelectDSLQuery(
		long companyId, Pagination pagination,
		Map<String, Serializable> parameters) {

		return DSLQueryFactoryUtil.select(
		).from(
			OrganizationTable.INSTANCE
		).where(
			buildPredicate(OrganizationTable.INSTANCE, companyId, parameters)
		).limit(
			(pagination.getPage() - 1) * pagination.getPageSize(),
			pagination.getPage() * pagination.getPageSize()
		);
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference(target = DTOConverterConstants.DXP_ENTITY_DTO_CONVERTER)
	private DTOConverter<BaseModel<?>, DXPEntity> _dxpEntityDTOConverter;

	@Reference
	private OrganizationLocalService _organizationLocalService;

}