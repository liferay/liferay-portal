/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.item.selector.AssetDisplayPageItemSelectorCriterion;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalServiceUtil;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.dynamic.data.mapping.form.renderer.constants.DDMFormRendererConstants;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.item.selector.DDMTemplateItemSelectorCriterion;
import com.liferay.dynamic.data.mapping.item.selector.DDMTemplateItemSelectorReturnType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToMapConverter;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalArticleServiceUtil;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.web.internal.configuration.JournalWebConfiguration;
import com.liferay.journal.web.internal.security.permission.resource.JournalArticlePermission;
import com.liferay.journal.web.internal.security.permission.resource.JournalFolderPermission;
import com.liferay.journal.web.internal.util.RecentGroupManagerUtil;
import com.liferay.layout.item.selector.LayoutItemSelectorCriterion;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.item.selector.SiteItemSelectorCriterion;
import com.liferay.site.manager.RecentGroupManager;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.text.Format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

/**
 * @author Eudaldo Alonso
 */
public class JournalEditArticleDisplayContext {

	public JournalEditArticleDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse, JournalArticle article,
		DepotEntryGroupRelLocalService depotEntryGroupRelLocalService,
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_article = article;
		_depotEntryGroupRelLocalService = depotEntryGroupRelLocalService;
		_depotEntryLocalService = depotEntryLocalService;
		_groupLocalService = groupLocalService;

		_itemSelector = (ItemSelector)httpServletRequest.getAttribute(
			ItemSelector.class.getName());
		_journalWebConfiguration =
			(JournalWebConfiguration)httpServletRequest.getAttribute(
				JournalWebConfiguration.class.getName());
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		setViewAttributes();
	}

	public String getArticleId() {
		if (_article == null) {
			return null;
		}

		return _article.getArticleId();
	}

	public Map<String, Object> getAssetDisplayPagePreviewContext() {
		String selectAssetDisplayPageEventName =
			_liferayPortletResponse.getNamespace() + "selectAssetDisplayPage";
		String selectSiteEventName =
			_liferayPortletResponse.getNamespace() + "selectSite";

		return HashMapBuilder.<String, Object>put(
			"newArticle",
			(_article == null) || Validator.isNull(_article.getArticleId())
		).put(
			"previewURL",
			() -> {
				String getPagePreviewURL = HttpComponentsUtil.addParameters(
					_themeDisplay.getPortalURL() + _themeDisplay.getPathMain() +
						"/portal/get_page_preview",
					"p_l_mode", Constants.PREVIEW, "className",
					JournalArticle.class.getName());

				if (_article != null) {
					getPagePreviewURL = HttpComponentsUtil.addParameters(
						getPagePreviewURL, "classPK",
						_article.getResourcePrimKey(), "version",
						_article.getVersion());
				}

				if (Validator.isNotNull(_themeDisplay.getDoAsUserId())) {
					getPagePreviewURL = PortalUtil.addPreservedParameters(
						_themeDisplay, getPagePreviewURL, false, true);
				}

				return getPagePreviewURL;
			}
		).put(
			"saveAsDraftURL",
			PortletURLBuilder.createActionURL(
				_liferayPortletResponse
			).setActionName(
				"/journal/save_as_draft_article"
			).buildString()
		).put(
			"selectAssetDisplayPageEventName", selectAssetDisplayPageEventName
		).put(
			"selectAssetDisplayPageURL",
			() -> {
				AssetDisplayPageItemSelectorCriterion
					assetDisplayPageItemSelectorCriterion =
						new AssetDisplayPageItemSelectorCriterion();

				assetDisplayPageItemSelectorCriterion.setClassNameId(
					PortalUtil.getClassNameId(JournalArticle.class));
				assetDisplayPageItemSelectorCriterion.setClassTypeId(
					getDDMStructureId());
				assetDisplayPageItemSelectorCriterion.
					setDesiredItemSelectorReturnTypes(
						new UUIDItemSelectorReturnType());

				return PortletURLBuilder.create(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest),
						selectAssetDisplayPageEventName,
						assetDisplayPageItemSelectorCriterion)
				).buildString();
			}
		).put(
			"selectSiteEventName", selectSiteEventName
		).put(
			"siteItemSelectorURL",
			() -> {
				SiteItemSelectorCriterion siteItemSelectorCriterion =
					new SiteItemSelectorCriterion();

				siteItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
					new URLItemSelectorReturnType());

				Group scopeGroup = _groupLocalService.getGroup(getGroupId());

				if (scopeGroup.isDepot()) {
					List<Long> connectedGroupIds = TransformUtil.transform(
						_depotEntryGroupRelLocalService.getDepotEntryGroupRels(
							_depotEntryLocalService.getDepotEntry(
								scopeGroup.getClassPK())),
						DepotEntryGroupRel::getGroupId);

					long[] excludedGroupsIds = null;

					if (ListUtil.isNotEmpty(connectedGroupIds)) {
						excludedGroupsIds = TransformUtil.transformToLongArray(
							_groupLocalService.getGroups(
								_themeDisplay.getCompanyId(),
								GroupConstants.ANY_PARENT_GROUP_ID, true,
								QueryUtil.ALL_POS, QueryUtil.ALL_POS),
							group -> {
								if (!connectedGroupIds.contains(
										group.getGroupId())) {

									return group.getGroupId();
								}

								return null;
							});
					}
					else {
						excludedGroupsIds = TransformUtil.transformToLongArray(
							_groupLocalService.getGroups(
								_themeDisplay.getCompanyId(),
								GroupConstants.ANY_PARENT_GROUP_ID, true,
								QueryUtil.ALL_POS, QueryUtil.ALL_POS),
							Group::getGroupId);
					}

					siteItemSelectorCriterion.setExcludedGroupIds(
						excludedGroupsIds);
				}

				return String.valueOf(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest),
						selectSiteEventName, siteItemSelectorCriterion));
			}
		).put(
			"sites",
			() -> {
				List<Group> groups = null;
				int max = _MAX_SITES;

				Group scopeGroup = _groupLocalService.getGroup(getGroupId());

				if (scopeGroup.isDepot()) {
					groups = _groupLocalService.getGroups(
						TransformUtil.transformToLongArray(
							_depotEntryGroupRelLocalService.
								getDepotEntryGroupRels(
									_depotEntryLocalService.getDepotEntry(
										scopeGroup.getClassPK())),
							DepotEntryGroupRel::getGroupId));
				}
				else {
					RecentGroupManager recentGroupManager =
						RecentGroupManagerUtil.getRecentGroupManager();

					List<Group> recentGroups = ListUtil.subList(
						recentGroupManager.getRecentGroups(_httpServletRequest),
						0, _MAX_SITES);

					JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

					for (Group group : recentGroups) {
						if (group.isCompany()) {
							continue;
						}

						jsonArray.put(
							JSONUtil.put(
								"groupId", group.getGroupId()
							).put(
								"name",
								group.getDescriptiveName(
									_themeDisplay.getLocale())
							));
					}

					if (recentGroups.size() == _MAX_SITES) {
						return jsonArray;
					}

					max = _MAX_SITES - recentGroups.size();

					groups = GroupServiceUtil.getGroups(
						_themeDisplay.getCompanyId(),
						GroupConstants.DEFAULT_PARENT_GROUP_ID, true);
				}

				JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

				for (Group group : groups) {
					if (max < 0) {
						break;
					}

					if (group.isCompany()) {
						continue;
					}

					max -= 1;

					jsonArray.put(
						JSONUtil.put(
							"groupId", group.getGroupId()
						).put(
							"name",
							group.getDescriptiveName(_themeDisplay.getLocale())
						));
				}

				return jsonArray;
			}
		).put(
			"sitesCount",
			() -> {
				if (!GroupPermissionUtil.contains(
						_themeDisplay.getPermissionChecker(),
						ActionKeys.VIEW)) {

					return 0;
				}

				Group scopeGroup = _groupLocalService.getGroup(getGroupId());

				if (scopeGroup.isDepot()) {
					return _depotEntryGroupRelLocalService.
						getDepotEntryGroupRelsCount(
							_depotEntryLocalService.getDepotEntry(
								scopeGroup.getClassPK()));
				}

				int groupsCount = GroupServiceUtil.getGroupsCount(
					_themeDisplay.getCompanyId(), 0, Boolean.TRUE);

				return Math.max(groupsCount - 1, 0);
			}
		).build();
	}

	public Set<Locale> getAvailableLocales() {
		if (_availableLocales != null) {
			return _availableLocales;
		}

		_availableLocales = LanguageUtil.getAvailableLocales(getGroupId());

		return _availableLocales;
	}

	public String getBackURL() {
		if (_backURL != null) {
			return _backURL;
		}

		_backURL = ParamUtil.getString(
			_httpServletRequest, "backURL", getRedirect());

		return _backURL;
	}

	public Map<String, Object> getChangeDefaultLanguageData() {
		return HashMapBuilder.<String, Object>put(
			"defaultLanguage", getDefaultArticleLanguageId()
		).put(
			"languages",
			() -> {
				Set<String> uniqueLanguageIds = new LinkedHashSet<>();

				uniqueLanguageIds.add(getSelectedLanguageId());

				for (Locale availableLocale : getAvailableLocales()) {
					uniqueLanguageIds.add(
						LocaleUtil.toLanguageId(availableLocale));
				}

				return TransformUtil.transform(
					uniqueLanguageIds,
					languageId -> HashMapBuilder.<String, Object>put(
						"icon",
						StringUtil.toLowerCase(
							StringUtil.replace(languageId, '_', '-'))
					).put(
						"label", languageId
					).build());
			}
		).put(
			"strings",
			() -> {
				Map<String, Object> strings = new HashMap<>();

				Set<Locale> locales = new HashSet<>(getAvailableLocales());

				locales.add(
					LocaleUtil.fromLanguageId(getDefaultArticleLanguageId()));

				for (Locale locale : locales) {
					strings.put(
						LocaleUtil.toLanguageId(locale),
						StringBundler.concat(
							locale.getDisplayLanguage(), StringPool.SPACE,
							StringPool.OPEN_PARENTHESIS, locale.getCountry(),
							StringPool.CLOSE_PARENTHESIS));
				}

				return strings;
			}
		).build();
	}

	public long getClassNameId() {
		if (_classNameId != null) {
			return _classNameId;
		}

		_classNameId = BeanParamUtil.getLong(
			_article, _httpServletRequest, "classNameId");

		return _classNameId;
	}

	public long getClassPK() {
		if (_classPK != null) {
			return _classPK;
		}

		_classPK = BeanParamUtil.getLong(
			_article, _httpServletRequest, "classPK");

		return _classPK;
	}

	public Map<String, Object> getComponentContext() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"articleId", getArticleId()
		).put(
			"autoSaveDraftEnabled",
			FeatureFlagManagerUtil.isEnabled("LPD-11228")
		).put(
			"autoSaveDraftURL",
			ResourceURLBuilder.createResourceURL(
				_liferayPortletResponse.createLiferayPortletURL(
					JournalPortletKeys.JOURNAL, PortletRequest.RESOURCE_PHASE,
					MimeResponse.Copy.PUBLIC)
			).setResourceID(
				"/journal/auto_save_article"
			).buildString()
		).put(
			"availableLocales", _getAvailableLanguageIds()
		).put(
			"classNameId", String.valueOf(getClassNameId())
		).put(
			"contentTitle", "titleMapAsXML"
		).put(
			"defaultLanguageId", getDefaultArticleLanguageId()
		).put(
			"hasSavePermission", hasSavePermission()
		).build();
	}

	public Map<String, Object> getDataEngineLayoutRendererComponentContext() {
		return HashMapBuilder.<String, Object>put(
			"currentLanguageId", getSelectedLanguageId()
		).build();
	}

	public DDMFormValues getDDMFormValues() throws PortalException {
		if (_ddmFormValues != null) {
			return _ddmFormValues;
		}

		DDMStructure ddmStructure = getDDMStructure();

		if (_isDDMFormValuesEdited() || (_article == null)) {
			DDMFormValuesFactory ddmFormValuesFactory =
				(DDMFormValuesFactory)_httpServletRequest.getAttribute(
					DDMFormValuesFactory.class.getName());

			return ddmFormValuesFactory.create(
				_httpServletRequest, ddmStructure.getDDMForm());
		}

		DDMFormValues ddmFormValues = _article.getDDMFormValues();

		if ((ddmFormValues == null) && _article.isNew() &&
			(getClassNameId() ==
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT)) {

			JournalArticle ddmStructureArticle =
				JournalArticleLocalServiceUtil.getArticle(
					ddmStructure.getGroupId(), DDMStructure.class.getName(),
					ddmStructure.getStructureId());

			ddmFormValues = ddmStructureArticle.getDDMFormValues();
		}

		if (ddmFormValues != null) {
			_ddmFormValues = ddmFormValues;
		}

		return _ddmFormValues;
	}

	public DDMStructure getDDMStructure() {
		if (_ddmStructure != null) {
			return _ddmStructure;
		}

		long ddmStructureId = ParamUtil.getLong(
			_httpServletRequest, "ddmStructureId");

		if (ddmStructureId > 0) {
			_ddmStructure = DDMStructureLocalServiceUtil.fetchStructure(
				ddmStructureId);
		}
		else if (_article != null) {
			_ddmStructure = DDMStructureLocalServiceUtil.fetchStructure(
				_article.getDDMStructureId());
		}

		return _ddmStructure;
	}

	public long getDDMStructureId() {
		DDMStructure ddmStructure = getDDMStructure();

		return ddmStructure.getStructureId();
	}

	public DDMTemplate getDDMTemplate() throws PortalException {
		if (_ddmTemplate != null) {
			return _ddmTemplate;
		}

		long ddmTemplateId = ParamUtil.getLong(
			_httpServletRequest, "ddmTemplateId");

		if (ddmTemplateId == -1) {
			return null;
		}

		if (ddmTemplateId > 0) {
			_ddmTemplate = DDMTemplateLocalServiceUtil.fetchDDMTemplate(
				ddmTemplateId);

			return _ddmTemplate;
		}

		if (Validator.isNotNull(getDDMTemplateKey())) {
			long groupId = ParamUtil.getLong(
				_httpServletRequest, "groupId", _themeDisplay.getSiteGroupId());

			if (_article != null) {
				groupId = _article.getGroupId();
			}

			_ddmTemplate = DDMTemplateLocalServiceUtil.fetchTemplate(
				groupId, PortalUtil.getClassNameId(DDMStructure.class),
				getDDMTemplateKey(), true);

			return _ddmTemplate;
		}

		if ((_article != null) || (_ddmTemplate != null)) {
			return null;
		}

		DDMStructure ddmStructure = getDDMStructure();

		List<DDMTemplate> ddmTemplates = DDMTemplateServiceUtil.getTemplates(
			_themeDisplay.getCompanyId(), ddmStructure.getGroupId(),
			PortalUtil.getClassNameId(DDMStructure.class),
			ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class), true,
			WorkflowConstants.STATUS_APPROVED);

		if (ddmTemplates.isEmpty()) {
			return null;
		}

		_ddmTemplate = ddmTemplates.get(0);

		return _ddmTemplate;
	}

	public String getDDMTemplateKey() {
		if (_ddmTemplateKey != null) {
			return _ddmTemplateKey;
		}

		String ddmTemplateKey = ParamUtil.getString(
			_httpServletRequest, "ddmTemplateKey");

		if (Validator.isNull(ddmTemplateKey) && (_article != null) &&
			Objects.equals(_article.getDDMStructureId(), getDDMStructureId())) {

			ddmTemplateKey = _article.getDDMTemplateKey();
		}

		_ddmTemplateKey = ddmTemplateKey;

		return _ddmTemplateKey;
	}

	public String getDefaultArticleLanguageId() {
		if (_defaultArticleLanguageId != null) {
			return _defaultArticleLanguageId;
		}

		String defaultArticleLanguageId = ParamUtil.getString(
			_httpServletRequest, "defaultLanguageId");

		if (Validator.isNotNull(getArticleId())) {
			DDMFormValues ddmFormValues = _article.getDDMFormValues();

			defaultArticleLanguageId = LocaleUtil.toLanguageId(
				ddmFormValues.getDefaultLocale());
		}
		else if (Validator.isNull(defaultArticleLanguageId) &&
				 (getClassNameId() ==
					 JournalArticleConstants.CLASS_NAME_ID_DEFAULT)) {

			defaultArticleLanguageId = _getDDMStructureDefaultLanguageId();
		}

		if ((defaultArticleLanguageId == null) ||
			!LanguageUtil.isAvailableLocale(
				getGroupId(), defaultArticleLanguageId)) {

			Locale siteDefaultLocale = null;

			try {
				siteDefaultLocale = PortalUtil.getSiteDefaultLocale(
					getGroupId());
			}
			catch (PortalException portalException) {
				_log.error(portalException);

				siteDefaultLocale = LocaleUtil.getSiteDefault();
			}

			defaultArticleLanguageId = LocaleUtil.toLanguageId(
				siteDefaultLocale);
		}

		_defaultArticleLanguageId = defaultArticleLanguageId;

		return _defaultArticleLanguageId;
	}

	public Map<String, Object> getFieldMap() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"descriptionMapAsXML",
			() -> {
				if (_article != null) {
					return _article.getDescriptionMap();
				}

				return Collections.emptyMap();
			}
		).put(
			"friendlyURL",
			() -> {
				if (_article != null) {
					return _article.getFriendlyURLMap();
				}

				return Collections.emptyMap();
			}
		).put(
			"titleMapAsXML",
			() -> {
				if (_article != null) {
					return _article.getTitleMap();
				}

				return Collections.emptyMap();
			}
		).build();
	}

	public long getFolderId() {
		if (_folderId != null) {
			return _folderId;
		}

		_folderId = BeanParamUtil.getLong(
			_article, _httpServletRequest, "folderId",
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		return _folderId;
	}

	public String getFolderName() {
		if (_folderName != null) {
			return _folderName;
		}

		if (JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID != getFolderId()) {
			JournalFolder folder =
				JournalFolderLocalServiceUtil.fetchJournalFolder(getFolderId());

			if (folder != null) {
				_folderName = folder.getName();

				return _folderName;
			}
		}

		_folderName = LanguageUtil.get(_httpServletRequest, "home");

		return _folderName;
	}

	public String getFriendlyURLBase() {
		StringBundler sb = new StringBundler(4);

		sb.append(_themeDisplay.getPortalURL());

		Group group = _themeDisplay.getScopeGroup();

		boolean privateLayout = false;

		if (_article != null) {
			Layout layout = _article.getLayout();

			if (layout != null) {
				privateLayout = layout.isPrivateLayout();
			}
		}

		if (privateLayout) {
			sb.append(PortalUtil.getPathFriendlyURLPrivateGroup());
		}
		else {
			sb.append(PortalUtil.getPathFriendlyURLPublic());
		}

		sb.append(group.getFriendlyURL());

		sb.append(JournalArticleConstants.CANONICAL_URL_SEPARATOR);

		return sb.toString();
	}

	public String getFriendlyURLDuplicatedWarningMessage()
		throws PortalException {

		if (_friendlyURLDuplicatedWarningMessage != null) {
			return _friendlyURLDuplicatedWarningMessage;
		}

		if (_article == null) {
			_friendlyURLDuplicatedWarningMessage = StringPool.BLANK;

			return _friendlyURLDuplicatedWarningMessage;
		}

		List<Long> excludedGroupIds = new ArrayList<>();

		Group group = _themeDisplay.getScopeGroup();

		excludedGroupIds.add(group.getGroupId());

		if (group.isStagingGroup()) {
			excludedGroupIds.add(group.getLiveGroupId());
		}
		else if (group.hasStagingGroup()) {
			Group stagingGroup = group.getStagingGroup();

			excludedGroupIds.add(stagingGroup.getGroupId());
		}

		List<Locale> friendlyURLDuplicatedLocales = new ArrayList<>();
		Map<String, List<Long>> friendlyURLGroupIdsMap = new HashMap<>();

		Map<Locale, String> friendlyURLMap = _article.getFriendlyURLMap();

		for (Map.Entry<Locale, String> entry : friendlyURLMap.entrySet()) {
			if (Validator.isNull(entry.getValue())) {
				continue;
			}

			List<Long> groupIds = friendlyURLGroupIdsMap.computeIfAbsent(
				entry.getValue(),
				key -> ListUtil.remove(
					JournalArticleLocalServiceUtil.getGroupIdsByUrlTitle(
						_themeDisplay.getCompanyId(), key),
					excludedGroupIds));

			if (!groupIds.isEmpty() &&
				((groupIds.size() > 1) ||
				 !Objects.equals(
					 groupIds.get(0), _themeDisplay.getScopeGroupId()))) {

				friendlyURLDuplicatedLocales.add(entry.getKey());
			}
		}

		if (friendlyURLDuplicatedLocales.isEmpty()) {
			_friendlyURLDuplicatedWarningMessage = StringPool.BLANK;

			return _friendlyURLDuplicatedWarningMessage;
		}

		Locale siteDefaultLocale = PortalUtil.getSiteDefaultLocale(group);

		if ((friendlyURLDuplicatedLocales.size() > 1) &&
			friendlyURLDuplicatedLocales.remove(siteDefaultLocale)) {

			friendlyURLDuplicatedLocales.add(0, siteDefaultLocale);
		}

		String friendlyURLDuplicatedWarningMessage = null;

		if (friendlyURLDuplicatedLocales.size() > 3) {
			friendlyURLDuplicatedWarningMessage = LanguageUtil.format(
				_themeDisplay.getLocale(),
				"the-url-used-in-x-and-x-more-translations-already-exists-in-" +
					"other-sites-or-asset-libraries",
				new String[] {
					_getLocaleDisplayNames(
						_themeDisplay.getLocale(),
						friendlyURLDuplicatedLocales.get(0),
						friendlyURLDuplicatedLocales.get(1),
						friendlyURLDuplicatedLocales.get(2)),
					String.valueOf(friendlyURLDuplicatedLocales.size() - 3)
				},
				false);
		}
		else if (friendlyURLDuplicatedLocales.size() > 1) {
			int lastElementIndex = friendlyURLDuplicatedLocales.size() - 1;

			List<Locale> locales = ListUtil.subList(
				friendlyURLDuplicatedLocales, 0, lastElementIndex);

			friendlyURLDuplicatedWarningMessage = LanguageUtil.format(
				_themeDisplay.getLocale(),
				"the-url-used-in-x-and-x-already-exists-in-other-sites-or-" +
					"asset-libraries",
				new String[] {
					_getLocaleDisplayNames(
						_themeDisplay.getLocale(),
						locales.toArray(new Locale[0])),
					_getLocaleDisplayNames(
						_themeDisplay.getLocale(),
						friendlyURLDuplicatedLocales.get(lastElementIndex))
				},
				false);
		}
		else {
			friendlyURLDuplicatedWarningMessage = LanguageUtil.format(
				_themeDisplay.getLocale(),
				"the-url-used-in-x-already-exists-in-other-sites-or-asset-" +
					"libraries",
				new String[] {
					_getLocaleDisplayNames(
						_themeDisplay.getLocale(),
						friendlyURLDuplicatedLocales.get(0))
				},
				false);
		}

		_friendlyURLDuplicatedWarningMessage =
			friendlyURLDuplicatedWarningMessage;

		return _friendlyURLDuplicatedWarningMessage;
	}

	public long getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		_groupId = BeanParamUtil.getLong(
			_article, _httpServletRequest, "groupId",
			_themeDisplay.getScopeGroupId());

		return _groupId;
	}

	public List<Map<String, Object>> getLocales() {
		return TransformUtil.transform(
			getAvailableLocales(),
			locale -> {
				String languageId = LanguageUtil.getLanguageId(locale);

				String label = StringUtil.replace(languageId, '_', '-');

				return HashMapBuilder.<String, Object>put(
					"displayName",
					() -> {
						String name = LanguageUtil.get(
							_httpServletRequest, "language." + languageId);

						if (name.contains("language.")) {
							name = LanguageUtil.get(
								_httpServletRequest,
								"language." + languageId.substring(0, 2));
						}

						return name;
					}
				).put(
					"id", languageId
				).put(
					"label", label
				).put(
					"symbol", StringUtil.toLowerCase(label)
				).build();
			});
	}

	public String getPermissionsURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/article/permissions.jsp"
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).setWindowState(
			LiferayWindowState.EXCLUSIVE
		).buildString();
	}

	public String getPortletResource() {
		if (_portletResource != null) {
			return _portletResource;
		}

		_portletResource = ParamUtil.getString(
			_httpServletRequest, "portletResource");

		return _portletResource;
	}

	public Map<String, Object> getProps() {
		return HashMapBuilder.<String, Object>put(
			"itemSelectorURL",
			() -> {
				ItemSelectorCriterion itemSelectorCriterion =
					new ImageItemSelectorCriterion();

				itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
					new FileEntryItemSelectorReturnType());

				return String.valueOf(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest),
						_liferayPortletResponse.getNamespace() + "selectImage",
						itemSelectorCriterion));
			}
		).put(
			"previewURL",
			() -> {
				if (_article == null) {
					return null;
				}

				return _article.getArticleImageURL(_themeDisplay);
			}
		).put(
			"smallImageId",
			() -> {
				if ((_article != null) &&
					(_article.getSmallImageSource() ==
						JournalArticleConstants.
							SMALL_IMAGE_SOURCE_DOCUMENTS_AND_MEDIA)) {

					return _article.getSmallImageId();
				}

				return 0;
			}
		).put(
			"smallImageName",
			() -> {
				if ((_article != null) && _article.isSmallImage() &&
					(_article.getSmallImageId() > 0) &&
					(_article.getSmallImageSource() ==
						JournalArticleConstants.
							SMALL_IMAGE_SOURCE_DOCUMENTS_AND_MEDIA)) {

					try {
						FileEntry fileEntry =
							DLAppLocalServiceUtil.getFileEntry(
								_article.getSmallImageId());

						return fileEntry.getTitle();
					}
					catch (PortalException portalException) {
						_log.error(portalException);
					}
				}

				return StringPool.BLANK;
			}
		).put(
			"smallImageSource",
			() -> {
				if (_article == null) {
					return JournalArticleConstants.SMALL_IMAGE_SOURCE_NONE;
				}

				return _article.getSmallImageSource();
			}
		).put(
			"smallImageURL",
			() -> {
				if ((_article != null) && _article.isSmallImage() &&
					(_article.getSmallImageSource() ==
						JournalArticleConstants.SMALL_IMAGE_SOURCE_URL)) {

					return _article.getSmallImageURL();
				}

				return StringPool.BLANK;
			}
		).build();
	}

	public String getPublishButtonLabel() throws PortalException {
		if (getClassNameId() > JournalArticleConstants.CLASS_NAME_ID_DEFAULT) {
			return "save";
		}

		if (_isWorkflowEnabled()) {
			return "submit-for-workflow";
		}

		return "publish";
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		return _redirect;
	}

	public long getRefererPlid() {
		if (_refererPlid != null) {
			return _refererPlid;
		}

		_refererPlid = ParamUtil.getLong(_httpServletRequest, "refererPlid");

		return _refererPlid;
	}

	public String getReferringPortletResource() {
		if (_referringPortletResource != null) {
			return _referringPortletResource;
		}

		_referringPortletResource = ParamUtil.getString(
			_httpServletRequest, "referringPortletResource");

		return _referringPortletResource;
	}

	public String getSaveButtonLabel() {
		if ((_article == null) || _article.isApproved() || _article.isDraft() ||
			_article.isExpired() || _article.isScheduled()) {

			return "save-as-draft";
		}

		return "save";
	}

	public Map<String, Object> getSaveButtonsContext() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"articleId", getArticleId()
		).put(
			"defaultLanguageId", getDefaultArticleLanguageId()
		).put(
			"displayDate",
			() -> {
				if ((_article == null) || (_article.getDisplayDate() == null) ||
					(!_article.isPending() && !_article.isScheduled())) {

					return null;
				}

				Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(
					"yyyy-MM-dd HH:mm", _themeDisplay.getLocale(),
					_themeDisplay.getTimeZone());

				return format.format(_article.getDisplayDate());
			}
		).put(
			"editingDefaultValues",
			getClassNameId() != JournalArticleConstants.CLASS_NAME_ID_DEFAULT
		).put(
			"permissionsURL", getPermissionsURL()
		).put(
			"publishButtonLabel",
			() -> LanguageUtil.get(_httpServletRequest, getPublishButtonLabel())
		).put(
			"saveButtonLabel",
			() -> LanguageUtil.get(_httpServletRequest, getSaveButtonLabel())
		).put(
			"selectedLanguageId", getSelectedLanguageId()
		).put(
			"showPublishModal", _isShowPublishModal()
		).put(
			"timeZone", getTimeZoneMap()
		).put(
			"workflowEnabled", () -> _isWorkflowEnabled()
		).build();
	}

	public Map<String, Object> getSelectAssetDisplayPageContext() {
		String selectAssetDisplayPageEventName =
			_liferayPortletResponse.getNamespace() + "selectAssetDisplayPage";

		return HashMapBuilder.<String, Object>put(
			"assetDisplayPageId", _getAssetDisplayPageId()
		).put(
			"assetDisplayPageType", _getAssetDisplayPageType()
		).put(
			"defaultDisplayPageName",
			() -> {
				LayoutPageTemplateEntry layoutPageTemplateEntry =
					_getDefaultLayoutPageTemplateEntry();

				if (layoutPageTemplateEntry != null) {
					return layoutPageTemplateEntry.getName();
				}

				return null;
			}
		).put(
			"layoutUuid", _getLayoutUuid()
		).put(
			"newArticle",
			(_article == null) || Validator.isNull(_article.getArticleId())
		).put(
			"saveAsDraftURL",
			PortletURLBuilder.createActionURL(
				_liferayPortletResponse
			).setActionName(
				"/journal/save_as_draft_article"
			).buildString()
		).put(
			"selectAssetDisplayPageEventName", selectAssetDisplayPageEventName
		).put(
			"selectAssetDisplayPageURL",
			() -> _getSelectAssetDisplayPageURL(
				selectAssetDisplayPageEventName, true)
		).put(
			"specificAssetDisplayPageName",
			() -> {
				if (_getAssetDisplayPageType() ==
						AssetDisplayPageConstants.TYPE_SPECIFIC) {

					return _getSpecificAssetDisplayPageName();
				}

				return null;
			}
		).build();
	}

	public String getSelectedLanguageId() {
		if (Validator.isNotNull(_selectedLanguageId)) {
			return _selectedLanguageId;
		}

		String selectedLanguageId = ParamUtil.getString(
			_httpServletRequest, "languageId");

		if (Validator.isNull(selectedLanguageId)) {
			selectedLanguageId = getDefaultArticleLanguageId();
		}

		_selectedLanguageId = selectedLanguageId;

		return _selectedLanguageId;
	}

	public List<TabsItem> getTabsItems() {
		TabsItemList tabsItemList = TabsItemListBuilder.add(
			tabsItem -> {
				tabsItem.setActive(true);
				tabsItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "properties"));
			}
		).build();

		if ((_article != null) &&
			(getClassNameId() ==
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT)) {

			tabsItemList.add(
				tabsItem -> tabsItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "usages")));
		}

		return tabsItemList;
	}

	public Map<String, Object> getTemplateComponentContext() {
		return HashMapBuilder.<String, Object>put(
			"availableLocales", _getAvailableLanguageIds()
		).put(
			"currentURL", _themeDisplay::getURLCurrent
		).put(
			"ddmTemplateId",
			() -> {
				DDMTemplate ddmTemplate = getDDMTemplate();

				if (ddmTemplate != null) {
					return String.valueOf(ddmTemplate.getTemplateId());
				}

				return "0";
			}
		).put(
			"editDDMTemplateURL",
			() -> PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCPath(
				"/edit_ddm_template.jsp"
			).setRedirect(
				_themeDisplay.getURLCurrent()
			).setParameter(
				"ddmTemplateId",
				() -> {
					DDMTemplate ddmTemplate = getDDMTemplate();

					if (ddmTemplate != null) {
						return ddmTemplate.getTemplateId();
					}

					return 0;
				}
			).buildString()
		).put(
			"previewArticleContentTemplateURL",
			() -> PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCPath(
				"/preview_article_content_template.jsp"
			).setParameter(
				"articleId", getArticleId()
			).setParameter(
				"groupId", getGroupId()
			).setParameter(
				"version", getVersion()
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		).put(
			"selectDDMTemplateURL",
			() -> {
				RequestBackedPortletURLFactory requestBackedPortletURLFactory =
					RequestBackedPortletURLFactoryUtil.create(
						_httpServletRequest);

				DDMTemplateItemSelectorCriterion
					ddmTemplateItemSelectorCriterion =
						new DDMTemplateItemSelectorCriterion();

				ddmTemplateItemSelectorCriterion.setClassNameId(
					PortalUtil.getClassNameId(JournalArticle.class.getName()));
				ddmTemplateItemSelectorCriterion.setDDMStructureId(
					_ddmStructure.getStructureId());
				ddmTemplateItemSelectorCriterion.
					setDesiredItemSelectorReturnTypes(
						new DDMTemplateItemSelectorReturnType());

				return String.valueOf(
					_itemSelector.getItemSelectorURL(
						requestBackedPortletURLFactory, "selectDDMTemplate",
						ddmTemplateItemSelectorCriterion));
			}
		).build();
	}

	public Map<String, Object> getTimeZoneMap() {
		TimeZone timeZone = _themeDisplay.getTimeZone();

		return HashMapBuilder.<String, Object>put(
			"id", timeZone.getID()
		).put(
			"name", timeZone.getDisplayName(false, TimeZone.SHORT)
		).build();
	}

	public String getTimeZoneName() {
		TimeZone timeZone = _themeDisplay.getTimeZone();

		return timeZone.getDisplayName(false, TimeZone.SHORT);
	}

	public Map<String, Object> getValues() throws PortalException {
		DDMFormValuesToMapConverter ddmFormValuesToMapConverter =
			(DDMFormValuesToMapConverter)_httpServletRequest.getAttribute(
				DDMFormValuesToMapConverter.class.getName());

		return ddmFormValuesToMapConverter.convert(
			getDDMFormValues(), getDDMStructure());
	}

	public double getVersion() {
		if (_version != null) {
			return _version;
		}

		_version = BeanParamUtil.getDouble(
			_article, _httpServletRequest, "version",
			JournalArticleConstants.VERSION_DEFAULT);

		return _version;
	}

	public boolean hasSavePermission() throws PortalException {
		if ((_article != null) && !_article.isNew()) {
			return JournalArticlePermission.contains(
				_themeDisplay.getPermissionChecker(), _article,
				ActionKeys.UPDATE);
		}

		return JournalFolderPermission.contains(
			_themeDisplay.getPermissionChecker(), getGroupId(), getFolderId(),
			ActionKeys.ADD_ARTICLE);
	}

	public boolean isChangeStructure() {
		if (_changeStructure != null) {
			return _changeStructure;
		}

		_changeStructure = GetterUtil.getBoolean(
			ParamUtil.getString(_httpServletRequest, "changeStructure"));

		return _changeStructure;
	}

	public boolean isNeverExpire() {
		if (_neverExpire != null) {
			return _neverExpire;
		}

		_neverExpire = ParamUtil.getBoolean(
			_httpServletRequest, "neverExpire", true);

		if ((_article != null) && (_article.getExpirationDate() != null)) {
			_neverExpire = false;
		}

		return _neverExpire;
	}

	public boolean isNeverReview() {
		if (_neverReview != null) {
			return _neverReview;
		}

		_neverReview = ParamUtil.getBoolean(
			_httpServletRequest, "neverReview", true);

		if ((_article != null) && (_article.getReviewDate() != null)) {
			_neverReview = false;
		}

		return _neverReview;
	}

	public boolean isPending() throws PortalException {
		if ((_article != null) && (getVersion() > 0) && _isWorkflowEnabled()) {
			return _article.isPending();
		}

		return false;
	}

	public boolean isShowSelectFolder() {
		if (_showSelectFolder != null) {
			return _showSelectFolder;
		}

		_showSelectFolder = false;

		if ((_article == null) &&
			(getClassNameId() ==
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT)) {

			_showSelectFolder = ParamUtil.getBoolean(
				_httpServletRequest, "showSelectFolder", true);
		}

		return _showSelectFolder;
	}

	public void setViewAttributes() {
		if (!_isShowHeader()) {
			return;
		}

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBackTitle(
			ParamUtil.getString(_httpServletRequest, "backURLTitle"));

		if (Validator.isNotNull(getBackURL())) {
			portletDisplay.setURLBack(getBackURL());
		}
		else if ((getClassNameId() ==
					JournalArticleConstants.CLASS_NAME_ID_DEFAULT) &&
				 (_article != null)) {

			portletDisplay.setURLBack(
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setParameter(
					"folderId", _article.getFolderId()
				).setParameter(
					"groupId", _article.getGroupId()
				).buildString());
		}

		if (_liferayPortletResponse instanceof RenderResponse) {
			RenderResponse renderResponse =
				(RenderResponse)_liferayPortletResponse;

			renderResponse.setTitle(_getTitle());
		}
	}

	private AssetDisplayPageEntry _getAssetDisplayPageEntry() {
		if (_assetDisplayPageEntry != null) {
			return _assetDisplayPageEntry;
		}

		if (_article == null) {
			return null;
		}

		AssetDisplayPageEntry assetDisplayPageEntry =
			AssetDisplayPageEntryLocalServiceUtil.fetchAssetDisplayPageEntry(
				getGroupId(), PortalUtil.getClassNameId(JournalArticle.class),
				_article.getResourcePrimKey());

		if (assetDisplayPageEntry != null) {
			_assetDisplayPageEntry = assetDisplayPageEntry;
		}

		return _assetDisplayPageEntry;
	}

	private long _getAssetDisplayPageId() {
		if (_assetDisplayPageId != null) {
			return _assetDisplayPageId;
		}

		_assetDisplayPageId = ParamUtil.getLong(
			_httpServletRequest, "assetDisplayPageId");

		AssetDisplayPageEntry assetDisplayPageEntry =
			_getAssetDisplayPageEntry();

		if (assetDisplayPageEntry != null) {
			_assetDisplayPageId =
				assetDisplayPageEntry.getLayoutPageTemplateEntryId();
		}

		return _assetDisplayPageId;
	}

	private int _getAssetDisplayPageType() {
		if (_displayPageType != null) {
			return _displayPageType;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getDefaultLayoutPageTemplateEntry();

		if ((_article == null) && (layoutPageTemplateEntry != null)) {
			_displayPageType = AssetDisplayPageConstants.TYPE_DEFAULT;

			return _displayPageType;
		}

		AssetDisplayPageEntry assetDisplayPageEntry =
			_getAssetDisplayPageEntry();

		if (assetDisplayPageEntry == null) {
			if (Validator.isNull(_getLayoutUuid())) {
				_displayPageType = AssetDisplayPageConstants.TYPE_DEFAULT;
			}
			else {
				_displayPageType = AssetDisplayPageConstants.TYPE_SPECIFIC;
			}
		}
		else {
			_displayPageType = assetDisplayPageEntry.getType();
		}

		return _displayPageType;
	}

	private String[] _getAvailableLanguageIds() {
		if (_article == null) {
			return new String[] {getDefaultArticleLanguageId()};
		}

		return _article.getAvailableLanguageIds();
	}

	private String _getDDMStructureDefaultLanguageId() {
		DDMStructure ddmStructure = getDDMStructure();

		if (!_journalWebConfiguration.changeableDefaultLanguage() ||
			(ddmStructure == null)) {

			return null;
		}

		try {
			JournalArticle ddmStructureJournalArticle =
				JournalArticleServiceUtil.getArticle(
					ddmStructure.getGroupId(), DDMStructure.class.getName(),
					ddmStructure.getStructureId());

			String defaultLanguageId =
				ddmStructureJournalArticle.getDefaultLanguageId();

			List<String> availableLocales = TransformUtil.transform(
				getAvailableLocales(),
				availableLocale -> LocaleUtil.toLanguageId(availableLocale));

			if (availableLocales.contains(defaultLanguageId)) {
				return defaultLanguageId;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	private LayoutPageTemplateEntry _getDefaultLayoutPageTemplateEntry() {
		if (_defaultLayoutPageTemplateEntry != null) {
			return _defaultLayoutPageTemplateEntry;
		}

		_defaultLayoutPageTemplateEntry =
			LayoutPageTemplateEntryServiceUtil.
				fetchDefaultLayoutPageTemplateEntry(
					getGroupId(),
					PortalUtil.getClassNameId(JournalArticle.class),
					getDDMStructureId());

		return _defaultLayoutPageTemplateEntry;
	}

	private long _getInheritedWorkflowDDMStructuresFolderId()
		throws PortalException {

		if (_inheritedWorkflowDDMStructuresFolderId != null) {
			return _inheritedWorkflowDDMStructuresFolderId;
		}

		_inheritedWorkflowDDMStructuresFolderId =
			JournalFolderLocalServiceUtil.getInheritedWorkflowFolderId(
				getFolderId());

		return _inheritedWorkflowDDMStructuresFolderId;
	}

	private String _getLayoutUuid() {
		return BeanParamUtil.getString(
			_article, _httpServletRequest, "layoutUuid", null);
	}

	private String _getLocaleDisplayNames(Locale locale, Locale... locales) {
		List<String> displayLocaleNames = new ArrayList<>();

		for (Locale currentLocale : locales) {
			displayLocaleNames.add(
				LocaleUtil.getLocaleDisplayName(currentLocale, locale));
		}

		return StringUtil.merge(displayLocaleNames, StringPool.COMMA_AND_SPACE);
	}

	private String _getSelectAssetDisplayPageURL(
		String selectAssetDisplayPageEventName, boolean showPortletLayouts) {

		List<ItemSelectorCriterion> itemSelectorCriteria = new ArrayList<>();

		AssetDisplayPageItemSelectorCriterion
			assetDisplayPageItemSelectorCriterion =
				new AssetDisplayPageItemSelectorCriterion();

		assetDisplayPageItemSelectorCriterion.setClassNameId(
			PortalUtil.getClassNameId(JournalArticle.class));
		assetDisplayPageItemSelectorCriterion.setClassTypeId(
			getDDMStructureId());
		assetDisplayPageItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());

		itemSelectorCriteria.add(assetDisplayPageItemSelectorCriterion);

		if (showPortletLayouts) {
			LayoutItemSelectorCriterion layoutItemSelectorCriterion =
				new LayoutItemSelectorCriterion();

			layoutItemSelectorCriterion.setCheckDisplayPage(true);
			layoutItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
				new UUIDItemSelectorReturnType());
			layoutItemSelectorCriterion.setShowBreadcrumb(false);

			itemSelectorCriteria.add(layoutItemSelectorCriterion);
		}

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				selectAssetDisplayPageEventName,
				itemSelectorCriteria.toArray(new ItemSelectorCriterion[0])));
	}

	private String _getSpecificAssetDisplayPageName() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntry(_getAssetDisplayPageId());

		if (layoutPageTemplateEntry != null) {
			return layoutPageTemplateEntry.getName();
		}

		String layoutUuid = _getLayoutUuid();

		if (Validator.isNull(layoutUuid)) {
			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout selLayout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
			layoutUuid, themeDisplay.getSiteGroupId(), false);

		if (selLayout == null) {
			selLayout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				layoutUuid, themeDisplay.getSiteGroupId(), true);
		}

		if (selLayout != null) {
			return selLayout.getBreadcrumb(themeDisplay.getLocale());
		}

		return null;
	}

	private String _getTitle() {
		if (getClassNameId() > JournalArticleConstants.CLASS_NAME_ID_DEFAULT) {
			return LanguageUtil.get(
				_httpServletRequest, "structure-default-values");
		}
		else if ((_article != null) && !_article.isNew()) {
			return _article.getTitle(_themeDisplay.getLocale());
		}

		return LanguageUtil.get(_httpServletRequest, "new-web-content");
	}

	private boolean _hasInheritedWorkflowDefinitionLink()
		throws PortalException {

		if (_getInheritedWorkflowDDMStructuresFolderId() <= 0) {
			return WorkflowDefinitionLinkLocalServiceUtil.
				hasWorkflowDefinitionLink(
					_themeDisplay.getCompanyId(), getGroupId(),
					JournalArticle.class.getName());
		}

		JournalFolder inheritedWorkflowDDMStructuresFolder =
			JournalFolderLocalServiceUtil.getFolder(
				_getInheritedWorkflowDDMStructuresFolderId());

		if (inheritedWorkflowDDMStructuresFolder.getRestrictionType() ==
				JournalFolderConstants.RESTRICTION_TYPE_INHERIT) {

			return true;
		}

		return false;
	}

	private boolean _isDDMFormValuesEdited() {
		Enumeration<String> enumeration =
			_httpServletRequest.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String parameterName = enumeration.nextElement();

			if (StringUtil.startsWith(
					parameterName,
					DDMFormRendererConstants.DDM_FORM_FIELD_NAME_PREFIX) &&
				StringUtil.endsWith(parameterName, "_edited") &&
				GetterUtil.getBoolean(
					_httpServletRequest.getParameter(parameterName))) {

				return true;
			}
		}

		return false;
	}

	private boolean _isShowHeader() {
		if (_showHeader != null) {
			return _showHeader;
		}

		_showHeader = ParamUtil.getBoolean(
			_httpServletRequest, "showHeader", true);

		return _showHeader;
	}

	private boolean _isShowPublishModal() throws PortalException {
		if ((_article == null) || (_article.getId() == 0)) {
			return true;
		}

		if (!FeatureFlagManagerUtil.isEnabled(
				_themeDisplay.getCompanyId(), "LPD-11228")) {

			return Validator.isNull(_article.getArticleId());
		}

		JournalArticle oldestArticle =
			JournalArticleLocalServiceUtil.getOldestArticle(
				_article.getGroupId(), _article.getArticleId());

		if ((oldestArticle == null) ||
			((oldestArticle.getStatus() == WorkflowConstants.STATUS_DRAFT) &&
			 (oldestArticle.getVersion() == _article.getVersion()))) {

			return true;
		}

		return false;
	}

	private boolean _isWorkflowEnabled() throws PortalException {
		if (getClassNameId() > JournalArticleConstants.CLASS_NAME_ID_DEFAULT) {
			return false;
		}

		if (_hasInheritedWorkflowDefinitionLink()) {
			return true;
		}

		DDMStructure ddmStructure = getDDMStructure();

		if (WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(
				_themeDisplay.getCompanyId(), getGroupId(),
				JournalFolder.class.getName(), getFolderId(),
				ddmStructure.getStructureId()) ||
			WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(
				_themeDisplay.getCompanyId(), getGroupId(),
				JournalFolder.class.getName(),
				_getInheritedWorkflowDDMStructuresFolderId(),
				ddmStructure.getStructureId()) ||
			WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(
				_themeDisplay.getCompanyId(), getGroupId(),
				JournalFolder.class.getName(),
				_getInheritedWorkflowDDMStructuresFolderId(),
				JournalArticleConstants.DDM_STRUCTURE_ID_ALL)) {

			return true;
		}

		return false;
	}

	private static final int _MAX_SITES = 6;

	private static final Log _log = LogFactoryUtil.getLog(
		JournalEditArticleDisplayContext.class);

	private final JournalArticle _article;
	private AssetDisplayPageEntry _assetDisplayPageEntry;
	private Long _assetDisplayPageId;
	private Set<Locale> _availableLocales;
	private String _backURL;
	private Boolean _changeStructure;
	private Long _classNameId;
	private Long _classPK;
	private DDMFormValues _ddmFormValues;
	private DDMStructure _ddmStructure;
	private DDMTemplate _ddmTemplate;
	private String _ddmTemplateKey;
	private String _defaultArticleLanguageId;
	private LayoutPageTemplateEntry _defaultLayoutPageTemplateEntry;
	private final DepotEntryGroupRelLocalService
		_depotEntryGroupRelLocalService;
	private final DepotEntryLocalService _depotEntryLocalService;
	private Integer _displayPageType;
	private Long _folderId;
	private String _folderName;
	private String _friendlyURLDuplicatedWarningMessage;
	private Long _groupId;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private Long _inheritedWorkflowDDMStructuresFolderId;
	private final ItemSelector _itemSelector;
	private final JournalWebConfiguration _journalWebConfiguration;
	private final LiferayPortletResponse _liferayPortletResponse;
	private Boolean _neverExpire;
	private Boolean _neverReview;
	private String _portletResource;
	private String _redirect;
	private Long _refererPlid;
	private String _referringPortletResource;
	private String _selectedLanguageId;
	private Boolean _showHeader;
	private Boolean _showSelectFolder;
	private final ThemeDisplay _themeDisplay;
	private Double _version;

}