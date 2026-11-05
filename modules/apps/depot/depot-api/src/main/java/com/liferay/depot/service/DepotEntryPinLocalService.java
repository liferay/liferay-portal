/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service;

import com.liferay.depot.model.DepotEntryPin;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for DepotEntryPin. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryPinLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface DepotEntryPinLocalService
	extends BaseLocalService, CTService<DepotEntryPin>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.depot.service.impl.DepotEntryPinLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the depot entry pin local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link DepotEntryPinLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the depot entry pin to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryPinLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryPin the depot entry pin
	 * @return the depot entry pin that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DepotEntryPin addDepotEntryPin(DepotEntryPin depotEntryPin);

	public DepotEntryPin addDepotEntryPin(long userId, long depotEntryId)
		throws PortalException;

	/**
	 * Creates a new depot entry pin with the primary key. Does not add the depot entry pin to the database.
	 *
	 * @param depotEntryPinId the primary key for the new depot entry pin
	 * @return the new depot entry pin
	 */
	@Transactional(enabled = false)
	public DepotEntryPin createDepotEntryPin(long depotEntryPinId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteDepotEntryDepotEntryPins(long depotEntryId);

	/**
	 * Deletes the depot entry pin from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryPinLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryPin the depot entry pin
	 * @return the depot entry pin that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public DepotEntryPin deleteDepotEntryPin(DepotEntryPin depotEntryPin);

	/**
	 * Deletes the depot entry pin with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryPinLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryPinId the primary key of the depot entry pin
	 * @return the depot entry pin that was removed
	 * @throws PortalException if a depot entry pin with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public DepotEntryPin deleteDepotEntryPin(long depotEntryPinId)
		throws PortalException;

	public DepotEntryPin deleteDepotEntryPin(long userId, long depotEntryId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteUserDepotEntryPins(long userId);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryPinModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryPinModelImpl</code>.
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
	public DepotEntryPin fetchDepotEntryPin(long depotEntryPinId);

	/**
	 * Returns the depot entry pin matching the UUID and group.
	 *
	 * @param uuid the depot entry pin's UUID
	 * @param groupId the primary key of the group
	 * @return the matching depot entry pin, or <code>null</code> if a matching depot entry pin could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DepotEntryPin fetchDepotEntryPinByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DepotEntryPin fetchGroupDepotEntryPin(long groupId, long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the depot entry pin with the primary key.
	 *
	 * @param depotEntryPinId the primary key of the depot entry pin
	 * @return the depot entry pin
	 * @throws PortalException if a depot entry pin with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DepotEntryPin getDepotEntryPin(long depotEntryPinId)
		throws PortalException;

	/**
	 * Returns the depot entry pin matching the UUID and group.
	 *
	 * @param uuid the depot entry pin's UUID
	 * @param groupId the primary key of the group
	 * @return the matching depot entry pin
	 * @throws PortalException if a matching depot entry pin could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DepotEntryPin getDepotEntryPinByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the depot entry pins.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryPinModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entry pins
	 * @param end the upper bound of the range of depot entry pins (not inclusive)
	 * @return the range of depot entry pins
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DepotEntryPin> getDepotEntryPins(int start, int end);

	/**
	 * Returns all the depot entry pins matching the UUID and company.
	 *
	 * @param uuid the UUID of the depot entry pins
	 * @param companyId the primary key of the company
	 * @return the matching depot entry pins, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DepotEntryPin> getDepotEntryPinsByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of depot entry pins matching the UUID and company.
	 *
	 * @param uuid the UUID of the depot entry pins
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of depot entry pins
	 * @param end the upper bound of the range of depot entry pins (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching depot entry pins, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DepotEntryPin> getDepotEntryPinsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DepotEntryPin> orderByComparator);

	/**
	 * Returns the number of depot entry pins.
	 *
	 * @return the number of depot entry pins
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDepotEntryPinsCount();

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
	public List<DepotEntryPin> getUserDepotEntryPins(
		long userId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserDepotEntryPinsCount(long userId);

	/**
	 * Updates the depot entry pin in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryPinLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryPin the depot entry pin
	 * @return the depot entry pin that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DepotEntryPin updateDepotEntryPin(DepotEntryPin depotEntryPin);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<DepotEntryPin> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<DepotEntryPin> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DepotEntryPin>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1156582129