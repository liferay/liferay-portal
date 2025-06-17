/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.portlet.action;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.asset.kernel.exception.DuplicateQueryRuleException;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.constants.AssetPublisherWebKeys;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.publisher.util.AssetQueryRule;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherPortletInstanceConfiguration;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherSelectionStyleConfigurationUtil;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration;
import com.liferay.asset.publisher.web.internal.constants.AssetPublisherSelectionStyleConstants;
import com.liferay.asset.publisher.web.internal.display.context.AssetPublisherDisplayContext;
import com.liferay.asset.publisher.web.internal.helper.AssetPublisherWebHelper;
import com.liferay.asset.publisher.web.internal.util.AssetPublisherCustomizer;
import com.liferay.asset.publisher.web.internal.util.AssetPublisherCustomizerRegistry;
import com.liferay.asset.publisher.web.internal.util.FF_LPD_39304_CompanyTemporarySwapper;
import com.liferay.asset.util.AssetHelper;
import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutRevisionLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.context.RequestContextMapper;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrTokenizer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Juan Fernández
 */
@Component(
	configurationPid = "com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration",
	property = "jakarta.portlet.name=" + AssetPublisherPortletKeys.ASSET_PUBLISHER,
	service = ConfigurationAction.class
)
public class AssetPublisherConfigurationAction extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		String cmd = ParamUtil.getString(httpServletRequest, Constants.CMD);

		if (Objects.equals(cmd, "edit_query_rule")) {
			return "/edit_query_rule.jsp";
		}

		return "/configuration.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String portletResource = ParamUtil.getString(
			httpServletRequest, "portletResource");

		String rootPortletId = PortletIdCodec.decodePortletName(
			portletResource);

		AssetPublisherCustomizer assetPublisherCustomizer =
			_assetPublisherCustomizerRegistry.getAssetPublisherCustomizer(
				rootPortletId);

		RenderRequest renderRequest =
			(RenderRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);
		RenderResponse renderResponse =
			(RenderResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		AssetPublisherDisplayContext assetPublisherDisplayContext =
			new AssetPublisherDisplayContext(
				assetHelper, assetListAssetEntryProvider,
				assetListEntrySegmentsEntryRelLocalService,
				assetPublisherCustomizer, assetPublisherHelper,
				assetPublisherWebConfiguration, assetPublisherWebHelper,
				infoItemServiceRegistry, itemSelector, portal, renderRequest,
				renderResponse, renderRequest.getPreferences(),
				requestContextMapper, segmentsEntryRetriever);

		httpServletRequest.setAttribute(
			AssetPublisherWebKeys.ASSET_PUBLISHER_DISPLAY_CONTEXT,
			assetPublisherDisplayContext);

		httpServletRequest.setAttribute(
			AssetPublisherWebKeys.ASSET_PUBLISHER_HELPER, assetPublisherHelper);
		httpServletRequest.setAttribute(
			AssetPublisherWebKeys.ASSET_PUBLISHER_WEB_HELPER,
			assetPublisherWebHelper);
		httpServletRequest.setAttribute(
			AssetPublisherWebKeys.ITEM_SELECTOR, itemSelector);

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Override
	public void postProcess(
			long companyId, PortletRequest portletRequest,
			PortletPreferences portletPreferences)
		throws PortalException {

		super.postProcess(companyId, portletRequest, portletPreferences);

		AssetPublisherPortletInstanceConfiguration
			assetPublisherPortletInstanceConfiguration =
				configurationProvider.getSystemConfiguration(
					AssetPublisherPortletInstanceConfiguration.class);

		String languageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());
		LocalizedValuesMap emailAssetEntryAddedBodyMap =
			assetPublisherPortletInstanceConfiguration.
				emailAssetEntryAddedBody();

		removeDefaultValue(
			portletRequest, portletPreferences,
			"emailAssetEntryAddedBody_" + languageId,
			emailAssetEntryAddedBodyMap.get(LocaleUtil.getSiteDefault()));

		LocalizedValuesMap emailAssetEntryAddedSubjectMap =
			assetPublisherPortletInstanceConfiguration.
				emailAssetEntryAddedSubject();

		removeDefaultValue(
			portletRequest, portletPreferences,
			"emailAssetEntryAddedSubject_" + languageId,
			emailAssetEntryAddedSubjectMap.get(LocaleUtil.getSiteDefault()));
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		PortletPreferences portletPreferences = actionRequest.getPreferences();

		if (cmd.equals(Constants.TRANSLATE)) {
			super.processAction(portletConfig, actionRequest, actionResponse);
		}
		else if (cmd.equals(Constants.UPDATE)) {
			try {
				AssetPublisherPortletInstanceConfiguration
					assetPublisherPortletInstanceConfiguration =
						_getAssetPublisherPortletInstanceConfiguration(
							portal.getHttpServletRequest(actionRequest));

				boolean emailAssetEntryAddedEnabled = GetterUtil.getBoolean(
					getParameter(actionRequest, "emailAssetEntryAddedEnabled"),
					assetPublisherPortletInstanceConfiguration.
						emailAssetEntryAddedEnabled());

				if (emailAssetEntryAddedEnabled) {
					validateEmail(actionRequest, "emailAssetEntryAdded");
					validateEmailFrom(actionRequest);
				}

				_updateSelectionStyle(actionRequest);

				_updateDisplaySettings(actionRequest);

				String selectionStyle = getParameter(
					actionRequest, "selectionStyle");

				if (Validator.isNull(selectionStyle)) {
					ThemeDisplay themeDisplay =
						(ThemeDisplay)actionRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					try (SafeCloseable safeCloseable =
							FF_LPD_39304_CompanyTemporarySwapper.
								setCompanyIdWithSafeCloseable(
									themeDisplay.getCompanyId())) {

						selectionStyle = getDefaultSelectionStyle();
					}
				}

				if (selectionStyle.equals(
						AssetPublisherSelectionStyleConstants.
							TYPE_ASSET_LIST)) {

					updateAssetListEntryPreferences(
						actionRequest, portletPreferences);
				}
				else if (selectionStyle.equals(
							AssetPublisherSelectionStyleConstants.
								TYPE_DYNAMIC)) {

					_updateQueryLogic(actionRequest, portletPreferences);
				}

				_updateDefaultAssetPublisher(actionRequest);

				super.processAction(
					portletConfig, actionRequest, actionResponse);
			}
			catch (Exception exception) {
				if (exception instanceof AssetTagException ||
					exception instanceof DuplicateQueryRuleException) {

					SessionErrors.add(
						actionRequest, exception.getClass(), exception);
				}
				else {
					throw exception;
				}
			}
		}
		else {
			if (cmd.equals("add-scope")) {
				_addScope(actionRequest, portletPreferences);
			}
			else if (cmd.equals("add-selection")) {
				_addSelection(actionRequest, portletPreferences);
			}
			else if (cmd.equals("move-selection-down")) {
				_moveSelectionDown(actionRequest, portletPreferences);
			}
			else if (cmd.equals("move-selection-up")) {
				_moveSelectionUp(actionRequest, portletPreferences);
			}
			else if (cmd.equals("remove-selection")) {
				_removeSelection(actionRequest, portletPreferences);
			}
			else if (cmd.equals("remove-scope")) {
				_removeScope(actionRequest, portletPreferences);
			}
			else if (cmd.equals("select-scope")) {
				_setScopes(actionRequest, portletPreferences);
			}
			else if (cmd.equals("selection-style")) {
				_setSelectionStyle(actionRequest, portletPreferences);
			}

			if (SessionErrors.isEmpty(actionRequest)) {
				portletPreferences.store();

				String portletResource = ParamUtil.getString(
					actionRequest, "portletResource");

				SessionMessages.add(
					actionRequest,
					portal.getPortletId(actionRequest) +
						SessionMessages.KEY_SUFFIX_REFRESH_PORTLET,
					portletResource);

				SessionMessages.add(
					actionRequest,
					portal.getPortletId(actionRequest) +
						SessionMessages.KEY_SUFFIX_UPDATED_CONFIGURATION);
			}

			String redirect = portal.escapeRedirect(
				ParamUtil.getString(actionRequest, "redirect"));

			if (Validator.isNotNull(redirect)) {
				actionResponse.sendRedirect(redirect);
			}
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		assetPublisherWebConfiguration = ConfigurableUtil.createConfigurable(
			AssetPublisherWebConfiguration.class, properties);

		_assetPublisherCustomizerRegistry =
			new AssetPublisherCustomizerRegistry(
				assetPublisherHelper, assetPublisherWebConfiguration);
	}

	protected String getDefaultSelectionStyle() {
		return AssetPublisherSelectionStyleConfigurationUtil.
			defaultSelectionStyle();
	}

	protected void updateAssetListEntryPreferences(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		AssetListEntry assetListEntry =
			assetListEntryLocalService.fetchAssetListEntry(
				GetterUtil.getLong(
					getParameter(actionRequest, "assetListEntryId")));

		if (assetListEntry == null) {
			portletPreferences.reset("assetListEntryExternalReferenceCode");
			portletPreferences.reset(
				"assetListEntryGroupExternalReferenceCode");

			return;
		}

		setPreference(
			actionRequest, "assetListEntryExternalReferenceCode",
			assetListEntry.getExternalReferenceCode());

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (assetListEntry.getGroupId() == themeDisplay.getScopeGroupId()) {
			portletPreferences.reset(
				"assetListEntryGroupExternalReferenceCode");
		}
		else {
			Group group = groupLocalService.getGroup(
				assetListEntry.getGroupId());

			setPreference(
				actionRequest, "assetListEntryGroupExternalReferenceCode",
				group.getExternalReferenceCode());
		}
	}

	@Reference
	protected AssetHelper assetHelper;

	@Reference
	protected AssetListAssetEntryProvider assetListAssetEntryProvider;

	@Reference
	protected AssetListEntryLocalService assetListEntryLocalService;

	@Reference
	protected AssetListEntrySegmentsEntryRelLocalService
		assetListEntrySegmentsEntryRelLocalService;

	@Reference
	protected AssetPublisherHelper assetPublisherHelper;

	protected volatile AssetPublisherWebConfiguration
		assetPublisherWebConfiguration;

	@Reference
	protected AssetPublisherWebHelper assetPublisherWebHelper;

	@Reference
	protected AssetTagLocalService assetTagLocalService;

	@Reference
	protected ConfigurationProvider configurationProvider;

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected InfoItemServiceRegistry infoItemServiceRegistry;

	@Reference
	protected ItemSelector itemSelector;

	@Reference
	protected LayoutLocalService layoutLocalService;

	@Reference
	protected LayoutRevisionLocalService layoutRevisionLocalService;

	@Reference
	protected Portal portal;

	@Reference
	protected RequestContextMapper requestContextMapper;

	@Reference
	protected SegmentsEntryRetriever segmentsEntryRetriever;

	@Reference
	protected Staging staging;

	private void _addScope(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String[] scopeIds = portletPreferences.getValues(
			"scopeIds",
			new String[] {
				AssetPublisherHelper.SCOPE_ID_GROUP_PREFIX +
					GroupConstants.DEFAULT
			});

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		Group selectedGroup = groupLocalService.fetchGroup(groupId);

		String scopeId = assetPublisherHelper.getScopeId(
			selectedGroup, themeDisplay.getScopeGroupId());

		_checkPermission(actionRequest, scopeId);

		if (!ArrayUtil.contains(scopeIds, scopeId)) {
			scopeIds = ArrayUtil.append(scopeIds, scopeId);
		}

		portletPreferences.setValues("scopeIds", scopeIds);
	}

	private void _addSelection(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		long[] assetEntryIds = ParamUtil.getLongValues(
			actionRequest, "assetEntryIds");
		int assetEntryOrder = ParamUtil.getInteger(
			actionRequest, "assetEntryOrder");
		String assetEntryType = ParamUtil.getString(
			actionRequest, "assetEntryType");

		for (long assetEntryId : assetEntryIds) {
			assetPublisherWebHelper.addSelection(
				portletPreferences, assetEntryId, assetEntryOrder,
				assetEntryType);
		}
	}

	private void _checkPermission(ActionRequest actionRequest, String scopeId)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!assetPublisherWebHelper.isScopeIdSelectable(
				themeDisplay.getPermissionChecker(), scopeId,
				themeDisplay.getCompanyGroupId(), themeDisplay.getLayout(),
				true)) {

			throw new PrincipalException();
		}
	}

	private String _getAssetClassName(
		ActionRequest actionRequest, String[] classNameIds) {

		String anyAssetTypeString = getParameter(actionRequest, "anyAssetType");

		boolean anyAssetType = GetterUtil.getBoolean(anyAssetTypeString);

		if (anyAssetType) {
			return null;
		}

		long defaultAssetTypeId = GetterUtil.getLong(anyAssetTypeString);

		if ((defaultAssetTypeId == 0) && (classNameIds.length == 1)) {
			defaultAssetTypeId = GetterUtil.getLong(classNameIds[0]);
		}

		if (defaultAssetTypeId <= 0) {
			return null;
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				portal.getClassName(defaultAssetTypeId));

		return assetPublisherWebHelper.getClassName(assetRendererFactory);
	}

	private AssetPublisherPortletInstanceConfiguration
			_getAssetPublisherPortletInstanceConfiguration(
				HttpServletRequest httpServletRequest)
		throws ConfigurationException {

		return configurationProvider.getPortletInstanceConfiguration(
			AssetPublisherPortletInstanceConfiguration.class,
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY));
	}

	private String[] _getClassTypeIds(
		ActionRequest actionRequest, String[] classNameIds) {

		String assetClassName = _getAssetClassName(actionRequest, classNameIds);

		if (assetClassName == null) {
			return null;
		}

		String anyAssetClassTypeString = getParameter(
			actionRequest, "anyClassType" + assetClassName);

		boolean anyAssetClassType = GetterUtil.getBoolean(
			anyAssetClassTypeString);

		if (anyAssetClassType) {
			return null;
		}

		long defaultAssetClassTypeId = GetterUtil.getLong(
			anyAssetClassTypeString, -1);

		if (defaultAssetClassTypeId > -1) {
			return new String[] {String.valueOf(defaultAssetClassTypeId)};
		}

		return StringUtil.split(
			getParameter(actionRequest, "classTypeIds" + assetClassName));
	}

	private AssetQueryRule _getQueryRule(
		ActionRequest actionRequest, int index) {

		boolean contains = ParamUtil.getBoolean(
			actionRequest, "queryContains" + index);
		boolean andOperator = ParamUtil.getBoolean(
			actionRequest, "queryAndOperator" + index);

		String name = ParamUtil.getString(actionRequest, "queryName" + index);

		String[] values = null;

		if (name.equals("assetTags")) {
			values = ParamUtil.getStringValues(
				actionRequest, "queryTagNames" + index);
		}
		else if (name.equals("keywords")) {
			StrTokenizer strTokenizer = new StrTokenizer(
				ParamUtil.getString(actionRequest, "keywords" + index));

			strTokenizer.setQuoteMatcher(StrMatcher.quoteMatcher());

			List<String> tokens = (List<String>)strTokenizer.getTokenList();

			values = tokens.toArray(new String[0]);
		}
		else {
			values = ParamUtil.getStringValues(
				actionRequest, "queryCategoryIds" + index);
		}

		return new AssetQueryRule(contains, andOperator, name, values);
	}

	private boolean _isSubtypeFieldsFilterEnabled(
		ActionRequest actionRequest, String[] classNameIds) {

		String assetClassName = _getAssetClassName(actionRequest, classNameIds);

		if (assetClassName == null) {
			return false;
		}

		return GetterUtil.getBoolean(
			getParameter(
				actionRequest, "subtypeFieldsFilterEnabled" + assetClassName));
	}

	private void _moveSelectionDown(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		int assetEntryOrder = ParamUtil.getInteger(
			actionRequest, "assetEntryOrder");

		String[] manualEntries = portletPreferences.getValues(
			"assetEntryXml", new String[0]);

		if ((assetEntryOrder >= (manualEntries.length - 1)) ||
			(assetEntryOrder < 0)) {

			return;
		}

		String temp = manualEntries[assetEntryOrder + 1];

		manualEntries[assetEntryOrder + 1] = manualEntries[assetEntryOrder];
		manualEntries[assetEntryOrder] = temp;

		portletPreferences.setValues("assetEntryXml", manualEntries);
	}

	private void _moveSelectionUp(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		int assetEntryOrder = ParamUtil.getInteger(
			actionRequest, "assetEntryOrder");

		String[] manualEntries = portletPreferences.getValues(
			"assetEntryXml", new String[0]);

		if ((assetEntryOrder >= manualEntries.length) ||
			(assetEntryOrder <= 0)) {

			return;
		}

		String temp = manualEntries[assetEntryOrder - 1];

		manualEntries[assetEntryOrder - 1] = manualEntries[assetEntryOrder];
		manualEntries[assetEntryOrder] = temp;

		portletPreferences.setValues("assetEntryXml", manualEntries);
	}

	private void _removeScope(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		String[] scopeIds = portletPreferences.getValues(
			"scopeIds",
			new String[] {
				AssetPublisherHelper.SCOPE_ID_GROUP_PREFIX +
					GroupConstants.DEFAULT
			});

		String scopeId = ParamUtil.getString(actionRequest, "scopeId");

		scopeIds = ArrayUtil.remove(scopeIds, scopeId);

		if (scopeId.startsWith(
				AssetPublisherHelper.SCOPE_ID_PARENT_GROUP_PREFIX)) {

			scopeId = scopeId.substring("Parent".length());

			scopeIds = ArrayUtil.remove(scopeIds, scopeId);
		}

		portletPreferences.setValues("scopeIds", scopeIds);
	}

	private void _removeSelection(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		int assetEntryOrder = ParamUtil.getInteger(
			actionRequest, "assetEntryOrder");

		String[] manualEntries = portletPreferences.getValues(
			"assetEntryXml", new String[0]);

		if (assetEntryOrder >= manualEntries.length) {
			return;
		}

		String[] newEntries = new String[manualEntries.length - 1];

		int i = 0;
		int j = 0;

		for (; i < manualEntries.length; i++) {
			if (i != assetEntryOrder) {
				newEntries[j++] = manualEntries[i];
			}
		}

		portletPreferences.setValues("assetEntryXml", newEntries);
	}

	private void _setScopes(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		String[] scopeIds = StringUtil.split(
			getParameter(actionRequest, "scopeIds"));

		portletPreferences.setValues("scopeIds", scopeIds);
	}

	private void _setSelectionStyle(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		String selectionStyle = getParameter(actionRequest, "selectionStyle");
		String displayStyle = getParameter(actionRequest, "displayStyle");

		portletPreferences.setValue("selectionStyle", selectionStyle);

		if (selectionStyle.equals(
				AssetPublisherSelectionStyleConstants.TYPE_MANUAL) ||
			selectionStyle.equals("view-count")) {

			portletPreferences.setValue("enableRss", Boolean.FALSE.toString());
			portletPreferences.setValue(
				"showQueryLogic", Boolean.FALSE.toString());

			portletPreferences.reset("rssDelta");
			portletPreferences.reset("rssDisplayStyle");
			portletPreferences.reset("rssFormat");
			portletPreferences.reset("rssName");
		}

		if (!selectionStyle.equals("view-count") &&
			displayStyle.equals("view-count-details")) {

			portletPreferences.setValue("displayStyle", "full-content");
		}
	}

	private void _updateDefaultAssetPublisher(ActionRequest actionRequest)
		throws Exception {

		boolean defaultAssetPublisher = ParamUtil.getBoolean(
			actionRequest, "defaultAssetPublisher");

		Layout layout = (Layout)actionRequest.getAttribute(WebKeys.LAYOUT);

		String portletResource = ParamUtil.getString(
			actionRequest, "portletResource");

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		if (defaultAssetPublisher) {
			typeSettingsUnicodeProperties.setProperty(
				LayoutTypePortletConstants.DEFAULT_ASSET_PUBLISHER_PORTLET_ID,
				portletResource);
		}
		else {
			String defaultAssetPublisherPortletId =
				typeSettingsUnicodeProperties.getProperty(
					LayoutTypePortletConstants.
						DEFAULT_ASSET_PUBLISHER_PORTLET_ID);

			if (Validator.isNotNull(defaultAssetPublisherPortletId) &&
				defaultAssetPublisherPortletId.equals(portletResource)) {

				typeSettingsUnicodeProperties.setProperty(
					LayoutTypePortletConstants.
						DEFAULT_ASSET_PUBLISHER_PORTLET_ID,
					StringPool.BLANK);
			}
		}

		layout = layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());

		if (layout.isSupportsEmbeddedPortlets() &&
			layout.isPortletEmbedded(portletResource, layout.getGroupId())) {

			return;
		}

		if (LayoutStagingUtil.isBranchingLayout(layout)) {
			HttpServletRequest httpServletRequest =
				portal.getHttpServletRequest(actionRequest);

			LayoutSetBranch layoutSetBranch =
				LayoutStagingUtil.getLayoutSetBranch(layout.getLayoutSet());

			long layoutRevisionId = staging.getRecentLayoutRevisionId(
				httpServletRequest, layoutSetBranch.getLayoutSetBranchId(),
				layout.getPlid());

			LayoutRevision layoutRevision =
				layoutRevisionLocalService.getLayoutRevision(layoutRevisionId);

			if (layoutRevision != null) {
				PortletPreferencesImpl portletPreferencesImpl =
					(PortletPreferencesImpl)actionRequest.getPreferences();

				portletPreferencesImpl.setPlid(
					layoutRevision.getLayoutRevisionId());
			}
		}
	}

	private void _updateDisplaySettings(ActionRequest actionRequest) {
		String[] classNameIds = StringUtil.split(
			getParameter(actionRequest, "classNameIds"));

		String[] classTypeIds = _getClassTypeIds(actionRequest, classNameIds);

		String[] extensions = actionRequest.getParameterValues("extensions");

		if (ArrayUtil.isNotEmpty(extensions) && (extensions.length == 1) &&
			extensions[0].equals(Boolean.FALSE.toString())) {

			extensions = new String[0];
		}

		boolean subtypeFieldsFilterEnabled = _isSubtypeFieldsFilterEnabled(
			actionRequest, classNameIds);

		setPreference(actionRequest, "classNameIds", classNameIds);
		setPreference(actionRequest, "classTypeIds", classTypeIds);
		setPreference(actionRequest, "extensions", extensions);
		setPreference(
			actionRequest, "subtypeFieldsFilterEnabled",
			String.valueOf(subtypeFieldsFilterEnabled));
	}

	private void _updateQueryLogic(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long userId = themeDisplay.getUserId();
		long groupId = themeDisplay.getSiteGroupId();

		int[] queryRulesIndexes = StringUtil.split(
			ParamUtil.getString(actionRequest, "queryLogicIndexes"), 0);

		int i = 0;

		List<AssetQueryRule> assetQueryRules = new ArrayList<>();

		for (int queryRulesIndex : queryRulesIndexes) {
			AssetQueryRule queryRule = _getQueryRule(
				actionRequest, queryRulesIndex);

			_validateQueryRule(userId, groupId, assetQueryRules, queryRule);

			assetQueryRules.add(queryRule);

			setPreference(
				actionRequest, "queryContains" + i,
				String.valueOf(queryRule.isContains()));
			setPreference(
				actionRequest, "queryAndOperator" + i,
				String.valueOf(queryRule.isAndOperator()));
			setPreference(actionRequest, "queryName" + i, queryRule.getName());
			setPreference(
				actionRequest, "queryValues" + i, queryRule.getValues());

			i++;
		}

		// Clear previous preferences that are now blank

		String[] values = portletPreferences.getValues(
			"queryValues" + i, new String[0]);

		while (values.length > 0) {
			setPreference(actionRequest, "queryContains" + i, StringPool.BLANK);
			setPreference(
				actionRequest, "queryAndOperator" + i, StringPool.BLANK);
			setPreference(actionRequest, "queryName" + i, StringPool.BLANK);
			setPreference(actionRequest, "queryValues" + i, new String[0]);

			i++;

			values = portletPreferences.getValues(
				"queryValues" + i, new String[0]);
		}
	}

	private void _updateSelectionStyle(ActionRequest actionRequest) {
		String selectionStyle = getParameter(actionRequest, "selectionStyle");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try (SafeCloseable safeCloseable =
				FF_LPD_39304_CompanyTemporarySwapper.
					setCompanyIdWithSafeCloseable(
						themeDisplay.getCompanyId())) {

			if (Validator.isNull(selectionStyle)) {
				setPreference(
					actionRequest, "selectionStyle",
					getDefaultSelectionStyle());
			}
		}
	}

	private void _validateQueryRule(
			long userId, long groupId, List<AssetQueryRule> assetQueryRules,
			AssetQueryRule queryRule)
		throws Exception {

		String name = queryRule.getName();

		if (name.equals("assetTags")) {
			assetTagLocalService.checkTags(
				userId, groupId, queryRule.getValues());
		}

		if (assetQueryRules.contains(queryRule)) {
			throw new DuplicateQueryRuleException(
				queryRule.isContains(), queryRule.isAndOperator(),
				queryRule.getName());
		}
	}

	private volatile AssetPublisherCustomizerRegistry
		_assetPublisherCustomizerRegistry;

}