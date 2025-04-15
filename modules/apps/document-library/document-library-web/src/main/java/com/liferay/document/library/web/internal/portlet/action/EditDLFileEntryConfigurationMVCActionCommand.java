/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.document.library.configuration.DLFileEntryConfigurationProvider;
import com.liferay.document.library.constants.DLFileEntryConfigurationConstants;
import com.liferay.document.library.exception.DLFileEntryConfigurationException;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Galluzzi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
		"mvc.command.name=/instance_settings/edit_dl_file_entry_configuration"
	},
	service = MVCActionCommand.class
)
public class EditDLFileEntryConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ExtendedObjectClassDefinition.Scope scope = _getScope(actionRequest);

		try {
			_dlFileEntryConfigurationProvider.update(
				_getPreviewableProcessorMaxSize(actionRequest),
				_getMaxNumberOfPages(actionRequest), scope,
				_getScopePK(actionRequest, scope));
		}
		catch (ConfigurationModelListenerException |
			   DLFileEntryConfigurationException exception) {

			SessionErrors.add(actionRequest, exception.getClass(), exception);

			actionResponse.sendRedirect(
				ParamUtil.getString(actionRequest, "redirect"));
		}
	}

	private int _getMaxNumberOfPages(ActionRequest actionRequest)
		throws DLFileEntryConfigurationException.
			InvalidMaxNumberOfPagesException {

		int maxNumberOfPages = ParamUtil.getInteger(
			actionRequest, "maxNumberOfPages");

		if (maxNumberOfPages <
				DLFileEntryConfigurationConstants.
					MAX_NUMBER_OF_PAGES_UNLIMITED) {

			throw new DLFileEntryConfigurationException.
				InvalidMaxNumberOfPagesException(
					"Maximum number of pages limit must be greater than or " +
						"equal to " +
							DLFileEntryConfigurationConstants.
								MAX_NUMBER_OF_PAGES_UNLIMITED);
		}

		return maxNumberOfPages;
	}

	private long _getPreviewableProcessorMaxSize(ActionRequest actionRequest)
		throws DLFileEntryConfigurationException.
			InvalidPreviewableProcessorMaxSizeException {

		long previewableProcessorMaxSize = ParamUtil.getLong(
			actionRequest, "previewableProcessorMaxSize");

		if (previewableProcessorMaxSize < DLFileEntryConfigurationConstants.
				PREVIEWABLE_PROCESSOR_MAX_SIZE_UNLIMITED) {

			throw new DLFileEntryConfigurationException.
				InvalidPreviewableProcessorMaxSizeException(
					"Maximum file size must be greater than or equal to " +
						DLFileEntryConfigurationConstants.
							PREVIEWABLE_PROCESSOR_MAX_SIZE_UNLIMITED);
		}

		return previewableProcessorMaxSize;
	}

	private ExtendedObjectClassDefinition.Scope _getScope(
			ActionRequest actionRequest)
		throws Exception {

		String scopeString = ParamUtil.getString(actionRequest, "scope");

		if (Validator.isNull(scopeString)) {
			throw new PortalException("Unsupported scope: " + scopeString);
		}

		return ExtendedObjectClassDefinition.Scope.getScope(scopeString);
	}

	private long _getScopePK(
			ActionRequest actionRequest,
			ExtendedObjectClassDefinition.Scope scope)
		throws PortalException {

		long scopePK = ParamUtil.getLong(actionRequest, "scopePK");

		if ((scopePK == 0) &&
			(scope != ExtendedObjectClassDefinition.Scope.SYSTEM)) {

			throw new PortalException(
				"Invalid scope primary key 0 for scope " + scope);
		}

		return scopePK;
	}

	@Reference
	private DLFileEntryConfigurationProvider _dlFileEntryConfigurationProvider;

}