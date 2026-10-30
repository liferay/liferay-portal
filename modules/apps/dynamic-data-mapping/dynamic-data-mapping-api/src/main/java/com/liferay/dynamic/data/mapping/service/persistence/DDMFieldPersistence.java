/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence;

import com.liferay.dynamic.data.mapping.exception.NoSuchFieldException;
import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the ddm field service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMFieldUtil
 * @generated
 */
@ProviderType
public interface DDMFieldPersistence
	extends BasePersistence<DDMField>, CTPersistence<DDMField> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DDMFieldUtil} to access the ddm field persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the ddm fields where storageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm fields
	 */
	public java.util.List<DDMField> findByStorageId(
		long storageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm field in the ordered set where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field
	 * @throws NoSuchFieldException if a matching ddm field could not be found
	 */
	public DDMField findByStorageId_First(
			long storageId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMField>
				orderByComparator)
		throws NoSuchFieldException;

	/**
	 * Returns the first ddm field in the ordered set where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field, or <code>null</code> if a matching ddm field could not be found
	 */
	public DDMField fetchByStorageId_First(
		long storageId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMField>
			orderByComparator);

	/**
	 * Removes all the ddm fields where storageId = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 */
	public void removeByStorageId(long storageId);

	/**
	 * Returns the number of ddm fields where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @return the number of matching ddm fields
	 */
	public int countByStorageId(long storageId);

	/**
	 * Returns an ordered range of all the ddm fields where storageId = &#63; and fieldName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm fields
	 */
	public java.util.List<DDMField> findByS_F(
		long storageId, String fieldName, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMField>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm field in the ordered set where storageId = &#63; and fieldName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field
	 * @throws NoSuchFieldException if a matching ddm field could not be found
	 */
	public DDMField findByS_F_First(
			long storageId, String fieldName,
			com.liferay.portal.kernel.util.OrderByComparator<DDMField>
				orderByComparator)
		throws NoSuchFieldException;

	/**
	 * Returns the first ddm field in the ordered set where storageId = &#63; and fieldName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field, or <code>null</code> if a matching ddm field could not be found
	 */
	public DDMField fetchByS_F_First(
		long storageId, String fieldName,
		com.liferay.portal.kernel.util.OrderByComparator<DDMField>
			orderByComparator);

	/**
	 * Removes all the ddm fields where storageId = &#63; and fieldName = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 */
	public void removeByS_F(long storageId, String fieldName);

	/**
	 * Returns the number of ddm fields where storageId = &#63; and fieldName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @return the number of matching ddm fields
	 */
	public int countByS_F(long storageId, String fieldName);

	/**
	 * Returns the ddm field where storageId = &#63; and instanceId = &#63; or throws a <code>NoSuchFieldException</code> if it could not be found.
	 *
	 * @param storageId the storage ID
	 * @param instanceId the instance ID
	 * @return the matching ddm field
	 * @throws NoSuchFieldException if a matching ddm field could not be found
	 */
	public DDMField findByS_I(long storageId, String instanceId)
		throws NoSuchFieldException;

	/**
	 * Returns the ddm field where storageId = &#63; and instanceId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param storageId the storage ID
	 * @param instanceId the instance ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm field, or <code>null</code> if a matching ddm field could not be found
	 */
	public DDMField fetchByS_I(
		long storageId, String instanceId, boolean useFinderCache);

	/**
	 * Removes the ddm field where storageId = &#63; and instanceId = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 * @param instanceId the instance ID
	 * @return the ddm field that was removed
	 */
	public DDMField removeByS_I(long storageId, String instanceId)
		throws NoSuchFieldException;

	/**
	 * Returns the number of ddm fields where storageId = &#63; and instanceId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param instanceId the instance ID
	 * @return the number of matching ddm fields
	 */
	public int countByS_I(long storageId, String instanceId);

	/**
	 * Creates a new ddm field with the primary key. Does not add the ddm field to the database.
	 *
	 * @param fieldId the primary key for the new ddm field
	 * @return the new ddm field
	 */
	public DDMField create(long fieldId);

	/**
	 * Removes the ddm field with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fieldId the primary key of the ddm field
	 * @return the ddm field that was removed
	 * @throws NoSuchFieldException if a ddm field with the primary key could not be found
	 */
	public DDMField remove(long fieldId) throws NoSuchFieldException;

	public DDMField updateImpl(DDMField ddmField);

	/**
	 * Returns the ddm field with the primary key or throws a <code>NoSuchFieldException</code> if it could not be found.
	 *
	 * @param fieldId the primary key of the ddm field
	 * @return the ddm field
	 * @throws NoSuchFieldException if a ddm field with the primary key could not be found
	 */
	public DDMField findByPrimaryKey(long fieldId) throws NoSuchFieldException;

	/**
	 * Returns the ddm field with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fieldId the primary key of the ddm field
	 * @return the ddm field, or <code>null</code> if a ddm field with the primary key could not be found
	 */
	public DDMField fetchByPrimaryKey(long fieldId);

	/**
	 * Returns the ddm field where storageId = &#63; and instanceId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param storageId the storage ID
	 * @param instanceId the instance ID
	 * @return the matching ddm field, or <code>null</code> if a matching ddm field could not be found
	 */
	public default DDMField fetchByS_I(long storageId, String instanceId) {
		return fetchByS_I(storageId, instanceId, true);
	}

	/**
	 * Returns all the ddm fields where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @return the matching ddm fields
	 */
	public default java.util.List<DDMField> findByStorageId(long storageId) {
		return findByStorageId(
			storageId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm fields where storageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @return the range of matching ddm fields
	 */
	public default java.util.List<DDMField> findByStorageId(
		long storageId, int start, int end) {

		return findByStorageId(storageId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddm fields where storageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm fields
	 */
	public default java.util.List<DDMField> findByStorageId(
		long storageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMField>
			orderByComparator) {

		return findByStorageId(storageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the ddm fields where storageId = &#63; and fieldName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @return the matching ddm fields
	 */
	public default java.util.List<DDMField> findByS_F(
		long storageId, String fieldName) {

		return findByS_F(
			storageId, fieldName,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm fields where storageId = &#63; and fieldName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @return the range of matching ddm fields
	 */
	public default java.util.List<DDMField> findByS_F(
		long storageId, String fieldName, int start, int end) {

		return findByS_F(storageId, fieldName, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddm fields where storageId = &#63; and fieldName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm fields
	 */
	public default java.util.List<DDMField> findByS_F(
		long storageId, String fieldName, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMField>
			orderByComparator) {

		return findByS_F(
			storageId, fieldName, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1195670358