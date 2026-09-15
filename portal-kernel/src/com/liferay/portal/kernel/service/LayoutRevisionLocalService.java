/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for LayoutRevision. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutRevisionLocalServiceUtil
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.portal.kernel.model.LayoutRevision"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface LayoutRevisionLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutRevisionLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the layout revision local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link LayoutRevisionLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the layout revision to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutRevisionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutRevision the layout revision
	 * @return the layout revision that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public LayoutRevision addLayoutRevision(LayoutRevision layoutRevision);

	public LayoutRevision addLayoutRevision(
			long userId, long layoutSetBranchId, long layoutBranchId,
			long parentLayoutRevisionId, boolean head, long plid,
			long portletPreferencesPlid, boolean privateLayout, String name,
			String title, String description, String keywords, String robots,
			String typeSettings, boolean iconImage, long iconImageId,
			String themeId, String colorSchemeId, String css,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new layout revision with the primary key. Does not add the layout revision to the database.
	 *
	 * @param layoutRevisionId the primary key for the new layout revision
	 * @return the new layout revision
	 */
	@Transactional(enabled = false)
	public LayoutRevision createLayoutRevision(long layoutRevisionId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteLayoutLayoutRevisions(long plid) throws PortalException;

	/**
	 * Deletes the layout revision from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutRevisionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutRevision the layout revision
	 * @return the layout revision that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public LayoutRevision deleteLayoutRevision(LayoutRevision layoutRevision)
		throws PortalException;

	/**
	 * Deletes the layout revision with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutRevisionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutRevisionId the primary key of the layout revision
	 * @return the layout revision that was removed
	 * @throws PortalException if a layout revision with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public LayoutRevision deleteLayoutRevision(long layoutRevisionId)
		throws PortalException;

	public void deleteLayoutRevisions(long layoutSetBranchId, long plid)
		throws PortalException;

	public void deleteLayoutRevisions(
			long layoutSetBranchId, long layoutBranchId, long plid)
		throws PortalException;

	public void deleteLayoutSetBranchLayoutRevisions(long layoutSetBranchId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
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
	public LayoutRevision fetchLastLayoutRevision(long plid, boolean head);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutRevision fetchLatestLayoutRevision(
		long layoutSetBranchId, long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutRevision fetchLatestLayoutRevision(
		long layoutSetBranchId, long layoutBranchId, long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutRevision fetchLayoutRevision(long layoutRevisionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutRevision> getChildLayoutRevisions(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutRevision> getChildLayoutRevisions(
		long layoutSetBranchId, long parentLayoutRevision, long plid, int start,
		int end, OrderByComparator<LayoutRevision> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getChildLayoutRevisionsCount(
		long layoutSetBranchId, long parentLayoutRevision, long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the layout revision with the primary key.
	 *
	 * @param layoutRevisionId the primary key of the layout revision
	 * @return the layout revision
	 * @throws PortalException if a layout revision with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutRevision getLayoutRevision(long layoutRevisionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutRevision getLayoutRevision(
			long layoutSetBranchId, long layoutBranchId, long plid)
		throws PortalException;

	/**
	 * Returns a range of all the layout revisions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of layout revisions
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutRevision> getLayoutRevisions(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutRevision> getLayoutRevisions(long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, boolean head);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, boolean head, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long plid, boolean head);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long plid, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long plid, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long layoutBranchId, long plid, int start,
		int end, OrderByComparator<LayoutRevision> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutRevision> getLayoutRevisionsByStatus(int status);

	/**
	 * Returns the number of layout revisions.
	 *
	 * @return the number of layout revisions
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutRevisionsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutRevisionsCount(long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutRevisionsCount(
		long layoutSetBranchId, long layoutBranchId, long plid);

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
	 * Updates the layout revision in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutRevisionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutRevision the layout revision
	 * @return the layout revision that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public LayoutRevision updateLayoutRevision(LayoutRevision layoutRevision);

	public LayoutRevision updateLayoutRevision(
			long userId, long layoutRevisionId, long layoutBranchId,
			String name, String title, String description, String keywords,
			String robots, String typeSettings, boolean iconImage,
			long iconImageId, String themeId, String colorSchemeId, String css,
			ServiceContext serviceContext)
		throws PortalException;

	public LayoutRevision updateStatus(
			long userId, long layoutRevisionId, int status,
			ServiceContext serviceContext)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-476905312