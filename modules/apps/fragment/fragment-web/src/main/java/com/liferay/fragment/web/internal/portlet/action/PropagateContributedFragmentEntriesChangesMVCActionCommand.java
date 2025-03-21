/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.web.internal.configuration.helper.FragmentServiceConfigurationHelper;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
		"mvc.command.name=/instance_settings/propagate_contributed_fragment_entries_changes"
	},
	service = MVCActionCommand.class
)
public class PropagateContributedFragmentEntriesChangesMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Callable<Object> callable =
			new PropagateContributedFragmentEntriesChangesCallable(
				actionRequest);

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			TransactionInvokerUtil.invoke(_transactionConfig, callable);

			jsonObject.put("success", true);
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			jsonObject.put("success", false);
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);

		hideDefaultSuccessMessage(actionRequest);
	}

	private void _propagateContributedFragmentEntriesChanges()
		throws Exception {

		Map<String, FragmentEntry> fragmentCollectionContributorEntries =
			_fragmentCollectionContributorRegistry.getFragmentEntries();

		for (Map.Entry<String, FragmentEntry> entry :
				fragmentCollectionContributorEntries.entrySet()) {

			FragmentEntry fragmentEntry = entry.getValue();

			ActionableDynamicQuery actionableDynamicQuery =
				_fragmentEntryLinkLocalService.getActionableDynamicQuery();

			actionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> {
					Property rendererKeyProperty = PropertyFactoryUtil.forName(
						"rendererKey");

					dynamicQuery.add(
						rendererKeyProperty.eq(
							fragmentEntry.getFragmentEntryKey()));
				});
			actionableDynamicQuery.setPerformActionMethod(
				(FragmentEntryLink fragmentEntryLink) ->
					_fragmentEntryLinkLocalService.updateLatestChanges(
						fragmentEntry, fragmentEntryLink));

			actionableDynamicQuery.performActions();
		}
	}

	private void _updateFragmentServiceConfiguration(
			ActionRequest actionRequest)
		throws Exception {

		String scope = ParamUtil.getString(actionRequest, "scope");

		if (Validator.isNull(scope)) {
			throw new PortalException("Unsupported scope: " + scope);
		}

		long scopePK = ParamUtil.getLong(actionRequest, "scopePK");

		if ((scopePK == 0) &&
			!scope.equals(
				ExtendedObjectClassDefinition.Scope.SYSTEM.getValue())) {

			throw new PortalException(
				"Invalid scope primary key 0 for scope " + scope);
		}

		boolean propagateChanges = ParamUtil.getBoolean(
			actionRequest, "propagateChanges");
		boolean propagateContributedFragmentChanges = ParamUtil.getBoolean(
			actionRequest, "propagateContributedFragmentChanges");

		_fragmentServiceConfigurationHelper.updatePropagateChanges(
			propagateChanges, propagateContributedFragmentChanges, scope,
			scopePK);
	}

	private void _updatePropagateContributedFragmentChangesPortletPreference(
			ActionRequest actionRequest)
		throws Exception {

		PortletPreferences portletPreferences =
			_portalPreferencesLocalService.getPreferences(
				_portal.getCompanyId(actionRequest),
				PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		portletPreferences.setValue(
			"alreadyPropagateContributedFragmentChanges",
			Boolean.TRUE.toString());

		portletPreferences.store();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PropagateContributedFragmentEntriesChangesMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentServiceConfigurationHelper
		_fragmentServiceConfigurationHelper;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	private class PropagateContributedFragmentEntriesChangesCallable
		implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			_propagateContributedFragmentEntriesChanges();
			_updateFragmentServiceConfiguration(_actionRequest);
			_updatePropagateContributedFragmentChangesPortletPreference(
				_actionRequest);

			return null;
		}

		private PropagateContributedFragmentEntriesChangesCallable(
			ActionRequest actionRequest) {

			_actionRequest = actionRequest;
		}

		private final ActionRequest _actionRequest;

	}

}