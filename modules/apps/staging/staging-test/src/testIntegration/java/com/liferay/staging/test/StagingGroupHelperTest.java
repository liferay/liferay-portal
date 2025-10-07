/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.instance.lifecycle.PortalInstanceLifecycleManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.GroupUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.staging.StagingGroupHelper;

import java.util.function.Supplier;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

/**
 * @author Akos Thurzo
 */
@RunWith(Arquillian.class)
@Sync(cleanTransaction = true)
public class StagingGroupHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_addLocalStagingGroups();

		_addRemoteStagingGroups();

		_localLiveScopeGroup = _addScopeGroup(_localLiveGroup);
		_localStagingScopeGroup = _addScopeGroup(_localStagingGroup);
		_remoteLiveScopeGroup = _addScopeGroup(_remoteLiveGroup);
		_remoteStagingScopeGroup = _addScopeGroup(_remoteStagingGroup);

		_regularGroup = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		try {
			GroupLocalServiceUtil.deleteGroup(_localLiveGroup.getGroupId());
		}
		catch (NoSuchGroupException noSuchGroupException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchGroupException);
			}
		}

		try {
			GroupLocalServiceUtil.deleteGroup(_localStagingGroup.getGroupId());
		}
		catch (NoSuchGroupException noSuchGroupException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchGroupException);
			}
		}

		try {
			GroupLocalServiceUtil.deleteGroup(_regularGroup.getGroupId());
		}
		catch (NoSuchGroupException noSuchGroupException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchGroupException);
			}
		}

		try {
			GroupLocalServiceUtil.deleteGroup(_remoteLiveGroup.getGroupId());
		}
		catch (NoSuchGroupException noSuchGroupException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchGroupException);
			}
		}

		try {
			GroupLocalServiceUtil.deleteGroup(_remoteStagingGroup.getGroupId());
		}
		catch (NoSuchGroupException noSuchGroupException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchGroupException);
			}
		}
	}

	@FeatureFlag("LPD-35914")
	@Test
	public void testFetchCompanyGroup() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-35914");

		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		_groupLocalService.deleteGroup(group);

		Bundle bundle = FrameworkUtil.getBundle(StagingGroupHelperTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ComponentDescriptionDTO componentDescriptionDTO1 =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				FrameworkUtil.getBundle(
					_addCompanyGroupPortalInstanceLifecycleListener.getClass()),
				ClassUtil.getClassName(
					_addCompanyGroupPortalInstanceLifecycleListener));

		Promise<Void> promise = _serviceComponentRuntime.disableComponent(
			componentDescriptionDTO1);

		promise.getValue();

		ServiceReference<PortalInstanceLifecycleManager> serviceReference =
			bundleContext.getServiceReference(
				PortalInstanceLifecycleManager.class);

		ComponentDescriptionDTO componentDescriptionDTO2 =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				serviceReference.getBundle(),
				"com.liferay.portal.instance.lifecycle.internal." +
					"PortalInstanceLifecycleListenerManagerImpl");

		promise = _serviceComponentRuntime.disableComponent(
			componentDescriptionDTO2);

		promise.getValue();

		Assert.assertNull(
			_stagingGroupHelper.fetchCompanyGroup(
				TestPropsValues.getCompanyId()));

		promise = _serviceComponentRuntime.enableComponent(
			componentDescriptionDTO1);

		promise.getValue();

		promise = _serviceComponentRuntime.enableComponent(
			componentDescriptionDTO2);

		promise.getValue();

		serviceReference = bundleContext.getServiceReference(
			PortalInstanceLifecycleManager.class);

		PortalInstanceLifecycleManager portalInstanceLifecycleManager =
			bundleContext.getService(serviceReference);

		portalInstanceLifecycleManager.registerCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-35914");

		Assert.assertNotNull(
			_stagingGroupHelper.fetchCompanyGroup(
				TestPropsValues.getCompanyId()));
	}

	@Test
	public void testFetchLiveGroup() throws Exception {
		Assert.assertEquals(
			_localLiveGroup,
			_stagingGroupHelper.fetchLiveGroup(_localStagingGroup));
		Assert.assertEquals(
			_localLiveGroup,
			_stagingGroupHelper.fetchLiveGroup(_localStagingScopeGroup));

		Assert.assertNull(_stagingGroupHelper.fetchLiveGroup(_localLiveGroup));
		Assert.assertNull(
			_stagingGroupHelper.fetchLiveGroup(_localLiveScopeGroup));

		Assert.assertEquals(
			_remoteLiveGroup,
			_executeWithRemoteCredentials(
				() -> _stagingGroupHelper.fetchLiveGroup(_remoteStagingGroup)));
		Assert.assertEquals(
			_remoteLiveGroup,
			_executeWithRemoteCredentials(
				() -> _stagingGroupHelper.fetchLiveGroup(
					_remoteStagingScopeGroup)));

		Assert.assertNull(
			_executeWithRemoteCredentials(
				() -> _stagingGroupHelper.fetchLiveGroup(_remoteLiveGroup)));
		Assert.assertNull(
			_executeWithRemoteCredentials(
				() -> _stagingGroupHelper.fetchLiveGroup(
					_remoteLiveScopeGroup)));

		Assert.assertNull(_stagingGroupHelper.fetchLiveGroup(_regularGroup));
	}

	@Test
	public void testFetchLocalLiveGroup() throws Exception {
		Assert.assertEquals(
			_localLiveGroup,
			_stagingGroupHelper.fetchLocalLiveGroup(_localStagingGroup));
		Assert.assertEquals(
			_localLiveGroup,
			_stagingGroupHelper.fetchLocalLiveGroup(_localStagingScopeGroup));

		Assert.assertNull(
			_stagingGroupHelper.fetchLocalLiveGroup(_localLiveGroup));
		Assert.assertNull(
			_stagingGroupHelper.fetchLocalLiveGroup(_localLiveScopeGroup));

		Assert.assertNull(
			_stagingGroupHelper.fetchLocalLiveGroup(_remoteStagingGroup));
		Assert.assertNull(
			_stagingGroupHelper.fetchLocalLiveGroup(_remoteStagingScopeGroup));

		Assert.assertNull(
			_stagingGroupHelper.fetchLocalLiveGroup(_remoteLiveGroup));
		Assert.assertNull(
			_stagingGroupHelper.fetchLocalLiveGroup(_remoteLiveScopeGroup));

		Assert.assertNull(
			_stagingGroupHelper.fetchLocalLiveGroup(_regularGroup));
	}

	@Test
	public void testFetchLocalStagingGroup() throws Exception {
		Assert.assertNull(
			_stagingGroupHelper.fetchLocalStagingGroup(_localStagingGroup));
		Assert.assertNull(
			_stagingGroupHelper.fetchLocalStagingGroup(
				_localStagingScopeGroup));

		Assert.assertEquals(
			_localStagingGroup,
			_stagingGroupHelper.fetchLocalStagingGroup(_localLiveGroup));
		Assert.assertEquals(
			_localStagingGroup,
			_stagingGroupHelper.fetchLocalStagingGroup(_localLiveScopeGroup));

		Assert.assertNull(
			_stagingGroupHelper.fetchLocalStagingGroup(_remoteStagingGroup));
		Assert.assertNull(
			_stagingGroupHelper.fetchLocalStagingGroup(
				_remoteStagingScopeGroup));

		Assert.assertNull(
			_stagingGroupHelper.fetchLocalStagingGroup(_remoteLiveGroup));
		Assert.assertNull(
			_stagingGroupHelper.fetchLocalStagingGroup(_remoteLiveScopeGroup));

		Assert.assertNull(
			_stagingGroupHelper.fetchLocalStagingGroup(_regularGroup));
	}

	@Test
	public void testFetchRemoteLiveGroup() throws Exception {
		Assert.assertNull(
			_stagingGroupHelper.fetchRemoteLiveGroup(_localStagingGroup));
		Assert.assertNull(
			_stagingGroupHelper.fetchRemoteLiveGroup(_localStagingScopeGroup));

		Assert.assertNull(
			_stagingGroupHelper.fetchRemoteLiveGroup(_localLiveGroup));
		Assert.assertNull(
			_stagingGroupHelper.fetchRemoteLiveGroup(_localLiveScopeGroup));

		Assert.assertEquals(
			_remoteLiveGroup,
			_executeWithRemoteCredentials(
				() -> _stagingGroupHelper.fetchRemoteLiveGroup(
					_remoteStagingGroup)));
		Assert.assertEquals(
			_remoteLiveGroup,
			_executeWithRemoteCredentials(
				() -> _stagingGroupHelper.fetchRemoteLiveGroup(
					_remoteStagingScopeGroup)));

		Assert.assertNull(
			_executeWithRemoteCredentials(
				() -> _stagingGroupHelper.fetchRemoteLiveGroup(
					_remoteLiveGroup)));
		Assert.assertNull(
			_executeWithRemoteCredentials(
				() -> _stagingGroupHelper.fetchRemoteLiveGroup(
					_remoteLiveScopeGroup)));

		Assert.assertNull(
			_stagingGroupHelper.fetchRemoteLiveGroup(_regularGroup));
	}

	@Test
	public void testIsLiveGroup() {
		Assert.assertFalse(_stagingGroupHelper.isLiveGroup(_localStagingGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLiveGroup(_localStagingGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isLiveGroup(_localStagingScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLiveGroup(
				_localStagingScopeGroup.getGroupId()));

		Assert.assertTrue(_stagingGroupHelper.isLiveGroup(_localLiveGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isLiveGroup(_localLiveGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isLiveGroup(_localLiveScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isLiveGroup(_localLiveScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isLiveGroup(_remoteStagingGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLiveGroup(_remoteStagingGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isLiveGroup(_remoteStagingScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLiveGroup(
				_remoteStagingScopeGroup.getGroupId()));

		Assert.assertTrue(_stagingGroupHelper.isLiveGroup(_remoteLiveGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isLiveGroup(_remoteLiveGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isLiveGroup(_remoteLiveScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isLiveGroup(
				_remoteLiveScopeGroup.getGroupId()));

		Assert.assertFalse(_stagingGroupHelper.isLiveGroup(_regularGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLiveGroup(_regularGroup.getGroupId()));

		Assert.assertFalse(_stagingGroupHelper.isLiveGroup(null));
		Assert.assertFalse(_stagingGroupHelper.isLiveGroup(-1));
	}

	@Test
	public void testIsLocalLiveGroup() {
		Assert.assertFalse(
			_stagingGroupHelper.isLocalLiveGroup(_localStagingGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalLiveGroup(
				_localStagingGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalLiveGroup(_localStagingScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalLiveGroup(
				_localStagingScopeGroup.getGroupId()));

		Assert.assertTrue(
			_stagingGroupHelper.isLocalLiveGroup(_localLiveGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isLocalLiveGroup(_localLiveGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isLocalLiveGroup(_localLiveScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isLocalLiveGroup(
				_localLiveScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isLocalLiveGroup(_remoteStagingGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalLiveGroup(
				_remoteStagingGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalLiveGroup(_remoteStagingScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalLiveGroup(
				_remoteStagingScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isLocalLiveGroup(_remoteLiveGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalLiveGroup(
				_remoteLiveGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalLiveGroup(_remoteLiveScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalLiveGroup(
				_remoteLiveScopeGroup.getGroupId()));

		Assert.assertFalse(_stagingGroupHelper.isLocalLiveGroup(_regularGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalLiveGroup(_regularGroup.getGroupId()));

		Assert.assertFalse(_stagingGroupHelper.isLocalLiveGroup(null));
		Assert.assertFalse(_stagingGroupHelper.isLocalLiveGroup(-1));
	}

	@Test
	public void testIsLocalStagingGroup() {
		Assert.assertTrue(
			_stagingGroupHelper.isLocalStagingGroup(_localStagingGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isLocalStagingGroup(
				_localStagingGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isLocalStagingGroup(_localStagingScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isLocalStagingGroup(
				_localStagingScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingGroup(_localLiveGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingGroup(
				_localLiveGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingGroup(_localLiveScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingGroup(
				_localLiveScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingGroup(_remoteStagingGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingGroup(
				_remoteStagingGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingGroup(_remoteStagingScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingGroup(
				_remoteStagingScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingGroup(_remoteLiveGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingGroup(
				_remoteLiveGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingGroup(_remoteLiveScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingGroup(
				_remoteLiveScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingGroup(_regularGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingGroup(
				_regularGroup.getGroupId()));

		Assert.assertFalse(_stagingGroupHelper.isLocalStagingGroup(null));
		Assert.assertFalse(_stagingGroupHelper.isLocalStagingGroup(-1));
	}

	@Test
	public void testIsLocalStagingOrLocalLiveGroup() {
		Assert.assertTrue(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_localStagingGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_localStagingGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_localStagingScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_localStagingScopeGroup.getGroupId()));

		Assert.assertTrue(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_localLiveGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_localLiveGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_localLiveScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_localLiveScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_remoteStagingGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_remoteStagingGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_remoteStagingScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_remoteStagingScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_remoteLiveGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_remoteLiveGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_remoteLiveScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_remoteLiveScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(_regularGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(
				_regularGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(null));
		Assert.assertFalse(
			_stagingGroupHelper.isLocalStagingOrLocalLiveGroup(-1));
	}

	@Test
	public void testIsRemoteLiveGroup() {
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteLiveGroup(_localStagingGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteLiveGroup(
				_localStagingGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteLiveGroup(_localStagingScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteLiveGroup(
				_localStagingScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isRemoteLiveGroup(_localLiveGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteLiveGroup(
				_localLiveGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteLiveGroup(_localLiveScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteLiveGroup(
				_localLiveScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isRemoteLiveGroup(_remoteStagingGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteLiveGroup(
				_remoteStagingGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteLiveGroup(_remoteStagingScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteLiveGroup(
				_remoteStagingScopeGroup.getGroupId()));

		Assert.assertTrue(
			_stagingGroupHelper.isRemoteLiveGroup(_remoteLiveGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isRemoteLiveGroup(
				_remoteLiveGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isRemoteLiveGroup(_remoteLiveScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isRemoteLiveGroup(
				_remoteLiveScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isRemoteLiveGroup(_regularGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteLiveGroup(_regularGroup.getGroupId()));

		Assert.assertFalse(_stagingGroupHelper.isRemoteLiveGroup(null));
		Assert.assertFalse(_stagingGroupHelper.isRemoteLiveGroup(-1));
	}

	@Test
	public void testIsRemoteStagingGroup() {
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingGroup(_localStagingGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingGroup(
				_localStagingGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingGroup(_localStagingScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingGroup(
				_localStagingScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingGroup(_localLiveGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingGroup(
				_localLiveGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingGroup(_localLiveScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingGroup(
				_localLiveScopeGroup.getGroupId()));

		Assert.assertTrue(
			_stagingGroupHelper.isRemoteStagingGroup(_remoteStagingGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isRemoteStagingGroup(
				_remoteStagingGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isRemoteStagingGroup(_remoteStagingScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isRemoteStagingGroup(
				_remoteStagingScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingGroup(_remoteLiveGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingGroup(
				_remoteLiveGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingGroup(_remoteLiveScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingGroup(
				_remoteLiveScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingGroup(_regularGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingGroup(
				_regularGroup.getGroupId()));

		Assert.assertFalse(_stagingGroupHelper.isRemoteStagingGroup(null));
		Assert.assertFalse(_stagingGroupHelper.isRemoteStagingGroup(-1));
	}

	@Test
	public void testIsRemoteStagingOrRemoteLiveGroup() {
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_localStagingGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_localStagingGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_localStagingScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_localStagingScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_localLiveGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_localLiveGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_localLiveScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_localLiveScopeGroup.getGroupId()));

		Assert.assertTrue(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_remoteStagingGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_remoteStagingGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_remoteStagingScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_remoteStagingScopeGroup.getGroupId()));

		Assert.assertTrue(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_remoteLiveGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_remoteLiveGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_remoteLiveScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_remoteLiveScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_regularGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(
				_regularGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(null));
		Assert.assertFalse(
			_stagingGroupHelper.isRemoteStagingOrRemoteLiveGroup(-1));
	}

	@Test
	public void testIsStagedPortlet() {
		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_localStagingGroup, _PORTLET_ID_BOOKMARKS));
		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_localStagingGroup.getGroupId(), _PORTLET_ID_BOOKMARKS));
		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_localStagingScopeGroup, _PORTLET_ID_BOOKMARKS));
		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_localStagingScopeGroup.getGroupId(), _PORTLET_ID_BOOKMARKS));

		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_remoteStagingGroup, _PORTLET_ID_BOOKMARKS));
		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_remoteStagingGroup.getGroupId(), _PORTLET_ID_BOOKMARKS));
		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_remoteStagingScopeGroup, _PORTLET_ID_BOOKMARKS));
		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_remoteStagingScopeGroup.getGroupId(), _PORTLET_ID_BOOKMARKS));

		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_localLiveGroup, _PORTLET_ID_BOOKMARKS));
		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_localLiveGroup.getGroupId(), _PORTLET_ID_BOOKMARKS));
		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_localLiveScopeGroup, _PORTLET_ID_BOOKMARKS));
		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_localLiveScopeGroup.getGroupId(), _PORTLET_ID_BOOKMARKS));

		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_remoteLiveGroup, _PORTLET_ID_BOOKMARKS));
		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_remoteLiveGroup.getGroupId(), _PORTLET_ID_BOOKMARKS));
		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_remoteLiveScopeGroup, _PORTLET_ID_BOOKMARKS));
		Assert.assertTrue(
			_stagingGroupHelper.isStagedPortlet(
				_remoteLiveScopeGroup.getGroupId(), _PORTLET_ID_BOOKMARKS));

		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_regularGroup, _PORTLET_ID_BOOKMARKS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_regularGroup.getGroupId(), _PORTLET_ID_BOOKMARKS));

		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(-1, _PORTLET_ID_BOOKMARKS));

		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_localStagingGroup, _PORTLET_ID_BLOGS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_localStagingGroup.getGroupId(), _PORTLET_ID_BLOGS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_localStagingScopeGroup, _PORTLET_ID_BLOGS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_localStagingScopeGroup.getGroupId(), _PORTLET_ID_BLOGS));

		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_remoteStagingGroup, _PORTLET_ID_BLOGS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_remoteStagingGroup.getGroupId(), _PORTLET_ID_BLOGS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_remoteStagingScopeGroup, _PORTLET_ID_BLOGS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_remoteStagingScopeGroup.getGroupId(), _PORTLET_ID_BLOGS));

		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_localLiveGroup, _PORTLET_ID_BLOGS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_localLiveGroup.getGroupId(), _PORTLET_ID_BLOGS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_localLiveScopeGroup, _PORTLET_ID_BLOGS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_localLiveScopeGroup.getGroupId(), _PORTLET_ID_BLOGS));

		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_remoteLiveGroup, _PORTLET_ID_BLOGS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_remoteLiveGroup.getGroupId(), _PORTLET_ID_BLOGS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_remoteLiveScopeGroup, _PORTLET_ID_BLOGS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_remoteLiveScopeGroup.getGroupId(), _PORTLET_ID_BLOGS));

		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_regularGroup, _PORTLET_ID_BLOGS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(
				_regularGroup.getGroupId(), _PORTLET_ID_BLOGS));

		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(null, _PORTLET_ID_BLOGS));
		Assert.assertFalse(
			_stagingGroupHelper.isStagedPortlet(-1, _PORTLET_ID_BLOGS));
	}

	@Test
	public void testIsStagingGroup() {
		Assert.assertTrue(
			_stagingGroupHelper.isStagingGroup(_localStagingGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingGroup(
				_localStagingGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingGroup(_localStagingScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingGroup(
				_localStagingScopeGroup.getGroupId()));

		Assert.assertFalse(_stagingGroupHelper.isStagingGroup(_localLiveGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isStagingGroup(_localLiveGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isStagingGroup(_localLiveScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isStagingGroup(
				_localLiveScopeGroup.getGroupId()));

		Assert.assertTrue(
			_stagingGroupHelper.isStagingGroup(_remoteStagingGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingGroup(
				_remoteStagingGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingGroup(_remoteStagingScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingGroup(
				_remoteStagingScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isStagingGroup(_remoteLiveGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isStagingGroup(_remoteLiveGroup.getGroupId()));
		Assert.assertFalse(
			_stagingGroupHelper.isStagingGroup(_remoteLiveScopeGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isStagingGroup(
				_remoteLiveScopeGroup.getGroupId()));

		Assert.assertFalse(_stagingGroupHelper.isStagingGroup(_regularGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isStagingGroup(_regularGroup.getGroupId()));

		Assert.assertFalse(_stagingGroupHelper.isStagingGroup(null));
		Assert.assertFalse(_stagingGroupHelper.isStagingGroup(-1));
	}

	@Test
	public void testIsStagingOrLiveGroup() {
		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(_localStagingGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(
				_localStagingGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(_localStagingScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(
				_localStagingScopeGroup.getGroupId()));

		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(_localLiveGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(
				_localLiveGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(_localLiveScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(
				_localLiveScopeGroup.getGroupId()));

		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(_remoteStagingGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(
				_remoteStagingGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(_remoteStagingScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(
				_remoteStagingScopeGroup.getGroupId()));

		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(_remoteLiveGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(
				_remoteLiveGroup.getGroupId()));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(_remoteLiveScopeGroup));
		Assert.assertTrue(
			_stagingGroupHelper.isStagingOrLiveGroup(
				_remoteLiveScopeGroup.getGroupId()));

		Assert.assertFalse(
			_stagingGroupHelper.isStagingOrLiveGroup(_regularGroup));
		Assert.assertFalse(
			_stagingGroupHelper.isStagingOrLiveGroup(
				_regularGroup.getGroupId()));

		Assert.assertFalse(_stagingGroupHelper.isStagingOrLiveGroup(null));
		Assert.assertFalse(_stagingGroupHelper.isStagingOrLiveGroup(-1));
	}

	private void _addLocalStagingGroups() throws Exception {
		_localLiveGroup = GroupTestUtil.addGroup();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAttribute(
			"staged--staged-portlet_" + _PORTLET_ID_BLOGS + "--", "false");
		serviceContext.setAttribute(
			"staged--staged-portlet_" + _PORTLET_ID_BOOKMARKS + "--", "true");

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), _localLiveGroup, false, false,
			serviceContext);

		_localStagingGroup = GroupLocalServiceUtil.fetchStagingGroup(
			_localLiveGroup.getGroupId());

		Assert.assertNotNull(_localStagingGroup);
	}

	private void _addRemoteStagingGroups() throws Exception {
		_remoteLiveGroup = GroupTestUtil.addGroup();
		_remoteStagingGroup = GroupTestUtil.addGroup();

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET",
					"F0E1D2C3B4A5968778695A4B3C2D1E0F");
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET_HEX", true)) {

			int serverPort = PortalUtil.getPortalServerPort(false);

			Assert.assertFalse(
				"Invalid server port: " + serverPort,
				(serverPort < 1) || (serverPort > 65535));

			String pathContext = PortalUtil.getPathContext();

			UserTestUtil.setUser(TestPropsValues.getUser());

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAttribute(
				"staged--staged-portlet_" + _PORTLET_ID_BLOGS + "--", "false");
			serviceContext.setAttribute(
				"staged--staged-portlet_" + _PORTLET_ID_BOOKMARKS + "--",
				"true");

			StagingLocalServiceUtil.enableRemoteStaging(
				TestPropsValues.getUserId(), _remoteStagingGroup, false, false,
				"localhost", serverPort, pathContext, false,
				_remoteLiveGroup.getGroupId(), serviceContext);

			GroupUtil.clearCache();

			_remoteLiveGroup = GroupLocalServiceUtil.getGroup(
				_remoteLiveGroup.getGroupId());
		}
	}

	private Group _addScopeGroup(Group group) throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		return GroupLocalServiceUtil.addGroup(
			TestPropsValues.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
			Layout.class.getName(), layout.getPlid(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), String.valueOf(layout.getPlid())
			).build(),
			null, 0, true, GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null,
			false, true, null);
	}

	private Group _executeWithRemoteCredentials(Supplier<Group> groupSupplier) {
		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET",
					"F0E1D2C3B4A5968778695A4B3C2D1E0F");
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET_HEX", true)) {

			return groupSupplier.get();
		}
	}

	private static final String _PORTLET_ID_BLOGS =
		"com_liferay_blogs_web_portlet_BlogsPortlet";

	private static final String _PORTLET_ID_BOOKMARKS =
		"com_liferay_bookmarks_web_portlet_BookmarksPortlet";

	private static final Log _log = LogFactoryUtil.getLog(
		StagingGroupHelperTest.class);

	@Inject(
		filter = "component.name=com.liferay.staging.internal.instance.lifecycle.AddCompanyGroupPortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener
		_addCompanyGroupPortalInstanceLifecycleListener;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private Group _localLiveGroup;
	private Group _localLiveScopeGroup;
	private Group _localStagingGroup;
	private Group _localStagingScopeGroup;
	private Group _regularGroup;
	private Group _remoteLiveGroup;
	private Group _remoteLiveScopeGroup;
	private Group _remoteStagingGroup;
	private Group _remoteStagingScopeGroup;

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

}