/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.saml.persistence.exception.NoSuchIbSloMessageException;
import com.liferay.saml.persistence.model.SamlIbSloMessage;
import com.liferay.saml.persistence.model.SamlIbSloMessageTable;
import com.liferay.saml.persistence.model.impl.SamlIbSloMessageImpl;
import com.liferay.saml.persistence.model.impl.SamlIbSloMessageModelImpl;
import com.liferay.saml.persistence.service.persistence.SamlIbSloMessagePersistence;
import com.liferay.saml.persistence.service.persistence.SamlIbSloMessageUtil;
import com.liferay.saml.persistence.service.persistence.impl.constants.SamlPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the saml ib slo message service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @generated
 */
@Component(service = SamlIbSloMessagePersistence.class)
public class SamlIbSloMessagePersistenceImpl
	extends BasePersistenceImpl<SamlIbSloMessage>
	implements SamlIbSloMessagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SamlIbSloMessageUtil</code> to access the saml ib slo message persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SamlIbSloMessageImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchBySamlIdpSessionIndex;

	/**
	 * Returns the saml ib slo message where samlIdpSessionIndex = &#63; or throws a <code>NoSuchIbSloMessageException</code> if it could not be found.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @return the matching saml ib slo message
	 * @throws NoSuchIbSloMessageException if a matching saml ib slo message could not be found
	 */
	@Override
	public SamlIbSloMessage findBySamlIdpSessionIndex(
			String samlIdpSessionIndex)
		throws NoSuchIbSloMessageException {

		SamlIbSloMessage samlIbSloMessage = fetchBySamlIdpSessionIndex(
			samlIdpSessionIndex);

		if (samlIbSloMessage == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("samlIdpSessionIndex=");
			sb.append(samlIdpSessionIndex);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchIbSloMessageException(sb.toString());
		}

		return samlIbSloMessage;
	}

	/**
	 * Returns the saml ib slo message where samlIdpSessionIndex = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @return the matching saml ib slo message, or <code>null</code> if a matching saml ib slo message could not be found
	 */
	@Override
	public SamlIbSloMessage fetchBySamlIdpSessionIndex(
		String samlIdpSessionIndex) {

		return fetchBySamlIdpSessionIndex(samlIdpSessionIndex, true);
	}

	/**
	 * Returns the saml ib slo message where samlIdpSessionIndex = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saml ib slo message, or <code>null</code> if a matching saml ib slo message could not be found
	 */
	@Override
	public SamlIbSloMessage fetchBySamlIdpSessionIndex(
		String samlIdpSessionIndex, boolean useFinderCache) {

		samlIdpSessionIndex = Objects.toString(samlIdpSessionIndex, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {samlIdpSessionIndex};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchBySamlIdpSessionIndex, finderArgs, this);
		}

		if (result instanceof SamlIbSloMessage) {
			SamlIbSloMessage samlIbSloMessage = (SamlIbSloMessage)result;

			if (!Objects.equals(
					samlIdpSessionIndex,
					samlIbSloMessage.getSamlIdpSessionIndex())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_SAMLIBSLOMESSAGE_WHERE);

			boolean bindSamlIdpSessionIndex = false;

			if (samlIdpSessionIndex.isEmpty()) {
				sb.append(
					_FINDER_COLUMN_SAMLIDPSESSIONINDEX_SAMLIDPSESSIONINDEX_3);
			}
			else {
				bindSamlIdpSessionIndex = true;

				sb.append(
					_FINDER_COLUMN_SAMLIDPSESSIONINDEX_SAMLIDPSESSIONINDEX_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindSamlIdpSessionIndex) {
					queryPos.add(samlIdpSessionIndex);
				}

				List<SamlIbSloMessage> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchBySamlIdpSessionIndex, finderArgs,
							list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {samlIdpSessionIndex};
							}

							_log.warn(
								"SamlIbSloMessagePersistenceImpl.fetchBySamlIdpSessionIndex(String, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					SamlIbSloMessage samlIbSloMessage = list.get(0);

					result = samlIbSloMessage;

					cacheResult(samlIbSloMessage);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (SamlIbSloMessage)result;
		}
	}

	/**
	 * Removes the saml ib slo message where samlIdpSessionIndex = &#63; from the database.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @return the saml ib slo message that was removed
	 */
	@Override
	public SamlIbSloMessage removeBySamlIdpSessionIndex(
			String samlIdpSessionIndex)
		throws NoSuchIbSloMessageException {

		SamlIbSloMessage samlIbSloMessage = findBySamlIdpSessionIndex(
			samlIdpSessionIndex);

		return remove(samlIbSloMessage);
	}

	/**
	 * Returns the number of saml ib slo messages where samlIdpSessionIndex = &#63;.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @return the number of matching saml ib slo messages
	 */
	@Override
	public int countBySamlIdpSessionIndex(String samlIdpSessionIndex) {
		SamlIbSloMessage samlIbSloMessage = fetchBySamlIdpSessionIndex(
			samlIdpSessionIndex);

		if (samlIbSloMessage == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_SAMLIDPSESSIONINDEX_SAMLIDPSESSIONINDEX_2 =
			"samlIbSloMessage.samlIdpSessionIndex = ?";

	private static final String
		_FINDER_COLUMN_SAMLIDPSESSIONINDEX_SAMLIDPSESSIONINDEX_3 =
			"(samlIbSloMessage.samlIdpSessionIndex IS NULL OR samlIbSloMessage.samlIdpSessionIndex = '')";

	public SamlIbSloMessagePersistenceImpl() {
		setModelClass(SamlIbSloMessage.class);

		setModelImplClass(SamlIbSloMessageImpl.class);
		setModelPKClass(long.class);

		setTable(SamlIbSloMessageTable.INSTANCE);
	}

	/**
	 * Caches the saml ib slo message in the entity cache if it is enabled.
	 *
	 * @param samlIbSloMessage the saml ib slo message
	 */
	@Override
	public void cacheResult(SamlIbSloMessage samlIbSloMessage) {
		entityCache.putResult(
			SamlIbSloMessageImpl.class, samlIbSloMessage.getPrimaryKey(),
			samlIbSloMessage);

		finderCache.putResult(
			_finderPathFetchBySamlIdpSessionIndex,
			new Object[] {samlIbSloMessage.getSamlIdpSessionIndex()},
			samlIbSloMessage);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the saml ib slo messages in the entity cache if it is enabled.
	 *
	 * @param samlIbSloMessages the saml ib slo messages
	 */
	@Override
	public void cacheResult(List<SamlIbSloMessage> samlIbSloMessages) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (samlIbSloMessages.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (SamlIbSloMessage samlIbSloMessage : samlIbSloMessages) {
			if (entityCache.getResult(
					SamlIbSloMessageImpl.class,
					samlIbSloMessage.getPrimaryKey()) == null) {

				cacheResult(samlIbSloMessage);
			}
		}
	}

	/**
	 * Clears the cache for all saml ib slo messages.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(SamlIbSloMessageImpl.class);

		finderCache.clearCache(SamlIbSloMessageImpl.class);
	}

	/**
	 * Clears the cache for the saml ib slo message.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(SamlIbSloMessage samlIbSloMessage) {
		entityCache.removeResult(SamlIbSloMessageImpl.class, samlIbSloMessage);
	}

	@Override
	public void clearCache(List<SamlIbSloMessage> samlIbSloMessages) {
		for (SamlIbSloMessage samlIbSloMessage : samlIbSloMessages) {
			entityCache.removeResult(
				SamlIbSloMessageImpl.class, samlIbSloMessage);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(SamlIbSloMessageImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(SamlIbSloMessageImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		SamlIbSloMessageModelImpl samlIbSloMessageModelImpl) {

		Object[] args = new Object[] {
			samlIbSloMessageModelImpl.getSamlIdpSessionIndex()
		};

		finderCache.putResult(
			_finderPathFetchBySamlIdpSessionIndex, args,
			samlIbSloMessageModelImpl);
	}

	/**
	 * Creates a new saml ib slo message with the primary key. Does not add the saml ib slo message to the database.
	 *
	 * @param samlIbSloMessageId the primary key for the new saml ib slo message
	 * @return the new saml ib slo message
	 */
	@Override
	public SamlIbSloMessage create(long samlIbSloMessageId) {
		SamlIbSloMessage samlIbSloMessage = new SamlIbSloMessageImpl();

		samlIbSloMessage.setNew(true);
		samlIbSloMessage.setPrimaryKey(samlIbSloMessageId);

		samlIbSloMessage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return samlIbSloMessage;
	}

	/**
	 * Removes the saml ib slo message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlIbSloMessageId the primary key of the saml ib slo message
	 * @return the saml ib slo message that was removed
	 * @throws NoSuchIbSloMessageException if a saml ib slo message with the primary key could not be found
	 */
	@Override
	public SamlIbSloMessage remove(long samlIbSloMessageId)
		throws NoSuchIbSloMessageException {

		return remove((Serializable)samlIbSloMessageId);
	}

	/**
	 * Removes the saml ib slo message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the saml ib slo message
	 * @return the saml ib slo message that was removed
	 * @throws NoSuchIbSloMessageException if a saml ib slo message with the primary key could not be found
	 */
	@Override
	public SamlIbSloMessage remove(Serializable primaryKey)
		throws NoSuchIbSloMessageException {

		Session session = null;

		try {
			session = openSession();

			SamlIbSloMessage samlIbSloMessage = (SamlIbSloMessage)session.get(
				SamlIbSloMessageImpl.class, primaryKey);

			if (samlIbSloMessage == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchIbSloMessageException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(samlIbSloMessage);
		}
		catch (NoSuchIbSloMessageException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected SamlIbSloMessage removeImpl(SamlIbSloMessage samlIbSloMessage) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(samlIbSloMessage)) {
				samlIbSloMessage = (SamlIbSloMessage)session.get(
					SamlIbSloMessageImpl.class,
					samlIbSloMessage.getPrimaryKeyObj());
			}

			if (samlIbSloMessage != null) {
				session.delete(samlIbSloMessage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (samlIbSloMessage != null) {
			clearCache(samlIbSloMessage);
		}

		return samlIbSloMessage;
	}

	@Override
	public SamlIbSloMessage updateImpl(SamlIbSloMessage samlIbSloMessage) {
		boolean isNew = samlIbSloMessage.isNew();

		if (!(samlIbSloMessage instanceof SamlIbSloMessageModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(samlIbSloMessage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					samlIbSloMessage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in samlIbSloMessage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SamlIbSloMessage implementation " +
					samlIbSloMessage.getClass());
		}

		SamlIbSloMessageModelImpl samlIbSloMessageModelImpl =
			(SamlIbSloMessageModelImpl)samlIbSloMessage;

		if (isNew && (samlIbSloMessage.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				samlIbSloMessage.setCreateDate(date);
			}
			else {
				samlIbSloMessage.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(samlIbSloMessage);
			}
			else {
				samlIbSloMessage = (SamlIbSloMessage)session.merge(
					samlIbSloMessage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			SamlIbSloMessageImpl.class, samlIbSloMessageModelImpl, false, true);

		cacheUniqueFindersCache(samlIbSloMessageModelImpl);

		if (isNew) {
			samlIbSloMessage.setNew(false);
		}

		samlIbSloMessage.resetOriginalValues();

		return samlIbSloMessage;
	}

	/**
	 * Returns the saml ib slo message with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the saml ib slo message
	 * @return the saml ib slo message
	 * @throws NoSuchIbSloMessageException if a saml ib slo message with the primary key could not be found
	 */
	@Override
	public SamlIbSloMessage findByPrimaryKey(Serializable primaryKey)
		throws NoSuchIbSloMessageException {

		SamlIbSloMessage samlIbSloMessage = fetchByPrimaryKey(primaryKey);

		if (samlIbSloMessage == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchIbSloMessageException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return samlIbSloMessage;
	}

	/**
	 * Returns the saml ib slo message with the primary key or throws a <code>NoSuchIbSloMessageException</code> if it could not be found.
	 *
	 * @param samlIbSloMessageId the primary key of the saml ib slo message
	 * @return the saml ib slo message
	 * @throws NoSuchIbSloMessageException if a saml ib slo message with the primary key could not be found
	 */
	@Override
	public SamlIbSloMessage findByPrimaryKey(long samlIbSloMessageId)
		throws NoSuchIbSloMessageException {

		return findByPrimaryKey((Serializable)samlIbSloMessageId);
	}

	/**
	 * Returns the saml ib slo message with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlIbSloMessageId the primary key of the saml ib slo message
	 * @return the saml ib slo message, or <code>null</code> if a saml ib slo message with the primary key could not be found
	 */
	@Override
	public SamlIbSloMessage fetchByPrimaryKey(long samlIbSloMessageId) {
		return fetchByPrimaryKey((Serializable)samlIbSloMessageId);
	}

	/**
	 * Returns all the saml ib slo messages.
	 *
	 * @return the saml ib slo messages
	 */
	@Override
	public List<SamlIbSloMessage> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saml ib slo messages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIbSloMessageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml ib slo messages
	 * @param end the upper bound of the range of saml ib slo messages (not inclusive)
	 * @return the range of saml ib slo messages
	 */
	@Override
	public List<SamlIbSloMessage> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the saml ib slo messages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIbSloMessageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml ib slo messages
	 * @param end the upper bound of the range of saml ib slo messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of saml ib slo messages
	 */
	@Override
	public List<SamlIbSloMessage> findAll(
		int start, int end,
		OrderByComparator<SamlIbSloMessage> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saml ib slo messages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIbSloMessageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml ib slo messages
	 * @param end the upper bound of the range of saml ib slo messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of saml ib slo messages
	 */
	@Override
	public List<SamlIbSloMessage> findAll(
		int start, int end,
		OrderByComparator<SamlIbSloMessage> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<SamlIbSloMessage> list = null;

		if (useFinderCache) {
			list = (List<SamlIbSloMessage>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_SAMLIBSLOMESSAGE);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_SAMLIBSLOMESSAGE;

				sql = sql.concat(SamlIbSloMessageModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<SamlIbSloMessage>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the saml ib slo messages from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (SamlIbSloMessage samlIbSloMessage : findAll()) {
			remove(samlIbSloMessage);
		}
	}

	/**
	 * Returns the number of saml ib slo messages.
	 *
	 * @return the number of saml ib slo messages
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_SAMLIBSLOMESSAGE);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "samlIbSloMessageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SAMLIBSLOMESSAGE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SamlIbSloMessageModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the saml ib slo message persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathFetchBySamlIdpSessionIndex = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchBySamlIdpSessionIndex",
			new String[] {String.class.getName()},
			new String[] {"samlIdpSessionIndex"}, true);

		SamlIbSloMessageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SamlIbSloMessageUtil.setPersistence(null);

		entityCache.removeCache(SamlIbSloMessageImpl.class.getName());
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

	private static final String _SQL_SELECT_SAMLIBSLOMESSAGE =
		"SELECT samlIbSloMessage FROM SamlIbSloMessage samlIbSloMessage";

	private static final String _SQL_SELECT_SAMLIBSLOMESSAGE_WHERE =
		"SELECT samlIbSloMessage FROM SamlIbSloMessage samlIbSloMessage WHERE ";

	private static final String _SQL_COUNT_SAMLIBSLOMESSAGE =
		"SELECT COUNT(samlIbSloMessage) FROM SamlIbSloMessage samlIbSloMessage";

	private static final String _SQL_COUNT_SAMLIBSLOMESSAGE_WHERE =
		"SELECT COUNT(samlIbSloMessage) FROM SamlIbSloMessage samlIbSloMessage WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "samlIbSloMessage.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No SamlIbSloMessage exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SamlIbSloMessage exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SamlIbSloMessagePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}