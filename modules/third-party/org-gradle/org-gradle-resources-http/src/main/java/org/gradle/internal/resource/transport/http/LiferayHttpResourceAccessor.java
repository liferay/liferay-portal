/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package org.gradle.internal.resource.transport.http;

import com.liferay.gradle.util.hash.HashUtil;
import com.liferay.gradle.util.hash.HashValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.net.URI;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Writer;
import org.apache.maven.artifact.versioning.ComparableVersion;

import org.gradle.internal.hash.HashCode;
import org.gradle.internal.resource.ExternalResourceName;
import org.gradle.internal.resource.metadata.DefaultExternalResourceMetaData;
import org.gradle.internal.resource.metadata.ExternalResourceMetaData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Di Giorgi
 */
public class LiferayHttpResourceAccessor extends HttpResourceAccessor {

	public LiferayHttpResourceAccessor(HttpClientHelper httpClientHelper) {
		super(httpClientHelper);
	}

	@Override
	public ExternalResourceMetaData getMetaData(
		ExternalResourceName externalResourceName, boolean revalidate) {

		URI uri = externalResourceName.getUri();

		if (!_isForcedCacheEnabled()) {
			return super.getMetaData(externalResourceName, revalidate);
		}

		String location = _getLocation(uri);

		if (StringUtils.isBlank(location)) {
			return super.getMetaData(externalResourceName, revalidate);
		}

		File cachedArtifactFile = _getCachedArtifactFile(location);

		if (cachedArtifactFile == null) {
			return super.getMetaData(externalResourceName, revalidate);
		}

		HashValue hashValue = HashUtil.sha1(cachedArtifactFile);

		return new DefaultExternalResourceMetaData(
			uri, cachedArtifactFile.lastModified(), cachedArtifactFile.length(),
			null, hashValue.asHexString(),
			HashCode.fromBytes(hashValue.asByteArray()));
	}

	@Override
	public HttpResponseResource openResource(
		ExternalResourceName externalResourceName, boolean revalidate) {

		URI uri = externalResourceName.getUri();

		if (!_isForcedCacheEnabled()) {
			return super.openResource(externalResourceName, revalidate);
		}

		String location = _getLocation(uri);

		if (StringUtils.isBlank(location)) {
			return super.openResource(externalResourceName, revalidate);
		}

		HttpResponseResource httpResponseResource = null;

		try {
			if (StringUtils.endsWithIgnoreCase(
					location, "/maven-metadata.xml")) {

				httpResponseResource = _getMavenMetadataResponseResource(
					uri, location);
			}
			else if (StringUtils.endsWithIgnoreCase(location, ".sha1")) {
				httpResponseResource = _getSHA1ResponseResource(uri, location);
			}
		}
		catch (Exception exception) {
			_logger.error(exception.getMessage(), exception);
		}

		if (httpResponseResource == null) {
			httpResponseResource = super.openResource(
				externalResourceName, revalidate);
		}

		return httpResponseResource;
	}

	private File _fetchCachedFile(File dir, String fileName) {
		File cachedFile = null;

		Iterator<File> iterator = FileUtils.iterateFiles(
			dir, new NameFileFilter(fileName), TrueFileFilter.TRUE);

		while (iterator.hasNext()) {
			File file = iterator.next();

			if ((cachedFile != null) &&
				(cachedFile.lastModified() >= file.lastModified())) {

				continue;
			}

			cachedFile = file;
		}

		return cachedFile;
	}

	private File _getCachedArtifactFile(String location) {
		String[] tokens = StringUtils.split(location, '/');

		String fileName = tokens[tokens.length - 1];
		String group = StringUtils.join(tokens, '.', 0, tokens.length - 3);
		String module = tokens[tokens.length - 3];
		String version = tokens[tokens.length - 2];

		File moduleDir = new File(
			_getGradleUserHome(),
			_FILES_CACHE_DIR_NAME + "/" + group + "/" + module);

		File artifactDir = new File(moduleDir, version);

		if (!artifactDir.exists()) {
			if (!StringUtils.endsWithIgnoreCase(version, "-SNAPSHOT") ||
				!StringUtils.startsWithIgnoreCase(fileName, module + "-")) {

				return null;
			}

			// If the name of the artifact directory in the Gradle cache is a
			// unique snapshot version (e.g., 3.10.200-20150904.172142-1), the
			// version token of the requested URI is just the snapshot version
			// (e.g., 3.10.200-SNAPSHOT).

			int pos = fileName.lastIndexOf('.');

			version = fileName.substring(module.length() + 1, pos);

			artifactDir = new File(moduleDir, version);

			if (!artifactDir.exists()) {
				return null;
			}
		}

		File cachedFile = _fetchCachedFile(artifactDir, fileName);

		if (cachedFile == null) {
			cachedFile = _fetchCachedFile(
				artifactDir,
				module + "-" + version + "." +
					FilenameUtils.getExtension(fileName));
		}

		return cachedFile;
	}

	private File _getGradleUserHome() {
		String gradleUserHome = System.getProperty("gradle.user.home");

		if (StringUtils.isBlank(gradleUserHome)) {
			gradleUserHome = System.getenv("GRADLE_USER_HOME");
		}

		if (StringUtils.isBlank(gradleUserHome)) {
			gradleUserHome = System.getProperty("user.home") + "/.gradle";
		}

		return new File(gradleUserHome);
	}

	private String _getLocation(URI uri) {
		String location = uri.toString();

		for (String repositoryUrl : _REPOSITORY_URLS) {
			if (location.startsWith(repositoryUrl)) {
				return location.substring(repositoryUrl.length());
			}
		}

		for (String key : _REPOSITORY_URL_PROPERTY_KEYS) {
			String repositoryUrl = System.getProperty(key);

			if ((repositoryUrl == null) || repositoryUrl.isEmpty()) {
				continue;
			}

			if (repositoryUrl.charAt(repositoryUrl.length() - 1) != '/') {
				repositoryUrl += '/';
			}

			if (location.startsWith(repositoryUrl)) {
				return location.substring(repositoryUrl.length());
			}
		}

		return null;
	}

	private HttpResponseResource _getMavenMetadataResponseResource(
			URI uri, String location)
		throws Exception {

		String[] tokens = StringUtils.split(location, '/');

		String group = null;
		String module = null;
		String version = tokens[tokens.length - 2];

		if (StringUtils.endsWithIgnoreCase(version, "-SNAPSHOT")) {
			group = StringUtils.join(tokens, '.', 0, tokens.length - 3);
			module = tokens[tokens.length - 3];
		}
		else {
			group = StringUtils.join(tokens, '.', 0, tokens.length - 2);
			module = version;
			version = null;
		}

		File moduleDir = new File(
			_getGradleUserHome(),
			_FILES_CACHE_DIR_NAME + "/" + group + "/" + module);

		if (!moduleDir.exists()) {
			return null;
		}

		if (StringUtils.isNotBlank(version)) {
			File artifactDir = new File(moduleDir, version);

			if (!artifactDir.exists()) {
				return null;
			}
		}

		Metadata metadata = new Metadata();

		metadata.setArtifactId(module);
		metadata.setGroupId(group);

		Versioning versioning = new Versioning();

		if (StringUtils.isNotBlank(version)) {
			metadata.setVersion(version);
		}
		else {
			SortedSet<ComparableVersion> moduleVersions = _getModuleVersions(
				moduleDir, false);

			for (ComparableVersion moduleVersion : moduleVersions) {
				versioning.addVersion(moduleVersion.toString());
			}
		}

		versioning.setLatest(_getModuleLatestVersion(moduleDir, false));
		versioning.setRelease(_getModuleLatestVersion(moduleDir, true));

		metadata.setVersioning(versioning);

		MetadataXpp3Writer metadataXpp3Writer = new MetadataXpp3Writer();

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		metadataXpp3Writer.write(byteArrayOutputStream, metadata);

		CloseableHttpResponse closeableHttpResponse =
			new BasicCloseableHttpResponse(
				new BasicStatusLine(
					HttpVersion.HTTP_1_1, HttpStatus.SC_OK, null));

		closeableHttpResponse.setEntity(
			new ByteArrayEntity(
				byteArrayOutputStream.toByteArray(),
				ContentType.APPLICATION_XML));
		closeableHttpResponse.setHeader(
			HttpHeaders.CONTENT_LENGTH,
			String.valueOf(byteArrayOutputStream.size()));

		HttpClientResponse httpClientResponse = new HttpClientResponse(
			HttpGet.METHOD_NAME, uri, closeableHttpResponse);

		return new HttpResponseResource(
			HttpGet.METHOD_NAME, uri, httpClientResponse);
	}

	private String _getModuleLatestVersion(
		File moduleDir, boolean excludeSnapshots) {

		SortedSet<ComparableVersion> moduleVersions = _getModuleVersions(
			moduleDir, excludeSnapshots);

		if (moduleVersions.isEmpty()) {
			return null;
		}

		ComparableVersion moduleVersion = moduleVersions.last();

		return moduleVersion.toString();
	}

	private SortedSet<ComparableVersion> _getModuleVersions(
		File moduleDir, boolean excludeSnapshots) {

		SortedSet<ComparableVersion> moduleVersions = new TreeSet<>();

		String[] versions = moduleDir.list(DirectoryFileFilter.DIRECTORY);

		for (String version : versions) {
			if (excludeSnapshots &&
				StringUtils.endsWithIgnoreCase(version, "-SNAPSHOT")) {

				continue;
			}

			moduleVersions.add(new ComparableVersion(version));
		}

		return moduleVersions;
	}

	private HttpResponseResource _getSHA1ResponseResource(
			URI uri, String location)
		throws Exception {

		location = location.substring(0, location.length() - 5);

		File cachedArtifactFile = _getCachedArtifactFile(location);

		if (cachedArtifactFile == null) {
			return null;
		}

		HashValue hashValue = HashUtil.sha1(cachedArtifactFile);

		String sha1 = hashValue.asHexString();

		CloseableHttpResponse closeableHttpResponse =
			new BasicCloseableHttpResponse(
				new BasicStatusLine(
					HttpVersion.HTTP_1_1, HttpStatus.SC_OK, null));

		closeableHttpResponse.setEntity(new StringEntity(sha1));
		closeableHttpResponse.setHeader(
			HttpHeaders.CONTENT_LENGTH, String.valueOf(sha1.length()));
		closeableHttpResponse.setHeader(
			HttpHeaders.LAST_MODIFIED,
			String.valueOf(cachedArtifactFile.lastModified()));

		HttpClientResponse httpClientResponse = new HttpClientResponse(
			HttpGet.METHOD_NAME, uri, closeableHttpResponse);

		return new HttpResponseResource(
			HttpGet.METHOD_NAME, uri, httpClientResponse);
	}

	private boolean _isForcedCacheEnabled() {
		return Boolean.getBoolean("forced.cache.enabled");
	}

	private static final String _FILES_CACHE_DIR_NAME =
		"caches/modules-2/files-2.1";

	private static final String[] _REPOSITORY_URL_PROPERTY_KEYS = {
		"repository.private.url", "repository.url"
	};

	private static final String[] _REPOSITORY_URLS = {
		"http://cdn.repository.liferay.com/nexus/content/groups/public/",
		"http://repository.liferay.com/nexus/content/groups/public/",
		"http://repository-cdn.liferay.com/nexus/content/groups/public/",
		"https://cdn.lfrs.sl/repository.liferay.com/nexus/content/groups" +
			"/public/",
		"https://repository.liferay.com/nexus/content/groups/public/",
		"https://repository-cdn.liferay.com/nexus/content/groups/public/"
	};

	private static final Logger _logger = LoggerFactory.getLogger(
		LiferayHttpResourceAccessor.class);

	private static class BasicCloseableHttpResponse
		extends BasicHttpResponse implements CloseableHttpResponse {

		public BasicCloseableHttpResponse(StatusLine statusLine) {
			super(statusLine);
		}

		@Override
		public void close() throws IOException {
		}

	}

}