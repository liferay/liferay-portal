/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.http;

import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>OAuthClientASLocalMetadataServiceUtil</code> service
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
public class OAuthClientASLocalMetadataServiceHttp {

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
				addOAuthClientASLocalMetadata(
					HttpPrincipal httpPrincipal, long userId,
					String authorizationEndpoint, Boolean enabled,
					String issuerString, String jwksUri,
					String[] supportedGrantTypes, String[] supportedScopes,
					String[] supportedSubjectTypes, String tokenEndpointString,
					String userinfoEndpoint)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientASLocalMetadataServiceUtil.class,
				"addOAuthClientASLocalMetadata",
				_addOAuthClientASLocalMetadataParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, authorizationEndpoint, enabled, issuerString,
				jwksUri, supportedGrantTypes, supportedScopes,
				supportedSubjectTypes, tokenEndpointString, userinfoEndpoint);

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

			return (com.liferay.oauth.client.persistence.model.
				OAuthClientASLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
				deleteOAuthClientASLocalMetadata(
					HttpPrincipal httpPrincipal,
					long oAuthClientASLocalMetadataId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientASLocalMetadataServiceUtil.class,
				"deleteOAuthClientASLocalMetadata",
				_deleteOAuthClientASLocalMetadataParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, oAuthClientASLocalMetadataId);

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

			return (com.liferay.oauth.client.persistence.model.
				OAuthClientASLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
				deleteOAuthClientASLocalMetadata(
					HttpPrincipal httpPrincipal, String localWellKnownURI)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientASLocalMetadataServiceUtil.class,
				"deleteOAuthClientASLocalMetadata",
				_deleteOAuthClientASLocalMetadataParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, localWellKnownURI);

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

			return (com.liferay.oauth.client.persistence.model.
				OAuthClientASLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata>
			getCompanyOAuthClientASLocalMetadata(
				HttpPrincipal httpPrincipal, long companyId) {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientASLocalMetadataServiceUtil.class,
				"getCompanyOAuthClientASLocalMetadata",
				_getCompanyOAuthClientASLocalMetadataParameterTypes3);

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
				<com.liferay.oauth.client.persistence.model.
					OAuthClientASLocalMetadata>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata>
			getCompanyOAuthClientASLocalMetadata(
				HttpPrincipal httpPrincipal, long companyId, int start,
				int end) {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientASLocalMetadataServiceUtil.class,
				"getCompanyOAuthClientASLocalMetadata",
				_getCompanyOAuthClientASLocalMetadataParameterTypes4);

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
				<com.liferay.oauth.client.persistence.model.
					OAuthClientASLocalMetadata>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
				getOAuthClientASLocalMetadata(
					HttpPrincipal httpPrincipal, String localWellKnownURI)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientASLocalMetadataServiceUtil.class,
				"getOAuthClientASLocalMetadata",
				_getOAuthClientASLocalMetadataParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, localWellKnownURI);

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

			return (com.liferay.oauth.client.persistence.model.
				OAuthClientASLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata>
			getUserOAuthClientASLocalMetadata(
				HttpPrincipal httpPrincipal, long userId) {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientASLocalMetadataServiceUtil.class,
				"getUserOAuthClientASLocalMetadata",
				_getUserOAuthClientASLocalMetadataParameterTypes6);

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
				<com.liferay.oauth.client.persistence.model.
					OAuthClientASLocalMetadata>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata>
			getUserOAuthClientASLocalMetadata(
				HttpPrincipal httpPrincipal, long userId, int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientASLocalMetadataServiceUtil.class,
				"getUserOAuthClientASLocalMetadata",
				_getUserOAuthClientASLocalMetadataParameterTypes7);

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
				<com.liferay.oauth.client.persistence.model.
					OAuthClientASLocalMetadata>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
				updateOAuthClientASLocalMetadata(
					HttpPrincipal httpPrincipal,
					long oAuthClientASLocalMetadataId,
					String authorizationEndpoint, Boolean enabled,
					String issuerString, String jwksUri,
					String[] supportedGrantTypes, String[] supportedScopes,
					String[] supportedSubjectTypes, String tokenEndpointString,
					String userinfoEndpoint)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientASLocalMetadataServiceUtil.class,
				"updateOAuthClientASLocalMetadata",
				_updateOAuthClientASLocalMetadataParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, oAuthClientASLocalMetadataId, authorizationEndpoint,
				enabled, issuerString, jwksUri, supportedGrantTypes,
				supportedScopes, supportedSubjectTypes, tokenEndpointString,
				userinfoEndpoint);

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

			return (com.liferay.oauth.client.persistence.model.
				OAuthClientASLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		OAuthClientASLocalMetadataServiceHttp.class);

	private static final Class<?>[]
		_addOAuthClientASLocalMetadataParameterTypes0 = new Class[] {
			long.class, String.class, Boolean.class, String.class, String.class,
			String[].class, String[].class, String[].class, String.class,
			String.class
		};
	private static final Class<?>[]
		_deleteOAuthClientASLocalMetadataParameterTypes1 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_deleteOAuthClientASLocalMetadataParameterTypes2 = new Class[] {
			String.class
		};
	private static final Class<?>[]
		_getCompanyOAuthClientASLocalMetadataParameterTypes3 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_getCompanyOAuthClientASLocalMetadataParameterTypes4 = new Class[] {
			long.class, int.class, int.class
		};
	private static final Class<?>[]
		_getOAuthClientASLocalMetadataParameterTypes5 = new Class[] {
			String.class
		};
	private static final Class<?>[]
		_getUserOAuthClientASLocalMetadataParameterTypes6 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_getUserOAuthClientASLocalMetadataParameterTypes7 = new Class[] {
			long.class, int.class, int.class
		};
	private static final Class<?>[]
		_updateOAuthClientASLocalMetadataParameterTypes8 = new Class[] {
			long.class, String.class, Boolean.class, String.class, String.class,
			String[].class, String[].class, String[].class, String.class,
			String.class
		};

}