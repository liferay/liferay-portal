/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.osb.patcher.model.PatcherFix;
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
 * Provides the local service interface for PatcherFix. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface PatcherFixLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.osb.patcher.service.impl.PatcherFixLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the patcher fix local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link PatcherFixLocalServiceUtil} if injection and service tracking are not available.
	 */
	public boolean addPatcherBuildPatcherFix(
		long patcherBuildId, long patcherFixId);

	public boolean addPatcherBuildPatcherFix(
		long patcherBuildId, PatcherFix patcherFix);

	public boolean addPatcherBuildPatcherFixes(
		long patcherBuildId, List<PatcherFix> patcherFixes);

	public boolean addPatcherBuildPatcherFixes(
		long patcherBuildId, long[] patcherFixIds);

	/**
	 * Adds the patcher fix to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFix the patcher fix
	 * @return the patcher fix that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public PatcherFix addPatcherFix(PatcherFix patcherFix);

	public boolean addPatcherFixPackPatcherFix(
		long patcherFixPackId, long patcherFixId);

	public boolean addPatcherFixPackPatcherFix(
		long patcherFixPackId, PatcherFix patcherFix);

	public boolean addPatcherFixPackPatcherFixes(
		long patcherFixPackId, List<PatcherFix> patcherFixes);

	public boolean addPatcherFixPackPatcherFixes(
		long patcherFixPackId, long[] patcherFixIds);

	public void clearPatcherBuildPatcherFixes(long patcherBuildId);

	public void clearPatcherFixPackPatcherFixes(long patcherFixPackId);

	/**
	 * Creates a new patcher fix with the primary key. Does not add the patcher fix to the database.
	 *
	 * @param patcherFixId the primary key for the new patcher fix
	 * @return the new patcher fix
	 */
	@Transactional(enabled = false)
	public PatcherFix createPatcherFix(long patcherFixId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deletePatcherBuildPatcherFix(
		long patcherBuildId, long patcherFixId);

	public void deletePatcherBuildPatcherFix(
		long patcherBuildId, PatcherFix patcherFix);

	public void deletePatcherBuildPatcherFixes(
		long patcherBuildId, List<PatcherFix> patcherFixes);

	public void deletePatcherBuildPatcherFixes(
		long patcherBuildId, long[] patcherFixIds);

	/**
	 * Deletes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix that was removed
	 * @throws PortalException if a patcher fix with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public PatcherFix deletePatcherFix(long patcherFixId)
		throws PortalException;

	/**
	 * Deletes the patcher fix from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFix the patcher fix
	 * @return the patcher fix that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public PatcherFix deletePatcherFix(PatcherFix patcherFix);

	public void deletePatcherFixPackPatcherFix(
		long patcherFixPackId, long patcherFixId);

	public void deletePatcherFixPackPatcherFix(
		long patcherFixPackId, PatcherFix patcherFix);

	public void deletePatcherFixPackPatcherFixes(
		long patcherFixPackId, List<PatcherFix> patcherFixes);

	public void deletePatcherFixPackPatcherFixes(
		long patcherFixPackId, long[] patcherFixIds);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixModelImpl</code>.
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
	public PatcherFix fetchPatcherFix(long patcherFixId);

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
	public List<PatcherFix> getPatcherBuildPatcherFixes(long patcherBuildId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherFix> getPatcherBuildPatcherFixes(
		long patcherBuildId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherFix> getPatcherBuildPatcherFixes(
		long patcherBuildId, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPatcherBuildPatcherFixesCount(long patcherBuildId);

	/**
	 * Returns the patcherBuildIds of the patcher builds associated with the patcher fix.
	 *
	 * @param patcherFixId the patcherFixId of the patcher fix
	 * @return long[] the patcherBuildIds of patcher builds associated with the patcher fix
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getPatcherBuildPrimaryKeys(long patcherFixId);

	/**
	 * Returns the patcher fix with the primary key.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix
	 * @throws PortalException if a patcher fix with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PatcherFix getPatcherFix(long patcherFixId) throws PortalException;

	/**
	 * Returns a range of all the patcher fixes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher fixes
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherFix> getPatcherFixes(int start, int end);

	/**
	 * Returns the number of patcher fixes.
	 *
	 * @return the number of patcher fixes
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPatcherFixesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherFix> getPatcherFixPackPatcherFixes(
		long patcherFixPackId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherFix> getPatcherFixPackPatcherFixes(
		long patcherFixPackId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PatcherFix> getPatcherFixPackPatcherFixes(
		long patcherFixPackId, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPatcherFixPackPatcherFixesCount(long patcherFixPackId);

	/**
	 * Returns the patcherFixPackIds of the patcher fix packs associated with the patcher fix.
	 *
	 * @param patcherFixId the patcherFixId of the patcher fix
	 * @return long[] the patcherFixPackIds of patcher fix packs associated with the patcher fix
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getPatcherFixPackPrimaryKeys(long patcherFixId);

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPatcherBuildPatcherFix(
		long patcherBuildId, long patcherFixId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPatcherBuildPatcherFixes(long patcherBuildId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPatcherFixPackPatcherFix(
		long patcherFixPackId, long patcherFixId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPatcherFixPackPatcherFixes(long patcherFixPackId);

	public void setPatcherBuildPatcherFixes(
		long patcherBuildId, long[] patcherFixIds);

	public void setPatcherFixPackPatcherFixes(
		long patcherFixPackId, long[] patcherFixIds);

	/**
	 * Updates the patcher fix in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFix the patcher fix
	 * @return the patcher fix that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public PatcherFix updatePatcherFix(PatcherFix patcherFix);

}