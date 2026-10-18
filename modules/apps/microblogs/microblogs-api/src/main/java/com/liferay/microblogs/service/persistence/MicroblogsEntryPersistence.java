/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.service.persistence;

import com.liferay.microblogs.exception.NoSuchEntryException;
import com.liferay.microblogs.model.MicroblogsEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the microblogs entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MicroblogsEntryUtil
 * @generated
 */
@ProviderType
public interface MicroblogsEntryPersistence
	extends BasePersistence<MicroblogsEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link MicroblogsEntryUtil} to access the microblogs entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the microblogs entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	public java.util.List<MicroblogsEntry> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first microblogs entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	public MicroblogsEntry findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first microblogs entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	public MicroblogsEntry fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	public java.util.List<MicroblogsEntry> filterFindByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator);

	/**
	 * Removes all the microblogs entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of microblogs entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching microblogs entries
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns the number of microblogs entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	public int filterCountByCompanyId(long companyId);

	/**
	 * Returns an ordered range of all the microblogs entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	public java.util.List<MicroblogsEntry> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first microblogs entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	public MicroblogsEntry findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first microblogs entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	public MicroblogsEntry fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	public java.util.List<MicroblogsEntry> filterFindByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator);

	/**
	 * Removes all the microblogs entries where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of microblogs entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching microblogs entries
	 */
	public int countByUserId(long userId);

	/**
	 * Returns the number of microblogs entries that the user has permission to view where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	public int filterCountByUserId(long userId);

	/**
	 * Returns an ordered range of all the microblogs entries where userId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	public java.util.List<MicroblogsEntry> findByU_T(
		long userId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first microblogs entry in the ordered set where userId = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	public MicroblogsEntry findByU_T_First(
			long userId, int type,
			com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first microblogs entry in the ordered set where userId = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	public MicroblogsEntry fetchByU_T_First(
		long userId, int type,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where userId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	public java.util.List<MicroblogsEntry> filterFindByU_T(
		long userId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator);

	/**
	 * Removes all the microblogs entries where userId = &#63; and type = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param type the type
	 */
	public void removeByU_T(long userId, int type);

	/**
	 * Returns the number of microblogs entries where userId = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @return the number of matching microblogs entries
	 */
	public int countByU_T(long userId, int type);

	/**
	 * Returns the number of microblogs entries that the user has permission to view where userId = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	public int filterCountByU_T(long userId, int type);

	/**
	 * Returns an ordered range of all the microblogs entries where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	public java.util.List<MicroblogsEntry> findByT_P(
		int type, long parentMicroblogsEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first microblogs entry in the ordered set where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	public MicroblogsEntry findByT_P_First(
			int type, long parentMicroblogsEntryId,
			com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first microblogs entry in the ordered set where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	public MicroblogsEntry fetchByT_P_First(
		int type, long parentMicroblogsEntryId,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	public java.util.List<MicroblogsEntry> filterFindByT_P(
		int type, long parentMicroblogsEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator);

	/**
	 * Removes all the microblogs entries where type = &#63; and parentMicroblogsEntryId = &#63; from the database.
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 */
	public void removeByT_P(int type, long parentMicroblogsEntryId);

	/**
	 * Returns the number of microblogs entries where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @return the number of matching microblogs entries
	 */
	public int countByT_P(int type, long parentMicroblogsEntryId);

	/**
	 * Returns the number of microblogs entries that the user has permission to view where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	public int filterCountByT_P(int type, long parentMicroblogsEntryId);

	/**
	 * Returns an ordered range of all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	public java.util.List<MicroblogsEntry> findByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first microblogs entry in the ordered set where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	public MicroblogsEntry findByCCNI_CCPK_T_First(
			long creatorClassNameId, long creatorClassPK, int type,
			com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first microblogs entry in the ordered set where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	public MicroblogsEntry fetchByCCNI_CCPK_T_First(
		long creatorClassNameId, long creatorClassPK, int type,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	public java.util.List<MicroblogsEntry> filterFindByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permission to view where creatorClassNameId = &#63; and creatorClassPK = any &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	public java.util.List<MicroblogsEntry> filterFindByCCNI_CCPK_T(
		long creatorClassNameId, long[] creatorClassPKs, int type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	public java.util.List<MicroblogsEntry> findByCCNI_CCPK_T(
		long creatorClassNameId, long[] creatorClassPKs, int type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63; from the database.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 */
	public void removeByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type);

	/**
	 * Returns the number of microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @return the number of matching microblogs entries
	 */
	public int countByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type);

	/**
	 * Returns the number of microblogs entries where creatorClassNameId = &#63; and creatorClassPK = any &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @return the number of matching microblogs entries
	 */
	public int countByCCNI_CCPK_T(
		long creatorClassNameId, long[] creatorClassPKs, int type);

	/**
	 * Returns the number of microblogs entries that the user has permission to view where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	public int filterCountByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type);

	/**
	 * Returns the number of microblogs entries that the user has permission to view where creatorClassNameId = &#63; and creatorClassPK = any &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	public int filterCountByCCNI_CCPK_T(
		long creatorClassNameId, long[] creatorClassPKs, int type);

	/**
	 * Returns an ordered range of all the microblogs entries where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	public java.util.List<MicroblogsEntry> findByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first microblogs entry in the ordered set where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	public MicroblogsEntry findByU_C_T_S_First(
			long userId, Date createDate, int type, int socialRelationType,
			com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first microblogs entry in the ordered set where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	public MicroblogsEntry fetchByU_C_T_S_First(
		long userId, Date createDate, int type, int socialRelationType,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	public java.util.List<MicroblogsEntry> filterFindByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator);

	/**
	 * Removes all the microblogs entries where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 */
	public void removeByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType);

	/**
	 * Returns the number of microblogs entries where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @return the number of matching microblogs entries
	 */
	public int countByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType);

	/**
	 * Returns the number of microblogs entries that the user has permission to view where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	public int filterCountByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType);

	/**
	 * Creates a new microblogs entry with the primary key. Does not add the microblogs entry to the database.
	 *
	 * @param microblogsEntryId the primary key for the new microblogs entry
	 * @return the new microblogs entry
	 */
	public MicroblogsEntry create(long microblogsEntryId);

	/**
	 * Removes the microblogs entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param microblogsEntryId the primary key of the microblogs entry
	 * @return the microblogs entry that was removed
	 * @throws NoSuchEntryException if a microblogs entry with the primary key could not be found
	 */
	public MicroblogsEntry remove(long microblogsEntryId)
		throws NoSuchEntryException;

	public MicroblogsEntry updateImpl(MicroblogsEntry microblogsEntry);

	/**
	 * Returns the microblogs entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param microblogsEntryId the primary key of the microblogs entry
	 * @return the microblogs entry
	 * @throws NoSuchEntryException if a microblogs entry with the primary key could not be found
	 */
	public MicroblogsEntry findByPrimaryKey(long microblogsEntryId)
		throws NoSuchEntryException;

	/**
	 * Returns the microblogs entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param microblogsEntryId the primary key of the microblogs entry
	 * @return the microblogs entry, or <code>null</code> if a microblogs entry with the primary key could not be found
	 */
	public MicroblogsEntry fetchByPrimaryKey(long microblogsEntryId);

	/**
	 * Returns all the microblogs entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByCompanyId(
		long companyId) {

		return findByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the microblogs entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the microblogs entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the microblogs entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching microblogs entries that the user has permission to view
	 */
	public default java.util.List<MicroblogsEntry> filterFindByCompanyId(
		long companyId) {

		return filterFindByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the microblogs entries that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of matching microblogs entries that the user has permission to view
	 */
	public default java.util.List<MicroblogsEntry> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns all the microblogs entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByUserId(long userId) {
		return findByUserId(
			userId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the microblogs entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the microblogs entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the microblogs entries that the user has permission to view where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching microblogs entries that the user has permission to view
	 */
	public default java.util.List<MicroblogsEntry> filterFindByUserId(
		long userId) {

		return filterFindByUserId(
			userId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the microblogs entries that the user has permission to view where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of matching microblogs entries that the user has permission to view
	 */
	public default java.util.List<MicroblogsEntry> filterFindByUserId(
		long userId, int start, int end) {

		return filterFindByUserId(userId, start, end, null);
	}

	/**
	 * Returns all the microblogs entries where userId = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @return the matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByU_T(
		long userId, int type) {

		return findByU_T(
			userId, type, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the microblogs entries where userId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByU_T(
		long userId, int type, int start, int end) {

		return findByU_T(userId, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the microblogs entries where userId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByU_T(
		long userId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator) {

		return findByU_T(userId, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the microblogs entries that the user has permission to view where userId = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @return the matching microblogs entries that the user has permission to view
	 */
	public default java.util.List<MicroblogsEntry> filterFindByU_T(
		long userId, int type) {

		return filterFindByU_T(
			userId, type, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the microblogs entries that the user has permission to view where userId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of matching microblogs entries that the user has permission to view
	 */
	public default java.util.List<MicroblogsEntry> filterFindByU_T(
		long userId, int type, int start, int end) {

		return filterFindByU_T(userId, type, start, end, null);
	}

	/**
	 * Returns all the microblogs entries where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @return the matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByT_P(
		int type, long parentMicroblogsEntryId) {

		return findByT_P(
			type, parentMicroblogsEntryId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the microblogs entries where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByT_P(
		int type, long parentMicroblogsEntryId, int start, int end) {

		return findByT_P(type, parentMicroblogsEntryId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the microblogs entries where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByT_P(
		int type, long parentMicroblogsEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator) {

		return findByT_P(
			type, parentMicroblogsEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the microblogs entries that the user has permission to view where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @return the matching microblogs entries that the user has permission to view
	 */
	public default java.util.List<MicroblogsEntry> filterFindByT_P(
		int type, long parentMicroblogsEntryId) {

		return filterFindByT_P(
			type, parentMicroblogsEntryId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the microblogs entries that the user has permission to view where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of matching microblogs entries that the user has permission to view
	 */
	public default java.util.List<MicroblogsEntry> filterFindByT_P(
		int type, long parentMicroblogsEntryId, int start, int end) {

		return filterFindByT_P(type, parentMicroblogsEntryId, start, end, null);
	}

	/**
	 * Returns all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @return the matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type) {

		return findByCCNI_CCPK_T(
			creatorClassNameId, creatorClassPK, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type, int start,
		int end) {

		return findByCCNI_CCPK_T(
			creatorClassNameId, creatorClassPK, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator) {

		return findByCCNI_CCPK_T(
			creatorClassNameId, creatorClassPK, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = any &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @return the matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByCCNI_CCPK_T(
		long creatorClassNameId, long[] creatorClassPKs, int type) {

		return findByCCNI_CCPK_T(
			creatorClassNameId, creatorClassPKs, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = any &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByCCNI_CCPK_T(
		long creatorClassNameId, long[] creatorClassPKs, int type, int start,
		int end) {

		return findByCCNI_CCPK_T(
			creatorClassNameId, creatorClassPKs, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = any &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByCCNI_CCPK_T(
		long creatorClassNameId, long[] creatorClassPKs, int type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator) {

		return findByCCNI_CCPK_T(
			creatorClassNameId, creatorClassPKs, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the microblogs entries that the user has permission to view where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @return the matching microblogs entries that the user has permission to view
	 */
	public default java.util.List<MicroblogsEntry> filterFindByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type) {

		return filterFindByCCNI_CCPK_T(
			creatorClassNameId, creatorClassPK, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the microblogs entries that the user has permission to view where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of matching microblogs entries that the user has permission to view
	 */
	public default java.util.List<MicroblogsEntry> filterFindByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type, int start,
		int end) {

		return filterFindByCCNI_CCPK_T(
			creatorClassNameId, creatorClassPK, type, start, end, null);
	}

	/**
	 * Returns all the microblogs entries that the user has permission to view where creatorClassNameId = &#63; and creatorClassPK = any &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @return the matching microblogs entries that the user has permission to view
	 */
	public default java.util.List<MicroblogsEntry> filterFindByCCNI_CCPK_T(
		long creatorClassNameId, long[] creatorClassPKs, int type) {

		return filterFindByCCNI_CCPK_T(
			creatorClassNameId, creatorClassPKs, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the microblogs entries that the user has permission to view where creatorClassNameId = &#63; and creatorClassPK = any &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of matching microblogs entries that the user has permission to view
	 */
	public default java.util.List<MicroblogsEntry> filterFindByCCNI_CCPK_T(
		long creatorClassNameId, long[] creatorClassPKs, int type, int start,
		int end) {

		return filterFindByCCNI_CCPK_T(
			creatorClassNameId, creatorClassPKs, type, start, end, null);
	}

	/**
	 * Returns all the microblogs entries where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @return the matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType) {

		return findByU_C_T_S(
			userId, createDate, type, socialRelationType,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the microblogs entries where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType,
		int start, int end) {

		return findByU_C_T_S(
			userId, createDate, type, socialRelationType, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the microblogs entries where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries
	 */
	public default java.util.List<MicroblogsEntry> findByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MicroblogsEntry>
			orderByComparator) {

		return findByU_C_T_S(
			userId, createDate, type, socialRelationType, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the microblogs entries that the user has permission to view where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @return the matching microblogs entries that the user has permission to view
	 */
	public default java.util.List<MicroblogsEntry> filterFindByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType) {

		return filterFindByU_C_T_S(
			userId, createDate, type, socialRelationType,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the microblogs entries that the user has permission to view where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of matching microblogs entries that the user has permission to view
	 */
	public default java.util.List<MicroblogsEntry> filterFindByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType,
		int start, int end) {

		return filterFindByU_C_T_S(
			userId, createDate, type, socialRelationType, start, end, null);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1357292936