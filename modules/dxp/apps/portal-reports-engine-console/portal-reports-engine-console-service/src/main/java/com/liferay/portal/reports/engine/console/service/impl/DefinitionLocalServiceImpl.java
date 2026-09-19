/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service.impl;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.reports.engine.console.exception.DefinitionFileException;
import com.liferay.portal.reports.engine.console.exception.DefinitionNameException;
import com.liferay.portal.reports.engine.console.model.Definition;
import com.liferay.portal.reports.engine.console.service.base.DefinitionLocalServiceBaseImpl;

import java.io.InputStream;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gavin Wan
 */
@Component(
	property = "model.class.name=com.liferay.portal.reports.engine.console.model.Definition",
	service = AopService.class
)
public class DefinitionLocalServiceImpl extends DefinitionLocalServiceBaseImpl {

	@Override
	public Definition addDefinition(
			long userId, long groupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, long sourceId,
			String reportParameters, String fileName, InputStream inputStream,
			ServiceContext serviceContext)
		throws PortalException {

		// Definition

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		_validate(nameMap);

		long definitionId = counterLocalService.increment();

		Definition definition = definitionPersistence.create(definitionId);

		definition.setUuid(serviceContext.getUuid());
		definition.setGroupId(groupId);
		definition.setCompanyId(user.getCompanyId());
		definition.setUserId(user.getUserId());
		definition.setUserName(user.getFullName());
		definition.setCreateDate(serviceContext.getCreateDate(date));
		definition.setModifiedDate(serviceContext.getModifiedDate(date));
		definition.setNameMap(nameMap);
		definition.setDescriptionMap(descriptionMap);
		definition.setSourceId(sourceId);
		definition.setReportName(
			StringUtil.extractFirst(fileName, StringPool.PERIOD));
		definition.setReportParameters(reportParameters);

		definition = definitionPersistence.update(definition);

		// Resources

		_resourceLocalService.addModelResources(definition, serviceContext);

		// Attachments

		if (Validator.isNotNull(fileName) && (inputStream != null)) {
			_addDefinitionFile(
				user.getCompanyId(), definition, fileName, inputStream);
		}
		else {
			throw new DefinitionFileException.InvalidDefinitionFile(
				fileName, inputStream == null);
		}

		return definition;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public Definition deleteDefinition(Definition definition)
		throws PortalException {

		// Definition

		definitionPersistence.remove(definition);

		// Resources

		_resourceLocalService.deleteResource(
			definition.getCompanyId(), Definition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, definition.getDefinitionId());

		// Attachments

		deleteDefinitionTemplates(
			definition.getCompanyId(), definition.getAttachmentsDir());

		return definition;
	}

	@Override
	public Definition deleteDefinition(long definitionId)
		throws PortalException {

		Definition definition = definitionPersistence.findByPrimaryKey(
			definitionId);

		return definitionLocalService.deleteDefinition(definition);
	}

	@Override
	public void deleteDefinitionTemplates(
			long companyId, String attachmentsDirectory)
		throws PortalException {

		_store.deleteDirectory(
			companyId, CompanyConstants.SYSTEM, attachmentsDirectory);
	}

	@Override
	public void deleteDefinitions(long groupId) throws PortalException {
		List<Definition> definitions = definitionPersistence.findByGroupId(
			groupId);

		for (Definition definition : definitions) {
			definitionLocalService.deleteDefinition(definition);
		}
	}

	@Override
	public String[] getAttachmentsFileNames(Definition definition) {
		return _store.getFileNames(
			definition.getCompanyId(), CompanyConstants.SYSTEM,
			definition.getAttachmentsDir());
	}

	@Override
	public List<Definition> getDefinitions(
		long groupId, String definitionName, String description,
		String sourceId, String reportName, boolean andSearch, int start,
		int end, OrderByComparator<Definition> orderByComparator) {

		return definitionFinder.findByG_S_N_D_RN(
			groupId, definitionName, description, GetterUtil.getLong(sourceId),
			reportName, andSearch, start, end, orderByComparator);
	}

	@Override
	public int getDefinitionsCount(
		long groupId, String definitionName, String description,
		String sourceId, String reportName, boolean andSearch) {

		return definitionFinder.countByG_S_N_D_RN(
			groupId, definitionName, description, GetterUtil.getLong(sourceId),
			reportName, andSearch);
	}

	@Override
	public Definition updateDefinition(
			long definitionId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, long sourceId,
			String reportParameters, String fileName, InputStream inputStream,
			ServiceContext serviceContext)
		throws PortalException {

		// Definition

		Definition definition = definitionPersistence.findByPrimaryKey(
			definitionId);

		_validate(nameMap);

		definition.setModifiedDate(serviceContext.getModifiedDate(null));
		definition.setNameMap(nameMap);
		definition.setDescriptionMap(descriptionMap);
		definition.setSourceId(sourceId);

		if (Validator.isNotNull(fileName)) {
			definition.setReportName(
				StringUtil.extractFirst(fileName, StringPool.PERIOD));
		}

		definition.setReportParameters(reportParameters);

		definition = definitionPersistence.update(definition);

		// Resources

		ModelPermissions modelPermissions =
			serviceContext.getModelPermissions();

		if (modelPermissions == null) {
			updateDefinitionResources(
				definition, StringPool.EMPTY_ARRAY, StringPool.EMPTY_ARRAY);
		}
		else {
			updateDefinitionResources(
				definition,
				modelPermissions.getActionIds(
					RoleConstants.PLACEHOLDER_DEFAULT_GROUP_ROLE),
				modelPermissions.getActionIds(RoleConstants.GUEST));
		}

		// Attachments

		if (Validator.isNotNull(fileName) && (inputStream != null)) {
			long companyId = definition.getCompanyId();

			_store.deleteDirectory(
				companyId, CompanyConstants.SYSTEM,
				definition.getAttachmentsDir());

			_addDefinitionFile(companyId, definition, fileName, inputStream);
		}

		return definition;
	}

	@Override
	public void updateDefinitionResources(
			Definition definition, String[] communityPermissions,
			String[] guestPermissions)
		throws PortalException {

		_resourceLocalService.updateResources(
			definition.getCompanyId(), definition.getGroupId(),
			Definition.class.getName(), definition.getDefinitionId(),
			communityPermissions, guestPermissions);
	}

	private void _addDefinitionFile(
			long companyId, Definition definition, String fileName,
			InputStream inputStream)
		throws PortalException {

		String directoryName = definition.getAttachmentsDir();

		String fileLocation = StringBundler.concat(
			directoryName, StringPool.SLASH, fileName);

		_store.addFile(
			companyId, CompanyConstants.SYSTEM, fileLocation,
			Store.VERSION_DEFAULT, inputStream);
	}

	private void _validate(Map<Locale, String> nameMap) throws PortalException {
		Locale locale = LocaleUtil.getDefault();

		String name = nameMap.get(locale);

		if (Validator.isNull(name)) {
			throw new DefinitionNameException.NullDefinitionFileName();
		}
	}

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference(target = "(default=true)")
	private Store _store;

	@Reference
	private UserLocalService _userLocalService;

}