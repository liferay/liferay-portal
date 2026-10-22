/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.manager;

import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.digital.signature.constants.DigitalSignatureConstants;
import com.liferay.digital.signature.internal.http.DSHttp;
import com.liferay.digital.signature.manager.DSRecipientViewDefinitionManager;
import com.liferay.digital.signature.model.DSRecipientViewDefinition;
import com.liferay.digital.signature.model.DSRequestRecipient;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author José Abelenda
 */
@Component(service = DSRecipientViewDefinitionManager.class)
public class DSRecipientViewDefinitionManagerImpl
	implements DSRecipientViewDefinitionManager {

	@Override
	public String addDSRecipientViewDefinition(
			long companyId, long groupId, String dsEnvelopeId,
			DSRecipientViewDefinition dsRecipientViewDefinition)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((permissionChecker == null) ||
			!permissionChecker.isCompanyAdmin(companyId)) {

			throw new PrincipalException.MustBeCompanyAdmin(permissionChecker);
		}

		return _addDSRecipientViewDefinition(
			companyId, groupId, dsEnvelopeId, dsRecipientViewDefinition);
	}

	@Override
	public String addDSRecipientViewDefinition(
			long companyId, long groupId, String dsEnvelopeId,
			DSRequestRecipient dsRequestRecipient, String portalURL)
		throws Exception {

		long userId = dsRequestRecipient.getUserId();

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((permissionChecker == null) ||
			((permissionChecker.getUserId() != userId) &&
			 !permissionChecker.isCompanyAdmin(companyId))) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, DSRecipientViewDefinition.class.getName(),
				userId, ActionKeys.VIEW);
		}

		DSRecipientViewDefinition dsRecipientViewDefinition =
			new DSRecipientViewDefinition();

		dsRecipientViewDefinition.setAuthenticationMethod("none");
		dsRecipientViewDefinition.setDSClientUserId(String.valueOf(userId));
		dsRecipientViewDefinition.setEmailAddress(
			dsRequestRecipient.getEmailAddress());

		DigitalSignatureConfiguration digitalSignatureConfiguration =
			DigitalSignatureConfigurationUtil.getDigitalSignatureConfiguration(
				companyId, groupId);

		String appOriginURL = DigitalSignatureConstants.getAppOriginURL(
			digitalSignatureConfiguration.environment());

		dsRecipientViewDefinition.setFrameAncestors(
			new String[] {appOriginURL, portalURL});
		dsRecipientViewDefinition.setMessageOrigins(
			new String[] {appOriginURL});
		dsRecipientViewDefinition.setReturnURL(appOriginURL);

		dsRecipientViewDefinition.setUserName(dsRequestRecipient.getName());

		return _addDSRecipientViewDefinition(
			companyId, groupId, dsEnvelopeId, dsRecipientViewDefinition);
	}

	private String _addDSRecipientViewDefinition(
			long companyId, long groupId, String dsEnvelopeId,
			DSRecipientViewDefinition dsRecipientViewDefinition)
		throws Exception {

		JSONObject jsonObject = _dsHttp.post(
			companyId, groupId,
			StringBundler.concat(
				"envelopes/", dsEnvelopeId, "/views/recipient"),
			dsRecipientViewDefinition.toJSONObject());

		if (Validator.isNotNull(jsonObject.getString("errorCode"))) {
			throw new PortalException(
				StringBundler.concat(
					"Unable to create the recipient view for digital ",
					"signature envelope ", dsEnvelopeId, ": ",
					jsonObject.getString("message")));
		}

		return jsonObject.getString("url");
	}

	@Reference
	private DSHttp _dsHttp;

}