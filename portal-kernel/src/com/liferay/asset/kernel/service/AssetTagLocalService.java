/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetTag;
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
import com.liferay.portal.kernel.increment.BufferedIncrement;
import com.liferay.portal.kernel.increment.NumberIncrement;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
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

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for AssetTag. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetTagLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {"model.class.name=com.liferay.asset.kernel.model.AssetTag"}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AssetTagLocalService
	extends BaseLocalService, CTService<AssetTag>, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetTagLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the asset tag local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AssetTagLocalServiceUtil} if injection and service tracking are not available.
	 */
	public boolean addAssetEntryAssetTag(long entryId, AssetTag assetTag);

	public boolean addAssetEntryAssetTag(long entryId, long tagId);

	public boolean addAssetEntryAssetTags(
		long entryId, List<AssetTag> assetTags);

	public boolean addAssetEntryAssetTags(long entryId, long[] tagIds);

	/**
	 * Adds the asset tag to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetTagLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetTag the asset tag
	 * @return the asset tag that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetTag addAssetTag(AssetTag assetTag);

	/**
	 * Adds an asset tag.
	 *
	 * @param externalReferenceCode
	 * @param userId                the primary key of the user adding the asset tag
	 * @param groupId               the primary key of the group in which the asset tag is to
	 be added
	 * @param name                  the asset tag's name
	 * @param serviceContext        the service context to be applied
	 * @return the asset tag that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetTag addTag(
			String externalReferenceCode, long userId, long groupId,
			String name, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Returns the asset tags matching the group and names, creating new asset
	 * tags matching the names if the group doesn't already have them.
	 *
	 * <p>
	 * For each name, if an asset tag with the name doesn't already exist in the
	 * group, this method creates a new asset tag with the name in the group.
	 * </p>
	 *
	 * @param userId the primary key of the user checking the asset tags
	 * @param group the group in which to check the asset tags
	 * @param names the asset tag names
	 * @return the asset tags matching the group and names and new asset tags
	 matching the names that don't already exist in the group
	 */
	public List<AssetTag> checkTags(long userId, Group group, String[] names)
		throws PortalException;

	/**
	 * Returns the asset tags matching the group and names, creating new asset
	 * tags matching the names if the group doesn't already have them.
	 *
	 * @param userId the primary key of the user checking the asset tags
	 * @param groupId the primary key of the group in which check the asset
	 tags
	 * @param names the asset tag names
	 * @return the asset tags matching the group and names and new asset tags
	 matching the names that don't already exist in the group
	 */
	public List<AssetTag> checkTags(long userId, long groupId, String[] names)
		throws PortalException;

	public void clearAssetEntryAssetTags(long entryId);

	/**
	 * Creates a new asset tag with the primary key. Does not add the asset tag to the database.
	 *
	 * @param tagId the primary key for the new asset tag
	 * @return the new asset tag
	 */
	@Transactional(enabled = false)
	public AssetTag createAssetTag(long tagId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Decrements the number of assets to which the asset tag has been applied.
	 *
	 * @param tagId the primary key of the asset tag
	 * @param classNameId the class name ID of the entity to which the asset
	 tag had been applied
	 * @return the asset tag
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetTag decrementAssetCount(long tagId, long classNameId)
		throws PortalException;

	public void deleteAssetEntryAssetTag(long entryId, AssetTag assetTag);

	public void deleteAssetEntryAssetTag(long entryId, long tagId);

	public void deleteAssetEntryAssetTags(
		long entryId, List<AssetTag> assetTags);

	public void deleteAssetEntryAssetTags(long entryId, long[] tagIds);

	/**
	 * Deletes the asset tag from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetTagLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetTag the asset tag
	 * @return the asset tag that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public AssetTag deleteAssetTag(AssetTag assetTag);

	/**
	 * Deletes the asset tag with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetTagLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param tagId the primary key of the asset tag
	 * @return the asset tag that was removed
	 * @throws PortalException if a asset tag with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public AssetTag deleteAssetTag(long tagId) throws PortalException;

	/**
	 * Deletes all asset tags in the group.
	 *
	 * @param groupId the primary key of the group in which to delete all asset
	 tags
	 */
	public void deleteGroupTags(long groupId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Deletes the asset tag.
	 *
	 * @param tag the asset tag to be deleted
	 */
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteTag(AssetTag tag) throws PortalException;

	/**
	 * Deletes the asset tag.
	 *
	 * @param tagId the primary key of the asset tag
	 */
	public void deleteTag(long tagId) throws PortalException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetTagModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetTagModelImpl</code>.
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
	public AssetTag fetchAssetTag(long tagId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetTag fetchAssetTagByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the asset tag matching the UUID and group.
	 *
	 * @param uuid the asset tag's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset tag, or <code>null</code> if a matching asset tag could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetTag fetchAssetTagByUuidAndGroupId(String uuid, long groupId);

	/**
	 * Returns the asset tag with the name in the group.
	 *
	 * @param groupId the primary key of the group
	 * @param name the asset tag's name
	 * @return the asset tag with the name in the group or <code>null</code> if
	 it could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetTag fetchTag(long groupId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getAssetEntryAssetTags(long entryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getAssetEntryAssetTags(
		long entryId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getAssetEntryAssetTags(
		long entryId, int start, int end,
		OrderByComparator<AssetTag> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetEntryAssetTagsCount(long entryId);

	/**
	 * Returns the entryIds of the asset entries associated with the asset tag.
	 *
	 * @param tagId the tagId of the asset tag
	 * @return long[] the entryIds of asset entries associated with the asset tag
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getAssetEntryPrimaryKeys(long tagId);

	/**
	 * Returns the asset tag with the primary key.
	 *
	 * @param tagId the primary key of the asset tag
	 * @return the asset tag
	 * @throws PortalException if a asset tag with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetTag getAssetTag(long tagId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetTag getAssetTagByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the asset tag matching the UUID and group.
	 *
	 * @param uuid the asset tag's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset tag
	 * @throws PortalException if a matching asset tag could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetTag getAssetTagByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the asset tags.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @return the range of asset tags
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getAssetTags(int start, int end);

	/**
	 * Returns all the asset tags matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset tags
	 * @param companyId the primary key of the company
	 * @return the matching asset tags, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getAssetTagsByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of asset tags matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset tags
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset tags, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getAssetTagsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetTag> orderByComparator);

	/**
	 * Returns the number of asset tags.
	 *
	 * @return the number of asset tags
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetTagsCount();

	/**
	 * Returns the number of asset tags in the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the number of asset tags in the company
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCompanyTagsCount(long companyId);

	/**
	 * Returns the asset tags of the asset entry.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset tags of the asset entry
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getEntryTags(long entryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	/**
	 * Returns the asset tags in the groups.
	 *
	 * @param groupIds the primary keys of the groups
	 * @return the asset tags in the groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getGroupsTags(long[] groupIds);

	/**
	 * Returns the asset tags in the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the asset tags in the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getGroupTags(long groupId);

	/**
	 * Returns a range of all the asset tags in the group.
	 *
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @return the range of matching asset tags
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getGroupTags(long groupId, int start, int end);

	/**
	 * Returns a range of all the asset tags in the group.
	 *
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the asset tags
	 (optionally <code>null</code>)
	 * @return the range of matching asset tags
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getGroupTags(
		long groupId, int start, int end,
		OrderByComparator<AssetTag> orderByComparator);

	/**
	 * Returns the number of asset tags in the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the number of asset tags in the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupTagsCount(long groupId);

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
	 * Returns the asset tag with the primary key.
	 *
	 * @param tagId the primary key of the asset tag
	 * @return the asset tag with the primary key
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetTag getTag(long tagId) throws PortalException;

	/**
	 * Returns the asset tag with the name in the group.
	 *
	 * @param groupId the primary key of the group
	 * @param name the name of the asset tag
	 * @return the asset tag with the name in the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetTag getTag(long groupId, String name) throws PortalException;

	/**
	 * Returns the primary keys of the asset tags with the names in the group.
	 *
	 * @param groupId the primary key of the group
	 * @param names the names of the asset tags
	 * @return the primary keys of the asset tags with the names in the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getTagIds(long groupId, String[] names);

	/**
	 * Returns the primary keys of the asset tags with the name in the groups.
	 *
	 * @param groupIds the primary keys of the groups
	 * @param name the name of the asset tags
	 * @return the primary keys of the asset tags with the name in the groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getTagIds(long[] groupIds, String name);

	/**
	 * Returns the primary keys of the asset tags with the names in the groups.
	 *
	 * @param groupIds the primary keys of the groups
	 * @param names the names of the asset tags
	 * @return the primary keys of the asset tags with the names in the groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getTagIds(long[] groupIds, String[] names);

	/**
	 * Returns the primary keys of the asset tags with the names.
	 *
	 * @param name the name of the asset tags
	 * @return the primary keys of the asset tags with the names
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getTagIds(String name);

	/**
	 * Returns the names of all the asset tags.
	 *
	 * @return the names of all the asset tags
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getTagNames();

	/**
	 * Returns the names of the asset tags of the entity.
	 *
	 * @param classNameId the class name ID of the entity
	 * @param classPK the primary key of the entity
	 * @return the names of the asset tags of the entity
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getTagNames(long classNameId, long classPK);

	/**
	 * Returns the names of the asset tags of the entity
	 *
	 * @param className the class name of the entity
	 * @param classPK the primary key of the entity
	 * @return the names of the asset tags of the entity
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getTagNames(String className, long classPK);

	/**
	 * Returns all the asset tags.
	 *
	 * @return the asset tags
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getTags();

	/**
	 * Returns the asset tags of the entity.
	 *
	 * @param classNameId the class name ID of the entity
	 * @param classPK the primary key of the entity
	 * @return the asset tags of the entity
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getTags(long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getTags(long groupId, long classNameId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getTags(
		long groupId, long classNameId, String name, int start, int end);

	/**
	 * Returns the asset tags of the entity.
	 *
	 * @param className the class name of the entity
	 * @param classPK the primary key of the entity
	 * @return the asset tags of the entity
	 */
	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> getTags(String className, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getTagsSize(long groupId, long classNameId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasAssetEntryAssetTag(long entryId, long tagId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasAssetEntryAssetTags(long entryId);

	/**
	 * Returns <code>true</code> if the group contains an asset tag with the
	 * name.
	 *
	 * @param groupId the primary key of the group
	 * @param name the name of the asset tag
	 * @return <code>true</code> if the group contains an asset tag with the
	 name; <code>false</code> otherwise.
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasTag(long groupId, String name);

	/**
	 * Increments the number of assets to which the asset tag has been applied.
	 *
	 * @param tagId the primary key of the asset tag
	 * @param classNameId the class name ID of the entity to which the asset
	 tag is being applied
	 * @return the asset tag
	 */
	@BufferedIncrement(incrementClass = NumberIncrement.class)
	@Indexable(type = IndexableType.REINDEX)
	public AssetTag incrementAssetCount(long tagId, long classNameId)
		throws PortalException;

	/**
	 * Replaces all occurrences of the first asset tag with the second asset tag
	 * and deletes the first asset tag.
	 *
	 * @param fromTagId the primary key of the asset tag to be replaced
	 * @param toTagId the primary key of the asset tag to apply to the asset
	 entries of the other asset tag
	 */
	public void mergeTags(long fromTagId, long toTagId) throws PortalException;

	/**
	 * Returns the asset tags in the group whose names match the pattern.
	 *
	 * @param groupId the primary key of the group
	 * @param name the pattern to match
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @return the asset tags in the group whose names match the pattern
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> search(long groupId, String name, int start, int end);

	/**
	 * Returns the asset tags in the groups whose names match the pattern.
	 *
	 * @param groupIds the primary keys of the groups
	 * @param name the pattern to match
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @return the asset tags in the groups whose names match the pattern
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetTag> search(
		long[] groupIds, String name, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<AssetTag> searchTags(
			long[] groupIds, String name, int start, int end, Sort sort)
		throws PortalException;

	public void setAssetEntryAssetTags(long entryId, long[] tagIds);

	public void subscribeTag(long userId, long groupId, long tagId)
		throws PortalException;

	public void unsubscribeTag(long userId, long tagId) throws PortalException;

	/**
	 * Updates the asset tag in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetTagLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetTag the asset tag
	 * @return the asset tag that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetTag updateAssetTag(AssetTag assetTag);

	@Indexable(type = IndexableType.REINDEX)
	public AssetTag updateTag(
			String externalReferenceCode, long userId, long tagId, String name,
			ServiceContext serviceContext)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<AssetTag> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<AssetTag> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetTag>, R, E> updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:1103982328