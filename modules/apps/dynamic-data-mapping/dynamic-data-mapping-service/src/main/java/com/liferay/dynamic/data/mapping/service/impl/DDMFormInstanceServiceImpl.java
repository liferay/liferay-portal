/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.dynamic.data.mapping.constants.DDMConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.service.base.DDMFormInstanceServiceBaseImpl;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Leonardo Barros
 */
@Component(
	property = {
		"json.web.service.context.name=ddm",
		"json.web.service.context.path=DDMFormInstance"
	},
	service = AopService.class
)
public class DDMFormInstanceServiceImpl extends DDMFormInstanceServiceBaseImpl {

	@Override
	public DDMFormInstance addFormInstance(
			long groupId, long ddmStructureId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap,
			DDMFormValues settingsDDMFormValues, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId, DDMActionKeys.ADD_FORM_INSTANCE);

		return ddmFormInstanceLocalService.addFormInstance(
			getUserId(), groupId, ddmStructureId, nameMap, descriptionMap,
			settingsDDMFormValues, serviceContext);
	}

	@Override
	public DDMFormInstance addFormInstance(
			long groupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, DDMForm ddmForm,
			DDMFormLayout ddmFormLayout, DDMFormValues settingsDDMFormValues,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId, DDMActionKeys.ADD_FORM_INSTANCE);

		return ddmFormInstanceLocalService.addFormInstance(
			getUserId(), groupId, nameMap, descriptionMap, ddmForm,
			ddmFormLayout, settingsDDMFormValues, serviceContext);
	}

	@Override
	public DDMFormInstance copyFormInstance(
			long groupId, Map<Locale, String> nameMap,
			DDMFormInstance sourceDDMFormInstance,
			DDMFormValues settingsDDMFormValues, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId, DDMActionKeys.ADD_FORM_INSTANCE);

		return ddmFormInstanceLocalService.copyFormInstance(
			getUserId(), groupId, nameMap, sourceDDMFormInstance,
			settingsDDMFormValues, serviceContext);
	}

	@Override
	public void deleteFormInstance(long ddmFormInstanceId)
		throws PortalException {

		_ddmFormInstanceModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceId, ActionKeys.DELETE);

		ddmFormInstanceLocalService.deleteFormInstance(ddmFormInstanceId);
	}

	@Override
	public DDMFormInstance fetchFormInstance(long ddmFormInstanceId)
		throws PortalException {

		DDMFormInstance ddmFormInstance =
			ddmFormInstancePersistence.fetchByPrimaryKey(ddmFormInstanceId);

		if (ddmFormInstance == null) {
			return null;
		}

		_ddmFormInstanceModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstance.getFormInstanceId(),
			ActionKeys.VIEW);

		return ddmFormInstance;
	}

	@Override
	public DDMFormInstance getFormInstance(long ddmFormInstanceId)
		throws PortalException {

		_ddmFormInstanceModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceId, ActionKeys.VIEW);

		return ddmFormInstancePersistence.findByPrimaryKey(ddmFormInstanceId);
	}

	@Override
	public List<DDMFormInstance> getFormInstances(
		long companyId, long groupId, int start, int end) {

		return ddmFormInstanceFinder.filterFindByC_G(
			companyId, groupId, start, end);
	}

	@Override
	public int getFormInstancesCount(long companyId, long groupId) {
		return ddmFormInstanceFinder.filterCountByC_G(companyId, groupId);
	}

	@Override
	public int getFormInstancesCount(String uuid) throws PortalException {
		return ddmFormInstanceLocalService.getFormInstancesCount(uuid);
	}

	@Override
	public List<DDMFormInstance> search(
		long companyId, long groupId, String keywords, int status, int start,
		int end, OrderByComparator<DDMFormInstance> orderByComparator) {

		return ddmFormInstanceFinder.filterFindByKeywords(
			companyId, groupId, keywords, status, start, end,
			orderByComparator);
	}

	@Override
	public List<DDMFormInstance> search(
		long companyId, long groupId, String keywords, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		return ddmFormInstanceFinder.filterFindByKeywords(
			companyId, groupId, keywords, start, end, orderByComparator);
	}

	@Override
	public List<DDMFormInstance> search(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		return ddmFormInstanceFinder.filterFindByC_G_N_D(
			companyId, groupId, names, descriptions, andOperator, start, end,
			orderByComparator);
	}

	@Override
	public int searchCount(long companyId, long groupId, String keywords) {
		return ddmFormInstanceFinder.filterCountByKeywords(
			companyId, groupId, keywords);
	}

	@Override
	public int searchCount(
		long companyId, long groupId, String keywords, int status) {

		return ddmFormInstanceFinder.filterCountByKeywords(
			companyId, groupId, keywords, status);
	}

	@Override
	public int searchCount(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator) {

		return ddmFormInstanceFinder.filterCountByC_G_N_D(
			companyId, groupId, names, descriptions, andOperator);
	}

	@Override
	public void sendEmail(
			long formInstanceId, String message, String subject,
			String[] toEmailAddresses)
		throws Exception {

		_ddmFormInstanceModelResourcePermission.check(
			getPermissionChecker(), formInstanceId, ActionKeys.UPDATE);

		ddmFormInstanceLocalService.sendEmail(
			getUserId(), message, subject, toEmailAddresses);
	}

	/**
	 * Updates the the record set's settings.
	 *
	 * @param  formInstanceId the primary key of the form instance
	 * @param  settingsDDMFormValues the record set's settings. For more
	 *         information see <code>DDMFormValues</code> in the
	 *         <code>dynamic.data.mapping.api</code> module.
	 * @return the record set
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public DDMFormInstance updateFormInstance(
			long formInstanceId, DDMFormValues settingsDDMFormValues)
		throws PortalException {

		_ddmFormInstanceModelResourcePermission.check(
			getPermissionChecker(), formInstanceId, ActionKeys.UPDATE);

		return ddmFormInstanceLocalService.updateFormInstance(
			formInstanceId, settingsDDMFormValues);
	}

	@Override
	public DDMFormInstance updateFormInstance(
			long ddmFormInstanceId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, DDMForm ddmForm,
			DDMFormLayout ddmFormLayout, DDMFormValues settingsDDMFormValues,
			ServiceContext serviceContext)
		throws PortalException {

		_ddmFormInstanceModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceId, ActionKeys.UPDATE);

		return ddmFormInstanceLocalService.updateFormInstance(
			getUserId(), ddmFormInstanceId, nameMap, descriptionMap, ddmForm,
			ddmFormLayout, settingsDDMFormValues, serviceContext);
	}

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstance)"
	)
	private ModelResourcePermission<DDMFormInstance>
		_ddmFormInstanceModelResourcePermission;

	@Reference(target = "(resource.name=" + DDMConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

}