/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.http;

import com.liferay.commerce.product.service.CPDefinitionOptionRelServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CPDefinitionOptionRelServiceUtil</code> service
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
public class CPDefinitionOptionRelServiceHttp {

	public static com.liferay.commerce.product.model.CPDefinitionOptionRel
			addCPDefinitionOptionRel(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				long cpOptionId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				String commerceOptionTypeKey, double priority,
				boolean facetable, boolean required, boolean skuContributor,
				boolean importOptionValue,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"addCPDefinitionOptionRel",
				_addCPDefinitionOptionRelParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, cpOptionId, nameMap, descriptionMap,
				commerceOptionTypeKey, priority, facetable, required,
				skuContributor, importOptionValue, serviceContext);

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

			return (com.liferay.commerce.product.model.CPDefinitionOptionRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinitionOptionRel
			addCPDefinitionOptionRel(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				long cpOptionId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				String commerceOptionTypeKey, String infoItemServiceKey,
				double priority, boolean definedExternally, boolean facetable,
				boolean required, boolean skuContributor,
				boolean importOptionValue, String priceType,
				String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"addCPDefinitionOptionRel",
				_addCPDefinitionOptionRelParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, cpOptionId, nameMap, descriptionMap,
				commerceOptionTypeKey, infoItemServiceKey, priority,
				definedExternally, facetable, required, skuContributor,
				importOptionValue, priceType, typeSettings, serviceContext);

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

			return (com.liferay.commerce.product.model.CPDefinitionOptionRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinitionOptionRel
			addCPDefinitionOptionRel(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				long cpOptionId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"addCPDefinitionOptionRel",
				_addCPDefinitionOptionRelParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, cpOptionId, serviceContext);

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

			return (com.liferay.commerce.product.model.CPDefinitionOptionRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteCPDefinitionOptionRel(
			HttpPrincipal httpPrincipal, long cpDefinitionOptionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"deleteCPDefinitionOptionRel",
				_deleteCPDefinitionOptionRelParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionOptionRelId);

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

	public static com.liferay.commerce.product.model.CPDefinitionOptionRel
			fetchCPDefinitionOptionRel(
				HttpPrincipal httpPrincipal, long cpDefinitionOptionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"fetchCPDefinitionOptionRel",
				_fetchCPDefinitionOptionRelParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionOptionRelId);

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

			return (com.liferay.commerce.product.model.CPDefinitionOptionRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinitionOptionRel
			fetchCPDefinitionOptionRel(
				HttpPrincipal httpPrincipal, long cpDefinitionId,
				long cpOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"fetchCPDefinitionOptionRel",
				_fetchCPDefinitionOptionRelParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, cpOptionId);

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

			return (com.liferay.commerce.product.model.CPDefinitionOptionRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinitionOptionRel
			fetchCPDefinitionOptionRelByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"fetchCPDefinitionOptionRelByExternalReferenceCode",
				_fetchCPDefinitionOptionRelByExternalReferenceCodeParameterTypes6);

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

			return (com.liferay.commerce.product.model.CPDefinitionOptionRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinitionOptionRel
			getCPDefinitionOptionRel(
				HttpPrincipal httpPrincipal, long cpDefinitionOptionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"getCPDefinitionOptionRel",
				_getCPDefinitionOptionRelParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionOptionRelId);

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

			return (com.liferay.commerce.product.model.CPDefinitionOptionRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinitionOptionRel
			getCPDefinitionOptionRelByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"getCPDefinitionOptionRelByExternalReferenceCode",
				_getCPDefinitionOptionRelByExternalReferenceCodeParameterTypes8);

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

			return (com.liferay.commerce.product.model.CPDefinitionOptionRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.Map<Long, java.util.List<Long>>
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				HttpPrincipal httpPrincipal, long cpDefinitionId, String json)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"getCPDefinitionOptionRelCPDefinitionOptionValueRelIds",
				_getCPDefinitionOptionRelCPDefinitionOptionValueRelIdsParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, json);

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

			return (java.util.Map<Long, java.util.List<Long>>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.Map<String, java.util.List<String>>
			getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
				HttpPrincipal httpPrincipal, long cpInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys",
				_getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeysParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpInstanceId);

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

			return (java.util.Map<String, java.util.List<String>>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CPDefinitionOptionRel>
				getCPDefinitionOptionRels(
					HttpPrincipal httpPrincipal, long cpDefinitionId,
					boolean skuContributor)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"getCPDefinitionOptionRels",
				_getCPDefinitionOptionRelsParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, skuContributor);

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
				<com.liferay.commerce.product.model.CPDefinitionOptionRel>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CPDefinitionOptionRel>
				getCPDefinitionOptionRels(
					HttpPrincipal httpPrincipal, long cpDefinitionId, int start,
					int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"getCPDefinitionOptionRels",
				_getCPDefinitionOptionRelsParameterTypes12);

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
				<com.liferay.commerce.product.model.CPDefinitionOptionRel>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.product.model.CPDefinitionOptionRel>
				getCPDefinitionOptionRels(
					HttpPrincipal httpPrincipal, long cpDefinitionId, int start,
					int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.product.model.
							CPDefinitionOptionRel> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"getCPDefinitionOptionRels",
				_getCPDefinitionOptionRelsParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, start, end, orderByComparator);

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
				<com.liferay.commerce.product.model.CPDefinitionOptionRel>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCPDefinitionOptionRelsCount(
			HttpPrincipal httpPrincipal, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"getCPDefinitionOptionRelsCount",
				_getCPDefinitionOptionRelsCountParameterTypes14);

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

	public static int getCPDefinitionOptionRelsCount(
			HttpPrincipal httpPrincipal, long cpDefinitionId,
			boolean skuContributor)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"getCPDefinitionOptionRelsCount",
				_getCPDefinitionOptionRelsCountParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionId, skuContributor);

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

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.product.model.CPDefinitionOptionRel>
				searchCPDefinitionOptionRels(
					HttpPrincipal httpPrincipal, long companyId, long groupId,
					long cpDefinitionId, String keywords, int start, int end,
					com.liferay.portal.kernel.search.Sort[] sorts)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"searchCPDefinitionOptionRels",
				_searchCPDefinitionOptionRelsParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, groupId, cpDefinitionId, keywords, start,
				end, sorts);

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
				<com.liferay.commerce.product.model.CPDefinitionOptionRel>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int searchCPDefinitionOptionRelsCount(
			HttpPrincipal httpPrincipal, long companyId, long groupId,
			long cpDefinitionId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"searchCPDefinitionOptionRelsCount",
				_searchCPDefinitionOptionRelsCountParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, groupId, cpDefinitionId, keywords);

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

	public static com.liferay.commerce.product.model.CPDefinitionOptionRel
			updateCPDefinitionOptionRel(
				HttpPrincipal httpPrincipal, long cpDefinitionOptionRelId,
				long cpOptionId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				String commerceOptionTypeKey, double priority,
				boolean facetable, boolean required, boolean skuContributor,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"updateCPDefinitionOptionRel",
				_updateCPDefinitionOptionRelParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionOptionRelId, cpOptionId, nameMap,
				descriptionMap, commerceOptionTypeKey, priority, facetable,
				required, skuContributor, serviceContext);

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

			return (com.liferay.commerce.product.model.CPDefinitionOptionRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinitionOptionRel
			updateCPDefinitionOptionRel(
				HttpPrincipal httpPrincipal, long cpDefinitionOptionRelId,
				long cpOptionId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				String commerceOptionTypeKey, String infoItemServiceKey,
				double priority, boolean definedExternally, boolean facetable,
				boolean required, boolean skuContributor, String priceType,
				String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"updateCPDefinitionOptionRel",
				_updateCPDefinitionOptionRelParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionOptionRelId, cpOptionId, nameMap,
				descriptionMap, commerceOptionTypeKey, infoItemServiceKey,
				priority, definedExternally, facetable, required,
				skuContributor, priceType, typeSettings, serviceContext);

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

			return (com.liferay.commerce.product.model.CPDefinitionOptionRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPDefinitionOptionRel
			updateExternalReferenceCode(
				HttpPrincipal httpPrincipal, long cpDefinitionOptionRelId,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPDefinitionOptionRelServiceUtil.class,
				"updateExternalReferenceCode",
				_updateExternalReferenceCodeParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpDefinitionOptionRelId, externalReferenceCode);

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

			return (com.liferay.commerce.product.model.CPDefinitionOptionRel)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CPDefinitionOptionRelServiceHttp.class);

	private static final Class<?>[] _addCPDefinitionOptionRelParameterTypes0 =
		new Class[] {
			long.class, long.class, java.util.Map.class, java.util.Map.class,
			String.class, double.class, boolean.class, boolean.class,
			boolean.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addCPDefinitionOptionRelParameterTypes1 =
		new Class[] {
			long.class, long.class, java.util.Map.class, java.util.Map.class,
			String.class, String.class, double.class, boolean.class,
			boolean.class, boolean.class, boolean.class, boolean.class,
			String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addCPDefinitionOptionRelParameterTypes2 =
		new Class[] {
			long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_deleteCPDefinitionOptionRelParameterTypes3 = new Class[] {long.class};
	private static final Class<?>[] _fetchCPDefinitionOptionRelParameterTypes4 =
		new Class[] {long.class};
	private static final Class<?>[] _fetchCPDefinitionOptionRelParameterTypes5 =
		new Class[] {long.class, long.class};
	private static final Class<?>[]
		_fetchCPDefinitionOptionRelByExternalReferenceCodeParameterTypes6 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _getCPDefinitionOptionRelParameterTypes7 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getCPDefinitionOptionRelByExternalReferenceCodeParameterTypes8 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_getCPDefinitionOptionRelCPDefinitionOptionValueRelIdsParameterTypes9 =
			new Class[] {long.class, String.class};
	private static final Class<?>[]
		_getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeysParameterTypes10 =
			new Class[] {long.class};
	private static final Class<?>[] _getCPDefinitionOptionRelsParameterTypes11 =
		new Class[] {long.class, boolean.class};
	private static final Class<?>[] _getCPDefinitionOptionRelsParameterTypes12 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[] _getCPDefinitionOptionRelsParameterTypes13 =
		new Class[] {
			long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getCPDefinitionOptionRelsCountParameterTypes14 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_getCPDefinitionOptionRelsCountParameterTypes15 = new Class[] {
			long.class, boolean.class
		};
	private static final Class<?>[]
		_searchCPDefinitionOptionRelsParameterTypes16 = new Class[] {
			long.class, long.class, long.class, String.class, int.class,
			int.class, com.liferay.portal.kernel.search.Sort[].class
		};
	private static final Class<?>[]
		_searchCPDefinitionOptionRelsCountParameterTypes17 = new Class[] {
			long.class, long.class, long.class, String.class
		};
	private static final Class<?>[]
		_updateCPDefinitionOptionRelParameterTypes18 = new Class[] {
			long.class, long.class, java.util.Map.class, java.util.Map.class,
			String.class, double.class, boolean.class, boolean.class,
			boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateCPDefinitionOptionRelParameterTypes19 = new Class[] {
			long.class, long.class, java.util.Map.class, java.util.Map.class,
			String.class, String.class, double.class, boolean.class,
			boolean.class, boolean.class, boolean.class, String.class,
			String.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateExternalReferenceCodeParameterTypes20 = new Class[] {
			long.class, String.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:1892206232