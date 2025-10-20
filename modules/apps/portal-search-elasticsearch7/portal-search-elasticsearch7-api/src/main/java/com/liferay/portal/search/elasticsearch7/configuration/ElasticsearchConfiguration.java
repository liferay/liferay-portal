/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Michael C. Han
 */
@ExtendedObjectClassDefinition(category = "search")
@Meta.OCD(
	id = "com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration",
	localization = "content/Language",
	name = "elasticsearch7-configuration-name"
)
@ProviderType
public interface ElasticsearchConfiguration {

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #productionModeEnabled()}
	 */
	@Deprecated
	@Meta.AD(
		deflt = "EMBEDDED", description = "operation-mode-help",
		name = "operation-mode", required = false
	)
	public OperationMode operationMode();

	@Meta.AD(
		deflt = "false", description = "production-mode-enabled-help",
		name = "production-mode-enabled", required = false
	)
	public boolean productionModeEnabled();

	@Meta.AD(
		description = "remote-cluster-connection-id-help",
		name = "remote-cluster-connection-id", required = false
	)
	public String remoteClusterConnectionId();

	@Meta.AD(
		deflt = "http://localhost:9200",
		description = "network-host-addresses-help",
		name = "network-host-addresses", required = false
	)
	public String[] networkHostAddresses();

	@Meta.AD(
		deflt = "false", description = "authentication-enabled-help",
		name = "authentication-enabled", required = false
	)
	public boolean authenticationEnabled();

	@Meta.AD(
		deflt = "elastic", description = "username-help", name = "username",
		required = false
	)
	public String username();

	@Meta.AD(
		description = "password-help", name = "password", required = false,
		type = Meta.Type.Password
	)
	public String password();

	@Meta.AD(
		deflt = "false", description = "http-ssl-enabled-help",
		name = "http-ssl-enabled", required = false
	)
	public boolean httpSSLEnabled();

	@Meta.AD(
		deflt = "pkcs12", description = "truststore-type-help",
		name = "truststore-type", required = false
	)
	public String truststoreType();

	@Meta.AD(
		deflt = "/path/to/localhost.p12", description = "truststore-path-help",
		name = "truststore-path", required = false
	)
	public String truststorePath();

	@Meta.AD(
		description = "truststore-password-help", name = "truststore-password",
		required = false, type = Meta.Type.Password
	)
	public String truststorePassword();

	@Meta.AD(
		deflt = "liferay-", description = "index-name-prefix-help",
		name = "index-name-prefix", required = false
	)
	public String indexNamePrefix();

	@Meta.AD(
		deflt = "", description = "number-of-index-replicas-help",
		name = "number-of-index-replicas", required = false
	)
	public String indexNumberOfReplicas();

	@Meta.AD(
		deflt = "1", description = "number-of-index-shards-help",
		name = "number-of-index-shards", required = false
	)
	public String indexNumberOfShards();

	@Meta.AD(
		deflt = "10000", description = "index-max-result-window-help",
		name = "index-max-result-window", required = false
	)
	public int indexMaxResultWindow();

	@Meta.AD(
		description = "additional-index-configurations-help",
		name = "additional-index-configurations", required = false
	)
	public String additionalIndexConfigurations();

	@Meta.AD(
		description = "additional-type-mappings-help",
		name = "additional-type-mappings", required = false
	)
	public String additionalTypeMappings();

	@Meta.AD(
		description = "override-type-mappings-help",
		name = "override-type-mappings", required = false
	)
	public String overrideTypeMappings();

	@Meta.AD(
		deflt = "true", description = "log-exceptions-only-help",
		name = "log-exceptions-only", required = false
	)
	public boolean logExceptionsOnly();

	@Meta.AD(
		deflt = "0.0.0", description = "minimum-required-node-version-help",
		name = "minimum-required-node-version", required = false
	)
	public String minimumRequiredNodeVersion();

	@Meta.AD(
		deflt = "ERROR", description = "rest-client-logger-level-help",
		name = "rest-client-logger-level", required = false
	)
	public RESTClientLoggerLevel restClientLoggerLevel();

	@Meta.AD(
		deflt = "LiferayElasticsearchCluster",
		description = "cluster-name-help", name = "cluster-name",
		required = false
	)
	public String clusterName();

	@Meta.AD(
		description = "node-name-help", name = "node-name", required = false
	)
	public String nodeName();

	@Meta.AD(
		deflt = "false", description = "bootstrap-mlockall-help",
		name = "bootstrap-mlockall", required = false
	)
	public boolean bootstrapMlockAll();

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #sidecarHttpPort()}
	 */
	@Deprecated
	@Meta.AD(
		deflt = "9201", description = "embedded-http-port-help",
		name = "embedded-http-port", required = false
	)
	public int embeddedHttpPort();

	@Meta.AD(
		description = "sidecar-http-port-help", name = "sidecar-http-port",
		required = false
	)
	public String sidecarHttpPort();

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Meta.AD(
		deflt = "9300-9400",
		description = "discovery-zen-ping-unicast-hosts-port-help",
		name = "discovery-zen-ping-unicast-hosts-port", required = false
	)
	public String discoveryZenPingUnicastHostsPort();

	@Meta.AD(
		deflt = "", description = "network-host-help", name = "network-host",
		required = false
	)
	public String networkHost();

	@Meta.AD(
		deflt = "", description = "network-bind-host-help",
		name = "network-bind-host", required = false
	)
	public String networkBindHost();

	@Meta.AD(
		deflt = "", description = "network-publish-host-help",
		name = "network-publish-host", required = false
	)
	public String networkPublishHost();

	@Meta.AD(
		deflt = "true", description = "track-total-hits-help",
		name = "track-total-hits", required = false
	)
	public boolean trackTotalHits();

	@Meta.AD(
		deflt = "2147483647", description = "track-total-hits-limit-help",
		max = "2147483647", name = "track-total-hits-limit", required = false
	)
	public int trackTotalHitsLimit();

	@Meta.AD(
		deflt = "", description = "transport-tcp-port-help",
		name = "transport-tcp-port", required = false
	)
	public String transportTcpPort();

	@Meta.AD(
		description = "additional-configurations-help",
		name = "additional-configurations", required = false
	)
	public String additionalConfigurations();

	@Meta.AD(
		deflt = "true", description = "http-cors-enabled-help",
		name = "http-cors-enabled", required = false
	)
	public boolean httpCORSEnabled();

	@Meta.AD(
		deflt = "/https?:\\/\\/localhost(:[0-9]+)?/",
		description = "http-cors-allow-origin-help",
		name = "http-cors-allow-origin", required = false
	)
	public String httpCORSAllowOrigin();

	@Meta.AD(
		description = "http-cors-configurations-help",
		name = "http-cors-configurations", required = false
	)
	public String httpCORSConfigurations();

	@Meta.AD(
		deflt = "75", description = "max-connections-help",
		name = "max-connections", required = false
	)
	public int maxConnections();

	@Meta.AD(
		deflt = "75", description = "max-connections-per-route-help",
		name = "max-connections-per-route", required = false
	)
	public int maxConnectionsPerRoute();

	@Meta.AD(
		deflt = "false", description = "sidecar-debug-help",
		name = "sidecar-debug", required = false
	)
	public boolean sidecarDebug();

	@Meta.AD(
		deflt = "-agentlib:jdwp=transport=dt_socket,address=8001,server=y,suspend=y,quiet=y",
		description = "sidecar-debug-settings-help",
		name = "sidecar-debug-settings", required = false
	)
	public String sidecarDebugSettings();

	@Meta.AD(
		deflt = "10000", description = "sidecar-heartbeat-interval-help",
		name = "sidecar-heartbeat-interval", required = false
	)
	public long sidecarHeartbeatInterval();

	@Meta.AD(
		deflt = "elasticsearch-sidecar", description = "sidecar-home-help",
		name = "sidecar-home", required = false
	)
	public String sidecarHome();

	@Meta.AD(
		deflt = "aggregations|analysis-common|blob-cache|data-streams|dot-prefix-validation|health-shards-availability|ingest-attachment|ingest-common|ingest-user-agent|lang-expression|lang-mustache|lang-painless|mapper-extras|parent-join|percolator|rank-eval|reindex|rest-root|runtime-fields-common|transport-netty4",
		description = "sidecar-module-names-help",
		name = "sidecar-module-names", required = false
	)
	public String[] sidecarModuleNames();

	@Meta.AD(
		deflt = "-Xms1g|-Xmx1g|-XX:+AlwaysPreTouch",
		description = "sidecar-jvm-options-help", name = "sidecar-jvm-options",
		required = false
	)
	public String[] sidecarJVMOptions();

	@Meta.AD(
		deflt = "10000", description = "sidecar-shutdown-timeout-help",
		name = "sidecar-shutdown-timeout", required = false
	)
	public long sidecarShutdownTimeout();

	@Meta.AD(
		description = "set-the-proxy-host-to-be-used-for-the-client-connection",
		name = "proxy-host", required = false
	)
	public String proxyHost();

	@Meta.AD(
		deflt = "0",
		description = "set-the-proxy-port-to-be-used-for-the-client-connection",
		name = "proxy-port", required = false
	)
	public int proxyPort();

	@Meta.AD(
		description = "proxy-username-help", name = "proxy-username",
		required = false
	)
	public String proxyUserName();

	@Meta.AD(
		description = "set-the-password-for-connecting-to-the-proxy",
		name = "proxy-password", required = false, type = Meta.Type.Password
	)
	public String proxyPassword();

}