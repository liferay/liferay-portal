/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service.http;

import com.liferay.list.type.service.ListTypeEntryServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>ListTypeEntryServiceUtil</code> service
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
 * @author Gabriel Albuquerque
 * @generated
 */
public class ListTypeEntryServiceHttp {

	public static com.liferay.list.type.model.ListTypeEntry addListTypeEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long listTypeDefinitionId, String key,
			java.util.Map<java.util.Locale, String> nameMap, boolean system)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeEntryServiceUtil.class, "addListTypeEntry",
				_addListTypeEntryParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, listTypeDefinitionId, key,
				nameMap, system);

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

			return (com.liferay.list.type.model.ListTypeEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.list.type.model.ListTypeEntry deleteListTypeEntry(
			HttpPrincipal httpPrincipal, long listTypeEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeEntryServiceUtil.class, "deleteListTypeEntry",
				_deleteListTypeEntryParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, listTypeEntryId);

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

			return (com.liferay.list.type.model.ListTypeEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.list.type.model.ListTypeEntry fetchListTypeEntry(
			HttpPrincipal httpPrincipal, long listTypeDefinitionId, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeEntryServiceUtil.class, "fetchListTypeEntry",
				_fetchListTypeEntryParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, listTypeDefinitionId, key);

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

			return (com.liferay.list.type.model.ListTypeEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.list.type.model.ListTypeEntry>
			getListTypeEntries(
				HttpPrincipal httpPrincipal, long listTypeDefinitionId,
				int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeEntryServiceUtil.class, "getListTypeEntries",
				_getListTypeEntriesParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, listTypeDefinitionId, start, end);

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

			return (java.util.List<com.liferay.list.type.model.ListTypeEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getListTypeEntriesCount(
			HttpPrincipal httpPrincipal, long listTypeDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeEntryServiceUtil.class, "getListTypeEntriesCount",
				_getListTypeEntriesCountParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, listTypeDefinitionId);

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

	public static com.liferay.list.type.model.ListTypeEntry getListTypeEntry(
			HttpPrincipal httpPrincipal, long listTypeEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeEntryServiceUtil.class, "getListTypeEntry",
				_getListTypeEntryParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, listTypeEntryId);

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

			return (com.liferay.list.type.model.ListTypeEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.list.type.model.ListTypeEntry
			getListTypeEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId, long listTypeDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeEntryServiceUtil.class,
				"getListTypeEntryByExternalReferenceCode",
				_getListTypeEntryByExternalReferenceCodeParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, companyId,
				listTypeDefinitionId);

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

			return (com.liferay.list.type.model.ListTypeEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.list.type.model.ListTypeEntry
			getOrAddEmptyListTypeEntry(
				HttpPrincipal httpPrincipal, long userId,
				long listTypeDefinitionId, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeEntryServiceUtil.class, "getOrAddEmptyListTypeEntry",
				_getOrAddEmptyListTypeEntryParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, listTypeDefinitionId, key);

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

			return (com.liferay.list.type.model.ListTypeEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.list.type.model.ListTypeEntry updateListTypeEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long listTypeEntryId,
			java.util.Map<java.util.Locale, String> nameMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeEntryServiceUtil.class, "updateListTypeEntry",
				_updateListTypeEntryParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, listTypeEntryId, nameMap);

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

			return (com.liferay.list.type.model.ListTypeEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		ListTypeEntryServiceHttp.class);

	private static final Class<?>[] _addListTypeEntryParameterTypes0 =
		new Class[] {
			String.class, long.class, String.class, java.util.Map.class,
			boolean.class
		};
	private static final Class<?>[] _deleteListTypeEntryParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _fetchListTypeEntryParameterTypes2 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _getListTypeEntriesParameterTypes3 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[] _getListTypeEntriesCountParameterTypes4 =
		new Class[] {long.class};
	private static final Class<?>[] _getListTypeEntryParameterTypes5 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getListTypeEntryByExternalReferenceCodeParameterTypes6 = new Class[] {
			String.class, long.class, long.class
		};
	private static final Class<?>[] _getOrAddEmptyListTypeEntryParameterTypes7 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[] _updateListTypeEntryParameterTypes8 =
		new Class[] {String.class, long.class, java.util.Map.class};

}