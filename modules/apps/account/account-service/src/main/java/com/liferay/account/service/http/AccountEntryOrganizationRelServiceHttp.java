/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.http;

import com.liferay.account.service.AccountEntryOrganizationRelServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>AccountEntryOrganizationRelServiceUtil</code> service
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
public class AccountEntryOrganizationRelServiceHttp {

	public static com.liferay.account.model.AccountEntryOrganizationRel
			addAccountEntryOrganizationRel(
				HttpPrincipal httpPrincipal, long accountEntryId,
				long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryOrganizationRelServiceUtil.class,
				"addAccountEntryOrganizationRel",
				_addAccountEntryOrganizationRelParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, organizationId);

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

			return (com.liferay.account.model.AccountEntryOrganizationRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void addAccountEntryOrganizationRels(
			HttpPrincipal httpPrincipal, long accountEntryId,
			long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryOrganizationRelServiceUtil.class,
				"addAccountEntryOrganizationRels",
				_addAccountEntryOrganizationRelsParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, organizationIds);

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

	public static void deleteAccountEntryOrganizationRel(
			HttpPrincipal httpPrincipal, long accountEntryId,
			long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryOrganizationRelServiceUtil.class,
				"deleteAccountEntryOrganizationRel",
				_deleteAccountEntryOrganizationRelParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, organizationId);

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

	public static void deleteAccountEntryOrganizationRels(
			HttpPrincipal httpPrincipal, long accountEntryId,
			long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryOrganizationRelServiceUtil.class,
				"deleteAccountEntryOrganizationRels",
				_deleteAccountEntryOrganizationRelsParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, organizationIds);

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

	public static com.liferay.account.model.AccountEntryOrganizationRel
			fetchAccountEntryOrganizationRel(
				HttpPrincipal httpPrincipal, long accountEntryOrganizationRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryOrganizationRelServiceUtil.class,
				"fetchAccountEntryOrganizationRel",
				_fetchAccountEntryOrganizationRelParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryOrganizationRelId);

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

			return (com.liferay.account.model.AccountEntryOrganizationRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntryOrganizationRel
			fetchAccountEntryOrganizationRel(
				HttpPrincipal httpPrincipal, long accountEntryId,
				long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryOrganizationRelServiceUtil.class,
				"fetchAccountEntryOrganizationRel",
				_fetchAccountEntryOrganizationRelParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, organizationId);

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

			return (com.liferay.account.model.AccountEntryOrganizationRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.account.model.AccountEntryOrganizationRel
			getAccountEntryOrganizationRel(
				HttpPrincipal httpPrincipal, long accountEntryId,
				long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryOrganizationRelServiceUtil.class,
				"getAccountEntryOrganizationRel",
				_getAccountEntryOrganizationRelParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, organizationId);

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

			return (com.liferay.account.model.AccountEntryOrganizationRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.account.model.AccountEntryOrganizationRel>
				getAccountEntryOrganizationRels(
					HttpPrincipal httpPrincipal, long accountEntryId, int start,
					int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryOrganizationRelServiceUtil.class,
				"getAccountEntryOrganizationRels",
				_getAccountEntryOrganizationRelsParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, start, end);

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

			return (java.util.List
				<com.liferay.account.model.AccountEntryOrganizationRel>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getAccountEntryOrganizationRelsCount(
			HttpPrincipal httpPrincipal, long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryOrganizationRelServiceUtil.class,
				"getAccountEntryOrganizationRelsCount",
				_getAccountEntryOrganizationRelsCountParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId);

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

			return ((Integer)returnObj).intValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void setAccountEntryOrganizationRels(
			HttpPrincipal httpPrincipal, long accountEntryId,
			long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AccountEntryOrganizationRelServiceUtil.class,
				"setAccountEntryOrganizationRels",
				_setAccountEntryOrganizationRelsParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, organizationIds);

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

	private static Log _log = LogFactoryUtil.getLog(
		AccountEntryOrganizationRelServiceHttp.class);

	private static final Class<?>[]
		_addAccountEntryOrganizationRelParameterTypes0 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[]
		_addAccountEntryOrganizationRelsParameterTypes1 = new Class[] {
			long.class, long[].class
		};
	private static final Class<?>[]
		_deleteAccountEntryOrganizationRelParameterTypes2 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[]
		_deleteAccountEntryOrganizationRelsParameterTypes3 = new Class[] {
			long.class, long[].class
		};
	private static final Class<?>[]
		_fetchAccountEntryOrganizationRelParameterTypes4 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_fetchAccountEntryOrganizationRelParameterTypes5 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[]
		_getAccountEntryOrganizationRelParameterTypes6 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[]
		_getAccountEntryOrganizationRelsParameterTypes7 = new Class[] {
			long.class, int.class, int.class
		};
	private static final Class<?>[]
		_getAccountEntryOrganizationRelsCountParameterTypes8 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_setAccountEntryOrganizationRelsParameterTypes9 = new Class[] {
			long.class, long[].class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:663297925