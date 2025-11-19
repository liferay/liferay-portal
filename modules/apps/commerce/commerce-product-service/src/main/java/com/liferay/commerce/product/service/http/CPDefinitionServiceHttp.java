/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.http;

import com.liferay.commerce.product.service.CPDefinitionServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CPDefinitionServiceUtil</code> service
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
public class CPDefinitionServiceHttp {

	public static com.liferay.commerce.product.model.CPDefinition
			addCPDefinition(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> shortDescriptionMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				java.util.Map<java.util.Locale, String> urlTitleMap,
				java.util.Map<java.util.Locale, String> metaTitleMap,
				java.util.Map<java.util.Locale, String> metaDescriptionMap,
				java.util.Map<java.util.Locale, String> metaKeywordsMap,
				String productTypeName, boolean ignoreSKUCombinations,
				boolean shippable, boolean freeShipping, boolean shipSeparately,
				double shippingExtraPrice, double width, double height,
				double depth, double weight, long cpTaxCategoryId,
				boolean taxExempt, boolean telcoOrElectronics,
				String ddmStructureKey, boolean published, int displayDateMonth,
				int displayDateDay, int displayDateYear, int displayDateHour,
				int displayDateMinute, int expirationDateMonth,
				int expirationDateDay, int expirationDateYear,
				int expirationDateHour, int expirationDateMinute,
				boolean neverExpire, String defaultSku,
				boolean subscriptionEnabled, int subscriptionLength,
				String subscriptionType,
				com.liferay.portal.kernel.util.UnicodeProperties
					subscriptionTypeSettingsUnicodeProperties,
				long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
				int deliverySubscriptionLength, String deliverySubscriptionType,
				com.liferay.portal.kernel.util.UnicodeProperties
					deliverySubscriptionTypeSettingsUnicodeProperties,
				long deliveryMaxSubscriptionCycles,
				boolean accountGroupFilterEnabled, boolean channelFilterEnabled,
				int status,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "addCPDefinition",
				_addCPDefinitionParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, nameMap,
				shortDescriptionMap, descriptionMap, urlTitleMap, metaTitleMap,
				metaDescriptionMap, metaKeywordsMap, productTypeName,
				ignoreSKUCombinations, shippable, freeShipping, shipSeparately,
				shippingExtraPrice, width, height, depth, weight,
				cpTaxCategoryId, taxExempt, telcoOrElectronics, ddmStructureKey,
				published, displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, defaultSku,
				subscriptionEnabled, subscriptionLength, subscriptionType,
				subscriptionTypeSettingsUnicodeProperties,
				maxSubscriptionCycles, deliverySubscriptionEnabled,
				deliverySubscriptionLength, deliverySubscriptionType,
				deliverySubscriptionTypeSettingsUnicodeProperties,
				deliveryMaxSubscriptionCycles, accountGroupFilterEnabled,
				channelFilterEnabled, status, serviceContext);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition
			addOrUpdateCPDefinition(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long cpDefinitionId, long groupId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> shortDescriptionMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				java.util.Map<java.util.Locale, String> urlTitleMap,
				java.util.Map<java.util.Locale, String> metaTitleMap,
				java.util.Map<java.util.Locale, String> metaDescriptionMap,
				java.util.Map<java.util.Locale, String> metaKeywordsMap,
				String productTypeName, boolean ignoreSKUCombinations,
				boolean shippable, boolean freeShipping, boolean shipSeparately,
				double shippingExtraPrice, double width, double height,
				double depth, double weight, long cpTaxCategoryId,
				boolean taxExempt, boolean telcoOrElectronics,
				String ddmStructureKey, boolean published, int displayDateMonth,
				int displayDateDay, int displayDateYear, int displayDateHour,
				int displayDateMinute, int expirationDateMonth,
				int expirationDateDay, int expirationDateYear,
				int expirationDateHour, int expirationDateMinute,
				boolean neverExpire, String defaultSku,
				boolean subscriptionEnabled, int subscriptionLength,
				String subscriptionType,
				com.liferay.portal.kernel.util.UnicodeProperties
					subscriptionTypeSettingsUnicodeProperties,
				long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
				int deliverySubscriptionLength, String deliverySubscriptionType,
				com.liferay.portal.kernel.util.UnicodeProperties
					deliverySubscriptionTypeSettingsUnicodeProperties,
				long deliveryMaxSubscriptionCycles,
				boolean accountGroupFilterEnabled, boolean channelFilterEnabled,
				int status,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "addOrUpdateCPDefinition",
				_addOrUpdateCPDefinitionParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, cpDefinitionId, groupId,
				nameMap, shortDescriptionMap, descriptionMap, urlTitleMap,
				metaTitleMap, metaDescriptionMap, metaKeywordsMap,
				productTypeName, ignoreSKUCombinations, shippable, freeShipping,
				shipSeparately, shippingExtraPrice, width, height, depth,
				weight, cpTaxCategoryId, taxExempt, telcoOrElectronics,
				ddmStructureKey, published, displayDateMonth, displayDateDay,
				displayDateYear, displayDateHour, displayDateMinute,
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, neverExpire,
				defaultSku, subscriptionEnabled, subscriptionLength,
				subscriptionType, subscriptionTypeSettingsUnicodeProperties,
				maxSubscriptionCycles, deliverySubscriptionEnabled,
				deliverySubscriptionLength, deliverySubscriptionType,
				deliverySubscriptionTypeSettingsUnicodeProperties,
				deliveryMaxSubscriptionCycles, accountGroupFilterEnabled,
				channelFilterEnabled, status, serviceContext);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition
			cloneCPDefinition(
				HttpPrincipal httpPrincipal, long cpDefinitionId, long groupId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "cloneCPDefinition",
				_cloneCPDefinitionParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, groupId, serviceContext);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition
			copyCPDefinition(
				HttpPrincipal httpPrincipal, long sourceCPDefinitionId,
				long groupId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "copyCPDefinition",
				_copyCPDefinitionParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, sourceCPDefinitionId, groupId, status);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteAssetCategoryCPDefinition(
			HttpPrincipal httpPrincipal, long cpDefinitionId, long categoryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class,
				"deleteAssetCategoryCPDefinition",
				_deleteAssetCategoryCPDefinitionParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, categoryId, serviceContext);

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

	public static void deleteCPDefinition(
			HttpPrincipal httpPrincipal, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "deleteCPDefinition",
				_deleteCPDefinitionParameterTypes5);

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

	public static com.liferay.commerce.product.model.CPDefinition
			fetchCPDefinition(HttpPrincipal httpPrincipal, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "fetchCPDefinition",
				_fetchCPDefinitionParameterTypes6);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition
			fetchCPDefinitionByCProductExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class,
				"fetchCPDefinitionByCProductExternalReferenceCode",
				_fetchCPDefinitionByCProductExternalReferenceCodeParameterTypes7);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition
			fetchCPDefinitionByCProductId(
				HttpPrincipal httpPrincipal, long cProductId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "fetchCPDefinitionByCProductId",
				_fetchCPDefinitionByCProductIdParameterTypes8);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition
			getCPDefinition(HttpPrincipal httpPrincipal, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "getCPDefinition",
				_getCPDefinitionParameterTypes9);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CPDefinition> getCPDefinitions(
				HttpPrincipal httpPrincipal, long groupId, int status,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.product.model.CPDefinition>
						orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "getCPDefinitions",
				_getCPDefinitionsParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, status, start, end, orderByComparator);

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
				<com.liferay.commerce.product.model.CPDefinition>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCPDefinitionsCount(
			HttpPrincipal httpPrincipal, long groupId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "getCPDefinitionsCount",
				_getCPDefinitionsCountParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, status);

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

	public static com.liferay.commerce.product.model.CPDefinition
			getCProductCPDefinition(
				HttpPrincipal httpPrincipal, long cProductId, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "getCProductCPDefinition",
				_getCProductCPDefinitionParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cProductId, version);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CPDefinition>
				getCProductCPDefinitions(
					HttpPrincipal httpPrincipal, long cProductId, int status,
					int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "getCProductCPDefinitions",
				_getCProductCPDefinitionsParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cProductId, status, start, end);

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
				<com.liferay.commerce.product.model.CPDefinition>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPAttachmentFileEntry
			getDefaultImageCPAttachmentFileEntry(
				HttpPrincipal httpPrincipal, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class,
				"getDefaultImageCPAttachmentFileEntry",
				_getDefaultImageCPAttachmentFileEntryParameterTypes14);

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

			return (com.liferay.commerce.product.model.CPAttachmentFileEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.Map<java.util.Locale, String> getUrlTitleMap(
			HttpPrincipal httpPrincipal, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "getUrlTitleMap",
				_getUrlTitleMapParameterTypes15);

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

			return (java.util.Map<java.util.Locale, String>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static String getUrlTitleMapAsXML(
			HttpPrincipal httpPrincipal, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "getUrlTitleMapAsXML",
				_getUrlTitleMapAsXMLParameterTypes16);

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

			return (String)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.product.model.CPDefinition> searchCPDefinitions(
				HttpPrincipal httpPrincipal, long companyId, String keywords,
				int status, boolean ignoreCommerceAccountGroup, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "searchCPDefinitions",
				_searchCPDefinitionsParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, keywords, status,
				ignoreCommerceAccountGroup, start, end, sort);

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
				<com.liferay.commerce.product.model.CPDefinition>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.product.model.CPDefinition> searchCPDefinitions(
				HttpPrincipal httpPrincipal, long companyId, String keywords,
				String filterFields, String filterValues, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "searchCPDefinitions",
				_searchCPDefinitionsParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, keywords, filterFields, filterValues,
				start, end, sort);

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
				<com.liferay.commerce.product.model.CPDefinition>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.product.model.CPDefinition>
				searchCPDefinitionsByChannelGroupId(
					HttpPrincipal httpPrincipal, long companyId,
					long commerceChannelGroupId, String keywords, int status,
					boolean ignoreCommerceAccountGroup, int start, int end,
					com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class,
				"searchCPDefinitionsByChannelGroupId",
				_searchCPDefinitionsByChannelGroupIdParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, commerceChannelGroupId, keywords, status,
				ignoreCommerceAccountGroup, start, end, sort);

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
				<com.liferay.commerce.product.model.CPDefinition>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition
			updateCPDefinition(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> shortDescriptionMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				java.util.Map<java.util.Locale, String> urlTitleMap,
				java.util.Map<java.util.Locale, String> metaTitleMap,
				java.util.Map<java.util.Locale, String> metaDescriptionMap,
				java.util.Map<java.util.Locale, String> metaKeywordsMap,
				boolean ignoreSKUCombinations, boolean shippable,
				boolean freeShipping, boolean shipSeparately,
				double shippingExtraPrice, double width, double height,
				double depth, double weight, long cpTaxCategoryId,
				boolean taxExempt, boolean telcoOrElectronics,
				String ddmStructureKey, boolean published, int displayDateMonth,
				int displayDateDay, int displayDateYear, int displayDateHour,
				int displayDateMinute, int expirationDateMonth,
				int expirationDateDay, int expirationDateYear,
				int expirationDateHour, int expirationDateMinute,
				boolean accountGroupFilterEnabled, boolean channelFilterEnabled,
				boolean neverExpire,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "updateCPDefinition",
				_updateCPDefinitionParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, nameMap, shortDescriptionMap,
				descriptionMap, urlTitleMap, metaTitleMap, metaDescriptionMap,
				metaKeywordsMap, ignoreSKUCombinations, shippable, freeShipping,
				shipSeparately, shippingExtraPrice, width, height, depth,
				weight, cpTaxCategoryId, taxExempt, telcoOrElectronics,
				ddmStructureKey, published, displayDateMonth, displayDateDay,
				displayDateYear, displayDateHour, displayDateMinute,
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute,
				accountGroupFilterEnabled, channelFilterEnabled, neverExpire,
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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition
			updateCPDefinitionAccountGroupFilter(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				boolean enable)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class,
				"updateCPDefinitionAccountGroupFilter",
				_updateCPDefinitionAccountGroupFilterParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, enable);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition
			updateCPDefinitionCategorization(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class,
				"updateCPDefinitionCategorization",
				_updateCPDefinitionCategorizationParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, serviceContext);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition
			updateCPDefinitionChannelFilter(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				boolean enable)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class,
				"updateCPDefinitionChannelFilter",
				_updateCPDefinitionChannelFilterParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, enable);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition
			updateExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "updateExternalReferenceCode",
				_updateExternalReferenceCodeParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, cpDefinitionId);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition
			updateShippingInfo(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				boolean shippable, boolean freeShipping, boolean shipSeparately,
				double shippingExtraPrice, double width, double height,
				double depth, double weight,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "updateShippingInfo",
				_updateShippingInfoParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, shippable, freeShipping,
				shipSeparately, shippingExtraPrice, width, height, depth,
				weight, serviceContext);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition updateStatus(
			HttpPrincipal httpPrincipal, long cpDefinitionId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "updateStatus",
				_updateStatusParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, status, serviceContext,
				workflowContext);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition
			updateSubscriptionInfo(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				boolean subscriptionEnabled, int subscriptionLength,
				String subscriptionType,
				com.liferay.portal.kernel.util.UnicodeProperties
					subscriptionTypeSettingsUnicodeProperties,
				long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
				int deliverySubscriptionLength, String deliverySubscriptionType,
				com.liferay.portal.kernel.util.UnicodeProperties
					deliverySubscriptionTypeSettingsUnicodeProperties,
				long deliveryMaxSubscriptionCycles)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "updateSubscriptionInfo",
				_updateSubscriptionInfoParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, subscriptionEnabled,
				subscriptionLength, subscriptionType,
				subscriptionTypeSettingsUnicodeProperties,
				maxSubscriptionCycles, deliverySubscriptionEnabled,
				deliverySubscriptionLength, deliverySubscriptionType,
				deliverySubscriptionTypeSettingsUnicodeProperties,
				deliveryMaxSubscriptionCycles);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinition
			updateTaxCategoryInfo(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				long cpTaxCategoryId, boolean taxExempt,
				boolean telcoOrElectronics)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionServiceUtil.class, "updateTaxCategoryInfo",
				_updateTaxCategoryInfoParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, cpTaxCategoryId, taxExempt,
				telcoOrElectronics);

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

			return (com.liferay.commerce.product.model.CPDefinition)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CPDefinitionServiceHttp.class);

	private static final Class<?>[] _addCPDefinitionParameterTypes0 =
		new Class[] {
			String.class, long.class, java.util.Map.class, java.util.Map.class,
			java.util.Map.class, java.util.Map.class, java.util.Map.class,
			java.util.Map.class, java.util.Map.class, String.class,
			boolean.class, boolean.class, boolean.class, boolean.class,
			double.class, double.class, double.class, double.class,
			double.class, long.class, boolean.class, boolean.class,
			String.class, boolean.class, int.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, boolean.class, String.class, boolean.class, int.class,
			String.class,
			com.liferay.portal.kernel.util.UnicodeProperties.class, long.class,
			boolean.class, int.class, String.class,
			com.liferay.portal.kernel.util.UnicodeProperties.class, long.class,
			boolean.class, boolean.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addOrUpdateCPDefinitionParameterTypes1 =
		new Class[] {
			String.class, long.class, long.class, java.util.Map.class,
			java.util.Map.class, java.util.Map.class, java.util.Map.class,
			java.util.Map.class, java.util.Map.class, java.util.Map.class,
			String.class, boolean.class, boolean.class, boolean.class,
			boolean.class, double.class, double.class, double.class,
			double.class, double.class, long.class, boolean.class,
			boolean.class, String.class, boolean.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, boolean.class, String.class, boolean.class,
			int.class, String.class,
			com.liferay.portal.kernel.util.UnicodeProperties.class, long.class,
			boolean.class, int.class, String.class,
			com.liferay.portal.kernel.util.UnicodeProperties.class, long.class,
			boolean.class, boolean.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _cloneCPDefinitionParameterTypes2 =
		new Class[] {
			long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _copyCPDefinitionParameterTypes3 =
		new Class[] {long.class, long.class, int.class};
	private static final Class<?>[]
		_deleteAssetCategoryCPDefinitionParameterTypes4 = new Class[] {
			long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteCPDefinitionParameterTypes5 =
		new Class[] {long.class};
	private static final Class<?>[] _fetchCPDefinitionParameterTypes6 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchCPDefinitionByCProductExternalReferenceCodeParameterTypes7 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_fetchCPDefinitionByCProductIdParameterTypes8 = new Class[] {
			long.class
		};
	private static final Class<?>[] _getCPDefinitionParameterTypes9 =
		new Class[] {long.class};
	private static final Class<?>[] _getCPDefinitionsParameterTypes10 =
		new Class[] {
			long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getCPDefinitionsCountParameterTypes11 =
		new Class[] {long.class, int.class};
	private static final Class<?>[] _getCProductCPDefinitionParameterTypes12 =
		new Class[] {long.class, int.class};
	private static final Class<?>[] _getCProductCPDefinitionsParameterTypes13 =
		new Class[] {long.class, int.class, int.class, int.class};
	private static final Class<?>[]
		_getDefaultImageCPAttachmentFileEntryParameterTypes14 = new Class[] {
			long.class
		};
	private static final Class<?>[] _getUrlTitleMapParameterTypes15 =
		new Class[] {long.class};
	private static final Class<?>[] _getUrlTitleMapAsXMLParameterTypes16 =
		new Class[] {long.class};
	private static final Class<?>[] _searchCPDefinitionsParameterTypes17 =
		new Class[] {
			long.class, String.class, int.class, boolean.class, int.class,
			int.class, com.liferay.portal.kernel.search.Sort.class
		};
	private static final Class<?>[] _searchCPDefinitionsParameterTypes18 =
		new Class[] {
			long.class, String.class, String.class, String.class, int.class,
			int.class, com.liferay.portal.kernel.search.Sort.class
		};
	private static final Class<?>[]
		_searchCPDefinitionsByChannelGroupIdParameterTypes19 = new Class[] {
			long.class, long.class, String.class, int.class, boolean.class,
			int.class, int.class, com.liferay.portal.kernel.search.Sort.class
		};
	private static final Class<?>[] _updateCPDefinitionParameterTypes20 =
		new Class[] {
			long.class, java.util.Map.class, java.util.Map.class,
			java.util.Map.class, java.util.Map.class, java.util.Map.class,
			java.util.Map.class, java.util.Map.class, boolean.class,
			boolean.class, boolean.class, boolean.class, double.class,
			double.class, double.class, double.class, double.class, long.class,
			boolean.class, boolean.class, String.class, boolean.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, int.class, int.class, boolean.class,
			boolean.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateCPDefinitionAccountGroupFilterParameterTypes21 = new Class[] {
			long.class, boolean.class
		};
	private static final Class<?>[]
		_updateCPDefinitionCategorizationParameterTypes22 = new Class[] {
			long.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateCPDefinitionChannelFilterParameterTypes23 = new Class[] {
			long.class, boolean.class
		};
	private static final Class<?>[]
		_updateExternalReferenceCodeParameterTypes24 = new Class[] {
			String.class, long.class
		};
	private static final Class<?>[] _updateShippingInfoParameterTypes25 =
		new Class[] {
			long.class, boolean.class, boolean.class, boolean.class,
			double.class, double.class, double.class, double.class,
			double.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateStatusParameterTypes26 =
		new Class[] {
			long.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class,
			java.util.Map.class
		};
	private static final Class<?>[] _updateSubscriptionInfoParameterTypes27 =
		new Class[] {
			long.class, boolean.class, int.class, String.class,
			com.liferay.portal.kernel.util.UnicodeProperties.class, long.class,
			boolean.class, int.class, String.class,
			com.liferay.portal.kernel.util.UnicodeProperties.class, long.class
		};
	private static final Class<?>[] _updateTaxCategoryInfoParameterTypes28 =
		new Class[] {long.class, long.class, boolean.class, boolean.class};

}