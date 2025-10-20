/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.spring.orm.LastSessionRecorderHelperUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class CompanyThreadLocal {

	public static User fetchGuestUser() {
		Long companyId = _companyId.get();

		if (companyId == CompanyConstants.SYSTEM) {
			return null;
		}

		User guestUser = null;

		if (!isUpgradingPortalInstance()) {
			try {
				guestUser = UserLocalServiceUtil.fetchGuestUser(companyId);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		if (guestUser != null) {
			return guestUser;
		}

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select userId, languageId, timeZoneId from User_ where " +
					"companyId = ? and type_ = ?")) {

			preparedStatement.setLong(1, companyId);
			preparedStatement.setInt(2, UserConstants.TYPE_GUEST);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (!resultSet.next()) {
					return null;
				}

				guestUser = UserLocalServiceUtil.createUser(
					resultSet.getLong("userId"));

				guestUser.setLanguageId(resultSet.getString("languageId"));
				guestUser.setTimeZoneId(resultSet.getString("timeZoneId"));
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return guestUser;
	}

	public static Long getCompanyId() {
		Long companyId = _companyId.get();

		if (_log.isDebugEnabled()) {
			_log.debug("Get company ID " + companyId);
		}

		return companyId;
	}

	public static long getNonsystemCompanyId() {
		long companyId = _companyId.get();

		if (companyId == CompanyConstants.SYSTEM) {
			return PortalInstancePool.getDefaultCompanyId();
		}

		return companyId;
	}

	public static boolean isInitializingPortalInstance() {
		return _initializingPortalInstance.get();
	}

	public static boolean isLocked() {
		return _locked.get();
	}

	public static boolean isUpgradingPortalInstance() {
		return _upgradingPortalInstance.get();
	}

	public static SafeCloseable lock(long companyId) {
		long currentCompanyId = _companyId.get();

		if (companyId == currentCompanyId) {
			if (isLocked()) {
				return () -> {
				};
			}

			_locked.set(true);

			return () -> _locked.set(false);
		}

		if (isLocked()) {
			throw new UnsupportedOperationException(
				StringBundler.concat(
					"Company ID ", companyId, " and company ID ",
					currentCompanyId, " are different"));
		}

		_syncLastDBPartitionSessionState();

		SafeCloseable safeCloseable = _companyId.setWithSafeCloseable(
			companyId);

		_locked.set(true);

		return () -> {
			_locked.set(false);

			_syncLastDBPartitionSessionState();

			safeCloseable.close();
		};
	}

	public static void setCompanyId(Long companyId) {
		if (companyId.equals(_companyId.get())) {
			return;
		}

		if (isLocked()) {
			throw new UnsupportedOperationException(
				"CompanyThreadLocal modification is not allowed");
		}

		_syncLastDBPartitionSessionState();

		if (_log.isDebugEnabled()) {
			_log.debug("setCompanyId " + companyId);
		}

		if (companyId > 0) {
			_companyId.set(companyId);
		}
		else {
			_companyId.set(CompanyConstants.SYSTEM);
		}

		for (CompanyCentralizedThreadLocal<?> companyCentralizedThreadLocal :
				CompanyCentralizedThreadLocal.
					getCompanyCentralizedThreadLocals()) {

			companyCentralizedThreadLocal.remove();
		}

		CTCollectionThreadLocal.removeCTCollectionId();
	}

	public static SafeCloseable setCompanyIdWithSafeCloseable(Long companyId) {
		return setCompanyIdWithSafeCloseable(
			companyId, CTCollectionThreadLocal.CT_COLLECTION_ID_PRODUCTION);
	}

	public static SafeCloseable setCompanyIdWithSafeCloseable(
		Long companyId, Long ctCollectionId) {

		List<SafeCloseable> safeCloseables = new ArrayList<>();

		if (!companyId.equals(_companyId.get())) {
			if (isLocked()) {
				throw new UnsupportedOperationException(
					"CompanyThreadLocal modification is not allowed");
			}

			_syncLastDBPartitionSessionState();

			if (_log.isDebugEnabled()) {
				_log.debug("setCompanyId " + companyId);
			}

			if (companyId > 0) {
				safeCloseables.add(_companyId.setWithSafeCloseable(companyId));
			}
			else {
				safeCloseables.add(
					_companyId.setWithSafeCloseable(CompanyConstants.SYSTEM));
			}

			for (CompanyCentralizedThreadLocal<?>
					companyCentralizedThreadLocal :
						CompanyCentralizedThreadLocal.
							getCompanyCentralizedThreadLocals()) {

				safeCloseables.add(
					companyCentralizedThreadLocal.setWithSafeCloseable(null));

				companyCentralizedThreadLocal.remove();
			}
		}

		safeCloseables.add(
			CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
				ctCollectionId));

		return () -> {
			if (safeCloseables.size() > 1) {
				_syncLastDBPartitionSessionState();
			}

			for (SafeCloseable safeCloseable : safeCloseables) {
				safeCloseable.close();
			}
		};
	}

	public static SafeCloseable setInitializingCompanyIdWithSafeCloseable(
		long companyId) {

		if (companyId > 0) {
			return _companyId.setWithSafeCloseable(companyId);
		}

		return _companyId.setWithSafeCloseable(CompanyConstants.SYSTEM);
	}

	public static SafeCloseable setInitializingPortalInstanceWithSafeCloseable(
		boolean initializingPortalInstance) {

		return _initializingPortalInstance.setWithSafeCloseable(
			initializingPortalInstance);
	}

	public static SafeCloseable setUpgradingPortalInstanceWithSafeCloseable(
		boolean upgradingPortalInstance) {

		return _upgradingPortalInstance.setWithSafeCloseable(
			upgradingPortalInstance);
	}

	private static void _syncLastDBPartitionSessionState() {
		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			LastSessionRecorderHelperUtil.syncLastSessionState(false);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyThreadLocal.class);

	private static final CentralizedThreadLocal<Long> _companyId;
	private static final CentralizedThreadLocal<Boolean>
		_initializingPortalInstance = new CentralizedThreadLocal<>(
			CompanyThreadLocal.class + "._initializingPortalInstance",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean> _locked =
		new CentralizedThreadLocal<>(
			CompanyThreadLocal.class + "._locked", () -> Boolean.FALSE);
	private static final CentralizedThreadLocal<Boolean>
		_upgradingPortalInstance = new CentralizedThreadLocal<>(
			CompanyThreadLocal.class + "._upgradingPortalInstance",
			() -> Boolean.FALSE);

	static {
		_companyId = new CentralizedThreadLocal<>(
			CompanyThreadLocal.class + "._companyId",
			() -> CompanyConstants.SYSTEM);
	}

}