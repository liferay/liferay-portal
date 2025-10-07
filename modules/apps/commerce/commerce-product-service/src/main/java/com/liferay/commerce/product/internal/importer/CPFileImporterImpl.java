/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.importer;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.commerce.product.importer.CPFileImporter;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.ThemeSetting;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.constants.PortletPreferencesFactoryConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.resource.bundle.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ClassResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.ThemeSettingImpl;

import jakarta.portlet.PortletPreferences;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(service = CPFileImporter.class)
public class CPFileImporterImpl implements CPFileImporter {

	public static final String GROUP_ID_PLACEHOLDER = "[$GROUP_ID$]";

	public static final String IMG_TAG =
		"<img alt='' src='%s' data-fileentryid='%s' />";

	public static final String LOCALE_PLACEHOLDER = "[$LOCALE$]";

	@Override
	public void cleanLayouts(ServiceContext serviceContext)
		throws PortalException {

		_layoutLocalService.deleteLayouts(
			serviceContext.getScopeGroupId(), true, serviceContext);

		_layoutLocalService.deleteLayouts(
			serviceContext.getScopeGroupId(), false, serviceContext);
	}

	@Override
	public void createJournalArticles(
			JSONArray journalArticleJSONArray, ClassLoader classLoader,
			String dependenciesFilePath, ServiceContext serviceContext)
		throws Exception {

		for (int i = 0; i < journalArticleJSONArray.length(); i++) {
			JSONObject journalArticleJSONObject =
				journalArticleJSONArray.getJSONObject(i);

			_createJournalArticle(
				journalArticleJSONObject, classLoader, dependenciesFilePath,
				serviceContext);
		}
	}

	@Override
	public void createLayouts(
			JSONArray jsonArray, ClassLoader classLoader,
			String dependenciesFilePath, ServiceContext serviceContext)
		throws Exception {

		createLayouts(
			jsonArray, null, classLoader, dependenciesFilePath, serviceContext);
	}

	@Override
	public void createRoles(JSONArray jsonArray, ServiceContext serviceContext)
		throws PortalException {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONObject actionsJSONObject = jsonObject.getJSONObject("actions");
			String name = jsonObject.getString("name");
			int scope = jsonObject.getInt("scope");
			int type = jsonObject.getInt("type");

			Role role = _getRole(name, type, serviceContext);

			if (actionsJSONObject != null) {
				_updateActions(role, actionsJSONObject, scope, serviceContext);
			}
			else {
				JSONArray actionsJSONArray = jsonObject.getJSONArray("actions");

				for (int j = 0; j < actionsJSONArray.length(); j++) {
					_updateActions(
						role, actionsJSONArray.getJSONObject(j), scope,
						serviceContext);
				}
			}
		}
	}

	@Override
	public DDMTemplate getDDMTemplate(
			File file, long classNameId, long classPK, long resourceClassNameId,
			String name, String type, String mode, String language,
			ServiceContext serviceContext)
		throws Exception {

		if (file == null) {
			return _ddmTemplateLocalService.fetchTemplate(
				serviceContext.getScopeGroupId(), classNameId, _getKey(name));
		}

		FileInputStream fileInputStream = new FileInputStream(file);

		BufferedInputStream bufferedInputStream = new BufferedInputStream(
			fileInputStream);

		String script = StringUtil.read(bufferedInputStream);

		return _fetchOrAddDDMTemplate(
			classNameId, classPK, resourceClassNameId, name, type, mode,
			language, script, true, serviceContext);
	}

	@Override
	public void updateLogo(
			File file, boolean privateLayout, boolean logo,
			ServiceContext serviceContext)
		throws PortalException {

		_layoutSetLocalService.updateLogo(
			serviceContext.getScopeGroupId(), privateLayout, logo, file);
	}

	@Override
	public void updateLookAndFeel(
			String themeId, boolean privateLayout,
			ServiceContext serviceContext)
		throws PortalException {

		Theme theme = _themeLocalService.fetchTheme(
			serviceContext.getCompanyId(), themeId);

		if (theme == null) {
			if (_log.isInfoEnabled()) {
				_log.info("No Theme registered with themeId: " + themeId);
			}

			return;
		}

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			serviceContext.getScopeGroupId(), privateLayout);

		UnicodeProperties typeSettingUnicodeProperties =
			layoutSet.getSettingsProperties();

		_setThemeSettingProperties(theme, typeSettingUnicodeProperties);

		_layoutSetLocalService.updateLookAndFeel(
			serviceContext.getScopeGroupId(), privateLayout, themeId,
			StringPool.BLANK, StringPool.BLANK);
	}

	protected void createLayouts(
			JSONArray jsonArray, Layout parentLayout, ClassLoader classLoader,
			String dependenciesFilePath, ServiceContext serviceContext)
		throws Exception {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject layoutJSONObject = jsonArray.getJSONObject(i);

			String layoutName = layoutJSONObject.getString("name");

			if (layoutName.equals("Returns") &&
				!FeatureFlagManagerUtil.isEnabled(
					serviceContext.getCompanyId(), "LPD-10562")) {

				continue;
			}

			_createLayout(
				jsonArray.getJSONObject(i), parentLayout, classLoader,
				dependenciesFilePath, serviceContext);
		}
	}

	private void _addLayoutPortlets(
			JSONArray jsonArray, Layout layout, String layoutTemplateId,
			ClassLoader classLoader, ServiceContext serviceContext)
		throws Exception {

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		layoutTypePortlet.setLayoutTemplateId(
			UserConstants.USER_ID_DEFAULT, layoutTemplateId, false);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject portletJSONObject = jsonArray.getJSONObject(i);

			String portletId = _addPortletId(
				portletJSONObject, layoutTypePortlet, serviceContext);

			_setPortletPreferences(
				portletJSONObject.getJSONObject("portletPreferences"), layout,
				portletId, serviceContext);

			_setPortletLookAndFeel(
				portletJSONObject.getJSONObject("lookAndFeel"), layout,
				portletId, classLoader);
		}
	}

	private String _addPortletId(
			JSONObject jsonObject, LayoutTypePortlet layoutTypePortlet,
			ServiceContext serviceContext)
		throws Exception {

		String layoutColumnId = jsonObject.getString("layoutColumnId");
		int layoutColumnPos = jsonObject.getInt("layoutColumnPos");
		String portletName = jsonObject.getString("portletName");

		return layoutTypePortlet.addPortletId(
			serviceContext.getUserId(), portletName, layoutColumnId,
			layoutColumnPos, false);
	}

	private JournalArticle _createJournalArticle(
			JSONObject jsonObject, ClassLoader classLoader,
			String dependenciesFilePath, ServiceContext serviceContext)
		throws Exception {

		String articleId = jsonObject.getString("articleId");

		articleId = StringUtil.toUpperCase(StringUtil.trim(articleId));

		JournalArticle journalArticle =
			_journalArticleLocalService.fetchArticle(
				serviceContext.getScopeGroupId(), articleId);

		if (journalArticle != null) {
			return journalArticle;
		}

		String ddmStructureKey = jsonObject.getString("ddmStructureKey");
		String ddmTemplateKey = jsonObject.getString("ddmTemplateKey");

		DDMStructure ddmStructure = _fetchOrAddDDMStructure(
			ddmStructureKey, classLoader, dependenciesFilePath,
			ddmStructureKey + ".json", serviceContext);

		InputStream inputStream = classLoader.getResourceAsStream(
			dependenciesFilePath + ddmTemplateKey + ".ftl");

		if (inputStream != null) {
			_fetchOrAddDDMTemplate(
				_portal.getClassNameId(DDMStructure.class),
				ddmStructure.getStructureId(),
				_portal.getClassNameId(JournalArticle.class), ddmTemplateKey,
				DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, null, "ftl",
				StringUtil.read(inputStream),
				jsonObject.getBoolean("cacheable", true), serviceContext);
		}

		Locale locale = serviceContext.getLocale();

		Map<Locale, String> titleMap = HashMapBuilder.put(
			locale, jsonObject.getString("title")
		).build();

		Map<Locale, String> descriptionMap = HashMapBuilder.put(
			locale, jsonObject.getString("description")
		).build();

		String content = StringUtil.read(
			classLoader, dependenciesFilePath + articleId + ".xml");

		content = _getNormalizedContent(
			content, classLoader, dependenciesFilePath, serviceContext);

		Calendar displayCalendar = CalendarFactoryUtil.getCalendar(
			serviceContext.getTimeZone());

		int displayDateMonth = displayCalendar.get(Calendar.MONTH);
		int displayDateDay = displayCalendar.get(Calendar.DAY_OF_MONTH);
		int displayDateYear = displayCalendar.get(Calendar.YEAR);
		int displayDateHour = displayCalendar.get(Calendar.HOUR);
		int displayDateMinute = displayCalendar.get(Calendar.MINUTE);
		int displayDateAmPm = displayCalendar.get(Calendar.AM_PM);

		if (displayDateAmPm == Calendar.PM) {
			displayDateHour += 12;
		}

		journalArticle = _journalArticleLocalService.addArticle(
			null, serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			0L, JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0L, articleId,
			false, 1, titleMap, descriptionMap, titleMap, content,
			ddmStructure.getStructureId(), ddmTemplateKey, StringPool.BLANK,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, 0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0, true, true,
			false, 0, 0, StringPool.BLANK, null, null, StringPool.BLANK,
			serviceContext);

		JSONArray permissionsJSONArray = jsonObject.getJSONArray("permissions");

		if ((permissionsJSONArray != null) &&
			(permissionsJSONArray.length() > 0)) {

			_updatePermissions(
				journalArticle.getCompanyId(),
				journalArticle.getModelClassName(),
				String.valueOf(journalArticle.getResourcePrimKey()),
				permissionsJSONArray);
		}
		else {

			// Give site members view permissions

			_updatePermissions(
				journalArticle.getCompanyId(),
				journalArticle.getModelClassName(),
				String.valueOf(journalArticle.getResourcePrimKey()), null);
		}

		return journalArticle;
	}

	private void _createLayout(
			JSONObject jsonObject, Layout parentLayout, ClassLoader classLoader,
			String dependenciesFilePath, ServiceContext serviceContext)
		throws Exception {

		boolean hidden = jsonObject.getBoolean("hidden");
		String icon = jsonObject.getString("icon");
		String layoutType = jsonObject.getString(
			"layoutType", LayoutConstants.TYPE_PORTLET);
		String name = jsonObject.getString("name");
		boolean privateLayout = jsonObject.getBoolean("privateLayout");

		long parentLayoutId = LayoutConstants.DEFAULT_PARENT_LAYOUT_ID;

		if (parentLayout != null) {
			parentLayoutId = parentLayout.getLayoutId();
		}

		String friendlyURL = StringUtil.toLowerCase(name);

		friendlyURL = friendlyURL.trim();

		friendlyURL = _friendlyURLNormalizer.normalize(friendlyURL);

		friendlyURL = CharPool.SLASH + friendlyURL;

		Layout layout = _layoutLocalService.addLayout(
			null, serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			privateLayout, parentLayoutId, name, name, StringPool.BLANK,
			layoutType, hidden, friendlyURL, serviceContext);

		if (Validator.isNotNull(icon)) {
			String filePath = dependenciesFilePath + "layout_icons/" + icon;

			InputStream inputStream = classLoader.getResourceAsStream(filePath);

			byte[] byteArray = _file.getBytes(inputStream);

			layout = _layoutLocalService.updateIconImage(
				layout.getPlid(), byteArray);
		}

		JSONObject lookAndFeelJSONObject = jsonObject.getJSONObject(
			"lookAndFeel");

		if (lookAndFeelJSONObject != null) {
			layout = _updateLayoutLookAndFeel(lookAndFeelJSONObject, layout);
		}

		JSONArray portletsJSONArray = jsonObject.getJSONArray("portlets");

		if ((portletsJSONArray != null) && (portletsJSONArray.length() > 0)) {
			String layoutTemplateId = jsonObject.getString("layoutTemplateId");

			_addLayoutPortlets(
				portletsJSONArray, layout, layoutTemplateId, classLoader,
				serviceContext);
		}

		layout = _layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());

		JSONArray permissionsJSONArray = jsonObject.getJSONArray("permissions");

		if ((permissionsJSONArray != null) &&
			(permissionsJSONArray.length() > 0)) {

			_updatePermissions(
				layout.getCompanyId(), layout.getModelClassName(),
				String.valueOf(layout.getPlid()), permissionsJSONArray);
		}

		JSONArray sublayoutsJSONArray = jsonObject.getJSONArray("subLayouts");

		if ((sublayoutsJSONArray != null) &&
			(sublayoutsJSONArray.length() > 0)) {

			createLayouts(
				sublayoutsJSONArray, layout, classLoader, dependenciesFilePath,
				serviceContext);
		}
	}

	private void _deleteThemeSettingsProperties(
		UnicodeProperties typeSettingsUnicodeProperties, String device) {

		String keyPrefix = ThemeSettingImpl.namespaceProperty(device);

		Set<String> keys = typeSettingsUnicodeProperties.keySet();

		Iterator<String> iterator = keys.iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();

			if (key.startsWith(keyPrefix)) {
				iterator.remove();
			}
		}
	}

	private DDMStructure _fetchOrAddDDMStructure(
			String ddmStructureKey, ClassLoader classLoader,
			String dependencyFilePath, String ddmStructureFileName,
			ServiceContext serviceContext)
		throws Exception {

		long classNameId = _portal.getClassNameId(JournalArticle.class);

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			serviceContext.getScopeGroupId(), classNameId, ddmStructureKey,
			true);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		Map<Locale, String> nameMap = Collections.singletonMap(
			serviceContext.getLocale(),
			TextFormatter.format(ddmStructureKey, TextFormatter.J));

		String json = StringUtil.read(
			classLoader, dependencyFilePath + ddmStructureFileName);

		json = _getNormalizedContent(
			json, classLoader, dependencyFilePath, serviceContext);

		DDMFormDeserializerDeserializeRequest
			ddmFormDeserializerDeserializeRequest =
				DDMFormDeserializerDeserializeRequest.Builder.newBuilder(
					json
				).build();

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_ddmFormDeserializer.deserialize(
					ddmFormDeserializerDeserializeRequest);

		DDMForm ddmForm = ddmFormDeserializerDeserializeResponse.getDDMForm();

		ddmForm = _updateDDMFormAvailableLocales(
			ddmForm, serviceContext.getLocale());

		DDMFormLayout ddmFormLayout = _ddm.getDefaultDDMFormLayout(ddmForm);

		return _ddmStructureLocalService.addStructure(
			null, serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			0, classNameId, ddmStructureKey, nameMap, null, ddmForm,
			ddmFormLayout, "json", DDMStructureConstants.TYPE_DEFAULT,
			serviceContext);
	}

	private DDMTemplate _fetchOrAddDDMTemplate(
			long classNameId, long classPK, long resourceClassNameId,
			String name, String type, String mode, String language,
			String script, boolean cacheable, ServiceContext serviceContext)
		throws Exception {

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getSiteDefault(), name
		).build();

		DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
			serviceContext.getScopeGroupId(), classNameId, _getKey(name));

		if (ddmTemplate == null) {
			ddmTemplate = _ddmTemplateLocalService.addTemplate(
				null, serviceContext.getUserId(),
				serviceContext.getScopeGroupId(), classNameId, classPK,
				resourceClassNameId, _getKey(name), nameMap, null, type, mode,
				language, script, cacheable, false, StringPool.BLANK, null,
				serviceContext);
		}
		else {
			ddmTemplate = _ddmTemplateLocalService.updateTemplate(
				serviceContext.getUserId(), ddmTemplate.getTemplateId(),
				classPK, nameMap, null, type, mode, language, script, cacheable,
				serviceContext);
		}

		return ddmTemplate;
	}

	private FileEntry _fetchOrAddFileEntry(
			ClassLoader classLoader, String dependenciesFilePath,
			String fileName, ServiceContext serviceContext)
		throws Exception {

		FileEntry fileEntry = null;

		try {
			fileEntry = _dlAppLocalService.getFileEntry(
				serviceContext.getScopeGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		if (fileEntry != null) {
			return fileEntry;
		}

		String filePath = dependenciesFilePath + fileName;

		InputStream inputStream = classLoader.getResourceAsStream(filePath);

		String mimeType = MimeTypesUtil.getContentType(fileName);

		byte[] byteArray = _file.getBytes(inputStream);

		return _dlAppLocalService.addFileEntry(
			null, serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName, mimeType,
			fileName, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			byteArray, null, null, null, serviceContext);
	}

	private long _getAssetEntryId(
			String articleId, ServiceContext serviceContext)
		throws Exception {

		if (Validator.isNull(articleId)) {
			return 0;
		}

		JournalArticle journalArticle =
			_journalArticleLocalService.fetchArticle(
				serviceContext.getScopeGroupId(), articleId);

		if (journalArticle == null) {
			return 0;
		}

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		if (assetEntry == null) {
			return 0;
		}

		return assetEntry.getEntryId();
	}

	private String _getKey(String name) {
		name = StringUtil.replace(name, CharPool.SPACE, CharPool.DASH);

		name = StringUtil.toUpperCase(name);

		return name;
	}

	private String _getNormalizedContent(
			String content, ClassLoader classLoader,
			String dependenciesFilePath, ServiceContext serviceContext)
		throws Exception {

		content = StringUtil.replace(
			content, GROUP_ID_PLACEHOLDER,
			String.valueOf(serviceContext.getScopeGroupId()));

		content = StringUtil.replace(
			content, LOCALE_PLACEHOLDER,
			LocaleUtil.toLanguageId(serviceContext.getLocale()));

		content = _replaceJournalArticleImages(
			content, _journalArticleHTMLImagePattern,
			fileEntry -> {
				String previewURL = DLURLHelperUtil.getDownloadURL(
					fileEntry, fileEntry.getLatestFileVersion(), null,
					StringPool.BLANK, false, false);

				return String.format(
					IMG_TAG, previewURL,
					String.valueOf(fileEntry.getFileEntryId()));
			},
			classLoader, dependenciesFilePath, serviceContext);

		content = _replaceJournalArticleImages(
			content, _journalArticleJSONImagePattern,
			fileEntry -> {
				JSONObject jsonObject = _jsonFactory.createJSONObject();

				jsonObject.put(
					"alt", fileEntry.getTitle()
				).put(
					"groupId", fileEntry.getGroupId()
				).put(
					"name", fileEntry.getFileName()
				).put(
					"title", fileEntry.getTitle()
				).put(
					"type", "document"
				).put(
					"url",
					_portletFileRepository.getDownloadPortletFileEntryURL(
						serviceContext.getThemeDisplay(), fileEntry,
						StringPool.BLANK)
				).put(
					"uuid", fileEntry.getUuid()
				);

				return jsonObject.toString();
			},
			classLoader, dependenciesFilePath, serviceContext);

		return content;
	}

	private Role _getRole(String name, int type, ServiceContext serviceContext)
		throws PortalException {

		Role role = _roleLocalService.fetchRole(
			serviceContext.getCompanyId(), name);

		if (role == null) {
			role = _roleLocalService.addRole(
				null, serviceContext.getUserId(), null, 0, name,
				HashMapBuilder.put(
					serviceContext.getLocale(), name
				).build(),
				null, type, null, serviceContext);
		}

		return role;
	}

	private String _replaceJournalArticleImages(
			String content, Pattern pattern,
			UnsafeFunction<FileEntry, String, Exception>
				replacementUnsafeFunction,
			ClassLoader classLoader, String dependenciesFilePath,
			ServiceContext serviceContext)
		throws Exception {

		StringBuffer sb = new StringBuffer();

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String fileName = matcher.group(1);

			FileEntry fileEntry = _fetchOrAddFileEntry(
				classLoader, dependenciesFilePath, fileName, serviceContext);

			String replacement = replacementUnsafeFunction.apply(fileEntry);

			matcher.appendReplacement(sb, replacement);
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private void _setLocalizedValues(
			PortletPreferences portletPreferences,
			ResourceBundleLoader resourceBundleLoader, long groupId, String key,
			String value)
		throws Exception {

		for (Locale locale : _language.getAvailableLocales(groupId)) {
			ResourceBundle resourceBundle =
				resourceBundleLoader.loadResourceBundle(locale);

			portletPreferences.setValue(
				key + StringPool.UNDERLINE + _language.getLanguageId(locale),
				_language.get(resourceBundle, value));
		}
	}

	private void _setPortletLookAndFeel(
			JSONObject jsonObject, Layout layout, String portletId,
			ClassLoader classLoader)
		throws Exception {

		if (jsonObject == null) {
			return;
		}

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getPreferences(
				PortletPreferencesFactoryUtil.getPortletPreferencesIds(
					layout.getCompanyId(), layout.getGroupId(),
					layout.getPlid(), portletId,
					PortletPreferencesFactoryConstants.
						SETTINGS_SCOPE_PORTLET_INSTANCE));

		ResourceBundleLoader resourceBundleLoader =
			new AggregateResourceBundleLoader(
				new ClassResourceBundleLoader("content.Language", classLoader),
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader());

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			String value = jsonObject.getString(key);

			if (key.equals("portletSetupTitle")) {
				_setLocalizedValues(
					portletPreferences, resourceBundleLoader,
					layout.getGroupId(), key, value);
			}
			else {
				portletPreferences.setValue(key, value);
			}
		}

		portletPreferences.store();
	}

	private void _setPortletPreferences(
			JSONObject jsonObject, Layout layout, String portletId,
			ServiceContext serviceContext)
		throws Exception {

		if (jsonObject == null) {
			return;
		}

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getPreferences(
				PortletPreferencesFactoryUtil.getPortletPreferencesIds(
					layout.getGroupId(), 0, layout, portletId, false));

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			String value = jsonObject.getString(key);

			if (key.equals("assetEntryId")) {
				String articleId = jsonObject.getString("articleId");

				value = String.valueOf(
					_getAssetEntryId(articleId, serviceContext));
			}
			else if (key.equals("groupId")) {
				value = String.valueOf(serviceContext.getScopeGroupId());
			}

			portletPreferences.setValue(key, value);
		}

		portletPreferences.store();
	}

	private void _setThemeSettingProperties(
		Theme theme, UnicodeProperties typeSettingUnicodeProperties) {

		String device = "regular";

		_deleteThemeSettingsProperties(typeSettingUnicodeProperties, device);

		Map<String, ThemeSetting> themeSettings =
			theme.getConfigurableSettings();

		for (Map.Entry<String, ThemeSetting> entry : themeSettings.entrySet()) {
			ThemeSetting themeSetting = entry.getValue();

			String value = themeSetting.getValue();

			if (!value.equals(themeSetting.getValue())) {
				typeSettingUnicodeProperties.setProperty(
					ThemeSettingImpl.namespaceProperty(device, entry.getKey()),
					value);
			}
		}
	}

	private void _updateAction(
			Role role, String resource, String actionId, int scope,
			ServiceContext serviceContext)
		throws PortalException {

		if (scope == ResourceConstants.SCOPE_COMPANY) {
			_resourcePermissionLocalService.addResourcePermission(
				serviceContext.getCompanyId(), resource, scope,
				String.valueOf(role.getCompanyId()), role.getRoleId(),
				actionId);
		}
		else if (scope == ResourceConstants.SCOPE_GROUP_TEMPLATE) {
			_resourcePermissionLocalService.addResourcePermission(
				serviceContext.getCompanyId(), resource,
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				role.getRoleId(), actionId);
		}
		else if (scope == ResourceConstants.SCOPE_GROUP) {
			_resourcePermissionLocalService.removeResourcePermissions(
				serviceContext.getCompanyId(), resource,
				ResourceConstants.SCOPE_GROUP, role.getRoleId(), actionId);

			_resourcePermissionLocalService.addResourcePermission(
				serviceContext.getCompanyId(), resource,
				ResourceConstants.SCOPE_GROUP,
				String.valueOf(serviceContext.getScopeGroupId()),
				role.getRoleId(), actionId);
		}
	}

	private void _updateActions(
			Role role, JSONObject jsonObject, int scope,
			ServiceContext serviceContext)
		throws PortalException {

		String resource = jsonObject.getString("resource");
		JSONArray actionIdsJSONArray = jsonObject.getJSONArray("actionIds");

		for (int i = 0; i < actionIdsJSONArray.length(); i++) {
			String actionId = actionIdsJSONArray.getString(i);

			_updateAction(role, resource, actionId, scope, serviceContext);
		}
	}

	private DDMForm _updateDDMFormAvailableLocales(
		DDMForm ddmForm, Locale locale) {

		Set<Locale> availableLocales = ddmForm.getAvailableLocales();

		availableLocales.add(locale);

		ddmForm.setAvailableLocales(availableLocales);

		ddmForm.setDefaultLocale(locale);

		return ddmForm;
	}

	private Layout _updateLayoutLookAndFeel(
		JSONObject jsonObject, Layout layout) {

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			Set<String> typeSettingPropertiesKeys =
				typeSettingsUnicodeProperties.keySet();

			if (typeSettingPropertiesKeys.contains(key)) {
				typeSettingsUnicodeProperties.replace(
					key, jsonObject.getString(key));
			}
			else {
				typeSettingsUnicodeProperties.put(
					key, jsonObject.getString(key));
			}
		}

		layout.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		return layout;
	}

	private void _updatePermissions(
			long companyId, String name, String primKey, JSONArray jsonArray)
		throws Exception {

		if (jsonArray == null) {
			jsonArray = _jsonFactory.createJSONArray(
				"[{\"actionIds\": [\"VIEW\"], \"roleName\": \"Site Member\"," +
					"\"scope\": 4}]");
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			int scope = jsonObject.getInt("scope");

			String roleName = jsonObject.getString("roleName");

			Role role = _roleLocalService.getRole(companyId, roleName);

			String[] actionIds = new String[0];

			JSONArray actionIdsJSONArray = jsonObject.getJSONArray("actionIds");

			if (actionIdsJSONArray != null) {
				for (int j = 0; j < actionIdsJSONArray.length(); j++) {
					actionIds = ArrayUtil.append(
						actionIds, actionIdsJSONArray.getString(j));
				}
			}

			_resourcePermissionLocalService.setResourcePermissions(
				companyId, name, scope, primKey, role.getRoleId(), actionIds);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPFileImporterImpl.class);

	private static final Pattern _journalArticleHTMLImagePattern =
		Pattern.compile("\\[%([^\\[%]+)%\\]");
	private static final Pattern _journalArticleJSONImagePattern =
		Pattern.compile("\\[\\$([^\\[%]+)\\$\\]");

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private DDM _ddm;

	@Reference(target = "(ddm.form.deserializer.type=json)")
	private DDMFormDeserializer _ddmFormDeserializer;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private com.liferay.portal.kernel.util.File _file;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private ThemeLocalService _themeLocalService;

}