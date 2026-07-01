/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchFieldException;
import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.dynamic.data.mapping.model.DDMFieldTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMFieldImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFieldPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFieldUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
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
 * The persistence implementation for the ddm field service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMFieldPersistence.class)
public class DDMFieldPersistenceImpl
	extends BasePersistenceImpl<DDMField, NoSuchFieldException>
	implements DDMFieldPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMFieldUtil</code> to access the ddm field persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMFieldImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<DDMField, NoSuchFieldException>
		_collectionPersistenceFinderByStorageId;

	/**
	 * Returns an ordered range of all the ddm fields where storageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm fields
	 */
	@Override
	public List<DDMField> findByStorageId(
		long storageId, int start, int end,
		OrderByComparator<DDMField> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByStorageId.find(
			finderCache, new Object[] {storageId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm field in the ordered set where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field
	 * @throws NoSuchFieldException if a matching ddm field could not be found
	 */
	@Override
	public DDMField findByStorageId_First(
			long storageId, OrderByComparator<DDMField> orderByComparator)
		throws NoSuchFieldException {

		return _collectionPersistenceFinderByStorageId.findFirst(
			finderCache, new Object[] {storageId}, orderByComparator);
	}

	/**
	 * Returns the first ddm field in the ordered set where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field, or <code>null</code> if a matching ddm field could not be found
	 */
	@Override
	public DDMField fetchByStorageId_First(
		long storageId, OrderByComparator<DDMField> orderByComparator) {

		return _collectionPersistenceFinderByStorageId.fetchFirst(
			finderCache, new Object[] {storageId}, orderByComparator);
	}

	/**
	 * Removes all the ddm fields where storageId = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 */
	@Override
	public void removeByStorageId(long storageId) {
		_collectionPersistenceFinderByStorageId.remove(
			finderCache, new Object[] {storageId});
	}

	/**
	 * Returns the number of ddm fields where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @return the number of matching ddm fields
	 */
	@Override
	public int countByStorageId(long storageId) {
		return _collectionPersistenceFinderByStorageId.count(
			finderCache, new Object[] {storageId});
	}

	private CollectionPersistenceFinder<DDMField, NoSuchFieldException>
		_collectionPersistenceFinderByStructureVersionId;

	/**
	 * Returns an ordered range of all the ddm fields where structureVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param structureVersionId the structure version ID
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm fields
	 */
	@Override
	public List<DDMField> findByStructureVersionId(
		long structureVersionId, int start, int end,
		OrderByComparator<DDMField> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByStructureVersionId.find(
			finderCache, new Object[] {structureVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm field in the ordered set where structureVersionId = &#63;.
	 *
	 * @param structureVersionId the structure version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field
	 * @throws NoSuchFieldException if a matching ddm field could not be found
	 */
	@Override
	public DDMField findByStructureVersionId_First(
			long structureVersionId,
			OrderByComparator<DDMField> orderByComparator)
		throws NoSuchFieldException {

		return _collectionPersistenceFinderByStructureVersionId.findFirst(
			finderCache, new Object[] {structureVersionId}, orderByComparator);
	}

	/**
	 * Returns the first ddm field in the ordered set where structureVersionId = &#63;.
	 *
	 * @param structureVersionId the structure version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field, or <code>null</code> if a matching ddm field could not be found
	 */
	@Override
	public DDMField fetchByStructureVersionId_First(
		long structureVersionId,
		OrderByComparator<DDMField> orderByComparator) {

		return _collectionPersistenceFinderByStructureVersionId.fetchFirst(
			finderCache, new Object[] {structureVersionId}, orderByComparator);
	}

	/**
	 * Removes all the ddm fields where structureVersionId = &#63; from the database.
	 *
	 * @param structureVersionId the structure version ID
	 */
	@Override
	public void removeByStructureVersionId(long structureVersionId) {
		_collectionPersistenceFinderByStructureVersionId.remove(
			finderCache, new Object[] {structureVersionId});
	}

	/**
	 * Returns the number of ddm fields where structureVersionId = &#63;.
	 *
	 * @param structureVersionId the structure version ID
	 * @return the number of matching ddm fields
	 */
	@Override
	public int countByStructureVersionId(long structureVersionId) {
		return _collectionPersistenceFinderByStructureVersionId.count(
			finderCache, new Object[] {structureVersionId});
	}

	private CollectionPersistenceFinder<DDMField, NoSuchFieldException>
		_collectionPersistenceFinderByC_F;

	/**
	 * Returns an ordered range of all the ddm fields where companyId = &#63; and fieldType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param fieldType the field type
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm fields
	 */
	@Override
	public List<DDMField> findByC_F(
		long companyId, String fieldType, int start, int end,
		OrderByComparator<DDMField> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_F.find(
			finderCache, new Object[] {companyId, fieldType}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm field in the ordered set where companyId = &#63; and fieldType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param fieldType the field type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field
	 * @throws NoSuchFieldException if a matching ddm field could not be found
	 */
	@Override
	public DDMField findByC_F_First(
			long companyId, String fieldType,
			OrderByComparator<DDMField> orderByComparator)
		throws NoSuchFieldException {

		return _collectionPersistenceFinderByC_F.findFirst(
			finderCache, new Object[] {companyId, fieldType},
			orderByComparator);
	}

	/**
	 * Returns the first ddm field in the ordered set where companyId = &#63; and fieldType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param fieldType the field type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field, or <code>null</code> if a matching ddm field could not be found
	 */
	@Override
	public DDMField fetchByC_F_First(
		long companyId, String fieldType,
		OrderByComparator<DDMField> orderByComparator) {

		return _collectionPersistenceFinderByC_F.fetchFirst(
			finderCache, new Object[] {companyId, fieldType},
			orderByComparator);
	}

	/**
	 * Removes all the ddm fields where companyId = &#63; and fieldType = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param fieldType the field type
	 */
	@Override
	public void removeByC_F(long companyId, String fieldType) {
		_collectionPersistenceFinderByC_F.remove(
			finderCache, new Object[] {companyId, fieldType});
	}

	/**
	 * Returns the number of ddm fields where companyId = &#63; and fieldType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param fieldType the field type
	 * @return the number of matching ddm fields
	 */
	@Override
	public int countByC_F(long companyId, String fieldType) {
		return _collectionPersistenceFinderByC_F.count(
			finderCache, new Object[] {companyId, fieldType});
	}

	private CollectionPersistenceFinder<DDMField, NoSuchFieldException>
		_collectionPersistenceFinderByS_F;

	/**
	 * Returns an ordered range of all the ddm fields where storageId = &#63; and fieldName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFieldModelImpl</code>.
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
	@Override
	public List<DDMField> findByS_F(
		long storageId, String fieldName, int start, int end,
		OrderByComparator<DDMField> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByS_F.find(
			finderCache, new Object[] {storageId, fieldName}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm field in the ordered set where storageId = &#63; and fieldName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field
	 * @throws NoSuchFieldException if a matching ddm field could not be found
	 */
	@Override
	public DDMField findByS_F_First(
			long storageId, String fieldName,
			OrderByComparator<DDMField> orderByComparator)
		throws NoSuchFieldException {

		return _collectionPersistenceFinderByS_F.findFirst(
			finderCache, new Object[] {storageId, fieldName},
			orderByComparator);
	}

	/**
	 * Returns the first ddm field in the ordered set where storageId = &#63; and fieldName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field, or <code>null</code> if a matching ddm field could not be found
	 */
	@Override
	public DDMField fetchByS_F_First(
		long storageId, String fieldName,
		OrderByComparator<DDMField> orderByComparator) {

		return _collectionPersistenceFinderByS_F.fetchFirst(
			finderCache, new Object[] {storageId, fieldName},
			orderByComparator);
	}

	/**
	 * Removes all the ddm fields where storageId = &#63; and fieldName = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 */
	@Override
	public void removeByS_F(long storageId, String fieldName) {
		_collectionPersistenceFinderByS_F.remove(
			finderCache, new Object[] {storageId, fieldName});
	}

	/**
	 * Returns the number of ddm fields where storageId = &#63; and fieldName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @return the number of matching ddm fields
	 */
	@Override
	public int countByS_F(long storageId, String fieldName) {
		return _collectionPersistenceFinderByS_F.count(
			finderCache, new Object[] {storageId, fieldName});
	}

	private UniquePersistenceFinder<DDMField, NoSuchFieldException>
		_uniquePersistenceFinderByS_I;

	/**
	 * Returns the ddm field where storageId = &#63; and instanceId = &#63; or throws a <code>NoSuchFieldException</code> if it could not be found.
	 *
	 * @param storageId the storage ID
	 * @param instanceId the instance ID
	 * @return the matching ddm field
	 * @throws NoSuchFieldException if a matching ddm field could not be found
	 */
	@Override
	public DDMField findByS_I(long storageId, String instanceId)
		throws NoSuchFieldException {

		return _uniquePersistenceFinderByS_I.find(
			finderCache, new Object[] {storageId, instanceId});
	}

	/**
	 * Returns the ddm field where storageId = &#63; and instanceId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param storageId the storage ID
	 * @param instanceId the instance ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm field, or <code>null</code> if a matching ddm field could not be found
	 */
	@Override
	public DDMField fetchByS_I(
		long storageId, String instanceId, boolean useFinderCache) {

		return _uniquePersistenceFinderByS_I.fetch(
			finderCache, new Object[] {storageId, instanceId}, useFinderCache);
	}

	/**
	 * Removes the ddm field where storageId = &#63; and instanceId = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 * @param instanceId the instance ID
	 * @return the ddm field that was removed
	 */
	@Override
	public DDMField removeByS_I(long storageId, String instanceId)
		throws NoSuchFieldException {

		DDMField ddmField = findByS_I(storageId, instanceId);

		return remove(ddmField);
	}

	/**
	 * Returns the number of ddm fields where storageId = &#63; and instanceId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param instanceId the instance ID
	 * @return the number of matching ddm fields
	 */
	@Override
	public int countByS_I(long storageId, String instanceId) {
		return _uniquePersistenceFinderByS_I.count(
			finderCache, new Object[] {storageId, instanceId});
	}

	public DDMFieldPersistenceImpl() {
		setModelClass(DDMField.class);

		setModelImplClass(DDMFieldImpl.class);
		setModelPKClass(long.class);

		setTable(DDMFieldTable.INSTANCE);
	}

	/**
	 * Creates a new ddm field with the primary key. Does not add the ddm field to the database.
	 *
	 * @param fieldId the primary key for the new ddm field
	 * @return the new ddm field
	 */
	@Override
	public DDMField create(long fieldId) {
		DDMField ddmField = new DDMFieldImpl();

		ddmField.setNew(true);
		ddmField.setPrimaryKey(fieldId);

		ddmField.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmField;
	}

	/**
	 * Removes the ddm field with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fieldId the primary key of the ddm field
	 * @return the ddm field that was removed
	 * @throws NoSuchFieldException if a ddm field with the primary key could not be found
	 */
	@Override
	public DDMField remove(long fieldId) throws NoSuchFieldException {
		return remove((Serializable)fieldId);
	}

	@Override
	protected DDMField removeImpl(DDMField ddmField) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmField)) {
				ddmField = (DDMField)session.get(
					DDMFieldImpl.class, ddmField.getPrimaryKeyObj());
			}

			if ((ddmField != null) && ctPersistenceHelper.isRemove(ddmField)) {
				session.delete(ddmField);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmField != null) {
			clearCache(ddmField);
		}

		return ddmField;
	}

	@Override
	public DDMField updateImpl(DDMField ddmField) {
		boolean isNew = ddmField.isNew();

		if (!(ddmField instanceof DDMFieldModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmField.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ddmField);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmField proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMField implementation " +
					ddmField.getClass());
		}

		DDMFieldModelImpl ddmFieldModelImpl = (DDMFieldModelImpl)ddmField;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmField)) {
				if (!isNew) {
					session.evict(
						DDMFieldImpl.class, ddmField.getPrimaryKeyObj());
				}

				session.save(ddmField);
			}
			else {
				ddmField = (DDMField)session.merge(ddmField);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmField, false);

		if (isNew) {
			ddmField.setNew(false);
		}

		ddmField.resetOriginalValues();

		return ddmField;
	}

	/**
	 * Returns the ddm field with the primary key or throws a <code>NoSuchFieldException</code> if it could not be found.
	 *
	 * @param fieldId the primary key of the ddm field
	 * @return the ddm field
	 * @throws NoSuchFieldException if a ddm field with the primary key could not be found
	 */
	@Override
	public DDMField findByPrimaryKey(long fieldId) throws NoSuchFieldException {
		return findByPrimaryKey((Serializable)fieldId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm field with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fieldId the primary key of the ddm field
	 * @return the ddm field, or <code>null</code> if a ddm field with the primary key could not be found
	 */
	@Override
	public DDMField fetchByPrimaryKey(long fieldId) {
		return fetchByPrimaryKey((Serializable)fieldId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "fieldId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMFIELD;
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
		return DDMFieldModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMField";
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
		ctMergeColumnNames.add("parentFieldId");
		ctMergeColumnNames.add("storageId");
		ctMergeColumnNames.add("structureVersionId");
		ctMergeColumnNames.add("fieldName");
		ctMergeColumnNames.add("fieldType");
		ctMergeColumnNames.add("instanceId");
		ctMergeColumnNames.add("localizable");
		ctMergeColumnNames.add("priority");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("fieldId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"storageId", "instanceId"});
	}

	/**
	 * Initializes the ddm field persistence.
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
				_SQL_SELECT_DDMFIELD_WHERE, _SQL_COUNT_DDMFIELD_WHERE,
				DDMFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"ddmField.", "storageId", FinderColumn.Type.LONG, "=", true,
					true, DDMField::getStorageId));

		_collectionPersistenceFinderByStructureVersionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByStructureVersionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"structureVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByStructureVersionId",
					new String[] {Long.class.getName()},
					new String[] {"structureVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByStructureVersionId",
					new String[] {Long.class.getName()},
					new String[] {"structureVersionId"}, false),
				_SQL_SELECT_DDMFIELD_WHERE, _SQL_COUNT_DDMFIELD_WHERE,
				DDMFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"ddmField.", "structureVersionId", FinderColumn.Type.LONG,
					"=", true, true, DDMField::getStructureVersionId));

		_collectionPersistenceFinderByC_F = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_F",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "fieldType"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "fieldType"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "fieldType"}, 0, 2, false, null),
			_SQL_SELECT_DDMFIELD_WHERE, _SQL_COUNT_DDMFIELD_WHERE,
			DDMFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"ddmField.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, DDMField::getCompanyId),
			new FinderColumn<>(
				"ddmField.", "fieldType", FinderColumn.Type.STRING, "=", true,
				true, DDMField::getFieldType));

		_collectionPersistenceFinderByS_F = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_F",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"storageId", "fieldName"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByS_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"storageId", "fieldName"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByS_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"storageId", "fieldName"}, 0, 2, false, null),
			_SQL_SELECT_DDMFIELD_WHERE, _SQL_COUNT_DDMFIELD_WHERE,
			DDMFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"ddmField.", "storageId", FinderColumn.Type.LONG, "=", true,
				true, DDMField::getStorageId),
			new FinderColumn<>(
				"ddmField.", "fieldName", FinderColumn.Type.STRING, "=", true,
				true, DDMField::getFieldName));

		_uniquePersistenceFinderByS_I = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByS_I",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"storageId", "instanceId"}, 0, 2, false,
				DDMField::getStorageId,
				convertNullFunction(DDMField::getInstanceId)),
			_SQL_SELECT_DDMFIELD_WHERE, "",
			new FinderColumn<>(
				"ddmField.", "storageId", FinderColumn.Type.LONG, "=", true,
				true, DDMField::getStorageId),
			new FinderColumn<>(
				"ddmField.", "instanceId", FinderColumn.Type.STRING, "=", true,
				true, DDMField::getInstanceId));

		DDMFieldUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMFieldUtil.setPersistence(null);

		entityCache.removeCache(DDMFieldImpl.class.getName());
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
		DDMFieldModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDMFIELD =
		"SELECT ddmField FROM DDMField ddmField";

	private static final String _SQL_SELECT_DDMFIELD_WHERE =
		"SELECT ddmField FROM DDMField ddmField WHERE ";

	private static final String _SQL_COUNT_DDMFIELD_WHERE =
		"SELECT COUNT(ddmField) FROM DDMField ddmField WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMField exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFieldPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1856272015