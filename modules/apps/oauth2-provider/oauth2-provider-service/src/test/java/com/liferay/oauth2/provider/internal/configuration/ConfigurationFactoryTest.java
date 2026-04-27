/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.configuration;

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.portal.k8s.agent.PortalK8sConfigMapModifier;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.AdditionalMatchers;
import org.mockito.Mockito;

/**
 * @author Raymond Augé
 */
public class ConfigurationFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_companyId = RandomTestUtil.randomLong();
		_externalReferenceCode = RandomTestUtil.randomString();
		_projectId = RandomTestUtil.randomString();
		_projectName = RandomTestUtil.randomString();
		_projectUid = RandomTestUtil.randomString();
		_serviceId = RandomTestUtil.randomString();
		_serviceUid = RandomTestUtil.randomString();
		_webId = RandomTestUtil.randomString();

		_company = Mockito.mock(Company.class);

		Mockito.when(
			_company.getCompanyId()
		).thenReturn(
			_companyId
		);

		Mockito.when(
			_company.getVirtualHostname()
		).thenReturn(
			_webId
		);

		Mockito.when(
			_company.getWebId()
		).thenReturn(
			_webId
		);

		_companyLocalService = Mockito.mock(CompanyLocalService.class);

		Mockito.when(
			_companyLocalService.getCompanyById(_companyId)
		).thenReturn(
			_company
		);

		_oAuth2Application = Mockito.mock(OAuth2Application.class);

		Mockito.when(
			_oAuth2Application.getClientId()
		).thenReturn(
			"ClientId"
		);

		Mockito.when(
			_oAuth2Application.getOAuth2ApplicationId()
		).thenReturn(
			1L
		);

		Mockito.when(
			_oAuth2Application.getUserId()
		).thenReturn(
			1L
		);

		Mockito.when(
			_oAuth2Application.getUserName()
		).thenReturn(
			"UserName"
		);

		_oAuth2ApplicationLocalService = Mockito.mock(
			OAuth2ApplicationLocalService.class);

		Mockito.when(
			_oAuth2ApplicationLocalService.addOrUpdateOAuth2Application(
				Mockito.anyString(), Mockito.anyLong(), Mockito.anyString(),
				Mockito.anyList(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyString(), Mockito.anyInt(),
				AdditionalMatchers.or(Mockito.anyString(), Mockito.isNull()),
				Mockito.anyString(), Mockito.anyList(), Mockito.anyString(),
				Mockito.anyLong(),
				AdditionalMatchers.or(Mockito.anyString(), Mockito.isNull()),
				Mockito.anyString(),
				AdditionalMatchers.or(Mockito.anyString(), Mockito.isNull()),
				Mockito.anyList(), Mockito.anyBoolean(), Mockito.anyBoolean(),
				AdditionalMatchers.or(
					Mockito.any(Consumer.class), Mockito.isNull()),
				Mockito.any())
		).thenReturn(
			_oAuth2Application
		);

		Mockito.when(
			_oAuth2ApplicationLocalService.
				fetchOAuth2ApplicationByExternalReferenceCode(
					_externalReferenceCode, _companyId)
		).thenReturn(
			_oAuth2Application
		);

		Mockito.when(
			_oAuth2ApplicationLocalService.updateScopeAliases(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
				AdditionalMatchers.or(Mockito.any(), Mockito.isNull()))
		).thenReturn(
			_oAuth2Application
		);

		_oAuth2ApplicationScopeAliasesLocalService = Mockito.mock(
			OAuth2ApplicationScopeAliasesLocalService.class);

		Mockito.when(
			_oAuth2ApplicationScopeAliasesLocalService.
				fetchOAuth2ApplicationScopeAliases(
					Mockito.anyLong(), Mockito.anyList())
		).thenReturn(
			null
		);

		PortalK8sConfigMapModifier portalK8sConfigMapModifier = Mockito.mock(
			PortalK8sConfigMapModifier.class);

		Map<String, String> annotations = new TreeMap<>();
		Map<String, String> binaryData = new TreeMap<>();
		Map<String, String> data = new TreeMap<>();
		_labels = new TreeMap<>();

		Mockito.when(
			portalK8sConfigMapModifier.modifyConfigMap(
				Mockito.any(), Mockito.anyString())
		).then(
			answer -> {
				Consumer<PortalK8sConfigMapModifier.ConfigMapModel> consumer =
					answer.getArgument(0);

				consumer.accept(
					new PortalK8sConfigMapModifier.ConfigMapModel() {

						@Override
						public Map<String, String> annotations() {
							return annotations;
						}

						@Override
						public Map<String, String> binaryData() {
							return binaryData;
						}

						@Override
						public Map<String, String> data() {
							return data;
						}

						@Override
						public Map<String, String> labels() {
							return _labels;
						}

					});

				return PortalK8sConfigMapModifier.Result.CREATED;
			}
		);

		_scopeLocator = Mockito.mock(ScopeLocator.class);

		Mockito.when(
			_scopeLocator.getLiferayOAuth2Scopes(_companyId)
		).thenReturn(
			new ArrayList<>()
		);

		Mockito.when(
			_scopeLocator.getScopeAliases(_companyId)
		).thenReturn(
			new ArrayList<>()
		);

		_snapshot = Mockito.mock(Snapshot.class);

		Mockito.when(
			_snapshot.get()
		).thenReturn(
			portalK8sConfigMapModifier
		);

		_user = Mockito.mock(User.class);

		Mockito.when(
			_user.getScreenName()
		).thenReturn(
			"ScreenName"
		);

		Mockito.when(
			_user.getUserId()
		).thenReturn(
			1L
		);

		_userLocalService = Mockito.mock(UserLocalService.class);

		Mockito.when(
			_userLocalService.getGuestUser(_companyId)
		).thenReturn(
			_user
		);

		Mockito.when(
			_userLocalService.getUserById(Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			_user
		);

		Mockito.when(
			_userLocalService.getUserByScreenName(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			_user
		);

		Runnable syncCallableFuture = ReflectionTestUtil.getFieldValue(
			DependencyManagerSyncUtil.class,
			"_syncCallableDefaultNoticeableFuture");

		syncCallableFuture.run();
	}

	@Test
	public void testOAuth2ProviderApplicationHeadlessServerConfigurationFactory()
		throws Exception {

		OAuth2ProviderApplicationHeadlessServerConfigurationFactory oa2pahscf =
			new OAuth2ProviderApplicationHeadlessServerConfigurationFactory();

		oa2pahscf.companyLocalService = _companyLocalService;
		oa2pahscf.oAuth2ApplicationLocalService =
			_oAuth2ApplicationLocalService;
		oa2pahscf.oAuth2ApplicationScopeAliasesLocalService =
			_oAuth2ApplicationScopeAliasesLocalService;
		oa2pahscf.scopeLocator = _scopeLocator;
		oa2pahscf.userLocalService = _userLocalService;

		ReflectionTestUtil.setFieldValue(
			oa2pahscf, "_scopeLocator", _scopeLocator);

		ReflectionTestUtil.setFieldValue(
			oa2pahscf, "_portalK8sConfigMapModifierSnapshot", _snapshot);

		oa2pahscf.activate(
			HashMapBuilder.<String, Object>put(
				"baseURL", "http://localhost"
			).put(
				"companyId", _companyId
			).put(
				"dxp.lxc.liferay.com/virtualInstanceId", _webId
			).put(
				"ext.lxc.liferay.com/projectId", _projectId
			).put(
				"ext.lxc.liferay.com/projectName", _projectName
			).put(
				"ext.lxc.liferay.com/projectUid", _projectUid
			).put(
				"ext.lxc.liferay.com/serviceId", _serviceId
			).put(
				"ext.lxc.liferay.com/serviceUid", _serviceUid
			).put(
				"lxc.liferay.com/metadataType", "ext-init"
			).put(
				"projectName", _projectName
			).put(
				"service.factoryPid", "foo"
			).put(
				"service.pid", "foo~" + _externalReferenceCode
			).build());

		Assert.assertEquals(
			_webId, _labels.get("dxp.lxc.liferay.com/virtualInstanceId"));
		Assert.assertEquals(
			_projectId, _labels.get("ext.lxc.liferay.com/projectId"));
		Assert.assertEquals(
			_projectName, _labels.get("ext.lxc.liferay.com/projectName"));
		Assert.assertEquals(
			_projectUid, _labels.get("ext.lxc.liferay.com/projectUid"));
		Assert.assertEquals(
			_serviceId, _labels.get("ext.lxc.liferay.com/serviceId"));
		Assert.assertEquals(
			_serviceUid, _labels.get("ext.lxc.liferay.com/serviceUid"));
		Assert.assertEquals(
			"ext-init", _labels.get("lxc.liferay.com/metadataType"));
	}

	@Test
	public void testOAuth2ProviderApplicationUserAgentConfigurationFactory()
		throws Exception {

		OAuth2ProviderApplicationUserAgentConfigurationFactory oa2pauacf =
			new OAuth2ProviderApplicationUserAgentConfigurationFactory();

		oa2pauacf.companyLocalService = _companyLocalService;
		oa2pauacf.oAuth2ApplicationLocalService =
			_oAuth2ApplicationLocalService;
		oa2pauacf.oAuth2ApplicationScopeAliasesLocalService =
			_oAuth2ApplicationScopeAliasesLocalService;
		oa2pauacf.scopeLocator = _scopeLocator;
		oa2pauacf.userLocalService = _userLocalService;

		ReflectionTestUtil.setFieldValue(
			oa2pauacf, "_portalK8sConfigMapModifierSnapshot", _snapshot);

		VirtualHostLocalService virtualHostLocalService = Mockito.mock(
			VirtualHostLocalService.class);

		Mockito.when(
			virtualHostLocalService.getVirtualHosts(_companyId)
		).thenReturn(
			new ArrayList<>()
		);

		ReflectionTestUtil.setFieldValue(
			oa2pauacf, "_virtualHostLocalService", virtualHostLocalService);

		oa2pauacf.activate(
			HashMapBuilder.<String, Object>put(
				"baseURL", "http://localhost"
			).put(
				"companyId", _companyId
			).put(
				"dxp.lxc.liferay.com/virtualInstanceId", _webId
			).put(
				"ext.lxc.liferay.com/projectId", _projectId
			).put(
				"ext.lxc.liferay.com/projectName", _projectName
			).put(
				"ext.lxc.liferay.com/projectUid", _projectUid
			).put(
				"ext.lxc.liferay.com/serviceId", _serviceId
			).put(
				"ext.lxc.liferay.com/serviceUid", _serviceUid
			).put(
				"lxc.liferay.com/metadataType", "ext-init"
			).put(
				"projectName", _projectName
			).put(
				"service.factoryPid", "foo"
			).put(
				"service.pid", "foo~" + _externalReferenceCode
			).build());

		Assert.assertEquals(
			_webId, _labels.get("dxp.lxc.liferay.com/virtualInstanceId"));
		Assert.assertEquals(
			_projectId, _labels.get("ext.lxc.liferay.com/projectId"));
		Assert.assertEquals(
			_projectName, _labels.get("ext.lxc.liferay.com/projectName"));
		Assert.assertEquals(
			_projectUid, _labels.get("ext.lxc.liferay.com/projectUid"));
		Assert.assertEquals(
			_serviceId, _labels.get("ext.lxc.liferay.com/serviceId"));
		Assert.assertEquals(
			_serviceUid, _labels.get("ext.lxc.liferay.com/serviceUid"));
		Assert.assertEquals(
			"ext-init", _labels.get("lxc.liferay.com/metadataType"));
	}

	private Company _company;
	private long _companyId;
	private CompanyLocalService _companyLocalService;
	private String _externalReferenceCode;
	private Map<String, String> _labels;
	private OAuth2Application _oAuth2Application;
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;
	private OAuth2ApplicationScopeAliasesLocalService
		_oAuth2ApplicationScopeAliasesLocalService;
	private String _projectId;
	private String _projectName;
	private String _projectUid;
	private ScopeLocator _scopeLocator;
	private String _serviceId;
	private String _serviceUid;
	private Snapshot<PortalK8sConfigMapModifier> _snapshot;
	private User _user;
	private UserLocalService _userLocalService;
	private String _webId;

}