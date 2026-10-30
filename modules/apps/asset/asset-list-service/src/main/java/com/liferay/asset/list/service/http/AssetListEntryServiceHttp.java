/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.http;

import com.liferay.asset.list.service.AssetListEntryServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>AssetListEntryServiceUtil</code> service
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
public class AssetListEntryServiceHttp {

	public static void addAssetEntrySelection(
			HttpPrincipal httpPrincipal, long assetListEntryId,
			long assetEntryId, long segmentsEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "addAssetEntrySelection",
				_addAssetEntrySelectionParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId, assetEntryId, segmentsEntryId,
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

	public static void addAssetEntrySelections(
			HttpPrincipal httpPrincipal, long assetListEntryId,
			long[] assetEntryIds, long segmentsEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "addAssetEntrySelections",
				_addAssetEntrySelectionsParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId, assetEntryIds, segmentsEntryId,
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

	public static void addAssetEntrySelections(
			HttpPrincipal httpPrincipal, long assetListEntryId,
			long[] assetEntryIds, String audienceEntryERC,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "addAssetEntrySelections",
				_addAssetEntrySelectionsParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId, assetEntryIds, audienceEntryERC,
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

	public static com.liferay.asset.list.model.AssetListEntry addAssetListEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, String title, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "addAssetListEntry",
				_addAssetListEntryParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, title, type,
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

			return (com.liferay.asset.list.model.AssetListEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.asset.list.model.AssetListEntry
			addDynamicAssetListEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, String title, String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "addDynamicAssetListEntry",
				_addDynamicAssetListEntryParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, title, typeSettings,
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

			return (com.liferay.asset.list.model.AssetListEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.asset.list.model.AssetListEntry
			addManualAssetListEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, String title, long[] assetEntryIds,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "addManualAssetListEntry",
				_addManualAssetListEntryParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, title, assetEntryIds,
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

			return (com.liferay.asset.list.model.AssetListEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteAssetEntrySelection(
			HttpPrincipal httpPrincipal, long assetListEntryId,
			long segmentsEntryId, int position)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "deleteAssetEntrySelection",
				_deleteAssetEntrySelectionParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId, segmentsEntryId, position);

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

	public static void deleteAssetEntrySelection(
			HttpPrincipal httpPrincipal, long assetListEntryId,
			String audienceEntryERC, int position)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "deleteAssetEntrySelection",
				_deleteAssetEntrySelectionParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId, audienceEntryERC, position);

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

	public static void deleteAssetListEntries(
			HttpPrincipal httpPrincipal, long[] assetListEntriesIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "deleteAssetListEntries",
				_deleteAssetListEntriesParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntriesIds);

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

	public static com.liferay.asset.list.model.AssetListEntry
			deleteAssetListEntry(
				HttpPrincipal httpPrincipal, long assetListEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "deleteAssetListEntry",
				_deleteAssetListEntryParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId);

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

			return (com.liferay.asset.list.model.AssetListEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteAssetListEntry(
			HttpPrincipal httpPrincipal, long assetListEntryId,
			long segmentsEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "deleteAssetListEntry",
				_deleteAssetListEntryParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId, segmentsEntryId);

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

	public static com.liferay.asset.list.model.AssetListEntry
			fetchAssetListEntry(
				HttpPrincipal httpPrincipal, long assetListEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "fetchAssetListEntry",
				_fetchAssetListEntryParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId);

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

			return (com.liferay.asset.list.model.AssetListEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.asset.list.model.AssetListEntry
			fetchAssetListEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class,
				"fetchAssetListEntryByExternalReferenceCode",
				_fetchAssetListEntryByExternalReferenceCodeParameterTypes12);

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

			return (com.liferay.asset.list.model.AssetListEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.list.model.AssetListEntry>
		getAssetListEntries(
			HttpPrincipal httpPrincipal, long groupId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.asset.list.model.AssetListEntry>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntries",
				_getAssetListEntriesParameterTypes13);

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

			return (java.util.List<com.liferay.asset.list.model.AssetListEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.list.model.AssetListEntry>
		getAssetListEntries(
			HttpPrincipal httpPrincipal, long groupId, String title, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.asset.list.model.AssetListEntry>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntries",
				_getAssetListEntriesParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, title, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.list.model.AssetListEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.list.model.AssetListEntry>
		getAssetListEntries(
			HttpPrincipal httpPrincipal, long[] groupIds, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.asset.list.model.AssetListEntry>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntries",
				_getAssetListEntriesParameterTypes15);

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

			return (java.util.List<com.liferay.asset.list.model.AssetListEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.list.model.AssetListEntry>
		getAssetListEntries(
			HttpPrincipal httpPrincipal, long[] groupIds, String title,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.asset.list.model.AssetListEntry>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntries",
				_getAssetListEntriesParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, title, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.list.model.AssetListEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.list.model.AssetListEntry>
		getAssetListEntries(
			HttpPrincipal httpPrincipal, long[] groupIds,
			String assetEntrySubtype, String assetEntryType, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.asset.list.model.AssetListEntry>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntries",
				_getAssetListEntriesParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, assetEntrySubtype, assetEntryType, start,
				end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.list.model.AssetListEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.list.model.AssetListEntry>
		getAssetListEntries(
			HttpPrincipal httpPrincipal, long[] groupIds, String title,
			String assetEntrySubtype, String assetEntryType, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.asset.list.model.AssetListEntry>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntries",
				_getAssetListEntriesParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, title, assetEntrySubtype, assetEntryType,
				start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.list.model.AssetListEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.list.model.AssetListEntry>
		getAssetListEntries(
			HttpPrincipal httpPrincipal, long[] groupIds, String title,
			String[] assetEntryTypes, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.asset.list.model.AssetListEntry>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntries",
				_getAssetListEntriesParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, title, assetEntryTypes, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.list.model.AssetListEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.asset.list.model.AssetListEntry>
		getAssetListEntries(
			HttpPrincipal httpPrincipal, long[] groupIds,
			String[] assetEntryTypes, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.asset.list.model.AssetListEntry>
					orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntries",
				_getAssetListEntriesParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, assetEntryTypes, start, end,
				orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.asset.list.model.AssetListEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getAssetListEntriesCount(
		HttpPrincipal httpPrincipal, long groupId) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntriesCount",
				_getAssetListEntriesCountParameterTypes21);

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

	public static int getAssetListEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, String title) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntriesCount",
				_getAssetListEntriesCountParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, title);

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

	public static int getAssetListEntriesCount(
		HttpPrincipal httpPrincipal, long[] groupIds) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntriesCount",
				_getAssetListEntriesCountParameterTypes23);

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

	public static int getAssetListEntriesCount(
		HttpPrincipal httpPrincipal, long[] groupIds, String title) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntriesCount",
				_getAssetListEntriesCountParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, title);

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

	public static int getAssetListEntriesCount(
		HttpPrincipal httpPrincipal, long[] groupIds, String assetEntrySubtype,
		String assetEntryType) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntriesCount",
				_getAssetListEntriesCountParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, assetEntrySubtype, assetEntryType);

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

	public static int getAssetListEntriesCount(
		HttpPrincipal httpPrincipal, long[] groupIds, String title,
		String assetEntrySubtype, String assetEntryType) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntriesCount",
				_getAssetListEntriesCountParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, title, assetEntrySubtype, assetEntryType);

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

	public static int getAssetListEntriesCount(
		HttpPrincipal httpPrincipal, long[] groupIds, String title,
		String[] assetEntryTypes) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntriesCount",
				_getAssetListEntriesCountParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, title, assetEntryTypes);

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

	public static int getAssetListEntriesCount(
		HttpPrincipal httpPrincipal, long[] groupIds,
		String[] assetEntryTypes) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntriesCount",
				_getAssetListEntriesCountParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupIds, assetEntryTypes);

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

	public static com.liferay.asset.list.model.AssetListEntry getAssetListEntry(
			HttpPrincipal httpPrincipal, long assetListEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntry",
				_getAssetListEntryParameterTypes29);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId);

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

			return (com.liferay.asset.list.model.AssetListEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.asset.list.model.AssetListEntry getAssetListEntry(
			HttpPrincipal httpPrincipal, long groupId, String assetListEntryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "getAssetListEntry",
				_getAssetListEntryParameterTypes30);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, assetListEntryKey);

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

			return (com.liferay.asset.list.model.AssetListEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.asset.list.model.AssetListEntry
			getAssetListEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class,
				"getAssetListEntryByExternalReferenceCode",
				_getAssetListEntryByExternalReferenceCodeParameterTypes31);

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

			return (com.liferay.asset.list.model.AssetListEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.asset.list.model.AssetListEntry
			getAssetListEntryByUuidAndGroupId(
				HttpPrincipal httpPrincipal, String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class,
				"getAssetListEntryByUuidAndGroupId",
				_getAssetListEntryByUuidAndGroupIdParameterTypes32);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, uuid, groupId);

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

			return (com.liferay.asset.list.model.AssetListEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void moveAssetEntrySelection(
			HttpPrincipal httpPrincipal, long assetListEntryId,
			long segmentsEntryId, int position, int newPosition)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "moveAssetEntrySelection",
				_moveAssetEntrySelectionParameterTypes33);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId, segmentsEntryId, position,
				newPosition);

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

	public static void moveAssetEntrySelection(
			HttpPrincipal httpPrincipal, long assetListEntryId,
			String audienceEntryERC, int position, int newPosition)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "moveAssetEntrySelection",
				_moveAssetEntrySelectionParameterTypes34);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId, audienceEntryERC, position,
				newPosition);

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

	public static void updateAssetListEntry(
			HttpPrincipal httpPrincipal, long assetListEntryId,
			long segmentsEntryId, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "updateAssetListEntry",
				_updateAssetListEntryParameterTypes35);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId, segmentsEntryId, typeSettings,
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

	public static com.liferay.asset.list.model.AssetListEntry
			updateAssetListEntry(
				HttpPrincipal httpPrincipal, long assetListEntryId,
				String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class, "updateAssetListEntry",
				_updateAssetListEntryParameterTypes36);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId, title);

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

			return (com.liferay.asset.list.model.AssetListEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void updateAssetListEntryTypeSettings(
			HttpPrincipal httpPrincipal, long assetListEntryId,
			long segmentsEntryId, String typeSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryServiceUtil.class,
				"updateAssetListEntryTypeSettings",
				_updateAssetListEntryTypeSettingsParameterTypes37);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId, segmentsEntryId, typeSettings);

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
		AssetListEntryServiceHttp.class);

	private static final Class<?>[] _addAssetEntrySelectionParameterTypes0 =
		new Class[] {
			long.class, long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addAssetEntrySelectionsParameterTypes1 =
		new Class[] {
			long.class, long[].class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addAssetEntrySelectionsParameterTypes2 =
		new Class[] {
			long.class, long[].class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addAssetListEntryParameterTypes3 =
		new Class[] {
			String.class, long.class, String.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addDynamicAssetListEntryParameterTypes4 =
		new Class[] {
			String.class, long.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addManualAssetListEntryParameterTypes5 =
		new Class[] {
			String.class, long.class, String.class, long[].class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteAssetEntrySelectionParameterTypes6 =
		new Class[] {long.class, long.class, int.class};
	private static final Class<?>[] _deleteAssetEntrySelectionParameterTypes7 =
		new Class[] {long.class, String.class, int.class};
	private static final Class<?>[] _deleteAssetListEntriesParameterTypes8 =
		new Class[] {long[].class};
	private static final Class<?>[] _deleteAssetListEntryParameterTypes9 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteAssetListEntryParameterTypes10 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _fetchAssetListEntryParameterTypes11 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchAssetListEntryByExternalReferenceCodeParameterTypes12 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _getAssetListEntriesParameterTypes13 =
		new Class[] {
			long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getAssetListEntriesParameterTypes14 =
		new Class[] {
			long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getAssetListEntriesParameterTypes15 =
		new Class[] {
			long[].class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getAssetListEntriesParameterTypes16 =
		new Class[] {
			long[].class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getAssetListEntriesParameterTypes17 =
		new Class[] {
			long[].class, String.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getAssetListEntriesParameterTypes18 =
		new Class[] {
			long[].class, String.class, String.class, String.class, int.class,
			int.class, com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getAssetListEntriesParameterTypes19 =
		new Class[] {
			long[].class, String.class, String[].class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getAssetListEntriesParameterTypes20 =
		new Class[] {
			long[].class, String[].class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getAssetListEntriesCountParameterTypes21 =
		new Class[] {long.class};
	private static final Class<?>[] _getAssetListEntriesCountParameterTypes22 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _getAssetListEntriesCountParameterTypes23 =
		new Class[] {long[].class};
	private static final Class<?>[] _getAssetListEntriesCountParameterTypes24 =
		new Class[] {long[].class, String.class};
	private static final Class<?>[] _getAssetListEntriesCountParameterTypes25 =
		new Class[] {long[].class, String.class, String.class};
	private static final Class<?>[] _getAssetListEntriesCountParameterTypes26 =
		new Class[] {long[].class, String.class, String.class, String.class};
	private static final Class<?>[] _getAssetListEntriesCountParameterTypes27 =
		new Class[] {long[].class, String.class, String[].class};
	private static final Class<?>[] _getAssetListEntriesCountParameterTypes28 =
		new Class[] {long[].class, String[].class};
	private static final Class<?>[] _getAssetListEntryParameterTypes29 =
		new Class[] {long.class};
	private static final Class<?>[] _getAssetListEntryParameterTypes30 =
		new Class[] {long.class, String.class};
	private static final Class<?>[]
		_getAssetListEntryByExternalReferenceCodeParameterTypes31 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_getAssetListEntryByUuidAndGroupIdParameterTypes32 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _moveAssetEntrySelectionParameterTypes33 =
		new Class[] {long.class, long.class, int.class, int.class};
	private static final Class<?>[] _moveAssetEntrySelectionParameterTypes34 =
		new Class[] {long.class, String.class, int.class, int.class};
	private static final Class<?>[] _updateAssetListEntryParameterTypes35 =
		new Class[] {
			long.class, long.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateAssetListEntryParameterTypes36 =
		new Class[] {long.class, String.class};
	private static final Class<?>[]
		_updateAssetListEntryTypeSettingsParameterTypes37 = new Class[] {
			long.class, long.class, String.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:-306727612