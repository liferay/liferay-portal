/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.service;

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
import com.liferay.template.model.TemplateEntry;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for TemplateEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see TemplateEntryLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface TemplateEntryLocalService
	extends BaseLocalService, CTService<TemplateEntry>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.template.service.impl.TemplateEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the template entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link TemplateEntryLocalServiceUtil} if injection and service tracking are not available.
	 */
	public TemplateEntry addTemplateEntry(
			String externalReferenceCode, long userId, long groupId,
			long ddmTemplateId, String infoItemClassName,
			String infoItemFormVariationKey, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds the template entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param templateEntry the template entry
	 * @return the template entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public TemplateEntry addTemplateEntry(TemplateEntry templateEntry);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new template entry with the primary key. Does not add the template entry to the database.
	 *
	 * @param templateEntryId the primary key for the new template entry
	 * @return the new template entry
	 */
	@Transactional(enabled = false)
	public TemplateEntry createTemplateEntry(long templateEntryId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteTemplateEntries(long groupId);

	/**
	 * Deletes the template entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param templateEntryId the primary key of the template entry
	 * @return the template entry that was removed
	 * @throws PortalException if a template entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public TemplateEntry deleteTemplateEntry(long templateEntryId)
		throws PortalException;

	/**
	 * Deletes the template entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param templateEntry the template entry
	 * @return the template entry that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public TemplateEntry deleteTemplateEntry(TemplateEntry templateEntry);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
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
	public TemplateEntry fetchTemplateEntry(long templateEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public TemplateEntry fetchTemplateEntryByDDMTemplateId(long ddmTemplateId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public TemplateEntry fetchTemplateEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the template entry matching the UUID and group.
	 *
	 * @param uuid the template entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public TemplateEntry fetchTemplateEntryByUuidAndGroupId(
		String uuid, long groupId);

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
	 * Returns a range of all the template entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @return the range of template entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<TemplateEntry> getTemplateEntries(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<TemplateEntry> getTemplateEntries(
		long groupId, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<TemplateEntry> getTemplateEntries(
		long groupId, String infoItemClassName, String infoItemFormVariationKey,
		int start, int end, OrderByComparator<TemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<TemplateEntry> getTemplateEntries(
		long[] groupIds, String infoItemClassName,
		String infoItemFormVariationKey, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator);

	/**
	 * Returns all the template entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the template entries
	 * @param companyId the primary key of the company
	 * @return the matching template entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<TemplateEntry> getTemplateEntriesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of template entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the template entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching template entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<TemplateEntry> getTemplateEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator);

	/**
	 * Returns the number of template entries.
	 *
	 * @return the number of template entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getTemplateEntriesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getTemplateEntriesCount(long groupId);

	/**
	 * Returns the template entry with the primary key.
	 *
	 * @param templateEntryId the primary key of the template entry
	 * @return the template entry
	 * @throws PortalException if a template entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public TemplateEntry getTemplateEntry(long templateEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public TemplateEntry getTemplateEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the template entry matching the UUID and group.
	 *
	 * @param uuid the template entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching template entry
	 * @throws PortalException if a matching template entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public TemplateEntry getTemplateEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	public TemplateEntry updateTemplateEntry(long templateEntryId)
		throws PortalException;

	/**
	 * Updates the template entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param templateEntry the template entry
	 * @return the template entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public TemplateEntry updateTemplateEntry(TemplateEntry templateEntry);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<TemplateEntry> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<TemplateEntry> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<TemplateEntry>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-177280571