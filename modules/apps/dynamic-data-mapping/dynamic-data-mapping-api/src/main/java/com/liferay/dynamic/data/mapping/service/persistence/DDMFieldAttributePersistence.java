/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence;

import com.liferay.dynamic.data.mapping.exception.NoSuchFieldAttributeException;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the ddm field attribute service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMFieldAttributeUtil
 * @generated
 */
@ProviderType
public interface DDMFieldAttributePersistence
	extends BasePersistence<DDMFieldAttribute>,
			CTPersistence<DDMFieldAttribute> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DDMFieldAttributeUtil} to access the ddm field attribute persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm field attributes
	 */
	public java.util.List<DDMFieldAttribute> findByStorageId(
		long storageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFieldAttribute>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute
	 * @throws NoSuchFieldAttributeException if a matching ddm field attribute could not be found
	 */
	public DDMFieldAttribute findByStorageId_First(
			long storageId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMFieldAttribute>
				orderByComparator)
		throws NoSuchFieldAttributeException;

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	public DDMFieldAttribute fetchByStorageId_First(
		long storageId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFieldAttribute>
			orderByComparator);

	/**
	 * Removes all the ddm field attributes where storageId = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 */
	public void removeByStorageId(long storageId);

	/**
	 * Returns the number of ddm field attributes where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @return the number of matching ddm field attributes
	 */
	public int countByStorageId(long storageId);

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and attributeName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm field attributes
	 */
	public java.util.List<DDMFieldAttribute> findByS_AN(
		long storageId, String attributeName, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFieldAttribute>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63; and attributeName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute
	 * @throws NoSuchFieldAttributeException if a matching ddm field attribute could not be found
	 */
	public DDMFieldAttribute findByS_AN_First(
			long storageId, String attributeName,
			com.liferay.portal.kernel.util.OrderByComparator<DDMFieldAttribute>
				orderByComparator)
		throws NoSuchFieldAttributeException;

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63; and attributeName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	public DDMFieldAttribute fetchByS_AN_First(
		long storageId, String attributeName,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFieldAttribute>
			orderByComparator);

	/**
	 * Removes all the ddm field attributes where storageId = &#63; and attributeName = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 */
	public void removeByS_AN(long storageId, String attributeName);

	/**
	 * Returns the number of ddm field attributes where storageId = &#63; and attributeName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @return the number of matching ddm field attributes
	 */
	public int countByS_AN(long storageId, String attributeName);

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm field attributes
	 */
	public java.util.List<DDMFieldAttribute> findByS_L(
		long storageId, String languageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFieldAttribute>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63; and languageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute
	 * @throws NoSuchFieldAttributeException if a matching ddm field attribute could not be found
	 */
	public DDMFieldAttribute findByS_L_First(
			long storageId, String languageId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMFieldAttribute>
				orderByComparator)
		throws NoSuchFieldAttributeException;

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63; and languageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	public DDMFieldAttribute fetchByS_L_First(
		long storageId, String languageId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFieldAttribute>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and languageId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param languageIds the language IDs
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm field attributes
	 */
	public java.util.List<DDMFieldAttribute> findByS_L(
		long storageId, String[] languageIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFieldAttribute>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the ddm field attributes where storageId = &#63; and languageId = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 */
	public void removeByS_L(long storageId, String languageId);

	/**
	 * Returns the number of ddm field attributes where storageId = &#63; and languageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @return the number of matching ddm field attributes
	 */
	public int countByS_L(long storageId, String languageId);

	/**
	 * Returns the number of ddm field attributes where storageId = &#63; and languageId = any &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageIds the language IDs
	 * @return the number of matching ddm field attributes
	 */
	public int countByS_L(long storageId, String[] languageIds);

	/**
	 * Returns the ddm field attribute where fieldId = &#63; and attributeName = &#63; and languageId = &#63; or throws a <code>NoSuchFieldAttributeException</code> if it could not be found.
	 *
	 * @param fieldId the field ID
	 * @param attributeName the attribute name
	 * @param languageId the language ID
	 * @return the matching ddm field attribute
	 * @throws NoSuchFieldAttributeException if a matching ddm field attribute could not be found
	 */
	public DDMFieldAttribute findByF_AN_L(
			long fieldId, String attributeName, String languageId)
		throws NoSuchFieldAttributeException;

	/**
	 * Returns the ddm field attribute where fieldId = &#63; and attributeName = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param fieldId the field ID
	 * @param attributeName the attribute name
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	public DDMFieldAttribute fetchByF_AN_L(
		long fieldId, String attributeName, String languageId,
		boolean useFinderCache);

	/**
	 * Removes the ddm field attribute where fieldId = &#63; and attributeName = &#63; and languageId = &#63; from the database.
	 *
	 * @param fieldId the field ID
	 * @param attributeName the attribute name
	 * @param languageId the language ID
	 * @return the ddm field attribute that was removed
	 */
	public DDMFieldAttribute removeByF_AN_L(
			long fieldId, String attributeName, String languageId)
		throws NoSuchFieldAttributeException;

	/**
	 * Returns the number of ddm field attributes where fieldId = &#63; and attributeName = &#63; and languageId = &#63;.
	 *
	 * @param fieldId the field ID
	 * @param attributeName the attribute name
	 * @param languageId the language ID
	 * @return the number of matching ddm field attributes
	 */
	public int countByF_AN_L(
		long fieldId, String attributeName, String languageId);

	/**
	 * Creates a new ddm field attribute with the primary key. Does not add the ddm field attribute to the database.
	 *
	 * @param fieldAttributeId the primary key for the new ddm field attribute
	 * @return the new ddm field attribute
	 */
	public DDMFieldAttribute create(long fieldAttributeId);

	/**
	 * Removes the ddm field attribute with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fieldAttributeId the primary key of the ddm field attribute
	 * @return the ddm field attribute that was removed
	 * @throws NoSuchFieldAttributeException if a ddm field attribute with the primary key could not be found
	 */
	public DDMFieldAttribute remove(long fieldAttributeId)
		throws NoSuchFieldAttributeException;

	public DDMFieldAttribute updateImpl(DDMFieldAttribute ddmFieldAttribute);

	/**
	 * Returns the ddm field attribute with the primary key or throws a <code>NoSuchFieldAttributeException</code> if it could not be found.
	 *
	 * @param fieldAttributeId the primary key of the ddm field attribute
	 * @return the ddm field attribute
	 * @throws NoSuchFieldAttributeException if a ddm field attribute with the primary key could not be found
	 */
	public DDMFieldAttribute findByPrimaryKey(long fieldAttributeId)
		throws NoSuchFieldAttributeException;

	/**
	 * Returns the ddm field attribute with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fieldAttributeId the primary key of the ddm field attribute
	 * @return the ddm field attribute, or <code>null</code> if a ddm field attribute with the primary key could not be found
	 */
	public DDMFieldAttribute fetchByPrimaryKey(long fieldAttributeId);

	/**
	 * Returns the ddm field attribute where fieldId = &#63; and attributeName = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param fieldId the field ID
	 * @param attributeName the attribute name
	 * @param languageId the language ID
	 * @return the matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	public default DDMFieldAttribute fetchByF_AN_L(
		long fieldId, String attributeName, String languageId) {

		return fetchByF_AN_L(fieldId, attributeName, languageId, true);
	}

	/**
	 * Returns all the ddm field attributes where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @return the matching ddm field attributes
	 */
	public default java.util.List<DDMFieldAttribute> findByStorageId(
		long storageId) {

		return findByStorageId(
			storageId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm field attributes where storageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @return the range of matching ddm field attributes
	 */
	public default java.util.List<DDMFieldAttribute> findByStorageId(
		long storageId, int start, int end) {

		return findByStorageId(storageId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm field attributes
	 */
	public default java.util.List<DDMFieldAttribute> findByStorageId(
		long storageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFieldAttribute>
			orderByComparator) {

		return findByStorageId(storageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the ddm field attributes where storageId = &#63; and attributeName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @return the matching ddm field attributes
	 */
	public default java.util.List<DDMFieldAttribute> findByS_AN(
		long storageId, String attributeName) {

		return findByS_AN(
			storageId, attributeName,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm field attributes where storageId = &#63; and attributeName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @return the range of matching ddm field attributes
	 */
	public default java.util.List<DDMFieldAttribute> findByS_AN(
		long storageId, String attributeName, int start, int end) {

		return findByS_AN(storageId, attributeName, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and attributeName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm field attributes
	 */
	public default java.util.List<DDMFieldAttribute> findByS_AN(
		long storageId, String attributeName, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFieldAttribute>
			orderByComparator) {

		return findByS_AN(
			storageId, attributeName, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the ddm field attributes where storageId = &#63; and languageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @return the matching ddm field attributes
	 */
	public default java.util.List<DDMFieldAttribute> findByS_L(
		long storageId, String languageId) {

		return findByS_L(
			storageId, languageId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm field attributes where storageId = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @return the range of matching ddm field attributes
	 */
	public default java.util.List<DDMFieldAttribute> findByS_L(
		long storageId, String languageId, int start, int end) {

		return findByS_L(storageId, languageId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm field attributes
	 */
	public default java.util.List<DDMFieldAttribute> findByS_L(
		long storageId, String languageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFieldAttribute>
			orderByComparator) {

		return findByS_L(
			storageId, languageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the ddm field attributes where storageId = &#63; and languageId = any &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageIds the language IDs
	 * @return the matching ddm field attributes
	 */
	public default java.util.List<DDMFieldAttribute> findByS_L(
		long storageId, String[] languageIds) {

		return findByS_L(
			storageId, languageIds,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm field attributes where storageId = &#63; and languageId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param languageIds the language IDs
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @return the range of matching ddm field attributes
	 */
	public default java.util.List<DDMFieldAttribute> findByS_L(
		long storageId, String[] languageIds, int start, int end) {

		return findByS_L(storageId, languageIds, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and languageId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param languageIds the language IDs
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm field attributes
	 */
	public default java.util.List<DDMFieldAttribute> findByS_L(
		long storageId, String[] languageIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFieldAttribute>
			orderByComparator) {

		return findByS_L(
			storageId, languageIds, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1443914329