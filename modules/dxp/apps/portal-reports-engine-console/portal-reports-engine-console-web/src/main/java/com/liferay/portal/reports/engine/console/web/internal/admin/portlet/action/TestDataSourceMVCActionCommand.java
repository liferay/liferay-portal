/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.portlet.action;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsolePortletKeys;
import com.liferay.portal.reports.engine.console.model.Source;
import com.liferay.portal.reports.engine.console.service.SourceService;
import com.liferay.portal.reports.engine.console.util.ReportsEngineConsoleUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ReportsEngineConsolePortletKeys.REPORTS_ADMIN,
		"mvc.command.name=/reports_admin/test_data_source"
	},
	service = MVCActionCommand.class
)
public class TestDataSourceMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long sourceId = ParamUtil.getLong(actionRequest, "sourceId");

		Source source = _sourceService.getSource(sourceId);

		ReportsEngineConsoleUtil.validateJDBCConnection(
			source.getDriverClassName(), source.getDriverUrl(),
			source.getDriverUserName(), _getDriverPassword(source));
	}

	private String _getDriverPassword(Source source) {
		String driverPassword = source.getDriverPassword();

		KeyReference keyReference = KeyReferenceUtil.parseKeyReference(
			driverPassword);

		if ((keyReference == null) ||
			!Objects.equals(
				keyReference.getIdentifier(),
				StringBundler.concat(
					Source.class.getSimpleName(), StringPool.SLASH,
					source.getCompanyId(), StringPool.SLASH,
					source.getSourceId()))) {

			return driverPassword;
		}

		return SecretResolverUtil.resolve(
			source.getCompanyId(), driverPassword);
	}

	@Reference
	private SourceService _sourceService;

}