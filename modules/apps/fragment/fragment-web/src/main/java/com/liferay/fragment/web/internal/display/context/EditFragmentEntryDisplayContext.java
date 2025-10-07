/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.fragment.service.FragmentCollectionServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.fragment.web.internal.info.field.type.CaptchaInfoFieldType;
import com.liferay.fragment.web.internal.info.field.type.FormButtonInfoFieldType;
import com.liferay.fragment.web.internal.info.field.type.LocalizationSelectInfoFieldType;
import com.liferay.fragment.web.internal.info.field.type.StepperInfoFieldType;
import com.liferay.info.field.type.BooleanInfoFieldType;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.DateTimeInfoFieldType;
import com.liferay.info.field.type.FileInfoFieldType;
import com.liferay.info.field.type.FriendlyURLInfoFieldType;
import com.liferay.info.field.type.HTMLInfoFieldType;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.field.type.LongTextInfoFieldType;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.field.type.NumberInfoFieldType;
import com.liferay.info.field.type.RelationshipInfoFieldType;
import com.liferay.info.field.type.SelectInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.learn.LearnMessage;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateManager;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jürgen Kappler
 */
public class EditFragmentEntryDisplayContext {

	public EditFragmentEntryDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;

		_fragmentCollectionContributorRegistry =
			(FragmentCollectionContributorRegistry)
				httpServletRequest.getAttribute(
					FragmentCollectionContributorRegistry.class.getName());
		_fragmentEntryProcessorRegistry =
			(FragmentEntryProcessorRegistry)httpServletRequest.getAttribute(
				FragmentEntryProcessorRegistry.class.getName());
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_setViewAttributes();
	}

	public long getFragmentCollectionId() {
		if (Validator.isNotNull(_fragmentCollectionId)) {
			return _fragmentCollectionId;
		}

		long defaultFragmentCollectionId = 0;

		List<FragmentCollection> fragmentCollections =
			FragmentCollectionLocalServiceUtil.getFragmentCollections(
				_themeDisplay.getScopeGroupId(), 0, 1);

		if (ListUtil.isNotEmpty(fragmentCollections)) {
			FragmentCollection fragmentCollection = fragmentCollections.get(0);

			defaultFragmentCollectionId =
				fragmentCollection.getFragmentCollectionId();
		}

		_fragmentCollectionId = ParamUtil.getLong(
			_httpServletRequest, "fragmentCollectionId",
			defaultFragmentCollectionId);

		return _fragmentCollectionId;
	}

	public Map<String, Object> getFragmentEditorData() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"context",
			Collections.singletonMap(
				"namespace", _renderResponse.getNamespace())
		).put(
			"props", _getProps()
		).build();
	}

	public FragmentEntry getFragmentEntry() {
		if (_fragmentEntry != null) {
			return _fragmentEntry;
		}

		FragmentEntry fragmentEntry =
			FragmentEntryLocalServiceUtil.fetchFragmentEntry(
				getFragmentEntryId());

		FragmentCollection fragmentCollection =
			FragmentCollectionLocalServiceUtil.fetchFragmentCollection(
				getFragmentCollectionId());

		if ((fragmentEntry == null) && (fragmentCollection != null)) {
			fragmentEntry = FragmentEntryLocalServiceUtil.fetchFragmentEntry(
				fragmentCollection.getGroupId(), getFragmentEntryKey());
		}

		if (fragmentEntry == null) {
			fragmentEntry =
				_fragmentCollectionContributorRegistry.getFragmentEntry(
					getFragmentEntryKey());
		}

		_fragmentEntry = fragmentEntry;

		return _fragmentEntry;
	}

	public long getFragmentEntryId() {
		if (Validator.isNotNull(_fragmentEntryId)) {
			return _fragmentEntryId;
		}

		long fragmentEntryId = ParamUtil.getLong(
			_httpServletRequest, "fragmentEntryId");

		FragmentEntry draftFragmentEntry =
			FragmentEntryLocalServiceUtil.fetchDraft(fragmentEntryId);

		if (draftFragmentEntry == null) {
			_fragmentEntryId = fragmentEntryId;
		}
		else {
			_fragmentEntryId = draftFragmentEntry.getFragmentEntryId();
		}

		return _fragmentEntryId;
	}

	public String getFragmentEntryKey() {
		if (Validator.isNotNull(_fragmentEntryKey)) {
			return _fragmentEntryKey;
		}

		_fragmentEntryKey = ParamUtil.getString(
			_httpServletRequest, "fragmentEntryKey");

		return _fragmentEntryKey;
	}

	public String getFragmentEntryTitle() {
		FragmentEntry fragmentEntry = getFragmentEntry();

		if (fragmentEntry == null) {
			return LanguageUtil.get(_httpServletRequest, "add-fragment");
		}

		return fragmentEntry.getName();
	}

	public String getName() {
		if (Validator.isNotNull(_name)) {
			return _name;
		}

		_name = ParamUtil.getString(_httpServletRequest, "name");

		FragmentEntry fragmentEntry = getFragmentEntry();

		if ((fragmentEntry != null) && Validator.isNull(_name)) {
			_name = fragmentEntry.getName();
		}

		return _name;
	}

	public String getRedirect() {
		String redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			return redirect;
		}

		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setParameter(
			"fragmentCollectionId",
			() -> {
				if (getFragmentCollectionId() > 0) {
					return getFragmentCollectionId();
				}

				return null;
			}
		).buildString();
	}

	private String _getConfigurationContent() {
		if (Validator.isNotNull(_configurationContent)) {
			return _configurationContent;
		}

		_configurationContent = ParamUtil.getString(
			_httpServletRequest, "configurationContent");

		FragmentEntry fragmentEntry = getFragmentEntry();

		if ((fragmentEntry != null) &&
			Validator.isNull(_configurationContent)) {

			_configurationContent = fragmentEntry.getConfiguration();

			if (Validator.isNull(_configurationContent)) {
				_configurationContent = "{\n\t\"fieldSets\": [\n\t]\n}";
			}
		}

		return _configurationContent;
	}

	private String _getCssContent() {
		if (Validator.isNotNull(_cssContent)) {
			return _cssContent;
		}

		FragmentEntry fragmentEntry = getFragmentEntry();

		if (fragmentEntry != null) {
			_cssContent = fragmentEntry.getCss();
		}

		return _cssContent;
	}

	private JSONArray _getFieldTypesJSONArray() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		FragmentEntry fragmentEntry = getFragmentEntry();

		if ((fragmentEntry == null) || !fragmentEntry.isTypeInput()) {
			return jsonArray;
		}

		for (InfoFieldType infoFieldType : _INFO_FIELD_TYPES) {
			jsonArray.put(
				JSONUtil.put(
					"key", infoFieldType.getName()
				).put(
					"label", infoFieldType.getLabel(_themeDisplay.getLocale())
				));
		}

		return jsonArray;
	}

	private String _getFragmentEntryRenderURL(String mvcRenderCommandName)
		throws Exception {

		FragmentEntry fragmentEntry = getFragmentEntry();

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				_httpServletRequest, FragmentPortletKeys.FRAGMENT,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			mvcRenderCommandName
		).setParameter(
			"fragmentEntryId", fragmentEntry.getFragmentEntryId()
		).setParameter(
			"fragmentEntryKey", fragmentEntry.getFragmentEntryKey()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private String _getHtmlContent() {
		if (Validator.isNotNull(_htmlContent)) {
			return _htmlContent;
		}

		FragmentEntry fragmentEntry = getFragmentEntry();

		if (fragmentEntry != null) {
			_htmlContent = fragmentEntry.getHtml();
		}

		return _htmlContent;
	}

	private JSONArray _getInitialFieldTypesJSONArray() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		FragmentEntry fragmentEntry = getFragmentEntry();

		if ((fragmentEntry == null) || !fragmentEntry.isTypeInput()) {
			return jsonArray;
		}

		JSONArray fieldTypesJSONArray = JSONFactoryUtil.createJSONArray();

		try {
			JSONObject typeOptionsJSONObject = JSONFactoryUtil.createJSONObject(
				fragmentEntry.getTypeOptions());

			fieldTypesJSONArray = typeOptionsJSONObject.getJSONArray(
				"fieldTypes");
		}
		catch (JSONException jsonException) {
			_log.error(jsonException);
		}

		if ((fieldTypesJSONArray == null) ||
			(fieldTypesJSONArray.length() <= 0)) {

			return jsonArray;
		}

		for (InfoFieldType infoFieldType : _INFO_FIELD_TYPES) {
			if (!JSONUtil.hasValue(
					fieldTypesJSONArray, infoFieldType.getName())) {

				continue;
			}

			jsonArray.put(infoFieldType.getName());
		}

		return jsonArray;
	}

	private String _getJsContent() {
		if (Validator.isNotNull(_jsContent)) {
			return _jsContent;
		}

		FragmentEntry fragmentEntry = getFragmentEntry();

		if (fragmentEntry != null) {
			_jsContent = fragmentEntry.getJs();
		}

		return _jsContent;
	}

	private Map<String, Object> _getProps() throws Exception {
		TemplateManager templateManager =
			TemplateManagerUtil.getTemplateManager(
				TemplateConstants.LANG_TYPE_FTL);

		Template template = templateManager.getTemplate(
			new StringTemplateResource(
				TemplateConstants.LANG_TYPE_FTL,
				TemplateConstants.LANG_TYPE_FTL),
			true);

		template.prepare(_httpServletRequest);

		Set<String> originalKeys = new HashSet<>(template.keySet());

		template.prepareTaglib(
			_httpServletRequest,
			PortalUtil.getHttpServletResponse(_renderResponse));

		Set<String> taglibKeys = new HashSet<>(template.keySet());

		taglibKeys.removeAll(originalKeys);

		List<String> freeMarkerTaglibs = new ArrayList<>(taglibKeys);

		List<String> freeMarkerVariables = new ArrayList<>(template.keySet());

		freeMarkerVariables.add("configuration");
		freeMarkerVariables.add("fragmentElementId");
		freeMarkerVariables.add("fragmentEntryLinkNamespace");
		freeMarkerVariables.add("fragmentName");
		freeMarkerVariables.add("layoutMode");

		FragmentCollection fragmentCollection =
			FragmentCollectionServiceUtil.fetchFragmentCollection(
				getFragmentCollectionId());

		List<String> resources = _getResources(fragmentCollection);

		return HashMapBuilder.<String, Object>put(
			"allowedStatus",
			HashMapBuilder.<String, Object>put(
				"approved", String.valueOf(WorkflowConstants.STATUS_APPROVED)
			).put(
				"draft", String.valueOf(WorkflowConstants.STATUS_DRAFT)
			).build()
		).put(
			"autocompleteTags",
			_fragmentEntryProcessorRegistry.getAvailableTagsJSONArray()
		).put(
			"dataAttributes",
			_fragmentEntryProcessorRegistry.getDataAttributesJSONArray()
		).put(
			"fieldTypes", _getFieldTypesJSONArray()
		).put(
			"fragmentCollectionId", getFragmentCollectionId()
		).put(
			"fragmentConfigurationURL",
			PortletURLBuilder.createRenderURL(
				PortalUtil.getLiferayPortletResponse(_renderResponse)
			).setMVCPath(
				"/configuration/icon/configuration.jsp"
			).setRedirect(
				_themeDisplay.getURLCurrent()
			).setBackURL(
				_themeDisplay.getURLCurrent()
			).buildString()
		).put(
			"fragmentEntryId", getFragmentEntryId()
		).put(
			"freeMarkerTaglibs", freeMarkerTaglibs
		).put(
			"freeMarkerVariables", freeMarkerVariables
		).put(
			"htmlEditorCustomEntities",
			ListUtil.fromArray(
				HashMapBuilder.<String, Object>put(
					"content", freeMarkerTaglibs
				).put(
					"end", "]"
				).put(
					"start", "[@"
				).build(),
				HashMapBuilder.<String, Object>put(
					"content", freeMarkerVariables
				).put(
					"end", "}"
				).put(
					"start", "${"
				).build(),
				HashMapBuilder.<String, Object>put(
					"content", resources
				).put(
					"end", "]"
				).put(
					"start", "[resources:"
				).build())
		).put(
			"initialConfiguration", _getConfigurationContent()
		).put(
			"initialCSS", _getCssContent()
		).put(
			"initialFieldTypes", _getInitialFieldTypesJSONArray()
		).put(
			"initialHTML", _getHtmlContent()
		).put(
			"initialJS", _getJsContent()
		).put(
			"learnMessageHTML",
			() -> {
				LearnMessage learnMessage = LearnMessageUtil.getLearnMessage(
					"deprecated-embedded-widgets",
					_themeDisplay.getLanguageId(), "fragment-web");

				return learnMessage.getHTML();
			}
		).put(
			"name", getName()
		).put(
			"portletNamespace", _renderResponse.getNamespace()
		).put(
			"propagationEnabled",
			() -> {
				FragmentServiceConfiguration fragmentServiceConfiguration =
					ConfigurationProviderUtil.getCompanyConfiguration(
						FragmentServiceConfiguration.class,
						_themeDisplay.getCompanyId());

				return fragmentServiceConfiguration.propagateChanges();
			}
		).put(
			"readOnly", _isReadOnlyFragmentEntry()
		).put(
			"resources", resources
		).put(
			"showFieldTypes", _showFieldTypes()
		).put(
			"spritemap", _themeDisplay.getPathThemeSpritemap()
		).put(
			"status",
			() -> {
				FragmentEntry fragmentEntry = getFragmentEntry();

				return String.valueOf(fragmentEntry.getStatus());
			}
		).put(
			"urls",
			HashMapBuilder.<String, Object>put(
				"current", _themeDisplay.getURLCurrent()
			).put(
				"edit",
				() -> PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/fragment/edit_fragment_entry"
				).buildString()
			).put(
				"preview",
				_getFragmentEntryRenderURL("/fragment/preview_fragment_entry")
			).put(
				"publish", _getPublishFragmentEntryActionURL()
			).put(
				"redirect", getRedirect()
			).put(
				"render",
				() -> {
					FragmentEntry fragmentEntry = getFragmentEntry();

					return HttpComponentsUtil.addParameters(
						_themeDisplay.getPathMain() +
							"/portal/fragment/render_fragment_entry",
						"p_l_mode", Constants.PREVIEW, "groupId",
						_themeDisplay.getScopeGroupId(), "fragmentEntryId",
						fragmentEntry.getFragmentEntryId(), "fragmentEntryKey",
						fragmentEntry.getFragmentEntryKey());
				}
			).build()
		).build();
	}

	private String _getPublishFragmentEntryActionURL() {
		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				_httpServletRequest, FragmentPortletKeys.FRAGMENT,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/fragment/publish_fragment_entry"
		).setParameter(
			"fragmentEntryId", getFragmentEntryId()
		).buildString();
	}

	private List<String> _getResources(FragmentCollection fragmentCollection)
		throws Exception {

		if (fragmentCollection == null) {
			return new ArrayList<>();
		}

		Map<String, FileEntry> resourcesMap =
			fragmentCollection.getResourcesMap();

		return new ArrayList<>(resourcesMap.keySet());
	}

	private boolean _isReadOnlyFragmentEntry() {
		if (_readOnly != null) {
			return _readOnly;
		}

		FragmentEntry fragmentEntry = getFragmentEntry();

		if (fragmentEntry.isReadOnly()) {
			_readOnly = true;

			return _readOnly;
		}

		boolean readOnly = false;

		FragmentCollection fragmentCollection =
			FragmentCollectionLocalServiceUtil.fetchFragmentCollection(
				getFragmentCollectionId());

		if ((fragmentCollection == null) ||
			(fragmentCollection.getGroupId() !=
				_themeDisplay.getScopeGroupId())) {

			readOnly = true;
		}

		_readOnly = readOnly;

		return _readOnly;
	}

	private void _setViewAttributes() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(getRedirect());
		portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

		FragmentEntry fragmentEntry = getFragmentEntry();

		if (WorkflowConstants.STATUS_DRAFT != fragmentEntry.getStatus()) {
			_renderResponse.setTitle(getFragmentEntryTitle());

			return;
		}

		_renderResponse.setTitle(
			StringBundler.concat(
				getFragmentEntryTitle(), " (",
				LanguageUtil.get(
					_httpServletRequest,
					WorkflowConstants.getStatusLabel(
						fragmentEntry.getStatus())),
				")"));
	}

	private boolean _showFieldTypes() {
		FragmentEntry fragmentEntry = getFragmentEntry();

		if ((fragmentEntry == null) || !fragmentEntry.isTypeInput()) {
			return false;
		}

		return true;
	}

	private static final InfoFieldType[] _INFO_FIELD_TYPES = {
		BooleanInfoFieldType.INSTANCE, CaptchaInfoFieldType.INSTANCE,
		DateInfoFieldType.INSTANCE, DateTimeInfoFieldType.INSTANCE,
		FileInfoFieldType.INSTANCE, FormButtonInfoFieldType.INSTANCE,
		FriendlyURLInfoFieldType.INSTANCE, HTMLInfoFieldType.INSTANCE,
		LocalizationSelectInfoFieldType.INSTANCE,
		LongTextInfoFieldType.INSTANCE, MultiselectInfoFieldType.INSTANCE,
		NumberInfoFieldType.INSTANCE, RelationshipInfoFieldType.INSTANCE,
		SelectInfoFieldType.INSTANCE, StepperInfoFieldType.INSTANCE,
		TextInfoFieldType.INSTANCE
	};

	private static final Log _log = LogFactoryUtil.getLog(
		EditFragmentEntryDisplayContext.class);

	private String _configurationContent;
	private String _cssContent;
	private final FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;
	private Long _fragmentCollectionId;
	private FragmentEntry _fragmentEntry;
	private Long _fragmentEntryId;
	private String _fragmentEntryKey;
	private final FragmentEntryProcessorRegistry
		_fragmentEntryProcessorRegistry;
	private String _htmlContent;
	private final HttpServletRequest _httpServletRequest;
	private String _jsContent;
	private String _name;
	private Boolean _readOnly;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}