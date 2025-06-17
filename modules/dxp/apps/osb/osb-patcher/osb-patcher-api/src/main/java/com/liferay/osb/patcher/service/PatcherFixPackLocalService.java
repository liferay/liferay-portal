/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.petra.sql.dsl.query.DSLQuery;
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
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for PatcherFixPack. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixPackLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface PatcherFixPackLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.osb.patcher.service.impl.PatcherFixPackLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the patcher fix pack local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link PatcherFixPackLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the patcher fix pack to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixPackLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixPack the patcher fix pack
	 * @return the patcher fix pack that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public PatcherFixPack addPatcherFixPack(PatcherFixPack patcherFixPack);

	public boolean addPatcherFixPatcherFixPack(
		long patcherFixId, long patcherFixPackId);

	public boolean addPatcherFixPatcherFixPack(
		long patcherFixId, PatcherFixPack patcherFixPack);

	public boolean addPatcherFixPatcherFixPacks(
		long patcherFixId, List<PatcherFixPack> patcherFixPacks);

	public boolean addPatcherFixPatcherFixPacks(
		long patcherFixId, long[] patcherFixPackIds);

	public void clearPatcherFixPatcherFixPacks(long patcherFixId);

	/**
	 * Creates a new patcher fix pack with the primary key. Does not add the patcher fix pack to the database.
	 *
	 * @param patcherFixPackId the primary key for the new patcher fix pack
	 * @return the new patcher fix pack
	 */
	@Transactional(enabled = false)
	public PatcherFixPack createPatcherFixPack(long patcherFixPackId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixPackLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack that was removed
	 * @throws PortalException if a patcher fix pack with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public PatcherFixPack deletePatcherFixPack(long patcherFixPackId)
		throws PortalException;

	/**
	 * Deletes the patcher fix pack from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixPackLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixPack the patcher fix pack
	 * @return the patcher fix pack that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public PatcherFixPack deletePatcherFixPack(PatcherFixPack patcherFixPack);

	public void deletePatcherFixPatcherFixPack(
		long patcherFixId, long patcherFixPackId);

	public void deletePatcherFixPatcherFixPack(
		long patcherFixId, PatcherFixPack patcherFixPack);

	public void deletePatcherFixPatcherFixPacks(
		long patcherFixId, List<PatcherFixPack> patcherFixPacks);

	public void deletePatcherFixPatcherFixPacks(
		long patcherFixId, long[] patcherFixPackIds);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl</code>.
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
	public PatcherFixPack fetchPatcherFixPack(long patcherFixPackId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * Returns the patcher fix pack with the primary key.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack
	 * @throws PortalException if a patcher fix pack with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PatcherFixPack getPatcherFixPack(long patcherFixPackId)
		throws PortalException;

	/**
	 * Returns a range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of patcher fix packs
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherFixPack> getPatcherFixPacks(int start, int end);

	/**
	 * Returns the number of patcher fix packs.
	 *
	 * @return the number of patcher fix packs
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPatcherFixPacksCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherFixPack> getPatcherFixPatcherFixPacks(long patcherFixId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long patcherFixId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long patcherFixId, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPatcherFixPatcherFixPacksCount(long patcherFixId);

	/**
	 * Returns the patcherFixIds of the patcher fixes associated with the patcher fix pack.
	 *
	 * @param patcherFixPackId the patcherFixPackId of the patcher fix pack
	 * @return long[] the patcherFixIds of patcher fixes associated with the patcher fix pack
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getPatcherFixPrimaryKeys(long patcherFixPackId);

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPatcherFixPatcherFixPack(
		long patcherFixId, long patcherFixPackId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPatcherFixPatcherFixPacks(long patcherFixId);

	public void setPatcherFixPatcherFixPacks(
		long patcherFixId, long[] patcherFixPackIds);

	/**
	 * Updates the patcher fix pack in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixPackLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixPack the patcher fix pack
	 * @return the patcher fix pack that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public PatcherFixPack updatePatcherFixPack(PatcherFixPack patcherFixPack);

}