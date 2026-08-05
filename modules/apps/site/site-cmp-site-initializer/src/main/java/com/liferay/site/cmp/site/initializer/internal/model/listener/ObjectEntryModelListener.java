/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.model.listener;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskInstanceTokenLocalService;
import com.liferay.site.cmp.site.initializer.internal.util.CMPObjectEntryUtil;
import com.liferay.site.cmp.site.initializer.internal.util.RoleUtil;
import com.liferay.site.cmp.site.initializer.internal.util.SiteInitializerUtil;
import com.liferay.site.cms.site.initializer.util.CMSObjectEntryUtil;
import com.liferay.site.initializer.SiteInitializer;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = ModelListener.class)
public class ObjectEntryModelListener extends BaseModelListener<ObjectEntry> {

	@Override
	public void onAfterCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_initializeSite(objectEntry);
			_reindexLinkedObjectEntry(objectEntry);
			_route("CMP_ADD_ASSET", objectEntry);
			_setResourcePermissions(objectEntry);
			_updateGroup(objectEntry);
			_updateProjectCompletionRate(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_deleteProjectDepotEntry(objectEntry);
			_reindexLinkedObjectEntry(objectEntry);
			_route("CMP_REMOVE_ASSET", objectEntry);
			_updateProjectCompletionRate(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_updateGroup(objectEntry);
			_updateProjectCompletionRate(objectEntry);
			_updateProjectManagerProjectSponsorUserGroupRoles(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onBeforeRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_deleteObjectEntries(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _deleteObjectEntries(ObjectEntry objectEntry)
		throws Exception {

		if (!CMSObjectEntryUtil.isCMSObjectEntry(objectEntry)) {
			return;
		}

		for (String objectDefinitionExternalReferenceCode :
				ListUtil.fromArray("L_CMP_PROJECT_LINK", "L_CMP_TASK_LINK")) {

			for (long objectEntryId :
					CMPObjectEntryUtil.getObjectEntryIds(
						_filterFactory, _groupLocalService,
						objectDefinitionExternalReferenceCode,
						_objectDefinitionLocalService, objectEntry,
						_objectEntryLocalService)) {

				_objectEntryLocalService.deleteObjectEntry(objectEntryId);
			}
		}
	}

	private void _deleteProjectDepotEntry(ObjectEntry objectEntry)
		throws Exception {

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if ((objectDefinition == null) ||
			!StringUtil.equals(
				objectDefinition.getExternalReferenceCode(), "L_CMP_PROJECT")) {

			return;
		}

		DepotEntry depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			objectEntry.getGroupId());

		if ((depotEntry == null) ||
			(depotEntry.getType() != DepotConstants.TYPE_PROJECT)) {

			return;
		}

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	private ObjectEntry _fetchLinkedObjectEntry(
		long companyId, Map<String, Serializable> values) {

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			MapUtil.getString(values, "groupExternalReferenceCode"), companyId);

		if (group == null) {
			return null;
		}

		ObjectDefinition linkedObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				companyId, MapUtil.getString(values, "className"));

		if (linkedObjectDefinition == null) {
			return null;
		}

		return _objectEntryLocalService.fetchObjectEntry(
			MapUtil.getString(values, "classExternalReferenceCode"),
			group.getGroupId(), linkedObjectDefinition.getObjectDefinitionId());
	}

	private JSONObject _getCMPDefaultPermissionJSONObject(
		ObjectDefinition objectDefinition) {

		String[] actionIds = TransformUtil.transformToArray(
			_resourceActionLocalService.getResourceActions(
				objectDefinition.getClassName()),
			ResourceAction::getActionId, String.class);

		return JSONUtil.put(
			DepotRolesConstants.PROJECT_CONTRIBUTOR,
			_getProjectContributorActionIds(objectDefinition)
		).put(
			DepotRolesConstants.PROJECT_MANAGER, actionIds
		).put(
			DepotRolesConstants.PROJECT_MEMBER,
			new String[] {
				ActionKeys.ADD_DISCUSSION, ActionKeys.VIEW,
				ObjectActionKeys.OBJECT_ENTRY_HISTORY
			}
		).put(
			RoleConstants.CMS_ADMINISTRATOR, actionIds
		).put(
			RoleConstants.OWNER, actionIds
		);
	}

	private int _getCount(
			String filterString, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		return _objectEntryLocalService.getValuesListCount(
			new Long[] {objectEntry.getGroupId()}, 0, 0,
			objectEntry.getObjectDefinitionId(),
			_filterFactory.create(filterString, objectDefinition), false, null);
	}

	private String _getLinkedObjectEntryTitle(
			long companyId, Map<String, Serializable> values)
		throws Exception {

		ObjectEntry linkedObjectEntry = _fetchLinkedObjectEntry(
			companyId, values);

		if (linkedObjectEntry == null) {
			return null;
		}

		return linkedObjectEntry.getTitleValue();
	}

	private String[] _getProjectContributorActionIds(
		ObjectDefinition objectDefinition) {

		String externalReferenceCode =
			objectDefinition.getExternalReferenceCode();

		if (StringUtil.equals(externalReferenceCode, "L_CMP_PROJECT_LINK") ||
			StringUtil.equals(externalReferenceCode, "L_CMP_TASK_LINK")) {

			return new String[] {ActionKeys.DELETE, ActionKeys.VIEW};
		}

		if (StringUtil.equals(externalReferenceCode, "L_CMP_TASK")) {
			return new String[] {
				ActionKeys.ADD_DISCUSSION, ActionKeys.UPDATE, ActionKeys.VIEW,
				ObjectActionKeys.OBJECT_ENTRY_HISTORY
			};
		}

		return new String[] {ActionKeys.ADD_DISCUSSION, ActionKeys.VIEW};
	}

	private void _initializeSite(ObjectEntry objectEntry) {
		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-58677")) {

			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if (!StringUtil.equals(
				objectDefinition.getExternalReferenceCode(), "L_CMP_PROJECT")) {

			return;
		}

		Group group = _groupLocalService.fetchGroup(
			objectEntry.getCompanyId(), GroupConstants.CMS);

		if (group == null) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchDefaultLayoutPageTemplateEntry(
						group.getGroupId(),
						PortalUtil.getClassNameId(
							objectDefinition.getClassName()),
						0);

			if (layoutPageTemplateEntry == null) {
				SiteInitializerUtil.initialize(
					objectEntry.getCompanyId(), _siteInitializer);
			}
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to initialize the CMS site for company " +
					objectEntry.getCompanyId(),
				portalException);
		}
	}

	private void _reindexKaleoTaskInstanceTokens(ObjectEntry objectEntry)
		throws Exception {

		Indexer<KaleoTaskInstanceToken> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(
				KaleoTaskInstanceToken.class);

		for (KaleoTaskInstanceToken kaleoTaskInstanceToken :
				_kaleoTaskInstanceTokenLocalService.getKaleoTaskInstanceTokens(
					objectEntry.getModelClassName(),
					objectEntry.getObjectEntryId())) {

			indexer.reindex(kaleoTaskInstanceToken);
		}
	}

	private void _reindexLinkedObjectEntry(ObjectEntry objectEntry)
		throws Exception {

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (!StringUtil.equals(
				objectDefinition.getExternalReferenceCode(),
				"L_CMP_PROJECT_LINK") &&
			!StringUtil.equals(
				objectDefinition.getExternalReferenceCode(),
				"L_CMP_TASK_LINK")) {

			return;
		}

		ObjectEntry linkedObjectEntry = _fetchLinkedObjectEntry(
			objectEntry.getCompanyId(), objectEntry.getValues());

		if (linkedObjectEntry == null) {
			return;
		}

		Indexer<ObjectEntry> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			linkedObjectEntry.getModelClassName());

		indexer.reindex(linkedObjectEntry);

		_reindexKaleoTaskInstanceTokens(linkedObjectEntry);
	}

	private void _route(String eventType, ObjectEntry objectEntry)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-58677")) {

			return;
		}

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (!StringUtil.equals(
				objectDefinition.getExternalReferenceCode(),
				"L_CMP_TASK_LINK")) {

			return;
		}

		Map<String, Serializable> values = objectEntry.getValues();

		ObjectEntry cmpTaskObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				MapUtil.getLong(values, "r_cmpTaskToCMPTaskLinks_c_cmpTaskId"));

		if (cmpTaskObjectEntry == null) {
			return;
		}

		ObjectDefinition cmpTaskObjectDefinition =
			cmpTaskObjectEntry.getObjectDefinition();

		if (!cmpTaskObjectDefinition.isEnableObjectEntryHistory()) {
			return;
		}

		String title = _getLinkedObjectEntryTitle(
			objectEntry.getCompanyId(), values);

		if (title == null) {
			return;
		}

		_auditRouter.route(
			AuditMessageBuilder.buildAuditMessage(
				cmpTaskObjectEntry.getModelClassName(),
				cmpTaskObjectEntry.getObjectEntryId(), eventType,
				Collections.singletonList(new Attribute(title))));
	}

	private void _setResourcePermissions(ObjectEntry objectEntry)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if (!StringUtil.equals(
				objectDefinition.getObjectFolderExternalReferenceCode(),
				"L_CMP_PROJECT_MANAGEMENT_DEFINITIONS")) {

			return;
		}

		JSONObject defaultPermissionsJSONObject =
			_getCMPDefaultPermissionJSONObject(objectDefinition);

		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			objectEntry.getModelClassName());

		for (Role role :
				TransformUtil.transformToList(
					ArrayUtil.append(
						DepotRolesConstants.PROJECT_ROLE_NAMES,
						RoleConstants.CMS_ADMINISTRATOR),
					roleName -> _roleLocalService.fetchRole(
						objectEntry.getCompanyId(), roleName))) {

			String[] actionIds = (String[])defaultPermissionsJSONObject.get(
				role.getName());

			if (actionIds == null) {
				actionIds = new String[0];
			}

			_resourcePermissionLocalService.setResourcePermissions(
				objectEntry.getCompanyId(), objectEntry.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry.getObjectEntryId()),
				role.getRoleId(),
				ArrayUtil.filter(actionIds, resourceActions::contains));
		}
	}

	private void _updateGroup(ObjectEntry objectEntry) {
		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-58677")) {

			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if (!StringUtil.equals(
				objectDefinition.getExternalReferenceCode(), "L_CMP_PROJECT")) {

			return;
		}

		String title = MapUtil.getString(objectEntry.getValues(), "title");

		if (Validator.isNull(title)) {
			return;
		}

		Group group = _groupLocalService.fetchGroup(objectEntry.getGroupId());

		if (group == null) {
			return;
		}

		Locale defaultLocale = LocaleUtil.fromLanguageId(
			group.getDefaultLanguageId());

		if (StringUtil.equals(group.getName(defaultLocale), title)) {
			return;
		}

		group.setName(title, defaultLocale);

		_groupLocalService.updateGroup(group);
	}

	private void _updateProjectCompletionRate(ObjectEntry objectEntry)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if (!StringUtil.equals(
				objectDefinition.getExternalReferenceCode(), "L_CMP_TASK")) {

			return;
		}

		ObjectEntry parentObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				MapUtil.getLong(
					objectEntry.getValues(),
					"r_cmpProjectToCMPTasks_c_cmpProjectId"));

		if (parentObjectEntry == null) {
			return;
		}

		int totalCount = _getCount(null, objectDefinition, objectEntry);

		int completionRate = 0;

		if (totalCount != 0) {
			int filteredCount = _getCount(
				"state eq 'done'", objectDefinition, objectEntry);

			completionRate = (filteredCount * 100) / totalCount;
		}

		if (Objects.equals(
				MapUtil.getInteger(
					parentObjectEntry.getValues(), "completionRate"),
				completionRate)) {

			return;
		}

		_objectEntryLocalService.partialUpdateObjectEntry(
			parentObjectEntry.getUserId(), parentObjectEntry.getObjectEntryId(),
			parentObjectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"completionRate", completionRate
			).build(),
			new ServiceContext());
	}

	private void _updateProjectManagerProjectSponsorUserGroupRoles(
			ObjectEntry objectEntry)
		throws Exception {

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (!StringUtil.equals(
				objectDefinition.getExternalReferenceCode(), "L_CMP_PROJECT")) {

			return;
		}

		_updateUserGroupRoles(
			objectEntry.getGroupId(),
			Collections.singletonList(DepotRolesConstants.PROJECT_MANAGER),
			MapUtil.getLong(
				objectEntry.getValues(), "r_userToCMPProjectManager_userId",
				0));
		_updateUserGroupRoles(
			objectEntry.getGroupId(),
			Collections.singletonList(DepotRolesConstants.PROJECT_MEMBER),
			MapUtil.getLong(
				objectEntry.getValues(), "r_userToCMPProjectSponsor_userId",
				0));
	}

	private User _updateUser(long[] groupIds, Long userId) throws Exception {
		User user = _userService.getUserById(userId);

		Contact contact = user.getContact();

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		calendar.setTime(user.getBirthday());

		return _userService.updateUser(
			user.getUserId(), user.getPassword(), null, null,
			user.isPasswordReset(), null, null, user.getScreenName(),
			user.getEmailAddress(), user.getLanguageId(), user.getTimeZoneId(),
			user.getGreeting(), user.getComments(), user.getFirstName(),
			user.getMiddleName(), user.getLastName(),
			contact.getPrefixListTypeId(), contact.getSuffixListTypeId(),
			user.isMale(), calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR),
			contact.getSmsSn(), contact.getFacebookSn(), contact.getJabberSn(),
			contact.getSkypeSn(), contact.getTwitterSn(), user.getJobTitle(),
			groupIds, user.getOrganizationIds(), null, null,
			user.getUserGroupIds(), new ServiceContext());
	}

	private void _updateUserGroupRoles(
			long groupId, List<String> roleNames, long userId)
		throws Exception {

		if (userId == 0) {
			return;
		}

		User user = _userService.getUserById(userId);

		user = _updateUser(
			ArrayUtil.append(user.getGroupIds(), groupId), userId);

		long companyId = user.getCompanyId();

		_userGroupRoleService.addUserGroupRoles(
			user.getUserId(), groupId,
			TransformUtil.transformToLongArray(
				roleNames,
				roleName -> {
					Role role = RoleUtil.getOrAddProjectRole(
						companyId, roleName, userId);

					return role.getRoleId();
				}));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private KaleoTaskInstanceTokenLocalService
		_kaleoTaskInstanceTokenLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference(
		target = "(site.initializer.key=com.liferay.site.initializer.cmp)"
	)
	private SiteInitializer _siteInitializer;

	@Reference
	private UserGroupRoleService _userGroupRoleService;

	@Reference
	private UserService _userService;

}