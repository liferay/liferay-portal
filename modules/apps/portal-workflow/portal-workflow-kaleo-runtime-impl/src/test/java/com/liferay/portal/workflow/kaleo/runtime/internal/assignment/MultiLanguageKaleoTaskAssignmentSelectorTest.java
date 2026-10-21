/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.assignment;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.kaleo.KaleoTaskAssignmentFactory;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentImpl;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.assignment.ScriptingAssigneeSelector;
import com.liferay.portal.workflow.kaleo.runtime.constants.AssigneeConstants;
import com.liferay.portal.workflow.kaleo.runtime.internal.configuration.WorkflowTaskScriptConfiguration;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Jiaxu Wei
 */
public class MultiLanguageKaleoTaskAssignmentSelectorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testUseJavaScriptingKaleoTaskAssignmentSelector()
		throws PortalException {

		TestJavaScriptingAssigneeSelector testJavaScriptingAssigneeSelector =
			_getTestJavaScriptingAssigneeSelector();

		_serviceRegistration = _bundleContext.registerService(
			ScriptingAssigneeSelector.class, testJavaScriptingAssigneeSelector,
			MapUtil.singletonDictionary("scripting.language", (Object)"java"));

		MultiLanguageKaleoTaskAssignmentSelector
			multiLanguageKaleoTaskAssignmentSelector =
				_getMultiLanguageKaleoTaskAssignmentSelector();

		Collection<KaleoTaskAssignment> kaleoTaskAssignments =
			multiLanguageKaleoTaskAssignmentSelector.getKaleoTaskAssignments(
				_getKaleoTaskAssignment(
					testJavaScriptingAssigneeSelector.getClass(), "java"),
				_getExecutionContext());

		Assert.assertEquals(
			kaleoTaskAssignments.toString(), 1, kaleoTaskAssignments.size());

		Iterator<KaleoTaskAssignment> iterator =
			kaleoTaskAssignments.iterator();

		KaleoTaskAssignment kaleoTaskAssignment = iterator.next();

		Assert.assertEquals(_USER_ID, kaleoTaskAssignment.getAssigneeClassPK());
	}

	private ConfigurationProvider _getConfigurationProvider()
		throws ConfigurationException {

		ConfigurationProvider configurationProvider = Mockito.mock(
			ConfigurationProvider.class);

		WorkflowTaskScriptConfiguration workflowTaskScriptConfiguration =
			_getWorkflowTaskScriptConfiguration();

		Mockito.when(
			configurationProvider.getSystemConfiguration(Mockito.any())
		).thenReturn(
			workflowTaskScriptConfiguration
		);

		return configurationProvider;
	}

	private ExecutionContext _getExecutionContext() {
		ExecutionContext executionContext = Mockito.mock(
			ExecutionContext.class);

		Mockito.when(
			executionContext.getKaleoTaskInstanceToken()
		).thenReturn(
			Mockito.mock(KaleoTaskInstanceToken.class)
		);

		return executionContext;
	}

	private KaleoInstanceLocalService _getKaleoInstanceLocalService() {
		KaleoInstanceLocalService kaleoInstanceLocalService = Mockito.mock(
			KaleoInstanceLocalService.class);

		Mockito.when(
			kaleoInstanceLocalService.updateKaleoInstance(Mockito.any())
		).thenReturn(
			Mockito.mock(KaleoInstance.class)
		);

		return kaleoInstanceLocalService;
	}

	private KaleoTaskAssignment _getKaleoTaskAssignment(
		Class<? extends ScriptingAssigneeSelector> clazz,
		String scriptLanguage) {

		KaleoTaskAssignment kaleoTaskAssignment = Mockito.mock(
			KaleoTaskAssignment.class);

		Mockito.when(
			kaleoTaskAssignment.getAssigneeScriptLanguage()
		).thenReturn(
			scriptLanguage
		);

		Mockito.when(
			kaleoTaskAssignment.getAssigneeScript()
		).thenReturn(
			clazz.getName()
		);

		return kaleoTaskAssignment;
	}

	private MultiLanguageKaleoTaskAssignmentSelector
			_getMultiLanguageKaleoTaskAssignmentSelector()
		throws ConfigurationException {

		MultiLanguageKaleoTaskAssignmentSelector
			multiLanguageKaleoTaskAssignmentSelector =
				new MultiLanguageKaleoTaskAssignmentSelector();

		ReflectionTestUtil.setFieldValue(
			multiLanguageKaleoTaskAssignmentSelector, "_configurationProvider",
			_getConfigurationProvider());
		ReflectionTestUtil.setFieldValue(
			multiLanguageKaleoTaskAssignmentSelector,
			"_kaleoInstanceLocalService", _getKaleoInstanceLocalService());

		KaleoTaskAssignmentFactory kaleoTaskAssignmentFactory = Mockito.mock(
			KaleoTaskAssignmentFactory.class);

		Mockito.when(
			kaleoTaskAssignmentFactory.createKaleoTaskAssignment()
		).thenReturn(
			new KaleoTaskAssignmentImpl()
		);

		ReflectionTestUtil.setFieldValue(
			multiLanguageKaleoTaskAssignmentSelector,
			"kaleoTaskAssignmentFactory", kaleoTaskAssignmentFactory);

		multiLanguageKaleoTaskAssignmentSelector.activate(_bundleContext);

		return multiLanguageKaleoTaskAssignmentSelector;
	}

	private TestJavaScriptingAssigneeSelector
		_getTestJavaScriptingAssigneeSelector() {

		KaleoTaskAssignmentFactory kaleoTaskAssignmentFactory = Mockito.mock(
			KaleoTaskAssignmentFactory.class);

		Mockito.when(
			kaleoTaskAssignmentFactory.createKaleoTaskAssignment()
		).thenReturn(
			new KaleoTaskAssignmentImpl()
		);

		return new TestJavaScriptingAssigneeSelector();
	}

	private WorkflowTaskScriptConfiguration
		_getWorkflowTaskScriptConfiguration() {

		WorkflowTaskScriptConfiguration workflowTaskScriptConfiguration =
			Mockito.mock(WorkflowTaskScriptConfiguration.class);

		Mockito.doReturn(
			0
		).when(
			workflowTaskScriptConfiguration
		).scriptedAssignmentCacheExpirationTime();

		return workflowTaskScriptConfiguration;
	}

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private ServiceRegistration<ScriptingAssigneeSelector> _serviceRegistration;

	private static class TestJavaScriptingAssigneeSelector
		implements ScriptingAssigneeSelector {

		public TestJavaScriptingAssigneeSelector() {
			Mockito.when(
				_user.getUserId()
			).thenReturn(
				_USER_ID
			);
		}

		@Override
		public Map<String, ?> getAssignees(
			ExecutionContext executionContext,
			KaleoTaskAssignment kaleoTaskAssignment) {

			return HashMapBuilder.put(
				AssigneeConstants.USER, _user
			).build();
		}

		private final User _user = Mockito.mock(User.class);

	}

}