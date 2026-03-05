/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.http;

import com.liferay.change.tracking.service.CTCollectionServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CTCollectionServiceUtil</code> service
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
public class CTCollectionServiceHttp {

	public static com.liferay.change.tracking.model.CTCollection
			addCTCollection(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long ctRemoteId, String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CTCollectionServiceUtil.class, "addCTCollection",
				_addCTCollectionParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, ctRemoteId, name,
				description);

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

			return (com.liferay.change.tracking.model.CTCollection)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteCTAutoResolutionInfo(
			HttpPrincipal httpPrincipal, long ctAutoResolutionInfoId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CTCollectionServiceUtil.class, "deleteCTAutoResolutionInfo",
				_deleteCTAutoResolutionInfoParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, ctAutoResolutionInfoId);

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

	public static com.liferay.change.tracking.model.CTCollection
			deleteCTCollection(
				HttpPrincipal httpPrincipal,
				com.liferay.change.tracking.model.CTCollection ctCollection)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CTCollectionServiceUtil.class, "deleteCTCollection",
				_deleteCTCollectionParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, ctCollection);

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

			return (com.liferay.change.tracking.model.CTCollection)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void discardCTEntry(
			HttpPrincipal httpPrincipal, long ctCollectionId,
			java.util.List<com.liferay.change.tracking.model.CTEntry> ctEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CTCollectionServiceUtil.class, "discardCTEntry",
				_discardCTEntryParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, ctCollectionId, ctEntries);

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

	public static void discardCTEntry(
			HttpPrincipal httpPrincipal, long ctCollectionId,
			long modelClassNameId, long modelClassPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CTCollectionServiceUtil.class, "discardCTEntry",
				_discardCTEntryParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, ctCollectionId, modelClassNameId, modelClassPK);

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

	public static java.util.List<com.liferay.change.tracking.model.CTCollection>
			getCTCollections(
				HttpPrincipal httpPrincipal, int[] statuses, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.change.tracking.model.CTCollection>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CTCollectionServiceUtil.class, "getCTCollections",
				_getCTCollectionsParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, statuses, start, end, orderByComparator);

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
				<com.liferay.change.tracking.model.CTCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.change.tracking.model.CTCollection>
			getCTCollections(
				HttpPrincipal httpPrincipal, int[] statuses, String keywords,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.change.tracking.model.CTCollection>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CTCollectionServiceUtil.class, "getCTCollections",
				_getCTCollectionsParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, statuses, keywords, start, end, orderByComparator);

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
				<com.liferay.change.tracking.model.CTCollection>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCTCollectionsCount(
			HttpPrincipal httpPrincipal, int[] statuses, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CTCollectionServiceUtil.class, "getCTCollectionsCount",
				_getCTCollectionsCountParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, statuses, keywords);

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

	public static void moveCTEntries(
			HttpPrincipal httpPrincipal, long fromCTCollectionId,
			long toCTCollectionId,
			java.util.List<com.liferay.change.tracking.model.CTEntry> ctEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CTCollectionServiceUtil.class, "moveCTEntries",
				_moveCTEntriesParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fromCTCollectionId, toCTCollectionId, ctEntries);

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

	public static void moveCTEntry(
			HttpPrincipal httpPrincipal, long fromCTCollectionId,
			long toCTCollectionId, long modelClassNameId, long modelClassPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CTCollectionServiceUtil.class, "moveCTEntry",
				_moveCTEntryParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fromCTCollectionId, toCTCollectionId,
				modelClassNameId, modelClassPK);

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

	public static void publishCTCollection(
			HttpPrincipal httpPrincipal, long userId, long ctCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CTCollectionServiceUtil.class, "publishCTCollection",
				_publishCTCollectionParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, ctCollectionId);

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

	public static com.liferay.change.tracking.model.CTCollection
			undoCTCollection(
				HttpPrincipal httpPrincipal, long ctCollectionId, long userId,
				String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CTCollectionServiceUtil.class, "undoCTCollection",
				_undoCTCollectionParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, ctCollectionId, userId, name, description);

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

			return (com.liferay.change.tracking.model.CTCollection)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.change.tracking.model.CTCollection
			updateCTCollection(
				HttpPrincipal httpPrincipal, long userId, long ctCollectionId,
				String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CTCollectionServiceUtil.class, "updateCTCollection",
				_updateCTCollectionParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, ctCollectionId, name, description);

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

			return (com.liferay.change.tracking.model.CTCollection)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CTCollectionServiceHttp.class);

	private static final Class<?>[] _addCTCollectionParameterTypes0 =
		new Class[] {String.class, long.class, String.class, String.class};
	private static final Class<?>[] _deleteCTAutoResolutionInfoParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteCTCollectionParameterTypes2 =
		new Class[] {com.liferay.change.tracking.model.CTCollection.class};
	private static final Class<?>[] _discardCTEntryParameterTypes3 =
		new Class[] {long.class, java.util.List.class};
	private static final Class<?>[] _discardCTEntryParameterTypes4 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[] _getCTCollectionsParameterTypes5 =
		new Class[] {
			int[].class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getCTCollectionsParameterTypes6 =
		new Class[] {
			int[].class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getCTCollectionsCountParameterTypes7 =
		new Class[] {int[].class, String.class};
	private static final Class<?>[] _moveCTEntriesParameterTypes8 =
		new Class[] {long.class, long.class, java.util.List.class};
	private static final Class<?>[] _moveCTEntryParameterTypes9 = new Class[] {
		long.class, long.class, long.class, long.class
	};
	private static final Class<?>[] _publishCTCollectionParameterTypes10 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _undoCTCollectionParameterTypes11 =
		new Class[] {long.class, long.class, String.class, String.class};
	private static final Class<?>[] _updateCTCollectionParameterTypes12 =
		new Class[] {long.class, long.class, String.class, String.class};

}