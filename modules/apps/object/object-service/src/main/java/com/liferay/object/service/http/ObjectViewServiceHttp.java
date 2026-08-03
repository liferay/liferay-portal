/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.http;

import com.liferay.object.service.ObjectViewServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>ObjectViewServiceUtil</code> service
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
public class ObjectViewServiceHttp {

	public static com.liferay.object.model.ObjectView addObjectView(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long objectDefinitionId, boolean defaultObjectView,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.List<com.liferay.object.model.ObjectViewColumn>
				objectViewColumns,
			java.util.List<com.liferay.object.model.ObjectViewFilterColumn>
				objectViewFilterColumns,
			java.util.List<com.liferay.object.model.ObjectViewSortColumn>
				objectViewSortColumns)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectViewServiceUtil.class, "addObjectView",
				_addObjectViewParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, objectDefinitionId,
				defaultObjectView, nameMap, objectViewColumns,
				objectViewFilterColumns, objectViewSortColumns);

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

			return (com.liferay.object.model.ObjectView)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectView deleteObjectView(
			HttpPrincipal httpPrincipal, long objectViewId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectViewServiceUtil.class, "deleteObjectView",
				_deleteObjectViewParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectViewId);

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

			return (com.liferay.object.model.ObjectView)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectView getObjectView(
			HttpPrincipal httpPrincipal, long objectViewId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectViewServiceUtil.class, "getObjectView",
				_getObjectViewParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectViewId);

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

			return (com.liferay.object.model.ObjectView)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectView updateObjectView(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long objectViewId, boolean defaultObjectView,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.List<com.liferay.object.model.ObjectViewColumn>
				objectViewColumns,
			java.util.List<com.liferay.object.model.ObjectViewFilterColumn>
				objectViewFilterColumns,
			java.util.List<com.liferay.object.model.ObjectViewSortColumn>
				objectViewSortColumns)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectViewServiceUtil.class, "updateObjectView",
				_updateObjectViewParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, objectViewId,
				defaultObjectView, nameMap, objectViewColumns,
				objectViewFilterColumns, objectViewSortColumns);

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

			return (com.liferay.object.model.ObjectView)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		ObjectViewServiceHttp.class);

	private static final Class<?>[] _addObjectViewParameterTypes0 =
		new Class[] {
			String.class, long.class, boolean.class, java.util.Map.class,
			java.util.List.class, java.util.List.class, java.util.List.class
		};
	private static final Class<?>[] _deleteObjectViewParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _getObjectViewParameterTypes2 =
		new Class[] {long.class};
	private static final Class<?>[] _updateObjectViewParameterTypes3 =
		new Class[] {
			String.class, long.class, boolean.class, java.util.Map.class,
			java.util.List.class, java.util.List.class, java.util.List.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:411669410