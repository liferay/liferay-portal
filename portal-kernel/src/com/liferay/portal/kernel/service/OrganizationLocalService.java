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
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
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
 * Provides the local service interface for Organization. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see OrganizationLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {"model.class.name=com.liferay.portal.kernel.model.Organization"}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface OrganizationLocalService
	extends BaseLocalService, CTService<Organization>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.OrganizationLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the organization local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link OrganizationLocalServiceUtil} if injection and service tracking are not available.
	 */
	public boolean addGroupOrganization(long groupId, long organizationId);

	public boolean addGroupOrganization(
		long groupId, Organization organization);

	public boolean addGroupOrganizations(
		long groupId, List<Organization> organizations);

	public boolean addGroupOrganizations(long groupId, long[] organizationIds);

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
	 * @param userId the primary key of the creator/owner of the organization
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param name the organization's name
	 * @param site whether the organization is to be associated with a main
	 site
	 * @return the organization
	 */
	public Organization addOrganization(
			long userId, long parentOrganizationId, String name, boolean site)
		throws PortalException;

	/**
	 * Adds the organization to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OrganizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param organization the organization
	 * @return the organization that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public Organization addOrganization(Organization organization);

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
	 * @param userId the primary key of the creator/owner of the organization
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
			String externalReferenceCode, long userId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean site, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds a resource for each type of permission available on the
	 * organization.
	 *
	 * @param userId the primary key of the creator/owner of the organization
	 * @param organization the organization
	 */
	public void addOrganizationResources(long userId, Organization organization)
		throws PortalException;

	public User addOrganizationUserByEmailAddress(
			String emailAddress, long organizationId,
			ServiceContext serviceContext)
		throws PortalException;

	public Organization addOrUpdateOrganization(
			String externalReferenceCode, long userId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean hasLogo, byte[] logoBytes, boolean site,
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
		long passwordPolicyId, long[] organizationIds);

	public boolean addUserOrganization(long userId, long organizationId);

	public boolean addUserOrganization(long userId, Organization organization);

	public void addUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws PortalException;

	public boolean addUserOrganizations(
		long userId, List<Organization> organizations);

	public boolean addUserOrganizations(long userId, long[] organizationIds);

	public void clearGroupOrganizations(long groupId);

	public void clearUserOrganizations(long userId);

	/**
	 * Creates a new organization with the primary key. Does not add the organization to the database.
	 *
	 * @param organizationId the primary key for the new organization
	 * @return the new organization
	 */
	@Transactional(enabled = false)
	public Organization createOrganization(long organizationId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteGroupOrganization(long groupId, long organizationId);

	public void deleteGroupOrganization(
		long groupId, Organization organization);

	public void deleteGroupOrganizations(
		long groupId, List<Organization> organizations);

	public void deleteGroupOrganizations(long groupId, long[] organizationIds);

	/**
	 * Deletes the organization's logo.
	 *
	 * @param organizationId the primary key of the organization
	 */
	public void deleteLogo(long organizationId) throws PortalException;

	/**
	 * Deletes the organization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OrganizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param organizationId the primary key of the organization
	 * @return the organization that was removed
	 * @throws PortalException if a organization with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public Organization deleteOrganization(long organizationId)
		throws PortalException;

	/**
	 * Deletes the organization from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OrganizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param organization the organization
	 * @return the organization that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public Organization deleteOrganization(Organization organization)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteUserOrganization(long userId, long organizationId);

	public void deleteUserOrganization(long userId, Organization organization);

	public void deleteUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws PortalException;

	public void deleteUserOrganizations(
		long userId, List<Organization> organizations);

	public void deleteUserOrganizations(long userId, long[] organizationIds);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.OrganizationModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.OrganizationModelImpl</code>.
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
	public Organization fetchOrganization(long organizationId);

	/**
	 * Returns the organization with the name.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param name the organization's name
	 * @return the organization with the name, or <code>null</code> if no
	 organization could be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Organization fetchOrganization(long companyId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Organization fetchOrganizationByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the organization with the matching UUID and company.
	 *
	 * @param uuid the organization's UUID
	 * @param companyId the primary key of the company
	 * @return the matching organization, or <code>null</code> if a matching organization could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Organization fetchOrganizationByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getChildrenTypes(String type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getGroupOrganizations(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getGroupOrganizations(
		long groupId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getGroupOrganizations(
		long groupId, int start, int end,
		OrderByComparator<Organization> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupOrganizationsCount(long groupId);

	/**
	 * Returns the groupIds of the groups associated with the organization.
	 *
	 * @param organizationId the organizationId of the organization
	 * @return long[] the groupIds of groups associated with the organization
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getGroupPrimaryKeys(long organizationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getGroupUserOrganizations(
			long groupId, long userId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getNoAssetOrganizations();

	@Indexable(type = IndexableType.REINDEX)
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Organization getOrAddIncompleteOrganization(
			String externalReferenceCode, long companyId, long userId,
			String name)
		throws Exception;

	/**
	 * Returns the organization with the primary key.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the organization
	 * @throws PortalException if a organization with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Organization getOrganization(long organizationId)
		throws PortalException;

	/**
	 * Returns the organization with the name.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param name the organization's name
	 * @return the organization with the name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Organization getOrganization(long companyId, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Organization getOrganizationByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the organization with the matching UUID and company.
	 *
	 * @param uuid the organization's UUID
	 * @param companyId the primary key of the company
	 * @return the matching organization
	 * @throws PortalException if a matching organization could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Organization getOrganizationByUuidAndCompanyId(
			String uuid, long companyId)
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
	public long getOrganizationId(long companyId, String name);

	/**
	 * Returns a range of all the organizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of organizations
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getOrganizations(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getOrganizations(
			long userId, int start, int end,
			OrderByComparator<Organization> orderByComparator)
		throws PortalException;

	/**
	 * Returns all the organizations belonging to the parent organization.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
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
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @return the range of organizations belonging to the parent organization
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationPersistence#findByC_P(
	 long, long, int, int)
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, String name, int start,
		int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getOrganizations(long companyId, String treePath);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getOrganizations(
		long companyId, String name, int start, int end,
		OrderByComparator<Organization> orderByComparator);

	/**
	 * Returns the organizations with the primary keys.
	 *
	 * @param organizationIds the primary keys of the organizations
	 * @return the organizations with the primary keys
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getOrganizations(long[] organizationIds)
		throws PortalException;

	/**
	 * Returns all the organizations and users belonging to the parent
	 * organization.
	 *
	 * @param companyId the primary key of the organization and user's company
	 * @param parentOrganizationId the primary key of the organization and
	 user's parent organization
	 * @param status the user's workflow status
	 * @param start the lower bound of the range of organizations and users to
	 return
	 * @param end the upper bound of the range of organizations and users to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the organizations and
	 users (optionally <code>null</code>)
	 * @return the organizations and users belonging to the parent organization
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getOrganizationsAndUsers(
		long companyId, long parentOrganizationId, int status, int start,
		int end, OrderByComparator<?> orderByComparator);

	/**
	 * Returns the number of organizations and users belonging to the parent
	 * organization.
	 *
	 * @param companyId the primary key of the organization and user's company
	 * @param parentOrganizationId the primary key of the organization and
	 user's parent organization
	 * @param status the user's workflow status
	 * @return the number of organizations and users belonging to the parent
	 organization
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOrganizationsAndUsersCount(
		long companyId, long parentOrganizationId, int status);

	/**
	 * Returns the number of organizations.
	 *
	 * @return the number of organizations
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOrganizationsCount();

	/**
	 * Returns the number of organizations belonging to the parent organization.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @return the number of organizations belonging to the parent organization
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOrganizationsCount(long companyId, long parentOrganizationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOrganizationsCount(
		long companyId, long parentOrganizationId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOrganizationsCount(long companyId, String name);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * Returns the parent organizations in order by closest ancestor. The list
	 * starts with the organization itself.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the parent organizations in order by closest ancestor
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getParentOrganizations(long organizationId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Returns the suborganizations of the organizations.
	 *
	 * @param organizations the organizations from which to get
	 suborganizations
	 * @return the suborganizations of the organizations
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getSuborganizations(
		List<Organization> organizations);

	/**
	 * Returns the suborganizations of the organization.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param organizationId the primary key of the organization
	 * @return the suborganizations of the organization
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getSuborganizations(
		long companyId, long organizationId);

	/**
	 * Returns the count of suborganizations of the organization.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param organizationId the primary key of the organization
	 * @return the count of suborganizations of the organization
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSuborganizationsCount(long companyId, long organizationId);

	/**
	 * Returns the intersection of <code>allOrganizations</code> and
	 * <code>availableOrganizations</code>.
	 *
	 * @param allOrganizations the organizations to check for availability
	 * @param availableOrganizations the available organizations
	 * @return the intersection of <code>allOrganizations</code> and
	 <code>availableOrganizations</code>
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getSubsetOrganizations(
		List<Organization> allOrganizations,
		List<Organization> availableOrganizations);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getTypes();

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
	 * @param userId the primary key of the user
	 * @param includeAdministrative whether to include the IDs of organizations
	 that the user administers or owns, even if he's not a member of
	 the organizations
	 * @return the IDs of organizations with which the user is explicitly
	 associated, optionally including the IDs of organizations that
	 the user administers or owns
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getUserOrganizationIds(
			long userId, boolean includeAdministrative)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getUserOrganizations(long userId);

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
	 * @param userId the primary key of the user
	 * @param includeAdministrative whether to include the IDs of organizations
	 that the user administers or owns, even if he's not a member of
	 the organizations
	 * @return the organizations with which the user is explicitly associated,
	 optionally including the organizations that the user administers
	 or owns
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getUserOrganizations(
			long userId, boolean includeAdministrative)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getUserOrganizations(
		long userId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> getUserOrganizations(
		long userId, int start, int end,
		OrderByComparator<Organization> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserOrganizationsCount(long userId);

	/**
	 * Returns the userIds of the users associated with the organization.
	 *
	 * @param organizationId the organizationId of the organization
	 * @return long[] the userIds of users associated with the organization
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getUserPrimaryKeys(long organizationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasGroupOrganization(long groupId, long organizationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasGroupOrganizations(long groupId);

	/**
	 * Returns <code>true</code> if the password policy has been assigned to the
	 * organization.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param organizationId the primary key of the organization
	 * @return <code>true</code> if the password policy has been assigned to the
	 organization; <code>false</code> otherwise
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPasswordPolicyOrganization(
		long passwordPolicyId, long organizationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserOrganization(long userId, long organizationId);

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
	 * @param userId the primary key of the organization's user
	 * @param organizationId the primary key of the organization
	 * @param inheritSuborganizations if <code>true</code> suborganizations are
	 considered in the determination
	 * @param includeSpecifiedOrganization if <code>true</code> the
	 organization specified by <code>organizationId</code> is
	 considered in the determination
	 * @return <code>true</code> if the user has access to the organization;
	 <code>false</code> otherwise
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserOrganization(
			long userId, long organizationId, boolean inheritSuborganizations,
			boolean includeSpecifiedOrganization)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserOrganizations(long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean isCountryEnabled(String type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean isCountryRequired(String type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean isRootable(String type);

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
	public void rebuildTree(long companyId) throws PortalException;

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
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param keywords the keywords (space separated), which may occur in the
	 organization's name, street, city, zipcode, type, region or
	 country (optionally <code>null</code>)
	 * @param params the finder parameters (optionally <code>null</code>).
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @param sort the field and direction by which to sort (optionally
	 <code>null</code>)
	 * @return the matching organizations ordered by name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long parentOrganizationId, String keywords,
		LinkedHashMap<String, Object> params, int start, int end, Sort sort);

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
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param keywords the keywords (space separated), which may occur in the
	 organization's name, street, city, or zipcode (optionally
	 <code>null</code>)
	 * @param type the organization's type (optionally <code>null</code>)
	 * @param regionId the primary key of the organization's region (optionally
	 <code>null</code>)
	 * @param countryId the primary key of the organization's country
	 (optionally <code>null</code>)
	 * @param params the finder params. For more information see {@link
	 com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @return the matching organizations ordered by name
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> search(
		long companyId, long parentOrganizationId, String keywords, String type,
		Long regionId, Long countryId, LinkedHashMap<String, Object> params,
		int start, int end);

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
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param keywords the keywords (space separated), which may occur in the
	 organization's name, street, city, or zipcode (optionally
	 <code>null</code>)
	 * @param type the organization's type (optionally <code>null</code>)
	 * @param regionId the primary key of the organization's region (optionally
	 <code>null</code>)
	 * @param countryId the primary key of the organization's country
	 (optionally <code>null</code>)
	 * @param params the finder params. For more information see {@link
	 com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the organizations
	 (optionally <code>null</code>)
	 * @return the matching organizations ordered by comparator
	 <code>orderByComparator</code>
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> search(
		long companyId, long parentOrganizationId, String keywords, String type,
		Long regionId, Long countryId, LinkedHashMap<String, Object> params,
		int start, int end, OrderByComparator<Organization> orderByComparator);

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
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 * @param name the name keywords (space separated, optionally
	 <code>null</code>)
	 * @param type the organization's type (optionally <code>null</code>)
	 * @param street the street keywords (optionally <code>null</code>)
	 * @param city the city keywords (optionally <code>null</code>)
	 * @param zip the zipcode keywords (optionally <code>null</code>)
	 * @param regionId the primary key of the organization's region (optionally
	 <code>null</code>)
	 * @param countryId the primary key of the organization's country
	 (optionally <code>null</code>)
	 * @param params the finder parameters (optionally <code>null</code>). For
	 more information see {@link
	 com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param andOperator whether every field must match its keywords, or just
	 one field. For example, &quot;organizations with the name
	 'Employees' and city 'Chicago'&quot; vs &quot;organizations with
	 the name 'Employees' or the city 'Chicago'&quot;.
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @return the matching organizations ordered by name
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> search(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end);

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
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param name the name keywords (space separated, optionally
	 <code>null</code>)
	 * @param type the organization's type (optionally <code>null</code>)
	 * @param street the street keywords (optionally <code>null</code>)
	 * @param city the city keywords (optionally <code>null</code>)
	 * @param zip the zipcode keywords (optionally <code>null</code>)
	 * @param regionId the primary key of the organization's region (optionally
	 <code>null</code>)
	 * @param countryId the primary key of the organization's country
	 (optionally <code>null</code>)
	 * @param params the finder parameters (optionally <code>null</code>). For
	 more information see {@link
	 com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param andOperator whether every field must match its keywords, or just
	 one field. For example, &quot;organizations with the name
	 'Employees' and city 'Chicago'&quot; vs &quot;organizations with
	 the name 'Employees' or the city 'Chicago'&quot;.
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the organizations
	 (optionally <code>null</code>)
	 * @return the matching organizations ordered by comparator
	 <code>orderByComparator</code>
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Organization> search(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<Organization> orderByComparator);

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
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param name the name keywords (space separated, optionally
	 <code>null</code>)
	 * @param type the type keywords (optionally <code>null</code>)
	 * @param street the street keywords (optionally <code>null</code>)
	 * @param city the city keywords (optionally <code>null</code>)
	 * @param zip the zipcode keywords (optionally <code>null</code>)
	 * @param region the region keywords (optionally <code>null</code>)
	 * @param country the country keywords (optionally <code>null</code>)
	 * @param params the finder parameters (optionally <code>null</code>).
	 * @param andSearch whether every field must match its keywords or just one
	 field
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @param sort the field and direction by which to sort (optionally
	 <code>null</code>)
	 * @return the matching organizations ordered by <code>sort</code>
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, String region, String country,
		LinkedHashMap<String, Object> params, boolean andSearch, int start,
		int end, Sort sort);

	/**
	 * Returns the number of organizations that match the keywords, type,
	 * region, and country.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param keywords the keywords (space separated), which may occur in the
	 organization's name, street, city, or zipcode (optionally
	 <code>null</code>)
	 * @param type the organization's type (optionally <code>null</code>)
	 * @param regionId the primary key of the organization's region (optionally
	 <code>null</code>)
	 * @param countryId the primary key of the organization's country
	 (optionally <code>null</code>)
	 * @param params the finder parameters (optionally <code>null</code>). For
	 more information see {@link
	 com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @return the number of matching organizations
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long parentOrganizationId, String keywords, String type,
		Long regionId, Long countryId, LinkedHashMap<String, Object> params);

	/**
	 * Returns the number of organizations with the type, region, and country,
	 * and whose name, street, city, and zipcode match the keywords specified
	 * for them.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param name the name keywords (space separated, optionally
	 <code>null</code>)
	 * @param type the organization's type (optionally <code>null</code>)
	 * @param street the street keywords (optionally <code>null</code>)
	 * @param city the city keywords (optionally <code>null</code>)
	 * @param zip the zipcode keywords (optionally <code>null</code>)
	 * @param regionId the primary key of the organization's region (optionally
	 <code>null</code>)
	 * @param countryId the primary key of the organization's country
	 (optionally <code>null</code>)
	 * @param params the finder parameters (optionally <code>null</code>). For
	 more information see {@link
	 com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param andOperator whether every field must match its keywords, or just
	 one field. For example, &quot;organizations with the name
	 'Employees' and city 'Chicago'&quot; vs &quot;organizations with
	 the name 'Employees' or the city 'Chicago'&quot;.
	 * @return the number of matching organizations
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		LinkedHashMap<String, Object> params, boolean andOperator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<Organization> searchOrganizations(
			long companyId, long parentOrganizationId, String keywords,
			LinkedHashMap<String, Object> params, int start, int end, Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<Organization> searchOrganizations(
			long companyId, long parentOrganizationId, String name, String type,
			String street, String city, String zip, String region,
			String country, LinkedHashMap<String, Object> params,
			boolean andSearch, int start, int end, Sort sort)
		throws PortalException;

	/**
	 * Returns the organizations and users that match the keywords specified for
	 * them and belong to the parent organization.
	 *
	 * @param companyId the primary key of the organization and user's company
	 * @param parentOrganizationId the primary key of the organization and
	 user's parent organization
	 * @param keywords the keywords (space separated), which may occur in the
	 organization's name, type, or location fields or user's first
	 name, middle name, last name, screen name, email address, or
	 address fields
	 * @param status user's workflow status
	 * @param params the finder parameters (optionally <code>null</code>).
	 * @param start the lower bound of the range of organizations and users to
	 return
	 * @param end the upper bound of the range of organizations and users to
	 return (not inclusive)
	 * @return the matching organizations and users
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits searchOrganizationsAndUsers(
			long companyId, long parentOrganizationId, String keywords,
			int status, LinkedHashMap<String, Object> params, int start,
			int end, Sort[] sorts)
		throws PortalException;

	/**
	 * Returns the number of organizations and users that match the keywords
	 * specified for them and belong to the parent organization.
	 *
	 * @param companyId the primary key of the organization and user's company
	 * @param parentOrganizationId the primary key of the organization and
	 user's parent organization
	 * @param keywords the keywords (space separated), which may occur in the
	 organization's name, type, or location fields or user's first
	 name, middle name, last name, screen name, email address, or
	 address fields
	 * @param status user's workflow status
	 * @param params the finder parameters (optionally <code>null</code>).
	 * @return the number of matching organizations and users
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchOrganizationsAndUsersCount(
			long companyId, long parentOrganizationId, String keywords,
			int status, LinkedHashMap<String, Object> params)
		throws PortalException;

	public void setGroupOrganizations(long groupId, long[] organizationIds);

	public void setUserOrganizations(long userId, long[] organizationIds);

	/**
	 * Removes the organizations from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	public void unsetGroupOrganizations(long groupId, long[] organizationIds);

	/**
	 * Removes the organizations from the password policy.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param organizationIds the primary keys of the organizations
	 */
	public void unsetPasswordPolicyOrganizations(
		long passwordPolicyId, long[] organizationIds);

	/**
	 * Updates the organization's asset with the new asset categories and tag
	 * names, removing and adding asset categories and tag names as necessary.
	 *
	 * @param userId the primary key of the user
	 * @param organization the organization
	 * @param assetCategoryIds the primary keys of the asset categories
	 * @param assetTagNames the asset tag names
	 */
	public void updateAsset(
			long userId, Organization organization, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException;

	public Organization updateLogo(long organizationId, byte[] logoBytes)
		throws PortalException;

	/**
	 * Updates the organization in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OrganizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param organization the organization
	 * @return the organization that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public Organization updateOrganization(Organization organization);

	/**
	 * Updates the organization.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param organizationId the primary key of the organization
	 * @param parentOrganizationId the primary key of organization's parent
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
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set asset category IDs and asset tag
	 names for the organization, and merge expando bridge attributes
	 for the organization.
	 * @return the organization
	 */
	public Organization updateOrganization(
			String externalReferenceCode, long companyId, long organizationId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean hasLogo, byte[] logoBytes, boolean site,
			ServiceContext serviceContext)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<Organization> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<Organization> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<Organization>, R, E>
				updateUnsafeFunction)
		throws E;

}