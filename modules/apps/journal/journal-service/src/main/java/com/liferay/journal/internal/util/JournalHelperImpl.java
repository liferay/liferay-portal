/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.util;

import com.liferay.diff.DiffHtml;
import com.liferay.diff.exception.CompareVersionsException;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.util.JournalHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.XPath;

import jakarta.portlet.PortletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tom Wang
 */
@Component(service = JournalHelper.class)
public class JournalHelperImpl implements JournalHelper {

	@Override
	public String createURLPattern(
			JournalArticle article, Locale locale, boolean privateLayout,
			String separator, ThemeDisplay themeDisplay)
		throws PortalException {

		StringBundler sb = new StringBundler(3);

		sb.append(
			_portal.getGroupFriendlyURL(
				_layoutSetLocalService.getLayoutSet(
					article.getGroupId(), privateLayout),
				themeDisplay, false, false));
		sb.append(separator);
		sb.append(article.getUrlTitle(locale));

		return sb.toString();
	}

	@Override
	public String diffHtml(
			long groupId, String articleId, double sourceVersion,
			double targetVersion, String languageId,
			PortletRequestModel portletRequestModel, ThemeDisplay themeDisplay)
		throws Exception {

		JournalArticle sourceArticle =
			JournalArticleLocalServiceUtil.getArticle(
				groupId, articleId, sourceVersion);

		if (!JournalArticleLocalServiceUtil.isRenderable(
				sourceArticle, portletRequestModel, themeDisplay)) {

			throw new CompareVersionsException(sourceVersion);
		}

		JournalArticleDisplay sourceArticleDisplay =
			JournalArticleLocalServiceUtil.getArticleDisplay(
				sourceArticle, null, Constants.VIEW, languageId, 1,
				portletRequestModel, themeDisplay);

		JournalArticle targetArticle =
			JournalArticleLocalServiceUtil.getArticle(
				groupId, articleId, targetVersion);

		if (!JournalArticleLocalServiceUtil.isRenderable(
				targetArticle, portletRequestModel, themeDisplay)) {

			throw new CompareVersionsException(targetVersion);
		}

		JournalArticleDisplay targetArticleDisplay =
			JournalArticleLocalServiceUtil.getArticleDisplay(
				targetArticle, null, Constants.VIEW, languageId, 1,
				portletRequestModel, themeDisplay);

		String diff = _diffHtml.diff(
			new UnsyncStringReader(sourceArticleDisplay.getContent()),
			new UnsyncStringReader(targetArticleDisplay.getContent()));

		if (!diff.matches(_MAP_REGEX)) {
			return diff;
		}

		try {
			return _processDiff(diff);
		}
		catch (DocumentException documentException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Invalid content:\n" + diff, documentException);
			}

			return diff;
		}
	}

	@Override
	public String getAbsolutePath(PortletRequest portletRequest, long folderId)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (folderId == JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return themeDisplay.translate("home");
		}

		JournalFolder folder = JournalFolderLocalServiceUtil.getFolder(
			folderId);

		List<JournalFolder> folders = folder.getAncestors();

		Collections.reverse(folders);

		StringBundler sb = new StringBundler((folders.size() * 3) + 5);

		sb.append(themeDisplay.translate("home"));
		sb.append(StringPool.SPACE);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		for (JournalFolder curFolder : folders) {
			if (permissionChecker.hasPermission(
					curFolder.getGroupId(), JournalFolder.class.getName(),
					curFolder.getFolderId(), ActionKeys.VIEW)) {

				sb.append(StringPool.RAQUO_CHAR);
				sb.append(StringPool.SPACE);
				sb.append(curFolder.getName());
				sb.append(StringPool.SPACE);
			}
			else {
				sb.append(StringPool.RAQUO_CHAR);
				sb.append(StringPool.SPACE);
				sb.append(StringPool.TRIPLE_PERIOD);
				sb.append(StringPool.SPACE);
			}
		}

		if (permissionChecker.hasPermission(
				folder.getGroupId(), JournalFolder.class.getName(),
				folder.getFolderId(), ActionKeys.VIEW)) {

			sb.append(StringPool.RAQUO_CHAR);
			sb.append(StringPool.SPACE);
			sb.append(folder.getName());
		}
		else {
			sb.append(StringPool.RAQUO_CHAR);
			sb.append(StringPool.SPACE);
			sb.append(StringPool.TRIPLE_PERIOD);
		}

		return sb.toString();
	}

	@Override
	public Layout getArticleLayout(String layoutUuid, long groupId) {
		if (Validator.isNull(layoutUuid)) {
			return null;
		}

		// The target page and the article must belong to the same group

		Layout layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layoutUuid, groupId, false);

		if (layout == null) {
			layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
				layoutUuid, groupId, true);
		}

		return layout;
	}

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public List<JournalArticle> getArticles(Hits hits) throws PortalException {
		List<Document> documents = hits.toList();

		List<JournalArticle> articles = new ArrayList<>(documents.size());

		for (Document document : documents) {
			String articleId = document.get(Field.ARTICLE_ID);
			long groupId = GetterUtil.getLong(
				document.get(Field.SCOPE_GROUP_ID));

			JournalArticle article =
				JournalArticleLocalServiceUtil.fetchLatestArticle(
					groupId, articleId, WorkflowConstants.STATUS_APPROVED);

			if (article == null) {
				articles = null;

				Indexer<JournalArticle> indexer =
					IndexerRegistryUtil.getIndexer(JournalArticle.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (articles != null) {
				articles.add(article);
			}
		}

		return articles;
	}

	@Override
	public int getRestrictionType(long folderId) {
		int restrictionType = JournalFolderConstants.RESTRICTION_TYPE_INHERIT;

		JournalFolder folder = JournalFolderLocalServiceUtil.fetchFolder(
			folderId);

		if (folder != null) {
			restrictionType = folder.getRestrictionType();
		}

		return restrictionType;
	}

	private List<String> _getAttributeValues(String content, Pattern pattern) {
		List<String> attributeValues = new ArrayList<>();

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			attributeValues.add(matcher.group(1));
		}

		return attributeValues;
	}

	private String _processDiff(String diff) throws Exception {
		com.liferay.portal.kernel.xml.Document document = SAXReaderUtil.read(
			diff);

		XPath xPathSelector = SAXReaderUtil.createXPath(
			"//div[@class='lfr-map']");

		List<Node> mapNodes = xPathSelector.selectNodes(document);

		for (Node mapNode : mapNodes) {
			Element mapElement = (Element)mapNode;

			Element spanElement = mapElement.element("span");

			if (spanElement == null) {
				continue;
			}

			String changes = HtmlUtil.stripHtml(
				spanElement.attributeValue("changes"));

			if (changes == null) {
				continue;
			}

			List<String> latitudes = _getAttributeValues(
				changes, _latitudePattern);

			String oldLatitude = latitudes.get(0);
			String newLatitude = latitudes.get(1);

			List<String> longitudes = _getAttributeValues(
				changes, _longitudePattern);

			String oldLongitude = longitudes.get(0);
			String newLongitude = longitudes.get(1);

			if (newLatitude.equals(oldLatitude) &&
				newLongitude.equals(oldLongitude)) {

				continue;
			}

			mapElement.addAttribute("style", "border: 2px solid #CFC;");

			Element oldMapElement = mapElement.createCopy();

			oldMapElement.addAttribute("data-latitude", oldLatitude);
			oldMapElement.addAttribute("data-longitude", oldLongitude);

			List<String> ids = _getAttributeValues(changes, _idPattern);

			oldMapElement.addAttribute("id", ids.get(0));

			oldMapElement.addAttribute("style", "border: 2px solid #FDC6C6;");

			Element parentElement = mapElement.getParent();

			List<Element> elements = parentElement.elements();

			elements.add(elements.indexOf(mapElement), oldMapElement);
		}

		return document.compactString();
	}

	private static final String _MAP_REGEX = ".*class=\"lfr-map\".*";

	private static final Log _log = LogFactoryUtil.getLog(
		JournalHelperImpl.class);

	private static final Pattern _idPattern = Pattern.compile(
		"id (_" + JournalPortletKeys.JOURNAL + "_[-0-9a-zA-Z_]+Map)");
	private static final Pattern _latitudePattern = Pattern.compile(
		"data-latitude (-?\\d+(?:\\.\\d+)?)");
	private static final Pattern _longitudePattern = Pattern.compile(
		"data-longitude (-?\\d+(?:\\.\\d+)?)");

	@Reference
	private DiffHtml _diffHtml;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private Portal _portal;

}