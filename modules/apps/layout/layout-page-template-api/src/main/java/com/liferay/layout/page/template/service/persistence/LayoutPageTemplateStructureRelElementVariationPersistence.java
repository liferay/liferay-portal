/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence;

import com.liferay.layout.page.template.exception.NoSuchPageTemplateStructureRelElementVariationException;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the layout page template structure rel element variation service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelElementVariationUtil
 * @generated
 */
@ProviderType
public interface LayoutPageTemplateStructureRelElementVariationPersistence
	extends BasePersistence<LayoutPageTemplateStructureRelElementVariation>,
			CTPersistence<LayoutPageTemplateStructureRelElementVariation> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link LayoutPageTemplateStructureRelElementVariationUtil} to access the layout page template structure rel element variation persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public java.util.List<LayoutPageTemplateStructureRelElementVariation>
		findByUuid(
			String uuid, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationException;

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateStructureRelElementVariation> orderByComparator);

	/**
	 * Removes all the layout page template structure rel element variations where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of layout page template structure rel element variations where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template structure rel element variations
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the layout page template structure rel element variation where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureRelElementVariationException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation findByUUID_G(
			String uuid, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationException;

	/**
	 * Returns the layout page template structure rel element variation where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the layout page template structure rel element variation where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template structure rel element variation that was removed
	 */
	public LayoutPageTemplateStructureRelElementVariation removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationException;

	/**
	 * Returns the number of layout page template structure rel element variations where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template structure rel element variations
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public java.util.List<LayoutPageTemplateStructureRelElementVariation>
		findByUuid_C(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationException;

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateStructureRelElementVariation> orderByComparator);

	/**
	 * Removes all the layout page template structure rel element variations where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of layout page template structure rel element variations where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template structure rel element variations
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public java.util.List<LayoutPageTemplateStructureRelElementVariation>
		findByPlid(
			long plid, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation findByPlid_First(
			long plid,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationException;

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation fetchByPlid_First(
		long plid,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateStructureRelElementVariation> orderByComparator);

	/**
	 * Removes all the layout page template structure rel element variations where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	public void removeByPlid(long plid);

	/**
	 * Returns the number of layout page template structure rel element variations where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout page template structure rel element variations
	 */
	public int countByPlid(long plid);

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public java.util.List<LayoutPageTemplateStructureRelElementVariation>
		findBySegmentsExperienceERC(
			String segmentsExperienceERC, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where segmentsExperienceERC = &#63;.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation
			findBySegmentsExperienceERC_First(
				String segmentsExperienceERC,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariation>
						orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationException;

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where segmentsExperienceERC = &#63;.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation
		fetchBySegmentsExperienceERC_First(
			String segmentsExperienceERC,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator);

	/**
	 * Removes all the layout page template structure rel element variations where segmentsExperienceERC = &#63; from the database.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 */
	public void removeBySegmentsExperienceERC(String segmentsExperienceERC);

	/**
	 * Returns the number of layout page template structure rel element variations where segmentsExperienceERC = &#63;.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching layout page template structure rel element variations
	 */
	public int countBySegmentsExperienceERC(String segmentsExperienceERC);

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public java.util.List<LayoutPageTemplateStructureRelElementVariation>
		findByP_SEERC(
			long plid, String segmentsExperienceERC, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation findByP_SEERC_First(
			long plid, String segmentsExperienceERC,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationException;

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation fetchByP_SEERC_First(
		long plid, String segmentsExperienceERC,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateStructureRelElementVariation> orderByComparator);

	/**
	 * Removes all the layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 */
	public void removeByP_SEERC(long plid, String segmentsExperienceERC);

	/**
	 * Returns the number of layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching layout page template structure rel element variations
	 */
	public int countByP_SEERC(long plid, String segmentsExperienceERC);

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public java.util.List<LayoutPageTemplateStructureRelElementVariation>
		findByA_P_SEERC(
			boolean active, long plid, String segmentsExperienceERC, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation findByA_P_SEERC_First(
			boolean active, long plid, String segmentsExperienceERC,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationException;

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation
		fetchByA_P_SEERC_First(
			boolean active, long plid, String segmentsExperienceERC,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator);

	/**
	 * Removes all the layout page template structure rel element variations where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 */
	public void removeByA_P_SEERC(
		boolean active, long plid, String segmentsExperienceERC);

	/**
	 * Returns the number of layout page template structure rel element variations where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching layout page template structure rel element variations
	 */
	public int countByA_P_SEERC(
		boolean active, long plid, String segmentsExperienceERC);

	/**
	 * Returns the layout page template structure rel element variation where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureRelElementVariationException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationException;

	/**
	 * Returns the layout page template structure rel element variation where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache);

	/**
	 * Removes the layout page template structure rel element variation where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout page template structure rel element variation that was removed
	 */
	public LayoutPageTemplateStructureRelElementVariation removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationException;

	/**
	 * Returns the number of layout page template structure rel element variations where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout page template structure rel element variations
	 */
	public int countByERC_G(String externalReferenceCode, long groupId);

	/**
	 * Creates a new layout page template structure rel element variation with the primary key. Does not add the layout page template structure rel element variation to the database.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key for the new layout page template structure rel element variation
	 * @return the new layout page template structure rel element variation
	 */
	public LayoutPageTemplateStructureRelElementVariation create(
		long layoutPageTemplateStructureRelElementVariationId);

	/**
	 * Removes the layout page template structure rel element variation with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key of the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation that was removed
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a layout page template structure rel element variation with the primary key could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation remove(
			long layoutPageTemplateStructureRelElementVariationId)
		throws NoSuchPageTemplateStructureRelElementVariationException;

	public LayoutPageTemplateStructureRelElementVariation updateImpl(
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation);

	/**
	 * Returns the layout page template structure rel element variation with the primary key or throws a <code>NoSuchPageTemplateStructureRelElementVariationException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key of the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a layout page template structure rel element variation with the primary key could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation findByPrimaryKey(
			long layoutPageTemplateStructureRelElementVariationId)
		throws NoSuchPageTemplateStructureRelElementVariationException;

	/**
	 * Returns the layout page template structure rel element variation with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key of the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation, or <code>null</code> if a layout page template structure rel element variation with the primary key could not be found
	 */
	public LayoutPageTemplateStructureRelElementVariation fetchByPrimaryKey(
		long layoutPageTemplateStructureRelElementVariationId);

	/**
	 * Returns the layout page template structure rel element variation where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public default LayoutPageTemplateStructureRelElementVariation fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the layout page template structure rel element variation where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public default LayoutPageTemplateStructureRelElementVariation fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns all the layout page template structure rel element variations where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByUuid(
			String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @return the range of matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByUuid(
			String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByUuid(
			String uuid, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template structure rel element variations where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByUuid_C(
			String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @return the range of matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByUuid_C(
			String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByUuid_C(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template structure rel element variations where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByPlid(long plid) {

		return findByPlid(
			plid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variations where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @return the range of matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByPlid(
			long plid, int start, int end) {

		return findByPlid(plid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByPlid(
			long plid, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator) {

		return findByPlid(plid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template structure rel element variations where segmentsExperienceERC = &#63;.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation>
			findBySegmentsExperienceERC(String segmentsExperienceERC) {

		return findBySegmentsExperienceERC(
			segmentsExperienceERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variations where segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @return the range of matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation>
			findBySegmentsExperienceERC(
				String segmentsExperienceERC, int start, int end) {

		return findBySegmentsExperienceERC(
			segmentsExperienceERC, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation>
			findBySegmentsExperienceERC(
				String segmentsExperienceERC, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariation>
						orderByComparator) {

		return findBySegmentsExperienceERC(
			segmentsExperienceERC, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByP_SEERC(
			long plid, String segmentsExperienceERC) {

		return findByP_SEERC(
			plid, segmentsExperienceERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @return the range of matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByP_SEERC(
			long plid, String segmentsExperienceERC, int start, int end) {

		return findByP_SEERC(
			plid, segmentsExperienceERC, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByP_SEERC(
			long plid, String segmentsExperienceERC, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator) {

		return findByP_SEERC(
			plid, segmentsExperienceERC, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template structure rel element variations where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByA_P_SEERC(
			boolean active, long plid, String segmentsExperienceERC) {

		return findByA_P_SEERC(
			active, plid, segmentsExperienceERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variations where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @return the range of matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByA_P_SEERC(
			boolean active, long plid, String segmentsExperienceERC, int start,
			int end) {

		return findByA_P_SEERC(
			active, plid, segmentsExperienceERC, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public default java.util.List
		<LayoutPageTemplateStructureRelElementVariation> findByA_P_SEERC(
			boolean active, long plid, String segmentsExperienceERC, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator) {

		return findByA_P_SEERC(
			active, plid, segmentsExperienceERC, start, end, orderByComparator,
			true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1173033274