/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.k8s.agent.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osgi.util.StringPlus;
import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.k8s.agent.PortalK8sConfigMapModifier;
import com.liferay.portal.k8s.agent.configuration.PortalK8sAgentConfiguration;
import com.liferay.portal.k8s.agent.internal.test.util.KubernetesMockServerTestUtilProxy;
import com.liferay.portal.k8s.agent.mutator.PortalK8sConfigurationPropertiesMutator;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class AgentPortalK8sConfigMapModifierTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_bundle = FrameworkUtil.getBundle(
			AgentPortalK8sConfigMapModifierTest.class);

		_bundleContext = _bundle.getBundleContext();

		_kubernetesMockServerTestUtilProxy =
			new KubernetesMockServerTestUtilProxy(
				_portalK8sConfigurationPropertiesMutator.getClass());

		_kubernetesMockServerTestUtilProxy.start();

		_agentConfiguration = _configurationAdmin.getConfiguration(
			PortalK8sAgentConfiguration.class.getName(), StringPool.QUESTION);

		_serviceTracker = new ServiceTracker<>(
			_bundleContext, PortalK8sConfigMapModifier.class, null);

		_serviceTracker.open();

		ConfigurationTestUtil.saveConfiguration(
			_agentConfiguration,
			HashMapDictionaryBuilder.<String, Object>put(
				"apiServerHost",
				_kubernetesMockServerTestUtilProxy.getHostName()
			).put(
				"apiServerPort", _kubernetesMockServerTestUtilProxy.getPort()
			).put(
				"apiServerSSL", Boolean.FALSE
			).put(
				"caCertData",
				StringUtil.read(
					AgentPortalK8sConfigMapModifierTest.class,
					"dependencies/ca.crt")
			).put(
				"debounceDelayMillis", 10
			).put(
				"namespace", "test"
			).put(
				"saToken", "saToken"
			).build());

		_portalK8sConfigMapModifier = _serviceTracker.waitForService(2000);

		Assert.assertNotNull(_portalK8sConfigMapModifier);

		ServiceReference<PortalK8sConfigMapModifier> serviceReference =
			_serviceTracker.getServiceReference();

		Assert.assertNull(serviceReference.getProperty("service.ranking"));
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_agentConfiguration);

		_kubernetesMockServerTestUtilProxy.stop();

		_serviceTracker.close();
	}

	@Test
	public void testCreateDXPMetadata() throws Exception {
		String webId = "foo.lxc.com";

		Company company = _companyLocalService.addCompany(
			null, webId, webId, webId, 0, true, true, null, null, null, null,
			null, null);

		JSONObject configMapJSONObject = _waitForConfigMap(
			webId.concat("-lxc-dxp-metadata"),
			data ->
				(data != null) &&
				Objects.equals(webId, data.get("com.liferay.lxc.dxp.domains")),
			3, TimeUnit.MINUTES);

		JSONObject metadataJSONObject = configMapJSONObject.getJSONObject(
			"metadata");

		JSONObject labelsJSONObject = metadataJSONObject.getJSONObject(
			"labels");

		Assert.assertEquals(
			webId,
			labelsJSONObject.getString(
				"dxp.lxc.liferay.com/virtualInstanceId"));
		Assert.assertEquals(
			"dxp", labelsJSONObject.getString("lxc.liferay.com/metadataType"));

		try {
			UserTestUtil.setUser(TestPropsValues.getUser());

			_companyLocalService.deleteCompany(company);
		}
		finally {
			PrincipalThreadLocal.setName(null);
			PermissionThreadLocal.setPermissionChecker(null);
		}

		Assert.assertNull(
			_waitForConfigMap(
				webId.concat("-lxc-dxp-metadata"), Objects::isNull, 20,
				TimeUnit.SECONDS));
	}

	@Test
	public void testCreateExtInitMetadata() throws Exception {
		String serviceId = RandomTestUtil.randomString();

		String configMapName = StringBundler.concat(
			serviceId, StringPool.DASH, TestPropsValues.COMPANY_WEB_ID,
			"-lxc-ext-init-metadata");

		_portalK8sConfigMapModifier.modifyConfigMap(
			configMapModel -> {
				Map<String, String> data = configMapModel.data();

				data.put(
					"com.liferay.lxc.dxp.domains",
					TestPropsValues.COMPANY_WEB_ID);
				data.put(
					"com.liferay.lxc.dxp.main.domain",
					TestPropsValues.COMPANY_WEB_ID);
				data.put(
					"com.liferay.lxc.dxp.mainDomain",
					TestPropsValues.COMPANY_WEB_ID);

				Map<String, String> labels = configMapModel.labels();

				labels.put("lxc.liferay.com/metadataType", "ext-init");
				labels.put("ext.lxc.liferay.com/serviceId", serviceId);
				labels.put(
					"dxp.lxc.liferay.com/virtualInstanceId",
					TestPropsValues.COMPANY_WEB_ID);
			},
			configMapName);

		JSONObject configMapJSONObject = _waitForConfigMap(
			configMapName, Objects::nonNull, 20, TimeUnit.SECONDS);

		JSONObject dataJSONObject = configMapJSONObject.getJSONObject("data");

		Assert.assertEquals(
			TestPropsValues.COMPANY_WEB_ID,
			dataJSONObject.getString("com.liferay.lxc.dxp.domains"));
		Assert.assertEquals(
			TestPropsValues.COMPANY_WEB_ID,
			dataJSONObject.getString("com.liferay.lxc.dxp.main.domain"));

		JSONObject metadataJSONObject = configMapJSONObject.getJSONObject(
			"metadata");

		JSONObject labelsJSONObject = metadataJSONObject.getJSONObject(
			"labels");

		Assert.assertEquals(
			TestPropsValues.COMPANY_WEB_ID,
			labelsJSONObject.getString(
				"dxp.lxc.liferay.com/virtualInstanceId"));
		Assert.assertEquals(
			serviceId,
			labelsJSONObject.getString("ext.lxc.liferay.com/serviceId"));
		Assert.assertEquals(
			"ext-init",
			labelsJSONObject.getString("lxc.liferay.com/metadataType"));
	}

	@Test
	public void testFlushModifyConfigMapFast() throws Exception {
		String serviceId = RandomTestUtil.randomString();

		String configMapName = StringBundler.concat(
			serviceId, StringPool.DASH, TestPropsValues.COMPANY_WEB_ID,
			"-lxc-ext-init-metadata");

		PortalK8sConfigMapModifier.Result result =
			_portalK8sConfigMapModifier.modifyConfigMap(
				configMapModel -> {
					Map<String, String> data = configMapModel.data();

					data.put(
						"com.liferay.lxc.dxp.domains",
						RandomTestUtil.randomString());
					data.put(
						"com.liferay.lxc.dxp.main.domain",
						RandomTestUtil.randomString());
					data.put(
						"com.liferay.lxc.dxp.mainDomain",
						RandomTestUtil.randomString());

					Map<String, String> labels = configMapModel.labels();

					labels.put("lxc.liferay.com/metadataType", "ext-init");
					labels.put("ext.lxc.liferay.com/serviceId", serviceId);
					labels.put(
						"dxp.lxc.liferay.com/virtualInstanceId",
						TestPropsValues.COMPANY_WEB_ID);
				},
				configMapName);

		Assert.assertEquals(PortalK8sConfigMapModifier.Result.BUFFERED, result);

		result = _portalK8sConfigMapModifier.modifyConfigMap(
			configMapModel -> {
				Map<String, String> data = configMapModel.data();

				data.put(
					"com.liferay.lxc.dxp.domains",
					TestPropsValues.COMPANY_WEB_ID);
				data.put(
					"com.liferay.lxc.dxp.main.domain",
					TestPropsValues.COMPANY_WEB_ID);
				data.put(
					"com.liferay.lxc.dxp.mainDomain",
					TestPropsValues.COMPANY_WEB_ID);

				Map<String, String> labels = configMapModel.labels();

				labels.put("lxc.liferay.com/metadataType", "ext-init");
				labels.put("ext.lxc.liferay.com/serviceId", serviceId);
				labels.put(
					"dxp.lxc.liferay.com/virtualInstanceId",
					TestPropsValues.COMPANY_WEB_ID);
			},
			configMapName);

		Assert.assertEquals(PortalK8sConfigMapModifier.Result.BUFFERED, result);

		JSONObject configMapJSONObject = _waitForConfigMap(
			configMapName, Objects::nonNull, 20, TimeUnit.SECONDS);

		JSONObject dataJSONObject = configMapJSONObject.getJSONObject("data");

		Assert.assertEquals(
			TestPropsValues.COMPANY_WEB_ID,
			dataJSONObject.getString("com.liferay.lxc.dxp.main.domain"));

		JSONObject metadataJSONObject = configMapJSONObject.getJSONObject(
			"metadata");

		Assert.assertEquals(1, metadataJSONObject.getLong("generation"));
	}

	@Test
	public void testFlushModifyConfigMapSlow() throws Exception {
		String serviceId = RandomTestUtil.randomString();
		String randomString = RandomTestUtil.randomString();

		String configMapName = StringBundler.concat(
			serviceId, StringPool.DASH, TestPropsValues.COMPANY_WEB_ID,
			"-lxc-ext-init-metadata");

		_portalK8sConfigMapModifier.modifyConfigMap(
			configMapModel -> {
				Map<String, String> data = configMapModel.data();

				data.put("com.liferay.lxc.dxp.domains", randomString);
				data.put("com.liferay.lxc.dxp.main.domain", randomString);
				data.put("com.liferay.lxc.dxp.mainDomain", randomString);

				Map<String, String> labels = configMapModel.labels();

				labels.put("lxc.liferay.com/metadataType", "ext-init");
				labels.put("ext.lxc.liferay.com/serviceId", serviceId);
				labels.put(
					"dxp.lxc.liferay.com/virtualInstanceId",
					TestPropsValues.COMPANY_WEB_ID);
			},
			configMapName);

		JSONObject configMapJSONObject = _waitForConfigMap(
			configMapName, Objects::nonNull, 20, TimeUnit.SECONDS);

		JSONObject dataJSONObject = configMapJSONObject.getJSONObject("data");

		Assert.assertEquals(
			randomString,
			dataJSONObject.getString("com.liferay.lxc.dxp.main.domain"));

		JSONObject metadataJSONObject = configMapJSONObject.getJSONObject(
			"metadata");

		Assert.assertEquals(1, metadataJSONObject.getLong("generation"));

		_portalK8sConfigMapModifier.modifyConfigMap(
			configMapModel -> {
				Map<String, String> data = configMapModel.data();

				data.put(
					"com.liferay.lxc.dxp.domains",
					TestPropsValues.COMPANY_WEB_ID);
				data.put(
					"com.liferay.lxc.dxp.main.domain",
					TestPropsValues.COMPANY_WEB_ID);
				data.put(
					"com.liferay.lxc.dxp.mainDomain",
					TestPropsValues.COMPANY_WEB_ID);

				Map<String, String> labels = configMapModel.labels();

				labels.put("lxc.liferay.com/metadataType", "ext-init");
				labels.put("ext.lxc.liferay.com/serviceId", serviceId);
				labels.put(
					"dxp.lxc.liferay.com/virtualInstanceId",
					TestPropsValues.COMPANY_WEB_ID);
			},
			configMapName);

		configMapJSONObject = _waitForConfigMap(
			configMapName,
			data ->
				(data != null) &&
				Objects.equals(
					TestPropsValues.COMPANY_WEB_ID,
					data.get("com.liferay.lxc.dxp.main.domain")),
			20, TimeUnit.SECONDS);

		metadataJSONObject = configMapJSONObject.getJSONObject("metadata");

		Assert.assertEquals(2, metadataJSONObject.getLong("generation"));
	}

	@Test
	public void testListenForExtProvisionMetadata() throws Exception {
		String serviceId = RandomTestUtil.randomString();

		String mainDomain = serviceId.concat("-extproject.lfr.sh");

		JSONObject configMapJSONObject = JSONUtil.put(
			"data",
			JSONUtil.put(
				"foo.client-extension-config.json",
				"{\"test.pid\": {\"test.key\": \"test.value\"}}")
		).put(
			"metadata",
			JSONUtil.put(
				"annotations",
				JSONUtil.put(
					"ext.lxc.liferay.com/domains",
					serviceId.concat("-extproject.lfr.sh")
				).put(
					"ext.lxc.liferay.com/mainDomain", mainDomain
				)
			).put(
				"labels",
				JSONUtil.put(
					"dxp.lxc.liferay.com/virtualInstanceId",
					TestPropsValues.COMPANY_WEB_ID
				).put(
					"ext.lxc.liferay.com/projectName",
					RandomTestUtil.randomString()
				).put(
					"ext.lxc.liferay.com/serviceId", serviceId
				).put(
					"lxc.liferay.com/metadataType", "ext-provision"
				)
			).put(
				"name",
				StringBundler.concat(
					serviceId, "-", TestPropsValues.COMPANY_WEB_ID,
					"-lxc-ext-provision-metadata")
			)
		);

		Configuration configuration = ConfigurationTestUtil.updateConfiguration(
			"test.pid", () -> _createOrReplaceConfigMap(configMapJSONObject));

		Assert.assertNotNull(configuration);

		try {
			_assertNotInDatabase(configuration.getPid());

			Dictionary<String, Object> properties =
				configuration.getProcessedProperties(null);

			Assert.assertEquals(
				Http.HTTPS_WITH_SLASH.concat(mainDomain),
				properties.get("baseURL"));
			Assert.assertEquals(
				TestPropsValues.getCompanyId(),
				(long)properties.get("companyId"));
			Assert.assertEquals("test.value", properties.get("test.key"));
		}
		finally {
			ConfigurationTestUtil.deleteConfiguration(configuration);
		}
	}

	@Test
	public void testListenForExtProvisionMetadataWithFactoryPid()
		throws Exception {

		String serviceId = RandomTestUtil.randomString();

		String mainDomain = serviceId.concat("-extproject.lfr.sh");

		JSONObject configMapJSONObject = JSONUtil.put(
			"data",
			JSONUtil.put(
				"foo.client-extension-config.json",
				"{\"test.pid~foo\": {\"test.key\": \"test.value\"}}")
		).put(
			"metadata",
			JSONUtil.put(
				"annotations",
				JSONUtil.put(
					"ext.lxc.liferay.com/domains",
					serviceId.concat("-extproject.lfr.sh")
				).put(
					"ext.lxc.liferay.com/mainDomain", mainDomain
				)
			).put(
				"labels",
				JSONUtil.put(
					"dxp.lxc.liferay.com/virtualInstanceId",
					TestPropsValues.COMPANY_WEB_ID
				).put(
					"ext.lxc.liferay.com/projectName",
					RandomTestUtil.randomString()
				).put(
					"ext.lxc.liferay.com/serviceId", serviceId
				).put(
					"lxc.liferay.com/metadataType", "ext-provision"
				)
			).put(
				"name",
				StringBundler.concat(
					serviceId, "-", TestPropsValues.COMPANY_WEB_ID,
					"-lxc-ext-provision-metadata")
			)
		);

		Configuration configuration =
			ConfigurationTestUtil.updateFactoryConfiguration(
				"test.pid~foo/" + TestPropsValues.COMPANY_WEB_ID,
				() -> _createOrReplaceConfigMap(configMapJSONObject));

		Assert.assertNotNull(configuration);

		try {
			_assertNotInDatabase(configuration.getPid());

			Dictionary<String, Object> properties =
				configuration.getProcessedProperties(null);

			Assert.assertEquals(
				Http.HTTPS_WITH_SLASH.concat(mainDomain),
				properties.get("baseURL"));
			Assert.assertEquals(
				TestPropsValues.getCompanyId(),
				(long)properties.get("companyId"));
			Assert.assertEquals("test.value", properties.get("test.key"));

			String factoryPid = (String)properties.get("service.factoryPid");

			List<String> servicePids = StringPlus.asList(
				properties.get(Constants.SERVICE_PID));

			Assert.assertEquals(
				"foo",
				ConfigurationFactoryUtil.getExternalReferenceCode(
					factoryPid, servicePids));
		}
		finally {
			ConfigurationTestUtil.deleteConfiguration(configuration);
		}
	}

	@Test
	public void testModifyConfigMap() throws Exception {
		try {
			_portalK8sConfigMapModifier.modifyConfigMap(
				configMapModel -> {
				},
				null);

			Assert.fail();
		}
		catch (NullPointerException nullPointerException) {
			Assert.assertNotNull(nullPointerException);
		}

		try {
			_portalK8sConfigMapModifier.modifyConfigMap(null, null);

			Assert.fail();
		}
		catch (NullPointerException nullPointerException) {
			Assert.assertNotNull(nullPointerException);
		}

		PortalK8sConfigMapModifier.Result result =
			_portalK8sConfigMapModifier.modifyConfigMap(
				configMapModel -> {
				},
				StringBundler.concat(
					RandomTestUtil.randomString(), StringPool.DASH,
					TestPropsValues.COMPANY_WEB_ID, "-lxc-ext-init-metadata"));

		Assert.assertEquals(PortalK8sConfigMapModifier.Result.BUFFERED, result);
	}

	private void _assertNotInDatabase(String pid) throws Exception {
		try (Connection connection = _dataSource.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select configurationId, dictionary from Configuration_ " +
					"where configurationId = ?")) {

			preparedStatement.setString(1, pid);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				boolean stored = resultSet.next();

				Assert.assertFalse(stored);
			}
		}
	}

	private void _createOrReplaceConfigMap(JSONObject configMapJSONObject) {
		_kubernetesMockServerTestUtilProxy.createOrReplaceConfigMap(
			configMapJSONObject.toString());
	}

	private JSONObject _waitForConfigMap(
			String name, Predicate<Map<String, String>> dataPredicate,
			long amount, TimeUnit timeUnit)
		throws Exception {

		String json = _kubernetesMockServerTestUtilProxy.waitForConfigMap(
			name, dataPredicate, amount, timeUnit);

		if (json == null) {
			return null;
		}

		return JSONFactoryUtil.createJSONObject(json);
	}

	private static Configuration _agentConfiguration;
	private static Bundle _bundle;
	private static BundleContext _bundleContext;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	private static KubernetesMockServerTestUtilProxy
		_kubernetesMockServerTestUtilProxy;
	private static PortalK8sConfigMapModifier _portalK8sConfigMapModifier;

	@Inject
	private static PortalK8sConfigurationPropertiesMutator
		_portalK8sConfigurationPropertiesMutator;

	private static ServiceTracker
		<PortalK8sConfigMapModifier, PortalK8sConfigMapModifier>
			_serviceTracker;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DataSource _dataSource;

}