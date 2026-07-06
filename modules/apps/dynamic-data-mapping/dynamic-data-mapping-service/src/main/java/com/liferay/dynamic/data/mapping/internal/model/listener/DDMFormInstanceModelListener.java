/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.model.listener;

import com.liferay.dynamic.data.mapping.internal.petra.executor.DDMFormInstanceReportPortalExecutor;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceReport;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceReportLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(service = ModelListener.class)
public class DDMFormInstanceModelListener
	extends BaseModelListener<DDMFormInstance> {

	@Override
	public void onAfterCreate(DDMFormInstance ddmFormInstance)
		throws ModelListenerException {

		try {
			ddmFormInstanceReportLocalService.addFormInstanceReport(
				ddmFormInstance.getFormInstanceId());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to update dynamic data mapping form instance ",
						"report for dynamic data mapping form instance ",
						ddmFormInstance.getFormInstanceId()),
					exception);
			}
		}
	}

	@Override
	public void onBeforeRemove(DDMFormInstance ddmFormInstance)
		throws ModelListenerException {

		try {
			DDMFormInstanceReport ddmFormInstanceReport =
				ddmFormInstanceReportLocalService.
					getFormInstanceReportByFormInstanceId(
						ddmFormInstance.getFormInstanceId());

			TransactionCallbackUtil.registerCommitCallback(
				() -> {
					_ddmFormInstanceReportPortalExecutor.execute(
						() ->
							ddmFormInstanceReportLocalService.
								deleteDDMFormInstanceReport(
									ddmFormInstanceReport.
										getFormInstanceReportId()));

					return null;
				});
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to update dynamic data mapping form instance ",
						"report for dynamic data mapping form instance ",
						ddmFormInstance.getFormInstanceId()),
					exception);
			}
		}
	}

	@Reference
	protected DDMFormInstanceReportLocalService
		ddmFormInstanceReportLocalService;

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormInstanceModelListener.class);

	@Reference
	private DDMFormInstanceReportPortalExecutor
		_ddmFormInstanceReportPortalExecutor;

}