/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.http;

import com.liferay.asset.list.service.AssetListEntryAudienceEntryRelServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>AssetListEntryAudienceEntryRelServiceUtil</code> service
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
public class AssetListEntryAudienceEntryRelServiceHttp {

	public static void deleteAssetListEntryAudienceEntryRel(
			HttpPrincipal httpPrincipal, long assetListEntryId,
			String audienceEntryERC)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryAudienceEntryRelServiceUtil.class,
				"deleteAssetListEntryAudienceEntryRel",
				_deleteAssetListEntryAudienceEntryRelParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryId, audienceEntryERC);

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

	public static void updatePriorities(
			HttpPrincipal httpPrincipal,
			long[] assetListEntryAudienceEntryRelIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				AssetListEntryAudienceEntryRelServiceUtil.class,
				"updatePriorities", _updatePrioritiesParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, assetListEntryAudienceEntryRelIds);

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
		AssetListEntryAudienceEntryRelServiceHttp.class);

	private static final Class<?>[]
		_deleteAssetListEntryAudienceEntryRelParameterTypes0 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _updatePrioritiesParameterTypes1 =
		new Class[] {long[].class};

}
// LIFERAY-SERVICE-BUILDER-HASH:-262052591