/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.http;

import com.liferay.commerce.price.list.service.CommercePriceListServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CommercePriceListServiceUtil</code> service
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
public class CommercePriceListServiceHttp {

	public static com.liferay.commerce.price.list.model.CommercePriceList
			addCommercePriceList(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, boolean catalogBasePriceList,
				String commerceCurrencyCode, int displayDateDay,
				int displayDateHour, int displayDateMinute,
				int displayDateMonth, int displayDateYear,
				int expirationDateDay, int expirationDateHour,
				int expirationDateMinute, int expirationDateMonth,
				int expirationDateYear, String name, boolean netPrice,
				boolean neverExpire, long parentCommercePriceListId,
				double priority, String type,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class, "addCommercePriceList",
				_addCommercePriceListParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, catalogBasePriceList,
				commerceCurrencyCode, displayDateDay, displayDateHour,
				displayDateMinute, displayDateMonth, displayDateYear,
				expirationDateDay, expirationDateHour, expirationDateMinute,
				expirationDateMonth, expirationDateYear, name, netPrice,
				neverExpire, parentCommercePriceListId, priority, type,
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

			return (com.liferay.commerce.price.list.model.CommercePriceList)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.price.list.model.CommercePriceList
			addOrUpdateCommercePriceList(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, long commercePriceListId,
				boolean catalogBasePriceList, String commerceCurrencyCode,
				int displayDateDay, int displayDateHour, int displayDateMinute,
				int displayDateMonth, int displayDateYear,
				int expirationDateDay, int expirationDateHour,
				int expirationDateMinute, int expirationDateMonth,
				int expirationDateYear, String name, boolean netPrice,
				boolean neverExpire, long parentCommercePriceListId,
				double priority, String type,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class,
				"addOrUpdateCommercePriceList",
				_addOrUpdateCommercePriceListParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, commercePriceListId,
				catalogBasePriceList, commerceCurrencyCode, displayDateDay,
				displayDateHour, displayDateMinute, displayDateMonth,
				displayDateYear, expirationDateDay, expirationDateHour,
				expirationDateMinute, expirationDateMonth, expirationDateYear,
				name, netPrice, neverExpire, parentCommercePriceListId,
				priority, type, serviceContext);

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

			return (com.liferay.commerce.price.list.model.CommercePriceList)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteCommercePriceList(
			HttpPrincipal httpPrincipal, long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class, "deleteCommercePriceList",
				_deleteCommercePriceListParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePriceListId);

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

	public static com.liferay.commerce.price.list.model.CommercePriceList
			fetchCatalogBaseCommercePriceListByType(
				HttpPrincipal httpPrincipal, long groupId, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class,
				"fetchCatalogBaseCommercePriceListByType",
				_fetchCatalogBaseCommercePriceListByTypeParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, type);

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

			return (com.liferay.commerce.price.list.model.CommercePriceList)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.price.list.model.CommercePriceList
			fetchCommercePriceList(
				HttpPrincipal httpPrincipal, long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class, "fetchCommercePriceList",
				_fetchCommercePriceListParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePriceListId);

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

			return (com.liferay.commerce.price.list.model.CommercePriceList)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.price.list.model.CommercePriceList
			fetchCommercePriceListByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class,
				"fetchCommercePriceListByExternalReferenceCode",
				_fetchCommercePriceListByExternalReferenceCodeParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, companyId);

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

			return (com.liferay.commerce.price.list.model.CommercePriceList)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.price.list.model.CommercePriceList
			getCommercePriceList(
				HttpPrincipal httpPrincipal, long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class, "getCommercePriceList",
				_getCommercePriceListParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePriceListId);

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

			return (com.liferay.commerce.price.list.model.CommercePriceList)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.price.list.model.CommercePriceList>
				getCommercePriceLists(
					HttpPrincipal httpPrincipal, long companyId, int status,
					int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.price.list.model.
							CommercePriceList> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class, "getCommercePriceLists",
				_getCommercePriceListsParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, status, start, end, orderByComparator);

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
				<com.liferay.commerce.price.list.model.CommercePriceList>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.price.list.model.CommercePriceList>
				getCommercePriceLists(
					HttpPrincipal httpPrincipal, long companyId, String type,
					int status, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.price.list.model.
							CommercePriceList> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class, "getCommercePriceLists",
				_getCommercePriceListsParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, type, status, start, end,
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
				<com.liferay.commerce.price.list.model.CommercePriceList>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCommercePriceListsCount(
			HttpPrincipal httpPrincipal, long companyId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class,
				"getCommercePriceListsCount",
				_getCommercePriceListsCountParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, status);

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

	public static int getCommercePriceListsCount(
			HttpPrincipal httpPrincipal, long commercePricingClassId,
			String title)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class,
				"getCommercePriceListsCount",
				_getCommercePriceListsCountParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassId, title);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.security.auth.
							PrincipalException) {

					throw (com.liferay.portal.kernel.security.auth.
						PrincipalException)exception;
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
		<com.liferay.commerce.price.list.model.CommercePriceList>
				searchByCommercePricingClassId(
					HttpPrincipal httpPrincipal, long commercePricingClassId,
					String name, int start, int end)
			throws com.liferay.portal.kernel.security.auth.PrincipalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class,
				"searchByCommercePricingClassId",
				_searchByCommercePricingClassIdParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassId, name, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.security.auth.
							PrincipalException) {

					throw (com.liferay.portal.kernel.security.auth.
						PrincipalException)exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.commerce.price.list.model.CommercePriceList>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.price.list.model.CommercePriceList>
				searchCommercePriceLists(
					HttpPrincipal httpPrincipal, long companyId,
					String keywords, int status, int start, int end,
					com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class, "searchCommercePriceLists",
				_searchCommercePriceListsParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, keywords, status, start, end, sort);

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

			return (com.liferay.portal.kernel.search.BaseModelSearchResult
				<com.liferay.commerce.price.list.model.CommercePriceList>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int searchCommercePriceListsCount(
			HttpPrincipal httpPrincipal, long companyId, String keywords,
			int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class,
				"searchCommercePriceListsCount",
				_searchCommercePriceListsCountParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, keywords, status);

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

	public static void setCatalogBasePriceList(
			HttpPrincipal httpPrincipal, long groupId, long commercePriceListId,
			String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class, "setCatalogBasePriceList",
				_setCatalogBasePriceListParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, commercePriceListId, type);

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

	public static com.liferay.commerce.price.list.model.CommercePriceList
			updateCommercePriceList(
				HttpPrincipal httpPrincipal, long commercePriceListId,
				boolean catalogBasePriceList, String commerceCurrencyCode,
				int displayDateDay, int displayDateHour, int displayDateMinute,
				int displayDateMonth, int displayDateYear,
				int expirationDateDay, int expirationDateHour,
				int expirationDateMinute, int expirationDateMonth,
				int expirationDateYear, String name, boolean netPrice,
				boolean neverExpire, long parentCommercePriceListId,
				double priority, String type,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class, "updateCommercePriceList",
				_updateCommercePriceListParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePriceListId, catalogBasePriceList,
				commerceCurrencyCode, displayDateDay, displayDateHour,
				displayDateMinute, displayDateMonth, displayDateYear,
				expirationDateDay, expirationDateHour, expirationDateMinute,
				expirationDateMonth, expirationDateYear, name, netPrice,
				neverExpire, parentCommercePriceListId, priority, type,
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

			return (com.liferay.commerce.price.list.model.CommercePriceList)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.price.list.model.CommercePriceList
			updateExternalReferenceCode(
				HttpPrincipal httpPrincipal,
				com.liferay.commerce.price.list.model.CommercePriceList
					commercePriceList,
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceListServiceUtil.class,
				"updateExternalReferenceCode",
				_updateExternalReferenceCodeParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePriceList, externalReferenceCode, companyId);

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

			return (com.liferay.commerce.price.list.model.CommercePriceList)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CommercePriceListServiceHttp.class);

	private static final Class<?>[] _addCommercePriceListParameterTypes0 =
		new Class[] {
			String.class, long.class, boolean.class, String.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, int.class, String.class, boolean.class,
			boolean.class, long.class, double.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_addOrUpdateCommercePriceListParameterTypes1 = new Class[] {
			String.class, long.class, long.class, boolean.class, String.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, int.class, int.class, String.class,
			boolean.class, boolean.class, long.class, double.class,
			String.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteCommercePriceListParameterTypes2 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchCatalogBaseCommercePriceListByTypeParameterTypes3 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _fetchCommercePriceListParameterTypes4 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchCommercePriceListByExternalReferenceCodeParameterTypes5 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _getCommercePriceListParameterTypes6 =
		new Class[] {long.class};
	private static final Class<?>[] _getCommercePriceListsParameterTypes7 =
		new Class[] {
			long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getCommercePriceListsParameterTypes8 =
		new Class[] {
			long.class, String.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getCommercePriceListsCountParameterTypes9 =
		new Class[] {long.class, int.class};
	private static final Class<?>[]
		_getCommercePriceListsCountParameterTypes10 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[]
		_searchByCommercePricingClassIdParameterTypes11 = new Class[] {
			long.class, String.class, int.class, int.class
		};
	private static final Class<?>[] _searchCommercePriceListsParameterTypes12 =
		new Class[] {
			long.class, String.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.search.Sort.class
		};
	private static final Class<?>[]
		_searchCommercePriceListsCountParameterTypes13 = new Class[] {
			long.class, String.class, int.class
		};
	private static final Class<?>[] _setCatalogBasePriceListParameterTypes14 =
		new Class[] {long.class, long.class, String.class};
	private static final Class<?>[] _updateCommercePriceListParameterTypes15 =
		new Class[] {
			long.class, boolean.class, String.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, String.class, boolean.class, boolean.class,
			long.class, double.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateExternalReferenceCodeParameterTypes16 = new Class[] {
			com.liferay.commerce.price.list.model.CommercePriceList.class,
			String.class, long.class
		};

}