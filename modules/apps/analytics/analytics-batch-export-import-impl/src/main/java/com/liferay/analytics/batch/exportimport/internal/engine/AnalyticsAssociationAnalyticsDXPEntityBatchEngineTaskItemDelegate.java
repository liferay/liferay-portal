/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.batch.exportimport.internal.engine;

import com.liferay.analytics.batch.exportimport.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.analytics.batch.exportimport.internal.engine.util.DTOConverterUtil;
import com.liferay.analytics.batch.exportimport.internal.odata.entity.AnalyticsDXPEntityEntityModel;
import com.liferay.analytics.dxp.entity.rest.dto.v1_0.DXPEntity;
import com.liferay.analytics.message.storage.model.AnalyticsAssociationTable;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.configuration.AnalyticsConfigurationRegistry;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.security.constants.AnalyticsSecurityConstants;
import com.liferay.batch.engine.BaseBatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserTable;
import com.liferay.portal.kernel.model.Users_GroupsTable;
import com.liferay.portal.kernel.model.Users_OrgsTable;
import com.liferay.portal.kernel.model.Users_RolesTable;
import com.liferay.portal.kernel.model.Users_TeamsTable;
import com.liferay.portal.kernel.model.Users_UserGroupsTable;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(
	property = "batch.engine.task.item.delegate.name=analytics-association-analytics-dxp-entities",
	service = BatchEngineTaskItemDelegate.class
)
public class AnalyticsAssociationAnalyticsDXPEntityBatchEngineTaskItemDelegate
	extends BaseBatchEngineTaskItemDelegate<DXPEntity> {

	@Override
	public EntityModel getEntityModel(Map<String, List<String>> multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public Page<DXPEntity> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		if (!_analyticsSettingsManager.syncedContactSettingsEnabled(
				contextCompany.getCompanyId())) {

			return Page.of(
				Collections.emptyList(),
				Pagination.of(pagination.getPage(), pagination.getPageSize()),
				0);
		}

		if (pagination.getPage() == 1) {
			_lastSeenUserId.set(0L);
		}

		List<User> users = _userLocalService.dslQuery(
			_createSelectDSLQuery(
				contextCompany.getCompanyId(), pagination, parameters));

		Long[] userIds = ListUtil.toArray(users, User.USER_ID_ACCESSOR);

		if (userIds.length != 0) {
			_lastSeenUserId.set(userIds[userIds.length - 1]);
		}

		Set<Serializable> contactIds = new HashSet<>();

		for (User user : users) {
			contactIds.add(user.getContactId());
		}

		Map<Serializable, Contact> contacts =
			_contactLocalService.fetchContacts(contactIds);

		Map<Long, List<Long>> groupIdsMap = _getGroupIdsMap(userIds);
		Map<Long, List<Long>> organizationIdsMap = _getOrganizationIdsMap(
			userIds);
		Map<Long, List<Long>> roleIdsMap = _getRoleIdsMap(userIds);
		Map<Long, List<Long>> teamIdsMap = _getTeamIdsMap(userIds);
		Map<Long, List<Long>> userGroupIdsMap = _getUserGroupIdsMap(userIds);

		for (User user : users) {
			Contact contact = contacts.get(user.getContactId());

			if (contact != null) {
				user.setContact(contact);
			}

			Long userId = user.getUserId();

			user.setGroupIds(
				ListUtil.toLongArray(groupIdsMap.get(userId), Long::longValue));
			user.setOrganizationIds(
				ListUtil.toLongArray(
					organizationIdsMap.get(userId), Long::longValue));
			user.setRoleIds(
				ListUtil.toLongArray(roleIdsMap.get(userId), Long::longValue));
			user.setTeamIds(
				ListUtil.toLongArray(teamIdsMap.get(userId), Long::longValue));
			user.setUserGroupIds(
				ListUtil.toLongArray(
					userGroupIdsMap.get(userId), Long::longValue));
		}

		return Page.of(
			DTOConverterUtil.toDTOs(users, _dxpEntityDTOConverter),
			Pagination.of(pagination.getPage(), pagination.getPageSize()),
			_userLocalService.dslQuery(
				_createCountDSLQuery(
					contextCompany.getCompanyId(), parameters)));
	}

	private Predicate _buildPredicate(
		AnalyticsAssociationTable analyticsAssociationTable, long companyId,
		Map<String, Serializable> parameters) {

		Predicate predicate = Predicate.and(
			analyticsAssociationTable.associationClassName.eq(
				User.class.getName()),
			analyticsAssociationTable.companyId.eq(companyId));

		Serializable resourceLastModifiedDate = parameters.get(
			"resourceLastModifiedDate");

		if (resourceLastModifiedDate == null) {
			return predicate;
		}

		return predicate.and(
			analyticsAssociationTable.modifiedDate.gt(
				(Date)resourceLastModifiedDate));
	}

	private DSLQuery _createCountDSLQuery(
		long companyId, Map<String, Serializable> parameters) {

		AnalyticsAssociationTable analyticsAssociationTableAlias =
			AnalyticsAssociationTable.INSTANCE.as("analyticsAssociationTable");
		UserTable userTableAlias = UserTable.INSTANCE.as("userTable");

		JoinStep joinStep = DSLQueryFactoryUtil.count(
		).from(
			analyticsAssociationTableAlias
		).innerJoinON(
			userTableAlias,
			userTableAlias.userId.eq(
				analyticsAssociationTableAlias.associationClassPK)
		);

		Predicate predicate = null;

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsConfigurationRegistry.getAnalyticsConfiguration(
				companyId);

		if (!analyticsConfiguration.syncAllContacts()) {
			String[] syncedOrganizationIds =
				analyticsConfiguration.syncedOrganizationIds();

			if (ArrayUtil.isNotEmpty(syncedOrganizationIds)) {
				joinStep = joinStep.leftJoinOn(
					Users_OrgsTable.INSTANCE,
					Users_OrgsTable.INSTANCE.userId.eq(userTableAlias.userId));

				predicate = Users_OrgsTable.INSTANCE.organizationId.in(
					TransformUtil.transform(
						syncedOrganizationIds, Long::parseLong, Long.class));
			}

			String[] syncedUserGroupIds =
				analyticsConfiguration.syncedUserGroupIds();

			if (ArrayUtil.isNotEmpty(syncedUserGroupIds)) {
				joinStep = joinStep.leftJoinOn(
					Users_UserGroupsTable.INSTANCE,
					Users_UserGroupsTable.INSTANCE.userId.eq(
						userTableAlias.userId));

				predicate = Predicate.or(
					predicate,
					Users_UserGroupsTable.INSTANCE.userGroupId.in(
						TransformUtil.transform(
							syncedUserGroupIds, Long::parseLong, Long.class)));
			}
		}

		return joinStep.where(
			Predicate.and(
				_buildPredicate(
					analyticsAssociationTableAlias, companyId, parameters),
				userTableAlias.screenName.neq(
					AnalyticsSecurityConstants.SCREEN_NAME_ANALYTICS_ADMIN
				).and(
					userTableAlias.status.neq(WorkflowConstants.STATUS_INACTIVE)
				).and(
					Predicate.withParentheses(predicate)
				)));
	}

	private DSLQuery _createSelectDSLQuery(
		long companyId, Pagination pagination,
		Map<String, Serializable> parameters) {

		AnalyticsAssociationTable analyticsAssociationTableAlias =
			AnalyticsAssociationTable.INSTANCE.as("analyticsAssociationTable");
		UserTable userTableAlias = UserTable.INSTANCE.as("userTable");

		JoinStep joinStep = DSLQueryFactoryUtil.select(
			userTableAlias
		).from(
			analyticsAssociationTableAlias
		).innerJoinON(
			userTableAlias,
			userTableAlias.userId.eq(
				analyticsAssociationTableAlias.associationClassPK)
		);

		Predicate predicate = null;

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsConfigurationRegistry.getAnalyticsConfiguration(
				companyId);

		if (!analyticsConfiguration.syncAllContacts()) {
			String[] syncedOrganizationIds =
				analyticsConfiguration.syncedOrganizationIds();

			if (ArrayUtil.isNotEmpty(syncedOrganizationIds)) {
				joinStep = joinStep.leftJoinOn(
					Users_OrgsTable.INSTANCE,
					Users_OrgsTable.INSTANCE.userId.eq(userTableAlias.userId));

				predicate = Users_OrgsTable.INSTANCE.organizationId.in(
					TransformUtil.transform(
						syncedOrganizationIds, Long::parseLong, Long.class));
			}

			String[] syncedUserGroupIds =
				analyticsConfiguration.syncedUserGroupIds();

			if (ArrayUtil.isNotEmpty(syncedUserGroupIds)) {
				joinStep = joinStep.leftJoinOn(
					Users_UserGroupsTable.INSTANCE,
					Users_UserGroupsTable.INSTANCE.userId.eq(
						userTableAlias.userId));

				predicate = Predicate.or(
					predicate,
					Users_UserGroupsTable.INSTANCE.userGroupId.in(
						TransformUtil.transform(
							syncedUserGroupIds, Long::parseLong, Long.class)));
			}
		}

		return joinStep.where(
			Predicate.and(
				_buildPredicate(
					analyticsAssociationTableAlias, companyId, parameters),
				userTableAlias.screenName.neq(
					AnalyticsSecurityConstants.SCREEN_NAME_ANALYTICS_ADMIN
				).and(
					userTableAlias.status.neq(WorkflowConstants.STATUS_INACTIVE)
				).and(
					Predicate.withParentheses(predicate)
				).and(
					userTableAlias.userId.gt(_lastSeenUserId.get())
				))
		).orderBy(
			orderByStep -> orderByStep.orderBy(
				userTableAlias.userId.ascending())
		).limit(
			0, pagination.getPageSize()
		);
	}

	private Map<Long, List<Long>> _getGroupIdsMap(Long[] userIds) {
		Map<Long, List<Long>> idsMap = new HashMap<>();

		if (userIds.length == 0) {
			return idsMap;
		}

		for (Object[] array :
				_userLocalService.<List<Object[]>>dslQuery(
					DSLQueryFactoryUtil.select(
						Users_GroupsTable.INSTANCE.userId,
						Users_GroupsTable.INSTANCE.groupId
					).from(
						Users_GroupsTable.INSTANCE
					).where(
						Users_GroupsTable.INSTANCE.userId.in(userIds)
					))) {

			List<Long> ids = idsMap.computeIfAbsent(
				(Long)array[0], key -> new ArrayList<>());

			ids.add((Long)array[1]);
		}

		return idsMap;
	}

	private Map<Long, List<Long>> _getOrganizationIdsMap(Long[] userIds) {
		Map<Long, List<Long>> idsMap = new HashMap<>();

		if (userIds.length == 0) {
			return idsMap;
		}

		for (Object[] array :
				_userLocalService.<List<Object[]>>dslQuery(
					DSLQueryFactoryUtil.select(
						Users_OrgsTable.INSTANCE.userId,
						Users_OrgsTable.INSTANCE.organizationId
					).from(
						Users_OrgsTable.INSTANCE
					).where(
						Users_OrgsTable.INSTANCE.userId.in(userIds)
					))) {

			List<Long> ids = idsMap.computeIfAbsent(
				(Long)array[0], key -> new ArrayList<>());

			ids.add((Long)array[1]);
		}

		return idsMap;
	}

	private Map<Long, List<Long>> _getRoleIdsMap(Long[] userIds) {
		Map<Long, List<Long>> idsMap = new HashMap<>();

		if (userIds.length == 0) {
			return idsMap;
		}

		for (Object[] array :
				_userLocalService.<List<Object[]>>dslQuery(
					DSLQueryFactoryUtil.select(
						Users_RolesTable.INSTANCE.userId,
						Users_RolesTable.INSTANCE.roleId
					).from(
						Users_RolesTable.INSTANCE
					).where(
						Users_RolesTable.INSTANCE.userId.in(userIds)
					))) {

			List<Long> ids = idsMap.computeIfAbsent(
				(Long)array[0], key -> new ArrayList<>());

			ids.add((Long)array[1]);
		}

		return idsMap;
	}

	private Map<Long, List<Long>> _getTeamIdsMap(Long[] userIds) {
		Map<Long, List<Long>> idsMap = new HashMap<>();

		if (userIds.length == 0) {
			return idsMap;
		}

		for (Object[] array :
				_userLocalService.<List<Object[]>>dslQuery(
					DSLQueryFactoryUtil.select(
						Users_TeamsTable.INSTANCE.userId,
						Users_TeamsTable.INSTANCE.teamId
					).from(
						Users_TeamsTable.INSTANCE
					).where(
						Users_TeamsTable.INSTANCE.userId.in(userIds)
					))) {

			List<Long> ids = idsMap.computeIfAbsent(
				(Long)array[0], key -> new ArrayList<>());

			ids.add((Long)array[1]);
		}

		return idsMap;
	}

	private Map<Long, List<Long>> _getUserGroupIdsMap(Long[] userIds) {
		Map<Long, List<Long>> idsMap = new HashMap<>();

		if (userIds.length == 0) {
			return idsMap;
		}

		for (Object[] array :
				_userLocalService.<List<Object[]>>dslQuery(
					DSLQueryFactoryUtil.select(
						Users_UserGroupsTable.INSTANCE.userId,
						Users_UserGroupsTable.INSTANCE.userGroupId
					).from(
						Users_UserGroupsTable.INSTANCE
					).where(
						Users_UserGroupsTable.INSTANCE.userId.in(userIds)
					))) {

			List<Long> ids = idsMap.computeIfAbsent(
				(Long)array[0], key -> new ArrayList<>());

			ids.add((Long)array[1]);
		}

		return idsMap;
	}

	private static final EntityModel _entityModel =
		new AnalyticsDXPEntityEntityModel();
	private static final ThreadLocal<Long> _lastSeenUserId =
		new CentralizedThreadLocal<>(
			AnalyticsAssociationAnalyticsDXPEntityBatchEngineTaskItemDelegate.
				class.getName() + "._lastSeenUserId");

	@Reference
	private AnalyticsConfigurationRegistry _analyticsConfigurationRegistry;

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private ContactLocalService _contactLocalService;

	@Reference(target = DTOConverterConstants.DXP_ENTITY_DTO_CONVERTER)
	private DTOConverter<BaseModel<?>, DXPEntity> _dxpEntityDTOConverter;

	@Reference
	private UserLocalService _userLocalService;

}