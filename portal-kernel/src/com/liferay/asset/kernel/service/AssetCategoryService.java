/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryDisplay;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for AssetCategory. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AssetCategoryServiceUtil
 * @generated
 */
@AccessControlled
@CTAware
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AssetCategoryService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetCategoryServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the asset category remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AssetCategoryServiceUtil} if injection and service tracking are not available.
	 */
	public AssetCategory addCategory(
			long groupId, long parentCategoryId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, long vocabularyId,
			String[] categoryProperties, ServiceContext serviceContext)
		throws PortalException;

	public AssetCategory addCategory(
			long groupId, String title, long vocabularyId,
			ServiceContext serviceContext)
		throws PortalException;

	public AssetCategory addCategory(
			String externalReferenceCode, long groupId, long parentCategoryId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			long vocabularyId, String[] categoryProperties,
			ServiceContext serviceContext)
		throws PortalException;

	public void deleteCategories(long[] categoryIds) throws PortalException;

	public void deleteCategory(long categoryId) throws PortalException;

	public AssetCategory deleteCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory fetchCategory(long categoryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory fetchCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory getAssetCategoryByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException;

	/**
	 * Returns a range of assetCategories related to an AssetEntry with the
	 * given "classNameId-classPK".
	 *
	 * @param classNameId the className of the asset
	 * @param classPK the classPK of the asset
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the matching assetCategories
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getCategories(
		long classNameId, long classPK, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getCategories(String className, long classPK)
		throws PortalException;

	/**
	 * Returns the number of assetCategories related to an AssetEntry with the
	 * given "classNameId-classPK".
	 *
	 * @param classNameId the className of the asset
	 * @param classPK the classPK of the asset
	 * @return the number of matching assetCategories
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCategoriesCount(long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory getCategory(long categoryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getCategoryPath(long categoryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getChildCategories(long parentCategoryId)
		throws PortalException;

	/**
	 * eturns a range of child assetCategories.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param orderByComparator the comparator
	 * @return the matching categories
	 * @throws PortalException
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getChildCategories(
			long parentCategoryId, int start, int end,
			OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException;

	/**
	 * Returns the number of child categories
	 *
	 * @param parentCategoryId the parent category ID
	 * @return the number of child categories
	 * @throws PortalException
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getChildCategoriesCount(long parentCategoryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategory getOrAddEmptyCategory(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getVocabularyCategories(
			long vocabularyId, int start, int end,
			OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getVocabularyCategories(
			long parentCategoryId, long vocabularyId, int start, int end,
			OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getVocabularyCategories(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end, OrderByComparator<AssetCategory> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getVocabularyCategories(
		long groupId, String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getVocabularyCategoriesCount(long groupId, long vocabularyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getVocabularyCategoriesCount(
		long groupId, long parentCategory, long vocabularyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getVocabularyCategoriesCount(
		long groupId, String name, long vocabularyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategoryDisplay getVocabularyCategoriesDisplay(
			long vocabularyId, int start, int end,
			OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategoryDisplay getVocabularyCategoriesDisplay(
			long groupId, String name, long vocabularyId, int start, int end,
			OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> getVocabularyRootCategories(
		long groupId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getVocabularyRootCategoriesCount(
		long groupId, long vocabularyId);

	public AssetCategory moveCategory(
			long categoryId, long parentCategoryId, long vocabularyId,
			ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetCategory> search(
		long groupId, String keywords, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JSONArray search(
			long groupId, String name, String[] categoryProperties, int start,
			int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JSONArray search(
			long[] groupIds, String name, long[] vocabularyIds, int start,
			int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategoryDisplay searchCategoriesDisplay(
			long groupId, String title, long vocabularyId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategoryDisplay searchCategoriesDisplay(
			long groupId, String title, long parentCategoryId,
			long vocabularyId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategoryDisplay searchCategoriesDisplay(
			long groupId, String title, long vocabularyId,
			long parentCategoryId, int start, int end, Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategoryDisplay searchCategoriesDisplay(
			long[] groupIds, String title, long[] vocabularyIds, int start,
			int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategoryDisplay searchCategoriesDisplay(
			long[] groupIds, String title, long[] parentCategoryIds,
			long[] vocabularyIds, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetCategoryDisplay searchCategoriesDisplay(
			long[] groupIds, String title, long[] vocabularyIds,
			long[] parentCategoryIds, int start, int end, Sort sort)
		throws PortalException;

	public AssetCategory updateCategory(
			long categoryId, long parentCategoryId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			long vocabularyId, String[] categoryProperties,
			ServiceContext serviceContext)
		throws PortalException;

}