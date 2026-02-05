/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.http;

import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>LayoutPageTemplateEntryServiceUtil</code> service
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
public class LayoutPageTemplateEntryServiceHttp {

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			addLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, long layoutPageTemplateCollectionId,
				String layoutPageTemplateEntryKey, long classNameId,
				long classTypeId, String classTypeKey, String name, int type,
				long previewFileEntryId, boolean defaultTemplate,
				long layoutPrototypeId, long plid, long masterLayoutPlid,
				int status,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"addLayoutPageTemplateEntry",
				_addLayoutPageTemplateEntryParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId,
				layoutPageTemplateCollectionId, layoutPageTemplateEntryKey,
				classNameId, classTypeId, classTypeKey, name, type,
				previewFileEntryId, defaultTemplate, layoutPrototypeId, plid,
				masterLayoutPlid, status, serviceContext);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			addLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, long layoutPageTemplateCollectionId,
				String layoutPageTemplateEntryKey, long classNameId,
				long classTypeId, String classTypeKey, String name,
				long masterLayoutPlid, int status,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"addLayoutPageTemplateEntry",
				_addLayoutPageTemplateEntryParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId,
				layoutPageTemplateCollectionId, layoutPageTemplateEntryKey,
				classNameId, classTypeId, classTypeKey, name, masterLayoutPlid,
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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			addLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, long layoutPageTemplateCollectionId,
				String layoutPageTemplateEntryKey, String name, int type,
				long masterLayoutPlid, int status,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"addLayoutPageTemplateEntry",
				_addLayoutPageTemplateEntryParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId,
				layoutPageTemplateCollectionId, layoutPageTemplateEntryKey,
				name, type, masterLayoutPlid, status, serviceContext);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			copyLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, long groupId,
				long layoutPageTemplateCollectionId,
				long sourceLayoutPageTemplateEntryId, boolean copyPermissions,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"copyLayoutPageTemplateEntry",
				_copyLayoutPageTemplateEntryParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId,
				sourceLayoutPageTemplateEntryId, copyPermissions,
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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			createLayoutPageTemplateEntryFromLayout(
				HttpPrincipal httpPrincipal, long segmentsExperienceId,
				com.liferay.portal.kernel.model.Layout sourceLayout,
				String name, long targetLayoutPageTemplateCollectionId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"createLayoutPageTemplateEntryFromLayout",
				_createLayoutPageTemplateEntryFromLayoutParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsExperienceId, sourceLayout, name,
				targetLayoutPageTemplateCollectionId, serviceContext);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteLayoutPageTemplateEntries(
			HttpPrincipal httpPrincipal, long[] layoutPageTemplateEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"deleteLayoutPageTemplateEntries",
				_deleteLayoutPageTemplateEntriesParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutPageTemplateEntryIds);

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

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			deleteLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, long layoutPageTemplateEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"deleteLayoutPageTemplateEntry",
				_deleteLayoutPageTemplateEntryParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutPageTemplateEntryId);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			deleteLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"deleteLayoutPageTemplateEntry",
				_deleteLayoutPageTemplateEntryParameterTypes7);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
		fetchDefaultLayoutPageTemplateEntry(
			HttpPrincipal httpPrincipal, long groupId, int type, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"fetchDefaultLayoutPageTemplateEntry",
				_fetchDefaultLayoutPageTemplateEntryParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, type, status);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
		fetchDefaultLayoutPageTemplateEntry(
			HttpPrincipal httpPrincipal, long groupId, long classNameId,
			long classTypeId) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"fetchDefaultLayoutPageTemplateEntry",
				_fetchDefaultLayoutPageTemplateEntryParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classTypeId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			fetchLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, long layoutPageTemplateEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"fetchLayoutPageTemplateEntry",
				_fetchLayoutPageTemplateEntryParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutPageTemplateEntryId);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			fetchLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, long groupId,
				long layoutPageTemplateCollectionId, String name, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"fetchLayoutPageTemplateEntry",
				_fetchLayoutPageTemplateEntryParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, name, type);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			fetchLayoutPageTemplateEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"fetchLayoutPageTemplateEntryByExternalReferenceCode",
				_fetchLayoutPageTemplateEntryByExternalReferenceCodeParameterTypes12);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			fetchLayoutPageTemplateEntryByPlid(
				HttpPrincipal httpPrincipal, long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"fetchLayoutPageTemplateEntryByPlid",
				_fetchLayoutPageTemplateEntryByPlidParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(methodKey, plid);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
		fetchLayoutPageTemplateEntryByUuidAndGroupId(
			HttpPrincipal httpPrincipal, String uuid, long groupId) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"fetchLayoutPageTemplateEntryByUuidAndGroupId",
				_fetchLayoutPageTemplateEntryByUuidAndGroupIdParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, uuid, groupId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<Object>
		getLayoutPageCollectionsAndLayoutPageTemplateEntries(
			HttpPrincipal httpPrincipal, long groupId,
			long layoutPageTemplateCollectionId, int type, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<Object>
				orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageCollectionsAndLayoutPageTemplateEntries",
				_getLayoutPageCollectionsAndLayoutPageTemplateEntriesParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, type, start,
				end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
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
		getLayoutPageCollectionsAndLayoutPageTemplateEntries(
			HttpPrincipal httpPrincipal, long groupId,
			long layoutPageTemplateCollectionId, long classNameId,
			long classTypeId, String name, int type, int status, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator<Object>
				orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageCollectionsAndLayoutPageTemplateEntries",
				_getLayoutPageCollectionsAndLayoutPageTemplateEntriesParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, classNameId,
				classTypeId, name, type, status, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
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

	public static int getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId,
		long layoutPageTemplateCollectionId, int type) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount",
				_getLayoutPageCollectionsAndLayoutPageTemplateEntriesCountParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, type);

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

	public static int getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId,
		long layoutPageTemplateCollectionId, long classNameId, long classTypeId,
		String name, int type, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount",
				_getLayoutPageCollectionsAndLayoutPageTemplateEntriesCountParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, classNameId,
				classTypeId, name, type, status);

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

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, status);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, int type, int status,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, type, status, start, end,
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
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, int type, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes21);

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
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, int[] types,
				int status, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, types, status, start, end,
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
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, int[] types,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes23);

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
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId,
				long layoutPageTemplateCollectionId, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, status);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, long classNameId,
				int type, boolean defaultTemplate) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, type, defaultTemplate);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId,
				long layoutPageTemplateCollectionId, int start, int end) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId,
				long layoutPageTemplateCollectionId, int status, int start,
				int end) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, status,
				start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId,
				long layoutPageTemplateCollectionId, int status, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, status,
				start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId,
				long layoutPageTemplateCollectionId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes29);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, start, end,
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
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, long classNameId,
				long classTypeId, int type) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes30);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classTypeId, type);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, long classNameId,
				long classTypeId, int type, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes31);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classTypeId, type, status);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, long classNameId,
				long classTypeId, int type, int status, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes32);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classTypeId, type, status,
				start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, long classNameId,
				long classTypeId, int type, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes33);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classTypeId, type, start, end,
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
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, long classNameId,
				long classTypeId, String name, int type, int status, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes34);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classTypeId, name, type,
				status, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, long classNameId,
				long classTypeId, String name, int type, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes35);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classTypeId, name, type, start,
				end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId,
				long layoutPageTemplateCollectionId, String name, int status,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes36);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, name,
				status, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId,
				long layoutPageTemplateCollectionId, String name, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes37);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, name, start,
				end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, String name,
				int type, int status, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes38);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, type, status, start, end,
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
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, String name,
				int type, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes39);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, type, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, String name,
				int[] types, int status, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes40);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, types, status, start, end,
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
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntries(
				HttpPrincipal httpPrincipal, long groupId, String name,
				int[] types, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntries",
				_getLayoutPageTemplateEntriesParameterTypes41);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, types, start, end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.layout.page.template.model.LayoutPageTemplateEntry>
			getLayoutPageTemplateEntriesByType(
				HttpPrincipal httpPrincipal, long groupId,
				long layoutPageTemplateCollectionId, int type, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.layout.page.template.model.
						LayoutPageTemplateEntry> orderByComparator) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesByType",
				_getLayoutPageTemplateEntriesByTypeParameterTypes42);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, type, start,
				end, orderByComparator);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateEntry>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, int type) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes43);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, type);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, int type, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes44);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, type, status);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, int[] types) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes45);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, int[] types, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes46);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, types, status);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId,
		long layoutPageTemplateCollectionId) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes47);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId,
		long layoutPageTemplateCollectionId, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes48);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, status);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, long classNameId,
		long classTypeId, int type) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes49);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classTypeId, type);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, long classNameId,
		long classTypeId, int type, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes50);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classTypeId, type, status);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, long classNameId,
		long classTypeId, String name, int type) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes51);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classTypeId, name, type);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, long classNameId,
		long classTypeId, String name, int type, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes52);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classTypeId, name, type,
				status);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId,
		long layoutPageTemplateCollectionId, String name) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes53);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, name);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId,
		long layoutPageTemplateCollectionId, String name, int status) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes54);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, name,
				status);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, String name, int type) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes55);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, type);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, String name, int type,
		int status) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes56);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, type, status);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, String name, int[] types) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes57);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, types);

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

	public static int getLayoutPageTemplateEntriesCount(
		HttpPrincipal httpPrincipal, long groupId, String name, int[] types,
		int status) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCount",
				_getLayoutPageTemplateEntriesCountParameterTypes58);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, types, status);

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

	public static int getLayoutPageTemplateEntriesCountByType(
		HttpPrincipal httpPrincipal, long groupId,
		long layoutPageTemplateCollectionId, int type) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntriesCountByType",
				_getLayoutPageTemplateEntriesCountByTypeParameterTypes59);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateCollectionId, type);

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

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			getLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, long layoutPageTemplateEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntry",
				_getLayoutPageTemplateEntryParameterTypes60);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutPageTemplateEntryId);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			getLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, long groupId,
				String layoutPageTemplateEntryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntry",
				_getLayoutPageTemplateEntryParameterTypes61);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutPageTemplateEntryKey);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			getLayoutPageTemplateEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"getLayoutPageTemplateEntryByExternalReferenceCode",
				_getLayoutPageTemplateEntryByExternalReferenceCodeParameterTypes62);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			moveLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, long layoutPageTemplateEntryId,
				long targetLayoutPageTemplateCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"moveLayoutPageTemplateEntry",
				_moveLayoutPageTemplateEntryParameterTypes63);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutPageTemplateEntryId,
				targetLayoutPageTemplateCollectionId);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			updateLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, long layoutPageTemplateEntryId,
				boolean defaultTemplate)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"updateLayoutPageTemplateEntry",
				_updateLayoutPageTemplateEntryParameterTypes64);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutPageTemplateEntryId, defaultTemplate);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			updateLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, long layoutPageTemplateEntryId,
				long previewFileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"updateLayoutPageTemplateEntry",
				_updateLayoutPageTemplateEntryParameterTypes65);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutPageTemplateEntryId, previewFileEntryId);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			updateLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, long layoutPageTemplateEntryId,
				long classNameId, long classTypeId, String classTypeKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"updateLayoutPageTemplateEntry",
				_updateLayoutPageTemplateEntryParameterTypes66);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutPageTemplateEntryId, classNameId, classTypeId,
				classTypeKey);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			updateLayoutPageTemplateEntry(
				HttpPrincipal httpPrincipal, long layoutPageTemplateEntryId,
				String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class,
				"updateLayoutPageTemplateEntry",
				_updateLayoutPageTemplateEntryParameterTypes67);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutPageTemplateEntryId, name);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateEntry
			updateStatus(
				HttpPrincipal httpPrincipal, long layoutPageTemplateEntryId,
				int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateEntryServiceUtil.class, "updateStatus",
				_updateStatusParameterTypes68);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutPageTemplateEntryId, status);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateEntry)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateEntryServiceHttp.class);

	private static final Class<?>[] _addLayoutPageTemplateEntryParameterTypes0 =
		new Class[] {
			String.class, long.class, long.class, String.class, long.class,
			long.class, String.class, String.class, int.class, long.class,
			boolean.class, long.class, long.class, long.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addLayoutPageTemplateEntryParameterTypes1 =
		new Class[] {
			String.class, long.class, long.class, String.class, long.class,
			long.class, String.class, String.class, long.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addLayoutPageTemplateEntryParameterTypes2 =
		new Class[] {
			String.class, long.class, long.class, String.class, String.class,
			int.class, long.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_copyLayoutPageTemplateEntryParameterTypes3 = new Class[] {
			long.class, long.class, long.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_createLayoutPageTemplateEntryFromLayoutParameterTypes4 = new Class[] {
			long.class, com.liferay.portal.kernel.model.Layout.class,
			String.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_deleteLayoutPageTemplateEntriesParameterTypes5 = new Class[] {
			long[].class
		};
	private static final Class<?>[]
		_deleteLayoutPageTemplateEntryParameterTypes6 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_deleteLayoutPageTemplateEntryParameterTypes7 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[]
		_fetchDefaultLayoutPageTemplateEntryParameterTypes8 = new Class[] {
			long.class, int.class, int.class
		};
	private static final Class<?>[]
		_fetchDefaultLayoutPageTemplateEntryParameterTypes9 = new Class[] {
			long.class, long.class, long.class
		};
	private static final Class<?>[]
		_fetchLayoutPageTemplateEntryParameterTypes10 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_fetchLayoutPageTemplateEntryParameterTypes11 = new Class[] {
			long.class, long.class, String.class, int.class
		};
	private static final Class<?>[]
		_fetchLayoutPageTemplateEntryByExternalReferenceCodeParameterTypes12 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_fetchLayoutPageTemplateEntryByPlidParameterTypes13 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_fetchLayoutPageTemplateEntryByUuidAndGroupIdParameterTypes14 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_getLayoutPageCollectionsAndLayoutPageTemplateEntriesParameterTypes15 =
			new Class[] {
				long.class, long.class, int.class, int.class, int.class,
				com.liferay.portal.kernel.util.OrderByComparator.class
			};
	private static final Class<?>[]
		_getLayoutPageCollectionsAndLayoutPageTemplateEntriesParameterTypes16 =
			new Class[] {
				long.class, long.class, long.class, long.class, String.class,
				int.class, int.class, int.class, int.class,
				com.liferay.portal.kernel.util.OrderByComparator.class
			};
	private static final Class<?>[]
		_getLayoutPageCollectionsAndLayoutPageTemplateEntriesCountParameterTypes17 =
			new Class[] {long.class, long.class, int.class};
	private static final Class<?>[]
		_getLayoutPageCollectionsAndLayoutPageTemplateEntriesCountParameterTypes18 =
			new Class[] {
				long.class, long.class, long.class, long.class, String.class,
				int.class, int.class
			};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes19 = new Class[] {
			long.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes20 = new Class[] {
			long.class, int.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes21 = new Class[] {
			long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes22 = new Class[] {
			long.class, int[].class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes23 = new Class[] {
			long.class, int[].class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes24 = new Class[] {
			long.class, long.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes25 = new Class[] {
			long.class, long.class, int.class, boolean.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes26 = new Class[] {
			long.class, long.class, int.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes27 = new Class[] {
			long.class, long.class, int.class, int.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes28 = new Class[] {
			long.class, long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes29 = new Class[] {
			long.class, long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes30 = new Class[] {
			long.class, long.class, long.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes31 = new Class[] {
			long.class, long.class, long.class, int.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes32 = new Class[] {
			long.class, long.class, long.class, int.class, int.class, int.class,
			int.class, com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes33 = new Class[] {
			long.class, long.class, long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes34 = new Class[] {
			long.class, long.class, long.class, String.class, int.class,
			int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes35 = new Class[] {
			long.class, long.class, long.class, String.class, int.class,
			int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes36 = new Class[] {
			long.class, long.class, String.class, int.class, int.class,
			int.class, com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes37 = new Class[] {
			long.class, long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes38 = new Class[] {
			long.class, String.class, int.class, int.class, int.class,
			int.class, com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes39 = new Class[] {
			long.class, String.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes40 = new Class[] {
			long.class, String.class, int[].class, int.class, int.class,
			int.class, com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesParameterTypes41 = new Class[] {
			long.class, String.class, int[].class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesByTypeParameterTypes42 = new Class[] {
			long.class, long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes43 = new Class[] {
			long.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes44 = new Class[] {
			long.class, int.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes45 = new Class[] {
			long.class, int[].class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes46 = new Class[] {
			long.class, int[].class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes47 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes48 = new Class[] {
			long.class, long.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes49 = new Class[] {
			long.class, long.class, long.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes50 = new Class[] {
			long.class, long.class, long.class, int.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes51 = new Class[] {
			long.class, long.class, long.class, String.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes52 = new Class[] {
			long.class, long.class, long.class, String.class, int.class,
			int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes53 = new Class[] {
			long.class, long.class, String.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes54 = new Class[] {
			long.class, long.class, String.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes55 = new Class[] {
			long.class, String.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes56 = new Class[] {
			long.class, String.class, int.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes57 = new Class[] {
			long.class, String.class, int[].class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountParameterTypes58 = new Class[] {
			long.class, String.class, int[].class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntriesCountByTypeParameterTypes59 = new Class[] {
			long.class, long.class, int.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntryParameterTypes60 = new Class[] {long.class};
	private static final Class<?>[]
		_getLayoutPageTemplateEntryParameterTypes61 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_getLayoutPageTemplateEntryByExternalReferenceCodeParameterTypes62 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_moveLayoutPageTemplateEntryParameterTypes63 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[]
		_updateLayoutPageTemplateEntryParameterTypes64 = new Class[] {
			long.class, boolean.class
		};
	private static final Class<?>[]
		_updateLayoutPageTemplateEntryParameterTypes65 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[]
		_updateLayoutPageTemplateEntryParameterTypes66 = new Class[] {
			long.class, long.class, long.class, String.class
		};
	private static final Class<?>[]
		_updateLayoutPageTemplateEntryParameterTypes67 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _updateStatusParameterTypes68 =
		new Class[] {long.class, int.class};

}