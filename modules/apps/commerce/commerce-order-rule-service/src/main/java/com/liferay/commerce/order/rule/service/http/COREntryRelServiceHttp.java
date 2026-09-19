/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.service.http;

import com.liferay.commerce.order.rule.service.COREntryRelServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>COREntryRelServiceUtil</code> service
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
 * @author Luca Pellizzon
 * @generated
 */
public class COREntryRelServiceHttp {

	public static com.liferay.commerce.order.rule.model.COREntryRel
			addCOREntryRel(
				HttpPrincipal httpPrincipal, String className, long classPK,
				long corEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class, "addCOREntryRel",
				_addCOREntryRelParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, className, classPK, corEntryId);

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

			return (com.liferay.commerce.order.rule.model.COREntryRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteCOREntryRel(
			HttpPrincipal httpPrincipal, long corEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class, "deleteCOREntryRel",
				_deleteCOREntryRelParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, corEntryRelId);

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

	public static void deleteCOREntryRels(
			HttpPrincipal httpPrincipal, String className, long corEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class, "deleteCOREntryRels",
				_deleteCOREntryRelsParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, className, corEntryId);

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

	public static void deleteCOREntryRelsByCOREntryId(
			HttpPrincipal httpPrincipal, long corEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class, "deleteCOREntryRelsByCOREntryId",
				_deleteCOREntryRelsByCOREntryIdParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, corEntryId);

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

	public static com.liferay.commerce.order.rule.model.COREntryRel
			fetchCOREntryRel(
				HttpPrincipal httpPrincipal, String className, long classPK,
				long corEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class, "fetchCOREntryRel",
				_fetchCOREntryRelParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, className, classPK, corEntryId);

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

			return (com.liferay.commerce.order.rule.model.COREntryRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.order.rule.model.COREntryRel>
				getAccountEntryCOREntryRels(
					HttpPrincipal httpPrincipal, long corEntryId,
					String keywords, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class, "getAccountEntryCOREntryRels",
				_getAccountEntryCOREntryRelsParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, corEntryId, keywords, start, end);

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
				<com.liferay.commerce.order.rule.model.COREntryRel>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getAccountEntryCOREntryRelsCount(
			HttpPrincipal httpPrincipal, long corEntryId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class,
				"getAccountEntryCOREntryRelsCount",
				_getAccountEntryCOREntryRelsCountParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, corEntryId, keywords);

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
		<com.liferay.commerce.order.rule.model.COREntryRel>
				getAccountGroupCOREntryRels(
					HttpPrincipal httpPrincipal, long corEntryId,
					String keywords, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class, "getAccountGroupCOREntryRels",
				_getAccountGroupCOREntryRelsParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, corEntryId, keywords, start, end);

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
				<com.liferay.commerce.order.rule.model.COREntryRel>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getAccountGroupCOREntryRelsCount(
			HttpPrincipal httpPrincipal, long corEntryId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class,
				"getAccountGroupCOREntryRelsCount",
				_getAccountGroupCOREntryRelsCountParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, corEntryId, keywords);

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

	public static com.liferay.commerce.order.rule.model.COREntryRel
			getCOREntryRel(HttpPrincipal httpPrincipal, long corEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class, "getCOREntryRel",
				_getCOREntryRelParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, corEntryRelId);

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

			return (com.liferay.commerce.order.rule.model.COREntryRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.order.rule.model.COREntryRel> getCOREntryRels(
				HttpPrincipal httpPrincipal, long corEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class, "getCOREntryRels",
				_getCOREntryRelsParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, corEntryId);

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
				<com.liferay.commerce.order.rule.model.COREntryRel>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.order.rule.model.COREntryRel> getCOREntryRels(
				HttpPrincipal httpPrincipal, long corEntryId, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.order.rule.model.COREntryRel>
						orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class, "getCOREntryRels",
				_getCOREntryRelsParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, corEntryId, start, end, orderByComparator);

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
				<com.liferay.commerce.order.rule.model.COREntryRel>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCOREntryRelsCount(
			HttpPrincipal httpPrincipal, long corEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class, "getCOREntryRelsCount",
				_getCOREntryRelsCountParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, corEntryId);

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
		<com.liferay.commerce.order.rule.model.COREntryRel>
				getCommerceChannelCOREntryRels(
					HttpPrincipal httpPrincipal, long corEntryId,
					String keywords, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class, "getCommerceChannelCOREntryRels",
				_getCommerceChannelCOREntryRelsParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, corEntryId, keywords, start, end);

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
				<com.liferay.commerce.order.rule.model.COREntryRel>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCommerceChannelCOREntryRelsCount(
			HttpPrincipal httpPrincipal, long corEntryId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class,
				"getCommerceChannelCOREntryRelsCount",
				_getCommerceChannelCOREntryRelsCountParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, corEntryId, keywords);

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
		<com.liferay.commerce.order.rule.model.COREntryRel>
				getCommerceOrderTypeCOREntryRels(
					HttpPrincipal httpPrincipal, long corEntryId,
					String keywords, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class,
				"getCommerceOrderTypeCOREntryRels",
				_getCommerceOrderTypeCOREntryRelsParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, corEntryId, keywords, start, end);

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
				<com.liferay.commerce.order.rule.model.COREntryRel>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCommerceOrderTypeCOREntryRelsCount(
			HttpPrincipal httpPrincipal, long corEntryId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				COREntryRelServiceUtil.class,
				"getCommerceOrderTypeCOREntryRelsCount",
				_getCommerceOrderTypeCOREntryRelsCountParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, corEntryId, keywords);

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
		COREntryRelServiceHttp.class);

	private static final Class<?>[] _addCOREntryRelParameterTypes0 =
		new Class[] {String.class, long.class, long.class};
	private static final Class<?>[] _deleteCOREntryRelParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteCOREntryRelsParameterTypes2 =
		new Class[] {String.class, long.class};
	private static final Class<?>[]
		_deleteCOREntryRelsByCOREntryIdParameterTypes3 = new Class[] {
			long.class
		};
	private static final Class<?>[] _fetchCOREntryRelParameterTypes4 =
		new Class[] {String.class, long.class, long.class};
	private static final Class<?>[]
		_getAccountEntryCOREntryRelsParameterTypes5 = new Class[] {
			long.class, String.class, int.class, int.class
		};
	private static final Class<?>[]
		_getAccountEntryCOREntryRelsCountParameterTypes6 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_getAccountGroupCOREntryRelsParameterTypes7 = new Class[] {
			long.class, String.class, int.class, int.class
		};
	private static final Class<?>[]
		_getAccountGroupCOREntryRelsCountParameterTypes8 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _getCOREntryRelParameterTypes9 =
		new Class[] {long.class};
	private static final Class<?>[] _getCOREntryRelsParameterTypes10 =
		new Class[] {long.class};
	private static final Class<?>[] _getCOREntryRelsParameterTypes11 =
		new Class[] {
			long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getCOREntryRelsCountParameterTypes12 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getCommerceChannelCOREntryRelsParameterTypes13 = new Class[] {
			long.class, String.class, int.class, int.class
		};
	private static final Class<?>[]
		_getCommerceChannelCOREntryRelsCountParameterTypes14 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_getCommerceOrderTypeCOREntryRelsParameterTypes15 = new Class[] {
			long.class, String.class, int.class, int.class
		};
	private static final Class<?>[]
		_getCommerceOrderTypeCOREntryRelsCountParameterTypes16 = new Class[] {
			long.class, String.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:1893838467