/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.exception.DuplicateDDMStructureExternalReferenceCodeException;
import com.liferay.dynamic.data.mapping.exception.InvalidParentStructureException;
import com.liferay.dynamic.data.mapping.exception.InvalidStructureVersionException;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureLayoutException;
import com.liferay.dynamic.data.mapping.exception.RequiredStructureException;
import com.liferay.dynamic.data.mapping.exception.StructureDefinitionException;
import com.liferay.dynamic.data.mapping.exception.StructureDuplicateElementException;
import com.liferay.dynamic.data.mapping.exception.StructureDuplicateStructureKeyException;
import com.liferay.dynamic.data.mapping.exception.StructureNameException;
import com.liferay.dynamic.data.mapping.internal.constants.DDMDestinationNames;
import com.liferay.dynamic.data.mapping.internal.search.util.DDMSearchUtil;
import com.liferay.dynamic.data.mapping.internal.util.DDMFormTemplateSynchonizer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstanceLink;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructureTable;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.security.permission.DDMPermissionSupport;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.service.base.DDMStructureLocalServiceBaseImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMDataProviderInstanceLinkPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureLayoutPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureLinkPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureVersionPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplatePersistence;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.dynamic.data.mapping.util.DDMDataDefinitionConverter;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldUtil;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidator;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.journal.model.JournalArticle;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.DDMStructureIndexer;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the local service for accessing, adding, deleting, and updating
 * dynamic data mapping (DDM) structures.
 *
 * <p>
 * DDM structures (structures) are used in Liferay to store structured content
 * like document types, dynamic data definitions, or web contents.
 * </p>
 *
 * <p>
 * Structures support inheritance via parent structures. They also support
 * multi-language names and descriptions.
 * </p>
 *
 * <p>
 * Structures can be related to many models in Liferay, such as those for web
 * contents, dynamic data lists, and documents. This relationship can be
 * established via the model's class name ID.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @author Bruno Basto
 * @author Marcellus Tavares
 * @author Juan Fernández
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.mapping.model.DDMStructure",
	service = AopService.class
)
public class DDMStructureLocalServiceImpl
	extends DDMStructureLocalServiceBaseImpl {

	@Override
	public DDMStructure addStructure(
			long userId, long groupId, long classNameId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			DDMForm ddmForm, DDMFormLayout ddmFormLayout, String storageType,
			ServiceContext serviceContext)
		throws PortalException {

		return addStructure(
			null, userId, groupId,
			DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID, classNameId,
			null, nameMap, descriptionMap, ddmForm, ddmFormLayout, storageType,
			DDMStructureConstants.TYPE_DEFAULT, serviceContext);
	}

	@Override
	public DDMStructure addStructure(
			long userId, long groupId, String parentStructureKey,
			long classNameId, String structureKey, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, DDMForm ddmForm,
			DDMFormLayout ddmFormLayout, String storageType, int type,
			ServiceContext serviceContext)
		throws PortalException {

		DDMStructure parentStructure = fetchStructure(
			groupId, classNameId, parentStructureKey);

		long parentStructureId =
			DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID;

		if (parentStructure != null) {
			parentStructureId = parentStructure.getStructureId();
		}

		return addStructure(
			null, userId, groupId, parentStructureId, classNameId, structureKey,
			nameMap, descriptionMap, ddmForm, ddmFormLayout, storageType, type,
			serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DDMStructure addStructure(
			String externalReferenceCode, long userId, long groupId,
			long parentStructureId, long classNameId, String structureKey,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			DDMForm ddmForm, DDMFormLayout ddmFormLayout, String storageType,
			int type, ServiceContext serviceContext)
		throws PortalException {

		// Structure

		User user = _userLocalService.getUser(userId);

		if (Validator.isNull(structureKey)) {
			structureKey = String.valueOf(counterLocalService.increment());
		}
		else {
			structureKey = StringUtil.toUpperCase(structureKey.trim());
		}

		if (classNameId == _classNameLocalService.getClassNameId(
				JournalArticle.class)) {

			long parentStructureLayoutId = 0;

			if (parentStructureId > 0) {
				DDMStructure ddmStructure = fetchDDMStructure(
					parentStructureId);

				parentStructureLayoutId =
					ddmStructure.getDefaultDDMStructureLayoutId();
			}

			ddmForm = _ddmDataDefinitionConverter.convertDDMFormDataDefinition(
				ddmForm, parentStructureId, parentStructureLayoutId);

			if (ddmFormLayout != null) {
				ddmFormLayout =
					_ddmDataDefinitionConverter.
						convertDDMFormLayoutDataDefinition(
							ddmForm, ddmFormLayout);
			}
		}

		_validateExternalReferenceCode(
			externalReferenceCode, 0, groupId, classNameId);

		_validate(
			groupId, parentStructureId, classNameId, structureKey, nameMap,
			ddmForm);

		DDMStructure structure = _addStructure(
			externalReferenceCode, user, groupId, parentStructureId,
			classNameId, structureKey, nameMap, descriptionMap, ddmForm,
			storageType, type, serviceContext);

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addStructureResources(
				structure, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addStructureResources(
				structure, serviceContext.getModelPermissions());
		}

		// Structure version

		DDMStructureVersion structureVersion = _addStructureVersion(
			user, structure, DDMStructureConstants.VERSION_DEFAULT,
			serviceContext);

		// Structure layout

		if (ddmFormLayout != null) {
			DDMStructureLayout structureLayout =
				_ddmStructureLayoutLocalService.addStructureLayout(
					userId, groupId, structureVersion.getStructureVersionId(),
					ddmFormLayout, serviceContext);

			structureLayout.setClassNameId(structure.getClassNameId());
			structureLayout.setStructureLayoutKey(structure.getStructureKey());

			_ddmStructureLayoutLocalService.updateDDMStructureLayout(
				structureLayout);
		}

		// Data provider instance links

		_addDataProviderInstanceLinks(
			groupId, structure.getStructureId(), ddmForm);

		return structure;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DDMStructure addStructure(
			String externalReferenceCode, long userId, long groupId,
			long parentStructureId, long classNameId, String structureKey,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			String definition, String storageType,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		if (Validator.isNull(structureKey)) {
			structureKey = String.valueOf(counterLocalService.increment());
		}
		else {
			structureKey = StringUtil.toUpperCase(structureKey.trim());
		}

		_validateExternalReferenceCode(
			externalReferenceCode, 0, groupId, classNameId);

		DDMStructure structure = ddmStructurePersistence.create(
			counterLocalService.increment());

		structure.setUuid(serviceContext.getUuid());
		structure.setExternalReferenceCode(externalReferenceCode);
		structure.setGroupId(groupId);
		structure.setCompanyId(user.getCompanyId());
		structure.setUserId(user.getUserId());
		structure.setUserName(user.getFullName());
		structure.setVersionUserId(user.getUserId());
		structure.setVersionUserName(user.getFullName());
		structure.setCreateDate(new Date());
		structure.setModifiedDate(new Date());
		structure.setParentStructureId(parentStructureId);
		structure.setClassNameId(classNameId);
		structure.setStructureKey(structureKey);
		structure.setVersion(DDMStructureConstants.VERSION_DEFAULT);
		structure.setNameMap(nameMap);
		structure.setDescriptionMap(descriptionMap);
		structure.setDefinition(definition);
		structure.setStorageType(storageType);
		structure.setType(DDMStructureConstants.TYPE_DEFAULT);

		structure = ddmStructurePersistence.update(structure);

		_addStructureVersion(
			user, structure, DDMStructureConstants.VERSION_DEFAULT,
			serviceContext);

		return structure;
	}

	/**
	 * Adds the resources to the structure.
	 *
	 * @param structure the structure to add resources to
	 * @param addGroupPermissions whether to add group permissions
	 * @param addGuestPermissions whether to add guest permissions
	 */
	@Override
	public void addStructureResources(
			DDMStructure structure, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		String resourceName =
			_ddmPermissionSupport.getStructureModelResourceName(
				structure.getClassName());

		_resourceLocalService.addResources(
			structure.getCompanyId(), structure.getGroupId(),
			structure.getUserId(), resourceName, structure.getStructureId(),
			false, addGroupPermissions, addGuestPermissions);
	}

	/**
	 * Adds the model resources with the permissions to the structure.
	 *
	 * @param structure the structure to add resources to
	 * @param modelPermissions the model permissions to be added
	 */
	@Override
	public void addStructureResources(
			DDMStructure structure, ModelPermissions modelPermissions)
		throws PortalException {

		String resourceName =
			_ddmPermissionSupport.getStructureModelResourceName(
				structure.getClassName());

		_resourceLocalService.addModelResources(
			structure.getCompanyId(), structure.getGroupId(),
			structure.getUserId(), resourceName, structure.getStructureId(),
			modelPermissions);
	}

	/**
	 * Copies a structure, creating a new structure with all the values
	 * extracted from the original one. The new structure supports a new name
	 * and description.
	 *
	 * @param  userId the primary key of the structure's creator/owner
	 * @param  sourceStructureId the primary key of the structure to be copied
	 * @param  nameMap the new structure's locales and localized names
	 * @param  descriptionMap the new structure's locales and localized
	 *         descriptions
	 * @param  serviceContext the service context to be applied. Can set the
	 *         UUID, creation date, modification date, guest permissions, and
	 *         group permissions for the structure.
	 * @return the new structure
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DDMStructure copyStructure(
			long userId, long sourceStructureId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, ServiceContext serviceContext)
		throws PortalException {

		// Structure

		User user = _userLocalService.getUser(userId);

		DDMStructure sourceStructure = ddmStructurePersistence.findByPrimaryKey(
			sourceStructureId);
		String structureKey = String.valueOf(counterLocalService.increment());

		_validate(
			sourceStructure.getGroupId(),
			sourceStructure.getParentStructureId(),
			sourceStructure.getClassNameId(), structureKey, nameMap,
			sourceStructure.getDDMForm());

		DDMStructure targetStructure = _addStructure(
			null, user, sourceStructure.getGroupId(),
			sourceStructure.getParentStructureId(),
			sourceStructure.getClassNameId(), structureKey, nameMap,
			descriptionMap, sourceStructure.getDDMForm(),
			sourceStructure.getStorageType(), sourceStructure.getType(),
			serviceContext);

		// Resources

		_resourceLocalService.copyModelResources(
			sourceStructure.getCompanyId(),
			_ddmPermissionSupport.getStructureModelResourceName(
				sourceStructure.getClassName()),
			sourceStructure.getPrimaryKey(), targetStructure.getPrimaryKey());

		// Structure version

		DDMStructureVersion structureVersion = _addStructureVersion(
			user, targetStructure, DDMStructureConstants.VERSION_DEFAULT,
			serviceContext);

		// Structure layout

		if (sourceStructure.getDDMFormLayout() != null) {
			_ddmStructureLayoutLocalService.addStructureLayout(
				userId, sourceStructure.getGroupId(),
				targetStructure.getClassNameId(),
				targetStructure.getStructureKey(),
				structureVersion.getStructureVersionId(),
				sourceStructure.getDDMFormLayout(), serviceContext);
		}

		// Data provider instance links

		_addDataProviderInstanceLinks(
			sourceStructure.getGroupId(), targetStructure.getStructureId(),
			sourceStructure.getDDMForm());

		return targetStructure;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DDMStructure copyStructure(
			long userId, long sourceStructureId, ServiceContext serviceContext)
		throws PortalException {

		DDMStructure sourceStructure = ddmStructurePersistence.findByPrimaryKey(
			sourceStructureId);

		return copyStructure(
			userId, sourceStructureId, sourceStructure.getNameMap(),
			sourceStructure.getDescriptionMap(), serviceContext);
	}

	/**
	 * Deletes the structure and its resources.
	 *
	 * <p>
	 * Before deleting the structure, this method verifies whether the structure
	 * is required by another entity. If it is needed, an exception is thrown.
	 * </p>
	 *
	 * @param structure the structure to be deleted
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public DDMStructure deleteStructure(DDMStructure structure)
		throws PortalException {

		if (!GroupThreadLocal.isDeleteInProcess()) {
			int count = _ddmStructureLinkPersistence.countByStructureId(
				structure.getStructureId());

			if (count > 0) {
				throw new RequiredStructureException.
					MustNotDeleteStructureReferencedByStructureLinks(
						structure.getStructureId());
			}

			count = ddmStructurePersistence.countByParentStructureId(
				structure.getStructureId());

			if (count > 0) {
				throw new RequiredStructureException.
					MustNotDeleteStructureThatHasChild(
						structure.getStructureId());
			}

			count = _ddmTemplatePersistence.countByClassPK(
				structure.getStructureId());

			if (count > 0) {
				throw new RequiredStructureException.
					MustNotDeleteStructureReferencedByTemplates(
						structure.getStructureId());
			}
		}

		// Structure

		ddmStructurePersistence.remove(structure);

		// Data provider instance links

		_ddmDataProviderInstanceLinkPersistence.removeByStructureId(
			structure.getStructureId());

		// Structure links

		_ddmStructureLinkPersistence.removeByStructureId(
			structure.getStructureId());

		// Structure versions

		List<DDMStructureVersion> structureVersions =
			_ddmStructureVersionPersistence.findByStructureId(
				structure.getStructureId());

		for (DDMStructureVersion structureVersion : structureVersions) {
			try {
				_ddmStructureLayoutPersistence.removeByStructureVersionId(
					structureVersion.getStructureVersionId());
			}
			catch (NoSuchStructureLayoutException
						noSuchStructureLayoutException) {

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchStructureLayoutException);
				}
			}

			_ddmStructureVersionPersistence.remove(structureVersion);
		}

		// Resources

		String resourceName =
			_ddmPermissionSupport.getStructureModelResourceName(
				structure.getClassName());

		_resourceLocalService.deleteResource(
			structure.getCompanyId(), resourceName,
			ResourceConstants.SCOPE_INDIVIDUAL, structure.getStructureId());

		return structure;
	}

	/**
	 * Deletes the structure and its resources.
	 *
	 * <p>
	 * Before deleting the structure, the system verifies whether the structure
	 * is required by another entity. If it is needed, an exception is thrown.
	 * </p>
	 *
	 * @param structureId the primary key of the structure to be deleted
	 */
	@Override
	public void deleteStructure(long structureId) throws PortalException {
		DDMStructure structure = ddmStructurePersistence.findByPrimaryKey(
			structureId);

		ddmStructureLocalService.deleteStructure(structure);
	}

	/**
	 * Deletes the matching structure and its resources.
	 *
	 * <p>
	 * Before deleting the structure, the system verifies whether the structure
	 * is required by another entity. If it is needed, an exception is thrown.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the structure's
	 *        related model
	 * @param structureKey the unique string identifying the structure
	 */
	@Override
	public void deleteStructure(
			long groupId, long classNameId, String structureKey)
		throws PortalException {

		structureKey = _getStructureKey(structureKey);

		DDMStructure structure = ddmStructurePersistence.findByG_C_S(
			groupId, classNameId, structureKey);

		ddmStructureLocalService.deleteStructure(structure);
	}

	/**
	 * Deletes all the structures of the group.
	 *
	 * <p>
	 * Before deleting the structures, the system verifies whether each
	 * structure is required by another entity. If any of the structures are
	 * needed, an exception is thrown.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 */
	@Override
	public void deleteStructures(long groupId) throws PortalException {
		List<DDMStructure> structures = ddmStructurePersistence.findByGroupId(
			groupId);

		_deleteStructures(structures);
	}

	@Override
	public void deleteStructures(long groupId, long classNameId)
		throws PortalException {

		List<DDMStructure> structures = ddmStructurePersistence.findByG_C(
			groupId, classNameId);

		_deleteStructures(structures);
	}

	/**
	 * Returns the structure with the ID.
	 *
	 * @param  structureId the primary key of the structure
	 * @return the structure with the structure ID, or <code>null</code> if a
	 *         matching structure could not be found
	 */
	@Override
	public DDMStructure fetchStructure(long structureId) {
		return ddmStructurePersistence.fetchByPrimaryKey(structureId);
	}

	/**
	 * Returns the structure matching the class name ID, structure key, and
	 * group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @param  structureKey the unique string identifying the structure
	 * @return the matching structure, or <code>null</code> if a matching
	 *         structure could not be found
	 */
	@Override
	public DDMStructure fetchStructure(
		long groupId, long classNameId, String structureKey) {

		structureKey = _getStructureKey(structureKey);

		return ddmStructurePersistence.fetchByG_C_S(
			groupId, classNameId, structureKey);
	}

	/**
	 * Returns the structure matching the class name ID, structure key, and
	 * group, optionally searching ancestor sites (that have sharing enabled)
	 * and global scoped sites.
	 *
	 * <p>
	 * This method first searches in the group. If the structure is still not
	 * found and <code>includeAncestorStructures</code> is set to
	 * <code>true</code>, this method searches the group's ancestor sites (that
	 * have sharing enabled) and lastly searches global scoped sites.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @param  structureKey the unique string identifying the structure
	 * @param  includeAncestorStructures whether to include ancestor sites (that
	 *         have sharing enabled) and include global scoped sites in the
	 *         search
	 * @return the matching structure, or <code>null</code> if a matching
	 *         structure could not be found
	 */
	@Override
	public DDMStructure fetchStructure(
		long groupId, long classNameId, String structureKey,
		boolean includeAncestorStructures) {

		structureKey = _getStructureKey(structureKey);

		DDMStructure structure = ddmStructurePersistence.fetchByG_C_S(
			groupId, classNameId, structureKey);

		if (structure != null) {
			return structure;
		}

		if (!includeAncestorStructures) {
			return null;
		}

		for (long ancestorSiteGroupId :
				_getAncestorSiteAndDepotGroupIds(groupId)) {

			structure = ddmStructurePersistence.fetchByG_C_S(
				ancestorSiteGroupId, classNameId, structureKey);

			if (structure != null) {
				return structure;
			}
		}

		return null;
	}

	@Override
	public DDMStructure fetchStructureByExternalReferenceCode(
		String externalReferenceCode, long groupId, long classNameId) {

		return ddmStructurePersistence.fetchByERC_G_C(
			externalReferenceCode, groupId, classNameId);
	}

	@Override
	public DDMStructure fetchStructureByUuidAndGroupId(
		String uuid, long groupId, boolean includeAncestorStructures) {

		DDMStructure structure = ddmStructurePersistence.fetchByUUID_G(
			uuid, groupId);

		if (structure != null) {
			return structure;
		}

		if (!includeAncestorStructures) {
			return null;
		}

		for (long ancestorSiteGroupId :
				_getAncestorSiteAndDepotGroupIds(groupId)) {

			structure = ddmStructurePersistence.fetchByUUID_G(
				uuid, ancestorSiteGroupId);

			if (structure != null) {
				return structure;
			}
		}

		return null;
	}

	@Override
	public List<DDMStructure> getChildrenStructures(long parentStructureId) {
		return ddmStructurePersistence.findByParentStructureId(
			parentStructureId);
	}

	/**
	 * Returns all the structures matching the class name ID.
	 *
	 * @param  companyId the primary key of the structure's company
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @return the structures matching the class name ID
	 */
	@Override
	public List<DDMStructure> getClassStructures(
		long companyId, long classNameId) {

		return ddmStructurePersistence.findByC_C(companyId, classNameId);
	}

	/**
	 * Returns a range of all the structures matching the class name ID.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  companyId the primary key of the structure's company
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @param  start the lower bound of the range of structures to return
	 * @param  end the upper bound of the range of structures to return (not
	 *         inclusive)
	 * @return the range of matching structures
	 */
	@Override
	public List<DDMStructure> getClassStructures(
		long companyId, long classNameId, int start, int end) {

		return ddmStructurePersistence.findByC_C(
			companyId, classNameId, start, end);
	}

	/**
	 * Returns all the structures matching the class name ID ordered by the
	 * comparator.
	 *
	 * @param  companyId the primary key of the structure's company
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @param  orderByComparator the comparator to order the structures
	 *         (optionally <code>null</code>)
	 * @return the matching structures ordered by the comparator
	 */
	@Override
	public List<DDMStructure> getClassStructures(
		long companyId, long classNameId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return ddmStructurePersistence.findByC_C(
			companyId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			orderByComparator);
	}

	/**
	 * Returns the structure with the ID.
	 *
	 * @param  structureId the primary key of the structure
	 * @return the structure with the ID
	 */
	@Override
	public DDMStructure getStructure(long structureId) throws PortalException {
		return ddmStructurePersistence.findByPrimaryKey(structureId);
	}

	/**
	 * Returns the structure matching the class name ID, structure key, and
	 * group.
	 *
	 * @param  groupId the primary key of the structure's group
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @param  structureKey the unique string identifying the structure
	 * @return the matching structure
	 */
	@Override
	public DDMStructure getStructure(
			long groupId, long classNameId, String structureKey)
		throws PortalException {

		structureKey = _getStructureKey(structureKey);

		return ddmStructurePersistence.findByG_C_S(
			groupId, classNameId, structureKey);
	}

	/**
	 * Returns the structure matching the class name ID, structure key, and
	 * group, optionally searching ancestor sites (that have sharing enabled)
	 * and global scoped sites.
	 *
	 * <p>
	 * This method first searches in the group. If the structure is still not
	 * found and <code>includeAncestorStructures</code> is set to
	 * <code>true</code>, this method searches the group's ancestor sites (that
	 * have sharing enabled) and lastly searches global scoped sites.
	 * </p>
	 *
	 * @param  groupId the primary key of the structure's group
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @param  structureKey the unique string identifying the structure
	 * @param  includeAncestorStructures whether to include ancestor sites (that
	 *         have sharing enabled) and include global scoped sites in the
	 *         search in the search
	 * @return the matching structure
	 */
	@Override
	public DDMStructure getStructure(
			long groupId, long classNameId, String structureKey,
			boolean includeAncestorStructures)
		throws PortalException {

		structureKey = _getStructureKey(structureKey);

		DDMStructure structure = ddmStructurePersistence.fetchByG_C_S(
			groupId, classNameId, structureKey);

		if (structure != null) {
			return structure;
		}

		if (!includeAncestorStructures) {
			throw new NoSuchStructureException(
				"No DDMStructure exists with the structure key " +
					structureKey);
		}

		for (long curGroupId : _getAncestorSiteAndDepotGroupIds(groupId)) {
			structure = ddmStructurePersistence.fetchByG_C_S(
				curGroupId, classNameId, structureKey);

			if (structure != null) {
				return structure;
			}
		}

		throw new NoSuchStructureException(
			"No DDMStructure exists with the structure key " + structureKey +
				" in the ancestor groups");
	}

	/**
	 * Returns all the structures matching the group, name, and description.
	 *
	 * @param  groupId the primary key of the structure's group
	 * @param  name the structure's name
	 * @param  description the structure's description
	 * @return the matching structures
	 */
	@Override
	public List<DDMStructure> getStructure(
		long groupId, String name, String description) {

		return ddmStructurePersistence.findByG_N_D(groupId, name, description);
	}

	@Override
	public DDMStructure getStructureByExternalReferenceCode(
			String externalReferenceCode, long groupId, long classNameId)
		throws PortalException {

		return ddmStructurePersistence.findByERC_G_C(
			externalReferenceCode, groupId, classNameId);
	}

	@Override
	@Transactional(enabled = false)
	public DDMForm getStructureDDMForm(DDMStructure structure)
		throws PortalException {

		return _deserializeJSONDDMForm(structure.getDefinition());
	}

	/**
	 * Returns all the structures present in the system.
	 *
	 * @return the structures present in the system
	 */
	@Override
	public List<DDMStructure> getStructures() {
		return ddmStructurePersistence.findAll();
	}

	/**
	 * Returns all the structures present in the group.
	 *
	 * @param  groupId the primary key of the group
	 * @return the structures present in the group
	 */
	@Override
	public List<DDMStructure> getStructures(long groupId) {
		return ddmStructurePersistence.findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the structures belonging to the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  start the lower bound of the range of structures to return
	 * @param  end the upper bound of the range of structures to return (not
	 *         inclusive)
	 * @return the range of matching structures
	 */
	@Override
	public List<DDMStructure> getStructures(long groupId, int start, int end) {
		return ddmStructurePersistence.findByGroupId(groupId, start, end);
	}

	/**
	 * Returns all the structures matching class name ID and group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @return the matching structures
	 */
	@Override
	public List<DDMStructure> getStructures(long groupId, long classNameId) {
		return ddmStructurePersistence.findByG_C(groupId, classNameId);
	}

	/**
	 * Returns a range of all the structures that match the class name ID and
	 * group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @param  start the lower bound of the range of structures to return
	 * @param  end the upper bound of the range of structures to return (not
	 *         inclusive)
	 * @return the range of matching structures
	 */
	@Override
	public List<DDMStructure> getStructures(
		long groupId, long classNameId, int start, int end) {

		return ddmStructurePersistence.findByG_C(
			groupId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the structures matching the class name ID
	 * and group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @param  start the lower bound of the range of structures to return
	 * @param  end the upper bound of the range of structures to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the structures
	 *         (optionally <code>null</code>)
	 * @return the range of matching structures ordered by the comparator
	 */
	@Override
	public List<DDMStructure> getStructures(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return ddmStructurePersistence.findByG_C(
			groupId, classNameId, start, end, orderByComparator);
	}

	@Override
	public List<DDMStructure> getStructures(
		long companyId, long[] groupIds, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getStructures(
			companyId, groupIds, classNameId, StringPool.BLANK,
			WorkflowConstants.STATUS_ANY, start, end, orderByComparator);
	}

	@Override
	public List<DDMStructure> getStructures(
		long companyId, long[] groupIds, long classNameId, String keywords,
		int status, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		Table<?> tempDDMStructureTable = DSLQueryFactoryUtil.selectDistinct(
			DDMStructureTable.INSTANCE.structureId
		).from(
			DDMStructureTable.INSTANCE
		).where(
			_getPredicate(companyId, groupIds, classNameId, keywords)
		).as(
			"tempDDMStructure", DDMStructureTable.INSTANCE
		);

		return ddmStructurePersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				DDMStructureTable.INSTANCE
			).from(
				tempDDMStructureTable
			).innerJoinON(
				DDMStructureTable.INSTANCE,
				DDMStructureTable.INSTANCE.structureId.eq(
					tempDDMStructureTable.getColumn("structureId", Long.class))
			).orderBy(
				DDMStructureTable.INSTANCE, orderByComparator
			).limit(
				start, end
			));
	}

	@Override
	public List<DDMStructure> getStructures(
		long groupId, String name, String description) {

		return ddmStructurePersistence.findByG_N_D(groupId, name, description);
	}

	/**
	 * Returns all the structures belonging to the groups.
	 *
	 * @param  groupIds the primary keys of the groups
	 * @return the structures belonging to the groups
	 */
	@Override
	public List<DDMStructure> getStructures(long[] groupIds) {
		return ddmStructurePersistence.findByGroupId(groupIds);
	}

	/**
	 * Returns all the structures matching the class name ID and belonging to
	 * the groups.
	 *
	 * @param  groupIds the primary keys of the groups
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @return the matching structures
	 */
	@Override
	public List<DDMStructure> getStructures(long[] groupIds, long classNameId) {
		return ddmStructurePersistence.findByG_C(groupIds, classNameId);
	}

	/**
	 * Returns a range of all the structures matching the class name ID and
	 * belonging to the groups.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupIds the primary keys of the groups
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @param  start the lower bound of the range of structures to return
	 * @param  end the upper bound of the range of structures to return (not
	 *         inclusive)
	 * @return the range of matching structures
	 */
	@Override
	public List<DDMStructure> getStructures(
		long[] groupIds, long classNameId, int start, int end) {

		return ddmStructurePersistence.findByG_C(
			groupIds, classNameId, start, end);
	}

	@Override
	public List<DDMStructure> getStructures(
		long[] groupIds, long classNameId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return ddmStructurePersistence.findByG_C(
			groupIds, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the structures matching the group, class
	 * name ID, name, and description.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupIds the primary keys of the groups
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @param  name the name keywords
	 * @param  description the description keywords
	 * @param  start the lower bound of the range of structures to return
	 * @param  end the upper bound of the range of structures to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the structures
	 *         (optionally <code>null</code>)
	 * @return the range of matching structures ordered by the comparator
	 */
	@Override
	public List<DDMStructure> getStructures(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		return ddmStructurePersistence.findByG_C_N_D(
			groupIds, classNameId, name, description, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of structures belonging to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @return the number of structures belonging to the group
	 */
	@Override
	public int getStructuresCount(long groupId) {
		return ddmStructurePersistence.countByGroupId(groupId);
	}

	/**
	 * Returns the number of structures matching the class name ID and group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @return the number of matching structures
	 */
	@Override
	public int getStructuresCount(long groupId, long classNameId) {
		return ddmStructurePersistence.countByG_C(groupId, classNameId);
	}

	@Override
	public int getStructuresCount(
		long companyId, long[] groupIds, long classNameId, String keywords,
		int status) {

		return ddmStructurePersistence.dslQueryCount(
			DSLQueryFactoryUtil.countDistinct(
				DDMStructureTable.INSTANCE.structureId
			).from(
				DDMStructureTable.INSTANCE
			).where(
				_getPredicate(companyId, groupIds, classNameId, keywords)
			));
	}

	/**
	 * Returns the number of structures matching the class name ID and belonging
	 * to the groups.
	 *
	 * @param  groupIds the primary keys of the groups
	 * @param  classNameId the primary key of the class name for the structure's
	 *         related model
	 * @return the number of matching structures
	 */
	@Override
	public int getStructuresCount(long[] groupIds, long classNameId) {
		return ddmStructurePersistence.countByG_C(groupIds, classNameId);
	}

	@Override
	public boolean hasStructure(
		long groupId, long classNameId, String structureKey) {

		structureKey = _getStructureKey(structureKey);

		int count = ddmStructurePersistence.countByG_C_S(
			groupId, classNameId, structureKey);

		if (count == 0) {
			return false;
		}

		return true;
	}

	@Override
	public String prepareLocalizedDefinitionForImport(
			DDMStructure structure, Locale defaultImportLocale)
		throws PortalException {

		DDMForm ddmForm = _ddm.updateDDMFormDefaultLocale(
			structure.getDDMForm(), defaultImportLocale);

		return _serializeJSONDDMForm(ddmForm);
	}

	@Override
	public void revertStructure(
			long userId, long structureId, String version,
			ServiceContext serviceContext)
		throws PortalException {

		DDMStructureVersion structureVersion =
			_ddmStructureVersionPersistence.findByS_V(structureId, version);

		if (!structureVersion.isApproved()) {
			throw new InvalidStructureVersionException(
				"Unable to revert from an unapproved file version");
		}

		DDMStructure structure = structureVersion.getStructure();

		serviceContext.setAttribute("majorVersion", Boolean.TRUE);
		serviceContext.setAttribute(
			"status", WorkflowConstants.STATUS_APPROVED);
		serviceContext.setCommand(Constants.REVERT);

		ddmStructureLocalService.updateStructure(
			userId, structure.getGroupId(),
			structureVersion.getParentStructureId(), structure.getClassNameId(),
			structure.getStructureKey(), structureVersion.getNameMap(),
			structureVersion.getDescriptionMap(), structureVersion.getDDMForm(),
			structureVersion.getDDMFormLayout(), serviceContext);
	}

	@Override
	public List<DDMStructure> search(
			long companyId, long[] groupIds, long classNameId, long classPK,
			String keywords, int start, int end,
			OrderByComparator<DDMStructure> orderByComparator)
		throws PortalException {

		SearchContext searchContext = DDMSearchUtil.buildStructureSearchContext(
			_ddmPermissionSupport, companyId, groupIds, classNameId, classPK,
			keywords, keywords, StringPool.BLANK, null,
			WorkflowConstants.STATUS_ANY, start, end, orderByComparator);

		return DDMSearchUtil.doSearch(
			searchContext, DDMStructure.class,
			ddmStructurePersistence::findByPrimaryKey);
	}

	/**
	 * Returns an ordered range of all the structures matching the groups and
	 * class name IDs, and matching the keywords in the structure names and
	 * descriptions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  companyId the primary key of the structure's company
	 * @param  groupIds the primary keys of the groups
	 * @param  classNameId the primary key of the class name of the model the
	 *         structure is related to
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         structure's name or description (optionally <code>null</code>)
	 * @param  start the lower bound of the range of structures to return
	 * @param  end the upper bound of the range of structures to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the structures
	 *         (optionally <code>null</code>)
	 * @return the range of matching structures ordered by the comparator
	 */
	@Override
	public List<DDMStructure> search(
		long companyId, long[] groupIds, long classNameId, String keywords,
		int status, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		SearchContext searchContext = DDMSearchUtil.buildStructureSearchContext(
			_ddmPermissionSupport, companyId, groupIds, classNameId, null,
			keywords, keywords, StringPool.BLANK, null, status, start, end,
			orderByComparator);

		return DDMSearchUtil.doSearch(
			searchContext, DDMStructure.class,
			ddmStructurePersistence::findByPrimaryKey);
	}

	/**
	 * Returns an ordered range of all the structures matching the groups, class
	 * name IDs, name keyword, description keyword, storage type, and type.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  companyId the primary key of the structure's company
	 * @param  groupIds the primary keys of the groups
	 * @param  classNameId the primary key of the class name of the model the
	 *         structure is related to
	 * @param  name the name keywords
	 * @param  description the description keywords
	 * @param  storageType the structure's storage type. It can be "xml" or
	 *         "expando". For more information, see {@link StorageType}.
	 * @param  type the structure's type. For more information, see {@link
	 *         DDMStructureConstants}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field
	 * @param  start the lower bound of the range of structures to return
	 * @param  end the upper bound of the range of structures to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the structures
	 *         (optionally <code>null</code>)
	 * @return the range of matching structures ordered by the comparator
	 */
	@Override
	public List<DDMStructure> search(
		long companyId, long[] groupIds, long classNameId, String name,
		String description, String storageType, int type, int status,
		boolean andOperator, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		SearchContext searchContext = DDMSearchUtil.buildStructureSearchContext(
			_ddmPermissionSupport, companyId, groupIds, classNameId, null, name,
			description, storageType, type, status, start, end,
			orderByComparator);

		return DDMSearchUtil.doSearch(
			searchContext, DDMStructure.class,
			ddmStructurePersistence::findByPrimaryKey);
	}

	@Override
	public int searchCount(
			long companyId, long[] groupIds, long classNameId, long classPK,
			String keywords)
		throws PortalException {

		SearchContext searchContext = DDMSearchUtil.buildStructureSearchContext(
			_ddmPermissionSupport, companyId, groupIds, classNameId, classPK,
			keywords, keywords, StringPool.BLANK, null,
			WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);

		return DDMSearchUtil.doSearchCount(searchContext, DDMStructure.class);
	}

	/**
	 * Returns the number of structures matching the groups and class name IDs,
	 * and matching the keywords in the structure names and descriptions.
	 *
	 * @param  companyId the primary key of the structure's company
	 * @param  groupIds the primary keys of the groups
	 * @param  classNameId the primary key of the class name of the model the
	 *         structure is related to
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         structure's name or description (optionally <code>null</code>)
	 * @return the number of matching structures
	 */
	@Override
	public int searchCount(
		long companyId, long[] groupIds, long classNameId, String keywords,
		int status) {

		SearchContext searchContext = DDMSearchUtil.buildStructureSearchContext(
			_ddmPermissionSupport, companyId, groupIds, classNameId, null,
			keywords, keywords, StringPool.BLANK, null, status,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		return DDMSearchUtil.doSearchCount(searchContext, DDMStructure.class);
	}

	/**
	 * Returns the number of structures matching the groups, class name IDs,
	 * name keyword, description keyword, storage type, and type
	 *
	 * @param  companyId the primary key of the structure's company
	 * @param  groupIds the primary keys of the groups
	 * @param  classNameId
	 * @param  name the name keywords
	 * @param  description the description keywords
	 * @param  storageType the structure's storage type. It can be "xml" or
	 *         "expando". For more information, see {@link StorageType}.
	 * @param  type the structure's type. For more information, see {@link
	 *         DDMStructureConstants}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field
	 * @return the number of matching structures
	 */
	@Override
	public int searchCount(
		long companyId, long[] groupIds, long classNameId, String name,
		String description, String storageType, int type, int status,
		boolean andOperator) {

		SearchContext searchContext = DDMSearchUtil.buildStructureSearchContext(
			_ddmPermissionSupport, companyId, groupIds, classNameId, null, name,
			description, storageType, type, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		return DDMSearchUtil.doSearchCount(searchContext, DDMStructure.class);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DDMStructure updateStructure(
			long userId, long structureId, DDMForm ddmForm,
			DDMFormLayout ddmFormLayout, ServiceContext serviceContext)
		throws PortalException {

		DDMStructure structure = ddmStructurePersistence.findByPrimaryKey(
			structureId);

		return _updateStructure(
			userId, structure.getParentStructureId(), structure.getNameMap(),
			structure.getDescriptionMap(), ddmForm, ddmFormLayout,
			serviceContext, structure);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DDMStructure updateStructure(
			long userId, long groupId, long parentStructureId, long classNameId,
			String structureKey, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, DDMForm ddmForm,
			DDMFormLayout ddmFormLayout, ServiceContext serviceContext)
		throws PortalException {

		structureKey = _getStructureKey(structureKey);

		DDMStructure structure = ddmStructurePersistence.findByG_C_S(
			groupId, classNameId, structureKey);

		return _updateStructure(
			userId, parentStructureId, nameMap, descriptionMap, ddmForm,
			ddmFormLayout, serviceContext, structure);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DDMStructure updateStructure(
			long userId, long structureId, long parentStructureId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			DDMForm ddmForm, DDMFormLayout ddmFormLayout,
			ServiceContext serviceContext)
		throws PortalException {

		DDMStructure structure = ddmStructurePersistence.findByPrimaryKey(
			structureId);

		return _updateStructure(
			userId, parentStructureId, nameMap, descriptionMap, ddmForm,
			ddmFormLayout, serviceContext, structure);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DDMStructure updateStructure(
			String externalReferenceCode, long userId, long structureId,
			long groupId, long parentStructureId, long classNameId,
			String structureKey, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, String definition,
			ServiceContext serviceContext)
		throws PortalException {

		_validateExternalReferenceCode(
			externalReferenceCode, structureId, groupId, classNameId);

		DDMStructure structure = ddmStructurePersistence.findByPrimaryKey(
			structureId);

		structure.setExternalReferenceCode(externalReferenceCode);

		User user = _userLocalService.getUser(userId);

		structure.setUserId(userId);
		structure.setParentStructureId(parentStructureId);

		if (Validator.isNotNull(structureKey)) {
			structureKey = StringUtil.toUpperCase(structureKey.trim());

			_validateStructureKey(
				structureId, structure.getGroupId(), structure.getClassNameId(),
				structureKey);

			structure.setStructureKey(structureKey);
		}

		DDMStructureVersion latestStructureVersion =
			_ddmStructureVersionLocalService.getLatestStructureVersion(
				structure.getStructureId());

		structure.setVersion(latestStructureVersion.getVersion());

		structure.setVersionUserId(user.getUserId());
		structure.setVersionUserName(user.getFullName());
		structure.setModifiedDate(new Date());
		structure.setNameMap(
			nameMap,
			LocaleUtil.fromLanguageId(structure.getDefaultLanguageId()));
		structure.setDescriptionMap(descriptionMap);
		structure.setDefinition(definition);

		structure = ddmStructurePersistence.update(structure);

		_reindexStructure(structure, serviceContext);

		return structure;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DDMStructureIndexer.class,
			"ddm.structure.indexer.class.name");
	}

	private void _addDataProviderInstanceLinks(
		long groupId, long structureId, DDMForm ddmForm) {

		Set<Long> dataProviderInstanceIds = _getDataProviderInstanceIds(
			groupId, ddmForm);

		for (Long dataProviderInstanceId : dataProviderInstanceIds) {
			_ddmDataProviderInstanceLinkLocalService.
				addDataProviderInstanceLink(
					dataProviderInstanceId, structureId);
		}
	}

	private DDMStructure _addStructure(
			String externalReferenceCode, User user, long groupId,
			long parentStructureId, long classNameId, String structureKey,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			DDMForm ddmForm, String storageType, int type,
			ServiceContext serviceContext)
		throws PortalException {

		DDMStructure structure = ddmStructurePersistence.create(
			counterLocalService.increment());

		structure.setUuid(serviceContext.getUuid());
		structure.setExternalReferenceCode(externalReferenceCode);
		structure.setGroupId(groupId);
		structure.setCompanyId(user.getCompanyId());
		structure.setUserId(user.getUserId());
		structure.setUserName(user.getFullName());
		structure.setVersionUserId(user.getUserId());
		structure.setVersionUserName(user.getFullName());
		structure.setParentStructureId(parentStructureId);
		structure.setClassNameId(classNameId);
		structure.setStructureKey(structureKey);
		structure.setVersion(DDMStructureConstants.VERSION_DEFAULT);
		structure.setNameMap(nameMap, ddmForm.getDefaultLocale());
		structure.setDescriptionMap(descriptionMap, ddmForm.getDefaultLocale());
		structure.setDefinition(_serializeJSONDDMForm(ddmForm));
		structure.setStorageType(storageType);
		structure.setType(type);

		return ddmStructurePersistence.update(structure);
	}

	private DDMStructureVersion _addStructureVersion(
		User user, DDMStructure structure, String version,
		ServiceContext serviceContext) {

		DDMStructureVersion structureVersion =
			_ddmStructureVersionPersistence.create(
				counterLocalService.increment());

		structureVersion.setGroupId(structure.getGroupId());
		structureVersion.setCompanyId(structure.getCompanyId());
		structureVersion.setUserId(user.getUserId());
		structureVersion.setUserName(user.getFullName());
		structureVersion.setCreateDate(structure.getModifiedDate());
		structureVersion.setStructureId(structure.getStructureId());
		structureVersion.setVersion(version);
		structureVersion.setParentStructureId(structure.getParentStructureId());
		structureVersion.setName(structure.getName());
		structureVersion.setDescription(structure.getDescription());
		structureVersion.setDefinition(structure.getDefinition());
		structureVersion.setStorageType(structure.getStorageType());
		structureVersion.setType(structure.getType());
		structureVersion.setStatus(
			GetterUtil.getInteger(
				serviceContext.getAttribute("status"),
				WorkflowConstants.STATUS_APPROVED));
		structureVersion.setStatusByUserId(user.getUserId());
		structureVersion.setStatusByUserName(user.getFullName());
		structureVersion.setStatusDate(structure.getModifiedDate());

		return _ddmStructureVersionPersistence.update(structureVersion);
	}

	private Set<Long> _deleteStructures(List<DDMStructure> structures)
		throws PortalException {

		if (ListUtil.isEmpty(structures)) {
			return Collections.emptySet();
		}

		Set<Long> deletedStructureIds = new HashSet<>();

		for (DDMStructure structure : structures) {
			if (deletedStructureIds.contains(structure.getStructureId())) {
				continue;
			}

			if (!GroupThreadLocal.isDeleteInProcess()) {
				List<DDMStructure> childDDMStructures =
					ddmStructurePersistence.findByParentStructureId(
						structure.getStructureId());

				deletedStructureIds.addAll(
					_deleteStructures(childDDMStructures));
			}

			ddmStructureLocalService.deleteStructure(structure);

			deletedStructureIds.add(structure.getStructureId());
		}

		return deletedStructureIds;
	}

	private DDMForm _deserializeDDMForm(
		String content, DDMFormDeserializer ddmFormDeserializer) {

		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(content);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				ddmFormDeserializer.deserialize(builder.build());

		return ddmFormDeserializerDeserializeResponse.getDDMForm();
	}

	private DDMForm _deserializeJSONDDMForm(String content) {
		return _deserializeDDMForm(content, _jsonDDMFormDeserializer);
	}

	private long[] _getAncestorSiteAndDepotGroupIds(long groupId) {
		SiteConnectedGroupGroupProvider siteConnectedGroupGroupProvider =
			_siteConnectedGroupGroupProviderSnapshot.get();

		try {
			if (siteConnectedGroupGroupProvider == null) {
				return _portal.getAncestorSiteGroupIds(groupId);
			}

			return siteConnectedGroupGroupProvider.
				getAncestorSiteAndDepotGroupIds(groupId, true);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return new long[0];
		}
	}

	private Set<Long> _getDataProviderInstanceIds(
		long groupId, DDMForm ddmForm) {

		Set<Long> dataProviderInstanceIds = new HashSet<>();

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (DDMFormField ddmFormField : ddmFormFieldsMap.values()) {
			long[] ddmDataProviderInstanceIds = _getDDMDataProviderInstanceIds(
				ddmFormField.getProperty("ddmDataProviderInstanceId"));

			for (long ddmDataProviderInstanceId : ddmDataProviderInstanceIds) {
				if (ddmDataProviderInstanceId > 0) {
					dataProviderInstanceIds.add(ddmDataProviderInstanceId);
				}
			}
		}

		for (DDMFormRule ddmFormRule : ddmForm.getDDMFormRules()) {
			Set<Long> ddmFormDataProviderInstanceIds =
				_getDataProviderInstanceIds(groupId, ddmFormRule);

			dataProviderInstanceIds.addAll(ddmFormDataProviderInstanceIds);
		}

		return dataProviderInstanceIds;
	}

	private Set<Long> _getDataProviderInstanceIds(
		long groupId, DDMFormRule ddmFormRule) {

		List<String> actions = ddmFormRule.getActions();

		if (ListUtil.isEmpty(actions)) {
			return Collections.emptySet();
		}

		Set<Long> dataProviderInstanceIds = new HashSet<>();

		for (String action : actions) {
			Matcher matcher = _callFunctionPattern.matcher(action);

			while (matcher.find()) {
				String dataProviderUuid = matcher.group(1);

				DDMDataProviderInstance dataProviderInstance =
					_ddmDataProviderInstanceLocalService.
						fetchDDMDataProviderInstanceByUuidAndGroupId(
							dataProviderUuid, groupId);

				if (dataProviderInstance != null) {
					dataProviderInstanceIds.add(
						dataProviderInstance.getDataProviderInstanceId());
				}
			}
		}

		return dataProviderInstanceIds;
	}

	private long[] _getDDMDataProviderInstanceIds(JSONArray jsonArray) {
		long[] ddmDataProviderInstanceIds = new long[jsonArray.length()];

		for (int i = 0; i < jsonArray.length(); i++) {
			ddmDataProviderInstanceIds[i] = jsonArray.getLong(i);
		}

		return ddmDataProviderInstanceIds;
	}

	private long[] _getDDMDataProviderInstanceIds(
		Object ddmDataProviderInstanceId) {

		if (ddmDataProviderInstanceId instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray)ddmDataProviderInstanceId;

			return _getDDMDataProviderInstanceIds(jsonArray);
		}
		else if (ddmDataProviderInstanceId instanceof String) {
			try {
				JSONArray jsonArray = _jsonFactory.createJSONArray(
					(String)ddmDataProviderInstanceId);

				return _getDDMDataProviderInstanceIds(jsonArray);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		long ddmDataProviderInstanceIdLong = GetterUtil.getLong(
			ddmDataProviderInstanceId);

		if (ddmDataProviderInstanceIdLong > 0) {
			return new long[] {ddmDataProviderInstanceIdLong};
		}

		return GetterUtil.DEFAULT_LONG_VALUES;
	}

	private Set<String> _getDDMFormFieldsNames(DDMForm ddmForm) {
		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		if (MapUtil.isEmpty(ddmFormFieldsMap)) {
			return Collections.emptySet();
		}

		Set<String> ddmFormFieldsNames = new HashSet<>();

		for (String ddmFormFieldName : ddmFormFieldsMap.keySet()) {
			ddmFormFieldsNames.add(StringUtil.toLowerCase(ddmFormFieldName));
		}

		return ddmFormFieldsNames;
	}

	private String _getNextVersion(String version, boolean majorVersion) {
		int[] versionParts = StringUtil.split(version, StringPool.PERIOD, 0);

		if (majorVersion) {
			versionParts[0]++;
			versionParts[1] = 0;
		}
		else {
			versionParts[1]++;
		}

		return versionParts[0] + StringPool.PERIOD + versionParts[1];
	}

	private DDMForm _getParentDDMForm(long parentStructureId) {
		DDMStructure parentStructure =
			ddmStructurePersistence.fetchByPrimaryKey(parentStructureId);

		if (parentStructure == null) {
			return null;
		}

		return parentStructure.getFullHierarchyDDMForm();
	}

	private Predicate _getPredicate(
		long companyId, long[] groupIds, long classNameId, String keywords) {

		Predicate predicate = DDMStructureTable.INSTANCE.companyId.eq(
			companyId
		).and(
			DDMStructureTable.INSTANCE.classNameId.eq(classNameId)
		).and(
			DDMStructureTable.INSTANCE.type.eq(0)
		);

		Predicate groupIdsPredicate = null;

		for (long groupId : groupIds) {
			Predicate groupIdPredicate = DDMStructureTable.INSTANCE.groupId.eq(
				groupId);

			if (groupIdsPredicate == null) {
				groupIdsPredicate = groupIdPredicate;
			}
			else {
				groupIdsPredicate = groupIdsPredicate.or(groupIdPredicate);
			}
		}

		if (groupIdsPredicate != null) {
			predicate = predicate.and(groupIdsPredicate.withParentheses());
		}

		Predicate keywordsPredicate = null;

		for (String keyword : _customSQL.keywords(keywords, true)) {
			if (keyword == null) {
				continue;
			}

			Predicate keywordPredicate = DSLFunctionFactoryUtil.lower(
				DSLFunctionFactoryUtil.castText(DDMStructureTable.INSTANCE.name)
			).like(
				keyword
			).or(
				DSLFunctionFactoryUtil.lower(
					DSLFunctionFactoryUtil.castClobText(
						DDMStructureTable.INSTANCE.description)
				).like(
					keyword
				)
			);

			if (keywordsPredicate == null) {
				keywordsPredicate = keywordPredicate;
			}
			else {
				keywordsPredicate = keywordsPredicate.or(keywordPredicate);
			}
		}

		if (keywordsPredicate != null) {
			predicate = predicate.and(keywordsPredicate.withParentheses());
		}

		return predicate;
	}

	private String _getStructureKey(String structureKey) {
		if (structureKey != null) {
			structureKey = structureKey.trim();

			return StringUtil.toUpperCase(structureKey);
		}

		return StringPool.BLANK;
	}

	private List<DDMTemplate> _getStructureTemplates(
		DDMStructure structure, String type) {

		return _ddmTemplateLocalService.getTemplates(
			structure.getGroupId(),
			_classNameLocalService.getClassNameId(DDMStructure.class),
			structure.getStructureId(), type);
	}

	private void _reindexStructure(
			DDMStructure structure, ServiceContext serviceContext)
		throws PortalException {

		if (!serviceContext.isIndexingEnabled()) {
			return;
		}

		DDMStructureIndexer ddmStructureIndexer = _serviceTrackerMap.getService(
			structure.getClassName());

		if (ddmStructureIndexer == null) {
			return;
		}

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				Message message = new Message();

				message.put("ddmStructureIndexer", ddmStructureIndexer);
				message.put("structureId", structure.getStructureId());

				_messageBus.sendMessage(
					DDMDestinationNames.DDM_STRUCTURE_REINDEX, message);

				return null;
			});
	}

	private String _serializeJSONDDMForm(DDMForm ddmForm)
		throws PortalException {

		try {
			DDMFormFieldUtil.sortNestedDDMFormFields(
				ddmForm.getDDMFormFields());

			DDMFormSerializerSerializeRequest.Builder builder =
				DDMFormSerializerSerializeRequest.Builder.newBuilder(ddmForm);

			DDMFormSerializerSerializeResponse
				ddmFormSerializerSerializeResponse =
					_jsonDDMFormSerializer.serialize(builder.build());

			return ddmFormSerializerSerializeResponse.getContent();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to serialize dynamic data mapping form to JSON: " +
						ddmForm,
					exception);
			}

			throw new StructureDefinitionException(exception);
		}
	}

	private void _syncStructureTemplatesFields(DDMStructure structure) {
		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				DDMFormTemplateSynchonizer ddmFormTemplateSynchonizer =
					new DDMFormTemplateSynchonizer(
						structure.getDDMForm(), _jsonDDMFormDeserializer,
						_jsonDDMFormSerializer, _ddmTemplateLocalService);

				List<DDMTemplate> templates = _getStructureTemplates(
					structure, DDMTemplateConstants.TEMPLATE_TYPE_FORM);

				ddmFormTemplateSynchonizer.setDDMFormTemplates(templates);

				ddmFormTemplateSynchonizer.synchronize();

				return null;
			});
	}

	private void _updateDataProviderInstanceLinks(
		long groupId, long structureId, DDMForm ddmForm) {

		Set<Long> dataProviderInstanceIds = _getDataProviderInstanceIds(
			groupId, ddmForm);

		List<DDMDataProviderInstanceLink> dataProviderInstanceLinks =
			_ddmDataProviderInstanceLinkLocalService.
				getDataProviderInstanceLinks(structureId);

		for (DDMDataProviderInstanceLink dataProviderInstanceLink :
				dataProviderInstanceLinks) {

			if (dataProviderInstanceIds.remove(
					dataProviderInstanceLink.getDataProviderInstanceId())) {

				continue;
			}

			_ddmDataProviderInstanceLinkLocalService.
				deleteDataProviderInstanceLink(dataProviderInstanceLink);
		}

		for (Long dataProviderInstanceId : dataProviderInstanceIds) {
			_ddmDataProviderInstanceLinkLocalService.
				addDataProviderInstanceLink(
					dataProviderInstanceId, structureId);
		}
	}

	private DDMStructure _updateStructure(
			long userId, long parentStructureId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, DDMForm ddmForm,
			DDMFormLayout ddmFormLayout, ServiceContext serviceContext,
			DDMStructure structure)
		throws PortalException {

		// Structure

		User user = _userLocalService.getUser(userId);

		DDMForm parentDDMForm = _getParentDDMForm(parentStructureId);

		_validateParentStructure(structure.getStructureId(), parentStructureId);
		_validate(nameMap, parentDDMForm, ddmForm);

		structure.setUserId(userId);
		structure.setParentStructureId(parentStructureId);

		DDMStructureVersion latestStructureVersion =
			_ddmStructureVersionLocalService.getLatestStructureVersion(
				structure.getStructureId());

		boolean updateVersion = false;

		if (latestStructureVersion.getStatus() ==
				WorkflowConstants.STATUS_DRAFT) {

			updateVersion = true;
		}

		boolean majorVersion = GetterUtil.getBoolean(
			serviceContext.getAttribute("majorVersion"));

		String version = _getNextVersion(
			latestStructureVersion.getVersion(), majorVersion);

		if (!updateVersion) {
			structure.setVersionUserId(user.getUserId());
			structure.setVersionUserName(user.getFullName());
			structure.setVersion(version);
		}

		structure.setNameMap(nameMap, ddmForm.getDefaultLocale());
		structure.setDescriptionMap(descriptionMap, ddmForm.getDefaultLocale());
		structure.setDefinition(_serializeJSONDDMForm(ddmForm));

		// Structure version

		DDMStructureVersion structureVersion;

		if (updateVersion) {
			latestStructureVersion.setDefinition(structure.getDefinition());
			latestStructureVersion.setStatus(
				GetterUtil.getInteger(
					serviceContext.getAttribute("status"),
					WorkflowConstants.STATUS_APPROVED));
			latestStructureVersion.setStatusByUserId(user.getUserId());
			latestStructureVersion.setStatusByUserName(user.getFullName());
			latestStructureVersion.setStatusDate(structure.getModifiedDate());

			structureVersion = _ddmStructureVersionPersistence.update(
				latestStructureVersion);
		}
		else {
			structureVersion = _addStructureVersion(
				user, structure, version, serviceContext);
		}

		// Structure layout

		// Explicitly pop UUID from service context to ensure no lingering
		// values remain there from other components (e.g. Journal)

		serviceContext.getUuid();

		_ddmStructureLayoutLocalService.addStructureLayout(
			structureVersion.getUserId(), structureVersion.getGroupId(),
			structureVersion.getStructureVersionId(), ddmFormLayout,
			serviceContext);

		if (!structureVersion.isApproved() && structureVersion.isPending()) {
			return structure;
		}

		structure = ddmStructurePersistence.update(structure);

		// Structure templates

		_syncStructureTemplatesFields(structure);

		// Data provider instance links

		_updateDataProviderInstanceLinks(
			structure.getGroupId(), structure.getStructureId(), ddmForm);

		// Indexer

		_reindexStructure(structure, serviceContext);

		return structure;
	}

	private void _validate(DDMForm ddmForm) throws PortalException {
		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		_ddmFormValidator.validate(ddmForm);
	}

	private void _validate(DDMForm parentDDMForm, DDMForm ddmForm)
		throws PortalException {

		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		Set<String> commonDDMFormFieldNames = SetUtil.intersect(
			_getDDMFormFieldsNames(parentDDMForm),
			_getDDMFormFieldsNames(ddmForm));

		if (!commonDDMFormFieldNames.isEmpty()) {
			throw new StructureDuplicateElementException(
				"Duplicate dynamic data mapping form field names: " +
					StringUtil.merge(commonDDMFormFieldNames));
		}
	}

	private void _validate(
			long groupId, long parentStructureId, long classNameId,
			String structureKey, Map<Locale, String> nameMap, DDMForm ddmForm)
		throws PortalException {

		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		structureKey = _getStructureKey(structureKey);

		DDMStructure structure = ddmStructurePersistence.fetchByG_C_S(
			groupId, classNameId, structureKey);

		if (structure != null) {
			StructureDuplicateStructureKeyException
				structureDuplicateStructureKeyException =
					new StructureDuplicateStructureKeyException();

			structureDuplicateStructureKeyException.setStructureKey(
				structure.getStructureKey());

			throw structureDuplicateStructureKeyException;
		}

		_validate(nameMap, _getParentDDMForm(parentStructureId), ddmForm);
	}

	private void _validate(
			Map<Locale, String> nameMap, DDMForm parentDDMForm, DDMForm ddmForm)
		throws PortalException {

		try {
			if (ExportImportThreadLocal.isImportInProcess()) {
				return;
			}

			_validate(nameMap, ddmForm.getDefaultLocale());
			_validate(ddmForm);

			if (parentDDMForm != null) {
				_validate(parentDDMForm, ddmForm);
			}
		}
		catch (DDMFormValidationException ddmFormValidationException) {
			throw ddmFormValidationException;
		}
		catch (LocaleException localeException) {
			throw localeException;
		}
		catch (StructureDuplicateElementException
					structureDuplicateElementException) {

			throw structureDuplicateElementException;
		}
		catch (StructureNameException structureNameException) {
			throw structureNameException;
		}
		catch (StructureDefinitionException structureDefinitionException) {
			throw structureDefinitionException;
		}
		catch (Exception exception) {
			throw new StructureDefinitionException(exception);
		}
	}

	private void _validate(
			Map<Locale, String> nameMap, Locale contentDefaultLocale)
		throws PortalException {

		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		String name = nameMap.get(contentDefaultLocale);

		if (Validator.isNull(name)) {
			throw new StructureNameException(
				"Name is null for locale " +
					contentDefaultLocale.getDisplayName());
		}

		if (!_language.isAvailableLocale(contentDefaultLocale)) {
			LocaleException localeException = new LocaleException(
				LocaleException.TYPE_CONTENT,
				StringBundler.concat(
					"The locale ", contentDefaultLocale,
					" is not available in company ",
					CompanyThreadLocal.getCompanyId()));

			localeException.setSourceAvailableLocales(
				Collections.singleton(contentDefaultLocale));
			localeException.setTargetAvailableLocales(
				_language.getAvailableLocales());

			throw localeException;
		}
	}

	private void _validateExternalReferenceCode(
		String externalReferenceCode, long structureId, long groupId,
		long classNameId) {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		DDMStructure structure = ddmStructurePersistence.fetchByERC_G_C(
			externalReferenceCode, groupId, classNameId);

		if ((structure != null) &&
			(structure.getStructureId() != structureId)) {

			throw new DuplicateDDMStructureExternalReferenceCodeException(
				StringBundler.concat(
					"Duplicate dynamic data mapping structure external ",
					"reference code \"", externalReferenceCode,
					"\" for class name ID \"", classNameId, "\" in group \"",
					groupId, "\""));
		}
	}

	private void _validateParentStructure(
			long structureId, long parentStructureId)
		throws PortalException {

		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		while (parentStructureId != 0) {
			DDMStructure parentStructure =
				ddmStructurePersistence.fetchByPrimaryKey(parentStructureId);

			if (structureId == parentStructure.getStructureId()) {
				throw new InvalidParentStructureException();
			}

			parentStructureId = parentStructure.getParentStructureId();
		}
	}

	private void _validateStructureKey(
			long structureId, long groupId, long classNameId,
			String structureKey)
		throws PortalException {

		DDMStructure structure = ddmStructurePersistence.fetchByG_C_S(
			groupId, classNameId, structureKey);

		if ((structure == null) ||
			(structure.getStructureId() == structureId)) {

			return;
		}

		StructureDuplicateStructureKeyException
			structureDuplicateStructureKeyException =
				new StructureDuplicateStructureKeyException();

		structureDuplicateStructureKeyException.setStructureKey(
			structure.getStructureKey());

		throw structureDuplicateStructureKeyException;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMStructureLocalServiceImpl.class);

	private static final Pattern _callFunctionPattern = Pattern.compile(
		"call\\(\\s*\'([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-" +
			"[0-9a-f]{12})\'\\s*,\\s*\'(.*)\'\\s*,\\s*\'(.*)\'\\s*\\)");
	private static final Snapshot<SiteConnectedGroupGroupProvider>
		_siteConnectedGroupGroupProviderSnapshot = new Snapshot<>(
			DDMStructureLocalServiceImpl.class,
			SiteConnectedGroupGroupProvider.class, null, true);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private DDM _ddm;

	@Reference
	private DDMDataDefinitionConverter _ddmDataDefinitionConverter;

	@Reference
	private DDMDataProviderInstanceLinkLocalService
		_ddmDataProviderInstanceLinkLocalService;

	@Reference
	private DDMDataProviderInstanceLinkPersistence
		_ddmDataProviderInstanceLinkPersistence;

	@Reference
	private DDMDataProviderInstanceLocalService
		_ddmDataProviderInstanceLocalService;

	@Reference
	private DDMFormValidator _ddmFormValidator;

	@Reference
	private DDMPermissionSupport _ddmPermissionSupport;

	@Reference
	private DDMStructureLayoutLocalService _ddmStructureLayoutLocalService;

	@Reference
	private DDMStructureLayoutPersistence _ddmStructureLayoutPersistence;

	@Reference
	private DDMStructureLinkPersistence _ddmStructureLinkPersistence;

	@Reference
	private DDMStructureVersionLocalService _ddmStructureVersionLocalService;

	@Reference
	private DDMStructureVersionPersistence _ddmStructureVersionPersistence;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private DDMTemplatePersistence _ddmTemplatePersistence;

	@Reference(target = "(ddm.form.deserializer.type=json)")
	private DDMFormDeserializer _jsonDDMFormDeserializer;

	@Reference(target = "(ddm.form.serializer.type=json)")
	private DDMFormSerializer _jsonDDMFormSerializer;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceLocalService _resourceLocalService;

	private ServiceTrackerMap<String, DDMStructureIndexer> _serviceTrackerMap;

	@Reference
	private UserLocalService _userLocalService;

}