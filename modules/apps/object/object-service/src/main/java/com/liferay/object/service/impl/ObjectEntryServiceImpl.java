/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.object.configuration.ObjectConfiguration;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.definition.security.permission.resource.ObjectDefinitionPortletResourcePermissionRegistryUtil;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.exception.ObjectDefinitionAccountEntryRestrictedException;
import com.liferay.object.exception.ObjectEntryCountException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.base.ObjectEntryServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.time.LocalDate;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@Component(
	configurationPid = "com.liferay.object.configuration.ObjectConfiguration",
	property = {
		"json.web.service.context.name=object",
		"json.web.service.context.path=ObjectEntry"
	},
	service = AopService.class
)
public class ObjectEntryServiceImpl extends ObjectEntryServiceBaseImpl {

	@Override
	public ObjectEntry addObjectEntry(
			long groupId, long objectDefinitionId, long objectEntryFolderId,
			String defaultLanguageId, Map<String, Serializable> values,
			ServiceContext serviceContext)
		throws PortalException {

		if (!ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission()) {
			_checkAddObjectEntryPortletResourcePermission(
				groupId, objectDefinitionId, objectEntryFolderId, values);
		}

		_validateSubmissionLimit(objectDefinitionId, getUser());

		return objectEntryLocalService.addObjectEntry(
			groupId, getUserId(), objectDefinitionId, objectEntryFolderId,
			defaultLanguageId, values, serviceContext);
	}

	@Override
	public ObjectEntry addOrUpdateObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId,
			long objectEntryFolderId, Map<String, Serializable> values,
			ServiceContext serviceContext)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryPersistence.fetchByERC_G_C_ODI(
			externalReferenceCode, groupId, serviceContext.getCompanyId(),
			objectDefinitionId);

		if (objectEntry == null) {
			_checkAddObjectEntryPortletResourcePermission(
				groupId, objectDefinitionId, objectEntryFolderId, values);
		}
		else {
			checkModelResourcePermission(
				objectDefinitionId, objectEntry.getObjectEntryId(),
				ActionKeys.UPDATE);
		}

		return objectEntryLocalService.addOrUpdateObjectEntry(
			externalReferenceCode, groupId, getUserId(), objectDefinitionId,
			objectEntryFolderId, values, serviceContext);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void checkModelResourcePermission(
			long objectDefinitionId, long objectEntryId, String actionId)
		throws PortalException {

		_checkPermission(
			actionId, objectDefinitionId,
			objectEntryLocalService.getObjectEntry(objectEntryId));
	}

	@Override
	public ObjectEntry deleteObjectEntry(long objectEntryId)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryLocalService.getObjectEntry(
			objectEntryId);

		_checkPermission(
			ActionKeys.DELETE, objectEntry.getObjectDefinitionId(),
			objectEntry);

		return objectEntryLocalService.deleteObjectEntry(objectEntryId);
	}

	@Override
	public ObjectEntry expireObjectEntry(
			long objectEntryId, ServiceContext serviceContext)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryLocalService.getObjectEntry(
			objectEntryId);

		checkModelResourcePermission(
			objectEntry.getObjectDefinitionId(), objectEntry.getObjectEntryId(),
			ActionKeys.UPDATE);

		return objectEntryLocalService.expireObjectEntry(
			getUserId(), objectEntryId, serviceContext);
	}

	@Override
	public ObjectEntry fetchManyToOneObjectEntry(
			long groupId, long objectRelationshipId, long primaryKey)
		throws PortalException {

		ObjectEntry objectEntry =
			objectEntryLocalService.fetchManyToOneObjectEntry(
				groupId, objectRelationshipId, primaryKey);

		if ((objectEntry != null) &&
			!ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission()) {

			objectEntryService.checkModelResourcePermission(
				objectEntry.getObjectDefinitionId(),
				objectEntry.getObjectEntryId(), ActionKeys.VIEW);
		}

		return objectEntry;
	}

	@Override
	public ObjectEntry fetchObjectEntry(long objectEntryId)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryLocalService.fetchObjectEntry(
			objectEntryId);

		if (objectEntry != null) {
			_checkPermission(
				ActionKeys.VIEW, objectEntry.getObjectDefinitionId(),
				objectEntry);
		}

		return objectEntry;
	}

	@Override
	public ObjectEntry fetchObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryLocalService.fetchObjectEntry(
			externalReferenceCode, groupId, objectDefinitionId);

		if ((objectEntry != null) &&
			!ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission()) {

			_checkPermission(
				ActionKeys.VIEW, objectEntry.getObjectDefinitionId(),
				objectEntry);
		}

		return objectEntry;
	}

	@Override
	public List<ObjectEntry> getManyToManyObjectEntries(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, boolean reverse, String search, int start, int end)
		throws PortalException {

		List<ObjectEntry> objectEntries =
			objectEntryLocalService.getManyToManyObjectEntries(
				groupId, objectRelationshipId, primaryKey, related, reverse,
				search, start, end);

		if (!ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission()) {
			for (ObjectEntry objectEntry : objectEntries) {
				objectEntryService.checkModelResourcePermission(
					objectEntry.getObjectDefinitionId(),
					objectEntry.getObjectEntryId(), ActionKeys.VIEW);
			}
		}

		return objectEntries;
	}

	@Override
	public int getManyToManyObjectEntriesCount(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, boolean reverse, String search)
		throws PortalException {

		return objectEntryLocalService.getManyToManyObjectEntriesCount(
			groupId, objectRelationshipId, primaryKey, related, reverse,
			search);
	}

	@Override
	public ModelResourcePermission<ObjectEntry> getModelResourcePermission(
			long objectDefinitionId)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		return ModelResourcePermissionRegistryUtil.getModelResourcePermission(
			objectDefinition.getClassName());
	}

	@Override
	public ObjectEntry getObjectEntry(long objectEntryId)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryLocalService.getObjectEntry(
			objectEntryId);

		if (!ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission()) {
			_checkPermission(
				ActionKeys.VIEW, objectEntry.getObjectDefinitionId(),
				objectEntry);
		}

		return objectEntry;
	}

	@Override
	public ObjectEntry getObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryLocalService.getObjectEntry(
			externalReferenceCode, groupId, objectDefinitionId);

		if (!ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission()) {
			_checkPermission(
				ActionKeys.VIEW, objectEntry.getObjectDefinitionId(),
				objectEntry);
		}

		return objectEntry;
	}

	@Override
	public List<ObjectEntry> getOneToManyObjectEntries(
			long groupId, long objectRelationshipId, Predicate predicate,
			boolean preferApproved, long primaryKey, boolean related,
			String search, int start, int end, Sort[] sorts)
		throws PortalException {

		List<ObjectEntry> objectEntries =
			objectEntryLocalService.getOneToManyObjectEntries(
				groupId, objectRelationshipId, predicate, preferApproved,
				primaryKey, related, search, start, end, sorts);

		if (!ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission()) {
			for (ObjectEntry objectEntry : objectEntries) {
				objectEntryService.checkModelResourcePermission(
					objectEntry.getObjectDefinitionId(),
					objectEntry.getObjectEntryId(), ActionKeys.VIEW);
			}
		}

		return objectEntries;
	}

	@Override
	public int getOneToManyObjectEntriesCount(
			long groupId, long objectRelationshipId, Predicate predicate,
			long primaryKey, boolean related, String search)
		throws PortalException {

		return objectEntryLocalService.getOneToManyObjectEntriesCount(
			groupId, objectRelationshipId, predicate, primaryKey, related,
			search);
	}

	@Override
	public ObjectEntry getOrAddEmptyObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryService.fetchObjectEntry(
			externalReferenceCode, groupId, objectDefinitionId);

		if (objectEntry != null) {
			return objectEntry;
		}

		if (!ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission()) {
			_checkAddObjectEntryPortletResourcePermission(
				groupId, objectDefinitionId, 0L, new HashMap<>());
		}

		_validateSubmissionLimit(objectDefinitionId, getUser());

		return objectEntryLocalService.getOrAddEmptyObjectEntry(
			externalReferenceCode, groupId, getUserId(), objectDefinitionId);
	}

	@Override
	public boolean hasModelResourcePermission(
			long objectDefinitionId, long objectEntryId, String actionId)
		throws PortalException {

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			getModelResourcePermission(objectDefinitionId);

		return modelResourcePermission.contains(
			getPermissionChecker(), objectEntryId, actionId);
	}

	@Override
	public boolean hasModelResourcePermission(
			ObjectEntry objectEntry, String actionId)
		throws PortalException {

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			getModelResourcePermission(objectEntry.getObjectDefinitionId());

		return modelResourcePermission.contains(
			getPermissionChecker(), objectEntry, actionId);
	}

	@Override
	public boolean hasModelResourcePermission(
			User user, long objectEntryId, String actionId)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryLocalService.getObjectEntry(
			objectEntryId);

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			getModelResourcePermission(objectEntry.getObjectDefinitionId());

		return modelResourcePermission.contains(
			_permissionCheckerFactory.create(user), objectEntryId, actionId);
	}

	@Override
	public boolean hasPortletResourcePermission(
			long groupId, long objectDefinitionId, String actionId)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		PortletResourcePermission portletResourcePermission =
			ObjectDefinitionPortletResourcePermissionRegistryUtil.getService(
				objectDefinition.getResourceName());

		return portletResourcePermission.contains(
			getPermissionChecker(), groupId, actionId);
	}

	@Override
	public ObjectEntry moveObjectEntryToTrash(
			ObjectEntry objectEntry, ServiceContext serviceContext)
		throws PortalException {

		_checkPermission(
			ActionKeys.DELETE, objectEntry.getObjectDefinitionId(),
			objectEntry);

		return objectEntryLocalService.moveObjectEntryToTrash(
			getUserId(), objectEntry, serviceContext);
	}

	@Override
	public ObjectEntry partialUpdateObjectEntry(
			long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryLocalService.getObjectEntry(
			objectEntryId);

		if (!ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission()) {
			checkModelResourcePermission(
				objectEntry.getObjectDefinitionId(),
				objectEntry.getObjectEntryId(), ActionKeys.UPDATE);
		}

		return objectEntryLocalService.partialUpdateObjectEntry(
			getUserId(), objectEntryId, objectEntryFolderId, values,
			serviceContext);
	}

	@Override
	public ObjectEntry restoreObjectEntryFromTrash(
			ObjectEntry objectEntry, ServiceContext serviceContext)
		throws PortalException {

		_checkPermission(
			ActionKeys.DELETE, objectEntry.getObjectDefinitionId(),
			objectEntry);

		return objectEntryLocalService.restoreObjectEntryFromTrash(
			getUserId(), objectEntry, serviceContext);
	}

	@Override
	public void subscribeObjectEntry(long groupId, long objectEntryId)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryLocalService.getObjectEntry(
			objectEntryId);

		checkModelResourcePermission(
			objectEntry.getObjectDefinitionId(), objectEntry.getObjectEntryId(),
			ActionKeys.SUBSCRIBE);

		objectEntryLocalService.subscribeObjectEntry(
			getUserId(), groupId, objectEntryId);
	}

	@Override
	public void unsubscribeObjectEntry(long objectEntryId)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryLocalService.getObjectEntry(
			objectEntryId);

		checkModelResourcePermission(
			objectEntry.getObjectDefinitionId(), objectEntry.getObjectEntryId(),
			ActionKeys.SUBSCRIBE);

		objectEntryLocalService.unsubscribeObjectEntry(
			getUserId(), objectEntryId);
	}

	@Override
	public ObjectEntry updateObjectEntry(
			long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryLocalService.getObjectEntry(
			objectEntryId);

		if (!ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission()) {
			checkModelResourcePermission(
				objectEntry.getObjectDefinitionId(),
				objectEntry.getObjectEntryId(), ActionKeys.UPDATE);
		}

		return objectEntryLocalService.updateObjectEntry(
			getUserId(), objectEntryId, objectEntryFolderId, values,
			serviceContext);
	}

	@Override
	public void validate(
			long groupId, ObjectEntry objectEntry,
			List<String> objectValidationRuleExternalReferenceCodes,
			ServiceContext serviceContext)
		throws PortalException {

		_checkAddObjectEntryPortletResourcePermission(
			groupId, objectEntry.getObjectDefinitionId(),
			objectEntry.getObjectEntryFolderId(), objectEntry.getValues());

		objectEntryLocalService.validate(
			groupId, objectEntry, objectValidationRuleExternalReferenceCodes,
			serviceContext, getUserId());
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_objectConfiguration = ConfigurableUtil.createConfigurable(
			ObjectConfiguration.class, properties);
	}

	private void _checkAddObjectEntryPortletResourcePermission(
			long groupId, long objectDefinitionId, long objectEntryFolderId,
			Map<String, Serializable> values)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		if (FeatureFlagManagerUtil.isEnabled(
				objectDefinition.getCompanyId(), "LPD-17564") &&
			(objectEntryFolderId !=
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT)) {

			ModelResourcePermissionUtil.check(
				_objectEntryFolderModelResourcePermission, permissionChecker,
				groupId, objectEntryFolderId, ActionKeys.ADD_ENTRY);
		}

		ObjectEntry rootObjectEntry = _getRootObjectEntry(
			objectDefinition, values);

		if (rootObjectEntry != null) {
			objectDefinition = _objectDefinitionPersistence.findByPrimaryKey(
				rootObjectEntry.getObjectDefinitionId());

			ModelResourcePermission<ObjectEntry> modelResourcePermission =
				getModelResourcePermission(
					rootObjectEntry.getObjectDefinitionId());

			if (modelResourcePermission.contains(
					permissionChecker, rootObjectEntry, ActionKeys.UPDATE)) {

				return;
			}
		}

		PortletResourcePermission portletResourcePermission =
			ObjectDefinitionPortletResourcePermissionRegistryUtil.getService(
				objectDefinition.getResourceName());

		portletResourcePermission.check(
			permissionChecker, groupId, ObjectActionKeys.ADD_OBJECT_ENTRY);

		if (LazyReferencingThreadLocal.isEnabled() ||
			permissionChecker.hasPermission(
				groupId, portletResourcePermission.getResourceName(), 0,
				ObjectActionKeys.ADD_OBJECT_ENTRY)) {

			return;
		}

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition.getAccountEntryRestrictedObjectFieldId());

		long accountEntryId = MapUtil.getLong(values, objectField.getName());

		if (rootObjectEntry != null) {
			accountEntryId = MapUtil.getLong(
				rootObjectEntry.getValues(), objectField.getName());
		}

		long[] accountEntryIds = ListUtil.toLongArray(
			_accountEntryLocalService.getUserAccountEntries(
				getUserId(), AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				null,
				AccountConstants.ACCOUNT_ENTRY_TYPES_DEFAULT_ALLOWED_TYPES,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			AccountEntry::getAccountEntryId);

		if (!ArrayUtil.contains(accountEntryIds, accountEntryId)) {
			throw new ObjectDefinitionAccountEntryRestrictedException(
				StringBundler.concat(
					"User ", getUserId(),
					" does not have access to account entry ", accountEntryId));
		}

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			accountEntryId);

		if (permissionChecker.hasPermission(
				accountEntry.getAccountEntryGroupId(),
				objectDefinition.getResourceName(), 0,
				ObjectActionKeys.ADD_OBJECT_ENTRY)) {

			return;
		}

		List<AccountEntryOrganizationRel> accountEntryOrganizationRels =
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRels(accountEntryId);

		for (AccountEntryOrganizationRel accountEntryOrganizationRel :
				accountEntryOrganizationRels) {

			Organization organization =
				accountEntryOrganizationRel.getOrganization();

			if (permissionChecker.hasPermission(
					organization.getGroupId(),
					objectDefinition.getResourceName(), 0,
					ObjectActionKeys.ADD_OBJECT_ENTRY)) {

				return;
			}

			for (Organization ancestorOrganization :
					organization.getAncestors()) {

				if (permissionChecker.hasPermission(
						ancestorOrganization.getGroupId(),
						objectDefinition.getResourceName(), 0,
						ObjectActionKeys.ADD_OBJECT_ENTRY)) {

					return;
				}
			}
		}

		throw new PrincipalException.MustHavePermission(
			permissionChecker, objectDefinition.getResourceName(), 0,
			ObjectActionKeys.ADD_OBJECT_ENTRY);
	}

	private void _checkPermission(
			String actionId, long objectDefinitionId, ObjectEntry objectEntry)
		throws PortalException {

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			getModelResourcePermission(objectDefinitionId);

		if (objectEntry.isRootDescendantNode() &&
			(actionId.equals(ActionKeys.DELETE) ||
			 actionId.equals(ActionKeys.VIEW)) &&
			modelResourcePermission.contains(
				getPermissionChecker(), objectEntry, ActionKeys.UPDATE)) {

			return;
		}

		modelResourcePermission.check(
			getPermissionChecker(), objectEntry, actionId);
	}

	private ObjectEntry _getRootObjectEntry(
			ObjectDefinition objectDefinition, Map<String, Serializable> values)
		throws PortalException {

		long parentObjectEntryId = 0;

		List<ObjectRelationship> objectRelationships =
			_objectRelationshipLocalService.
				getObjectRelationshipsByObjectDefinitionId2(
					objectDefinition.getObjectDefinitionId(), true);

		for (ObjectRelationship objectRelationship : objectRelationships) {
			ObjectField objectField2 = _objectFieldLocalService.getObjectField(
				objectRelationship.getObjectFieldId2());

			parentObjectEntryId = MapUtil.getLong(
				values, objectField2.getName());

			if (parentObjectEntryId != 0) {
				break;
			}
		}

		if (parentObjectEntryId == 0) {
			return null;
		}

		ObjectEntry parentObjectEntry = objectEntryLocalService.getObjectEntry(
			parentObjectEntryId);

		return objectEntryLocalService.getObjectEntry(
			parentObjectEntry.getRootObjectEntryId());
	}

	private Date _getStartDate() {
		return Date.from(
			LocalDate.now(
			).minusDays(
				Objects.equals(_objectConfiguration.timeScale(), "days") ?
					_objectConfiguration.duration() - 1 :
						(_objectConfiguration.duration() * 7) - 1
			).atStartOfDay(
				ZoneId.systemDefault()
			).toInstant());
	}

	private void _sendUserNotificationEvents(
			long userId, String portletId, ObjectDefinition objectDefinition)
		throws PortalException {

		_userNotificationEventLocalService.sendUserNotificationEvents(
			userId, portletId, UserNotificationDeliveryConstants.TYPE_WEBSITE,
			true, false,
			JSONUtil.put(
				"className", objectDefinition.getClassName()
			).put(
				"exceedsObjectEntryLimit", true
			).put(
				"externalReferenceCode",
				objectDefinition.getExternalReferenceCode()
			).put(
				"notificationMessage",
				StringBundler.concat(
					"The limit of guest entries for ",
					objectDefinition.getLabel(
						objectDefinition.getDefaultLanguageId()),
					" has been reached and will no longer be accepted. Go to ",
					"Instance Settings to change this.")
			).put(
				"portletId", portletId
			));
	}

	private void _sendUserNotificationEvents(ObjectDefinition objectDefinition)
		throws PortalException {

		List<Long> userIds = new ArrayList<>();

		String portletId =
			objectDefinition.isUnmodifiableSystemObject() ? StringPool.BLANK :
				objectDefinition.getPortletId();
		long timestamp = LocalDate.now(
		).atStartOfDay(
			ZoneId.systemDefault()
		).toInstant(
		).getEpochSecond();

		Role role = _roleLocalService.getRole(
			objectDefinition.getCompanyId(), RoleConstants.ADMINISTRATOR);

		for (long userId : _userLocalService.getRoleUserIds(role.getRoleId())) {
			boolean addUserId = true;

			List<UserNotificationEvent> userNotificationEvents =
				_userNotificationEventLocalService.getUserNotificationEvents(
					userId, portletId, timestamp, true);

			for (UserNotificationEvent userNotificationEvent :
					userNotificationEvents) {

				JSONObject jsonObject = _jsonFactory.createJSONObject(
					userNotificationEvent.getPayload());

				if (jsonObject.has("exceedsObjectEntryLimit")) {
					addUserId = false;

					break;
				}
			}

			if (addUserId) {
				userIds.add(userId);
			}
		}

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					for (long userId : userIds) {
						_sendUserNotificationEvents(
							userId, portletId, objectDefinition);
					}

					return null;
				});
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}
	}

	private void _validateSubmissionLimit(long objectDefinitionId, User user)
		throws PortalException {

		if (!user.isGuestUser()) {
			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		try {
			_objectConfiguration =
				_configurationProvider.getCompanyConfiguration(
					ObjectConfiguration.class, objectDefinition.getCompanyId());

			if (_objectConfiguration == null) {
				_objectConfiguration =
					_configurationProvider.getSystemConfiguration(
						ObjectConfiguration.class);
			}
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}

		long count = objectEntryLocalService.getObjectEntriesCount(
			user.getUserId(), _getStartDate(),
			objectDefinition.getObjectDefinitionId());

		long maximumNumberOfGuestUserObjectEntriesPerObjectDefinition =
			_objectConfiguration.
				maximumNumberOfGuestUserObjectEntriesPerObjectDefinition();

		if (count >= maximumNumberOfGuestUserObjectEntriesPerObjectDefinition) {
			_sendUserNotificationEvents(objectDefinition);

			throw new ObjectEntryCountException(
				Collections.singletonList(
					objectDefinition.getLabel(
						objectDefinition.getDefaultLanguageId())),
				StringBundler.concat(
					"The limit of guest entries for ",
					objectDefinition.getLabel(
						objectDefinition.getDefaultLanguageId()),
					" has been reached and will no longer be accepted"),
				"the-limit-of-guest-entries-for-object-definition-has-been-" +
					"reached-and-will-no-longer-be-accepted",
				objectDefinition.getLabel(
					objectDefinition.getDefaultLanguageId()));
		}
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSONFactory _jsonFactory;

	private volatile ObjectConfiguration _objectConfiguration;

	@Reference
	private ObjectDefinitionPersistence _objectDefinitionPersistence;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectEntryFolder)"
	)
	private ModelResourcePermission<ObjectEntryFolder>
		_objectEntryFolderModelResourcePermission;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}