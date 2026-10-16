/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence;

import com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the layout page template collection service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateCollectionUtil
 * @generated
 */
@ProviderType
public interface LayoutPageTemplateCollectionPersistence
	extends BasePersistence<LayoutPageTemplateCollection>,
			CTPersistence<LayoutPageTemplateCollection> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link LayoutPageTemplateCollectionUtil} to access the layout page template collection persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the layout page template collections where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	public java.util.List<LayoutPageTemplateCollection> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the first layout page template collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator);

	/**
	 * Removes all the layout page template collections where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of layout page template collections where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template collections
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the layout page template collection where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection findByUUID_G(String uuid, long groupId)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the layout page template collection where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the layout page template collection where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template collection that was removed
	 */
	public LayoutPageTemplateCollection removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the number of layout page template collections where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	public java.util.List<LayoutPageTemplateCollection> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the first layout page template collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator);

	/**
	 * Removes all the layout page template collections where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template collections
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	public java.util.List<LayoutPageTemplateCollection> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateCollection> filterFindByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator);

	/**
	 * Removes all the layout page template collections where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of layout page template collections where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	public int filterCountByGroupId(long groupId);

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	public java.util.List<LayoutPageTemplateCollection> findByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection findByG_P_First(
			long groupId, long parentLayoutPageTemplateCollectionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection fetchByG_P_First(
		long groupId, long parentLayoutPageTemplateCollectionId,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateCollection> filterFindByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator);

	/**
	 * Removes all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 */
	public void removeByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId);

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @return the number of matching layout page template collections
	 */
	public int countByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId);

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	public int filterCountByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId);

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	public java.util.List<LayoutPageTemplateCollection> findByG_T(
		long groupId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection findByG_T_First(
			long groupId, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection fetchByG_T_First(
		long groupId, int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateCollection> filterFindByG_T(
		long groupId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator);

	/**
	 * Removes all the layout page template collections where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	public void removeByG_T(long groupId, int type);

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	public int countByG_T(long groupId, int type);

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	public int filterCountByG_T(long groupId, int type);

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	public java.util.List<LayoutPageTemplateCollection> findByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection findByG_P_T_First(
			long groupId, long parentLayoutPageTemplateCollectionId, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection fetchByG_P_T_First(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateCollection> filterFindByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator);

	/**
	 * Removes all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 */
	public void removeByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type);

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	public int countByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type);

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	public int filterCountByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type);

	/**
	 * Returns the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection findByG_LPTCK_T(
			long groupId, String layoutPageTemplateCollectionKey, int type)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection fetchByG_LPTCK_T(
		long groupId, String layoutPageTemplateCollectionKey, int type,
		boolean useFinderCache);

	/**
	 * Removes the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the layout page template collection that was removed
	 */
	public LayoutPageTemplateCollection removeByG_LPTCK_T(
			long groupId, String layoutPageTemplateCollectionKey, int type)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	public int countByG_LPTCK_T(
		long groupId, String layoutPageTemplateCollectionKey, int type);

	/**
	 * Returns all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collections
	 */
	public java.util.List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type);

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	public java.util.List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	public java.util.List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	public java.util.List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection findByG_LikeN_T_First(
			long groupId, String name, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection fetchByG_LikeN_T_First(
		long groupId, String name, int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator);

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collections that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateCollection> filterFindByG_LikeN_T(
		long groupId, String name, int type);

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateCollection> filterFindByG_LikeN_T(
		long groupId, String name, int type, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateCollection> filterFindByG_LikeN_T(
		long groupId, String name, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator);

	/**
	 * Removes all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 */
	public void removeByG_LikeN_T(long groupId, String name, int type);

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	public int countByG_LikeN_T(long groupId, String name, int type);

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	public int filterCountByG_LikeN_T(long groupId, String name, int type);

	/**
	 * Returns the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection findByG_P_N_T(
			long groupId, long parentLayoutPageTemplateCollectionId,
			String name, int type)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection fetchByG_P_N_T(
		long groupId, long parentLayoutPageTemplateCollectionId, String name,
		int type, boolean useFinderCache);

	/**
	 * Removes the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the layout page template collection that was removed
	 */
	public LayoutPageTemplateCollection removeByG_P_N_T(
			long groupId, long parentLayoutPageTemplateCollectionId,
			String name, int type)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	public int countByG_P_N_T(
		long groupId, long parentLayoutPageTemplateCollectionId, String name,
		int type);

	/**
	 * Returns the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public LayoutPageTemplateCollection fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache);

	/**
	 * Removes the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout page template collection that was removed
	 */
	public LayoutPageTemplateCollection removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the number of layout page template collections where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections
	 */
	public int countByERC_G(String externalReferenceCode, long groupId);

	/**
	 * Creates a new layout page template collection with the primary key. Does not add the layout page template collection to the database.
	 *
	 * @param layoutPageTemplateCollectionId the primary key for the new layout page template collection
	 * @return the new layout page template collection
	 */
	public LayoutPageTemplateCollection create(
		long layoutPageTemplateCollectionId);

	/**
	 * Removes the layout page template collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection that was removed
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	public LayoutPageTemplateCollection remove(
			long layoutPageTemplateCollectionId)
		throws NoSuchPageTemplateCollectionException;

	public LayoutPageTemplateCollection updateImpl(
		LayoutPageTemplateCollection layoutPageTemplateCollection);

	/**
	 * Returns the layout page template collection with the primary key or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	public LayoutPageTemplateCollection findByPrimaryKey(
			long layoutPageTemplateCollectionId)
		throws NoSuchPageTemplateCollectionException;

	/**
	 * Returns the layout page template collection with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection, or <code>null</code> if a layout page template collection with the primary key could not be found
	 */
	public LayoutPageTemplateCollection fetchByPrimaryKey(
		long layoutPageTemplateCollectionId);

	/**
	 * Returns the layout page template collection where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public default LayoutPageTemplateCollection fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public default LayoutPageTemplateCollection fetchByG_LPTCK_T(
		long groupId, String layoutPageTemplateCollectionKey, int type) {

		return fetchByG_LPTCK_T(
			groupId, layoutPageTemplateCollectionKey, type, true);
	}

	/**
	 * Returns the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public default LayoutPageTemplateCollection fetchByG_P_N_T(
		long groupId, long parentLayoutPageTemplateCollectionId, String name,
		int type) {

		return fetchByG_P_N_T(
			groupId, parentLayoutPageTemplateCollectionId, name, type, true);
	}

	/**
	 * Returns the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public default LayoutPageTemplateCollection fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns all the layout page template collections where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByUuid(
		String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template collections where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template collections where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByGroupId(
		long groupId) {

		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout page template collections that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateCollection>
		filterFindByGroupId(long groupId) {

		return filterFindByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateCollection>
		filterFindByGroupId(long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @return the matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		return findByG_P(
			groupId, parentLayoutPageTemplateCollectionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end) {

		return findByG_P(
			groupId, parentLayoutPageTemplateCollectionId, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator) {

		return findByG_P(
			groupId, parentLayoutPageTemplateCollectionId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @return the matching layout page template collections that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateCollection> filterFindByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		return filterFindByG_P(
			groupId, parentLayoutPageTemplateCollectionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateCollection> filterFindByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end) {

		return filterFindByG_P(
			groupId, parentLayoutPageTemplateCollectionId, start, end, null);
	}

	/**
	 * Returns all the layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByG_T(
		long groupId, int type) {

		return findByG_T(
			groupId, type, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByG_T(
		long groupId, int type, int start, int end) {

		return findByG_T(groupId, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByG_T(
		long groupId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator) {

		return findByG_T(groupId, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layout page template collections that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateCollection> filterFindByG_T(
		long groupId, int type) {

		return filterFindByG_T(
			groupId, type, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateCollection> filterFindByG_T(
		long groupId, int type, int start, int end) {

		return filterFindByG_T(groupId, type, start, end, null);
	}

	/**
	 * Returns all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @return the matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		return findByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end) {

		return findByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type, start, end,
			null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	public default java.util.List<LayoutPageTemplateCollection> findByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateCollection> orderByComparator) {

		return findByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @return the matching layout page template collections that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateCollection>
		filterFindByG_P_T(
			long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		return filterFindByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateCollection>
		filterFindByG_P_T(
			long groupId, long parentLayoutPageTemplateCollectionId, int type,
			int start, int end) {

		return filterFindByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type, start, end,
			null);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-792068262