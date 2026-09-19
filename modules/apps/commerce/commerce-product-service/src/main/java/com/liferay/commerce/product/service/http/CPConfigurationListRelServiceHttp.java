/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.http;

import com.liferay.commerce.product.service.CPConfigurationListRelServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CPConfigurationListRelServiceUtil</code> service
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
 * @author Marco Leo
 * @generated
 */
public class CPConfigurationListRelServiceHttp {

	public static com.liferay.commerce.product.model.CPConfigurationListRel
			addCPConfigurationListRel(
				HttpPrincipal httpPrincipal, String className, long classPK,
				long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"addCPConfigurationListRel",
				_addCPConfigurationListRelParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, className, classPK, cpConfigurationListId);

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

			return (com.liferay.commerce.product.model.CPConfigurationListRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPConfigurationListRel
			deleteCPConfigurationListRel(
				HttpPrincipal httpPrincipal,
				com.liferay.commerce.product.model.CPConfigurationListRel
					cpConfigurationListRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"deleteCPConfigurationListRel",
				_deleteCPConfigurationListRelParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationListRel);

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

			return (com.liferay.commerce.product.model.CPConfigurationListRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPConfigurationListRel
			deleteCPConfigurationListRel(
				HttpPrincipal httpPrincipal, long cpConfigurationListRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"deleteCPConfigurationListRel",
				_deleteCPConfigurationListRelParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationListRelId);

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

			return (com.liferay.commerce.product.model.CPConfigurationListRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteCPConfigurationListRels(
			HttpPrincipal httpPrincipal, long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"deleteCPConfigurationListRels",
				_deleteCPConfigurationListRelsParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationListId);

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

	public static void deleteCPConfigurationListRels(
			HttpPrincipal httpPrincipal, String className,
			long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"deleteCPConfigurationListRels",
				_deleteCPConfigurationListRelsParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, className, cpConfigurationListId);

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

	public static com.liferay.commerce.product.model.CPConfigurationListRel
			fetchCPConfigurationListRel(
				HttpPrincipal httpPrincipal, String className, long classPK,
				long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"fetchCPConfigurationListRel",
				_fetchCPConfigurationListRelParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, className, classPK, cpConfigurationListId);

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

			return (com.liferay.commerce.product.model.CPConfigurationListRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CPConfigurationListRel>
				getAccountEntryCPConfigurationListRels(
					HttpPrincipal httpPrincipal, long cpConfigurationListId,
					String keywords, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"getAccountEntryCPConfigurationListRels",
				_getAccountEntryCPConfigurationListRelsParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationListId, keywords, start, end);

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
				<com.liferay.commerce.product.model.CPConfigurationListRel>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getAccountEntryCPConfigurationListRelsCount(
			HttpPrincipal httpPrincipal, long cpConfigurationListId,
			String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"getAccountEntryCPConfigurationListRelsCount",
				_getAccountEntryCPConfigurationListRelsCountParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationListId, keywords);

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

	public static java.util.List
		<com.liferay.commerce.product.model.CPConfigurationListRel>
				getAccountGroupCPConfigurationListRels(
					HttpPrincipal httpPrincipal, long cpConfigurationListId,
					String keywords, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"getAccountGroupCPConfigurationListRels",
				_getAccountGroupCPConfigurationListRelsParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationListId, keywords, start, end);

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
				<com.liferay.commerce.product.model.CPConfigurationListRel>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getAccountGroupCPConfigurationListRelsCount(
			HttpPrincipal httpPrincipal, long cpConfigurationListId,
			String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"getAccountGroupCPConfigurationListRelsCount",
				_getAccountGroupCPConfigurationListRelsCountParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationListId, keywords);

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

	public static com.liferay.commerce.product.model.CPConfigurationListRel
			getCPConfigurationListRel(
				HttpPrincipal httpPrincipal, long cpConfigurationListRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"getCPConfigurationListRel",
				_getCPConfigurationListRelParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationListRelId);

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

			return (com.liferay.commerce.product.model.CPConfigurationListRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CPConfigurationListRel>
				getCPConfigurationListRels(
					HttpPrincipal httpPrincipal, long cpConfigurationListId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"getCPConfigurationListRels",
				_getCPConfigurationListRelsParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationListId);

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
				<com.liferay.commerce.product.model.CPConfigurationListRel>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CPConfigurationListRel>
				getCPConfigurationListRels(
					HttpPrincipal httpPrincipal, long cpConfigurationListId,
					int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.product.model.
							CPConfigurationListRel> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"getCPConfigurationListRels",
				_getCPConfigurationListRelsParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationListId, start, end,
				orderByComparator);

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
				<com.liferay.commerce.product.model.CPConfigurationListRel>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CPConfigurationListRel>
				getCPConfigurationListRels(
					HttpPrincipal httpPrincipal, String className,
					long cpConfigurationListId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"getCPConfigurationListRels",
				_getCPConfigurationListRelsParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, className, cpConfigurationListId);

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
				<com.liferay.commerce.product.model.CPConfigurationListRel>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CPConfigurationListRel>
				getCPConfigurationListRels(
					HttpPrincipal httpPrincipal, String className,
					long cpConfigurationListId, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.product.model.
							CPConfigurationListRel> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"getCPConfigurationListRels",
				_getCPConfigurationListRelsParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, className, cpConfigurationListId, start, end,
				orderByComparator);

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
				<com.liferay.commerce.product.model.CPConfigurationListRel>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCPConfigurationListRelsCount(
			HttpPrincipal httpPrincipal, long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"getCPConfigurationListRelsCount",
				_getCPConfigurationListRelsCountParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationListId);

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

	public static int getCPConfigurationListRelsCount(
			HttpPrincipal httpPrincipal, String className,
			long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"getCPConfigurationListRelsCount",
				_getCPConfigurationListRelsCountParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, className, cpConfigurationListId);

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

	public static java.util.List
		<com.liferay.commerce.product.model.CPConfigurationListRel>
				getCommerceOrderTypeCPConfigurationListRels(
					HttpPrincipal httpPrincipal, long cpConfigurationListId,
					String keywords, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"getCommerceOrderTypeCPConfigurationListRels",
				_getCommerceOrderTypeCPConfigurationListRelsParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationListId, keywords, start, end);

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
				<com.liferay.commerce.product.model.CPConfigurationListRel>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCommerceOrderTypeCPConfigurationListRelsCount(
			HttpPrincipal httpPrincipal, long cpConfigurationListId,
			String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationListRelServiceUtil.class,
				"getCommerceOrderTypeCPConfigurationListRelsCount",
				_getCommerceOrderTypeCPConfigurationListRelsCountParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationListId, keywords);

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

	private static Log _log = LogFactoryUtil.getLog(
		CPConfigurationListRelServiceHttp.class);

	private static final Class<?>[] _addCPConfigurationListRelParameterTypes0 =
		new Class[] {String.class, long.class, long.class};
	private static final Class<?>[]
		_deleteCPConfigurationListRelParameterTypes1 = new Class[] {
			com.liferay.commerce.product.model.CPConfigurationListRel.class
		};
	private static final Class<?>[]
		_deleteCPConfigurationListRelParameterTypes2 = new Class[] {long.class};
	private static final Class<?>[]
		_deleteCPConfigurationListRelsParameterTypes3 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_deleteCPConfigurationListRelsParameterTypes4 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[]
		_fetchCPConfigurationListRelParameterTypes5 = new Class[] {
			String.class, long.class, long.class
		};
	private static final Class<?>[]
		_getAccountEntryCPConfigurationListRelsParameterTypes6 = new Class[] {
			long.class, String.class, int.class, int.class
		};
	private static final Class<?>[]
		_getAccountEntryCPConfigurationListRelsCountParameterTypes7 =
			new Class[] {long.class, String.class};
	private static final Class<?>[]
		_getAccountGroupCPConfigurationListRelsParameterTypes8 = new Class[] {
			long.class, String.class, int.class, int.class
		};
	private static final Class<?>[]
		_getAccountGroupCPConfigurationListRelsCountParameterTypes9 =
			new Class[] {long.class, String.class};
	private static final Class<?>[] _getCPConfigurationListRelParameterTypes10 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getCPConfigurationListRelsParameterTypes11 = new Class[] {long.class};
	private static final Class<?>[]
		_getCPConfigurationListRelsParameterTypes12 = new Class[] {
			long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getCPConfigurationListRelsParameterTypes13 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[]
		_getCPConfigurationListRelsParameterTypes14 = new Class[] {
			String.class, long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getCPConfigurationListRelsCountParameterTypes15 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_getCPConfigurationListRelsCountParameterTypes16 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[]
		_getCommerceOrderTypeCPConfigurationListRelsParameterTypes17 =
			new Class[] {long.class, String.class, int.class, int.class};
	private static final Class<?>[]
		_getCommerceOrderTypeCPConfigurationListRelsCountParameterTypes18 =
			new Class[] {long.class, String.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1773196083