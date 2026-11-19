/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinitionLink;
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
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CPDefinitionLink. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Marco Leo
 * @see CPDefinitionLinkLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CPDefinitionLinkLocalService
	extends BaseLocalService, CTService<CPDefinitionLink>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPDefinitionLinkLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the cp definition link local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CPDefinitionLinkLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the cp definition link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionLink the cp definition link
	 * @return the cp definition link that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CPDefinitionLink addCPDefinitionLink(
		CPDefinitionLink cpDefinitionLink);

	public CPDefinitionLink addCPDefinitionLinkByCProductId(
			long cpDefinitionId, long cProductId, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, double priority, String type,
			ServiceContext serviceContext)
		throws PortalException;

	public void checkCPDefinitionLinks() throws PortalException;

	/**
	 * Creates a new cp definition link with the primary key. Does not add the cp definition link to the database.
	 *
	 * @param CPDefinitionLinkId the primary key for the new cp definition link
	 * @return the new cp definition link
	 */
	@Transactional(enabled = false)
	public CPDefinitionLink createCPDefinitionLink(long CPDefinitionLinkId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the cp definition link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionLink the cp definition link
	 * @return the cp definition link that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPDefinitionLink deleteCPDefinitionLink(
			CPDefinitionLink cpDefinitionLink)
		throws PortalException;

	/**
	 * Deletes the cp definition link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPDefinitionLinkId the primary key of the cp definition link
	 * @return the cp definition link that was removed
	 * @throws PortalException if a cp definition link with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CPDefinitionLink deleteCPDefinitionLink(long CPDefinitionLinkId)
		throws PortalException;

	public void deleteCPDefinitionLinksByCPDefinitionId(long cpDefinitionId)
		throws PortalException;

	public void deleteCPDefinitionLinksByCProductId(long cProductId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
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
	public CPDefinitionLink fetchCPDefinitionLink(long CPDefinitionLinkId);

	/**
	 * Returns the cp definition link matching the UUID and group.
	 *
	 * @param uuid the cp definition link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPDefinitionLink fetchCPDefinitionLinkByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the cp definition link with the primary key.
	 *
	 * @param CPDefinitionLinkId the primary key of the cp definition link
	 * @return the cp definition link
	 * @throws PortalException if a cp definition link with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPDefinitionLink getCPDefinitionLink(long CPDefinitionLinkId)
		throws PortalException;

	/**
	 * Returns the cp definition link matching the UUID and group.
	 *
	 * @param uuid the cp definition link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition link
	 * @throws PortalException if a matching cp definition link could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPDefinitionLink getCPDefinitionLinkByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the cp definition links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of cp definition links
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionLink> getCPDefinitionLinks(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, String type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, String type, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, String type, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator);

	/**
	 * Returns all the cp definition links matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition links
	 * @param companyId the primary key of the company
	 * @return the matching cp definition links, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionLink> getCPDefinitionLinksByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of cp definition links matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition links
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp definition links, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionLink> getCPDefinitionLinksByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator);

	/**
	 * Returns the number of cp definition links.
	 *
	 * @return the number of cp definition links
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCPDefinitionLinksCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCPDefinitionLinksCount(long cpDefinitionId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCPDefinitionLinksCount(long cpDefinitionId, String type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCPDefinitionLinksCount(
		long cpDefinitionId, String type, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

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

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionLink> getReverseCPDefinitionLinks(
		long cProductId, String type, int status);

	/**
	 * Updates the cp definition link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionLink the cp definition link
	 * @return the cp definition link that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CPDefinitionLink updateCPDefinitionLink(
		CPDefinitionLink cpDefinitionLink);

	public CPDefinitionLink updateCPDefinitionLink(
			long userId, long cpDefinitionLinkId, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, double priority, ServiceContext serviceContext)
		throws PortalException;

	public void updateCPDefinitionLinkCProductIds(
			long cpDefinitionId, long[] cProductIds, String type,
			ServiceContext serviceContext)
		throws PortalException;

	public CPDefinitionLink updateStatus(
			long userId, long cpDefinitionLinkId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<CPDefinitionLink> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<CPDefinitionLink> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPDefinitionLink>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-149309770