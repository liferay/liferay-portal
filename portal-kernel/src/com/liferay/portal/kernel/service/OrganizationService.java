/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.OrgLabor;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for Organization. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see OrganizationServiceUtil
 * @generated
 */
@AccessControlled
@CTAware
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface OrganizationService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.OrganizationServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the organization remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link OrganizationServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the organizations to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	public void addGroupOrganizations(long groupId, long[] organizationIds)
		throws PortalException;

	/**
	 * Adds an organization with additional parameters.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the organization
	 * including its resources, metadata, and internal data structures.
	 * </p>
	 *
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param name the organization's name
	 * @param type the organization's type
	 * @param regionId the primary key of the organization's region
	 * @param countryId the primary key of the organization's country
	 * @param statusListTypeId the organization's workflow status
	 * @param comments the comments about the organization
	 * @param site whether the organization is to be associated with a main
	 site
	 * @param addresses the organization's addresses
	 * @param emailAddresses the organization's email addresses
	 * @param orgLabors the organization's hours of operation
	 * @param phones the organization's phone numbers
	 * @param websites the organization's websites
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set asset category IDs, asset tag names,
	 and expando bridge attributes for the organization.
	 * @return the organization
	 */
	public Organization addOrganization(
			String externalReferenceCode, long parentOrganizationId,
			String name, String type, long regionId, long countryId,
			long statusListTypeId, String comments, boolean site,
			List<Address> addresses, List<EmailAddress> emailAddresses,
			List<OrgLabor> orgLabors, List<Phone> phones,
			List<Website> websites, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds an organization.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the organization
	 * including its resources, metadata, and internal data structures.
	 * </p>
	 *
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param name the organization's name
	 * @param type the organization's type
	 * @param regionId the primary key of the organization's region
	 * @param countryId the primary key of the organization's country
	 * @param statusListTypeId the organization's workflow status
	 * @param comments the comments about the organization
	 * @param site whether the organization is to be associated with a main
	 site
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set asset category IDs, asset tag names,
	 and expando bridge attributes for the organization.
	 * @return the organization
	 */
	public Organization addOrganization(
			String externalReferenceCode, long parentOrganizationId,
			String name, String type, long regionId, long countryId,
			long statusListTypeId, String comments, boolean site,
			ServiceContext serviceContext)
		throws PortalException;

	public User addOrganizationUserByEmailAddress(
			String emailAddress, long organizationId,
			ServiceContext serviceContext)
		throws PortalException;

	public Organization addOrUpdateOrganization(
			String externalReferenceCode, long parentOrganizationId,
			String name, String type, long regionId, long countryId,
			long statusListTypeId, String comments, boolean hasLogo,
			byte[] logoBytes, boolean site, List<Address> addresses,
			List<EmailAddress> emailAddresses, List<OrgLabor> orgLabors,
			List<Phone> phones, List<Website> websites,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Assigns the password policy to the organizations, removing any other
	 * currently assigned password policies.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param organizationIds the primary keys of the organizations
	 */
	public void addPasswordPolicyOrganizations(
			long passwordPolicyId, long[] organizationIds)
		throws PortalException;

	public void addUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws PortalException;

	/**
	 * Deletes the organization's logo.
	 *
	 * @param organizationId the primary key of the organization
	 */
	public void deleteLogo(long organizationId) throws PortalException;

	/**
	 * Deletes the organization. The organization's associated resources and
	 * assets are also deleted.
	 *
	 * @param organizationId the primary key of the organization
	 */
	public void deleteOrganization(long organizationId) throws PortalException;

	public void deleteUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws PortalException;

	/**
	 * Returns the organization with the primary key.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the organization with the primary key, or <code>null</code> if an
	 organization with the primary key could not be found or if the
	 user did not have permission to view the organization
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Organization fetchOrganization(long organizationId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Organization fetchOrganizationByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getGtOrganizations(
		long gtOrganizationId, long companyId, long parentOrganizationId,
		int size);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Organization getOrAddEmptyOrganization(
			String externalReferenceCode, String name)
		throws PortalException;

	/**
	 * Returns the organization with the primary key.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the organization with the primary key
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Organization getOrganization(long organizationId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Organization getOrganizationByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the primary key of the organization with the name.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param name the organization's name
	 * @return the primary key of the organization with the name, or
	 <code>0</code> if the organization could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getOrganizationId(long companyId, String name)
		throws PortalException;

	/**
	 * Returns all the organizations belonging to the parent organization.
	 *
	 * @param companyId the primary key of the organizations' company
	 * @param parentOrganizationId the primary key of the organizations' parent
	 organization
	 * @return the organizations belonging to the parent organization
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId);

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
	 * @param companyId the primary key of the organizations' company
	 * @param parentOrganizationId the primary key of the organizations' parent
	 organization
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @return the range of organizations belonging to the parent organization
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, int start, int end,
		OrderByComparator<Organization> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, String name, int start,
		int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, String name, int start,
		int end, OrderByComparator<Organization> orderByComparator);

	/**
	 * Returns the number of organizations belonging to the parent organization.
	 *
	 * @param companyId the primary key of the organizations' company
	 * @param parentOrganizationId the primary key of the organizations' parent
	 organization
	 * @return the number of organizations belonging to the parent organization
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOrganizationsCount(long companyId, long parentOrganizationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOrganizationsCount(
			long companyId, long parentOrganizationId, String name)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

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
	 * @param userId the primary key of the user
	 * @return the organizations with which the user is explicitly associated
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getUserOrganizations(long userId)
		throws PortalException;

	/**
	 * Sets the organizations in the group, removing and adding organizations to
	 * the group as necessary.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	public void setGroupOrganizations(long groupId, long[] organizationIds)
		throws PortalException;

	/**
	 * Removes the organizations from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	public void unsetGroupOrganizations(long groupId, long[] organizationIds)
		throws PortalException;

	/**
	 * Removes the organizations from the password policy.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param organizationIds the primary keys of the organizations
	 */
	public void unsetPasswordPolicyOrganizations(
			long passwordPolicyId, long[] organizationIds)
		throws PortalException;

	public Organization updateLogo(long organizationId, byte[] logoBytes)
		throws PortalException;

	/**
	 * Updates the organization with additional parameters.
	 *
	 * @param organizationId the primary key of the organization
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param name the organization's name
	 * @param type the organization's type
	 * @param regionId the primary key of the organization's region
	 * @param countryId the primary key of the organization's country
	 * @param statusListTypeId the organization's workflow status
	 * @param comments the comments about the organization
	 * @param hasLogo if the organization has a custom logo
	 * @param logoBytes the new logo image data
	 * @param site whether the organization is to be associated with a main
	 site
	 * @param addresses the organization's addresses
	 * @param emailAddresses the organization's email addresses
	 * @param orgLabors the organization's hours of operation
	 * @param phones the organization's phone numbers
	 * @param websites the organization's websites
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set asset category IDs and asset tag
	 names for the organization, and merge expando bridge attributes
	 for the organization.
	 * @return the organization
	 */
	public Organization updateOrganization(
			String externalReferenceCode, long organizationId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean hasLogo, byte[] logoBytes, boolean site,
			List<Address> addresses, List<EmailAddress> emailAddresses,
			List<OrgLabor> orgLabors, List<Phone> phones,
			List<Website> websites, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param name the organization's name
	 * @param type the organization's type
	 * @param regionId the primary key of the organization's region
	 * @param countryId the primary key of the organization's country
	 * @param statusListTypeId the organization's workflow status
	 * @param comments the comments about the organization
	 * @param site whether the organization is to be associated with a main
	 site
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set asset category IDs and asset tag
	 names for the organization, and merge expando bridge attributes
	 for the organization.
	 * @return the organization
	 */
	public Organization updateOrganization(
			String externalReferenceCode, long organizationId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean site, ServiceContext serviceContext)
		throws PortalException;

}