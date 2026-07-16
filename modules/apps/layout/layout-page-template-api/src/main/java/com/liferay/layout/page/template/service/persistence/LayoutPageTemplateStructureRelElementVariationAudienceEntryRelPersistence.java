/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence;

import com.liferay.layout.page.template.exception.NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the layout page template structure rel element variation audience entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelElementVariationAudienceEntryRelUtil
 * @generated
 */
@ProviderType
public interface
	LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence
		extends BasePersistence
			<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>,
				CTPersistence
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link LayoutPageTemplateStructureRelElementVariationAudienceEntryRelUtil} to access the layout page template structure rel element variation audience entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid(
				String uuid, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator,
				boolean useFinderCache);

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByUuid_First(
				String uuid,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException;

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
					orderByComparator);

	/**
	 * Removes all the layout page template structure rel element variation audience entry rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByUUID_G(String uuid, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException;

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByUUID_G(String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the layout page template structure rel element variation audience entry rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template structure rel element variation audience entry rel that was removed
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			removeByUUID_G(String uuid, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException;

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid_C(
				String uuid, long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator,
				boolean useFinderCache);

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByUuid_C_First(
				String uuid, long companyId,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException;

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
					orderByComparator);

	/**
	 * Removes all the layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByLayoutPageTemplateStructureRelElementVariationERC(
				String layoutPageTemplateStructureRelElementVariationERC,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator,
				boolean useFinderCache);

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByLayoutPageTemplateStructureRelElementVariationERC_First(
				String layoutPageTemplateStructureRelElementVariationERC,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException;

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByLayoutPageTemplateStructureRelElementVariationERC_First(
			String layoutPageTemplateStructureRelElementVariationERC,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
					orderByComparator);

	/**
	 * Removes all the layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63; from the database.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 */
	public void removeByLayoutPageTemplateStructureRelElementVariationERC(
		String layoutPageTemplateStructureRelElementVariationERC);

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	public int countByLayoutPageTemplateStructureRelElementVariationERC(
		String layoutPageTemplateStructureRelElementVariationERC);

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByC_AEERC(
				long companyId, String audienceEntryERC, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator,
				boolean useFinderCache);

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByC_AEERC_First(
				long companyId, String audienceEntryERC,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException;

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByC_AEERC_First(
			long companyId, String audienceEntryERC,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
					orderByComparator);

	/**
	 * Removes all the layout page template structure rel element variation audience entry rels where companyId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 */
	public void removeByC_AEERC(long companyId, String audienceEntryERC);

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	public int countByC_AEERC(long companyId, String audienceEntryERC);

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException;

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByERC_G(
			String externalReferenceCode, long groupId, boolean useFinderCache);

	/**
	 * Removes the layout page template structure rel element variation audience entry rel where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout page template structure rel element variation audience entry rel that was removed
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException;

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	public int countByERC_G(String externalReferenceCode, long groupId);

	/**
	 * Creates a new layout page template structure rel element variation audience entry rel with the primary key. Does not add the layout page template structure rel element variation audience entry rel to the database.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key for the new layout page template structure rel element variation audience entry rel
	 * @return the new layout page template structure rel element variation audience entry rel
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		create(
			long
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);

	/**
	 * Removes the layout page template structure rel element variation audience entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key of the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel that was removed
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a layout page template structure rel element variation audience entry rel with the primary key could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			remove(
				long
					layoutPageTemplateStructureRelElementVariationAudienceEntryRelId)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException;

	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		updateImpl(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel);

	/**
	 * Returns the layout page template structure rel element variation audience entry rel with the primary key or throws a <code>NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key of the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a layout page template structure rel element variation audience entry rel with the primary key could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByPrimaryKey(
				long
					layoutPageTemplateStructureRelElementVariationAudienceEntryRelId)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException;

	/**
	 * Returns the layout page template structure rel element variation audience entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key of the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel, or <code>null</code> if a layout page template structure rel element variation audience entry rel with the primary key could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByPrimaryKey(
			long
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public default
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			fetchByUUID_G(String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public default
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			fetchByERC_G(String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns all the layout page template structure rel element variation audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout page template structure rel element variation audience entry rels
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid(String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variation audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @return the range of matching layout page template structure rel element variation audience entry rels
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid(String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid(
				String uuid, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout page template structure rel element variation audience entry rels
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid_C(String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @return the range of matching layout page template structure rel element variation audience entry rels
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid_C(String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid_C(
				String uuid, long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @return the matching layout page template structure rel element variation audience entry rels
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByLayoutPageTemplateStructureRelElementVariationERC(
				String layoutPageTemplateStructureRelElementVariationERC) {

		return findByLayoutPageTemplateStructureRelElementVariationERC(
			layoutPageTemplateStructureRelElementVariationERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @return the range of matching layout page template structure rel element variation audience entry rels
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByLayoutPageTemplateStructureRelElementVariationERC(
				String layoutPageTemplateStructureRelElementVariationERC,
				int start, int end) {

		return findByLayoutPageTemplateStructureRelElementVariationERC(
			layoutPageTemplateStructureRelElementVariationERC, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByLayoutPageTemplateStructureRelElementVariationERC(
				String layoutPageTemplateStructureRelElementVariationERC,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator) {

		return findByLayoutPageTemplateStructureRelElementVariationERC(
			layoutPageTemplateStructureRelElementVariationERC, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout page template structure rel element variation audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching layout page template structure rel element variation audience entry rels
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByC_AEERC(long companyId, String audienceEntryERC) {

		return findByC_AEERC(
			companyId, audienceEntryERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variation audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @return the range of matching layout page template structure rel element variation audience entry rels
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByC_AEERC(
				long companyId, String audienceEntryERC, int start, int end) {

		return findByC_AEERC(
			companyId, audienceEntryERC, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByC_AEERC(
				long companyId, String audienceEntryERC, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator) {

		return findByC_AEERC(
			companyId, audienceEntryERC, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1258272629