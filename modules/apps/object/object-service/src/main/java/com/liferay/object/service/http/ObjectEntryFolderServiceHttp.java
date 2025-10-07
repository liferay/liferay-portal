/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.http;

import com.liferay.object.service.ObjectEntryFolderServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>ObjectEntryFolderServiceUtil</code> service
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
public class ObjectEntryFolderServiceHttp {

	public static com.liferay.object.model.ObjectEntryFolder
			addObjectEntryFolder(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, long parentObjectEntryFolderId,
				String description,
				java.util.Map<java.util.Locale, String> labelMap, String name,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class, "addObjectEntryFolder",
				_addObjectEntryFolderParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId,
				parentObjectEntryFolderId, description, labelMap, name,
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

			return (com.liferay.object.model.ObjectEntryFolder)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntryFolder
			deleteObjectEntryFolder(
				HttpPrincipal httpPrincipal, long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class, "deleteObjectEntryFolder",
				_deleteObjectEntryFolderParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryFolderId);

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

			return (com.liferay.object.model.ObjectEntryFolder)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntryFolder
			deleteObjectEntryFolderByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class,
				"deleteObjectEntryFolderByExternalReferenceCode",
				_deleteObjectEntryFolderByExternalReferenceCodeParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, companyId);

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

			return (com.liferay.object.model.ObjectEntryFolder)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntryFolder
			fetchObjectEntryFolder(
				HttpPrincipal httpPrincipal, long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class, "fetchObjectEntryFolder",
				_fetchObjectEntryFolderParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryFolderId);

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

			return (com.liferay.object.model.ObjectEntryFolder)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntryFolder
			fetchObjectEntryFolderByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class,
				"fetchObjectEntryFolderByExternalReferenceCode",
				_fetchObjectEntryFolderByExternalReferenceCodeParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, companyId);

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

			return (com.liferay.object.model.ObjectEntryFolder)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntryFolder
			getObjectEntryFolder(
				HttpPrincipal httpPrincipal, long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class, "getObjectEntryFolder",
				_getObjectEntryFolderParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryFolderId);

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

			return (com.liferay.object.model.ObjectEntryFolder)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntryFolder
			getObjectEntryFolderByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class,
				"getObjectEntryFolderByExternalReferenceCode",
				_getObjectEntryFolderByExternalReferenceCodeParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, companyId);

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

			return (com.liferay.object.model.ObjectEntryFolder)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.object.model.ObjectEntryFolder>
			getObjectEntryFolders(
				HttpPrincipal httpPrincipal, long groupId, long companyId,
				long parentObjectEntryFolderId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class, "getObjectEntryFolders",
				_getObjectEntryFoldersParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, companyId, parentObjectEntryFolderId, start,
				end);

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

			return (java.util.List<com.liferay.object.model.ObjectEntryFolder>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getObjectEntryFoldersCount(
			HttpPrincipal httpPrincipal, long groupId, long companyId,
			long parentObjectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class,
				"getObjectEntryFoldersCount",
				_getObjectEntryFoldersCountParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, companyId, parentObjectEntryFolderId);

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

	public static com.liferay.object.model.ObjectEntryFolder
			getOrAddEmptyObjectEntryFolder(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, long companyId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class,
				"getOrAddEmptyObjectEntryFolder",
				_getOrAddEmptyObjectEntryFolderParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, companyId,
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

			return (com.liferay.object.model.ObjectEntryFolder)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntryFolder
			moveObjectEntryFolderToTrash(
				HttpPrincipal httpPrincipal,
				com.liferay.object.model.ObjectEntryFolder objectEntryFolder,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class,
				"moveObjectEntryFolderToTrash",
				_moveObjectEntryFolderToTrashParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryFolder, serviceContext);

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

			return (com.liferay.object.model.ObjectEntryFolder)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntryFolder
			restoreObjectEntryFolderFromTrash(
				HttpPrincipal httpPrincipal,
				com.liferay.object.model.ObjectEntryFolder objectEntryFolder,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class,
				"restoreObjectEntryFolderFromTrash",
				_restoreObjectEntryFolderFromTrashParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryFolder, serviceContext);

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

			return (com.liferay.object.model.ObjectEntryFolder)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void subscribeObjectEntryFolder(
			HttpPrincipal httpPrincipal, long groupId, long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class,
				"subscribeObjectEntryFolder",
				_subscribeObjectEntryFolderParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, objectEntryFolderId);

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

	public static void unsubscribeObjectEntryFolder(
			HttpPrincipal httpPrincipal, long groupId, long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class,
				"unsubscribeObjectEntryFolder",
				_unsubscribeObjectEntryFolderParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, objectEntryFolderId);

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

	public static com.liferay.object.model.ObjectEntryFolder
			updateObjectEntryFolder(
				HttpPrincipal httpPrincipal, long objectEntryFolderId,
				long parentObjectEntryFolderId, String description,
				java.util.Map<java.util.Locale, String> labelMap, String name,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryFolderServiceUtil.class, "updateObjectEntryFolder",
				_updateObjectEntryFolderParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryFolderId, parentObjectEntryFolderId,
				description, labelMap, name, serviceContext);

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

			return (com.liferay.object.model.ObjectEntryFolder)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		ObjectEntryFolderServiceHttp.class);

	private static final Class<?>[] _addObjectEntryFolderParameterTypes0 =
		new Class[] {
			String.class, long.class, long.class, String.class,
			java.util.Map.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteObjectEntryFolderParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[]
		_deleteObjectEntryFolderByExternalReferenceCodeParameterTypes2 =
			new Class[] {String.class, long.class, long.class};
	private static final Class<?>[] _fetchObjectEntryFolderParameterTypes3 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchObjectEntryFolderByExternalReferenceCodeParameterTypes4 =
			new Class[] {String.class, long.class, long.class};
	private static final Class<?>[] _getObjectEntryFolderParameterTypes5 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getObjectEntryFolderByExternalReferenceCodeParameterTypes6 =
			new Class[] {String.class, long.class, long.class};
	private static final Class<?>[] _getObjectEntryFoldersParameterTypes7 =
		new Class[] {long.class, long.class, long.class, int.class, int.class};
	private static final Class<?>[] _getObjectEntryFoldersCountParameterTypes8 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[]
		_getOrAddEmptyObjectEntryFolderParameterTypes9 = new Class[] {
			String.class, long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_moveObjectEntryFolderToTrashParameterTypes10 = new Class[] {
			com.liferay.object.model.ObjectEntryFolder.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_restoreObjectEntryFolderFromTrashParameterTypes11 = new Class[] {
			com.liferay.object.model.ObjectEntryFolder.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_subscribeObjectEntryFolderParameterTypes12 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[]
		_unsubscribeObjectEntryFolderParameterTypes13 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[] _updateObjectEntryFolderParameterTypes14 =
		new Class[] {
			long.class, long.class, String.class, java.util.Map.class,
			String.class, com.liferay.portal.kernel.service.ServiceContext.class
		};

}