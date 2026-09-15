/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.LayoutRevisionServiceUtil;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>LayoutRevisionServiceUtil</code> service
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
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutRevisionServiceHttp {

	public static com.liferay.portal.kernel.model.LayoutRevision
			addLayoutRevision(
				HttpPrincipal httpPrincipal, long userId,
				long layoutSetBranchId, long layoutBranchId,
				long parentLayoutRevisionId, boolean head, long plid,
				long portletPreferencesPlid, boolean privateLayout, String name,
				String title, String description, String keywords,
				String robots, String typeSettings, boolean iconImage,
				long iconImageId, String themeId, String colorSchemeId,
				String css,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				LayoutRevisionServiceUtil.class, "addLayoutRevision",
				_addLayoutRevisionParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, userId, layoutSetBranchId, layoutBranchId,
				parentLayoutRevisionId, head, plid, portletPreferencesPlid,
				privateLayout, name, title, description, keywords, robots,
				typeSettings, iconImage, iconImageId, themeId, colorSchemeId,
				css, serviceContext);

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

			return (com.liferay.portal.kernel.model.LayoutRevision)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		LayoutRevisionServiceHttp.class);

	private static final Class<?>[] _addLayoutRevisionParameterTypes0 =
		new Class[] {
			long.class, long.class, long.class, long.class, boolean.class,
			long.class, long.class, boolean.class, String.class, String.class,
			String.class, String.class, String.class, String.class,
			boolean.class, long.class, String.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:83445233