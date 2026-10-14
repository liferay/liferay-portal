/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.workflow.kaleo.exception.NoSuchInstanceException;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the kaleo instance service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KaleoInstanceUtil
 * @generated
 */
@ProviderType
public interface KaleoInstancePersistence
	extends BasePersistence<KaleoInstance>, CTPersistence<KaleoInstance> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link KaleoInstanceUtil} to access the kaleo instance persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the kaleo instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	public java.util.List<KaleoInstance> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kaleo instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	public KaleoInstance findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
				orderByComparator)
		throws NoSuchInstanceException;

	/**
	 * Returns the first kaleo instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	public KaleoInstance fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator);

	/**
	 * Removes all the kaleo instances where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of kaleo instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo instances
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	public java.util.List<KaleoInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	public KaleoInstance findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
				orderByComparator)
		throws NoSuchInstanceException;

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	public KaleoInstance fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator);

	/**
	 * Removes all the kaleo instances where kaleoDefinitionVersionId = &#63; from the database.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 */
	public void removeByKaleoDefinitionVersionId(long kaleoDefinitionVersionId);

	/**
	 * Returns the number of kaleo instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo instances
	 */
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId);

	/**
	 * Returns an ordered range of all the kaleo instances where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	public java.util.List<KaleoInstance> findByC_U(
		long companyId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kaleo instance in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	public KaleoInstance findByC_U_First(
			long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
				orderByComparator)
		throws NoSuchInstanceException;

	/**
	 * Returns the first kaleo instance in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	public KaleoInstance fetchByC_U_First(
		long companyId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator);

	/**
	 * Removes all the kaleo instances where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	public void removeByC_U(long companyId, long userId);

	/**
	 * Returns the number of kaleo instances where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching kaleo instances
	 */
	public int countByC_U(long companyId, long userId);

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	public java.util.List<KaleoInstance> findByKDI_C(
		long kaleoDefinitionId, boolean completed, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	public KaleoInstance findByKDI_C_First(
			long kaleoDefinitionId, boolean completed,
			com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
				orderByComparator)
		throws NoSuchInstanceException;

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	public KaleoInstance fetchByKDI_C_First(
		long kaleoDefinitionId, boolean completed,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator);

	/**
	 * Removes all the kaleo instances where kaleoDefinitionId = &#63; and completed = &#63; from the database.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 */
	public void removeByKDI_C(long kaleoDefinitionId, boolean completed);

	/**
	 * Returns the number of kaleo instances where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @return the number of matching kaleo instances
	 */
	public int countByKDI_C(long kaleoDefinitionId, boolean completed);

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	public java.util.List<KaleoInstance> findByKDVI_C(
		long kaleoDefinitionVersionId, boolean completed, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	public KaleoInstance findByKDVI_C_First(
			long kaleoDefinitionVersionId, boolean completed,
			com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
				orderByComparator)
		throws NoSuchInstanceException;

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	public KaleoInstance fetchByKDVI_C_First(
		long kaleoDefinitionVersionId, boolean completed,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator);

	/**
	 * Removes all the kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63; from the database.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 */
	public void removeByKDVI_C(
		long kaleoDefinitionVersionId, boolean completed);

	/**
	 * Returns the number of kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @return the number of matching kaleo instances
	 */
	public int countByKDVI_C(long kaleoDefinitionVersionId, boolean completed);

	/**
	 * Returns the kaleo instance where kaleoInstanceId = &#63; and companyId = &#63; and userId = &#63; or throws a <code>NoSuchInstanceException</code> if it could not be found.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	public KaleoInstance findByKII_C_U(
			long kaleoInstanceId, long companyId, long userId)
		throws NoSuchInstanceException;

	/**
	 * Returns the kaleo instance where kaleoInstanceId = &#63; and companyId = &#63; and userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	public KaleoInstance fetchByKII_C_U(
		long kaleoInstanceId, long companyId, long userId,
		boolean useFinderCache);

	/**
	 * Removes the kaleo instance where kaleoInstanceId = &#63; and companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the kaleo instance that was removed
	 */
	public KaleoInstance removeByKII_C_U(
			long kaleoInstanceId, long companyId, long userId)
		throws NoSuchInstanceException;

	/**
	 * Returns the number of kaleo instances where kaleoInstanceId = &#63; and companyId = &#63; and userId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching kaleo instances
	 */
	public int countByKII_C_U(
		long kaleoInstanceId, long companyId, long userId);

	/**
	 * Creates a new kaleo instance with the primary key. Does not add the kaleo instance to the database.
	 *
	 * @param kaleoInstanceId the primary key for the new kaleo instance
	 * @return the new kaleo instance
	 */
	public KaleoInstance create(long kaleoInstanceId);

	/**
	 * Removes the kaleo instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance that was removed
	 * @throws NoSuchInstanceException if a kaleo instance with the primary key could not be found
	 */
	public KaleoInstance remove(long kaleoInstanceId)
		throws NoSuchInstanceException;

	public KaleoInstance updateImpl(KaleoInstance kaleoInstance);

	/**
	 * Returns the kaleo instance with the primary key or throws a <code>NoSuchInstanceException</code> if it could not be found.
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance
	 * @throws NoSuchInstanceException if a kaleo instance with the primary key could not be found
	 */
	public KaleoInstance findByPrimaryKey(long kaleoInstanceId)
		throws NoSuchInstanceException;

	/**
	 * Returns the kaleo instance with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance, or <code>null</code> if a kaleo instance with the primary key could not be found
	 */
	public KaleoInstance fetchByPrimaryKey(long kaleoInstanceId);

	/**
	 * Returns the kaleo instance where kaleoInstanceId = &#63; and companyId = &#63; and userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	public default KaleoInstance fetchByKII_C_U(
		long kaleoInstanceId, long companyId, long userId) {

		return fetchByKII_C_U(kaleoInstanceId, companyId, userId, true);
	}

	/**
	 * Returns all the kaleo instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByCompanyId(
		long companyId) {

		return findByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @return the range of matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kaleo instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId) {

		return findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @return the range of matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end) {

		return findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator) {

		return findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kaleo instances where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByC_U(
		long companyId, long userId) {

		return findByC_U(
			companyId, userId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo instances where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @return the range of matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByC_U(
		long companyId, long userId, int start, int end) {

		return findByC_U(companyId, userId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByC_U(
		long companyId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator) {

		return findByC_U(
			companyId, userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kaleo instances where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @return the matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByKDI_C(
		long kaleoDefinitionId, boolean completed) {

		return findByKDI_C(
			kaleoDefinitionId, completed,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo instances where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @return the range of matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByKDI_C(
		long kaleoDefinitionId, boolean completed, int start, int end) {

		return findByKDI_C(
			kaleoDefinitionId, completed, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByKDI_C(
		long kaleoDefinitionId, boolean completed, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator) {

		return findByKDI_C(
			kaleoDefinitionId, completed, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @return the matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByKDVI_C(
		long kaleoDefinitionVersionId, boolean completed) {

		return findByKDVI_C(
			kaleoDefinitionVersionId, completed,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @return the range of matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByKDVI_C(
		long kaleoDefinitionVersionId, boolean completed, int start, int end) {

		return findByKDVI_C(
			kaleoDefinitionVersionId, completed, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo instances
	 */
	public default java.util.List<KaleoInstance> findByKDVI_C(
		long kaleoDefinitionVersionId, boolean completed, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator) {

		return findByKDVI_C(
			kaleoDefinitionVersionId, completed, start, end, orderByComparator,
			true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1882039428