/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.http;

import com.liferay.fragment.service.FragmentCompositionServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>FragmentCompositionServiceUtil</code> service
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
public class FragmentCompositionServiceHttp {

	public static com.liferay.fragment.model.FragmentComposition
			addFragmentComposition(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, long fragmentCollectionId,
				String fragmentCompositionKey, String name, String description,
				String data, long previewFileEntryId, int status,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class, "addFragmentComposition",
				_addFragmentCompositionParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, fragmentCollectionId,
				fragmentCompositionKey, name, description, data,
				previewFileEntryId, status, serviceContext);

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

			return (com.liferay.fragment.model.FragmentComposition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentComposition
			deleteFragmentComposition(
				HttpPrincipal httpPrincipal, long fragmentCompositionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class,
				"deleteFragmentComposition",
				_deleteFragmentCompositionParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCompositionId);

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

			return (com.liferay.fragment.model.FragmentComposition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentComposition
			deleteFragmentComposition(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class,
				"deleteFragmentComposition",
				_deleteFragmentCompositionParameterTypes2);

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

			return (com.liferay.fragment.model.FragmentComposition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentComposition
		fetchFragmentComposition(
			HttpPrincipal httpPrincipal, long fragmentCompositionId) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class,
				"fetchFragmentComposition",
				_fetchFragmentCompositionParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCompositionId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.fragment.model.FragmentComposition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentComposition
		fetchFragmentComposition(
			HttpPrincipal httpPrincipal, long groupId,
			String fragmentCompositionKey) {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class,
				"fetchFragmentComposition",
				_fetchFragmentCompositionParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, fragmentCompositionKey);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.fragment.model.FragmentComposition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentComposition
			getFragmentCompositionByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class,
				"getFragmentCompositionByExternalReferenceCode",
				_getFragmentCompositionByExternalReferenceCodeParameterTypes5);

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

			return (com.liferay.fragment.model.FragmentComposition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentComposition>
			getFragmentCompositions(
				HttpPrincipal httpPrincipal, long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class, "getFragmentCompositions",
				_getFragmentCompositionsParameterTypes6);

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

			return (java.util.List
				<com.liferay.fragment.model.FragmentComposition>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentComposition>
			getFragmentCompositions(
				HttpPrincipal httpPrincipal, long fragmentCollectionId,
				int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class, "getFragmentCompositions",
				_getFragmentCompositionsParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCollectionId, start, end);

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
				<com.liferay.fragment.model.FragmentComposition>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentComposition>
			getFragmentCompositions(
				HttpPrincipal httpPrincipal, long groupId,
				long fragmentCollectionId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class, "getFragmentCompositions",
				_getFragmentCompositionsParameterTypes8);

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

			return (java.util.List
				<com.liferay.fragment.model.FragmentComposition>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentComposition>
			getFragmentCompositions(
				HttpPrincipal httpPrincipal, long groupId,
				long fragmentCollectionId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.fragment.model.FragmentComposition>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class, "getFragmentCompositions",
				_getFragmentCompositionsParameterTypes9);

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

			return (java.util.List
				<com.liferay.fragment.model.FragmentComposition>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.fragment.model.FragmentComposition>
			getFragmentCompositions(
				HttpPrincipal httpPrincipal, long groupId,
				long fragmentCollectionId, String name, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.fragment.model.FragmentComposition>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class, "getFragmentCompositions",
				_getFragmentCompositionsParameterTypes10);

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

			return (java.util.List
				<com.liferay.fragment.model.FragmentComposition>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getFragmentCompositionsCount(
			HttpPrincipal httpPrincipal, long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class,
				"getFragmentCompositionsCount",
				_getFragmentCompositionsCountParameterTypes11);

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

			return ((Integer)returnObj).intValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentComposition
			moveFragmentComposition(
				HttpPrincipal httpPrincipal, long fragmentCompositionId,
				long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class, "moveFragmentComposition",
				_moveFragmentCompositionParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCompositionId, fragmentCollectionId);

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

			return (com.liferay.fragment.model.FragmentComposition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentComposition
			updateFragmentComposition(
				HttpPrincipal httpPrincipal, long fragmentCompositionId,
				long previewFileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class,
				"updateFragmentComposition",
				_updateFragmentCompositionParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCompositionId, previewFileEntryId);

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

			return (com.liferay.fragment.model.FragmentComposition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentComposition
			updateFragmentComposition(
				HttpPrincipal httpPrincipal, long fragmentCompositionId,
				long fragmentCollectionId, String name, String description,
				String data, long previewFileEntryId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class,
				"updateFragmentComposition",
				_updateFragmentCompositionParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCompositionId, fragmentCollectionId, name,
				description, data, previewFileEntryId, status);

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

			return (com.liferay.fragment.model.FragmentComposition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentComposition
			updateFragmentComposition(
				HttpPrincipal httpPrincipal, long fragmentCompositionId,
				String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class,
				"updateFragmentComposition",
				_updateFragmentCompositionParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCompositionId, name);

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

			return (com.liferay.fragment.model.FragmentComposition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.fragment.model.FragmentComposition
			updateFragmentComposition(
				HttpPrincipal httpPrincipal, long fragmentCompositionId,
				String name, String description, String data,
				long previewFileEntryId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				FragmentCompositionServiceUtil.class,
				"updateFragmentComposition",
				_updateFragmentCompositionParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, fragmentCompositionId, name, description, data,
				previewFileEntryId, status);

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

			return (com.liferay.fragment.model.FragmentComposition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		FragmentCompositionServiceHttp.class);

	private static final Class<?>[] _addFragmentCompositionParameterTypes0 =
		new Class[] {
			String.class, long.class, long.class, String.class, String.class,
			String.class, String.class, long.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteFragmentCompositionParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteFragmentCompositionParameterTypes2 =
		new Class[] {String.class, long.class};
	private static final Class<?>[] _fetchFragmentCompositionParameterTypes3 =
		new Class[] {long.class};
	private static final Class<?>[] _fetchFragmentCompositionParameterTypes4 =
		new Class[] {long.class, String.class};
	private static final Class<?>[]
		_getFragmentCompositionByExternalReferenceCodeParameterTypes5 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _getFragmentCompositionsParameterTypes6 =
		new Class[] {long.class};
	private static final Class<?>[] _getFragmentCompositionsParameterTypes7 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[] _getFragmentCompositionsParameterTypes8 =
		new Class[] {long.class, long.class, int.class};
	private static final Class<?>[] _getFragmentCompositionsParameterTypes9 =
		new Class[] {
			long.class, long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getFragmentCompositionsParameterTypes10 =
		new Class[] {
			long.class, long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getFragmentCompositionsCountParameterTypes11 = new Class[] {
			long.class
		};
	private static final Class<?>[] _moveFragmentCompositionParameterTypes12 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _updateFragmentCompositionParameterTypes13 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _updateFragmentCompositionParameterTypes14 =
		new Class[] {
			long.class, long.class, String.class, String.class, String.class,
			long.class, int.class
		};
	private static final Class<?>[] _updateFragmentCompositionParameterTypes15 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _updateFragmentCompositionParameterTypes16 =
		new Class[] {
			long.class, String.class, String.class, String.class, long.class,
			int.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1215940433