/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.external.data.source.test.service.persistence.impl;

import com.liferay.external.data.source.test.exception.NoSuchTestEntityException;
import com.liferay.external.data.source.test.model.TestEntity;
import com.liferay.external.data.source.test.model.TestEntityTable;
import com.liferay.external.data.source.test.model.impl.TestEntityImpl;
import com.liferay.external.data.source.test.model.impl.TestEntityModelImpl;
import com.liferay.external.data.source.test.service.persistence.TestEntityPersistence;
import com.liferay.external.data.source.test.service.persistence.TestEntityUtil;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the test entity service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class TestEntityPersistenceImpl
	extends BasePersistenceImpl<TestEntity, NoSuchTestEntityException>
	implements TestEntityPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>TestEntityUtil</code> to access the test entity persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		TestEntityImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public TestEntityPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("id", "id_");
		dbColumnNames.put("data", "data_");

		setDBColumnNames(dbColumnNames);

		setModelClass(TestEntity.class);

		setModelImplClass(TestEntityImpl.class);
		setModelPKClass(long.class);

		setTable(TestEntityTable.INSTANCE);
	}

	/**
	 * Creates a new test entity with the primary key. Does not add the test entity to the database.
	 *
	 * @param id the primary key for the new test entity
	 * @return the new test entity
	 */
	@Override
	public TestEntity create(long id) {
		TestEntity testEntity = new TestEntityImpl();

		testEntity.setNew(true);
		testEntity.setPrimaryKey(id);

		return testEntity;
	}

	/**
	 * Removes the test entity with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param id the primary key of the test entity
	 * @return the test entity that was removed
	 * @throws NoSuchTestEntityException if a test entity with the primary key could not be found
	 */
	@Override
	public TestEntity remove(long id) throws NoSuchTestEntityException {
		return remove((Serializable)id);
	}

	@Override
	protected TestEntity removeImpl(TestEntity testEntity) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(testEntity)) {
				testEntity = (TestEntity)session.get(
					TestEntityImpl.class, testEntity.getPrimaryKeyObj());
			}

			if (testEntity != null) {
				session.delete(testEntity);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (testEntity != null) {
			clearCache(testEntity);
		}

		return testEntity;
	}

	@Override
	public TestEntity updateImpl(TestEntity testEntity) {
		boolean isNew = testEntity.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(testEntity);
			}
			else {
				testEntity = (TestEntity)session.merge(testEntity);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(testEntity, false);

		if (isNew) {
			testEntity.setNew(false);
		}

		testEntity.resetOriginalValues();

		return testEntity;
	}

	/**
	 * Returns the test entity with the primary key or throws a <code>NoSuchTestEntityException</code> if it could not be found.
	 *
	 * @param id the primary key of the test entity
	 * @return the test entity
	 * @throws NoSuchTestEntityException if a test entity with the primary key could not be found
	 */
	@Override
	public TestEntity findByPrimaryKey(long id)
		throws NoSuchTestEntityException {

		return findByPrimaryKey((Serializable)id);
	}

	/**
	 * Returns the test entity with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param id the primary key of the test entity
	 * @return the test entity, or <code>null</code> if a test entity with the primary key could not be found
	 */
	@Override
	public TestEntity fetchByPrimaryKey(long id) {
		return fetchByPrimaryKey((Serializable)id);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "id_";
	}

	@Override
	protected String getPKFieldName() {
		return "id";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_TESTENTITY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return TestEntityModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the test entity persistence.
	 */
	public void afterPropertiesSet() {
		TestEntityUtil.setPersistence(this);
	}

	public void destroy() {
		TestEntityUtil.setPersistence(null);

		entityCache.removeCache(TestEntityImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_TESTENTITY =
		"SELECT testEntity FROM TestEntity testEntity";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"id", "data"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-399365217