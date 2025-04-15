/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.instance.tracker.url.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.UnicodeLanguageUtil;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.workflow.instance.tracker.url.provider.WorkflowInstanceTrackerURLProvider;

import jakarta.portlet.PortletRequest;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class WorkflowInstanceTrackerURLProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetURL() throws Exception {
		ServiceRegistration<WorkflowHandler<?>>
			workflowHandlerServiceRegistration = _registryWorkflowHandler();

		Class<?> clazz = getClass();

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			TestPropsValues.getCompanyId(), 0, TestPropsValues.getUserId(),
			clazz.getName(), 1, null, new ServiceContext());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		Object object = new Object() {

			public long getCompanyId() throws PortalException {
				return TestPropsValues.getCompanyId();
			}

			public long getGroupId() {
				return 0;
			}

			public long getPrimaryKey() {
				return 1;
			}

		};

		WorkflowInstanceLink workflowInstanceLink =
			_workflowInstanceLinkLocalService.getWorkflowInstanceLink(
				TestPropsValues.getCompanyId(), 0, clazz.getName(), 1);

		String portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				mockHttpServletRequest, null,
				_WORKFLOW_INSTANCE_TRACKER_PORTLET, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setParameter(
			"instanceId", workflowInstanceLink.getWorkflowInstanceId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();

		Assert.assertEquals(
			StringBundler.concat(
				"javascript:Liferay.Util.openModal({iframeBodyCssClass: '', ",
				"title: '",
				UnicodeLanguageUtil.get(
					mockHttpServletRequest, "track-workflow"),
				"', url: '", portletURL, "'});;"),
			_workflowInstanceTrackerURLProvider.getURL(
				object, mockHttpServletRequest, clazz, true));

		Assert.assertEquals(
			portletURL,
			_workflowInstanceTrackerURLProvider.getURL(
				object, mockHttpServletRequest, clazz, false));

		workflowHandlerServiceRegistration.unregister();
	}

	private ServiceRegistration<WorkflowHandler<?>> _registryWorkflowHandler() {
		Class<?> clazz = getClass();

		Bundle bundle = FrameworkUtil.getBundle(clazz);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			(Class<WorkflowHandler<?>>)(Class<?>)WorkflowHandler.class,
			(WorkflowHandler)ProxyUtil.newProxyInstance(
				WorkflowHandler.class.getClassLoader(),
				new Class<?>[] {WorkflowHandler.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "getClassName")) {
						return clazz.getName();
					}

					if (Objects.equals(method.getName(), "getType")) {
						return StringPool.BLANK;
					}

					if (Objects.equals(method.getName(), "isScopeable")) {
						return false;
					}

					if (Objects.equals(
							method.getName(), "getWorkflowDefinitionLink")) {

						return _workflowDefinitionLinkLocalService.
							updateWorkflowDefinitionLink(
								TestPropsValues.getUserId(),
								TestPropsValues.getCompanyId(), 0,
								clazz.getName(), 0, 0, "Single Approver", 1);
					}

					if (Objects.equals(
							method.getName(), "startWorkflowInstance")) {

						_workflowInstanceLinkLocalService.startWorkflowInstance(
							TestPropsValues.getCompanyId(), 0, (Long)args[2],
							clazz.getName(), 1,
							(Map<String, Serializable>)args[5]);
					}

					return null;
				}),
			HashMapDictionaryBuilder.put(
				"model.class.name=", clazz.getName()
			).build());
	}

	private static final String _WORKFLOW_INSTANCE_TRACKER_PORTLET =
		"com_liferay_portal_workflow_instance_tracker_web_internal_portlet_" +
			"WorkflowInstanceTrackerPortlet";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Inject
	private WorkflowInstanceTrackerURLProvider
		_workflowInstanceTrackerURLProvider;

}