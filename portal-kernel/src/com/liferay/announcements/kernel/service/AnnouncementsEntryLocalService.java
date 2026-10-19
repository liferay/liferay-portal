/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.kernel.service;

import com.liferay.announcements.kernel.model.AnnouncementsEntry;
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
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for AnnouncementsEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see AnnouncementsEntryLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.announcements.kernel.model.AnnouncementsEntry"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AnnouncementsEntryLocalService
	extends BaseLocalService, CTService<AnnouncementsEntry>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.announcements.service.impl.AnnouncementsEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the announcements entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AnnouncementsEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the announcements entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnnouncementsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param announcementsEntry the announcements entry
	 * @return the announcements entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AnnouncementsEntry addAnnouncementsEntry(
		AnnouncementsEntry announcementsEntry);

	public AnnouncementsEntry addEntry(
			long userId, long classNameId, long classPK, String title,
			String content, String url, String type, Date displayDate,
			Date expirationDate, int priority, boolean alert)
		throws PortalException;

	public void checkEntries(Date startDate, Date endDate)
		throws PortalException;

	/**
	 * Creates a new announcements entry with the primary key. Does not add the announcements entry to the database.
	 *
	 * @param entryId the primary key for the new announcements entry
	 * @return the new announcements entry
	 */
	@Transactional(enabled = false)
	public AnnouncementsEntry createAnnouncementsEntry(long entryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the announcements entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnnouncementsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param announcementsEntry the announcements entry
	 * @return the announcements entry that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public AnnouncementsEntry deleteAnnouncementsEntry(
		AnnouncementsEntry announcementsEntry);

	/**
	 * Deletes the announcements entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnnouncementsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entryId the primary key of the announcements entry
	 * @return the announcements entry that was removed
	 * @throws PortalException if a announcements entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public AnnouncementsEntry deleteAnnouncementsEntry(long entryId)
		throws PortalException;

	public void deleteEntries(long companyId);

	public void deleteEntries(long classNameId, long classPK)
		throws PortalException;

	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteEntry(AnnouncementsEntry entry) throws PortalException;

	public void deleteEntry(long entryId) throws PortalException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.announcements.model.impl.AnnouncementsEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.announcements.model.impl.AnnouncementsEntryModelImpl</code>.
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
	public AnnouncementsEntry fetchAnnouncementsEntry(long entryId);

	/**
	 * Returns the announcements entry with the matching UUID and company.
	 *
	 * @param uuid the announcements entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching announcements entry, or <code>null</code> if a matching announcements entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AnnouncementsEntry fetchAnnouncementsEntryByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns a range of all the announcements entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.announcements.model.impl.AnnouncementsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of announcements entries
	 * @param end the upper bound of the range of announcements entries (not inclusive)
	 * @return the range of announcements entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AnnouncementsEntry> getAnnouncementsEntries(int start, int end);

	/**
	 * Returns the number of announcements entries.
	 *
	 * @return the number of announcements entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAnnouncementsEntriesCount();

	/**
	 * Returns the announcements entry with the primary key.
	 *
	 * @param entryId the primary key of the announcements entry
	 * @return the announcements entry
	 * @throws PortalException if a announcements entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AnnouncementsEntry getAnnouncementsEntry(long entryId)
		throws PortalException;

	/**
	 * Returns the announcements entry with the matching UUID and company.
	 *
	 * @param uuid the announcements entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching announcements entry
	 * @throws PortalException if a matching announcements entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AnnouncementsEntry getAnnouncementsEntryByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AnnouncementsEntry> getEntries(
		long userId, LinkedHashMap<Long, long[]> scopes, boolean alert,
		int flagValue, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AnnouncementsEntry> getEntries(
		long userId, LinkedHashMap<Long, long[]> scopes, int displayDateMonth,
		int displayDateDay, int displayDateYear, int displayDateHour,
		int displayDateMinute, int expirationDateMonth, int expirationDateDay,
		int expirationDateYear, int expirationDateHour,
		int expirationDateMinute, boolean alert, int flagValue, int start,
		int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AnnouncementsEntry> getEntries(
		long companyId, long classNameId, long classPK, boolean alert,
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AnnouncementsEntry> getEntries(
		long companyId, long classNameId, long classPK, boolean alert,
		int start, int end,
		OrderByComparator<AnnouncementsEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getEntriesCount(
		long userId, LinkedHashMap<Long, long[]> scopes, boolean alert,
		int flagValue);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getEntriesCount(
		long userId, LinkedHashMap<Long, long[]> scopes, int displayDateMonth,
		int displayDateDay, int displayDateYear, int displayDateHour,
		int displayDateMinute, int expirationDateMonth, int expirationDateDay,
		int expirationDateYear, int expirationDateHour,
		int expirationDateMinute, boolean alert, int flagValue);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getEntriesCount(
		long companyId, long classNameId, long classPK, boolean alert);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AnnouncementsEntry getEntry(long entryId) throws PortalException;

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
	public List<AnnouncementsEntry> getUserEntries(
		long userId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserEntriesCount(long userId);

	/**
	 * Updates the announcements entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnnouncementsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param announcementsEntry the announcements entry
	 * @return the announcements entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AnnouncementsEntry updateAnnouncementsEntry(
		AnnouncementsEntry announcementsEntry);

	public AnnouncementsEntry updateEntry(
			long entryId, String title, String content, String url, String type,
			Date displayDate, Date expirationDate, int priority)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<AnnouncementsEntry> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<AnnouncementsEntry> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AnnouncementsEntry>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:69301426