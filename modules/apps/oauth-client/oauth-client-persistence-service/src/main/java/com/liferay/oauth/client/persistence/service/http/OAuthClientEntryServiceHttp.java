/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.http;

import com.liferay.oauth.client.persistence.service.OAuthClientEntryServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>OAuthClientEntryServiceUtil</code> service
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
public class OAuthClientEntryServiceHttp {

	public static com.liferay.oauth.client.persistence.model.OAuthClientEntry
			addOAuthClientEntry(
				HttpPrincipal httpPrincipal, long userId,
				String authRequestParametersJSON, String authServerWellKnownURI,
				String customClaimsJSON, String infoJSON,
				long metadataCacheTime, String oidcUserInfoMapperJSON,
				String tokenRequestParametersJSON)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientEntryServiceUtil.class, "addOAuthClientEntry",
				_addOAuthClientEntryParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, authRequestParametersJSON,
				authServerWellKnownURI, customClaimsJSON, infoJSON,
				metadataCacheTime, oidcUserInfoMapperJSON,
				tokenRequestParametersJSON);

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

			return (com.liferay.oauth.client.persistence.model.OAuthClientEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.oauth.client.persistence.model.OAuthClientEntry
			deleteOAuthClientEntry(
				HttpPrincipal httpPrincipal, long oAuthClientEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientEntryServiceUtil.class, "deleteOAuthClientEntry",
				_deleteOAuthClientEntryParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, oAuthClientEntryId);

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

			return (com.liferay.oauth.client.persistence.model.OAuthClientEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.oauth.client.persistence.model.OAuthClientEntry
			deleteOAuthClientEntry(
				HttpPrincipal httpPrincipal, long companyId,
				String authServerWellKnownURI, String clientId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientEntryServiceUtil.class, "deleteOAuthClientEntry",
				_deleteOAuthClientEntryParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, authServerWellKnownURI, clientId);

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

			return (com.liferay.oauth.client.persistence.model.OAuthClientEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
				getAuthServerWellKnownURISuffixOAuthClientEntries(
					HttpPrincipal httpPrincipal, long companyId,
					String authServerWellKnownURISuffix)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientEntryServiceUtil.class,
				"getAuthServerWellKnownURISuffixOAuthClientEntries",
				_getAuthServerWellKnownURISuffixOAuthClientEntriesParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, authServerWellKnownURISuffix);

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
				<com.liferay.oauth.client.persistence.model.OAuthClientEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
			getCompanyOAuthClientEntries(
				HttpPrincipal httpPrincipal, long companyId) {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientEntryServiceUtil.class,
				"getCompanyOAuthClientEntries",
				_getCompanyOAuthClientEntriesParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.oauth.client.persistence.model.OAuthClientEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
			getCompanyOAuthClientEntries(
				HttpPrincipal httpPrincipal, long companyId, int start,
				int end) {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientEntryServiceUtil.class,
				"getCompanyOAuthClientEntries",
				_getCompanyOAuthClientEntriesParameterTypes5);

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
				<com.liferay.oauth.client.persistence.model.OAuthClientEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.oauth.client.persistence.model.OAuthClientEntry
			getOAuthClientEntry(
				HttpPrincipal httpPrincipal, long companyId,
				String authServerWellKnownURI, String clientId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientEntryServiceUtil.class, "getOAuthClientEntry",
				_getOAuthClientEntryParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, authServerWellKnownURI, clientId);

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

			return (com.liferay.oauth.client.persistence.model.OAuthClientEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
			getUserOAuthClientEntries(
				HttpPrincipal httpPrincipal, long userId) {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientEntryServiceUtil.class, "getUserOAuthClientEntries",
				_getUserOAuthClientEntriesParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(methodKey, userId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.oauth.client.persistence.model.OAuthClientEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
			getUserOAuthClientEntries(
				HttpPrincipal httpPrincipal, long userId, int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientEntryServiceUtil.class, "getUserOAuthClientEntries",
				_getUserOAuthClientEntriesParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.oauth.client.persistence.model.OAuthClientEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.oauth.client.persistence.model.OAuthClientEntry
			updateOAuthClientEntry(
				HttpPrincipal httpPrincipal, long oAuthClientEntryId,
				String authRequestParametersJSON, String authServerWellKnownURI,
				String customClaimsJSON, String infoJSON,
				long metadataCacheTime, String oidcUserInfoMapperJSON,
				String tokenRequestParametersJSON)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientEntryServiceUtil.class, "updateOAuthClientEntry",
				_updateOAuthClientEntryParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, oAuthClientEntryId, authRequestParametersJSON,
				authServerWellKnownURI, customClaimsJSON, infoJSON,
				metadataCacheTime, oidcUserInfoMapperJSON,
				tokenRequestParametersJSON);

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

			return (com.liferay.oauth.client.persistence.model.OAuthClientEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		OAuthClientEntryServiceHttp.class);

	private static final Class<?>[] _addOAuthClientEntryParameterTypes0 =
		new Class[] {
			long.class, String.class, String.class, String.class, String.class,
			long.class, String.class, String.class
		};
	private static final Class<?>[] _deleteOAuthClientEntryParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteOAuthClientEntryParameterTypes2 =
		new Class[] {long.class, String.class, String.class};
	private static final Class<?>[]
		_getAuthServerWellKnownURISuffixOAuthClientEntriesParameterTypes3 =
			new Class[] {long.class, String.class};
	private static final Class<?>[]
		_getCompanyOAuthClientEntriesParameterTypes4 = new Class[] {long.class};
	private static final Class<?>[]
		_getCompanyOAuthClientEntriesParameterTypes5 = new Class[] {
			long.class, int.class, int.class
		};
	private static final Class<?>[] _getOAuthClientEntryParameterTypes6 =
		new Class[] {long.class, String.class, String.class};
	private static final Class<?>[] _getUserOAuthClientEntriesParameterTypes7 =
		new Class[] {long.class};
	private static final Class<?>[] _getUserOAuthClientEntriesParameterTypes8 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[] _updateOAuthClientEntryParameterTypes9 =
		new Class[] {
			long.class, String.class, String.class, String.class, String.class,
			long.class, String.class, String.class
		};

}