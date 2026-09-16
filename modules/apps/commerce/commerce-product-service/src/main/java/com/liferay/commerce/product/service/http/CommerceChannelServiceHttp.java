/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.http;

import com.liferay.commerce.product.service.CommerceChannelServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CommerceChannelServiceUtil</code> service
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
public class CommerceChannelServiceHttp {

	public static com.liferay.commerce.product.model.CommerceChannel
			addCommerceChannel(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long accountEntryId, long siteGroupId, String name, String type,
				com.liferay.portal.kernel.util.UnicodeProperties
					typeSettingsUnicodeProperties,
				String commerceCurrencyCode,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class, "addCommerceChannel",
				_addCommerceChannelParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, accountEntryId, siteGroupId,
				name, type, typeSettingsUnicodeProperties, commerceCurrencyCode,
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

			return (com.liferay.commerce.product.model.CommerceChannel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceChannel
			addOrUpdateCommerceChannel(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long accountEntryId, long siteGroupId, String name, String type,
				com.liferay.portal.kernel.util.UnicodeProperties
					typeSettingsUnicodeProperties,
				String commerceCurrencyCode,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class, "addOrUpdateCommerceChannel",
				_addOrUpdateCommerceChannelParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, accountEntryId, siteGroupId,
				name, type, typeSettingsUnicodeProperties, commerceCurrencyCode,
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

			return (com.liferay.commerce.product.model.CommerceChannel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceChannel
			deleteCommerceChannel(
				HttpPrincipal httpPrincipal, long commerceChannelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class, "deleteCommerceChannel",
				_deleteCommerceChannelParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceChannelId);

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

			return (com.liferay.commerce.product.model.CommerceChannel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceChannel
			fetchCommerceChannel(
				HttpPrincipal httpPrincipal, long commerceChannelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class, "fetchCommerceChannel",
				_fetchCommerceChannelParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceChannelId);

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

			return (com.liferay.commerce.product.model.CommerceChannel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceChannel
			fetchCommerceChannelByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class,
				"fetchCommerceChannelByExternalReferenceCode",
				_fetchCommerceChannelByExternalReferenceCodeParameterTypes4);

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

			return (com.liferay.commerce.product.model.CommerceChannel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceChannel
			getCommerceChannel(
				HttpPrincipal httpPrincipal, long commerceChannelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class, "getCommerceChannel",
				_getCommerceChannelParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceChannelId);

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

			return (com.liferay.commerce.product.model.CommerceChannel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceChannel
			getCommerceChannelByOrderGroupId(
				HttpPrincipal httpPrincipal, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class,
				"getCommerceChannelByOrderGroupId",
				_getCommerceChannelByOrderGroupIdParameterTypes6);

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

			return (com.liferay.commerce.product.model.CommerceChannel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CommerceChannel>
				getCommerceChannels(HttpPrincipal httpPrincipal, long companyId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class, "getCommerceChannels",
				_getCommerceChannelsParameterTypes7);

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

			return (java.util.List
				<com.liferay.commerce.product.model.CommerceChannel>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CommerceChannel>
				getEligibleCommerceChannels(
					HttpPrincipal httpPrincipal, long accountEntryId,
					String name, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class, "getEligibleCommerceChannels",
				_getEligibleCommerceChannelsParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, accountEntryId, name, start, end);

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
				<com.liferay.commerce.product.model.CommerceChannel>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceChannel
			getOrAddEmptyCommerceChannel(
				HttpPrincipal httpPrincipal, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class,
				"getOrAddEmptyCommerceChannel",
				_getOrAddEmptyCommerceChannelParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode);

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

			return (com.liferay.commerce.product.model.CommerceChannel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CommerceChannel> search(
				HttpPrincipal httpPrincipal, long companyId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class, "search",
				_searchParameterTypes10);

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

			return (java.util.List
				<com.liferay.commerce.product.model.CommerceChannel>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CommerceChannel> search(
				HttpPrincipal httpPrincipal, long companyId, String keywords,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class, "search",
				_searchParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, keywords, start, end, sort);

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
				<com.liferay.commerce.product.model.CommerceChannel>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int searchCommerceChannelsCount(
			HttpPrincipal httpPrincipal, long companyId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class, "searchCommerceChannelsCount",
				_searchCommerceChannelsCountParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, keywords);

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

	public static com.liferay.commerce.product.model.CommerceChannel
			updateCommerceChannel(
				HttpPrincipal httpPrincipal, long commerceChannelId,
				long accountEntryId, long siteGroupId, String name, String type,
				com.liferay.portal.kernel.util.UnicodeProperties
					typeSettingsUnicodeProperties,
				String commerceCurrencyCode, String priceDisplayType,
				boolean discountsTargetNetPrice)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class, "updateCommerceChannel",
				_updateCommerceChannelParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceChannelId, accountEntryId, siteGroupId, name,
				type, typeSettingsUnicodeProperties, commerceCurrencyCode,
				priceDisplayType, discountsTargetNetPrice);

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

			return (com.liferay.commerce.product.model.CommerceChannel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CommerceChannel
			updateCommerceChannelExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long commerceChannelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceChannelServiceUtil.class,
				"updateCommerceChannelExternalReferenceCode",
				_updateCommerceChannelExternalReferenceCodeParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commerceChannelId);

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

			return (com.liferay.commerce.product.model.CommerceChannel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CommerceChannelServiceHttp.class);

	private static final Class<?>[] _addCommerceChannelParameterTypes0 =
		new Class[] {
			String.class, long.class, long.class, String.class, String.class,
			com.liferay.portal.kernel.util.UnicodeProperties.class,
			String.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addOrUpdateCommerceChannelParameterTypes1 =
		new Class[] {
			String.class, long.class, long.class, String.class, String.class,
			com.liferay.portal.kernel.util.UnicodeProperties.class,
			String.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteCommerceChannelParameterTypes2 =
		new Class[] {long.class};
	private static final Class<?>[] _fetchCommerceChannelParameterTypes3 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchCommerceChannelByExternalReferenceCodeParameterTypes4 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _getCommerceChannelParameterTypes5 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getCommerceChannelByOrderGroupIdParameterTypes6 = new Class[] {
			long.class
		};
	private static final Class<?>[] _getCommerceChannelsParameterTypes7 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getEligibleCommerceChannelsParameterTypes8 = new Class[] {
			long.class, String.class, int.class, int.class
		};
	private static final Class<?>[]
		_getOrAddEmptyCommerceChannelParameterTypes9 = new Class[] {
			String.class
		};
	private static final Class<?>[] _searchParameterTypes10 = new Class[] {
		long.class
	};
	private static final Class<?>[] _searchParameterTypes11 = new Class[] {
		long.class, String.class, int.class, int.class,
		com.liferay.portal.kernel.search.Sort.class
	};
	private static final Class<?>[]
		_searchCommerceChannelsCountParameterTypes12 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _updateCommerceChannelParameterTypes13 =
		new Class[] {
			long.class, long.class, long.class, String.class, String.class,
			com.liferay.portal.kernel.util.UnicodeProperties.class,
			String.class, String.class, boolean.class
		};
	private static final Class<?>[]
		_updateCommerceChannelExternalReferenceCodeParameterTypes14 =
			new Class[] {String.class, long.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:1378799471