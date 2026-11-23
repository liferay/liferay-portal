/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.TomcatClusterTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.test.cluster.tomcat.TomcatCluster;
import com.liferay.portal.test.cluster.tomcat.TomcatNode;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.lang.reflect.Constructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jiefeng Wu
 */
@RunWith(Arquillian.class)
public class JGroupsClusterChannelTest implements Serializable {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@ClassRule
	public static final TomcatClusterTestRule tomcatClusterTestRule =
		new TomcatClusterTestRule();

	@Test
	public void testConnectWithJDBCPing() throws Exception {
		_recreateJGroupsPingTable();

		Path jdbcPingXMLPath = _createJDBCPingXMLPath(
			"clustering_jdbc_ping.xml");

		TomcatNode tomcatNode1 = _buildJDBCPingTomcatNode(
			false, jdbcPingXMLPath);

		tomcatNode1.start(true);

		_injectDataSourceIntoJDBCPing(tomcatNode1);

		TomcatNode tomcatNode2 = _buildJDBCPingTomcatNode(
			false, jdbcPingXMLPath);

		tomcatNode2.start(true);

		_injectDataSourceIntoJDBCPing(tomcatNode2);

		_assertJGroupsPingCount(2);

		ClusterNode clusterNode1 = tomcatNode1.syncExecute(
			ClusterExecutorUtil::getLocalClusterNode);

		Assert.assertNotNull(clusterNode1);

		ClusterNode clusterNode2 = tomcatNode2.syncExecute(
			ClusterExecutorUtil::getLocalClusterNode);

		Assert.assertNotNull(clusterNode2);

		Assert.assertTrue(
			tomcatNode1.syncExecute(
				() -> {
					List<ClusterNode> clusterNodes =
						ClusterExecutorUtil.getClusterNodes();

					return clusterNodes.contains(clusterNode2);
				}));

		Assert.assertTrue(
			tomcatNode2.syncExecute(
				() -> {
					List<ClusterNode> clusterNodes =
						ClusterExecutorUtil.getClusterNodes();

					return clusterNodes.contains(clusterNode1);
				}));

		_assertAuthenticatedUserId(tomcatNode2);
	}

	@Test
	public void testConnectWithJDBCPingZombieEntries() throws Exception {
		_recreateJGroupsPingTable();

		TomcatNode tomcatNode1 = _buildJDBCPingTomcatNode(
			true, _createJDBCPingXMLPath("clustering_jdbc_ping_zombie.xml"));

		tomcatNode1.start(true);

		_injectDataSourceIntoJDBCPing(tomcatNode1);

		List<String> clusterNames = new ArrayList<>();
		List<String> ownAddresses = new ArrayList<>();
		List<byte[]> pingData = new ArrayList<>();

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select own_addr, cluster_name, ping_data from JGROUPSPING");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				clusterNames.add(resultSet.getString("cluster_name"));
				ownAddresses.add(resultSet.getString("own_addr"));
				pingData.add(resultSet.getBytes("ping_data"));
			}
		}

		Assert.assertFalse(ownAddresses.isEmpty());

		tomcatNode1.stop();

		_recreateJGroupsPingTable();

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"insert into JGROUPSPING (own_addr, cluster_name, " +
						"ping_data) values (?, ?, ?)")) {

			for (int i = 0; i < ownAddresses.size(); i++) {
				preparedStatement.setString(1, ownAddresses.get(i));
				preparedStatement.setString(2, clusterNames.get(i));
				preparedStatement.setBytes(3, pingData.get(i));

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}

		_assertJGroupsPingCount(ownAddresses.size());

		TomcatNode tomcatNode2 = _buildJDBCPingTomcatNode(
			true, _createJDBCPingXMLPath("clustering_jdbc_ping.xml"));

		tomcatNode2.start(true);

		_injectDataSourceIntoJDBCPing(tomcatNode2);

		Assert.assertNotNull(
			tomcatNode2.syncExecute(ClusterExecutorUtil::getLocalClusterNode));

		_assertAuthenticatedUserId(tomcatNode2);
	}

	private void _assertAuthenticatedUserId(TomcatNode tomcatNode)
		throws Exception {

		User user = TestPropsValues.getUser();

		String emailAddress = user.getEmailAddress();

		Assert.assertEquals(
			user.getUserId(),
			(long)tomcatNode.syncExecute(
				() -> AuthenticatedSessionManagerUtil.getAuthenticatedUserId(
					new MockHttpServletRequest(), emailAddress,
					TestPropsValues.USER_PASSWORD, null)));
	}

	private void _assertJGroupsPingCount(int expectedCount) throws Exception {
		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) as countValue from JGROUPSPING");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());
			Assert.assertEquals(expectedCount, resultSet.getInt("countValue"));
		}
	}

	private TomcatNode _buildJDBCPingTomcatNode(
			boolean clusterLinkChannelTransport, Path jdbcPingXMLPath)
		throws Exception {

		TomcatCluster.Builder builder = tomcatClusterTestRule.buildTomcatNode();

		TomcatNode tomcatNode = builder.build();

		List<String> clusterLinkChannelProperties = new ArrayList<>();

		clusterLinkChannelProperties.add(
			PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_CONTROL + "=" +
				jdbcPingXMLPath.toAbsolutePath());

		if (clusterLinkChannelTransport) {
			clusterLinkChannelProperties.add(
				PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_TRANSPORT + ".0=" +
					jdbcPingXMLPath.toAbsolutePath());
		}

		Files.write(
			tomcatNode.getPortalExtPropertiesPath(),
			clusterLinkChannelProperties, StandardOpenOption.APPEND);

		return tomcatNode;
	}

	private Path _createJDBCPingXMLPath(String fileName) throws Exception {
		Path jdbcPingXMLPath = Files.createTempFile(
			"clustering_jdbc_ping_", ".xml");

		try (InputStream inputStream =
				JGroupsClusterChannelTest.class.getResourceAsStream(
					"dependencies/" + fileName)) {

			Files.copy(
				inputStream, jdbcPingXMLPath,
				StandardCopyOption.REPLACE_EXISTING);
		}

		File jdbcPingXMLFile = jdbcPingXMLPath.toFile();

		jdbcPingXMLFile.deleteOnExit();

		return jdbcPingXMLPath;
	}

	private void _injectDataSourceIntoJDBCPing(TomcatNode tomcatNode)
		throws Exception {

		Assert.assertTrue(
			tomcatNode.syncExecute(
				() -> {
					Class<?> driverClass = Class.forName(
						PropsUtil.get("jdbc.default.driverClassName"), true,
						PortalClassLoaderUtil.getClassLoader());

					Constructor<?> constructor = driverClass.getConstructor();

					Driver driver = (Driver)constructor.newInstance();

					Properties properties = new Properties();

					properties.setProperty(
						"password", PropsUtil.get("jdbc.default.password"));
					properties.setProperty(
						"user", PropsUtil.get("jdbc.default.username"));

					String url = PropsUtil.get("jdbc.default.url");

					DataSource dataSource =
						(DataSource)ProxyUtil.newProxyInstance(
							DataSource.class.getClassLoader(),
							new Class<?>[] {DataSource.class},
							(proxy, method, arguments) -> {
								if (Objects.equals(
										method.getName(), "getConnection")) {

									return driver.connect(url, properties);
								}

								throw new UnsupportedOperationException(
									method.getName());
							});

					return SystemBundleUtil.callService(
						ClusterExecutor.class,
						clusterExecutor -> {
							Object clusterChannel =
								ReflectionTestUtil.getFieldValue(
									clusterExecutor, "_clusterChannel");

							Object jChannel = ReflectionTestUtil.getFieldValue(
								clusterChannel, "_jChannel");

							Object protocolStack = ReflectionTestUtil.invoke(
								jChannel, "getProtocolStack", new Class<?>[0]);

							List<?> protocols = ReflectionTestUtil.invoke(
								protocolStack, "getProtocols", new Class<?>[0]);

							for (Object protocol : protocols) {
								Class<?> clazz = protocol.getClass();

								if (!Objects.equals(
										clazz.getSimpleName(), "JDBC_PING")) {

									continue;
								}

								ReflectionTestUtil.invoke(
									protocol, "setDataSource",
									new Class<?>[] {DataSource.class},
									dataSource);

								return true;
							}

							return false;
						});
				}));
	}

	private void _recreateJGroupsPingTable() throws Exception {
		DB db = DBManagerUtil.getDB();

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			if (dbInspector.hasTable("JGROUPSPING")) {
				db.runSQL("drop table JGROUPSPING");
			}
		}

		db.runSQL(
			StringBundler.concat(
				"create table JGROUPSPING (own_addr VARCHAR(200) not null, ",
				"cluster_name VARCHAR(200) not null, ping_data SBLOB null, ",
				"primary key (own_addr, cluster_name))"));
	}

}