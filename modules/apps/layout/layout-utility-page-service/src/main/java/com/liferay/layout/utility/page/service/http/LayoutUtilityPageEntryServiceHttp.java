/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.service.http;

import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>LayoutUtilityPageEntryServiceUtil</code> service
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
public class LayoutUtilityPageEntryServiceHttp {

	public static com.liferay.layout.utility.page.model.LayoutUtilityPageEntry
			addLayoutUtilityPageEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, long plid, long previewFileEntryId,
				boolean defaultLayoutUtilityPageEntry, String name, String type,
				String masterLayoutPageTemplateEntryERC,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"addLayoutUtilityPageEntry",
				_addLayoutUtilityPageEntryParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, plid,
				previewFileEntryId, defaultLayoutUtilityPageEntry, name, type,
				masterLayoutPageTemplateEntryERC, serviceContext);

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

			return (com.liferay.layout.utility.page.model.
				LayoutUtilityPageEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.utility.page.model.LayoutUtilityPageEntry
			copyLayoutUtilityPageEntry(
				HttpPrincipal httpPrincipal, long groupId,
				long sourceLayoutUtilityPageEntryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"copyLayoutUtilityPageEntry",
				_copyLayoutUtilityPageEntryParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, sourceLayoutUtilityPageEntryId,
				serviceContext);

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

			return (com.liferay.layout.utility.page.model.
				LayoutUtilityPageEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.utility.page.model.LayoutUtilityPageEntry
			deleteLayoutUtilityPageEntry(
				HttpPrincipal httpPrincipal, long layoutUtilityPageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"deleteLayoutUtilityPageEntry",
				_deleteLayoutUtilityPageEntryParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutUtilityPageEntryId);

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

			return (com.liferay.layout.utility.page.model.
				LayoutUtilityPageEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.utility.page.model.LayoutUtilityPageEntry
			deleteLayoutUtilityPageEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"deleteLayoutUtilityPageEntry",
				_deleteLayoutUtilityPageEntryParameterTypes3);

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

			return (com.liferay.layout.utility.page.model.
				LayoutUtilityPageEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.utility.page.model.LayoutUtilityPageEntry
			fetchLayoutUtilityPageEntry(
				HttpPrincipal httpPrincipal, long layoutUtilityPageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"fetchLayoutUtilityPageEntry",
				_fetchLayoutUtilityPageEntryParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutUtilityPageEntryId);

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

			return (com.liferay.layout.utility.page.model.
				LayoutUtilityPageEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.utility.page.model.LayoutUtilityPageEntry
			fetchLayoutUtilityPageEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"fetchLayoutUtilityPageEntryByExternalReferenceCode",
				_fetchLayoutUtilityPageEntryByExternalReferenceCodeParameterTypes5);

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

			return (com.liferay.layout.utility.page.model.
				LayoutUtilityPageEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.utility.page.model.LayoutUtilityPageEntry
			getDefaultLayoutUtilityPageEntry(
				HttpPrincipal httpPrincipal, long groupId, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"getDefaultLayoutUtilityPageEntry",
				_getDefaultLayoutUtilityPageEntryParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, type);

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

			return (com.liferay.layout.utility.page.model.
				LayoutUtilityPageEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.utility.page.model.LayoutUtilityPageEntry>
			getLayoutUtilityPageEntries(
				HttpPrincipal httpPrincipal, long groupId) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"getLayoutUtilityPageEntries",
				_getLayoutUtilityPageEntriesParameterTypes7);

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
				<com.liferay.layout.utility.page.model.LayoutUtilityPageEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.utility.page.model.LayoutUtilityPageEntry>
			getLayoutUtilityPageEntries(
				HttpPrincipal httpPrincipal, long groupId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.utility.page.model.
						LayoutUtilityPageEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"getLayoutUtilityPageEntries",
				_getLayoutUtilityPageEntriesParameterTypes8);

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
				<com.liferay.layout.utility.page.model.LayoutUtilityPageEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.utility.page.model.LayoutUtilityPageEntry>
			getLayoutUtilityPageEntries(
				HttpPrincipal httpPrincipal, long groupId, String type,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.utility.page.model.
						LayoutUtilityPageEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"getLayoutUtilityPageEntries",
				_getLayoutUtilityPageEntriesParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, type, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.utility.page.model.LayoutUtilityPageEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.utility.page.model.LayoutUtilityPageEntry>
			getLayoutUtilityPageEntries(
				HttpPrincipal httpPrincipal, long groupId, String keyword,
				String[] types, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.utility.page.model.
						LayoutUtilityPageEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"getLayoutUtilityPageEntries",
				_getLayoutUtilityPageEntriesParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, keyword, types, start, end,
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
				<com.liferay.layout.utility.page.model.LayoutUtilityPageEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.utility.page.model.LayoutUtilityPageEntry>
			getLayoutUtilityPageEntries(
				HttpPrincipal httpPrincipal, long groupId, String[] types,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.utility.page.model.
						LayoutUtilityPageEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"getLayoutUtilityPageEntries",
				_getLayoutUtilityPageEntriesParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, types, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.utility.page.model.LayoutUtilityPageEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getLayoutUtilityPageEntriesCount(
		HttpPrincipal httpPrincipal, long groupId) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"getLayoutUtilityPageEntriesCount",
				_getLayoutUtilityPageEntriesCountParameterTypes12);

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

	public static int getLayoutUtilityPageEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, String keyword,
		String[] types) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"getLayoutUtilityPageEntriesCount",
				_getLayoutUtilityPageEntriesCountParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, keyword, types);

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

	public static int getLayoutUtilityPageEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, String[] types) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"getLayoutUtilityPageEntriesCount",
				_getLayoutUtilityPageEntriesCountParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, types);

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

	public static com.liferay.layout.utility.page.model.LayoutUtilityPageEntry
			getLayoutUtilityPageEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"getLayoutUtilityPageEntryByExternalReferenceCode",
				_getLayoutUtilityPageEntryByExternalReferenceCodeParameterTypes15);

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

			return (com.liferay.layout.utility.page.model.
				LayoutUtilityPageEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.utility.page.model.LayoutUtilityPageEntry
			setDefaultLayoutUtilityPageEntry(
				HttpPrincipal httpPrincipal, long layoutUtilityPageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"setDefaultLayoutUtilityPageEntry",
				_setDefaultLayoutUtilityPageEntryParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutUtilityPageEntryId);

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

			return (com.liferay.layout.utility.page.model.
				LayoutUtilityPageEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.utility.page.model.LayoutUtilityPageEntry
			unsetDefaultLayoutUtilityPageEntry(
				HttpPrincipal httpPrincipal, long layoutUtilityPageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"unsetDefaultLayoutUtilityPageEntry",
				_unsetDefaultLayoutUtilityPageEntryParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutUtilityPageEntryId);

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

			return (com.liferay.layout.utility.page.model.
				LayoutUtilityPageEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.utility.page.model.LayoutUtilityPageEntry
			updateLayoutUtilityPageEntry(
				HttpPrincipal httpPrincipal, long layoutUtilityPageEntryId,
				long previewFileEntryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"updateLayoutUtilityPageEntry",
				_updateLayoutUtilityPageEntryParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutUtilityPageEntryId, previewFileEntryId,
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

			return (com.liferay.layout.utility.page.model.
				LayoutUtilityPageEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.utility.page.model.LayoutUtilityPageEntry
			updateLayoutUtilityPageEntry(
				HttpPrincipal httpPrincipal, long layoutUtilityPageEntryId,
				String name,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutUtilityPageEntryServiceUtil.class,
				"updateLayoutUtilityPageEntry",
				_updateLayoutUtilityPageEntryParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutUtilityPageEntryId, name, serviceContext);

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

			return (com.liferay.layout.utility.page.model.
				LayoutUtilityPageEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		LayoutUtilityPageEntryServiceHttp.class);

	private static final Class<?>[] _addLayoutUtilityPageEntryParameterTypes0 =
		new Class[] {
			String.class, long.class, long.class, long.class, boolean.class,
			String.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _copyLayoutUtilityPageEntryParameterTypes1 =
		new Class[] {
			long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_deleteLayoutUtilityPageEntryParameterTypes2 = new Class[] {long.class};
	private static final Class<?>[]
		_deleteLayoutUtilityPageEntryParameterTypes3 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[]
		_fetchLayoutUtilityPageEntryParameterTypes4 = new Class[] {long.class};
	private static final Class<?>[]
		_fetchLayoutUtilityPageEntryByExternalReferenceCodeParameterTypes5 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_getDefaultLayoutUtilityPageEntryParameterTypes6 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_getLayoutUtilityPageEntriesParameterTypes7 = new Class[] {long.class};
	private static final Class<?>[]
		_getLayoutUtilityPageEntriesParameterTypes8 = new Class[] {
			long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutUtilityPageEntriesParameterTypes9 = new Class[] {
			long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutUtilityPageEntriesParameterTypes10 = new Class[] {
			long.class, String.class, String[].class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutUtilityPageEntriesParameterTypes11 = new Class[] {
			long.class, String[].class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutUtilityPageEntriesCountParameterTypes12 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_getLayoutUtilityPageEntriesCountParameterTypes13 = new Class[] {
			long.class, String.class, String[].class
		};
	private static final Class<?>[]
		_getLayoutUtilityPageEntriesCountParameterTypes14 = new Class[] {
			long.class, String[].class
		};
	private static final Class<?>[]
		_getLayoutUtilityPageEntryByExternalReferenceCodeParameterTypes15 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_setDefaultLayoutUtilityPageEntryParameterTypes16 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_unsetDefaultLayoutUtilityPageEntryParameterTypes17 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_updateLayoutUtilityPageEntryParameterTypes18 = new Class[] {
			long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateLayoutUtilityPageEntryParameterTypes19 = new Class[] {
			long.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:-2008021252