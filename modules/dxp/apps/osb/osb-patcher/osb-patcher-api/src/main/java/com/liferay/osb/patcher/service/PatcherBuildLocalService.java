/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.osb.patcher.model.PatcherBuild;
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
 * Provides the local service interface for PatcherBuild. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuildLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface PatcherBuildLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.osb.patcher.service.impl.PatcherBuildLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the patcher build local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link PatcherBuildLocalServiceUtil} if injection and service tracking are not available.
	 */
	public boolean addPatcherAccountPatcherBuild(
		long patcherAccountId, long patcherBuildId);

	public boolean addPatcherAccountPatcherBuild(
		long patcherAccountId, PatcherBuild patcherBuild);

	public boolean addPatcherAccountPatcherBuilds(
		long patcherAccountId, List<PatcherBuild> patcherBuilds);

	public boolean addPatcherAccountPatcherBuilds(
		long patcherAccountId, long[] patcherBuildIds);

	/**
	 * Adds the patcher build to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuild the patcher build
	 * @return the patcher build that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public PatcherBuild addPatcherBuild(PatcherBuild patcherBuild);

	public boolean addPatcherFixPatcherBuild(
		long patcherFixId, long patcherBuildId);

	public boolean addPatcherFixPatcherBuild(
		long patcherFixId, PatcherBuild patcherBuild);

	public boolean addPatcherFixPatcherBuilds(
		long patcherFixId, List<PatcherBuild> patcherBuilds);

	public boolean addPatcherFixPatcherBuilds(
		long patcherFixId, long[] patcherBuildIds);

	public void clearPatcherAccountPatcherBuilds(long patcherAccountId);

	public void clearPatcherFixPatcherBuilds(long patcherFixId);

	/**
	 * Creates a new patcher build with the primary key. Does not add the patcher build to the database.
	 *
	 * @param patcherBuildId the primary key for the new patcher build
	 * @return the new patcher build
	 */
	@Transactional(enabled = false)
	public PatcherBuild createPatcherBuild(long patcherBuildId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deletePatcherAccountPatcherBuild(
		long patcherAccountId, long patcherBuildId);

	public void deletePatcherAccountPatcherBuild(
		long patcherAccountId, PatcherBuild patcherBuild);

	public void deletePatcherAccountPatcherBuilds(
		long patcherAccountId, List<PatcherBuild> patcherBuilds);

	public void deletePatcherAccountPatcherBuilds(
		long patcherAccountId, long[] patcherBuildIds);

	/**
	 * Deletes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build that was removed
	 * @throws PortalException if a patcher build with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public PatcherBuild deletePatcherBuild(long patcherBuildId)
		throws PortalException;

	/**
	 * Deletes the patcher build from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuild the patcher build
	 * @return the patcher build that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public PatcherBuild deletePatcherBuild(PatcherBuild patcherBuild);

	public void deletePatcherFixPatcherBuild(
		long patcherFixId, long patcherBuildId);

	public void deletePatcherFixPatcherBuild(
		long patcherFixId, PatcherBuild patcherBuild);

	public void deletePatcherFixPatcherBuilds(
		long patcherFixId, List<PatcherBuild> patcherBuilds);

	public void deletePatcherFixPatcherBuilds(
		long patcherFixId, long[] patcherBuildIds);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl</code>.
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
	public PatcherBuild fetchPatcherBuild(long patcherBuildId);

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

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long patcherAccountId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long patcherAccountId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long patcherAccountId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPatcherAccountPatcherBuildsCount(long patcherAccountId);

	/**
	 * Returns the patcherAccountIds of the patcher accounts associated with the patcher build.
	 *
	 * @param patcherBuildId the patcherBuildId of the patcher build
	 * @return long[] the patcherAccountIds of patcher accounts associated with the patcher build
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getPatcherAccountPrimaryKeys(long patcherBuildId);

	/**
	 * Returns the patcher build with the primary key.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build
	 * @throws PortalException if a patcher build with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PatcherBuild getPatcherBuild(long patcherBuildId)
		throws PortalException;

	/**
	 * Returns a range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher builds
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherBuild> getPatcherBuilds(int start, int end);

	/**
	 * Returns the number of patcher builds.
	 *
	 * @return the number of patcher builds
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPatcherBuildsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherBuild> getPatcherFixPatcherBuilds(long patcherFixId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherBuild> getPatcherFixPatcherBuilds(
		long patcherFixId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherBuild> getPatcherFixPatcherBuilds(
		long patcherFixId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPatcherFixPatcherBuildsCount(long patcherFixId);

	/**
	 * Returns the patcherFixIds of the patcher fixes associated with the patcher build.
	 *
	 * @param patcherBuildId the patcherBuildId of the patcher build
	 * @return long[] the patcherFixIds of patcher fixes associated with the patcher build
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getPatcherFixPrimaryKeys(long patcherBuildId);

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPatcherAccountPatcherBuild(
		long patcherAccountId, long patcherBuildId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPatcherAccountPatcherBuilds(long patcherAccountId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPatcherFixPatcherBuild(
		long patcherFixId, long patcherBuildId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPatcherFixPatcherBuilds(long patcherFixId);

	public void setPatcherAccountPatcherBuilds(
		long patcherAccountId, long[] patcherBuildIds);

	public void setPatcherFixPatcherBuilds(
		long patcherFixId, long[] patcherBuildIds);

	/**
	 * Updates the patcher build in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuild the patcher build
	 * @return the patcher build that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public PatcherBuild updatePatcherBuild(PatcherBuild patcherBuild);

}