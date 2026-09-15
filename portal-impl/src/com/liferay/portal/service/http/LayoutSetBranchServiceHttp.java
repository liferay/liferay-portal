/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.LayoutSetBranchServiceUtil;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>LayoutSetBranchServiceUtil</code> service
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
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutSetBranchServiceHttp {

	public static com.liferay.portal.kernel.model.LayoutSetBranch
			addLayoutSetBranch(
				HttpPrincipal httpPrincipal, long groupId,
				boolean privateLayout, String name, String description,
				boolean master, long copyLayoutSetBranchId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutSetBranchServiceUtil.class, "addLayoutSetBranch",
				_addLayoutSetBranchParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, privateLayout, name, description, master,
				copyLayoutSetBranchId, serviceContext);

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

			return (com.liferay.portal.kernel.model.LayoutSetBranch)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteLayoutSetBranch(
			HttpPrincipal httpPrincipal, long layoutSetBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutSetBranchServiceUtil.class, "deleteLayoutSetBranch",
				_deleteLayoutSetBranchParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutSetBranchId);

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

	public static void deleteLayoutSetBranch(
			HttpPrincipal httpPrincipal, long currentLayoutPlid,
			long layoutSetBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutSetBranchServiceUtil.class, "deleteLayoutSetBranch",
				_deleteLayoutSetBranchParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, currentLayoutPlid, layoutSetBranchId);

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

	public static java.util.List
		<com.liferay.portal.kernel.model.LayoutSetBranch> getLayoutSetBranches(
			HttpPrincipal httpPrincipal, long groupId, boolean privateLayout) {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutSetBranchServiceUtil.class, "getLayoutSetBranches",
				_getLayoutSetBranchesParameterTypes3);

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

			return (java.util.List
				<com.liferay.portal.kernel.model.LayoutSetBranch>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.LayoutSetBranch
			mergeLayoutSetBranch(
				HttpPrincipal httpPrincipal, long layoutSetBranchId,
				long mergeLayoutSetBranchId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutSetBranchServiceUtil.class, "mergeLayoutSetBranch",
				_mergeLayoutSetBranchParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, layoutSetBranchId, mergeLayoutSetBranchId,
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

			return (com.liferay.portal.kernel.model.LayoutSetBranch)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.LayoutSetBranch
			updateLayoutSetBranch(
				HttpPrincipal httpPrincipal, long groupId,
				long layoutSetBranchId, String name, String description,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutSetBranchServiceUtil.class, "updateLayoutSetBranch",
				_updateLayoutSetBranchParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, layoutSetBranchId, name, description,
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

			return (com.liferay.portal.kernel.model.LayoutSetBranch)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		LayoutSetBranchServiceHttp.class);

	private static final Class<?>[] _addLayoutSetBranchParameterTypes0 =
		new Class[] {
			long.class, boolean.class, String.class, String.class,
			boolean.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteLayoutSetBranchParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteLayoutSetBranchParameterTypes2 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getLayoutSetBranchesParameterTypes3 =
		new Class[] {long.class, boolean.class};
	private static final Class<?>[] _mergeLayoutSetBranchParameterTypes4 =
		new Class[] {
			long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateLayoutSetBranchParameterTypes5 =
		new Class[] {
			long.class, long.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1378538256