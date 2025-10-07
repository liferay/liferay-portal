/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManagerUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.DuplicateOrganizationException;
import com.liferay.portal.kernel.exception.OrganizationCommentsException;
import com.liferay.portal.kernel.exception.OrganizationNameException;
import com.liferay.portal.kernel.exception.OrganizationParentException;
import com.liferay.portal.kernel.exception.OrganizationTypeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RequiredOrganizationException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.search.reindexer.ReindexerBridge;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.EmailAddressLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.PasswordPolicyRelLocalService;
import com.liferay.portal.kernel.service.PhoneLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WebsiteLocalService;
import com.liferay.portal.kernel.service.persistence.CompanyPersistence;
import com.liferay.portal.kernel.service.persistence.CountryPersistence;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.RegionPersistence;
import com.liferay.portal.kernel.service.persistence.UserFinder;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.tree.TreeModelTasksAdapter;
import com.liferay.portal.kernel.tree.TreePathUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.comparator.OrganizationIdComparator;
import com.liferay.portal.kernel.util.comparator.OrganizationNameComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.base.OrganizationLocalServiceBaseImpl;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portlet.usersadmin.search.OrganizationUsersSearcher;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.users.admin.kernel.file.uploads.UserFileUploadsSettings;
import com.liferay.users.admin.kernel.organization.types.OrganizationTypesSettings;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Provides the local service for accessing, adding, deleting, and updating
 * organizations.
 *
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Julio Camarero
 * @author Hugo Huijser
 * @author Juan Fernández
 * @author Marco Leo
 */
public class OrganizationLocalServiceImpl
	extends OrganizationLocalServiceBaseImpl {

	/**
	 * Adds the organization to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationId the primary key of the organization
	 * @return <code>true</code> if the association between the ${groupId} and ${organizationId} is added; <code>false</code> if it was already added
	 */
	@Override
	public boolean addGroupOrganization(long groupId, long organizationId) {
		if (!super.addGroupOrganization(groupId, organizationId)) {
			return false;
		}

		try {
			reindexUsers(organizationId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	/**
	 * Adds the organization to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organization the organization
	 * @return <code>true</code> if the association between the ${groupId} and ${organization} is added; <code>false</code> if it was already added
	 */
	@Override
	public boolean addGroupOrganization(
		long groupId, Organization organization) {

		if (!super.addGroupOrganization(groupId, organization)) {
			return false;
		}

		try {
			reindexUsers(organization);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	/**
	 * Adds the organizations to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizations the organizations
	 * @return <code>true</code> if at least an association between the ${groupId} and the ${organizations} is added; <code>false</code> if all were already added
	 */
	@Override
	public boolean addGroupOrganizations(
		long groupId, List<Organization> organizations) {

		if (!super.addGroupOrganizations(groupId, organizations)) {
			return false;
		}

		try {
			reindexUsers(organizations);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	/**
	 * Adds the organizations to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 * @return <code>true</code> if at least an association between the ${groupId} and the ${organizationIds} is added; <code>false</code> if all were already added
	 */
	@Override
	public boolean addGroupOrganizations(long groupId, long[] organizationIds) {
		if (!super.addGroupOrganizations(groupId, organizationIds)) {
			return false;
		}

		try {
			reindexUsers(organizationIds);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	/**
	 * Adds an organization.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the organization
	 * including its resources, metadata, and internal data structures. It is
	 * not necessary to make a subsequent call to {@link
	 * #addOrganizationResources(long, Organization)}.
	 * </p>
	 *
	 * @param  userId the primary key of the creator/owner of the organization
	 * @param  parentOrganizationId the primary key of the organization's parent
	 *         organization
	 * @param  name the organization's name
	 * @param  site whether the organization is to be associated with a main
	 *         site
	 * @return the organization
	 */
	@Override
	public Organization addOrganization(
			long userId, long parentOrganizationId, String name, boolean site)
		throws PortalException {

		String[] types = getTypes();

		User user = _userPersistence.findByPrimaryKey(userId);

		ListType listType = _listTypeLocalService.getListType(
			user.getCompanyId(), ListTypeConstants.ORGANIZATION_STATUS_DEFAULT,
			ListTypeConstants.ORGANIZATION_STATUS);

		return addOrganization(
			null, userId, parentOrganizationId, name, types[0], 0, 0,
			listType.getListTypeId(), StringPool.BLANK, site, null);
	}

	/**
	 * Adds an organization.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the organization
	 * including its resources, metadata, and internal data structures. It is
	 * not necessary to make a subsequent call to {@link
	 * #addOrganizationResources(long, Organization)}.
	 * </p>
	 *
	 * @param  userId the primary key of the creator/owner of the organization
	 * @param  parentOrganizationId the primary key of the organization's parent
	 *         organization
	 * @param  name the organization's name
	 * @param  type the organization's type
	 * @param  regionId the primary key of the organization's region
	 * @param  countryId the primary key of the organization's country
	 * @param  statusListTypeId the organization's workflow status
	 * @param  comments the comments about the organization
	 * @param  site whether the organization is to be associated with a main
	 *         site
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set asset category IDs, asset tag names,
	 *         and expando bridge attributes for the organization.
	 * @return the organization
	 */
	@Override
	public Organization addOrganization(
			String externalReferenceCode, long userId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean site, ServiceContext serviceContext)
		throws PortalException {

		// Organization

		User user = _userPersistence.findByPrimaryKey(userId);

		parentOrganizationId = getParentOrganizationId(
			user.getCompanyId(), parentOrganizationId);

		validate(
			user.getCompanyId(), parentOrganizationId, name, type, countryId,
			statusListTypeId, comments);

		long organizationId = counterLocalService.increment();

		Organization organization = organizationPersistence.create(
			organizationId);

		if (serviceContext != null) {
			organization.setUuid(serviceContext.getUuid());
		}

		organization.setExternalReferenceCode(externalReferenceCode);
		organization.setCompanyId(user.getCompanyId());
		organization.setUserId(user.getUserId());
		organization.setUserName(user.getFullName());
		organization.setParentOrganizationId(parentOrganizationId);
		organization.setTreePath(organization.buildTreePath());
		organization.setName(name);
		organization.setType(type);
		organization.setRecursable(true);
		organization.setRegionId(regionId);
		organization.setCountryId(countryId);
		organization.setStatusListTypeId(statusListTypeId);
		organization.setComments(comments);

		if (EmptyModelManagerUtil.isEmptyModel()) {
			organization.setStatus(WorkflowConstants.STATUS_EMPTY);
		}
		else {
			organization.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		organization.setExpandoBridgeAttributes(serviceContext);

		organization = organizationPersistence.update(organization);

		// Group

		long parentGroupId = GroupConstants.DEFAULT_PARENT_GROUP_ID;

		if (parentOrganizationId !=
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

			Organization parentOrganization =
				organizationPersistence.fetchByPrimaryKey(parentOrganizationId);

			if (parentOrganization != null) {
				Group parentGroup = parentOrganization.getGroup();

				if (site && parentGroup.isSite()) {
					parentGroupId = parentOrganization.getGroupId();
				}
			}
		}

		Group group = _groupLocalService.addGroup(
			userId, parentGroupId, Organization.class.getName(), organizationId,
			GroupConstants.DEFAULT_LIVE_GROUP_ID, getLocalizationMap(name),
			null, GroupConstants.TYPE_SITE_PRIVATE, false,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, site, true,
			null);

		// Role

		Role role = _roleLocalService.getRole(
			organization.getCompanyId(), RoleConstants.ORGANIZATION_OWNER);

		_userGroupRoleLocalService.addUserGroupRoles(
			userId, group.getGroupId(), new long[] {role.getRoleId()});

		// Resources

		addOrganizationResources(userId, organization);

		// Asset

		if (serviceContext != null) {
			updateAsset(
				userId, organization, serviceContext.getAssetCategoryIds(),
				serviceContext.getAssetTagNames());
		}

		// Indexer

		if ((serviceContext == null) || serviceContext.isIndexingEnabled()) {
			Indexer<Organization> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(Organization.class);

			indexer.reindex(organization);
		}

		return organization;
	}

	/**
	 * Adds a resource for each type of permission available on the
	 * organization.
	 *
	 * @param userId the primary key of the creator/owner of the organization
	 * @param organization the organization
	 */
	@Override
	public void addOrganizationResources(long userId, Organization organization)
		throws PortalException {

		_resourceLocalService.addResources(
			organization.getCompanyId(), 0, userId,
			Organization.class.getName(), organization.getOrganizationId(),
			false, false, false);
	}

	@Override
	public User addOrganizationUserByEmailAddress(
			String emailAddress, long organizationId,
			ServiceContext serviceContext)
		throws PortalException {

		Organization organization = organizationPersistence.findByPrimaryKey(
			organizationId);

		User user = _userLocalService.fetchUserByEmailAddress(
			serviceContext.getCompanyId(), emailAddress);

		if (user == null) {
			Group group = organization.getGroup();

			long[] groupIds = {group.getGroupId()};

			if (serviceContext.getScopeGroupId() > 0) {
				groupIds = ArrayUtil.append(
					groupIds, serviceContext.getScopeGroupId());
			}

			user = _userLocalService.addUserWithWorkflow(
				serviceContext.getUserId(), serviceContext.getCompanyId(), true,
				StringPool.BLANK, StringPool.BLANK, true, StringPool.BLANK,
				emailAddress, serviceContext.getLocale(), emailAddress,
				StringPool.BLANK, emailAddress, 0, 0, true, 1, 1, 1970,
				StringPool.BLANK, UserConstants.TYPE_REGULAR, groupIds, null,
				null, null, true, serviceContext);
		}

		_userLocalService.addOrganizationUser(organizationId, user);

		return user;
	}

	@Override
	public Organization addOrUpdateOrganization(
			String externalReferenceCode, long userId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean hasLogo, byte[] logoBytes, boolean site,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		Organization organization = organizationPersistence.fetchByERC_C(
			externalReferenceCode, user.getCompanyId());

		if (organization == null) {
			organization = addOrganization(
				externalReferenceCode, userId, parentOrganizationId, name, type,
				regionId, countryId, statusListTypeId, comments, site,
				serviceContext);

			UserFileUploadsSettings userFileUploadsSettings =
				_userFileUploadsSettingsSnapshot.get();

			PortalUtil.updateImageId(
				organization, hasLogo, logoBytes, "logoId",
				userFileUploadsSettings.getImageMaxSize(),
				userFileUploadsSettings.getImageMaxHeight(),
				userFileUploadsSettings.getImageMaxWidth());

			organization = organizationPersistence.update(organization);
		}
		else {
			organization = updateOrganization(
				externalReferenceCode, user.getCompanyId(),
				organization.getOrganizationId(), parentOrganizationId, name,
				type, regionId, countryId, statusListTypeId, comments, hasLogo,
				logoBytes, site, serviceContext);
		}

		return organization;
	}

	/**
	 * Assigns the password policy to the organizations, removing any other
	 * currently assigned password policies.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param organizationIds the primary keys of the organizations
	 */
	@Override
	public void addPasswordPolicyOrganizations(
		long passwordPolicyId, long[] organizationIds) {

		_passwordPolicyRelLocalService.addPasswordPolicyRels(
			passwordPolicyId, Organization.class.getName(), organizationIds);
	}

	@Override
	public void addUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws PortalException {

		Organization organization = organizationPersistence.findByPrimaryKey(
			organizationId);

		User user = _userPersistence.findByC_EA(
			organization.getCompanyId(), emailAddress);

		_userLocalService.addOrganizationUser(organizationId, user);
	}

	/**
	 * Deletes the organization's logo.
	 *
	 * @param organizationId the primary key of the organization
	 */
	@Override
	public void deleteLogo(long organizationId) throws PortalException {
		PortalUtil.updateImageId(
			getOrganization(organizationId), false, null, "logoId", 0, 0, 0);
	}

	/**
	 * Deletes the organization. The organization's associated resources and
	 * assets are also deleted.
	 *
	 * @param  organizationId the primary key of the organization
	 * @return the deleted organization
	 */
	@Override
	public Organization deleteOrganization(long organizationId)
		throws PortalException {

		Organization organization = organizationPersistence.findByPrimaryKey(
			organizationId);

		return organizationLocalService.deleteOrganization(organization);
	}

	/**
	 * Deletes the organization. The organization's associated resources and
	 * assets are also deleted.
	 *
	 * @param  organization the organization
	 * @return the deleted organization
	 */
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public Organization deleteOrganization(Organization organization)
		throws PortalException {

		if (!PortalInstances.isCurrentCompanyInDeletionProcess()) {
			int count1 = organizationPersistence.countByC_P(
				organization.getCompanyId(), organization.getOrganizationId());
			int count2 = _userFinder.countByKeywords(
				organization.getCompanyId(), null,
				WorkflowConstants.STATUS_APPROVED,
				LinkedHashMapBuilder.<String, Object>put(
					"usersOrgs", Long.valueOf(organization.getOrganizationId())
				).build());

			if ((count1 > 0) || (count2 > 0)) {
				throw new RequiredOrganizationException();
			}
		}

		// Asset

		_assetEntryLocalService.deleteEntry(
			Organization.class.getName(), organization.getOrganizationId());

		// Addresses

		_addressLocalService.deleteAddresses(
			organization.getCompanyId(), Organization.class.getName(),
			organization.getOrganizationId());

		// Email addresses

		_emailAddressLocalService.deleteEmailAddresses(
			organization.getCompanyId(), Organization.class.getName(),
			organization.getOrganizationId());

		// Expando

		_expandoRowLocalService.deleteRows(organization.getOrganizationId());

		// Password policy relation

		_passwordPolicyRelLocalService.deletePasswordPolicyRel(
			Organization.class.getName(), organization.getOrganizationId());

		// Phone

		_phoneLocalService.deletePhones(
			organization.getCompanyId(), Organization.class.getName(),
			organization.getOrganizationId());

		// Website

		_websiteLocalService.deleteWebsites(
			organization.getCompanyId(), Organization.class.getName(),
			organization.getOrganizationId());

		// Group

		Group group = organization.getGroup();

		if (group.isSite()) {
			group.setSite(false);

			group = _groupPersistence.update(group);
		}

		_groupLocalService.deleteGroup(group);

		// Resources

		_resourceLocalService.deleteResource(
			organization.getCompanyId(), Organization.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			organization.getOrganizationId());

		// Organization

		organizationPersistence.remove(organization);

		return organization;
	}

	@Override
	public void deleteUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws PortalException {

		Organization organization = organizationPersistence.findByPrimaryKey(
			organizationId);

		User user = _userPersistence.findByC_EA(
			organization.getCompanyId(), emailAddress);

		_userLocalService.unsetOrganizationUsers(
			organizationId, new long[] {user.getUserId()});
	}

	/**
	 * Returns the organization with the name.
	 *
	 * @param  companyId the primary key of the organization's company
	 * @param  name the organization's name
	 * @return the organization with the name, or <code>null</code> if no
	 *         organization could be found
	 */
	@Override
	public Organization fetchOrganization(long companyId, String name) {
		return organizationPersistence.fetchByC_N(companyId, name);
	}

	@Override
	public String[] getChildrenTypes(String type) {
		OrganizationTypesSettings organizationTypesSettings =
			_organizationTypesSettingsSnapshot.get();

		return organizationTypesSettings.getChildrenTypes(type);
	}

	@Override
	public List<Organization> getGroupUserOrganizations(
			long groupId, long userId)
		throws PortalException {

		long[] groupOrganizationIds =
			_groupPersistence.getOrganizationPrimaryKeys(groupId);

		if (groupOrganizationIds.length == 0) {
			return Collections.emptyList();
		}

		long[] userOrganizationIds =
			_userPersistence.getOrganizationPrimaryKeys(userId);

		if (userOrganizationIds.length == 0) {
			return Collections.emptyList();
		}

		Set<Long> organizationIds = SetUtil.intersect(
			groupOrganizationIds, userOrganizationIds);

		if (organizationIds.isEmpty()) {
			return Collections.emptyList();
		}

		List<Organization> organizations = new ArrayList<>(
			organizationIds.size());

		for (Long organizationId : organizationIds) {
			organizations.add(
				organizationPersistence.findByPrimaryKey(organizationId));
		}

		return organizations;
	}

	@Override
	public List<Organization> getNoAssetOrganizations() {
		return organizationFinder.findO_ByNoAssets();
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Organization getOrAddEmptyOrganization(
			String externalReferenceCode, long companyId, long userId,
			String name)
		throws PortalException {

		return EmptyModelManagerUtil.getOrAddEmptyModel(
			Organization.class, companyId, externalReferenceCode,
			this::fetchOrganizationByExternalReferenceCode,
			this::getOrganizationByExternalReferenceCode,
			() -> {
				String organizationName = name;

				if (Validator.isNull(name) ||
					(fetchOrganization(companyId, name) != null)) {

					organizationName = externalReferenceCode;
				}

				String[] types = getTypes();

				ListType listType = _listTypeLocalService.getListType(
					companyId, ListTypeConstants.ORGANIZATION_STATUS_DEFAULT,
					ListTypeConstants.ORGANIZATION_STATUS);

				return addOrganization(
					externalReferenceCode, userId, 0, organizationName,
					types[0], 0, 0, listType.getListTypeId(), StringPool.BLANK,
					false, null);
			});
	}

	/**
	 * Returns the organization with the name.
	 *
	 * @param  companyId the primary key of the organization's company
	 * @param  name the organization's name
	 * @return the organization with the name
	 */
	@Override
	public Organization getOrganization(long companyId, String name)
		throws PortalException {

		return organizationPersistence.findByC_N(companyId, name);
	}

	/**
	 * Returns the primary key of the organization with the name.
	 *
	 * @param  companyId the primary key of the organization's company
	 * @param  name the organization's name
	 * @return the primary key of the organization with the name, or
	 *         <code>0</code> if the organization could not be found
	 */
	@Override
	public long getOrganizationId(long companyId, String name) {
		Organization organization = organizationPersistence.fetchByC_N(
			companyId, name);

		if (organization != null) {
			return organization.getOrganizationId();
		}

		return 0;
	}

	@Override
	public List<Organization> getOrganizations(
			long userId, int start, int end,
			OrderByComparator<Organization> orderByComparator)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		List<Organization> organizations = ListUtil.copy(
			_userPersistence.getOrganizations(userId));

		Iterator<Organization> iterator = organizations.iterator();

		while (iterator.hasNext()) {
			Organization organization = iterator.next();

			if (organization.getCompanyId() != user.getCompanyId()) {
				iterator.remove();
			}
		}

		if (organizations.isEmpty()) {
			return organizations;
		}

		if (orderByComparator == null) {
			orderByComparator = OrganizationNameComparator.getInstance(true);
		}

		Collections.sort(organizations, orderByComparator);

		return ListUtil.subList(organizations, start, end);
	}

	/**
	 * Returns all the organizations belonging to the parent organization.
	 *
	 * @param  companyId the primary key of the organization's company
	 * @param  parentOrganizationId the primary key of the organization's parent
	 *         organization
	 * @return the organizations belonging to the parent organization
	 */
	@Override
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId) {

		return getOrganizations(
			companyId, parentOrganizationId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the organizations belonging to the parent
	 * organization.
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
	 * @param  companyId the primary key of the organization's company
	 * @param  parentOrganizationId the primary key of the organization's parent
	 *         organization
	 * @param  start the lower bound of the range of organizations to return
	 * @param  end the upper bound of the range of organizations to return (not
	 *         inclusive)
	 * @return the range of organizations belonging to the parent organization
	 * @see    com.liferay.portal.kernel.service.persistence.OrganizationPersistence#findByC_P(
	 *         long, long, int, int)
	 */
	@Override
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, int start, int end) {

		if (parentOrganizationId ==
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID) {

			return organizationPersistence.findByCompanyId(
				companyId, start, end);
		}

		return organizationPersistence.findByC_P(
			companyId, parentOrganizationId, start, end);
	}

	@Override
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, String name, int start,
		int end) {

		if (Validator.isNull(name)) {
			return organizationPersistence.findByC_P(
				companyId, parentOrganizationId, start, end);
		}

		return organizationPersistence.findByC_P_LikeN(
			companyId, parentOrganizationId, name, start, end);
	}

	@Override
	public List<Organization> getOrganizations(
		long companyId, String treePath) {

		return organizationPersistence.findByC_LikeT(companyId, treePath);
	}

	@Override
	public List<Organization> getOrganizations(
		long companyId, String name, int start, int end,
		OrderByComparator<Organization> orderByComparator) {

		if (Validator.isNull(name)) {
			return organizationPersistence.findByCompanyId(
				companyId, start, end, orderByComparator);
		}

		return organizationPersistence.findByC_LikeN(
			companyId, StringUtil.quote(name, StringPool.PERCENT), start, end,
			orderByComparator);
	}

	/**
	 * Returns the organizations with the primary keys.
	 *
	 * @param  organizationIds the primary keys of the organizations
	 * @return the organizations with the primary keys
	 */
	@Override
	public List<Organization> getOrganizations(long[] organizationIds)
		throws PortalException {

		List<Organization> organizations = new ArrayList<>(
			organizationIds.length);

		for (long organizationId : organizationIds) {
			organizations.add(getOrganization(organizationId));
		}

		return organizations;
	}

	/**
	 * Returns all the organizations and users belonging to the parent
	 * organization.
	 *
	 * @param  companyId the primary key of the organization and user's company
	 * @param  parentOrganizationId the primary key of the organization and
	 *         user's parent organization
	 * @param  status the user's workflow status
	 * @param  start the lower bound of the range of organizations and users to
	 *         return
	 * @param  end the upper bound of the range of organizations and users to
	 *         return (not inclusive)
	 * @param  orderByComparator the comparator to order the organizations and
	 *         users (optionally <code>null</code>)
	 * @return the organizations and users belonging to the parent organization
	 */
	@Override
	public List<Object> getOrganizationsAndUsers(
		long companyId, long parentOrganizationId, int status, int start,
		int end, OrderByComparator<?> orderByComparator) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, false, 0, false, start, end,
			(OrderByComparator<Object>)orderByComparator);

		return organizationFinder.findO_U_ByC_P(
			companyId, parentOrganizationId, queryDefinition);
	}

	/**
	 * Returns the number of organizations and users belonging to the parent
	 * organization.
	 *
	 * @param  companyId the primary key of the organization and user's company
	 * @param  parentOrganizationId the primary key of the organization and
	 *         user's parent organization
	 * @param  status the user's workflow status
	 * @return the number of organizations and users belonging to the parent
	 *         organization
	 */
	@Override
	public int getOrganizationsAndUsersCount(
		long companyId, long parentOrganizationId, int status) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, false, 0, false, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);

		return organizationFinder.countO_U_ByC_P(
			companyId, parentOrganizationId, queryDefinition);
	}

	public List<Organization> getOrganizationsByLogoId(long logoId) {
		return organizationPersistence.findByLogoId(logoId);
	}

	/**
	 * Returns the number of organizations belonging to the parent organization.
	 *
	 * @param  companyId the primary key of the organization's company
	 * @param  parentOrganizationId the primary key of the organization's parent
	 *         organization
	 * @return the number of organizations belonging to the parent organization
	 */
	@Override
	public int getOrganizationsCount(
		long companyId, long parentOrganizationId) {

		if (parentOrganizationId ==
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID) {

			return organizationPersistence.countByCompanyId(companyId);
		}

		return organizationPersistence.countByC_P(
			companyId, parentOrganizationId);
	}

	@Override
	public int getOrganizationsCount(
		long companyId, long parentOrganizationId, String name) {

		if (Validator.isNull(name)) {
			return organizationPersistence.countByC_P(
				companyId, parentOrganizationId);
		}

		return organizationPersistence.countByC_P_LikeN(
			companyId, parentOrganizationId, name);
	}

	@Override
	public int getOrganizationsCount(long companyId, String name) {
		if (Validator.isNull(name)) {
			return organizationPersistence.countByCompanyId(companyId);
		}

		return organizationPersistence.countByC_LikeN(
			companyId, StringUtil.quote(name, StringPool.PERCENT));
	}

	/**
	 * Returns the parent organizations in order by closest ancestor. The list
	 * starts with the organization itself.
	 *
	 * @param  organizationId the primary key of the organization
	 * @return the parent organizations in order by closest ancestor
	 */
	@Override
	public List<Organization> getParentOrganizations(long organizationId)
		throws PortalException {

		if (organizationId ==
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

			return new ArrayList<>();
		}

		Organization organization = organizationPersistence.findByPrimaryKey(
			organizationId);

		return organization.getAncestors();
	}

	/**
	 * Returns the suborganizations of the organizations.
	 *
	 * @param  organizations the organizations from which to get
	 *         suborganizations
	 * @return the suborganizations of the organizations
	 */
	@Override
	public List<Organization> getSuborganizations(
		List<Organization> organizations) {

		List<Organization> allSuborganizations = new ArrayList<>();

		for (Organization organization : organizations) {
			List<Organization> suborganizations =
				organizationPersistence.findByC_P(
					organization.getCompanyId(),
					organization.getOrganizationId());

			addSuborganizations(allSuborganizations, suborganizations);
		}

		return allSuborganizations;
	}

	/**
	 * Returns the suborganizations of the organization.
	 *
	 * @param  companyId the primary key of the organization's company
	 * @param  organizationId the primary key of the organization
	 * @return the suborganizations of the organization
	 */
	@Override
	public List<Organization> getSuborganizations(
		long companyId, long organizationId) {

		return organizationPersistence.findByC_P(companyId, organizationId);
	}

	/**
	 * Returns the count of suborganizations of the organization.
	 *
	 * @param  companyId the primary key of the organization's company
	 * @param  organizationId the primary key of the organization
	 * @return the count of suborganizations of the organization
	 */
	@Override
	public int getSuborganizationsCount(long companyId, long organizationId) {
		return organizationPersistence.countByC_P(companyId, organizationId);
	}

	/**
	 * Returns the intersection of <code>allOrganizations</code> and
	 * <code>availableOrganizations</code>.
	 *
	 * @param  allOrganizations the organizations to check for availability
	 * @param  availableOrganizations the available organizations
	 * @return the intersection of <code>allOrganizations</code> and
	 *         <code>availableOrganizations</code>
	 */
	@Override
	public List<Organization> getSubsetOrganizations(
		List<Organization> allOrganizations,
		List<Organization> availableOrganizations) {

		return TransformUtil.transform(
			allOrganizations,
			organization -> {
				if (availableOrganizations.contains(organization)) {
					return organization;
				}

				return null;
			});
	}

	@Override
	public String[] getTypes() {
		OrganizationTypesSettings organizationTypesSettings =
			_organizationTypesSettingsSnapshot.get();

		return organizationTypesSettings.getTypes();
	}

	/**
	 * Returns all the IDs of organizations with which the user is explicitly
	 * associated, optionally including the IDs of organizations that the user
	 * administers or owns.
	 *
	 * <p>
	 * A user is considered to be <i>explicitly</i> associated with an
	 * organization if his account is individually created within the
	 * organization or if the user is later added to it.
	 * </p>
	 *
	 * @param  userId the primary key of the user
	 * @param  includeAdministrative whether to include the IDs of organizations
	 *         that the user administers or owns, even if he's not a member of
	 *         the organizations
	 * @return the IDs of organizations with which the user is explicitly
	 *         associated, optionally including the IDs of organizations that
	 *         the user administers or owns
	 */
	@Override
	public long[] getUserOrganizationIds(
			long userId, boolean includeAdministrative)
		throws PortalException {

		if (!includeAdministrative) {
			return _userPersistence.getOrganizationPrimaryKeys(userId);
		}

		Set<Long> organizationIds = SetUtil.fromArray(
			_userPersistence.getOrganizationPrimaryKeys(userId));

		List<UserGroupRole> userGroupRoles =
			_userGroupRoleLocalService.getUserGroupRoles(userId);

		for (UserGroupRole userGroupRole : userGroupRoles) {
			Role role = userGroupRole.getRole();

			String roleName = role.getName();

			if (roleName.equals(RoleConstants.ORGANIZATION_ADMINISTRATOR) ||
				roleName.equals(RoleConstants.ORGANIZATION_OWNER)) {

				Group group = userGroupRole.getGroup();

				organizationIds.add(group.getOrganizationId());
			}
		}

		return ArrayUtil.toLongArray(organizationIds);
	}

	/**
	 * Returns all the organizations with which the user is explicitly
	 * associated, optionally including the organizations that the user
	 * administers or owns.
	 *
	 * <p>
	 * A user is considered to be <i>explicitly</i> associated with an
	 * organization if his account is individually created within the
	 * organization or if the user is later added as a member.
	 * </p>
	 *
	 * @param  userId the primary key of the user
	 * @param  includeAdministrative whether to include the IDs of organizations
	 *         that the user administers or owns, even if he's not a member of
	 *         the organizations
	 * @return the organizations with which the user is explicitly associated,
	 *         optionally including the organizations that the user administers
	 *         or owns
	 */
	@Override
	public List<Organization> getUserOrganizations(
			long userId, boolean includeAdministrative)
		throws PortalException {

		if (!includeAdministrative) {
			return getUserOrganizations(userId);
		}

		Set<Organization> organizations = new HashSet<>(
			getUserOrganizations(userId));

		List<UserGroupRole> userGroupRoles =
			_userGroupRoleLocalService.getUserGroupRoles(userId);

		for (UserGroupRole userGroupRole : userGroupRoles) {
			Role role = userGroupRole.getRole();

			String roleName = role.getName();

			if (roleName.equals(RoleConstants.ORGANIZATION_ADMINISTRATOR) ||
				roleName.equals(RoleConstants.ORGANIZATION_OWNER)) {

				Group group = userGroupRole.getGroup();

				Organization organization =
					organizationPersistence.findByPrimaryKey(
						group.getOrganizationId());

				organizations.add(organization);
			}
		}

		return new ArrayList<>(organizations);
	}

	/**
	 * Returns <code>true</code> if the password policy has been assigned to the
	 * organization.
	 *
	 * @param  passwordPolicyId the primary key of the password policy
	 * @param  organizationId the primary key of the organization
	 * @return <code>true</code> if the password policy has been assigned to the
	 *         organization; <code>false</code> otherwise
	 */
	@Override
	public boolean hasPasswordPolicyOrganization(
		long passwordPolicyId, long organizationId) {

		return _passwordPolicyRelLocalService.hasPasswordPolicyRel(
			passwordPolicyId, Organization.class.getName(), organizationId);
	}

	/**
	 * Returns <code>true</code> if the user is a member of the organization,
	 * optionally focusing on suborganizations or the specified organization.
	 * This method is usually called to determine if the user has view access to
	 * a resource belonging to the organization.
	 *
	 * <ol>
	 * <li>
	 * If <code>inheritSuborganizations=<code>false</code></code>:
	 * the method checks whether the user belongs to the organization specified
	 * by <code>organizationId</code>. The parameter
	 * <code>includeSpecifiedOrganization</code> is ignored.
	 * </li>
	 * <li>
	 * The parameter <code>includeSpecifiedOrganization</code> is
	 * ignored unless <code>inheritSuborganizations</code> is also
	 * <code>true</code>.
	 * </li>
	 * <li>
	 * If <code>inheritSuborganizations=<code>true</code></code> and
	 * <code>includeSpecifiedOrganization=<code>false</code></code>: the method
	 * checks
	 * whether the user belongs to one of the child organizations of the one
	 * specified by <code>organizationId</code>.
	 * </li>
	 * <li>
	 * If <code>inheritSuborganizations=<code>true</code></code> and
	 * <code>includeSpecifiedOrganization=<code>true</code></code>: the method
	 * checks whether
	 * the user belongs to the organization specified by
	 * <code>organizationId</code> or any of
	 * its child organizations.
	 * </li>
	 * </ol>
	 *
	 * @param  userId the primary key of the organization's user
	 * @param  organizationId the primary key of the organization
	 * @param  inheritSuborganizations if <code>true</code> suborganizations are
	 *         considered in the determination
	 * @param  includeSpecifiedOrganization if <code>true</code> the
	 *         organization specified by <code>organizationId</code> is
	 *         considered in the determination
	 * @return <code>true</code> if the user has access to the organization;
	 *         <code>false</code> otherwise
	 * @see    com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	@Override
	public boolean hasUserOrganization(
			long userId, long organizationId, boolean inheritSuborganizations,
			boolean includeSpecifiedOrganization)
		throws PortalException {

		if (!inheritSuborganizations) {
			return _userPersistence.containsOrganization(
				userId, organizationId);
		}

		List<Organization> organizationsTree = new ArrayList<>();

		Organization organization = organizationPersistence.findByPrimaryKey(
			organizationId);

		if (includeSpecifiedOrganization) {
			organizationsTree.add(organization);
		}
		else {
			organizationsTree.addAll(organization.getSuborganizations());
		}

		if (ListUtil.isNotEmpty(organizationsTree)) {
			int count = _userFinder.countByUser(
				userId,
				LinkedHashMapBuilder.<String, Object>put(
					"usersOrgsTree", organizationsTree
				).build());

			if (count > 0) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isCountryEnabled(String type) {
		OrganizationTypesSettings organizationTypesSettings =
			_organizationTypesSettingsSnapshot.get();

		return organizationTypesSettings.isCountryEnabled(type);
	}

	@Override
	public boolean isCountryRequired(String type) {
		OrganizationTypesSettings organizationTypesSettings =
			_organizationTypesSettingsSnapshot.get();

		return organizationTypesSettings.isCountryRequired(type);
	}

	@Override
	public boolean isRootable(String type) {
		OrganizationTypesSettings organizationTypesSettings =
			_organizationTypesSettingsSnapshot.get();

		return organizationTypesSettings.isRootable(type);
	}

	/**
	 * Rebuilds the organization's tree.
	 *
	 * <p>
	 * Only call this method if the tree has become stale through operations
	 * other than normal CRUD. Under normal circumstances the tree is
	 * automatically rebuilt whenever necessary.
	 * </p>
	 *
	 * @param companyId the primary key of the organization's company
	 */
	@Override
	public void rebuildTree(long companyId) throws PortalException {
		TreePathUtil.rebuildTree(
			companyId, OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			StringPool.SLASH,
			new TreeModelTasksAdapter<Organization>() {

				@Override
				public List<Organization> findTreeModels(
					long previousId, long companyId, long parentPrimaryKey,
					int size) {

					return organizationPersistence.findByGtO_C_P(
						previousId, companyId, parentPrimaryKey,
						QueryUtil.ALL_POS, size,
						OrganizationIdComparator.getInstance(true));
				}

			});
	}

	/**
	 * Returns an ordered range of all the organizations that match the
	 * keywords, using the indexer. It is preferable to use this method instead
	 * of the non-indexed version whenever possible for performance reasons.
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
	 * @param  companyId the primary key of the organization's company
	 * @param  parentOrganizationId the primary key of the organization's parent
	 *         organization
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         organization's name, street, city, zipcode, type, region or
	 *         country (optionally <code>null</code>)
	 * @param  params the finder parameters (optionally <code>null</code>).
	 * @param  start the lower bound of the range of organizations to return
	 * @param  end the upper bound of the range of organizations to return (not
	 *         inclusive)
	 * @param  sort the field and direction by which to sort (optionally
	 *         <code>null</code>)
	 * @return the matching organizations ordered by name
	 */
	@Override
	public Hits search(
		long companyId, long parentOrganizationId, String keywords,
		LinkedHashMap<String, Object> params, int start, int end, Sort sort) {

		String name = null;
		String type = null;
		String street = null;
		String city = null;
		String zip = null;
		String region = null;
		String country = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			name = keywords;
			type = keywords;
			street = keywords;
			city = keywords;
			zip = keywords;
			region = keywords;
			country = keywords;
		}
		else {
			andOperator = true;
		}

		if (params != null) {
			params.put("keywords", keywords);
		}

		return search(
			companyId, parentOrganizationId, name, type, street, city, zip,
			region, country, params, andOperator, start, end, sort);
	}

	/**
	 * Returns a name ordered range of all the organizations that match the
	 * keywords, type, region, and country, without using the indexer. It is
	 * preferable to use the indexed version {@link #search(long, long, String,
	 * LinkedHashMap, int, int, Sort)} instead of this method wherever possible
	 * for performance reasons.
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
	 * @param  companyId the primary key of the organization's company
	 * @param  parentOrganizationId the primary key of the organization's parent
	 *         organization
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         organization's name, street, city, or zipcode (optionally
	 *         <code>null</code>)
	 * @param  type the organization's type (optionally <code>null</code>)
	 * @param  regionId the primary key of the organization's region (optionally
	 *         <code>null</code>)
	 * @param  countryId the primary key of the organization's country
	 *         (optionally <code>null</code>)
	 * @param  params the finder params. For more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param  start the lower bound of the range of organizations to return
	 * @param  end the upper bound of the range of organizations to return (not
	 *         inclusive)
	 * @return the matching organizations ordered by name
	 * @see    com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	@Override
	public List<Organization> search(
		long companyId, long parentOrganizationId, String keywords, String type,
		Long regionId, Long countryId, LinkedHashMap<String, Object> params,
		int start, int end) {

		return search(
			companyId, parentOrganizationId, keywords, type, regionId,
			countryId, params, start, end,
			OrganizationNameComparator.getInstance(true));
	}

	/**
	 * Returns an ordered range of all the organizations that match the
	 * keywords, type, region, and country, without using the indexer. It is
	 * preferable to use the indexed version {@link #search(long, long, String,
	 * String, String, String, String, String, String, LinkedHashMap, boolean,
	 * int, int, Sort)} instead of this method wherever possible for performance
	 * reasons.
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
	 * @param  companyId the primary key of the organization's company
	 * @param  parentOrganizationId the primary key of the organization's parent
	 *         organization
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         organization's name, street, city, or zipcode (optionally
	 *         <code>null</code>)
	 * @param  type the organization's type (optionally <code>null</code>)
	 * @param  regionId the primary key of the organization's region (optionally
	 *         <code>null</code>)
	 * @param  countryId the primary key of the organization's country
	 *         (optionally <code>null</code>)
	 * @param  params the finder params. For more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param  start the lower bound of the range of organizations to return
	 * @param  end the upper bound of the range of organizations to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the organizations
	 *         (optionally <code>null</code>)
	 * @return the matching organizations ordered by comparator
	 *         <code>orderByComparator</code>
	 * @see    com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	@Override
	public List<Organization> search(
		long companyId, long parentOrganizationId, String keywords, String type,
		Long regionId, Long countryId, LinkedHashMap<String, Object> params,
		int start, int end, OrderByComparator<Organization> orderByComparator) {

		String parentOrganizationIdComparator = StringPool.EQUAL;

		if (parentOrganizationId ==
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID) {

			parentOrganizationIdComparator = StringPool.NOT_EQUAL;
		}

		return organizationFinder.findO_ByKeywords(
			companyId, parentOrganizationId, parentOrganizationIdComparator,
			keywords, type, regionId, countryId, params, start, end,
			orderByComparator);
	}

	/**
	 * Returns a name ordered range of all the organizations with the type,
	 * region, and country, and whose name, street, city, and zipcode match the
	 * keywords specified for them, without using the indexer. It is preferable
	 * to use the indexed version {@link #search(long, long, String, String,
	 * String, String, String, String, String, LinkedHashMap, boolean, int, int,
	 * Sort)} instead of this method wherever possible for performance reasons.
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
	 * @param  companyId the primary key of the organization's company
	 * @param  parentOrganizationId the primary key of the organization's parent
	 * @param  name the name keywords (space separated, optionally
	 *         <code>null</code>)
	 * @param  type the organization's type (optionally <code>null</code>)
	 * @param  street the street keywords (optionally <code>null</code>)
	 * @param  city the city keywords (optionally <code>null</code>)
	 * @param  zip the zipcode keywords (optionally <code>null</code>)
	 * @param  regionId the primary key of the organization's region (optionally
	 *         <code>null</code>)
	 * @param  countryId the primary key of the organization's country
	 *         (optionally <code>null</code>)
	 * @param  params the finder parameters (optionally <code>null</code>). For
	 *         more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field. For example, &quot;organizations with the name
	 *         'Employees' and city 'Chicago'&quot; vs &quot;organizations with
	 *         the name 'Employees' or the city 'Chicago'&quot;.
	 * @param  start the lower bound of the range of organizations to return
	 * @param  end the upper bound of the range of organizations to return (not
	 *         inclusive)
	 * @return the matching organizations ordered by name
	 * @see    com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	@Override
	public List<Organization> search(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end) {

		return search(
			companyId, parentOrganizationId, name, type, street, city, zip,
			regionId, countryId, params, andOperator, start, end,
			OrganizationNameComparator.getInstance(true));
	}

	/**
	 * Returns an ordered range of all the organizations with the type, region,
	 * and country, and whose name, street, city, and zipcode match the keywords
	 * specified for them, without using the indexer. It is preferable to use
	 * the indexed version {@link #search(long, long, String, String, String,
	 * String, String, String, String, LinkedHashMap, boolean, int, int, Sort)}
	 * instead of this method wherever possible for performance reasons.
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
	 * @param  companyId the primary key of the organization's company
	 * @param  parentOrganizationId the primary key of the organization's parent
	 *         organization
	 * @param  name the name keywords (space separated, optionally
	 *         <code>null</code>)
	 * @param  type the organization's type (optionally <code>null</code>)
	 * @param  street the street keywords (optionally <code>null</code>)
	 * @param  city the city keywords (optionally <code>null</code>)
	 * @param  zip the zipcode keywords (optionally <code>null</code>)
	 * @param  regionId the primary key of the organization's region (optionally
	 *         <code>null</code>)
	 * @param  countryId the primary key of the organization's country
	 *         (optionally <code>null</code>)
	 * @param  params the finder parameters (optionally <code>null</code>). For
	 *         more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field. For example, &quot;organizations with the name
	 *         'Employees' and city 'Chicago'&quot; vs &quot;organizations with
	 *         the name 'Employees' or the city 'Chicago'&quot;.
	 * @param  start the lower bound of the range of organizations to return
	 * @param  end the upper bound of the range of organizations to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the organizations
	 *         (optionally <code>null</code>)
	 * @return the matching organizations ordered by comparator
	 *         <code>orderByComparator</code>
	 * @see    com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	@Override
	public List<Organization> search(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<Organization> orderByComparator) {

		String parentOrganizationIdComparator = StringPool.EQUAL;

		if (parentOrganizationId ==
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID) {

			parentOrganizationIdComparator = StringPool.NOT_EQUAL;
		}

		return organizationFinder.findO_ByC_PO_N_T_S_C_Z_R_C(
			companyId, parentOrganizationId, parentOrganizationIdComparator,
			name, type, street, city, zip, regionId, countryId, params,
			andOperator, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the organizations whose name, type, or
	 * location fields match the keywords specified for them, using the indexer.
	 * It is preferable to use this method instead of the non-indexed version
	 * whenever possible for performance reasons.
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
	 * @param  companyId the primary key of the organization's company
	 * @param  parentOrganizationId the primary key of the organization's parent
	 *         organization
	 * @param  name the name keywords (space separated, optionally
	 *         <code>null</code>)
	 * @param  type the type keywords (optionally <code>null</code>)
	 * @param  street the street keywords (optionally <code>null</code>)
	 * @param  city the city keywords (optionally <code>null</code>)
	 * @param  zip the zipcode keywords (optionally <code>null</code>)
	 * @param  region the region keywords (optionally <code>null</code>)
	 * @param  country the country keywords (optionally <code>null</code>)
	 * @param  params the finder parameters (optionally <code>null</code>).
	 * @param  andSearch whether every field must match its keywords or just one
	 *         field
	 * @param  start the lower bound of the range of organizations to return
	 * @param  end the upper bound of the range of organizations to return (not
	 *         inclusive)
	 * @param  sort the field and direction by which to sort (optionally
	 *         <code>null</code>)
	 * @return the matching organizations ordered by <code>sort</code>
	 */
	@Override
	public Hits search(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, String region, String country,
		LinkedHashMap<String, Object> params, boolean andSearch, int start,
		int end, Sort sort) {

		try {
			Indexer<Organization> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(Organization.class);

			SearchContext searchContext = buildSearchContext(
				companyId, parentOrganizationId, name, type, street, city, zip,
				region, country, params, andSearch, start, end, sort);

			return indexer.search(searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	/**
	 * Returns the number of organizations that match the keywords, type,
	 * region, and country.
	 *
	 * @param  companyId the primary key of the organization's company
	 * @param  parentOrganizationId the primary key of the organization's parent
	 *         organization
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         organization's name, street, city, or zipcode (optionally
	 *         <code>null</code>)
	 * @param  type the organization's type (optionally <code>null</code>)
	 * @param  regionId the primary key of the organization's region (optionally
	 *         <code>null</code>)
	 * @param  countryId the primary key of the organization's country
	 *         (optionally <code>null</code>)
	 * @param  params the finder parameters (optionally <code>null</code>). For
	 *         more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @return the number of matching organizations
	 * @see    com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	@Override
	public int searchCount(
		long companyId, long parentOrganizationId, String keywords, String type,
		Long regionId, Long countryId, LinkedHashMap<String, Object> params) {

		Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			Organization.class);

		if (!indexer.isIndexerEnabled() ||
			!PropsValues.ORGANIZATIONS_SEARCH_WITH_INDEX ||
			isUseCustomSQL(params)) {

			String parentOrganizationIdComparator = StringPool.EQUAL;

			if (parentOrganizationId ==
					OrganizationConstants.ANY_PARENT_ORGANIZATION_ID) {

				parentOrganizationIdComparator = StringPool.NOT_EQUAL;
			}

			return organizationFinder.countO_ByKeywords(
				companyId, parentOrganizationId, parentOrganizationIdComparator,
				keywords, type, regionId, countryId, params);
		}

		try {
			String name = null;
			String street = null;
			String city = null;
			String zip = null;
			boolean andOperator = false;

			if (Validator.isNotNull(keywords)) {
				name = keywords;
				street = keywords;
				city = keywords;
				zip = keywords;
			}
			else {
				andOperator = true;
			}

			if (params != null) {
				params.put("keywords", keywords);
			}

			SearchContext searchContext = buildSearchContext(
				companyId, parentOrganizationId, name, type, street, city, zip,
				regionId, countryId, params, andOperator, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

			return (int)indexer.searchCount(searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	/**
	 * Returns the number of organizations with the type, region, and country,
	 * and whose name, street, city, and zipcode match the keywords specified
	 * for them.
	 *
	 * @param  companyId the primary key of the organization's company
	 * @param  parentOrganizationId the primary key of the organization's parent
	 *         organization
	 * @param  name the name keywords (space separated, optionally
	 *         <code>null</code>)
	 * @param  type the organization's type (optionally <code>null</code>)
	 * @param  street the street keywords (optionally <code>null</code>)
	 * @param  city the city keywords (optionally <code>null</code>)
	 * @param  zip the zipcode keywords (optionally <code>null</code>)
	 * @param  regionId the primary key of the organization's region (optionally
	 *         <code>null</code>)
	 * @param  countryId the primary key of the organization's country
	 *         (optionally <code>null</code>)
	 * @param  params the finder parameters (optionally <code>null</code>). For
	 *         more information see {@link
	 *         com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param  andOperator whether every field must match its keywords, or just
	 *         one field. For example, &quot;organizations with the name
	 *         'Employees' and city 'Chicago'&quot; vs &quot;organizations with
	 *         the name 'Employees' or the city 'Chicago'&quot;.
	 * @return the number of matching organizations
	 * @see    com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	@Override
	public int searchCount(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		LinkedHashMap<String, Object> params, boolean andOperator) {

		Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			Organization.class);

		if (!indexer.isIndexerEnabled() ||
			!PropsValues.ORGANIZATIONS_SEARCH_WITH_INDEX ||
			isUseCustomSQL(params)) {

			String parentOrganizationIdComparator = StringPool.EQUAL;

			if (parentOrganizationId ==
					OrganizationConstants.ANY_PARENT_ORGANIZATION_ID) {

				parentOrganizationIdComparator = StringPool.NOT_EQUAL;
			}

			return organizationFinder.countO_ByC_PO_N_T_S_C_Z_R_C(
				companyId, parentOrganizationId, parentOrganizationIdComparator,
				name, type, street, city, zip, regionId, countryId, params,
				andOperator);
		}

		try {
			SearchContext searchContext = buildSearchContext(
				companyId, parentOrganizationId, name, type, street, city, zip,
				regionId, countryId, params, andOperator, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

			return (int)indexer.searchCount(searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public BaseModelSearchResult<Organization> searchOrganizations(
			long companyId, long parentOrganizationId, String keywords,
			LinkedHashMap<String, Object> params, int start, int end, Sort sort)
		throws PortalException {

		String name = null;
		String type = null;
		String street = null;
		String city = null;
		String zip = null;
		String region = null;
		String country = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			name = keywords;
			type = keywords;
			street = keywords;
			city = keywords;
			zip = keywords;
			region = keywords;
			country = keywords;
		}
		else {
			andOperator = true;
		}

		if (params != null) {
			params.put("keywords", keywords);
		}

		return searchOrganizations(
			companyId, parentOrganizationId, name, type, street, city, zip,
			region, country, params, andOperator, start, end, sort);
	}

	@Override
	public BaseModelSearchResult<Organization> searchOrganizations(
			long companyId, long parentOrganizationId, String name, String type,
			String street, String city, String zip, String region,
			String country, LinkedHashMap<String, Object> params,
			boolean andSearch, int start, int end, Sort sort)
		throws PortalException {

		Indexer<Organization> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			Organization.class);

		SearchContext searchContext = buildSearchContext(
			companyId, parentOrganizationId, name, type, street, city, zip,
			region, country, params, andSearch, start, end, sort);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext);

			List<Organization> organizations = UsersAdminUtil.getOrganizations(
				hits);

			if (organizations != null) {
				return new BaseModelSearchResult<>(
					organizations, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	/**
	 * Returns the organizations and users that match the keywords specified for
	 * them and belong to the parent organization.
	 *
	 * @param  companyId the primary key of the organization and user's company
	 * @param  parentOrganizationId the primary key of the organization and
	 *         user's parent organization
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         organization's name, type, or location fields or user's first
	 *         name, middle name, last name, screen name, email address, or
	 *         address fields
	 * @param  status user's workflow status
	 * @param  params the finder parameters (optionally <code>null</code>).
	 * @param  start the lower bound of the range of organizations and users to
	 *         return
	 * @param  end the upper bound of the range of organizations and users to
	 *         return (not inclusive)
	 * @return the matching organizations and users
	 */
	@Override
	public Hits searchOrganizationsAndUsers(
			long companyId, long parentOrganizationId, String keywords,
			int status, LinkedHashMap<String, Object> params, int start,
			int end, Sort[] sorts)
		throws PortalException {

		Indexer<?> indexer = OrganizationUsersSearcher.getInstance();

		SearchContext searchContext = buildSearchContext(
			companyId, parentOrganizationId, keywords, status, params, start,
			end, sorts);

		return indexer.search(searchContext);
	}

	/**
	 * Returns the number of organizations and users that match the keywords
	 * specified for them and belong to the parent organization.
	 *
	 * @param  companyId the primary key of the organization and user's company
	 * @param  parentOrganizationId the primary key of the organization and
	 *         user's parent organization
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         organization's name, type, or location fields or user's first
	 *         name, middle name, last name, screen name, email address, or
	 *         address fields
	 * @param  status user's workflow status
	 * @param  params the finder parameters (optionally <code>null</code>).
	 * @return the number of matching organizations and users
	 */
	@Override
	public int searchOrganizationsAndUsersCount(
			long companyId, long parentOrganizationId, String keywords,
			int status, LinkedHashMap<String, Object> params)
		throws PortalException {

		Indexer<?> indexer = OrganizationUsersSearcher.getInstance();

		SearchContext searchContext = buildSearchContext(
			companyId, parentOrganizationId, keywords, status, params,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Hits hits = indexer.search(searchContext);

		return hits.getLength();
	}

	/**
	 * Removes the organizations from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	@Override
	public void unsetGroupOrganizations(long groupId, long[] organizationIds) {
		_groupPersistence.removeOrganizations(groupId, organizationIds);

		try {
			reindexUsers(organizationIds);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	/**
	 * Removes the organizations from the password policy.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param organizationIds the primary keys of the organizations
	 */
	@Override
	public void unsetPasswordPolicyOrganizations(
		long passwordPolicyId, long[] organizationIds) {

		_passwordPolicyRelLocalService.deletePasswordPolicyRels(
			passwordPolicyId, Organization.class.getName(), organizationIds);
	}

	/**
	 * Updates the organization's asset with the new asset categories and tag
	 * names, removing and adding asset categories and tag names as necessary.
	 *
	 * @param userId the primary key of the user
	 * @param organization the organization
	 * @param assetCategoryIds the primary keys of the asset categories
	 * @param assetTagNames the asset tag names
	 */
	@Override
	public void updateAsset(
			long userId, Organization organization, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		Company company = _companyPersistence.findByPrimaryKey(
			user.getCompanyId());

		Group companyGroup = company.getGroup();

		_assetEntryLocalService.updateEntry(
			userId, companyGroup.getGroupId(), null, null,
			Organization.class.getName(), organization.getOrganizationId(),
			organization.getUuid(), 0, assetCategoryIds, assetTagNames, true,
			false, null, null, null, null, null, organization.getName(),
			StringPool.BLANK, null, null, null, 0, 0, null);
	}

	@Override
	public Organization updateLogo(long organizationId, byte[] logoBytes)
		throws PortalException {

		Organization organization = organizationPersistence.findByPrimaryKey(
			organizationId);

		UserFileUploadsSettings userFileUploadsSettings =
			_userFileUploadsSettingsSnapshot.get();

		PortalUtil.updateImageId(
			organization, true, logoBytes, "logoId",
			userFileUploadsSettings.getImageMaxSize(),
			userFileUploadsSettings.getImageMaxHeight(),
			userFileUploadsSettings.getImageMaxWidth());

		return organizationPersistence.update(organization);
	}

	/**
	 * Updates the organization.
	 *
	 * @param  companyId the primary key of the organization's company
	 * @param  organizationId the primary key of the organization
	 * @param  parentOrganizationId the primary key of organization's parent
	 *         organization
	 * @param  name the organization's name
	 * @param  type the organization's type
	 * @param  regionId the primary key of the organization's region
	 * @param  countryId the primary key of the organization's country
	 * @param  statusListTypeId the organization's workflow status
	 * @param  comments the comments about the organization
	 * @param  hasLogo if the organization has a custom logo
	 * @param  logoBytes the new logo image data
	 * @param  site whether the organization is to be associated with a main
	 *         site
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set asset category IDs and asset tag
	 *         names for the organization, and merge expando bridge attributes
	 *         for the organization.
	 * @return the organization
	 */
	@Override
	public Organization updateOrganization(
			String externalReferenceCode, long companyId, long organizationId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean hasLogo, byte[] logoBytes, boolean site,
			ServiceContext serviceContext)
		throws PortalException {

		// Organization

		parentOrganizationId = getParentOrganizationId(
			companyId, parentOrganizationId);

		validate(
			companyId, organizationId, parentOrganizationId, name, type,
			countryId, statusListTypeId, comments);

		Organization organization = organizationPersistence.findByPrimaryKey(
			organizationId);

		long oldParentOrganizationId = organization.getParentOrganizationId();
		String oldName = organization.getName();

		organization.setExternalReferenceCode(externalReferenceCode);
		organization.setParentOrganizationId(parentOrganizationId);
		organization.setTreePath(organization.buildTreePath());
		organization.setName(name);
		organization.setType(type);
		organization.setRecursable(true);
		organization.setRegionId(regionId);
		organization.setCountryId(countryId);
		organization.setStatusListTypeId(statusListTypeId);
		organization.setComments(comments);

		UserFileUploadsSettings userFileUploadsSettings =
			_userFileUploadsSettingsSnapshot.get();

		PortalUtil.updateImageId(
			organization, hasLogo, logoBytes, "logoId",
			userFileUploadsSettings.getImageMaxSize(),
			userFileUploadsSettings.getImageMaxHeight(),
			userFileUploadsSettings.getImageMaxWidth());

		if (organization.getStatus() == WorkflowConstants.STATUS_EMPTY) {
			organization.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		organization.setExpandoBridgeAttributes(serviceContext);

		organization = organizationPersistence.update(organization);

		// Group

		Group group = organization.getGroup();

		long parentGroupId = group.getParentGroupId();

		boolean createSite = false;

		if (!group.isSite() && site) {
			createSite = true;
		}

		boolean organizationGroup = isOrganizationGroup(
			oldParentOrganizationId, group.getParentGroupId());

		if (createSite || organizationGroup) {
			if (parentOrganizationId !=
					OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

				Organization parentOrganization =
					organizationPersistence.fetchByPrimaryKey(
						parentOrganizationId);

				Group parentGroup = parentOrganization.getGroup();

				if (site && parentGroup.isSite()) {
					parentGroupId = parentOrganization.getGroupId();
				}
				else {
					parentGroupId = GroupConstants.DEFAULT_PARENT_GROUP_ID;
				}
			}
			else {
				parentGroupId = GroupConstants.DEFAULT_PARENT_GROUP_ID;
			}
		}

		if (createSite || !oldName.equals(name) || organizationGroup) {
			_groupLocalService.updateGroup(
				group.getGroupId(), parentGroupId, getLocalizationMap(name),
				group.getDescriptionMap(), group.getType(),
				group.isManualMembership(), group.getMembershipRestriction(),
				group.getFriendlyURL(), group.isInheritContent(),
				group.isActive(), null);
		}

		if (group.isSite() != site) {
			_groupLocalService.updateSite(group.getGroupId(), site);

			reindexUsers(organizationId);
		}

		// Organizations

		if (createSite) {
			List<Organization> childOrganizations =
				organizationLocalService.getOrganizations(
					companyId, organizationId);

			for (Organization childOrganization : childOrganizations) {
				Group childGroup = childOrganization.getGroup();

				if (childGroup.isSite() &&
					(childGroup.getParentGroupId() ==
						GroupConstants.DEFAULT_PARENT_GROUP_ID)) {

					childGroup.setParentGroupId(group.getGroupId());

					_groupLocalService.updateGroup(childGroup);
				}
			}
		}

		// Asset

		if (serviceContext != null) {
			updateAsset(
				serviceContext.getUserId(), organization,
				serviceContext.getAssetCategoryIds(),
				serviceContext.getAssetTagNames());
		}

		// Indexer

		Indexer<Organization> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			Organization.class);

		if (!oldName.equals(name) ||
			(oldParentOrganizationId != parentOrganizationId)) {

			long[] reindexOrganizationIds = getReindexOrganizationIds(
				organization);

			List<Organization> reindexOrganizations = new ArrayList<>(
				reindexOrganizationIds.length);

			for (long reindexOrganizationId : reindexOrganizationIds) {
				Organization reindexOrganization = fetchOrganization(
					reindexOrganizationId);

				reindexOrganizations.add(reindexOrganization);
			}

			indexer.reindex(reindexOrganizations);
		}
		else {
			indexer.reindex(organization);
		}

		return organization;
	}

	protected void addSuborganizations(
		List<Organization> allSuborganizations,
		List<Organization> organizations) {

		for (Organization organization : organizations) {
			if (!allSuborganizations.contains(organization)) {
				allSuborganizations.add(organization);

				List<Organization> suborganizations =
					organizationPersistence.findByC_P(
						organization.getCompanyId(),
						organization.getOrganizationId());

				addSuborganizations(allSuborganizations, suborganizations);
			}
		}
	}

	protected SearchContext buildSearchContext(
		long companyId, long parentOrganizationId, String keywords, int status,
		LinkedHashMap<String, Object> params, int start, int end,
		Sort[] sorts) {

		String city = null;
		String country = null;
		String firstName = null;
		String fullName = null;
		String lastName = null;
		String middleName = null;
		String name = null;
		String region = null;
		String screenName = null;
		String street = null;
		String type = null;
		String zip = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			city = keywords;
			country = keywords;
			firstName = keywords;
			fullName = keywords;
			lastName = keywords;
			middleName = keywords;
			name = keywords;
			region = keywords;
			screenName = keywords;
			street = keywords;
			type = keywords;
			zip = keywords;
		}
		else {
			andOperator = true;
		}

		if (params == null) {
			params = new LinkedHashMap<>();
		}

		params.put("keywords", keywords);
		params.put("usersOrgs", parentOrganizationId);

		SearchContext searchContext = buildSearchContext(
			companyId, parentOrganizationId, name, type, street, city, zip,
			region, country, params, andOperator, start, end, null);

		Map<String, Serializable> attributes = searchContext.getAttributes();

		attributes.put("firstName", firstName);
		attributes.put("fullName", fullName);
		attributes.put("lastName", lastName);
		attributes.put("middleName", middleName);
		attributes.put("screenName", screenName);
		attributes.put("status", status);

		searchContext.setAttributes(attributes);

		if (sorts != null) {
			searchContext.setSorts(sorts);
		}

		return searchContext;
	}

	protected SearchContext buildSearchContext(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		LinkedHashMap<String, Object> params, boolean andSearch, int start,
		int end, Sort sort) {

		String regionCode = null;

		if (regionId != null) {
			Region region = _regionPersistence.fetchByPrimaryKey(regionId);

			regionCode = region.getRegionCode();
		}

		String countryName = null;

		if (countryId != null) {
			Country country = _countryPersistence.fetchByPrimaryKey(countryId);

			countryName = country.getName();
		}

		return buildSearchContext(
			companyId, parentOrganizationId, name, type, street, city, zip,
			regionCode, countryName, params, andSearch, start, end, sort);
	}

	protected SearchContext buildSearchContext(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, String region, String country,
		LinkedHashMap<String, Object> params, boolean andSearch, int start,
		int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAndSearch(andSearch);

		Map<String, Serializable> attributes = new HashMap<>();

		attributes.put("city", city);
		attributes.put("country", country);
		attributes.put("name", name);
		attributes.put("params", params);
		attributes.put(
			"parentOrganizationId", String.valueOf(parentOrganizationId));
		attributes.put("region", region);
		attributes.put("street", street);
		attributes.put("type", type);
		attributes.put("zip", zip);

		searchContext.setAttributes(attributes);

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(new long[] {-1L});

		if (params != null) {
			String keywords = (String)params.remove("keywords");

			if (Validator.isNotNull(keywords)) {
				searchContext.setKeywords(keywords);
			}
		}

		if (sort != null) {
			searchContext.setSorts(_getSorts(sort));
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	protected long getParentOrganizationId(
		long companyId, long parentOrganizationId) {

		if (parentOrganizationId !=
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

			// Ensure parent organization exists and belongs to the proper
			// company

			Organization parentOrganization =
				organizationPersistence.fetchByPrimaryKey(parentOrganizationId);

			if ((parentOrganization == null) ||
				(companyId != parentOrganization.getCompanyId())) {

				parentOrganizationId =
					OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID;
			}
		}

		return parentOrganizationId;
	}

	protected long[] getReindexOrganizationIds(Organization organization)
		throws PortalException {

		List<Organization> organizations =
			organizationPersistence.findByC_LikeT(
				organization.getCompanyId(),
				CustomSQLUtil.keywords(
					StringBundler.concat(
						StringPool.FORWARD_SLASH,
						organization.getOrganizationId(),
						StringPool.FORWARD_SLASH))[0],
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				OrganizationNameComparator.getInstance(true));

		long[] organizationIds = new long[organizations.size()];

		for (int i = 0; i < organizations.size(); i++) {
			Organization curOrganization = organizations.get(i);

			curOrganization.setTreePath(curOrganization.buildTreePath());

			curOrganization = organizationPersistence.update(curOrganization);

			organizationIds[i] = curOrganization.getOrganizationId();
		}

		if (!ArrayUtil.contains(
				organizationIds, organization.getOrganizationId())) {

			organizationIds = ArrayUtil.append(
				organizationIds, organization.getOrganizationId());
		}

		return organizationIds;
	}

	protected boolean isOrganizationGroup(long organizationId, long groupId) {
		if ((organizationId ==
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) &&
			(groupId == GroupConstants.DEFAULT_PARENT_GROUP_ID)) {

			return true;
		}

		if (organizationId !=
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

			Organization organization =
				organizationPersistence.fetchByPrimaryKey(organizationId);

			if (organization.getGroupId() == groupId) {
				return true;
			}
		}

		return false;
	}

	protected boolean isParentOrganization(
			long parentOrganizationId, long organizationId)
		throws PortalException {

		// Return true if parentOrganizationId is among the parent organizatons
		// of organizationId

		if (organizationId ==
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

			return false;
		}

		Organization organization = organizationPersistence.findByPrimaryKey(
			organizationId);

		String treePath = organization.getTreePath();

		return treePath.contains(
			StringPool.SLASH + parentOrganizationId + StringPool.SLASH);
	}

	protected boolean isUseCustomSQL(LinkedHashMap<String, Object> params) {
		return MapUtil.isNotEmpty(params);
	}

	protected void reindex(long companyId, long[] userIds)
		throws PortalException {

		ReindexerBridge reindexerBridge = _reindexerBridgeSnapshot.get();

		reindexerBridge.reindex(companyId, User.class.getName(), userIds);
	}

	protected void reindexUsers(List<Organization> organizations)
		throws PortalException {

		for (Organization organization : organizations) {
			reindexUsers(organization);
		}
	}

	protected void reindexUsers(long organizationId) throws PortalException {
		reindexUsers(getOrganization(organizationId));
	}

	protected void reindexUsers(long[] organizationIds) throws PortalException {
		for (long organizationId : organizationIds) {
			reindexUsers(organizationId);
		}
	}

	protected void reindexUsers(Organization organization)
		throws PortalException {

		long[] userIds = getUserPrimaryKeys(organization.getOrganizationId());

		if (ArrayUtil.isNotEmpty(userIds)) {
			long companyId = organization.getCompanyId();

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					reindex(companyId, userIds);

					return null;
				});
		}
	}

	protected void validate(
			long companyId, long organizationId, long parentOrganizationId,
			String name, String type, long countryId, long statusListTypeId,
			String comments)
		throws PortalException {

		if (!ArrayUtil.contains(getTypes(), type)) {
			throw new OrganizationTypeException(
				"Invalid organization type " + type);
		}

		if (parentOrganizationId ==
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

			if (!isRootable(type)) {
				throw new OrganizationParentException.MustBeRootable(type);
			}
		}
		else {
			Organization parentOrganization =
				organizationPersistence.fetchByPrimaryKey(parentOrganizationId);

			if (parentOrganization == null) {
				throw new OrganizationParentException(
					"Organization " + parentOrganizationId + " does not exist");
			}

			String[] childrenTypes = getChildrenTypes(
				parentOrganization.getType());

			if (childrenTypes.length == 0) {
				throw new OrganizationParentException.MustNotHaveChildren(type);
			}

			if ((companyId != parentOrganization.getCompanyId()) ||
				(parentOrganizationId == organizationId)) {

				throw new OrganizationParentException();
			}

			if (!ArrayUtil.contains(childrenTypes, type)) {
				throw new OrganizationParentException.MustHaveValidChildType(
					type, parentOrganization.getType());
			}
		}

		if ((organizationId > 0) &&
			(parentOrganizationId !=
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID)) {

			// Prevent circular organizational references

			if (isParentOrganization(organizationId, parentOrganizationId)) {
				throw new OrganizationParentException();
			}
		}

		if (Validator.isNull(name)) {
			throw new OrganizationNameException.MustNotBeNull();
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			Organization.class.getName(), "name");

		if (name.length() > maxLength) {
			throw new OrganizationNameException.MustNotExceedMaximumLength(
				name, maxLength);
		}

		Organization organization = organizationPersistence.fetchByC_N(
			companyId, name);

		if ((organization != null) &&
			StringUtil.equalsIgnoreCase(organization.getName(), name) &&
			((organizationId <= 0) ||
			 (organization.getOrganizationId() != organizationId))) {

			throw new DuplicateOrganizationException(
				"There is another organization named " + name);
		}

		OrganizationTypesSettings organizationTypesSettings =
			_organizationTypesSettingsSnapshot.get();

		boolean countryRequired = organizationTypesSettings.isCountryRequired(
			type);

		if ((countryRequired && !EmptyModelManagerUtil.isEmptyModel()) ||
			(countryId > 0)) {

			_countryPersistence.findByPrimaryKey(countryId);
		}

		_listTypeLocalService.validate(
			statusListTypeId, ListTypeConstants.ORGANIZATION_STATUS);

		maxLength = ModelHintsUtil.getMaxLength(
			Organization.class.getName(), "comments");

		if (Validator.isNotNull(comments) && (comments.length() > maxLength)) {
			throw new OrganizationCommentsException.MustNotExceedMaximumLength(
				comments, maxLength);
		}
	}

	protected void validate(
			long companyId, long parentOrganizationId, String name, String type,
			long countryId, long statusListTypeId, String comments)
		throws PortalException {

		validate(
			companyId, 0, parentOrganizationId, name, type, countryId,
			statusListTypeId, comments);
	}

	private Sort[] _getSorts(Sort sort) {
		Sort[] sorts = {sort};

		if (Objects.equals(_TYPE_FIELD_NAME, sort.getFieldName())) {
			sorts = ArrayUtil.append(
				sorts,
				SortFactoryUtil.getSort(
					Organization.class, Field.NAME,
					sort.isReverse() ? "desc" : "asc"));
		}

		return sorts;
	}

	private static final String _TYPE_FIELD_NAME = Field.getSortableFieldName(
		Field.TYPE + "_String");

	private static final Snapshot<OrganizationTypesSettings>
		_organizationTypesSettingsSnapshot = new Snapshot<>(
			OrganizationLocalServiceImpl.class,
			OrganizationTypesSettings.class);
	private static final Snapshot<ReindexerBridge> _reindexerBridgeSnapshot =
		new Snapshot<>(
			OrganizationLocalServiceImpl.class, ReindexerBridge.class);
	private static final Snapshot<UserFileUploadsSettings>
		_userFileUploadsSettingsSnapshot = new Snapshot<>(
			OrganizationLocalServiceImpl.class, UserFileUploadsSettings.class);

	@BeanReference(type = AddressLocalService.class)
	private AddressLocalService _addressLocalService;

	@BeanReference(type = AssetEntryLocalService.class)
	private AssetEntryLocalService _assetEntryLocalService;

	@BeanReference(type = CompanyPersistence.class)
	private CompanyPersistence _companyPersistence;

	@BeanReference(type = CountryPersistence.class)
	private CountryPersistence _countryPersistence;

	@BeanReference(type = EmailAddressLocalService.class)
	private EmailAddressLocalService _emailAddressLocalService;

	@BeanReference(type = ExpandoRowLocalService.class)
	private ExpandoRowLocalService _expandoRowLocalService;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = ListTypeLocalService.class)
	private ListTypeLocalService _listTypeLocalService;

	@BeanReference(type = PasswordPolicyRelLocalService.class)
	private PasswordPolicyRelLocalService _passwordPolicyRelLocalService;

	@BeanReference(type = PhoneLocalService.class)
	private PhoneLocalService _phoneLocalService;

	@BeanReference(type = RegionPersistence.class)
	private RegionPersistence _regionPersistence;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = RoleLocalService.class)
	private RoleLocalService _roleLocalService;

	@BeanReference(type = UserFinder.class)
	private UserFinder _userFinder;

	@BeanReference(type = UserGroupRoleLocalService.class)
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@BeanReference(type = UserLocalService.class)
	private UserLocalService _userLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

	@BeanReference(type = WebsiteLocalService.class)
	private WebsiteLocalService _websiteLocalService;

}