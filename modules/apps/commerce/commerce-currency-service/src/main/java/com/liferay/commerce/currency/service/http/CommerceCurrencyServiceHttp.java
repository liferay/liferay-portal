/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.service.http;

import com.liferay.commerce.currency.service.CommerceCurrencyServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CommerceCurrencyServiceUtil</code> service
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
 * @author Andrea Di Giorgi
 * @generated
 */
public class CommerceCurrencyServiceHttp {

	public static com.liferay.commerce.currency.model.CommerceCurrency
			addCommerceCurrency(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				String code, java.util.Map<java.util.Locale, String> nameMap,
				String symbol, java.math.BigDecimal rate,
				java.util.Map<java.util.Locale, String> formatPatternMap,
				int maxFractionDigits, int minFractionDigits,
				String roundingMode, boolean primary, double priority,
				boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class, "addCommerceCurrency",
				_addCommerceCurrencyParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, code, nameMap, symbol, rate,
				formatPatternMap, maxFractionDigits, minFractionDigits,
				roundingMode, primary, priority, active);

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

			return (com.liferay.commerce.currency.model.CommerceCurrency)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteCommerceCurrency(
			HttpPrincipal httpPrincipal, long commerceCurrencyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class, "deleteCommerceCurrency",
				_deleteCommerceCurrencyParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceCurrencyId);

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

	public static com.liferay.commerce.currency.model.CommerceCurrency
			fetchCommerceCurrencyByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class,
				"fetchCommerceCurrencyByExternalReferenceCode",
				_fetchCommerceCurrencyByExternalReferenceCodeParameterTypes2);

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

			return (com.liferay.commerce.currency.model.CommerceCurrency)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.currency.model.CommerceCurrency
			fetchPrimaryCommerceCurrency(
				HttpPrincipal httpPrincipal, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class,
				"fetchPrimaryCommerceCurrency",
				_fetchPrimaryCommerceCurrencyParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId);

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

			return (com.liferay.commerce.currency.model.CommerceCurrency)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.currency.model.CommerceCurrency>
				getCommerceCurrencies(
					HttpPrincipal httpPrincipal, long companyId, boolean active,
					int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.currency.model.CommerceCurrency>
							orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class, "getCommerceCurrencies",
				_getCommerceCurrenciesParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, active, start, end, orderByComparator);

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
				<com.liferay.commerce.currency.model.CommerceCurrency>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.currency.model.CommerceCurrency>
				getCommerceCurrencies(
					HttpPrincipal httpPrincipal, long companyId, int start,
					int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.currency.model.CommerceCurrency>
							orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class, "getCommerceCurrencies",
				_getCommerceCurrenciesParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, start, end, orderByComparator);

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
				<com.liferay.commerce.currency.model.CommerceCurrency>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCommerceCurrenciesCount(
			HttpPrincipal httpPrincipal, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class, "getCommerceCurrenciesCount",
				_getCommerceCurrenciesCountParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId);

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

	public static int getCommerceCurrenciesCount(
			HttpPrincipal httpPrincipal, long companyId, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class, "getCommerceCurrenciesCount",
				_getCommerceCurrenciesCountParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, active);

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

	public static com.liferay.commerce.currency.model.CommerceCurrency
			getCommerceCurrency(
				HttpPrincipal httpPrincipal, long commerceCurrencyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class, "getCommerceCurrency",
				_getCommerceCurrencyParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceCurrencyId);

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

			return (com.liferay.commerce.currency.model.CommerceCurrency)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.currency.model.CommerceCurrency
			getCommerceCurrency(
				HttpPrincipal httpPrincipal, long companyId, String code)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class, "getCommerceCurrency",
				_getCommerceCurrencyParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, code);

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

			return (com.liferay.commerce.currency.model.CommerceCurrency)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.currency.model.CommerceCurrency
			getOrAddEmptyCommerceCurrency(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				String code)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class,
				"getOrAddEmptyCommerceCurrency",
				_getOrAddEmptyCommerceCurrencyParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, code);

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

			return (com.liferay.commerce.currency.model.CommerceCurrency)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.currency.model.CommerceCurrency>
				searchCommerceCurrencies(
					HttpPrincipal httpPrincipal, long companyId,
					String keywords,
					java.util.LinkedHashMap<String, Object> params, int start,
					int end, com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class, "searchCommerceCurrencies",
				_searchCommerceCurrenciesParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, keywords, params, start, end, sort);

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
				<com.liferay.commerce.currency.model.CommerceCurrency>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.currency.model.CommerceCurrency
			setActive(
				HttpPrincipal httpPrincipal, long commerceCurrencyId,
				boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class, "setActive",
				_setActiveParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceCurrencyId, active);

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

			return (com.liferay.commerce.currency.model.CommerceCurrency)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.currency.model.CommerceCurrency
			setPrimary(
				HttpPrincipal httpPrincipal, long commerceCurrencyId,
				boolean primary)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class, "setPrimary",
				_setPrimaryParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceCurrencyId, primary);

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

			return (com.liferay.commerce.currency.model.CommerceCurrency)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.currency.model.CommerceCurrency
			updateCommerceCurrency(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long commerceCurrencyId,
				java.util.Map<java.util.Locale, String> nameMap, String symbol,
				java.math.BigDecimal rate,
				java.util.Map<java.util.Locale, String> formatPatternMap,
				int maxFractionDigits, int minFractionDigits,
				String roundingMode, boolean primary, double priority,
				boolean active,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class, "updateCommerceCurrency",
				_updateCommerceCurrencyParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commerceCurrencyId, nameMap,
				symbol, rate, formatPatternMap, maxFractionDigits,
				minFractionDigits, roundingMode, primary, priority, active,
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

			return (com.liferay.commerce.currency.model.CommerceCurrency)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void updateExchangeRate(
			HttpPrincipal httpPrincipal, long commerceCurrencyId,
			String exchangeRateProviderKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class, "updateExchangeRate",
				_updateExchangeRateParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceCurrencyId, exchangeRateProviderKey);

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

	public static void updateExchangeRates(HttpPrincipal httpPrincipal)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceCurrencyServiceUtil.class, "updateExchangeRates",
				_updateExchangeRatesParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(methodKey);

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
		CommerceCurrencyServiceHttp.class);

	private static final Class<?>[] _addCommerceCurrencyParameterTypes0 =
		new Class[] {
			String.class, String.class, java.util.Map.class, String.class,
			java.math.BigDecimal.class, java.util.Map.class, int.class,
			int.class, String.class, boolean.class, double.class, boolean.class
		};
	private static final Class<?>[] _deleteCommerceCurrencyParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchCommerceCurrencyByExternalReferenceCodeParameterTypes2 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_fetchPrimaryCommerceCurrencyParameterTypes3 = new Class[] {long.class};
	private static final Class<?>[] _getCommerceCurrenciesParameterTypes4 =
		new Class[] {
			long.class, boolean.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getCommerceCurrenciesParameterTypes5 =
		new Class[] {
			long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getCommerceCurrenciesCountParameterTypes6 =
		new Class[] {long.class};
	private static final Class<?>[] _getCommerceCurrenciesCountParameterTypes7 =
		new Class[] {long.class, boolean.class};
	private static final Class<?>[] _getCommerceCurrencyParameterTypes8 =
		new Class[] {long.class};
	private static final Class<?>[] _getCommerceCurrencyParameterTypes9 =
		new Class[] {long.class, String.class};
	private static final Class<?>[]
		_getOrAddEmptyCommerceCurrencyParameterTypes10 = new Class[] {
			String.class, String.class
		};
	private static final Class<?>[] _searchCommerceCurrenciesParameterTypes11 =
		new Class[] {
			long.class, String.class, java.util.LinkedHashMap.class, int.class,
			int.class, com.liferay.portal.kernel.search.Sort.class
		};
	private static final Class<?>[] _setActiveParameterTypes12 = new Class[] {
		long.class, boolean.class
	};
	private static final Class<?>[] _setPrimaryParameterTypes13 = new Class[] {
		long.class, boolean.class
	};
	private static final Class<?>[] _updateCommerceCurrencyParameterTypes14 =
		new Class[] {
			String.class, long.class, java.util.Map.class, String.class,
			java.math.BigDecimal.class, java.util.Map.class, int.class,
			int.class, String.class, boolean.class, double.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateExchangeRateParameterTypes15 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _updateExchangeRatesParameterTypes16 =
		new Class[] {};

}
// LIFERAY-SERVICE-BUILDER-HASH:-449708529