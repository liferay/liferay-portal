/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
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
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for DDMStructureLayout. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructureLayoutLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface DDMStructureLayoutLocalService
	extends BaseLocalService, CTService<DDMStructureLayout>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.dynamic.data.mapping.service.impl.DDMStructureLayoutLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the ddm structure layout local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link DDMStructureLayoutLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the ddm structure layout to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLayout the ddm structure layout
	 * @return the ddm structure layout that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DDMStructureLayout addDDMStructureLayout(
		DDMStructureLayout ddmStructureLayout);

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #addStructureLayout(long, long, long, String, long,
	 DDMFormLayout, ServiceContext)}
	 */
	@Deprecated
	public DDMStructureLayout addStructureLayout(
			long userId, long groupId, long structureVersionId,
			DDMFormLayout ddmFormLayout, ServiceContext serviceContext)
		throws PortalException;

	public DDMStructureLayout addStructureLayout(
			long userId, long groupId, long classNameId,
			String structureLayoutKey, long structureVersionId,
			DDMFormLayout ddmFormLayout, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public DDMStructureLayout addStructureLayout(
			long userId, long groupId, long classNameId,
			String structureLayoutKey, long structureVersionId,
			Map<Locale, String> name, Map<Locale, String> description,
			String definition, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new ddm structure layout with the primary key. Does not add the ddm structure layout to the database.
	 *
	 * @param structureLayoutId the primary key for the new ddm structure layout
	 * @return the new ddm structure layout
	 */
	@Transactional(enabled = false)
	public DDMStructureLayout createDDMStructureLayout(long structureLayoutId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the ddm structure layout from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLayout the ddm structure layout
	 * @return the ddm structure layout that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public DDMStructureLayout deleteDDMStructureLayout(
		DDMStructureLayout ddmStructureLayout);

	/**
	 * Deletes the ddm structure layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param structureLayoutId the primary key of the ddm structure layout
	 * @return the ddm structure layout that was removed
	 * @throws PortalException if a ddm structure layout with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public DDMStructureLayout deleteDDMStructureLayout(long structureLayoutId)
		throws PortalException;

	public void deleteDDMStructureLayouts(
			long classNameId, DDMStructureVersion ddmStructureVersion)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteStructureLayout(DDMStructureLayout structureLayout);

	public void deleteStructureLayout(long structureLayoutId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl</code>.
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
	public DDMStructureLayout fetchDDMStructureLayout(long structureLayoutId);

	/**
	 * Returns the ddm structure layout matching the UUID and group.
	 *
	 * @param uuid the ddm structure layout's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm structure layout, or <code>null</code> if a matching ddm structure layout could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMStructureLayout fetchDDMStructureLayoutByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMStructureLayout fetchStructureLayout(long structureLayoutId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMStructureLayout fetchStructureLayout(
		long groupId, long classNameId, String structureLayoutKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the ddm structure layout with the primary key.
	 *
	 * @param structureLayoutId the primary key of the ddm structure layout
	 * @return the ddm structure layout
	 * @throws PortalException if a ddm structure layout with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMStructureLayout getDDMStructureLayout(long structureLayoutId)
		throws PortalException;

	/**
	 * Returns the ddm structure layout matching the UUID and group.
	 *
	 * @param uuid the ddm structure layout's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm structure layout
	 * @throws PortalException if a matching ddm structure layout could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMStructureLayout getDDMStructureLayoutByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the ddm structure layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structure layouts
	 * @param end the upper bound of the range of ddm structure layouts (not inclusive)
	 * @return the range of ddm structure layouts
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMStructureLayout> getDDMStructureLayouts(int start, int end);

	/**
	 * Returns all the ddm structure layouts matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm structure layouts
	 * @param companyId the primary key of the company
	 * @return the matching ddm structure layouts, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMStructureLayout> getDDMStructureLayoutsByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of ddm structure layouts matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm structure layouts
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of ddm structure layouts
	 * @param end the upper bound of the range of ddm structure layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching ddm structure layouts, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMStructureLayout> getDDMStructureLayoutsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMStructureLayout> orderByComparator);

	/**
	 * Returns the number of ddm structure layouts.
	 *
	 * @return the number of ddm structure layouts
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDDMStructureLayoutsCount();

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
	public DDMStructureLayout getStructureLayout(long structureLayoutId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMStructureLayout getStructureLayout(
			long groupId, long classNameId, String structureLayoutKey)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMStructureLayout getStructureLayoutByStructureVersionId(
			long structureVersionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMFormLayout getStructureLayoutDDMFormLayout(
		DDMStructureLayout structureLayout);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMStructureLayout> getStructureLayouts(
			long groupId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMStructureLayout> getStructureLayouts(
		long groupId, long classNameId, long structureVersionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMStructureLayout> getStructureLayouts(
		long groupId, long classNameId, long structureVersionId, int start,
		int end, OrderByComparator<DDMStructureLayout> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getStructureLayoutsCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getStructureLayoutsCount(
		long groupId, long classNameId, long structureVersionId);

	/**
	 * Updates the ddm structure layout in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLayout the ddm structure layout
	 * @return the ddm structure layout that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DDMStructureLayout updateDDMStructureLayout(
		DDMStructureLayout ddmStructureLayout);

	public DDMStructureLayout updateStructureLayout(
			long structureLayoutId, DDMFormLayout ddmFormLayout,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public DDMStructureLayout updateStructureLayout(
			long structureLayoutId, long structureVersionId,
			Map<Locale, String> name, Map<Locale, String> description,
			String definition, ServiceContext serviceContext)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<DDMStructureLayout> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<DDMStructureLayout> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDMStructureLayout>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:1586769185