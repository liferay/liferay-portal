/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.portlet;

import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PublicRenderParameter;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.LiferayPortletConfigWrapper;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletConfigurationLayoutUtil;
import com.liferay.portal.kernel.portlet.PortletConfigurationListener;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletQNameUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.propagator.PermissionPropagator;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.settings.ArchivedSettings;
import com.liferay.portal.kernel.settings.ArchivedSettingsFactory;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.PortletInstanceSettingsLocator;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.settings.SettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.configuration.kernel.util.PortletConfigurationUtil;
import com.liferay.portlet.configuration.web.internal.constants.PortletConfigurationPortletKeys;
import com.liferay.portlet.configuration.web.internal.constants.PortletConfigurationWebKeys;
import com.liferay.portlet.configuration.web.internal.portlet.action.ActionUtil;
import com.liferay.portlet.portletconfiguration.util.PublicRenderParameterConfiguration;
import com.liferay.roles.admin.constants.RolesAdminWebKeys;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;

import jakarta.portlet.ActionParameters;
import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-configuration",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.system=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Portlet Configuration",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/edit_configuration.jsp",
		"jakarta.portlet.name=" + PortletConfigurationPortletKeys.PORTLET_CONFIGURATION,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.version=4.0"
	},
	service = jakarta.portlet.Portlet.class
)
public class PortletConfigurationPortlet extends MVCPortlet {

	public void deleteArchivedSetups(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Portlet portlet = ActionUtil.getPortlet(actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String[] names = null;

		String name = ParamUtil.getString(actionRequest, "name");

		if (Validator.isNotNull(name)) {
			names = new String[] {name};
		}
		else {
			names = ParamUtil.getStringValues(actionRequest, "rowIds");
		}

		for (String curName : names) {
			ArchivedSettings archivedSettings =
				_archivedSettingsFactory.getPortletInstanceArchivedSettings(
					themeDisplay.getSiteGroupId(), portlet.getRootPortletId(),
					curName);

			archivedSettings.delete();
		}
	}

	public void editConfiguration(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Portlet portlet = ActionUtil.getPortlet(actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		actionRequest = ActionUtil.getWrappedActionRequest(
			actionRequest,
			_getPortletPreferences(themeDisplay, portlet.getPortletId()));

		ConfigurationAction configurationAction = _getConfigurationAction(
			portlet);

		if (configurationAction == null) {
			return;
		}

		PortletConfig portletConfig = (PortletConfig)actionRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG);

		configurationAction.processAction(
			portletConfig, actionRequest, actionResponse);

		PortletLayoutListener portletLayoutListener =
			portlet.getPortletLayoutListenerInstance();

		if (portletLayoutListener != null) {
			Layout layout = themeDisplay.getLayout();

			portletLayoutListener.onSetup(
				portlet.getPortletId(), layout.getPlid());
		}
	}

	public void editPublicRenderParameters(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Portlet portlet = ActionUtil.getPortlet(actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletPreferences portletPreferences = _getPortletPreferences(
			themeDisplay, portlet.getPortletId());

		if (portletPreferences == null) {
			portletPreferences = ActionUtil.getLayoutPortletSetup(
				actionRequest, portlet);
		}

		actionRequest = ActionUtil.getWrappedActionRequest(
			actionRequest, portletPreferences);

		Enumeration<String> enumeration = portletPreferences.getNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			if (name.startsWith(
					PublicRenderParameterConfiguration.IGNORE_PREFIX) ||
				name.startsWith(
					PublicRenderParameterConfiguration.MAPPING_PREFIX)) {

				portletPreferences.reset(name);
			}
		}

		for (PublicRenderParameter publicRenderParameter :
				portlet.getPublicRenderParameters()) {

			String publicRenderParameterName =
				PortletQNameUtil.getPublicRenderParameterName(
					publicRenderParameter.getQName());

			String ignoreKey = PublicRenderParameterConfiguration.getIgnoreKey(
				publicRenderParameterName);

			boolean ignoreValue = ParamUtil.getBoolean(
				actionRequest, ignoreKey);

			if (ignoreValue) {
				portletPreferences.setValue(
					ignoreKey, String.valueOf(Boolean.TRUE));
			}
			else {
				String mappingKey =
					PublicRenderParameterConfiguration.getMappingKey(
						publicRenderParameterName);

				String mappingValue = ParamUtil.getString(
					actionRequest, mappingKey);

				if (Validator.isNotNull(mappingValue)) {
					portletPreferences.setValue(mappingKey, mappingValue);
				}
			}
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			portletPreferences.store();
		}
	}

	public void editScope(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_updateScope(actionRequest);

		if (!SessionErrors.isEmpty(actionRequest)) {
			return;
		}

		String portletResource = ParamUtil.getString(
			actionRequest, "portletResource");

		SessionMessages.add(
			actionRequest,
			_portal.getPortletId(actionRequest) +
				SessionMessages.KEY_SUFFIX_REFRESH_PORTLET,
			portletResource);

		SessionMessages.add(
			actionRequest,
			_portal.getPortletId(actionRequest) +
				SessionMessages.KEY_SUFFIX_UPDATED_CONFIGURATION);
	}

	public void editSharing(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		PortletPreferences portletPreferences =
			ActionUtil.getLayoutPortletSetup(
				actionRequest, ActionUtil.getPortlet(actionRequest));

		actionRequest = ActionUtil.getWrappedActionRequest(
			actionRequest, portletPreferences);

		updateAnyWebsite(actionRequest, portletPreferences);
		_updateFacebook(actionRequest, portletPreferences);
		_updateFriends(actionRequest, portletPreferences);
		_updateGoogleGadget(actionRequest, portletPreferences);
		_updateNetvibes(actionRequest, portletPreferences);

		portletPreferences.store();

		if (!SessionErrors.isEmpty(actionRequest)) {
			return;
		}

		String portletResource = ParamUtil.getString(
			actionRequest, "portletResource");

		SessionMessages.add(
			actionRequest,
			_portal.getPortletId(actionRequest) +
				SessionMessages.KEY_SUFFIX_REFRESH_PORTLET,
			portletResource);

		SessionMessages.add(
			actionRequest,
			_portal.getPortletId(actionRequest) +
				SessionMessages.KEY_SUFFIX_UPDATED_CONFIGURATION);
	}

	public void editSupportedClients(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Portlet portlet = ActionUtil.getPortlet(actionRequest);

		PortletPreferences portletPreferences =
			ActionUtil.getLayoutPortletSetup(
				actionRequest, ActionUtil.getPortlet(actionRequest));

		actionRequest = ActionUtil.getWrappedActionRequest(
			actionRequest, portletPreferences);

		Set<String> allPortletModes = portlet.getAllPortletModes();

		for (String portletMode : allPortletModes) {
			String mobileDevicesParam =
				"portletSetupSupportedClientsMobileDevices_" + portletMode;

			boolean mobileDevices = ParamUtil.getBoolean(
				actionRequest, mobileDevicesParam);

			portletPreferences.setValue(
				mobileDevicesParam, String.valueOf(mobileDevices));
		}

		portletPreferences.store();
	}

	@Override
	public void init(PortletConfig portletConfig) throws PortletException {
		if (portletConfig instanceof LiferayPortletConfig) {
			PortletConfigurationPortletPortletConfig
				portletConfigurationPortletPortletConfig =
					new PortletConfigurationPortletPortletConfig(
						(LiferayPortletConfig)portletConfig);

			super.init(portletConfigurationPortletPortletConfig);
		}
		else {
			super.init(portletConfig);
		}
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		_portletRequestThreadLocal.set(actionRequest);

		actionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, getPortletConfig());

		super.processAction(actionRequest, actionResponse);
	}

	@Override
	public void processEvent(
			EventRequest eventRequest, EventResponse eventResponse)
		throws IOException, PortletException {

		_portletRequestThreadLocal.set(eventRequest);

		eventRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, getPortletConfig());

		super.processEvent(eventRequest, eventResponse);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		_portletRequestThreadLocal.set(renderRequest);

		renderRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, getPortletConfig());

		super.render(renderRequest, renderResponse);
	}

	public void restoreArchivedSetup(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Portlet portlet = ActionUtil.getPortlet(actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Settings portletInstanceSettings = FallbackKeysSettingsUtil.getSettings(
			new PortletInstanceSettingsLocator(
				themeDisplay.getLayout(), portlet.getPortletId()));

		ModifiableSettings portletInstanceModifiableSettings =
			portletInstanceSettings.getModifiableSettings();

		String name = ParamUtil.getString(actionRequest, "name");

		ArchivedSettings archivedSettings =
			_archivedSettingsFactory.getPortletInstanceArchivedSettings(
				themeDisplay.getSiteGroupId(), portlet.getRootPortletId(),
				name);

		portletInstanceModifiableSettings.reset();

		portletInstanceModifiableSettings.setValues(archivedSettings);

		portletInstanceModifiableSettings.store();

		String portletResource = ParamUtil.getString(
			actionRequest, "portletResource");

		SessionMessages.add(
			actionRequest,
			_portal.getPortletId(actionRequest) +
				SessionMessages.KEY_SUFFIX_REFRESH_PORTLET,
			portletResource);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		_portletRequestThreadLocal.set(resourceRequest);

		resourceRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, getPortletConfig());

		super.serveResource(resourceRequest, resourceResponse);
	}

	public void updateAnyWebsite(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		boolean widgetShowAddAppLink = ParamUtil.getBoolean(
			actionRequest, "widgetShowAddAppLink");

		portletPreferences.setValue(
			"lfrWidgetShowAddAppLink", String.valueOf(widgetShowAddAppLink));
	}

	public void updateArchivedSetup(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Portlet portlet = ActionUtil.getPortlet(actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String name = ParamUtil.getString(actionRequest, "name");

		ArchivedSettings archivedSettings =
			_archivedSettingsFactory.getPortletInstanceArchivedSettings(
				themeDisplay.getSiteGroupId(), portlet.getRootPortletId(),
				name);

		SettingsLocator settingsLocator = new PortletInstanceSettingsLocator(
			themeDisplay.getLayout(), portlet.getPortletId());

		Settings portletInstanceSettings = FallbackKeysSettingsUtil.getSettings(
			settingsLocator);

		ModifiableSettings portletInstanceModifiableSettings =
			portletInstanceSettings.getModifiableSettings();

		archivedSettings.setValues(portletInstanceModifiableSettings);

		archivedSettings.store();
	}

	public void updateRolePermissions(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String portletResource = ParamUtil.getString(
			actionRequest, "portletResource");
		String modelResource = ParamUtil.getString(
			actionRequest, "modelResource");
		long[] roleIds = StringUtil.split(
			ParamUtil.getString(
				actionRequest, "rolesSearchContainerPrimaryKeys"),
			0L);

		String selResource = PortletIdCodec.decodePortletName(portletResource);

		if (Validator.isNotNull(modelResource)) {
			selResource = modelResource;
		}

		long resourceGroupId = ParamUtil.getLong(
			actionRequest, "resourceGroupId", themeDisplay.getScopeGroupId());
		String[] resourcePrimKeys = ParamUtil.getStringValues(
			actionRequest, "resourcePrimKey");

		Map<Long, String[]> roleIdCheckedActionIdsMap = new HashMap<>();

		for (long roleId : roleIds) {
			roleIdCheckedActionIdsMap.put(
				roleId,
				ArrayUtil.toStringArray(
					_getCheckedActionIds(
						actionRequest, roleId,
						value -> !Objects.equals(value, "indeterminate"))));
		}

		PermissionPropagator permissionPropagator = null;

		if (PropsValues.PERMISSIONS_PROPAGATION_ENABLED &&
			Validator.isNotNull(portletResource)) {

			Portlet portlet = _portletLocalService.getPortletById(
				themeDisplay.getCompanyId(), portletResource);

			permissionPropagator = portlet.getPermissionPropagatorInstance();
		}

		for (String resourcePrimKey : resourcePrimKeys) {
			Map<Long, String[]> roleIdActionIdsMap = new HashMap<>(
				roleIdCheckedActionIdsMap);

			if (resourcePrimKeys.length > 1) {
				_addIndeterminateActionIds(
					actionRequest, themeDisplay.getCompanyId(), resourcePrimKey,
					roleIdActionIdsMap, selResource);
			}

			long ctCollectionId = 0;

			if (_serviceTrackerMap.containsKey(selResource)) {
				ctCollectionId = CTCollectionThreadLocal.getCTCollectionId();
			}

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollectionId)) {

				_resourcePermissionService.setIndividualResourcePermissions(
					resourceGroupId, themeDisplay.getCompanyId(), selResource,
					resourcePrimKey, roleIdActionIdsMap);
			}

			if (permissionPropagator != null) {
				permissionPropagator.propagateRolePermissions(
					actionRequest, modelResource, resourcePrimKey, roleIds);
			}
		}

		if (Validator.isNull(modelResource)) {

			// Force update of layout modified date. See LPS-59246.

			PortletPreferences portletPreferences =
				ActionUtil.getLayoutPortletSetup(
					actionRequest, ActionUtil.getPortlet(actionRequest));

			portletPreferences.store();
		}

		_updateLayoutStatus(
			themeDisplay.getLayout(),
			ServiceContextFactory.getInstance(actionRequest),
			themeDisplay.getUserId());

		if (resourcePrimKeys.length > 1) {
			SessionMessages.add(
				actionRequest, "requestProcessed",
				_language.format(
					themeDisplay.getLocale(),
					"x-permissions-were-updated-successfully",
					resourcePrimKeys.length));
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, (Class<CTService<?>>)(Class<?>)CTService.class, null,
			(serviceReference, emitter) -> {
				CTService<?> ctService = bundleContext.getService(
					serviceReference);

				Class<?> modelClass = ctService.getModelClass();

				emitter.emit(modelClass.getName());
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			String mvcPath = renderRequest.getParameter("mvcPath");

			if (mvcPath.equals("/edit_permissions.jsp")) {
				_checkEditPermissionsJSP(renderRequest);

				renderRequest.setAttribute(
					RolesAdminWebKeys.ROLE_TYPE_CONTRIBUTOR_PROVIDER,
					_roleTypeContributorProvider);

				super.doDispatch(renderRequest, renderResponse);

				return;
			}

			Portlet portlet = ActionUtil.getPortlet(renderRequest);

			if (mvcPath.endsWith("edit_configuration.jsp") ||
				mvcPath.endsWith("edit_public_render_parameters.jsp")) {

				PortletPreferences portletPreferences = _getPortletPreferences(
					(ThemeDisplay)renderRequest.getAttribute(
						WebKeys.THEME_DISPLAY),
					portlet.getPortletId());

				renderRequest = ActionUtil.getWrappedRenderRequest(
					renderRequest, portletPreferences);

				if (mvcPath.endsWith("edit_configuration.jsp")) {
					_renderEditConfiguration(renderRequest, portlet);
				}
				else {
					_renderEditPublicParameters(renderRequest, portlet);
				}
			}
			else {
				PortletPreferences portletPreferences =
					ActionUtil.getLayoutPortletSetup(renderRequest, portlet);

				renderRequest = ActionUtil.getWrappedRenderRequest(
					renderRequest, portletPreferences);

				if (mvcPath.endsWith("edit_configuration_templates.jsp")) {
					String moduleName = _npmResolver.resolveModuleName(
						"portlet-configuration-web");

					renderRequest.setAttribute(
						PortletConfigurationWebKeys.MODULE_NAME, moduleName);

					renderRequest.setAttribute(
						PortletConfigurationWebKeys.SETTINGS_FACTORY,
						_archivedSettingsFactory);
				}
			}

			renderResponse.setTitle(
				ActionUtil.getTitle(portlet, renderRequest));

			super.doDispatch(renderRequest, renderResponse);
		}
		catch (PortalException portalException) {
			if (_log.isInfoEnabled()) {
				_log.info(portalException);
			}

			_handleException(renderRequest, renderResponse, portalException);
		}
		catch (Exception exception) {
			_log.error(exception);

			_handleException(renderRequest, renderResponse, exception);
		}
	}

	private void _addIndeterminateActionIds(
			ActionRequest actionRequest, long companyId, String resourcePrimKey,
			Map<Long, String[]> roleIdActionIdsMap, String selResource)
		throws Exception {

		for (Map.Entry<Long, String[]> entry : roleIdActionIdsMap.entrySet()) {
			Long roleId = entry.getKey();

			List<String> indeterminateActionIds = _getCheckedActionIds(
				actionRequest, roleId,
				value -> Objects.equals(value, "indeterminate"));

			if (ListUtil.isEmpty(indeterminateActionIds)) {
				continue;
			}

			List<String> availableActionIds =
				_resourcePermissionLocalService.
					getAvailableResourcePermissionActionIds(
						companyId, selResource,
						ResourceConstants.SCOPE_INDIVIDUAL, resourcePrimKey,
						roleId, indeterminateActionIds);

			entry.setValue(
				ArrayUtil.append(
					entry.getValue(),
					ArrayUtil.toStringArray(availableActionIds)));
		}
	}

	private void _checkEditPermissionsJSP(PortletRequest request)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		String modelResource = ParamUtil.getString(request, "modelResource");

		long resourceGroupId = ParamUtil.getLong(
			request, "resourceGroupId", themeDisplay.getScopeGroupId());

		if (Validator.isNotNull(modelResource)) {
			String[] resourcePrimKeys = ParamUtil.getStringValues(
				request, "resourcePrimKey");

			for (String resourcePrimKey : resourcePrimKeys) {
				_permissionService.checkPermission(
					resourceGroupId, modelResource, resourcePrimKey);
			}

			return;
		}

		String portletResource = ParamUtil.getString(
			request, "portletResource");

		PortletPermissionUtil.check(
			themeDisplay.getPermissionChecker(), resourceGroupId,
			PortletConfigurationLayoutUtil.getLayout(themeDisplay),
			portletResource, ActionKeys.PERMISSIONS);
	}

	private List<String> _getCheckedActionIds(
		ActionRequest actionRequest, long roleId,
		Predicate<String> valuePredicate) {

		List<String> actionIds = new ArrayList<>();

		ActionParameters actionParameters = actionRequest.getActionParameters();

		for (String name : actionParameters.getNames()) {
			if (!name.startsWith(roleId + ActionUtil.ACTION)) {
				continue;
			}

			if (valuePredicate.test(actionParameters.getValue(name))) {
				int pos = name.indexOf(ActionUtil.ACTION);

				String actionId = name.substring(
					pos + ActionUtil.ACTION.length());

				actionIds.add(actionId);
			}
		}

		return actionIds;
	}

	private ConfigurationAction _getConfigurationAction(Portlet portlet)
		throws Exception {

		if (portlet == null) {
			return null;
		}

		ConfigurationAction configurationAction =
			portlet.getConfigurationActionInstance();

		if (configurationAction == null) {
			_log.error(
				"Configuration action for portlet " + portlet.getPortletId() +
					" is null");
		}

		return configurationAction;
	}

	private Tuple _getNewScope(ActionRequest actionRequest) throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		String[] scopes = StringUtil.split(
			ParamUtil.getString(actionRequest, "scope"));

		String scopeType = scopes[0];

		long scopeGroupId = 0;
		String scopeName = null;

		if (Validator.isNull(scopeType)) {
			scopeGroupId = layout.getGroupId();
		}
		else if (scopeType.equals("company")) {
			scopeGroupId = themeDisplay.getCompanyGroupId();
			scopeName = themeDisplay.translate("global");
		}
		else if (scopeType.equals("layout")) {
			String scopeLayoutUuid = scopes[1];

			Layout scopeLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
				scopeLayoutUuid, layout.getGroupId(), layout.isPrivateLayout());

			if (!scopeLayout.hasScopeGroup()) {
				_groupLocalService.addGroup(
					themeDisplay.getUserId(),
					GroupConstants.DEFAULT_PARENT_GROUP_ID,
					Layout.class.getName(), scopeLayout.getPlid(),
					GroupConstants.DEFAULT_LIVE_GROUP_ID,
					HashMapBuilder.put(
						LocaleUtil.getDefault(),
						String.valueOf(scopeLayout.getPlid())
					).build(),
					null, 0, true,
					GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false,
					true, null);
			}

			scopeGroupId = scopeLayout.getGroupId();
			scopeName = scopeLayout.getName(themeDisplay.getLocale());
		}
		else {
			throw new IllegalArgumentException(
				"Scope type " + scopeType + " is invalid");
		}

		return new Tuple(scopeGroupId, scopeName);
	}

	private String _getOldScopeName(ActionRequest actionRequest)
		throws Exception {

		PortletPreferences portletPreferences = actionRequest.getPreferences();

		String scopeType = GetterUtil.getString(
			portletPreferences.getValue("lfrScopeType", null));

		if (Validator.isNull(scopeType)) {
			return null;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String scopeName = null;

		if (scopeType.equals("company")) {
			scopeName = themeDisplay.translate("global");
		}
		else if (scopeType.equals("layout")) {
			String scopeLayoutUuid = GetterUtil.getString(
				portletPreferences.getValue("lfrScopeLayoutUuid", null));

			Layout layout = themeDisplay.getLayout();

			Layout scopeLayout =
				_layoutLocalService.fetchLayoutByUuidAndGroupId(
					scopeLayoutUuid, layout.getGroupId(),
					layout.isPrivateLayout());

			if (scopeLayout != null) {
				scopeName = scopeLayout.getName(themeDisplay.getLocale());
			}
		}
		else {
			throw new IllegalArgumentException(
				"Scope type " + scopeType + " is invalid");
		}

		return scopeName;
	}

	private PortletPreferences _getPortletPreferences(
			ThemeDisplay themeDisplay, String portletId)
		throws PortalException {

		Layout layout = themeDisplay.getLayout();

		if (!layout.isSupportsEmbeddedPortlets() ||
			!themeDisplay.isPortletEmbedded(portletId)) {

			return null;
		}

		return _portletPreferencesLocalService.getPreferences(
			PortletPreferencesFactoryUtil.getPortletPreferencesIds(
				themeDisplay.getRequest(), layout, portletId));
	}

	private String _getPortletTitle(
		PortletRequest portletRequest, Portlet portlet,
		PortletPreferences portletPreferences) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String portletTitle = PortletConfigurationUtil.getPortletTitle(
			portlet.getPortletId(), portletPreferences,
			themeDisplay.getLanguageId());

		if (Validator.isNull(portletTitle)) {
			ServletContext servletContext =
				(ServletContext)portletRequest.getAttribute(WebKeys.CTX);

			portletTitle = _portal.getPortletTitle(
				portlet, servletContext, themeDisplay.getLocale());
		}

		return portletTitle;
	}

	private void _handleException(
			RenderRequest renderRequest, RenderResponse renderResponse,
			Exception exception)
		throws IOException, PortletException {

		SessionErrors.add(renderRequest, exception.getClass());

		include("/error.jsp", renderRequest, renderResponse);
	}

	private void _renderEditConfiguration(
			RenderRequest renderRequest, Portlet portlet)
		throws Exception {

		ConfigurationAction configurationAction = _getConfigurationAction(
			portlet);

		if (configurationAction != null) {
			renderRequest.setAttribute(
				WebKeys.CONFIGURATION_ACTION, configurationAction);
		}
		else if (_log.isDebugEnabled()) {
			_log.debug("Configuration action is null");
		}
	}

	private void _renderEditPublicParameters(
			RenderRequest renderRequest, Portlet portlet)
		throws Exception {

		ActionUtil.getLayoutPublicRenderParameters(renderRequest);

		ActionUtil.getPublicRenderParameterConfigurationList(
			renderRequest, portlet);
	}

	private void _updateFacebook(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		String facebookAPIKey = ParamUtil.getString(
			actionRequest, "facebookAPIKey");
		String facebookCanvasPageURL = ParamUtil.getString(
			actionRequest, "facebookCanvasPageURL");
		boolean facebookShowAddAppLink = ParamUtil.getBoolean(
			actionRequest, "facebookShowAddAppLink");

		portletPreferences.setValue("lfrFacebookApiKey", facebookAPIKey);
		portletPreferences.setValue(
			"lfrFacebookCanvasPageUrl", facebookCanvasPageURL);
		portletPreferences.setValue(
			"lfrFacebookShowAddAppLink",
			String.valueOf(facebookShowAddAppLink));
	}

	private void _updateFriends(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		boolean appShowShareWithFriendsLink = ParamUtil.getBoolean(
			actionRequest, "appShowShareWithFriendsLink");

		portletPreferences.setValue(
			"lfrAppShowShareWithFriendsLink",
			String.valueOf(appShowShareWithFriendsLink));
	}

	private void _updateGoogleGadget(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		boolean iGoogleShowAddAppLink = ParamUtil.getBoolean(
			actionRequest, "iGoogleShowAddAppLink");

		portletPreferences.setValue(
			"lfrIgoogleShowAddAppLink", String.valueOf(iGoogleShowAddAppLink));
	}

	private void _updateLayoutStatus(
			Layout layout, ServiceContext serviceContext, long userId)
		throws Exception {

		if (layout.isDraftLayout()) {
			_layoutLocalService.updateStatus(
				userId, layout.getPlid(), WorkflowConstants.STATUS_DRAFT,
				serviceContext);
		}
	}

	private void _updateNetvibes(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		boolean netvibesShowAddAppLink = ParamUtil.getBoolean(
			actionRequest, "netvibesShowAddAppLink");

		portletPreferences.setValue(
			"lfrNetvibesShowAddAppLink",
			String.valueOf(netvibesShowAddAppLink));
	}

	private void _updateScope(ActionRequest actionRequest) throws Exception {
		Portlet portlet = ActionUtil.getPortlet(actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletPreferences portletPreferences =
			themeDisplay.getStrictLayoutPortletSetup(
				themeDisplay.getLayout(), portlet.getPortletId());

		actionRequest = ActionUtil.getWrappedActionRequest(
			actionRequest, portletPreferences);

		String oldScopeName = _getOldScopeName(actionRequest);

		String[] scopes = StringUtil.split(
			ParamUtil.getString(actionRequest, "scope"));

		String scopeType = scopes[0];

		portletPreferences.setValue("lfrScopeType", scopeType);

		String scopeLayoutUuid = StringPool.BLANK;

		if ((scopes.length > 1) && scopeType.equals("layout")) {
			scopeLayoutUuid = scopes[1];
		}

		portletPreferences.setValue("lfrScopeLayoutUuid", scopeLayoutUuid);

		String portletTitle = _getPortletTitle(
			actionRequest, portlet, portletPreferences);

		Tuple newScopeTuple = _getNewScope(actionRequest);

		String newScopeName = (String)newScopeTuple.getObject(1);

		String newPortletTitle = _portal.getNewPortletTitle(
			portletTitle, oldScopeName, newScopeName);

		if (!newPortletTitle.equals(portletTitle)) {
			portletPreferences.setValue(
				"portletSetupTitle_" + themeDisplay.getLanguageId(),
				newPortletTitle);

			portletPreferences.setValue(
				"portletSetupUseCustomTitle", Boolean.TRUE.toString());
		}

		portletPreferences.store();

		PortletConfigurationListener portletConfigurationListener =
			portlet.getPortletConfigurationListenerInstance();

		if (portletConfigurationListener != null) {
			portletConfigurationListener.onUpdateScope(
				portlet.getPortletId(), portletPreferences);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletConfigurationPortlet.class);

	@Reference
	private ArchivedSettingsFactory _archivedSettingsFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private NPMResolver _npmResolver;

	@Reference
	private PermissionService _permissionService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	private final ThreadLocal<PortletRequest> _portletRequestThreadLocal =
		new CentralizedThreadLocal<>("_portletRequestThreadLocal");

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.portlet.configuration.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private ResourcePermissionService _resourcePermissionService;

	@Reference
	private RoleTypeContributorProvider _roleTypeContributorProvider;

	private ServiceTrackerMap<String, CTService<?>> _serviceTrackerMap;

	private class PortletConfigurationPortletPortletConfig
		extends LiferayPortletConfigWrapper {

		@Override
		public ResourceBundle getResourceBundle(Locale locale) {
			try {
				PortletRequest portletRequest =
					_portletRequestThreadLocal.get();

				long companyId = _portal.getCompanyId(portletRequest);

				String portletResource = ParamUtil.getString(
					portletRequest, "portletResource");

				Portlet portlet = _portletLocalService.getPortletById(
					companyId, portletResource);

				HttpServletRequest httpServletRequest =
					_portal.getHttpServletRequest(portletRequest);

				PortletConfig portletConfig = PortletConfigFactoryUtil.create(
					portlet, httpServletRequest.getServletContext());

				return new AggregateResourceBundle(
					super.getResourceBundle(locale),
					portletConfig.getResourceBundle(locale));
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			return super.getResourceBundle(locale);
		}

		private PortletConfigurationPortletPortletConfig(
			LiferayPortletConfig liferayPortletConfig) {

			super(liferayPortletConfig);
		}

	}

}