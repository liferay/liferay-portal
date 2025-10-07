/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
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
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
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
 * Provides the local service interface for AssetCategory. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetCategoryLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {"model.class.name=com.liferay.asset.kernel.model.AssetCategory"}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AssetCategoryLocalService
	extends BaseLocalService, CTService<AssetCategory>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetCategoryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the asset category local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AssetCategoryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the asset category to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetCategory the asset category
	 * @return the asset category that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetCategory addAssetCategory(AssetCategory assetCategory);

	public AssetCategory addCategory(
			long userId, long groupId, String title, long vocabularyId,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public AssetCategory addCategory(
			String externalReferenceCode, long userId, long groupId,
			long parentCategoryId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, long vocabularyId,
			String[] categoryProperties, ServiceContext serviceContext)
		throws PortalException;

	public void addCategoryResources(
			AssetCategory category, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	public void addCategoryResources(
			AssetCategory category, ModelPermissions modelPermissions)
		throws PortalException;

	/**
	 * Creates a new asset category with the primary key. Does not add the asset category to the database.
	 *
	 * @param categoryId the primary key for the new asset category
	 * @return the new asset category
	 */
	@Transactional(enabled = false)
	public AssetCategory createAssetCategory(long categoryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the asset category from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetCategory the asset category
	 * @return the asset category that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public AssetCategory deleteAssetCategory(AssetCategory assetCategory);

	/**
	 * Deletes the asset category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param categoryId the primary key of the asset category
	 * @return the asset category that was removed
	 * @throws PortalException if a asset category with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public AssetCategory deleteAssetCategory(long categoryId)
		throws PortalException;

	public void deleteCategories(List<AssetCategory> categories)
		throws PortalException;

	public void deleteCategories(long[] categoryIds) throws PortalException;

	public AssetCategory deleteCategory(AssetCategory category)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public AssetCategory deleteCategory(
			AssetCategory category, boolean skipRebuildTree)
		throws PortalException;

	public AssetCategory deleteCategory(long categoryId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteVocabularyCategories(long vocabularyId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
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
	public AssetCategory fetchAssetCategory(long categoryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory fetchAssetCategoryByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the asset category matching the UUID and group.
	 *
	 * @param uuid the asset category's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory fetchAssetCategoryByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory fetchCategory(long categoryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory fetchCategory(
		long groupId, long parentCategoryId, String name, long vocabularyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns a range of all the asset categories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of asset categories
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getAssetCategories(int start, int end);

	/**
	 * Returns all the asset categories matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset categories
	 * @param companyId the primary key of the company
	 * @return the matching asset categories, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getAssetCategoriesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of asset categories matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset categories
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset categories, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getAssetCategoriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator);

	/**
	 * Returns the number of asset categories.
	 *
	 * @return the number of asset categories
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetCategoriesCount();

	/**
	 * Returns the asset category with the primary key.
	 *
	 * @param categoryId the primary key of the asset category
	 * @return the asset category
	 * @throws PortalException if a asset category with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory getAssetCategory(long categoryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory getAssetCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the asset category matching the UUID and group.
	 *
	 * @param uuid the asset category's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset category
	 * @throws PortalException if a matching asset category could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory getAssetCategoryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getCategories();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getCategories(Hits hits) throws PortalException;

	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getCategories(long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getCategories(
		long classNameId, long classPK, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getCategories(String className, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCategoriesCount(long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory getCategory(long categoryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory getCategory(String uuid, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getCategoryIds(String className, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getCategoryNames();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getCategoryNames(long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getCategoryNames(String className, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getChildCategories(long parentCategoryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getChildCategories(
		long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getChildCategoriesCount(long parentCategoryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getDescendantCategories(AssetCategory category);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getEntryCategories(long entryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory getOrAddEmptyCategory(
			String externalReferenceCode, long userId, long groupId)
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
	public List<Long> getSubcategoryIds(long parentCategoryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getViewableCategoryIds(
			String className, long classPK, long[] categoryIds)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getVocabularyCategories(
		long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getVocabularyCategories(
		long parentCategoryId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getVocabularyCategoriesCount(long vocabularyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getVocabularyRootCategories(
		long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getVocabularyRootCategoriesCount(long vocabularyId);

	@Indexable(type = IndexableType.REINDEX)
	public AssetCategory mergeCategories(long fromCategoryId, long toCategoryId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public AssetCategory moveCategory(
			long categoryId, long parentCategoryId, long vocabularyId,
			ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> search(
		long groupId, String name, String[] categoryProperties, int start,
		int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<AssetCategory> searchCategories(
			long companyId, long groupIds, String title, long vocabularyId,
			int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<AssetCategory> searchCategories(
			long companyId, long[] groupIds, String title, long[] vocabularyIds,
			int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<AssetCategory> searchCategories(
			long companyId, long[] groupIds, String title,
			long[] parentCategoryIds, long[] vocabularyIds, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<AssetCategory> searchCategories(
			long companyId, long[] groupIds, String title, long[] vocabularyIds,
			long[] parentCategoryIds, int start, int end, Sort sort)
		throws PortalException;

	/**
	 * Updates the asset category in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetCategory the asset category
	 * @return the asset category that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetCategory updateAssetCategory(AssetCategory assetCategory);

	@Indexable(type = IndexableType.REINDEX)
	public AssetCategory updateCategory(
			long userId, long categoryId, long parentCategoryId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			long vocabularyId, String[] categoryProperties,
			ServiceContext serviceContext)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<AssetCategory> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<AssetCategory> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetCategory>, R, E>
				updateUnsafeFunction)
		throws E;

}