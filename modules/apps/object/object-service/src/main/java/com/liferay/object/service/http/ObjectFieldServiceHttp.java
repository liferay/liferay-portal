/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.http;

import com.liferay.object.service.ObjectFieldServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>ObjectFieldServiceUtil</code> service
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
public class ObjectFieldServiceHttp {

	public static com.liferay.object.model.ObjectField addCustomObjectField(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long listTypeDefinitionId, long objectDefinitionId,
			String businessType, String dbType,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean indexed, boolean indexedAsKeyword, String indexedLanguageId,
			java.util.Map<java.util.Locale, String> labelMap, boolean localized,
			String name, String readOnly, String readOnlyConditionExpression,
			boolean required, boolean state,
			java.util.List<com.liferay.object.model.ObjectFieldSetting>
				objectFieldSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectFieldServiceUtil.class, "addCustomObjectField",
				_addCustomObjectFieldParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, listTypeDefinitionId,
				objectDefinitionId, businessType, dbType, descriptionMap,
				indexed, indexedAsKeyword, indexedLanguageId, labelMap,
				localized, name, readOnly, readOnlyConditionExpression,
				required, state, objectFieldSettings);

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

			return (com.liferay.object.model.ObjectField)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectField deleteObjectField(
			HttpPrincipal httpPrincipal, long objectFieldId)
		throws Exception {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectFieldServiceUtil.class, "deleteObjectField",
				_deleteObjectFieldParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectFieldId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof Exception) {
					throw (Exception)exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.object.model.ObjectField)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectField getObjectField(
			HttpPrincipal httpPrincipal, long objectFieldId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectFieldServiceUtil.class, "getObjectField",
				_getObjectFieldParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectFieldId);

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

			return (com.liferay.object.model.ObjectField)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectField updateObjectField(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long objectFieldId, long listTypeDefinitionId, String businessType,
			String dbType,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean indexed, boolean indexedAsKeyword, String indexedLanguageId,
			java.util.Map<java.util.Locale, String> labelMap, boolean localized,
			String name, String readOnly, String readOnlyConditionExpression,
			boolean required, boolean state,
			java.util.List<com.liferay.object.model.ObjectFieldSetting>
				objectFieldSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectFieldServiceUtil.class, "updateObjectField",
				_updateObjectFieldParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, objectFieldId,
				listTypeDefinitionId, businessType, dbType, descriptionMap,
				indexed, indexedAsKeyword, indexedLanguageId, labelMap,
				localized, name, readOnly, readOnlyConditionExpression,
				required, state, objectFieldSettings);

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

			return (com.liferay.object.model.ObjectField)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		ObjectFieldServiceHttp.class);

	private static final Class<?>[] _addCustomObjectFieldParameterTypes0 =
		new Class[] {
			String.class, long.class, long.class, String.class, String.class,
			java.util.Map.class, boolean.class, boolean.class, String.class,
			java.util.Map.class, boolean.class, String.class, String.class,
			String.class, boolean.class, boolean.class, java.util.List.class
		};
	private static final Class<?>[] _deleteObjectFieldParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _getObjectFieldParameterTypes2 =
		new Class[] {long.class};
	private static final Class<?>[] _updateObjectFieldParameterTypes3 =
		new Class[] {
			String.class, long.class, long.class, String.class, String.class,
			java.util.Map.class, boolean.class, boolean.class, String.class,
			java.util.Map.class, boolean.class, String.class, String.class,
			String.class, boolean.class, boolean.class, java.util.List.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:718157227