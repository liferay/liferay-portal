/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service;

import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommercePaymentEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Luca Pellizzon
 * @see CommercePaymentEntryLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommercePaymentEntryLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.payment.service.impl.CommercePaymentEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce payment entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommercePaymentEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the commerce payment entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntry the commerce payment entry
	 * @return the commerce payment entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommercePaymentEntry addCommercePaymentEntry(
		CommercePaymentEntry commercePaymentEntry);

	@Indexable(type = IndexableType.REINDEX)
	public CommercePaymentEntry addCommercePaymentEntry(
			long userId, long classNameId, long classPK, long commerceChannelId,
			BigDecimal amount, String callbackURL, String cancelURL,
			String currencyCode, String languageId, String note, String payload,
			String paymentIntegrationKey, int paymentIntegrationType,
			String reasonKey, String transactionCode, int type,
			ServiceContext serviceContext)
		throws PortalException;

	public CommercePaymentEntry addOrUpdateCommercePaymentEntry(
			String externalReferenceCode, long userId, long classNameId,
			long classPK, long commerceChannelId, BigDecimal amount,
			String callbackURL, String cancelURL, String currencyCode,
			String errorMessages, String languageId, String note,
			String payload, String paymentIntegrationKey,
			int paymentIntegrationType, int paymentStatus, String reasonKey,
			String redirectURL, String transactionCode, int type,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new commerce payment entry with the primary key. Does not add the commerce payment entry to the database.
	 *
	 * @param commercePaymentEntryId the primary key for the new commerce payment entry
	 * @return the new commerce payment entry
	 */
	@Transactional(enabled = false)
	public CommercePaymentEntry createCommercePaymentEntry(
		long commercePaymentEntryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteCommercePaymentEntries(long companyId)
		throws PortalException;

	/**
	 * Deletes the commerce payment entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntry the commerce payment entry
	 * @return the commerce payment entry that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommercePaymentEntry deleteCommercePaymentEntry(
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException;

	/**
	 * Deletes the commerce payment entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntryId the primary key of the commerce payment entry
	 * @return the commerce payment entry that was removed
	 * @throws PortalException if a commerce payment entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommercePaymentEntry deleteCommercePaymentEntry(
			long commercePaymentEntryId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentEntryModelImpl</code>.
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
	public CommercePaymentEntry fetchCommercePaymentEntry(
		long commercePaymentEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePaymentEntry
		fetchCommercePaymentEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns a range of all the commerce payment entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @return the range of commerce payment entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePaymentEntry> getCommercePaymentEntries(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePaymentEntry> getCommercePaymentEntries(
		long companyId, long classNameId, long classPK, int type, int start,
		int end, OrderByComparator<CommercePaymentEntry> orderByComparator);

	/**
	 * Returns the number of commerce payment entries.
	 *
	 * @return the number of commerce payment entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommercePaymentEntriesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommercePaymentEntriesCount(
		long companyId, long classNameId, long classPK, int type);

	/**
	 * Returns the commerce payment entry with the primary key.
	 *
	 * @param commercePaymentEntryId the primary key of the commerce payment entry
	 * @return the commerce payment entry
	 * @throws PortalException if a commerce payment entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePaymentEntry getCommercePaymentEntry(
			long commercePaymentEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePaymentEntry getCommercePaymentEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

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
	public List<CommercePaymentEntry> getRefundCommercePaymentEntries(
		long companyId, long classNameId, long classPK, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRefundCommercePaymentEntriesCount(
		long companyId, long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BigDecimal getRefundedAmount(
		long companyId, long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CommercePaymentEntry>
		searchCommercePaymentEntries(
			long companyId, String keywords,
			LinkedHashMap<String, Object> params, int start, int end,
			Sort sort);

	/**
	 * Updates the commerce payment entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntry the commerce payment entry
	 * @return the commerce payment entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommercePaymentEntry updateCommercePaymentEntry(
		CommercePaymentEntry commercePaymentEntry);

	@Indexable(type = IndexableType.REINDEX)
	public CommercePaymentEntry updateCommercePaymentEntry(
			String externalReferenceCode, long commercePaymentEntryId,
			long commerceChannelId, BigDecimal amount, String callbackURL,
			String cancelURL, String currencyCode, String errorMessages,
			String languageId, String note, String payload,
			String paymentIntegrationKey, int paymentIntegrationType,
			int paymentStatus, String reasonKey, String redirectURL,
			String transactionCode, int type)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommercePaymentEntry updateExternalReferenceCode(
			long commercePaymentEntryId, String externalReferenceCode)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommercePaymentEntry updateNote(
			long commercePaymentEntryId, String note)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommercePaymentEntry updateReasonKey(
			long commercePaymentEntryId, String reasonKey)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:910008949