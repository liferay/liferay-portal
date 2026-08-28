/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service;

import com.liferay.layout.content.model.LayoutContentVersionPreview;
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
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for LayoutContentVersionPreview. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Lourdes Fernández Besada
 * @see LayoutContentVersionPreviewLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface LayoutContentVersionPreviewLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.layout.content.service.impl.LayoutContentVersionPreviewLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the layout content version preview local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link LayoutContentVersionPreviewLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the layout content version preview to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersionPreview the layout content version preview
	 * @return the layout content version preview that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public LayoutContentVersionPreview addLayoutContentVersionPreview(
		LayoutContentVersionPreview layoutContentVersionPreview);

	public LayoutContentVersionPreview addLayoutContentVersionPreview(
			long userId, long layoutContentVersionId, String html,
			String languageId, String segmentsExperienceERC)
		throws PortalException;

	/**
	 * Creates a new layout content version preview with the primary key. Does not add the layout content version preview to the database.
	 *
	 * @param layoutContentVersionPreviewId the primary key for the new layout content version preview
	 * @return the new layout content version preview
	 */
	@Transactional(enabled = false)
	public LayoutContentVersionPreview createLayoutContentVersionPreview(
		long layoutContentVersionPreviewId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the layout content version preview from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersionPreview the layout content version preview
	 * @return the layout content version preview that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public LayoutContentVersionPreview deleteLayoutContentVersionPreview(
		LayoutContentVersionPreview layoutContentVersionPreview);

	/**
	 * Deletes the layout content version preview with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview that was removed
	 * @throws PortalException if a layout content version preview with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public LayoutContentVersionPreview deleteLayoutContentVersionPreview(
			long layoutContentVersionPreviewId)
		throws PortalException;

	public void deleteLayoutContentVersionPreviews(long layoutContentVersionId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
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
	public LayoutContentVersionPreview fetchLayoutContentVersionPreview(
		long layoutContentVersionPreviewId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutContentVersionPreview fetchLayoutContentVersionPreview(
			long layoutContentVersionId, String languageId,
			String segmentsExperienceERC)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the layout content version preview with the primary key.
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview
	 * @throws PortalException if a layout content version preview with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutContentVersionPreview getLayoutContentVersionPreview(
			long layoutContentVersionPreviewId)
		throws PortalException;

	/**
	 * Returns a range of all the layout content version previews.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout content version previews
	 * @param end the upper bound of the range of layout content version previews (not inclusive)
	 * @return the range of layout content version previews
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutContentVersionPreview> getLayoutContentVersionPreviews(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutContentVersionPreview> getLayoutContentVersionPreviews(
			long layoutContentVersionId)
		throws PortalException;

	/**
	 * Returns the number of layout content version previews.
	 *
	 * @return the number of layout content version previews
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutContentVersionPreviewsCount();

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
	public Map<String, List<String>> getSegmentsExperienceERCsLanguageIds(
			long layoutContentVersionId)
		throws PortalException;

	/**
	 * Updates the layout content version preview in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersionPreview the layout content version preview
	 * @return the layout content version preview that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public LayoutContentVersionPreview updateLayoutContentVersionPreview(
		LayoutContentVersionPreview layoutContentVersionPreview);

}
// LIFERAY-SERVICE-BUILDER-HASH:1016787761