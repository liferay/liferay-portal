/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.exception.DataLimitExceededException;
import com.liferay.portal.kernel.exception.DuplicateOrganizationException;
import com.liferay.portal.kernel.exception.NoSuchCountryException;
import com.liferay.portal.kernel.exception.NoSuchListTypeException;
import com.liferay.portal.kernel.exception.NoSuchOrganizationException;
import com.liferay.portal.kernel.exception.OrganizationNameException;
import com.liferay.portal.kernel.exception.OrganizationParentException;
import com.liferay.portal.kernel.exception.RequiredOrganizationException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Julio Camarero
 * @author Jorge Ferrer
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ORGANIZATIONS,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/users_admin/edit_organization"
	},
	service = MVCActionCommand.class
)
public class EditOrganizationMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	protected void deleteOrganizations(ActionRequest actionRequest)
		throws Exception {

		long[] deleteOrganizationIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "deleteOrganizationIds"), 0L);

		for (long deleteOrganizationId : deleteOrganizationIds) {
			_organizationService.deleteOrganization(deleteOrganizationId);
		}
	}

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			Organization organization = null;

			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				organization = updateOrganization(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				deleteOrganizations(actionRequest);
			}

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (organization != null) {
				redirect = HttpComponentsUtil.setParameter(
					redirect, actionResponse.getNamespace() + "organizationId",
					organization.getOrganizationId());
			}

			actionRequest.setAttribute(WebKeys.REDIRECT, redirect);

			sendRedirect(actionRequest, actionResponse);
		}
		catch (Exception exception) {
			String mvcPath = "/edit_organization.jsp";

			if (exception instanceof DataLimitExceededException ||
				exception instanceof NoSuchOrganizationException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);

				mvcPath = "/error.jsp";
			}
			else if (exception instanceof AssetCategoryException ||
					 exception instanceof AssetTagException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);
			}
			else if (exception instanceof DuplicateOrganizationException ||
					 exception instanceof NoSuchCountryException ||
					 exception instanceof NoSuchListTypeException ||
					 exception instanceof OrganizationNameException ||
					 exception instanceof OrganizationParentException ||
					 exception instanceof RequiredOrganizationException) {

				if (exception instanceof NoSuchListTypeException) {
					NoSuchListTypeException noSuchListTypeException =
						(NoSuchListTypeException)exception;

					Class<?> clazz = exception.getClass();

					SessionErrors.add(
						actionRequest,
						clazz.getName() + noSuchListTypeException.getType());
				}
				else {
					SessionErrors.add(
						actionRequest, exception.getClass(), exception);
				}

				if (exception instanceof RequiredOrganizationException) {
					String redirect = _portal.escapeRedirect(
						ParamUtil.getString(actionRequest, "redirect"));

					long organizationId = ParamUtil.getLong(
						actionRequest, "organizationId");

					if (organizationId > 0) {
						redirect = HttpComponentsUtil.setParameter(
							redirect,
							actionResponse.getNamespace() + "organizationId",
							organizationId);
					}

					if (Validator.isNotNull(redirect)) {
						sendRedirect(actionRequest, actionResponse, redirect);

						return;
					}
				}
			}
			else {
				throw exception;
			}

			actionResponse.setRenderParameter("mvcPath", mvcPath);
		}
	}

	protected Organization updateOrganization(ActionRequest actionRequest)
		throws Exception {

		long organizationId = ParamUtil.getLong(
			actionRequest, "organizationId");

		long parentOrganizationId = ParamUtil.getLong(
			actionRequest, "parentOrganizationId",
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID);
		String name = ParamUtil.getString(actionRequest, "name");
		long statusId = ParamUtil.getLong(actionRequest, "statusId");
		String type = ParamUtil.getString(actionRequest, "type");
		long regionId = ParamUtil.getLong(actionRequest, "regionId");
		long countryId = ParamUtil.getLong(actionRequest, "countryId");
		String comments = ParamUtil.getString(actionRequest, "comments");

		byte[] logoBytes = null;

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		if (fileEntryId > 0) {
			FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			logoBytes = FileUtil.getBytes(fileEntry.getContentStream());
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Organization.class.getName(), actionRequest);

		Organization organization = null;

		if (organizationId <= 0) {

			// Add organization

			organization = _organizationService.addOrganization(
				null, parentOrganizationId, name, type, regionId, countryId,
				statusId, comments, false, Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(),
				serviceContext);

			if (logoBytes != null) {
				organization = _organizationLocalService.updateLogo(
					organization.getOrganizationId(), logoBytes);
			}
		}
		else {

			// Update organization

			boolean deleteLogo = ParamUtil.getBoolean(
				actionRequest, "deleteLogo");

			organization = _organizationService.getOrganization(organizationId);

			Group organizationGroup = organization.getGroup();

			organization = _organizationService.updateOrganization(
				organization.getExternalReferenceCode(), organizationId,
				parentOrganizationId, name, type, regionId, countryId, statusId,
				comments, !deleteLogo, logoBytes, organizationGroup.isSite(),
				null, null, null, null, null, serviceContext);
		}

		return organization;
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private OrganizationService _organizationService;

	@Reference
	private Portal _portal;

}