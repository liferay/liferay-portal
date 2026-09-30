/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.servlet;

import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.taglib.internal.util.SegmentsExperienceUtil;
import com.liferay.layout.util.structure.CommonStylesUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/layout-common-styles",
		"osgi.http.whiteboard.servlet.name=com.liferay.layout.taglib.internal.servlet.LayoutStructureCommonStylesCSSServlet",
		"osgi.http.whiteboard.servlet.pattern=/layout-common-styles/*"
	},
	service = Servlet.class
)
public class LayoutStructureCommonStylesCSSServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			User user = _portal.getUser(httpServletRequest);

			if (user == null) {
				HttpSession httpSession = httpServletRequest.getSession();

				if (PortalSessionThreadLocal.getHttpSession() == null) {
					PortalSessionThreadLocal.setHttpSession(httpSession);
				}

				String userIdString = (String)httpSession.getAttribute(
					"j_username");
				String password = (String)httpSession.getAttribute(
					"j_password");

				if ((userIdString != null) && (password != null)) {
					long userId = GetterUtil.getLong(userIdString);

					user = _userLocalService.getUser(userId);
				}
			}

			if (user != null) {
				PrincipalThreadLocal.setName(user.getUserId());
				PrincipalThreadLocal.setPassword(
					_portal.getUserPassword(httpServletRequest));

				PermissionThreadLocal.setPermissionChecker(
					_permissionCheckerFactory.create(user));
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		long previewCTCollectionId = ParamUtil.getLong(
			httpServletRequest, "previewCTCollectionId",
			CTCollectionThreadLocal.getCTCollectionId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					previewCTCollectionId)) {

			_generateCSS(httpServletRequest, httpServletResponse);
		}
	}

	private void _generateCSS(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletResponse.setContentType(ContentTypes.TEXT_CSS_UTF8);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		Layout layout = null;

		long plid = ParamUtil.getLong(httpServletRequest, "plid");

		if (plid > 0) {
			layout = _layoutLocalService.fetchLayout(plid);
		}

		if ((layout == null) ||
			(!layout.isTypeAssetDisplay() && !layout.isTypeContent() &&
			 !layout.isTypeUtility() &&
			 ((layout.getMasterLayoutPlid() == 0) ||
			  !layout.isTypePortlet()))) {

			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				layout.getPlid(),
				SegmentsExperienceUtil.getSegmentsExperienceId(
					httpServletRequest));

		if (layoutStructure == null) {
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(
			CommonStylesUtil.getCSS(
				layout, layoutStructure,
				ParamUtil.getBoolean(
					httpServletRequest, "styleBookEntryPreview")));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutStructureCommonStylesCSSServlet.class);

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutStructureProvider _layoutStructureProvider;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}