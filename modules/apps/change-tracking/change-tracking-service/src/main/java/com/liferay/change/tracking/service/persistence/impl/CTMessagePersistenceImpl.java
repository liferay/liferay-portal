/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.impl;

import com.liferay.change.tracking.exception.NoSuchMessageException;
import com.liferay.change.tracking.model.CTMessage;
import com.liferay.change.tracking.model.CTMessageTable;
import com.liferay.change.tracking.model.impl.CTMessageImpl;
import com.liferay.change.tracking.model.impl.CTMessageModelImpl;
import com.liferay.change.tracking.service.persistence.CTMessagePersistence;
import com.liferay.change.tracking.service.persistence.CTMessageUtil;
import com.liferay.change.tracking.service.persistence.impl.constants.CTPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the ct message service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTMessagePersistence.class)
public class CTMessagePersistenceImpl
	extends BasePersistenceImpl<CTMessage, NoSuchMessageException>
	implements CTMessagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTMessageUtil</code> to access the ct message persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTMessageImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<CTMessage, NoSuchMessageException>
		_collectionPersistenceFinderByCtCollectionId;

	/**
	 * Returns an ordered range of all the ct messages where ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTMessageModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of ct messages
	 * @param end the upper bound of the range of ct messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct messages
	 */
	@Override
	public List<CTMessage> findByCtCollectionId(
		long ctCollectionId, int start, int end,
		OrderByComparator<CTMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCtCollectionId.find(
			finderCache, new Object[] {ctCollectionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ct message in the ordered set where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct message
	 * @throws NoSuchMessageException if a matching ct message could not be found
	 */
	@Override
	public CTMessage findByCtCollectionId_First(
			long ctCollectionId, OrderByComparator<CTMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByCtCollectionId.findFirst(
			finderCache, new Object[] {ctCollectionId}, orderByComparator);
	}

	/**
	 * Returns the first ct message in the ordered set where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct message, or <code>null</code> if a matching ct message could not be found
	 */
	@Override
	public CTMessage fetchByCtCollectionId_First(
		long ctCollectionId, OrderByComparator<CTMessage> orderByComparator) {

		return _collectionPersistenceFinderByCtCollectionId.fetchFirst(
			finderCache, new Object[] {ctCollectionId}, orderByComparator);
	}

	/**
	 * Removes all the ct messages where ctCollectionId = &#63; from the database.
	 *
	 * @param ctCollectionId the ct collection ID
	 */
	@Override
	public void removeByCtCollectionId(long ctCollectionId) {
		_collectionPersistenceFinderByCtCollectionId.remove(
			finderCache, new Object[] {ctCollectionId});
	}

	/**
	 * Returns the number of ct messages where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @return the number of matching ct messages
	 */
	@Override
	public int countByCtCollectionId(long ctCollectionId) {
		return _collectionPersistenceFinderByCtCollectionId.count(
			finderCache, new Object[] {ctCollectionId});
	}

	public CTMessagePersistenceImpl() {
		setModelClass(CTMessage.class);

		setModelImplClass(CTMessageImpl.class);
		setModelPKClass(long.class);

		setTable(CTMessageTable.INSTANCE);
	}

	/**
	 * Creates a new ct message with the primary key. Does not add the ct message to the database.
	 *
	 * @param ctMessageId the primary key for the new ct message
	 * @return the new ct message
	 */
	@Override
	public CTMessage create(long ctMessageId) {
		CTMessage ctMessage = new CTMessageImpl();

		ctMessage.setNew(true);
		ctMessage.setPrimaryKey(ctMessageId);

		ctMessage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctMessage;
	}

	/**
	 * Removes the ct message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctMessageId the primary key of the ct message
	 * @return the ct message that was removed
	 * @throws NoSuchMessageException if a ct message with the primary key could not be found
	 */
	@Override
	public CTMessage remove(long ctMessageId) throws NoSuchMessageException {
		return remove((Serializable)ctMessageId);
	}

	@Override
	protected CTMessage removeImpl(CTMessage ctMessage) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctMessage)) {
				ctMessage = (CTMessage)session.get(
					CTMessageImpl.class, ctMessage.getPrimaryKeyObj());
			}

			if (ctMessage != null) {
				session.delete(ctMessage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctMessage != null) {
			clearCache(ctMessage);
		}

		return ctMessage;
	}

	@Override
	public CTMessage updateImpl(CTMessage ctMessage) {
		boolean isNew = ctMessage.isNew();

		if (!(ctMessage instanceof CTMessageModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctMessage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ctMessage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctMessage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTMessage implementation " +
					ctMessage.getClass());
		}

		CTMessageModelImpl ctMessageModelImpl = (CTMessageModelImpl)ctMessage;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ctMessage);
			}
			else {
				ctMessage = (CTMessage)session.merge(ctMessage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ctMessage, false);

		if (isNew) {
			ctMessage.setNew(false);
		}

		ctMessage.resetOriginalValues();

		return ctMessage;
	}

	/**
	 * Returns the ct message with the primary key or throws a <code>NoSuchMessageException</code> if it could not be found.
	 *
	 * @param ctMessageId the primary key of the ct message
	 * @return the ct message
	 * @throws NoSuchMessageException if a ct message with the primary key could not be found
	 */
	@Override
	public CTMessage findByPrimaryKey(long ctMessageId)
		throws NoSuchMessageException {

		return findByPrimaryKey((Serializable)ctMessageId);
	}

	/**
	 * Returns the ct message with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctMessageId the primary key of the ct message
	 * @return the ct message, or <code>null</code> if a ct message with the primary key could not be found
	 */
	@Override
	public CTMessage fetchByPrimaryKey(long ctMessageId) {
		return fetchByPrimaryKey((Serializable)ctMessageId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "ctMessageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTMESSAGE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CTMessageModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the ct message persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCtCollectionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCtCollectionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"ctCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCtCollectionId", new String[] {Long.class.getName()},
					new String[] {"ctCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCtCollectionId",
					new String[] {Long.class.getName()},
					new String[] {"ctCollectionId"}, false),
				_SQL_SELECT_CTMESSAGE_WHERE, _SQL_COUNT_CTMESSAGE_WHERE,
				CTMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"ctMessage.", "ctCollectionId", FinderColumn.Type.LONG, "=",
					true, true, CTMessage::getCtCollectionId));

		CTMessageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTMessageUtil.setPersistence(null);

		entityCache.removeCache(CTMessageImpl.class.getName());
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CTMessageModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CTMESSAGE =
		"SELECT ctMessage FROM CTMessage ctMessage";

	private static final String _SQL_SELECT_CTMESSAGE_WHERE =
		"SELECT ctMessage FROM CTMessage ctMessage WHERE ";

	private static final String _SQL_COUNT_CTMESSAGE_WHERE =
		"SELECT COUNT(ctMessage) FROM CTMessage ctMessage WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1486607077