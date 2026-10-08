/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.bulk.selection;

import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Date;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akhash Ramprakash
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord",
	service = BulkSelectionFactory.class
)
public class DDMFormInstanceRecordBulkSelectionFactory
	implements BulkSelectionFactory<DDMFormInstanceRecord> {

	@Override
	public BulkSelection<DDMFormInstanceRecord> create(
		Map<String, String[]> parameterMap) {

		return new DDMFormInstanceRecordBulkSelection(
			new Date(MapUtil.getLong(parameterMap, "createDate")),
			_ddmFormInstanceRecordLocalService,
			MapUtil.getLong(parameterMap, "formInstanceId"), parameterMap);
	}

	@Reference
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

}