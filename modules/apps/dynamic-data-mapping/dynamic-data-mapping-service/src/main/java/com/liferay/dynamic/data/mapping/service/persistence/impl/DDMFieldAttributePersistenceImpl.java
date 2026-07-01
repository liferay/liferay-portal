/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchFieldAttributeException;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttributeTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFieldAttributePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFieldAttributeUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the ddm field attribute service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMFieldAttributePersistence.class)
public class DDMFieldAttributePersistenceImpl
	extends BasePersistenceImpl
		<DDMFieldAttribute, NoSuchFieldAttributeException>
	implements DDMFieldAttributePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMFieldAttributeUtil</code> to access the ddm field attribute persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMFieldAttributeImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DDMFieldAttribute, NoSuchFieldAttributeException>
			_collectionPersistenceFinderByStorageId;

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm field attributes
	 */
	@Override
	public List<DDMFieldAttribute> findByStorageId(
		long storageId, int start, int end,
		OrderByComparator<DDMFieldAttribute> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByStorageId.find(
			finderCache, new Object[] {storageId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute
	 * @throws NoSuchFieldAttributeException if a matching ddm field attribute could not be found
	 */
	@Override
	public DDMFieldAttribute findByStorageId_First(
			long storageId,
			OrderByComparator<DDMFieldAttribute> orderByComparator)
		throws NoSuchFieldAttributeException {

		return _collectionPersistenceFinderByStorageId.findFirst(
			finderCache, new Object[] {storageId}, orderByComparator);
	}

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	@Override
	public DDMFieldAttribute fetchByStorageId_First(
		long storageId,
		OrderByComparator<DDMFieldAttribute> orderByComparator) {

		return _collectionPersistenceFinderByStorageId.fetchFirst(
			finderCache, new Object[] {storageId}, orderByComparator);
	}

	/**
	 * Removes all the ddm field attributes where storageId = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 */
	@Override
	public void removeByStorageId(long storageId) {
		_collectionPersistenceFinderByStorageId.remove(
			finderCache, new Object[] {storageId});
	}

	/**
	 * Returns the number of ddm field attributes where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @return the number of matching ddm field attributes
	 */
	@Override
	public int countByStorageId(long storageId) {
		return _collectionPersistenceFinderByStorageId.count(
			finderCache, new Object[] {storageId});
	}

	private CollectionPersistenceFinder
		<DDMFieldAttribute, NoSuchFieldAttributeException>
			_collectionPersistenceFinderByS_AN;

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and attributeName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFieldAttributeModelImpl</code>.
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
	@Override
	public List<DDMFieldAttribute> findByS_AN(
		long storageId, String attributeName, int start, int end,
		OrderByComparator<DDMFieldAttribute> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByS_AN.find(
			finderCache, new Object[] {storageId, attributeName}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63; and attributeName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute
	 * @throws NoSuchFieldAttributeException if a matching ddm field attribute could not be found
	 */
	@Override
	public DDMFieldAttribute findByS_AN_First(
			long storageId, String attributeName,
			OrderByComparator<DDMFieldAttribute> orderByComparator)
		throws NoSuchFieldAttributeException {

		return _collectionPersistenceFinderByS_AN.findFirst(
			finderCache, new Object[] {storageId, attributeName},
			orderByComparator);
	}

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63; and attributeName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	@Override
	public DDMFieldAttribute fetchByS_AN_First(
		long storageId, String attributeName,
		OrderByComparator<DDMFieldAttribute> orderByComparator) {

		return _collectionPersistenceFinderByS_AN.fetchFirst(
			finderCache, new Object[] {storageId, attributeName},
			orderByComparator);
	}

	/**
	 * Removes all the ddm field attributes where storageId = &#63; and attributeName = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 */
	@Override
	public void removeByS_AN(long storageId, String attributeName) {
		_collectionPersistenceFinderByS_AN.remove(
			finderCache, new Object[] {storageId, attributeName});
	}

	/**
	 * Returns the number of ddm field attributes where storageId = &#63; and attributeName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @return the number of matching ddm field attributes
	 */
	@Override
	public int countByS_AN(long storageId, String attributeName) {
		return _collectionPersistenceFinderByS_AN.count(
			finderCache, new Object[] {storageId, attributeName});
	}

	private CollectionPersistenceFinder
		<DDMFieldAttribute, NoSuchFieldAttributeException>
			_collectionPersistenceFinderByS_L;

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFieldAttributeModelImpl</code>.
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
	@Override
	public List<DDMFieldAttribute> findByS_L(
		long storageId, String languageId, int start, int end,
		OrderByComparator<DDMFieldAttribute> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByS_L.find(
			finderCache, new Object[] {storageId, new String[] {languageId}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63; and languageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute
	 * @throws NoSuchFieldAttributeException if a matching ddm field attribute could not be found
	 */
	@Override
	public DDMFieldAttribute findByS_L_First(
			long storageId, String languageId,
			OrderByComparator<DDMFieldAttribute> orderByComparator)
		throws NoSuchFieldAttributeException {

		DDMFieldAttribute ddmFieldAttribute = fetchByS_L_First(
			storageId, languageId, orderByComparator);

		if (ddmFieldAttribute != null) {
			return ddmFieldAttribute;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("storageId=");
		sb.append(storageId);

		sb.append(", languageId=");
		sb.append(languageId);

		sb.append("}");

		throw new NoSuchFieldAttributeException(sb.toString());
	}

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63; and languageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	@Override
	public DDMFieldAttribute fetchByS_L_First(
		long storageId, String languageId,
		OrderByComparator<DDMFieldAttribute> orderByComparator) {

		return _collectionPersistenceFinderByS_L.fetchFirst(
			finderCache, new Object[] {storageId, new String[] {languageId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and languageId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFieldAttributeModelImpl</code>.
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
	@Override
	public List<DDMFieldAttribute> findByS_L(
		long storageId, String[] languageIds, int start, int end,
		OrderByComparator<DDMFieldAttribute> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByS_L.find(
			finderCache,
			new Object[] {storageId, ArrayUtil.sortedUnique(languageIds)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the ddm field attributes where storageId = &#63; and languageId = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 */
	@Override
	public void removeByS_L(long storageId, String languageId) {
		_collectionPersistenceFinderByS_L.remove(
			finderCache, new Object[] {storageId, new String[] {languageId}});
	}

	/**
	 * Returns the number of ddm field attributes where storageId = &#63; and languageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @return the number of matching ddm field attributes
	 */
	@Override
	public int countByS_L(long storageId, String languageId) {
		return _collectionPersistenceFinderByS_L.count(
			finderCache, new Object[] {storageId, new String[] {languageId}});
	}

	/**
	 * Returns the number of ddm field attributes where storageId = &#63; and languageId = any &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageIds the language IDs
	 * @return the number of matching ddm field attributes
	 */
	@Override
	public int countByS_L(long storageId, String[] languageIds) {
		return _collectionPersistenceFinderByS_L.count(
			finderCache,
			new Object[] {storageId, ArrayUtil.sortedUnique(languageIds)});
	}

	private CollectionPersistenceFinder
		<DDMFieldAttribute, NoSuchFieldAttributeException>
			_collectionPersistenceFinderByAN_SAV;

	/**
	 * Returns an ordered range of all the ddm field attributes where attributeName = &#63; and smallAttributeValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param attributeName the attribute name
	 * @param smallAttributeValue the small attribute value
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm field attributes
	 */
	@Override
	public List<DDMFieldAttribute> findByAN_SAV(
		String attributeName, String smallAttributeValue, int start, int end,
		OrderByComparator<DDMFieldAttribute> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAN_SAV.find(
			finderCache, new Object[] {attributeName, smallAttributeValue},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm field attribute in the ordered set where attributeName = &#63; and smallAttributeValue = &#63;.
	 *
	 * @param attributeName the attribute name
	 * @param smallAttributeValue the small attribute value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute
	 * @throws NoSuchFieldAttributeException if a matching ddm field attribute could not be found
	 */
	@Override
	public DDMFieldAttribute findByAN_SAV_First(
			String attributeName, String smallAttributeValue,
			OrderByComparator<DDMFieldAttribute> orderByComparator)
		throws NoSuchFieldAttributeException {

		return _collectionPersistenceFinderByAN_SAV.findFirst(
			finderCache, new Object[] {attributeName, smallAttributeValue},
			orderByComparator);
	}

	/**
	 * Returns the first ddm field attribute in the ordered set where attributeName = &#63; and smallAttributeValue = &#63;.
	 *
	 * @param attributeName the attribute name
	 * @param smallAttributeValue the small attribute value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	@Override
	public DDMFieldAttribute fetchByAN_SAV_First(
		String attributeName, String smallAttributeValue,
		OrderByComparator<DDMFieldAttribute> orderByComparator) {

		return _collectionPersistenceFinderByAN_SAV.fetchFirst(
			finderCache, new Object[] {attributeName, smallAttributeValue},
			orderByComparator);
	}

	/**
	 * Removes all the ddm field attributes where attributeName = &#63; and smallAttributeValue = &#63; from the database.
	 *
	 * @param attributeName the attribute name
	 * @param smallAttributeValue the small attribute value
	 */
	@Override
	public void removeByAN_SAV(
		String attributeName, String smallAttributeValue) {

		_collectionPersistenceFinderByAN_SAV.remove(
			finderCache, new Object[] {attributeName, smallAttributeValue});
	}

	/**
	 * Returns the number of ddm field attributes where attributeName = &#63; and smallAttributeValue = &#63;.
	 *
	 * @param attributeName the attribute name
	 * @param smallAttributeValue the small attribute value
	 * @return the number of matching ddm field attributes
	 */
	@Override
	public int countByAN_SAV(String attributeName, String smallAttributeValue) {
		return _collectionPersistenceFinderByAN_SAV.count(
			finderCache, new Object[] {attributeName, smallAttributeValue});
	}

	private UniquePersistenceFinder
		<DDMFieldAttribute, NoSuchFieldAttributeException>
			_uniquePersistenceFinderByF_AN_L;

	/**
	 * Returns the ddm field attribute where fieldId = &#63; and attributeName = &#63; and languageId = &#63; or throws a <code>NoSuchFieldAttributeException</code> if it could not be found.
	 *
	 * @param fieldId the field ID
	 * @param attributeName the attribute name
	 * @param languageId the language ID
	 * @return the matching ddm field attribute
	 * @throws NoSuchFieldAttributeException if a matching ddm field attribute could not be found
	 */
	@Override
	public DDMFieldAttribute findByF_AN_L(
			long fieldId, String attributeName, String languageId)
		throws NoSuchFieldAttributeException {

		return _uniquePersistenceFinderByF_AN_L.find(
			finderCache, new Object[] {fieldId, attributeName, languageId});
	}

	/**
	 * Returns the ddm field attribute where fieldId = &#63; and attributeName = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param fieldId the field ID
	 * @param attributeName the attribute name
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	@Override
	public DDMFieldAttribute fetchByF_AN_L(
		long fieldId, String attributeName, String languageId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByF_AN_L.fetch(
			finderCache, new Object[] {fieldId, attributeName, languageId},
			useFinderCache);
	}

	/**
	 * Removes the ddm field attribute where fieldId = &#63; and attributeName = &#63; and languageId = &#63; from the database.
	 *
	 * @param fieldId the field ID
	 * @param attributeName the attribute name
	 * @param languageId the language ID
	 * @return the ddm field attribute that was removed
	 */
	@Override
	public DDMFieldAttribute removeByF_AN_L(
			long fieldId, String attributeName, String languageId)
		throws NoSuchFieldAttributeException {

		DDMFieldAttribute ddmFieldAttribute = findByF_AN_L(
			fieldId, attributeName, languageId);

		return remove(ddmFieldAttribute);
	}

	/**
	 * Returns the number of ddm field attributes where fieldId = &#63; and attributeName = &#63; and languageId = &#63;.
	 *
	 * @param fieldId the field ID
	 * @param attributeName the attribute name
	 * @param languageId the language ID
	 * @return the number of matching ddm field attributes
	 */
	@Override
	public int countByF_AN_L(
		long fieldId, String attributeName, String languageId) {

		return _uniquePersistenceFinderByF_AN_L.count(
			finderCache, new Object[] {fieldId, attributeName, languageId});
	}

	public DDMFieldAttributePersistenceImpl() {
		setModelClass(DDMFieldAttribute.class);

		setModelImplClass(DDMFieldAttributeImpl.class);
		setModelPKClass(long.class);

		setTable(DDMFieldAttributeTable.INSTANCE);
	}

	/**
	 * Creates a new ddm field attribute with the primary key. Does not add the ddm field attribute to the database.
	 *
	 * @param fieldAttributeId the primary key for the new ddm field attribute
	 * @return the new ddm field attribute
	 */
	@Override
	public DDMFieldAttribute create(long fieldAttributeId) {
		DDMFieldAttribute ddmFieldAttribute = new DDMFieldAttributeImpl();

		ddmFieldAttribute.setNew(true);
		ddmFieldAttribute.setPrimaryKey(fieldAttributeId);

		ddmFieldAttribute.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmFieldAttribute;
	}

	/**
	 * Removes the ddm field attribute with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fieldAttributeId the primary key of the ddm field attribute
	 * @return the ddm field attribute that was removed
	 * @throws NoSuchFieldAttributeException if a ddm field attribute with the primary key could not be found
	 */
	@Override
	public DDMFieldAttribute remove(long fieldAttributeId)
		throws NoSuchFieldAttributeException {

		return remove((Serializable)fieldAttributeId);
	}

	@Override
	protected DDMFieldAttribute removeImpl(
		DDMFieldAttribute ddmFieldAttribute) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmFieldAttribute)) {
				ddmFieldAttribute = (DDMFieldAttribute)session.get(
					DDMFieldAttributeImpl.class,
					ddmFieldAttribute.getPrimaryKeyObj());
			}

			if ((ddmFieldAttribute != null) &&
				ctPersistenceHelper.isRemove(ddmFieldAttribute)) {

				session.delete(ddmFieldAttribute);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmFieldAttribute != null) {
			clearCache(ddmFieldAttribute);
		}

		return ddmFieldAttribute;
	}

	@Override
	public DDMFieldAttribute updateImpl(DDMFieldAttribute ddmFieldAttribute) {
		boolean isNew = ddmFieldAttribute.isNew();

		if (!(ddmFieldAttribute instanceof DDMFieldAttributeModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmFieldAttribute.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmFieldAttribute);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmFieldAttribute proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMFieldAttribute implementation " +
					ddmFieldAttribute.getClass());
		}

		DDMFieldAttributeModelImpl ddmFieldAttributeModelImpl =
			(DDMFieldAttributeModelImpl)ddmFieldAttribute;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmFieldAttribute)) {
				if (!isNew) {
					session.evict(
						DDMFieldAttributeImpl.class,
						ddmFieldAttribute.getPrimaryKeyObj());
				}

				session.save(ddmFieldAttribute);
			}
			else {
				ddmFieldAttribute = (DDMFieldAttribute)session.merge(
					ddmFieldAttribute);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmFieldAttribute, false);

		if (isNew) {
			ddmFieldAttribute.setNew(false);
		}

		ddmFieldAttribute.resetOriginalValues();

		return ddmFieldAttribute;
	}

	/**
	 * Returns the ddm field attribute with the primary key or throws a <code>NoSuchFieldAttributeException</code> if it could not be found.
	 *
	 * @param fieldAttributeId the primary key of the ddm field attribute
	 * @return the ddm field attribute
	 * @throws NoSuchFieldAttributeException if a ddm field attribute with the primary key could not be found
	 */
	@Override
	public DDMFieldAttribute findByPrimaryKey(long fieldAttributeId)
		throws NoSuchFieldAttributeException {

		return findByPrimaryKey((Serializable)fieldAttributeId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm field attribute with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fieldAttributeId the primary key of the ddm field attribute
	 * @return the ddm field attribute, or <code>null</code> if a ddm field attribute with the primary key could not be found
	 */
	@Override
	public DDMFieldAttribute fetchByPrimaryKey(long fieldAttributeId) {
		return fetchByPrimaryKey((Serializable)fieldAttributeId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "fieldAttributeId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMFIELDATTRIBUTE;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return DDMFieldAttributeModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMFieldAttribute";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("fieldId");
		ctMergeColumnNames.add("storageId");
		ctMergeColumnNames.add("attributeName");
		ctMergeColumnNames.add("languageId");
		ctMergeColumnNames.add("largeAttributeValue");
		ctMergeColumnNames.add("smallAttributeValue");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("fieldAttributeId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"fieldId", "attributeName", "languageId"});
	}

	/**
	 * Initializes the ddm field attribute persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByStorageId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByStorageId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"storageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByStorageId", new String[] {Long.class.getName()},
					new String[] {"storageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByStorageId", new String[] {Long.class.getName()},
					new String[] {"storageId"}, false),
				_SQL_SELECT_DDMFIELDATTRIBUTE_WHERE,
				_SQL_COUNT_DDMFIELDATTRIBUTE_WHERE,
				DDMFieldAttributeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"ddmFieldAttribute.", "storageId", FinderColumn.Type.LONG,
					"=", true, true, DDMFieldAttribute::getStorageId));

		_collectionPersistenceFinderByS_AN = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_AN",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"storageId", "attributeName"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByS_AN",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"storageId", "attributeName"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByS_AN",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"storageId", "attributeName"}, 0, 2, false, null),
			_SQL_SELECT_DDMFIELDATTRIBUTE_WHERE,
			_SQL_COUNT_DDMFIELDATTRIBUTE_WHERE,
			DDMFieldAttributeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"ddmFieldAttribute.", "storageId", FinderColumn.Type.LONG, "=",
				true, true, DDMFieldAttribute::getStorageId),
			new FinderColumn<>(
				"ddmFieldAttribute.", "attributeName", FinderColumn.Type.STRING,
				"=", true, true, DDMFieldAttribute::getAttributeName));

		_collectionPersistenceFinderByS_L = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_L",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"storageId", "languageId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByS_L",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"storageId", "languageId"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByS_L",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"storageId", "languageId"}, 0, 2, false, null),
			_SQL_SELECT_DDMFIELDATTRIBUTE_WHERE,
			_SQL_COUNT_DDMFIELDATTRIBUTE_WHERE,
			DDMFieldAttributeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"ddmFieldAttribute.", "storageId", FinderColumn.Type.LONG, "=",
				true, true, DDMFieldAttribute::getStorageId),
			new ArrayableFinderColumn<>(
				"ddmFieldAttribute.", "languageId", FinderColumn.Type.STRING,
				"=", false, true, true, DDMFieldAttribute::getLanguageId));

		_collectionPersistenceFinderByAN_SAV =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByAN_SAV",
					new String[] {
						String.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"attributeName", "smallAttributeValue"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByAN_SAV",
					new String[] {
						String.class.getName(), String.class.getName()
					},
					new String[] {"attributeName", "smallAttributeValue"}, 0, 3,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByAN_SAV",
					new String[] {
						String.class.getName(), String.class.getName()
					},
					new String[] {"attributeName", "smallAttributeValue"}, 0, 3,
					false, null),
				_SQL_SELECT_DDMFIELDATTRIBUTE_WHERE,
				_SQL_COUNT_DDMFIELDATTRIBUTE_WHERE,
				DDMFieldAttributeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"ddmFieldAttribute.", "attributeName",
					FinderColumn.Type.STRING, "=", true, true,
					DDMFieldAttribute::getAttributeName),
				new FinderColumn<>(
					"ddmFieldAttribute.", "smallAttributeValue",
					FinderColumn.Type.STRING, "=", true, true,
					DDMFieldAttribute::getSmallAttributeValue));

		_uniquePersistenceFinderByF_AN_L = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByF_AN_L",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"fieldId", "attributeName", "languageId"}, 0, 6,
				false, DDMFieldAttribute::getFieldId,
				convertNullFunction(DDMFieldAttribute::getAttributeName),
				convertNullFunction(DDMFieldAttribute::getLanguageId)),
			_SQL_SELECT_DDMFIELDATTRIBUTE_WHERE, "",
			new FinderColumn<>(
				"ddmFieldAttribute.", "fieldId", FinderColumn.Type.LONG, "=",
				true, true, DDMFieldAttribute::getFieldId),
			new FinderColumn<>(
				"ddmFieldAttribute.", "attributeName", FinderColumn.Type.STRING,
				"=", true, true, DDMFieldAttribute::getAttributeName),
			new FinderColumn<>(
				"ddmFieldAttribute.", "languageId", FinderColumn.Type.STRING,
				"=", true, true, DDMFieldAttribute::getLanguageId));

		DDMFieldAttributeUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMFieldAttributeUtil.setPersistence(null);

		entityCache.removeCache(DDMFieldAttributeImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		DDMFieldAttributeModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDMFIELDATTRIBUTE =
		"SELECT ddmFieldAttribute FROM DDMFieldAttribute ddmFieldAttribute";

	private static final String _SQL_SELECT_DDMFIELDATTRIBUTE_WHERE =
		"SELECT ddmFieldAttribute FROM DDMFieldAttribute ddmFieldAttribute WHERE ";

	private static final String _SQL_COUNT_DDMFIELDATTRIBUTE_WHERE =
		"SELECT COUNT(ddmFieldAttribute) FROM DDMFieldAttribute ddmFieldAttribute WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMFieldAttribute exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFieldAttributePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1095286178