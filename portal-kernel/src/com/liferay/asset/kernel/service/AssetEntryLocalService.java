/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
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
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for AssetEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetEntryLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {"model.class.name=com.liferay.asset.kernel.model.AssetEntry"}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AssetEntryLocalService
	extends BaseLocalService, CTService<AssetEntry>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the asset entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AssetEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the asset entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetEntry the asset entry
	 * @return the asset entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetEntry addAssetEntry(AssetEntry assetEntry);

	public boolean addAssetTagAssetEntries(
		long tagId, List<AssetEntry> assetEntries);

	public boolean addAssetTagAssetEntries(long tagId, long[] entryIds);

	public boolean addAssetTagAssetEntry(long tagId, AssetEntry assetEntry);

	public boolean addAssetTagAssetEntry(long tagId, long entryId);

	public void clearAssetTagAssetEntries(long tagId);

	/**
	 * Creates a new asset entry with the primary key. Does not add the asset entry to the database.
	 *
	 * @param entryId the primary key for the new asset entry
	 * @return the new asset entry
	 */
	@Transactional(enabled = false)
	public AssetEntry createAssetEntry(long entryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the asset entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetEntry the asset entry
	 * @return the asset entry that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public AssetEntry deleteAssetEntry(AssetEntry assetEntry);

	/**
	 * Deletes the asset entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset entry that was removed
	 * @throws PortalException if a asset entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public AssetEntry deleteAssetEntry(long entryId) throws PortalException;

	public void deleteAssetTagAssetEntries(
		long tagId, List<AssetEntry> assetEntries);

	public void deleteAssetTagAssetEntries(long tagId, long[] entryIds);

	public void deleteAssetTagAssetEntry(long tagId, AssetEntry assetEntry);

	public void deleteAssetTagAssetEntry(long tagId, long entryId);

	public void deleteEntries(long companyId, String className)
		throws PortalException;

	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public AssetEntry deleteEntry(AssetEntry entry) throws PortalException;

	public AssetEntry deleteEntry(long entryId) throws PortalException;

	public AssetEntry deleteEntry(String className, long classPK)
		throws PortalException;

	public void deleteGroupEntries(long groupId) throws PortalException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
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
	public AssetEntry fetchAssetEntry(long entryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetEntry fetchEntry(long entryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetEntry fetchEntry(long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetEntry fetchEntry(long groupId, String classUuid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetEntry fetchEntry(String className, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns a range of all the asset entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @return the range of asset entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetEntry> getAssetEntries(int start, int end);

	/**
	 * Returns the number of asset entries.
	 *
	 * @return the number of asset entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetEntriesCount();

	/**
	 * Returns the asset entry with the primary key.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset entry
	 * @throws PortalException if a asset entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetEntry getAssetEntry(long entryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetEntry> getAssetTagAssetEntries(long tagId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetEntry> getAssetTagAssetEntries(
		long tagId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetEntry> getAssetTagAssetEntries(
		long tagId, int start, int end,
		OrderByComparator<AssetEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetTagAssetEntriesCount(long tagId);

	/**
	 * Returns the tagIds of the asset tags associated with the asset entry.
	 *
	 * @param entryId the entryId of the asset entry
	 * @return long[] the tagIds of asset tags associated with the asset entry
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getAssetTagPrimaryKeys(long entryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetEntry> getCompanyEntries(
		long companyId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCompanyEntriesCount(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetEntry> getEntries(AssetEntryQuery entryQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getEntriesCount(AssetEntryQuery entryQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetEntry getEntry(long entryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetEntry getEntry(long groupId, String classUuid)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetEntry getEntry(String className, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public double getEntryPriority(long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public double getEntryPriority(String className, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetEntry> getGroupEntries(long groupId);

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
	public boolean hasAssetTagAssetEntries(long tagId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasAssetTagAssetEntry(long tagId, long entryId);

	public void incrementViewCounter(long userId, AssetEntry assetEntry)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetEntry incrementViewCounter(
			long companyId, long userId, String className, long classPK)
		throws PortalException;

	@Transactional(enabled = false)
	public void incrementViewCounter(
		long companyId, long userId, String className, long classPK,
		int increment);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void reindex(List<AssetEntry> entries) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long[] groupIds, long userId, long[] classNameIds,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses, int start, int end, Sort sort);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long[] groupIds, long userId, long[] classNameIds,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses, int start, int end, Sort[] sorts);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, boolean showNonindexable, int status,
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses, int start, int end, Sort sort);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses, int start, int end, Sort[] sorts);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, boolean showNonindexable,
		int status, boolean andSearch, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, boolean showNonindexable,
		int[] statuses, boolean andSearch, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, int status,
		boolean andSearch, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		String keywords, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, int status,
		boolean andSearch, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long searchCount(
		long companyId, long[] groupIds, long userId, long[] classNameIds,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long searchCount(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long searchCount(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, String assetCategoryIds,
		String assetTagNames, boolean showInvisible, boolean showNonindexable,
		int[] statuses, boolean andSearch);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long searchCount(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, boolean showInvisible,
		boolean showNonindexable, int[] statuses, boolean andSearch);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long searchCount(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, boolean showNonindexable,
		int[] statuses, boolean andSearch);

	public void setAssetTagAssetEntries(long tagId, long[] entryIds);

	/**
	 * Updates the asset entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetEntry the asset entry
	 * @return the asset entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetEntry updateAssetEntry(AssetEntry assetEntry);

	public AssetEntry updateEntry(
			long userId, long groupId, Date createDate, Date modifiedDate,
			String className, long classPK, String classUuid, long classTypeId,
			long[] categoryIds, String[] tagNames, boolean listable,
			boolean visible, Date startDate, Date endDate, Date publishDate,
			Date expirationDate, String mimeType, String title,
			String description, String summary, String url, String layoutUuid,
			int height, int width, Double priority)
		throws PortalException;

	public AssetEntry updateEntry(
			long userId, long groupId, Date createDate, Date modifiedDate,
			String className, long classPK, String classUuid, long classTypeId,
			long[] categoryIds, String[] tagNames, boolean listable,
			boolean visible, Date startDate, Date endDate, Date publishDate,
			Date expirationDate, String mimeType, String title,
			String description, String summary, String url, String layoutUuid,
			int height, int width, Double priority,
			ServiceContext serviceContext)
		throws PortalException;

	public AssetEntry updateEntry(
			long userId, long groupId, String className, long classPK,
			long[] categoryIds, String[] tagNames)
		throws PortalException;

	public AssetEntry updateEntry(
			String className, long classPK, Date publishDate,
			Date expirationDate, boolean listable, boolean visible)
		throws PortalException;

	public AssetEntry updateVisible(AssetEntry entry, boolean visible)
		throws PortalException;

	public AssetEntry updateVisible(
			String className, long classPK, boolean visible)
		throws PortalException;

	public void validate(
			long groupId, String className, long classPK, long classTypePK,
			long[] categoryIds, String[] tagNames)
		throws PortalException;

	public void validate(
			long groupId, String className, long classTypePK,
			long[] categoryIds, String[] tagNames)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<AssetEntry> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<AssetEntry> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetEntry>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1450929286