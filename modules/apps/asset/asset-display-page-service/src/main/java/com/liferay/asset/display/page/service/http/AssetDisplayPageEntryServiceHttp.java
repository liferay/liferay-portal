/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.service.http;

import com.liferay.asset.display.page.service.AssetDisplayPageEntryServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>AssetDisplayPageEntryServiceUtil</code> service
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
public class AssetDisplayPageEntryServiceHttp {

	public static com.liferay.asset.display.page.model.AssetDisplayPageEntry
			addAssetDisplayPageEntry(
				HttpPrincipal httpPrincipal, long groupId, long classNameId,
				long classPK, long layoutPageTemplateEntryId, int type,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		try {
			MethodKey methodKey = new MethodKey(
				AssetDisplayPageEntryServiceUtil.class,
				"addAssetDisplayPageEntry",
				_addAssetDisplayPageEntryParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classPK,
				layoutPageTemplateEntryId, type, serviceContext);

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

			return (com.liferay.asset.display.page.model.AssetDisplayPageEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.asset.display.page.model.AssetDisplayPageEntry
			addAssetDisplayPageEntry(
				HttpPrincipal httpPrincipal, long groupId, long classNameId,
				long classPK, long layoutPageTemplateEntryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		try {
			MethodKey methodKey = new MethodKey(
				AssetDisplayPageEntryServiceUtil.class,
				"addAssetDisplayPageEntry",
				_addAssetDisplayPageEntryParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classPK,
				layoutPageTemplateEntryId, serviceContext);

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

			return (com.liferay.asset.display.page.model.AssetDisplayPageEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteAssetDisplayPageEntry(
			HttpPrincipal httpPrincipal, long groupId, long classNameId,
			long classPK)
		throws Exception {

		try {
			MethodKey methodKey = new MethodKey(
				AssetDisplayPageEntryServiceUtil.class,
				"deleteAssetDisplayPageEntry",
				_deleteAssetDisplayPageEntryParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classPK);

			try {
				TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof Exception) {
					throw (Exception)exception;
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

	public static com.liferay.asset.display.page.model.AssetDisplayPageEntry
			fetchAssetDisplayPageEntry(
				HttpPrincipal httpPrincipal, long groupId, long classNameId,
				long classPK)
		throws Exception {

		try {
			MethodKey methodKey = new MethodKey(
				AssetDisplayPageEntryServiceUtil.class,
				"fetchAssetDisplayPageEntry",
				_fetchAssetDisplayPageEntryParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classPK);

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

			return (com.liferay.asset.display.page.model.AssetDisplayPageEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.asset.display.page.model.AssetDisplayPageEntry>
			getAssetDisplayPageEntries(
				HttpPrincipal httpPrincipal, long classNameId, long classTypeId,
				long layoutPageTemplateEntryId, boolean defaultTemplate,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.asset.display.page.model.AssetDisplayPageEntry>
						orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetDisplayPageEntryServiceUtil.class,
				"getAssetDisplayPageEntries",
				_getAssetDisplayPageEntriesParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, classNameId, classTypeId, layoutPageTemplateEntryId,
				defaultTemplate, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.asset.display.page.model.AssetDisplayPageEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.asset.display.page.model.AssetDisplayPageEntry>
			getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
				HttpPrincipal httpPrincipal, long layoutPageTemplateEntryId) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetDisplayPageEntryServiceUtil.class,
				"getAssetDisplayPageEntriesByLayoutPageTemplateEntryId",
				_getAssetDisplayPageEntriesByLayoutPageTemplateEntryIdParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutPageTemplateEntryId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.asset.display.page.model.AssetDisplayPageEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.asset.display.page.model.AssetDisplayPageEntry>
			getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
				HttpPrincipal httpPrincipal, long layoutPageTemplateEntryId,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.asset.display.page.model.AssetDisplayPageEntry>
						orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetDisplayPageEntryServiceUtil.class,
				"getAssetDisplayPageEntriesByLayoutPageTemplateEntryId",
				_getAssetDisplayPageEntriesByLayoutPageTemplateEntryIdParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutPageTemplateEntryId, start, end,
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
				<com.liferay.asset.display.page.model.AssetDisplayPageEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getAssetDisplayPageEntriesCount(
		HttpPrincipal httpPrincipal, long classNameId, long classTypeId,
		long layoutPageTemplateEntryId, boolean defaultTemplate) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetDisplayPageEntryServiceUtil.class,
				"getAssetDisplayPageEntriesCount",
				_getAssetDisplayPageEntriesCountParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, classNameId, classTypeId, layoutPageTemplateEntryId,
				defaultTemplate);

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

	public static int
		getAssetDisplayPageEntriesCountByLayoutPageTemplateEntryId(
			HttpPrincipal httpPrincipal, long layoutPageTemplateEntryId) {

		try {
			MethodKey methodKey = new MethodKey(
				AssetDisplayPageEntryServiceUtil.class,
				"getAssetDisplayPageEntriesCountByLayoutPageTemplateEntryId",
				_getAssetDisplayPageEntriesCountByLayoutPageTemplateEntryIdParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutPageTemplateEntryId);

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

	public static com.liferay.asset.display.page.model.AssetDisplayPageEntry
			updateAssetDisplayPageEntry(
				HttpPrincipal httpPrincipal, long assetDisplayPageEntryId,
				long layoutPageTemplateEntryId, int type)
		throws Exception {

		try {
			MethodKey methodKey = new MethodKey(
				AssetDisplayPageEntryServiceUtil.class,
				"updateAssetDisplayPageEntry",
				_updateAssetDisplayPageEntryParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetDisplayPageEntryId, layoutPageTemplateEntryId,
				type);

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

			return (com.liferay.asset.display.page.model.AssetDisplayPageEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		AssetDisplayPageEntryServiceHttp.class);

	private static final Class<?>[] _addAssetDisplayPageEntryParameterTypes0 =
		new Class[] {
			long.class, long.class, long.class, long.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addAssetDisplayPageEntryParameterTypes1 =
		new Class[] {
			long.class, long.class, long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_deleteAssetDisplayPageEntryParameterTypes2 = new Class[] {
			long.class, long.class, long.class
		};
	private static final Class<?>[] _fetchAssetDisplayPageEntryParameterTypes3 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[] _getAssetDisplayPageEntriesParameterTypes4 =
		new Class[] {
			long.class, long.class, long.class, boolean.class, int.class,
			int.class, com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getAssetDisplayPageEntriesByLayoutPageTemplateEntryIdParameterTypes5 =
			new Class[] {long.class};
	private static final Class<?>[]
		_getAssetDisplayPageEntriesByLayoutPageTemplateEntryIdParameterTypes6 =
			new Class[] {
				long.class, int.class, int.class,
				com.liferay.portal.kernel.util.OrderByComparator.class
			};
	private static final Class<?>[]
		_getAssetDisplayPageEntriesCountParameterTypes7 = new Class[] {
			long.class, long.class, long.class, boolean.class
		};
	private static final Class<?>[]
		_getAssetDisplayPageEntriesCountByLayoutPageTemplateEntryIdParameterTypes8 =
			new Class[] {long.class};
	private static final Class<?>[]
		_updateAssetDisplayPageEntryParameterTypes9 = new Class[] {
			long.class, long.class, int.class
		};

}