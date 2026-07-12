/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.segments.service.SegmentsExperienceAudienceEntryRelServiceUtil;

/**
 * Provides the HTTP utility for the
 * <code>SegmentsExperienceAudienceEntryRelServiceUtil</code> service
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
 * @author Eduardo Garcia
 * @generated
 */
public class SegmentsExperienceAudienceEntryRelServiceHttp {

	public static java.util.List
		<com.liferay.segments.model.SegmentsExperienceAudienceEntryRel>
				updateSegmentsExperienceAudienceEntryRels(
					HttpPrincipal httpPrincipal, long groupId,
					String[] audienceEntryERCs, String segmentsExperienceERC)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsExperienceAudienceEntryRelServiceUtil.class,
				"updateSegmentsExperienceAudienceEntryRels",
				_updateSegmentsExperienceAudienceEntryRelsParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, audienceEntryERCs, segmentsExperienceERC);

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
				<com.liferay.segments.model.SegmentsExperienceAudienceEntryRel>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		SegmentsExperienceAudienceEntryRelServiceHttp.class);

	private static final Class<?>[]
		_updateSegmentsExperienceAudienceEntryRelsParameterTypes0 =
			new Class[] {long.class, String[].class, String.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:-142582206