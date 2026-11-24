/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.http;

import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CSDiagramEntryServiceUtil</code> service
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
public class CSDiagramEntryServiceHttp {

	public static com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry
			addCSDiagramEntry(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				long cpInstanceId, long cProductId, boolean diagram,
				int quantity, String sequence, String sku,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramEntryServiceUtil.class, "addCSDiagramEntry",
				_addCSDiagramEntryParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, cpInstanceId, cProductId, diagram,
				quantity, sequence, sku, serviceContext);

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

			return (com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteCSDiagramEntries(
			HttpPrincipal httpPrincipal, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramEntryServiceUtil.class, "deleteCSDiagramEntries",
				_deleteCSDiagramEntriesParameterTypes1);

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

	public static void deleteCSDiagramEntry(
			HttpPrincipal httpPrincipal, long csDiagramEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramEntryServiceUtil.class, "deleteCSDiagramEntry",
				_deleteCSDiagramEntryParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, csDiagramEntryId);

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

	public static com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry
			fetchCSDiagramEntry(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				String sequence)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramEntryServiceUtil.class, "fetchCSDiagramEntry",
				_fetchCSDiagramEntryParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, sequence);

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

			return (com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry>
				getCProductCSDiagramEntries(
					HttpPrincipal httpPrincipal, long cProductId, int start,
					int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.shop.by.diagram.model.
							CSDiagramEntry> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramEntryServiceUtil.class, "getCProductCSDiagramEntries",
				_getCProductCSDiagramEntriesParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cProductId, start, end, orderByComparator);

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
				<com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCProductCSDiagramEntriesCount(
			HttpPrincipal httpPrincipal, long cProductId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramEntryServiceUtil.class,
				"getCProductCSDiagramEntriesCount",
				_getCProductCSDiagramEntriesCountParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cProductId);

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

	public static java.util.List
		<com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry>
				getCSDiagramEntries(
					HttpPrincipal httpPrincipal, long cpDefinitionId, int start,
					int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramEntryServiceUtil.class, "getCSDiagramEntries",
				_getCSDiagramEntriesParameterTypes6);

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
				<com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCSDiagramEntriesCount(
			HttpPrincipal httpPrincipal, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramEntryServiceUtil.class, "getCSDiagramEntriesCount",
				_getCSDiagramEntriesCountParameterTypes7);

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

	public static com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry
			getCSDiagramEntry(
				HttpPrincipal httpPrincipal, long csDiagramEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramEntryServiceUtil.class, "getCSDiagramEntry",
				_getCSDiagramEntryParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, csDiagramEntryId);

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

			return (com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry
			getCSDiagramEntry(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				String sequence)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramEntryServiceUtil.class, "getCSDiagramEntry",
				_getCSDiagramEntryParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, sequence);

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

			return (com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry
			updateCSDiagramEntry(
				HttpPrincipal httpPrincipal, long csDiagramEntryId,
				long cpInstanceId, long cProductId, boolean diagram,
				int quantity, String sequence, String sku,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CSDiagramEntryServiceUtil.class, "updateCSDiagramEntry",
				_updateCSDiagramEntryParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, csDiagramEntryId, cpInstanceId, cProductId, diagram,
				quantity, sequence, sku, serviceContext);

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

			return (com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CSDiagramEntryServiceHttp.class);

	private static final Class<?>[] _addCSDiagramEntryParameterTypes0 =
		new Class[] {
			long.class, long.class, long.class, boolean.class, int.class,
			String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteCSDiagramEntriesParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteCSDiagramEntryParameterTypes2 =
		new Class[] {long.class};
	private static final Class<?>[] _fetchCSDiagramEntryParameterTypes3 =
		new Class[] {long.class, String.class};
	private static final Class<?>[]
		_getCProductCSDiagramEntriesParameterTypes4 = new Class[] {
			long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getCProductCSDiagramEntriesCountParameterTypes5 = new Class[] {
			long.class
		};
	private static final Class<?>[] _getCSDiagramEntriesParameterTypes6 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[] _getCSDiagramEntriesCountParameterTypes7 =
		new Class[] {long.class};
	private static final Class<?>[] _getCSDiagramEntryParameterTypes8 =
		new Class[] {long.class};
	private static final Class<?>[] _getCSDiagramEntryParameterTypes9 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _updateCSDiagramEntryParameterTypes10 =
		new Class[] {
			long.class, long.class, long.class, boolean.class, int.class,
			String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:1522906232