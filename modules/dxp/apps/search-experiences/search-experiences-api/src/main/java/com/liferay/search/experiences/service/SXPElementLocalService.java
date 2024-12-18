/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.sql.dsl.query.DSLQuery;
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
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.search.experiences.model.SXPElement;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for SXPElement. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see SXPElementLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface SXPElementLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.search.experiences.service.impl.SXPElementLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the sxp element local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link SXPElementLocalServiceUtil} if injection and service tracking are not available.
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SXPElement addSXPElement(
			String externalReferenceCode, long userId,
			Map<Locale, String> descriptionMap, String elementDefinitionJSON,
			String fallbackDescription, String fallbackTitle, boolean readOnly,
			String schemaVersion, Map<Locale, String> titleMap, int type,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds the sxp element to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPElementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpElement the sxp element
	 * @return the sxp element that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SXPElement addSXPElement(SXPElement sxpElement);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new sxp element with the primary key. Does not add the sxp element to the database.
	 *
	 * @param sxpElementId the primary key for the new sxp element
	 * @return the new sxp element
	 */
	@Transactional(enabled = false)
	public SXPElement createSXPElement(long sxpElementId);

	public void deleteCompanySXPElements(long companyId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Deletes the sxp element with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPElementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpElementId the primary key of the sxp element
	 * @return the sxp element that was removed
	 * @throws PortalException if a sxp element with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public SXPElement deleteSXPElement(long sxpElementId)
		throws PortalException;

	/**
	 * Deletes the sxp element from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPElementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpElement the sxp element
	 * @return the sxp element that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SXPElement deleteSXPElement(SXPElement sxpElement)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.search.experiences.model.impl.SXPElementModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.search.experiences.model.impl.SXPElementModelImpl</code>.
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
	public SXPElement fetchSXPElement(long sxpElementId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SXPElement fetchSXPElementByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the sxp element with the matching UUID and company.
	 *
	 * @param uuid the sxp element's UUID
	 * @param companyId the primary key of the company
	 * @return the matching sxp element, or <code>null</code> if a matching sxp element could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SXPElement fetchSXPElementByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

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
	 * Returns the sxp element with the primary key.
	 *
	 * @param sxpElementId the primary key of the sxp element
	 * @return the sxp element
	 * @throws PortalException if a sxp element with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SXPElement getSXPElement(long sxpElementId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SXPElement getSXPElementByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the sxp element with the matching UUID and company.
	 *
	 * @param uuid the sxp element's UUID
	 * @param companyId the primary key of the company
	 * @return the matching sxp element
	 * @throws PortalException if a matching sxp element could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SXPElement getSXPElementByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException;

	/**
	 * Returns a range of all the sxp elements.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.search.experiences.model.impl.SXPElementModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of sxp elements
	 * @param end the upper bound of the range of sxp elements (not inclusive)
	 * @return the range of sxp elements
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SXPElement> getSXPElements(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SXPElement> getSXPElements(long companyId, boolean readOnly);

	/**
	 * Returns the number of sxp elements.
	 *
	 * @return the number of sxp elements
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSXPElementsCount();

	@Indexable(type = IndexableType.REINDEX)
	public SXPElement updateSXPElement(
			String externalReferenceCode, long userId, long sxpElementId,
			Map<Locale, String> descriptionMap, String elementDefinitionJSON,
			boolean hidden, String schemaVersion, Map<Locale, String> titleMap,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the sxp element in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPElementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpElement the sxp element
	 * @return the sxp element that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SXPElement updateSXPElement(SXPElement sxpElement);

}