/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.message.boards.model.MBCategory;
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
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for MBCategory. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see MBCategoryLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface MBCategoryLocalService
	extends BaseLocalService, CTService<MBCategory>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.message.boards.service.impl.MBCategoryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the message boards category local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link MBCategoryLocalServiceUtil} if injection and service tracking are not available.
	 */
	public MBCategory addCategory(
			String externalReferenceCode, long userId, long parentCategoryId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException;

	public MBCategory addCategory(
			String externalReferenceCode, long userId, long parentCategoryId,
			String name, String description, String displayStyle,
			String emailAddress, String inProtocol, String inServerName,
			int inServerPort, boolean inUseSSL, String inUserName,
			String inPassword, int inReadInterval, String outEmailAddress,
			boolean outCustom, String outServerName, int outServerPort,
			boolean outUseSSL, String outUserName, String outPassword,
			boolean allowAnonymous, boolean mailingListActive,
			ServiceContext serviceContext)
		throws PortalException;

	public void addCategoryResources(
			long categoryId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	public void addCategoryResources(
			long categoryId, ModelPermissions modelPermissions)
		throws PortalException;

	public void addCategoryResources(
			MBCategory category, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	public void addCategoryResources(
			MBCategory category, ModelPermissions modelPermissions)
		throws PortalException;

	/**
	 * Adds the message boards category to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbCategory the message boards category
	 * @return the message boards category that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public MBCategory addMBCategory(MBCategory mbCategory);

	/**
	 * Creates a new message boards category with the primary key. Does not add the message boards category to the database.
	 *
	 * @param categoryId the primary key for the new message boards category
	 * @return the new message boards category
	 */
	@Transactional(enabled = false)
	public MBCategory createMBCategory(long categoryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteCategories(long groupId) throws PortalException;

	public void deleteCategory(long categoryId) throws PortalException;

	public void deleteCategory(MBCategory category) throws PortalException;

	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public void deleteCategory(
			MBCategory category, boolean includeTrashedEntries)
		throws PortalException;

	public void deleteCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Deletes the message boards category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category that was removed
	 * @throws PortalException if a message boards category with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public MBCategory deleteMBCategory(long categoryId) throws PortalException;

	/**
	 * Deletes the message boards category from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbCategory the message boards category
	 * @return the message boards category that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public MBCategory deleteMBCategory(MBCategory mbCategory);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
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
	public MBCategory fetchMBCategory(long categoryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBCategory fetchMBCategory(long groupId, String friendlyURL);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBCategory fetchMBCategoryByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the message boards category matching the UUID and group.
	 *
	 * @param uuid the message boards category's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBCategory fetchMBCategoryByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBCategory> getCategories(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBCategory> getCategories(long groupId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBCategory> getCategories(
		long groupId, long parentCategoryId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBCategory> getCategories(
		long groupId, long parentCategoryId, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBCategory> getCategories(
		long groupId, long excludedCategoryId, long parentCategoryId,
		int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBCategory> getCategories(
		long groupId, long[] parentCategoryIds, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBCategory> getCategories(
		long groupId, long[] parentCategoryIds, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBCategory> getCategories(
		long groupId, long[] excludedCategoryIds, long[] parentCategoryIds,
		int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getCategoriesAndThreads(long groupId, long categoryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getCategoriesAndThreads(
		long groupId, long categoryId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getCategoriesAndThreads(
		long groupId, long categoryId, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCategoriesAndThreadsCount(
		long groupId, long categoryId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCategoriesCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCategoriesCount(long groupId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCategoriesCount(long groupId, long parentCategoryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCategoriesCount(
		long groupId, long parentCategoryId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCategoriesCount(
		long groupId, long excludedCategoryId, long parentCategoryId,
		int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCategoriesCount(long groupId, long[] parentCategoryIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCategoriesCount(
		long groupId, long[] parentCategoryIds, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCategoriesCount(
		long groupId, long[] excludedCategoryIds, long[] parentCategoryIds,
		int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBCategory getCategory(long categoryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns a range of all the message boards categories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of message boards categories
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBCategory> getMBCategories(int start, int end);

	/**
	 * Returns all the message boards categories matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards categories
	 * @param companyId the primary key of the company
	 * @return the matching message boards categories, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBCategory> getMBCategoriesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of message boards categories matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards categories
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching message boards categories, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBCategory> getMBCategoriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator);

	/**
	 * Returns the number of message boards categories.
	 *
	 * @return the number of message boards categories
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getMBCategoriesCount();

	/**
	 * Returns the message boards category with the primary key.
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category
	 * @throws PortalException if a message boards category with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBCategory getMBCategory(long categoryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBCategory getMBCategory(long groupId, String friendlyURL)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBCategory getMBCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the message boards category matching the UUID and group.
	 *
	 * @param uuid the message boards category's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards category
	 * @throws PortalException if a matching message boards category could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBCategory getMBCategoryByUuidAndGroupId(String uuid, long groupId)
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
	public List<Long> getSubcategoryIds(
		List<Long> categoryIds, long groupId, long categoryId);

	public void moveCategoriesToTrash(long groupId, long userId)
		throws PortalException;

	public MBCategory moveCategory(
			long categoryId, long parentCategoryId,
			boolean mergeWithParentCategory)
		throws PortalException;

	public MBCategory moveCategoryFromTrash(
			long userId, long categoryId, long newCategoryId)
		throws PortalException;

	public MBCategory moveCategoryToTrash(long userId, long categoryId)
		throws PortalException;

	public void restoreCategoryFromTrash(long userId, long categoryId)
		throws PortalException;

	public void subscribeCategory(long userId, long groupId, long categoryId)
		throws PortalException;

	public void unsubscribeCategory(long userId, long groupId, long categoryId)
		throws PortalException;

	public MBCategory updateCategory(
			long categoryId, long parentCategoryId, String name,
			String description, String displayStyle, String emailAddress,
			String inProtocol, String inServerName, int inServerPort,
			boolean inUseSSL, String inUserName, String inPassword,
			int inReadInterval, String outEmailAddress, boolean outCustom,
			String outServerName, int outServerPort, boolean outUseSSL,
			String outUserName, String outPassword, boolean allowAnonymous,
			boolean mailingListActive, boolean mergeWithParentCategory,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the message boards category in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbCategory the message boards category
	 * @return the message boards category that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public MBCategory updateMBCategory(MBCategory mbCategory);

	public MBCategory updateStatus(long userId, long categoryId, int status)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<MBCategory> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<MBCategory> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<MBCategory>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1444454554