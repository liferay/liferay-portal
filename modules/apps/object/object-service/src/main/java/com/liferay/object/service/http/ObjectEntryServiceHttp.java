/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.http;

import com.liferay.object.service.ObjectEntryServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>ObjectEntryServiceUtil</code> service
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
public class ObjectEntryServiceHttp {

	public static com.liferay.object.model.ObjectEntry addObjectEntry(
			HttpPrincipal httpPrincipal, long groupId, long objectDefinitionId,
			long objectEntryFolderId, String defaultLanguageId,
			java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "addObjectEntry",
				_addObjectEntryParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, objectDefinitionId, objectEntryFolderId,
				defaultLanguageId, values, serviceContext);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntry addOrUpdateObjectEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, long objectDefinitionId, long objectEntryFolderId,
			java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "addOrUpdateObjectEntry",
				_addOrUpdateObjectEntryParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, objectDefinitionId,
				objectEntryFolderId, values, serviceContext);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void checkModelResourcePermission(
			HttpPrincipal httpPrincipal, long objectDefinitionId,
			long objectEntryId, String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "checkModelResourcePermission",
				_checkModelResourcePermissionParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectDefinitionId, objectEntryId, actionId);

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

	public static void checkModelResourcePermission(
			HttpPrincipal httpPrincipal,
			com.liferay.portal.kernel.security.permission.resource.
				ModelResourcePermission<com.liferay.object.model.ObjectEntry>
					modelResourcePermission,
			com.liferay.object.model.ObjectEntry objectEntry, String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "checkModelResourcePermission",
				_checkModelResourcePermissionParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, modelResourcePermission, objectEntry, actionId);

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

	public static void checkModelResourcePermission(
			HttpPrincipal httpPrincipal,
			com.liferay.object.model.ObjectEntry objectEntry, String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "checkModelResourcePermission",
				_checkModelResourcePermissionParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntry, actionId);

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

	public static com.liferay.object.model.ObjectEntry copyObjectEntry(
			HttpPrincipal httpPrincipal, long objectEntryId,
			long objectEntryFolderId,
			java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "copyObjectEntry",
				_copyObjectEntryParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryId, objectEntryFolderId, values,
				serviceContext);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntry deleteObjectEntry(
			HttpPrincipal httpPrincipal, long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "deleteObjectEntry",
				_deleteObjectEntryParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryId);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntry expireObjectEntry(
			HttpPrincipal httpPrincipal, long objectEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "expireObjectEntry",
				_expireObjectEntryParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryId, serviceContext);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntry fetchObjectEntry(
			HttpPrincipal httpPrincipal, long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "fetchObjectEntry",
				_fetchObjectEntryParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryId);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntry fetchObjectEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "fetchObjectEntry",
				_fetchObjectEntryParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, objectDefinitionId);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntry fetchRelatedObjectEntry(
			HttpPrincipal httpPrincipal, long groupId,
			long objectRelationshipId, long primaryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "fetchRelatedObjectEntry",
				_fetchRelatedObjectEntryParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, objectRelationshipId, primaryKey);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.object.model.ObjectEntry>
			getManyToManyObjectEntries(
				HttpPrincipal httpPrincipal, long groupId,
				long objectRelationshipId, long primaryKey, boolean related,
				boolean reverse, String search, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "getManyToManyObjectEntries",
				_getManyToManyObjectEntriesParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, objectRelationshipId, primaryKey, related,
				reverse, search, start, end);

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

			return (java.util.List<com.liferay.object.model.ObjectEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getManyToManyObjectEntriesCount(
			HttpPrincipal httpPrincipal, long groupId,
			long objectRelationshipId, long primaryKey, boolean related,
			boolean reverse, String search)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "getManyToManyObjectEntriesCount",
				_getManyToManyObjectEntriesCountParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, objectRelationshipId, primaryKey, related,
				reverse, search);

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

	public static com.liferay.portal.kernel.security.permission.resource.
		ModelResourcePermission<com.liferay.object.model.ObjectEntry>
				getModelResourcePermission(
					HttpPrincipal httpPrincipal, long objectDefinitionId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "getModelResourcePermission",
				_getModelResourcePermissionParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectDefinitionId);

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

			return (com.liferay.portal.kernel.security.permission.resource.
				ModelResourcePermission<com.liferay.object.model.ObjectEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.object.model.ObjectEntry>
			getObjectEntries(
				HttpPrincipal httpPrincipal, long groupId,
				long objectDefinitionId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "getObjectEntries",
				_getObjectEntriesParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, objectDefinitionId, status, start, end);

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

			return (java.util.List<com.liferay.object.model.ObjectEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getObjectEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, long objectDefinitionId,
		int status) {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "getObjectEntriesCount",
				_getObjectEntriesCountParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, objectDefinitionId, status);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
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

	public static com.liferay.object.model.ObjectEntry getObjectEntry(
			HttpPrincipal httpPrincipal, long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "getObjectEntry",
				_getObjectEntryParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryId);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntry getObjectEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "getObjectEntry",
				_getObjectEntryParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, objectDefinitionId);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntry getOrAddEmptyObjectEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "getOrAddEmptyObjectEntry",
				_getOrAddEmptyObjectEntryParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, objectDefinitionId);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.object.model.ObjectEntry>
			getRelatedObjectEntries(
				HttpPrincipal httpPrincipal, long groupId,
				long objectRelationshipId,
				com.liferay.petra.sql.dsl.expression.Predicate predicate,
				boolean preferApproved, long primaryKey, boolean related,
				String search, int start, int end,
				com.liferay.portal.kernel.search.Sort[] sorts)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "getRelatedObjectEntries",
				_getRelatedObjectEntriesParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, objectRelationshipId, predicate,
				preferApproved, primaryKey, related, search, start, end, sorts);

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

			return (java.util.List<com.liferay.object.model.ObjectEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getRelatedObjectEntriesCount(
			HttpPrincipal httpPrincipal, long groupId,
			long objectRelationshipId,
			com.liferay.petra.sql.dsl.expression.Predicate predicate,
			long primaryKey, boolean related, String search)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "getRelatedObjectEntriesCount",
				_getRelatedObjectEntriesCountParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, objectRelationshipId, predicate, primaryKey,
				related, search);

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

	public static boolean hasModelResourcePermission(
			HttpPrincipal httpPrincipal, long objectDefinitionId,
			long objectEntryId, String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "hasModelResourcePermission",
				_hasModelResourcePermissionParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectDefinitionId, objectEntryId, actionId);

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

			return ((Boolean)returnObj).booleanValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static boolean hasModelResourcePermission(
			HttpPrincipal httpPrincipal,
			com.liferay.object.model.ObjectEntry objectEntry, String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "hasModelResourcePermission",
				_hasModelResourcePermissionParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntry, actionId);

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

			return ((Boolean)returnObj).booleanValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static boolean hasModelResourcePermission(
			HttpPrincipal httpPrincipal,
			com.liferay.portal.kernel.model.User user, long objectEntryId,
			String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "hasModelResourcePermission",
				_hasModelResourcePermissionParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, user, objectEntryId, actionId);

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

			return ((Boolean)returnObj).booleanValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static boolean hasPortletResourcePermission(
			HttpPrincipal httpPrincipal, long groupId, long objectDefinitionId,
			String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "hasPortletResourcePermission",
				_hasPortletResourcePermissionParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, objectDefinitionId, actionId);

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

			return ((Boolean)returnObj).booleanValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntry moveObjectEntry(
			HttpPrincipal httpPrincipal, long objectEntryId,
			long objectEntryFolderId,
			java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "moveObjectEntry",
				_moveObjectEntryParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryId, objectEntryFolderId, values,
				serviceContext);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntry moveObjectEntryToTrash(
			HttpPrincipal httpPrincipal,
			com.liferay.object.model.ObjectEntry objectEntry,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "moveObjectEntryToTrash",
				_moveObjectEntryToTrashParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntry, serviceContext);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntry partialUpdateObjectEntry(
			HttpPrincipal httpPrincipal, long objectEntryId,
			long objectEntryFolderId,
			java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "partialUpdateObjectEntry",
				_partialUpdateObjectEntryParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryId, objectEntryFolderId, values,
				serviceContext);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntry
			restoreObjectEntryFromTrash(
				HttpPrincipal httpPrincipal,
				com.liferay.object.model.ObjectEntry objectEntry,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "restoreObjectEntryFromTrash",
				_restoreObjectEntryFromTrashParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntry, serviceContext);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void subscribeObjectEntry(
			HttpPrincipal httpPrincipal, long groupId, long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "subscribeObjectEntry",
				_subscribeObjectEntryParameterTypes29);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, objectEntryId);

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

	public static void unsubscribeObjectEntry(
			HttpPrincipal httpPrincipal, long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "unsubscribeObjectEntry",
				_unsubscribeObjectEntryParameterTypes30);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryId);

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

	public static com.liferay.object.model.ObjectEntry updateObjectEntry(
			HttpPrincipal httpPrincipal, long objectEntryId,
			long objectEntryFolderId,
			java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "updateObjectEntry",
				_updateObjectEntryParameterTypes31);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryId, objectEntryFolderId, values,
				serviceContext);

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

			return (com.liferay.object.model.ObjectEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void validate(
			HttpPrincipal httpPrincipal, long groupId,
			com.liferay.object.model.ObjectEntry objectEntry,
			java.util.List<String> objectValidationRuleExternalReferenceCodes,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryServiceUtil.class, "validate",
				_validateParameterTypes32);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, objectEntry,
				objectValidationRuleExternalReferenceCodes, serviceContext);

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
		ObjectEntryServiceHttp.class);

	private static final Class<?>[] _addObjectEntryParameterTypes0 =
		new Class[] {
			long.class, long.class, long.class, String.class,
			java.util.Map.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addOrUpdateObjectEntryParameterTypes1 =
		new Class[] {
			String.class, long.class, long.class, long.class,
			java.util.Map.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_checkModelResourcePermissionParameterTypes2 = new Class[] {
			long.class, long.class, String.class
		};
	private static final Class<?>[]
		_checkModelResourcePermissionParameterTypes3 = new Class[] {
			com.liferay.portal.kernel.security.permission.resource.
				ModelResourcePermission.class,
			com.liferay.object.model.ObjectEntry.class, String.class
		};
	private static final Class<?>[]
		_checkModelResourcePermissionParameterTypes4 = new Class[] {
			com.liferay.object.model.ObjectEntry.class, String.class
		};
	private static final Class<?>[] _copyObjectEntryParameterTypes5 =
		new Class[] {
			long.class, long.class, java.util.Map.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteObjectEntryParameterTypes6 =
		new Class[] {long.class};
	private static final Class<?>[] _expireObjectEntryParameterTypes7 =
		new Class[] {
			long.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _fetchObjectEntryParameterTypes8 =
		new Class[] {long.class};
	private static final Class<?>[] _fetchObjectEntryParameterTypes9 =
		new Class[] {String.class, long.class, long.class};
	private static final Class<?>[] _fetchRelatedObjectEntryParameterTypes10 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[]
		_getManyToManyObjectEntriesParameterTypes11 = new Class[] {
			long.class, long.class, long.class, boolean.class, boolean.class,
			String.class, int.class, int.class
		};
	private static final Class<?>[]
		_getManyToManyObjectEntriesCountParameterTypes12 = new Class[] {
			long.class, long.class, long.class, boolean.class, boolean.class,
			String.class
		};
	private static final Class<?>[]
		_getModelResourcePermissionParameterTypes13 = new Class[] {long.class};
	private static final Class<?>[] _getObjectEntriesParameterTypes14 =
		new Class[] {long.class, long.class, int.class, int.class, int.class};
	private static final Class<?>[] _getObjectEntriesCountParameterTypes15 =
		new Class[] {long.class, long.class, int.class};
	private static final Class<?>[] _getObjectEntryParameterTypes16 =
		new Class[] {long.class};
	private static final Class<?>[] _getObjectEntryParameterTypes17 =
		new Class[] {String.class, long.class, long.class};
	private static final Class<?>[] _getOrAddEmptyObjectEntryParameterTypes18 =
		new Class[] {String.class, long.class, long.class};
	private static final Class<?>[] _getRelatedObjectEntriesParameterTypes19 =
		new Class[] {
			long.class, long.class,
			com.liferay.petra.sql.dsl.expression.Predicate.class, boolean.class,
			long.class, boolean.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.search.Sort[].class
		};
	private static final Class<?>[]
		_getRelatedObjectEntriesCountParameterTypes20 = new Class[] {
			long.class, long.class,
			com.liferay.petra.sql.dsl.expression.Predicate.class, long.class,
			boolean.class, String.class
		};
	private static final Class<?>[]
		_hasModelResourcePermissionParameterTypes21 = new Class[] {
			long.class, long.class, String.class
		};
	private static final Class<?>[]
		_hasModelResourcePermissionParameterTypes22 = new Class[] {
			com.liferay.object.model.ObjectEntry.class, String.class
		};
	private static final Class<?>[]
		_hasModelResourcePermissionParameterTypes23 = new Class[] {
			com.liferay.portal.kernel.model.User.class, long.class, String.class
		};
	private static final Class<?>[]
		_hasPortletResourcePermissionParameterTypes24 = new Class[] {
			long.class, long.class, String.class
		};
	private static final Class<?>[] _moveObjectEntryParameterTypes25 =
		new Class[] {
			long.class, long.class, java.util.Map.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _moveObjectEntryToTrashParameterTypes26 =
		new Class[] {
			com.liferay.object.model.ObjectEntry.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _partialUpdateObjectEntryParameterTypes27 =
		new Class[] {
			long.class, long.class, java.util.Map.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_restoreObjectEntryFromTrashParameterTypes28 = new Class[] {
			com.liferay.object.model.ObjectEntry.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _subscribeObjectEntryParameterTypes29 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _unsubscribeObjectEntryParameterTypes30 =
		new Class[] {long.class};
	private static final Class<?>[] _updateObjectEntryParameterTypes31 =
		new Class[] {
			long.class, long.class, java.util.Map.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _validateParameterTypes32 = new Class[] {
		long.class, com.liferay.object.model.ObjectEntry.class,
		java.util.List.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};

}
// LIFERAY-SERVICE-BUILDER-HASH:2115413867