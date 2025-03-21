/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.display.context;

import com.liferay.item.selector.ItemSelector;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributorRegistry;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.provider.SegmentsEntryProviderRegistry;
import com.liferay.segments.service.SegmentsEntryService;
import com.liferay.segments.web.internal.security.permission.resource.SegmentsEntryPermission;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Eduardo García
 */
public class EditSegmentsEntryDisplayContext {

	public EditSegmentsEntryDisplayContext(
		CompanyLocalService companyLocalService,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, ItemSelector itemSelector,
		RenderRequest renderRequest, RenderResponse renderResponse,
		SegmentsConfigurationProvider segmentsConfigurationProvider,
		SegmentsCriteriaContributorRegistry segmentsCriteriaContributorRegistry,
		SegmentsEntryProviderRegistry segmentsEntryProviderRegistry,
		SegmentsEntryService segmentsEntryService) {

		_companyLocalService = companyLocalService;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;
		_itemSelector = itemSelector;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_segmentsConfigurationProvider = segmentsConfigurationProvider;
		_segmentsCriteriaContributorRegistry =
			segmentsCriteriaContributorRegistry;
		_segmentsEntryProviderRegistry = segmentsEntryProviderRegistry;
		_segmentsEntryService = segmentsEntryService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_locale = _themeDisplay.getLocale();
	}

	public String getBackURL() {
		if (_backURL != null) {
			return _backURL;
		}

		_backURL = ParamUtil.getString(
			_httpServletRequest, "backURL", getRedirect());

		return _backURL;
	}

	public String getBackURLTitle() {
		String backURLTitle = ParamUtil.getString(
			_httpServletRequest, "backURLTitle");

		if (Validator.isNotNull(backURLTitle)) {
			return backURLTitle;
		}

		return LanguageUtil.get(_httpServletRequest, "segments");
	}

	public Map<String, Object> getData() {
		if (_data != null) {
			return _data;
		}

		HashMapBuilder.HashMapWrapper<String, Object> hashMapWrapper =
			HashMapBuilder.<String, Object>put("context", _getContext());

		try {
			hashMapWrapper.put("props", _getProps());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			hashMapWrapper.put(
				"error",
				LanguageUtil.get(
					_httpServletRequest, "the-segment-is-no-longer-available"));
		}

		_data = hashMapWrapper.build();

		return _data;
	}

	public long getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		_groupId = ParamUtil.getLong(_httpServletRequest, "groupId");

		return _groupId;
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		if (Validator.isNull(_redirect)) {
			PortletURL portletURL = _renderResponse.createRenderURL();

			_redirect = portletURL.toString();
		}

		return _redirect;
	}

	public long getSegmentsEntryId() {
		if (_segmentsEntryId != null) {
			return _segmentsEntryId;
		}

		_segmentsEntryId = ParamUtil.getLong(
			_httpServletRequest, "segmentsEntryId");

		return _segmentsEntryId;
	}

	public String getSegmentsEntryKey() throws PortalException {
		if (_segmentsEntryKey != null) {
			return _segmentsEntryKey;
		}

		SegmentsEntry segmentsEntry = _getSegmentsEntry();

		if (segmentsEntry == null) {
			_segmentsEntryKey = StringPool.BLANK;
		}
		else {
			_segmentsEntryKey = segmentsEntry.getSegmentsEntryKey();
		}

		return _segmentsEntryKey;
	}

	public String getTitle(Locale locale) throws PortalException {
		if (_title != null) {
			return _title;
		}

		SegmentsEntry segmentsEntry = _getSegmentsEntry();

		if (segmentsEntry != null) {
			_title = segmentsEntry.getName(locale);
		}
		else {
			String type = ResourceActionsUtil.getModelResource(
				locale, User.class.getName());

			_title = LanguageUtil.format(
				_httpServletRequest, "new-x-segment", type, false);
		}

		return _title;
	}

	private Map<String, String> _getAvailableLocales() throws Exception {
		Map<String, String> availableLocales = new HashMap<>();

		for (Locale availableLocale :
				LanguageUtil.getAvailableLocales(_getGroupId())) {

			String availableLanguageId = LocaleUtil.toLanguageId(
				availableLocale);

			availableLocales.put(
				availableLanguageId, availableLocale.getDisplayName(_locale));
		}

		return availableLocales;
	}

	private Map<String, Object> _getContext() {
		return HashMapBuilder.<String, Object>put(
			"imagesPath", PortalUtil.getPathContext(_renderRequest) + "/images"
		).put(
			"namespace", _renderResponse.getNamespace()
		).put(
			"requestFieldValueNameURL", _getSegmentsFieldValueNameURL()
		).build();
	}

	private JSONArray _getContributorsJSONArray() throws Exception {
		JSONArray contributorsJSONArray = JSONFactoryUtil.createJSONArray();

		for (SegmentsCriteriaContributor segmentsCriteriaContributor :
				_segmentsCriteriaContributorRegistry.
					getSegmentsCriteriaContributors()) {

			JSONObject jsonObject =
				segmentsCriteriaContributor.getCriteriaJSONObject(
					_getCriteria());

			contributorsJSONArray.put(
				JSONUtil.put(
					"conjunctionId", jsonObject.get("conjunctionName")
				).put(
					"conjunctionInputId",
					_renderResponse.getNamespace() + "criterionConjunction" +
						segmentsCriteriaContributor.getKey()
				).put(
					"initialQuery", jsonObject.get("query")
				).put(
					"inputId",
					_renderResponse.getNamespace() + "criterionFilter" +
						segmentsCriteriaContributor.getKey()
				).put(
					"propertyKey", segmentsCriteriaContributor.getKey()
				));
		}

		return contributorsJSONArray;
	}

	private Criteria _getCriteria() throws Exception {
		SegmentsEntry segmentsEntry = _getSegmentsEntry();

		if ((segmentsEntry == null) ||
			(segmentsEntry.getCriteriaObj() == null)) {

			return new Criteria();
		}

		return segmentsEntry.getCriteriaObj();
	}

	private String _getDefaultLanguageId() throws Exception {
		Locale siteDefaultLocale = null;

		try {
			siteDefaultLocale = PortalUtil.getSiteDefaultLocale(_getGroupId());
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			siteDefaultLocale = LocaleUtil.getSiteDefault();
		}

		SegmentsEntry segmentsEntry = _getSegmentsEntry();

		if (segmentsEntry == null) {
			return LocaleUtil.toLanguageId(siteDefaultLocale);
		}

		return LocalizationUtil.getDefaultLanguageId(
			segmentsEntry.getName(), siteDefaultLocale);
	}

	private String _getGroupDescriptiveName() throws Exception {
		Group group = _groupLocalService.getGroup(_getGroupId());

		return group.getDescriptiveName(_locale);
	}

	private long _getGroupId() throws PortalException {
		return BeanParamUtil.getLong(
			_getSegmentsEntry(), _httpServletRequest, "groupId",
			_themeDisplay.getScopeGroupId());
	}

	private JSONObject _getInitialSegmentsNameJSONObject() throws Exception {
		SegmentsEntry segmentsEntry = _getSegmentsEntry();

		if (segmentsEntry == null) {
			return null;
		}

		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		return JSONFactoryUtil.createJSONObject(
			jsonSerializer.serializeDeep(segmentsEntry.getNameMap()));
	}

	private String _getPreviewMembersURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/segments/preview_segments_entry_users"
		).setParameter(
			"segmentsEntryId", getSegmentsEntryId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private JSONArray _getPropertyGroupsJSONArray() throws Exception {
		JSONArray jsonContributorsJSONArray = JSONFactoryUtil.createJSONArray();

		for (SegmentsCriteriaContributor segmentsCriteriaContributor :
				_segmentsCriteriaContributorRegistry.
					getSegmentsCriteriaContributors()) {

			jsonContributorsJSONArray.put(
				JSONUtil.put(
					"entityName", segmentsCriteriaContributor.getEntityName()
				).put(
					"name", segmentsCriteriaContributor.getLabel(_locale)
				).put(
					"properties",
					JSONFactoryUtil.createJSONArray(
						JSONFactoryUtil.looseSerializeDeep(
							segmentsCriteriaContributor.getFields(
								_renderRequest)))
				).put(
					"propertyKey", segmentsCriteriaContributor.getKey()
				));
		}

		return jsonContributorsJSONArray;
	}

	private Map<String, Object> _getProps() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"availableLocales", _getAvailableLocales()
		).put(
			"contributors", _getContributorsJSONArray()
		).put(
			"defaultLanguageId", _getDefaultLanguageId()
		).put(
			"formId", _renderResponse.getNamespace() + "editSegmentFm"
		).put(
			"groupId", _getGroupId()
		).put(
			"hasUpdatePermission", _hasUpdatePermission()
		).put(
			"initialMembersCount", _getSegmentsEntryClassPKsCount()
		).put(
			"initialSegmentActive", _isInitialSegmentActive()
		).put(
			"initialSegmentName", _getInitialSegmentsNameJSONObject()
		).put(
			"isSegmentationEnabled",
			_isSegmentationEnabled(_themeDisplay.getCompanyId())
		).put(
			"locale", _locale.toString()
		).put(
			"portletNamespace", _renderResponse.getNamespace()
		).put(
			"previewMembersURL", _getPreviewMembersURL()
		).put(
			"propertyGroups", _getPropertyGroupsJSONArray()
		).put(
			"redirect", getRedirect()
		).put(
			"requestMembersCountURL", _getSegmentsEntryClassPKsCountURL()
		).put(
			"scopeName", _getGroupDescriptiveName()
		).put(
			"segmentsConfigurationURL", _getSegmentsCompanyConfigurationURL()
		).put(
			"showInEditMode", _isShowInEditMode()
		).build();
	}

	private String _getSegmentsCompanyConfigurationURL() {
		try {
			return _segmentsConfigurationProvider.getCompanyConfigurationURL(
				_httpServletRequest);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return StringPool.BLANK;
	}

	private SegmentsEntry _getSegmentsEntry() throws PortalException {
		if (_segmentsEntry != null) {
			return _segmentsEntry;
		}

		long segmentsEntryId = getSegmentsEntryId();

		if (segmentsEntryId > 0) {
			_segmentsEntry = _segmentsEntryService.getSegmentsEntry(
				segmentsEntryId);

			return _segmentsEntry;
		}

		return null;
	}

	private int _getSegmentsEntryClassPKsCount() {
		try {
			SegmentsEntry segmentsEntry = _getSegmentsEntry();

			if (segmentsEntry != null) {
				return _segmentsEntryProviderRegistry.
					getSegmentsEntryClassPKsCount(
						segmentsEntry.getSegmentsEntryId());
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get the segments entry class PKs count",
					portalException);
			}
		}

		return 0;
	}

	private String _getSegmentsEntryClassPKsCountURL() {
		ResourceURL resourceURL = _renderResponse.createResourceURL();

		resourceURL.setResourceID(
			"/segments/get_segments_entry_class_pks_count");

		return resourceURL.toString();
	}

	private String _getSegmentsFieldValueNameURL() {
		ResourceURL resourceURL = _renderResponse.createResourceURL();

		resourceURL.setResourceID("/segments/get_segments_field_value_name");

		return resourceURL.toString();
	}

	private boolean _hasUpdatePermission() throws Exception {
		SegmentsEntry segmentsEntry = _getSegmentsEntry();

		if (segmentsEntry != null) {
			return SegmentsEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), segmentsEntry,
				ActionKeys.UPDATE);
		}

		return true;
	}

	private boolean _isInitialSegmentActive() throws Exception {
		SegmentsEntry segmentsEntry = _getSegmentsEntry();

		if (segmentsEntry != null) {
			return segmentsEntry.isActive();
		}

		return false;
	}

	private boolean _isSegmentationEnabled(long companyId) {
		try {
			return _segmentsConfigurationProvider.isSegmentationEnabled(
				companyId);
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		return false;
	}

	private boolean _isShowInEditMode() {
		return ParamUtil.getBoolean(
			_httpServletRequest, "showInEditMode", true);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditSegmentsEntryDisplayContext.class);

	private String _backURL;
	private final CompanyLocalService _companyLocalService;
	private Map<String, Object> _data;
	private Long _groupId;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final Locale _locale;
	private String _redirect;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final SegmentsConfigurationProvider _segmentsConfigurationProvider;
	private final SegmentsCriteriaContributorRegistry
		_segmentsCriteriaContributorRegistry;
	private SegmentsEntry _segmentsEntry;
	private Long _segmentsEntryId;
	private String _segmentsEntryKey;
	private final SegmentsEntryProviderRegistry _segmentsEntryProviderRegistry;
	private final SegmentsEntryService _segmentsEntryService;
	private final ThemeDisplay _themeDisplay;
	private String _title;

}