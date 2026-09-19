/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.http;

import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CommercePricingClassCPDefinitionRelServiceUtil</code> service
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
 * @author Riccardo Alberti
 * @generated
 */
public class CommercePricingClassCPDefinitionRelServiceHttp {

	public static
		com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel
				addCommercePricingClassCPDefinitionRel(
					HttpPrincipal httpPrincipal, long commercePricingClassId,
					long cpDefinitionId,
					com.liferay.portal.kernel.service.ServiceContext
						serviceContext)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePricingClassCPDefinitionRelServiceUtil.class,
				"addCommercePricingClassCPDefinitionRel",
				_addCommercePricingClassCPDefinitionRelParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassId, cpDefinitionId,
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

			return (com.liferay.commerce.pricing.model.
				CommercePricingClassCPDefinitionRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel
				deleteCommercePricingClassCPDefinitionRel(
					HttpPrincipal httpPrincipal,
					com.liferay.commerce.pricing.model.
						CommercePricingClassCPDefinitionRel
							commercePricingClassCPDefinitionRel)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePricingClassCPDefinitionRelServiceUtil.class,
				"deleteCommercePricingClassCPDefinitionRel",
				_deleteCommercePricingClassCPDefinitionRelParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassCPDefinitionRel);

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

			return (com.liferay.commerce.pricing.model.
				CommercePricingClassCPDefinitionRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel
				deleteCommercePricingClassCPDefinitionRel(
					HttpPrincipal httpPrincipal,
					long commercePricingClassCPDefinitionRelId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePricingClassCPDefinitionRelServiceUtil.class,
				"deleteCommercePricingClassCPDefinitionRel",
				_deleteCommercePricingClassCPDefinitionRelParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassCPDefinitionRelId);

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

			return (com.liferay.commerce.pricing.model.
				CommercePricingClassCPDefinitionRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel
				fetchCommercePricingClassCPDefinitionRel(
					HttpPrincipal httpPrincipal, long commercePricingClassId,
					long cpDefinitionId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePricingClassCPDefinitionRelServiceUtil.class,
				"fetchCommercePricingClassCPDefinitionRel",
				_fetchCommercePricingClassCPDefinitionRelParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassId, cpDefinitionId);

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

			return (com.liferay.commerce.pricing.model.
				CommercePricingClassCPDefinitionRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static long[] getCPDefinitionIds(
			HttpPrincipal httpPrincipal, long commercePricingClassId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePricingClassCPDefinitionRelServiceUtil.class,
				"getCPDefinitionIds", _getCPDefinitionIdsParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassId);

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

			return (long[])returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel
				getCommercePricingClassCPDefinitionRel(
					HttpPrincipal httpPrincipal,
					long commercePricingClassCPDefinitionRelId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePricingClassCPDefinitionRelServiceUtil.class,
				"getCommercePricingClassCPDefinitionRel",
				_getCommercePricingClassCPDefinitionRelParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassCPDefinitionRelId);

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

			return (com.liferay.commerce.pricing.model.
				CommercePricingClassCPDefinitionRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel>
				getCommercePricingClassCPDefinitionRelByClassId(
					HttpPrincipal httpPrincipal, long commercePricingClassId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePricingClassCPDefinitionRelServiceUtil.class,
				"getCommercePricingClassCPDefinitionRelByClassId",
				_getCommercePricingClassCPDefinitionRelByClassIdParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassId);

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
				<com.liferay.commerce.pricing.model.
					CommercePricingClassCPDefinitionRel>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel>
				getCommercePricingClassCPDefinitionRels(
					HttpPrincipal httpPrincipal, long commercePricingClassId,
					int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.pricing.model.
							CommercePricingClassCPDefinitionRel>
								orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePricingClassCPDefinitionRelServiceUtil.class,
				"getCommercePricingClassCPDefinitionRels",
				_getCommercePricingClassCPDefinitionRelsParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassId, start, end,
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
				<com.liferay.commerce.pricing.model.
					CommercePricingClassCPDefinitionRel>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCommercePricingClassCPDefinitionRelsCount(
			HttpPrincipal httpPrincipal, long commercePricingClassId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePricingClassCPDefinitionRelServiceUtil.class,
				"getCommercePricingClassCPDefinitionRelsCount",
				_getCommercePricingClassCPDefinitionRelsCountParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassId);

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

	public static int getCommercePricingClassCPDefinitionRelsCount(
		HttpPrincipal httpPrincipal, long commercePricingClassId, String name,
		String languageId) {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePricingClassCPDefinitionRelServiceUtil.class,
				"getCommercePricingClassCPDefinitionRelsCount",
				_getCommercePricingClassCPDefinitionRelsCountParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassId, name, languageId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
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
		<com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel>
				searchByCommercePricingClassId(
					HttpPrincipal httpPrincipal, long commercePricingClassId,
					String name, String languageId, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePricingClassCPDefinitionRelServiceUtil.class,
				"searchByCommercePricingClassId",
				_searchByCommercePricingClassIdParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassId, name, languageId, start,
				end);

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
				<com.liferay.commerce.pricing.model.
					CommercePricingClassCPDefinitionRel>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CommercePricingClassCPDefinitionRelServiceHttp.class);

	private static final Class<?>[]
		_addCommercePricingClassCPDefinitionRelParameterTypes0 = new Class[] {
			long.class, long.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_deleteCommercePricingClassCPDefinitionRelParameterTypes1 =
			new Class[] {
				com.liferay.commerce.pricing.model.
					CommercePricingClassCPDefinitionRel.class
			};
	private static final Class<?>[]
		_deleteCommercePricingClassCPDefinitionRelParameterTypes2 =
			new Class[] {long.class};
	private static final Class<?>[]
		_fetchCommercePricingClassCPDefinitionRelParameterTypes3 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[] _getCPDefinitionIdsParameterTypes4 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getCommercePricingClassCPDefinitionRelParameterTypes5 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_getCommercePricingClassCPDefinitionRelByClassIdParameterTypes6 =
			new Class[] {long.class};
	private static final Class<?>[]
		_getCommercePricingClassCPDefinitionRelsParameterTypes7 = new Class[] {
			long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getCommercePricingClassCPDefinitionRelsCountParameterTypes8 =
			new Class[] {long.class};
	private static final Class<?>[]
		_getCommercePricingClassCPDefinitionRelsCountParameterTypes9 =
			new Class[] {long.class, String.class, String.class};
	private static final Class<?>[]
		_searchByCommercePricingClassIdParameterTypes10 = new Class[] {
			long.class, String.class, String.class, int.class, int.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:-2113053970