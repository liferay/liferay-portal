/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SystemProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 */
public class PreupgradeVerifyProperties extends PreupgradeVerifyProcess {

	protected static InputStream getPropertiesResourceAsStream(
			String resourceName)
		throws FileNotFoundException {

		File propertyFile = new File(resourceName);

		if (propertyFile.exists()) {
			return new FileInputStream(propertyFile);
		}

		ClassLoader classLoader =
			PreupgradeVerifyProperties.class.getClassLoader();

		try {
			return classLoader.getResourceAsStream(resourceName);
		}
		catch (RuntimeException runtimeException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get resource " + resourceName, runtimeException);
			}

			return null;
		}
	}

	protected static Properties loadPortalProperties() {
		Properties properties = new Properties();

		List<String> propertiesResourceNames = ListUtil.fromArray(
			PropsUtil.getArray("include-and-override"));

		propertiesResourceNames.add(0, "portal.properties");

		for (String propertyResourceName : propertiesResourceNames) {
			if (propertyResourceName.contains("${") &&
				propertyResourceName.contains("}")) {

				continue;
			}

			try (InputStream inputStream = getPropertiesResourceAsStream(
					propertyResourceName)) {

				if (inputStream != null) {
					properties.load(inputStream);
				}
			}
			catch (IOException ioException) {
				_log.error(
					"Unable to load property " + propertyResourceName,
					ioException);
			}
		}

		return properties;
	}

	protected static void verifyMigratedPortalProperty(
			Properties portalProperties, String oldKey, String newKey,
			List<String> unmigratedKeys)
		throws Exception {

		if (portalProperties.containsKey(oldKey)) {
			_log.error(
				StringBundler.concat(
					"Portal property \"", oldKey,
					"\" was migrated to the system property \"", newKey, "\""));

			unmigratedKeys.add(oldKey);
		}
	}

	protected static void verifyMigratedSystemProperty(
			String oldKey, String newKey)
		throws Exception {

		String value = SystemProperties.get(oldKey);

		if (value != null) {
			_log.error(
				StringBundler.concat(
					"System property \"", oldKey,
					"\" was migrated to the portal property \"", newKey, "\""));
		}
	}

	protected static void verifyModularizedPortalProperty(
			Properties portalProperties, String oldKey, String newKey,
			String moduleName)
		throws Exception {

		if (portalProperties.containsKey(oldKey)) {
			_log.error(
				StringBundler.concat(
					"Portal property \"", oldKey, "\" was modularized to ",
					moduleName, " as \"", newKey, "\""));
		}
	}

	protected static void verifyModularizedSystemProperty(
			Set<String> systemPropertyNames, String oldKey, String newKey,
			String moduleName)
		throws Exception {

		if (systemPropertyNames.contains(oldKey)) {
			_log.error(
				StringBundler.concat(
					"System property \"", oldKey, "\" was modularized to ",
					moduleName, " as \"", newKey, "\""));
		}
	}

	protected static void verifyObsoletePortalProperty(
			Properties portalProperties, String key)
		throws Exception {

		if (portalProperties.containsKey(key)) {
			_log.error("Portal property \"" + key + "\" is obsolete");
		}
	}

	protected static void verifyObsoleteSystemProperty(String key)
		throws Exception {

		String value = SystemProperties.get(key);

		if (value != null) {
			_log.error("System property \"" + key + "\" is obsolete");
		}
	}

	protected static List<String> verifyPortalProperties() throws Exception {
		List<String> unmigratedKeys = new LinkedList<>();

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			Properties portalProperties = loadPortalProperties();

			for (String[] keys : _MIGRATED_PORTAL_KEYS) {
				String oldKey = keys[0];
				String newKey = keys[1];

				verifyMigratedPortalProperty(
					portalProperties, oldKey, newKey, unmigratedKeys);
			}

			for (String[] keys : _RENAMED_PORTAL_KEYS) {
				String oldKey = keys[0];
				String newKey = keys[1];

				verifyRenamedPortalProperty(portalProperties, oldKey, newKey);
			}

			for (String key : _OBSOLETE_PORTAL_KEYS) {
				verifyObsoletePortalProperty(portalProperties, key);
			}

			for (String[] keys : _MODULARIZED_PORTAL_KEYS) {
				String oldKey = keys[0];
				String newKey = keys[1];
				String moduleName = keys[2];

				verifyModularizedPortalProperty(
					portalProperties, oldKey, newKey, moduleName);
			}
		}

		return unmigratedKeys;
	}

	protected static void verifyRenamedPortalProperty(
			Properties portalProperties, String oldKey, String newKey)
		throws Exception {

		if (portalProperties.containsKey(oldKey)) {
			_log.error(
				StringBundler.concat(
					"Portal property \"", oldKey, "\" was renamed to \"",
					newKey, "\""));
		}
	}

	protected static void verifyRenamedSystemProperty(
			String oldKey, String newKey)
		throws Exception {

		String value = SystemProperties.get(oldKey);

		if (value != null) {
			_log.error(
				StringBundler.concat(
					"System property \"", oldKey, "\" was renamed to \"",
					newKey, "\""));
		}
	}

	protected static void verifySystemProperties() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			for (String[] keys : _MIGRATED_SYSTEM_KEYS) {
				String oldKey = keys[0];
				String newKey = keys[1];

				verifyMigratedSystemProperty(oldKey, newKey);
			}

			for (String[] keys : _RENAMED_SYSTEM_KEYS) {
				String oldKey = keys[0];
				String newKey = keys[1];

				verifyRenamedSystemProperty(oldKey, newKey);
			}

			for (String key : _OBSOLETE_SYSTEM_KEYS) {
				verifyObsoleteSystemProperty(key);
			}

			Set<String> propertyNames = SystemProperties.getPropertyNames();

			for (String[] keys : _MODULARIZED_SYSTEM_KEYS) {
				String oldKey = keys[0];
				String newKey = keys[1];
				String moduleName = keys[2];

				verifyModularizedSystemProperty(
					propertyNames, oldKey, newKey, moduleName);
			}
		}
	}

	@Override
	protected void doVerify() throws Exception {
		verifySystemProperties();

		List<String> keys = verifyPortalProperties();

		if (!keys.isEmpty()) {
			throw new VerifyException(
				"Incorrect use of migrated portal properties keys: " + keys);
		}
	}

	@Override
	protected boolean isSkipDBPartitions() {
		return true;
	}

	private static final String[][] _MIGRATED_PORTAL_KEYS = {
		{"cookie.http.only.names.excludes", "cookie.http.only.names.excludes"},
		{
			"http.header.secure.x.content.type.options",
			"http.header.secure.x.content.type.options"
		},
		{
			"http.header.secure.x.content.type.options.urls.excludes",
			"http.header.secure.x.content.type.options.urls.excludes"
		},
		{
			"http.header.secure.x.frame.options",
			"http.header.secure.x.frame.options"
		},
		{
			"http.header.secure.x.frame.options.255",
			"http.header.secure.x.frame.options.255"
		},
		{
			"module.framework.beginning.start.level",
			"module.framework.beginning.start.level"
		},
		{
			"module.framework.dynamic.install.start.level",
			"module.framework.dynamic.install.start.level"
		},
		{
			"module.framework.file.install.config.encoding",
			"module.framework.file.install.config.encoding"
		},
		{
			"module.framework.concurrent.startup.enabled",
			"module.framework.concurrent.startup.enabled"
		},
		{
			"module.framework.configuration.bundle.symbolic.names",
			"module.framework.configuration.bundle.symbolic.names"
		},
		{
			"module.framework.runtime.start.level",
			"module.framework.runtime.start.level"
		},
		{
			"module.framework.services.ignored.interfaces",
			"module.framework.services.ignored.interfaces"
		},
		{"module.framework.static.jars", "module.framework.static.jars"},
		{
			"module.framework.system.packages.extra",
			"module.framework.system.packages.extra"
		},
		{"module.framework.web.start.level", "module.framework.web.start.level"}
	};

	private static final String[][] _MIGRATED_SYSTEM_KEYS = {
		{
			"com.liferay.filters.compression.CompressionFilter",
			"com.liferay.portal.servlet.filters.gzip.GZipFilter"
		},
		{
			"com.liferay.filters.strip.StripFilter",
			"com.liferay.portal.servlet.filters.strip.StripFilter"
		},
		{
			"com.liferay.util.Http.max.connections.per.host",
			"com.liferay.portal.util.HttpImpl.max.connections.per.host"
		},
		{
			"com.liferay.util.Http.max.total.connections",
			"com.liferay.portal.util.HttpImpl.max.total.connections"
		},
		{
			"com.liferay.util.Http.proxy.auth.type",
			"com.liferay.portal.util.HttpImpl.proxy.auth.type"
		},
		{
			"com.liferay.util.Http.proxy.ntlm.domain",
			"com.liferay.portal.util.HttpImpl.proxy.ntlm.domain"
		},
		{
			"com.liferay.util.Http.proxy.ntlm.host",
			"com.liferay.portal.util.HttpImpl.proxy.ntlm.host"
		},
		{
			"com.liferay.util.Http.proxy.password",
			"com.liferay.portal.util.HttpImpl.proxy.password"
		},
		{
			"com.liferay.util.Http.proxy.username",
			"com.liferay.portal.util.HttpImpl.proxy.username"
		},
		{
			"com.liferay.util.Http.timeout",
			"com.liferay.portal.util.HttpImpl.timeout"
		},
		{
			"com.liferay.portal.util.HttpImpl.max.connections.per.host",
			"com.liferay.portal.kernel.util.Http.max.connections.per.host"
		},
		{
			"com.liferay.portal.util.HttpImpl.max.total.connections",
			"com.liferay.portal.kernel.util.Http.max.total.connections"
		},
		{
			"com.liferay.portal.util.HttpImpl.proxy.auth.type",
			"com.liferay.portal.kernel.util.Http.proxy.auth.type"
		},
		{
			"com.liferay.portal.util.HttpImpl.proxy.ntlm.domain",
			"com.liferay.portal.kernel.util.Http.proxy.ntlm.domain"
		},
		{
			"com.liferay.portal.util.HttpImpl.proxy.ntlm.host",
			"com.liferay.portal.kernel.util.Http.proxy.ntlm.host"
		},
		{
			"com.liferay.portal.util.HttpImpl.proxy.password",
			"com.liferay.portal.kernel.util.Http.proxy.password"
		},
		{
			"com.liferay.portal.util.HttpImpl.proxy.username",
			"com.liferay.portal.kernel.util.Http.proxy.username"
		},
		{
			"com.liferay.portal.util.HttpImpl.timeout",
			"com.liferay.portal.kernel.util.Http.timeout"
		},
		{
			"com.liferay.util.format.PhoneNumberFormat",
			"phone.number.format.impl"
		},
		{
			"com.liferay.util.servlet.UploadServletRequest.max.size",
			"com.liferay.portal.upload.UploadServletRequestImpl.max.size"
		},
		{
			"com.liferay.util.servlet.UploadServletRequest.temp.dir",
			"com.liferay.portal.upload.UploadServletRequestImpl.temp.dir"
		},
		{
			"com.liferay.util.servlet.fileupload.LiferayFileItem.threshold." +
				"size",
			"com.liferay.portal.upload.LiferayFileItem.threshold.size"
		},
		{
			"com.liferay.util.servlet.fileupload.LiferayInputStream." +
				"threshold.size",
			"com.liferay.portal.upload.LiferayInputStream.threshold.size"
		}
	};

	private static final String[][] _MODULARIZED_PORTAL_KEYS = {

		// Asset

		{
			"asset.browser.search.with.database", "search.with.database",
			"com.liferay.asset.browser.web"
		},
		{
			"asset.categories.navigation.display.templates.config",
			"display.templates.config",
			"com.liferay.asset.categories.navigation.web"
		},
		{
			"asset.publisher.check.interval", "check.interval",
			"com.liferay.asset.publisher.web"
		},
		{
			"asset.publisher.email.from.address", "email.from.address",
			"com.liferay.asset.publisher.web"
		},
		{
			"asset.publisher.email.from.name", "email.from.name",
			"com.liferay.asset.publisher.web"
		},
		{
			"asset.publisher.email.asset.entry.added.enabled",
			"email.asset.entry.added.enabled", "com.liferay.asset.publisher.web"
		},
		{
			"asset.publisher.email.asset.entry.added.subject",
			"email.asset.entry.added.subject", "com.liferay.asset.publisher.web"
		},
		{
			"asset.publisher.email.asset.entry.added.body",
			"email.asset.entry.added.body", "com.liferay.asset.publisher.web"
		},
		{
			"asset.publisher.display.style.default", "display.style.default",
			"com.liferay.asset.publisher.web"
		},
		{
			"asset.publisher.display.styles", "display.styles",
			"com.liferay.asset.publisher.web"
		},
		{
			"asset.publisher.display.templates.config",
			"display.templates.config", "com.liferay.asset.publisher.web"
		},
		{
			"asset.publisher.dynamic.subscription.limit",
			"dynamic.subscription.limit", "com.liferay.asset.publisher.web"
		},
		{
			"asset.publisher.permission.checking.configurable",
			"permission.checking.configurable",
			"com.liferay.asset.publisher.web"
		},
		{
			"asset.publisher.search.with.index", "search.with.index",
			"com.liferay.asset.publisher.web"
		},
		{
			"asset.tags.navigation.display.templates.config",
			"display.templates.config", "com.liferay.asset.tags.navigation.web"
		},

		// Authentication Verifier

		{
			"auth.verifier.BasicAuthHeaderAutoLogin.basic_auth",
			"auth.verifier.BasicAuthHeaderAuthVerifier.basic_auth",
			"com.liferay.portal.security.auth.verifier"
		},
		{
			"auth.verifier.BasicAuthHeaderAutoLogin.hosts.allowed",
			"auth.verifier.BasicAuthHeaderAuthVerifier.hosts.allowed",
			"com.liferay.portal.security.auth.verifier"
		},
		{
			"auth.verifier.BasicAuthHeaderAutoLogin.urls.excludes",
			"auth.verifier.BasicAuthHeaderAuthVerifier.urls.excludes",
			"com.liferay.portal.security.auth.verifier"
		},
		{
			"auth.verifier.BasicAuthHeaderAutoLogin.urls.includes",
			"auth.verifier.BasicAuthHeaderAuthVerifier.urls.includes",
			"com.liferay.portal.security.auth.verifier"
		},

		{
			"auth.verifier.DigestAuthenticationAuthVerifier.digest_auth",
			"auth.verifier.DigestAuthenticationAuthVerifier.digest_auth",
			"com.liferay.portal.security.auth.verifier"
		},
		{
			"auth.verifier.DigestAuthenticationAuthVerifier.hosts.allowed",
			"auth.verifier.DigestAuthenticationAuthVerifier.hosts.allowed",
			"com.liferay.portal.security.auth.verifier"
		},
		{
			"auth.verifier.DigestAuthenticationAuthVerifier.urls.excludes",
			"auth.verifier.DigestAuthenticationAuthVerifier.urls.excludes",
			"com.liferay.portal.security.auth.verifier"
		},
		{
			"auth.verifier.DigestAuthenticationAuthVerifier.urls.includes",
			"auth.verifier.DigestAuthenticationAuthVerifier.urls.includes",
			"com.liferay.portal.security.auth.verifier"
		},

		{
			"auth.verifier.ImageRequestAuthVerifier.hosts.allowed",
			"auth.verifier.ImageRequestAuthVerifier.hosts.allowed",
			"com.liferay.document.library.document.conversion"
		},
		{
			"auth.verifier.ImageRequestAuthVerifier.urls.excludes",
			"auth.verifier.ImageRequestAuthVerifier.urls.excludes",
			"com.liferay.document.library.document.conversion"
		},
		{
			"auth.verifier.ImageRequestAuthVerifier.urls.includes",
			"auth.verifier.ImageRequestAuthVerifier.urls.includes",
			"com.liferay.document.library.document.conversion"
		},

		{
			"auth.verifier.ParameterAutoLogin.hosts.allowed",
			"auth.verifier.RequestParameterAuthVerifier.hosts.allowed",
			"com.liferay.portal.security.auth.verifier"
		},
		{
			"auth.verifier.ParameterAutoLogin.urls.excludes",
			"auth.verifier.RequestParameterAuthVerifier.urls.excludes",
			"com.liferay.portal.security.auth.verifier"
		},
		{
			"auth.verifier.ParameterAutoLogin.urls.includes",
			"auth.verifier.RequestParameterAuthVerifier.urls.includes",
			"com.liferay.portal.security.auth.verifier"
		},

		{
			"auth.verifier.PortalSessionAuthVerifier.hosts.allowed",
			"auth.verifier.PortalSessionAuthVerifier.hosts.allowed",
			"com.liferay.portal.security.auth.verifier"
		},
		{
			"auth.verifier.PortalSessionAuthVerifier.urls.excludes",
			"auth.verifier.PortalSessionAuthVerifier.urls.excludes",
			"com.liferay.portal.security.auth.verifier"
		},
		{
			"auth.verifier.PortalSessionAuthVerifier.urls.includes",
			"auth.verifier.PortalSessionAuthVerifier.urls.includes",
			"com.liferay.portal.security.auth.verifier"
		},

		{
			"auth.verifier.TunnelingServletAuthVerifier.hosts.allowed",
			"auth.verifier.TunnelAuthVerifier.hosts.allowed",
			"com.liferay.portal.security.auth.verifier"
		},
		{
			"auth.verifier.TunnelingServletAuthVerifier.urls.excludes",
			"auth.verifier.TunnelAuthVerifier.urls.excludes",
			"com.liferay.portal.security.auth.verifier"
		},
		{
			"auth.verifier.TunnelingServletAuthVerifier.urls.includes",
			"auth.verifier.TunnelAuthVerifier.urls.includes",
			"com.liferay.portal.security.auth.verifier"
		},

		// Blogs

		{
			"blogs.display.templates.config", "display.templates.config",
			"com.liferay.blogs.web"
		},

		{
			"blogs.entry.check.interval", "entry.check.interval",
			"com.liferay.blogs.web"
		},

		{
			"blogs.image.max.size", "blogs.image.max.size",
			"com.liferay.blogs.api"
		},

		{
			"blogs.image.extensions", "blogs.image.extensions",
			"com.liferay.blogs.api"
		},

		{
			"blogs.linkback.job.interval", "linkback.job.interval",
			"com.liferay.blogs.web"
		},

		// Bookmarks

		{
			"bookmarks.email.entry.added.body", "email.entry.added.body",
			"com.liferay.bookmarks.service"
		},
		{
			"bookmarks.email.entry.added.enabled", "email.entry.added.enabled",
			"com.liferay.bookmarks.service"
		},
		{
			"bookmarks.email.entry.added.subject", "email.entry.added.subject",
			"com.liferay.bookmarks.service"
		},
		{
			"bookmarks.email.entry.updated.body", "email.entry.updated.body",
			"com.liferay.bookmarks.service"
		},
		{
			"bookmarks.email.entry.updated.enabled",
			"email.entry.updated.enabled", "com.liferay.bookmarks.service"
		},
		{
			"bookmarks.email.entry.updated.subject",
			"email.entry.updated.subject", "com.liferay.bookmarks.service"
		},
		{
			"bookmarks.email.from.address", "email.from.address",
			"com.liferay.bookmarks.service"
		},
		{
			"bookmarks.email.from.name", "email.from.name",
			"com.liferay.bookmarks.service"
		},
		{
			"bookmarks.entry.columns", "entry.columns",
			"com.liferay.bookmarks.service"
		},
		{
			"bookmarks.folder.columns", "folder.columns",
			"com.liferay.bookmarks.service"
		},
		{
			"bookmarks.folders.search.visible", "folders.search.visible",
			"com.liferay.bookmarks.service"
		},
		{
			"bookmarks.related.assets.enabled", "related.assets.enabled",
			"com.liferay.bookmarks.service"
		},
		{
			"bookmarks.subfolders.visible", "subfolders.visible",
			"com.liferay.bookmarks.service"
		},

		// Breadcrumb

		{
			"breadcrumb.display.style.default", "ddm.template.key.default",
			"com.liferay.site.navigation.breadcrumb.web"
		},
		{
			"breadcrumb.display.templates.config", "display.templates.config",
			"com.liferay.site.navigation.breadcrumb.web"
		},
		{
			"breadcrumb.show.guest.group", "show.guest.group",
			"com.liferay.site.navigation.breadcrumb.web"
		},
		{
			"breadcrumb.show.parent.groups", "show.parent.groups",
			"com.liferay.site.navigation.breadcrumb.web"
		},

		// CAS

		{"cas.auth.enabled", "enabled", "com.liferay.portal.security.sso.cas"},
		{
			"cas.import.from.ldap", "import.from.ldap",
			"com.liferay.portal.security.sso.cas"
		},
		{"cas.login.url", "login.url", "com.liferay.portal.security.sso.cas"},
		{
			"cas.logout.on.session.expiration", "logout.on.session.expiration",
			"com.liferay.portal.security.sso.cas"
		},
		{
			"cas.logout.url", "logout.url",
			"com.liferay.portal.security.sso.cas"
		},
		{
			"cas.no.such.user.redirect.url", "no.such.user.redirect.url",
			"com.liferay.portal.security.sso.cas"
		},
		{
			"cas.server.name", "server.name",
			"com.liferay.portal.security.sso.cas"
		},
		{
			"cas.server.url", "server.url",
			"com.liferay.portal.security.sso.cas"
		},
		{
			"cas.service.url", "service.url",
			"com.liferay.portal.security.sso.cas"
		},

		// Cluster Link

		{
			"cluster.link.debug.enabled", "cluster.link.debug.enabled",
			"com.liferay.portal.cluster"
		},

		// Currency Converter

		{
			"currency.converter.symbols", "symbols",
			"com.liferay.currency.converter.web"
		},

		// Discussion

		{
			"discussion.email.body", "discussion.email.body",
			"com.liferay.comment.api"
		},

		{
			"discussion.email.comments.added.enabled",
			"discussion.email.comments.added.enabled", "com.liferay.comment.api"
		},

		{
			"discussion.email.subject", "discussion.email.subject",
			"com.liferay.comment.api"
		},

		// Document Library

		{
			"dl.file.rank.check.interval", "check.file.ranks.interval",
			"com.liferay.recent.documents.web"
		},
		{
			"dl.file.rank.max.size", "max.size",
			"com.liferay.document.library.file.rank.service"
		},
		{
			"dl.display.templates.config", "display.templates.config",
			"com.liferay.document.library.web"
		},
		{
			"dl.repository.cmis.delete.depth", "delete.depth",
			"com.liferay.document.library.repository.cmis"
		},
		{
			"dl.store.advanced.file.system.root.dir", "root.dir",
			"com.liferay.portal.store.filesystem"
		},
		{
			"dl.store.file.system.root.dir", "root.dir",
			"com.liferay.portal.store.filesystem"
		},
		{
			"dl.store.s3.access.key", "access.key",
			"com.liferay.portal.store.s3"
		},
		{
			"dl.store.s3.bucket.name", "bucket.name",
			"com.liferay.portal.store.s3"
		},
		{
			"dl.store.s3.jets3t[httpclient.max-connections]",
			"http.client.max.connections", "com.liferay.portal.store.s3"
		},
		{
			"dl.store.s3.jets3t[s3service.default-bucket-location]",
			"s3service.default.bucket.location", "com.liferay.portal.store.s3"
		},
		{
			"dl.store.s3.jets3t[s3service.default-storage-class]",
			"s3service.default.storage.class", "com.liferay.portal.store.s3"
		},
		{
			"dl.store.s3.jets3t[s3service.s3-endpoint]",
			"s3service.s3.endpoint", "com.liferay.portal.store.s3"
		},
		{
			"dl.store.s3.secret.key", "secret.key",
			"com.liferay.portal.store.s3"
		},
		{
			"dl.store.s3.temp.dir.clean.up.expunge",
			"temp.dir.clean.up.expunge", "com.liferay.portal.store.s3"
		},
		{
			"dl.store.s3.temp.dir.clean.up.frequency",
			"temp.dir.clean.up.frequency", "com.liferay.portal.store.s3"
		},
		{
			"dl.temporary.file.entries.check.interval",
			"temporary.file.entries.check.interval",
			"com.liferay.document.library.web"
		},

		// Dynamic Data Lists

		{
			"dynamic.data.lists.error.template",
			"dynamic.data.lists.error.template",
			"com.liferay.dynamic.data.lists.web"
		},
		{
			"dynamic.data.lists.storage.type",
			"dynamic.data.lists.storage.type",
			"com.liferay.dynamic.data.lists.web"
		},

		// Dynamic Data Mapping

		{
			"dynamic.data.mapping.image.extensions",
			"dynamic.data.mapping.image.extensions",
			"com.liferay.dynamic.data.mapping.service"
		},
		{
			"dynamic.data.mapping.image.small.max.size",
			"dynamic.data.mapping.image.small.max.size",
			"com.liferay.dynamic.data.mapping.service"
		},
		{
			"dynamic.data.mapping.structure.force.autogenerate.key",
			"dynamic.data.mapping.structure.force.autogenerate.key",
			"com.liferay.dynamic.data.mapping.web"
		},
		{
			"dynamic.data.mapping.template.force.autogenerate.key",
			"dynamic.data.mapping.template.force.autogenerate.key",
			"com.liferay.dynamic.data.mapping.web"
		},
		{
			"dynamic.data.mapping.template.language.default",
			"dynamic.data.mapping.template.language.default",
			"com.liferay.dynamic.data.mapping.web"
		},
		{
			"dynamic.data.mapping.template.language.content",
			"dynamic.data.mapping.template.language.content",
			"com.liferay.dynamic.data.mapping.web"
		},

		// Facebook Connect

		{
			"facebook.connect.auth.enabled", "enabled",
			"com.liferay.portal.security.sso.facebook.connect"
		},
		{
			"facebook.connect.app.id", "app.id",
			"com.liferay.portal.security.sso.facebook.connect"
		},
		{
			"facebook.connect.app.secret", "app.secret",
			"com.liferay.portal.security.sso.facebook.connect"
		},
		{
			"facebook.connect.graph.url", "graph.url",
			"com.liferay.portal.security.sso.facebook.connect"
		},
		{
			"facebook.connect.oauth.auth.url", "oauth.auth.url",
			"com.liferay.portal.security.sso.facebook.connect"
		},
		{
			"facebook.connect.oauth.redirect.url", "oauth.redirect.url",
			"com.liferay.portal.security.sso.facebook.connect"
		},
		{
			"facebook.connect.oauth.token.url", "oauth.token.url",
			"com.liferay.portal.security.sso.facebook.connect"
		},
		{
			"facebook.connect.verified.account.required",
			"verified.account.required",
			"com.liferay.portal.security.sso.facebook.connect"
		},

		// Flags

		{"flags.email.body", "email.body", "com.liferay.flags"},
		{
			"flags.email.from.address", "email.from.address",
			"com.liferay.flags"
		},
		{"flags.email.from.name", "email.from.name", "com.liferay.flags"},
		{"flags.email.subject", "email.subject", "com.liferay.flags"},
		{
			"flags.guest.users.enabled", "guest.users.enabled",
			"com.liferay.flags"
		},
		{"flags.reasons", "reasons", "com.liferay.flags"},

		// FreeMarker Engine

		{
			"freemarker.engine.localized.lookup", "localized.lookup",
			"com.liferay.portal.template.freemarker"
		},
		{
			"freemarker.engine.macro.library", "macro.library",
			"com.liferay.portal.template.freemarker"
		},
		{
			"freemarker.engine.resource.modification.check.interval",
			"resource.modification.check",
			"com.liferay.portal.template.freemarker"
		},
		{
			"freemarker.engine.restricted.classes", "restricted.classes",
			"com.liferay.portal.template.freemarker"
		},
		{
			"freemarker.engine.restricted.packages", "restricted.packages",
			"com.liferay.portal.template.freemarker"
		},
		{
			"freemarker.engine.template.exception.handler",
			"template.exception.handler",
			"com.liferay.portal.template.freemarker"
		},
		{
			"freemarker.engine.template.parsers", "template.parsers",
			"com.liferay.portal.template.freemarker"
		},
		{
			"journal.template.freemarker.restricted.variables",
			"restricted.variables", "com.liferay.portal.template.freemarker"
		},

		// IFrame

		{"iframe.auth", "auth", "com.liferay.iframe.web"},
		{"iframe.auth-type", "auth.type", "com.liferay.iframe.web"},
		{"iframe.form-method", "form.method", "com.liferay.iframe.web"},
		{
			"iframe.hidden-variables", "hidden.variables",
			"com.liferay.iframe.web"
		},

		// Journal

		{
			"journal.article.check.interval", "check.interval",
			"com.liferay.journal.web"
		},
		{
			"journal.article.comments.enabled",
			"journal.article.comments.enabled", "com.liferay.journal.service"
		},
		{
			"journal.article.custom.tokens", "journal.article.custom.tokens",
			"com.liferay.journal.service"
		},
		{
			"journal.article.database.keyword.search.content",
			"journal.article.database.keyword.search.content",
			"com.liferay.journal.service"
		},
		{
			"journal.article.expire.all.versions",
			"journal.article.expire.all.versions", "com.liferay.journal.service"
		},
		{
			"journal.article.force.autogenerate.id",
			"journal.article.force.autogenerate.id", "com.liferay.journal.web"
		},
		{
			"journal.articles.search.with.index",
			"journal.articles.search.with.index", "com.liferay.journal.web"
		},
		{
			"journal.article.storage.type", "journal.article.storage.type",
			"com.liferay.journal.service"
		},
		{
			"journal.article.token.page.break",
			"journal.article.token.page.break", "com.liferay.journal.service"
		},
		{
			"journal.article.view.permission.check.enabled",
			"journal.article.view.permission.check.enabled",
			"com.liferay.journal.service"
		},
		{
			"journal.articles.index.all.versions",
			"journal.articles.index.all.versions", "com.liferay.journal.service"
		},
		{
			"journal.char.blacklist", "char.blacklist",
			"com.liferay.journal.service"
		},
		{
			"journal.content.publish.to.live.by.default",
			"publish.to.live.by.default", "com.liferay.journal.content.web"
		},
		{
			"journal.content.search.show.listed", "show.listed",
			"com.liferay.journal.content.search.web"
		},
		{
			"journal.default.display.view", "default.display.view",
			"com.liferay.journal.web"
		},
		{"journal.display.views", "display.views", "com.liferay.journal.web"},
		{
			"journal.email.from.address", "email.from.address",
			"com.liferay.journal.service"
		},
		{
			"journal.email.from.name", "email.from.name",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.added.enabled",
			"email.article.added.enabled", "com.liferay.journal.service"
		},
		{
			"journal.email.article.added.subject",
			"email.article.added.subject", "com.liferay.journal.service"
		},
		{
			"journal.email.article.added.body", "email.article.added.body",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.approval.denied.enabled",
			"email.article.approval.denied.enabled",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.approval.denied.subject",
			"email.article.approval.denied.subject",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.approval.denied.body",
			"email.article.approval.denied.body", "com.liferay.journal.service"
		},
		{
			"journal.email.article.approval.granted.enabled",
			"email.article.approval.granted.enabled",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.approval.granted.subject",
			"email.article.approval.granted.subject",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.approval.granted.body",
			"email.article.approval.granted.body", "com.liferay.journal.service"
		},
		{
			"journal.email.article.approval.requested.enabled",
			"email.article.approval.requested.enabled",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.approval.requested.subject",
			"email.article.approval.requested.subject",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.approval.requested.body",
			"email.article.approval.requested.body",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.moved.to.folder.enabled",
			"email.article.moved.to.folder.enabled",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.moved.to.folder.subject",
			"email.article.moved.to.folder.subject",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.moved.from.folder.body",
			"email.article.moved.from.folder.body",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.moved.from.folder.enabled",
			"email.article.moved.from.folder.enabled",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.moved.from.folder.subject",
			"email.article.moved.from.folder.subject",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.moved.from.folder.body",
			"email.article.moved.from.folder.body",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.review.enabled",
			"email.article.review.enabled", "com.liferay.journal.service"
		},
		{
			"journal.email.article.review.subject",
			"email.article.review.subject", "com.liferay.journal.service"
		},
		{
			"journal.email.article.review.body", "email.article.review.body",
			"com.liferay.journal.service"
		},
		{
			"journal.email.article.updated.enabled",
			"email.article.updated.enabled", "com.liferay.journal.service"
		},
		{
			"journal.email.article.updated.subject",
			"email.article.updated.subject", "com.liferay.journal.service"
		},
		{
			"journal.email.article.updated.body", "email.article.updated.body",
			"com.liferay.journal.service"
		},
		{
			"journal.error.template[ftl]", "error.template[ftl]",
			"com.liferay.journal.service"
		},
		{
			"journal.error.template[vm]", "error.template[vm]",
			"com.liferay.journal.service"
		},
		{
			"journal.feed.force.autogenerate.id",
			"journal.feed.force.autogenerate.id", "com.liferay.journal.web"
		},
		{
			"journal.folder.icon.check.count",
			"journal.folder.icon.check.count", "com.liferay.journal.service"
		},
		{
			"journal.lar.creation.strategy", "lar.creation.strategy",
			"com.liferay.journal.service"
		},
		{
			"journal.publish.to.live.by.default", "publish.to.live.by.defaul",
			"com.liferay.journal.web"
		},
		{
			"journal.publish.version.history.by.default",
			"publish.version.history.by.default", "com.liferay.journal.web"
		},
		{
			"journal.sync.content.search.on.startup",
			"sync.content.search.on.startup", "com.liferay.journal.service"
		},
		{
			"journal.template.language.content[css]",
			"journal.article.template.language.content[css]",
			"com.liferay.journal.web"
		},
		{
			"journal.template.language.content[ftl]",
			"journal.article.template.language.content[ftl]",
			"com.liferay.journal.web"
		},
		{
			"journal.template.language.content[vm]",
			"journal.article.template.language.content[vm]",
			"com.liferay.journal.web"
		},
		{
			"journal.transformer.listener", "transformer.listener",
			"com.liferay.journal.service"
		},
		{
			"journal.transformer.regex.pattern", "transformer.regex.pattern",
			"com.liferay.journal.service"
		},
		{
			"journal.transformer.regex.replacement",
			"transformer.regex.replacement", "com.liferay.journal.service"
		},
		{
			"terms.of.use.journal.article.group.id",
			"terms.of.use.journal.article.group.id",
			"com.liferay.journal.service"
		},
		{
			"terms.of.use.journal.article.id",
			"terms.of.use.journal.article.id", "com.liferay.journal.service"
		},

		// Language

		{
			"language.display.style.default", "ddm.template.key.default",
			"com.liferay.site.navigation.language.web"
		},
		{
			"language.display.templates.config", "display.templates.config",
			"com.liferay.site.navigation.language.web"
		},

		// Lucene

		{
			"lucene.analyzer.max.tokens", "analyzer.max.tokens",
			"com.liferay.portal.search.lucene"
		},
		{
			"lucene.buffer.size", "buffer.size",
			"com.liferay.portal.search.lucene"
		},
		{
			"lucene.commit.batch.size", "commit.batch.size",
			"com.liferay.portal.search.lucene"
		},
		{
			"lucene.commit.time.interval", "commit.time.interval",
			"com.liferay.portal.search.lucene"
		},
		{"lucene.dir", "dir", "com.liferay.portal.search.lucene"},
		{
			"lucene.merge.factor", "merge.factor",
			"com.liferay.portal.search.lucene"
		},
		{
			"lucene.merge.policy", "merge.policy",
			"com.liferay.portal.search.lucene"
		},
		{
			"lucene.merge.scheduler", "merge.scheduler",
			"com.liferay.portal.search.lucene"
		},
		{
			"lucene.store.type", "store.type",
			"com.liferay.portal.search.lucene"
		},
		{
			"lucene.store.type.file.force.mmap", "store.type.file.force.mmp",
			"com.liferay.portal.search.lucene"
		},

		// Message Boards

		{
			"message.boards.expire.ban.job.interval", "expire.ban.job.interval",
			"com.liferay.message.boards.web"
		},

		// Monitoring

		{
			"monitoring.portal.request", "monitor.portal.request",
			"com.liferay.portal.monitoring"
		},
		{
			"monitoring.portlet.action.request",
			"monitor.portlet.action.request", "com.liferay.portal.monitoring"
		},
		{
			"monitoring.portlet.event.request", "monitor.portlet.event.request",
			"com.liferay.portal.monitoring"
		},
		{
			"monitoring.portlet.render.request",
			"monitor.portlet.render.request", "com.liferay.portal.monitoring"
		},
		{
			"monitoring.portlet.resource.request",
			"monitor.portlet.resource.request", "com.liferay.portal.monitoring"
		},
		{
			"monitoring.show.per.request.data.sample",
			"show.per.request.data.sample", "com.liferay.portal.monitoring"
		},

		// Navigation

		{
			"navigation.display.style.default", "ddm.template.key.default",
			"com.liferay.site.navigation.menu.web"
		},
		{
			"navigation.display.style.options", "display.style.options",
			"com.liferay.site.navigation.menu.web"
		},

		// Nested Portlets

		{
			"nested.portlets.layout.template.default",
			"layout.template.default", "com.liferay.nested.portlets.web"
		},
		{
			"nested.portlets.layout.template.unsupported",
			"layout.template.unsupported", "com.liferay.nested.portlets.web"
		},

		// OpenID

		{
			"open.id.auth.enabled", "enabled",
			"com.liferay.portal.security.sso.openid"
		},
		{
			"open.id.providers", "providers",
			"com.liferay.portal.security.sso.openid"
		},
		{
			"open.id.ax.schema[default]", "ax.schema",
			"com.liferay.portal.security.sso.openid"
		},
		{
			"open.id.ax.type.email[default]", "ax.type.email",
			"com.liferay.portal.security.sso.openid"
		},
		{
			"open.id.ax.type.firstname[default]", "ax.type.firstname",
			"com.liferay.portal.security.sso.openid"
		},
		{
			"open.id.ax.type.lastname[default]", "ax.type.lastname",
			"com.liferay.portal.security.sso.openid"
		},
		{
			"open.id.ax.schema[yahoo]", "ax.schema",
			"com.liferay.portal.security.sso.openid"
		},
		{
			"open.id.ax.type.email[yahoo]", "ax.type.email",
			"com.liferay.portal.security.sso.openid"
		},
		{
			"open.id.ax.type.fullname[yahoo]", "ax.type.fullname",
			"com.liferay.portal.security.sso.openid"
		},
		{
			"open.id.url[yahoo]", "url",
			"com.liferay.portal.security.sso.openid"
		},

		// OpenSSO

		{
			"open.sso.auth.enabled", "enabled",
			"com.liferay.portal.security.sso.opensso"
		},
		{
			"open.sso.email.address.attr", "email.address.attr",
			"com.liferay.portal.security.sso.opensso"
		},
		{
			"open.sso.first.name.attr", "first.name.attr",
			"com.liferay.portal.security.sso.opensso"
		},
		{
			"open.sso.last.name.attr", "last.name.attr",
			"com.liferay.portal.security.sso.opensso"
		},
		{
			"open.sso.import.from.ldap", "import.from.ldap",
			"com.liferay.portal.security.sso.opensso"
		},
		{
			"open.sso.login.url", "login.url",
			"com.liferay.portal.security.sso.opensso"
		},
		{
			"open.sso.logout.on.session.expiration",
			"logout.on.session.expiration",
			"com.liferay.portal.security.sso.opensso"
		},
		{
			"open.sso.logout.url", "logout.url",
			"com.liferay.portal.security.sso.opensso"
		},
		{
			"open.sso.screen.name.attr", "screen.name.attr",
			"com.liferay.portal.security.sso.opensso"
		},
		{
			"open.sso.service.url", "service.url",
			"com.liferay.portal.security.sso.opensso"
		},

		// Permissions

		{
			"permissions.inline.sql.check.enabled", "sqlCheckEnabled",
			"com.liferay.portal.security.permission.impl"
		},

		// Polls

		{
			"polls.publish.to.live.by.default", "publish.to.live.by.default",
			"com.liferay.polls.service"
		},

		// Request Header

		{
			"request.header.auth.hosts.allowed", "authHostsAllowed",
			"com.liferay.portal.security.auto.login"
		},

		{
			"request.header.auth.import.from.ldap", "importFromLDAP",
			"com.liferay.portal.security.auto.login"
		},

		// RSS

		{
			"rss.display.templates.config", "display.templates.config",
			"com.liferay.rss.web"
		},

		// Scripting

		{
			"scripting.forbidden.classes", "forbidden.classes",
			"com.liferay.portal.scripting.javascript"
		},
		{
			"scripting.jruby.load.paths", "load.paths",
			"com.liferay.portal.scripting.ruby"
		},

		// Search

		{
			"search.facet.configuration", "facet.configuration",
			"com.liferay.search.web"
		},

		// Session

		{
			"session.timeout.auto.extend", "auto-extend",
			"com.liferay.frontend.js.web"
		},
		{
			"session.timeout.auto.extend.offset", "auto-extend-offset",
			"com.liferay.frontend.js.web"
		},

		// Site Map

		{
			"sitemap.display.templates.config", "display.templates.config",
			"com.liferay.site.navigation.site.map.web"
		},

		// Staging

		{
			"staging.draft.export.import.configuration.check.interval",
			"draft.export.import.configuration.check.interval",
			"com.liferay.exportimport.web"
		},
		{
			"staging.draft.export.import.configuration.clean.up.count",
			"draft.export.import.configuration.clean.up.count",
			"com.liferay.exportimport.web"
		},

		// Social Activity

		{
			"social.activity.contribution.increments",
			"contribution.increments", "com.liferay.social.activity"
		},
		{
			"social.activity.contribution.limit.values",
			"contribution.limit.values", "com.liferay.social.activity"
		},
		{
			"social.activity.participation.increments",
			"participation.increments", "com.liferay.social.activity"
		},
		{
			"social.activity.participation.limit.values",
			"participation.limit.values", "com.liferay.social.activity"
		},

		// Tags Compiler

		{
			"tags.compiler.enabled", "enabled",
			"com.liferay.asset.tags.compiler.web"
		},

		// Text Extraction

		{
			"text.extraction.fork.process.enabled",
			"text-extraction-fork-process-enabled",
			"com.liferay.portal.tika"
		},
		{
			"text.extraction.fork.process.mime.types",
			"text-extraction-fork-process-mime-types",
			"com.liferay.portal.tika"
		},

		// Translator

		{
			"translator.default.languages", "translation.id",
			"com.liferay.translator.web"
		},
		{"translator.languages", "language.ids", "com.liferay.translator.web"},

		// Velocity Engine

		{
			"velocity.engine.directive.if.to.string.null.check",
			"directive.if.to.string.null.check",
			"com.liferay.portal.template.velocity"
		},
		{
			"velocity.engine.resource.parsers", "resource.parsers",
			"com.liferay.portal.template.velocity"
		},
		{
			"velocity.engine.resource.modification.check.interval",
			"resource.modification.check.interval",
			"com.liferay.portal.template.velocity"
		},
		{
			"velocity.engine.restricted.classes", "restricted.classes",
			"com.liferay.portal.template.velocity"
		},
		{
			"velocity.engine.restricted.packages", "restricted.packages",
			"com.liferay.portal.template.velocity"
		},
		{
			"velocity.engine.restricted.variables", "restricted.variables",
			"com.liferay.portal.template.velocity"
		},
		{
			"velocity.engine.velocimacro.library", "macro.library",
			"com.liferay.portal.template.velocity"
		},
		{
			"velocity.engine.logger", "logger",
			"com.liferay.portal.template.velocity"
		},
		{
			"velocity.engine.logger.category", "logger.category",
			"com.liferay.portal.template.velocity"
		},

		// View Count

		{
			"view.count.enabled", "enabled", "com.liferay.view.count.service"
		},

		// XSL Content

		{
			"xsl.content.valid.url.prefixes", "valid.url.prefixes",
			"com.liferay.xsl.content.web"
		},
		{
			"xsl.content.xml.doctype.declaration.allowed",
			"xml.doctype.declaration.allowed", "com.liferay.xsl.content.web"
		},
		{
			"xsl.content.xml.external.general.entities.allowed",
			"xml.external.general.entities.allowed",
			"com.liferay.xsl.content.web"
		},
		{
			"xsl.content.xml.external.parameter.entities.allowed",
			"xml.external.parameter.entities.allowed",
			"com.liferay.xsl.content.web"
		},
		{
			"xsl.content.xsl.secure.processing.enabled",
			"xsl.secure.processing.enabled", "com.liferay.xsl.content.web"
		},
	};

	private static final String[][] _MODULARIZED_SYSTEM_KEYS = {

		// Calendar

		{
			"ical4j.compatibility.outlook", "ical4j.compatibility.outlook",
			"com.liferay.calendar.service"
		},
		{
			"ical4j.parsing.relaxed", "ical4j.parsing.relaxed",
			"com.liferay.calendar.service"
		},
		{
			"ical4j.unfolding.relaxed", "ical4j.unfolding.relaxed",
			"com.liferay.calendar.service"
		},
		{
			"ical4j.validation.relaxed", "ical4j.validation.relaxed",
			"com.liferay.calendar.service"
		},

		// Tika

		{
			"tika.config", "tika-config-xml", "com.liferay.portal.tika"
		}

	};

	private static final String[] _OBSOLETE_PORTAL_KEYS = {
		"aim.login", "aim.login", "amazon.access.key.id",
		"amazon.associate.tag", "amazon.secret.access.key",
		"asset.categories.properties.default", "asset.entry.validator",
		"asset.publisher.asset.entry.query.processors",
		"asset.publisher.filter.unlistable.entries",
		"asset.publisher.query.form.configuration",
		"asset.tag.permissions.enabled", "asset.tag.properties.default",
		"asset.tag.properties.enabled", "asset.tag.suggestions.enabled",
		"auth.login.prompt.enabled", "auth.max.failures.limit",
		"auth.user.uuid.store.enabled", "auto.deploy.blacklist.threshold",
		"auto.deploy.copy.commons.logging", "auto.deploy.copy.log4j",
		"auto.deploy.custom.portlet.xml", "auto.deploy.dest.dir",
		"auto.deploy.default.dest.dir", "auto.deploy.jboss.dest.dir",
		"auto.deploy.jboss.dest.dir[5]", "auto.deploy.jboss.prefix",
		"auto.deploy.tomcat.dest.dir", "auto.deploy.tomcat.lib.dir",
		"auto.deploy.unpack.war", "auto.deploy.weblogic.dest.dir",
		"auto.deploy.websphere.dest.dir",
		"auto.deploy.websphere.wsadmin.app.manager.install.options",
		"auto.deploy.websphere.wsadmin.app.manager.list.options",
		"auto.deploy.websphere.wsadmin.app.manager.query",
		"auto.deploy.websphere.wsadmin.app.manager.update.options",
		"auto.deploy.websphere.wsadmin.app.name.suffix",
		"auto.deploy.websphere.wsadmin.properties.file.name",
		"auto.deploy.wildfly.dest.dir", "auto.deploy.wildfly.prefix",
		"axis.servlet.enabled", "axis.servlet.hosts.allowed",
		"axis.servlet.https.required", "axis.servlet.mapping",
		"blogs.image.small.max.size", "breadcrumb.display.style.options",
		"browser.compatibility.ie.versions",
		"buffered.increment.parallel.queue.size",
		"buffered.increment.serial.queue.size",
		"cache.clear.on.context.initialization",
		"cache.clear.on.plugin.undeploy", "calendar.publish.to.live.by.default",
		"captcha.max.challenges", "captcha.check.portal.create_account",
		"captcha.check.portal.send_password",
		"captcha.check.portlet.message_boards.edit_category",
		"captcha.check.portlet.message_boards.edit_message",
		"captcha.engine.impl", "captcha.engine.recaptcha.key.private",
		"captcha.engine.recaptcha.key.public",
		"captcha.engine.recaptcha.url.script",
		"captcha.engine.recaptcha.url.noscript",
		"captcha.engine.recaptcha.url.verify",
		"captcha.engine.simplecaptcha.height",
		"captcha.engine.simplecaptcha.width",
		"captcha.engine.simplecaptcha.background.producers",
		"captcha.engine.simplecaptcha.gimpy.renderers",
		"captcha.engine.simplecaptcha.noise.producers",
		"captcha.engine.simplecaptcha.text.producers",
		"captcha.engine.simplecaptcha.word.renderers", "cas.validate.url",
		"change.tracking.sql.transformer.cache.size",
		"cluster.executor.heartbeat.interval",
		"cluster.link.node.bootup.response.timeout",
		"com.liferay.filters.doubleclick.DoubleClickFilter",
		"com.liferay.portal.servlet.filters.audit.AuditFilter",
		"com.liferay.portal.servlet.filters.doubleclick.DoubleClickFilter",
		"com.liferay.portal.servlet.filters.charbufferpool." +
			"CharBufferPoolFilter",
		"com.liferay.portal.servlet.filters.i18n.I18nFilter",
		"com.liferay.portal.servlet.filters.jsoncontenttype." +
			"JSONContentTypeFilter",
		"com.liferay.portal.servlet.filters.monitoring.MonitoringFilter",
		"com.liferay.portal.servlet.filters.secure.SecureFilter",
		"com.liferay.portal.servlet.filters.sso.opensso.OpenSSOFilter",
		"com.liferay.portal.servlet.filters.unsyncprintwriterpool." +
			"UnsyncPrintWriterPoolFilter",
		"com.liferay.portal.servlet.filters.validhtml.ValidHtmlFilter",
		"com.liferay.portal.upload.UploadServletRequestImpl.max.size",
		"com.liferay.portal.upload.UploadServletRequestImpl.temp.dir",
		"commons.pool.enabled", "company.security.auth.requires.https",
		"company.security.send.password", "company.settings.form.configuration",
		"company.settings.form.identification",
		"company.settings.form.miscellaneous", "company.settings.form.social",
		"control.panel.home.portlet.id",
		"control.panel.navigation.max.organizations",
		"control.panel.navigation.max.sites", "convert.processes",
		"counter.jdbc.prefix", "data.limit.max.dl.storage.size",
		"data.limit.max.journal.article.count",
		"data.limit.max.journal.folder.count",
		"data.limit.max.mail.message.count",
		"data.limit.max.mail.message.period",
		"data.limit.max.organization.count", "data.limit.max.role.count",
		"data.limit.max.site.count", "data.limit.max.team.count",
		"data.limit.max.user.count", "default.guest.public.layout.column-1",
		"default.guest.public.layout.column-2",
		"default.guest.public.layout.column-3",
		"default.guest.public.layout.column-4",
		"default.guest.public.layout.wap.color.scheme.id",
		"default.guest.public.layout.wap.theme.id",
		"default.user.private.layout.wap.color.scheme.id",
		"default.user.private.layout.wap.theme.id",
		"default.user.public.layout.wap.color.scheme.id",
		"default.user.public.layout.wap.theme.id",
		"default.wap.color.scheme.id", "default.wap.theme.id",
		"discussion.subscribe.by.default", "discussion.thread.view",
		"dl.file.entry.image.exif.metadata.rotation.enabled",
		"dl.file.entry.open.in.ms.office.manual.check.in.required",
		"dl.file.entry.preview.auto.create.on.upgrade",
		"dl.file.entry.preview.document.depth",
		"dl.file.entry.previewable.processor.max.size",
		"dl.file.entry.processors", "dl.file.entry.read.count.enabled",
		"dl.file.entry.thumbnail.video.frame.percentage",
		"dl.file.entry.type.ig.image.auto.create.on.upgrade",
		"dl.file.entry.version.policy", "dl.file.extensions",
		"dl.file.indexing.interval", "dl.file.max.size", "dl.file.rank.enabled",
		"dl.folder.icon.check.count", "dl.folder.menu.visible",
		"dl.hook.cmis.credentials.password",
		"dl.hook.cmis.credentials.username", "dl.hook.cmis.repository.url",
		"dl.hook.cmis.system.root.dir", "dl.hook.file.system.root.dir",
		"dl.hook.jcr.fetch.delay", "dl.hook.jcr.fetch.max.failures",
		"dl.hook.jcr.move.version.labels", "dl.store.antivirus.impl",
		"dl.store.cmis.credentials.username",
		"dl.store.cmis.credentials.password", "dl.store.cmis.repository.url",
		"dl.store.cmis.system.root.dir", "dl.store.file.system.root.dir",
		"dl.store.jcr.fetch.delay", "dl.store.jcr.fetch.max.failures",
		"dl.store.jcr.move.version.labels", "dl.tabs.visible",
		"dockbar.add.portlets", "dockbar.administrative.links.show.in.pop.up",
		"dynamic.data.lists.record.set.force.autogenerate.key",
		"dynamic.data.lists.template.language.parser[ftl]",
		"dynamic.data.lists.template.language.parser[vm]",
		"dynamic.data.lists.template.language.parser[xsl]",
		"dynamic.data.mapping.structure.index.with.thread",
		"dynamic.data.mapping.structure.private.field.names",
		"dynamic.data.mapping.structure.private.field.datatype[_fieldsDisplay]",
		"dynamic.data.mapping.structure.private.field.repeatable[" +
			"_fieldsDisplay]",
		"dynamic.data.mapping.template.language.types",
		"dynamic.resource.servlet.allowed.paths", "editor.ckeditor.version",
		"editor.inline.editing.enabled",
		"editor.wysiwyg.portal-web.docroot.html.portlet.asset_publisher." +
			"configuration.jsp",
		"editor.wysiwyg.portal-web.docroot.html.portlet.blogs.configuration." +
			"jsp",
		"editor.wysiwyg.portal-web.docroot.html.portlet.bookmarks." +
			"configuration.jsp",
		"editor.wysiwyg.portal-web.docroot.html.portlet.document_library." +
			"editor.wysiwyg.portal-web.docroot.html.portlet.invitation." +
				"configuration.jsp",
		"editor.wysiwyg.portal-web.docroot.html.portlet.journal." +
			"configuration.jsp",
		"editor.wysiwyg.portal-web.docroot.html.portlet.login.configuration." +
			"jsp",
		"editor.wysiwyg.portal-web.docroot.html.portlet.mail.edit.jsp",
		"editor.wysiwyg.portal-web.docroot.html.portlet.mail.edit_message.jsp",
		"editor.wysiwyg.portal-web.docroot.html.portlet.message_boards." +
			"configuration.jsp",
		"editor.wysiwyg.portal-web.docroot.html.portlet.message_boards." +
			"edit_message.bb_code.jsp",
		"editor.wysiwyg.portal-web.docroot.html.portlet.message_boards." +
			"edit_message.html.jsp",
		"editor.wysiwyg.portal-web.docroot.html.portlet.portal_settings." +
			"email_notifications.jsp",
		"editor.wysiwyg.portal-web.docroot.html.taglib.ui.discussion.jsp",
		"ehcache.blocking.cache.allowed",
		"ehcache.bootstrap.cache.loader.enabled",
		"ehcache.bootstrap.cache.loader.factory",
		"ehcache.bootstrap.cache.loader.properties",
		"ehcache.bootstrap.cache.loader.properties.default",
		"ehcache.cache.event.listener.factory",
		"ehcache.cache.manager.peer.listener.factory",
		"ehcache.cache.manager.peer.provider.factory",
		"ehcache.cache.manager.statistics.thread.pool.size",
		"ehcache.multi.vm.config.location.peerProviderProperties",
		"ehcache.rmi.peer.listener.factory.class",
		"ehcache.rmi.peer.listener.factory.properties",
		"ehcache.rmi.peer.provider.factory.class",
		"ehcache.rmi.peer.provider.factory.properties",
		"ehcache.socket.so.timeout", "ehcache.socket.start.port",
		"ehcache.statistics.enabled", "enterprise.product.commerce.enabled",
		"finalize.manager.thread.enabled",
		"hot.deploy.hook.custom.jsp.verification.enabled",
		"hot.undeploy.enabled", "hot.undeploy.interval",
		"hot.undeploy.on.redeploy", "hibernate.cache.region.factory_class",
		"hibernate.cache.use_minimal_puts", "hibernate.cache.use_query_cache",
		"hibernate.cache.use_second_level_cache",
		"hibernate.cache.use_structured_entries",
		"hibernate.connection.release_mode",
		"hibernate.session.factory.imported.class.name.regexp", "icq.jar",
		"icq.login", "icq.password", "image.hook.impl",
		"image.hook.file.system.root.dir", "index.dump.compression.enabled",
		"index.filter.search.limit", "index.on.upgrade",
		"index.permission.filter.search.amplification.factor",
		"index.portal.field.analyzer.enabled", "index.search.engine.id",
		"index.search.highlight.enabled", "index.search.writer.max.queue.size",
		"index.read.only", "index.with.thread", "intraband.impl",
		"intraband.mailbox.reaper.thread.enabled",
		"intraband.mailbox.storage.life", "intraband.proxy.dump.classes.dir",
		"intraband.proxy.dump.classes.enabled", "intraband.timeout.default",
		"intraband.welder.impl", "intraband.welder.socket.buffer.size",
		"intraband.welder.socket.keep.alive",
		"intraband.welder.socket.reuse.address",
		"intraband.welder.socket.server.start.port",
		"intraband.welder.socket.so.linger",
		"intraband.welder.socket.so.timeout",
		"intraband.welder.socket.tcp.no.delay",
		"invitation.email.max.recipients", "invitation.email.message.body",
		"invitation.email.message.subject", "invoker.filter.chain.cache.size",
		"javadoc.manager.enabled", "jakarta.persistence.validation.mode",
		"jbi.workflow.url", "jcr.initialize.on.startup",
		"jcr.jackrabbit.config.file.path",
		"jcr.jackrabbit.credentials.password",
		"jcr.jackrabbit.credentials.username", "jcr.jackrabbit.repository.home",
		"jcr.jackrabbit.repository.root", "jcr.node.documentlibrary",
		"jcr.workspace.name", "jcr.wrap.session",
		"jdbc.default.liferay.pool.provider", "jdbc.default.acquireIncrement",
		"jdbc.default.acquireRetryAttempts", "jdbc.default.acquireRetryDelay",
		"jdbc.default.connectionCustomizerClassName",
		"jdbc.default.defaultTransactionIsolation", "jdbc.default.fairQueue",
		"jdbc.default.idleConnectionTestPeriod", "jdbc.default.initialPoolSize",
		"jdbc.default.jdbcInterceptors", "jdbc.default.jmxEnabled",
		"jdbc.default.logAbandoned", "jdbc.default.maxActive",
		"jdbc.default.maxIdleTime", "jdbc.default.maxPoolSize",
		"jdbc.default.minIdle", "jdbc.default.minPoolSize",
		"jdbc.default.numHelperThreads", "jdbc.default.removeAbandonedTimeout",
		"jdbc.default.testWhileIdle",
		"jdbc.default.timeBetweenEvictionRunsMillis", "jdbc.default.useEquals",
		"jdbc.default.validationQuery", "json.deserializer.strict.mode",
		"journal.article.form.add", "journal.article.form.default.values",
		"journal.article.form.update", "journal.article.form.translate",
		"journal.article.types", "journal.articles.page.delta.values",
		"journal.browse.by.structures.sorted.by.name",
		"journal.error.template[xsl]", "journal.image.extensions",
		"journal.image.small.max.size",
		"journal.template.language.content[xsl]",
		"journal.template.language.parser[css]",
		"journal.template.language.parser[ftl]",
		"journal.template.language.parser[vm]",
		"journal.template.language.parser[xsl]",
		"journal.template.language.types", "jpa.configs",
		"jpa.database.platform", "jpa.database.type", "jpa.load.time.weaver",
		"jpa.provider", "jpa.provider.property.eclipselink.allow-zero-id",
		"jpa.provider.property.eclipselink.logging.level",
		"jpa.provider.property.eclipselink.logging.timestamp",
		"language.display.style.options", "layout.comments.enabled",
		"layout.configuration.action.update[embedded]",
		"layout.configuration.action.update[link_to_layout]",
		"layout.configuration.action.update[url]",
		"layout.configuration.action.delete[embedded]",
		"layout.configuration.action.delete[link_to_layout]",
		"layout.configuration.action.delete[url]",
		"layout.edit.page[control_panel]", "layout.edit.page[embedded]",
		"layout.edit.page[link_to_layout]", "layout.edit.page[panel]",
		"layout.edit.page[url]", "layout.first.pageable[control_panel]",
		"layout.first.pageable[embedded]",
		"layout.first.pageable[link_to_layout]", "layout.first.pageable[panel]",
		"layout.first.pageable[url]", "layout.form.add", "layout.form.update",
		"layout.parallel.render.enable",
		"layout.parallel.render.thread.pool.allow.core.thread.timeout",
		"layout.parallel.render.thread.pool.core.thread.count",
		"layout.parallel.render.thread.pool.keep.alive.time",
		"layout.parallel.render.thread.pool.max.queue.size",
		"layout.parallel.render.thread.pool.max.thread.count",
		"layout.parallel.render.timeout", "layout.parentable[control_panel]",
		"layout.parentable[embedded]", "layout.parentable[link_to_layout]",
		"layout.parentable[panel]", "layout.parentable[url]",
		"layout.reset.portlet.ids", "layout.set.form.update",
		"layout.sitemapable[embedded]", "layout.sitemapable[link_to_layout]",
		"layout.sitemapable[url]", "layout.types", "layout.url[control_panel]",
		"layout.url[embedded]", "layout.url[link_to_layout]",
		"layout.url[panel]", "layout.url[url]",
		"layout.url.friendliable[control_panel]",
		"layout.url.friendliable[embedded]",
		"layout.url.friendliable[link_to_layout]",
		"layout.url.friendliable[panel]", "layout.url.friendliable[url]",
		"layout.view.page[control_panel]", "layout.view.page[embedded]",
		"layout.view.page[link_to_layout]", "layout.view.page[panel]",
		"layout.view.page[url]", "ldap.clock.skew",
		"ldap.ignore.user.search.filter.for.auth=true",
		"learn.resources.cdn.enabled", "learn.resources.enabled",
		"learn.resources.refresh.time", "library.download.url.resin.jar",
		"library.download.url.script-10.jar", "liferay.lib.global.shared.dir",
		"liferay.web.portal.dir", "look.and.feel.modifiable", "lucene.analyzer",
		"lucene.cluster.index.loading.sync.timeout", "lucene.file.extractor",
		"lucene.file.extractor.regexp.strip", "lucene.replicate.write",
		"lucene.store.jdbc.auto.clean.up",
		"lucene.store.jdbc.auto.clean.up.enabled",
		"lucene.store.jdbc.auto.clean.up.interval",
		"lucene.store.jdbc.dialect.db2", "lucene.store.jdbc.dialect.derby",
		"lucene.store.jdbc.dialect.hsqldb", "lucene.store.jdbc.dialect.jtds",
		"lucene.store.jdbc.dialect.microsoft",
		"lucene.store.jdbc.dialect.mysql", "lucene.store.jdbc.dialect.oracle",
		"lucene.store.jdbc.dialect.postgresql", "mail.audit.trail",
		"mail.batch.size", "mail.hook.cyrus.add.user",
		"mail.hook.cyrus.delete.user", "mail.hook.cyrus.home",
		"mail.hook.fusemail.account.type", "mail.hook.fusemail.group.parent",
		"mail.hook.fusemail.password", "mail.hook.fusemail.url",
		"mail.hook.fusemail.username", "mail.hook.impl",
		"mail.hook.sendmail.add.user", "mail.hook.sendmail.change.password",
		"mail.hook.sendmail.delete.user", "mail.hook.sendmail.home",
		"mail.hook.sendmail.virtusertable",
		"mail.hook.sendmail.virtusertable.refresh", "mail.hook.shell.script",
		"mail.send.blacklist", "mail.session.jndi.name", "mail.session.mail",
		"mail.session.mail.advanced.properties", "mail.session.mail.pop3.host",
		"mail.session.mail.pop3.password", "mail.session.mail.pop3.port",
		"mail.session.mail.pop3.user", "mail.session.mail.smtp.auth",
		"mail.session.mail.smtp.host", "mail.session.mail.smtp.password",
		"mail.session.mail.smtp.port", "mail.session.mail.smtp.starttls.enable",
		"mail.session.mail.smtp.user", "mail.session.mail.store.protocol",
		"mail.session.mail.transport.protocol",
		"mail.throws.exception.on.failure",
		"memory.cluster.scheduler.lock.cache.enabled",
		"message.boards.email.message.added.signature",
		"message.boards.email.message.updated.signature",
		"message.boards.thread.locking.enabled",
		"message.boards.thread.previous.and.next.navigation.enabled",
		"message.boards.thread.views", "message.boards.thread.views.default",
		"microsoft.translator.client.id", "microsoft.translator.client.secret",
		"minifier.inline.content.cache.size",
		"mobile.device.styling.wap.enabled", "module.framework.initial.bundles",
		"module.framework.properties.ds.lock.timeout.milliseconds",
		"module.framework.properties.ds.stop.timeout.milliseconds",
		"module.framework.properties.felix.fileinstall.disableNio2",
		"module.framework.properties.felix.fileinstall.log.level",
		"module.framework.properties.file.install.disableNio2",
		"module.framework.properties.file.install.log.level",
		"module.framework.properties.file.install.optionalImportRefreshScope",
		"module.framework.properties.lpkg.deployer.dir",
		"module.framework.properties.lpkg.index.validator.enabled",
		"module.framework.register.liferay.services",
		"module.framework.resolver.revision.batch.size", "msn.login",
		"msn.password", "multicast.group.address[\"hibernate\"]",
		"multicast.group.port[\"hibernate\"]", "my.sites.display.style",
		"multi.value.map.com.liferay.portal.convert." +
			"ConvertPermissionAlgorithm.convertResourcePermission",
		"multi.value.map.com.liferay.portal.convert." +
			"ConvertPermissionAlgorithm.convertRoles",
		"net.sf.ehcache.configurationResourceName",
		"net.sf.ehcache.configurationResourceName.peerProviderProperties",
		"openoffice.server.enabled", "openoffice.server.host",
		"openoffice.server.port", "openoffice.cache.enabled",
		"organizations.children.types", "organizations.country.enabled",
		"organizations.country.required",
		"organizations.form.add.identification", "organizations.form.add.main",
		"organizations.form.add.miscellaneous",
		"organizations.form.update.identification",
		"organizations.form.update.main",
		"organizations.form.update.miscellaneous",
		"organizations.indexer.enabled", "organizations.rootable",
		"organizations.types", "permissions.object.blocking.cache",
		"poller.notifications.timeout", "poller.request.timeout",
		"pop.server.notifications.enabled", "pop.server.subdomain",
		"portal.cache.manager.type.multi.vm",
		"portal.cache.manager.type.single.vm", "portal.ctx",
		"portal.fabric.enabled", "portal.fabric.agent.selector.class",
		"portal.fabric.server.host", "portal.fabric.server.port",
		"portal.fabric.server.boss.group.thread.count",
		"portal.fabric.server.worker.group.thread.count",
		"portal.fabric.server.worker.startup.timeout",
		"portal.fabric.server.file.server.folder.compression.level",
		"portal.fabric.server.file.server.group.thread.count",
		"portal.fabric.server.registeration.group.thread.count",
		"portal.fabric.server.repository.parent.folder",
		"portal.fabric.server.repository.get.file.timeout",
		"portal.fabric.server.rpc.group.thread.count",
		"portal.fabric.server.rpc.relay.timeout",
		"portal.fabric.server.warmup.agent.on.register",
		"portal.fabric.shutdown.quiet.period", "portal.fabric.shutdown.timeout",
		"portal.jaas.impl", "portal.jaas.strict.password",
		"portal.resiliency.enabled", "portal.resiliency.portlet.show.footer",
		"portal.resiliency.spi.agent.client.pool.max.size",
		"portal.security.manager.enable",
		"portlet.url.generate.by.path.enabled",
		"permissions.inline.sql.resource.block.query.threshold",
		"permissions.list.filter", "permissions.thread.local.cache.max.size",
		"permissions.user.check.algorithm", "persistence.provider",
		"plugin.notifications.enabled", "plugin.notifications.packages.ignored",
		"plugin.repositories.trusted", "plugin.repositories.untrusted",
		"plugin.types", "pop.server.notifications.interval",
		"ratings.max.score", "ratings.min.score",
		"redirect.url.domains.allowed", "redirect.url.ips.allowed",
		"redirect.url.security.mode", "rss.publish.to.live.by.default",
		"rtl.css.excluded.paths.regexp", "sandbox.deploy.dir",
		"sandbox.deploy.enabled", "sandbox.deploy.interval",
		"sandbox.deploy.listeners", "sc.image.max.size",
		"sc.image.thumbnail.max.height", "sc.image.thumbnail.max.width",
		"sc.product.comments.enabled", "scheduler.classes",
		"scheduler.event.message.listener.lock.timeout", "schema.run.minimal",
		"scripting.jruby.compile.mode", "scripting.jruby.compile.threshold",
		"search.container.page.iterator.page.values",
		"service.builder.service.read.only.prefixes", "session.disabled",
		"setup.database.types", "shard.available.names", "shard.default.name",
		"shard.selector", "siteminder.auth.enabled",
		"siteminder.import.from.ldap", "siteminder.user.header",
		"sites.form.add.advanced", "sites.form.add.main",
		"sites.form.add.miscellaneous", "sites.form.add.seo",
		"sites.form.update.advanced", "sites.form.update.main",
		"sites.form.update.miscellaneous", "sites.form.update.seo",
		"spring.infrastructure.configs",
		"sql.data.com.liferay.portal.kernel.model.Country.country.id",
		"sql.data.com.liferay.portal.kernel.model.ListType.account.address",
		"sql.data.com.liferay.portal.kernel.model.ListType.account.email." +
			"address",
		"sql.data.com.liferay.portal.kernel.model.ListType.company.address",
		"sql.data.com.liferay.portal.kernel.model.ListType.company.email." +
			"address",
		"sql.data.com.liferay.portal.kernel.model.ListType.contact.email." +
			"address",
		"sql.data.com.liferay.portal.kernel.model.ListType.organization.status",
		"sql.data.com.liferay.portal.kernel.model.Region.region.id",
		"staging.lock.enabled", "social.activity.sets.bundling.enabled",
		"social.activity.sets.enabled", "social.bookmark.display.styles",
		"social.bookmark.types", "spring.hibernate.data.source",
		"spring.hibernate.configuration.proxy.factory.preload.classloader." +
			"classes",
		"spring.hibernate.session.factory", "spring.portlet.configs",
		"spring.remoting.servlet.hosts.allowed",
		"spring.remoting.servlet.https.required", "sprite.enabled",
		"sprite.file.name", "sprite.properties.file.name", "sprite.root.dir",
		"staging.delete.temp.lar.on.failure",
		"staging.delete.temp.lar.on.success",
		"struts.portlet.ignored.parameters.regexp",
		"struts.portlet.request.processor",
		"table.mapper.cache.mapping.table.names", "tck.url",
		"transaction.manager.impl", "transaction.isolation.counter",
		"user.groups.copy.layouts.to.user.personal.site",
		"user.groups.indexer.enabled", "users.form.add.identification",
		"users.indexer.enabled", "users.form.add.main",
		"users.form.add.miscellaneous", "users.form.my.account.identification",
		"users.form.my.account.main", "users.form.my.account.miscellaneous",
		"users.form.update.identification", "users.form.update.main",
		"users.form.update.miscellaneous", "users.image.check.token",
		"users.image.default.use.initials", "users.image.max.height",
		"users.image.max.size", "users.image.max.width", "users.list.views",
		"vaadin.resources.path", "vaadin.theme", "vaadin.widgetset",
		"value.object.entity.blocking.cache",
		"value.object.finder.blocking.cache", "verify.database.transactions",
		"verify.frequency", "verify.patch.levels.disabled", "verify.processes",
		"verify.process.concurrency.threshold", "webdav.storage.class",
		"webdav.storage.show.edit.url", "webdav.storage.show.view.url",
		"webdav.storage.tokens", "wiki.email.page.added.signature",
		"wiki.email.page.updated.signature", "work.dir.override.enabled",
		"xml.sitemap.index.enabled", "xsl.template.secure.processing.enabled",
		"xss.allow", "xuggler.enabled", "xuggler.jar.file", "xuggler.jar.url",
		"xuggler.jar.options", "xuggler.ffpreset.8x8dct", "xuggler.ffpreset.bf",
		"xuggler.f.ffpreset.cmp", "xuggler.f.ffpreset.coder",
		"xuggler.f.ffpreset.flags", "xuggler.f.ffpreset.flags2",
		"xuggler.f.ffpreset.i_qfactor", "xuggler.f.ffpreset.mbtree",
		"xuggler.f.ffpreset.me_method", "xuggler.f.ffpreset.me_range",
		"xuggler.f.ffpreset.qcomp", "xuggler.f.ffpreset.qdiff",
		"xuggler.f.ffpreset.qmin", "xuggler.f.ffpreset.qmax",
		"xuggler.f.ffpreset.sc_threshold", "xuggler.f.ffpreset.subq",
		"xuggler.f.ffpreset.trellis", "xuggler.f.ffpreset.wpredp", "ym.login",
		"ym.password", "zip.file.name.encoding"
	};

	private static final String[] _OBSOLETE_SYSTEM_KEYS = {
		"com.liferay.petra.memory.FinalizeManager.thread.enabled",
		"com.liferay.portal.kernel.memory.FinalizeManager.thread.enabled",
		"com.liferay.portal.kernel.util.ServiceProxyFactory.timeout",
		"com.liferay.util.axis.SimpleHTTPSender.regexp.pattern",
		"com.liferay.util.Http.proxy.host", "com.liferay.util.Http.proxy.port",
		"com.liferay.util.XSSUtil.regexp.pattern",
		"finalize.manager.thread.enabled"
	};

	private static final String[][] _RENAMED_PORTAL_KEYS = {
		{"amazon.license.0", "amazon.access.key.id"},
		{"amazon.license.1", "amazon.access.key.id"},
		{"amazon.license.2", "amazon.access.key.id"},
		{"amazon.license.3", "amazon.access.key.id"},
		{"buffered.increment.enabled", "view.count.enabled"},
		{"cdn.host", "cdn.host.http"},
		{"cluster.executor.debug.enabled", "cluster.link.debug.enabled"},
		{
			"com.liferay.portal.servlet.filters.compression.CompressionFilter",
			"com.liferay.portal.servlet.filters.gzip.GZipFilter"
		},
		{
			"com.liferay.portal.servlet.filters.urlrewrite.UrlRewriteFilter",
			"com.liferay.portal.url.rewrite.filter.internal.URLRewriteFilter"
		},
		{
			"com.liferay.portal.upload.LiferayFileItem.threshold.size",
			"com.liferay.portal.kernel.upload.FileItem.threshold.size"
		},
		{
			"default.guest.friendly.url",
			"default.guest.public.layout.friendly.url"
		},
		{"default.guest.layout.column", "default.guest.public.layout.column"},
		{"default.guest.layout.name", "default.guest.public.layout.name"},
		{
			"default.guest.layout.template.id",
			"default.guest.public.layout.template.id"
		},
		{"default.user.layout.column", "default.user.public.layout.column"},
		{"default.user.layout.name", "default.user.public.layout.name"},
		{
			"default.user.layout.template.id",
			"default.user.public.layout.template.id"
		},
		{"default.user.private.layout.lar", "default.user.private.layouts.lar"},
		{"default.user.public.layout.lar", "default.user.public.layouts.lar"},
		{"dl.hook.impl", "dl.store.impl"},
		{"dl.hook.s3.access.key", "dl.store.s3.access.key"},
		{"dl.hook.s3.bucket.name", "dl.store.s3.bucket.name"},
		{"dl.hook.s3.secret.key", "dl.store.s3.secret.key"},
		{
			"editor.wysiwyg.portal-web.docroot.html.portlet.calendar." +
				"edit_configuration.jsp",
			"editor.wysiwyg.portal-web.docroot.html.portlet.calendar." +
				"configuration.jsp"
		},
		{
			"editor.wysiwyg.portal-web.docroot.html.portlet.invitation." +
				"edit_configuration.jsp",
			"editor.wysiwyg.portal-web.docroot.html.portlet.invitation." +
				"configuration.jsp"
		},
		{
			"editor.wysiwyg.portal-web.docroot.html.portlet.journal." +
				"edit_configuration.jsp",
			"editor.wysiwyg.portal-web.docroot.html.portlet.journal." +
				"configuration.jsp"
		},
		{
			"editor.wysiwyg.portal-web.docroot.html.portlet.message_boards." +
				"edit_configuration.jsp",
			"editor.wysiwyg.portal-web.docroot.html.portlet.message_boards." +
				"configuration.jsp"
		},
		{
			"ehcache.cluster.link.replicator.properties",
			"ehcache.replicator.properties"
		},
		{
			"ehcache.cluster.link.replicator.properties.default",
			"ehcache.replicator.properties.default"
		},
		{
			"field.editable.com.liferay.portal.kernel.model.User.emailAddress",
			"field.editable.user.types"
		},
		{
			"field.editable.com.liferay.portal.kernel.model.User.screenName",
			"field.editable.user.types"
		},
		{"icon.menu.max.display.items", "menu.max.display.items"},
		{"journal.error.template.freemarker", "journal.error.template[ftl]"},
		{"journal.error.template.velocity", "journal.error.template[vm]"},
		{"journal.error.template.xsl", "journal.error.template[xsl]"},
		{
			"journal.template.velocity.restricted.variables",
			"velocity.engine.restricted.variables"
		},
		{
			"module.framework.properties.dependency.manager.sync.timeout",
			"dependency.manager.sync.timeout"
		},
		{
			"module.framework.properties.dependency.manager.thread.pool." +
				"enabled",
			"dependency.manager.thread.pool.enabled"
		},
		{
			"module.framework.properties.felix.fileinstall.bundles.new.start",
			"module.framework.file.install.bundles.start.new"
		},
		{
			"module.framework.properties.file.install.bundles.new.start",
			"module.framework.file.install.bundles.start.new"
		},
		{
			"module.framework.properties.felix.fileinstall.bundles." +
				"startActivationPolicy",
			"module.framework.file.install.bundles.use.start.activation.policy"
		},
		{
			"module.framework.properties.file.install.bundles." +
				"startActivationPolicy",
			"module.framework.file.install.bundles.use.start.activation.policy"
		},
		{
			"module.framework.properties.felix.fileinstall.bundles." +
				"startTransient",
			"module.framework.file.install.bundles.start.transient"
		},
		{
			"module.framework.properties.file.install.bundles.startTransient",
			"module.framework.file.install.bundles.start.transient"
		},
		{
			"module.framework.properties.felix.fileinstall.noInitialDelay",
			"module.framework.file.install.no.initial.delay"
		},
		{
			"module.framework.properties.file.install.noInitialDelay",
			"module.framework.file.install.no.initial.delay"
		},
		{
			"module.framework.properties.felix.fileinstall.subdir.mode",
			"module.framework.file.install.subdir.mode"
		},
		{
			"module.framework.properties.file.install.subdir.mode",
			"module.framework.file.install.subdir.mode"
		},
		{
			"module.framework.properties.initial.system.check.enabled",
			"initial.system.check.enabled"
		},
		{
			"passwords.passwordpolicytoolkit.charset.lowercase",
			"passwords.passwordpolicytoolkit.validator.charset.lowercase"
		},
		{
			"passwords.passwordpolicytoolkit.charset.numbers",
			"passwords.passwordpolicytoolkit.validator.charset.numbers"
		},
		{
			"passwords.passwordpolicytoolkit.charset.symbols",
			"passwords.passwordpolicytoolkit.validator.charset.symbols"
		},
		{
			"passwords.passwordpolicytoolkit.charset.uppercase",
			"passwords.passwordpolicytoolkit.validator.charset.uppercase"
		},
		{
			"permissions.inline.sql.resource.block.query.threshhold",
			"permissions.inline.sql.resource.block.query.threshold"
		},
		{"portal.instance.http.port", "portal.instance.http.socket.address"},
		{"portal.instance.https.port", "portal.instance.http.socket.address"},
		{"referer.url.domains.allowed", "redirect.url.domains.allowed"},
		{"referer.url.ips.allowed", "redirect.url.ips.allowed"},
		{"referer.url.security.mode", "redirect.url.security.mode"},
		{
			"tags.asset.increment.view.counter.enabled",
			"asset.entry.increment.view.counter.enabled"
		},
		{"sql.data.max.parameters", "database.max.parameters"},
		{
			"staging.groups.in.memory.filter.limit",
			"cacheable.query.limit.LPD-28122"
		},
		{
			"virtual.hosts.per.company.in.memory.filter.limit",
			"cacheable.query.limit.LPD-27353"
		}
	};

	private static final String[][] _RENAMED_SYSTEM_KEYS = {
		{
			"com.liferay.portal.kernel.util.StringBundler.unsafe.create." +
				"threshold",
			"com.liferay.portal.kernel.util.StringBundler.threadlocal.buffer." +
				"limit"
		}
	};

	private static final Log _log = LogFactoryUtil.getLog(
		PreupgradeVerifyProperties.class);

}