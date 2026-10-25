/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.data.provider.configuration.DDMDataProviderConfiguration;
import com.liferay.dynamic.data.mapping.exception.DataProviderInstanceNameException;
import com.liferay.dynamic.data.mapping.exception.DataProviderInstanceURLException;
import com.liferay.dynamic.data.mapping.exception.DuplicateDataProviderInstanceInputParameterNameException;
import com.liferay.dynamic.data.mapping.exception.NoSuchDataProviderInstanceException;
import com.liferay.dynamic.data.mapping.exception.RequiredDataProviderInstanceException;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.base.DDMDataProviderInstanceLocalServiceBaseImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMDataProviderInstanceLinkPersistence;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.net.URL;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	configurationPid = "com.liferay.dynamic.data.mapping.data.provider.configuration.DDMDataProviderConfiguration",
	property = "model.class.name=com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance",
	service = AopService.class
)
public class DDMDataProviderInstanceLocalServiceImpl
	extends DDMDataProviderInstanceLocalServiceBaseImpl {

	@Override
	public DDMDataProviderInstance addDataProviderInstance(
			long userId, long groupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, DDMFormValues ddmFormValues,
			String type, ServiceContext serviceContext)
		throws PortalException {

		// Data provider instance

		User user = _userLocalService.getUser(userId);

		_validate(nameMap, ddmFormValues);

		long dataProviderInstanceId = counterLocalService.increment();

		DDMDataProviderInstance dataProviderInstance =
			ddmDataProviderInstancePersistence.create(dataProviderInstanceId);

		dataProviderInstance.setUuid(serviceContext.getUuid());
		dataProviderInstance.setGroupId(groupId);
		dataProviderInstance.setCompanyId(user.getCompanyId());
		dataProviderInstance.setUserId(user.getUserId());
		dataProviderInstance.setUserName(user.getFullName());
		dataProviderInstance.setNameMap(nameMap);
		dataProviderInstance.setDescriptionMap(descriptionMap);
		dataProviderInstance.setDefinition(_serialize(ddmFormValues));
		dataProviderInstance.setType(type);

		dataProviderInstance = ddmDataProviderInstancePersistence.update(
			dataProviderInstance);

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			_addDataProviderInstanceResources(
				dataProviderInstance, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			_addDataProviderInstanceResources(
				dataProviderInstance, serviceContext.getModelPermissions());
		}

		return dataProviderInstance;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteDataProviderInstance(
			DDMDataProviderInstance dataProviderInstance)
		throws PortalException {

		if (!GroupThreadLocal.isDeleteInProcess()) {
			int count =
				_ddmDataProviderInstanceLinkPersistence.
					countByDataProviderInstanceId(
						dataProviderInstance.getDataProviderInstanceId());

			if (count > 0) {
				throw new RequiredDataProviderInstanceException.
					MustNotDeleteDataProviderInstanceReferencedByDataProviderInstanceLinks(
						dataProviderInstance.getDataProviderInstanceId());
			}
		}

		// Data provider instance

		ddmDataProviderInstancePersistence.remove(dataProviderInstance);

		// Resources

		_resourceLocalService.deleteResource(
			dataProviderInstance.getCompanyId(),
			DDMDataProviderInstance.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			dataProviderInstance.getDataProviderInstanceId());
	}

	@Override
	public void deleteDataProviderInstance(long dataProviderInstanceId)
		throws PortalException {

		DDMDataProviderInstance dataProviderInstance =
			ddmDataProviderInstancePersistence.findByPrimaryKey(
				dataProviderInstanceId);

		ddmDataProviderInstanceLocalService.deleteDataProviderInstance(
			dataProviderInstance);
	}

	@Override
	public void deleteDataProviderInstances(long companyId, long groupId)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			ddmDataProviderInstanceLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property groupIdProperty = PropertyFactoryUtil.forName(
					"groupId");

				dynamicQuery.add(groupIdProperty.eq(groupId));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(DDMDataProviderInstance ddmDataProviderInstance) ->
				deleteDataProviderInstance(ddmDataProviderInstance));
		actionableDynamicQuery.setCompanyId(companyId);

		actionableDynamicQuery.performActions();
	}

	@Override
	public DDMDataProviderInstance fetchDataProviderInstance(
		long dataProviderInstanceId) {

		return ddmDataProviderInstancePersistence.fetchByPrimaryKey(
			dataProviderInstanceId);
	}

	@Override
	public DDMDataProviderInstance fetchDataProviderInstanceByUuid(
		String uuid) {

		List<DDMDataProviderInstance> ddmDataProviderInstances =
			ddmDataProviderInstancePersistence.findByUuid(uuid);

		if (ddmDataProviderInstances.isEmpty()) {
			return null;
		}

		return ddmDataProviderInstances.get(0);
	}

	@Override
	public DDMDataProviderInstance getDataProviderInstance(
			long dataProviderInstanceId)
		throws PortalException {

		return ddmDataProviderInstancePersistence.findByPrimaryKey(
			dataProviderInstanceId);
	}

	@Override
	public DDMDataProviderInstance getDataProviderInstanceByUuid(String uuid)
		throws PortalException {

		List<DDMDataProviderInstance> ddmDataProviderInstances =
			ddmDataProviderInstancePersistence.findByUuid(uuid);

		if (ddmDataProviderInstances.isEmpty()) {
			throw new NoSuchDataProviderInstanceException(
				"No DataProviderInstance found with uuid: " + uuid);
		}

		return ddmDataProviderInstances.get(0);
	}

	@Override
	public List<DDMDataProviderInstance> getDataProviderInstances(
		long[] groupIds) {

		return ddmDataProviderInstancePersistence.findByGroupId(groupIds);
	}

	@Override
	public List<DDMDataProviderInstance> getDataProviderInstances(
		long[] groupIds, int start, int end) {

		return ddmDataProviderInstancePersistence.findByGroupId(
			groupIds, start, end);
	}

	@Override
	public List<DDMDataProviderInstance> getDataProviderInstances(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		return ddmDataProviderInstancePersistence.findByGroupId(
			groupIds, start, end, orderByComparator);
	}

	@Override
	public DDMDataProviderInstance updateDataProviderInstance(
			long userId, long dataProviderInstanceId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			DDMFormValues ddmFormValues, ServiceContext serviceContext)
		throws PortalException {

		_validate(nameMap, ddmFormValues);

		DDMDataProviderInstance dataProviderInstance =
			ddmDataProviderInstancePersistence.findByPrimaryKey(
				dataProviderInstanceId);

		User user = _userLocalService.getUser(userId);

		dataProviderInstance.setUserId(user.getUserId());
		dataProviderInstance.setUserName(user.getFullName());

		dataProviderInstance.setModifiedDate(new Date());
		dataProviderInstance.setNameMap(nameMap);
		dataProviderInstance.setDescriptionMap(descriptionMap);
		dataProviderInstance.setDefinition(_serialize(ddmFormValues));

		return ddmDataProviderInstancePersistence.update(dataProviderInstance);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_ddmDataProviderConfiguration = ConfigurableUtil.createConfigurable(
			DDMDataProviderConfiguration.class, properties);
	}

	private void _addDataProviderInstanceResources(
			DDMDataProviderInstance dataProviderInstance,
			boolean addGroupPermissions, boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			dataProviderInstance.getCompanyId(),
			dataProviderInstance.getGroupId(), dataProviderInstance.getUserId(),
			DDMDataProviderInstance.class.getName(),
			dataProviderInstance.getDataProviderInstanceId(), false,
			addGroupPermissions, addGuestPermissions);
	}

	private void _addDataProviderInstanceResources(
			DDMDataProviderInstance dataProviderInstance,
			ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			dataProviderInstance.getCompanyId(),
			dataProviderInstance.getGroupId(), dataProviderInstance.getUserId(),
			DDMDataProviderInstance.class.getName(),
			dataProviderInstance.getDataProviderInstanceId(), modelPermissions);
	}

	private boolean _isLocalNetworkURL(String value) {
		try {
			URL url = new URL(value);

			return InetAddressUtil.isLocalInetAddress(
				InetAddressUtil.getInetAddressByName(url.getHost()));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return true;
	}

	private String _serialize(DDMFormValues ddmFormValues) {
		DDMFormValuesSerializerSerializeRequest.Builder builder =
			DDMFormValuesSerializerSerializeRequest.Builder.newBuilder(
				ddmFormValues);

		DDMFormValuesSerializerSerializeResponse
			ddmFormValuesSerializerSerializeResponse =
				_jsonDDMFormValuesSerializer.serialize(builder.build());

		return ddmFormValuesSerializerSerializeResponse.getContent();
	}

	private void _validate(
			Map<Locale, String> nameMap, DDMFormValues ddmFormValues)
		throws PortalException {

		Locale locale = LocaleUtil.getSiteDefault();

		String name = nameMap.get(locale);

		if (Validator.isNull(name)) {
			throw new DataProviderInstanceNameException(
				"Name is null for locale " + locale.getDisplayName());
		}

		if (!_ddmDataProviderConfiguration.accessLocalNetwork()) {
			_validateLocalNetworkURL(ddmFormValues);
		}

		_validateInputParameterNames(ddmFormValues);

		_ddmFormValuesValidator.validate(ddmFormValues);
	}

	private void _validateInputParameterNames(DDMFormValues ddmFormValues)
		throws PortalException {

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap();

		if (!ddmFormFieldValuesMap.containsKey("inputParameters")) {
			return;
		}

		Set<String> inputParameterNames = new HashSet<>();

		for (DDMFormFieldValue inputParametersDDMFormFieldValue :
				ddmFormFieldValuesMap.get("inputParameters")) {

			Map<String, List<DDMFormFieldValue>> nestedDDMFormFieldValuesMap =
				inputParametersDDMFormFieldValue.
					getNestedDDMFormFieldValuesMap();

			for (DDMFormFieldValue inputParameterNameDDMFormFieldValue :
					nestedDDMFormFieldValuesMap.get("inputParameterName")) {

				Value inputParameterNameValue =
					inputParameterNameDDMFormFieldValue.getValue();

				Map<Locale, String> inputParameterNameValuesMap =
					inputParameterNameValue.getValues();

				for (String inputParameterName :
						inputParameterNameValuesMap.values()) {

					if (!inputParameterNames.add(inputParameterName)) {
						throw new DuplicateDataProviderInstanceInputParameterNameException(
							"Duplicate data provider input parameter name: " +
								inputParameterName);
					}
				}
			}
		}
	}

	private void _validateLocalNetworkURL(DDMFormValues ddmFormValues)
		throws PortalException {

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap();

		if (!ddmFormFieldValuesMap.containsKey("url")) {
			return;
		}

		for (DDMFormFieldValue ddmFormFieldValue :
				ddmFormFieldValuesMap.get("url")) {

			Value value = ddmFormFieldValue.getValue();

			for (Locale locale : value.getAvailableLocales()) {
				String valueString = value.getString(locale);

				if (!Validator.isUrl(valueString)) {
					continue;
				}

				if (_isLocalNetworkURL(valueString)) {
					throw new DataProviderInstanceURLException(
						"URL must not be a local network");
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMDataProviderInstanceLocalServiceImpl.class);

	private volatile DDMDataProviderConfiguration _ddmDataProviderConfiguration;

	@Reference
	private DDMDataProviderInstanceLinkPersistence
		_ddmDataProviderInstanceLinkPersistence;

	@Reference
	private DDMFormValuesValidator _ddmFormValuesValidator;

	@Reference(target = "(ddm.form.values.serializer.type=json)")
	private DDMFormValuesSerializer _jsonDDMFormValuesSerializer;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}