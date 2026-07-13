/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.http;

import com.liferay.fragment.service.FragmentCollectionServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>FragmentCollectionServiceUtil</code> service
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
public class FragmentCollectionServiceHttp {

	public static com.liferay.fragment.model.FragmentCollection
			addFragmentCollection(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, String name, String description,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "addFragmentCollection",
				_addFragmentCollectionParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, name, description,
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

			return (com.liferay.fragment.model.FragmentCollection)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentCollection
			addFragmentCollection(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, String fragmentCollectionKey, String name,
				String description, boolean marketplace,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "addFragmentCollection",
				_addFragmentCollectionParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId,
				fragmentCollectionKey, name, description, marketplace,
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

			return (com.liferay.fragment.model.FragmentCollection)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentCollection
			deleteFragmentCollection(
				HttpPrincipal httpPrincipal, long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "deleteFragmentCollection",
				_deleteFragmentCollectionParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCollectionId);

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

			return (com.liferay.fragment.model.FragmentCollection)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentCollection
			deleteFragmentCollection(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "deleteFragmentCollection",
				_deleteFragmentCollectionParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId);

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

			return (com.liferay.fragment.model.FragmentCollection)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteFragmentCollections(
			HttpPrincipal httpPrincipal, long[] fragmentCollectionIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"deleteFragmentCollections",
				_deleteFragmentCollectionsParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCollectionIds);

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

	public static com.liferay.fragment.model.FragmentCollection
			fetchFragmentCollection(
				HttpPrincipal httpPrincipal, long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "fetchFragmentCollection",
				_fetchFragmentCollectionParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCollectionId);

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

			return (com.liferay.fragment.model.FragmentCollection)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getExportableFragmentCollections(
			HttpPrincipal httpPrincipal, long[] fragmentCollectionIds) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getExportableFragmentCollections",
				_getExportableFragmentCollectionsParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCollectionIds);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getExportableFragmentCollectionsByGroupId(
			HttpPrincipal httpPrincipal, long[] groupIds, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.fragment.model.FragmentCollection>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getExportableFragmentCollectionsByGroupId",
				_getExportableFragmentCollectionsByGroupIdParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getExportableFragmentCollectionsByGroupId(
			HttpPrincipal httpPrincipal, long[] groupIds, String name,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.fragment.model.FragmentCollection>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getExportableFragmentCollectionsByGroupId",
				_getExportableFragmentCollectionsByGroupIdParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, name, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getExportableFragmentCollectionsCount(
		HttpPrincipal httpPrincipal, long[] fragmentCollectionIds) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getExportableFragmentCollectionsCount",
				_getExportableFragmentCollectionsCountParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCollectionIds);

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

	public static int getExportableFragmentCollectionsCountByGroupId(
		HttpPrincipal httpPrincipal, long[] groupIds) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getExportableFragmentCollectionsCountByGroupId",
				_getExportableFragmentCollectionsCountByGroupIdParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds);

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

	public static int getExportableFragmentCollectionsCountByGroupId(
		HttpPrincipal httpPrincipal, long[] groupIds, String name) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getExportableFragmentCollectionsCountByGroupId",
				_getExportableFragmentCollectionsCountByGroupIdParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, name);

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

	public static com.liferay.fragment.model.FragmentCollection
			getFragmentCollection(
				HttpPrincipal httpPrincipal, long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "getFragmentCollection",
				_getFragmentCollectionParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCollectionId);

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

			return (com.liferay.fragment.model.FragmentCollection)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentCollection
			getFragmentCollectionByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getFragmentCollectionByExternalReferenceCode",
				_getFragmentCollectionByExternalReferenceCodeParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId);

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

			return (com.liferay.fragment.model.FragmentCollection)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry>
				getFragmentCollectionFileEntries(
					HttpPrincipal httpPrincipal, long fragmentCollectionId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getFragmentCollectionFileEntries",
				_getFragmentCollectionFileEntriesParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCollectionId);

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
				<com.liferay.portal.kernel.repository.model.FileEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getFragmentCollections(HttpPrincipal httpPrincipal, long groupId) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "getFragmentCollections",
				_getFragmentCollectionsParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(methodKey, groupId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getFragmentCollections(
			HttpPrincipal httpPrincipal, long groupId, boolean includeSystem) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "getFragmentCollections",
				_getFragmentCollectionsParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, includeSystem);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getFragmentCollections(
			HttpPrincipal httpPrincipal, long groupId, boolean includeSystem,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.fragment.model.FragmentCollection>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "getFragmentCollections",
				_getFragmentCollectionsParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, includeSystem, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getFragmentCollections(
			HttpPrincipal httpPrincipal, long groupId, int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "getFragmentCollections",
				_getFragmentCollectionsParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getFragmentCollections(
			HttpPrincipal httpPrincipal, long groupId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.fragment.model.FragmentCollection>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "getFragmentCollections",
				_getFragmentCollectionsParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getFragmentCollections(
			HttpPrincipal httpPrincipal, long groupId, String name,
			boolean includeSystem, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.fragment.model.FragmentCollection>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "getFragmentCollections",
				_getFragmentCollectionsParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, includeSystem, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getFragmentCollections(
			HttpPrincipal httpPrincipal, long groupId, String name, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.fragment.model.FragmentCollection>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "getFragmentCollections",
				_getFragmentCollectionsParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getFragmentCollections(HttpPrincipal httpPrincipal, long[] groupIds) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "getFragmentCollections",
				_getFragmentCollectionsParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getFragmentCollections(
			HttpPrincipal httpPrincipal, long[] groupIds, boolean marketplace,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.fragment.model.FragmentCollection>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "getFragmentCollections",
				_getFragmentCollectionsParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, marketplace, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getFragmentCollections(
			HttpPrincipal httpPrincipal, long[] groupIds, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.fragment.model.FragmentCollection>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "getFragmentCollections",
				_getFragmentCollectionsParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getFragmentCollections(
			HttpPrincipal httpPrincipal, long[] groupIds, String name,
			boolean marketplace, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.fragment.model.FragmentCollection>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "getFragmentCollections",
				_getFragmentCollectionsParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, name, marketplace, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentCollection>
		getFragmentCollections(
			HttpPrincipal httpPrincipal, long[] groupIds, String name,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.fragment.model.FragmentCollection>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "getFragmentCollections",
				_getFragmentCollectionsParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, name, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.fragment.model.FragmentCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getFragmentCollectionsCount(
		HttpPrincipal httpPrincipal, long groupId) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getFragmentCollectionsCount",
				_getFragmentCollectionsCountParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(methodKey, groupId);

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

	public static int getFragmentCollectionsCount(
		HttpPrincipal httpPrincipal, long groupId, boolean includeSystem) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getFragmentCollectionsCount",
				_getFragmentCollectionsCountParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, includeSystem);

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

	public static int getFragmentCollectionsCount(
		HttpPrincipal httpPrincipal, long groupId, String name) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getFragmentCollectionsCount",
				_getFragmentCollectionsCountParameterTypes29);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name);

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

	public static int getFragmentCollectionsCount(
		HttpPrincipal httpPrincipal, long groupId, String name,
		boolean includeSystem) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getFragmentCollectionsCount",
				_getFragmentCollectionsCountParameterTypes30);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, includeSystem);

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

	public static int getFragmentCollectionsCount(
		HttpPrincipal httpPrincipal, long[] groupIds) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getFragmentCollectionsCount",
				_getFragmentCollectionsCountParameterTypes31);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds);

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

	public static int getFragmentCollectionsCount(
		HttpPrincipal httpPrincipal, long[] groupIds, boolean marketplace) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getFragmentCollectionsCount",
				_getFragmentCollectionsCountParameterTypes32);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, marketplace);

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

	public static int getFragmentCollectionsCount(
		HttpPrincipal httpPrincipal, long[] groupIds, String name) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getFragmentCollectionsCount",
				_getFragmentCollectionsCountParameterTypes33);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, name);

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

	public static int getFragmentCollectionsCount(
		HttpPrincipal httpPrincipal, long[] groupIds, String name,
		boolean marketplace) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class,
				"getFragmentCollectionsCount",
				_getFragmentCollectionsCountParameterTypes34);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, name, marketplace);

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

	public static String[] getTempFileNames(
			HttpPrincipal httpPrincipal, long groupId, String folderName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "getTempFileNames",
				_getTempFileNamesParameterTypes35);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderName);

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

			return (String[])returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentCollection
			updateFragmentCollection(
				HttpPrincipal httpPrincipal, long fragmentCollectionId,
				String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCollectionServiceUtil.class, "updateFragmentCollection",
				_updateFragmentCollectionParameterTypes36);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCollectionId, name, description);

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

			return (com.liferay.fragment.model.FragmentCollection)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		FragmentCollectionServiceHttp.class);

	private static final Class<?>[] _addFragmentCollectionParameterTypes0 =
		new Class[] {
			String.class, long.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addFragmentCollectionParameterTypes1 =
		new Class[] {
			String.class, long.class, String.class, String.class, String.class,
			boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteFragmentCollectionParameterTypes2 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteFragmentCollectionParameterTypes3 =
		new Class[] {String.class, long.class};
	private static final Class<?>[] _deleteFragmentCollectionsParameterTypes4 =
		new Class[] {long[].class};
	private static final Class<?>[] _fetchFragmentCollectionParameterTypes5 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getExportableFragmentCollectionsParameterTypes6 = new Class[] {
			long[].class
		};
	private static final Class<?>[]
		_getExportableFragmentCollectionsByGroupIdParameterTypes7 =
			new Class[] {
				long[].class, int.class, int.class,
				com.liferay.portal.kernel.util.OrderByComparator.class
			};
	private static final Class<?>[]
		_getExportableFragmentCollectionsByGroupIdParameterTypes8 =
			new Class[] {
				long[].class, String.class, int.class, int.class,
				com.liferay.portal.kernel.util.OrderByComparator.class
			};
	private static final Class<?>[]
		_getExportableFragmentCollectionsCountParameterTypes9 = new Class[] {
			long[].class
		};
	private static final Class<?>[]
		_getExportableFragmentCollectionsCountByGroupIdParameterTypes10 =
			new Class[] {long[].class};
	private static final Class<?>[]
		_getExportableFragmentCollectionsCountByGroupIdParameterTypes11 =
			new Class[] {long[].class, String.class};
	private static final Class<?>[] _getFragmentCollectionParameterTypes12 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getFragmentCollectionByExternalReferenceCodeParameterTypes13 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_getFragmentCollectionFileEntriesParameterTypes14 = new Class[] {
			long.class
		};
	private static final Class<?>[] _getFragmentCollectionsParameterTypes15 =
		new Class[] {long.class};
	private static final Class<?>[] _getFragmentCollectionsParameterTypes16 =
		new Class[] {long.class, boolean.class};
	private static final Class<?>[] _getFragmentCollectionsParameterTypes17 =
		new Class[] {
			long.class, boolean.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getFragmentCollectionsParameterTypes18 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[] _getFragmentCollectionsParameterTypes19 =
		new Class[] {
			long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getFragmentCollectionsParameterTypes20 =
		new Class[] {
			long.class, String.class, boolean.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getFragmentCollectionsParameterTypes21 =
		new Class[] {
			long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getFragmentCollectionsParameterTypes22 =
		new Class[] {long[].class};
	private static final Class<?>[] _getFragmentCollectionsParameterTypes23 =
		new Class[] {
			long[].class, boolean.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getFragmentCollectionsParameterTypes24 =
		new Class[] {
			long[].class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getFragmentCollectionsParameterTypes25 =
		new Class[] {
			long[].class, String.class, boolean.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getFragmentCollectionsParameterTypes26 =
		new Class[] {
			long[].class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getFragmentCollectionsCountParameterTypes27 = new Class[] {long.class};
	private static final Class<?>[]
		_getFragmentCollectionsCountParameterTypes28 = new Class[] {
			long.class, boolean.class
		};
	private static final Class<?>[]
		_getFragmentCollectionsCountParameterTypes29 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_getFragmentCollectionsCountParameterTypes30 = new Class[] {
			long.class, String.class, boolean.class
		};
	private static final Class<?>[]
		_getFragmentCollectionsCountParameterTypes31 = new Class[] {
			long[].class
		};
	private static final Class<?>[]
		_getFragmentCollectionsCountParameterTypes32 = new Class[] {
			long[].class, boolean.class
		};
	private static final Class<?>[]
		_getFragmentCollectionsCountParameterTypes33 = new Class[] {
			long[].class, String.class
		};
	private static final Class<?>[]
		_getFragmentCollectionsCountParameterTypes34 = new Class[] {
			long[].class, String.class, boolean.class
		};
	private static final Class<?>[] _getTempFileNamesParameterTypes35 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _updateFragmentCollectionParameterTypes36 =
		new Class[] {long.class, String.class, String.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1898602058