/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.http;

import com.liferay.account.service.AccountRoleServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>AccountRoleServiceUtil</code> service
 * utility. The
 * static methods of this class calls the same methods of the service utility.
 * However, the signatures are different because it requires an additional
 * <code>HttpPrincipal</code> parameter.
 *
 * <p>
 * The benefits of using the HTTP utility is that it is fast and allows for
 * tunneling without the cost of serializing to text. The drawback is that it
 * only works with Java.
 * </p>
 *
 * <p>
 * Set the property <b>tunnel.servlet.hosts.allowed</b> in portal.properties to
 * configure security.
 * </p>
 *
 * <p>
 * The HTTP utility is only generated for remote services.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AccountRoleServiceHttp {

	public static com.liferay.account.model.AccountRole addAccountRole(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long accountEntryId, String name,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountRoleServiceUtil.class, "addAccountRole",
				_addAccountRoleParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, accountEntryId, name,
				titleMap, descriptionMap);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.account.model.AccountRole)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void associateUser(
			HttpPrincipal httpPrincipal, long accountEntryId,
			long accountRoleId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountRoleServiceUtil.class, "associateUser",
				_associateUserParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, accountRoleId, userId);

			try {
				TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void associateUser(
			HttpPrincipal httpPrincipal, long accountEntryId,
			long[] accountRoleIds, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountRoleServiceUtil.class, "associateUser",
				_associateUserParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, accountRoleIds, userId);

			try {
				TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountRole deleteAccountRole(
			HttpPrincipal httpPrincipal,
			com.liferay.account.model.AccountRole accountRole)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountRoleServiceUtil.class, "deleteAccountRole",
				_deleteAccountRoleParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountRole);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.account.model.AccountRole)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountRole deleteAccountRole(
			HttpPrincipal httpPrincipal, long accountRoleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountRoleServiceUtil.class, "deleteAccountRole",
				_deleteAccountRoleParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountRoleId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.account.model.AccountRole)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountRole getAccountRoleByRoleId(
			HttpPrincipal httpPrincipal, long roleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountRoleServiceUtil.class, "getAccountRoleByRoleId",
				_getAccountRoleByRoleIdParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(methodKey, roleId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.account.model.AccountRole)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.account.model.AccountRole> searchAccountRoles(
				HttpPrincipal httpPrincipal, long companyId,
				long[] accountEntryIds, String keywords,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator<?>
					orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountRoleServiceUtil.class, "searchAccountRoles",
				_searchAccountRolesParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, accountEntryIds, keywords, params, start,
				end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.kernel.search.BaseModelSearchResult
				<com.liferay.account.model.AccountRole>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void setUserAccountRoles(
			HttpPrincipal httpPrincipal, long accountEntryId,
			long[] accountRoleIds, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountRoleServiceUtil.class, "setUserAccountRoles",
				_setUserAccountRolesParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, accountRoleIds, userId);

			try {
				TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void unassociateUser(
			HttpPrincipal httpPrincipal, long accountEntryId,
			long accountRoleId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountRoleServiceUtil.class, "unassociateUser",
				_unassociateUserParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, accountRoleId, userId);

			try {
				TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountRole updateAccountRole(
			HttpPrincipal httpPrincipal, long accountRoleId, String name,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountRoleServiceUtil.class, "updateAccountRole",
				_updateAccountRoleParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountRoleId, name, titleMap, descriptionMap);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.account.model.AccountRole)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		AccountRoleServiceHttp.class);

	private static final Class<?>[] _addAccountRoleParameterTypes0 =
		new Class[] {
			String.class, long.class, String.class, java.util.Map.class,
			java.util.Map.class
		};
	private static final Class<?>[] _associateUserParameterTypes1 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[] _associateUserParameterTypes2 =
		new Class[] {long.class, long[].class, long.class};
	private static final Class<?>[] _deleteAccountRoleParameterTypes3 =
		new Class[] {com.liferay.account.model.AccountRole.class};
	private static final Class<?>[] _deleteAccountRoleParameterTypes4 =
		new Class[] {long.class};
	private static final Class<?>[] _getAccountRoleByRoleIdParameterTypes5 =
		new Class[] {long.class};
	private static final Class<?>[] _searchAccountRolesParameterTypes6 =
		new Class[] {
			long.class, long[].class, String.class,
			java.util.LinkedHashMap.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _setUserAccountRolesParameterTypes7 =
		new Class[] {long.class, long[].class, long.class};
	private static final Class<?>[] _unassociateUserParameterTypes8 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[] _updateAccountRoleParameterTypes9 =
		new Class[] {
			long.class, String.class, java.util.Map.class, java.util.Map.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:548931147