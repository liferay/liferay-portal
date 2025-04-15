/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.result.display.context;

import jakarta.portlet.PortletURL;

import java.io.Serializable;

import java.util.List;

/**
 * @author André de Oliveira
 */
public class SearchResultSummaryDisplayContext implements Serializable {

	public long getAssetEntryUserId() {
		return _assetEntryUserId;
	}

	public long getAssetRendererDownloadSize() {
		return _assetRendererDownloadSize;
	}

	public String getAssetRendererURLDownload() {
		return _assetRendererURLDownload;
	}

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public String getContent() {
		return _content;
	}

	public String getCreationDateString() {
		return _creationDateString;
	}

	public String getCreatorUserName() {
		return _creatorUserName;
	}

	public String getCreatorUserPortraitURLString() {
		return _creatorUserPortraitURLString;
	}

	public List<SearchResultFieldDisplayContext>
		getDocumentFormFieldDisplayContexts() {

		return _documentFormFieldDisplayContexts;
	}

	public String getFieldAssetCategoryIds() {
		return _fieldAssetCategoryIds;
	}

	public String getFieldAssetTagNames() {
		return _fieldAssetTagNames;
	}

	public List<SearchResultFieldDisplayContext> getFieldDisplayContexts() {
		return _fieldDisplayContexts;
	}

	public String getHighlightedTitle() {
		return _highlightedTitle;
	}

	public String getIconId() {
		return _iconId;
	}

	public String getLocaleLanguageId() {
		return _localeLanguageId;
	}

	public String getLocaleReminder() {
		return _localeReminder;
	}

	public String getModelResource() {
		return _modelResource;
	}

	public String getModifiedByUserName() {
		return _modifiedByUserName;
	}

	public String getModifiedByUserPortraitURLString() {
		return _modifiedByUserPortraitURLString;
	}

	public String getModifiedDateString() {
		return _modifiedDateString;
	}

	public String getPathThemeImages() {
		return _pathThemeImages;
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public String getPublishedDateString() {
		return _publishedDateString;
	}

	public String getThumbnailURLString() {
		return _thumbnailURLString;
	}

	public String getTitle() {
		return _title;
	}

	public String getUserPortraitURLString() {
		return _userPortraitURLString;
	}

	public String getViewURL() {
		return _viewURL;
	}

	public boolean isAssetCategoriesOrTagsVisible() {
		return _assetCategoriesOrTagsVisible;
	}

	public boolean isAssetRendererURLDownloadVisible() {
		return _assetRendererURLDownloadVisible;
	}

	public boolean isContentVisible() {
		return _contentVisible;
	}

	public boolean isCreationDateVisible() {
		return _creationDateVisible;
	}

	public boolean isCreatorUserPortraitVisible() {
		return _creatorUserPortraitVisible;
	}

	public boolean isCreatorVisible() {
		return _creatorVisible;
	}

	public boolean isDocumentFormVisible() {
		return _documentFormVisible;
	}

	public boolean isFieldsVisible() {
		return _fieldsVisible;
	}

	public boolean isIconVisible() {
		return _iconVisible;
	}

	public boolean isLocaleReminderVisible() {
		return _localeReminderVisible;
	}

	public boolean isModelResourceVisible() {
		return _modelResourceVisible;
	}

	public boolean isModifiedByUserNameVisible() {
		return _modifiedByUserNameVisible;
	}

	public boolean isModifiedByUserPortraitVisible() {
		return _modifiedByUserPortraitVisible;
	}

	public boolean isModifiedDateVisible() {
		return _modifiedDateVisible;
	}

	public boolean isPublishedDateVisible() {
		return _publishedDateVisible;
	}

	public boolean isTemporarilyUnavailable() {
		return _temporarilyUnavailable;
	}

	public boolean isThumbnailVisible() {
		return _thumbnailVisible;
	}

	public boolean isUserPortraitVisible() {
		return _userPortraitVisible;
	}

	public void setAssetCategoriesOrTagsVisible(
		boolean assetCategoriesOrTagsVisible) {

		_assetCategoriesOrTagsVisible = assetCategoriesOrTagsVisible;
	}

	public void setAssetEntryUserId(long assetEntryUserId) {
		_assetEntryUserId = assetEntryUserId;
	}

	public void setAssetRendererDownloadSize(long assetRendererDownloadSize) {
		_assetRendererDownloadSize = assetRendererDownloadSize;
	}

	public void setAssetRendererURLDownload(String assetRendererURLDownload) {
		_assetRendererURLDownload = assetRendererURLDownload;
	}

	public void setAssetRendererURLDownloadVisible(
		boolean assetRendererURLDownloadVisible) {

		_assetRendererURLDownloadVisible = assetRendererURLDownloadVisible;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setContent(String content) {
		_content = content;
	}

	public void setContentVisible(boolean contentVisible) {
		_contentVisible = contentVisible;
	}

	public void setCreationDateString(String creationDateString) {
		_creationDateString = creationDateString;
	}

	public void setCreationDateVisible(boolean creationDateVisible) {
		_creationDateVisible = creationDateVisible;
	}

	public void setCreatorUserName(String creatorUserName) {
		_creatorUserName = creatorUserName;
	}

	public void setCreatorUserPortraitURLString(
		String creatorUserPortraitURLString) {

		_creatorUserPortraitURLString = creatorUserPortraitURLString;
	}

	public void setCreatorUserPortraitVisible(
		boolean creatorUserPortraitVisible) {

		_creatorUserPortraitVisible = creatorUserPortraitVisible;
	}

	public void setCreatorVisible(boolean writtenByVisible) {
		_creatorVisible = writtenByVisible;
	}

	public void setDocumentFormFieldDisplayContexts(
		List<SearchResultFieldDisplayContext>
			documentFormFieldDisplayContexts) {

		_documentFormFieldDisplayContexts = documentFormFieldDisplayContexts;
	}

	public void setDocumentFormVisible(boolean documentFormVisible) {
		_documentFormVisible = documentFormVisible;
	}

	public void setFieldAssetCategoryIds(String fieldAssetCategoryIds) {
		_fieldAssetCategoryIds = fieldAssetCategoryIds;
	}

	public void setFieldAssetTagNames(String fieldAssetTagNames) {
		_fieldAssetTagNames = fieldAssetTagNames;
	}

	public void setFieldDisplayContexts(
		List<SearchResultFieldDisplayContext> fieldDisplayContexts) {

		_fieldDisplayContexts = fieldDisplayContexts;
	}

	public void setFieldsVisible(boolean fieldsVisible) {
		_fieldsVisible = fieldsVisible;
	}

	public void setHighlightedTitle(String highlightedTitle) {
		_highlightedTitle = highlightedTitle;
	}

	public void setIconId(String iconId) {
		_iconId = iconId;
	}

	public void setIconVisible(boolean iconVisible) {
		_iconVisible = iconVisible;
	}

	public void setLocaleLanguageId(String localeLanguageId) {
		_localeLanguageId = localeLanguageId;
	}

	public void setLocaleReminder(String localeReminder) {
		_localeReminder = localeReminder;
	}

	public void setLocaleReminderVisible(boolean localeReminderVisible) {
		_localeReminderVisible = localeReminderVisible;
	}

	public void setModelResource(String modelResource) {
		_modelResource = modelResource;
	}

	public void setModelResourceVisible(boolean modelResourceVisible) {
		_modelResourceVisible = modelResourceVisible;
	}

	public void setModifiedByUserName(String modifiedByUserName) {
		_modifiedByUserName = modifiedByUserName;
	}

	public void setModifiedByUserNameVisible(
		boolean modifiedByUserNameVisible) {

		_modifiedByUserNameVisible = modifiedByUserNameVisible;
	}

	public void setModifiedByUserPortraitURLString(
		String modifiedByUserPortraitURLString) {

		_modifiedByUserPortraitURLString = modifiedByUserPortraitURLString;
	}

	public void setModifiedByUserPortraitVisible(
		boolean modifiedByUserPortraitVisible) {

		_modifiedByUserPortraitVisible = modifiedByUserPortraitVisible;
	}

	public void setModifiedDateString(String modifiedDateString) {
		_modifiedDateString = modifiedDateString;
	}

	public void setModifiedDateVisible(boolean modifiedDateVisible) {
		_modifiedDateVisible = modifiedDateVisible;
	}

	public void setPathThemeImages(String pathThemeImages) {
		_pathThemeImages = pathThemeImages;
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	public void setPublishedDateString(String publishedDateString) {
		_publishedDateString = publishedDateString;
	}

	public void setPublishedDateVisible(boolean publishedDateVisible) {
		_publishedDateVisible = publishedDateVisible;
	}

	public void setTemporarilyUnavailable(boolean temporarilyUnavailable) {
		_temporarilyUnavailable = temporarilyUnavailable;
	}

	public void setThumbnailURLString(String thumbnailURLString) {
		_thumbnailURLString = thumbnailURLString;
	}

	public void setThumbnailVisible(boolean thumbnailVisible) {
		_thumbnailVisible = thumbnailVisible;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setUserPortraitURLString(String userPortraitURLString) {
		_userPortraitURLString = userPortraitURLString;
	}

	public void setUserPortraitVisible(boolean userPortraitVisible) {
		_userPortraitVisible = userPortraitVisible;
	}

	public void setViewURL(String viewURL) {
		_viewURL = viewURL;
	}

	private boolean _assetCategoriesOrTagsVisible;
	private long _assetEntryUserId;
	private long _assetRendererDownloadSize;
	private String _assetRendererURLDownload;
	private boolean _assetRendererURLDownloadVisible;
	private String _className;
	private long _classPK;
	private String _content;
	private boolean _contentVisible;
	private String _creationDateString;
	private boolean _creationDateVisible;
	private String _creatorUserName;
	private String _creatorUserPortraitURLString;
	private boolean _creatorUserPortraitVisible;
	private boolean _creatorVisible;
	private List<SearchResultFieldDisplayContext>
		_documentFormFieldDisplayContexts;
	private boolean _documentFormVisible;
	private String _fieldAssetCategoryIds;
	private String _fieldAssetTagNames;
	private List<SearchResultFieldDisplayContext> _fieldDisplayContexts;
	private boolean _fieldsVisible;
	private String _highlightedTitle;
	private String _iconId;
	private boolean _iconVisible;
	private String _localeLanguageId;
	private String _localeReminder;
	private boolean _localeReminderVisible;
	private String _modelResource;
	private boolean _modelResourceVisible;
	private String _modifiedByUserName;
	private boolean _modifiedByUserNameVisible;
	private String _modifiedByUserPortraitURLString;
	private boolean _modifiedByUserPortraitVisible;
	private String _modifiedDateString;
	private boolean _modifiedDateVisible;
	private String _pathThemeImages;
	private PortletURL _portletURL;
	private String _publishedDateString;
	private boolean _publishedDateVisible;
	private boolean _temporarilyUnavailable;
	private String _thumbnailURLString;
	private boolean _thumbnailVisible;
	private String _title;
	private String _userPortraitURLString;
	private boolean _userPortraitVisible;
	private String _viewURL;

}