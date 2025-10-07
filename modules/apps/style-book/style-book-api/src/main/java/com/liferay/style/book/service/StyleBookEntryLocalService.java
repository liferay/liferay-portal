/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service;

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
import com.liferay.portal.kernel.service.version.VersionService;
import com.liferay.portal.kernel.service.version.VersionServiceListener;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.model.StyleBookEntryVersion;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for StyleBookEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookEntryLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface StyleBookEntryLocalService
	extends BaseLocalService, CTService<StyleBookEntry>,
			PersistedModelLocalService,
			VersionService<StyleBookEntry, StyleBookEntryVersion> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.style.book.service.impl.StyleBookEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the style book entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link StyleBookEntryLocalServiceUtil} if injection and service tracking are not available.
	 */
	public StyleBookEntry addStyleBookEntry(
			String externalReferenceCode, long userId, long groupId,
			boolean defaultStyleBookEntry, String frontendTokensValues,
			String name, String styleBookEntryKey, String themeId,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds the style book entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect StyleBookEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param styleBookEntry the style book entry
	 * @return the style book entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public StyleBookEntry addStyleBookEntry(StyleBookEntry styleBookEntry);

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public StyleBookEntry checkout(
			StyleBookEntry publishedStyleBookEntry, int version)
		throws PortalException;

	public StyleBookEntry copyStyleBookEntry(
			long userId, long groupId, long sourceStyleBookEntryId,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new style book entry. Does not add the style book entry to the database.
	 *
	 * @return the new style book entry
	 */
	@Override
	@Transactional(enabled = false)
	public StyleBookEntry create();

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@Override
	public StyleBookEntry delete(StyleBookEntry publishedStyleBookEntry)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@Override
	public StyleBookEntry deleteDraft(StyleBookEntry draftStyleBookEntry)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteStyleBookEntries(long groupId) throws PortalException;

	/**
	 * Deletes the style book entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect StyleBookEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param styleBookEntryId the primary key of the style book entry
	 * @return the style book entry that was removed
	 * @throws PortalException if a style book entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public StyleBookEntry deleteStyleBookEntry(long styleBookEntryId)
		throws PortalException;

	public StyleBookEntry deleteStyleBookEntry(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Deletes the style book entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect StyleBookEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param styleBookEntry the style book entry
	 * @return the style book entry that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	public StyleBookEntry deleteStyleBookEntry(StyleBookEntry styleBookEntry)
		throws PortalException;

	@Override
	public StyleBookEntryVersion deleteVersion(
			StyleBookEntryVersion styleBookEntryVersion)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryModelImpl</code>.
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
	public StyleBookEntry fetchDefaultStyleBookEntry(
		long groupId, String themeId);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntry fetchDraft(long primaryKey);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntry fetchDraft(StyleBookEntry styleBookEntry);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntryVersion fetchLatestVersion(
		StyleBookEntry styleBookEntry);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntry fetchPublished(long primaryKey);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntry fetchPublished(StyleBookEntry styleBookEntry);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntry fetchStyleBookEntry(long styleBookEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntry fetchStyleBookEntry(
		long groupId, String styleBookEntryKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntry fetchStyleBookEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntry fetchStyleBookEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId, boolean head);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntry fetchStyleBookEntryByUuidAndGroupId(
		String uuid, long groupId);

	public String generateStyleBookEntryKey(long groupId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntry getDraft(long primaryKey) throws PortalException;

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntry getDraft(StyleBookEntry styleBookEntry)
		throws PortalException;

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
	 * Returns a range of all the style book entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @return the range of style book entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<StyleBookEntry> getStyleBookEntries(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<StyleBookEntry> getStyleBookEntries(
		long groupId, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<StyleBookEntry> getStyleBookEntries(
		long groupId, String themeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<StyleBookEntry> getStyleBookEntries(
		long groupId, String name, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<StyleBookEntry> getStyleBookEntriesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns the number of style book entries.
	 *
	 * @return the number of style book entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getStyleBookEntriesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getStyleBookEntriesCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getStyleBookEntriesCount(long groupId, String name);

	/**
	 * Returns the style book entry with the primary key.
	 *
	 * @param styleBookEntryId the primary key of the style book entry
	 * @return the style book entry
	 * @throws PortalException if a style book entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntry getStyleBookEntry(long styleBookEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntry getStyleBookEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntry getStyleBookEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId, boolean head)
		throws PortalException;

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public StyleBookEntryVersion getVersion(
			StyleBookEntry styleBookEntry, int version)
		throws PortalException;

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<StyleBookEntryVersion> getVersions(
		StyleBookEntry styleBookEntry);

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public StyleBookEntry publishDraft(StyleBookEntry draftStyleBookEntry)
		throws PortalException;

	@Override
	public void registerListener(
		VersionServiceListener<StyleBookEntry, StyleBookEntryVersion>
			versionServiceListener);

	@Override
	public void unregisterListener(
		VersionServiceListener<StyleBookEntry, StyleBookEntryVersion>
			versionServiceListener);

	public StyleBookEntry updateDefaultStyleBookEntry(
			long styleBookEntryId, boolean defaultStyleBookEntry)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public StyleBookEntry updateDraft(StyleBookEntry draftStyleBookEntry)
		throws PortalException;

	public StyleBookEntry updateFrontendTokensValues(
			long styleBookEntryId, String frontendTokensValues)
		throws PortalException;

	public StyleBookEntry updateName(long styleBookEntryId, String name)
		throws PortalException;

	public StyleBookEntry updatePreviewFileEntryId(
			long styleBookEntryId, long previewFileEntryId)
		throws PortalException;

	public StyleBookEntry updateStyleBookEntry(
			long userId, long styleBookEntryId, boolean defaultStylebookEntry,
			String frontendTokensValues, String name, String styleBookEntryKey,
			long previewFileEntryId)
		throws PortalException;

	public StyleBookEntry updateStyleBookEntry(
			long styleBookEntryId, String frontendTokensValues, String name)
		throws PortalException;

	/**
	 * Updates the style book entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect StyleBookEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param draftStyleBookEntry the style book entry
	 * @return the style book entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public StyleBookEntry updateStyleBookEntry(
			StyleBookEntry draftStyleBookEntry)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<StyleBookEntry> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<StyleBookEntry> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<StyleBookEntry>, R, E>
				updateUnsafeFunction)
		throws E;

}