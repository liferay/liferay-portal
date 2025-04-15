/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.PortletPreferencesServiceUtil;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>PortletPreferencesServiceUtil</code> service
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
public class PortletPreferencesServiceHttp {

	public static void deleteArchivedPreferences(
			HttpPrincipal httpPrincipal, long portletItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				PortletPreferencesServiceUtil.class,
				"deleteArchivedPreferences",
				_deleteArchivedPreferencesParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, portletItemId);

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

	public static void restoreArchivedPreferences(
			HttpPrincipal httpPrincipal, long groupId,
			com.liferay.portal.kernel.model.Layout layout, String portletId,
			long portletItemId,
			jakarta.portlet.PortletPreferences jxPortletPreferences)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				PortletPreferencesServiceUtil.class,
				"restoreArchivedPreferences",
				_restoreArchivedPreferencesParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layout, portletId, portletItemId,
				jxPortletPreferences);

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

	public static void restoreArchivedPreferences(
			HttpPrincipal httpPrincipal, long groupId,
			com.liferay.portal.kernel.model.Layout layout, String portletId,
			com.liferay.portal.kernel.model.PortletItem portletItem,
			jakarta.portlet.PortletPreferences jxPortletPreferences)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				PortletPreferencesServiceUtil.class,
				"restoreArchivedPreferences",
				_restoreArchivedPreferencesParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layout, portletId, portletItem,
				jxPortletPreferences);

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

	public static void restoreArchivedPreferences(
			HttpPrincipal httpPrincipal, long groupId, String name,
			com.liferay.portal.kernel.model.Layout layout, String portletId,
			jakarta.portlet.PortletPreferences jxPortletPreferences)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				PortletPreferencesServiceUtil.class,
				"restoreArchivedPreferences",
				_restoreArchivedPreferencesParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, layout, portletId,
				jxPortletPreferences);

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

	public static void updateArchivePreferences(
			HttpPrincipal httpPrincipal, long userId, long groupId, String name,
			String portletId,
			jakarta.portlet.PortletPreferences jxPortletPreferences)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				PortletPreferencesServiceUtil.class, "updateArchivePreferences",
				_updateArchivePreferencesParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, groupId, name, portletId,
				jxPortletPreferences);

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
		PortletPreferencesServiceHttp.class);

	private static final Class<?>[] _deleteArchivedPreferencesParameterTypes0 =
		new Class[] {long.class};
	private static final Class<?>[] _restoreArchivedPreferencesParameterTypes1 =
		new Class[] {
			long.class, com.liferay.portal.kernel.model.Layout.class,
			String.class, long.class, jakarta.portlet.PortletPreferences.class
		};
	private static final Class<?>[] _restoreArchivedPreferencesParameterTypes2 =
		new Class[] {
			long.class, com.liferay.portal.kernel.model.Layout.class,
			String.class, com.liferay.portal.kernel.model.PortletItem.class,
			jakarta.portlet.PortletPreferences.class
		};
	private static final Class<?>[] _restoreArchivedPreferencesParameterTypes3 =
		new Class[] {
			long.class, String.class,
			com.liferay.portal.kernel.model.Layout.class, String.class,
			jakarta.portlet.PortletPreferences.class
		};
	private static final Class<?>[] _updateArchivePreferencesParameterTypes4 =
		new Class[] {
			long.class, long.class, String.class, String.class,
			jakarta.portlet.PortletPreferences.class
		};

}