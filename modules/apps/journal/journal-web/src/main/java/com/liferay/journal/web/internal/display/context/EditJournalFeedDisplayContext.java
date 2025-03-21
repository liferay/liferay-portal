/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.journal.constants.JournalFeedConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalFeed;
import com.liferay.journal.web.internal.portlet.action.ActionUtil;
import com.liferay.journal.web.internal.security.permission.resource.JournalFeedPermission;
import com.liferay.journal.web.internal.security.permission.resource.JournalPermission;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.rss.util.RSSUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Lourdes Fernández Besada
 */
public class EditJournalFeedDisplayContext {

	public EditJournalFeedDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_itemSelector = (ItemSelector)httpServletRequest.getAttribute(
			ItemSelector.class.getName());
		_journalFeed = ActionUtil.getFeed(httpServletRequest);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAssetCategoriesSelectorURL() {
		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new InfoItemItemSelectorReturnType());
		itemSelectorCriterion.setItemType(AssetCategory.class.getName());

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_liferayPortletResponse.getNamespace() + "selectAssetCategory",
				itemSelectorCriterion)
		).buildString();
	}

	public long getAssetCategoryId() throws PortalException {
		if (_assetCategoryId != null) {
			return _assetCategoryId;
		}

		long assetCategoryId = 0;

		long[] assetCategoryIds = ParamUtil.getLongValues(
			_httpServletRequest, "assetCategoryIds", null);

		if (assetCategoryIds != null) {
			if (ArrayUtil.isNotEmpty(assetCategoryIds)) {
				assetCategoryId = assetCategoryIds[0];
			}
		}
		else if (_journalFeed != null) {
			AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(
				JournalFeed.class.getName(), _journalFeed.getId());

			assetCategoryIds = assetEntry.getCategoryIds();

			if (ArrayUtil.isNotEmpty(assetCategoryIds)) {
				assetCategoryId = assetCategoryIds[0];
			}
		}

		_assetCategoryId = assetCategoryId;

		return _assetCategoryId;
	}

	public String getAssetCategoryIds() throws PortalException {
		if (getAssetCategoryId() > 0) {
			return String.valueOf(getAssetCategoryId());
		}

		return StringPool.BLANK;
	}

	public String getAssetCategoryName() throws PortalException {
		if (_assetCategoryName != null) {
			return _assetCategoryName;
		}

		String assetCategoryName = StringPool.BLANK;

		if (getAssetCategoryId() > 0) {
			AssetCategory assetCategory =
				AssetCategoryLocalServiceUtil.fetchAssetCategory(
					getAssetCategoryId());

			if (assetCategory != null) {
				assetCategoryName = assetCategory.getTitle(
					_themeDisplay.getLocale());
			}
		}

		_assetCategoryName = assetCategoryName;

		return _assetCategoryName;
	}

	public String getContentField() {
		if (_contentField != null) {
			return _contentField;
		}

		String contentField = ParamUtil.getString(
			_httpServletRequest, "contentField");

		if (Validator.isNull(contentField) && (_journalFeed != null)) {
			contentField = _journalFeed.getContentField();
		}

		if (Validator.isNull(contentField) ||
			((_getDDMStructure() == null) &&
			 !contentField.equals(
				 JournalFeedConstants.WEB_CONTENT_DESCRIPTION) &&
			 !contentField.equals(JournalFeedConstants.RENDERED_WEB_CONTENT))) {

			contentField = JournalFeedConstants.WEB_CONTENT_DESCRIPTION;
		}

		_contentField = contentField;

		return _contentField;
	}

	public DDMForm getDDMForm() {
		DDMStructure ddmStructure = _getDDMStructure();

		if (ddmStructure == null) {
			return null;
		}

		return ddmStructure.getDDMForm();
	}

	public String getDDMRendererTemplateKey() {
		if (_ddmRendererTemplateKey != null) {
			return _ddmRendererTemplateKey;
		}

		String ddmRendererTemplateKey = ParamUtil.getString(
			_httpServletRequest, "ddmRendererTemplateKey");

		if (Validator.isNull(ddmRendererTemplateKey) &&
			(_journalFeed != null)) {

			ddmRendererTemplateKey = _journalFeed.getDDMRendererTemplateKey();
		}

		_ddmRendererTemplateKey = ddmRendererTemplateKey;

		return _ddmRendererTemplateKey;
	}

	public Long getDDMStructureId() {
		if (_ddmStructureId != null) {
			return _ddmStructureId;
		}

		long ddmStructureId = ParamUtil.getLong(
			_httpServletRequest, "ddmStructureId", -1);

		if ((ddmStructureId < 0) && (_journalFeed != null)) {
			ddmStructureId = _journalFeed.getDDMStructureId();
		}

		_ddmStructureId = ddmStructureId;

		return _ddmStructureId;
	}

	public String getDDMStructureName() {
		if (_ddmStructureName != null) {
			return _ddmStructureName;
		}

		DDMStructure ddmStructure = _getDDMStructure();

		String ddmStructureName = StringPool.BLANK;

		if (ddmStructure != null) {
			ddmStructureName = ddmStructure.getName(_themeDisplay.getLocale());
		}

		_ddmStructureName = ddmStructureName;

		return _ddmStructureName;
	}

	public String getDDMTemplateKey() {
		if (_ddmTemplateKey != null) {
			return _ddmTemplateKey;
		}

		String ddmTemplateKey = ParamUtil.getString(
			_httpServletRequest, "ddmTemplateKey");

		if (Validator.isNull(ddmTemplateKey) && (_journalFeed != null)) {
			ddmTemplateKey = _journalFeed.getDDMTemplateKey();
		}

		_ddmTemplateKey = ddmTemplateKey;

		return _ddmTemplateKey;
	}

	public List<DDMTemplate> getDDMTemplates() throws PortalException {
		DDMStructure ddmStructure = _getDDMStructure();

		if (ddmStructure == null) {
			return Collections.emptyList();
		}

		return DDMTemplateLocalServiceUtil.getTemplates(
			_themeDisplay.getScopeGroupId(), _getDDMStructureClassNameId(),
			ddmStructure.getStructureId(), true);
	}

	public String getFeedId() {
		if (_feedId != null) {
			return _feedId;
		}

		_feedId = BeanParamUtil.getString(
			_journalFeed, _httpServletRequest, "feedId");

		return _feedId;
	}

	public String getFeedType() {
		if (_feedType != null) {
			return _feedType;
		}

		_feedType = RSSUtil.getFeedType(
			BeanParamUtil.getString(
				_journalFeed, _httpServletRequest, "feedFormat",
				RSSUtil.FORMAT_DEFAULT),
			BeanParamUtil.getDouble(
				_journalFeed, _httpServletRequest, "feedVersion",
				RSSUtil.VERSION_DEFAULT));

		return _feedType;
	}

	public String getFeedURL() {
		if (_feedURL != null) {
			return _feedURL;
		}

		String feedURL = StringPool.BLANK;

		if (_journalFeed != null) {
			long targetLayoutPlid = PortalUtil.getPlidFromFriendlyURL(
				_journalFeed.getCompanyId(),
				_journalFeed.getTargetLayoutFriendlyUrl());

			feedURL = ResourceURLBuilder.createResourceURL(
				PortletURLFactoryUtil.create(
					_httpServletRequest, JournalPortletKeys.JOURNAL,
					targetLayoutPlid, PortletRequest.RESOURCE_PHASE)
			).setParameter(
				"groupId", getGroupId()
			).setParameter(
				"feedId", getFeedId()
			).setCacheability(
				ResourceURL.FULL
			).setResourceID(
				"/journal/rss"
			).buildString();
		}

		_feedURL = feedURL;

		return _feedURL;
	}

	public long getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		_groupId = BeanParamUtil.getLong(
			_journalFeed, _httpServletRequest, "groupId",
			_themeDisplay.getScopeGroupId());

		return _groupId;
	}

	public JournalFeed getJournalFeed() {
		return _journalFeed;
	}

	public String getNewFeedId() {
		if (_newFeedId != null) {
			return _newFeedId;
		}

		_newFeedId = ParamUtil.getString(_httpServletRequest, "newFeedId");

		return _newFeedId;
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		return _redirect;
	}

	public String getTitle() {
		if (_journalFeed != null) {
			return _journalFeed.getName();
		}

		return LanguageUtil.get(_httpServletRequest, "new-feed");
	}

	public boolean hasSavePermission() throws PortalException {
		if (_savePermission != null) {
			return _savePermission;
		}

		boolean savePermission;

		if (_journalFeed != null) {
			savePermission = JournalFeedPermission.contains(
				_themeDisplay.getPermissionChecker(), _journalFeed,
				ActionKeys.UPDATE);
		}
		else {
			savePermission = JournalPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), ActionKeys.ADD_FEED);
		}

		_savePermission = savePermission;

		return _savePermission;
	}

	private DDMStructure _getDDMStructure() {
		if (getDDMStructureId() == 0) {
			return null;
		}

		DDMStructure ddmStructure = DDMStructureLocalServiceUtil.fetchStructure(
			getDDMStructureId());

		if (ddmStructure == null) {
			DDMTemplate ddmTemplate = _getDDMTemplate();

			if (ddmTemplate == null) {
				return null;
			}

			ddmStructure = DDMStructureLocalServiceUtil.fetchStructure(
				ddmTemplate.getClassPK());
		}

		return ddmStructure;
	}

	private long _getDDMStructureClassNameId() {
		if (_ddmStructureClassNameId != null) {
			return _ddmStructureClassNameId;
		}

		_ddmStructureClassNameId = PortalUtil.getClassNameId(
			DDMStructure.class);

		return _ddmStructureClassNameId;
	}

	private DDMTemplate _getDDMTemplate() {
		if (Validator.isNull(getDDMTemplateKey())) {
			return null;
		}

		return DDMTemplateLocalServiceUtil.fetchTemplate(
			_themeDisplay.getSiteGroupId(), _getDDMStructureClassNameId(),
			getDDMTemplateKey(), true);
	}

	private Long _assetCategoryId;
	private String _assetCategoryName;
	private String _contentField;
	private String _ddmRendererTemplateKey;
	private Long _ddmStructureClassNameId;
	private Long _ddmStructureId;
	private String _ddmStructureName;
	private String _ddmTemplateKey;
	private String _feedId;
	private String _feedType;
	private String _feedURL;
	private Long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final JournalFeed _journalFeed;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _newFeedId;
	private String _redirect;
	private Boolean _savePermission;
	private final ThemeDisplay _themeDisplay;

}