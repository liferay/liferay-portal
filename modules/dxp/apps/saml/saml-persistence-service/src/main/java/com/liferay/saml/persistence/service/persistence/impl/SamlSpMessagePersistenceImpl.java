/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.saml.persistence.exception.NoSuchSpMessageException;
import com.liferay.saml.persistence.model.SamlSpMessage;
import com.liferay.saml.persistence.model.SamlSpMessageTable;
import com.liferay.saml.persistence.model.impl.SamlSpMessageImpl;
import com.liferay.saml.persistence.model.impl.SamlSpMessageModelImpl;
import com.liferay.saml.persistence.service.persistence.SamlSpMessagePersistence;
import com.liferay.saml.persistence.service.persistence.SamlSpMessageUtil;
import com.liferay.saml.persistence.service.persistence.impl.constants.SamlPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the saml sp message service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @generated
 */
@Component(service = SamlSpMessagePersistence.class)
public class SamlSpMessagePersistenceImpl
	extends BasePersistenceImpl<SamlSpMessage, NoSuchSpMessageException>
	implements SamlSpMessagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SamlSpMessageUtil</code> to access the saml sp message persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SamlSpMessageImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<SamlSpMessage, NoSuchSpMessageException>
		_collectionPersistenceFinderByLtExpirationDate;

	/**
	 * Returns all the saml sp messages where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the matching saml sp messages
	 */
	@Override
	public List<SamlSpMessage> findByLtExpirationDate(Date expirationDate) {
		return findByLtExpirationDate(
			expirationDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saml sp messages where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlSpMessageModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of saml sp messages
	 * @param end the upper bound of the range of saml sp messages (not inclusive)
	 * @return the range of matching saml sp messages
	 */
	@Override
	public List<SamlSpMessage> findByLtExpirationDate(
		Date expirationDate, int start, int end) {

		return findByLtExpirationDate(expirationDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saml sp messages where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlSpMessageModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of saml sp messages
	 * @param end the upper bound of the range of saml sp messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saml sp messages
	 */
	@Override
	public List<SamlSpMessage> findByLtExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<SamlSpMessage> orderByComparator) {

		return findByLtExpirationDate(
			expirationDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saml sp messages where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlSpMessageModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of saml sp messages
	 * @param end the upper bound of the range of saml sp messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml sp messages
	 */
	@Override
	public List<SamlSpMessage> findByLtExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<SamlSpMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtExpirationDate.find(
			finderCache, new Object[] {expirationDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first saml sp message in the ordered set where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml sp message
	 * @throws NoSuchSpMessageException if a matching saml sp message could not be found
	 */
	@Override
	public SamlSpMessage findByLtExpirationDate_First(
			Date expirationDate,
			OrderByComparator<SamlSpMessage> orderByComparator)
		throws NoSuchSpMessageException {

		return _collectionPersistenceFinderByLtExpirationDate.findFirst(
			finderCache, new Object[] {expirationDate}, orderByComparator);
	}

	/**
	 * Returns the first saml sp message in the ordered set where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml sp message, or <code>null</code> if a matching saml sp message could not be found
	 */
	@Override
	public SamlSpMessage fetchByLtExpirationDate_First(
		Date expirationDate,
		OrderByComparator<SamlSpMessage> orderByComparator) {

		return _collectionPersistenceFinderByLtExpirationDate.fetchFirst(
			finderCache, new Object[] {expirationDate}, orderByComparator);
	}

	/**
	 * Removes all the saml sp messages where expirationDate &lt; &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 */
	@Override
	public void removeByLtExpirationDate(Date expirationDate) {
		_collectionPersistenceFinderByLtExpirationDate.remove(
			finderCache, new Object[] {expirationDate});
	}

	/**
	 * Returns the number of saml sp messages where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the number of matching saml sp messages
	 */
	@Override
	public int countByLtExpirationDate(Date expirationDate) {
		return _collectionPersistenceFinderByLtExpirationDate.count(
			finderCache, new Object[] {expirationDate});
	}

	private UniquePersistenceFinder<SamlSpMessage, NoSuchSpMessageException>
		_uniquePersistenceFinderBySIEI_SIRK;

	/**
	 * Returns the saml sp message where samlIdpEntityId = &#63; and samlIdpResponseKey = &#63; or throws a <code>NoSuchSpMessageException</code> if it could not be found.
	 *
	 * @param samlIdpEntityId the saml idp entity ID
	 * @param samlIdpResponseKey the saml idp response key
	 * @return the matching saml sp message
	 * @throws NoSuchSpMessageException if a matching saml sp message could not be found
	 */
	@Override
	public SamlSpMessage findBySIEI_SIRK(
			String samlIdpEntityId, String samlIdpResponseKey)
		throws NoSuchSpMessageException {

		return _uniquePersistenceFinderBySIEI_SIRK.find(
			finderCache, new Object[] {samlIdpEntityId, samlIdpResponseKey});
	}

	/**
	 * Returns the saml sp message where samlIdpEntityId = &#63; and samlIdpResponseKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param samlIdpEntityId the saml idp entity ID
	 * @param samlIdpResponseKey the saml idp response key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saml sp message, or <code>null</code> if a matching saml sp message could not be found
	 */
	@Override
	public SamlSpMessage fetchBySIEI_SIRK(
		String samlIdpEntityId, String samlIdpResponseKey,
		boolean useFinderCache) {

		return _uniquePersistenceFinderBySIEI_SIRK.fetch(
			finderCache, new Object[] {samlIdpEntityId, samlIdpResponseKey},
			useFinderCache);
	}

	/**
	 * Removes the saml sp message where samlIdpEntityId = &#63; and samlIdpResponseKey = &#63; from the database.
	 *
	 * @param samlIdpEntityId the saml idp entity ID
	 * @param samlIdpResponseKey the saml idp response key
	 * @return the saml sp message that was removed
	 */
	@Override
	public SamlSpMessage removeBySIEI_SIRK(
			String samlIdpEntityId, String samlIdpResponseKey)
		throws NoSuchSpMessageException {

		SamlSpMessage samlSpMessage = findBySIEI_SIRK(
			samlIdpEntityId, samlIdpResponseKey);

		return remove(samlSpMessage);
	}

	/**
	 * Returns the number of saml sp messages where samlIdpEntityId = &#63; and samlIdpResponseKey = &#63;.
	 *
	 * @param samlIdpEntityId the saml idp entity ID
	 * @param samlIdpResponseKey the saml idp response key
	 * @return the number of matching saml sp messages
	 */
	@Override
	public int countBySIEI_SIRK(
		String samlIdpEntityId, String samlIdpResponseKey) {

		return _uniquePersistenceFinderBySIEI_SIRK.count(
			finderCache, new Object[] {samlIdpEntityId, samlIdpResponseKey});
	}

	public SamlSpMessagePersistenceImpl() {
		setModelClass(SamlSpMessage.class);

		setModelImplClass(SamlSpMessageImpl.class);
		setModelPKClass(long.class);

		setTable(SamlSpMessageTable.INSTANCE);
	}

	/**
	 * Creates a new saml sp message with the primary key. Does not add the saml sp message to the database.
	 *
	 * @param samlSpMessageId the primary key for the new saml sp message
	 * @return the new saml sp message
	 */
	@Override
	public SamlSpMessage create(long samlSpMessageId) {
		SamlSpMessage samlSpMessage = new SamlSpMessageImpl();

		samlSpMessage.setNew(true);
		samlSpMessage.setPrimaryKey(samlSpMessageId);

		samlSpMessage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return samlSpMessage;
	}

	/**
	 * Removes the saml sp message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlSpMessageId the primary key of the saml sp message
	 * @return the saml sp message that was removed
	 * @throws NoSuchSpMessageException if a saml sp message with the primary key could not be found
	 */
	@Override
	public SamlSpMessage remove(long samlSpMessageId)
		throws NoSuchSpMessageException {

		return remove((Serializable)samlSpMessageId);
	}

	@Override
	protected SamlSpMessage removeImpl(SamlSpMessage samlSpMessage) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(samlSpMessage)) {
				samlSpMessage = (SamlSpMessage)session.get(
					SamlSpMessageImpl.class, samlSpMessage.getPrimaryKeyObj());
			}

			if (samlSpMessage != null) {
				session.delete(samlSpMessage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (samlSpMessage != null) {
			clearCache(samlSpMessage);
		}

		return samlSpMessage;
	}

	@Override
	public SamlSpMessage updateImpl(SamlSpMessage samlSpMessage) {
		boolean isNew = samlSpMessage.isNew();

		if (!(samlSpMessage instanceof SamlSpMessageModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(samlSpMessage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					samlSpMessage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in samlSpMessage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SamlSpMessage implementation " +
					samlSpMessage.getClass());
		}

		SamlSpMessageModelImpl samlSpMessageModelImpl =
			(SamlSpMessageModelImpl)samlSpMessage;

		if (isNew && (samlSpMessage.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				samlSpMessage.setCreateDate(date);
			}
			else {
				samlSpMessage.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(samlSpMessage);
			}
			else {
				samlSpMessage = (SamlSpMessage)session.merge(samlSpMessage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(samlSpMessage, false);

		if (isNew) {
			samlSpMessage.setNew(false);
		}

		samlSpMessage.resetOriginalValues();

		return samlSpMessage;
	}

	/**
	 * Returns the saml sp message with the primary key or throws a <code>NoSuchSpMessageException</code> if it could not be found.
	 *
	 * @param samlSpMessageId the primary key of the saml sp message
	 * @return the saml sp message
	 * @throws NoSuchSpMessageException if a saml sp message with the primary key could not be found
	 */
	@Override
	public SamlSpMessage findByPrimaryKey(long samlSpMessageId)
		throws NoSuchSpMessageException {

		return findByPrimaryKey((Serializable)samlSpMessageId);
	}

	/**
	 * Returns the saml sp message with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlSpMessageId the primary key of the saml sp message
	 * @return the saml sp message, or <code>null</code> if a saml sp message with the primary key could not be found
	 */
	@Override
	public SamlSpMessage fetchByPrimaryKey(long samlSpMessageId) {
		return fetchByPrimaryKey((Serializable)samlSpMessageId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "samlSpMessageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SAMLSPMESSAGE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SamlSpMessageModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the saml sp message persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByLtExpirationDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLtExpirationDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"expirationDate"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByLtExpirationDate",
					new String[] {Date.class.getName()},
					new String[] {"expirationDate"}, false),
				_SQL_SELECT_SAMLSPMESSAGE_WHERE, _SQL_COUNT_SAMLSPMESSAGE_WHERE,
				SamlSpMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"samlSpMessage.", "expirationDate", FinderColumn.Type.DATE,
					"<", true, true, SamlSpMessage::getExpirationDate));

		_uniquePersistenceFinderBySIEI_SIRK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchBySIEI_SIRK",
				new String[] {String.class.getName(), String.class.getName()},
				new String[] {"samlIdpEntityId", "samlIdpResponseKey"}, 0, 3,
				false, convertNullFunction(SamlSpMessage::getSamlIdpEntityId),
				convertNullFunction(SamlSpMessage::getSamlIdpResponseKey)),
			_SQL_SELECT_SAMLSPMESSAGE_WHERE, "",
			new FinderColumn<>(
				"samlSpMessage.", "samlIdpEntityId", FinderColumn.Type.STRING,
				"=", true, true, SamlSpMessage::getSamlIdpEntityId),
			new FinderColumn<>(
				"samlSpMessage.", "samlIdpResponseKey",
				FinderColumn.Type.STRING, "=", true, true,
				SamlSpMessage::getSamlIdpResponseKey));

		SamlSpMessageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SamlSpMessageUtil.setPersistence(null);

		entityCache.removeCache(SamlSpMessageImpl.class.getName());
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		SamlSpMessageModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SAMLSPMESSAGE =
		"SELECT samlSpMessage FROM SamlSpMessage samlSpMessage";

	private static final String _SQL_SELECT_SAMLSPMESSAGE_WHERE =
		"SELECT samlSpMessage FROM SamlSpMessage samlSpMessage WHERE ";

	private static final String _SQL_COUNT_SAMLSPMESSAGE_WHERE =
		"SELECT COUNT(samlSpMessage) FROM SamlSpMessage samlSpMessage WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1092704059