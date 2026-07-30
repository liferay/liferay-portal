/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.internal.search.util.DDMSearchUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureTable;
import com.liferay.dynamic.data.mapping.security.permission.DDMPermissionSupport;
import com.liferay.dynamic.data.mapping.service.base.DDMStructureServiceBaseImpl;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the remote service for accessing, adding, deleting, and updating
 * dynamic data mapping (DDM) structures. Its methods include permission checks.
 *
 * @author Brian Wing Shun Chan
 * @author Bruno Basto
 * @author Marcellus Tavares
 * @see    DDMStructureLocalServiceImpl
 */
@Component(
	property = {
		"json.web.service.context.name=ddm",
		"json.web.service.context.path=DDMStructure"
	},
	service = AopService.class
)
public class DDMStructureServiceImpl extends DDMStructureServiceBaseImpl {

	@Override
	public DDMStructure addStructure(
			long groupId, long parentStructureId, long classNameId,
			String structureKey, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, DDMForm ddmForm,
			DDMFormLayout ddmFormLayout, String storageType, int type,
			ServiceContext serviceContext)
		throws PortalException {

		_ddmPermissionSupport.checkAddStructurePermission(
			getPermissionChecker(), groupId, classNameId);

		return ddmStructureLocalService.addStructure(
			null, getUserId(), groupId, parentStructureId, classNameId,
			structureKey, nameMap, descriptionMap, ddmForm, ddmFormLayout,
			storageType, type, serviceContext);
	}

	@Override
	public DDMStructure addStructure(
			long groupId, long classNameId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, DDMForm ddmForm,
			DDMFormLayout ddmFormLayout, String storageType,
			ServiceContext serviceContext)
		throws PortalException {

		_ddmPermissionSupport.checkAddStructurePermission(
			getPermissionChecker(), groupId, classNameId);

		return ddmStructureLocalService.addStructure(
			getUserId(), groupId, classNameId, nameMap, descriptionMap, ddmForm,
			ddmFormLayout, storageType, serviceContext);
	}

	@Override
	public DDMStructure addStructure(
			long groupId, String parentStructureKey, long classNameId,
			String structureKey, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, DDMForm ddmForm,
			DDMFormLayout ddmFormLayout, String storageType, int type,
			ServiceContext serviceContext)
		throws PortalException {

		_ddmPermissionSupport.checkAddStructurePermission(
			getPermissionChecker(), groupId, classNameId);

		return ddmStructureLocalService.addStructure(
			getUserId(), groupId, parentStructureKey, classNameId, structureKey,
			nameMap, descriptionMap, ddmForm, ddmFormLayout, storageType, type,
			serviceContext);
	}

	/**
	 * Copies a structure, creating a new structure with all the values
	 * extracted from the original one. The new structure supports a new name
	 * and description.
	 *
	 * @param  sourceStructureId the primary key of the structure to be copied
	 * @param  nameMap the new structure's locales and localized names
	 * @param  descriptionMap the new structure's locales and localized
	 *         descriptions
	 * @param  serviceContext the service context to be applied. Can set the
	 *         UUID, creation date, modification date, guest permissions, and
	 *         group permissions for the structure.
	 * @return the new structure
	 */
	@Override
	public DDMStructure copyStructure(
			long sourceStructureId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, ServiceContext serviceContext)
		throws PortalException {

		DDMStructure sourceStructure = ddmStructurePersistence.findByPrimaryKey(
			sourceStructureId);

		_ddmStructureModelResourcePermission.check(
			getPermissionChecker(), sourceStructure, ActionKeys.VIEW);

		_ddmPermissionSupport.checkAddStructurePermission(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			sourceStructure.getClassNameId());

		return ddmStructureLocalService.copyStructure(
			getUserId(), sourceStructureId, nameMap, descriptionMap,
			serviceContext);
	}

	@Override
	public DDMStructure copyStructure(
			long sourceStructureId, ServiceContext serviceContext)
		throws PortalException {

		DDMStructure sourceStructure = ddmStructurePersistence.findByPrimaryKey(
			sourceStructureId);

		_ddmStructureModelResourcePermission.check(
			getPermissionChecker(), sourceStructure, ActionKeys.VIEW);

		_ddmPermissionSupport.checkAddStructurePermission(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			sourceStructure.getClassNameId());

		return ddmStructureLocalService.copyStructure(
			getUserId(), sourceStructureId, serviceContext);
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
		_ddmStructureModelResourcePermission.check(
			getPermissionChecker(), structureId, ActionKeys.DELETE);

		ddmStructureLocalService.deleteStructure(structureId);
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
			long groupId, long classNameId, String structureKey)
		throws PortalException {

		DDMStructure ddmStructure = ddmStructurePersistence.fetchByG_C_S(
			groupId, classNameId, structureKey);

		if (ddmStructure != null) {
			_ddmStructureModelResourcePermission.check(
				getPermissionChecker(), ddmStructure, ActionKeys.VIEW);
		}

		return ddmStructure;
	}

	@Override
	public DDMStructure fetchStructure(
			long groupId, long classNameId, String structureKey,
			boolean includeAncestorStructures)
		throws PortalException {

		DDMStructure ddmStructure = ddmStructureLocalService.fetchStructure(
			groupId, classNameId, structureKey, includeAncestorStructures);

		if (ddmStructure != null) {
			_ddmStructureModelResourcePermission.check(
				getPermissionChecker(), ddmStructure, ActionKeys.VIEW);
		}

		return ddmStructure;
	}

	@Override
	public DDMStructure fetchStructureByExternalReferenceCode(
			String externalReferenceCode, long groupId, long classNameId)
		throws PortalException {

		DDMStructure ddmStructure = ddmStructurePersistence.fetchByERC_G_C(
			externalReferenceCode, groupId, classNameId);

		if (ddmStructure != null) {
			_ddmStructureModelResourcePermission.check(
				getPermissionChecker(), ddmStructure, ActionKeys.VIEW);
		}

		return ddmStructure;
	}

	/**
	 * Returns the structure with the ID.
	 *
	 * @param  structureId the primary key of the structure
	 * @return the structure with the ID
	 */
	@Override
	public DDMStructure getStructure(long structureId) throws PortalException {
		_ddmStructureModelResourcePermission.check(
			getPermissionChecker(), structureId, ActionKeys.VIEW);

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

		DDMStructure structure = ddmStructureLocalService.getStructure(
			groupId, classNameId, structureKey);

		_ddmStructureModelResourcePermission.check(
			getPermissionChecker(), structure, ActionKeys.VIEW);

		return structure;
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
	 *         search
	 * @return the matching structure
	 */
	@Override
	public DDMStructure getStructure(
			long groupId, long classNameId, String structureKey,
			boolean includeAncestorStructures)
		throws PortalException {

		DDMStructure structure = ddmStructureLocalService.getStructure(
			groupId, classNameId, structureKey, includeAncestorStructures);

		_ddmStructureModelResourcePermission.check(
			getPermissionChecker(), structure, ActionKeys.VIEW);

		return structure;
	}

	@Override
	public DDMStructure getStructureByExternalReferenceCode(
			String externalReferenceCode, long groupId, long classNameId)
		throws PortalException {

		DDMStructure structure = ddmStructurePersistence.findByERC_G_C(
			externalReferenceCode, groupId, classNameId);

		_ddmStructureModelResourcePermission.check(
			getPermissionChecker(), structure, ActionKeys.VIEW);

		return structure;
	}

	@Override
	public List<DDMStructure> getStructures(
		long companyId, long[] groupIds, long classNameId, int status) {

		try {
			SearchContext searchContext =
				DDMSearchUtil.buildStructureSearchContext(
					_ddmPermissionSupport, companyId, groupIds, getUserId(),
					classNameId, null, StringPool.BLANK, StringPool.BLANK,
					StringPool.BLANK, null, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

			return DDMSearchUtil.doSearch(
				searchContext, DDMStructure.class,
				ddmStructurePersistence::findByPrimaryKey);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		return Collections.emptyList();
	}

	@Override
	public List<DDMStructure> getStructures(
		long companyId, long[] groupIds, long classNameId, int status,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		try {
			SearchContext searchContext =
				DDMSearchUtil.buildStructureSearchContext(
					_ddmPermissionSupport, companyId, groupIds, getUserId(),
					classNameId, null, StringPool.BLANK, StringPool.BLANK,
					StringPool.BLANK, null, status, start, end,
					orderByComparator);

			return DDMSearchUtil.doSearch(
				searchContext, DDMStructure.class,
				ddmStructurePersistence::findByPrimaryKey);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		return Collections.emptyList();
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
	public int getStructuresCount(
		long companyId, long[] groupIds, long classNameId) {

		return getStructuresCount(
			companyId, groupIds, classNameId, StringPool.BLANK,
			WorkflowConstants.STATUS_ANY);
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

	@Override
	public void revertStructure(
			long structureId, String version, ServiceContext serviceContext)
		throws PortalException {

		_ddmStructureModelResourcePermission.check(
			getPermissionChecker(), structureId, ActionKeys.UPDATE);

		ddmStructureLocalService.revertStructure(
			getUserId(), structureId, version, serviceContext);
	}

	@Override
	public List<DDMStructure> search(
			long companyId, long[] groupIds, long classNameId, long classPK,
			String keywords, int status, int start, int end,
			OrderByComparator<DDMStructure> orderByComparator)
		throws PortalException {

		try {
			SearchContext searchContext =
				DDMSearchUtil.buildStructureSearchContext(
					_ddmPermissionSupport, companyId, groupIds, getUserId(),
					classNameId, classPK, keywords, keywords, StringPool.BLANK,
					null, status, start, end, orderByComparator);

			return DDMSearchUtil.doSearch(
				searchContext, DDMStructure.class,
				ddmStructureLocalService::fetchStructure);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		return Collections.emptyList();
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
	 * @param  type the structure's type. For more information, see {@link
	 *         com.liferay.dynamic.data.mapping.constants.DDMStructureConstants}.
	 * @param  status the workflow's status.
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
		int type, int status, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		try {
			SearchContext searchContext =
				DDMSearchUtil.buildStructureSearchContext(
					_ddmPermissionSupport, companyId, groupIds, getUserId(),
					classNameId, null, keywords, keywords, StringPool.BLANK,
					type, status, start, end, orderByComparator);

			return DDMSearchUtil.doSearch(
				searchContext, DDMStructure.class,
				ddmStructurePersistence::findByPrimaryKey);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		return Collections.emptyList();
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
	 * @param  status the workflow's status.
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

		try {
			SearchContext searchContext =
				DDMSearchUtil.buildStructureSearchContext(
					_ddmPermissionSupport, companyId, groupIds, getUserId(),
					classNameId, null, keywords, keywords, StringPool.BLANK,
					null, status, start, end, orderByComparator);

			return DDMSearchUtil.doSearch(
				searchContext, DDMStructure.class,
				ddmStructureLocalService::fetchStructure);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		return Collections.emptyList();
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
	 *         "expando". For more information, see {@link
	 *         com.liferay.dynamic.data.mapping.storage.StorageType}.
	 * @param  type the structure's type. For more information, see {@link
	 *         com.liferay.dynamic.data.mapping.constants.DDMStructureConstants}.
	 * @param  status the workflow's status.
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

		try {
			SearchContext searchContext =
				DDMSearchUtil.buildStructureSearchContext(
					_ddmPermissionSupport, companyId, groupIds, getUserId(),
					classNameId, null, name, description, storageType, type,
					status, start, end, orderByComparator);

			return DDMSearchUtil.doSearch(
				searchContext, DDMStructure.class,
				ddmStructurePersistence::findByPrimaryKey);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		return Collections.emptyList();
	}

	@Override
	public int searchCount(
			long companyId, long[] groupIds, long classNameId, long classPK,
			String keywords, int status)
		throws PortalException {

		try {
			SearchContext searchContext =
				DDMSearchUtil.buildStructureSearchContext(
					_ddmPermissionSupport, companyId, groupIds, getUserId(),
					classNameId, classPK, keywords, keywords, StringPool.BLANK,
					null, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			return DDMSearchUtil.doSearchCount(
				searchContext, DDMStructure.class);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		return 0;
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
	 * @param  status the workflow's status.
	 * @return the number of matching structures
	 */
	@Override
	public int searchCount(
		long companyId, long[] groupIds, long classNameId, String keywords,
		int status) {

		try {
			SearchContext searchContext =
				DDMSearchUtil.buildStructureSearchContext(
					_ddmPermissionSupport, companyId, groupIds, getUserId(),
					classNameId, null, keywords, keywords, StringPool.BLANK,
					null, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			return DDMSearchUtil.doSearchCount(
				searchContext, DDMStructure.class);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		return 0;
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
	 * @param  type the structure's type. For more information, see {@link
	 *         com.liferay.dynamic.data.mapping.constants.DDMStructureConstants}.
	 * @param  status the workflow's status.
	 * @return the number of matching structures
	 */
	@Override
	public int searchCount(
		long companyId, long[] groupIds, long classNameId, String keywords,
		int type, int status) {

		try {
			SearchContext searchContext =
				DDMSearchUtil.buildStructureSearchContext(
					_ddmPermissionSupport, companyId, groupIds, getUserId(),
					classNameId, null, keywords, keywords, StringPool.BLANK,
					type, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			return DDMSearchUtil.doSearchCount(
				searchContext, DDMStructure.class);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		return 0;
	}

	/**
	 * Returns the number of structures matching the groups, class name IDs,
	 * name keyword, description keyword, storage type, and type
	 *
	 * @param  companyId the primary key of the structure's company
	 * @param  groupIds the primary keys of the groups
	 * @param  classNameId the primary key of the class name of the model the
	 *         structure is related to
	 * @param  name the name keywords
	 * @param  description the description keywords
	 * @param  storageType the structure's storage type. It can be "xml" or
	 *         "expando". For more information, see {@link
	 *         com.liferay.dynamic.data.mapping.storage.StorageType}.
	 * @param  type the structure's type. For more information, see {@link
	 *         com.liferay.dynamic.data.mapping.constants.DDMStructureConstants}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field
	 * @return the number of matching structures
	 */
	@Override
	public int searchCount(
		long companyId, long[] groupIds, long classNameId, String name,
		String description, String storageType, int type, int status,
		boolean andOperator) {

		try {
			SearchContext searchContext =
				DDMSearchUtil.buildStructureSearchContext(
					_ddmPermissionSupport, companyId, groupIds, getUserId(),
					classNameId, null, name, description, storageType, type,
					status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			return DDMSearchUtil.doSearchCount(
				searchContext, DDMStructure.class);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		return 0;
	}

	@Override
	public DDMStructure updateStructure(
			long groupId, long parentStructureId, long classNameId,
			String structureKey, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, DDMForm ddmForm,
			DDMFormLayout ddmFormLayout, ServiceContext serviceContext)
		throws PortalException {

		DDMStructure structure = ddmStructurePersistence.findByG_C_S(
			groupId, classNameId, structureKey);

		_ddmStructureModelResourcePermission.check(
			getPermissionChecker(), structure, ActionKeys.UPDATE);

		return ddmStructureLocalService.updateStructure(
			getUserId(), groupId, parentStructureId, classNameId, structureKey,
			nameMap, descriptionMap, ddmForm, ddmFormLayout, serviceContext);
	}

	@Override
	public DDMStructure updateStructure(
			long structureId, long parentStructureId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			DDMForm ddmForm, DDMFormLayout ddmFormLayout,
			ServiceContext serviceContext)
		throws PortalException {

		_ddmStructureModelResourcePermission.check(
			getPermissionChecker(), structureId, ActionKeys.UPDATE);

		return ddmStructureLocalService.updateStructure(
			getUserId(), structureId, parentStructureId, nameMap,
			descriptionMap, ddmForm, ddmFormLayout, serviceContext);
	}

	private Predicate _getPredicate(
		long companyId, long[] groupIds, long classNameId, String keywords) {

		Predicate predicate = DDMStructureTable.INSTANCE.companyId.eq(
			companyId
		).and(
			DDMStructureTable.INSTANCE.classNameId.eq(classNameId)
		).and(
			DDMStructureTable.INSTANCE.type.eq(0)
		).and(
			_inlineSQLHelper.getPermissionWherePredicate(
				_ddmPermissionSupport.getStructureModelResourceName(
					classNameId),
				DDMStructureTable.INSTANCE.structureId, groupIds)
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

	private static final Log _log = LogFactoryUtil.getLog(
		DDMStructureServiceImpl.class);

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private DDMPermissionSupport _ddmPermissionSupport;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMStructure)"
	)
	private ModelResourcePermission<DDMStructure>
		_ddmStructureModelResourcePermission;

	@Reference
	private InlineSQLHelper _inlineSQLHelper;

}