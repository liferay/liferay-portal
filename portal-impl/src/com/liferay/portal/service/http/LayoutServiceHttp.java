/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.LayoutServiceUtil;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>LayoutServiceUtil</code> service
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
public class LayoutServiceHttp {

	public static com.liferay.portal.kernel.model.Layout addLayout(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, boolean privateLayout, long parentLayoutId,
			long classNameId, long classPK,
			java.util.Map<java.util.Locale, String> localeNamesMap,
			java.util.Map<java.util.Locale, String> localeTitlesMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<java.util.Locale, String> keywordsMap,
			java.util.Map<java.util.Locale, String> robotsMap, String type,
			String typeSettings, boolean hidden, boolean system,
			java.util.Map<java.util.Locale, String> friendlyURLMap,
			long masterLayoutPlid,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "addLayout",
				_addLayoutParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, privateLayout,
				parentLayoutId, classNameId, classPK, localeNamesMap,
				localeTitlesMap, descriptionMap, keywordsMap, robotsMap, type,
				typeSettings, hidden, system, friendlyURLMap, masterLayoutPlid,
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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout addLayout(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, boolean privateLayout, long parentLayoutId,
			java.util.Map<java.util.Locale, String> localeNamesMap,
			java.util.Map<java.util.Locale, String> localeTitlesMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<java.util.Locale, String> keywordsMap,
			java.util.Map<java.util.Locale, String> robotsMap, String type,
			String typeSettings, boolean hidden,
			java.util.Map<java.util.Locale, String> friendlyURLMap,
			long masterLayoutPlid,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "addLayout",
				_addLayoutParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, privateLayout,
				parentLayoutId, localeNamesMap, localeTitlesMap, descriptionMap,
				keywordsMap, robotsMap, type, typeSettings, hidden,
				friendlyURLMap, masterLayoutPlid, serviceContext);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout addLayout(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, boolean privateLayout, long parentLayoutId,
			java.util.Map<java.util.Locale, String> localeNamesMap,
			java.util.Map<java.util.Locale, String> localeTitlesMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<java.util.Locale, String> keywordsMap,
			java.util.Map<java.util.Locale, String> robotsMap, String type,
			String typeSettings, boolean hidden,
			java.util.Map<java.util.Locale, String> friendlyURLMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "addLayout",
				_addLayoutParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, privateLayout,
				parentLayoutId, localeNamesMap, localeTitlesMap, descriptionMap,
				keywordsMap, robotsMap, type, typeSettings, hidden,
				friendlyURLMap, serviceContext);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout addLayout(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, boolean privateLayout, long parentLayoutId,
			String name, String title, String description, String type,
			boolean hidden, String friendlyURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "addLayout",
				_addLayoutParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, privateLayout,
				parentLayoutId, name, title, description, type, hidden,
				friendlyURL, serviceContext);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			addTempFileEntry(
				HttpPrincipal httpPrincipal, long groupId, String folderName,
				String fileName, java.io.InputStream inputStream,
				String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "addTempFileEntry",
				_addTempFileEntryParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderName, fileName, inputStream,
				mimeType);

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

			return (com.liferay.portal.kernel.repository.model.FileEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout copyLayout(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
			java.util.Map<java.util.Locale, String> localeNamesMap,
			boolean hidden, boolean system, boolean copyPermissions,
			long sourcePlid,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "copyLayout",
				_copyLayoutParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, localeNamesMap, hidden,
				system, copyPermissions, sourcePlid, serviceContext);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteLayout(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
			long layoutId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "deleteLayout",
				_deleteLayoutParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, layoutId, serviceContext);

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

	public static void deleteLayout(
			HttpPrincipal httpPrincipal, long plid,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "deleteLayout",
				_deleteLayoutParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, plid, serviceContext);

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

	public static void deleteLayout(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "deleteLayout",
				_deleteLayoutParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId);

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

	public static void deleteTempFileEntry(
			HttpPrincipal httpPrincipal, long groupId, String folderName,
			String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "deleteTempFileEntry",
				_deleteTempFileEntryParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, folderName, fileName);

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

	public static com.liferay.portal.kernel.model.Layout fetchFirstLayout(
		HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
		boolean published) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "fetchFirstLayout",
				_fetchFirstLayoutParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, published);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout fetchLayout(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
			long layoutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "fetchLayout",
				_fetchLayoutParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, layoutId);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout
			fetchLayoutByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "fetchLayoutByExternalReferenceCode",
				_fetchLayoutByExternalReferenceCodeParameterTypes12);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static long fetchLayoutPlid(
			HttpPrincipal httpPrincipal, String uuid, long groupId,
			boolean privateLayout)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "fetchLayoutPlid",
				_fetchLayoutPlidParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, uuid, groupId, privateLayout);

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

			return ((Long)returnObj).longValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Layout>
			getAncestorLayouts(HttpPrincipal httpPrincipal, long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getAncestorLayouts",
				_getAncestorLayoutsParameterTypes14);

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

			return (java.util.List<com.liferay.portal.kernel.model.Layout>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static long getControlPanelLayoutPlid(HttpPrincipal httpPrincipal)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getControlPanelLayoutPlid",
				_getControlPanelLayoutPlidParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(methodKey);

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

			return ((Long)returnObj).longValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static long getDefaultPlid(
		HttpPrincipal httpPrincipal, long groupId, boolean privateLayout) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getDefaultPlid",
				_getDefaultPlidParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return ((Long)returnObj).longValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static long getDefaultPlid(
			HttpPrincipal httpPrincipal, long groupId, long scopeGroupId,
			boolean privateLayout, String portletId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getDefaultPlid",
				_getDefaultPlidParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, scopeGroupId, privateLayout, portletId);

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

			return ((Long)returnObj).longValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static long getDefaultPlid(
			HttpPrincipal httpPrincipal, long groupId, long scopeGroupId,
			String portletId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getDefaultPlid",
				_getDefaultPlidParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, scopeGroupId, portletId);

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

			return ((Long)returnObj).longValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout
			getLayoutByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayoutByExternalReferenceCode",
				_getLayoutByExternalReferenceCodeParameterTypes19);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout
			getLayoutByUuidAndGroupId(
				HttpPrincipal httpPrincipal, String uuid, long groupId,
				boolean privateLayout)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayoutByUuidAndGroupId",
				_getLayoutByUuidAndGroupIdParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, uuid, groupId, privateLayout);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static String getLayoutName(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
			long layoutId, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayoutName",
				_getLayoutNameParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, layoutId, languageId);

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

			return (String)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static long getLayoutPlid(
			HttpPrincipal httpPrincipal, String uuid, long groupId,
			boolean privateLayout)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayoutPlid",
				_getLayoutPlidParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, uuid, groupId, privateLayout);

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

			return ((Long)returnObj).longValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.LayoutReference[]
		getLayoutReferences(
			HttpPrincipal httpPrincipal, long companyId, String portletId,
			String preferencesKey, String preferencesValue) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayoutReferences",
				_getLayoutReferencesParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, portletId, preferencesKey,
				preferencesValue);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.kernel.model.LayoutReference[])returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Layout>
		getLayouts(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayouts",
				_getLayoutsParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.portal.kernel.model.Layout>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Layout>
			getLayouts(
				HttpPrincipal httpPrincipal, long groupId,
				boolean privateLayout, long parentLayoutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayouts",
				_getLayoutsParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, parentLayoutId);

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

			return (java.util.List<com.liferay.portal.kernel.model.Layout>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Layout>
			getLayouts(
				HttpPrincipal httpPrincipal, long groupId,
				boolean privateLayout, long parentLayoutId, boolean incomplete,
				int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayouts",
				_getLayoutsParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, parentLayoutId, incomplete,
				start, end);

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

			return (java.util.List<com.liferay.portal.kernel.model.Layout>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Layout>
			getLayouts(
				HttpPrincipal httpPrincipal, long groupId,
				boolean privateLayout, long parentLayoutId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayouts",
				_getLayoutsParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, parentLayoutId, start, end);

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

			return (java.util.List<com.liferay.portal.kernel.model.Layout>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Layout>
			getLayouts(
				HttpPrincipal httpPrincipal, long groupId,
				boolean privateLayout, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayouts",
				_getLayoutsParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, type);

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

			return (java.util.List<com.liferay.portal.kernel.model.Layout>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Layout>
			getLayouts(
				HttpPrincipal httpPrincipal, long groupId,
				boolean privateLayout, String type, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayouts",
				_getLayoutsParameterTypes29);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, type, start, end);

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

			return (java.util.List<com.liferay.portal.kernel.model.Layout>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Layout>
			getLayouts(
				HttpPrincipal httpPrincipal, long groupId,
				boolean privateLayout, String keywords, String[] types,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.model.Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayouts",
				_getLayoutsParameterTypes30);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, keywords, types, start, end,
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

			return (java.util.List<com.liferay.portal.kernel.model.Layout>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Layout>
			getLayouts(
				HttpPrincipal httpPrincipal, long groupId,
				boolean privateLayout, String keywords, String[] types,
				int[] statuses, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.model.Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayouts",
				_getLayoutsParameterTypes31);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, keywords, types, statuses,
				start, end, orderByComparator);

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

			return (java.util.List<com.liferay.portal.kernel.model.Layout>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Layout>
		getLayouts(HttpPrincipal httpPrincipal, long groupId, String type) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayouts",
				_getLayoutsParameterTypes32);

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

			return (java.util.List<com.liferay.portal.kernel.model.Layout>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.Layout>
		getLayouts(
			HttpPrincipal httpPrincipal, long groupId, String type, int start,
			int end) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayouts",
				_getLayoutsParameterTypes33);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, type, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.portal.kernel.model.Layout>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getLayoutsCount(
		HttpPrincipal httpPrincipal, long groupId, boolean privateLayout) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayoutsCount",
				_getLayoutsCountParameterTypes34);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout);

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

	public static int getLayoutsCount(
		HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
		long parentLayoutId) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayoutsCount",
				_getLayoutsCountParameterTypes35);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, parentLayoutId);

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

	public static int getLayoutsCount(
		HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
		long parentLayoutId, int priority) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayoutsCount",
				_getLayoutsCountParameterTypes36);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, parentLayoutId, priority);

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

	public static int getLayoutsCount(
		HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
		String type) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayoutsCount",
				_getLayoutsCountParameterTypes37);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, type);

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

	public static int getLayoutsCount(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
			String keywords, String[] types)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayoutsCount",
				_getLayoutsCountParameterTypes38);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, keywords, types);

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

	public static int getLayoutsCount(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
			String keywords, String[] types, int[] statuses)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayoutsCount",
				_getLayoutsCountParameterTypes39);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, keywords, types, statuses);

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

	public static int getLayoutsCount(
		HttpPrincipal httpPrincipal, long groupId, String type) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getLayoutsCount",
				_getLayoutsCountParameterTypes40);

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

	public static com.liferay.portal.kernel.model.Layout getOrAddEmptyLayout(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "getOrAddEmptyLayout",
				_getOrAddEmptyLayoutParameterTypes41);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, serviceContext);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
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
				LayoutServiceUtil.class, "getTempFileNames",
				_getTempFileNamesParameterTypes42);

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

	public static boolean hasLayout(
			HttpPrincipal httpPrincipal, String uuid, long groupId,
			boolean privateLayout)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "hasLayout",
				_hasLayoutParameterTypes43);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, uuid, groupId, privateLayout);

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

	public static boolean hasPortletId(
			HttpPrincipal httpPrincipal, long plid, String portletId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "hasPortletId",
				_hasPortletIdParameterTypes44);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, plid, portletId);

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

	public static com.liferay.portal.kernel.model.Layout publishLayout(
			HttpPrincipal httpPrincipal, long plid)
		throws Exception {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "publishLayout",
				_publishLayoutParameterTypes45);

			MethodHandler methodHandler = new MethodHandler(methodKey, plid);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void schedulePublishToLive(
			HttpPrincipal httpPrincipal, long sourceGroupId, long targetGroupId,
			boolean privateLayout, long[] layoutIds,
			java.util.Map<String, String[]> parameterMap, String groupName,
			String cronText, java.util.Date schedulerStartDate,
			java.util.Date schedulerEndDate, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "schedulePublishToLive",
				_schedulePublishToLiveParameterTypes46);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, sourceGroupId, targetGroupId, privateLayout,
				layoutIds, parameterMap, groupName, cronText,
				schedulerStartDate, schedulerEndDate, description);

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

	public static void schedulePublishToRemote(
			HttpPrincipal httpPrincipal, long sourceGroupId,
			boolean privateLayout, java.util.Map<Long, Boolean> layoutIdMap,
			java.util.Map<String, String[]> parameterMap, String remoteAddress,
			int remotePort, String remotePathContext, boolean secureConnection,
			long remoteGroupId, boolean remotePrivateLayout,
			java.util.Date startDate, java.util.Date endDate, String groupName,
			String cronText, java.util.Date schedulerStartDate,
			java.util.Date schedulerEndDate, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "schedulePublishToRemote",
				_schedulePublishToRemoteParameterTypes47);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, sourceGroupId, privateLayout, layoutIdMap,
				parameterMap, remoteAddress, remotePort, remotePathContext,
				secureConnection, remoteGroupId, remotePrivateLayout, startDate,
				endDate, groupName, cronText, schedulerStartDate,
				schedulerEndDate, description);

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

	public static void setLayouts(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
			long parentLayoutId, long[] layoutIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "setLayouts",
				_setLayoutsParameterTypes48);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, parentLayoutId, layoutIds,
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

	public static void unschedulePublishToLive(
			HttpPrincipal httpPrincipal, long groupId, String jobName,
			String groupName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "unschedulePublishToLive",
				_unschedulePublishToLiveParameterTypes49);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, jobName, groupName);

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

	public static void unschedulePublishToRemote(
			HttpPrincipal httpPrincipal, long groupId, String jobName,
			String groupName)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "unschedulePublishToRemote",
				_unschedulePublishToRemoteParameterTypes50);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, jobName, groupName);

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

	public static com.liferay.portal.kernel.model.Layout updateIconImage(
			HttpPrincipal httpPrincipal, long plid, byte[] bytes)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "updateIconImage",
				_updateIconImageParameterTypes51);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, plid, bytes);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout updateLayout(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
			long layoutId, long parentLayoutId,
			java.util.Map<java.util.Locale, String> localeNamesMap,
			java.util.Map<java.util.Locale, String> localeTitlesMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<java.util.Locale, String> keywordsMap,
			java.util.Map<java.util.Locale, String> robotsMap, String type,
			boolean hidden,
			java.util.Map<java.util.Locale, String> friendlyURLMap,
			boolean hasIconImage, byte[] iconBytes, String styleBookEntryERC,
			long faviconFileEntryId, long masterLayoutPlid,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "updateLayout",
				_updateLayoutParameterTypes52);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, layoutId, parentLayoutId,
				localeNamesMap, localeTitlesMap, descriptionMap, keywordsMap,
				robotsMap, type, hidden, friendlyURLMap, hasIconImage,
				iconBytes, styleBookEntryERC, faviconFileEntryId,
				masterLayoutPlid, serviceContext);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout updateLayout(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
			long layoutId, String typeSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "updateLayout",
				_updateLayoutParameterTypes53);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, layoutId, typeSettings);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout updateLookAndFeel(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
			long layoutId, String themeId, String colorSchemeId, String css)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "updateLookAndFeel",
				_updateLookAndFeelParameterTypes54);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, layoutId, themeId,
				colorSchemeId, css);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout updateName(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
			long layoutId, String name, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "updateName",
				_updateNameParameterTypes55);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, layoutId, name, languageId);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout updateName(
			HttpPrincipal httpPrincipal, long plid, String name,
			String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "updateName",
				_updateNameParameterTypes56);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, plid, name, languageId);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout updateParentLayoutId(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
			long layoutId, long parentLayoutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "updateParentLayoutId",
				_updateParentLayoutIdParameterTypes57);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, layoutId, parentLayoutId);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout updateParentLayoutId(
			HttpPrincipal httpPrincipal, long plid, long parentPlid)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "updateParentLayoutId",
				_updateParentLayoutIdParameterTypes58);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, plid, parentPlid);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout
			updateParentLayoutIdAndPriority(
				HttpPrincipal httpPrincipal, long plid, long parentPlid,
				int priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "updateParentLayoutIdAndPriority",
				_updateParentLayoutIdAndPriorityParameterTypes59);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, plid, parentPlid, priority);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout updatePriority(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
			long layoutId, int priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "updatePriority",
				_updatePriorityParameterTypes60);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, layoutId, priority);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout updatePriority(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout,
			long layoutId, long nextLayoutId, long previousLayoutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "updatePriority",
				_updatePriorityParameterTypes61);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, layoutId, nextLayoutId,
				previousLayoutId);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout updatePriority(
			HttpPrincipal httpPrincipal, long plid, int priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "updatePriority",
				_updatePriorityParameterTypes62);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, plid, priority);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.Layout updateType(
			HttpPrincipal httpPrincipal, long plid, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutServiceUtil.class, "updateType",
				_updateTypeParameterTypes63);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, plid, type);

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

			return (com.liferay.portal.kernel.model.Layout)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(LayoutServiceHttp.class);

	private static final Class<?>[] _addLayoutParameterTypes0 = new Class[] {
		String.class, long.class, boolean.class, long.class, long.class,
		long.class, java.util.Map.class, java.util.Map.class,
		java.util.Map.class, java.util.Map.class, java.util.Map.class,
		String.class, String.class, boolean.class, boolean.class,
		java.util.Map.class, long.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addLayoutParameterTypes1 = new Class[] {
		String.class, long.class, boolean.class, long.class,
		java.util.Map.class, java.util.Map.class, java.util.Map.class,
		java.util.Map.class, java.util.Map.class, String.class, String.class,
		boolean.class, java.util.Map.class, long.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addLayoutParameterTypes2 = new Class[] {
		String.class, long.class, boolean.class, long.class,
		java.util.Map.class, java.util.Map.class, java.util.Map.class,
		java.util.Map.class, java.util.Map.class, String.class, String.class,
		boolean.class, java.util.Map.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addLayoutParameterTypes3 = new Class[] {
		String.class, long.class, boolean.class, long.class, String.class,
		String.class, String.class, String.class, boolean.class, String.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _addTempFileEntryParameterTypes4 =
		new Class[] {
			long.class, String.class, String.class, java.io.InputStream.class,
			String.class
		};
	private static final Class<?>[] _copyLayoutParameterTypes5 = new Class[] {
		long.class, boolean.class, java.util.Map.class, boolean.class,
		boolean.class, boolean.class, long.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _deleteLayoutParameterTypes6 = new Class[] {
		long.class, boolean.class, long.class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _deleteLayoutParameterTypes7 = new Class[] {
		long.class, com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _deleteLayoutParameterTypes8 = new Class[] {
		String.class, long.class
	};
	private static final Class<?>[] _deleteTempFileEntryParameterTypes9 =
		new Class[] {long.class, String.class, String.class};
	private static final Class<?>[] _fetchFirstLayoutParameterTypes10 =
		new Class[] {long.class, boolean.class, boolean.class};
	private static final Class<?>[] _fetchLayoutParameterTypes11 = new Class[] {
		long.class, boolean.class, long.class
	};
	private static final Class<?>[]
		_fetchLayoutByExternalReferenceCodeParameterTypes12 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _fetchLayoutPlidParameterTypes13 =
		new Class[] {String.class, long.class, boolean.class};
	private static final Class<?>[] _getAncestorLayoutsParameterTypes14 =
		new Class[] {long.class};
	private static final Class<?>[] _getControlPanelLayoutPlidParameterTypes15 =
		new Class[] {};
	private static final Class<?>[] _getDefaultPlidParameterTypes16 =
		new Class[] {long.class, boolean.class};
	private static final Class<?>[] _getDefaultPlidParameterTypes17 =
		new Class[] {long.class, long.class, boolean.class, String.class};
	private static final Class<?>[] _getDefaultPlidParameterTypes18 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[]
		_getLayoutByExternalReferenceCodeParameterTypes19 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _getLayoutByUuidAndGroupIdParameterTypes20 =
		new Class[] {String.class, long.class, boolean.class};
	private static final Class<?>[] _getLayoutNameParameterTypes21 =
		new Class[] {long.class, boolean.class, long.class, String.class};
	private static final Class<?>[] _getLayoutPlidParameterTypes22 =
		new Class[] {String.class, long.class, boolean.class};
	private static final Class<?>[] _getLayoutReferencesParameterTypes23 =
		new Class[] {long.class, String.class, String.class, String.class};
	private static final Class<?>[] _getLayoutsParameterTypes24 = new Class[] {
		long.class, boolean.class
	};
	private static final Class<?>[] _getLayoutsParameterTypes25 = new Class[] {
		long.class, boolean.class, long.class
	};
	private static final Class<?>[] _getLayoutsParameterTypes26 = new Class[] {
		long.class, boolean.class, long.class, boolean.class, int.class,
		int.class
	};
	private static final Class<?>[] _getLayoutsParameterTypes27 = new Class[] {
		long.class, boolean.class, long.class, int.class, int.class
	};
	private static final Class<?>[] _getLayoutsParameterTypes28 = new Class[] {
		long.class, boolean.class, String.class
	};
	private static final Class<?>[] _getLayoutsParameterTypes29 = new Class[] {
		long.class, boolean.class, String.class, int.class, int.class
	};
	private static final Class<?>[] _getLayoutsParameterTypes30 = new Class[] {
		long.class, boolean.class, String.class, String[].class, int.class,
		int.class, com.liferay.portal.kernel.util.OrderByComparator.class
	};
	private static final Class<?>[] _getLayoutsParameterTypes31 = new Class[] {
		long.class, boolean.class, String.class, String[].class, int[].class,
		int.class, int.class,
		com.liferay.portal.kernel.util.OrderByComparator.class
	};
	private static final Class<?>[] _getLayoutsParameterTypes32 = new Class[] {
		long.class, String.class
	};
	private static final Class<?>[] _getLayoutsParameterTypes33 = new Class[] {
		long.class, String.class, int.class, int.class
	};
	private static final Class<?>[] _getLayoutsCountParameterTypes34 =
		new Class[] {long.class, boolean.class};
	private static final Class<?>[] _getLayoutsCountParameterTypes35 =
		new Class[] {long.class, boolean.class, long.class};
	private static final Class<?>[] _getLayoutsCountParameterTypes36 =
		new Class[] {long.class, boolean.class, long.class, int.class};
	private static final Class<?>[] _getLayoutsCountParameterTypes37 =
		new Class[] {long.class, boolean.class, String.class};
	private static final Class<?>[] _getLayoutsCountParameterTypes38 =
		new Class[] {long.class, boolean.class, String.class, String[].class};
	private static final Class<?>[] _getLayoutsCountParameterTypes39 =
		new Class[] {
			long.class, boolean.class, String.class, String[].class, int[].class
		};
	private static final Class<?>[] _getLayoutsCountParameterTypes40 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _getOrAddEmptyLayoutParameterTypes41 =
		new Class[] {
			String.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _getTempFileNamesParameterTypes42 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _hasLayoutParameterTypes43 = new Class[] {
		String.class, long.class, boolean.class
	};
	private static final Class<?>[] _hasPortletIdParameterTypes44 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _publishLayoutParameterTypes45 =
		new Class[] {long.class};
	private static final Class<?>[] _schedulePublishToLiveParameterTypes46 =
		new Class[] {
			long.class, long.class, boolean.class, long[].class,
			java.util.Map.class, String.class, String.class,
			java.util.Date.class, java.util.Date.class, String.class
		};
	private static final Class<?>[] _schedulePublishToRemoteParameterTypes47 =
		new Class[] {
			long.class, boolean.class, java.util.Map.class, java.util.Map.class,
			String.class, int.class, String.class, boolean.class, long.class,
			boolean.class, java.util.Date.class, java.util.Date.class,
			String.class, String.class, java.util.Date.class,
			java.util.Date.class, String.class
		};
	private static final Class<?>[] _setLayoutsParameterTypes48 = new Class[] {
		long.class, boolean.class, long.class, long[].class,
		com.liferay.portal.kernel.service.ServiceContext.class
	};
	private static final Class<?>[] _unschedulePublishToLiveParameterTypes49 =
		new Class[] {long.class, String.class, String.class};
	private static final Class<?>[] _unschedulePublishToRemoteParameterTypes50 =
		new Class[] {long.class, String.class, String.class};
	private static final Class<?>[] _updateIconImageParameterTypes51 =
		new Class[] {long.class, byte[].class};
	private static final Class<?>[] _updateLayoutParameterTypes52 =
		new Class[] {
			long.class, boolean.class, long.class, long.class,
			java.util.Map.class, java.util.Map.class, java.util.Map.class,
			java.util.Map.class, java.util.Map.class, String.class,
			boolean.class, java.util.Map.class, boolean.class, byte[].class,
			String.class, long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateLayoutParameterTypes53 =
		new Class[] {long.class, boolean.class, long.class, String.class};
	private static final Class<?>[] _updateLookAndFeelParameterTypes54 =
		new Class[] {
			long.class, boolean.class, long.class, String.class, String.class,
			String.class
		};
	private static final Class<?>[] _updateNameParameterTypes55 = new Class[] {
		long.class, boolean.class, long.class, String.class, String.class
	};
	private static final Class<?>[] _updateNameParameterTypes56 = new Class[] {
		long.class, String.class, String.class
	};
	private static final Class<?>[] _updateParentLayoutIdParameterTypes57 =
		new Class[] {long.class, boolean.class, long.class, long.class};
	private static final Class<?>[] _updateParentLayoutIdParameterTypes58 =
		new Class[] {long.class, long.class};
	private static final Class<?>[]
		_updateParentLayoutIdAndPriorityParameterTypes59 = new Class[] {
			long.class, long.class, int.class
		};
	private static final Class<?>[] _updatePriorityParameterTypes60 =
		new Class[] {long.class, boolean.class, long.class, int.class};
	private static final Class<?>[] _updatePriorityParameterTypes61 =
		new Class[] {
			long.class, boolean.class, long.class, long.class, long.class
		};
	private static final Class<?>[] _updatePriorityParameterTypes62 =
		new Class[] {long.class, int.class};
	private static final Class<?>[] _updateTypeParameterTypes63 = new Class[] {
		long.class, String.class
	};

}