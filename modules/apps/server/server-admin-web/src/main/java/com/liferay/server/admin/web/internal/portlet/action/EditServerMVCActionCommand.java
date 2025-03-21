/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action;

import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollectionModel;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.document.library.kernel.document.conversion.DocumentConversion;
import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.processor.AudioProcessor;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.PDFProcessor;
import com.liferay.document.library.kernel.processor.VideoProcessor;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.preview.processor.BasePreviewableDLProcessor;
import com.liferay.image.Ghostscript;
import com.liferay.image.ImageMagick;
import com.liferay.mail.kernel.model.Account;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.convert.ConvertException;
import com.liferay.portal.convert.ConvertProcess;
import com.liferay.portal.convert.ConvertProcessUtil;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncPrintWriter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.SanitizerLogWrapper;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutStagingHandler;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayActionResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.scripting.ScriptingException;
import com.liferay.portal.kernel.scripting.ScriptingHelperUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicy;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicyFactory;
import com.liferay.portal.kernel.security.membershippolicy.RoleMembershipPolicy;
import com.liferay.portal.kernel.security.membershippolicy.UserGroupMembershipPolicy;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutRevisionLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.servlet.DirectServletRegistryUtil;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.ThreadUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.log4j.Log4JUtil;
import com.liferay.portal.security.membershippolicy.RoleMembershipPolicyFactoryUtil;
import com.liferay.portal.security.membershippolicy.SiteMembershipPolicyUtil;
import com.liferay.portal.security.membershippolicy.UserGroupMembershipPolicyFactoryUtil;
import com.liferay.portal.util.MaintenanceUtil;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.ShutdownUtil;
import com.liferay.server.admin.web.internal.constants.ImageMagickResourceLimitConstants;
import com.liferay.server.admin.web.internal.scripting.util.ServerScriptingUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletSession;
import jakarta.portlet.WindowState;

import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.logging.log4j.Level;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Philip Jones
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"jakarta.portlet.name=" + PortletKeys.SERVER_ADMIN,
		"mvc.command.name=/server_admin/edit_server"
	},
	service = MVCActionCommand.class
)
public class EditServerMVCActionCommand extends BaseMVCActionCommand {

	@Override
	public void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		if (!StringUtil.equals(actionRequest.getMethod(), HttpMethods.POST)) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		PortletPreferences portletPreferences = _prefsProps.getPreferences(
			ParamUtil.getLong(actionRequest, "preferencesCompanyId"));

		if (permissionChecker.isCompanyAdmin() && cmd.equals("updateMail")) {
			_updateMail(actionRequest, portletPreferences);

			sendRedirect(actionRequest, actionResponse, redirect);

			return;
		}

		if (!permissionChecker.isOmniadmin()) {
			SessionErrors.add(
				actionRequest,
				PrincipalException.MustBeOmniadmin.class.getName());

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		if (!cmd.equals("addLogLevel") &&
			!cmd.equals("dlGenerateAudioPreviews") &&
			!cmd.equals("dlGenerateOpenOfficePreviews") &&
			!cmd.equals("dlGenerateVideoPreviews") &&
			!cmd.equals("updateLogLevels") &&
			!cmd.equals("updatePortalProperties")) {

			CaptchaUtil.check(actionRequest);
		}

		if (cmd.equals("addLogLevel")) {
			_updateLogLevels(
				Collections.singletonMap(
					ParamUtil.getString(actionRequest, "loggerName"),
					ParamUtil.getString(actionRequest, "priority")));
		}
		else if (cmd.equals("cacheDb")) {
			_cacheDb();
		}
		else if (cmd.equals("cacheMulti")) {
			_cacheMulti();
		}
		else if (cmd.equals("cacheServlet")) {
			_cacheServlet();
		}
		else if (cmd.equals("cacheSingle")) {
			_cacheSingle();
		}
		else if (cmd.equals("cleanUpAddToPagePermissions")) {
			_cleanUpAddToPagePermissions(actionRequest);
		}
		else if (cmd.equals("cleanUpLayoutRevisionPortletPreferences")) {
			_cleanUpLayoutRevisionPortletPreferences();
		}
		else if (cmd.equals("cleanUpOrphanedPortletPreferences")) {
			_cleanUpOrphanedPortletPreferences();
		}
		else if (cmd.startsWith("convertProcess.")) {
			redirect = _convertProcess(actionRequest, actionResponse, cmd);
		}
		else if (cmd.equals("dlDeletePreviews")) {
			_deleteFiles();
		}
		else if (cmd.equals("dlGenerateAudioPreviews")) {
			AudioProcessor audioProcessor = (AudioProcessor)_audioDLProcessor;

			audioProcessor.generatePreviews();

			hideDefaultSuccessMessage(actionRequest);

			SessionMessages.add(actionRequest, "dlGenerateAudioPreviews");
		}
		else if (cmd.equals("dlGenerateOpenOfficePreviews")) {
			_documentConversion.generatePreviews();

			hideDefaultSuccessMessage(actionRequest);

			SessionMessages.add(actionRequest, "dlGenerateOpenOfficePreviews");
		}
		else if (cmd.equals("dlGeneratePDFPreviews")) {
			PDFProcessor pdfProcessor = (PDFProcessor)_dlProcessor;

			pdfProcessor.generatePreviews();

			hideDefaultSuccessMessage(actionRequest);

			SessionMessages.add(actionRequest, "dlGeneratePDFPreviews");
		}
		else if (cmd.equals("dlGenerateVideoPreviews")) {
			VideoProcessor videoProcessor = (VideoProcessor)_videoDLProcessor;

			videoProcessor.generatePreviews();

			hideDefaultSuccessMessage(actionRequest);

			SessionMessages.add(actionRequest, "dlGenerateVideoPreviews");
		}
		else if (cmd.equals("gc")) {
			_gc();
		}
		else if (cmd.equals("runScript")) {
			_runScript(actionRequest, actionResponse);
		}
		else if (cmd.equals("shutdown")) {
			_shutdown(actionRequest);
		}
		else if (cmd.equals("threadDump")) {
			_threadDump();
		}
		else if (cmd.equals("updateExternalServices")) {
			_updateExternalServices(actionRequest, portletPreferences);
		}
		else if (cmd.equals("updateLogLevels")) {
			_updateLogLevels(actionRequest);
		}
		else if (cmd.equals("updateMail")) {
			_updateMail(actionRequest, portletPreferences);
		}
		else if (cmd.equals("updatePortalProperties")) {
			_updatePortalProperties(actionRequest);
		}
		else if (cmd.equals("verifyMembershipPolicies")) {
			_verifyMembershipPolicies();
		}

		sendRedirect(actionRequest, actionResponse, redirect);
	}

	private static void _resetLogLevels(
		Map<String, String> logLevels, Map<String, String> customLogSettings) {

		for (Map.Entry<String, String> logLevel : logLevels.entrySet()) {
			Log4JUtil.setLevel(
				logLevel.getKey(), logLevel.getValue(),
				customLogSettings.containsKey(logLevel.getKey()));
		}
	}

	private static void _updateLogLevels(Map<String, String> logLevels) {
		for (Map.Entry<String, String> logLevelEntry : logLevels.entrySet()) {
			Log4JUtil.setLevel(
				logLevelEntry.getKey(), logLevelEntry.getValue(), true);
		}

		if (!ClusterExecutorUtil.isEnabled()) {
			return;
		}

		if (ClusterMasterExecutorUtil.isMaster()) {
			ClusterRequest clusterRequest =
				ClusterRequest.createMulticastRequest(
					new MethodHandler(
						_resetLogLevelsMethodKey, Log4JUtil.getPriorities(),
						Log4JUtil.getCustomLogSettings()),
					true);

			clusterRequest.setFireAndForget(true);

			ClusterExecutorUtil.execute(clusterRequest);
		}
		else {
			ClusterMasterExecutorUtil.executeOnMaster(
				new MethodHandler(_updateLogLevelsMethodKey, logLevels));
		}
	}

	private void _cacheDb() throws Exception {
		CacheRegistryUtil.clear();
	}

	private void _cacheMulti() throws Exception {
		_multiVMPool.clear();
	}

	private void _cacheServlet() throws Exception {
		DirectServletRegistryUtil.clearServlets();
	}

	private void _cacheSingle() throws Exception {
		_singleVMPool.clear();
	}

	private void _cleanUpAddToPagePermissions(ActionRequest actionRequest)
		throws Exception {

		long companyId = _portal.getCompanyId(actionRequest);

		Role role = _roleLocalService.getRole(companyId, RoleConstants.GUEST);

		_cleanUpAddToPagePermissions(companyId, role.getRoleId(), false);

		role = _roleLocalService.getRole(companyId, RoleConstants.POWER_USER);

		_cleanUpAddToPagePermissions(companyId, role.getRoleId(), true);

		role = _roleLocalService.getRole(companyId, RoleConstants.USER);

		_cleanUpAddToPagePermissions(companyId, role.getRoleId(), false);
	}

	private void _cleanUpAddToPagePermissions(
			long companyId, long roleId, boolean limitScope)
		throws Exception {

		Group userPersonalSite = _groupLocalService.getGroup(
			companyId, GroupConstants.USER_PERSONAL_SITE);

		String groupIdString = String.valueOf(userPersonalSite.getGroupId());

		for (ResourcePermission resourcePermission :
				_resourcePermissionLocalService.getRoleResourcePermissions(
					roleId)) {

			if (!resourcePermission.hasActionId(ActionKeys.ADD_TO_PAGE)) {
				continue;
			}

			_resourcePermissionLocalService.removeResourcePermission(
				companyId, resourcePermission.getName(),
				resourcePermission.getScope(), resourcePermission.getPrimKey(),
				roleId, ActionKeys.ADD_TO_PAGE);

			if (!limitScope) {
				continue;
			}

			_resourcePermissionLocalService.addResourcePermission(
				companyId, resourcePermission.getName(),
				ResourceConstants.SCOPE_GROUP, groupIdString, roleId,
				ActionKeys.ADD_TO_PAGE);
		}
	}

	private void _cleanUpLayoutRevisionPortletPreferences() throws Exception {
		boolean active = CacheRegistryUtil.isActive();

		CacheRegistryUtil.setActive(true);

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			ActionableDynamicQuery actionableDynamicQuery =
				_portletPreferencesLocalService.getActionableDynamicQuery();

			actionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> {
					Property plidProperty = PropertyFactoryUtil.forName("plid");

					DynamicQuery layoutRevisionDynamicQuery =
						_layoutRevisionLocalService.dynamicQuery();

					layoutRevisionDynamicQuery.setProjection(
						ProjectionFactoryUtil.property("layoutRevisionId"));

					dynamicQuery.add(
						plidProperty.in(layoutRevisionDynamicQuery));
				});
			actionableDynamicQuery.setParallel(true);
			actionableDynamicQuery.setPerformActionMethod(
				(com.liferay.portal.kernel.model.PortletPreferences
					portletPreferences) -> {

					LayoutRevision layoutRevision =
						_layoutRevisionLocalService.getLayoutRevision(
							portletPreferences.getPlid());

					Layout layout = _layoutLocalService.getLayout(
						layoutRevision.getPlid());

					if (!layout.isTypePortlet() ||
						_containsPortlet(
							layout, portletPreferences.getPortletId())) {

						return;
					}

					LayoutStagingHandler layoutStagingHandler =
						new LayoutStagingHandler(layout);

					layoutStagingHandler.setLayoutRevision(layoutRevision);

					if (_containsPortlet(
							_proxyProviderFunction.apply(layoutStagingHandler),
							portletPreferences.getPortletId())) {

						return;
					}

					if (_log.isWarnEnabled()) {
						_log.warn(
							"Removing portlet preferences " +
								portletPreferences.getPortletPreferencesId());
					}

					_portletPreferencesLocalService.deletePortletPreferences(
						portletPreferences.getPortletPreferencesId());
				});

			actionableDynamicQuery.performActions();
		}
		finally {
			CacheRegistryUtil.setActive(active);
		}
	}

	private void _cleanUpOrphanedPortletPreferences() throws Exception {
		boolean active = CacheRegistryUtil.isActive();

		CacheRegistryUtil.setActive(true);

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_companyLocalService.forEachCompanyId(
				companyId -> {
					List<Long> ctCollectionIds = ListUtil.toList(
						_ctCollectionLocalService.getCTCollections(
							companyId,
							new int[] {
								WorkflowConstants.STATUS_DRAFT,
								WorkflowConstants.STATUS_SCHEDULED
							},
							QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
						CTCollectionModel::getCtCollectionId);

					ctCollectionIds.add(
						CTConstants.CT_COLLECTION_ID_PRODUCTION);

					for (long ctCollectionId : ctCollectionIds) {
						_cleanUpOrphanedPortletPreferences(ctCollectionId);
					}
				});
		}
		finally {
			CacheRegistryUtil.setActive(active);
		}
	}

	private void _cleanUpOrphanedPortletPreferences(long ctCollectionId)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			ActionableDynamicQuery actionableDynamicQuery =
				_portletPreferencesLocalService.getActionableDynamicQuery();

			actionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> {
					Property plidProperty = PropertyFactoryUtil.forName("plid");

					DynamicQuery layoutRevisionDynamicQuery =
						_layoutRevisionLocalService.dynamicQuery();

					layoutRevisionDynamicQuery.setProjection(
						ProjectionFactoryUtil.property("layoutRevisionId"));

					dynamicQuery.add(
						plidProperty.notIn(layoutRevisionDynamicQuery));
				});
			actionableDynamicQuery.setParallel(true);
			actionableDynamicQuery.setPerformActionMethod(
				(com.liferay.portal.kernel.model.PortletPreferences
					portletPreferences) -> _performAction(portletPreferences));

			actionableDynamicQuery.performActions();
		}
	}

	private boolean _containsPortlet(Layout layout, String portletId) {
		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		List<String> portletIds = ListUtil.toList(
			layoutTypePortlet.getAllPortlets(), Portlet.PORTLET_ID_ACCESSOR);

		return portletIds.contains(portletId);
	}

	private String _convertProcess(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String cmd)
		throws Exception {

		String className = StringUtil.replaceFirst(
			cmd, "convertProcess.", StringPool.BLANK);

		ConvertProcess convertProcess = ConvertProcessUtil.getConvertProcess(
			className);

		String[] parameters = convertProcess.getParameterNames();

		if (parameters != null) {
			String[] values = new String[parameters.length];

			for (int i = 0; i < parameters.length; i++) {
				String parameter =
					className + StringPool.PERIOD + parameters[i];

				if (parameters[i].contains(StringPool.EQUAL)) {
					String[] parameterPair = StringUtil.split(
						parameters[i], CharPool.EQUAL);

					parameter =
						className + StringPool.PERIOD + parameterPair[0];
				}

				values[i] = ParamUtil.getString(actionRequest, parameter);
			}

			convertProcess.setParameterValues(values);
		}

		try {
			convertProcess.validate();
		}
		catch (ConvertException convertException) {
			SessionErrors.add(
				actionRequest, convertException.getClass(), convertException);

			return null;
		}

		String path = convertProcess.getPath();

		if (path != null) {
			LiferayActionResponse liferayActionResponse =
				(LiferayActionResponse)actionResponse;

			return PortletURLBuilder.createRenderURL(
				liferayActionResponse
			).setMVCRenderCommandName(
				path
			).setWindowState(
				WindowState.MAXIMIZED
			).buildString();
		}

		PortletSession portletSession = actionRequest.getPortletSession();

		MaintenanceUtil.maintain(portletSession.getId(), className);

		Message message = new Message();

		message.setPayload(convertProcess);

		_messageBus.sendMessage(DestinationNames.CONVERT_PROCESS, message);

		return null;
	}

	private void _deleteFiles() {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				_store.deleteDirectory(
					companyId, BasePreviewableDLProcessor.REPOSITORY_ID,
					BasePreviewableDLProcessor.PREVIEW_PATH);

				_store.deleteDirectory(
					companyId, BasePreviewableDLProcessor.REPOSITORY_ID,
					BasePreviewableDLProcessor.THUMBNAIL_PATH);
			});
	}

	private void _gc() throws Exception {
		Runtime runtime = Runtime.getRuntime();

		runtime.gc();
	}

	private void _performAction(
			com.liferay.portal.kernel.model.PortletPreferences
				portletPreferences)
		throws PortalException {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					portletPreferences.getCtCollectionId())) {

			if ((portletPreferences.getOwnerId() !=
					PortletKeys.PREFS_OWNER_ID_DEFAULT) ||
				(portletPreferences.getOwnerType() !=
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT)) {

				return;
			}

			Layout layout = _layoutLocalService.getLayout(
				portletPreferences.getPlid());

			if (layout.isTypeAssetDisplay() || layout.isTypeContent() ||
				layout.isTypeControlPanel()) {

				return;
			}

			UnicodeProperties typeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			Set<String> keys = typeSettingsUnicodeProperties.keySet();

			boolean orphan = true;

			for (String key : keys) {
				String value = typeSettingsUnicodeProperties.getProperty(key);

				if (value.contains(portletPreferences.getPortletId())) {
					orphan = false;

					break;
				}
			}

			if (orphan) {
				_portletPreferencesLocalService.deletePortletPreferences(
					portletPreferences.getPortletPreferencesId());
			}
		}
	}

	private void _runScript(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String language = ParamUtil.getString(actionRequest, "language");
		String output = ParamUtil.getString(actionRequest, "output");
		String script = ParamUtil.getString(actionRequest, "script");

		PortletConfig portletConfig = getPortletConfig(actionRequest);

		Map<String, Object> portletObjects =
			ScriptingHelperUtil.getPortletObjects(
				portletConfig, portletConfig.getPortletContext(), actionRequest,
				actionResponse);

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		UnsyncPrintWriter unsyncPrintWriter = new UnsyncPrintWriter(
			unsyncByteArrayOutputStream);

		portletObjects.put("out", unsyncPrintWriter);

		try {
			SessionMessages.add(actionRequest, "language", language);
			SessionMessages.add(actionRequest, "script", script);
			SessionMessages.add(actionRequest, "output", output);

			ServerScriptingUtil.execute(portletObjects, language, script);

			unsyncPrintWriter.flush();

			SessionMessages.add(
				actionRequest, "scriptOutput",
				unsyncByteArrayOutputStream.toString());
		}
		catch (ScriptingException scriptingException) {
			SessionErrors.add(
				actionRequest, ScriptingException.class.getName(),
				scriptingException);

			Log log = SanitizerLogWrapper.allowCRLF(_log);

			log.error(scriptingException.getMessage());
		}
	}

	private void _shutdown(ActionRequest actionRequest) throws Exception {
		if (ShutdownUtil.isInProcess()) {
			ShutdownUtil.cancel();
		}
		else {
			long minutes =
				ParamUtil.getInteger(actionRequest, "minutes") * Time.MINUTE;

			if (minutes <= 0) {
				SessionErrors.add(actionRequest, "shutdownMinutes");
			}
			else {
				String message = ParamUtil.getString(actionRequest, "message");

				ShutdownUtil.shutdown(minutes, message);
			}
		}
	}

	private void _threadDump() throws Exception {
		if (_log.isInfoEnabled()) {
			Log log = SanitizerLogWrapper.allowCRLF(_log);

			log.info(ThreadUtil.threadDump());
		}
		else {
			Class<?> clazz = getClass();

			_log.error(
				"Thread dumps require the log level to be at least INFO for " +
					clazz.getName());
		}
	}

	private void _updateExternalServices(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		boolean imageMagickEnabled = ParamUtil.getBoolean(
			actionRequest, "imageMagickEnabled");
		String imageMagickPath = ParamUtil.getString(
			actionRequest, "imageMagickPath");

		portletPreferences.setValue(
			PropsKeys.IMAGEMAGICK_ENABLED, String.valueOf(imageMagickEnabled));
		portletPreferences.setValue(
			PropsKeys.IMAGEMAGICK_GLOBAL_SEARCH_PATH, imageMagickPath);

		for (String name : ImageMagickResourceLimitConstants.PROPERTY_NAMES) {
			String propertyName = PropsKeys.IMAGEMAGICK_RESOURCE_LIMIT + name;

			portletPreferences.setValue(
				propertyName, ParamUtil.getString(actionRequest, propertyName));
		}

		portletPreferences.store();

		_ghostscript.reset();
		_imageMagick.reset();
	}

	private void _updateLogLevels(ActionRequest actionRequest) {
		Enumeration<String> enumeration = actionRequest.getParameterNames();

		Map<String, String> logLevels = new HashMap<>();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			if (name.startsWith("logLevel")) {
				logLevels.put(
					name.substring(8),
					ParamUtil.getString(
						actionRequest, name, Level.INFO.toString()));
			}
		}

		_updateLogLevels(logLevels);
	}

	private void _updateMail(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		String advancedProperties = ParamUtil.getString(
			actionRequest, "advancedProperties");
		String pop3Host = ParamUtil.getString(actionRequest, "pop3Host");
		String pop3Password = ParamUtil.getString(
			actionRequest, "pop3Password");
		int pop3Port = ParamUtil.getInteger(actionRequest, "pop3Port");
		boolean pop3Secure = ParamUtil.getBoolean(actionRequest, "pop3Secure");
		String pop3User = ParamUtil.getString(actionRequest, "pop3User");
		boolean popServerNotificationsEnabled = ParamUtil.getBoolean(
			actionRequest, "popServerNotificationsEnabled");
		String smtpHost = ParamUtil.getString(actionRequest, "smtpHost");
		String smtpPassword = ParamUtil.getString(
			actionRequest, "smtpPassword");
		int smtpPort = ParamUtil.getInteger(actionRequest, "smtpPort");
		boolean smtpSecure = ParamUtil.getBoolean(actionRequest, "smtpSecure");
		boolean smtpStartTLSEnable = ParamUtil.getBoolean(
			actionRequest, "smtpStartTLSEnable");
		String smtpUser = ParamUtil.getString(actionRequest, "smtpUser");

		String storeProtocol = Account.PROTOCOL_POP;

		if (pop3Secure) {
			storeProtocol = Account.PROTOCOL_POPS;
		}

		String transportProtocol = Account.PROTOCOL_SMTP;

		if (smtpSecure) {
			transportProtocol = Account.PROTOCOL_SMTPS;
		}

		portletPreferences.setValue(PropsKeys.MAIL_SESSION_MAIL, "true");
		portletPreferences.setValue(
			PropsKeys.MAIL_SESSION_MAIL_ADVANCED_PROPERTIES,
			advancedProperties);
		portletPreferences.setValue(
			PropsKeys.MAIL_SESSION_MAIL_POP3_HOST, pop3Host);

		if (!pop3Password.equals(Portal.TEMP_OBFUSCATION_VALUE)) {
			portletPreferences.setValue(
				PropsKeys.MAIL_SESSION_MAIL_POP3_PASSWORD, pop3Password);
		}

		portletPreferences.setValue(
			PropsKeys.MAIL_SESSION_MAIL_POP3_PORT, String.valueOf(pop3Port));
		portletPreferences.setValue(
			PropsKeys.MAIL_SESSION_MAIL_POP3_USER, pop3User);
		portletPreferences.setValue(
			PropsKeys.MAIL_SESSION_MAIL_SMTP_HOST, smtpHost);

		if (!smtpPassword.equals(Portal.TEMP_OBFUSCATION_VALUE)) {
			portletPreferences.setValue(
				PropsKeys.MAIL_SESSION_MAIL_SMTP_PASSWORD, smtpPassword);
		}

		portletPreferences.setValue(
			PropsKeys.MAIL_SESSION_MAIL_SMTP_PORT, String.valueOf(smtpPort));
		portletPreferences.setValue(
			PropsKeys.MAIL_SESSION_MAIL_SMTP_STARTTLS_ENABLE,
			String.valueOf(smtpStartTLSEnable));
		portletPreferences.setValue(
			PropsKeys.MAIL_SESSION_MAIL_SMTP_USER, smtpUser);
		portletPreferences.setValue(
			PropsKeys.MAIL_SESSION_MAIL_STORE_PROTOCOL, storeProtocol);
		portletPreferences.setValue(
			PropsKeys.MAIL_SESSION_MAIL_TRANSPORT_PROTOCOL, transportProtocol);
		portletPreferences.setValue(
			PropsKeys.POP_SERVER_NOTIFICATIONS_ENABLED,
			String.valueOf(popServerNotificationsEnabled));

		portletPreferences.store();

		_mailService.clearSession();
	}

	private void _updatePortalProperties(ActionRequest actionRequest) {
		Enumeration<String> enumeration = actionRequest.getParameterNames();

		Map<String, String> portalProperties = new HashMap<>();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			if (name.startsWith("portalProperty")) {
				portalProperties.put(
					name.substring(14),
					ParamUtil.getString(actionRequest, name, "false"));
			}
		}

		_updatePortalProperties(portalProperties);
	}

	private void _updatePortalProperties(Map<String, String> portalProperties) {
		for (Map.Entry<String, String> entry : portalProperties.entrySet()) {
			PropsUtil.set(entry.getKey(), entry.getValue());
		}
	}

	private void _verifyMembershipPolicies() throws Exception {
		OrganizationMembershipPolicy organizationMembershipPolicy =
			_organizationMembershipPolicyFactory.
				getOrganizationMembershipPolicy();

		organizationMembershipPolicy.verifyPolicy();

		RoleMembershipPolicy roleMembershipPolicy =
			RoleMembershipPolicyFactoryUtil.getRoleMembershipPolicy();

		roleMembershipPolicy.verifyPolicy();

		SiteMembershipPolicyUtil.verifyPolicy();

		UserGroupMembershipPolicy userGroupMembershipPolicy =
			UserGroupMembershipPolicyFactoryUtil.getUserGroupMembershipPolicy();

		userGroupMembershipPolicy.verifyPolicy();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditServerMVCActionCommand.class);

	private static final Function<InvocationHandler, Layout>
		_proxyProviderFunction = ProxyUtil.getProxyProviderFunction(
			Layout.class, ModelWrapper.class);
	private static final MethodKey _resetLogLevelsMethodKey = new MethodKey(
		EditServerMVCActionCommand.class, "_resetLogLevels", Map.class,
		Map.class);
	private static final MethodKey _updateLogLevelsMethodKey = new MethodKey(
		EditServerMVCActionCommand.class, "_updateLogLevels", Map.class);

	@Reference(target = "(type=" + DLProcessorConstants.AUDIO_PROCESSOR + ")")
	private DLProcessor _audioDLProcessor;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference(target = "(type=" + DLProcessorConstants.PDF_PROCESSOR + ")")
	private DLProcessor _dlProcessor;

	@Reference
	private DocumentConversion _documentConversion;

	@Reference
	private Ghostscript _ghostscript;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ImageMagick _imageMagick;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutRevisionLocalService _layoutRevisionLocalService;

	@Reference
	private MailService _mailService;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private MultiVMPool _multiVMPool;

	@Reference
	private OrganizationMembershipPolicyFactory
		_organizationMembershipPolicyFactory;

	@Reference
	private Portal _portal;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private PrefsProps _prefsProps;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SingleVMPool _singleVMPool;

	@Reference(target = "(default=true)")
	private Store _store;

	@Reference(target = "(type=" + DLProcessorConstants.VIDEO_PROCESSOR + ")")
	private DLProcessor _videoDLProcessor;

}