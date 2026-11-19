/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
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
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
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
 * Provides the local service interface for LayoutPageTemplateStructureRel. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface LayoutPageTemplateStructureRelLocalService
	extends BaseLocalService, CTService<LayoutPageTemplateStructureRel>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.layout.page.template.service.impl.LayoutPageTemplateStructureRelLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the layout page template structure rel local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link LayoutPageTemplateStructureRelLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the layout page template structure rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRel the layout page template structure rel
	 * @return the layout page template structure rel that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public LayoutPageTemplateStructureRel addLayoutPageTemplateStructureRel(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel);

	public LayoutPageTemplateStructureRel addLayoutPageTemplateStructureRel(
			long userId, long groupId, long layoutPageTemplateStructureId,
			long segmentsExperienceId, String data,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new layout page template structure rel with the primary key. Does not add the layout page template structure rel to the database.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key for the new layout page template structure rel
	 * @return the new layout page template structure rel
	 */
	@Transactional(enabled = false)
	public LayoutPageTemplateStructureRel createLayoutPageTemplateStructureRel(
		long layoutPageTemplateStructureRelId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the layout page template structure rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRel the layout page template structure rel
	 * @return the layout page template structure rel that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public LayoutPageTemplateStructureRel deleteLayoutPageTemplateStructureRel(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel);

	/**
	 * Deletes the layout page template structure rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelId the primary key of the layout page template structure rel
	 * @return the layout page template structure rel that was removed
	 * @throws PortalException if a layout page template structure rel with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public LayoutPageTemplateStructureRel deleteLayoutPageTemplateStructureRel(
			long layoutPageTemplateStructureRelId)
		throws PortalException;

	public void deleteLayoutPageTemplateStructureRelsBySegmentsExperienceId(
		long segmentsExperienceId);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelModelImpl</code>.
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
	public LayoutPageTemplateStructureRel fetchLayoutPageTemplateStructureRel(
		long layoutPageTemplateStructureRelId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutPageTemplateStructureRel fetchLayoutPageTemplateStructureRel(
		long layoutPageTemplateStructureId, long segmentsExperienceId);

	/**
	 * Returns the layout page template structure rel matching the UUID and group.
	 *
	 * @param uuid the layout page template structure rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutPageTemplateStructureRel
		fetchLayoutPageTemplateStructureRelByUuidAndGroupId(
			String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the layout page template structure rel with the primary key.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key of the layout page template structure rel
	 * @return the layout page template structure rel
	 * @throws PortalException if a layout page template structure rel with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutPageTemplateStructureRel getLayoutPageTemplateStructureRel(
			long layoutPageTemplateStructureRelId)
		throws PortalException;

	/**
	 * Returns the layout page template structure rel matching the UUID and group.
	 *
	 * @param uuid the layout page template structure rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template structure rel
	 * @throws PortalException if a matching layout page template structure rel could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutPageTemplateStructureRel
			getLayoutPageTemplateStructureRelByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the layout page template structure rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @return the range of layout page template structure rels
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateStructureRel>
		getLayoutPageTemplateStructureRels(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateStructureRel>
		getLayoutPageTemplateStructureRels(long layoutPageTemplateStructureId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateStructureRel>
		getLayoutPageTemplateStructureRelsBySegmentsExperienceId(
			long segmentsExperienceId);

	/**
	 * Returns all the layout page template structure rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template structure rels
	 * @param companyId the primary key of the company
	 * @return the matching layout page template structure rels, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateStructureRel>
		getLayoutPageTemplateStructureRelsByUuidAndCompanyId(
			String uuid, long companyId);

	/**
	 * Returns a range of layout page template structure rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template structure rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layout page template structure rels, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateStructureRel>
		getLayoutPageTemplateStructureRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRel>
				orderByComparator);

	/**
	 * Returns the number of layout page template structure rels.
	 *
	 * @return the number of layout page template structure rels
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateStructureRelsCount();

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
	 * Updates the layout page template structure rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRel the layout page template structure rel
	 * @return the layout page template structure rel that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public LayoutPageTemplateStructureRel updateLayoutPageTemplateStructureRel(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel);

	public LayoutPageTemplateStructureRel updateLayoutPageTemplateStructureRel(
			long layoutPageTemplateStructureId, long segmentsExperienceId,
			String data)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<LayoutPageTemplateStructureRel> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<LayoutPageTemplateStructureRel> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<LayoutPageTemplateStructureRel>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-376850939