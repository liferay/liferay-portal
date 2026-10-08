/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.bulk.selection;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.bulk.selection.BaseContainerEntryBulkSelection;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.bulk.selection.EmptyBulkSelection;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.Date;
import java.util.Map;

/**
 * @author Akhash Ramprakash
 */
public class DDMFormInstanceRecordBulkSelection
	extends BaseContainerEntryBulkSelection<DDMFormInstanceRecord> {

	public DDMFormInstanceRecordBulkSelection(
		Date createDate,
		DDMFormInstanceRecordLocalService ddmFormInstanceRecordLocalService,
		long formInstanceId, Map<String, String[]> parameterMap) {

		super(formInstanceId, parameterMap);

		_createDate = createDate;
		_ddmFormInstanceRecordLocalService = ddmFormInstanceRecordLocalService;
		_formInstanceId = formInstanceId;
	}

	@Override
	public <E extends PortalException> void forEach(
			UnsafeConsumer<DDMFormInstanceRecord, E> unsafeConsumer)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			_getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			(DDMFormInstanceRecord ddmFormInstanceRecord) ->
				unsafeConsumer.accept(ddmFormInstanceRecord));

		actionableDynamicQuery.performActions();
	}

	@Override
	public Class<? extends BulkSelectionFactory>
		getBulkSelectionFactoryClass() {

		return DDMFormInstanceRecordBulkSelectionFactory.class;
	}

	@Override
	public long getSize() throws PortalException {
		ActionableDynamicQuery actionableDynamicQuery =
			_getActionableDynamicQuery();

		return actionableDynamicQuery.performCount();
	}

	@Override
	public BulkSelection<AssetEntry> toAssetEntryBulkSelection() {
		return new EmptyBulkSelection<>();
	}

	private ActionableDynamicQuery _getActionableDynamicQuery() {
		ActionableDynamicQuery actionableDynamicQuery =
			_ddmFormInstanceRecordLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property createDateProperty = PropertyFactoryUtil.forName(
					"createDate");

				dynamicQuery.add(createDateProperty.le(_createDate));

				Property formInstanceIdProperty = PropertyFactoryUtil.forName(
					"formInstanceId");

				dynamicQuery.add(formInstanceIdProperty.eq(_formInstanceId));
			});

		return actionableDynamicQuery;
	}

	private final Date _createDate;
	private final DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;
	private final long _formInstanceId;

}