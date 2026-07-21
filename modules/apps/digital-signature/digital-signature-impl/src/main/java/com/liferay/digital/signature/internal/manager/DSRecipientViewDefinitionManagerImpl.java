/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.manager;

import com.liferay.digital.signature.internal.http.DSHttp;
import com.liferay.digital.signature.manager.DSRecipientViewDefinitionManager;
import com.liferay.digital.signature.model.DSRecipientViewDefinition;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.StringUtil;

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

		_checkPermission(companyId, dsRecipientViewDefinition);

		JSONObject jsonObject = _dsHttp.post(
			companyId, groupId,
			StringBundler.concat(
				"envelopes/", dsEnvelopeId, "/views/recipient"),
			dsRecipientViewDefinition.toJSONObject());

		return jsonObject.getString("url");
	}

	private void _checkPermission(
			long companyId, DSRecipientViewDefinition dsRecipientViewDefinition)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			throw new PrincipalException.MustBeCompanyAdmin(permissionChecker);
		}

		if (permissionChecker.isCompanyAdmin(companyId)) {
			return;
		}

		User user = permissionChecker.getUser();

		if ((user != null) &&
			StringUtil.equalsIgnoreCase(
				user.getEmailAddress(),
				dsRecipientViewDefinition.getEmailAddress())) {

			return;
		}

		throw new PrincipalException.MustBeCompanyAdmin(permissionChecker);
	}

	@Reference
	private DSHttp _dsHttp;

}