/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.OrgLabor;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicyUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.comparator.OrganizationIdComparator;
import com.liferay.portal.service.base.OrganizationServiceBaseImpl;
import com.liferay.portal.service.permission.PasswordPolicyPermissionUtil;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service for accessing, adding, deleting, and updating
 * organizations. Its methods include permission checks.
 *
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Julio Camarero
 */
public class OrganizationServiceImpl extends OrganizationServiceBaseImpl {

	/**
	 * Adds the organizations to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	@Override
	public void addGroupOrganizations(long groupId, long[] organizationIds)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.ASSIGN_MEMBERS);

		organizationLocalService.addGroupOrganizations(
			groupId, organizationIds);
	}

	/**
	 * Adds an organization with additional parameters.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the organization
	 * including its resources, metadata, and internal data structures.
	 * </p>
	 *
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
	 * @param  addresses the organization's addresses
	 * @param  emailAddresses the organization's email addresses
	 * @param  orgLabors the organization's hours of operation
	 * @param  phones the organization's phone numbers
	 * @param  websites the organization's websites
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set asset category IDs, asset tag names,
	 *         and expando bridge attributes for the organization.
	 * @return the organization
	 */
	@Override
	public Organization addOrganization(
			String externalReferenceCode, long parentOrganizationId,
			String name, String type, long regionId, long countryId,
			long statusListTypeId, String comments, boolean site,
			List<Address> addresses, List<EmailAddress> emailAddresses,
			List<OrgLabor> orgLabors, List<Phone> phones,
			List<Website> websites, ServiceContext serviceContext)
		throws PortalException {

		boolean indexingEnabled = true;

		if (serviceContext != null) {
			indexingEnabled = serviceContext.isIndexingEnabled();

			serviceContext.setIndexingEnabled(false);
		}

		try {
			Organization organization = addOrganization(
				externalReferenceCode, parentOrganizationId, name, type,
				regionId, countryId, statusListTypeId, comments, site,
				serviceContext);

			UsersAdminUtil.updateAddresses(
				Organization.class.getName(), organization.getOrganizationId(),
				addresses, ListTypeConstants.ORGANIZATION_ADDRESS);

			UsersAdminUtil.updateEmailAddresses(
				Organization.class.getName(), organization.getOrganizationId(),
				emailAddresses);

			UsersAdminUtil.updateOrgLabors(
				organization.getOrganizationId(), orgLabors);

			UsersAdminUtil.updatePhones(
				Organization.class.getName(), organization.getOrganizationId(),
				phones);

			UsersAdminUtil.updateWebsites(
				Organization.class.getName(), organization.getOrganizationId(),
				websites);

			if (indexingEnabled) {
				Indexer<Organization> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(Organization.class);

				indexer.reindex(organization);
			}

			return organization;
		}
		finally {
			if (serviceContext != null) {
				serviceContext.setIndexingEnabled(indexingEnabled);
			}
		}
	}

	/**
	 * Adds an organization.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the organization
	 * including its resources, metadata, and internal data structures.
	 * </p>
	 *
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
			String externalReferenceCode, long parentOrganizationId,
			String name, String type, long regionId, long countryId,
			long statusListTypeId, String comments, boolean site,
			ServiceContext serviceContext)
		throws PortalException {

		if (parentOrganizationId ==
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

			PortalPermissionUtil.check(
				getPermissionChecker(), ActionKeys.ADD_ORGANIZATION);
		}
		else {
			OrganizationPermissionUtil.check(
				getPermissionChecker(), parentOrganizationId,
				ActionKeys.ADD_ORGANIZATION);
		}

		Organization organization = organizationLocalService.addOrganization(
			externalReferenceCode, getUserId(), parentOrganizationId, name,
			type, regionId, countryId, statusListTypeId, comments, site,
			serviceContext);

		OrganizationMembershipPolicyUtil.verifyPolicy(organization);

		return organization;
	}

	@Override
	public User addOrganizationUserByEmailAddress(
			String emailAddress, long organizationId,
			ServiceContext serviceContext)
		throws PortalException {

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.ASSIGN_MEMBERS);

		return organizationLocalService.addOrganizationUserByEmailAddress(
			emailAddress, organizationId, serviceContext);
	}

	@Override
	public Organization addOrUpdateOrganization(
			String externalReferenceCode, long parentOrganizationId,
			String name, String type, long regionId, long countryId,
			long statusListTypeId, String comments, boolean hasLogo,
			byte[] logoBytes, boolean site, List<Address> addresses,
			List<EmailAddress> emailAddresses, List<OrgLabor> orgLabors,
			List<Phone> phones, List<Website> websites,
			ServiceContext serviceContext)
		throws PortalException {

		User user = getUser();

		Organization organization =
			organizationLocalService.fetchOrganizationByExternalReferenceCode(
				externalReferenceCode, user.getCompanyId());

		if (organization == null) {
			if (parentOrganizationId ==
					OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

				PortalPermissionUtil.check(
					getPermissionChecker(), ActionKeys.ADD_ORGANIZATION);
			}
			else {
				OrganizationPermissionUtil.check(
					getPermissionChecker(), parentOrganizationId,
					ActionKeys.ADD_ORGANIZATION);
			}
		}
		else {
			OrganizationPermissionUtil.check(
				getPermissionChecker(), organization, ActionKeys.UPDATE);

			if (organization.getParentOrganizationId() !=
					parentOrganizationId) {

				if (parentOrganizationId ==
						OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

					PortalPermissionUtil.check(
						getPermissionChecker(), ActionKeys.ADD_ORGANIZATION);
				}
				else {
					OrganizationPermissionUtil.check(
						getPermissionChecker(), parentOrganizationId,
						ActionKeys.ADD_ORGANIZATION);
				}
			}
		}

		organization = organizationLocalService.addOrUpdateOrganization(
			externalReferenceCode, user.getUserId(), parentOrganizationId, name,
			type, regionId, countryId, statusListTypeId, comments, hasLogo,
			logoBytes, site, serviceContext);

		if (addresses != null) {
			UsersAdminUtil.updateAddresses(
				Organization.class.getName(), organization.getOrganizationId(),
				addresses, ListTypeConstants.ORGANIZATION_ADDRESS);
		}

		if (emailAddresses != null) {
			UsersAdminUtil.updateEmailAddresses(
				Organization.class.getName(), organization.getOrganizationId(),
				emailAddresses);
		}

		if (orgLabors != null) {
			UsersAdminUtil.updateOrgLabors(
				organization.getOrganizationId(), orgLabors);
		}

		if (phones != null) {
			UsersAdminUtil.updatePhones(
				Organization.class.getName(), organization.getOrganizationId(),
				phones);
		}

		if (websites != null) {
			UsersAdminUtil.updateWebsites(
				Organization.class.getName(), organization.getOrganizationId(),
				websites);
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
			long passwordPolicyId, long[] organizationIds)
		throws PortalException {

		PasswordPolicyPermissionUtil.check(
			getPermissionChecker(), passwordPolicyId, ActionKeys.UPDATE);

		organizationLocalService.addPasswordPolicyOrganizations(
			passwordPolicyId, organizationIds);
	}

	@Override
	public void addUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws PortalException {

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.ASSIGN_MEMBERS);

		organizationLocalService.addUserOrganizationByEmailAddress(
			emailAddress, organizationId);
	}

	/**
	 * Deletes the organization's logo.
	 *
	 * @param organizationId the primary key of the organization
	 */
	@Override
	public void deleteLogo(long organizationId) throws PortalException {
		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.UPDATE);

		organizationLocalService.deleteLogo(organizationId);
	}

	/**
	 * Deletes the organization. The organization's associated resources and
	 * assets are also deleted.
	 *
	 * @param organizationId the primary key of the organization
	 */
	@Override
	public void deleteOrganization(long organizationId) throws PortalException {
		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.DELETE);

		organizationLocalService.deleteOrganization(organizationId);
	}

	@Override
	public void deleteUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws PortalException {

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.ASSIGN_MEMBERS);

		organizationLocalService.deleteUserOrganizationByEmailAddress(
			emailAddress, organizationId);
	}

	/**
	 * Returns the organization with the primary key.
	 *
	 * @param  organizationId the primary key of the organization
	 * @return the organization with the primary key, or <code>null</code> if an
	 *         organization with the primary key could not be found or if the
	 *         user did not have permission to view the organization
	 */
	@Override
	public Organization fetchOrganization(long organizationId)
		throws PortalException {

		Organization organization = organizationLocalService.fetchOrganization(
			organizationId);

		if (organization != null) {
			OrganizationPermissionUtil.check(
				getPermissionChecker(), organization, ActionKeys.VIEW);
		}

		return organization;
	}

	@Override
	public Organization fetchOrganizationByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		Organization organization =
			organizationLocalService.fetchOrganizationByExternalReferenceCode(
				externalReferenceCode, companyId);

		if (organization != null) {
			OrganizationPermissionUtil.check(
				getPermissionChecker(), organization, ActionKeys.VIEW);
		}

		return organization;
	}

	@Override
	public List<Organization> getGtOrganizations(
		long gtOrganizationId, long companyId, long parentOrganizationId,
		int size) {

		return organizationPersistence.filterFindByGtO_C_P(
			gtOrganizationId, companyId, parentOrganizationId, 0, size,
			OrganizationIdComparator.getInstance(true));
	}

	public Organization getOrAddEmptyOrganization(
			String externalReferenceCode, String name)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		Organization organization = fetchOrganizationByExternalReferenceCode(
			externalReferenceCode, permissionChecker.getCompanyId());

		if (organization != null) {
			return organization;
		}

		PortalPermissionUtil.check(
			getPermissionChecker(), ActionKeys.ADD_ORGANIZATION);

		return organizationLocalService.getOrAddEmptyOrganization(
			externalReferenceCode, permissionChecker.getCompanyId(),
			permissionChecker.getUserId(), name);
	}

	/**
	 * Returns the organization with the primary key.
	 *
	 * @param  organizationId the primary key of the organization
	 * @return the organization with the primary key
	 */
	@Override
	public Organization getOrganization(long organizationId)
		throws PortalException {

		Organization organization = organizationLocalService.getOrganization(
			organizationId);

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organization, ActionKeys.VIEW);

		return organization;
	}

	@Override
	public Organization getOrganizationByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		Organization organization =
			organizationLocalService.getOrganizationByExternalReferenceCode(
				externalReferenceCode, companyId);

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organization, ActionKeys.VIEW);

		return organization;
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
	public long getOrganizationId(long companyId, String name)
		throws PortalException {

		long organizationId = organizationLocalService.getOrganizationId(
			companyId, name);

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.VIEW);

		return organizationId;
	}

	/**
	 * Returns all the organizations belonging to the parent organization.
	 *
	 * @param  companyId the primary key of the organizations' company
	 * @param  parentOrganizationId the primary key of the organizations' parent
	 *         organization
	 * @return the organizations belonging to the parent organization
	 */
	@Override
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId) {

		if (parentOrganizationId ==
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID) {

			return organizationPersistence.filterFindByCompanyId(companyId);
		}

		return organizationPersistence.filterFindByC_P(
			companyId, parentOrganizationId);
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
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  companyId the primary key of the organizations' company
	 * @param  parentOrganizationId the primary key of the organizations' parent
	 *         organization
	 * @param  start the lower bound of the range of organizations to return
	 * @param  end the upper bound of the range of organizations to return (not
	 *         inclusive)
	 * @return the range of organizations belonging to the parent organization
	 */
	@Override
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, int start, int end) {

		if (parentOrganizationId ==
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID) {

			return organizationPersistence.filterFindByCompanyId(
				companyId, start, end);
		}

		return organizationPersistence.filterFindByC_P(
			companyId, parentOrganizationId, start, end);
	}

	@Override
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, int start, int end,
		OrderByComparator<Organization> orderByComparator) {

		if (parentOrganizationId ==
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID) {

			return organizationPersistence.filterFindByCompanyId(
				companyId, start, end, orderByComparator);
		}

		return organizationPersistence.filterFindByC_P(
			companyId, parentOrganizationId, start, end, orderByComparator);
	}

	@Override
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, String name, int start,
		int end) {

		if (Validator.isNull(name)) {
			return getOrganizations(
				companyId, parentOrganizationId, start, end);
		}

		if (parentOrganizationId ==
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID) {

			return organizationPersistence.filterFindByC_LikeN(companyId, name);
		}

		return organizationPersistence.filterFindByC_P_LikeN(
			companyId, parentOrganizationId, name, start, end);
	}

	@Override
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, String name, int start,
		int end, OrderByComparator<Organization> orderByComparator) {

		if (Validator.isNull(name)) {
			return getOrganizations(
				companyId, parentOrganizationId, start, end, orderByComparator);
		}

		if (parentOrganizationId ==
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID) {

			return organizationPersistence.filterFindByC_LikeN(
				companyId, name, start, end, orderByComparator);
		}

		return organizationPersistence.filterFindByC_P_LikeN(
			companyId, parentOrganizationId, name, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of organizations belonging to the parent organization.
	 *
	 * @param  companyId the primary key of the organizations' company
	 * @param  parentOrganizationId the primary key of the organizations' parent
	 *         organization
	 * @return the number of organizations belonging to the parent organization
	 */
	@Override
	public int getOrganizationsCount(
		long companyId, long parentOrganizationId) {

		if (parentOrganizationId ==
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID) {

			return organizationPersistence.filterCountByCompanyId(companyId);
		}

		return organizationPersistence.filterCountByC_P(
			companyId, parentOrganizationId);
	}

	@Override
	public int getOrganizationsCount(
			long companyId, long parentOrganizationId, String name)
		throws PortalException {

		if (Validator.isNull(name)) {
			return getOrganizationsCount(companyId, parentOrganizationId);
		}

		if (parentOrganizationId ==
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID) {

			return organizationPersistence.filterCountByC_LikeN(
				companyId, name);
		}

		return organizationPersistence.filterCountByC_P_LikeN(
			companyId, parentOrganizationId, name);
	}

	/**
	 * Returns all the organizations with which the user is explicitly
	 * associated.
	 *
	 * <p>
	 * A user is considered to be <i>explicitly</i> associated with an
	 * organization if his account is individually created within the
	 * organization or if the user is later added as a member.
	 * </p>
	 *
	 * @param  userId the primary key of the user
	 * @return the organizations with which the user is explicitly associated
	 */
	@Override
	public List<Organization> getUserOrganizations(long userId)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.VIEW);

		return organizationLocalService.getUserOrganizations(userId);
	}

	/**
	 * Sets the organizations in the group, removing and adding organizations to
	 * the group as necessary.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	@Override
	public void setGroupOrganizations(long groupId, long[] organizationIds)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.ASSIGN_MEMBERS);

		organizationLocalService.setGroupOrganizations(
			groupId, organizationIds);
	}

	/**
	 * Removes the organizations from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	@Override
	public void unsetGroupOrganizations(long groupId, long[] organizationIds)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.ASSIGN_MEMBERS);

		organizationLocalService.unsetGroupOrganizations(
			groupId, organizationIds);
	}

	/**
	 * Removes the organizations from the password policy.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param organizationIds the primary keys of the organizations
	 */
	@Override
	public void unsetPasswordPolicyOrganizations(
			long passwordPolicyId, long[] organizationIds)
		throws PortalException {

		PasswordPolicyPermissionUtil.check(
			getPermissionChecker(), passwordPolicyId, ActionKeys.UPDATE);

		organizationLocalService.unsetPasswordPolicyOrganizations(
			passwordPolicyId, organizationIds);
	}

	@Override
	public Organization updateLogo(long organizationId, byte[] logoBytes)
		throws PortalException {

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.UPDATE);

		return organizationLocalService.updateLogo(organizationId, logoBytes);
	}

	/**
	 * Updates the organization with additional parameters.
	 *
	 * @param  organizationId the primary key of the organization
	 * @param  parentOrganizationId the primary key of the organization's parent
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
	 * @param  addresses the organization's addresses
	 * @param  emailAddresses the organization's email addresses
	 * @param  orgLabors the organization's hours of operation
	 * @param  phones the organization's phone numbers
	 * @param  websites the organization's websites
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set asset category IDs and asset tag
	 *         names for the organization, and merge expando bridge attributes
	 *         for the organization.
	 * @return the organization
	 */
	@Override
	public Organization updateOrganization(
			String externalReferenceCode, long organizationId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean hasLogo, byte[] logoBytes, boolean site,
			List<Address> addresses, List<EmailAddress> emailAddresses,
			List<OrgLabor> orgLabors, List<Phone> phones,
			List<Website> websites, ServiceContext serviceContext)
		throws PortalException {

		Organization organization = organizationPersistence.findByPrimaryKey(
			organizationId);

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organization, ActionKeys.UPDATE);

		if (organization.getParentOrganizationId() != parentOrganizationId) {
			if (parentOrganizationId ==
					OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

				PortalPermissionUtil.check(
					getPermissionChecker(), ActionKeys.ADD_ORGANIZATION);
			}
			else {
				OrganizationPermissionUtil.check(
					getPermissionChecker(), parentOrganizationId,
					ActionKeys.ADD_ORGANIZATION);
			}
		}

		if (addresses != null) {
			UsersAdminUtil.updateAddresses(
				Organization.class.getName(), organizationId, addresses,
				ListTypeConstants.ORGANIZATION_ADDRESS);
		}

		if (emailAddresses != null) {
			UsersAdminUtil.updateEmailAddresses(
				Organization.class.getName(), organizationId, emailAddresses);
		}

		if (orgLabors != null) {
			UsersAdminUtil.updateOrgLabors(organizationId, orgLabors);
		}

		if (phones != null) {
			UsersAdminUtil.updatePhones(
				Organization.class.getName(), organizationId, phones);
		}

		if (websites != null) {
			UsersAdminUtil.updateWebsites(
				Organization.class.getName(), organizationId, websites);
		}

		User user = getUser();

		Organization oldOrganization = organization;

		List<AssetCategory> oldAssetCategories =
			_assetCategoryLocalService.getCategories(
				Organization.class.getName(), organizationId);

		List<AssetTag> oldAssetTags = _assetTagLocalService.getTags(
			Organization.class.getName(), organizationId);

		ExpandoBridge oldExpandoBridge = oldOrganization.getExpandoBridge();

		Map<String, Serializable> oldExpandoAttributes =
			oldExpandoBridge.getAttributes();

		organization = organizationLocalService.updateOrganization(
			externalReferenceCode, user.getCompanyId(), organizationId,
			parentOrganizationId, name, type, regionId, countryId,
			statusListTypeId, comments, hasLogo, logoBytes, site,
			serviceContext);

		OrganizationMembershipPolicyUtil.verifyPolicy(
			organization, oldOrganization, oldAssetCategories, oldAssetTags,
			oldExpandoAttributes);

		return organization;
	}

	/**
	 * Updates the organization.
	 *
	 * @param  organizationId the primary key of the organization
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
	 *         <code>null</code>). Can set asset category IDs and asset tag
	 *         names for the organization, and merge expando bridge attributes
	 *         for the organization.
	 * @return the organization
	 */
	@Override
	public Organization updateOrganization(
			String externalReferenceCode, long organizationId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean site, ServiceContext serviceContext)
		throws PortalException {

		return updateOrganization(
			externalReferenceCode, organizationId, parentOrganizationId, name,
			type, regionId, countryId, statusListTypeId, comments, true, null,
			site, null, null, null, null, null, serviceContext);
	}

	@BeanReference(type = AssetCategoryLocalService.class)
	private AssetCategoryLocalService _assetCategoryLocalService;

	@BeanReference(type = AssetTagLocalService.class)
	private AssetTagLocalService _assetTagLocalService;

}