/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.service;

import com.liferay.data.engine.model.DEDataDefinitionFieldLink;
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
 * Provides the local service interface for DEDataDefinitionFieldLink. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see DEDataDefinitionFieldLinkLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface DEDataDefinitionFieldLinkLocalService
	extends BaseLocalService, CTService<DEDataDefinitionFieldLink>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.data.engine.service.impl.DEDataDefinitionFieldLinkLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the de data definition field link local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link DEDataDefinitionFieldLinkLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the de data definition field link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataDefinitionFieldLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataDefinitionFieldLink the de data definition field link
	 * @return the de data definition field link that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DEDataDefinitionFieldLink addDEDataDefinitionFieldLink(
		DEDataDefinitionFieldLink deDataDefinitionFieldLink);

	public DEDataDefinitionFieldLink addDEDataDefinitionFieldLink(
			long groupId, long classNameId, long classPK, long ddmStructureId,
			String fieldName)
		throws PortalException;

	public DEDataDefinitionFieldLink addDEDataDefinitionFieldLink(
			long groupId, long classNameId, long classPK, long ddmStructureId,
			String fieldName, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new de data definition field link with the primary key. Does not add the de data definition field link to the database.
	 *
	 * @param deDataDefinitionFieldLinkId the primary key for the new de data definition field link
	 * @return the new de data definition field link
	 */
	@Transactional(enabled = false)
	public DEDataDefinitionFieldLink createDEDataDefinitionFieldLink(
		long deDataDefinitionFieldLinkId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the de data definition field link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataDefinitionFieldLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataDefinitionFieldLink the de data definition field link
	 * @return the de data definition field link that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public DEDataDefinitionFieldLink deleteDEDataDefinitionFieldLink(
		DEDataDefinitionFieldLink deDataDefinitionFieldLink);

	/**
	 * Deletes the de data definition field link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataDefinitionFieldLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataDefinitionFieldLinkId the primary key of the de data definition field link
	 * @return the de data definition field link that was removed
	 * @throws PortalException if a de data definition field link with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public DEDataDefinitionFieldLink deleteDEDataDefinitionFieldLink(
			long deDataDefinitionFieldLinkId)
		throws PortalException;

	public void deleteDEDataDefinitionFieldLinks(long ddmStructureId);

	public void deleteDEDataDefinitionFieldLinks(
		long classNameId, long classPK);

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 DEDataDefinitionFieldLinkLocalServiceImpl#deleteDEDataDefinitionFieldLinks(
	 long, long, String[])}
	 */
	@Deprecated
	public void deleteDEDataDefinitionFieldLinks(
		long classNameId, long ddmStructureId, String fieldName);

	public void deleteDEDataDefinitionFieldLinks(
		long classNameId, long ddmStructureId, String[] fieldNames);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkModelImpl</code>.
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
	public DEDataDefinitionFieldLink fetchDEDataDefinitionFieldLink(
		long deDataDefinitionFieldLinkId);

	/**
	 * Returns the de data definition field link matching the UUID and group.
	 *
	 * @param uuid the de data definition field link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching de data definition field link, or <code>null</code> if a matching de data definition field link could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DEDataDefinitionFieldLink
		fetchDEDataDefinitionFieldLinkByUuidAndGroupId(
			String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DEDataDefinitionFieldLink fetchDEDataDefinitionFieldLinks(
		long classNameId, long classPK, long ddmStructureId, String fieldName);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getClassPKS(long classNameId, long ddmStructureId);

	/**
	 * Returns the de data definition field link with the primary key.
	 *
	 * @param deDataDefinitionFieldLinkId the primary key of the de data definition field link
	 * @return the de data definition field link
	 * @throws PortalException if a de data definition field link with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DEDataDefinitionFieldLink getDEDataDefinitionFieldLink(
			long deDataDefinitionFieldLinkId)
		throws PortalException;

	/**
	 * Returns the de data definition field link matching the UUID and group.
	 *
	 * @param uuid the de data definition field link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching de data definition field link
	 * @throws PortalException if a matching de data definition field link could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DEDataDefinitionFieldLink
			getDEDataDefinitionFieldLinkByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the de data definition field links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @return the range of de data definition field links
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DEDataDefinitionFieldLink> getDEDataDefinitionFieldLinks(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DEDataDefinitionFieldLink> getDEDataDefinitionFieldLinks(
		long ddmStructureId);

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 DEDataDefinitionFieldLinkLocalServiceImpl#getDEDataDefinitionFieldLinks(
	 long, long, String[])}
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DEDataDefinitionFieldLink> getDEDataDefinitionFieldLinks(
		long classNameId, long ddmStructureId, String fieldName);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DEDataDefinitionFieldLink> getDEDataDefinitionFieldLinks(
		long classNameId, long ddmStructureId, String[] fieldNames);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DEDataDefinitionFieldLink> getDEDataDefinitionFieldLinks(
		long ddmStructureId, String[] fieldNames);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinksByClassNameIdAndClassPK(
			long classNameId, long classPK);

	/**
	 * Returns all the de data definition field links matching the UUID and company.
	 *
	 * @param uuid the UUID of the de data definition field links
	 * @param companyId the primary key of the company
	 * @return the matching de data definition field links, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinksByUuidAndCompanyId(
			String uuid, long companyId);

	/**
	 * Returns a range of de data definition field links matching the UUID and company.
	 *
	 * @param uuid the UUID of the de data definition field links
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching de data definition field links, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinksByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<DEDataDefinitionFieldLink> orderByComparator);

	/**
	 * Returns the number of de data definition field links.
	 *
	 * @return the number of de data definition field links
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDEDataDefinitionFieldLinksCount();

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
	 * Updates the de data definition field link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataDefinitionFieldLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataDefinitionFieldLink the de data definition field link
	 * @return the de data definition field link that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DEDataDefinitionFieldLink updateDEDataDefinitionFieldLink(
		DEDataDefinitionFieldLink deDataDefinitionFieldLink);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<DEDataDefinitionFieldLink> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<DEDataDefinitionFieldLink> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DEDataDefinitionFieldLink>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-860418285