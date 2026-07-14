/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.http;

import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>LayoutPageTemplateStructureRelElementVariationServiceUtil</code> service
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
public class LayoutPageTemplateStructureRelElementVariationServiceHttp {

	public static com.liferay.layout.page.template.model.
		LayoutPageTemplateStructureRelElementVariation
				addOrUpdateLayoutPageTemplateStructureRelElementVariation(
					HttpPrincipal httpPrincipal, String externalReferenceCode,
					long groupId, boolean active, String hide,
					java.util.Map<java.util.Locale, String> htmlMap,
					java.util.Map<java.util.Locale, String> jsMap, String name,
					long plid, String segmentsExperienceERC,
					String targetElement, String[] audienceEntryERCs,
					com.liferay.portal.kernel.service.ServiceContext
						serviceContext)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateStructureRelElementVariationServiceUtil.class,
				"addOrUpdateLayoutPageTemplateStructureRelElementVariation",
				_addOrUpdateLayoutPageTemplateStructureRelElementVariationParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, active, hide,
				htmlMap, jsMap, name, plid, segmentsExperienceERC,
				targetElement, audienceEntryERCs, serviceContext);

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

			return (com.liferay.layout.page.template.model.
				LayoutPageTemplateStructureRelElementVariation)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteLayoutPageTemplateStructureRelElementVariation(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long groupId, long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateStructureRelElementVariationServiceUtil.class,
				"deleteLayoutPageTemplateStructureRelElementVariation",
				_deleteLayoutPageTemplateStructureRelElementVariationParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, plid);

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

	public static java.util.List
		<com.liferay.layout.page.template.model.
			LayoutPageTemplateStructureRelElementVariation>
					getLayoutPageTemplateStructureRelElementVariations(
						HttpPrincipal httpPrincipal, long plid)
				throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutPageTemplateStructureRelElementVariationServiceUtil.class,
				"getLayoutPageTemplateStructureRelElementVariations",
				_getLayoutPageTemplateStructureRelElementVariationsParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(methodKey, plid);

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
				<com.liferay.layout.page.template.model.
					LayoutPageTemplateStructureRelElementVariation>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateStructureRelElementVariationServiceHttp.class);

	private static final Class<?>[]
		_addOrUpdateLayoutPageTemplateStructureRelElementVariationParameterTypes0 =
			new Class[] {
				String.class, long.class, boolean.class, String.class,
				java.util.Map.class, java.util.Map.class, String.class,
				long.class, String.class, String.class, String[].class,
				com.liferay.portal.kernel.service.ServiceContext.class
			};
	private static final Class<?>[]
		_deleteLayoutPageTemplateStructureRelElementVariationParameterTypes1 =
			new Class[] {String.class, long.class, long.class};
	private static final Class<?>[]
		_getLayoutPageTemplateStructureRelElementVariationsParameterTypes2 =
			new Class[] {long.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:1122815085