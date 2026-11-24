/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.http;

import com.liferay.commerce.shop.by.diagram.service.CSDiagramPinServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CSDiagramPinServiceUtil</code> service
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
 * @author Alessio Antonio Rendina
 * @generated
 */
public class CSDiagramPinServiceHttp {

	public static com.liferay.commerce.shop.by.diagram.model.CSDiagramPin
			addCSDiagramPin(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				double positionX, double positionY, String sequence)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramPinServiceUtil.class, "addCSDiagramPin",
				_addCSDiagramPinParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, positionX, positionY, sequence);

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

			return (com.liferay.commerce.shop.by.diagram.model.CSDiagramPin)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteCSDiagramPin(
			HttpPrincipal httpPrincipal, long csDiagramPinId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramPinServiceUtil.class, "deleteCSDiagramPin",
				_deleteCSDiagramPinParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, csDiagramPinId);

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

	public static void deleteCSDiagramPins(
			HttpPrincipal httpPrincipal, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramPinServiceUtil.class, "deleteCSDiagramPins",
				_deleteCSDiagramPinsParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId);

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

	public static com.liferay.commerce.shop.by.diagram.model.CSDiagramPin
		fetchCSDiagramPin(HttpPrincipal httpPrincipal, long csDiagramPinId) {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramPinServiceUtil.class, "fetchCSDiagramPin",
				_fetchCSDiagramPinParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, csDiagramPinId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.commerce.shop.by.diagram.model.CSDiagramPin)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.shop.by.diagram.model.CSDiagramPin
			getCSDiagramPin(HttpPrincipal httpPrincipal, long csDiagramPinId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramPinServiceUtil.class, "getCSDiagramPin",
				_getCSDiagramPinParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, csDiagramPinId);

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

			return (com.liferay.commerce.shop.by.diagram.model.CSDiagramPin)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.shop.by.diagram.model.CSDiagramPin>
				getCSDiagramPins(
					HttpPrincipal httpPrincipal, long cpDefinitionId, int start,
					int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramPinServiceUtil.class, "getCSDiagramPins",
				_getCSDiagramPinsParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, start, end);

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

			return (java.util.List
				<com.liferay.commerce.shop.by.diagram.model.CSDiagramPin>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCSDiagramPinsCount(
			HttpPrincipal httpPrincipal, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramPinServiceUtil.class, "getCSDiagramPinsCount",
				_getCSDiagramPinsCountParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId);

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

	public static com.liferay.commerce.shop.by.diagram.model.CSDiagramPin
			updateCSDiagramPin(
				HttpPrincipal httpPrincipal, long csDiagramPinId,
				double positionX, double positionY, String sequence)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramPinServiceUtil.class, "updateCSDiagramPin",
				_updateCSDiagramPinParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, csDiagramPinId, positionX, positionY, sequence);

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

			return (com.liferay.commerce.shop.by.diagram.model.CSDiagramPin)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CSDiagramPinServiceHttp.class);

	private static final Class<?>[] _addCSDiagramPinParameterTypes0 =
		new Class[] {long.class, double.class, double.class, String.class};
	private static final Class<?>[] _deleteCSDiagramPinParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteCSDiagramPinsParameterTypes2 =
		new Class[] {long.class};
	private static final Class<?>[] _fetchCSDiagramPinParameterTypes3 =
		new Class[] {long.class};
	private static final Class<?>[] _getCSDiagramPinParameterTypes4 =
		new Class[] {long.class};
	private static final Class<?>[] _getCSDiagramPinsParameterTypes5 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[] _getCSDiagramPinsCountParameterTypes6 =
		new Class[] {long.class};
	private static final Class<?>[] _updateCSDiagramPinParameterTypes7 =
		new Class[] {long.class, double.class, double.class, String.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1247396514