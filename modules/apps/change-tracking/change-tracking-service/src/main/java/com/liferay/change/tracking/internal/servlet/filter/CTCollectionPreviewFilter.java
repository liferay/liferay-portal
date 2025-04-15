/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.servlet.filter;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.exception.NoSuchCollectionException;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.portal.kernel.change.tracking.CTCollectionPreviewThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	property = {
		"dispatcher=FORWARD", "dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=CTCollection Preview Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class CTCollectionPreviewFilter extends BasePortalFilter {

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		long previewCTCollectionId = ParamUtil.getLong(
			httpServletRequest, "previewCTCollectionId", -1);

		if (previewCTCollectionId == -1) {
			processFilter(
				CTCollectionPreviewFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);

			return;
		}

		String mode = ParamUtil.getString(httpServletRequest, "p_l_mode");

		if (!mode.equals("preview")) {
			httpServletResponse.sendRedirect(
				HttpComponentsUtil.addParameter(
					_portal.getCurrentURL(httpServletRequest), "p_l_mode",
					Constants.PREVIEW));

			return;
		}

		if (previewCTCollectionId != CTConstants.CT_COLLECTION_ID_PRODUCTION) {
			CTCollection ctCollection =
				_ctCollectionLocalService.fetchCTCollection(
					previewCTCollectionId);

			if (ctCollection == null) {
				_portal.sendError(
					new NoSuchCollectionException(), httpServletRequest,
					httpServletResponse);

				return;
			}

			if ((ctCollection.getStatus() !=
					WorkflowConstants.STATUS_APPROVED) &&
				(ctCollection.getStatus() != WorkflowConstants.STATUS_DRAFT) &&
				(ctCollection.getStatus() !=
					WorkflowConstants.STATUS_EXPIRED) &&
				(ctCollection.getStatus() !=
					WorkflowConstants.STATUS_PENDING) &&
				(ctCollection.getStatus() !=
					WorkflowConstants.STATUS_SCHEDULED)) {

				_portal.sendError(
					new PortalException("Collection is not available"),
					httpServletRequest, httpServletResponse);

				return;
			}

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			if (permissionChecker == null) {
				permissionChecker = permissionCheckerFactory.create(
					_portal.getUser(httpServletRequest));
			}

			if (!_modelResourcePermission.contains(
					permissionChecker, ctCollection, ActionKeys.VIEW)) {

				_portal.sendError(
					new PrincipalException.MustHavePermission(
						permissionChecker, CTCollection.class.getName(),
						previewCTCollectionId, ActionKeys.VIEW),
					httpServletRequest, httpServletResponse);

				return;
			}
		}

		CTCollectionPreviewThreadLocal.setCTCollectionId(previewCTCollectionId);

		CTCollectionPreviewThreadLocal.setIndicatorEnabled(
			ParamUtil.getBoolean(httpServletRequest, "previewCTIndicator"));

		processFilter(
			CTCollectionPreviewFilter.class.getName(), httpServletRequest,
			httpServletResponse, filterChain);
	}

	@Reference
	protected PermissionCheckerFactory permissionCheckerFactory;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.change.tracking.model.CTCollection)"
	)
	private ModelResourcePermission<CTCollection> _modelResourcePermission;

	@Reference
	private Portal _portal;

}