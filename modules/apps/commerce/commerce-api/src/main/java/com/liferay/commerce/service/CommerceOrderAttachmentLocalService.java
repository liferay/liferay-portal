/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommerceOrderAttachment. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderAttachmentLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceOrderAttachmentLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceOrderAttachmentLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce order attachment local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceOrderAttachmentLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the commerce order attachment to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderAttachmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderAttachment the commerce order attachment
	 * @return the commerce order attachment that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderAttachment addCommerceOrderAttachment(
		CommerceOrderAttachment commerceOrderAttachment);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderAttachment addCommerceOrderAttachment(
			String externalReferenceCode, long userId, long commerceOrderId,
			double priority, boolean restricted, String title, String type,
			String fileName, InputStream inputStream)
		throws PortalException;

	/**
	 * Creates a new commerce order attachment with the primary key. Does not add the commerce order attachment to the database.
	 *
	 * @param commerceOrderAttachmentId the primary key for the new commerce order attachment
	 * @return the new commerce order attachment
	 */
	@Transactional(enabled = false)
	public CommerceOrderAttachment createCommerceOrderAttachment(
		long commerceOrderAttachmentId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the commerce order attachment from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderAttachmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderAttachment the commerce order attachment
	 * @return the commerce order attachment that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceOrderAttachment deleteCommerceOrderAttachment(
			CommerceOrderAttachment commerceOrderAttachment)
		throws PortalException;

	/**
	 * Deletes the commerce order attachment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderAttachmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderAttachmentId the primary key of the commerce order attachment
	 * @return the commerce order attachment that was removed
	 * @throws PortalException if a commerce order attachment with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceOrderAttachment deleteCommerceOrderAttachment(
			long commerceOrderAttachmentId)
		throws PortalException;

	public void deleteCommerceOrderAttachments(long commerceOrderId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderAttachmentModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderAttachmentModelImpl</code>.
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
	public CommerceOrderAttachment fetchCommerceOrderAttachment(
		long commerceOrderAttachmentId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderAttachment
		fetchCommerceOrderAttachmentByExternalReferenceCode(
			String externalReferenceCode, long companyId);

	/**
	 * Returns the commerce order attachment matching the UUID and group.
	 *
	 * @param uuid the commerce order attachment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order attachment, or <code>null</code> if a matching commerce order attachment could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderAttachment fetchCommerceOrderAttachmentByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the commerce order attachment with the primary key.
	 *
	 * @param commerceOrderAttachmentId the primary key of the commerce order attachment
	 * @return the commerce order attachment
	 * @throws PortalException if a commerce order attachment with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderAttachment getCommerceOrderAttachment(
			long commerceOrderAttachmentId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderAttachment
			getCommerceOrderAttachmentByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the commerce order attachment matching the UUID and group.
	 *
	 * @param uuid the commerce order attachment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order attachment
	 * @throws PortalException if a matching commerce order attachment could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderAttachment getCommerceOrderAttachmentByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the commerce order attachments.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderAttachmentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce order attachments
	 * @param end the upper bound of the range of commerce order attachments (not inclusive)
	 * @return the range of commerce order attachments
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderAttachment> getCommerceOrderAttachments(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderAttachment> getCommerceOrderAttachments(
		long commerceOrderId, boolean restricted, int start, int end,
		OrderByComparator<CommerceOrderAttachment> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderAttachment> getCommerceOrderAttachments(
		long commerceOrderId, int start, int end,
		OrderByComparator<CommerceOrderAttachment> orderByComparator);

	/**
	 * Returns all the commerce order attachments matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce order attachments
	 * @param companyId the primary key of the company
	 * @return the matching commerce order attachments, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderAttachment>
		getCommerceOrderAttachmentsByUuidAndCompanyId(
			String uuid, long companyId);

	/**
	 * Returns a range of commerce order attachments matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce order attachments
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce order attachments
	 * @param end the upper bound of the range of commerce order attachments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce order attachments, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderAttachment>
		getCommerceOrderAttachmentsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CommerceOrderAttachment> orderByComparator);

	/**
	 * Returns the number of commerce order attachments.
	 *
	 * @return the number of commerce order attachments
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceOrderAttachmentsCount();

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

	/**
	 * Updates the commerce order attachment in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderAttachmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderAttachment the commerce order attachment
	 * @return the commerce order attachment that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderAttachment updateCommerceOrderAttachment(
		CommerceOrderAttachment commerceOrderAttachment);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderAttachment updateCommerceOrderAttachment(
			long commerceOrderAttachmentId, double priority, boolean restricted,
			String title, String type)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:1558053577