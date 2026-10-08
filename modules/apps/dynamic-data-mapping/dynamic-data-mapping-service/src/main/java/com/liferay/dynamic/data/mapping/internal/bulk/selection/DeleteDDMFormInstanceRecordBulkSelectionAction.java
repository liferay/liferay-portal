/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.bulk.selection;

import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.dynamic.data.mapping.internal.util.DDMFormInstanceRecordThreadLocal;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceReportLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akhash Ramprakash
 */
@Component(
	property = "bulk.selection.action.key=delete.ddm.form.instance.record",
	service = BulkSelectionAction.class
)
public class DeleteDDMFormInstanceRecordBulkSelectionAction
	implements BulkSelectionAction<DDMFormInstanceRecord> {

	@Override
	public void execute(
			User user, BulkSelection<DDMFormInstanceRecord> bulkSelection,
			Map<String, Serializable> inputMap)
		throws Exception {

		try (SafeCloseable safeCloseable =
				DDMFormInstanceRecordThreadLocal.
					setDeleteInProcessWithSafeCloseable(true)) {

			bulkSelection.forEach(
				ddmFormInstanceRecord -> {
					try {
						_ddmFormInstanceRecordService.deleteFormInstanceRecord(
							ddmFormInstanceRecord.getFormInstanceRecordId());
					}
					catch (Exception exception) {
						if (_log.isWarnEnabled()) {
							_log.warn(exception);
						}
					}
				});
		}
		finally {
			_ddmFormInstanceReportLocalService.
				updateFormInstanceReportByFormInstanceId(
					MapUtil.getLong(
						bulkSelection.getParameterMap(), "formInstanceId"));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteDDMFormInstanceRecordBulkSelectionAction.class);

	@Reference
	private DDMFormInstanceRecordService _ddmFormInstanceRecordService;

	@Reference
	private DDMFormInstanceReportLocalService
		_ddmFormInstanceReportLocalService;

}