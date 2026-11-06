/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.helper;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.exception.NoSuchEntryException;
import com.liferay.osb.faro.engine.client.model.IndividualSegment;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.internal.jaxrs.util.ProblemUtil;
import com.liferay.osb.faro.rest.internal.security.permission.WorkspacePermission;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(service = IndividualSegmentHelper.class)
public class IndividualSegmentHelper {

	public IndividualSegment getIndividualSegment(
		FaroProject faroProject, String individualSegmentId) {

		IndividualSegment individualSegment = null;

		try {
			individualSegment = _contactsEngineClient.getIndividualSegment(
				faroProject, individualSegmentId, false);
		}
		catch (FaroEngineClientException faroEngineClientException) {
			if ((faroEngineClientException instanceof NoSuchEntryException) ||
				(faroEngineClientException.getStatus() ==
					HttpServletResponse.SC_BAD_REQUEST)) {

				throw ProblemUtil.getNotFoundException(
					_getNotFoundExceptionTitle(individualSegmentId));
			}

			throw faroEngineClientException;
		}

		String channelId = individualSegment.getChannelId();

		if (Validator.isNull(channelId)) {
			return individualSegment;
		}

		FaroChannel faroChannel = _faroChannelLocalService.fetchFaroChannel(
			channelId, faroProject.getGroupId());

		if (faroChannel == null) {
			throw ProblemUtil.getNotFoundException(
				_getNotFoundExceptionTitle(individualSegmentId));
		}

		if (!_workspacePermission.hasChannelPermission(
				faroChannel, PermissionThreadLocal.getPermissionChecker())) {

			throw ProblemUtil.getForbiddenException(
				StringBundler.concat(
					"Unable to access the individual segment with ID ",
					individualSegmentId, ". Call ",
					"\"getWorkspaceGroupChannelIndividualSegmentsPage\" to ",
					"list the individual segments you can use."));
		}

		return individualSegment;
	}

	private String _getNotFoundExceptionTitle(String individualSegmentId) {
		return "No individual segment was found with ID " + individualSegmentId;
	}

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroChannelLocalService _faroChannelLocalService;

	@Reference
	private WorkspacePermission _workspacePermission;

}