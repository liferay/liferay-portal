/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context.util;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;

/**
 * @author Carolina Barbosa
 */
public class DDMFormGuestUploadFieldUtil {

	public static boolean isMaximumSubmissionLimitReached(
			DDMFormInstance ddmFormInstance,
			HttpServletRequest httpServletRequest,
			int guestUploadMaximumSubmissions)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay.isSignedIn() ||
			!hasGuestUploadField(ddmFormInstance)) {

			return false;
		}

		int count = 0;

		DDMFormInstanceRecordLocalService ddmFormInstanceRecordLocalService =
			_ddmFormInstanceRecordLocalServiceSnapshot.get();

		for (DDMFormInstanceRecord ddmFormInstanceRecord :
				ddmFormInstanceRecordLocalService.getFormInstanceRecords(
					ddmFormInstance.getFormInstanceId(),
					WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			if (Objects.equals(
					ddmFormInstanceRecord.getIpAddress(),
					httpServletRequest.getRemoteAddr()) &&
				(++count == guestUploadMaximumSubmissions)) {

				return true;
			}
		}

		return false;
	}

	protected static boolean hasGuestUploadField(
			DDMFormInstance ddmFormInstance)
		throws PortalException {

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		DDMForm ddmForm = ddmStructure.getDDMForm();

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (DDMFormField ddmFormField : ddmFormFieldsMap.values()) {
			if (Objects.equals(ddmFormField.getType(), "document_library") &&
				GetterUtil.getBoolean(
					ddmFormField.getProperty("allowGuestUsers"))) {

				return true;
			}
		}

		return false;
	}

	private static final Snapshot<DDMFormInstanceRecordLocalService>
		_ddmFormInstanceRecordLocalServiceSnapshot = new Snapshot<>(
			DDMFormGuestUploadFieldUtil.class,
			DDMFormInstanceRecordLocalService.class);

}