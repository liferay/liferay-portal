/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.http;

import com.liferay.object.service.ObjectRelationshipServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>ObjectRelationshipServiceUtil</code> service
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
public class ObjectRelationshipServiceHttp {

	public static com.liferay.object.model.ObjectRelationship
			addObjectRelationship(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long objectDefinitionId1, long objectDefinitionId2,
				long parameterObjectFieldId, String deletionType,
				java.util.Map<java.util.Locale, String> descriptionMap,
				boolean edge, java.util.Map<java.util.Locale, String> labelMap,
				String name, boolean system, String type,
				com.liferay.object.model.ObjectField objectField)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectRelationshipServiceUtil.class, "addObjectRelationship",
				_addObjectRelationshipParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, objectDefinitionId1,
				objectDefinitionId2, parameterObjectFieldId, deletionType,
				descriptionMap, edge, labelMap, name, system, type,
				objectField);

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

			return (com.liferay.object.model.ObjectRelationship)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void addObjectRelationshipMappingTableValues(
			HttpPrincipal httpPrincipal, long objectRelationshipId,
			long primaryKey1, long primaryKey2,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectRelationshipServiceUtil.class,
				"addObjectRelationshipMappingTableValues",
				_addObjectRelationshipMappingTableValuesParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectRelationshipId, primaryKey1, primaryKey2,
				serviceContext);

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

	public static com.liferay.object.model.ObjectRelationship
			deleteObjectRelationship(
				HttpPrincipal httpPrincipal, long objectRelationshipId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectRelationshipServiceUtil.class, "deleteObjectRelationship",
				_deleteObjectRelationshipParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectRelationshipId);

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

			return (com.liferay.object.model.ObjectRelationship)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectRelationship
			fetchObjectRelationshipByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId, long objectDefinitionId1)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectRelationshipServiceUtil.class,
				"fetchObjectRelationshipByExternalReferenceCode",
				_fetchObjectRelationshipByExternalReferenceCodeParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, companyId,
				objectDefinitionId1);

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

			return (com.liferay.object.model.ObjectRelationship)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectRelationship
			getObjectRelationship(
				HttpPrincipal httpPrincipal, long objectRelationshipId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectRelationshipServiceUtil.class, "getObjectRelationship",
				_getObjectRelationshipParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectRelationshipId);

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

			return (com.liferay.object.model.ObjectRelationship)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectRelationship
			getObjectRelationship(
				HttpPrincipal httpPrincipal, long objectDefinitionId1,
				String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectRelationshipServiceUtil.class, "getObjectRelationship",
				_getObjectRelationshipParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectDefinitionId1, name);

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

			return (com.liferay.object.model.ObjectRelationship)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.object.model.ObjectRelationship>
			getObjectRelationships(
				HttpPrincipal httpPrincipal, long objectDefinitionId1,
				int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectRelationshipServiceUtil.class, "getObjectRelationships",
				_getObjectRelationshipsParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectDefinitionId1, start, end);

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

			return (java.util.List<com.liferay.object.model.ObjectRelationship>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectRelationship
			updateObjectRelationship(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long objectRelationshipId, long parameterObjectFieldId,
				String deletionType,
				java.util.Map<java.util.Locale, String> descriptionMap,
				boolean edge, java.util.Map<java.util.Locale, String> labelMap,
				com.liferay.object.model.ObjectField objectField)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectRelationshipServiceUtil.class, "updateObjectRelationship",
				_updateObjectRelationshipParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, objectRelationshipId,
				parameterObjectFieldId, deletionType, descriptionMap, edge,
				labelMap, objectField);

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

			return (com.liferay.object.model.ObjectRelationship)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		ObjectRelationshipServiceHttp.class);

	private static final Class<?>[] _addObjectRelationshipParameterTypes0 =
		new Class[] {
			String.class, long.class, long.class, long.class, String.class,
			java.util.Map.class, boolean.class, java.util.Map.class,
			String.class, boolean.class, String.class,
			com.liferay.object.model.ObjectField.class
		};
	private static final Class<?>[]
		_addObjectRelationshipMappingTableValuesParameterTypes1 = new Class[] {
			long.class, long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteObjectRelationshipParameterTypes2 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchObjectRelationshipByExternalReferenceCodeParameterTypes3 =
			new Class[] {String.class, long.class, long.class};
	private static final Class<?>[] _getObjectRelationshipParameterTypes4 =
		new Class[] {long.class};
	private static final Class<?>[] _getObjectRelationshipParameterTypes5 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _getObjectRelationshipsParameterTypes6 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[] _updateObjectRelationshipParameterTypes7 =
		new Class[] {
			String.class, long.class, long.class, String.class,
			java.util.Map.class, boolean.class, java.util.Map.class,
			com.liferay.object.model.ObjectField.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1860049716