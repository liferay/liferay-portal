/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.batch.exportimport.internal.engine;

import com.liferay.analytics.batch.exportimport.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.analytics.batch.exportimport.internal.engine.util.DTOConverterUtil;
import com.liferay.analytics.dxp.entity.rest.dto.v1_0.DXPEntity;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(
	property = "batch.engine.task.item.delegate.name=expando-column-analytics-dxp-entities",
	service = BatchEngineTaskItemDelegate.class
)
public class ExpandoColumnAnalyticsDXPEntityBatchEngineTaskItemDelegate
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

		if (dynamicQuery == null) {
			return Page.of(Collections.emptyList(), pagination, 0);
		}

		return Page.of(
			DTOConverterUtil.toDTOs(
				_expandoColumnLocalService.dynamicQuery(
					dynamicQuery, pagination.getStartPosition(),
					pagination.getEndPosition()),
				_dxpEntityDTOConverter),
			pagination,
			_expandoColumnLocalService.dynamicQueryCount(dynamicQuery));
	}

	private DynamicQuery _buildDynamicQuery(
			long companyId, Map<String, Serializable> parameters)
		throws Exception {

		ExpandoTable organizationExpandoTable =
			_expandoTableLocalService.fetchTable(
				companyId,
				_classNameLocalService.getClassNameId(
					Organization.class.getName()),
				ExpandoTableConstants.DEFAULT_TABLE_NAME);
		ExpandoTable userExpandoTable = _expandoTableLocalService.fetchTable(
			companyId,
			_classNameLocalService.getClassNameId(User.class.getName()),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);

		if ((organizationExpandoTable == null) && (userExpandoTable == null)) {
			return null;
		}

		DynamicQuery dynamicQuery = _expandoColumnLocalService.dynamicQuery();

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsSettingsManager.getAnalyticsConfiguration(companyId);
		Property nameProperty = PropertyFactoryUtil.forName("name");
		Property tableIdProperty = PropertyFactoryUtil.forName("tableId");

		if ((organizationExpandoTable != null) && (userExpandoTable != null)) {
			dynamicQuery.add(
				RestrictionsFactoryUtil.or(
					tableIdProperty.eq(organizationExpandoTable.getTableId()),
					RestrictionsFactoryUtil.and(
						tableIdProperty.eq(userExpandoTable.getTableId()),
						nameProperty.in(
							analyticsConfiguration.syncedUserFieldNames()))));
		}
		else if (organizationExpandoTable != null) {
			dynamicQuery.add(
				tableIdProperty.eq(organizationExpandoTable.getTableId()));
		}
		else {
			dynamicQuery.add(tableIdProperty.eq(userExpandoTable.getTableId()));
			dynamicQuery.add(
				nameProperty.in(analyticsConfiguration.syncedUserFieldNames()));
		}

		return buildDynamicQuery(companyId, dynamicQuery, parameters);
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference(target = DTOConverterConstants.DXP_ENTITY_DTO_CONVERTER)
	private DTOConverter<BaseModel<?>, DXPEntity> _dxpEntityDTOConverter;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

}