/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryVersion;
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
import com.liferay.portal.kernel.service.version.VersionService;
import com.liferay.portal.kernel.service.version.VersionServiceListener;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for FragmentEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface FragmentEntryLocalService
	extends BaseLocalService, CTService<FragmentEntry>,
			PersistedModelLocalService,
			VersionService<FragmentEntry, FragmentEntryVersion> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.fragment.service.impl.FragmentEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the fragment entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link FragmentEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the fragment entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentEntry the fragment entry
	 * @return the fragment entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public FragmentEntry addFragmentEntry(FragmentEntry fragmentEntry);

	public FragmentEntry addFragmentEntry(
			String externalReferenceCode, long userId, long groupId,
			long fragmentCollectionId, String fragmentEntryKey, String name,
			String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean marketplace, boolean readOnly, int type, String typeOptions,
			int status, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public FragmentEntry checkout(
			FragmentEntry publishedFragmentEntry, int version)
		throws PortalException;

	public FragmentEntry copyFragmentEntry(
			long userId, long groupId, long sourceFragmentEntryId,
			long fragmentCollectionId, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new fragment entry. Does not add the fragment entry to the database.
	 *
	 * @return the new fragment entry
	 */
	@Override
	@Transactional(enabled = false)
	public FragmentEntry create();

	public FragmentEntry createFragmentEntry(long fragmentEntryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@Override
	public FragmentEntry delete(FragmentEntry publishedFragmentEntry)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@Override
	public FragmentEntry deleteDraft(FragmentEntry draftFragmentEntry)
		throws PortalException;

	/**
	 * Deletes the fragment entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentEntry the fragment entry
	 * @return the fragment entry that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public FragmentEntry deleteFragmentEntry(FragmentEntry fragmentEntry)
		throws PortalException;

	/**
	 * Deletes the fragment entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentEntryId the primary key of the fragment entry
	 * @return the fragment entry that was removed
	 * @throws PortalException if a fragment entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public FragmentEntry deleteFragmentEntry(long fragmentEntryId)
		throws PortalException;

	public FragmentEntry deleteFragmentEntry(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Override
	public FragmentEntryVersion deleteVersion(
			FragmentEntryVersion fragmentEntryVersion)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
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

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry fetchDraft(FragmentEntry fragmentEntry);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry fetchDraft(long primaryKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry fetchFragmentEntry(long fragmentEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry fetchFragmentEntry(
		long groupId, String fragmentEntryKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry fetchFragmentEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry fetchFragmentEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId, boolean head);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry fetchFragmentEntryByUuidAndGroupId(
		String uuid, long groupId);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntryVersion fetchLatestVersion(FragmentEntry fragmentEntry);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry fetchPublished(FragmentEntry fragmentEntry);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry fetchPublished(long primaryKey);

	public String generateFragmentEntryKey(long groupId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry getDraft(FragmentEntry fragmentEntry)
		throws PortalException;

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry getDraft(long primaryKey) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	/**
	 * Returns a range of all the fragment entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of fragment entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntries(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntries(long fragmentCollectionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntries(
		long fragmentCollectionId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, int status, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end, OrderByComparator<FragmentEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, String name, int start,
		int end, OrderByComparator<FragmentEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntriesByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator);

	/**
	 * Returns the number of fragment entries.
	 *
	 * @return the number of fragment entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentEntriesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentEntriesCount(long fragmentCollectionId);

	/**
	 * Returns the fragment entry with the primary key.
	 *
	 * @param fragmentEntryId the primary key of the fragment entry
	 * @return the fragment entry
	 * @throws PortalException if a fragment entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry getFragmentEntry(long fragmentEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry getFragmentEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry getFragmentEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId, boolean head)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry getFragmentEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

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
	public String[] getTempFileNames(
			long userId, long groupId, String folderName)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getUniqueFragmentEntryName(
		long groupId, long fragmentCollectionId, String name);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntryVersion getVersion(
			FragmentEntry fragmentEntry, int version)
		throws PortalException;

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryVersion> getVersions(FragmentEntry fragmentEntry);

	public FragmentEntry moveFragmentEntry(
			long fragmentEntryId, long fragmentCollectionId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public FragmentEntry publishDraft(FragmentEntry draftFragmentEntry)
		throws PortalException;

	@Override
	public void registerListener(
		VersionServiceListener<FragmentEntry, FragmentEntryVersion>
			versionServiceListener);

	@Override
	public void unregisterListener(
		VersionServiceListener<FragmentEntry, FragmentEntryVersion>
			versionServiceListener);

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public FragmentEntry updateDraft(FragmentEntry draftFragmentEntry)
		throws PortalException;

	/**
	 * Updates the fragment entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param draftFragmentEntry the fragment entry
	 * @return the fragment entry that was updated
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.REINDEX)
	public FragmentEntry updateFragmentEntry(FragmentEntry draftFragmentEntry)
		throws PortalException;

	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, boolean cacheable)
		throws PortalException;

	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, long previewFileEntryId)
		throws PortalException;

	public FragmentEntry updateFragmentEntry(
			long userId, long fragmentEntryId, long fragmentCollectionId,
			String name, String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean readOnly, String typeOptions, int status)
		throws PortalException;

	public FragmentEntry updateFragmentEntry(long fragmentEntryId, String name)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<FragmentEntry> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<FragmentEntry> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<FragmentEntry>, R, E>
				updateUnsafeFunction)
		throws E;

}