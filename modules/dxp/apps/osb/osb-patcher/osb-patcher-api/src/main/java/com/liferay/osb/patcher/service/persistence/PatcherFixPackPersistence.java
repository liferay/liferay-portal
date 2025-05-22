/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the patcher fix pack service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixPackUtil
 * @generated
 */
@ProviderType
public interface PatcherFixPackPersistence
	extends BasePersistence<PatcherFixPack> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherFixPackUtil} to access the patcher fix pack persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the patcher fix pack where patcherBuildId = &#63; or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPatcherBuildId(long patcherBuildId)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the patcher fix pack where patcherBuildId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPatcherBuildId(long patcherBuildId);

	/**
	 * Returns the patcher fix pack where patcherBuildId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPatcherBuildId(
		long patcherBuildId, boolean useFinderCache);

	/**
	 * Removes the patcher fix pack where patcherBuildId = &#63; from the database.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the patcher fix pack that was removed
	 */
	public PatcherFixPack removeByPatcherBuildId(long patcherBuildId)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the number of patcher fix packs where patcherBuildId = &#63;.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the number of matching patcher fix packs
	 */
	public int countByPatcherBuildId(long patcherBuildId);

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @return the matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPatcherFixComponentId(
		long patcherFixComponentId);

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPatcherFixComponentId_First(
			long patcherFixComponentId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPatcherFixComponentId_First(
		long patcherFixComponentId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPatcherFixComponentId_Last(
			long patcherFixComponentId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPatcherFixComponentId_Last(
		long patcherFixComponentId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack[] findByPatcherFixComponentId_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPatcherFixComponentId(
		long patcherFixComponentId);

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack[] filterFindByPatcherFixComponentId_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 */
	public void removeByPatcherFixComponentId(long patcherFixComponentId);

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @return the number of matching patcher fix packs
	 */
	public int countByPatcherFixComponentId(long patcherFixComponentId);

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	public int filterCountByPatcherFixComponentId(long patcherFixComponentId);

	/**
	 * Returns all the patcher fix packs where version = &#63;.
	 *
	 * @param version the version
	 * @return the matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByVersion(int version);

	/**
	 * Returns a range of all the patcher fix packs where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByVersion(
		int version, int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix packs where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByVersion(
		int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher fix packs where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByVersion(
		int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByVersion_First(
			int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the first patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByVersion_First(
		int version,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the last patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByVersion_Last(
			int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the last patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByVersion_Last(
		int version,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack[] findByVersion_PrevAndNext(
			long patcherFixPackId, int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns all the patcher fix packs that the user has permission to view where version = &#63;.
	 *
	 * @param version the version
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByVersion(int version);

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByVersion(
		int version, int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByVersion(
		int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where version = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack[] filterFindByVersion_PrevAndNext(
			long patcherFixPackId, int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Removes all the patcher fix packs where version = &#63; from the database.
	 *
	 * @param version the version
	 */
	public void removeByVersion(int version);

	/**
	 * Returns the number of patcher fix packs where version = &#63;.
	 *
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	public int countByVersion(int version);

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where version = &#63;.
	 *
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	public int filterCountByVersion(int version);

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId);

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end);

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPFCI_PPVI_First(
			long patcherFixComponentId, long patcherProjectVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPFCI_PPVI_First(
		long patcherFixComponentId, long patcherProjectVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPFCI_PPVI_Last(
			long patcherFixComponentId, long patcherProjectVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPFCI_PPVI_Last(
		long patcherFixComponentId, long patcherProjectVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack[] findByPFCI_PPVI_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId);

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end);

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack[] filterFindByPFCI_PPVI_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 */
	public void removeByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId);

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher fix packs
	 */
	public int countByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId);

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	public int filterCountByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId);

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @return the matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_V(
		long patcherFixComponentId, int version);

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_V(
		long patcherFixComponentId, int version, int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_V(
		long patcherFixComponentId, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_V(
		long patcherFixComponentId, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPFCI_V_First(
			long patcherFixComponentId, int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPFCI_V_First(
		long patcherFixComponentId, int version,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPFCI_V_Last(
			long patcherFixComponentId, int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPFCI_V_Last(
		long patcherFixComponentId, int version,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack[] findByPFCI_V_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId, int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_V(
		long patcherFixComponentId, int version);

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_V(
		long patcherFixComponentId, int version, int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_V(
		long patcherFixComponentId, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack[] filterFindByPFCI_V_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId, int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 */
	public void removeByPFCI_V(long patcherFixComponentId, int version);

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	public int countByPFCI_V(long patcherFixComponentId, int version);

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	public int filterCountByPFCI_V(long patcherFixComponentId, int version);

	/**
	 * Returns the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPFCI_N(
			long patcherProjectVersionId, String name)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPFCI_N(
		long patcherProjectVersionId, String name);

	/**
	 * Returns the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPFCI_N(
		long patcherProjectVersionId, String name, boolean useFinderCache);

	/**
	 * Removes the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the patcher fix pack that was removed
	 */
	public PatcherFixPack removeByPFCI_N(
			long patcherProjectVersionId, String name)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the number of patcher fix packs where patcherProjectVersionId = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the number of matching patcher fix packs
	 */
	public int countByPFCI_N(long patcherProjectVersionId, String name);

	/**
	 * Returns all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @return the matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_S(
		long patcherProjectVersionId, int status);

	/**
	 * Returns a range of all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPFCI_S_First(
			long patcherProjectVersionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPFCI_S_First(
		long patcherProjectVersionId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPFCI_S_Last(
			long patcherProjectVersionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPFCI_S_Last(
		long patcherProjectVersionId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack[] findByPFCI_S_PrevAndNext(
			long patcherFixPackId, long patcherProjectVersionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_S(
		long patcherProjectVersionId, int status);

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack[] filterFindByPFCI_S_PrevAndNext(
			long patcherFixPackId, long patcherProjectVersionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Removes all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 */
	public void removeByPFCI_S(long patcherProjectVersionId, int status);

	/**
	 * Returns the number of patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @return the number of matching patcher fix packs
	 */
	public int countByPFCI_S(long patcherProjectVersionId, int status);

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	public int filterCountByPFCI_S(long patcherProjectVersionId, int status);

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version);

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPFCI_PPVI_GtV_First(
			long patcherFixComponentId, long patcherProjectVersionId,
			int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPFCI_PPVI_GtV_First(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPFCI_PPVI_GtV_Last(
			long patcherFixComponentId, long patcherProjectVersionId,
			int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPFCI_PPVI_GtV_Last(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack[] findByPFCI_PPVI_GtV_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId, int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version);

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack[] filterFindByPFCI_PPVI_GtV_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId, int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 */
	public void removeByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version);

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	public int countByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version);

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	public int filterCountByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version);

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version);

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPFCI_PPVI_LtV_First(
			long patcherFixComponentId, long patcherProjectVersionId,
			int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPFCI_PPVI_LtV_First(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPFCI_PPVI_LtV_Last(
			long patcherFixComponentId, long patcherProjectVersionId,
			int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPFCI_PPVI_LtV_Last(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack[] findByPFCI_PPVI_LtV_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId, int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version);

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	public java.util.List<PatcherFixPack> filterFindByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack[] filterFindByPFCI_PPVI_LtV_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId, int version,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
				orderByComparator)
		throws NoSuchPatcherFixPackException;

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 */
	public void removeByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version);

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	public int countByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version);

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	public int filterCountByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version);

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack findByPFCI_PPVI_N_V(
			long patcherFixComponentId, long patcherProjectVersionId,
			String name, int version)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId, String name,
		int version);

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public PatcherFixPack fetchByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId, String name,
		int version, boolean useFinderCache);

	/**
	 * Removes the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the patcher fix pack that was removed
	 */
	public PatcherFixPack removeByPFCI_PPVI_N_V(
			long patcherFixComponentId, long patcherProjectVersionId,
			String name, int version)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	public int countByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId, String name,
		int version);

	/**
	 * Caches the patcher fix pack in the entity cache if it is enabled.
	 *
	 * @param patcherFixPack the patcher fix pack
	 */
	public void cacheResult(PatcherFixPack patcherFixPack);

	/**
	 * Caches the patcher fix packs in the entity cache if it is enabled.
	 *
	 * @param patcherFixPacks the patcher fix packs
	 */
	public void cacheResult(java.util.List<PatcherFixPack> patcherFixPacks);

	/**
	 * Creates a new patcher fix pack with the primary key. Does not add the patcher fix pack to the database.
	 *
	 * @param patcherFixPackId the primary key for the new patcher fix pack
	 * @return the new patcher fix pack
	 */
	public PatcherFixPack create(long patcherFixPackId);

	/**
	 * Removes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack that was removed
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack remove(long patcherFixPackId)
		throws NoSuchPatcherFixPackException;

	public PatcherFixPack updateImpl(PatcherFixPack patcherFixPack);

	/**
	 * Returns the patcher fix pack with the primary key or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack findByPrimaryKey(long patcherFixPackId)
		throws NoSuchPatcherFixPackException;

	/**
	 * Returns the patcher fix pack with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack, or <code>null</code> if a patcher fix pack with the primary key could not be found
	 */
	public PatcherFixPack fetchByPrimaryKey(long patcherFixPackId);

	/**
	 * Returns all the patcher fix packs.
	 *
	 * @return the patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findAll();

	/**
	 * Returns a range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher fix packs
	 */
	public java.util.List<PatcherFixPack> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the patcher fix packs from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of patcher fix packs.
	 *
	 * @return the number of patcher fix packs
	 */
	public int countAll();

	/**
	 * Returns the primaryKeys of patcher fixes associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return long[] of the primaryKeys of patcher fixes associated with the patcher fix pack
	 */
	public long[] getPatcherFixPrimaryKeys(long pk);

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the patcher fix packs associated with the patcher fix
	 */
	public java.util.List<PatcherFixPack> getPatcherFixPatcherFixPacks(long pk);

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher fix packs associated with the patcher fix
	 */
	public java.util.List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long pk, int start, int end);

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix packs associated with the patcher fix
	 */
	public java.util.List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixPack>
			orderByComparator);

	/**
	 * Returns the number of patcher fixes associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return the number of patcher fixes associated with the patcher fix pack
	 */
	public int getPatcherFixesSize(long pk);

	/**
	 * Returns <code>true</code> if the patcher fix is associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if the patcher fix is associated with the patcher fix pack; <code>false</code> otherwise
	 */
	public boolean containsPatcherFix(long pk, long patcherFixPK);

	/**
	 * Returns <code>true</code> if the patcher fix pack has any patcher fixes associated with it.
	 *
	 * @param pk the primary key of the patcher fix pack to check for associations with patcher fixes
	 * @return <code>true</code> if the patcher fix pack has any patcher fixes associated with it; <code>false</code> otherwise
	 */
	public boolean containsPatcherFixes(long pk);

	/**
	 * Adds an association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if an association between the patcher fix pack and the patcher fix was added; <code>false</code> if they were already associated
	 */
	public boolean addPatcherFix(long pk, long patcherFixPK);

	/**
	 * Adds an association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFix the patcher fix
	 * @return <code>true</code> if an association between the patcher fix pack and the patcher fix was added; <code>false</code> if they were already associated
	 */
	public boolean addPatcherFix(
		long pk, com.liferay.osb.patcher.model.PatcherFix patcherFix);

	/**
	 * Adds an association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher fix pack and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	public boolean addPatcherFixes(long pk, long[] patcherFixPKs);

	/**
	 * Adds an association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixes the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher fix pack and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	public boolean addPatcherFixes(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes);

	/**
	 * Clears all associations between the patcher fix pack and its patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack to clear the associated patcher fixes from
	 */
	public void clearPatcherFixes(long pk);

	/**
	 * Removes the association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 */
	public void removePatcherFix(long pk, long patcherFixPK);

	/**
	 * Removes the association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFix the patcher fix
	 */
	public void removePatcherFix(
		long pk, com.liferay.osb.patcher.model.PatcherFix patcherFix);

	/**
	 * Removes the association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 */
	public void removePatcherFixes(long pk, long[] patcherFixPKs);

	/**
	 * Removes the association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixes the patcher fixes
	 */
	public void removePatcherFixes(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes);

	/**
	 * Sets the patcher fixes associated with the patcher fix pack, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes to be associated with the patcher fix pack
	 */
	public void setPatcherFixes(long pk, long[] patcherFixPKs);

	/**
	 * Sets the patcher fixes associated with the patcher fix pack, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixes the patcher fixes to be associated with the patcher fix pack
	 */
	public void setPatcherFixes(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes);

}