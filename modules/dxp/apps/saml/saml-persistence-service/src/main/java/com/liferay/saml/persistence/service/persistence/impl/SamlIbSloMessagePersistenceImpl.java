/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
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

import java.util.Date;
import java.util.Map;

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
	extends BasePersistenceImpl<SamlIbSloMessage, NoSuchIbSloMessageException>
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

	private UniquePersistenceFinder
		<SamlIbSloMessage, NoSuchIbSloMessageException>
			_uniquePersistenceFinderBySamlIdpSessionIndex;

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

		return _uniquePersistenceFinderBySamlIdpSessionIndex.find(
			finderCache, new Object[] {samlIdpSessionIndex});
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

		return _uniquePersistenceFinderBySamlIdpSessionIndex.fetch(
			finderCache, new Object[] {samlIdpSessionIndex}, useFinderCache);
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
		return _uniquePersistenceFinderBySamlIdpSessionIndex.count(
			finderCache, new Object[] {samlIdpSessionIndex});
	}

	public SamlIbSloMessagePersistenceImpl() {
		setModelClass(SamlIbSloMessage.class);

		setModelImplClass(SamlIbSloMessageImpl.class);
		setModelPKClass(long.class);

		setTable(SamlIbSloMessageTable.INSTANCE);
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

		cacheUniqueFindersResult(samlIbSloMessage, false);

		if (isNew) {
			samlIbSloMessage.setNew(false);
		}

		samlIbSloMessage.resetOriginalValues();

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
		_uniquePersistenceFinderBySamlIdpSessionIndex =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchBySamlIdpSessionIndex",
					new String[] {String.class.getName()},
					new String[] {"samlIdpSessionIndex"}, 0, 1, false,
					convertNullFunction(
						SamlIbSloMessage::getSamlIdpSessionIndex)),
				_SQL_SELECT_SAMLIBSLOMESSAGE_WHERE, "",
				new FinderColumn<>(
					"samlIbSloMessage.", "samlIdpSessionIndex",
					FinderColumn.Type.STRING, "=", true, true,
					SamlIbSloMessage::getSamlIdpSessionIndex));

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

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SamlIbSloMessage exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SamlIbSloMessagePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:521945372