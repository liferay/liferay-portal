/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryPersistence;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.exception.RemoteExportException;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.kernel.staging.StagingURLHelperUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.exportimport.kernel.staging.constants.StagingConstants;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.DataLimitExceededException;
import com.liferay.portal.kernel.exception.DuplicateGroupException;
import com.liferay.portal.kernel.exception.GroupFriendlyURLException;
import com.liferay.portal.kernel.exception.GroupInheritContentException;
import com.liferay.portal.kernel.exception.GroupKeyException;
import com.liferay.portal.kernel.exception.GroupParentException;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.NoSuchCompanyException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchLayoutSetException;
import com.liferay.portal.kernel.exception.PendingBackgroundTaskException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RemoteOptionsException;
import com.liferay.portal.kernel.exception.RequiredGroupException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.UserPersonalSite;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelperUtil;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.reindexer.ReindexerBridge;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.RemoteAuthException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.permission.RolePermissions;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.MembershipRequestLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.persistence.CompanyPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutPersistence;
import com.liferay.portal.kernel.service.persistence.OrganizationPersistence;
import com.liferay.portal.kernel.service.persistence.ResourcePermissionPersistence;
import com.liferay.portal.kernel.service.persistence.RolePersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.tree.TreeModelTasksAdapter;
import com.liferay.portal.kernel.tree.TreePathUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.comparator.GroupIdComparator;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;
import com.liferay.portal.model.impl.GroupModelImpl;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.security.permission.PermissionCacheUtil;
import com.liferay.portal.service.base.GroupLocalServiceBaseImpl;
import com.liferay.portal.service.http.ClassNameServiceHttp;
import com.liferay.portal.service.http.GroupServiceHttp;
import com.liferay.portal.theme.ThemeLoader;
import com.liferay.portal.theme.ThemeLoaderFactory;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.site.initializer.kernel.util.SiteInitializerThreadLocal;
import com.liferay.social.kernel.service.SocialActivityLocalService;
import com.liferay.social.kernel.service.SocialActivitySettingLocalService;
import com.liferay.social.kernel.service.SocialRequestLocalService;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.io.File;
import java.io.Serializable;

import java.net.ConnectException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Provides the local service for accessing, adding, deleting, and updating
 * groups. Groups are mostly used in Liferay as a resource container for
 * permissioning and content scoping purposes.
 *
 * <p>
 * Groups are also the entity to which LayoutSets are generally associated.
 * Since LayoutSets are the parent entities of Layouts (i.e. pages), no entity
 * can have associated pages without also having an associated Group. This
 * relationship can be depicted as ... Layout -> LayoutSet -> Group[type] ->
 * [Entity]. Note, the Entity part is optional.
 * </p>
 *
 * <p>
 * Group has a "type" definition that is typically identified by two fields of
 * the entity - <code>String className</code>, and <code>int type</code>.
 * </p>
 *
 * <p>
 * The <code>className</code> field helps create the group's association with
 * other entities (e.g. Organization, User, Company, UserGroup, etc.). The value
 * of <code>className</code> is the full name of the entity's class and the
 * primary key of the associated entity instance. A site has
 * <code>className="Group"</code> and has no associated entity.
 * </p>
 *
 * <p>
 * The <code>type</code> field helps distinguish between a group used strictly
 * for scoping and a group that also has pages (in which case the type is
 * <code>SITE</code>). For a list of types, see {@link GroupConstants}.
 * </p>
 *
 * <p>
 * Here is a listing of how Group is related to some portal entities:
 * </p>
 *
 * <ul>
 * <li>
 * Site is a Group with <code>className="Group"</code>
 * </li>
 * <li>
 * Company has one Group (this is the global scope, but never has pages)
 * </li>
 * <li>
 * User has one Group (pages are optional based on the behavior configuration
 * for
 * personal pages)
 * </li>
 * <li>
 * Layout Template (<code>LayoutPrototype</code>) has 1 Group which uses only
 * one
 * of its two LayoutSets to store a single page which can later be used to
 * derive a single page in any Site
 * </li>
 * <li>
 * Site Template (<code>LayoutSetPrototype</code>) has 1 Group which uses only
 * one of its two LayoutSets to store many pages which can later be used to
 * derive
 * entire Sites or pulled into an existing Site
 * </li>
 * <li>
 * Organization has one Group, but can also be associated to a Site at any point
 * in its life cycle in order to support having pages
 * </li>
 * <li>
 * UserGroup has one Group that can have pages in both of the group's LayoutSets
 * which are later inherited by users assigned to the UserGroup
 * </li>
 * </ul>
 *
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 * @author Bruno Farache
 * @author Wesley Gong
 * @see    com.liferay.portal.model.impl.GroupImpl
 */
public class GroupLocalServiceImpl extends GroupLocalServiceBaseImpl {

	public static final String ORGANIZATION_NAME_SUFFIX = " LFR_ORGANIZATION";

	public static final String ORGANIZATION_STAGING_SUFFIX = " (Staging)";

	/**
	 * Constructs a group local service.
	 */
	public GroupLocalServiceImpl() {
		initImportLARFile();
	}

	@Override
	public Group addGroup(
			long userId, long parentGroupId, String className, long classPK,
			long liveGroupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean inheritContent,
			boolean active, ServiceContext serviceContext)
		throws PortalException {

		// Group

		User user = _userPersistence.findByPrimaryKey(userId);

		if (site && (PropsValues.DATA_LIMIT_SITE_MAX_COUNT > 0) &&
			(groupPersistence.countByC_S(user.getCompanyId(), site) >=
				PropsValues.DATA_LIMIT_SITE_MAX_COUNT)) {

			throw new DataLimitExceededException(
				"Unable to exceed maximum number of allowed sites");
		}

		className = GetterUtil.getString(className);

		long classNameId = _classNameLocalService.getClassNameId(className);

		String groupKey = StringPool.BLANK;
		String friendlyName = StringPool.BLANK;

		if (nameMap != null) {
			nameMap = _normalizeNameMap(nameMap);

			groupKey = nameMap.get(LocaleUtil.getDefault());
			friendlyName = nameMap.get(LocaleUtil.getDefault());

			if (Validator.isNull(groupKey)) {
				Locale userLocale = user.getLocale();

				if (userLocale != null) {
					groupKey = nameMap.get(userLocale);
					friendlyName = nameMap.get(userLocale);
				}
			}

			if (Validator.isNull(groupKey)) {
				Locale mostRelevantLocale = LocaleUtil.getMostRelevantLocale();

				if (mostRelevantLocale != null) {
					groupKey = nameMap.get(mostRelevantLocale);
					friendlyName = nameMap.get(mostRelevantLocale);
				}
			}

			if (Validator.isNull(groupKey)) {
				groupKey = nameMap.get(LocaleUtil.US);
				friendlyName = nameMap.get(LocaleUtil.US);
			}
		}

		long groupId = 0;

		while (true) {
			groupId = counterLocalService.increment();

			User screenNameUser = _userPersistence.fetchByC_SN(
				user.getCompanyId(), String.valueOf(groupId));

			if (screenNameUser == null) {
				break;
			}
		}

		boolean staging = isStaging(serviceContext);

		long groupClassNameId = _classNameLocalService.getClassNameId(
			Group.class);

		if ((classNameId <= 0) || className.equals(Group.class.getName()) ||
			(className.equals(Company.class.getName()) && staging)) {

			className = Group.class.getName();
			classNameId = groupClassNameId;
			classPK = groupId;
		}
		else if (className.equals(Organization.class.getName())) {
			groupKey = getOrgGroupName(groupKey);
		}
		else if ((type != GroupConstants.TYPE_DEPOT) &&
				 !GroupConstants.USER_PERSONAL_SITE.equals(groupKey)) {

			groupKey = String.valueOf(classPK);
		}

		if (className.equals(Organization.class.getName()) && staging) {
			classPK = liveGroupId;
		}

		if (className.equals(Layout.class.getName())) {
			Layout layout = _layoutLocalService.getLayout(classPK);

			parentGroupId = layout.getGroupId();
		}

		friendlyURL = getFriendlyURL(
			user.getCompanyId(), groupId, classNameId, classPK, friendlyName,
			friendlyURL);

		if (staging) {
			int groupKeyMaxLength = ModelHintsUtil.getMaxLength(
				Group.class.getName(), "groupKey");
			String stagingGroupKeySuffix = "-staging";

			if (groupKey.length() <=
					(groupKeyMaxLength - stagingGroupKeySuffix.length())) {

				groupKey = groupKey.concat(stagingGroupKeySuffix);
			}
			else {
				int counter = 1;

				groupKey = _getGroupKey(
					counter, groupKey, groupKeyMaxLength,
					stagingGroupKeySuffix);

				while (fetchGroup(user.getCompanyId(), groupKey) != null) {
					counter++;

					groupKey = _getGroupKey(
						counter, groupKey, groupKeyMaxLength,
						stagingGroupKeySuffix);
				}
			}

			for (Map.Entry<Locale, String> entry : nameMap.entrySet()) {
				String name = entry.getValue();

				if (Validator.isNull(name)) {
					continue;
				}

				nameMap.put(
					entry.getKey(), name.concat(ORGANIZATION_STAGING_SUFFIX));
			}

			friendlyURL = getFriendlyURL(friendlyURL.concat("-staging"));

			friendlyURL = getValidatedFriendlyURL(
				user.getCompanyId(), groupId, classNameId, classPK,
				friendlyURL);
		}

		if (parentGroupId == GroupConstants.DEFAULT_PARENT_GROUP_ID) {
			membershipRestriction =
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION;
		}

		if (className.equals(Group.class.getName())) {
			if (!site && (liveGroupId == 0) &&
				!(StringUtil.startsWith(groupKey, GroupConstants.APP) ||
				  groupKey.equals(GroupConstants.CALENDAR) ||
				  groupKey.equals(GroupConstants.CMS) ||
				  groupKey.equals(GroupConstants.CONTROL_PANEL) ||
				  groupKey.equals(GroupConstants.FORMS))) {

				throw new IllegalArgumentException();
			}
		}
		else if (!className.equals(Company.class.getName()) &&
				 !className.equals(Organization.class.getName()) &&
				 className.startsWith("com.liferay.portal.kernel.model.")) {

			if (site) {
				throw new IllegalArgumentException();
			}
		}

		if ((classNameId <= 0) || (type == GroupConstants.TYPE_DEPOT) ||
			className.equals(Group.class.getName())) {

			validateGroupKey(
				groupId, user.getCompanyId(), groupKey, type, site);
		}

		validateInheritContent(parentGroupId, inheritContent);

		friendlyURL = getValidatedFriendlyURL(
			user.getCompanyId(), groupId, classNameId, classPK, friendlyURL);

		validateParentGroup(groupId, parentGroupId);

		Group group = groupPersistence.create(groupId);

		if (serviceContext != null) {
			group.setUuid(serviceContext.getUuid());
		}

		group.setCompanyId(user.getCompanyId());
		group.setCreatorUserId(userId);
		group.setClassNameId(classNameId);
		group.setClassPK(classPK);
		group.setParentGroupId(parentGroupId);
		group.setLiveGroupId(liveGroupId);
		group.setTreePath(group.buildTreePath());
		group.setGroupKey(groupKey);
		group.setNameMap(nameMap);
		group.setDescriptionMap(descriptionMap);
		group.setType(type);
		group.setManualMembership(manualMembership);
		group.setMembershipRestriction(membershipRestriction);
		group.setFriendlyURL(friendlyURL);
		group.setSite(site);
		group.setInheritContent(inheritContent);
		group.setActive(active);

		if ((serviceContext != null) && (classNameId == groupClassNameId) &&
			!user.isGuestUser()) {

			group.setExpandoBridgeAttributes(serviceContext);
		}

		group = groupPersistence.update(group);

		// Layout sets

		_layoutSetLocalService.addLayoutSet(groupId, true);

		_layoutSetLocalService.addLayoutSet(groupId, false);

		// Resources

		_resourceLocalService.addResources(
			group.getCompanyId(), 0, 0, Group.class.getName(),
			group.getGroupId(), false, false, false);

		if ((classNameId == groupClassNameId) && !user.isGuestUser()) {

			// Site roles

			Role role = _roleLocalService.getRole(
				group.getCompanyId(), RoleConstants.SITE_OWNER);

			_userGroupRoleLocalService.addUserGroupRoles(
				userId, groupId, new long[] {role.getRoleId()});

			// User

			_userLocalService.addGroupUsers(
				group.getGroupId(), new long[] {userId});

			// Asset

			if (serviceContext != null) {
				updateAsset(
					userId, group, serviceContext.getAssetCategoryIds(),
					serviceContext.getAssetTagNames());
			}
		}

		addPortletDefaultData(group);

		return group;
	}

	@Override
	public Group addGroup(
			long userId, long parentGroupId, String className, long classPK,
			long liveGroupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean active,
			ServiceContext serviceContext)
		throws PortalException {

		return addGroup(
			userId, parentGroupId, className, classPK, liveGroupId, nameMap,
			descriptionMap, type, manualMembership, membershipRestriction,
			friendlyURL, site, false, active, serviceContext);
	}

	/**
	 * Adds the group to the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param group the group
	 * @return <code>true</code> if the association between the ${organizationId} and ${group} is added; <code>false</code> if it was already added
	 */
	@Override
	public boolean addOrganizationGroup(long organizationId, Group group) {
		return addOrganizationGroup(organizationId, group.getGroupId());
	}

	/**
	 * Adds the group to the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param groupId the primary key of the group
	 * @return <code>true</code> if the association between the ${organizationId} and ${groupId} is added; <code>false</code> if it was already added
	 */
	@Override
	public boolean addOrganizationGroup(long organizationId, long groupId) {
		if (!super.addOrganizationGroup(organizationId, groupId)) {
			return false;
		}

		try {
			reindexUsersInOrganization(organizationId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	/**
	 * Adds the groups to the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param groups the groups
	 * @return <code>true</code> if at least an association between the ${organizationId} and the ${groups} is added; <code>false</code> if all were already added
	 */
	@Override
	public boolean addOrganizationGroups(
		long organizationId, List<Group> groups) {

		if (!super.addOrganizationGroups(organizationId, groups)) {
			return false;
		}

		try {
			reindexUsersInOrganization(organizationId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	/**
	 * Adds the groups to the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param groupIds the primary keys of the groups
	 * @return <code>true</code> if at least an association between the ${organizationId} and the ${groupIds} is added; <code>false</code> if all were already added
	 */
	@Override
	public boolean addOrganizationGroups(long organizationId, long[] groupIds) {
		if (!super.addOrganizationGroups(organizationId, groupIds)) {
			return false;
		}

		try {
			reindexUsersInOrganization(organizationId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	@Override
	public Group addOrUpdateGroup(
			String externalReferenceCode, long userId, long parentGroupId,
			String className, long classPK, long liveGroupId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			int type, boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean inheritContent,
			boolean active, ServiceContext serviceContext)
		throws Exception {

		User user = _userLocalService.getUser(userId);

		Group group = groupPersistence.fetchByERC_C(
			externalReferenceCode, user.getCompanyId());

		if (group == null) {
			group = addGroup(
				userId, parentGroupId, className, classPK, liveGroupId, nameMap,
				descriptionMap, type, manualMembership, membershipRestriction,
				friendlyURL, site, inheritContent, active, serviceContext);

			group.setExternalReferenceCode(externalReferenceCode);

			group = groupPersistence.update(group);
		}
		else {
			group = updateGroup(
				group.getGroupId(), parentGroupId, nameMap, descriptionMap,
				type, manualMembership, membershipRestriction, friendlyURL,
				inheritContent, active, serviceContext);
		}

		return group;
	}

	/**
	 * Adds the group to the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param group the group
	 * @return <code>true</code> if the association between the ${userGroupId} and ${group} is added; <code>false</code> if it was already added
	 */
	@Override
	public boolean addUserGroupGroup(long userGroupId, Group group) {
		return addUserGroupGroup(userGroupId, group.getGroupId());
	}

	/**
	 * Adds the group to the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param groupId the primary key of the group
	 * @return <code>true</code> if the association between the ${userGroupId} and ${groupId} is added; <code>false</code> if it was already added
	 */
	@Override
	public boolean addUserGroupGroup(long userGroupId, long groupId) {
		if (!super.addUserGroupGroup(userGroupId, groupId)) {
			return false;
		}

		try {
			reindexUsersInUserGroup(userGroupId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	/**
	 * Adds the groups to the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param groups the groups
	 * @return <code>true</code> if at least an association between the ${userGroupId} and the ${groups} is added; <code>false</code> if all were already added
	 */
	@Override
	public boolean addUserGroupGroups(long userGroupId, List<Group> groups) {
		if (!super.addUserGroupGroups(userGroupId, groups)) {
			return false;
		}

		try {
			reindexUsersInUserGroup(userGroupId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	/**
	 * Adds the groups to the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param groupIds the primary keys of the groups
	 * @return <code>true</code> if at least an association between the ${userGroupId} and the ${groupIds} is added; <code>false</code> if all were already added
	 */
	@Override
	public boolean addUserGroupGroups(long userGroupId, long[] groupIds) {
		if (!super.addUserGroupGroups(userGroupId, groupIds)) {
			return false;
		}

		try {
			reindexUsersInUserGroup(userGroupId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			ModelListener.class, new GroupModelListener(),
			MapUtil.singletonDictionary(
				"persistence.test.rule.aware", Boolean.TRUE));
	}

	@Override
	public Group checkScopeGroup(Layout layout, long userId)
		throws PortalException {

		if (layout.hasScopeGroup()) {
			return layout.getScopeGroup();
		}

		return groupLocalService.addGroup(
			userId, GroupConstants.DEFAULT_PARENT_GROUP_ID,
			Layout.class.getName(), layout.getPlid(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), String.valueOf(layout.getPlid())
			).build(),
			null, 0, true, GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null,
			false, true, null);
	}

	/**
	 * Creates systems groups and other related data needed by the system on the
	 * very first startup. Also takes care of creating the Control Panel groups
	 * and layouts.
	 *
	 * @param  companyId the primary key of the company
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void checkSystemGroups(long companyId) throws PortalException {
		String companyIdHexString = StringUtil.toHexString(companyId);

		String companyIdString = String.valueOf(companyId);

		String[] systemGroups = ArrayUtil.append(
			PortalUtil.getSystemGroups(), companyIdString);

		for (Group group :
				groupPersistence.findByC_GK(companyId, systemGroups)) {

			_systemGroupsMap.put(
				companyIdHexString.concat(group.getGroupKey()), group);
		}

		long guestUserId = _userLocalService.getGuestUserId(companyId);

		for (String groupKey : systemGroups) {
			if (groupKey.equals(GroupConstants.CMS) &&
				!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {

				continue;
			}

			String groupCacheKey = companyIdHexString.concat(groupKey);

			Group group = _systemGroupsMap.get(groupCacheKey);

			if (group == null) {
				String className = null;
				long classPK = 0;
				int type = GroupConstants.TYPE_SITE_RESTRICTED;
				String friendlyURL = null;
				boolean site = true;
				UnicodeProperties typeSettingsUnicodeProperties = null;

				if (groupKey.equals(GroupConstants.CALENDAR)) {
					type = GroupConstants.TYPE_SITE_PRIVATE;
					friendlyURL = GroupConstants.CALENDAR_FRIENDLY_URL;
					site = false;
				}
				else if (groupKey.equals(GroupConstants.CMS)) {
					type = GroupConstants.TYPE_SITE_PRIVATE;
					friendlyURL = GroupConstants.CMS_FRIENDLY_URL;
					site = false;
				}
				else if (groupKey.equals(GroupConstants.CONTROL_PANEL)) {
					type = GroupConstants.TYPE_SITE_PRIVATE;
					friendlyURL = GroupConstants.CONTROL_PANEL_FRIENDLY_URL;
					site = false;
				}
				else if (groupKey.equals(GroupConstants.FORMS)) {
					type = GroupConstants.TYPE_SITE_PRIVATE;
					friendlyURL = GroupConstants.FORMS_FRIENDLY_URL;
					site = false;
				}
				else if (groupKey.equals(GroupConstants.GUEST)) {
					friendlyURL = "/guest";
					typeSettingsUnicodeProperties =
						UnicodePropertiesBuilder.create(
							true
						).put(
							"siteInitializerKey",
							SiteInitializerThreadLocal.getKey()
						).build();
				}
				else if (groupKey.equals(GroupConstants.USER_PERSONAL_SITE)) {
					className = UserPersonalSite.class.getName();
					classPK = guestUserId;
					type = GroupConstants.TYPE_SITE_PRIVATE;
					friendlyURL =
						GroupConstants.USER_PERSONAL_SITE_FRIENDLY_URL;
					site = false;
				}
				else if (groupKey.equals(companyIdString)) {
					className = Company.class.getName();
					classPK = companyId;
					groupKey = GroupConstants.GLOBAL;
					type = 0;
					friendlyURL = GroupConstants.GLOBAL_FRIENDLY_URL;
				}

				group = groupLocalService.addGroup(
					guestUserId, GroupConstants.DEFAULT_PARENT_GROUP_ID,
					className, classPK, GroupConstants.DEFAULT_LIVE_GROUP_ID,
					getLocalizationMap(groupKey), null, type, true,
					GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL,
					site, true, null);

				if (typeSettingsUnicodeProperties != null) {
					group.setTypeSettingsProperties(
						typeSettingsUnicodeProperties);
				}

				group.setExternalReferenceCode(
					_toExternalReferenceCode(groupKey));

				group = groupPersistence.update(group);

				if (groupKey.equals(GroupConstants.USER_PERSONAL_SITE)) {
					initUserPersonalSitePermissions(group);
				}

				if (group.isControlPanel()) {
					addControlPanelLayouts(group);
				}

				if (group.isGuest()) {
					addDefaultGuestPublicLayouts(group);
				}
			}

			_systemGroupsMap.put(groupCacheKey, group);
		}
	}

	/**
	 * Clears the groups from the organization.
	 *
	 * @param organizationId the primary key of the organization
	 */
	@Override
	public void clearOrganizationGroups(long organizationId) {
		super.clearOrganizationGroups(organizationId);

		try {
			reindexUsersInOrganization(organizationId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	/**
	 * Clears the groups from the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 */
	@Override
	public void clearUserGroupGroups(long userGroupId) {
		super.clearUserGroupGroups(userGroupId);

		try {
			reindexUsersInUserGroup(userGroupId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	/**
	 * Deletes the group and its associated data.
	 *
	 * <p>
	 * The group is unstaged and its assets and resources including layouts,
	 * membership requests, subscriptions, teams, blogs, bookmarks, events,
	 * image gallery, journals, message boards, polls, and wikis are also
	 * deleted.
	 * </p>
	 *
	 * @param  group the group
	 * @return the deleted group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group deleteGroup(Group group) throws PortalException {
		boolean deleteInProcess = GroupThreadLocal.isDeleteInProcess();

		try {
			GroupThreadLocal.setDeleteInProcess(true);

			if (((group.isCompany() && !group.isCompanyStagingGroup()) ||
				 PortalUtil.isSystemGroup(group.getGroupKey())) &&
				!PortalInstances.isCurrentCompanyInDeletionProcess()) {

				throw new RequiredGroupException.MustNotDeleteSystemGroup(
					group.getGroupId());
			}

			int count = groupPersistence.countByC_P_S(
				group.getCompanyId(), group.getGroupId(), true);

			if (count > 0) {
				throw new RequiredGroupException.MustNotDeleteGroupThatHasChild(
					group.getGroupId());
			}

			if (ListUtil.isNotNull(
					BackgroundTaskManagerUtil.getBackgroundTasks(
						group.getGroupId(),
						BackgroundTaskConstants.STATUS_IN_PROGRESS))) {

				throw new PendingBackgroundTaskException(
					"Unable to delete group with pending background tasks");
			}

			// Background tasks

			BackgroundTaskManagerUtil.deleteGroupBackgroundTasks(
				group.getGroupId());

			// Layout set branches

			_layoutSetBranchLocalService.deleteLayoutSetBranches(
				group.getGroupId(), true, true);

			_layoutSetBranchLocalService.deleteLayoutSetBranches(
				group.getGroupId(), false, true);

			// Layout sets

			ServiceContext serviceContext = new ServiceContext();

			try {
				_layoutSetLocalService.deleteLayoutSet(
					group.getGroupId(), true, serviceContext);
			}
			catch (NoSuchLayoutSetException noSuchLayoutSetException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchLayoutSetException);
				}
			}

			try {
				_layoutSetLocalService.deleteLayoutSet(
					group.getGroupId(), false, serviceContext);
			}
			catch (NoSuchLayoutSetException noSuchLayoutSetException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchLayoutSetException);
				}
			}

			// Membership requests

			_membershipRequestLocalService.deleteMembershipRequests(
				group.getGroupId());

			// Portlet preferences

			_portletPreferencesLocalService.deletePortletPreferences(
				group.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				PortletKeys.PREFS_PLID_SHARED);
			_portletPreferencesLocalService.deletePortletPreferences(
				group.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				PortletKeys.PREFS_PLID_SHARED);

			// Repositories

			_dlAppLocalService.deleteAllRepositories(group.getGroupId());

			// Teams

			_teamLocalService.deleteTeams(group.getGroupId());

			// Staging

			_exportImportConfigurationLocalService.
				deleteExportImportConfigurations(group.getGroupId());

			unscheduleStaging(group);

			if (group.hasStagingGroup()) {
				try {
					_stagingLocalService.disableStaging(group, serviceContext);
				}
				catch (Exception exception) {
					_log.error(
						"Unable to disable staging for group " +
							group.getGroupId(),
						exception);
				}
			}

			// Themes

			ThemeLoader themeLoader =
				ThemeLoaderFactory.getDefaultThemeLoader();

			if (themeLoader != null) {
				String themePath =
					themeLoader.getFileStorage() + StringPool.SLASH +
						group.getGroupId();

				FileUtil.deltree(themePath + "-private");
				FileUtil.deltree(themePath + "-public");
			}

			// Portlet data

			deletePortletData(group);

			// Asset

			if (group.isRegularSite()) {
				_assetEntryLocalService.deleteEntry(
					Group.class.getName(), group.getGroupId());
			}

			_assetEntryLocalService.deleteGroupEntries(group.getGroupId());

			_assetTagLocalService.deleteGroupTags(group.getGroupId());

			_assetVocabularyLocalService.deleteVocabularies(group.getGroupId());

			// Expando

			_expandoRowLocalService.deleteRows(
				group.getCompanyId(),
				_classNameLocalService.getClassNameId(Group.class.getName()),
				group.getGroupId());

			// Social

			_socialActivityLocalService.deleteActivities(group.getGroupId());
			_socialActivitySettingLocalService.deleteActivitySettings(
				group.getGroupId());
			_socialRequestLocalService.deleteRequests(
				_classNameLocalService.getClassNameId(Group.class),
				group.getGroupId());

			// Workflow

			List<WorkflowDefinitionLink> workflowDefinitionLinks =
				_workflowDefinitionLinkLocalService.getWorkflowDefinitionLinks(
					group.getCompanyId(), group.getGroupId(), 0);

			for (WorkflowDefinitionLink workflowDefinitionLink :
					workflowDefinitionLinks) {

				_workflowDefinitionLinkLocalService.
					deleteWorkflowDefinitionLink(workflowDefinitionLink);
			}

			// Group

			if (!group.isStagingGroup() && group.isOrganization() &&
				group.isSite()) {

				group.setSite(false);

				group = groupPersistence.update(group);

				// Resources

				List<ResourcePermission> resourcePermissions =
					_resourcePermissionPersistence.findByC_S_P(
						group.getCompanyId(), ResourceConstants.SCOPE_GROUP,
						String.valueOf(group.getGroupId()));

				for (ResourcePermission resourcePermission :
						resourcePermissions) {

					_resourcePermissionLocalService.deleteResourcePermission(
						resourcePermission);
				}

				// Group roles

				_userGroupRoleLocalService.deleteUserGroupRoles(
					group.getGroupId(), RoleConstants.TYPE_SITE);

				// User group roles

				_userGroupGroupRoleLocalService.deleteUserGroupGroupRoles(
					group.getGroupId(), RoleConstants.TYPE_SITE);
			}
			else {

				// Group roles

				_userGroupRoleLocalService.deleteUserGroupRolesByGroupId(
					group.getGroupId());

				// User group roles

				_userGroupGroupRoleLocalService.
					deleteUserGroupGroupRolesByGroupId(group.getGroupId());

				// Resources

				List<ResourcePermission> resourcePermissions =
					_resourcePermissionPersistence.findByC_LikeP(
						group.getCompanyId(),
						String.valueOf(group.getGroupId()));

				for (ResourcePermission resourcePermission :
						resourcePermissions) {

					_resourcePermissionLocalService.deleteResourcePermission(
						resourcePermission);
				}

				// Indexer

				long companyId = group.getCompanyId();
				long[] userIds = getUserPrimaryKeys(group.getGroupId());

				if (ArrayUtil.isNotEmpty(userIds)) {
					TransactionCommitCallbackUtil.registerCallback(
						() -> {
							reindex(companyId, userIds);

							return null;
						});
				}

				List<UserGroup> groupUserGroups =
					_userGroupLocalService.getGroupUserGroups(
						group.getGroupId());

				for (UserGroup groupUserGroup : groupUserGroups) {
					reindexUserGroup(groupUserGroup.getUserGroupId());
				}

				groupPersistence.remove(group);
			}

			// System Events

			_systemEventLocalService.deleteSystemEvents(group.getGroupId());

			return group;
		}
		finally {
			GroupThreadLocal.setDeleteInProcess(deleteInProcess);
		}
	}

	/**
	 * Deletes the group and its associated data.
	 *
	 * <p>
	 * The group is unstaged and its assets and resources including layouts,
	 * membership requests, subscriptions, teams, blogs, bookmarks, events,
	 * image gallery, journals, message boards, polls, and wikis are also
	 * deleted.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @return the deleted group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group deleteGroup(long groupId) throws PortalException {
		Group group = groupPersistence.findByPrimaryKey(groupId);

		return deleteGroup(group);
	}

	/**
	 * Deletes the group from the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param group the group
	 */
	@Override
	public void deleteOrganizationGroup(long organizationId, Group group) {
		super.deleteOrganizationGroup(organizationId, group);

		try {
			reindexUsersInOrganization(organizationId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	/**
	 * Deletes the group from the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param groupId the primary key of the group
	 */
	@Override
	public void deleteOrganizationGroup(long organizationId, long groupId) {
		super.deleteOrganizationGroup(organizationId, groupId);

		try {
			reindexUsersInOrganization(organizationId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	/**
	 * Deletes the groups from the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param groups the groups
	 */
	@Override
	public void deleteOrganizationGroups(
		long organizationId, List<Group> groups) {

		super.deleteOrganizationGroups(organizationId, groups);

		try {
			reindexUsersInOrganization(organizationId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	/**
	 * Deletes the groups from the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param groupIds the primary keys of the groups
	 */
	@Override
	public void deleteOrganizationGroups(long organizationId, long[] groupIds) {
		super.deleteOrganizationGroups(organizationId, groupIds);

		try {
			reindexUsersInOrganization(organizationId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	/**
	 * Deletes the group from the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param group the group
	 */
	@Override
	public void deleteUserGroupGroup(long userGroupId, Group group) {
		super.deleteUserGroupGroup(userGroupId, group);

		try {
			reindexUsersInUserGroup(userGroupId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	/**
	 * Deletes the group from the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param groupId the primary key of the group
	 */
	@Override
	public void deleteUserGroupGroup(long userGroupId, long groupId) {
		super.deleteUserGroupGroup(userGroupId, groupId);

		try {
			reindexUsersInUserGroup(userGroupId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	/**
	 * Deletes the groups from the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param groups the groups
	 */
	@Override
	public void deleteUserGroupGroups(long userGroupId, List<Group> groups) {
		super.deleteUserGroupGroups(userGroupId, groups);

		try {
			reindexUsersInUserGroup(userGroupId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	/**
	 * Deletes the groups from the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param groupIds the primary keys of the groups
	 */
	@Override
	public void deleteUserGroupGroups(long userGroupId, long[] groupIds) {
		super.deleteUserGroupGroups(userGroupId, groupIds);

		try {
			reindexUsersInUserGroup(userGroupId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	@Override
	public void destroy() {
		super.destroy();

		_serviceRegistration.unregister();
	}

	@Override
	public synchronized void disableStaging(long groupId)
		throws PortalException {

		Group group = groupPersistence.findByPrimaryKey(groupId);

		int stagingGroupCount = group.getRemoteStagingGroupCount();

		if (stagingGroupCount == 0) {
			return;
		}

		stagingGroupCount = stagingGroupCount - 1;

		group.setRemoteStagingGroupCount(stagingGroupCount);

		if (stagingGroupCount == 0) {
			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			List<String> keys = new ArrayList<>();

			for (String key : typeSettingsUnicodeProperties.keySet()) {
				if (key.startsWith(StagingConstants.STAGED_PORTLET)) {
					keys.add(key);
				}
			}

			for (String key : keys) {
				typeSettingsUnicodeProperties.remove(key);
			}

			group.setTypeSettingsProperties(typeSettingsUnicodeProperties);
		}

		groupPersistence.update(group);
	}

	@Override
	public synchronized void enableStaging(long groupId)
		throws PortalException {

		Group group = groupPersistence.findByPrimaryKey(groupId);

		int stagingGroupCount = group.getRemoteStagingGroupCount() + 1;

		group.setRemoteStagingGroupCount(stagingGroupCount);

		groupPersistence.update(group);
	}

	/**
	 * Returns the company's group.
	 *
	 * @param  companyId the primary key of the company
	 * @return the company's group, or <code>null</code> if a matching group
	 *         could not be found
	 */
	@Override
	public Group fetchCompanyGroup(long companyId) {
		return groupPersistence.fetchByC_C_C(
			companyId, _classNameLocalService.getClassNameId(Company.class),
			companyId);
	}

	/**
	 * Returns the group with the matching friendly URL.
	 *
	 * @param  companyId the primary key of the company
	 * @param  friendlyURL the friendly URL
	 * @return the group with the friendly URL, or <code>null</code> if a
	 *         matching group could not be found
	 */
	@Override
	public Group fetchFriendlyURLGroup(long companyId, String friendlyURL) {
		if (Validator.isNull(friendlyURL)) {
			return null;
		}

		friendlyURL = getFriendlyURL(friendlyURL);

		return groupPersistence.fetchByC_F(companyId, friendlyURL);
	}

	@Override
	@ThreadLocalCachable
	public Group fetchGroup(long groupId) {
		return groupPersistence.fetchByPrimaryKey(groupId);
	}

	@Override
	public Group fetchGroup(long companyId, long classNameId, long classPK) {
		return groupPersistence.fetchByC_C_C(companyId, classNameId, classPK);
	}

	/**
	 * Returns the group with the matching group key by first searching the
	 * system groups and then using the finder cache.
	 *
	 * @param  companyId the primary key of the company
	 * @param  groupKey the group key
	 * @return the group with the group key and associated company, or
	 *         <code>null</code> if a matching group could not be found
	 */
	@Override
	@Transactional(enabled = false)
	public Group fetchGroup(long companyId, String groupKey) {
		String companyIdHexString = StringUtil.toHexString(companyId);

		Group group = _systemGroupsMap.get(companyIdHexString.concat(groupKey));

		if (group != null) {
			return group;
		}

		return groupLocalService.loadFetchGroup(companyId, groupKey);
	}

	@Override
	public Group fetchStagingGroup(long liveGroupId) {
		if (liveGroupId == 0) {
			return null;
		}

		if (_cacheableQueryLimitLPD28122 <= 0) {
			List<Group> groups = groupPersistence.findByLiveGroupId(
				liveGroupId);

			if (groups.isEmpty()) {
				return null;
			}

			if ((groups.size() > 1) && _log.isWarnEnabled()) {
				_log.warn(
					"More than one staging group uses live group ID " +
						liveGroupId);
			}

			return groups.get(groups.size() - 1);
		}

		Map<Long, Long> stagingGroupIds =
			_stagingGroupIdsDCLSingleton.getSingleton(
				this::_getStagingGroupIds);

		if (stagingGroupIds.size() > _cacheableQueryLimitLPD28122) {
			_cacheableQueryLimitLPD28122 = 0;

			_stagingGroupIdsDCLSingleton.destroy(null);
		}

		Long stagingGroupId = stagingGroupIds.get(liveGroupId);

		if (stagingGroupId == null) {
			return null;
		}

		return groupPersistence.fetchByPrimaryKey(stagingGroupId);
	}

	@Override
	public Group fetchUserGroup(long companyId, long userId) {
		return groupPersistence.fetchByC_C_C(
			companyId, _classNameLocalService.getClassNameId(User.class),
			userId);
	}

	/**
	 * Returns the default user's personal site group.
	 *
	 * @param  companyId the primary key of the company
	 * @return the default user's personal site group, or <code>null</code> if a
	 *         matching group could not be found
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group fetchUserPersonalSiteGroup(long companyId)
		throws PortalException {

		return groupPersistence.fetchByC_C_C(
			companyId,
			_classNameLocalService.getClassNameId(UserPersonalSite.class),
			_userLocalService.getGuestUserId(companyId));
	}

	@Override
	public List<Long> getActiveGroupIds(long userId) {
		return groupFinder.findByActiveGroupIds(userId);
	}

	/**
	 * Returns all the active or inactive groups associated with the company.
	 *
	 * @param  companyId the primary key of the company
	 * @param  active whether to return only active groups or only inactive
	 *         groups
	 * @return the active or inactive groups
	 */
	@Override
	public List<Group> getActiveGroups(long companyId, boolean active) {
		return groupPersistence.findByC_A(companyId, active);
	}

	/**
	 * Returns the active or inactive groups associated with the company and,
	 * optionally, the main site.
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
	 * @param  companyId the primary key of the company
	 * @param  site whether the group is associated with a main site
	 * @param  active whether to return only active groups or only inactive
	 *         groups
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the groups (optionally
	 *         <code>null</code>)
	 * @return the active or inactive groups
	 */
	@Override
	public List<Group> getActiveGroups(
		long companyId, boolean site, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return groupPersistence.findByC_S_A(
			companyId, site, active, start, end, orderByComparator);
	}

	/**
	 * Returns the active or inactive groups associated with the company.
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
	 * @param  companyId the primary key of the company
	 * @param  active whether to return only active groups or only inactive
	 *         groups
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the groups (optionally
	 *         <code>null</code>)
	 * @return the active or inactive groups
	 */
	@Override
	public List<Group> getActiveGroups(
		long companyId, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return groupPersistence.findByC_A(
			companyId, active, start, end, orderByComparator);
	}

	/**
	 * Returns the number of active or inactive groups associated with the
	 * company.
	 *
	 * @param  companyId the primary key of the company
	 * @param  active whether to count only active groups or only inactive
	 *         groups
	 * @return the number of active or inactive groups
	 */
	@Override
	public int getActiveGroupsCount(long companyId, boolean active) {
		return groupPersistence.countByC_A(companyId, active);
	}

	/**
	 * Returns the number of active or inactive groups associated with the
	 * company.
	 *
	 * @param  companyId the primary key of the company
	 * @param  active whether to count only active groups or only inactive
	 *         groups
	 * @param  site whether the group is to be associated with a main site
	 * @return the number of active or inactive groups
	 */
	@Override
	public int getActiveGroupsCount(
		long companyId, boolean active, boolean site) {

		return groupPersistence.countByC_S_A(companyId, active, site);
	}

	/**
	 * Returns the company group.
	 *
	 * @param  companyId the primary key of the company
	 * @return the group associated with the company
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getCompanyGroup(long companyId) throws PortalException {
		return groupPersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(Company.class),
			companyId);
	}

	/**
	 * Returns a range of all the groups associated with the company.
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
	 * @param  companyId the primary key of the company
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @return the range of groups associated with the company
	 */
	@Override
	public List<Group> getCompanyGroups(long companyId, int start, int end) {
		return groupPersistence.findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns the number of groups associated with the company.
	 *
	 * @param  companyId the primary key of the company
	 * @return the number of groups associated with the company
	 */
	@Override
	public int getCompanyGroupsCount(long companyId) {
		return groupPersistence.countByCompanyId(companyId);
	}

	/**
	 * Returns the group with the matching friendly URL.
	 *
	 * @param  companyId the primary key of the company
	 * @param  friendlyURL the group's friendlyURL
	 * @return the group with the friendly URL
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getFriendlyURLGroup(long companyId, String friendlyURL)
		throws PortalException {

		if (Validator.isNull(friendlyURL)) {
			throw new NoSuchGroupException(
				StringBundler.concat(
					"{companyId=", companyId, ", friendlyURL=", friendlyURL,
					"}"));
		}

		friendlyURL = getFriendlyURL(friendlyURL);

		return groupPersistence.findByC_F(companyId, friendlyURL);
	}

	/**
	 * Returns the group with the matching primary key.
	 *
	 * @param  groupId the primary key of the group
	 * @return the group with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	@ThreadLocalCachable
	public Group getGroup(long groupId) throws PortalException {
		return groupPersistence.findByPrimaryKey(groupId);
	}

	/**
	 * Returns the group with the matching group key.
	 *
	 * @param  companyId the primary key of the company
	 * @param  groupKey the group key
	 * @return the group with the group key
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	@Transactional(enabled = false)
	public Group getGroup(long companyId, String groupKey)
		throws PortalException {

		String companyIdHexString = StringUtil.toHexString(companyId);

		Group group = _systemGroupsMap.get(companyIdHexString.concat(groupKey));

		if (group != null) {
			return group;
		}

		return groupLocalService.loadGetGroup(companyId, groupKey);
	}

	@Override
	public List<Long> getGroupIds(long companyId, boolean active) {
		return groupFinder.findByC_A(companyId, active);
	}

	/**
	 * Returns all the groups that are direct children of the parent group.
	 *
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  site whether the group is to be associated with a main site
	 * @return the matching groups, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Group> getGroups(
		long companyId, long parentGroupId, boolean site) {

		if (parentGroupId == GroupConstants.ANY_PARENT_GROUP_ID) {
			return groupPersistence.findByC_S(companyId, site);
		}

		return groupPersistence.findByC_P_S(companyId, parentGroupId, site);
	}

	@Override
	public List<Group> getGroups(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent) {

		return groupPersistence.findByC_P_S_I(
			companyId, parentGroupId, site, inheritContent);
	}

	@Override
	public List<Group> getGroups(
		long companyId, long parentGroupId, boolean site, int start, int end) {

		if (parentGroupId == GroupConstants.ANY_PARENT_GROUP_ID) {
			return groupPersistence.findByC_S(companyId, site, start, end);
		}

		return groupPersistence.findByC_P_S(
			companyId, parentGroupId, site, start, end);
	}

	@Override
	public List<Group> getGroups(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end) {

		if (Validator.isNull(name)) {
			return getGroups(companyId, parentGroupId, site, start, end);
		}

		if (parentGroupId == GroupConstants.ANY_PARENT_GROUP_ID) {
			return groupPersistence.findByC_LikeN_S(
				companyId, name, site, start, end);
		}

		return groupPersistence.findByC_P_LikeN_S(
			companyId, parentGroupId, name, site, start, end);
	}

	@Override
	public List<Group> getGroups(
		long companyId, String treePath, boolean site) {

		return groupPersistence.findByC_LikeT_S(companyId, treePath, site);
	}

	/**
	 * Returns all the groups that are direct children of the parent group with
	 * the matching className.
	 *
	 * @param  companyId the primary key of the company
	 * @param  className the class name of the group
	 * @param  parentGroupId the primary key of the parent group
	 * @return the matching groups, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Group> getGroups(
		long companyId, String className, long parentGroupId) {

		return groupPersistence.findByC_C_P(
			companyId, _classNameLocalService.getClassNameId(className),
			parentGroupId);
	}

	/**
	 * Returns a range of all the groups that are direct children of the parent
	 * group with the matching className.
	 *
	 * @param  companyId the primary key of the company
	 * @param  className the class name of the group
	 * @param  parentGroupId the primary key of the parent group
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> getGroups(
		long companyId, String className, long parentGroupId, int start,
		int end) {

		return groupPersistence.findByC_C_P(
			companyId, _classNameLocalService.getClassNameId(className),
			parentGroupId, start, end);
	}

	/**
	 * Returns the groups with the matching primary keys.
	 *
	 * @param  groupIds the primary keys of the groups
	 * @return the groups with the primary keys
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> getGroups(long[] groupIds) throws PortalException {
		List<Group> groups = new ArrayList<>(groupIds.length);

		for (long groupId : groupIds) {
			groups.add(getGroup(groupId));
		}

		return groups;
	}

	/**
	 * Returns the number of groups that are direct children of the parent
	 * group.
	 *
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  site whether the group is to be associated with a main site
	 * @return the number of matching groups
	 */
	@Override
	public int getGroupsCount(
		long companyId, long parentGroupId, boolean site) {

		if (parentGroupId == GroupConstants.ANY_PARENT_GROUP_ID) {
			return groupPersistence.countByC_S(companyId, site);
		}

		return groupPersistence.countByC_P_S(companyId, parentGroupId, site);
	}

	@Override
	public int getGroupsCount(
		long companyId, long parentGroupId, String name, boolean site) {

		if (Validator.isNull(name)) {
			return getGroupsCount(companyId, parentGroupId, site);
		}

		if (parentGroupId == GroupConstants.ANY_PARENT_GROUP_ID) {
			return groupPersistence.countByC_LikeN_S(companyId, name, site);
		}

		return groupPersistence.countByC_P_LikeN_S(
			companyId, parentGroupId, name, site);
	}

	/**
	 * Returns the number of groups that are direct children of the parent group
	 * with the matching className.
	 *
	 * @param  companyId the primary key of the company
	 * @param  className the class name of the group
	 * @param  parentGroupId the primary key of the parent group
	 * @return the number of matching groups
	 */
	@Override
	public int getGroupsCount(
		long companyId, String className, long parentGroupId) {

		return groupPersistence.countByC_C_P(
			companyId, _classNameLocalService.getClassNameId(className),
			parentGroupId);
	}

	/**
	 * Returns the group associated with the layout.
	 *
	 * @param  companyId the primary key of the company
	 * @param  plid the primary key of the layout
	 * @return the group associated with the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getLayoutGroup(long companyId, long plid)
		throws PortalException {

		return groupPersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(Layout.class),
			plid);
	}

	/**
	 * Returns the group associated with the layout prototype.
	 *
	 * @param  companyId the primary key of the company
	 * @param  layoutPrototypeId the primary key of the layout prototype
	 * @return the group associated with the layout prototype
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getLayoutPrototypeGroup(long companyId, long layoutPrototypeId)
		throws PortalException {

		return groupPersistence.findByC_C_C(
			companyId,
			_classNameLocalService.getClassNameId(LayoutPrototype.class),
			layoutPrototypeId);
	}

	/**
	 * Returns the group associated with the layout set prototype.
	 *
	 * @param  companyId the primary key of the company
	 * @param  layoutSetPrototypeId the primary key of the layout set prototype
	 * @return the group associated with the layout set prototype
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getLayoutSetPrototypeGroup(
			long companyId, long layoutSetPrototypeId)
		throws PortalException {

		return groupPersistence.findByC_C_C(
			companyId,
			_classNameLocalService.getClassNameId(LayoutSetPrototype.class),
			layoutSetPrototypeId);
	}

	/**
	 * Returns a range of all groups that are children of the parent group and
	 * that have at least one layout.
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
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  site whether the group is to be associated with a main site
	 * @param  active whether to return only active groups, or only inactive
	 *         groups
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the groups (optionally
	 *         <code>null</code>)
	 * @return the range of matching groups ordered by comparator
	 *         <code>orderByComparator</code>
	 */
	@Override
	public List<Group> getLayoutsGroups(
		long companyId, long parentGroupId, boolean site, boolean active,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return groupFinder.findByLayouts(
			companyId, parentGroupId, site, active, start, end,
			orderByComparator);
	}

	/**
	 * Returns a range of all groups that are children of the parent group and
	 * that have at least one layout.
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
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  site whether the group is to be associated with a main site
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the groups (optionally
	 *         <code>null</code>)
	 * @return the range of matching groups ordered by comparator
	 *         <code>orderByComparator</code>
	 */
	@Override
	public List<Group> getLayoutsGroups(
		long companyId, long parentGroupId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return groupFinder.findByLayouts(
			companyId, parentGroupId, site, null, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of groups that are children or the parent group and
	 * that have at least one layout
	 *
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  site whether the group is to be associated with a main site
	 * @return the number of matching groups
	 */
	@Override
	public int getLayoutsGroupsCount(
		long companyId, long parentGroupId, boolean site) {

		return groupFinder.countByLayouts(companyId, parentGroupId, site);
	}

	/**
	 * Returns the number of groups that are children or the parent group and
	 * that have at least one layout
	 *
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  site whether the group is to be associated with a main site
	 * @param  active whether to return only active groups, or only inactive
	 *         groups
	 * @return the number of matching groups
	 */
	@Override
	public int getLayoutsGroupsCount(
		long companyId, long parentGroupId, boolean site, boolean active) {

		return groupFinder.countByLayouts(
			companyId, parentGroupId, site, active);
	}

	/**
	 * Returns all live groups.
	 *
	 * @return all live groups
	 */
	@Override
	public List<Group> getLiveGroups() {
		return groupFinder.findByLiveGroups();
	}

	/**
	 * Returns the specified organization group.
	 *
	 * @param  companyId the primary key of the company
	 * @param  organizationId the primary key of the organization
	 * @return the group associated with the organization
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getOrganizationGroup(long companyId, long organizationId)
		throws PortalException {

		return groupPersistence.findByC_C_C(
			companyId,
			_classNameLocalService.getClassNameId(Organization.class),
			organizationId);
	}

	/**
	 * Returns the specified organization groups.
	 *
	 * @param  organizations the organizations
	 * @return the groups associated with the organizations
	 */
	@Override
	public List<Group> getOrganizationsGroups(
		List<Organization> organizations) {

		return TransformUtil.transform(
			organizations, organization -> organization.getGroup());
	}

	/**
	 * Returns all the groups related to the organizations.
	 *
	 * @param  organizations the organizations
	 * @return the groups related to the organizations
	 */
	@Override
	public List<Group> getOrganizationsRelatedGroups(
		List<Organization> organizations) {

		List<Group> organizationGroups = new ArrayList<>();

		for (Organization organization : organizations) {
			organizationGroups.addAll(
				_organizationPersistence.getGroups(
					organization.getOrganizationId()));
		}

		return organizationGroups;
	}

	/**
	 * Returns the group followed by all its parent groups ordered by closest
	 * ancestor.
	 *
	 * @param  groupId the primary key of the group
	 * @return the group followed by all its parent groups ordered by closest
	 *         ancestor
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> getParentGroups(long groupId) throws PortalException {
		if (groupId == GroupConstants.DEFAULT_PARENT_GROUP_ID) {
			return new ArrayList<>();
		}

		Group group = groupPersistence.findByPrimaryKey(groupId);

		return group.getAncestors();
	}

	@Override
	public List<Group> getStagedSites() {
		return groupFinder.findByL_TS_S_RSGC(0, "staged=true", true, 0);
	}

	/**
	 * Returns the group directly associated with the user.
	 *
	 * @param  companyId the primary key of the company
	 * @param  userId the primary key of the user
	 * @return the group directly associated with the user
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getUserGroup(long companyId, long userId)
		throws PortalException {

		return groupPersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(User.class),
			userId);
	}

	/**
	 * Returns the specified "user group" group. That is, the group that
	 * represents the {@link UserGroup} entity.
	 *
	 * @param  companyId the primary key of the company
	 * @param  userGroupId the primary key of the user group
	 * @return the group associated with the user group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getUserGroupGroup(long companyId, long userGroupId)
		throws PortalException {

		return groupPersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(UserGroup.class),
			userGroupId);
	}

	/**
	 * Returns all the user's site groups and immediate organization groups,
	 * optionally including the user's inherited organization groups and user
	 * groups. System and staged groups are not included.
	 *
	 * @param  userId the primary key of the user
	 * @param  inherit whether to include the user's inherited organization
	 *         groups and user groups
	 * @return the user's groups and immediate organization groups
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> getUserGroups(long userId, boolean inherit)
		throws PortalException {

		return getUserGroups(
			userId, inherit, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns an ordered range of all the user's site groups and immediate
	 * organization groups, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
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
	 * @param  userId the primary key of the user
	 * @param  inherit whether to include the user's inherited organization
	 *         groups and user groups
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @return the range of the user's groups and immediate organization groups
	 *         ordered by name
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> getUserGroups(
			long userId, boolean inherit, int start, int end)
		throws PortalException {

		if (inherit) {
			User user = _userPersistence.findByPrimaryKey(userId);

			return search(
				user.getCompanyId(), null, null,
				LinkedHashMapBuilder.<String, Object>put(
					"usersGroups", Long.valueOf(userId)
				).build(),
				start, end);
		}

		return _userPersistence.getGroups(userId, start, end);
	}

	/**
	 * Returns the groups associated with the user groups.
	 *
	 * @param  userGroups the user groups
	 * @return the groups associated with the user groups
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> getUserGroupsGroups(List<UserGroup> userGroups)
		throws PortalException {

		return TransformUtil.transform(
			userGroups, userGroup -> userGroup.getGroup());
	}

	/**
	 * Returns all the groups related to the user groups.
	 *
	 * @param  userGroups the user groups
	 * @return the groups related to the user groups
	 */
	@Override
	public List<Group> getUserGroupsRelatedGroups(List<UserGroup> userGroups) {
		List<Group> userGroupGroups = new ArrayList<>();

		for (UserGroup userGroup : userGroups) {
			userGroupGroups.addAll(
				_userGroupPersistence.getGroups(userGroup.getUserGroupId()));
		}

		return userGroupGroups;
	}

	/**
	 * Returns the range of all groups associated with the user's organization
	 * groups, including the ancestors of the organization groups, unless portal
	 * property <code>organizations.membership.strict</code> is set to
	 * <code>true</code>.
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
	 * @param  userId the primary key of the user
	 * @param  start the lower bound of the range of groups to consider
	 * @param  end the upper bound of the range of groups to consider (not
	 *         inclusive)
	 * @return the range of groups associated with the user's organization
	 *         groups
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> getUserOrganizationsGroups(
			long userId, int start, int end)
		throws PortalException {

		List<Group> userOrgsGroups = new ArrayList<>();

		List<Organization> userOrgs =
			_organizationLocalService.getUserOrganizations(userId, start, end);

		for (Organization organization : userOrgs) {
			userOrgsGroups.add(0, organization.getGroup());

			if (!PropsValues.ORGANIZATIONS_MEMBERSHIP_STRICT) {
				for (Organization ancestorOrganization :
						organization.getAncestors()) {

					userOrgsGroups.add(0, ancestorOrganization.getGroup());
				}
			}
		}

		return ListUtil.unique(userOrgsGroups);
	}

	/**
	 * Returns the default user's personal site group.
	 *
	 * @param  companyId the primary key of the company
	 * @return the default user's personal site group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getUserPersonalSiteGroup(long companyId)
		throws PortalException {

		return groupPersistence.findByC_C_C(
			companyId,
			_classNameLocalService.getClassNameId(UserPersonalSite.class),
			_userLocalService.getGuestUserId(companyId));
	}

	@Override
	public List<Group> getUserSitesGroups(long userId) throws PortalException {
		UserBag userBag = PermissionCacheUtil.getUserBag(userId);

		if (userBag == null) {
			User user = _userPersistence.findByPrimaryKey(userId);

			return groupFinder.findByCompanyId(
				user.getCompanyId(),
				LinkedHashMapBuilder.<String, Object>put(
					"inherit", Boolean.TRUE
				).put(
					"site", Boolean.TRUE
				).put(
					"usersGroups", userId
				).build(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new GroupNameComparator(true));
		}

		Collection<Group> userGroups = userBag.getUserGroups();

		List<Group> userSiteGroups = new ArrayList<>(userGroups.size());

		for (Group group : userGroups) {
			if (group.isSite()) {
				userSiteGroups.add(group);
			}
		}

		userSiteGroups.sort(new GroupNameComparator(true));

		return userSiteGroups;
	}

	@Override
	public List<Group> getUserSitesGroups(
			long userId, boolean includeAdministrative)
		throws PortalException {

		if (!includeAdministrative) {
			return getUserSitesGroups(userId);
		}

		Set<Group> siteGroups = new HashSet<>();

		List<UserGroupRole> userGroupRoles =
			_userGroupRoleLocalService.getUserGroupRoles(userId);

		for (UserGroupRole userGroupRole : userGroupRoles) {
			Role role = userGroupRole.getRole();

			String roleName = role.getName();

			if (roleName.equals(RoleConstants.SITE_ADMINISTRATOR) ||
				roleName.equals(RoleConstants.SITE_OWNER)) {

				siteGroups.add(userGroupRole.getGroup());
			}
		}

		siteGroups.addAll(getUserSitesGroups(userId));

		return new ArrayList<>(siteGroups);
	}

	@Override
	public List<Group> getUserSitesGroups(long userId, int start, int end)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		return groupFinder.findByCompanyId(
			user.getCompanyId(),
			LinkedHashMapBuilder.<String, Object>put(
				"inherit", Boolean.TRUE
			).put(
				"site", Boolean.TRUE
			).put(
				"usersGroups", userId
			).build(),
			start, end, new GroupNameComparator(true));
	}

	/**
	 * Returns <code>true</code> if the user is immediately associated with the
	 * group, or associated with the group via the user's organizations,
	 * inherited organizations, or user groups.
	 *
	 * @param  userId the primary key of the user
	 * @param  groupId the primary key of the group
	 * @return <code>true</code> if the user is associated with the group;
	 *         <code>false</code> otherwise
	 */
	@Override
	public boolean hasUserGroup(long userId, long groupId) {
		return hasUserGroup(userId, groupId, true);
	}

	/**
	 * Returns <code>true</code> if the user is immediately associated with the
	 * group, or optionally if the user is associated with the group via the
	 * user's organizations, inherited organizations, or user groups.
	 *
	 * @param  userId the primary key of the user
	 * @param  groupId the primary key of the group
	 * @param  inherit whether to include organization groups and user groups to
	 *         which the user belongs in the determination
	 * @return <code>true</code> if the user is associated with the group;
	 *         <code>false</code> otherwise
	 */
	@Override
	public boolean hasUserGroup(long userId, long groupId, boolean inherit) {
		if (groupFinder.countByG_U(groupId, userId, inherit) > 0) {
			return true;
		}

		return false;
	}

	@Override
	@Transactional(enabled = false)
	public boolean isLiveGroupActive(Group group) {
		if (group == null) {
			return false;
		}

		if (!group.isStagingGroup()) {
			return group.isActive();
		}

		Group liveGroup = group.getLiveGroup();

		if (liveGroup == null) {
			return false;
		}

		return liveGroup.isActive();
	}

	/**
	 * Returns the group with the matching group key by first searching the
	 * system groups and then using the finder cache.
	 *
	 * @param  companyId the primary key of the company
	 * @param  groupKey the group key
	 * @return the group with the group key and associated company, or
	 *         <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group loadFetchGroup(long companyId, String groupKey) {
		return groupPersistence.fetchByC_GK(companyId, groupKey);
	}

	/**
	 * Returns the group with the matching group key.
	 *
	 * @param  companyId the primary key of the company
	 * @param  groupKey the group key
	 * @return the group with the group key and associated company
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group loadGetGroup(long companyId, String groupKey)
		throws PortalException {

		return groupPersistence.findByC_GK(companyId, groupKey);
	}

	/**
	 * Rebuilds the group tree.
	 *
	 * <p>
	 * Only call this method if the tree has become stale through operations
	 * other than normal CRUD. Under normal circumstances the tree is
	 * automatically rebuilt whenever necessary.
	 * </p>
	 *
	 * @param  companyId the primary key of the group's company
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void rebuildTree(long companyId) throws PortalException {
		long classNameId = _classNameLocalService.getClassNameId(Group.class);

		TreePathUtil.rebuildTree(
			companyId, GroupConstants.DEFAULT_PARENT_GROUP_ID, StringPool.SLASH,
			new TreeModelTasksAdapter<Group>() {

				@Override
				public List<Group> findTreeModels(
					long previousId, long companyId, long parentPrimaryKey,
					int size) {

					return groupPersistence.findByGtG_C_C_P(
						previousId, companyId, classNameId, parentPrimaryKey,
						QueryUtil.ALL_POS, size,
						GroupIdComparator.getInstance(true));
				}

			});
	}

	/**
	 * Returns an ordered range of all the company's groups, optionally
	 * including the user's inherited organization groups and user groups.
	 * System and staged groups are not included.
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
	 * @param  companyId the primary key of the company
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include a user's organizations, inherited organizations, and user
	 *         groups in the search, add an entry with key
	 *         &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 *         key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 *         For more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @return the matching groups ordered by name
	 */
	@Override
	public List<Group> search(
		long companyId, LinkedHashMap<String, Object> params, int start,
		int end) {

		return groupFinder.findByCompanyId(
			companyId, params, start, end, new GroupNameComparator(true));
	}

	/**
	 * Returns an ordered range of all the groups belonging to the parent group
	 * that match the keywords, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
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
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         sites's name, or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organizations and user groups in the
	 *         search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @return the matching groups ordered by name
	 */
	@Override
	public List<Group> search(
		long companyId, long parentGroupId, String keywords,
		LinkedHashMap<String, Object> params, int start, int end) {

		return search(
			companyId, getClassNameIds(), parentGroupId, keywords, params,
			start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups belonging to the parent group
	 * that match the keywords, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
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
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         sites's name, or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organizations and user groups in the
	 *         search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the groups (optionally
	 *         <code>null</code>)
	 * @return the matching groups ordered by comparator
	 *         <code>orderByComparator</code>
	 */
	@Override
	public List<Group> search(
		long companyId, long parentGroupId, String keywords,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return search(
			companyId, getClassNameIds(), parentGroupId, keywords, params,
			start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the site groups belonging to the parent
	 * group and organization groups that match the name and description,
	 * optionally including the user's inherited organization groups and user
	 * groups. System and staged groups are not included.
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
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  name the group's name (optionally <code>null</code>)
	 * @param  description the group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organizations and user groups in the
	 *         search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @return the matching groups ordered by name
	 */
	@Override
	public List<Group> search(
		long companyId, long parentGroupId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end) {

		return search(
			companyId, getClassNameIds(), parentGroupId, name, description,
			params, andOperator, start, end, null);
	}

	/**
	 * Returns an ordered range of all the site groups belonging to the parent
	 * group and organization groups that match the name and description,
	 * optionally including the user's inherited organization groups and user
	 * groups. System and staged groups are not included.
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
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  name the group's name (optionally <code>null</code>)
	 * @param  description the group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organizations and user groups in the
	 *         search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the groups (optionally
	 *         <code>null</code>)
	 * @return the matching groups ordered by comparator
	 *         <code>orderByComparator</code>
	 */
	@Override
	public List<Group> search(
		long companyId, long parentGroupId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<Group> orderByComparator) {

		return search(
			companyId, getClassNameIds(), parentGroupId, name, description,
			params, andOperator, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups belonging to the parent group
	 * that match the class name IDs and keywords, optionally including the
	 * user's inherited organization groups and user groups. System and staged
	 * groups are not included.
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
	 * @param  companyId the primary key of the company
	 * @param  classNameIds the primary keys of the class names of the entities
	 *         the groups are related to (optionally <code>null</code>)
	 * @param  parentGroupId the primary key of the parent group
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         sites's name, or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include a user's organizations, inherited organizations, and user
	 *         groups in the search, add an entry with key
	 *         &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 *         key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 *         For more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @return the matching groups ordered by name
	 */
	@Override
	public List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId,
		String keywords, LinkedHashMap<String, Object> params, int start,
		int end) {

		return search(
			companyId, classNameIds, parentGroupId, keywords, params, start,
			end, null);
	}

	/**
	 * Returns an ordered range of all the groups belonging to the parent group
	 * that match the class name IDs and keywords, optionally including the
	 * user's inherited organization groups and user groups. System and staged
	 * groups are not included.
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
	 * @param  companyId the primary key of the company
	 * @param  classNameIds the primary keys of the class names of the entities
	 *         the groups are related to (optionally <code>null</code>)
	 * @param  parentGroupId the primary key of the parent group
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         sites's name, or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include a user's organizations, inherited organizations, and user
	 *         groups in the search, add an entry with key
	 *         &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 *         key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 *         For more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the groups (optionally
	 *         <code>null</code>)
	 * @return the matching groups ordered by comparator
	 *         <code>orderByComparator</code>
	 */
	@Override
	public List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId,
		String keywords, LinkedHashMap<String, Object> params, int start,
		int end, OrderByComparator<Group> orderByComparator) {

		String[] keywordsArray = getSearchNames(companyId, keywords);

		boolean andOperator = false;

		if (Validator.isNull(keywords)) {
			andOperator = true;
		}

		if (isUseComplexSQL(classNameIds)) {
			return groupFinder.findByC_C_PG_N_D(
				companyId, classNameIds, parentGroupId, keywordsArray,
				keywordsArray, params, andOperator, start, end,
				orderByComparator);
		}

		Collection<Group> groups = doSearch(
			companyId, classNameIds, parentGroupId, keywordsArray,
			keywordsArray, params, andOperator);

		return sort(groups, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups belonging to the parent group
	 * that match the class name IDs, name, and description, optionally
	 * including the user's inherited organization groups and user groups.
	 * System and staged groups are not included.
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
	 * @param  companyId the primary key of the company
	 * @param  classNameIds the primary keys of the class names of the entities
	 *         the groups are related to (optionally <code>null</code>)
	 * @param  parentGroupId the primary key of the parent group
	 * @param  name the group's name (optionally <code>null</code>)
	 * @param  description the group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include a user's organizations, inherited organizations, and user
	 *         groups in the search, add an entry with key
	 *         &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 *         key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 *         For more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @return the matching groups ordered by name
	 */
	@Override
	public List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId, String name,
		String description, LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end) {

		return search(
			companyId, classNameIds, parentGroupId, name, description, params,
			andOperator, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups belonging to the parent group
	 * that match the class name IDs, name, and description, optionally
	 * including the user's inherited organization groups and user groups.
	 * System and staged groups are not included.
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
	 * @param  companyId the primary key of the company
	 * @param  classNameIds the primary keys of the class names of the entities
	 *         the groups are related to (optionally <code>null</code>)
	 * @param  parentGroupId the primary key of the parent group
	 * @param  name the group's name (optionally <code>null</code>)
	 * @param  description the group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include a user's organizations, inherited organizations, and user
	 *         groups in the search, add an entry with key
	 *         &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 *         key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 *         For more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the groups (optionally
	 *         <code>null</code>)
	 * @return the matching groups ordered by comparator
	 *         <code>orderByComparator</code>
	 */
	@Override
	public List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId, String name,
		String description, LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		String[] names = getSearchNames(companyId, name);
		String[] descriptions = CustomSQLUtil.keywords(description);

		if (isUseComplexSQL(classNameIds)) {
			return groupFinder.findByC_C_PG_N_D(
				companyId, classNameIds, parentGroupId, names, descriptions,
				params, andOperator, start, end, orderByComparator);
		}

		Collection<Group> groups = doSearch(
			companyId, classNameIds, parentGroupId, names, descriptions, params,
			andOperator);

		return sort(groups, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups that match the class name IDs
	 * and keywords, optionally including the user's inherited organization
	 * groups and user groups. System and staged groups are not included.
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
	 * @param  companyId the primary key of the company
	 * @param  classNameIds the primary keys of the class names of the entities
	 *         the groups are related to (optionally <code>null</code>)
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         sites's name, or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include a user's organizations, inherited organizations, and user
	 *         groups in the search, add an entry with key
	 *         &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 *         key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 *         For more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @return the matching groups ordered by name
	 */
	@Override
	public List<Group> search(
		long companyId, long[] classNameIds, String keywords,
		LinkedHashMap<String, Object> params, int start, int end) {

		return search(
			companyId, classNameIds, GroupConstants.ANY_PARENT_GROUP_ID,
			keywords, params, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups that match the class name IDs
	 * and keywords, optionally including the user's inherited organization
	 * groups and user groups. System and staged groups are not included.
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
	 * @param  companyId the primary key of the company
	 * @param  classNameIds the primary keys of the class names of the entities
	 *         the groups are related to (optionally <code>null</code>)
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         sites's name, or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include a user's organizations, inherited organizations, and user
	 *         groups in the search, add an entry with key
	 *         &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 *         key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 *         For more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the groups (optionally
	 *         <code>null</code>)
	 * @return the matching groups ordered by comparator
	 *         <code>orderByComparator</code>
	 */
	@Override
	public List<Group> search(
		long companyId, long[] classNameIds, String keywords,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return search(
			companyId, classNameIds, GroupConstants.ANY_PARENT_GROUP_ID,
			keywords, params, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups that match the class name IDs,
	 * name, and description, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
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
	 * @param  companyId the primary key of the company
	 * @param  classNameIds the primary keys of the class names of the entities
	 *         the groups are related to (optionally <code>null</code>)
	 * @param  name the group's name (optionally <code>null</code>)
	 * @param  description the group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include a user's organizations, inherited organizations, and user
	 *         groups in the search, add an entry with key
	 *         &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 *         key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 *         For more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @return the matching groups ordered by name
	 */
	@Override
	public List<Group> search(
		long companyId, long[] classNameIds, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end) {

		return search(
			companyId, classNameIds, GroupConstants.ANY_PARENT_GROUP_ID, name,
			description, params, andOperator, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups that match the class name IDs,
	 * name, and description, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
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
	 * @param  companyId the primary key of the company
	 * @param  classNameIds the primary keys of the class names of the entities
	 *         the groups are related to (optionally <code>null</code>)
	 * @param  name the group's name (optionally <code>null</code>)
	 * @param  description the group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include a user's organizations, inherited organizations, and user
	 *         groups in the search, add an entry with key
	 *         &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 *         key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 *         For more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the groups (optionally
	 *         <code>null</code>)
	 * @return the matching groups ordered by comparator
	 *         <code>orderByComparator</code>
	 */
	@Override
	public List<Group> search(
		long companyId, long[] classNameIds, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<Group> orderByComparator) {

		return search(
			companyId, classNameIds, GroupConstants.ANY_PARENT_GROUP_ID, name,
			description, params, andOperator, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups that match the keywords,
	 * optionally including the user's inherited organization groups and user
	 * groups. System and staged groups are not included.
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
	 * @param  companyId the primary key of the company
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         sites's name, or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organizations and user groups in the
	 *         search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @return the matching groups ordered by name
	 */
	@Override
	@ThreadLocalCachable
	public List<Group> search(
		long companyId, String keywords, LinkedHashMap<String, Object> params,
		int start, int end) {

		return search(
			companyId, getClassNameIds(), GroupConstants.ANY_PARENT_GROUP_ID,
			keywords, params, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups that match the keywords,
	 * optionally including the user's inherited organization groups and user
	 * groups. System and staged groups are not included.
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
	 * @param  companyId the primary key of the company
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         sites's name, or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organizations and user groups in the
	 *         search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the groups (optionally
	 *         <code>null</code>)
	 * @return the matching groups ordered by comparator
	 *         <code>orderByComparator</code>
	 */
	@Override
	public List<Group> search(
		long companyId, String keywords, LinkedHashMap<String, Object> params,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return search(
			companyId, getClassNameIds(), GroupConstants.ANY_PARENT_GROUP_ID,
			keywords, params, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the site groups and organization groups
	 * that match the name and description, optionally including the user's
	 * inherited organization groups and user groups. System and staged groups
	 * are not included.
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
	 * @param  companyId the primary key of the company
	 * @param  name the group's name (optionally <code>null</code>)
	 * @param  description the group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organizations and user groups in the
	 *         search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @return the matching groups ordered by name
	 */
	@Override
	public List<Group> search(
		long companyId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end) {

		return search(
			companyId, getClassNameIds(), GroupConstants.ANY_PARENT_GROUP_ID,
			name, description, params, andOperator, start, end, null);
	}

	/**
	 * Returns an ordered range of all the site groups and organization groups
	 * that match the name and description, optionally including the user's
	 * inherited organization groups and user groups. System and staged groups
	 * are not included.
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
	 * @param  companyId the primary key of the company
	 * @param  name the group's name (optionally <code>null</code>)
	 * @param  description the group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organizations and user groups in the
	 *         search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the groups (optionally
	 *         <code>null</code>)
	 * @return the matching groups ordered by comparator
	 *         <code>orderByComparator</code>
	 */
	@Override
	public List<Group> search(
		long companyId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<Group> orderByComparator) {

		return search(
			companyId, getClassNameIds(), GroupConstants.ANY_PARENT_GROUP_ID,
			name, description, params, andOperator, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of groups belonging to the parent group that match the
	 * keywords, optionally including the user's inherited organization groups
	 * and user groups. System and staged groups are not included.
	 *
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         sites's name, or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organization groups and user groups
	 *         in the search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @return the number of matching groups
	 */
	@Override
	@ThreadLocalCachable
	public int searchCount(
		long companyId, long parentGroupId, String keywords,
		LinkedHashMap<String, Object> params) {

		return searchCount(
			companyId, getClassNameIds(), parentGroupId, keywords, params);
	}

	/**
	 * Returns the number of groups belonging to the parent group and immediate
	 * organization groups that match the name and description, optionally
	 * including the user's inherited organization groups and user groups.
	 * System and staged groups are not included.
	 *
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  name the group's name (optionally <code>null</code>)
	 * @param  description the group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organization groups and user groups
	 *         in the search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field.
	 * @return the number of matching groups
	 */
	@Override
	@ThreadLocalCachable
	public int searchCount(
		long companyId, long parentGroupId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator) {

		return searchCount(
			companyId, getClassNameIds(), parentGroupId, name, description,
			params, andOperator);
	}

	/**
	 * Returns the number of groups belonging to the parent group that match the
	 * class name IDs, and keywords, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
	 *
	 * @param  companyId the primary key of the company
	 * @param  classNameIds the primary keys of the class names of the entities
	 *         the groups are related to (optionally <code>null</code>)
	 * @param  parentGroupId the primary key of the parent group
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         sites's name, or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organization groups and user groups
	 *         in the search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @return the number of matching groups
	 */
	@Override
	@ThreadLocalCachable
	public int searchCount(
		long companyId, long[] classNameIds, long parentGroupId,
		String keywords, LinkedHashMap<String, Object> params) {

		String[] keywordsArray = getSearchNames(companyId, keywords);

		boolean andOperator = false;

		if (Validator.isNull(keywords)) {
			andOperator = true;
		}

		if (isUseComplexSQL(classNameIds)) {
			return groupFinder.countByC_C_PG_N_D(
				companyId, classNameIds, parentGroupId, keywordsArray,
				keywordsArray, params, andOperator);
		}

		Collection<Group> groups = doSearch(
			companyId, classNameIds, parentGroupId, keywordsArray,
			keywordsArray, params, andOperator);

		return groups.size();
	}

	/**
	 * Returns the number of groups belonging to the parent group that match the
	 * class name IDs, name, and description, optionally including the user's
	 * inherited organization groups and user groups. System and staged groups
	 * are not included.
	 *
	 * @param  companyId the primary key of the company
	 * @param  classNameIds the primary keys of the class names of the entities
	 *         the groups are related to (optionally <code>null</code>)
	 * @param  parentGroupId the primary key of the parent group
	 * @param  name the group's name (optionally <code>null</code>)
	 * @param  description the group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organization groups and user groups
	 *         in the search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field.
	 * @return the number of matching groups
	 */
	@Override
	@ThreadLocalCachable
	public int searchCount(
		long companyId, long[] classNameIds, long parentGroupId, String name,
		String description, LinkedHashMap<String, Object> params,
		boolean andOperator) {

		String[] names = getSearchNames(companyId, name);
		String[] descriptions = CustomSQLUtil.keywords(description);

		if (isUseComplexSQL(classNameIds)) {
			return groupFinder.countByC_C_PG_N_D(
				companyId, classNameIds, parentGroupId, names, descriptions,
				params, andOperator);
		}

		Collection<Group> groups = doSearch(
			companyId, classNameIds, parentGroupId, names, descriptions, params,
			andOperator);

		return groups.size();
	}

	/**
	 * Returns the number of groups that match the class name IDs, and keywords,
	 * optionally including the user's inherited organization groups and user
	 * groups. System and staged groups are not included.
	 *
	 * @param  companyId the primary key of the company
	 * @param  classNameIds the primary keys of the class names of the entities
	 *         the groups are related to (optionally <code>null</code>)
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         sites's name, or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organization groups and user groups
	 *         in the search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @return the number of matching groups
	 */
	@Override
	@ThreadLocalCachable
	public int searchCount(
		long companyId, long[] classNameIds, String keywords,
		LinkedHashMap<String, Object> params) {

		return searchCount(
			companyId, classNameIds, GroupConstants.ANY_PARENT_GROUP_ID,
			keywords, params);
	}

	/**
	 * Returns the number of groups that match the class name IDs, name, and
	 * description, optionally including the user's inherited organization
	 * groups and user groups. System and staged groups are not included.
	 *
	 * @param  companyId the primary key of the company
	 * @param  classNameIds the primary keys of the class names of the entities
	 *         the groups are related to (optionally <code>null</code>)
	 * @param  name the group's name (optionally <code>null</code>)
	 * @param  description the group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organization groups and user groups
	 *         in the search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field.
	 * @return the number of matching groups
	 */
	@Override
	@ThreadLocalCachable
	public int searchCount(
		long companyId, long[] classNameIds, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator) {

		return searchCount(
			companyId, classNameIds, GroupConstants.ANY_PARENT_GROUP_ID, name,
			description, params, andOperator);
	}

	/**
	 * Returns the number of groups that match the keywords, optionally
	 * including the user's inherited organization groups and user groups.
	 * System and staged groups are not included.
	 *
	 * @param  companyId the primary key of the company
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         sites's name, or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organization groups and user groups
	 *         in the search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @return the number of matching groups
	 */
	@Override
	@ThreadLocalCachable
	public int searchCount(
		long companyId, String keywords, LinkedHashMap<String, Object> params) {

		return searchCount(
			companyId, getClassNameIds(), GroupConstants.ANY_PARENT_GROUP_ID,
			keywords, params);
	}

	/**
	 * Returns the number of groups and immediate organization groups that match
	 * the name and description, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the group's name (optionally <code>null</code>)
	 * @param  description the group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organization groups and user groups
	 *         in the search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field.
	 * @return the number of matching groups
	 */
	@Override
	@ThreadLocalCachable
	public int searchCount(
		long companyId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator) {

		return searchCount(
			companyId, getClassNameIds(), GroupConstants.ANY_PARENT_GROUP_ID,
			name, description, params, andOperator);
	}

	/**
	 * Removes the groups from the role.
	 *
	 * @param roleId the primary key of the role
	 * @param groupIds the primary keys of the groups
	 */
	@Override
	public void unsetRoleGroups(long roleId, long[] groupIds) {
		_rolePersistence.removeGroups(roleId, groupIds);
	}

	/**
	 * Removes the user from the groups.
	 *
	 * @param userId the primary key of the user
	 * @param groupIds the primary keys of the groups
	 */
	@Override
	public void unsetUserGroups(long userId, long[] groupIds) {
		_userGroupRoleLocalService.deleteUserGroupRoles(userId, groupIds);

		_userPersistence.removeGroups(userId, groupIds);
	}

	/**
	 * Updates the group's asset replacing categories and tag names.
	 *
	 * @param  userId the primary key of the user
	 * @param  group the group
	 * @param  assetCategoryIds the primary keys of the asset categories
	 *         (optionally <code>null</code>)
	 * @param  assetTagNames the asset tag names (optionally <code>null</code>)
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void updateAsset(
			long userId, Group group, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		Company company = _companyPersistence.findByPrimaryKey(
			user.getCompanyId());

		Group companyGroup = company.getGroup();

		_assetEntryLocalService.updateEntry(
			userId, companyGroup.getGroupId(), null, null,
			Group.class.getName(), group.getGroupId(), null, 0,
			assetCategoryIds, assetTagNames, true, false, null, null, null,
			null, null, group.getDescriptiveName(), group.getDescription(),
			null, null, null, 0, 0, null);
	}

	/**
	 * Updates the group's friendly URL.
	 *
	 * @param  groupId the primary key of the group
	 * @param  friendlyURL the group's new friendlyURL (optionally
	 *         <code>null</code>)
	 * @return the group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group updateFriendlyURL(long groupId, String friendlyURL)
		throws PortalException {

		Group group = groupPersistence.findByPrimaryKey(groupId);

		if (group.isUser()) {
			User user = _userPersistence.findByPrimaryKey(group.getClassPK());

			friendlyURL = StringPool.SLASH + user.getScreenName();

			if (friendlyURL.equals(group.getFriendlyURL())) {
				return group;
			}
		}

		friendlyURL = getFriendlyURL(
			group.getCompanyId(), groupId, group.getClassNameId(),
			group.getClassPK(), StringPool.BLANK, friendlyURL);

		if (group.isUser()) {
			friendlyURL = getValidatedFriendlyURL(
				group.getCompanyId(), group.getGroupId(),
				group.getClassNameId(), group.getClassPK(), friendlyURL);
		}
		else {
			validateFriendlyURL(
				group.getCompanyId(), group.getGroupId(),
				group.getClassNameId(), group.getClassPK(), friendlyURL);
		}

		group.setFriendlyURL(friendlyURL);

		return groupPersistence.update(group);
	}

	@Override
	public Group updateGroup(
			long groupId, long parentGroupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean inheritContent, boolean active,
			ServiceContext serviceContext)
		throws PortalException {

		List<String> names = new ArrayList<>(nameMap.values());

		if (ListUtil.isNull(names)) {
			throw new GroupKeyException();
		}

		Group group = groupPersistence.findByPrimaryKey(groupId);

		String className = group.getClassName();
		long classNameId = group.getClassNameId();
		long classPK = group.getClassPK();

		String groupKey = group.getGroupKey();

		if (nameMap != null) {
			nameMap = _normalizeNameMap(nameMap);

			if (Validator.isNotNull(
					nameMap.get(
						LocaleUtil.fromLanguageId(
							group.getDefaultLanguageId())))) {

				groupKey = nameMap.get(
					LocaleUtil.fromLanguageId(group.getDefaultLanguageId()));
			}
		}

		friendlyURL = getFriendlyURL(
			group.getCompanyId(), groupId, classNameId, classPK,
			StringPool.BLANK, friendlyURL);

		if ((classNameId <= 0) || (type == GroupConstants.TYPE_DEPOT) ||
			className.equals(Group.class.getName())) {

			validateGroupKey(
				group.getGroupId(), group.getCompanyId(), groupKey,
				group.getType(), group.isSite());
		}
		else if (className.equals(Organization.class.getName())) {
			Organization organization =
				_organizationPersistence.findByPrimaryKey(classPK);

			groupKey = getOrgGroupName(organization.getName());
		}
		else if (!GroupConstants.USER_PERSONAL_SITE.equals(
					group.getGroupKey())) {

			groupKey = String.valueOf(classPK);
		}

		if (PortalUtil.isSystemGroup(group.getGroupKey()) &&
			!groupKey.equals(group.getGroupKey())) {

			throw new RequiredGroupException.MustNotDeleteSystemGroup(
				group.getGroupId());
		}

		if (group.isUser()) {
			friendlyURL = getValidatedFriendlyURL(
				group.getCompanyId(), group.getGroupId(),
				group.getClassNameId(), group.getClassPK(), friendlyURL);
		}
		else {
			validateFriendlyURL(
				group.getCompanyId(), group.getGroupId(),
				group.getClassNameId(), group.getClassPK(), friendlyURL);
		}

		validateParentGroup(group.getGroupId(), parentGroupId);

		group.setParentGroupId(parentGroupId);
		group.setTreePath(group.buildTreePath());
		group.setGroupKey(groupKey);

		Locale defaultLocale = LocaleUtil.fromLanguageId(
			group.getDefaultLanguageId());

		group.setNameMap(nameMap, defaultLocale);
		group.setDescriptionMap(descriptionMap, defaultLocale);

		group.setType(type);
		group.setManualMembership(manualMembership);
		group.setMembershipRestriction(membershipRestriction);
		group.setFriendlyURL(friendlyURL);
		group.setInheritContent(inheritContent);

		if (group.isActive() != active) {
			group.setActive(active);

			long companyId = group.getCompanyId();

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					reindex(companyId, getUserPrimaryKeys(groupId));

					return null;
				});
		}

		if ((serviceContext != null) && group.isSite()) {
			group.setExpandoBridgeAttributes(serviceContext);
		}

		group = groupPersistence.update(group);

		if (group.hasStagingGroup() && !group.isStagedRemotely()) {
			Group stagingGroup = group.getStagingGroup();

			stagingGroup.setParentGroupId(group.getParentGroupId());
			stagingGroup.setTreePath(stagingGroup.buildTreePath());

			groupPersistence.update(stagingGroup);
		}

		// Asset

		if ((serviceContext == null) || !group.isSite()) {
			return group;
		}

		User user = _userPersistence.fetchByPrimaryKey(
			group.getCreatorUserId());

		if (user == null) {
			user = _userPersistence.fetchByPrimaryKey(
				serviceContext.getUserId());
		}

		if (user == null) {
			user = _userLocalService.getGuestUser(group.getCompanyId());
		}

		updateAsset(
			user.getUserId(), group, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames());

		return group;
	}

	/**
	 * Updates the group's type settings.
	 *
	 * @param  groupId the primary key of the group
	 * @param  typeSettings the group's new type settings (optionally
	 *         <code>null</code>)
	 * @return the group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group updateGroup(long groupId, String typeSettings)
		throws PortalException {

		Group group = groupPersistence.findByPrimaryKey(groupId);

		UnicodeProperties oldTypeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.create(
				true
			).fastLoad(
				group.getTypeSettings()
			).build();

		_validateGroupKeyChange(groupId, typeSettings);

		group = groupPersistence.findByPrimaryKey(groupId);

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.create(
				true
			).fastLoad(
				typeSettings
			).build();

		boolean inheritLocales = GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.getProperty(
				GroupConstants.TYPE_SETTINGS_KEY_INHERIT_LOCALES),
			true);

		if (inheritLocales) {
			typeSettingsUnicodeProperties.setProperty(
				PropsKeys.LOCALES,
				StringUtil.merge(
					LocaleUtil.toLanguageIds(
						LanguageUtil.getAvailableLocales())));
		}

		String newLanguageIds = typeSettingsUnicodeProperties.getProperty(
			PropsKeys.LOCALES);

		if (Validator.isNotNull(newLanguageIds)) {
			Group companyGroup = getCompanyGroup(group.getCompanyId());

			group = groupPersistence.findByPrimaryKey(groupId);

			String oldLanguageIds =
				oldTypeSettingsUnicodeProperties.getProperty(
					PropsKeys.LOCALES, StringPool.BLANK);

			String defaultLanguageId =
				typeSettingsUnicodeProperties.getProperty(
					"languageId",
					LocaleUtil.toLanguageId(LocaleUtil.getDefault()));

			validateLanguageIds(
				inheritLocales, companyGroup.getGroupId(), defaultLanguageId,
				newLanguageIds);

			if (!Objects.equals(
					group.getDefaultLanguageId(), defaultLanguageId)) {

				Locale defaultLocale = LocaleUtil.fromLanguageId(
					defaultLanguageId);

				Map<Locale, String> oldNameMap = group.getNameMap();

				group.setNameMap(oldNameMap, defaultLocale);

				Map<Locale, String> oldDescriptionMap =
					group.getDescriptionMap();

				group.setDescriptionMap(oldDescriptionMap, defaultLocale);

				Map<Locale, String> nameMap = group.getNameMap();

				if ((nameMap != null) &&
					Validator.isNotNull(nameMap.get(defaultLocale)) &&
					((group.getClassNameId() <= 0) ||
					 Objects.equals(
						 group.getClassName(), Group.class.getName()) ||
					 (group.getType() == GroupConstants.TYPE_DEPOT))) {

					group.setGroupKey(nameMap.get(defaultLocale));
				}
			}

			if (!Objects.equals(oldLanguageIds, newLanguageIds)) {
				LanguageUtil.resetAvailableGroupLocales(groupId);
			}
		}

		group.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		return groupPersistence.update(group);
	}

	/**
	 * Associates the group with a main site if the group is an organization.
	 *
	 * @param  groupId the primary key of the group
	 * @param  site whether the group is to be associated with a main site
	 * @return the group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group updateSite(long groupId, boolean site) throws PortalException {
		Group group = groupPersistence.findByPrimaryKey(groupId);

		if (!group.isOrganization()) {
			return group;
		}

		group.setSite(site);

		return groupPersistence.update(group);
	}

	@Override
	public void validateRemote(
			long groupId, String remoteAddress, int remotePort,
			String remotePathContext, boolean secureConnection,
			long remoteGroupId)
		throws PortalException {

		RemoteOptionsException remoteOptionsException = null;

		if (!Validator.isDomain(remoteAddress) &&
			!Validator.isIPAddress(remoteAddress)) {

			remoteOptionsException = new RemoteOptionsException(
				RemoteOptionsException.REMOTE_ADDRESS);

			remoteOptionsException.setRemoteAddress(remoteAddress);

			throw remoteOptionsException;
		}

		if ((remotePort < 1) || (remotePort > 65535)) {
			remoteOptionsException = new RemoteOptionsException(
				RemoteOptionsException.REMOTE_PORT);

			remoteOptionsException.setRemotePort(remotePort);

			throw remoteOptionsException;
		}

		if (Validator.isNotNull(remotePathContext) &&
			(!remotePathContext.startsWith(StringPool.FORWARD_SLASH) ||
			 remotePathContext.endsWith(StringPool.FORWARD_SLASH))) {

			remoteOptionsException = new RemoteOptionsException(
				RemoteOptionsException.REMOTE_PATH_CONTEXT);

			remoteOptionsException.setRemotePathContext(remotePathContext);

			throw remoteOptionsException;
		}

		validateRemoteGroup(
			groupId, remoteGroupId, remoteAddress, remotePort,
			remotePathContext, secureConnection);
	}

	protected void addControlPanelLayouts(Group group) throws PortalException {
		long guestUserId = _userLocalService.getGuestUserId(
			group.getCompanyId());

		String friendlyURL = getFriendlyURL(
			PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);

		_layoutLocalService.addLayout(
			null, guestUserId, group.getGroupId(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			PropsValues.CONTROL_PANEL_LAYOUT_NAME, StringPool.BLANK,
			StringPool.BLANK, LayoutConstants.TYPE_CONTROL_PANEL, false,
			friendlyURL, serviceContext);
	}

	protected void addDefaultGuestPublicLayouts(Group group)
		throws PortalException {

		if (publicLARFile != null) {
			addDefaultGuestPublicLayoutsByLAR(group, publicLARFile);
		}
	}

	protected void addDefaultGuestPublicLayoutsByLAR(Group group, File larFile)
		throws PortalException {

		User guestUser = _userLocalService.getGuestUser(group.getCompanyId());

		Map<String, Serializable> importLayoutSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildImportLayoutSettingsMap(
					guestUser, group.getGroupId(), false, null,
					HashMapBuilder.put(
						PortletDataHandlerKeys.PERMISSIONS,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_CONFIGURATION,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_DATA,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT,
						new String[] {Boolean.TRUE.toString()}
					).build());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					guestUser.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					importLayoutSettingsMap);

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, larFile);
	}

	protected void addPortletDefaultData(Group group) throws PortalException {
		PortletDataContext portletDataContext =
			PortletDataContextFactoryUtil.createPreparePortletDataContext(
				group.getCompanyId(), group.getGroupId(), null, null);

		List<PortletDataHandler> portletDataHandlers = getPortletDataHandlers(
			group);

		for (PortletDataHandler portletDataHandler : portletDataHandlers) {
			try {
				portletDataHandler.addDefaultData(
					portletDataContext, portletDataHandler.getPortletId(),
					null);
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Unable to add default data for portlet ",
						portletDataHandler.getPortletId(), " in group ",
						group.getGroupId()));

				if (portletDataHandler.isRollbackOnException()) {
					throw new SystemException(exception);
				}
			}
		}
	}

	protected void deletePortletData(Group group) throws PortalException {
		PortletDataContext portletDataContext =
			PortletDataContextFactoryUtil.createPreparePortletDataContext(
				group.getCompanyId(), group.getGroupId(), null, null);

		List<PortletDataHandler> portletDataHandlers = getPortletDataHandlers(
			group);

		for (PortletDataHandler portletDataHandler : portletDataHandlers) {
			try {
				portletDataHandler.deleteData(
					portletDataContext, portletDataHandler.getPortletId(),
					null);
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Unable to delete data for portlet ",
						portletDataHandler.getPortletId(), " in group ",
						group.getGroupId()),
					exception);

				if (portletDataHandler.isRollbackOnException()) {
					throw exception;
				}
			}
		}
	}

	protected Collection<Group> doSearch(
		long companyId, long[] classNameIds, long parentGroupId, String[] names,
		String[] descriptions, LinkedHashMap<String, Object> params,
		boolean andOperator) {

		boolean parentGroupIdEquals = true;

		if (parentGroupId == GroupConstants.ANY_PARENT_GROUP_ID) {
			parentGroupIdEquals = false;
		}

		params = new LinkedHashMap<>(params);

		Boolean active = (Boolean)params.remove("active");
		List<Long> excludedGroupIds = (List<Long>)params.remove(
			"excludedGroupIds");
		List<Group> groupsTree = (List<Group>)params.remove("groupsTree");
		Boolean manualMembership = (Boolean)params.remove("manualMembership");
		Integer membershipRestriction = (Integer)params.remove(
			"membershipRestriction");
		Boolean site = (Boolean)params.remove("site");
		List<Integer> types = (List<Integer>)params.remove("types");

		Collection<Group> groups = new HashSet<>();

		Long userId = (Long)params.remove("usersGroups");

		for (long classNameId : classNameIds) {
			if (site != null) {
				groups.addAll(
					groupPersistence.findByC_C_S(companyId, classNameId, site));
			}
			else {
				groups.addAll(
					groupPersistence.findByC_C(companyId, classNameId));
			}
		}

		Iterator<Group> iterator = groups.iterator();

		while (iterator.hasNext()) {
			Group group = iterator.next();

			// Filter by live group ID

			long liveGroupId = group.getLiveGroupId();

			if (liveGroupId != 0) {
				iterator.remove();

				continue;
			}

			// Filter by parent group ID

			long groupParentGroupId = group.getParentGroupId();

			if (parentGroupIdEquals && (groupParentGroupId != parentGroupId)) {
				iterator.remove();

				continue;
			}

			// Filter by name and description

			String groupKey = group.getGroupKey();

			if (groupKey.equals(GroupConstants.CONTROL_PANEL)) {
				iterator.remove();

				continue;
			}

			boolean containsName = matches(group.getNameCurrentValue(), names);

			if (!containsName) {
				AssetEntry assetEntry = _assetEntryPersistence.fetchByC_C(
					group.getClassNameId(), group.getGroupId());

				if (assetEntry != null) {
					containsName = matches(assetEntry.getTitle(), names);
				}
			}

			boolean containsDescription = matches(
				group.getDescriptionCurrentValue(), descriptions);

			if ((andOperator && (!containsName || !containsDescription)) ||
				(!andOperator && !containsName && !containsDescription)) {

				iterator.remove();

				continue;
			}

			// Filter by active

			if ((active != null) && (active != isLiveGroupActive(group))) {
				iterator.remove();

				continue;
			}

			// Filter by excluded group IDs

			if ((excludedGroupIds != null) &&
				excludedGroupIds.contains(group.getGroupId())) {

				iterator.remove();

				continue;
			}

			// Filter by groups tree

			if (groupsTree != null) {
				String treePath = group.getTreePath();

				boolean matched = false;

				for (Group groupTree : groupsTree) {
					String groupTreePath = StringUtil.quote(
						String.valueOf(groupTree.getGroupId()),
						StringPool.SLASH);

					if (treePath.contains(groupTreePath)) {
						matched = true;

						break;
					}
				}

				if (!matched) {
					iterator.remove();

					continue;
				}
			}

			// Filter by manual membership

			if ((manualMembership != null) &&
				(manualMembership != group.isManualMembership())) {

				iterator.remove();

				continue;
			}

			// Filter by membership restriction

			if ((membershipRestriction != null) &&
				(membershipRestriction != group.getMembershipRestriction())) {

				iterator.remove();

				continue;
			}

			// Filter by type and types

			int type = group.getType();

			if (type == 4) {
				iterator.remove();

				continue;
			}

			if ((types != null) && !types.contains(type)) {
				iterator.remove();
			}
		}

		// Join by role permissions

		RolePermissions rolePermissions = (RolePermissions)params.remove(
			"rolePermissions");

		if (rolePermissions != null) {
			ResourceAction resourceAction =
				_resourceActionLocalService.fetchResourceAction(
					rolePermissions.getName(), rolePermissions.getActionId());

			if (resourceAction != null) {
				Set<Group> rolePermissionsGroups = new HashSet<>();

				List<ResourcePermission> resourcePermissions =
					_resourcePermissionPersistence.findByC_N_S_R(
						companyId, rolePermissions.getName(),
						rolePermissions.getScope(),
						rolePermissions.getRoleId());

				for (ResourcePermission resourcePermission :
						resourcePermissions) {

					if (resourcePermission.hasAction(resourceAction)) {
						Group group = groupPersistence.fetchByPrimaryKey(
							GetterUtil.getLong(
								resourcePermission.getPrimKey()));

						if (group != null) {
							rolePermissionsGroups.add(group);
						}
					}
				}

				groups.retainAll(rolePermissionsGroups);
			}
		}

		// Join by Groups_Roles

		Long roleId = (Long)params.remove("groupsRoles");

		if (roleId != null) {
			groups.retainAll(_rolePersistence.getGroups(roleId));
		}

		String actionId = (String)params.remove("actionId");

		if (userId == null) {
			if (actionId != null) {
				return _filterGroups(actionId, groups);
			}

			return groups;
		}

		// Join by Users_Groups

		Set<Group> joinedGroups = new HashSet<>(
			_userPersistence.getGroups(userId));

		boolean inherit = GetterUtil.getBoolean(params.remove("inherit"), true);

		if (inherit) {

			// Join by Users_Orgs

			List<Organization> organizations =
				_userPersistence.getOrganizations(userId);

			for (Organization organization : organizations) {
				for (Group group : groups) {
					long classPK = group.getClassPK();

					if (organization.getOrganizationId() == classPK) {
						joinedGroups.add(group);
					}
					else if (!PropsValues.ORGANIZATIONS_MEMBERSHIP_STRICT) {
						String treePath = organization.getTreePath();

						if (treePath.contains(String.valueOf(classPK))) {
							joinedGroups.add(group);
						}
					}
				}

				// Join by Groups_Orgs and Users_Orgs

				joinedGroups.addAll(
					_organizationPersistence.getGroups(
						organization.getOrganizationId()));
			}

			// Join by Groups_UserGroups and Users_UserGroups

			long[] userGroupIds = _userPersistence.getUserGroupPrimaryKeys(
				userId);

			for (long userGroupId : userGroupIds) {
				joinedGroups.addAll(
					_userGroupPersistence.getGroups(userGroupId));
			}
		}

		if (actionId != null) {
			joinedGroups.addAll(_filterGroups(actionId, groups));
		}

		if (_log.isDebugEnabled() && !params.isEmpty()) {
			_log.debug("Unprocessed parameters " + MapUtil.toString(params));
		}

		joinedGroups.retainAll(groups);

		return joinedGroups;
	}

	protected long[] getClassNameIds() {
		if (_classNameIdsSupplier == null) {
			_classNameIdsSupplier =
				_classNameLocalService.getClassNameIdsSupplier(
					new String[] {
						Group.class.getName(), Organization.class.getName()
					});
		}

		return _classNameIdsSupplier.get();
	}

	protected String getFriendlyURL(
			long companyId, long groupId, long classNameId, long classPK,
			String friendlyName, String friendlyURL)
		throws PortalException {

		friendlyURL = getFriendlyURL(friendlyURL);

		if (Validator.isNotNull(friendlyURL)) {
			return friendlyURL;
		}

		String safeFriendlyName = StringUtil.removeChars(
			friendlyName, CharPool.PERCENT);

		friendlyURL = StringPool.SLASH + getFriendlyURL(safeFriendlyName);

		return getValidatedFriendlyURL(
			companyId, groupId, classNameId, classPK, friendlyURL);
	}

	protected String getFriendlyURL(String friendlyURL) {
		return FriendlyURLNormalizerUtil.normalizeWithEncoding(friendlyURL);
	}

	protected String getOrgGroupName(String name) {
		return name + ORGANIZATION_NAME_SUFFIX;
	}

	protected List<PortletDataHandler> getPortletDataHandlers(Group group) {
		List<Portlet> portlets = _portletLocalService.getPortlets(
			group.getCompanyId());

		List<PortletDataHandler> portletDataHandlers = new ArrayList<>(
			portlets.size());

		for (Portlet portlet : portlets) {
			if (!portlet.isActive()) {
				continue;
			}

			PortletDataHandler portletDataHandler =
				portlet.getPortletDataHandlerInstance();

			if ((portletDataHandler != null) &&
				!portletDataHandler.isDataPortalLevel()) {

				portletDataHandlers.add(portletDataHandler);
			}
		}

		return portletDataHandlers;
	}

	protected String[] getSearchNames(long companyId, String name) {
		if (Validator.isNull(name)) {
			return new String[] {null};
		}

		Company company = _companyPersistence.fetchByPrimaryKey(companyId);

		if (company == null) {
			return CustomSQLUtil.keywords(name);
		}

		if (StringUtil.wildcardMatches(
				company.getName(), name, CharPool.UNDERLINE, CharPool.PERCENT,
				CharPool.BACK_SLASH, false)) {

			String[] searchNames = CustomSQLUtil.keywords(name);

			String guestName = StringUtil.quote(
				StringUtil.toLowerCase(GroupConstants.GUEST),
				StringPool.PERCENT);

			return ArrayUtil.append(searchNames, guestName);
		}

		return CustomSQLUtil.keywords(name);
	}

	protected String getValidatedFriendlyURL(
			long companyId, long groupId, long classNameId, long classPK,
			String friendlyURL)
		throws PortalException {

		int i = 0;

		while (true) {
			try {
				validateFriendlyURL(
					companyId, groupId, classNameId, classPK, friendlyURL);

				break;
			}
			catch (GroupFriendlyURLException groupFriendlyURLException) {
				int type = groupFriendlyURLException.getType();

				if (type == GroupFriendlyURLException.DUPLICATE) {
					if (friendlyURL.matches(".+-[0-9]+$")) {
						int end = friendlyURL.lastIndexOf(CharPool.DASH);

						long suffix = GetterUtil.getLong(
							friendlyURL.substring(end + 1));

						if (!(friendlyURL.contains("group") &&
							  (groupId == suffix))) {

							friendlyURL = friendlyURL.substring(0, end);
						}
					}

					if (StringUtil.endsWith(friendlyURL, CharPool.DASH)) {
						friendlyURL = friendlyURL + ++i;
					}
					else {
						friendlyURL = friendlyURL + CharPool.DASH + ++i;
					}
				}
				else if (type == GroupFriendlyURLException.ENDS_WITH_DASH) {
					friendlyURL = StringUtil.replaceLast(
						friendlyURL, CharPool.DASH, StringPool.BLANK);
				}
				else {
					friendlyURL = "/group-" + classPK;
				}
			}
		}

		return friendlyURL;
	}

	protected void initImportLARFile() {
		String publicLARFileName = PropsValues.DEFAULT_GUEST_PUBLIC_LAYOUTS_LAR;

		if (_log.isDebugEnabled()) {
			_log.debug("Reading public LAR file " + publicLARFileName);
		}

		if (Validator.isNotNull(publicLARFileName)) {
			publicLARFile = new File(publicLARFileName);

			if (!publicLARFile.exists()) {
				_log.error(
					"Public LAR file " + publicLARFile + " does not exist");

				publicLARFile = null;
			}
			else {
				if (_log.isDebugEnabled()) {
					_log.debug("Using public LAR file " + publicLARFileName);
				}
			}
		}
	}

	protected void initUserPersonalSitePermissions(Group group)
		throws PortalException {

		// User role

		Role role = _roleLocalService.getRole(
			group.getCompanyId(), RoleConstants.USER);

		setRolePermissions(
			group, role, Layout.class.getName(),
			new String[] {ActionKeys.VIEW});

		// Power User role

		role = _roleLocalService.getRole(
			group.getCompanyId(), RoleConstants.POWER_USER);

		setRolePermissions(
			group, role, Group.class.getName(),
			new String[] {
				ActionKeys.MANAGE_LAYOUTS, ActionKeys.VIEW_SITE_ADMINISTRATION
			});
	}

	protected boolean isCompanyGroup(HttpPrincipal httpPrincipal, Group group) {
		ClassName className = ClassNameServiceHttp.fetchByClassNameId(
			httpPrincipal, group.getClassNameId());

		return Objects.equals(className.getValue(), Company.class.getName());
	}

	protected boolean isParentGroup(long parentGroupId, long groupId)
		throws PortalException {

		// Return true if parentGroupId is among the parent groups of groupId

		if (groupId == GroupConstants.DEFAULT_PARENT_GROUP_ID) {
			return false;
		}

		Group group = groupPersistence.findByPrimaryKey(groupId);

		String treePath = group.getTreePath();

		return treePath.contains(
			StringPool.SLASH + parentGroupId + StringPool.SLASH);
	}

	protected boolean isStaging(ServiceContext serviceContext) {
		if (serviceContext != null) {
			return ParamUtil.getBoolean(serviceContext, "staging");
		}

		return false;
	}

	protected boolean isUseComplexSQL(long[] classNameIds) {
		if (ArrayUtil.isEmpty(classNameIds)) {
			return true;
		}

		if (_complexSQLClassNameIdsSupplier == null) {
			_complexSQLClassNameIdsSupplier =
				_classNameLocalService.getClassNameIdsSupplier(
					PropsValues.GROUPS_COMPLEX_SQL_CLASS_NAMES);
		}

		for (long classNameId : classNameIds) {
			if (ArrayUtil.contains(
					_complexSQLClassNameIdsSupplier.get(), classNameId)) {

				return true;
			}
		}

		return false;
	}

	protected boolean matches(String s, String[] keywords) {
		if ((keywords == null) ||
			((keywords.length == 1) && (keywords[0] == null))) {

			return true;
		}

		for (String keyword : keywords) {
			if (StringUtil.wildcardMatches(
					s, keyword, CharPool.UNDERLINE, CharPool.PERCENT,
					CharPool.BACK_SLASH, false)) {

				return true;
			}
		}

		return false;
	}

	protected void reindex(long companyId, long[] userIds)
		throws PortalException {

		ReindexerBridge reindexerBridge = _reindexerBridgeSnapshot.get();

		reindexerBridge.reindex(companyId, User.class.getName(), userIds);
	}

	protected void reindexUserGroup(long userGroupId) throws PortalException {
		Indexer<UserGroup> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			UserGroup.class);

		indexer.reindex(_userGroupLocalService.getUserGroup(userGroupId));
	}

	protected void reindexUsersInOrganization(long organizationId)
		throws PortalException {

		Organization organization = _organizationLocalService.getOrganization(
			organizationId);

		long[] userIds = _organizationLocalService.getUserPrimaryKeys(
			organizationId);

		if (ArrayUtil.isNotEmpty(userIds)) {
			long companyId = organization.getCompanyId();

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					reindex(companyId, userIds);

					return null;
				});
		}
	}

	protected void reindexUsersInUserGroup(long userGroupId)
		throws PortalException {

		UserGroup userGroup = _userGroupLocalService.getUserGroup(userGroupId);

		long[] userIds = _organizationLocalService.getUserPrimaryKeys(
			userGroupId);

		if (ArrayUtil.isNotEmpty(userIds)) {
			long companyId = userGroup.getCompanyId();

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					reindex(companyId, userIds);
					reindexUserGroup(userGroupId);

					return null;
				});
		}
	}

	protected void setCompanyPermissions(
			Role role, String name, String[] actionIds)
		throws PortalException {

		_resourcePermissionLocalService.setResourcePermissions(
			role.getCompanyId(), name, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(role.getCompanyId()), role.getRoleId(), actionIds);
	}

	protected void setRolePermissions(Group group, Role role, String name)
		throws PortalException {

		List<String> actions = ResourceActionsUtil.getModelResourceActions(
			name);

		setRolePermissions(group, role, name, actions.toArray(new String[0]));
	}

	protected void setRolePermissions(
			Group group, Role role, String name, String[] actionIds)
		throws PortalException {

		_resourcePermissionLocalService.setResourcePermissions(
			group.getCompanyId(), name, ResourceConstants.SCOPE_GROUP,
			String.valueOf(group.getGroupId()), role.getRoleId(), actionIds);
	}

	protected List<Group> sort(
		Collection<Group> groups, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		if (orderByComparator == null) {
			orderByComparator = new GroupNameComparator(true);
		}

		List<Group> groupList = null;

		if (groups instanceof List) {
			groupList = (List<Group>)groups;
		}
		else {
			groupList = new ArrayList<>(groups);
		}

		Collections.sort(groupList, orderByComparator);

		return Collections.unmodifiableList(
			ListUtil.subList(groupList, start, end));
	}

	protected void unscheduleStaging(Group group) {
		try {

			// Remote publishing

			String groupName = StagingUtil.getSchedulerGroupName(
				DestinationNames.LAYOUTS_REMOTE_PUBLISHER, group.getGroupId());

			SchedulerEngineHelperUtil.delete(groupName, StorageType.PERSISTED);

			long liveGroupId = 0;
			long stagingGroupId = 0;

			if (group.isStagingGroup()) {
				liveGroupId = group.getLiveGroupId();

				stagingGroupId = group.getGroupId();
			}
			else if (group.hasStagingGroup()) {
				liveGroupId = group.getGroupId();

				Group stagingGroup = group.getStagingGroup();

				stagingGroupId = stagingGroup.getGroupId();
			}

			if ((liveGroupId != 0) && (stagingGroupId != 0)) {

				// Publish to live

				groupName = StagingUtil.getSchedulerGroupName(
					DestinationNames.LAYOUTS_LOCAL_PUBLISHER, liveGroupId);

				SchedulerEngineHelperUtil.delete(
					groupName, StorageType.PERSISTED);

				// Copy from live

				groupName = StagingUtil.getSchedulerGroupName(
					DestinationNames.LAYOUTS_LOCAL_PUBLISHER, stagingGroupId);

				SchedulerEngineHelperUtil.delete(
					groupName, StorageType.PERSISTED);
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to unschedule events for group: " + group.getGroupId(),
				exception);
		}
	}

	protected void validateFriendlyURL(
			long companyId, long groupId, long classNameId, long classPK,
			String friendlyURL)
		throws PortalException {

		if (Validator.isNull(friendlyURL)) {
			return;
		}

		int exceptionType = LayoutImpl.validateFriendlyURL(friendlyURL);

		if (exceptionType != -1) {
			throw new GroupFriendlyURLException(exceptionType);
		}

		Group group = groupPersistence.fetchByC_F(companyId, friendlyURL);

		if ((group != null) && (group.getGroupId() != groupId)) {
			GroupFriendlyURLException groupFriendlyURLException =
				new GroupFriendlyURLException(
					GroupFriendlyURLException.DUPLICATE);

			groupFriendlyURLException.setDuplicateClassPK(group.getGroupId());
			groupFriendlyURLException.setDuplicateClassName(
				Group.class.getName());

			throw groupFriendlyURLException;
		}

		String groupIdFriendlyURL = friendlyURL.substring(1);

		if (Validator.isNumber(groupIdFriendlyURL)) {
			long groupClassNameId = _classNameLocalService.getClassNameId(
				Group.class);

			if (((classNameId != groupClassNameId) &&
				 !groupIdFriendlyURL.equals(String.valueOf(classPK)) &&
				 !PropsValues.USERS_SCREEN_NAME_ALLOW_NUMERIC) ||
				(classNameId == groupClassNameId)) {

				GroupFriendlyURLException groupFriendlyURLException =
					new GroupFriendlyURLException(
						GroupFriendlyURLException.POSSIBLE_DUPLICATE);

				groupFriendlyURLException.setKeywordConflict(
					groupIdFriendlyURL);

				throw groupFriendlyURLException;
			}
		}

		if (StringUtil.count(friendlyURL, CharPool.SLASH) > 1) {
			throw new GroupFriendlyURLException(
				GroupFriendlyURLException.TOO_DEEP);
		}

		if (StringUtil.endsWith(friendlyURL, CharPool.DASH)) {
			throw new GroupFriendlyURLException(
				GroupFriendlyURLException.ENDS_WITH_DASH);
		}

		if (StringUtil.equals(friendlyURL, "/.") ||
			StringUtil.equals(friendlyURL, "/..")) {

			throw new GroupFriendlyURLException(
				GroupFriendlyURLException.INVALID_CHARACTERS);
		}

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = StringUtil.toLowerCase(
				LocaleUtil.toLanguageId(locale));

			String i18nPathLanguageId =
				StringPool.SLASH +
					PortalUtil.getI18nPathLanguageId(locale, languageId);

			String underlineI18nPathLanguageId = StringUtil.replace(
				i18nPathLanguageId, CharPool.DASH, CharPool.UNDERLINE);

			if (friendlyURL.startsWith(i18nPathLanguageId + StringPool.SLASH) ||
				friendlyURL.startsWith(
					underlineI18nPathLanguageId + StringPool.SLASH) ||
				friendlyURL.startsWith(
					StringPool.SLASH + languageId + StringPool.SLASH) ||
				friendlyURL.equals(i18nPathLanguageId) ||
				friendlyURL.equals(underlineI18nPathLanguageId) ||
				friendlyURL.equals(StringPool.SLASH + languageId)) {

				GroupFriendlyURLException groupFriendlyURLException =
					new GroupFriendlyURLException(
						GroupFriendlyURLException.KEYWORD_CONFLICT);

				groupFriendlyURLException.setKeywordConflict(
					i18nPathLanguageId);

				throw groupFriendlyURLException;
			}
		}
	}

	protected void validateGroupKey(
			long groupId, long companyId, String groupKey, int type,
			boolean site)
		throws PortalException {

		int groupKeyMaxLength = ModelHintsUtil.getMaxLength(
			Group.class.getName(), "groupKey");

		if (Validator.isNull(groupKey) || Validator.isNumber(groupKey) ||
			groupKey.contains(StringPool.STAR) ||
			groupKey.contains(ORGANIZATION_NAME_SUFFIX) ||
			(groupKey.length() > groupKeyMaxLength)) {

			throw new GroupKeyException();
		}

		Group group = groupFinder.fetchByC_GK(companyId, groupKey);

		if ((group != null) &&
			((groupId <= 0) || (group.getGroupId() != groupId))) {

			throw new DuplicateGroupException(
				StringBundler.concat(
					"{companyId=", companyId, ", groupId=", groupId,
					", groupKey=", groupKey, "}"));
		}

		if (site || (type == GroupConstants.TYPE_DEPOT)) {
			try {
				Company company = _companyLocalService.getCompany(companyId);

				if (groupKey.equals(company.getName())) {
					throw new DuplicateGroupException();
				}
			}
			catch (NoSuchCompanyException noSuchCompanyException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchCompanyException);
				}
			}
		}
	}

	protected void validateInheritContent(
			long parentGroupId, boolean inheritContent)
		throws GroupInheritContentException {

		if (!inheritContent) {
			return;
		}

		if (parentGroupId == GroupConstants.DEFAULT_PARENT_GROUP_ID) {
			throw new GroupInheritContentException();
		}

		Group parentGroup = groupPersistence.fetchByPrimaryKey(parentGroupId);

		if (parentGroup.isInheritContent()) {
			throw new GroupInheritContentException();
		}
	}

	protected void validateLanguageIds(
			boolean inheritLocales, long groupId, String defaultLanguageId,
			String languageIds)
		throws PortalException {

		String[] languageIdsArray = StringUtil.split(languageIds);

		for (String languageId : languageIdsArray) {
			if (!inheritLocales &&
				!LanguageUtil.isAvailableLocale(groupId, languageId)) {

				LocaleException localeException = new LocaleException(
					LocaleException.TYPE_DISPLAY_SETTINGS);

				localeException.setSourceAvailableLocales(
					LanguageUtil.getAvailableLocales());
				localeException.setTargetAvailableLanguageIds(
					Arrays.asList(languageIdsArray));

				throw localeException;
			}
		}

		if (!ArrayUtil.contains(languageIdsArray, defaultLanguageId)) {
			LocaleException localeException = new LocaleException(
				LocaleException.TYPE_DEFAULT);

			localeException.setSourceAvailableLocales(
				LanguageUtil.getAvailableLocales());
			localeException.setTargetAvailableLanguageIds(
				Arrays.asList(languageIdsArray));

			throw localeException;
		}
	}

	protected void validateParentGroup(long groupId, long parentGroupId)
		throws PortalException {

		if (parentGroupId == GroupConstants.DEFAULT_PARENT_GROUP_ID) {
			return;
		}

		if (groupId == parentGroupId) {
			throw new GroupParentException.MustNotBeOwnParent(groupId);
		}

		Group group = groupPersistence.fetchByPrimaryKey(groupId);

		if (group == null) {
			return;
		}

		if ((groupId > 0) &&
			(parentGroupId != GroupConstants.DEFAULT_PARENT_GROUP_ID)) {

			// Prevent circular groupal references

			if (isParentGroup(groupId, parentGroupId)) {
				throw new GroupParentException.MustNotHaveChildParent(
					groupId, parentGroupId);
			}
		}

		if (group.isStagingGroup()) {
			Group parentGroup = groupPersistence.findByPrimaryKey(
				parentGroupId);

			Group stagingGroup = parentGroup.getStagingGroup();

			long stagingGroupId = stagingGroup.getGroupId();

			if (groupId == stagingGroupId) {
				throw new GroupParentException.MustNotHaveStagingParent(
					groupId, stagingGroupId);
			}
		}
	}

	protected void validateRemoteGroup(
			long groupId, long remoteGroupId, String remoteAddress,
			int remotePort, String remotePathContext, boolean secureConnection)
		throws PortalException {

		if (remoteGroupId <= 0) {
			RemoteOptionsException remoteOptionsException =
				new RemoteOptionsException(
					RemoteOptionsException.REMOTE_GROUP_ID);

			remoteOptionsException.setRemoteGroupId(remoteGroupId);

			throw remoteOptionsException;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		User user = permissionChecker.getUser();

		String remoteURL = StagingURLHelperUtil.buildRemoteURL(
			remoteAddress, remotePort, remotePathContext, secureConnection);

		HttpPrincipal httpPrincipal = new HttpPrincipal(
			remoteURL, user.getLogin(), user.getPassword(),
			user.isPasswordEncrypted());

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				PortalClassLoaderUtil.getClassLoader())) {

			// Ping the remote host and verify that the remote group exists in
			// the same company as the remote user

			try {
				try {
					TunnelUtil.invoke(
						httpPrincipal,
						new MethodHandler(
							_checkRemoteStagingGroupMethodKey, remoteGroupId));
				}
				catch (Exception exception) {
					if (exception instanceof PortalException) {
						throw (PortalException)exception;
					}

					throw new SystemException(exception);
				}
			}
			catch (SystemException systemException) {
				if (systemException.getCause() instanceof ConnectException) {
					_log.error(
						"Unable to connect to remote live: " +
							systemException.getMessage());

					if (_log.isDebugEnabled()) {
						_log.debug(systemException);
					}
				}
				else {
					_log.error(systemException);
				}

				throw systemException;
			}

			// Ensure that the local group and the remote group are not the same
			// group and that they are either both company groups or both not
			// company groups

			Group group = groupLocalService.getGroup(groupId);

			Group remoteGroup = GroupServiceHttp.getGroup(
				httpPrincipal, remoteGroupId);

			if ((group.isCompany() ^
				 isCompanyGroup(httpPrincipal, remoteGroup)) ||
				(group.isDepot() ^ remoteGroup.isDepot())) {

				RemoteExportException remoteExportException =
					new RemoteExportException(
						RemoteExportException.INVALID_GROUP);

				remoteExportException.setGroupId(remoteGroupId);

				throw remoteExportException;
			}

			// Ensure that local staging is not enabled in the remote group

			boolean remoteStaged = GetterUtil.getBoolean(
				remoteGroup.getTypeSettingsProperty("staged"));
			boolean remoteStagedRemotely = GetterUtil.getBoolean(
				remoteGroup.getTypeSettingsProperty("stagedRemotely"));

			if (remoteStaged && !remoteStagedRemotely) {
				RemoteExportException remoteExportException =
					new RemoteExportException(
						RemoteExportException.INVALID_STATE,
						"Local staging is already enabled in remote group");

				remoteExportException.setGroupId(remoteGroupId);

				throw remoteExportException;
			}
		}
		catch (NoSuchGroupException noSuchGroupException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchGroupException);
			}

			RemoteExportException remoteExportException =
				new RemoteExportException(RemoteExportException.NO_GROUP);

			remoteExportException.setGroupId(remoteGroupId);

			throw remoteExportException;
		}
		catch (PrincipalException principalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			RemoteExportException remoteExportException =
				new RemoteExportException(RemoteExportException.NO_PERMISSIONS);

			remoteExportException.setGroupId(remoteGroupId);

			throw remoteExportException;
		}
		catch (RemoteAuthException remoteAuthException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(remoteAuthException);
			}

			remoteAuthException.setURL(remoteURL);

			throw remoteAuthException;
		}
		catch (SystemException systemException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(systemException);
			}

			RemoteExportException remoteExportException =
				new RemoteExportException(
					RemoteExportException.BAD_CONNECTION,
					systemException.getMessage());

			remoteExportException.setURL(remoteURL);

			throw remoteExportException;
		}
	}

	protected File publicLARFile;

	private static void _clearStagingGroupIds() {
		_doClearStagingGroupIds();

		if (!ClusterExecutorUtil.isEnabled()) {
			return;
		}

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				ClusterRequest clusterRequest =
					ClusterRequest.createMulticastRequest(
						_doClearStagingGroupIdsMethodHandler, true);

				clusterRequest.setFireAndForget(true);

				ClusterExecutorUtil.execute(clusterRequest);

				return null;
			});
	}

	private static void _doClearStagingGroupIds() {
		_stagingGroupIdsDCLSingleton.destroy(null);
	}

	private Collection<Group> _filterGroups(
		String actionId, Collection<Group> groups) {

		Collection<Group> filteredGroups = new HashSet<>();

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		for (Group group : groups) {
			try {
				if (group.isCompany() ||
					permissionChecker.isGroupAdmin(group.getGroupId()) ||
					GroupPermissionUtil.contains(
						permissionChecker, group.getGroupId(), actionId)) {

					filteredGroups.add(group);
				}
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to check permission for group " +
							group.getGroupId(),
						portalException);
				}
			}
		}

		return filteredGroups;
	}

	private String _getGroupKey(
		int counter, String groupKey, int groupKeyMaxLength,
		String stagingGroupKeySuffix) {

		String suffix = counter + stagingGroupKeySuffix;

		groupKey = groupKey.substring(0, groupKeyMaxLength - suffix.length());

		groupKey = groupKey.concat(suffix);

		return groupKey;
	}

	private Map<Long, Long> _getStagingGroupIds() {
		Session session = null;

		try {
			session = groupPersistence.openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				"select liveGroupId, groupId from Group_ where liveGroupId " +
					"!= 0" + GroupModelImpl.ORDER_BY_SQL);

			sqlQuery.addScalar("liveGroupId", Type.LONG);
			sqlQuery.addScalar("groupId", Type.LONG);

			List<Object[]> results = sqlQuery.list(false, false);

			if (results.isEmpty()) {
				return Collections.emptyMap();
			}

			Map<Long, Long> stagingGroupIds = new HashMap<>();

			for (Object[] result : results) {
				Long originalValue = stagingGroupIds.put(
					(Long)result[0], (Long)result[1]);

				if ((originalValue != null) && _log.isWarnEnabled()) {
					_log.warn(
						"More than one staging group uses live group ID " +
							result[0]);
				}
			}

			return stagingGroupIds;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			groupPersistence.closeSession(session);
		}
	}

	private Map<Locale, String> _normalizeNameMap(Map<Locale, String> nameMap) {
		Map<Locale, String> normalizedNameMap = new HashMap<>();

		for (Map.Entry<Locale, String> entry : nameMap.entrySet()) {
			String value = entry.getValue();

			if (Validator.isNotNull(value)) {
				normalizedNameMap.put(entry.getKey(), StringUtil.trim(value));
			}
		}

		return normalizedNameMap;
	}

	private String _toExternalReferenceCode(String groupKey) {
		return "L_" + TextFormatter.format(groupKey, TextFormatter.A);
	}

	private void _validateGroupKeyChange(long groupId, String typeSettings)
		throws PortalException {

		Group group = groupPersistence.findByPrimaryKey(groupId);

		if (!Objects.equals(group.getClassName(), Group.class.getName()) &&
			!group.isDepot()) {

			return;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.create(
				true
			).fastLoad(
				typeSettings
			).build();

		String defaultLanguageId = typeSettingsUnicodeProperties.getProperty(
			"languageId", LocaleUtil.toLanguageId(LocaleUtil.getDefault()));

		Locale defaultLocale = LocaleUtil.fromLanguageId(defaultLanguageId);

		Map<Locale, String> nameMap = group.getNameMap();

		if ((nameMap != null) &&
			Validator.isNotNull(nameMap.get(defaultLocale)) &&
			(group.getCompanyId() > 0)) {

			validateGroupKey(
				group.getGroupId(), group.getCompanyId(),
				nameMap.get(defaultLocale), group.getType(), group.isSite());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GroupLocalServiceImpl.class);

	private static volatile int _cacheableQueryLimitLPD28122 =
		GetterUtil.getInteger(PropsUtil.get("cacheable.query.limit.LPD-28122"));
	private static final MethodKey _checkRemoteStagingGroupMethodKey =
		new MethodKey(
			GroupServiceUtil.class, "checkRemoteStagingGroup",
			new Class<?>[] {long.class});
	private static final MethodHandler _doClearStagingGroupIdsMethodHandler =
		new MethodHandler(
			new MethodKey(
				GroupLocalServiceImpl.class, "_doClearStagingGroupIds"));
	private static final Snapshot<ReindexerBridge> _reindexerBridgeSnapshot =
		new Snapshot<>(GroupLocalServiceImpl.class, ReindexerBridge.class);
	private static final DCLSingleton<Map<Long, Long>>
		_stagingGroupIdsDCLSingleton = new DCLSingleton<>();

	@BeanReference(type = AssetEntryLocalService.class)
	private AssetEntryLocalService _assetEntryLocalService;

	@BeanReference(type = AssetEntryPersistence.class)
	private AssetEntryPersistence _assetEntryPersistence;

	@BeanReference(type = AssetTagLocalService.class)
	private AssetTagLocalService _assetTagLocalService;

	@BeanReference(type = AssetVocabularyLocalService.class)
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private volatile Supplier<long[]> _classNameIdsSupplier;

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = CompanyLocalService.class)
	private CompanyLocalService _companyLocalService;

	@BeanReference(type = CompanyPersistence.class)
	private CompanyPersistence _companyPersistence;

	private volatile Supplier<long[]> _complexSQLClassNameIdsSupplier;

	@BeanReference(type = DLAppLocalService.class)
	private DLAppLocalService _dlAppLocalService;

	@BeanReference(type = ExpandoRowLocalService.class)
	private ExpandoRowLocalService _expandoRowLocalService;

	@BeanReference(type = ExportImportConfigurationLocalService.class)
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@BeanReference(type = ExportImportLocalService.class)
	private ExportImportLocalService _exportImportLocalService;

	@BeanReference(type = LayoutLocalService.class)
	private LayoutLocalService _layoutLocalService;

	@BeanReference(type = LayoutPersistence.class)
	private LayoutPersistence _layoutPersistence;

	@BeanReference(type = LayoutSetBranchLocalService.class)
	private LayoutSetBranchLocalService _layoutSetBranchLocalService;

	@BeanReference(type = LayoutSetLocalService.class)
	private LayoutSetLocalService _layoutSetLocalService;

	@BeanReference(type = MembershipRequestLocalService.class)
	private MembershipRequestLocalService _membershipRequestLocalService;

	@BeanReference(type = OrganizationLocalService.class)
	private OrganizationLocalService _organizationLocalService;

	@BeanReference(type = OrganizationPersistence.class)
	private OrganizationPersistence _organizationPersistence;

	@BeanReference(type = PortletLocalService.class)
	private PortletLocalService _portletLocalService;

	@BeanReference(type = PortletPreferencesLocalService.class)
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@BeanReference(type = ResourceActionLocalService.class)
	private ResourceActionLocalService _resourceActionLocalService;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = ResourcePermissionLocalService.class)
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@BeanReference(type = ResourcePermissionPersistence.class)
	private ResourcePermissionPersistence _resourcePermissionPersistence;

	@BeanReference(type = RoleLocalService.class)
	private RoleLocalService _roleLocalService;

	@BeanReference(type = RolePersistence.class)
	private RolePersistence _rolePersistence;

	private ServiceRegistration<?> _serviceRegistration;

	@BeanReference(type = SocialActivityLocalService.class)
	private SocialActivityLocalService _socialActivityLocalService;

	@BeanReference(type = SocialActivitySettingLocalService.class)
	private SocialActivitySettingLocalService
		_socialActivitySettingLocalService;

	@BeanReference(type = SocialRequestLocalService.class)
	private SocialRequestLocalService _socialRequestLocalService;

	@BeanReference(type = StagingLocalService.class)
	private StagingLocalService _stagingLocalService;

	@BeanReference(type = SystemEventLocalService.class)
	private SystemEventLocalService _systemEventLocalService;

	private final Map<String, Group> _systemGroupsMap = new HashMap<>();

	@BeanReference(type = TeamLocalService.class)
	private TeamLocalService _teamLocalService;

	@BeanReference(type = UserGroupGroupRoleLocalService.class)
	private UserGroupGroupRoleLocalService _userGroupGroupRoleLocalService;

	@BeanReference(type = UserGroupLocalService.class)
	private UserGroupLocalService _userGroupLocalService;

	@BeanReference(type = UserGroupPersistence.class)
	private UserGroupPersistence _userGroupPersistence;

	@BeanReference(type = UserGroupRoleLocalService.class)
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@BeanReference(type = UserLocalService.class)
	private UserLocalService _userLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

	@BeanReference(type = WorkflowDefinitionLinkLocalService.class)
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	private static class GroupModelListener extends BaseModelListener<Group> {

		@Override
		public Class<?> getModelClass() {
			return Group.class;
		}

		@Override
		public void onBeforeCreate(Group group) throws ModelListenerException {
			if (group.getLiveGroupId() != 0) {
				_clearStagingGroupIds();
			}
		}

		@Override
		public void onBeforeRemove(Group group) throws ModelListenerException {
			if (group.getLiveGroupId() != 0) {
				_clearStagingGroupIds();
			}
		}

		@Override
		public void onBeforeUpdate(Group originalGroup, Group group) {
			if (originalGroup.getLiveGroupId() != group.getLiveGroupId()) {
				_clearStagingGroupIds();
			}
		}

	}

}