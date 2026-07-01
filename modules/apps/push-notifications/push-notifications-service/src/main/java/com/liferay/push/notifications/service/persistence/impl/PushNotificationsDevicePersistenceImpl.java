/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.push.notifications.exception.NoSuchDeviceException;
import com.liferay.push.notifications.model.PushNotificationsDevice;
import com.liferay.push.notifications.model.PushNotificationsDeviceTable;
import com.liferay.push.notifications.model.impl.PushNotificationsDeviceImpl;
import com.liferay.push.notifications.model.impl.PushNotificationsDeviceModelImpl;
import com.liferay.push.notifications.service.persistence.PushNotificationsDevicePersistence;
import com.liferay.push.notifications.service.persistence.PushNotificationsDeviceUtil;
import com.liferay.push.notifications.service.persistence.impl.constants.PushNotificationsPersistenceConstants;

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
 * The persistence implementation for the push notifications device service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Bruno Farache
 * @generated
 */
@Component(service = PushNotificationsDevicePersistence.class)
public class PushNotificationsDevicePersistenceImpl
	extends BasePersistenceImpl<PushNotificationsDevice, NoSuchDeviceException>
	implements PushNotificationsDevicePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PushNotificationsDeviceUtil</code> to access the push notifications device persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PushNotificationsDeviceImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<PushNotificationsDevice, NoSuchDeviceException>
			_uniquePersistenceFinderByToken;

	/**
	 * Returns the push notifications device where token = &#63; or throws a <code>NoSuchDeviceException</code> if it could not be found.
	 *
	 * @param token the token
	 * @return the matching push notifications device
	 * @throws NoSuchDeviceException if a matching push notifications device could not be found
	 */
	@Override
	public PushNotificationsDevice findByToken(String token)
		throws NoSuchDeviceException {

		return _uniquePersistenceFinderByToken.find(
			finderCache, new Object[] {token});
	}

	/**
	 * Returns the push notifications device where token = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param token the token
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching push notifications device, or <code>null</code> if a matching push notifications device could not be found
	 */
	@Override
	public PushNotificationsDevice fetchByToken(
		String token, boolean useFinderCache) {

		return _uniquePersistenceFinderByToken.fetch(
			finderCache, new Object[] {token}, useFinderCache);
	}

	/**
	 * Removes the push notifications device where token = &#63; from the database.
	 *
	 * @param token the token
	 * @return the push notifications device that was removed
	 */
	@Override
	public PushNotificationsDevice removeByToken(String token)
		throws NoSuchDeviceException {

		PushNotificationsDevice pushNotificationsDevice = findByToken(token);

		return remove(pushNotificationsDevice);
	}

	/**
	 * Returns the number of push notifications devices where token = &#63;.
	 *
	 * @param token the token
	 * @return the number of matching push notifications devices
	 */
	@Override
	public int countByToken(String token) {
		return _uniquePersistenceFinderByToken.count(
			finderCache, new Object[] {token});
	}

	private CollectionPersistenceFinder
		<PushNotificationsDevice, NoSuchDeviceException>
			_collectionPersistenceFinderByU_P;

	/**
	 * Returns an ordered range of all the push notifications devices where userId = &#63; and platform = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PushNotificationsDeviceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param platform the platform
	 * @param start the lower bound of the range of push notifications devices
	 * @param end the upper bound of the range of push notifications devices (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching push notifications devices
	 */
	@Override
	public List<PushNotificationsDevice> findByU_P(
		long userId, String platform, int start, int end,
		OrderByComparator<PushNotificationsDevice> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_P.find(
			finderCache, new Object[] {new long[] {userId}, platform}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first push notifications device in the ordered set where userId = &#63; and platform = &#63;.
	 *
	 * @param userId the user ID
	 * @param platform the platform
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching push notifications device
	 * @throws NoSuchDeviceException if a matching push notifications device could not be found
	 */
	@Override
	public PushNotificationsDevice findByU_P_First(
			long userId, String platform,
			OrderByComparator<PushNotificationsDevice> orderByComparator)
		throws NoSuchDeviceException {

		PushNotificationsDevice pushNotificationsDevice = fetchByU_P_First(
			userId, platform, orderByComparator);

		if (pushNotificationsDevice != null) {
			return pushNotificationsDevice;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", platform=");
		sb.append(platform);

		sb.append("}");

		throw new NoSuchDeviceException(sb.toString());
	}

	/**
	 * Returns the first push notifications device in the ordered set where userId = &#63; and platform = &#63;.
	 *
	 * @param userId the user ID
	 * @param platform the platform
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching push notifications device, or <code>null</code> if a matching push notifications device could not be found
	 */
	@Override
	public PushNotificationsDevice fetchByU_P_First(
		long userId, String platform,
		OrderByComparator<PushNotificationsDevice> orderByComparator) {

		return _collectionPersistenceFinderByU_P.fetchFirst(
			finderCache, new Object[] {new long[] {userId}, platform},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the push notifications devices where userId = &#63; and platform = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PushNotificationsDeviceModelImpl</code>.
	 * </p>
	 *
	 * @param userIds the user IDs
	 * @param platform the platform
	 * @param start the lower bound of the range of push notifications devices
	 * @param end the upper bound of the range of push notifications devices (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching push notifications devices
	 */
	@Override
	public List<PushNotificationsDevice> findByU_P(
		long[] userIds, String platform, int start, int end,
		OrderByComparator<PushNotificationsDevice> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_P.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(userIds), platform}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the push notifications devices where userId = &#63; and platform = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param platform the platform
	 */
	@Override
	public void removeByU_P(long userId, String platform) {
		_collectionPersistenceFinderByU_P.remove(
			finderCache, new Object[] {new long[] {userId}, platform});
	}

	/**
	 * Returns the number of push notifications devices where userId = &#63; and platform = &#63;.
	 *
	 * @param userId the user ID
	 * @param platform the platform
	 * @return the number of matching push notifications devices
	 */
	@Override
	public int countByU_P(long userId, String platform) {
		return _collectionPersistenceFinderByU_P.count(
			finderCache, new Object[] {new long[] {userId}, platform});
	}

	/**
	 * Returns the number of push notifications devices where userId = any &#63; and platform = &#63;.
	 *
	 * @param userIds the user IDs
	 * @param platform the platform
	 * @return the number of matching push notifications devices
	 */
	@Override
	public int countByU_P(long[] userIds, String platform) {
		return _collectionPersistenceFinderByU_P.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(userIds), platform});
	}

	public PushNotificationsDevicePersistenceImpl() {
		setModelClass(PushNotificationsDevice.class);

		setModelImplClass(PushNotificationsDeviceImpl.class);
		setModelPKClass(long.class);

		setTable(PushNotificationsDeviceTable.INSTANCE);
	}

	/**
	 * Creates a new push notifications device with the primary key. Does not add the push notifications device to the database.
	 *
	 * @param pushNotificationsDeviceId the primary key for the new push notifications device
	 * @return the new push notifications device
	 */
	@Override
	public PushNotificationsDevice create(long pushNotificationsDeviceId) {
		PushNotificationsDevice pushNotificationsDevice =
			new PushNotificationsDeviceImpl();

		pushNotificationsDevice.setNew(true);
		pushNotificationsDevice.setPrimaryKey(pushNotificationsDeviceId);

		pushNotificationsDevice.setCompanyId(CompanyThreadLocal.getCompanyId());

		return pushNotificationsDevice;
	}

	/**
	 * Removes the push notifications device with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param pushNotificationsDeviceId the primary key of the push notifications device
	 * @return the push notifications device that was removed
	 * @throws NoSuchDeviceException if a push notifications device with the primary key could not be found
	 */
	@Override
	public PushNotificationsDevice remove(long pushNotificationsDeviceId)
		throws NoSuchDeviceException {

		return remove((Serializable)pushNotificationsDeviceId);
	}

	@Override
	protected PushNotificationsDevice removeImpl(
		PushNotificationsDevice pushNotificationsDevice) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(pushNotificationsDevice)) {
				pushNotificationsDevice = (PushNotificationsDevice)session.get(
					PushNotificationsDeviceImpl.class,
					pushNotificationsDevice.getPrimaryKeyObj());
			}

			if (pushNotificationsDevice != null) {
				session.delete(pushNotificationsDevice);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (pushNotificationsDevice != null) {
			clearCache(pushNotificationsDevice);
		}

		return pushNotificationsDevice;
	}

	@Override
	public PushNotificationsDevice updateImpl(
		PushNotificationsDevice pushNotificationsDevice) {

		boolean isNew = pushNotificationsDevice.isNew();

		if (!(pushNotificationsDevice instanceof
				PushNotificationsDeviceModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(pushNotificationsDevice.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					pushNotificationsDevice);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in pushNotificationsDevice proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PushNotificationsDevice implementation " +
					pushNotificationsDevice.getClass());
		}

		PushNotificationsDeviceModelImpl pushNotificationsDeviceModelImpl =
			(PushNotificationsDeviceModelImpl)pushNotificationsDevice;

		if (isNew && (pushNotificationsDevice.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				pushNotificationsDevice.setCreateDate(date);
			}
			else {
				pushNotificationsDevice.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(pushNotificationsDevice);
			}
			else {
				pushNotificationsDevice =
					(PushNotificationsDevice)session.merge(
						pushNotificationsDevice);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(pushNotificationsDevice, false);

		if (isNew) {
			pushNotificationsDevice.setNew(false);
		}

		pushNotificationsDevice.resetOriginalValues();

		return pushNotificationsDevice;
	}

	/**
	 * Returns the push notifications device with the primary key or throws a <code>NoSuchDeviceException</code> if it could not be found.
	 *
	 * @param pushNotificationsDeviceId the primary key of the push notifications device
	 * @return the push notifications device
	 * @throws NoSuchDeviceException if a push notifications device with the primary key could not be found
	 */
	@Override
	public PushNotificationsDevice findByPrimaryKey(
			long pushNotificationsDeviceId)
		throws NoSuchDeviceException {

		return findByPrimaryKey((Serializable)pushNotificationsDeviceId);
	}

	/**
	 * Returns the push notifications device with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param pushNotificationsDeviceId the primary key of the push notifications device
	 * @return the push notifications device, or <code>null</code> if a push notifications device with the primary key could not be found
	 */
	@Override
	public PushNotificationsDevice fetchByPrimaryKey(
		long pushNotificationsDeviceId) {

		return fetchByPrimaryKey((Serializable)pushNotificationsDeviceId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "pushNotificationsDeviceId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PUSHNOTIFICATIONSDEVICE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PushNotificationsDeviceModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the push notifications device persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByToken = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByToken",
				new String[] {String.class.getName()}, new String[] {"token"},
				0, 1, false,
				convertNullFunction(PushNotificationsDevice::getToken)),
			_SQL_SELECT_PUSHNOTIFICATIONSDEVICE_WHERE, "",
			new FinderColumn<>(
				"pushNotificationsDevice.", "token", FinderColumn.Type.STRING,
				"=", true, true, PushNotificationsDevice::getToken));

		_collectionPersistenceFinderByU_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_P",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId", "platform"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_P",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"userId", "platform"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByU_P",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"userId", "platform"}, 0, 2, false, null),
			_SQL_SELECT_PUSHNOTIFICATIONSDEVICE_WHERE,
			_SQL_COUNT_PUSHNOTIFICATIONSDEVICE_WHERE,
			PushNotificationsDeviceModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new ArrayableFinderColumn<>(
				"pushNotificationsDevice.", "userId", FinderColumn.Type.LONG,
				"=", false, true, true, PushNotificationsDevice::getUserId),
			new FinderColumn<>(
				"pushNotificationsDevice.", "platform",
				FinderColumn.Type.STRING, "=", true, true,
				PushNotificationsDevice::getPlatform));

		PushNotificationsDeviceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PushNotificationsDeviceUtil.setPersistence(null);

		entityCache.removeCache(PushNotificationsDeviceImpl.class.getName());
	}

	@Override
	@Reference(
		target = PushNotificationsPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = PushNotificationsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = PushNotificationsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		PushNotificationsDeviceModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PUSHNOTIFICATIONSDEVICE =
		"SELECT pushNotificationsDevice FROM PushNotificationsDevice pushNotificationsDevice";

	private static final String _SQL_SELECT_PUSHNOTIFICATIONSDEVICE_WHERE =
		"SELECT pushNotificationsDevice FROM PushNotificationsDevice pushNotificationsDevice WHERE ";

	private static final String _SQL_COUNT_PUSHNOTIFICATIONSDEVICE_WHERE =
		"SELECT COUNT(pushNotificationsDevice) FROM PushNotificationsDevice pushNotificationsDevice WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PushNotificationsDevice exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PushNotificationsDevicePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1444063332