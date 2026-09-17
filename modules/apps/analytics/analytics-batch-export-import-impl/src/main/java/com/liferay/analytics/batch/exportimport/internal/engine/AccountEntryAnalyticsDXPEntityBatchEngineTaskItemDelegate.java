/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.batch.exportimport.internal.engine;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryTable;
import com.liferay.account.model.AccountGroupRelTable;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.analytics.batch.exportimport.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.analytics.batch.exportimport.internal.engine.util.DTOConverterUtil;
import com.liferay.analytics.batch.exportimport.internal.odata.entity.AccountEntryAnalyticsDXPEntityEntityModel;
import com.liferay.analytics.dxp.entity.rest.dto.v1_0.DXPEntity;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.odata.entity.EntityModel;
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
	property = "batch.engine.task.item.delegate.name=account-entry-analytics-dxp-entities",
	service = BatchEngineTaskItemDelegate.class
)
public class AccountEntryAnalyticsDXPEntityBatchEngineTaskItemDelegate
	extends BaseAnalyticsDXPEntityBatchEngineTaskItemDelegate<DXPEntity> {

	@Override
	public EntityModel getEntityModel(Map<String, List<String>> multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	protected Page<DXPEntity> doRead(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		if (!_analyticsSettingsManager.syncedAccountSettingsEnabled(
				contextCompany.getCompanyId())) {

			return Page.of(
				Collections.emptyList(),
				Pagination.of(pagination.getPage(), pagination.getPageSize()),
				0);
		}

		return Page.of(
			DTOConverterUtil.toDTOs(
				_accountEntryLocalService.<List<AccountEntry>>dslQuery(
					_createSelectDSLQuery(
						contextCompany.getCompanyId(), pagination, parameters)),
				_dxpEntityDTOConverter),
			Pagination.of(pagination.getPage(), pagination.getPageSize()),
			_accountEntryLocalService.dslQuery(
				_createCountDSLQuery(
					contextCompany.getCompanyId(), parameters)));
	}

	private DSLQuery _buildAccountEntryIdsDSLQuery(long companyId)
		throws Exception {

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsSettingsManager.getAnalyticsConfiguration(companyId);

		String[] syncedAccountGroupIds =
			analyticsConfiguration.syncedAccountGroupIds();

		if (ArrayUtil.isEmpty(syncedAccountGroupIds)) {
			return null;
		}

		AccountGroupRelTable accountGroupRelTable =
			AccountGroupRelTable.INSTANCE;

		return DSLQueryFactoryUtil.selectDistinct(
			accountGroupRelTable.classPK
		).from(
			accountGroupRelTable
		).where(
			accountGroupRelTable.classNameId.eq(
				_classNameLocalService.getClassNameId(
					AccountEntry.class.getName())
			).and(
				accountGroupRelTable.accountGroupId.in(
					TransformUtil.transform(
						syncedAccountGroupIds, GetterUtil::getLong, Long.class))
			)
		);
	}

	private Predicate _buildPredicate(
			long companyId, Map<String, Serializable> parameters)
		throws Exception {

		Predicate predicate = buildPredicate(
			AccountEntryTable.INSTANCE, companyId, parameters);

		DSLQuery dslQuery = _buildAccountEntryIdsDSLQuery(companyId);

		if (dslQuery == null) {
			return predicate;
		}

		return predicate.and(
			AccountEntryTable.INSTANCE.accountEntryId.in(dslQuery));
	}

	private DSLQuery _createCountDSLQuery(
			long companyId, Map<String, Serializable> parameters)
		throws Exception {

		return DSLQueryFactoryUtil.count(
		).from(
			AccountEntryTable.INSTANCE
		).where(
			_buildPredicate(companyId, parameters)
		);
	}

	private DSLQuery _createSelectDSLQuery(
			long companyId, Pagination pagination,
			Map<String, Serializable> parameters)
		throws Exception {

		return DSLQueryFactoryUtil.select(
			AccountEntryTable.INSTANCE
		).from(
			AccountEntryTable.INSTANCE
		).where(
			_buildPredicate(companyId, parameters)
		).limit(
			(pagination.getPage() - 1) * pagination.getPageSize(),
			pagination.getPage() * pagination.getPageSize()
		);
	}

	private static final EntityModel _entityModel =
		new AccountEntryAnalyticsDXPEntityEntityModel();

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference(target = DTOConverterConstants.DXP_ENTITY_DTO_CONVERTER)
	private DTOConverter<BaseModel<?>, DXPEntity> _dxpEntityDTOConverter;

}