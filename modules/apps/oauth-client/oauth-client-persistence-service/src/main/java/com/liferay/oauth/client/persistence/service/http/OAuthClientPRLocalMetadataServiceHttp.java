/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.http;

import com.liferay.oauth.client.persistence.service.OAuthClientPRLocalMetadataServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>OAuthClientPRLocalMetadataServiceUtil</code> service
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
public class OAuthClientPRLocalMetadataServiceHttp {

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
				addOAuthClientPRLocalMetadata(
					HttpPrincipal httpPrincipal, String metadataJSON)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"addOAuthClientPRLocalMetadata",
				_addOAuthClientPRLocalMetadataParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, metadataJSON);

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
				OAuthClientPRLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
				addOAuthClientPRLocalMetadata(
					HttpPrincipal httpPrincipal, String externalReferenceCode,
					String[] authorizationServers,
					String[] bearerMethodsSupported,
					boolean localWellKnownEnabled, String resource,
					String resourceName, String[] scopesSupported)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"addOAuthClientPRLocalMetadata",
				_addOAuthClientPRLocalMetadataParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, authorizationServers,
				bearerMethodsSupported, localWellKnownEnabled, resource,
				resourceName, scopesSupported);

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
				OAuthClientPRLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
				deleteOAuthClientPRLocalMetadata(
					HttpPrincipal httpPrincipal,
					long oAuthClientPRLocalMetadataId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"deleteOAuthClientPRLocalMetadata",
				_deleteOAuthClientPRLocalMetadataParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, oAuthClientPRLocalMetadataId);

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
				OAuthClientPRLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
				deleteOAuthClientPRLocalMetadata(
					HttpPrincipal httpPrincipal, long companyId,
					String localWellKnownURI)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"deleteOAuthClientPRLocalMetadata",
				_deleteOAuthClientPRLocalMetadataParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, localWellKnownURI);

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
				OAuthClientPRLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
				fetchOAuthClientPRLocalMetadata(
					HttpPrincipal httpPrincipal,
					long oAuthClientPRLocalMetadataId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"fetchOAuthClientPRLocalMetadata",
				_fetchOAuthClientPRLocalMetadataParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, oAuthClientPRLocalMetadataId);

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
				OAuthClientPRLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
				fetchOAuthClientPRLocalMetadata(
					HttpPrincipal httpPrincipal, long companyId,
					String resource)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"fetchOAuthClientPRLocalMetadata",
				_fetchOAuthClientPRLocalMetadataParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, resource);

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
				OAuthClientPRLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
				fetchOAuthClientPRLocalMetadataByExternalReferenceCode(
					HttpPrincipal httpPrincipal, String externalReferenceCode,
					long companyId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"fetchOAuthClientPRLocalMetadataByExternalReferenceCode",
				_fetchOAuthClientPRLocalMetadataByExternalReferenceCodeParameterTypes6);

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

			return (com.liferay.oauth.client.persistence.model.
				OAuthClientPRLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata>
			getCompanyOAuthClientPRLocalMetadata(
				HttpPrincipal httpPrincipal, long companyId) {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"getCompanyOAuthClientPRLocalMetadata",
				_getCompanyOAuthClientPRLocalMetadataParameterTypes7);

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
					OAuthClientPRLocalMetadata>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata>
			getCompanyOAuthClientPRLocalMetadata(
				HttpPrincipal httpPrincipal, long companyId, int start,
				int end) {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"getCompanyOAuthClientPRLocalMetadata",
				_getCompanyOAuthClientPRLocalMetadataParameterTypes8);

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
					OAuthClientPRLocalMetadata>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
				getOAuthClientPRLocalMetadata(
					HttpPrincipal httpPrincipal, long companyId,
					boolean localWellKnownEnabled,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.oauth.client.persistence.model.
							OAuthClientPRLocalMetadata> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"getOAuthClientPRLocalMetadata",
				_getOAuthClientPRLocalMetadataParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, localWellKnownEnabled, orderByComparator);

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
				OAuthClientPRLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
				getOAuthClientPRLocalMetadata(
					HttpPrincipal httpPrincipal, long companyId,
					String resource)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"getOAuthClientPRLocalMetadata",
				_getOAuthClientPRLocalMetadataParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, resource);

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
				OAuthClientPRLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
				getOAuthClientPRLocalMetadataByExternalReferenceCode(
					HttpPrincipal httpPrincipal, String externalReferenceCode,
					long companyId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"getOAuthClientPRLocalMetadataByExternalReferenceCode",
				_getOAuthClientPRLocalMetadataByExternalReferenceCodeParameterTypes11);

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

			return (com.liferay.oauth.client.persistence.model.
				OAuthClientPRLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
				getOAuthClientPRLocalMetadataByLocalWellKnownURI(
					HttpPrincipal httpPrincipal, long companyId,
					String localWellKnownURI)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"getOAuthClientPRLocalMetadataByLocalWellKnownURI",
				_getOAuthClientPRLocalMetadataByLocalWellKnownURIParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, localWellKnownURI);

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
				OAuthClientPRLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata>
			getUserOAuthClientPRLocalMetadata(
				HttpPrincipal httpPrincipal, long userId) {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"getUserOAuthClientPRLocalMetadata",
				_getUserOAuthClientPRLocalMetadataParameterTypes13);

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
					OAuthClientPRLocalMetadata>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata>
			getUserOAuthClientPRLocalMetadata(
				HttpPrincipal httpPrincipal, long userId, int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"getUserOAuthClientPRLocalMetadata",
				_getUserOAuthClientPRLocalMetadataParameterTypes14);

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
					OAuthClientPRLocalMetadata>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
				updateOAuthClientPRLocalMetadata(
					HttpPrincipal httpPrincipal,
					long oAuthClientPRLocalMetadataId, String metadataJSON)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"updateOAuthClientPRLocalMetadata",
				_updateOAuthClientPRLocalMetadataParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, oAuthClientPRLocalMetadataId, metadataJSON);

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
				OAuthClientPRLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
				updateOAuthClientPRLocalMetadata(
					HttpPrincipal httpPrincipal,
					long oAuthClientPRLocalMetadataId,
					String[] authorizationServers,
					String[] bearerMethodsSupported,
					boolean localWellKnownEnabled, String resource,
					String resourceName, String[] scopesSupported)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				OAuthClientPRLocalMetadataServiceUtil.class,
				"updateOAuthClientPRLocalMetadata",
				_updateOAuthClientPRLocalMetadataParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, oAuthClientPRLocalMetadataId, authorizationServers,
				bearerMethodsSupported, localWellKnownEnabled, resource,
				resourceName, scopesSupported);

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
				OAuthClientPRLocalMetadata)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		OAuthClientPRLocalMetadataServiceHttp.class);

	private static final Class<?>[]
		_addOAuthClientPRLocalMetadataParameterTypes0 = new Class[] {
			String.class
		};
	private static final Class<?>[]
		_addOAuthClientPRLocalMetadataParameterTypes1 = new Class[] {
			String.class, String[].class, String[].class, boolean.class,
			String.class, String.class, String[].class
		};
	private static final Class<?>[]
		_deleteOAuthClientPRLocalMetadataParameterTypes2 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_deleteOAuthClientPRLocalMetadataParameterTypes3 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_fetchOAuthClientPRLocalMetadataParameterTypes4 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_fetchOAuthClientPRLocalMetadataParameterTypes5 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_fetchOAuthClientPRLocalMetadataByExternalReferenceCodeParameterTypes6 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_getCompanyOAuthClientPRLocalMetadataParameterTypes7 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_getCompanyOAuthClientPRLocalMetadataParameterTypes8 = new Class[] {
			long.class, int.class, int.class
		};
	private static final Class<?>[]
		_getOAuthClientPRLocalMetadataParameterTypes9 = new Class[] {
			long.class, boolean.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getOAuthClientPRLocalMetadataParameterTypes10 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_getOAuthClientPRLocalMetadataByExternalReferenceCodeParameterTypes11 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_getOAuthClientPRLocalMetadataByLocalWellKnownURIParameterTypes12 =
			new Class[] {long.class, String.class};
	private static final Class<?>[]
		_getUserOAuthClientPRLocalMetadataParameterTypes13 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_getUserOAuthClientPRLocalMetadataParameterTypes14 = new Class[] {
			long.class, int.class, int.class
		};
	private static final Class<?>[]
		_updateOAuthClientPRLocalMetadataParameterTypes15 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_updateOAuthClientPRLocalMetadataParameterTypes16 = new Class[] {
			long.class, String[].class, String[].class, boolean.class,
			String.class, String.class, String[].class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:1174817114