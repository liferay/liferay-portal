/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.helper;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.processor.ImageProcessorUtil;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.journal.constants.JournalFeedConstants;
import com.liferay.journal.exception.NoSuchFeedException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.model.JournalFeed;
import com.liferay.journal.service.JournalFeedLocalService;
import com.liferay.journal.util.JournalContent;
import com.liferay.journal.web.internal.util.JournalSearcherUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.rss.export.RSSExporter;
import com.liferay.rss.model.SyndContent;
import com.liferay.rss.model.SyndEnclosure;
import com.liferay.rss.model.SyndEntry;
import com.liferay.rss.model.SyndFeed;
import com.liferay.rss.model.SyndLink;
import com.liferay.rss.model.SyndModelFactory;
import com.liferay.rss.util.RSSUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.ResourceURL;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(service = JournalRSSHelper.class)
public class JournalRSSHelper {

	public List<SyndEnclosure> getDLEnclosures(String portalURL, String url) {
		List<SyndEnclosure> syndEnclosures = new ArrayList<>();

		FileEntry fileEntry = getFileEntry(url);

		if (fileEntry == null) {
			return syndEnclosures;
		}

		SyndEnclosure syndEnclosure = _syndModelFactory.createSyndEnclosure();

		syndEnclosure.setLength(fileEntry.getSize());
		syndEnclosure.setType(fileEntry.getMimeType());
		syndEnclosure.setUrl(portalURL + url);

		syndEnclosures.add(syndEnclosure);

		return syndEnclosures;
	}

	public List<SyndLink> getDLLinks(String portalURL, String url) {
		List<SyndLink> syndLinks = new ArrayList<>();

		FileEntry fileEntry = getFileEntry(url);

		if (fileEntry == null) {
			return syndLinks;
		}

		SyndLink syndLink = _syndModelFactory.createSyndLink();

		syndLink.setHref(portalURL + url);
		syndLink.setLength(fileEntry.getSize());
		syndLink.setRel("enclosure");
		syndLink.setType(fileEntry.getMimeType());

		syndLinks.add(syndLink);

		return syndLinks;
	}

	public FileEntry getFileEntry(String url) {
		FileEntry fileEntry = null;

		Map<String, String[]> parameters =
			HttpComponentsUtil.parameterMapFromString(
				HttpComponentsUtil.getQueryString(url));

		if (url.startsWith("/documents/")) {
			String[] pathArray = StringUtil.split(url, CharPool.SLASH);

			String uuid = null;
			long groupId = GetterUtil.getLong(pathArray[2]);
			long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
			String title = null;

			if (pathArray.length == 4) {
				uuid = pathArray[3];
			}
			else if (pathArray.length == 5) {
				folderId = GetterUtil.getLong(pathArray[3]);
				title = HttpComponentsUtil.decodeURL(pathArray[4]);
			}
			else if (pathArray.length > 5) {
				uuid = pathArray[5];
			}

			try {
				if (Validator.isNotNull(uuid)) {
					fileEntry = _dlAppLocalService.getFileEntryByUuidAndGroupId(
						uuid, groupId);
				}
				else {
					fileEntry = _dlAppLocalService.getFileEntry(
						groupId, folderId, title);
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}
		else if (parameters.containsKey("folderId") &&
				 parameters.containsKey("name")) {

			try {
				long fileEntryId = GetterUtil.getLong(
					parameters.get("fileEntryId")[0]);

				fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}
		else if (parameters.containsKey("uuid") &&
				 parameters.containsKey("groupId")) {

			try {
				String uuid = parameters.get("uuid")[0];
				long groupId = GetterUtil.getLong(parameters.get("groupId")[0]);

				fileEntry = _dlAppLocalService.getFileEntryByUuidAndGroupId(
					uuid, groupId);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}

		return fileEntry;
	}

	public List<SyndEnclosure> getIGEnclosures(String portalURL, String url) {
		List<SyndEnclosure> syndEnclosures = new ArrayList<>();

		Object[] imageProperties = _getImageProperties(url);

		if (imageProperties == null) {
			return syndEnclosures;
		}

		SyndEnclosure syndEnclosure = _syndModelFactory.createSyndEnclosure();

		syndEnclosure.setLength((Long)imageProperties[1]);
		syndEnclosure.setType(
			MimeTypesUtil.getExtensionContentType(
				imageProperties[0].toString()));
		syndEnclosure.setUrl(portalURL + url);

		syndEnclosures.add(syndEnclosure);

		return syndEnclosures;
	}

	public List<SyndLink> getIGLinks(String portalURL, String url) {
		List<SyndLink> syndLinks = new ArrayList<>();

		Object[] imageProperties = _getImageProperties(url);

		if (imageProperties == null) {
			return syndLinks;
		}

		SyndLink syndLink = _syndModelFactory.createSyndLink();

		syndLink.setHref(portalURL + url);
		syndLink.setLength((Long)imageProperties[1]);
		syndLink.setRel("enclosure");
		syndLink.setType(
			MimeTypesUtil.getExtensionContentType(
				imageProperties[0].toString()));

		syndLinks.add(syndLink);

		return syndLinks;
	}

	public Image getImage(String url) {
		Image image = null;

		Map<String, String[]> parameters =
			HttpComponentsUtil.parameterMapFromString(
				HttpComponentsUtil.getQueryString(url));

		if (parameters.containsKey("image_id") ||
			parameters.containsKey("img_id") ||
			parameters.containsKey("i_id")) {

			try {
				long imageId = 0;

				if (parameters.containsKey("image_id")) {
					imageId = GetterUtil.getLong(parameters.get("image_id")[0]);
				}
				else if (parameters.containsKey("img_id")) {
					imageId = GetterUtil.getLong(parameters.get("img_id")[0]);
				}
				else if (parameters.containsKey("i_id")) {
					imageId = GetterUtil.getLong(parameters.get("i_id")[0]);
				}

				image = _imageLocalService.getImage(imageId);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}

		return image;
	}

	public byte[] getRSS(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		JournalFeed feed = null;

		long id = ParamUtil.getLong(resourceRequest, "id");

		if (id > 0) {
			try {
				feed = _journalFeedLocalService.getFeed(id);
			}
			catch (NoSuchFeedException noSuchFeedException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFeedException);
				}

				// Backward compatibility with old URLs

				feed = _journalFeedLocalService.getFeed(
					themeDisplay.getScopeGroupId(), String.valueOf(id));
			}
		}
		else {
			long groupId = ParamUtil.getLong(resourceRequest, "groupId");
			String feedId = ParamUtil.getString(resourceRequest, "feedId");

			feed = _journalFeedLocalService.getFeed(groupId, feedId);
		}

		String rss = _exportToRSS(
			resourceRequest, resourceResponse, feed,
			_language.getLanguageId(resourceRequest), themeDisplay);

		return rss.getBytes(StringPool.UTF8);
	}

	private String _exportToRSS(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			JournalFeed feed, String languageId, ThemeDisplay themeDisplay)
		throws Exception {

		SyndFeed syndFeed = _syndModelFactory.createSyndFeed();

		syndFeed.setDescription(feed.getDescription());

		List<SyndEntry> syndEntries = new ArrayList<>();

		syndFeed.setEntries(syndEntries);

		SearchResponse searchResponse =
			JournalSearcherUtil.searchJournalArticles(
				searchContext -> _populateSearchContext(feed, searchContext));

		List<JournalArticle> articles =
			JournalSearcherUtil.transformJournalArticles(
				searchResponse.getDocuments71());

		if (_log.isDebugEnabled()) {
			_log.debug("Syndicating " + articles.size() + " articles");
		}

		for (JournalArticle article : articles) {
			SyndEntry syndEntry = _syndModelFactory.createSyndEntry();

			syndEntry.setAuthor(_portal.getUserName(article));

			SyndContent syndContent = _syndModelFactory.createSyndContent();

			syndContent.setType(RSSUtil.ENTRY_TYPE_DEFAULT);

			String value = article.getDescription(languageId);

			try {
				value = _processContent(
					resourceRequest, resourceResponse, feed, article,
					languageId, themeDisplay, syndEntry, syndContent);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}

			syndContent.setValue(value);

			syndEntry.setDescription(syndContent);

			String link = _getEntryURL(
				resourceRequest, feed, article, themeDisplay);

			syndEntry.setLink(link);

			syndEntry.setPublishedDate(article.getDisplayDate());
			syndEntry.setTitle(article.getTitle(languageId));
			syndEntry.setUpdatedDate(article.getModifiedDate());
			syndEntry.setUri(link);

			syndEntries.add(syndEntry);
		}

		syndFeed.setFeedType(
			feed.getFeedFormat() + "_" + feed.getFeedVersion());

		SyndLink selfSyndLink = _syndModelFactory.createSyndLink();

		ResourceURL feedURL = resourceResponse.createResourceURL();

		feedURL.setCacheability(ResourceURL.FULL);
		feedURL.setParameter("groupId", String.valueOf(feed.getGroupId()));
		feedURL.setParameter("feedId", String.valueOf(feed.getFeedId()));
		feedURL.setResourceID("/journal/rss");

		selfSyndLink.setHref(feedURL.toString());

		selfSyndLink.setRel("self");

		syndFeed.setLinks(ListUtil.fromArray(selfSyndLink));

		syndFeed.setPublishedDate(new Date());
		syndFeed.setTitle(feed.getName());
		syndFeed.setUri(feedURL.toString());

		return _rssExporter.export(syndFeed);
	}

	private String _getEntryURL(
			ResourceRequest resourceRequest, JournalFeed feed,
			JournalArticle article, ThemeDisplay themeDisplay)
		throws Exception {

		AssetRendererFactory<JournalArticle> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				JournalArticle.class);

		AssetRenderer<JournalArticle> assetRenderer =
			assetRendererFactory.getAssetRenderer(article.getResourcePrimKey());

		String viewInContextURL = assetRenderer.getURLViewInContext(
			themeDisplay, StringPool.BLANK);

		if (Validator.isNotNull(viewInContextURL)) {
			return viewInContextURL;
		}

		String portletId = feed.getTargetPortletId();

		if (Validator.isNull(portletId)) {
			return StringPool.BLANK;
		}

		long plid = _portal.getPlidFromFriendlyURL(
			feed.getCompanyId(), feed.getTargetLayoutFriendlyUrl());

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				resourceRequest, portletId, plid, PortletRequest.RENDER_PHASE)
		).setParameter(
			"articleId", article.getArticleId()
		).setParameter(
			"groupId", article.getGroupId()
		).buildString();
	}

	private Object[] _getImageProperties(String url) {
		String type = null;
		long size = 0;

		Image image = getImage(url);

		if (image != null) {
			type = image.getType();
			size = image.getSize();
		}
		else {
			FileEntry fileEntry = getFileEntry(url);

			Set<String> imageMimeTypes = ImageProcessorUtil.getImageMimeTypes();

			if ((fileEntry != null) &&
				imageMimeTypes.contains(fileEntry.getMimeType())) {

				type = fileEntry.getExtension();
				size = fileEntry.getSize();
			}
		}

		if (Validator.isNotNull(type)) {
			return new Object[] {type, size};
		}

		return null;
	}

	private void _populateSearchContext(
		JournalFeed feed, SearchContext searchContext) {

		List<AssetCategory> assetCategories =
			_assetCategoryLocalService.getCategories(
				JournalFeed.class.getName(), feed.getId());

		if (!assetCategories.isEmpty()) {
			AssetCategory assetCategory = assetCategories.get(0);

			searchContext.setAndSearch(true);
			searchContext.setAssetCategoryIds(
				new long[] {assetCategory.getCategoryId()});
		}

		Map<String, Serializable> attributes = searchContext.getAttributes();

		attributes.put(Field.STATUS, WorkflowConstants.STATUS_APPROVED);

		if (Validator.isNotNull(feed.getDDMTemplateKey())) {
			attributes.put("ddmTemplateKey", feed.getDDMTemplateKey());
		}

		attributes.put("head", true);

		searchContext.setAttributes(attributes);

		if (feed.getDDMStructureId() > 0) {
			DDMStructure ddmStructure =
				_ddmStructureLocalService.fetchStructure(
					feed.getDDMStructureId());

			if (ddmStructure != null) {
				searchContext.setClassTypeIds(
					new long[] {ddmStructure.getStructureId()});
			}
		}

		searchContext.setCompanyId(feed.getCompanyId());
		searchContext.setEnd(feed.getDelta());
		searchContext.setGroupIds(new long[] {feed.getGroupId()});

		String fieldName = "modified";

		if (StringUtil.equalsIgnoreCase(feed.getOrderByCol(), "display-date")) {
			fieldName = "displayDate";
		}

		searchContext.setSorts(
			new Sort(
				fieldName, Sort.LONG_TYPE,
				StringUtil.equalsIgnoreCase(feed.getOrderByType(), "desc")));
		searchContext.setStart(0);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);
	}

	private String _processContent(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			JournalFeed feed, JournalArticle article, String languageId,
			ThemeDisplay themeDisplay, SyndEntry syndEntry,
			SyndContent syndContent)
		throws Exception {

		String content = article.getDescription(languageId);

		String contentField = feed.getContentField();

		if (contentField.equals(JournalFeedConstants.RENDERED_WEB_CONTENT)) {
			String ddmRendererTemplateKey = article.getDDMTemplateKey();

			if (Validator.isNotNull(feed.getDDMRendererTemplateKey())) {
				ddmRendererTemplateKey = feed.getDDMRendererTemplateKey();
			}

			JournalArticleDisplay articleDisplay = _journalContent.getDisplay(
				feed.getGroupId(), article.getArticleId(),
				ddmRendererTemplateKey, null, languageId, 1,
				new PortletRequestModel(resourceRequest, resourceResponse) {

					@Override
					public String toXML() {
						return _XML_REQUUEST;
					}

				},
				themeDisplay);

			if (articleDisplay != null) {
				content = articleDisplay.getContent();
			}
		}
		else if (!contentField.equals(
					JournalFeedConstants.WEB_CONTENT_DESCRIPTION)) {

			DDMFormValues ddmFormValues = article.getDDMFormValues();

			Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
				ddmFormValues.getDDMFormFieldValuesMap(true);

			List<DDMFormFieldValue> ddmFormFieldValues =
				ddmFormFieldValuesMap.get(contentField);

			if (ddmFormFieldValues.isEmpty()) {
				return content;
			}

			DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

			Value value = ddmFormFieldValue.getValue();

			if (Objects.equals(
					ddmFormFieldValue.getType(),
					DDMFormFieldTypeConstants.DOCUMENT_LIBRARY) ||
				Objects.equals(
					ddmFormFieldValue.getType(),
					DDMFormFieldTypeConstants.IMAGE)) {

				JSONObject jsonObject = _jsonFactory.createJSONObject(
					value.getString(_language.getLocale(languageId)));

				String uuid = jsonObject.getString("uuid");
				long groupId = jsonObject.getLong("groupId");

				if (Validator.isNotNull(uuid) && (groupId > 0)) {
					FileEntry fileEntry =
						_dlAppLocalService.getFileEntryByUuidAndGroupId(
							uuid, groupId);

					String url = _dlURLHelper.getPreviewURL(
						fileEntry, fileEntry.getFileVersion(), null,
						StringPool.BLANK, false, true);

					url = _processURL(feed, url, themeDisplay, syndEntry);

					content = StringBundler.concat(
						content, "<br /><br /><a href=\"",
						themeDisplay.getURLPortal(), url, "\" />");
				}
			}
			else if (Objects.equals(
						ddmFormFieldValue.getType(),
						DDMFormFieldTypeConstants.RICH_TEXT) ||
					 Objects.equals(
						 ddmFormFieldValue.getType(),
						 DDMFormFieldTypeConstants.TEXT)) {

				syndContent.setType("text");

				content = value.getString(_language.getLocale(languageId));
			}
			else {
				content = value.getString(_language.getLocale(languageId));
			}
		}

		return content;
	}

	private String _processURL(
		JournalFeed feed, String url, ThemeDisplay themeDisplay,
		SyndEntry syndEntry) {

		url = StringUtil.replace(
			url, new String[] {"@group_id@", "@image_path@", "@main_path@"},
			new String[] {
				String.valueOf(feed.getGroupId()), themeDisplay.getPathImage(),
				themeDisplay.getPathMain()
			});

		List<SyndEnclosure> syndEnclosures = getDLEnclosures(
			themeDisplay.getURLPortal(), url);

		syndEnclosures.addAll(
			getIGEnclosures(themeDisplay.getURLPortal(), url));

		syndEntry.setEnclosures(syndEnclosures);

		List<SyndLink> syndLinks = getDLLinks(themeDisplay.getURLPortal(), url);

		syndLinks.addAll(getIGLinks(themeDisplay.getURLPortal(), url));

		syndEntry.setLinks(syndLinks);

		return url;
	}

	private static final String _XML_REQUUEST =
		"<request><parameters><parameter><name>rss</name><value>true</value>" +
			"</parameter></parameters></request>";

	private static final Log _log = LogFactoryUtil.getLog(
		JournalRSSHelper.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private JournalContent _journalContent;

	@Reference
	private JournalFeedLocalService _journalFeedLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private RSSExporter _rssExporter;

	@Reference
	private SyndModelFactory _syndModelFactory;

}