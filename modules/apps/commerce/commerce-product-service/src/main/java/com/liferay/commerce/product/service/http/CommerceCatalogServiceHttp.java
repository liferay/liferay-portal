/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.http;

import com.liferay.commerce.product.service.CommerceCatalogServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CommerceCatalogServiceUtil</code> service
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
public class CommerceCatalogServiceHttp {

	public static com.liferay.commerce.product.model.CommerceCatalog
			addCommerceCatalog(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long accountEntryId, String name, String commerceCurrencyCode,
				String catalogDefaultLanguageId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCatalogServiceUtil.class, "addCommerceCatalog",
				_addCommerceCatalogParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, accountEntryId, name,
				commerceCurrencyCode, catalogDefaultLanguageId, serviceContext);

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

			return (com.liferay.commerce.product.model.CommerceCatalog)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceCatalog
			deleteCommerceCatalog(
				HttpPrincipal httpPrincipal, long commerceCatalogId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCatalogServiceUtil.class, "deleteCommerceCatalog",
				_deleteCommerceCatalogParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceCatalogId);

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

			return (com.liferay.commerce.product.model.CommerceCatalog)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceCatalog
			fetchCommerceCatalog(
				HttpPrincipal httpPrincipal, long commerceCatalogId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCatalogServiceUtil.class, "fetchCommerceCatalog",
				_fetchCommerceCatalogParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceCatalogId);

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

			return (com.liferay.commerce.product.model.CommerceCatalog)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceCatalog
			fetchCommerceCatalogByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCatalogServiceUtil.class,
				"fetchCommerceCatalogByExternalReferenceCode",
				_fetchCommerceCatalogByExternalReferenceCodeParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, companyId);

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

			return (com.liferay.commerce.product.model.CommerceCatalog)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceCatalog
			fetchCommerceCatalogByGroupId(
				HttpPrincipal httpPrincipal, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCatalogServiceUtil.class,
				"fetchCommerceCatalogByGroupId",
				_fetchCommerceCatalogByGroupIdParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(methodKey, groupId);

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

			return (com.liferay.commerce.product.model.CommerceCatalog)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceCatalog
			getCommerceCatalog(
				HttpPrincipal httpPrincipal, long commerceCatalogId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCatalogServiceUtil.class, "getCommerceCatalog",
				_getCommerceCatalogParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceCatalogId);

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

			return (com.liferay.commerce.product.model.CommerceCatalog)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CommerceCatalog>
			getCommerceCatalogs(
				HttpPrincipal httpPrincipal, long companyId, int start,
				int end) {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCatalogServiceUtil.class, "getCommerceCatalogs",
				_getCommerceCatalogsParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.commerce.product.model.CommerceCatalog>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceCatalog
			getOrAddEmptyCommerceCatalog(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				String commerceCurrencyCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCatalogServiceUtil.class,
				"getOrAddEmptyCommerceCatalog",
				_getOrAddEmptyCommerceCatalogParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commerceCurrencyCode);

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

			return (com.liferay.commerce.product.model.CommerceCatalog)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CommerceCatalog> search(
				HttpPrincipal httpPrincipal, long companyId, String keywords,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCatalogServiceUtil.class, "search",
				_searchParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, keywords, start, end, sort);

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
				<com.liferay.commerce.product.model.CommerceCatalog>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int searchCommerceCatalogsCount(
			HttpPrincipal httpPrincipal, long companyId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCatalogServiceUtil.class, "searchCommerceCatalogsCount",
				_searchCommerceCatalogsCountParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, keywords);

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

	public static com.liferay.commerce.product.model.CommerceCatalog
			updateCommerceCatalog(
				HttpPrincipal httpPrincipal, long commerceCatalogId,
				long accountEntryId, String name, String commerceCurrencyCode,
				String catalogDefaultLanguageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCatalogServiceUtil.class, "updateCommerceCatalog",
				_updateCommerceCatalogParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceCatalogId, accountEntryId, name,
				commerceCurrencyCode, catalogDefaultLanguageId);

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

			return (com.liferay.commerce.product.model.CommerceCatalog)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceCatalog
			updateCommerceCatalogExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long commerceCatalogId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCatalogServiceUtil.class,
				"updateCommerceCatalogExternalReferenceCode",
				_updateCommerceCatalogExternalReferenceCodeParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commerceCatalogId);

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

			return (com.liferay.commerce.product.model.CommerceCatalog)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CommerceCatalogServiceHttp.class);

	private static final Class<?>[] _addCommerceCatalogParameterTypes0 =
		new Class[] {
			String.class, long.class, String.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteCommerceCatalogParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _fetchCommerceCatalogParameterTypes2 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchCommerceCatalogByExternalReferenceCodeParameterTypes3 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_fetchCommerceCatalogByGroupIdParameterTypes4 = new Class[] {
			long.class
		};
	private static final Class<?>[] _getCommerceCatalogParameterTypes5 =
		new Class[] {long.class};
	private static final Class<?>[] _getCommerceCatalogsParameterTypes6 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[]
		_getOrAddEmptyCommerceCatalogParameterTypes7 = new Class[] {
			String.class, String.class
		};
	private static final Class<?>[] _searchParameterTypes8 = new Class[] {
		long.class, String.class, int.class, int.class,
		com.liferay.portal.kernel.search.Sort.class
	};
	private static final Class<?>[]
		_searchCommerceCatalogsCountParameterTypes9 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _updateCommerceCatalogParameterTypes10 =
		new Class[] {
			long.class, long.class, String.class, String.class, String.class
		};
	private static final Class<?>[]
		_updateCommerceCatalogExternalReferenceCodeParameterTypes11 =
			new Class[] {String.class, long.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:438772409