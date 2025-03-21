/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.background.task.ReindexBackgroundTaskConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.search.admin.web.internal.constants.SearchAdminPortletKeys;
import com.liferay.portal.search.admin.web.internal.util.DictionaryReindexer;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletSession;

import java.io.Serializable;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SearchAdminPortletKeys.SEARCH_ADMIN,
		"mvc.command.name=/portal_search_admin/edit"
	},
	service = MVCActionCommand.class
)
public class EditMVCActionCommand extends BaseMVCActionCommand {

	@Override
	public void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		long[] companyIds = ParamUtil.getLongValues(
			actionRequest, "companyIds");

		if (!permissionChecker.isOmniadmin()) {
			for (long companyId : companyIds) {
				if (!permissionChecker.isCompanyAdmin(companyId)) {
					SessionErrors.add(
						actionRequest,
						PrincipalException.MustHavePermission.class.getName());

					actionResponse.setRenderParameter("mvcPath", "/error.jsp");

					return;
				}
			}
		}

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		String className = ParamUtil.getString(actionRequest, "className");
		String executionMode = ParamUtil.getString(
			actionRequest, "executionMode");

		if (cmd.equals("reindex")) {
			_reindex(
				ParamUtil.getBoolean(actionRequest, "blocking"), className,
				companyIds, executionMode, actionRequest.getPortletSession(),
				themeDisplay,
				ParamUtil.getLong(actionRequest, "timeout", Time.HOUR));

			if (Validator.isBlank(className)) {
				_reindexIndexReindexer(
					className, companyIds, executionMode, themeDisplay);
			}
		}
		else if (cmd.equals("reindexDictionaries")) {
			_reindexDictionaries(companyIds);
		}
		else if (cmd.equals("reindexIndexReindexer")) {
			_reindexIndexReindexer(
				className, companyIds, executionMode, themeDisplay);
		}

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		String namespace = actionResponse.getNamespace();

		redirect = HttpComponentsUtil.setParameter(
			redirect, namespace + "companyIds", StringUtil.merge(companyIds));
		redirect = HttpComponentsUtil.setParameter(
			redirect, namespace + "executionMode", executionMode);
		redirect = HttpComponentsUtil.setParameter(
			redirect, namespace + "scope",
			ParamUtil.getString(actionRequest, "scope"));

		sendRedirect(actionRequest, actionResponse, redirect);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private void _reindex(
			boolean blocking, String className, long[] companyIds,
			String executionMode, PortletSession portletSession,
			ThemeDisplay themeDisplay, long timeout)
		throws Exception {

		Map<String, Serializable> taskContextMap =
			new HashMapBuilder<>().<String, Serializable>put(
				ReindexBackgroundTaskConstants.EXECUTION_MODE, executionMode
			).build();

		if (!blocking) {
			_indexWriterHelper.reindex(
				themeDisplay.getUserId(), "reindex", companyIds, className,
				taskContextMap);

			return;
		}

		String jobName = "reindex-".concat(PortalUUIDUtil.generate());

		CountDownLatch countDownLatch = new CountDownLatch(1);

		MessageListener messageListener = message -> {
			int status = message.getInteger("status");

			if ((status != BackgroundTaskConstants.STATUS_CANCELLED) &&
				(status != BackgroundTaskConstants.STATUS_FAILED) &&
				(status != BackgroundTaskConstants.STATUS_SUCCESSFUL)) {

				return;
			}

			if (!jobName.equals(message.getString("name"))) {
				return;
			}

			int extendedMaxInactiveIntervalTime =
				(int)(System.currentTimeMillis() -
					portletSession.getLastAccessedTime() +
						portletSession.getMaxInactiveInterval());

			portletSession.setMaxInactiveInterval(
				extendedMaxInactiveIntervalTime);

			countDownLatch.countDown();
		};

		ServiceRegistration<MessageListener> serviceRegistration =
			_bundleContext.registerService(
				MessageListener.class, messageListener,
				MapUtil.singletonDictionary(
					"destination.name",
					DestinationNames.BACKGROUND_TASK_STATUS));

		try {
			_indexWriterHelper.reindex(
				themeDisplay.getUserId(), jobName, companyIds, className,
				taskContextMap);

			countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _reindexDictionaries(long[] companyIds) throws Exception {
		DictionaryReindexer dictionaryReindexer = new DictionaryReindexer(
			_indexWriterHelper);

		dictionaryReindexer.reindexDictionaries(companyIds);
	}

	private void _reindexIndexReindexer(
			String className, long[] companyIds, String executionMode,
			ThemeDisplay themeDisplay)
		throws Exception {

		_backgroundTaskManager.addBackgroundTask(
			themeDisplay.getUserId(), CompanyConstants.SYSTEM,
			"reindexIndexReindexer",
			_CLASS_NAME_REINDEX_INDEX_REINDEXER_BACKGROUND_TASK_EXECUTOR,
			HashMapBuilder.<String, Serializable>put(
				BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS, true
			).put(
				ReindexBackgroundTaskConstants.CLASS_NAME, className
			).put(
				ReindexBackgroundTaskConstants.COMPANY_IDS, companyIds
			).put(
				ReindexBackgroundTaskConstants.EXECUTION_MODE, executionMode
			).build(),
			new ServiceContext());
	}

	private static final String
		_CLASS_NAME_REINDEX_INDEX_REINDEXER_BACKGROUND_TASK_EXECUTOR =
			"com.liferay.portal.search.internal.background.task." +
				"ReindexIndexReindexerBackgroundTaskExecutor";

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	private BundleContext _bundleContext;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

}