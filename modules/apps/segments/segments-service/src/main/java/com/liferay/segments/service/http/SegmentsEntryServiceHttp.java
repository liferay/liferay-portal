/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.segments.service.SegmentsEntryServiceUtil;

/**
 * Provides the HTTP utility for the
 * <code>SegmentsEntryServiceUtil</code> service
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
 * @author Eduardo Garcia
 * @generated
 */
public class SegmentsEntryServiceHttp {

	public static com.liferay.segments.model.SegmentsEntry addSegmentsEntry(
			HttpPrincipal httpPrincipal, String segmentsEntryKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, String criteria,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class, "addSegmentsEntry",
				_addSegmentsEntryParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsEntryKey, nameMap, descriptionMap, active,
				criteria, serviceContext);

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

			return (com.liferay.segments.model.SegmentsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.segments.model.SegmentsEntry addSegmentsEntry(
			HttpPrincipal httpPrincipal, String segmentsEntryKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, String criteria, String source,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class, "addSegmentsEntry",
				_addSegmentsEntryParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsEntryKey, nameMap, descriptionMap, active,
				criteria, source, serviceContext);

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

			return (com.liferay.segments.model.SegmentsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void addSegmentsEntryClassPKs(
			HttpPrincipal httpPrincipal, long segmentsEntryId, long[] classPKs,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class, "addSegmentsEntryClassPKs",
				_addSegmentsEntryClassPKsParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsEntryId, classPKs, serviceContext);

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

	public static com.liferay.segments.model.SegmentsEntry deleteSegmentsEntry(
			HttpPrincipal httpPrincipal, long segmentsEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class, "deleteSegmentsEntry",
				_deleteSegmentsEntryParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsEntryId);

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

			return (com.liferay.segments.model.SegmentsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteSegmentsEntryClassPKs(
			HttpPrincipal httpPrincipal, long segmentsEntryId, long[] classPKs)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class, "deleteSegmentsEntryClassPKs",
				_deleteSegmentsEntryClassPKsParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsEntryId, classPKs);

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

	public static com.liferay.segments.model.SegmentsEntry
			fetchSegmentsEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String segmentsEntryERC,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class,
				"fetchSegmentsEntryByExternalReferenceCode",
				_fetchSegmentsEntryByExternalReferenceCodeParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsEntryERC, groupId);

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

			return (com.liferay.segments.model.SegmentsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.segments.model.SegmentsEntry>
		getSegmentsEntries(HttpPrincipal httpPrincipal, long groupId) {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class, "getSegmentsEntries",
				_getSegmentsEntriesParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(methodKey, groupId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.segments.model.SegmentsEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.segments.model.SegmentsEntry>
		getSegmentsEntries(
			HttpPrincipal httpPrincipal, long groupId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.segments.model.SegmentsEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class, "getSegmentsEntries",
				_getSegmentsEntriesParameterTypes7);

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

			return (java.util.List<com.liferay.segments.model.SegmentsEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.segments.model.SegmentsEntry>
		getSegmentsEntries(
			HttpPrincipal httpPrincipal, long groupId, String source, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.segments.model.SegmentsEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class, "getSegmentsEntries",
				_getSegmentsEntriesParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, source, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.segments.model.SegmentsEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getSegmentsEntriesCount(
		HttpPrincipal httpPrincipal, long groupId) {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class, "getSegmentsEntriesCount",
				_getSegmentsEntriesCountParameterTypes9);

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

	public static int getSegmentsEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, String source) {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class, "getSegmentsEntriesCount",
				_getSegmentsEntriesCountParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, source);

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

	public static com.liferay.segments.model.SegmentsEntry getSegmentsEntry(
			HttpPrincipal httpPrincipal, long segmentsEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class, "getSegmentsEntry",
				_getSegmentsEntryParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsEntryId);

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

			return (com.liferay.segments.model.SegmentsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.segments.model.SegmentsEntry
			getSegmentsEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String segmentsEntryERC,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class,
				"getSegmentsEntryByExternalReferenceCode",
				_getSegmentsEntryByExternalReferenceCodeParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsEntryERC, groupId);

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

			return (com.liferay.segments.model.SegmentsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.segments.model.SegmentsEntry> searchSegmentsEntries(
				HttpPrincipal httpPrincipal, long companyId, long groupId,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class, "searchSegmentsEntries",
				_searchSegmentsEntriesParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, groupId, keywords, start, end, sort);

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

			return (com.liferay.portal.kernel.search.BaseModelSearchResult
				<com.liferay.segments.model.SegmentsEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.segments.model.SegmentsEntry updateSegmentsEntry(
			HttpPrincipal httpPrincipal, long segmentsEntryId,
			String segmentsEntryKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, String criteria,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsEntryServiceUtil.class, "updateSegmentsEntry",
				_updateSegmentsEntryParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsEntryId, segmentsEntryKey, nameMap,
				descriptionMap, active, criteria, serviceContext);

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

			return (com.liferay.segments.model.SegmentsEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		SegmentsEntryServiceHttp.class);

	private static final Class<?>[] _addSegmentsEntryParameterTypes0 =
		new Class[] {
			String.class, java.util.Map.class, java.util.Map.class,
			boolean.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addSegmentsEntryParameterTypes1 =
		new Class[] {
			String.class, java.util.Map.class, java.util.Map.class,
			boolean.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addSegmentsEntryClassPKsParameterTypes2 =
		new Class[] {
			long.class, long[].class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteSegmentsEntryParameterTypes3 =
		new Class[] {long.class};
	private static final Class<?>[]
		_deleteSegmentsEntryClassPKsParameterTypes4 = new Class[] {
			long.class, long[].class
		};
	private static final Class<?>[]
		_fetchSegmentsEntryByExternalReferenceCodeParameterTypes5 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _getSegmentsEntriesParameterTypes6 =
		new Class[] {long.class};
	private static final Class<?>[] _getSegmentsEntriesParameterTypes7 =
		new Class[] {
			long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getSegmentsEntriesParameterTypes8 =
		new Class[] {
			long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getSegmentsEntriesCountParameterTypes9 =
		new Class[] {long.class};
	private static final Class<?>[] _getSegmentsEntriesCountParameterTypes10 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _getSegmentsEntryParameterTypes11 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getSegmentsEntryByExternalReferenceCodeParameterTypes12 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _searchSegmentsEntriesParameterTypes13 =
		new Class[] {
			long.class, long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.search.Sort.class
		};
	private static final Class<?>[] _updateSegmentsEntryParameterTypes14 =
		new Class[] {
			long.class, String.class, java.util.Map.class, java.util.Map.class,
			boolean.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};

}