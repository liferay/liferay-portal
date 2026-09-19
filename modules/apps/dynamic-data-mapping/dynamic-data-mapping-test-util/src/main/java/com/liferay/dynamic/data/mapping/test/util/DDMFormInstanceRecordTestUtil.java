/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.test.util;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Gabriel Ibson
 */
public class DDMFormInstanceRecordTestUtil {

	public static DDMFormInstanceRecord addDDMFormInstanceRecord(
			DDMFormInstance ddmFormInstance, DDMFormValues ddmFormValues,
			Group group, long userId)
		throws Exception {

		return DDMFormInstanceRecordLocalServiceUtil.addFormInstanceRecord(
			userId, group.getGroupId(), ddmFormInstance.getFormInstanceId(),
			ddmFormValues, ServiceContextTestUtil.getServiceContext());
	}

	public static DDMFormInstanceRecord
			addDDMFormInstanceRecordWithRandomValues(
				DDMFormInstance ddmFormInstance, Group group, long userId)
		throws Exception {

		DDMForm ddmForm = ddmFormInstance.getDDMForm();

		return addDDMFormInstanceRecord(
			ddmFormInstance,
			DDMFormValuesTestUtil.createDDMFormValuesWithRandomValues(ddmForm),
			group, userId);
	}

	public static DDMFormInstanceRecord
			addDDMFormInstanceRecordWithRandomValues(Group group, long userId)
		throws Exception {

		return addDDMFormInstanceRecordWithRandomValues(
			DDMFormInstanceTestUtil.addDDMFormInstance(group, userId), group,
			userId);
	}

	public static DDMFormInstanceRecord
			addDDMFormInstanceRecordWithStatusByUserIdAndNoValues(
				Group group, long statusByUserId, long userId)
		throws Exception {

		DDMFormInstanceRecord ddmFormInstanceRecord =
			addDDMFormInstanceRecordWithoutValues(group, userId);

		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
			ddmFormInstanceRecord.getFormInstanceRecordVersion();

		return DDMFormInstanceRecordLocalServiceUtil.updateStatus(
			statusByUserId,
			ddmFormInstanceRecordVersion.getFormInstanceRecordVersionId(),
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));
	}

	public static DDMFormInstanceRecord addDDMFormInstanceRecordWithoutValues(
			Group group, long userId)
		throws Exception {

		DDMFormInstance ddmFormInstance =
			DDMFormInstanceTestUtil.addDDMFormInstance(group, userId);

		return addDDMFormInstanceRecord(
			ddmFormInstance,
			DDMFormValuesTestUtil.createDDMFormValues(
				ddmFormInstance.getDDMForm()),
			group, userId);
	}

}