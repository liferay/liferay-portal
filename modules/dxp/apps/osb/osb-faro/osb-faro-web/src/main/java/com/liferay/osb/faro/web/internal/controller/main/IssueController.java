/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;

import jakarta.annotation.security.RolesAllowed;

import jakarta.mail.internet.InternetAddress;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(service = {FaroController.class, IssueController.class})
@Path("/{groupId}/issue")
@Produces(MediaType.APPLICATION_JSON)
public class IssueController extends BaseFaroController {

	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public void create(
			@PathParam("groupId") long groupId,
			@FormParam("currentURL") String currentURL,
			@FormParam("description") String description,
			@FormParam("title") String title)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		if (!faroProject.isTrial()) {
			throw new FaroException("The workspace is not free tier");
		}

		Date submissionDate = new Date();
		User user = getUser();

		_mailService.sendEmail(
			new MailMessage(
				new InternetAddress(
					"actrial@liferay.com", "AC Trial Support Request"),
				new InternetAddress(FaroPropsValues.ISSUES_EMAIL_ADDRESS, null),
				faroProject.getName() + " - " + title,
				StringBundler.concat(
					"Account Name: ", faroProject.getAccountName(), "\n",
					"Current URL: ", currentURL, "\n", "Data Center Region: ",
					faroProject.getServerLocation(), "\n", "Issue Title: ",
					title, "\n", "Submission Date: ", submissionDate, "\n",
					"User Email: ", user.getEmailAddress(), "\n", "User Name: ",
					user.getFullName(), "\n", "Workspace Name: ",
					faroProject.getName(), "\n", "Description: ", description),
				false));
	}

	@Reference
	private MailService _mailService;

}