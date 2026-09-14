/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.persistence;

import com.liferay.audiences.exception.NoSuchAudiencesEntryGroupRelException;
import com.liferay.audiences.model.AudiencesEntryGroupRel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the audiences entry group rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AudiencesEntryGroupRelUtil
 * @generated
 */
@ProviderType
public interface AudiencesEntryGroupRelPersistence
	extends BasePersistence<AudiencesEntryGroupRel> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link AudiencesEntryGroupRelUtil} to access the audiences entry group rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching audiences entry group rels
	 */
	public java.util.List<AudiencesEntryGroupRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AudiencesEntryGroupRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first audiences entry group rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry group rel
	 * @throws NoSuchAudiencesEntryGroupRelException if a matching audiences entry group rel could not be found
	 */
	public AudiencesEntryGroupRel findByC_AEERC_First(
			long companyId, String audienceEntryERC,
			com.liferay.portal.kernel.util.OrderByComparator
				<AudiencesEntryGroupRel> orderByComparator)
		throws NoSuchAudiencesEntryGroupRelException;

	/**
	 * Returns the first audiences entry group rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry group rel, or <code>null</code> if a matching audiences entry group rel could not be found
	 */
	public AudiencesEntryGroupRel fetchByC_AEERC_First(
		long companyId, String audienceEntryERC,
		com.liferay.portal.kernel.util.OrderByComparator<AudiencesEntryGroupRel>
			orderByComparator);

	/**
	 * Removes all the audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 */
	public void removeByC_AEERC(long companyId, String audienceEntryERC);

	/**
	 * Returns the number of audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching audiences entry group rels
	 */
	public int countByC_AEERC(long companyId, String audienceEntryERC);

	/**
	 * Returns an ordered range of all the audiences entry group rels where companyId = &#63; and groupERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching audiences entry group rels
	 */
	public java.util.List<AudiencesEntryGroupRel> findByC_GERC(
		long companyId, String groupERC, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AudiencesEntryGroupRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first audiences entry group rel in the ordered set where companyId = &#63; and groupERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry group rel
	 * @throws NoSuchAudiencesEntryGroupRelException if a matching audiences entry group rel could not be found
	 */
	public AudiencesEntryGroupRel findByC_GERC_First(
			long companyId, String groupERC,
			com.liferay.portal.kernel.util.OrderByComparator
				<AudiencesEntryGroupRel> orderByComparator)
		throws NoSuchAudiencesEntryGroupRelException;

	/**
	 * Returns the first audiences entry group rel in the ordered set where companyId = &#63; and groupERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry group rel, or <code>null</code> if a matching audiences entry group rel could not be found
	 */
	public AudiencesEntryGroupRel fetchByC_GERC_First(
		long companyId, String groupERC,
		com.liferay.portal.kernel.util.OrderByComparator<AudiencesEntryGroupRel>
			orderByComparator);

	/**
	 * Removes all the audiences entry group rels where companyId = &#63; and groupERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 */
	public void removeByC_GERC(long companyId, String groupERC);

	/**
	 * Returns the number of audiences entry group rels where companyId = &#63; and groupERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @return the number of matching audiences entry group rels
	 */
	public int countByC_GERC(long companyId, String groupERC);

	/**
	 * Returns the audiences entry group rel where companyId = &#63; and audienceEntryERC = &#63; and groupERC = &#63; or throws a <code>NoSuchAudiencesEntryGroupRelException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param groupERC the group erc
	 * @return the matching audiences entry group rel
	 * @throws NoSuchAudiencesEntryGroupRelException if a matching audiences entry group rel could not be found
	 */
	public AudiencesEntryGroupRel findByC_AEERC_GERC(
			long companyId, String audienceEntryERC, String groupERC)
		throws NoSuchAudiencesEntryGroupRelException;

	/**
	 * Returns the audiences entry group rel where companyId = &#63; and audienceEntryERC = &#63; and groupERC = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param groupERC the group erc
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching audiences entry group rel, or <code>null</code> if a matching audiences entry group rel could not be found
	 */
	public AudiencesEntryGroupRel fetchByC_AEERC_GERC(
		long companyId, String audienceEntryERC, String groupERC,
		boolean useFinderCache);

	/**
	 * Removes the audiences entry group rel where companyId = &#63; and audienceEntryERC = &#63; and groupERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param groupERC the group erc
	 * @return the audiences entry group rel that was removed
	 */
	public AudiencesEntryGroupRel removeByC_AEERC_GERC(
			long companyId, String audienceEntryERC, String groupERC)
		throws NoSuchAudiencesEntryGroupRelException;

	/**
	 * Returns the number of audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63; and groupERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param groupERC the group erc
	 * @return the number of matching audiences entry group rels
	 */
	public int countByC_AEERC_GERC(
		long companyId, String audienceEntryERC, String groupERC);

	/**
	 * Creates a new audiences entry group rel with the primary key. Does not add the audiences entry group rel to the database.
	 *
	 * @param audiencesEntryGroupRelId the primary key for the new audiences entry group rel
	 * @return the new audiences entry group rel
	 */
	public AudiencesEntryGroupRel create(long audiencesEntryGroupRelId);

	/**
	 * Removes the audiences entry group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param audiencesEntryGroupRelId the primary key of the audiences entry group rel
	 * @return the audiences entry group rel that was removed
	 * @throws NoSuchAudiencesEntryGroupRelException if a audiences entry group rel with the primary key could not be found
	 */
	public AudiencesEntryGroupRel remove(long audiencesEntryGroupRelId)
		throws NoSuchAudiencesEntryGroupRelException;

	public AudiencesEntryGroupRel updateImpl(
		AudiencesEntryGroupRel audiencesEntryGroupRel);

	/**
	 * Returns the audiences entry group rel with the primary key or throws a <code>NoSuchAudiencesEntryGroupRelException</code> if it could not be found.
	 *
	 * @param audiencesEntryGroupRelId the primary key of the audiences entry group rel
	 * @return the audiences entry group rel
	 * @throws NoSuchAudiencesEntryGroupRelException if a audiences entry group rel with the primary key could not be found
	 */
	public AudiencesEntryGroupRel findByPrimaryKey(
			long audiencesEntryGroupRelId)
		throws NoSuchAudiencesEntryGroupRelException;

	/**
	 * Returns the audiences entry group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param audiencesEntryGroupRelId the primary key of the audiences entry group rel
	 * @return the audiences entry group rel, or <code>null</code> if a audiences entry group rel with the primary key could not be found
	 */
	public AudiencesEntryGroupRel fetchByPrimaryKey(
		long audiencesEntryGroupRelId);

	/**
	 * Returns the audiences entry group rel where companyId = &#63; and audienceEntryERC = &#63; and groupERC = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param groupERC the group erc
	 * @return the matching audiences entry group rel, or <code>null</code> if a matching audiences entry group rel could not be found
	 */
	public default AudiencesEntryGroupRel fetchByC_AEERC_GERC(
		long companyId, String audienceEntryERC, String groupERC) {

		return fetchByC_AEERC_GERC(companyId, audienceEntryERC, groupERC, true);
	}

	/**
	 * Returns all the audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching audiences entry group rels
	 */
	public default java.util.List<AudiencesEntryGroupRel> findByC_AEERC(
		long companyId, String audienceEntryERC) {

		return findByC_AEERC(
			companyId, audienceEntryERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @return the range of matching audiences entry group rels
	 */
	public default java.util.List<AudiencesEntryGroupRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end) {

		return findByC_AEERC(
			companyId, audienceEntryERC, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching audiences entry group rels
	 */
	public default java.util.List<AudiencesEntryGroupRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AudiencesEntryGroupRel>
			orderByComparator) {

		return findByC_AEERC(
			companyId, audienceEntryERC, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the audiences entry group rels where companyId = &#63; and groupERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @return the matching audiences entry group rels
	 */
	public default java.util.List<AudiencesEntryGroupRel> findByC_GERC(
		long companyId, String groupERC) {

		return findByC_GERC(
			companyId, groupERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the audiences entry group rels where companyId = &#63; and groupERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @return the range of matching audiences entry group rels
	 */
	public default java.util.List<AudiencesEntryGroupRel> findByC_GERC(
		long companyId, String groupERC, int start, int end) {

		return findByC_GERC(companyId, groupERC, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the audiences entry group rels where companyId = &#63; and groupERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching audiences entry group rels
	 */
	public default java.util.List<AudiencesEntryGroupRel> findByC_GERC(
		long companyId, String groupERC, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AudiencesEntryGroupRel>
			orderByComparator) {

		return findByC_GERC(
			companyId, groupERC, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1399141980