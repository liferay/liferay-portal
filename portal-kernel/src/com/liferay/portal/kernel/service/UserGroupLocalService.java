/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for UserGroup. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see UserGroupLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {"model.class.name=com.liferay.portal.kernel.model.UserGroup"}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface UserGroupLocalService
	extends BaseLocalService, CTService<UserGroup>, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.UserGroupLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the user group local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link UserGroupLocalServiceUtil} if injection and service tracking are not available.
	 */
	public boolean addGroupUserGroup(long groupId, long userGroupId);

	public boolean addGroupUserGroup(long groupId, UserGroup userGroup);

	public boolean addGroupUserGroups(long groupId, List<UserGroup> userGroups);

	public boolean addGroupUserGroups(long groupId, long[] userGroupIds);

	public UserGroup addOrUpdateUserGroup(
			String externalReferenceCode, long userId, long companyId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException;

	public boolean addTeamUserGroup(long teamId, long userGroupId);

	public boolean addTeamUserGroup(long teamId, UserGroup userGroup);

	public boolean addTeamUserGroups(long teamId, List<UserGroup> userGroups);

	public boolean addTeamUserGroups(long teamId, long[] userGroupIds);

	/**
	 * Adds a user group.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user group,
	 * including its resources, metadata, and internal data structures. It is
	 * not necessary to make subsequent calls to setup default groups and
	 * resources for the user group.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param companyId the primary key of the user group's company
	 * @param name the user group's name
	 * @param description the user group's description
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set expando bridge attributes for the
	 user group.
	 * @return the user group
	 */
	public UserGroup addUserGroup(
			String externalReferenceCode, long userId, long companyId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds the user group to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserGroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userGroup the user group
	 * @return the user group that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public UserGroup addUserGroup(UserGroup userGroup);

	/**
	 * @throws PortalException
	 */
	public boolean addUserUserGroup(long userId, long userGroupId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public boolean addUserUserGroup(long userId, UserGroup userGroup)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public boolean addUserUserGroups(long userId, List<UserGroup> userGroups)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public boolean addUserUserGroups(long userId, long[] userGroupIds)
		throws PortalException;

	public void clearGroupUserGroups(long groupId);

	public void clearTeamUserGroups(long teamId);

	public void clearUserUserGroups(long userId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new user group with the primary key. Does not add the user group to the database.
	 *
	 * @param userGroupId the primary key for the new user group
	 * @return the new user group
	 */
	@Transactional(enabled = false)
	public UserGroup createUserGroup(long userGroupId);

	public void deleteGroupUserGroup(long groupId, long userGroupId);

	public void deleteGroupUserGroup(long groupId, UserGroup userGroup);

	public void deleteGroupUserGroups(long groupId, List<UserGroup> userGroups);

	public void deleteGroupUserGroups(long groupId, long[] userGroupIds);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteTeamUserGroup(long teamId, long userGroupId);

	public void deleteTeamUserGroup(long teamId, UserGroup userGroup);

	public void deleteTeamUserGroups(long teamId, List<UserGroup> userGroups);

	public void deleteTeamUserGroups(long teamId, long[] userGroupIds);

	/**
	 * Deletes the user group with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserGroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userGroupId the primary key of the user group
	 * @return the user group that was removed
	 * @throws PortalException if a user group with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public UserGroup deleteUserGroup(long userGroupId) throws PortalException;

	/**
	 * Deletes the user group from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserGroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userGroup the user group
	 * @return the user group that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public UserGroup deleteUserGroup(UserGroup userGroup)
		throws PortalException;

	public void deleteUserGroups(long companyId) throws PortalException;

	public void deleteUserUserGroup(long userId, long userGroupId);

	public void deleteUserUserGroup(long userId, UserGroup userGroup);

	public void deleteUserUserGroups(long userId, List<UserGroup> userGroups);

	public void deleteUserUserGroups(long userId, long[] userGroupIds);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserGroupModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserGroupModelImpl</code>.
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

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserGroup fetchUserGroup(long userGroupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserGroup fetchUserGroup(long companyId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserGroup fetchUserGroupByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the user group with the matching UUID and company.
	 *
	 * @param uuid the user group's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserGroup fetchUserGroupByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	/**
	 * Returns the groupIds of the groups associated with the user group.
	 *
	 * @param userGroupId the userGroupId of the user group
	 * @return long[] the groupIds of groups associated with the user group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getGroupPrimaryKeys(long userGroupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> getGroupUserGroups(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> getGroupUserGroups(long groupId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> getGroupUserGroups(
		long groupId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupUserGroupsCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> getGroupUserUserGroups(long groupId, long userId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserGroup getOrAddEmptyUserGroup(
			String externalReferenceCode, long companyId, long userId,
			String name)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Returns the teamIds of the teams associated with the user group.
	 *
	 * @param userGroupId the userGroupId of the user group
	 * @return long[] the teamIds of teams associated with the user group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getTeamPrimaryKeys(long userGroupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> getTeamUserGroups(long teamId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> getTeamUserGroups(long teamId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> getTeamUserGroups(
		long teamId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getTeamUserGroupsCount(long teamId);

	/**
	 * Returns the user group with the primary key.
	 *
	 * @param userGroupId the primary key of the user group
	 * @return the user group
	 * @throws PortalException if a user group with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserGroup getUserGroup(long userGroupId) throws PortalException;

	/**
	 * Returns the user group with the name.
	 *
	 * @param companyId the primary key of the user group's company
	 * @param name the user group's name
	 * @return Returns the user group with the name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserGroup getUserGroup(long companyId, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserGroup getUserGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the user group with the matching UUID and company.
	 *
	 * @param uuid the user group's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user group
	 * @throws PortalException if a matching user group could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserGroup getUserGroupByUuidAndCompanyId(String uuid, long companyId)
		throws PortalException;

	/**
	 * Returns a range of all the user groups.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of user groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> getUserGroups(int start, int end);

	/**
	 * Returns all the user groups belonging to the company.
	 *
	 * @param companyId the primary key of the user groups' company
	 * @return the user groups belonging to the company
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> getUserGroups(long companyId);

	/**
	 * Returns all the user groups with the primary keys.
	 *
	 * @param userGroupIds the primary keys of the user groups
	 * @return the user groups with the primary keys
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> getUserGroups(long[] userGroupIds)
		throws PortalException;

	/**
	 * Returns the number of user groups.
	 *
	 * @return the number of user groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserGroupsCount();

	/**
	 * Returns the userIds of the users associated with the user group.
	 *
	 * @param userGroupId the userGroupId of the user group
	 * @return long[] the userIds of users associated with the user group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getUserPrimaryKeys(long userGroupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> getUserUserGroups(long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> getUserUserGroups(long userId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> getUserUserGroups(
		long userId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserUserGroupsCount(long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasGroupUserGroup(long groupId, long userGroupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasGroupUserGroups(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasTeamUserGroup(long teamId, long userGroupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasTeamUserGroups(long teamId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserUserGroup(long userId, long userGroupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserUserGroups(long userId);

	/**
	 * Returns an ordered range of all the user groups that match the keywords.
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
	 * @param companyId the primary key of the user group's company
	 * @param keywords the keywords (space separated), which may occur in the
	 user group's name or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.UserGroupFinder}
	 * @param start the lower bound of the range of user groups to return
	 * @param end the upper bound of the range of user groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the user groups
	 (optionally <code>null</code>)
	 * @return the matching user groups ordered by comparator
	 <code>orderByComparator</code>
	 * @see com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> search(
		long companyId, String keywords, LinkedHashMap<String, Object> params,
		int start, int end, OrderByComparator<UserGroup> orderByComparator);

	/**
	 * Returns an ordered range of all the user groups that match the keywords,
	 * using the indexer. It is preferable to use this method instead of the
	 * non-indexed version whenever possible for performance reasons.
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
	 * @param companyId the primary key of the user group's company
	 * @param keywords the keywords (space separated), which may occur in the
	 user group's name or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). For more
	 information see {@link
	 com.liferay.user.groups.admin.web.search.UserGroupIndexer}
	 * @param start the lower bound of the range of user groups to return
	 * @param end the upper bound of the range of user groups to return (not
	 inclusive)
	 * @param sort the field and direction by which to sort (optionally
	 <code>null</code>)
	 * @return the matching user groups ordered by sort
	 * @see com.liferay.user.groups.admin.web.search.UserGroupIndexer
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, String keywords, LinkedHashMap<String, Object> params,
		int start, int end, Sort sort);

	/**
	 * Returns an ordered range of all the user groups that match the name and
	 * description.
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
	 * @param companyId the primary key of the user group's company
	 * @param name the user group's name (optionally <code>null</code>)
	 * @param description the user group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.UserGroupFinder}
	 * @param andOperator whether every field must match its keywords or just
	 one field
	 * @param start the lower bound of the range of user groups to return
	 * @param end the upper bound of the range of user groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the user groups
	 (optionally <code>null</code>)
	 * @return the matching user groups ordered by comparator
	 <code>orderByComparator</code>
	 * @see com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroup> search(
		long companyId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<UserGroup> orderByComparator);

	/**
	 * Returns an ordered range of all the user groups that match the name and
	 * description. It is preferable to use this method instead of the
	 * non-indexed version whenever possible for performance reasons.
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
	 * @param companyId the primary key of the user group's company
	 * @param name the user group's name (optionally <code>null</code>)
	 * @param description the user group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). For more
	 information see {@link
	 com.liferay.user.groups.admin.web.search.UserGroupIndexer}
	 * @param andSearch whether every field must match its keywords or just one
	 field
	 * @param start the lower bound of the range of user groups to return
	 * @param end the upper bound of the range of user groups to return (not
	 inclusive)
	 * @param sort the field and direction by which to sort (optionally
	 <code>null</code>)
	 * @return the matching user groups ordered by sort
	 * @see com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andSearch, int start,
		int end, Sort sort);

	/**
	 * Returns the number of user groups that match the keywords
	 *
	 * @param companyId the primary key of the user group's company
	 * @param keywords the keywords (space separated), which may occur in the
	 user group's name or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.UserGroupFinder}
	 * @return the number of matching user groups
	 * @see com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, String keywords, LinkedHashMap<String, Object> params);

	/**
	 * Returns the number of user groups that match the name and description.
	 *
	 * @param companyId the primary key of the user group's company
	 * @param name the user group's name (optionally <code>null</code>)
	 * @param description the user group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.UserGroupFinder}
	 * @param andOperator whether every field must match its keywords or just
	 one field
	 * @return the number of matching user groups
	 * @see com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<UserGroup> searchUserGroups(
			long companyId, String keywords,
			LinkedHashMap<String, Object> params, int start, int end, Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<UserGroup> searchUserGroups(
			long companyId, String name, String description,
			LinkedHashMap<String, Object> params, boolean andSearch, int start,
			int end, Sort sort)
		throws PortalException;

	public void setGroupUserGroups(long groupId, long[] userGroupIds);

	public void setTeamUserGroups(long teamId, long[] userGroupIds);

	/**
	 * @throws PortalException
	 */
	public void setUserUserGroups(long userId, long[] userGroupIds)
		throws PortalException;

	/**
	 * Removes the user groups from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userGroupIds the primary keys of the user groups
	 */
	public void unsetGroupUserGroups(long groupId, long[] userGroupIds);

	/**
	 * Removes the user groups from the team.
	 *
	 * @param teamId the primary key of the team
	 * @param userGroupIds the primary keys of the user groups
	 */
	public void unsetTeamUserGroups(long teamId, long[] userGroupIds);

	@Indexable(type = IndexableType.REINDEX)
	public UserGroup updateExternalReferenceCode(
			UserGroup userGroup, String externalReferenceCode)
		throws PortalException;

	/**
	 * Updates the user group.
	 *
	 * @param externalReferenceCode the user group's external reference code
	 * @param companyId the primary key of the user group's company
	 * @param userGroupId the primary key of the user group
	 * @param name the user group's name
	 * @param description the user group's description
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set expando bridge attributes for the
	 user group.
	 * @return the user group
	 */
	public UserGroup updateUserGroup(
			String externalReferenceCode, long companyId, long userGroupId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the user group in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserGroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userGroup the user group
	 * @return the user group that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public UserGroup updateUserGroup(UserGroup userGroup);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<UserGroup> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<UserGroup> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<UserGroup>, R, E> updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:1073455538