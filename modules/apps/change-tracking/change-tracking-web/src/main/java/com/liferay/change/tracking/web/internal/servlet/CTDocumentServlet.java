/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.servlet;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.sql.CTSQLModeThreadLocal;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Preston Crary
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.change.tracking.web.internal.servlet.CTForwardingServlet",
		"osgi.http.whiteboard.servlet.pattern=/change_tracking/documents/*",
		"servlet.init.httpMethods=GET,POST"
	},
	service = Servlet.class
)
public class CTDocumentServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		_process(httpServletRequest, httpServletResponse);
	}

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		_process(httpServletRequest, httpServletResponse);
	}

	private <T extends CTModel<T>> InputStream _getInputStream(
			CTCollection ctCollection, CTEntry ctEntry, String type, String key)
		throws PortalException {

		CTDisplayRenderer<T> ctDisplayRenderer =
			_ctDisplayRendererRegistry.getCTDisplayRenderer(
				ctEntry.getModelClassNameId());

		long ctCollectionId = ctCollection.getCtCollectionId();

		if (CTConstants.TYPE_AFTER.equals(type) &&
			(ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED)) {

			ctCollectionId = _ctEntryLocalService.getCTRowCTCollectionId(
				ctEntry);
		}
		else if (CTConstants.TYPE_BEFORE.equals(type) &&
				 (ctCollection.getStatus() !=
					 WorkflowConstants.STATUS_APPROVED)) {

			ctCollectionId = CTConstants.CT_COLLECTION_ID_PRODUCTION;
		}
		else if (CTConstants.TYPE_LATEST.equals(type)) {
			ctCollectionId = _ctDisplayRendererRegistry.getCtCollectionId(
				ctCollection, ctEntry);
		}

		CTSQLModeThreadLocal.CTSQLMode ctSQLMode =
			_ctDisplayRendererRegistry.getCTSQLMode(ctCollectionId, ctEntry);

		T ctModel = _ctDisplayRendererRegistry.fetchCTModel(
			ctCollectionId, ctSQLMode, ctEntry.getModelClassNameId(),
			ctEntry.getModelClassPK());

		if (ctModel == null) {
			throw new NoSuchModelException(
				"ctEntryId = " + ctEntry.getCtEntryId());
		}

		if (CTConstants.TYPE_LATEST.equals(type)) {
			if (ctEntry.getChangeType() ==
					CTConstants.CT_CHANGE_TYPE_DELETION) {

				ctCollectionId = ctCollection.getCtCollectionId();

				if (ctCollection.getStatus() ==
						WorkflowConstants.STATUS_APPROVED) {

					ctCollectionId =
						_ctEntryLocalService.getCTRowCTCollectionId(ctEntry);
				}

				ctSQLMode = CTSQLModeThreadLocal.CTSQLMode.DEFAULT;
			}
			else {
				ctCollectionId = CTConstants.CT_COLLECTION_ID_PRODUCTION;

				if (ctCollection.getStatus() ==
						WorkflowConstants.STATUS_APPROVED) {

					ctCollectionId = ctEntry.getCtCollectionId();
				}

				ctSQLMode = _ctDisplayRendererRegistry.getCTSQLMode(
					ctCollectionId, ctEntry);
			}

			try (SafeCloseable safeCloseable1 =
					CTSQLModeThreadLocal.setCTSQLModeWithSafeCloseable(
						ctSQLMode);
				SafeCloseable safeCloseable2 =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollectionId)) {

				ctModel = ctDisplayRenderer.fetchLatestVersionedModel(ctModel);

				return ctDisplayRenderer.getDownloadInputStream(ctModel, key);
			}
		}

		try (SafeCloseable safeCloseable1 =
				CTSQLModeThreadLocal.setCTSQLModeWithSafeCloseable(ctSQLMode);
			SafeCloseable safeCloseable2 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			return ctDisplayRenderer.getDownloadInputStream(ctModel, key);
		}
	}

	private void _process(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			HttpSession httpSession = httpServletRequest.getSession();

			if (PortalSessionThreadLocal.getHttpSession() == null) {
				PortalSessionThreadLocal.setHttpSession(httpSession);
			}

			User user = _portal.getUser(httpServletRequest);

			if (user == null) {
				String userIdString = (String)httpSession.getAttribute(
					"j_username");
				String password = (String)httpSession.getAttribute(
					"j_password");

				if ((userIdString != null) && (password != null)) {
					long userId = GetterUtil.getLong(userIdString);

					user = _userLocalService.getUser(userId);
				}
			}

			if (user == null) {
				httpServletResponse.sendRedirect(
					StringBundler.concat(
						_portal.getPathMain(), "/portal/login?redirect=",
						URLCodec.encodeURL(
							_portal.getCurrentURL(httpServletRequest))));

				return;
			}

			PrincipalThreadLocal.setName(user.getUserId());
			PrincipalThreadLocal.setPassword(
				_portal.getUserPassword(httpServletRequest));

			PermissionChecker permissionChecker =
				_permissionCheckerFactory.create(user);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);

			List<String> pathInfos = StringUtil.split(
				HttpComponentsUtil.fixPath(
					httpServletRequest.getPathInfo(), true, true),
				CharPool.SLASH);

			long ctEntryId = GetterUtil.getLong(pathInfos.get(0));

			CTEntry ctEntry = _ctEntryLocalService.getCTEntry(ctEntryId);

			CTCollection ctCollection =
				_ctCollectionLocalService.getCTCollection(
					ctEntry.getCtCollectionId());

			_modelResourcePermission.check(
				permissionChecker, ctCollection, ActionKeys.VIEW);

			String key = HttpComponentsUtil.decodeURL(pathInfos.get(2));

			String fileTitle = ParamUtil.getString(
				httpServletRequest, "title", key);

			String type = HttpComponentsUtil.decodeURL(pathInfos.get(1));
			long fileSize = ParamUtil.getLong(httpServletRequest, "size");

			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse, fileTitle,
				_getInputStream(ctCollection, ctEntry, type, key), fileSize,
				StringPool.BLANK, HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
		}
		catch (NoSuchModelException noSuchModelException) {
			_portal.sendError(
				HttpServletResponse.SC_NOT_FOUND, noSuchModelException,
				httpServletRequest, httpServletResponse);
		}
		catch (PrincipalException principalException) {
			_portal.sendError(
				HttpServletResponse.SC_UNAUTHORIZED, principalException,
				httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_portal.sendError(
				exception, httpServletRequest, httpServletResponse);
		}
	}

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTDisplayRendererRegistry _ctDisplayRendererRegistry;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.change.tracking.model.CTCollection)"
	)
	private ModelResourcePermission<CTCollection> _modelResourcePermission;

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