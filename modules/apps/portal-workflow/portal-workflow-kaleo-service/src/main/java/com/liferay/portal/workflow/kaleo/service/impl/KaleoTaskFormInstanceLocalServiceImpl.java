/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskForm;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskFormInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.form.FormValueProcessor;
import com.liferay.portal.workflow.kaleo.service.base.KaleoTaskFormInstanceLocalServiceBaseImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskFormPersistence;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoTaskFormInstance",
	service = AopService.class
)
public class KaleoTaskFormInstanceLocalServiceImpl
	extends KaleoTaskFormInstanceLocalServiceBaseImpl {

	@Override
	public KaleoTaskFormInstance addKaleoTaskFormInstance(
			long groupId, long kaleoTaskFormId, String formValues,
			KaleoTaskInstanceToken kaleoTaskInstanceToken,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(
			serviceContext.getGuestOrUserId());
		Date date = new Date();

		long kaleoTaskFormInstanceId = counterLocalService.increment();

		KaleoTaskFormInstance kaleoTaskFormInstance =
			kaleoTaskFormInstancePersistence.create(kaleoTaskFormInstanceId);

		kaleoTaskFormInstance.setGroupId(groupId);
		kaleoTaskFormInstance.setCompanyId(user.getCompanyId());
		kaleoTaskFormInstance.setUserId(user.getUserId());
		kaleoTaskFormInstance.setUserName(user.getFullName());
		kaleoTaskFormInstance.setCreateDate(date);
		kaleoTaskFormInstance.setModifiedDate(date);
		kaleoTaskFormInstance.setKaleoDefinitionId(
			kaleoTaskInstanceToken.getKaleoDefinitionId());
		kaleoTaskFormInstance.setKaleoDefinitionVersionId(
			kaleoTaskInstanceToken.getKaleoDefinitionVersionId());
		kaleoTaskFormInstance.setKaleoInstanceId(
			kaleoTaskInstanceToken.getKaleoInstanceId());
		kaleoTaskFormInstance.setKaleoTaskId(
			kaleoTaskInstanceToken.getKaleoTaskId());
		kaleoTaskFormInstance.setKaleoTaskInstanceTokenId(
			kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId());
		kaleoTaskFormInstance.setKaleoTaskFormId(kaleoTaskFormId);

		KaleoTaskForm kaleoTaskForm =
			_kaleoTaskFormPersistence.findByPrimaryKey(kaleoTaskFormId);

		if (Validator.isNotNull(kaleoTaskForm.getFormDefinition())) {
			kaleoTaskFormInstance.setFormValues(formValues);
		}
		else {
			kaleoTaskFormInstance = _formValueProcessor.processFormValues(
				kaleoTaskForm, kaleoTaskFormInstance, formValues,
				serviceContext);
		}

		return kaleoTaskFormInstancePersistence.update(kaleoTaskFormInstance);
	}

	@Override
	public int countKaleoTaskFormInstanceByKaleoTaskId(long kaleoTaskId) {
		return kaleoTaskFormInstancePersistence.countByKaleoTaskId(kaleoTaskId);
	}

	@Override
	public void deleteCompanyKaleoTaskFormInstances(long companyId) {
		kaleoTaskFormInstancePersistence.removeByCompanyId(companyId);
	}

	@Override
	public void deleteKaleoDefinitionVersionKaleoTaskFormInstances(
		long kaleoDefinitionVersionId) {

		kaleoTaskFormInstancePersistence.removeByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId);
	}

	@Override
	public void deleteKaleoInstanceKaleoTaskFormInstances(
		long kaleoInstanceId) {

		kaleoTaskFormInstancePersistence.removeByKaleoInstanceId(
			kaleoInstanceId);
	}

	@Override
	public void deleteKaleoTaskInstanceTokenKaleoTaskFormInstances(
		long kaleoTaskInstanceTokenId) {

		kaleoTaskFormInstancePersistence.removeByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId);
	}

	@Override
	public KaleoTaskFormInstance fetchKaleoTaskFormKaleoTaskFormInstance(
		long kaleoTaskFormId) {

		return kaleoTaskFormInstancePersistence.fetchByKaleoTaskFormId(
			kaleoTaskFormId);
	}

	@Override
	public KaleoTaskFormInstance getKaleoTaskFormKaleoTaskFormInstance(
			long kaleoTaskFormId)
		throws PortalException {

		return kaleoTaskFormInstancePersistence.findByKaleoTaskFormId(
			kaleoTaskFormId);
	}

	@Override
	public List<KaleoTaskFormInstance> getKaleoTaskKaleoTaskFormInstances(
		long kaleoTaskId) {

		return kaleoTaskFormInstancePersistence.findByKaleoTaskId(kaleoTaskId);
	}

	@Reference
	private FormValueProcessor _formValueProcessor;

	@Reference
	private KaleoTaskFormPersistence _kaleoTaskFormPersistence;

	@Reference
	private UserLocalService _userLocalService;

}