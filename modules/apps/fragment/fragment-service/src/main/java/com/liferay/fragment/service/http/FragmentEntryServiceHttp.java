/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.http;

import com.liferay.fragment.service.FragmentEntryServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>FragmentEntryServiceUtil</code> service
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
public class FragmentEntryServiceHttp {

	public static com.liferay.fragment.model.FragmentEntry addFragmentEntry(
			HttpPrincipal httpPrincipal, long groupId,
			long fragmentCollectionId, String fragmentEntryKey, String name,
			String css, String html, String js, String configuration,
			long previewFileEntryId, int type, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "addFragmentEntry",
				_addFragmentEntryParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, fragmentEntryKey,
				name, css, html, js, configuration, previewFileEntryId, type,
				status, serviceContext);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentEntry addFragmentEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, long fragmentCollectionId, String fragmentEntryKey,
			String name, String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean marketplace, boolean readOnly, int type, String typeOptions,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "addFragmentEntry",
				_addFragmentEntryParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, fragmentCollectionId,
				fragmentEntryKey, name, css, html, js, cacheable, configuration,
				icon, previewFileEntryId, marketplace, readOnly, type,
				typeOptions, status, serviceContext);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentEntry copyFragmentEntry(
			HttpPrincipal httpPrincipal, long groupId,
			long sourceFragmentEntryId, long fragmentCollectionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "copyFragmentEntry",
				_copyFragmentEntryParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, sourceFragmentEntryId, fragmentCollectionId,
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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteFragmentEntries(
			HttpPrincipal httpPrincipal, long[] fragmentEntriesIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "deleteFragmentEntries",
				_deleteFragmentEntriesParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentEntriesIds);

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

	public static com.liferay.fragment.model.FragmentEntry deleteFragmentEntry(
			HttpPrincipal httpPrincipal, long fragmentEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "deleteFragmentEntry",
				_deleteFragmentEntryParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentEntryId);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentEntry deleteFragmentEntry(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "deleteFragmentEntry",
				_deleteFragmentEntryParameterTypes5);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentEntry fetchDraft(
		HttpPrincipal httpPrincipal, long primaryKey) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "fetchDraft",
				_fetchDraftParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, primaryKey);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentEntry fetchFragmentEntry(
			HttpPrincipal httpPrincipal, long fragmentEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "fetchFragmentEntry",
				_fetchFragmentEntryParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentEntryId);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentEntry getDraft(
			HttpPrincipal httpPrincipal, long primaryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "getDraft",
				_getDraftParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, primaryKey);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<Object>
			getFragmentCompositionsAndFragmentEntries(
				HttpPrincipal httpPrincipal, long groupId,
				long fragmentCollectionId, int status, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator<?>
					orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class,
				"getFragmentCompositionsAndFragmentEntries",
				_getFragmentCompositionsAndFragmentEntriesParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, status, start, end,
				orderByComparator);

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

			return (java.util.List<Object>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<Object>
			getFragmentCompositionsAndFragmentEntries(
				HttpPrincipal httpPrincipal, long groupId,
				long fragmentCollectionId, String name, int status, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator<?>
					orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class,
				"getFragmentCompositionsAndFragmentEntries",
				_getFragmentCompositionsAndFragmentEntriesParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, name, status, start,
				end, orderByComparator);

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

			return (java.util.List<Object>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getFragmentCompositionsAndFragmentEntriesCount(
			HttpPrincipal httpPrincipal, long groupId,
			long fragmentCollectionId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class,
				"getFragmentCompositionsAndFragmentEntriesCount",
				_getFragmentCompositionsAndFragmentEntriesCountParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, status);

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

	public static int getFragmentCompositionsAndFragmentEntriesCount(
			HttpPrincipal httpPrincipal, long groupId,
			long fragmentCollectionId, String name, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class,
				"getFragmentCompositionsAndFragmentEntriesCount",
				_getFragmentCompositionsAndFragmentEntriesCountParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, name, status);

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

	public static java.util.List<com.liferay.fragment.model.FragmentEntry>
			getFragmentEntries(
				HttpPrincipal httpPrincipal, long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "getFragmentEntries",
				_getFragmentEntriesParameterTypes13);

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

			return (java.util.List<com.liferay.fragment.model.FragmentEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentEntry>
			getFragmentEntries(
				HttpPrincipal httpPrincipal, long groupId,
				long fragmentCollectionId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "getFragmentEntries",
				_getFragmentEntriesParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, start, end);

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

			return (java.util.List<com.liferay.fragment.model.FragmentEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentEntry>
			getFragmentEntries(
				HttpPrincipal httpPrincipal, long groupId,
				long fragmentCollectionId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.fragment.model.FragmentEntry>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "getFragmentEntries",
				_getFragmentEntriesParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, start, end,
				orderByComparator);

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

			return (java.util.List<com.liferay.fragment.model.FragmentEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentEntry>
			getFragmentEntriesByName(
				HttpPrincipal httpPrincipal, long groupId,
				long fragmentCollectionId, String name, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.fragment.model.FragmentEntry>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "getFragmentEntriesByName",
				_getFragmentEntriesByNameParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, name, start, end,
				orderByComparator);

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

			return (java.util.List<com.liferay.fragment.model.FragmentEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentEntry>
			getFragmentEntriesByNameAndStatus(
				HttpPrincipal httpPrincipal, long groupId,
				long fragmentCollectionId, String name, int status, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.fragment.model.FragmentEntry>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class,
				"getFragmentEntriesByNameAndStatus",
				_getFragmentEntriesByNameAndStatusParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, name, status, start,
				end, orderByComparator);

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

			return (java.util.List<com.liferay.fragment.model.FragmentEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentEntry>
			getFragmentEntriesByStatus(
				HttpPrincipal httpPrincipal, long groupId,
				long fragmentCollectionId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "getFragmentEntriesByStatus",
				_getFragmentEntriesByStatusParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, status);

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

			return (java.util.List<com.liferay.fragment.model.FragmentEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentEntry>
			getFragmentEntriesByStatus(
				HttpPrincipal httpPrincipal, long groupId,
				long fragmentCollectionId, int status, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.fragment.model.FragmentEntry>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "getFragmentEntriesByStatus",
				_getFragmentEntriesByStatusParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, status, start, end,
				orderByComparator);

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

			return (java.util.List<com.liferay.fragment.model.FragmentEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentEntry>
			getFragmentEntriesByType(
				HttpPrincipal httpPrincipal, long groupId,
				long fragmentCollectionId, int type, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.fragment.model.FragmentEntry>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "getFragmentEntriesByType",
				_getFragmentEntriesByTypeParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, type, start, end,
				orderByComparator);

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

			return (java.util.List<com.liferay.fragment.model.FragmentEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentEntry>
			getFragmentEntriesByTypeAndStatus(
				HttpPrincipal httpPrincipal, long groupId,
				long fragmentCollectionId, int type, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class,
				"getFragmentEntriesByTypeAndStatus",
				_getFragmentEntriesByTypeAndStatusParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, type, status);

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

			return (java.util.List<com.liferay.fragment.model.FragmentEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentEntry>
			getFragmentEntriesByTypeAndStatus(
				HttpPrincipal httpPrincipal, long groupId,
				long fragmentCollectionId, int type, int status, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.fragment.model.FragmentEntry>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class,
				"getFragmentEntriesByTypeAndStatus",
				_getFragmentEntriesByTypeAndStatusParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, type, status, start,
				end, orderByComparator);

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

			return (java.util.List<com.liferay.fragment.model.FragmentEntry>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getFragmentEntriesCount(
			HttpPrincipal httpPrincipal, long groupId,
			long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "getFragmentEntriesCount",
				_getFragmentEntriesCountParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId);

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

	public static int getFragmentEntriesCountByName(
			HttpPrincipal httpPrincipal, long groupId,
			long fragmentCollectionId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "getFragmentEntriesCountByName",
				_getFragmentEntriesCountByNameParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, name);

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

	public static int getFragmentEntriesCountByNameAndStatus(
			HttpPrincipal httpPrincipal, long groupId,
			long fragmentCollectionId, String name, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class,
				"getFragmentEntriesCountByNameAndStatus",
				_getFragmentEntriesCountByNameAndStatusParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, name, status);

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

	public static int getFragmentEntriesCountByStatus(
			HttpPrincipal httpPrincipal, long groupId,
			long fragmentCollectionId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class,
				"getFragmentEntriesCountByStatus",
				_getFragmentEntriesCountByStatusParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, status);

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

	public static int getFragmentEntriesCountByType(
			HttpPrincipal httpPrincipal, long groupId,
			long fragmentCollectionId, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "getFragmentEntriesCountByType",
				_getFragmentEntriesCountByTypeParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, type);

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

	public static int getFragmentEntriesCountByTypeAndStatus(
			HttpPrincipal httpPrincipal, long groupId,
			long fragmentCollectionId, int type, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class,
				"getFragmentEntriesCountByTypeAndStatus",
				_getFragmentEntriesCountByTypeAndStatusParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCollectionId, type, status);

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

	public static com.liferay.fragment.model.FragmentEntry
			getFragmentEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class,
				"getFragmentEntryByExternalReferenceCode",
				_getFragmentEntryByExternalReferenceCodeParameterTypes29);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
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
				FragmentEntryServiceUtil.class, "getTempFileNames",
				_getTempFileNamesParameterTypes30);

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

	public static com.liferay.fragment.model.FragmentEntry moveFragmentEntry(
			HttpPrincipal httpPrincipal, long fragmentEntryId,
			long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "moveFragmentEntry",
				_moveFragmentEntryParameterTypes31);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentEntryId, fragmentCollectionId);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentEntry publishDraft(
			HttpPrincipal httpPrincipal,
			com.liferay.fragment.model.FragmentEntry draftFragmentEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "publishDraft",
				_publishDraftParameterTypes32);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, draftFragmentEntry);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentEntry updateDraft(
			HttpPrincipal httpPrincipal,
			com.liferay.fragment.model.FragmentEntry draftFragmentEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "updateDraft",
				_updateDraftParameterTypes33);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, draftFragmentEntry);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentEntry updateFragmentEntry(
			HttpPrincipal httpPrincipal,
			com.liferay.fragment.model.FragmentEntry fragmentEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "updateFragmentEntry",
				_updateFragmentEntryParameterTypes34);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentEntry);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentEntry updateFragmentEntry(
			HttpPrincipal httpPrincipal, long fragmentEntryId,
			boolean cacheable)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "updateFragmentEntry",
				_updateFragmentEntryParameterTypes35);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentEntryId, cacheable);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentEntry updateFragmentEntry(
			HttpPrincipal httpPrincipal, long fragmentEntryId,
			long previewFileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "updateFragmentEntry",
				_updateFragmentEntryParameterTypes36);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentEntryId, previewFileEntryId);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentEntry updateFragmentEntry(
			HttpPrincipal httpPrincipal, long fragmentEntryId,
			long fragmentCollectionId, String name, String css, String html,
			String js, boolean cacheable, String configuration, String icon,
			long previewFileEntryId, boolean readOnly, String typeOptions,
			int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "updateFragmentEntry",
				_updateFragmentEntryParameterTypes37);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentEntryId, fragmentCollectionId, name, css,
				html, js, cacheable, configuration, icon, previewFileEntryId,
				readOnly, typeOptions, status);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentEntry updateFragmentEntry(
			HttpPrincipal httpPrincipal, long fragmentEntryId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentEntryServiceUtil.class, "updateFragmentEntry",
				_updateFragmentEntryParameterTypes38);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentEntryId, name);

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

			return (com.liferay.fragment.model.FragmentEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		FragmentEntryServiceHttp.class);

	private static final Class<?>[] _addFragmentEntryParameterTypes0 =
		new Class[] {
			long.class, long.class, String.class, String.class, String.class,
			String.class, String.class, String.class, long.class, int.class,
			int.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addFragmentEntryParameterTypes1 =
		new Class[] {
			String.class, long.class, long.class, String.class, String.class,
			String.class, String.class, String.class, boolean.class,
			String.class, String.class, long.class, boolean.class,
			boolean.class, int.class, String.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _copyFragmentEntryParameterTypes2 =
		new Class[] {
			long.class, long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteFragmentEntriesParameterTypes3 =
		new Class[] {long[].class};
	private static final Class<?>[] _deleteFragmentEntryParameterTypes4 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteFragmentEntryParameterTypes5 =
		new Class[] {String.class, long.class};
	private static final Class<?>[] _fetchDraftParameterTypes6 = new Class[] {
		long.class
	};
	private static final Class<?>[] _fetchFragmentEntryParameterTypes7 =
		new Class[] {long.class};
	private static final Class<?>[] _getDraftParameterTypes8 = new Class[] {
		long.class
	};
	private static final Class<?>[]
		_getFragmentCompositionsAndFragmentEntriesParameterTypes9 =
			new Class[] {
				long.class, long.class, int.class, int.class, int.class,
				com.liferay.portal.kernel.util.OrderByComparator.class
			};
	private static final Class<?>[]
		_getFragmentCompositionsAndFragmentEntriesParameterTypes10 =
			new Class[] {
				long.class, long.class, String.class, int.class, int.class,
				int.class,
				com.liferay.portal.kernel.util.OrderByComparator.class
			};
	private static final Class<?>[]
		_getFragmentCompositionsAndFragmentEntriesCountParameterTypes11 =
			new Class[] {long.class, long.class, int.class};
	private static final Class<?>[]
		_getFragmentCompositionsAndFragmentEntriesCountParameterTypes12 =
			new Class[] {long.class, long.class, String.class, int.class};
	private static final Class<?>[] _getFragmentEntriesParameterTypes13 =
		new Class[] {long.class};
	private static final Class<?>[] _getFragmentEntriesParameterTypes14 =
		new Class[] {long.class, long.class, int.class, int.class};
	private static final Class<?>[] _getFragmentEntriesParameterTypes15 =
		new Class[] {
			long.class, long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getFragmentEntriesByNameParameterTypes16 =
		new Class[] {
			long.class, long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getFragmentEntriesByNameAndStatusParameterTypes17 = new Class[] {
			long.class, long.class, String.class, int.class, int.class,
			int.class, com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getFragmentEntriesByStatusParameterTypes18 = new Class[] {
			long.class, long.class, int.class
		};
	private static final Class<?>[]
		_getFragmentEntriesByStatusParameterTypes19 = new Class[] {
			long.class, long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getFragmentEntriesByTypeParameterTypes20 =
		new Class[] {
			long.class, long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getFragmentEntriesByTypeAndStatusParameterTypes21 = new Class[] {
			long.class, long.class, int.class, int.class
		};
	private static final Class<?>[]
		_getFragmentEntriesByTypeAndStatusParameterTypes22 = new Class[] {
			long.class, long.class, int.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getFragmentEntriesCountParameterTypes23 =
		new Class[] {long.class, long.class};
	private static final Class<?>[]
		_getFragmentEntriesCountByNameParameterTypes24 = new Class[] {
			long.class, long.class, String.class
		};
	private static final Class<?>[]
		_getFragmentEntriesCountByNameAndStatusParameterTypes25 = new Class[] {
			long.class, long.class, String.class, int.class
		};
	private static final Class<?>[]
		_getFragmentEntriesCountByStatusParameterTypes26 = new Class[] {
			long.class, long.class, int.class
		};
	private static final Class<?>[]
		_getFragmentEntriesCountByTypeParameterTypes27 = new Class[] {
			long.class, long.class, int.class
		};
	private static final Class<?>[]
		_getFragmentEntriesCountByTypeAndStatusParameterTypes28 = new Class[] {
			long.class, long.class, int.class, int.class
		};
	private static final Class<?>[]
		_getFragmentEntryByExternalReferenceCodeParameterTypes29 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _getTempFileNamesParameterTypes30 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _moveFragmentEntryParameterTypes31 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _publishDraftParameterTypes32 =
		new Class[] {com.liferay.fragment.model.FragmentEntry.class};
	private static final Class<?>[] _updateDraftParameterTypes33 = new Class[] {
		com.liferay.fragment.model.FragmentEntry.class
	};
	private static final Class<?>[] _updateFragmentEntryParameterTypes34 =
		new Class[] {com.liferay.fragment.model.FragmentEntry.class};
	private static final Class<?>[] _updateFragmentEntryParameterTypes35 =
		new Class[] {long.class, boolean.class};
	private static final Class<?>[] _updateFragmentEntryParameterTypes36 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _updateFragmentEntryParameterTypes37 =
		new Class[] {
			long.class, long.class, String.class, String.class, String.class,
			String.class, boolean.class, String.class, String.class, long.class,
			boolean.class, String.class, int.class
		};
	private static final Class<?>[] _updateFragmentEntryParameterTypes38 =
		new Class[] {long.class, String.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1425033567