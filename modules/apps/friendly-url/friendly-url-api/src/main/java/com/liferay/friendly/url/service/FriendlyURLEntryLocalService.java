/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.friendly.url.exception.NoSuchFriendlyURLEntryLocalizationException;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
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
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for FriendlyURLEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see FriendlyURLEntryLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface FriendlyURLEntryLocalService
	extends BaseLocalService, CTService<FriendlyURLEntry>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.friendly.url.service.impl.FriendlyURLEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the friendly url entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link FriendlyURLEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the friendly url entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FriendlyURLEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param friendlyURLEntry the friendly url entry
	 * @return the friendly url entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public FriendlyURLEntry addFriendlyURLEntry(
		FriendlyURLEntry friendlyURLEntry);

	public FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long parentClassPK, long classPK,
			String defaultLanguageId, Map<String, String> urlTitleMap,
			ServiceContext serviceContext)
		throws PortalException;

	public FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long classPK,
			Map<String, String> urlTitleMap, ServiceContext serviceContext)
		throws PortalException;

	public FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long classPK,
			String defaultLanguageId, Map<String, String> urlTitleMap,
			ServiceContext serviceContext)
		throws PortalException;

	public FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long classPK, String urlTitle,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new friendly url entry with the primary key. Does not add the friendly url entry to the database.
	 *
	 * @param friendlyURLEntryId the primary key for the new friendly url entry
	 * @return the new friendly url entry
	 */
	@Transactional(enabled = false)
	public FriendlyURLEntry createFriendlyURLEntry(long friendlyURLEntryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteCompanyFriendlyURLEntries(
		long companyId, long classNameId);

	/**
	 * Deletes the friendly url entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FriendlyURLEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param friendlyURLEntry the friendly url entry
	 * @return the friendly url entry that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public FriendlyURLEntry deleteFriendlyURLEntry(
		FriendlyURLEntry friendlyURLEntry);

	/**
	 * Deletes the friendly url entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FriendlyURLEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param friendlyURLEntryId the primary key of the friendly url entry
	 * @return the friendly url entry that was removed
	 * @throws PortalException if a friendly url entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public FriendlyURLEntry deleteFriendlyURLEntry(long friendlyURLEntryId)
		throws PortalException;

	public void deleteFriendlyURLEntry(
		long groupId, long classNameId, long classPK);

	public void deleteFriendlyURLLocalizationEntry(
			FriendlyURLEntryLocalization friendlyURLEntryLocalization)
		throws PortalException;

	public void deleteFriendlyURLLocalizationEntry(
			long friendlyURLEntryId, String languageId)
		throws PortalException;

	public void deleteGroupFriendlyURLEntries(long groupId, long classNameId);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryModelImpl</code>.
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
	public FriendlyURLEntry fetchFriendlyURLEntry(long friendlyURLEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FriendlyURLEntry fetchFriendlyURLEntry(
		long groupId, long classNameId, long parentClassPK, String urlTitle);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FriendlyURLEntry fetchFriendlyURLEntry(
		long groupId, long classNameId, String urlTitle);

	/**
	 * Returns the friendly url entry matching the UUID and group.
	 *
	 * @param uuid the friendly url entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching friendly url entry, or <code>null</code> if a matching friendly url entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FriendlyURLEntry fetchFriendlyURLEntryByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FriendlyURLEntryLocalization fetchFriendlyURLEntryLocalization(
		long groupId, long classNameId, long parentClassPK, String urlTitle);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FriendlyURLEntryLocalization fetchFriendlyURLEntryLocalization(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FriendlyURLEntryLocalization fetchFriendlyURLEntryLocalization(
		long friendlyURLEntryId, String languageId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FriendlyURLEntry fetchMainFriendlyURLEntry(
		long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	/**
	 * Returns a range of all the friendly url entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of friendly url entries
	 * @param end the upper bound of the range of friendly url entries (not inclusive)
	 * @return the range of friendly url entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FriendlyURLEntry> getFriendlyURLEntries(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FriendlyURLEntry> getFriendlyURLEntries(
		long groupId, long classNameId, long classPK);

	/**
	 * Returns all the friendly url entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the friendly url entries
	 * @param companyId the primary key of the company
	 * @return the matching friendly url entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FriendlyURLEntry> getFriendlyURLEntriesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of friendly url entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the friendly url entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of friendly url entries
	 * @param end the upper bound of the range of friendly url entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching friendly url entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FriendlyURLEntry> getFriendlyURLEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FriendlyURLEntry> orderByComparator);

	/**
	 * Returns the number of friendly url entries.
	 *
	 * @return the number of friendly url entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFriendlyURLEntriesCount();

	/**
	 * Returns the friendly url entry with the primary key.
	 *
	 * @param friendlyURLEntryId the primary key of the friendly url entry
	 * @return the friendly url entry
	 * @throws PortalException if a friendly url entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FriendlyURLEntry getFriendlyURLEntry(long friendlyURLEntryId)
		throws PortalException;

	/**
	 * Returns the friendly url entry matching the UUID and group.
	 *
	 * @param uuid the friendly url entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching friendly url entry
	 * @throws PortalException if a matching friendly url entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FriendlyURLEntry getFriendlyURLEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FriendlyURLEntryLocalization getFriendlyURLEntryLocalization(
			long groupId, long classNameId, String urlTitle)
		throws NoSuchFriendlyURLEntryLocalizationException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FriendlyURLEntryLocalization getFriendlyURLEntryLocalization(
			long groupId, long classNameId, String languageId, String urlTitle)
		throws NoSuchFriendlyURLEntryLocalizationException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FriendlyURLEntryLocalization getFriendlyURLEntryLocalization(
			long friendlyURLEntryId, String languageId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FriendlyURLEntryLocalization> getFriendlyURLEntryLocalizations(
		long friendlyURLEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FriendlyURLEntryLocalization> getFriendlyURLEntryLocalizations(
		long groupId, long classNameId, long classPK, String languageId,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FriendlyURLEntry getMainFriendlyURLEntry(
			long classNameId, long classPK)
		throws PortalException;

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
	public String getUniqueUrlTitle(
		long groupId, long classNameId, long parentClassPK, long classPK,
		String urlTitle, String languageId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getUniqueUrlTitle(
		long groupId, long classNameId, long classPK, String urlTitle,
		String languageId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<String, String> getUniqueUrlTitleMap(
		long groupId, long classNameId, long parentClassPK, long classPK,
		Map<Locale, String> titleMap);

	public void setMainFriendlyURLEntry(FriendlyURLEntry friendlyURLEntry);

	/**
	 * Updates the friendly url entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FriendlyURLEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param friendlyURLEntry the friendly url entry
	 * @return the friendly url entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public FriendlyURLEntry updateFriendlyURLEntry(
		FriendlyURLEntry friendlyURLEntry);

	public FriendlyURLEntry updateFriendlyURLEntry(
			long friendlyURLEntryId, long classNameId, long parentClassPK,
			long classPK, String defaultLanguageId,
			Map<String, String> urlTitleMap, ServiceContext serviceContext)
		throws PortalException;

	public FriendlyURLEntry updateFriendlyURLEntry(
			long friendlyURLEntryId, long classNameId, long classPK,
			String defaultLanguageId, Map<String, String> urlTitleMap)
		throws PortalException;

	public FriendlyURLEntry updateFriendlyURLEntry(
			long friendlyURLEntryId, long classNameId, long classPK,
			String defaultLanguageId, Map<String, String> urlTitleMap,
			ServiceContext serviceContext)
		throws PortalException;

	public FriendlyURLEntryLocalization updateFriendlyURLEntryLocalization(
			FriendlyURLEntry friendlyURLEntry, String languageId,
			String urlTitle)
		throws PortalException;

	public List<FriendlyURLEntryLocalization>
			updateFriendlyURLEntryLocalizations(
				FriendlyURLEntry friendlyURLEntry,
				Map<String, String> urlTitleMap)
		throws PortalException;

	public FriendlyURLEntryLocalization updateFriendlyURLLocalization(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization);

	public FriendlyURLEntryLocalization updateFriendlyURLLocalization(
			long friendlyURLLocalizationId, String urlTitle)
		throws PortalException;

	public void validate(
			long groupId, long classNameId, long parentClassPK, long classPK,
			Map<String, String> urlTitleMap)
		throws PortalException;

	public void validate(
			long groupId, long classNameId, long parentClassPK, long classPK,
			String urlTitle)
		throws PortalException;

	public void validate(
			long groupId, long classNameId, long parentClassPK, long classPK,
			String languageId, String urlTitle)
		throws PortalException;

	public void validate(
			long groupId, long classNameId, long classPK,
			Map<String, String> urlTitleMap)
		throws PortalException;

	public void validate(
			long groupId, long classNameId, long classPK, String urlTitle)
		throws PortalException;

	public void validate(
			long groupId, long classNameId, long classPK, String languageId,
			String urlTitle)
		throws PortalException;

	public void validate(long groupId, long classNameId, String urlTitle)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<FriendlyURLEntry> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<FriendlyURLEntry> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<FriendlyURLEntry>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1021384353