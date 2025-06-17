/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for Group. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see GroupLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {"model.class.name=com.liferay.portal.kernel.model.Group"}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface GroupLocalService
	extends BaseLocalService, CTService<Group>, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.GroupLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the group local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link GroupLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the group to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect GroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param group the group
	 * @return the group that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public Group addGroup(Group group);

	public Group addGroup(
			long userId, long parentGroupId, String className, long classPK,
			long liveGroupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean inheritContent,
			boolean active, ServiceContext serviceContext)
		throws PortalException;

	public Group addGroup(
			long userId, long parentGroupId, String className, long classPK,
			long liveGroupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean active,
			ServiceContext serviceContext)
		throws PortalException;

	public boolean addOrganizationGroup(long organizationId, Group group);

	public boolean addOrganizationGroup(long organizationId, long groupId);

	public boolean addOrganizationGroups(
		long organizationId, List<Group> groups);

	public boolean addOrganizationGroups(long organizationId, long[] groupIds);

	public Group addOrUpdateGroup(
			String externalReferenceCode, long userId, long parentGroupId,
			String className, long classPK, long liveGroupId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			int type, boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean inheritContent,
			boolean active, ServiceContext serviceContext)
		throws Exception;

	public boolean addRoleGroup(long roleId, Group group);

	public boolean addRoleGroup(long roleId, long groupId);

	public boolean addRoleGroups(long roleId, List<Group> groups);

	public boolean addRoleGroups(long roleId, long[] groupIds);

	public boolean addUserGroup(long userId, Group group);

	public boolean addUserGroup(long userId, long groupId);

	public boolean addUserGroupGroup(long userGroupId, Group group);

	public boolean addUserGroupGroup(long userGroupId, long groupId);

	public boolean addUserGroupGroups(long userGroupId, List<Group> groups);

	public boolean addUserGroupGroups(long userGroupId, long[] groupIds);

	public boolean addUserGroups(long userId, List<Group> groups);

	public boolean addUserGroups(long userId, long[] groupIds);

	public Group checkScopeGroup(Layout layout, long userId)
		throws PortalException;

	/**
	 * Creates systems groups and other related data needed by the system on the
	 * very first startup. Also takes care of creating the Control Panel groups
	 * and layouts.
	 *
	 * @param companyId the primary key of the company
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void checkSystemGroups(long companyId) throws PortalException;

	public void clearOrganizationGroups(long organizationId);

	public void clearRoleGroups(long roleId);

	public void clearUserGroupGroups(long userGroupId);

	public void clearUserGroups(long userId);

	/**
	 * Creates a new group with the primary key. Does not add the group to the database.
	 *
	 * @param groupId the primary key for the new group
	 * @return the new group
	 */
	@Transactional(enabled = false)
	public Group createGroup(long groupId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the group from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect GroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param group the group
	 * @return the group that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	public Group deleteGroup(Group group) throws PortalException;

	/**
	 * Deletes the group with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect GroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @return the group that was removed
	 * @throws PortalException if a group with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public Group deleteGroup(long groupId) throws PortalException;

	public void deleteOrganizationGroup(long organizationId, Group group);

	public void deleteOrganizationGroup(long organizationId, long groupId);

	public void deleteOrganizationGroups(
		long organizationId, List<Group> groups);

	public void deleteOrganizationGroups(long organizationId, long[] groupIds);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteRoleGroup(long roleId, Group group);

	public void deleteRoleGroup(long roleId, long groupId);

	public void deleteRoleGroups(long roleId, List<Group> groups);

	public void deleteRoleGroups(long roleId, long[] groupIds);

	public void deleteUserGroup(long userId, Group group);

	public void deleteUserGroup(long userId, long groupId);

	public void deleteUserGroupGroup(long userGroupId, Group group);

	public void deleteUserGroupGroup(long userGroupId, long groupId);

	public void deleteUserGroupGroups(long userGroupId, List<Group> groups);

	public void deleteUserGroupGroups(long userGroupId, long[] groupIds);

	public void deleteUserGroups(long userId, List<Group> groups);

	public void deleteUserGroups(long userId, long[] groupIds);

	public void disableStaging(long groupId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	public void enableStaging(long groupId) throws PortalException;

	/**
	 * Returns the company's group.
	 *
	 * @param companyId the primary key of the company
	 * @return the company's group, or <code>null</code> if a matching group
	 could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group fetchCompanyGroup(long companyId);

	/**
	 * Returns the group with the matching friendly URL.
	 *
	 * @param companyId the primary key of the company
	 * @param friendlyURL the friendly URL
	 * @return the group with the friendly URL, or <code>null</code> if a
	 matching group could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group fetchFriendlyURLGroup(long companyId, String friendlyURL);

	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group fetchGroup(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group fetchGroup(long companyId, long classNameId, long classPK);

	/**
	 * Returns the group with the matching group key by first searching the
	 * system groups and then using the finder cache.
	 *
	 * @param companyId the primary key of the company
	 * @param groupKey the group key
	 * @return the group with the group key and associated company, or
	 <code>null</code> if a matching group could not be found
	 */
	@Transactional(enabled = false)
	public Group fetchGroup(long companyId, String groupKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group fetchGroupByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the group with the matching UUID and company.
	 *
	 * @param uuid the group's UUID
	 * @param companyId the primary key of the company
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group fetchGroupByUuidAndCompanyId(String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group fetchStagingGroup(long liveGroupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group fetchUserGroup(long companyId, long userId);

	/**
	 * Returns the default user's personal site group.
	 *
	 * @param companyId the primary key of the company
	 * @return the default user's personal site group, or <code>null</code> if a
	 matching group could not be found
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group fetchUserPersonalSiteGroup(long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getActiveGroupIds(long userId);

	/**
	 * Returns all the active or inactive groups associated with the company.
	 *
	 * @param companyId the primary key of the company
	 * @param active whether to return only active groups or only inactive
	 groups
	 * @return the active or inactive groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getActiveGroups(long companyId, boolean active);

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
	 * @param companyId the primary key of the company
	 * @param site whether the group is associated with a main site
	 * @param active whether to return only active groups or only inactive
	 groups
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the active or inactive groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getActiveGroups(
		long companyId, boolean site, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator);

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
	 * @param companyId the primary key of the company
	 * @param active whether to return only active groups or only inactive
	 groups
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the active or inactive groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getActiveGroups(
		long companyId, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator);

	/**
	 * Returns the number of active or inactive groups associated with the
	 * company.
	 *
	 * @param companyId the primary key of the company
	 * @param active whether to count only active groups or only inactive
	 groups
	 * @return the number of active or inactive groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getActiveGroupsCount(long companyId, boolean active);

	/**
	 * Returns the number of active or inactive groups associated with the
	 * company.
	 *
	 * @param companyId the primary key of the company
	 * @param active whether to count only active groups or only inactive
	 groups
	 * @param site whether the group is to be associated with a main site
	 * @return the number of active or inactive groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getActiveGroupsCount(
		long companyId, boolean active, boolean site);

	/**
	 * Returns the company group.
	 *
	 * @param companyId the primary key of the company
	 * @return the group associated with the company
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group getCompanyGroup(long companyId) throws PortalException;

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
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the range of groups associated with the company
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getCompanyGroups(long companyId, int start, int end);

	/**
	 * Returns the number of groups associated with the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the number of groups associated with the company
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCompanyGroupsCount(long companyId);

	/**
	 * Returns the group with the matching friendly URL.
	 *
	 * @param companyId the primary key of the company
	 * @param friendlyURL the group's friendlyURL
	 * @return the group with the friendly URL
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group getFriendlyURLGroup(long companyId, String friendlyURL)
		throws PortalException;

	/**
	 * Returns the group with the primary key.
	 *
	 * @param groupId the primary key of the group
	 * @return the group
	 * @throws PortalException if a group with the primary key could not be found
	 */
	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group getGroup(long groupId) throws PortalException;

	/**
	 * Returns the group with the matching group key.
	 *
	 * @param companyId the primary key of the company
	 * @param groupKey the group key
	 * @return the group with the group key
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(enabled = false)
	public Group getGroup(long companyId, String groupKey)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group getGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the group with the matching UUID and company.
	 *
	 * @param uuid the group's UUID
	 * @param companyId the primary key of the company
	 * @return the matching group
	 * @throws PortalException if a matching group could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group getGroupByUuidAndCompanyId(String uuid, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getGroupIds(long companyId, boolean active);

	/**
	 * Returns a range of all the groups.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getGroups(int start, int end);

	/**
	 * Returns all the groups that are direct children of the parent group.
	 *
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param site whether the group is to be associated with a main site
	 * @return the matching groups, or <code>null</code> if no matches were
	 found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getGroups(
		long companyId, long parentGroupId, boolean site);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getGroups(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getGroups(
		long companyId, long parentGroupId, boolean site, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getGroups(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getGroups(long companyId, String treePath, boolean site);

	/**
	 * Returns all the groups that are direct children of the parent group with
	 * the matching className.
	 *
	 * @param companyId the primary key of the company
	 * @param className the class name of the group
	 * @param parentGroupId the primary key of the parent group
	 * @return the matching groups, or <code>null</code> if no matches were
	 found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getGroups(
		long companyId, String className, long parentGroupId);

	/**
	 * Returns a range of all the groups that are direct children of the parent
	 * group with the matching className.
	 *
	 * @param companyId the primary key of the company
	 * @param className the class name of the group
	 * @param parentGroupId the primary key of the parent group
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getGroups(
		long companyId, String className, long parentGroupId, int start,
		int end);

	/**
	 * Returns the groups with the matching primary keys.
	 *
	 * @param groupIds the primary keys of the groups
	 * @return the groups with the primary keys
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getGroups(long[] groupIds) throws PortalException;

	/**
	 * Returns the number of groups.
	 *
	 * @return the number of groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupsCount();

	/**
	 * Returns the number of groups that are direct children of the parent
	 * group.
	 *
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param site whether the group is to be associated with a main site
	 * @return the number of matching groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupsCount(long companyId, long parentGroupId, boolean site);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupsCount(
		long companyId, long parentGroupId, String name, boolean site);

	/**
	 * Returns the number of groups that are direct children of the parent group
	 * with the matching className.
	 *
	 * @param companyId the primary key of the company
	 * @param className the class name of the group
	 * @param parentGroupId the primary key of the parent group
	 * @return the number of matching groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupsCount(
		long companyId, String className, long parentGroupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the group associated with the layout.
	 *
	 * @param companyId the primary key of the company
	 * @param plid the primary key of the layout
	 * @return the group associated with the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group getLayoutGroup(long companyId, long plid)
		throws PortalException;

	/**
	 * Returns the group associated with the layout prototype.
	 *
	 * @param companyId the primary key of the company
	 * @param layoutPrototypeId the primary key of the layout prototype
	 * @return the group associated with the layout prototype
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group getLayoutPrototypeGroup(long companyId, long layoutPrototypeId)
		throws PortalException;

	/**
	 * Returns the group associated with the layout set prototype.
	 *
	 * @param companyId the primary key of the company
	 * @param layoutSetPrototypeId the primary key of the layout set prototype
	 * @return the group associated with the layout set prototype
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group getLayoutSetPrototypeGroup(
			long companyId, long layoutSetPrototypeId)
		throws PortalException;

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
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param site whether the group is to be associated with a main site
	 * @param active whether to return only active groups, or only inactive
	 groups
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the range of matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getLayoutsGroups(
		long companyId, long parentGroupId, boolean site, boolean active,
		int start, int end, OrderByComparator<Group> orderByComparator);

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
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param site whether the group is to be associated with a main site
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the range of matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getLayoutsGroups(
		long companyId, long parentGroupId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator);

	/**
	 * Returns the number of groups that are children or the parent group and
	 * that have at least one layout
	 *
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param site whether the group is to be associated with a main site
	 * @return the number of matching groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutsGroupsCount(
		long companyId, long parentGroupId, boolean site);

	/**
	 * Returns the number of groups that are children or the parent group and
	 * that have at least one layout
	 *
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param site whether the group is to be associated with a main site
	 * @param active whether to return only active groups, or only inactive
	 groups
	 * @return the number of matching groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutsGroupsCount(
		long companyId, long parentGroupId, boolean site, boolean active);

	/**
	 * Returns all live groups.
	 *
	 * @return all live groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getLiveGroups();

	/**
	 * Returns the specified organization group.
	 *
	 * @param companyId the primary key of the company
	 * @param organizationId the primary key of the organization
	 * @return the group associated with the organization
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group getOrganizationGroup(long companyId, long organizationId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getOrganizationGroups(long organizationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getOrganizationGroups(
		long organizationId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getOrganizationGroups(
		long organizationId, int start, int end,
		OrderByComparator<Group> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOrganizationGroupsCount(long organizationId);

	/**
	 * Returns the organizationIds of the organizations associated with the group.
	 *
	 * @param groupId the groupId of the group
	 * @return long[] the organizationIds of organizations associated with the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getOrganizationPrimaryKeys(long groupId);

	/**
	 * Returns the specified organization groups.
	 *
	 * @param organizations the organizations
	 * @return the groups associated with the organizations
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getOrganizationsGroups(List<Organization> organizations);

	/**
	 * Returns all the groups related to the organizations.
	 *
	 * @param organizations the organizations
	 * @return the groups related to the organizations
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getOrganizationsRelatedGroups(
		List<Organization> organizations);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * Returns the group followed by all its parent groups ordered by closest
	 * ancestor.
	 *
	 * @param groupId the primary key of the group
	 * @return the group followed by all its parent groups ordered by closest
	 ancestor
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getParentGroups(long groupId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getRoleGroups(long roleId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getRoleGroups(long roleId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getRoleGroups(
		long roleId, int start, int end,
		OrderByComparator<Group> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRoleGroupsCount(long roleId);

	/**
	 * Returns the roleIds of the roles associated with the group.
	 *
	 * @param groupId the groupId of the group
	 * @return long[] the roleIds of roles associated with the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getRolePrimaryKeys(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getStagedSites();

	/**
	 * Returns the group directly associated with the user.
	 *
	 * @param companyId the primary key of the company
	 * @param userId the primary key of the user
	 * @return the group directly associated with the user
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group getUserGroup(long companyId, long userId)
		throws PortalException;

	/**
	 * Returns the specified "user group" group. That is, the group that
	 * represents the {@link UserGroup} entity.
	 *
	 * @param companyId the primary key of the company
	 * @param userGroupId the primary key of the user group
	 * @return the group associated with the user group
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group getUserGroupGroup(long companyId, long userGroupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getUserGroupGroups(long userGroupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getUserGroupGroups(long userGroupId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getUserGroupGroups(
		long userGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserGroupGroupsCount(long userGroupId);

	/**
	 * Returns the userGroupIds of the user groups associated with the group.
	 *
	 * @param groupId the groupId of the group
	 * @return long[] the userGroupIds of user groups associated with the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getUserGroupPrimaryKeys(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getUserGroups(long userId);

	/**
	 * Returns all the user's site groups and immediate organization groups,
	 * optionally including the user's inherited organization groups and user
	 * groups. System and staged groups are not included.
	 *
	 * @param userId the primary key of the user
	 * @param inherit whether to include the user's inherited organization
	 groups and user groups
	 * @return the user's groups and immediate organization groups
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getUserGroups(long userId, boolean inherit)
		throws PortalException;

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
	 * @param userId the primary key of the user
	 * @param inherit whether to include the user's inherited organization
	 groups and user groups
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the range of the user's groups and immediate organization groups
	 ordered by name
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getUserGroups(
			long userId, boolean inherit, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getUserGroups(long userId, int start, int end);

	/**
	 * @throws PortalException
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getUserGroups(
			long userId, int start, int end,
			OrderByComparator<Group> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserGroupsCount(long userId);

	/**
	 * Returns the groups associated with the user groups.
	 *
	 * @param userGroups the user groups
	 * @return the groups associated with the user groups
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getUserGroupsGroups(List<UserGroup> userGroups)
		throws PortalException;

	/**
	 * Returns all the groups related to the user groups.
	 *
	 * @param userGroups the user groups
	 * @return the groups related to the user groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getUserGroupsRelatedGroups(List<UserGroup> userGroups);

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
	 * @param userId the primary key of the user
	 * @param start the lower bound of the range of groups to consider
	 * @param end the upper bound of the range of groups to consider (not
	 inclusive)
	 * @return the range of groups associated with the user's organization
	 groups
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getUserOrganizationsGroups(
			long userId, int start, int end)
		throws PortalException;

	/**
	 * Returns the default user's personal site group.
	 *
	 * @param companyId the primary key of the company
	 * @return the default user's personal site group
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group getUserPersonalSiteGroup(long companyId)
		throws PortalException;

	/**
	 * Returns the userIds of the users associated with the group.
	 *
	 * @param groupId the groupId of the group
	 * @return long[] the userIds of users associated with the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getUserPrimaryKeys(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getUserSitesGroups(long userId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getUserSitesGroups(
			long userId, boolean includeAdministrative)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> getUserSitesGroups(long userId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasOrganizationGroup(long organizationId, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasOrganizationGroups(long organizationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasRoleGroup(long roleId, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasRoleGroups(long roleId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserGroup(long userId, long groupId);

	/**
	 * Returns <code>true</code> if the user is immediately associated with the
	 * group, or optionally if the user is associated with the group via the
	 * user's organizations, inherited organizations, or user groups.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @param inherit whether to include organization groups and user groups to
	 which the user belongs in the determination
	 * @return <code>true</code> if the user is associated with the group;
	 <code>false</code> otherwise
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserGroup(long userId, long groupId, boolean inherit);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserGroupGroup(long userGroupId, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserGroupGroups(long userGroupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserGroups(long userId);

	@Transactional(enabled = false)
	public boolean isLiveGroupActive(Group group);

	/**
	 * Returns the group with the matching group key by first searching the
	 * system groups and then using the finder cache.
	 *
	 * @param companyId the primary key of the company
	 * @param groupKey the group key
	 * @return the group with the group key and associated company, or
	 <code>null</code> if a matching group could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group loadFetchGroup(long companyId, String groupKey);

	/**
	 * Returns the group with the matching group key.
	 *
	 * @param companyId the primary key of the company
	 * @param groupKey the group key
	 * @return the group with the group key and associated company
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Group loadGetGroup(long companyId, String groupKey)
		throws PortalException;

	/**
	 * Rebuilds the group tree.
	 *
	 * <p>
	 * Only call this method if the tree has become stale through operations
	 * other than normal CRUD. Under normal circumstances the tree is
	 * automatically rebuilt whenever necessary.
	 * </p>
	 *
	 * @param companyId the primary key of the group's company
	 * @throws PortalException if a portal exception occurred
	 */
	public void rebuildTree(long companyId) throws PortalException;

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
	 * @param companyId the primary key of the company
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, LinkedHashMap<String, Object> params, int start,
		int end);

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
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, long parentGroupId, String keywords,
		LinkedHashMap<String, Object> params, int start, int end);

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
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, long parentGroupId, String keywords,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Group> orderByComparator);

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
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, long parentGroupId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end);

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
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, long parentGroupId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<Group> orderByComparator);

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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param parentGroupId the primary key of the parent group
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId,
		String keywords, LinkedHashMap<String, Object> params, int start,
		int end);

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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param parentGroupId the primary key of the parent group
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId,
		String keywords, LinkedHashMap<String, Object> params, int start,
		int end, OrderByComparator<Group> orderByComparator);

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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param parentGroupId the primary key of the parent group
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId, String name,
		String description, LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end);

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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param parentGroupId the primary key of the parent group
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId, String name,
		String description, LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end,
		OrderByComparator<Group> orderByComparator);

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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, long[] classNameIds, String keywords,
		LinkedHashMap<String, Object> params, int start, int end);

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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, long[] classNameIds, String keywords,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Group> orderByComparator);

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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, long[] classNameIds, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end);

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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, long[] classNameIds, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<Group> orderByComparator);

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
	 * @param companyId the primary key of the company
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, String keywords, LinkedHashMap<String, Object> params,
		int start, int end);

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
	 * @param companyId the primary key of the company
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, String keywords, LinkedHashMap<String, Object> params,
		int start, int end, OrderByComparator<Group> orderByComparator);

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
	 * @param companyId the primary key of the company
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end);

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
	 * @param companyId the primary key of the company
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Group> search(
		long companyId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<Group> orderByComparator);

	/**
	 * Returns the number of groups belonging to the parent group that match the
	 * keywords, optionally including the user's inherited organization groups
	 * and user groups. System and staged groups are not included.
	 *
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @return the number of matching groups
	 */
	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long parentGroupId, String keywords,
		LinkedHashMap<String, Object> params);

	/**
	 * Returns the number of groups belonging to the parent group and immediate
	 * organization groups that match the name and description, optionally
	 * including the user's inherited organization groups and user groups.
	 * System and staged groups are not included.
	 *
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @return the number of matching groups
	 */
	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long parentGroupId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator);

	/**
	 * Returns the number of groups belonging to the parent group that match the
	 * class name IDs, and keywords, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
	 *
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param parentGroupId the primary key of the parent group
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @return the number of matching groups
	 */
	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long[] classNameIds, long parentGroupId,
		String keywords, LinkedHashMap<String, Object> params);

	/**
	 * Returns the number of groups belonging to the parent group that match the
	 * class name IDs, name, and description, optionally including the user's
	 * inherited organization groups and user groups. System and staged groups
	 * are not included.
	 *
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param parentGroupId the primary key of the parent group
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @return the number of matching groups
	 */
	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long[] classNameIds, long parentGroupId, String name,
		String description, LinkedHashMap<String, Object> params,
		boolean andOperator);

	/**
	 * Returns the number of groups that match the class name IDs, and keywords,
	 * optionally including the user's inherited organization groups and user
	 * groups. System and staged groups are not included.
	 *
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @return the number of matching groups
	 */
	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long[] classNameIds, String keywords,
		LinkedHashMap<String, Object> params);

	/**
	 * Returns the number of groups that match the class name IDs, name, and
	 * description, optionally including the user's inherited organization
	 * groups and user groups. System and staged groups are not included.
	 *
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @return the number of matching groups
	 */
	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long[] classNameIds, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator);

	/**
	 * Returns the number of groups that match the keywords, optionally
	 * including the user's inherited organization groups and user groups.
	 * System and staged groups are not included.
	 *
	 * @param companyId the primary key of the company
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @return the number of matching groups
	 */
	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, String keywords, LinkedHashMap<String, Object> params);

	/**
	 * Returns the number of groups and immediate organization groups that match
	 * the name and description, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
	 *
	 * @param companyId the primary key of the company
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @return the number of matching groups
	 */
	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator);

	public void setOrganizationGroups(long organizationId, long[] groupIds);

	public void setRoleGroups(long roleId, long[] groupIds);

	public void setUserGroupGroups(long userGroupId, long[] groupIds);

	public void setUserGroups(long userId, long[] groupIds);

	/**
	 * Removes the groups from the role.
	 *
	 * @param roleId the primary key of the role
	 * @param groupIds the primary keys of the groups
	 */
	public void unsetRoleGroups(long roleId, long[] groupIds);

	/**
	 * Removes the user from the groups.
	 *
	 * @param userId the primary key of the user
	 * @param groupIds the primary keys of the groups
	 */
	public void unsetUserGroups(long userId, long[] groupIds);

	/**
	 * Updates the group's asset replacing categories and tag names.
	 *
	 * @param userId the primary key of the user
	 * @param group the group
	 * @param assetCategoryIds the primary keys of the asset categories
	 (optionally <code>null</code>)
	 * @param assetTagNames the asset tag names (optionally <code>null</code>)
	 * @throws PortalException if a portal exception occurred
	 */
	public void updateAsset(
			long userId, Group group, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException;

	/**
	 * Updates the group's friendly URL.
	 *
	 * @param groupId the primary key of the group
	 * @param friendlyURL the group's new friendlyURL (optionally
	 <code>null</code>)
	 * @return the group
	 * @throws PortalException if a portal exception occurred
	 */
	public Group updateFriendlyURL(long groupId, String friendlyURL)
		throws PortalException;

	/**
	 * Updates the group in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect GroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param group the group
	 * @return the group that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public Group updateGroup(Group group);

	public Group updateGroup(
			long groupId, long parentGroupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean inheritContent, boolean active,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the group's type settings.
	 *
	 * @param groupId the primary key of the group
	 * @param typeSettings the group's new type settings (optionally
	 <code>null</code>)
	 * @return the group
	 * @throws PortalException if a portal exception occurred
	 */
	public Group updateGroup(long groupId, String typeSettings)
		throws PortalException;

	/**
	 * Associates the group with a main site if the group is an organization.
	 *
	 * @param groupId the primary key of the group
	 * @param site whether the group is to be associated with a main site
	 * @return the group
	 * @throws PortalException if a portal exception occurred
	 */
	public Group updateSite(long groupId, boolean site) throws PortalException;

	public void validateRemote(
			long groupId, String remoteAddress, int remotePort,
			String remotePathContext, boolean secureConnection,
			long remoteGroupId)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<Group> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<Group> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<Group>, R, E> updateUnsafeFunction)
		throws E;

}