/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.learn;

import com.liferay.data.engine.rest.client.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.client.resource.v2_0.DataDefinitionResource;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.client.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.headless.admin.taxonomy.client.resource.v1_0.TaxonomyVocabularyResource;
import com.liferay.headless.admin.user.client.dto.v1_0.Site;
import com.liferay.headless.admin.user.client.resource.v1_0.SiteResource;
import com.liferay.headless.delivery.client.dto.v1_0.ContentField;
import com.liferay.headless.delivery.client.dto.v1_0.ContentFieldValue;
import com.liferay.headless.delivery.client.dto.v1_0.StructuredContent;
import com.liferay.headless.delivery.client.dto.v1_0.StructuredContentFolder;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.StructuredContentFolderResource;
import com.liferay.headless.delivery.client.resource.v1_0.StructuredContentResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ext.admonition.AdmonitionExtension;
import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension;
import com.vladsch.flexmark.ext.aside.AsideExtension;
import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.media.tags.MediaTagsExtension;
import com.vladsch.flexmark.ext.superscript.SuperscriptExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterBlock;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterNode;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterVisitor;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterVisitorExt;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Block;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.TextCollectingVisitor;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.io.File;
import java.io.FileInputStream;

import java.net.URL;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import org.json.JSONArray;
import org.json.JSONObject;

import org.yaml.snakeyaml.Yaml;

/**
 * @author Brian Wing Shun Chan
 * @author Rich Sezov
 * @author Allen Ziegenfus
 */
public class Main {

	public static void main(String[] arguments) throws Exception {
		String lastestHashFileName = System.getenv(
			"LIFERAY_LEARN_ETC_CRON_LATEST_HASH_FILE_NAME");

		if (lastestHashFileName == null) {
			lastestHashFileName = ".latest_hash";
		}

		String liferayDataDefinitionKey = System.getenv(
			"LIFERAY_LEARN_ETC_CRON_LIFERAY_DATA_DEFINITION_KEY");

		if (liferayDataDefinitionKey == null) {
			liferayDataDefinitionKey = "LEARN-ARTICLE";
		}

		String liferaySiteFriendlyUrlPath = System.getenv(
			"LIFERAY_LEARN_ETC_CRON_LIFERAY_SITE_FRIENDLY_URL_PATH");

		if (liferaySiteFriendlyUrlPath == null) {
			liferaySiteFriendlyUrlPath = "liferay-learn";
		}

		String liferayUrl = System.getenv("LIFERAY_LEARN_ETC_CRON_LIFERAY_URL");

		if (liferayUrl == null) {
			liferayUrl = "http://localhost:8080";
		}

		String baseDir = System.getenv(
			"LIFERAY_LEARN_ETC_CRON_GIT_REPOSITORY_DIR");

		if (baseDir == null) {
			baseDir = "~/liferay-learn";
		}

		File baseDirFile = new File(baseDir);

		Main main = new Main(
			lastestHashFileName, liferayDataDefinitionKey,
			System.getenv("LIFERAY_LEARN_ETC_CRON_LIFERAY_OAUTH_CLIENT_ID"),
			System.getenv("LIFERAY_LEARN_ETC_CRON_LIFERAY_OAUTH_CLIENT_SECRET"),
			liferaySiteFriendlyUrlPath, new URL(liferayUrl), baseDirFile,
			GetterUtil.getBoolean(
				System.getenv("LIFERAY_LEARN_ETC_CRON_OFFLINE")),
			GetterUtil.getBoolean(
				System.getenv("LIFERAY_LEARN_ETC_SKIP_DIFF_CHECK")));

		String exceptionMessage = null;

		try {
			main.uploadToLiferay();
		}
		catch (Exception exception) {
			exceptionMessage = exception.getMessage();
		}

		sendSlackMessage(exceptionMessage);
	}

	public static void sendSlackMessage(String exceptionMessage)
		throws Exception {

		String slackEndpoint = System.getenv(
			"LIFERAY_LEARN_ETC_CRON_SLACK_ENDPOINT");

		if (slackEndpoint == null) {
			return;
		}

		HttpPost httpPost = new HttpPost(slackEndpoint);

		String slackMessage = StringBundler.concat(
			new Date(), " *", System.getenv("LCP_PROJECT_ID"), "*->*",
			System.getenv("LCP_SERVICE_ID"), "* <https://console.",
			System.getenv("LCP_INFRASTRUCTURE_DOMAIN"), "/projects/",
			System.getenv("LCP_PROJECT_ID"), "/logs?instanceId=",
			System.getenv("HOSTNAME"), "&logServiceId=",
			System.getenv("LCP_SERVICE_ID"), "|", System.getenv("HOSTNAME"),
			"> \n>");

		if (Validator.isNotNull(exceptionMessage)) {
			slackMessage +=
				":red-alert:Import job finished with return code 1\n>" +
					exceptionMessage;
		}
		else {
			slackMessage += ":sunflower:Import job finished with return code 0";
		}

		httpPost.setEntity(
			new StringEntity(
				StringBundler.concat(
					"{\"channel\": \"",
					System.getenv("LIFERAY_LEARN_ETC_CRON_SLACK_CHANNEL"),
					"\", \"icon_emoji\": \":robot_face:\", \"text\": \"",
					slackMessage, "\", \"username\": \"devopsbot\"}")));

		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		try (CloseableHttpClient closeableHttpClient =
				httpClientBuilder.build()) {

			closeableHttpClient.execute(httpPost);
		}
	}

	public Main(
			String latestHashFileName, String liferayDataDefinitionKey,
			String liferayOAuthClientId, String liferayOAuthClientSecret,
			String liferaySiteFriendlyUrlPath, URL liferayURL, File baseDir,
			boolean offline, boolean skipDiffCheck)
		throws Exception {

		_liferayOAuthClientId = liferayOAuthClientId;
		_liferayOAuthClientSecret = liferayOAuthClientSecret;
		_liferayURL = liferayURL;
		_offline = offline;
		_skipDiffCheck = skipDiffCheck;

		_lastestHashFileName = latestHashFileName;

		_baseDirName = baseDir.getCanonicalPath();

		_docsDirName = _baseDirName + "/docs";

		System.out.println("Liferay URL: " + _liferayURL);

		_readHashFromFile(baseDir);

		_addFileNames(_docsDirName);

		_getGitDiff(baseDir);

		_initFlexmark();

		if (_offline) {
			_globalSiteId = 0;
			_liferayContentStructureId = 0;
			_liferaySiteId = 0;
		}
		else {
			_initResourceBuilders(_getOAuthAuthorization());

			Site globalSite = _siteResource.getSiteByFriendlyUrlPath("global");

			_globalSiteId = globalSite.getId();

			Site liferaySite = _siteResource.getSiteByFriendlyUrlPath(
				liferaySiteFriendlyUrlPath);

			_liferaySiteId = liferaySite.getId();

			System.out.println("Liferay site ID: " + liferaySite.getId());
			System.out.println("Liferay site name: " + liferaySite.getName());

			DataDefinition dataDefinition =
				_dataDefinitionResource.
					getSiteDataDefinitionByContentTypeByDataDefinitionKey(
						liferaySite.getId(), "journal",
						liferayDataDefinitionKey);

			_liferayContentStructureId = dataDefinition.getId();

			_loadTaxonomyVocabularies();
		}
	}

	public void uploadToLiferay() throws Exception {
		long start = System.currentTimeMillis();

		int addedStructuredContentCount = 0;
		Set<Long> existingStructuredContentIds = new HashSet<>();
		Map<String, StructuredContent> externalReferenceCodeStructuredContents =
			new HashMap<>();
		Map<String, StructuredContent> friendlyUrlPathStructuredContents =
			new HashMap<>();
		Map<Long, StructuredContent> idStructuredContents = new HashMap<>();
		Set<Long> importedStructuredContentIds = new HashSet<>();
		int updatedStructuredContentCount = 0;

		List<StructuredContent> siteStructuredContents =
			_getSiteStructuredContents(_liferaySiteId);

		System.out.println(
			"Site has " + siteStructuredContents.size() +
				" structured contents");

		for (StructuredContent siteStructuredContent : siteStructuredContents) {
			if (siteStructuredContent.getContentStructureId() !=
					_liferayContentStructureId) {

				continue;
			}

			existingStructuredContentIds.add(siteStructuredContent.getId());
			externalReferenceCodeStructuredContents.put(
				siteStructuredContent.getExternalReferenceCode(),
				siteStructuredContent);
			friendlyUrlPathStructuredContents.put(
				siteStructuredContent.getFriendlyUrlPath(),
				siteStructuredContent);
			idStructuredContents.put(
				siteStructuredContent.getId(), siteStructuredContent);
		}

		for (String fileName : _fileNames) {
			if (!fileName.contains("/en/") || !fileName.endsWith(".md")) {
				continue;
			}

			if (_offline) {
				JSONObject jsonObject = new JSONObject(
					_toStructuredContent(fileName));

				_write(
					jsonObject.toString(4), "build/structured-content",
					new File(fileName));

				continue;
			}

			long delta = System.currentTimeMillis() - start;

			if (delta > (_oauthExpirationMillis - 100000)) {
				_initResourceBuilders(_getOAuthAuthorization());

				start = System.currentTimeMillis();
			}

			try {
				StructuredContent importedStructuredContent = null;

				StructuredContent structuredContent = _toStructuredContent(
					fileName);

				if (externalReferenceCodeStructuredContents.containsKey(
						structuredContent.getExternalReferenceCode())) {

					StructuredContent siteStructuredContent =
						externalReferenceCodeStructuredContents.get(
							structuredContent.getExternalReferenceCode());

					importedStructuredContentIds.add(
						siteStructuredContent.getId());

					String relativeFileName = StringUtil.removeSubstring(
						fileName, _baseDirName);

					if (!_diffFileNames.isEmpty() &&
						!_diffFileNames.contains(relativeFileName) &&
						!_skipDiffCheck) {

						System.out.println(
							"Skipping structured content (no diffs) " +
								structuredContent.getFriendlyUrlPath());

						continue;
					}

					if (StringUtil.equals(
							DigestUtils.md5Hex(new FileInputStream(fileName)),
							_getMD5Hex(siteStructuredContent)) &&
						!_skipDiffCheck) {

						System.out.println(
							"Skipping structured content (same md5Hex) " +
								structuredContent.getFriendlyUrlPath());

						continue;
					}

					System.out.println(
						"Updating structured content " +
							structuredContent.getFriendlyUrlPath());

					importedStructuredContent =
						_structuredContentResource.putStructuredContent(
							siteStructuredContent.getId(), structuredContent);

					_structuredContentResource.
						putStructuredContentPermissionsPage(
							importedStructuredContent.getId(),
							_getPermissions(
								fileName, importedStructuredContent.getId()));

					updatedStructuredContentCount++;
				}
				else {
					if (friendlyUrlPathStructuredContents.containsKey(
							structuredContent.getFriendlyUrlPath())) {

						StructuredContent siteStructuredContent =
							friendlyUrlPathStructuredContents.get(
								structuredContent.getFriendlyUrlPath());

						importedStructuredContentIds.add(
							siteStructuredContent.getId());

						String relativeFileName = StringUtil.removeSubstring(
							fileName, _baseDirName);

						if (!_diffFileNames.isEmpty() &&
							!_diffFileNames.contains(relativeFileName) &&
							!_skipDiffCheck) {

							System.out.println(
								"Skipping structured content " +
									structuredContent.getFriendlyUrlPath());

							continue;
						}

						System.out.println(
							"Deleting structured content " +
								structuredContent.getFriendlyUrlPath());

						_structuredContentResource.deleteStructuredContent(
							siteStructuredContent.getId());
					}

					String relativeFileName = StringUtil.removeSubstring(
						fileName, _baseDirName);

					if (!_diffFileNames.isEmpty() &&
						!_diffFileNames.contains(relativeFileName) &&
						!_skipDiffCheck) {

						System.out.println(
							"Skipping structured content " +
								structuredContent.getFriendlyUrlPath());

						continue;
					}

					System.out.println(
						"Adding structured content " +
							structuredContent.getFriendlyUrlPath());

					structuredContent.setPermissions(
						() -> _getPermissions(
							fileName, structuredContent.getId()));

					importedStructuredContent =
						_structuredContentResource.
							postStructuredContentFolderStructuredContent(
								structuredContent.
									getStructuredContentFolderId(),
								structuredContent);

					addedStructuredContentCount++;
				}

				if (!Objects.equals(
						importedStructuredContent.getFriendlyUrlPath(),
						structuredContent.getFriendlyUrlPath())) {

					_structuredContentResource.deleteStructuredContent(
						importedStructuredContent.getId());

					throw new Exception(
						"Modified friendly URL path " +
							importedStructuredContent.getFriendlyUrlPath());
				}
			}
			catch (Exception exception) {
				_error(fileName + ": " + exception.getMessage());
			}
		}

		existingStructuredContentIds.removeAll(importedStructuredContentIds);

		for (Long existingStructuredContentId : existingStructuredContentIds) {
			StructuredContent structuredContent = idStructuredContents.get(
				existingStructuredContentId);

			try {
				System.out.println(
					"Deleting orphaned structured content " +
						structuredContent.getFriendlyUrlPath());

				_structuredContentResource.deleteStructuredContent(
					existingStructuredContentId);
			}
			catch (Exception exception) {
				_error(
					structuredContent.getFriendlyUrlPath() + ": " +
						exception.getMessage());
			}
		}

		_saveHashToFile(new File(_baseDirName), _newHash);

		System.out.println(
			addedStructuredContentCount + " structured contents were added.");
		System.out.println(
			existingStructuredContentIds.size() +
				" structured contents were deleted.");
		System.out.println(
			updatedStructuredContentCount +
				" structured contents were updated.");

		if (!_warningMessages.isEmpty()) {
			System.out.println(_warningMessages.size() + " warning messages:");

			for (String warningMessage : _warningMessages) {
				System.out.println(warningMessage);
			}
		}

		if (!_errorMessages.isEmpty()) {
			System.out.println(_errorMessages.size() + " error messages:");

			for (String errorMessage : _errorMessages) {
				System.out.println(errorMessage);
			}

			throw new Exception(_errorMessages.size() + " error messages");
		}
	}

	private void _addFileNames(String fileName) {
		File file = new File(fileName);

		if (file.isDirectory() &&
			!Objects.equals(file.getName(), "resources") &&
			!Objects.equals(file.getName(), "_snippets")) {

			for (String currentFileName : file.list()) {
				_addFileNames(fileName + "/" + currentFileName);
			}
		}

		_fileNames.add(fileName);
	}

	private void _error(String errorMessage) {
		System.out.println(errorMessage);

		_errorMessages.add(errorMessage);
	}

	private JSONArray _getBreadcrumbJSONArray(File file) throws Exception {
		JSONArray breadcrumbJSONArray = new JSONArray();

		if (file == null) {
			return breadcrumbJSONArray;
		}

		File parentMarkdownFile = null;

		while ((parentMarkdownFile = _getParentMarkdownFile(file)) != null) {
			breadcrumbJSONArray.put(
				_getNavigationItemJSONObject(parentMarkdownFile));

			file = parentMarkdownFile;
		}

		return breadcrumbJSONArray;
	}

	private JSONArray _getChildrenJSONArray(File file, boolean nested)
		throws Exception {

		JSONArray childrenJSONArray = new JSONArray();

		if (file == null) {
			return childrenJSONArray;
		}

		SnakeYamlFrontMatterVisitor snakeYamlFrontMatterVisitor =
			new SnakeYamlFrontMatterVisitor();

		snakeYamlFrontMatterVisitor.visit(
			_parser.parse(
				FileUtils.readFileToString(file, StandardCharsets.UTF_8)));

		Map<String, Object> data = snakeYamlFrontMatterVisitor.getData();

		if ((data == null) || !data.containsKey("toc")) {
			return childrenJSONArray;
		}

		Object toc = data.get("toc");

		if (!(toc instanceof ArrayList)) {
			return childrenJSONArray;
		}

		for (Object tocEntry : (ArrayList)toc) {
			if (!(tocEntry instanceof String)) {
				continue;
			}

			Matcher matcher = _markdownLinkPattern.matcher((String)tocEntry);

			if (matcher.find()) {
				JSONObject linkJSONObject = new JSONObject();

				linkJSONObject.put(
					"title", matcher.group(1)
				).put(
					"url", matcher.group(2)
				);

				childrenJSONArray.put(linkJSONObject);

				continue;
			}

			String tocFileName = (String)tocEntry;

			String filePathString =
				file.getParent() + File.separator + tocFileName;

			File tocFile = new File(filePathString);

			if (!tocFile.exists() || tocFile.isDirectory()) {
				_warn(
					StringBundler.concat(
						"Nonexistent or invalid TOC file ", tocFile.getPath(),
						" in file ", file.getPath()));

				continue;
			}

			JSONObject childJSONObject = _getNavigationItemJSONObject(tocFile);

			if (nested) {
				childJSONObject.put(
					"children", _getChildrenJSONArray(tocFile, false));
			}

			childrenJSONArray.put(childJSONObject);
		}

		return childrenJSONArray;
	}

	private String _getDescription(String text) {
		TextCollectingVisitor textCollectingVisitor =
			new TextCollectingVisitor();

		return StringUtil.shorten(
			textCollectingVisitor.collectAndGetText(_parser.parse(text)), 300);
	}

	private String[] _getDirNames(String fileName) {
		List<String> dirNames = new ArrayList<>();

		String[] parts = fileName.split(
			Matcher.quoteReplacement(File.separator));

		for (String part : parts) {
			if (StringUtil.equalsIgnoreCase(part, "en") ||
				StringUtil.equalsIgnoreCase(part, "ja") ||
				StringUtil.equalsIgnoreCase(part, "latest")) {

				continue;
			}

			String dirName = part;

			dirNames.add(dirName);
		}

		return dirNames.toArray(new String[0]);
	}

	private void _getGitDiff(File dir) throws Exception {
		Git git = Git.open(new File(dir, ".git"));

		Repository repository = git.getRepository();

		ObjectId newRev = repository.resolve("HEAD");
		ObjectId oldRev = repository.resolve(_oldHash);

		_newHash = newRev.getName();

		if (oldRev == null) {
			return;
		}

		CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
		CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();

		RevCommit newCommit = repository.parseCommit(newRev);

		RevCommit oldCommit = repository.parseCommit(oldRev);

		newTreeParser.reset(
			repository.newObjectReader(),
			newCommit.getTree(
			).getId());
		oldTreeParser.reset(
			repository.newObjectReader(),
			oldCommit.getTree(
			).getId());

		List<DiffEntry> diffs = git.diff(
		).setOldTree(
			oldTreeParser
		).setNewTree(
			newTreeParser
		).call();

		for (DiffEntry diff : diffs) {
			if (diff.getNewPath(
				).endsWith(
					".md"
				)) {

				_diffFileNames.add("/" + diff.getNewPath());
			}
		}
	}

	private String _getHTML(File file) throws Exception {
		String htmlFilePath = file.getCanonicalPath(
		).replaceFirst(
			_docsDirName, _baseDirName + "/site"
		).replaceFirst(
			"\\.md", ".html"
		);

		File htmlFile = new File(htmlFilePath);

		return FileUtils.readFileToString(htmlFile, StandardCharsets.UTF_8);
	}

	private String _getMD5Hex(StructuredContent structuredContent) {
		ContentField[] contentFields = structuredContent.getContentFields();

		for (ContentField contentField : contentFields) {
			if (!StringUtil.equals(contentField.getName(), "md5Hex")) {
				continue;
			}

			return contentField.getContentFieldValue(
			).getData();
		}

		return StringPool.BLANK;
	}

	private JSONObject _getNavigationItemJSONObject(File file)
		throws Exception {

		JSONObject navigationItemJSONObject = new JSONObject();

		if (file == null) {
			return navigationItemJSONObject;
		}

		navigationItemJSONObject.put(
			"title",
			_getTitle(
				FileUtils.readFileToString(file, StandardCharsets.UTF_8)));

		Path docsPath = Paths.get(_docsDirName);
		Path filePath = Paths.get(file.toURI());

		Path relativePath = docsPath.relativize(filePath);

		String urlString =
			"/w/" + FilenameUtils.removeExtension(String.valueOf(relativePath));

		urlString =
			urlString.substring(0, urlString.indexOf("/latest/")) +
				urlString.substring(urlString.indexOf("/latest/") + 10);

		navigationItemJSONObject.put("url", urlString);

		return navigationItemJSONObject;
	}

	private JSONObject _getNavigationJSONObject(File file) throws Exception {
		JSONObject navigationJSONObject = new JSONObject();

		navigationJSONObject.put(
			"breadcrumb", _getBreadcrumbJSONArray(file)
		).put(
			"children", _getChildrenJSONArray(file, true)
		).put(
			"parent", _getNavigationItemJSONObject(_getParentMarkdownFile(file))
		).put(
			"self", _getNavigationItemJSONObject(file)
		).put(
			"siblings",
			_getChildrenJSONArray(_getParentMarkdownFile(file), false)
		);

		return navigationJSONObject;
	}

	private String _getOAuthAuthorization() throws Exception {
		HttpPost httpPost = new HttpPost(_liferayURL + "/o/oauth2/token");

		httpPost.setEntity(
			new UrlEncodedFormEntity(
				Arrays.asList(
					new BasicNameValuePair("client_id", _liferayOAuthClientId),
					new BasicNameValuePair(
						"client_secret", _liferayOAuthClientSecret),
					new BasicNameValuePair(
						"grant_type", "client_credentials"))));
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		try (CloseableHttpClient closeableHttpClient =
				httpClientBuilder.build();
			CloseableHttpResponse closeableHttpResponse =
				closeableHttpClient.execute(httpPost)) {

			StatusLine statusLine = closeableHttpResponse.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				JSONObject jsonObject = new JSONObject(
					EntityUtils.toString(
						closeableHttpResponse.getEntity(),
						Charset.defaultCharset()));

				_oauthExpirationMillis =
					jsonObject.getLong("expires_in") * 1000;

				return jsonObject.getString("token_type") + " " +
					jsonObject.getString("access_token");
			}

			throw new Exception("Unable to get OAuth authorization");
		}
	}

	private File _getParentMarkdownFile(File file) throws Exception {
		if (Objects.equals(file.getName(), "index.md")) {
			return null;
		}

		File parentFile = file.getParentFile();

		File parentMarkdownFile = new File(parentFile.getPath() + ".md");

		while (!parentMarkdownFile.exists()) {
			parentFile = parentFile.getParentFile();

			if (Objects.equals(parentFile.getPath(), _docsDirName)) {
				break;
			}

			parentMarkdownFile = new File(parentFile.getPath() + ".md");
		}

		if (!parentMarkdownFile.exists()) {
			parentFile = file.getParentFile();

			parentMarkdownFile = new File(
				parentFile.getPath() + File.separator + "index.md");
		}

		if (!parentMarkdownFile.exists()) {
			_warn(
				"Missing parent markdown for " + parentMarkdownFile.getPath());

			return null;
		}

		return parentMarkdownFile;
	}

	private Permission[] _getPermissions(
			String fileName, Long structuredContentId)
		throws Exception {

		List<Permission> permissions = new ArrayList<>();

		if (structuredContentId != null) {
			Page<Permission> structuredContentPermissionsPage =
				_structuredContentResource.getStructuredContentPermissionsPage(
					structuredContentId, null);

			for (Permission permission :
					structuredContentPermissionsPage.getItems()) {

				if (Objects.equals(permission.getRoleName(), "Owner")) {
					continue;
				}

				permission.setActionIds(new String[0]);

				permissions.add(permission);
			}
		}

		SnakeYamlFrontMatterVisitor snakeYamlFrontMatterVisitor =
			new SnakeYamlFrontMatterVisitor();

		File file = new File(fileName);

		snakeYamlFrontMatterVisitor.visit(
			_parser.parse(
				FileUtils.readFileToString(file, StandardCharsets.UTF_8)));

		Map<String, Object> data = snakeYamlFrontMatterVisitor.getData();

		if ((data == null) || !data.containsKey("visibility")) {
			permissions.add(
				new Permission() {
					{
						setActionIds(new String[] {"VIEW"});
						setRoleName("Guest");
					}
				});

			return permissions.toArray(new Permission[0]);
		}

		Object visibilityObject = data.get("visibility");

		if (!(visibilityObject instanceof ArrayList)) {
			return null;
		}

		for (Object object : (ArrayList)visibilityObject) {
			if (!(object instanceof String)) {
				continue;
			}

			permissions.add(
				new Permission() {
					{
						setActionIds(new String[] {"ADD_DISCUSSION", "VIEW"});
						setRoleName((String)object);
					}
				});
		}

		if (permissions.isEmpty()) {
			return null;
		}

		permissions.add(
			new Permission() {
				{
					setActionIds(new String[0]);
					setRoleName("Guest");
				}
			});

		return permissions.toArray(new Permission[0]);
	}

	private List<StructuredContent> _getSiteStructuredContents(long siteId)
		throws Exception {

		if (_offline) {
			return Collections.emptyList();
		}

		List<StructuredContent> structuredContents = new ArrayList<>();

		for (int page = 1;; page++) {
			Page<StructuredContent> structuredContentsPage =
				_structuredContentResource.getSiteStructuredContentsPage(
					siteId, true, null, null, null, Pagination.of(page, 50),
					null);

			structuredContents.addAll(structuredContentsPage.getItems());

			if (structuredContentsPage.getLastPage() == page) {
				break;
			}
		}

		return structuredContents;
	}

	private Long _getStructuredContentFolderId(String fileName)
		throws Exception {

		Long structuredContentFolderId = 0L;

		for (String dirName : _getDirNames(fileName)) {
			structuredContentFolderId = _getStructuredContentFolderId(
				dirName, structuredContentFolderId);
		}

		return structuredContentFolderId;
	}

	private Long _getStructuredContentFolderId(
			String dirName, Long parentStructuredContentFolderId)
		throws Exception {

		String key = parentStructuredContentFolderId + "#" + dirName;

		Long structuredContentFolderId = _structuredContentFolderIds.get(key);

		if (structuredContentFolderId != null) {
			return structuredContentFolderId;
		}

		StructuredContentFolder structuredContentFolder = null;

		if (parentStructuredContentFolderId == 0) {
			Page<StructuredContentFolder> page =
				_structuredContentFolderResource.
					getSiteStructuredContentFoldersPage(
						_liferaySiteId, null, null, null,
						"name eq '" + dirName + "'", null, null);

			structuredContentFolder = page.fetchFirstItem();

			if (structuredContentFolder == null) {
				structuredContentFolder =
					_structuredContentFolderResource.
						postSiteStructuredContentFolder(
							_liferaySiteId,
							new StructuredContentFolder() {
								{
									setDescription(() -> "");
									setName(() -> dirName);
									setViewableBy(() -> ViewableBy.ANYONE);
								}
							});
			}
		}
		else {
			Page<StructuredContentFolder> page =
				_structuredContentFolderResource.
					getStructuredContentFolderStructuredContentFoldersPage(
						parentStructuredContentFolderId, null, null,
						"name eq '" + dirName + "'", null, null);

			structuredContentFolder = page.fetchFirstItem();

			if (structuredContentFolder == null) {
				structuredContentFolder =
					_structuredContentFolderResource.
						postStructuredContentFolderStructuredContentFolder(
							parentStructuredContentFolderId,
							new StructuredContentFolder() {
								{
									setDescription(() -> "");
									setName(() -> dirName);
									setViewableBy(() -> ViewableBy.ANYONE);
								}
							});
			}
		}

		structuredContentFolderId = structuredContentFolder.getId();

		_structuredContentFolderIds.put(key, structuredContentFolderId);

		return structuredContentFolderId;
	}

	private Long[] _getTaxonomyCategoryIds(String text) {
		SnakeYamlFrontMatterVisitor snakeYamlFrontMatterVisitor =
			new SnakeYamlFrontMatterVisitor();

		snakeYamlFrontMatterVisitor.visit(_parser.parse(text));

		Map<String, Object> data = snakeYamlFrontMatterVisitor.getData();

		if ((data == null) || !data.containsKey("taxonomy-category-names")) {
			return new Long[0];
		}

		Object taxonomyCategoryNames = data.get("taxonomy-category-names");

		if (!(taxonomyCategoryNames instanceof ArrayList)) {
			return new Long[0];
		}

		List<Long> taxonomyCategoryIds = new ArrayList<>();

		try {
			taxonomyCategoryIds.add(
				_taxonomyCategoriesJSONObject.getLong(
					"OFFICIAL_DOCUMENTATION"));
		}
		catch (Exception exception) {
			_error(exception.getMessage());
		}

		for (Object taxonomyCategoryNameObject :
				(ArrayList)taxonomyCategoryNames) {

			if (!(taxonomyCategoryNameObject instanceof String)) {
				continue;
			}

			String taxonomyCategoryName = (String)taxonomyCategoryNameObject;

			if (!_taxonomyCategoriesJSONObject.has(taxonomyCategoryName)) {
				_warn(
					"No taxonomy category exists with the name: " +
						taxonomyCategoryName);

				continue;
			}

			taxonomyCategoryIds.add(
				_taxonomyCategoriesJSONObject.getLong(taxonomyCategoryName));
		}

		if (taxonomyCategoryIds.isEmpty()) {
			return new Long[0];
		}

		return taxonomyCategoryIds.toArray(new Long[0]);
	}

	private String _getTitle(Node node) {
		if (node instanceof Heading) {
			Heading heading = (Heading)node;

			if ((heading.getLevel() == 1) && heading.hasChildren()) {
				TextCollectingVisitor textCollectingVisitor =
					new TextCollectingVisitor();

				return textCollectingVisitor.collectAndGetText(heading);
			}
		}

		if ((node instanceof Block) && node.hasChildren()) {
			Node childNode = node.getFirstChild();

			while (childNode != null) {
				String title = _getTitle(childNode);

				if (title != null) {
					return title;
				}

				childNode = childNode.getNext();
			}
		}

		return null;
	}

	private String _getTitle(String text) {
		return _getTitle(_parser.parse(text));
	}

	private String _getUuid(String text) {
		Document document = _parser.parse(text);

		SnakeYamlFrontMatterVisitor snakeYamlFrontMatterVisitor =
			new SnakeYamlFrontMatterVisitor();

		snakeYamlFrontMatterVisitor.visit(document);

		Map<String, Object> data = snakeYamlFrontMatterVisitor.getData();

		if ((data == null) || !data.containsKey("uuid")) {
			return StringPool.BLANK;
		}

		Object uuid = data.get("uuid");

		if (!(uuid instanceof String)) {
			return StringPool.BLANK;
		}

		return uuid.toString();
	}

	private void _initFlexmark() {
		MutableDataSet mutableDataSet = new MutableDataSet(
		).set(
			AdmonitionExtension.QUALIFIER_TYPE_MAP,
			HashMapBuilder.put(
				"error", "error"
			).put(
				"important", "important"
			).put(
				"note", "note"
			).put(
				"tip", "tip"
			).put(
				"warning", "warning"
			).build()
		).set(
			AdmonitionExtension.TYPE_SVG_MAP, new HashMap<String, String>()
		).set(
			AsideExtension.ALLOW_LEADING_SPACE, true
		).set(
			AsideExtension.EXTEND_TO_BLANK_LINE, false
		).set(
			AsideExtension.IGNORE_BLANK_LINE, false
		).set(
			AsideExtension.INTERRUPTS_ITEM_PARAGRAPH, true
		).set(
			AsideExtension.INTERRUPTS_PARAGRAPH, true
		).set(
			AsideExtension.WITH_LEAD_SPACES_INTERRUPTS_ITEM_PARAGRAPH, true
		).set(
			HtmlRenderer.GENERATE_HEADER_ID, true
		).set(
			Parser.EXTENSIONS,
			Arrays.asList(
				AdmonitionExtension.create(), AnchorLinkExtension.create(),
				AsideExtension.create(), AttributesExtension.create(),
				DefinitionExtension.create(), FootnoteExtension.create(),
				MediaTagsExtension.create(), StrikethroughExtension.create(),
				SuperscriptExtension.create(), TablesExtension.create(),
				TocExtension.create(), TypographicExtension.create(),
				YamlFrontMatterExtension.create())
		);

		_parser = Parser.builder(
			mutableDataSet
		).build();
	}

	private void _initResourceBuilders(String authorization) throws Exception {
		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			DataDefinitionResource.builder();

		_dataDefinitionResource = dataDefinitionResourceBuilder.header(
			"Authorization", authorization
		).endpoint(
			_liferayURL
		).build();

		SiteResource.Builder siteResourceBuilder = SiteResource.builder();

		_siteResource = siteResourceBuilder.header(
			"Authorization", authorization
		).endpoint(
			_liferayURL
		).build();

		StructuredContentFolderResource.Builder
			structuredContentFolderResourceBuilder =
				StructuredContentFolderResource.builder();

		_structuredContentFolderResource =
			structuredContentFolderResourceBuilder.header(
				"Authorization", authorization
			).endpoint(
				_liferayURL
			).build();

		StructuredContentResource.Builder structuredContentResourceBuilder =
			StructuredContentResource.builder();

		_structuredContentResource = structuredContentResourceBuilder.header(
			"Authorization", authorization
		).endpoint(
			_liferayURL
		).build();

		TaxonomyCategoryResource.Builder taxonomyCategoryResourceBuilder =
			TaxonomyCategoryResource.builder();

		_taxonomyCategoryResource = taxonomyCategoryResourceBuilder.header(
			"Authorization", authorization
		).endpoint(
			_liferayURL
		).build();

		TaxonomyVocabularyResource.Builder taxonomyVocabularyResourceBuilder =
			TaxonomyVocabularyResource.builder();

		_taxonomyVocabularyResource = taxonomyVocabularyResourceBuilder.header(
			"Authorization", authorization
		).endpoint(
			_liferayURL
		).build();
	}

	private boolean _isShowChildrenCards(File file) throws Exception {
		SnakeYamlFrontMatterVisitor snakeYamlFrontMatterVisitor =
			new SnakeYamlFrontMatterVisitor();

		snakeYamlFrontMatterVisitor.visit(
			_parser.parse(
				FileUtils.readFileToString(file, StandardCharsets.UTF_8)));

		Map<String, Object> data = snakeYamlFrontMatterVisitor.getData();

		if ((data == null) || !data.containsKey("showChildrenCards") ||
			!StringUtil.equals(
				data.get(
					"showChildrenCards"
				).toString(),
				"false")) {

			return true;
		}

		return GetterUtil.getBoolean(data.get("showChildrenCards"));
	}

	private void _loadTaxonomyCategories(
			Map<String, String> existingTaxonomyCategories,
			JSONObject jsonObject, String parentTaxonomyCategoryId,
			long taxonomyVocabularyId)
		throws Exception {

		if (!jsonObject.has("taxonomyCategories")) {
			return;
		}

		JSONArray jsonArray = jsonObject.getJSONArray("taxonomyCategories");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject taxonomyCategoryJSONObject = jsonArray.getJSONObject(i);

			String name = taxonomyCategoryJSONObject.getString("name");

			if (!existingTaxonomyCategories.containsKey(name)) {
				TaxonomyCategory taxonomyCategory = new TaxonomyCategory();

				taxonomyCategory.setName(() -> name);
				taxonomyCategory.setTaxonomyVocabularyId(
					() -> taxonomyVocabularyId);

				if (parentTaxonomyCategoryId != null) {
					taxonomyCategory =
						_taxonomyCategoryResource.
							postTaxonomyCategoryTaxonomyCategory(
								parentTaxonomyCategoryId, taxonomyCategory);
				}
				else {
					taxonomyCategory =
						_taxonomyCategoryResource.
							postTaxonomyVocabularyTaxonomyCategory(
								taxonomyCategory.getTaxonomyVocabularyId(),
								taxonomyCategory);
				}

				_taxonomyCategoriesJSONObject.put(
					name, taxonomyCategory.getId());
			}
			else {
				_taxonomyCategoriesJSONObject.put(
					name, existingTaxonomyCategories.get(name));
			}

			_loadTaxonomyCategories(
				existingTaxonomyCategories, taxonomyCategoryJSONObject,
				_taxonomyCategoriesJSONObject.getString(name),
				taxonomyVocabularyId);
		}
	}

	private void _loadTaxonomyVocabularies() throws Exception {
		File file = new File(_docsDirName + "/../taxonomy-vocabularies.json");

		if (!file.exists()) {
			return;
		}

		JSONObject taxonomyVocabulariesJSONObject = new JSONObject(
			FileUtils.readFileToString(file, StandardCharsets.UTF_8));

		if (taxonomyVocabulariesJSONObject.isEmpty()) {
			return;
		}

		Map<String, String> existingTaxonomyCategories = new HashMap<>();
		Map<String, Long> existingTaxonomyVocabularies = new HashMap<>();

		com.liferay.headless.admin.taxonomy.client.pagination.Page
			<TaxonomyVocabulary> taxonomyVocabulariesPage =
				_taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
					_globalSiteId, null, null, null,
					com.liferay.headless.admin.taxonomy.client.pagination.
						Pagination.of(-1, -1),
					null);

		for (TaxonomyVocabulary taxonomyVocabulary :
				taxonomyVocabulariesPage.getItems()) {

			if (StringUtil.equals(
					taxonomyVocabulary.getExternalReferenceCode(),
					"RESOURCE_TYPE")) {

				TaxonomyCategory taxonomyCategory =
					_taxonomyCategoryResource.
						getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
							taxonomyVocabulary.getId(),
							"OFFICIAL_DOCUMENTATION");

				_taxonomyCategoriesJSONObject.put(
					taxonomyCategory.getExternalReferenceCode(),
					taxonomyCategory.getId());

				continue;
			}

			existingTaxonomyVocabularies.put(
				taxonomyVocabulary.getName(), taxonomyVocabulary.getId());

			com.liferay.headless.admin.taxonomy.client.pagination.Page
				<TaxonomyCategory> taxonomyCategoriesPage =
					_taxonomyCategoryResource.
						getTaxonomyVocabularyTaxonomyCategoriesPage(
							taxonomyVocabulary.getId(), true, null, null, null,
							com.liferay.headless.admin.taxonomy.client.
								pagination.Pagination.of(-1, -1),
							null);

			for (TaxonomyCategory taxonomyCategory :
					taxonomyCategoriesPage.getItems()) {

				existingTaxonomyCategories.put(
					taxonomyCategory.getName(), taxonomyCategory.getId());
			}
		}

		JSONArray jsonArray = taxonomyVocabulariesJSONObject.getJSONArray(
			"taxonomyVocabularies");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject taxonomyVocabularyJSONObject = jsonArray.getJSONObject(
				i);

			String name = taxonomyVocabularyJSONObject.getString("name");

			Long taxonomyVocabularyId = existingTaxonomyVocabularies.get(name);

			if (taxonomyVocabularyId == null) {
				TaxonomyVocabulary taxonomyVocabulary =
					new TaxonomyVocabulary();

				taxonomyVocabulary.setName(() -> name);

				taxonomyVocabulary =
					_taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
						_globalSiteId, taxonomyVocabulary);

				taxonomyVocabularyId = taxonomyVocabulary.getId();
			}

			_loadTaxonomyCategories(
				existingTaxonomyCategories, taxonomyVocabularyJSONObject, null,
				taxonomyVocabularyId);
		}
	}

	private void _readHashFromFile(File dir) throws Exception {
		File hashFile = new File(dir, _lastestHashFileName);

		if (hashFile.exists()) {
			_oldHash = Files.readString(hashFile.toPath());
		}
	}

	private void _saveHashToFile(File dir, String hash) throws Exception {
		File hashFile = new File(dir, _lastestHashFileName);

		Files.writeString(hashFile.toPath(), hash);
	}

	private String _toFriendlyURLPath(File file) {
		String filePathString = file.getPath();

		String relativeFilePathString = filePathString.substring(
			_docsDirName.length() + 1);

		String friendlyURLPathString = StringUtil.merge(
			_getDirNames(relativeFilePathString), StringPool.FORWARD_SLASH);

		return FilenameUtils.removeExtension(friendlyURLPathString);
	}

	private StructuredContent _toStructuredContent(String fileName)
		throws Exception {

		StructuredContent structuredContent = new StructuredContent();

		File englishFile = new File(fileName);

		String englishText = FileUtils.readFileToString(
			englishFile, StandardCharsets.UTF_8);

		ContentFieldValue englishContentContentFieldValue =
			new ContentFieldValue() {
				{
					setData(() -> _getHTML(englishFile));
				}
			};
		ContentFieldValue englishMD5HexContentFieldValue =
			new ContentFieldValue() {
				{
					setData(
						() -> DigestUtils.md5Hex(
							new FileInputStream(englishFile)));
				}
			};
		ContentFieldValue englishNavigationContentFieldValue =
			new ContentFieldValue() {
				{
					setData(
						() -> String.valueOf(
							_getNavigationJSONObject(englishFile)));
				}
			};
		ContentFieldValue englishShowChildrenCardsContentFieldValue =
			new ContentFieldValue() {
				{
					setData(
						() -> String.valueOf(
							_isShowChildrenCards(englishFile)));
				}
			};

		String englishTitle = _getTitle(englishText);

		File japaneseFile = new File(
			StringUtil.replace(fileName, "/en/", "/ja/"));

		if (japaneseFile.exists()) {
			String japaneseText = FileUtils.readFileToString(
				japaneseFile, StandardCharsets.UTF_8);

			structuredContent.setContentFields(
				() -> new ContentField[] {
					new ContentField() {
						{
							setContentFieldValue(
								() -> englishContentContentFieldValue);
							setContentFieldValue_i18n(
								() -> HashMapBuilder.put(
									"en-US", englishContentContentFieldValue
								).put(
									"ja-JP",
									new ContentFieldValue() {
										{
											setData(
												() -> _getHTML(japaneseFile));
										}
									}
								).build());
							setName(() -> "content");
						}
					},
					new ContentField() {
						{
							setContentFieldValue(
								() -> englishMD5HexContentFieldValue);
							setContentFieldValue_i18n(
								() -> HashMapBuilder.put(
									"en-US", englishMD5HexContentFieldValue
								).put(
									"ja-JP",
									new ContentFieldValue() {
										{
											setData(
												() -> DigestUtils.md5Hex(
													new FileInputStream(
														japaneseFile)));
										}
									}
								).build());
							setName(() -> "md5Hex");
						}
					},
					new ContentField() {
						{
							setContentFieldValue(
								() -> englishNavigationContentFieldValue);
							setContentFieldValue_i18n(
								() -> HashMapBuilder.put(
									"en-US", englishNavigationContentFieldValue
								).put(
									"ja-JP",
									new ContentFieldValue() {
										{
											setData(
												() -> String.valueOf(
													_getNavigationJSONObject(
														japaneseFile)));
										}
									}
								).build());
							setName(() -> "navigation");
						}
					},
					new ContentField() {
						{
							setContentFieldValue(
								() ->
									englishShowChildrenCardsContentFieldValue);
							setContentFieldValue_i18n(
								() -> HashMapBuilder.put(
									"en-US",
									englishShowChildrenCardsContentFieldValue
								).put(
									"ja-JP",
									new ContentFieldValue() {
										{
											setData(
												() -> String.valueOf(
													_isShowChildrenCards(
														japaneseFile)));
										}
									}
								).build());
							setName(() -> "showChildrenCards");
						}
					}
				});
			structuredContent.setDescription_i18n(
				() -> HashMapBuilder.put(
					"en-US", _getDescription(englishText)
				).put(
					"ja-JP", _getDescription(japaneseText)
				).build());

			structuredContent.setFriendlyUrlPath_i18n(
				() -> HashMapBuilder.put(
					"en-US", _toFriendlyURLPath(englishFile)
				).put(
					"ja-JP", _toFriendlyURLPath(japaneseFile)
				).build());
			structuredContent.setTitle_i18n(
				() -> HashMapBuilder.put(
					"en-US", englishTitle
				).put(
					"ja-JP", _getTitle(japaneseText)
				).build());
		}
		else {
			structuredContent.setContentFields(
				() -> new ContentField[] {
					new ContentField() {
						{
							setContentFieldValue(
								() -> englishContentContentFieldValue);
							setName(() -> "content");
						}
					},
					new ContentField() {
						{
							setContentFieldValue(
								() -> englishMD5HexContentFieldValue);
							setName(() -> "md5Hex");
						}
					},
					new ContentField() {
						{
							setContentFieldValue(
								() -> englishNavigationContentFieldValue);
							setName(() -> "navigation");
						}
					},
					new ContentField() {
						{
							setContentFieldValue(
								() ->
									englishShowChildrenCardsContentFieldValue);
							setName(() -> "showChildrenCards");
						}
					}
				});
			structuredContent.setDescription(
				() -> _getDescription(englishText));
		}

		structuredContent.setContentStructureId(
			() -> _liferayContentStructureId);
		structuredContent.setExternalReferenceCode(() -> _getUuid(englishText));
		structuredContent.setFriendlyUrlPath(
			() -> _toFriendlyURLPath(englishFile));
		structuredContent.setTaxonomyCategoryIds(
			() -> _getTaxonomyCategoryIds(englishText));

		if (!_offline) {
			structuredContent.setStructuredContentFolderId(
				() -> _getStructuredContentFolderId(
					FilenameUtils.getPathNoEndSeparator(
						fileName.substring(_docsDirName.length()))));
		}

		structuredContent.setTitle(() -> englishTitle);

		return structuredContent;
	}

	private void _warn(String warningMessage) {
		System.out.println(warningMessage);

		_warningMessages.add(warningMessage);
	}

	private void _write(String content, String dirName, File markdownFile)
		throws Exception {

		String markdownFileName = markdownFile.getCanonicalPath();

		markdownFileName = markdownFileName.substring(_docsDirName.length());

		File file = new File(dirName + markdownFileName);

		FileUtils.forceMkdirParent(file);

		FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
	}

	private static final Pattern _markdownLinkPattern = Pattern.compile(
		"\\[(.*)\\]\\((.*)\\)");

	private final String _baseDirName;
	private DataDefinitionResource _dataDefinitionResource;
	private final Set<String> _diffFileNames = new TreeSet<>();
	private final String _docsDirName;
	private final List<String> _errorMessages = new ArrayList<>();
	private final Set<String> _fileNames = new TreeSet<>();
	private final long _globalSiteId;
	private final String _lastestHashFileName;
	private final long _liferayContentStructureId;
	private final String _liferayOAuthClientId;
	private final String _liferayOAuthClientSecret;
	private final long _liferaySiteId;
	private final URL _liferayURL;
	private String _newHash = StringPool.BLANK;
	private long _oauthExpirationMillis;
	private final boolean _offline;
	private String _oldHash = StringPool.BLANK;
	private Parser _parser;
	private SiteResource _siteResource;
	private final boolean _skipDiffCheck;
	private final Map<String, Long> _structuredContentFolderIds =
		new HashMap<>();
	private StructuredContentFolderResource _structuredContentFolderResource;
	private StructuredContentResource _structuredContentResource;
	private final JSONObject _taxonomyCategoriesJSONObject = new JSONObject();
	private TaxonomyCategoryResource _taxonomyCategoryResource;
	private TaxonomyVocabularyResource _taxonomyVocabularyResource;
	private final List<String> _warningMessages = new ArrayList<>();
	private final Yaml _yaml = new Yaml();

	private class SnakeYamlFrontMatterVisitor
		implements YamlFrontMatterVisitor {

		public Map<String, Object> getData() {
			return _data;
		}

		public void visit(Node node) {
			_yamlFrontMatterVisitor.visit(node);
		}

		@Override
		public void visit(YamlFrontMatterBlock yamlFrontMatterBlock) {
			String yamlString = String.valueOf(yamlFrontMatterBlock.getChars());

			yamlString = yamlString.replaceAll("---", "");

			_data = _yaml.load(yamlString);
		}

		@Override
		public void visit(YamlFrontMatterNode yamlFrontMatterNode) {
		}

		private Map<String, Object> _data;
		private final NodeVisitor _yamlFrontMatterVisitor = new NodeVisitor(
			YamlFrontMatterVisitorExt.VISIT_HANDLERS(this));

	}

}