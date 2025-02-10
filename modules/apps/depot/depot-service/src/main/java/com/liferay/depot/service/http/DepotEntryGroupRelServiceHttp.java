/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.http;

import com.liferay.depot.service.DepotEntryGroupRelServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>DepotEntryGroupRelServiceUtil</code> service
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
public class DepotEntryGroupRelServiceHttp {

	public static com.liferay.depot.model.DepotEntryGroupRel
			addDepotEntryGroupRel(
				HttpPrincipal httpPrincipal, long depotEntryId, long toGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DepotEntryGroupRelServiceUtil.class, "addDepotEntryGroupRel",
				_addDepotEntryGroupRelParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, depotEntryId, toGroupId);

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

			return (com.liferay.depot.model.DepotEntryGroupRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.depot.model.DepotEntryGroupRel
			deleteDepotEntryGroupRel(
				HttpPrincipal httpPrincipal, long depotEntryGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DepotEntryGroupRelServiceUtil.class, "deleteDepotEntryGroupRel",
				_deleteDepotEntryGroupRelParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, depotEntryGroupRelId);

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

			return (com.liferay.depot.model.DepotEntryGroupRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.depot.model.DepotEntryGroupRel
			getDepotEntryGroupRelByDepotEntryIdToGroupId(
				HttpPrincipal httpPrincipal, long depotEntryId, long toGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DepotEntryGroupRelServiceUtil.class,
				"getDepotEntryGroupRelByDepotEntryIdToGroupId",
				_getDepotEntryGroupRelByDepotEntryIdToGroupIdParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, depotEntryId, toGroupId);

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

			return (com.liferay.depot.model.DepotEntryGroupRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.depot.model.DepotEntryGroupRel>
			getDepotEntryGroupRels(
				HttpPrincipal httpPrincipal, long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DepotEntryGroupRelServiceUtil.class, "getDepotEntryGroupRels",
				_getDepotEntryGroupRelsParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, start, end);

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

			return (java.util.List<com.liferay.depot.model.DepotEntryGroupRel>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getDepotEntryGroupRelsCount(
			HttpPrincipal httpPrincipal,
			com.liferay.depot.model.DepotEntry depotEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DepotEntryGroupRelServiceUtil.class,
				"getDepotEntryGroupRelsCount",
				_getDepotEntryGroupRelsCountParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, depotEntry);

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

	public static int getDepotEntryGroupRelsCount(
			HttpPrincipal httpPrincipal, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DepotEntryGroupRelServiceUtil.class,
				"getDepotEntryGroupRelsCount",
				_getDepotEntryGroupRelsCountParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(methodKey, groupId);

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

	public static com.liferay.depot.model.DepotEntryGroupRel
			updateDDMStructuresAvailable(
				HttpPrincipal httpPrincipal, long depotEntryGroupRelId,
				boolean ddmStructuresAvailable)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DepotEntryGroupRelServiceUtil.class,
				"updateDDMStructuresAvailable",
				_updateDDMStructuresAvailableParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, depotEntryGroupRelId, ddmStructuresAvailable);

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

			return (com.liferay.depot.model.DepotEntryGroupRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.depot.model.DepotEntryGroupRel updateSearchable(
			HttpPrincipal httpPrincipal, long depotEntryGroupRelId,
			boolean searchable)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				DepotEntryGroupRelServiceUtil.class, "updateSearchable",
				_updateSearchableParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, depotEntryGroupRelId, searchable);

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

			return (com.liferay.depot.model.DepotEntryGroupRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		DepotEntryGroupRelServiceHttp.class);

	private static final Class<?>[] _addDepotEntryGroupRelParameterTypes0 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _deleteDepotEntryGroupRelParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getDepotEntryGroupRelByDepotEntryIdToGroupIdParameterTypes2 =
			new Class[] {long.class, long.class};
	private static final Class<?>[] _getDepotEntryGroupRelsParameterTypes3 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[]
		_getDepotEntryGroupRelsCountParameterTypes4 = new Class[] {
			com.liferay.depot.model.DepotEntry.class
		};
	private static final Class<?>[]
		_getDepotEntryGroupRelsCountParameterTypes5 = new Class[] {long.class};
	private static final Class<?>[]
		_updateDDMStructuresAvailableParameterTypes6 = new Class[] {
			long.class, boolean.class
		};
	private static final Class<?>[] _updateSearchableParameterTypes7 =
		new Class[] {long.class, boolean.class};

}