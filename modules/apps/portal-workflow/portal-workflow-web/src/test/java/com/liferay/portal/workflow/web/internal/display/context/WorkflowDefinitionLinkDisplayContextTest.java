/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.display.context;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerVisibleFilter;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.comparator.WorkflowComparatorFactory;
import com.liferay.portal.workflow.util.WorkflowDefinitionManagerUtil;
import com.liferay.portal.workflow.web.internal.search.WorkflowDefinitionLinkSearchEntry;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Mateus Xavier
 */
public class WorkflowDefinitionLinkDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpHttpServletRequest();
		_setUpLanguageUtil();
		_setUpPortalUtil();
		_setUpWorkflowDefinitionLink();
		_setUpWorkflowDefinitionLinkLocalService();
		_setUpWorkflowDefinitionManagerUtil();
		_setUpWorkflowHandler();

		_workflowDefinitionLinkDisplayContext = Mockito.spy(
			new WorkflowDefinitionLinkDisplayContext(
				_renderRequest, _renderResponse,
				_workflowDefinitionLinkLocalService,
				Mockito.mock(ResourceBundleLoader.class),
				Mockito.mock(WorkflowHandlerVisibleFilter.class),
				Mockito.mock(WorkflowComparatorFactory.class)));

		Mockito.doReturn(
			_DEFAULT_WORKFLOW_DEFINITION_LABEL
		).when(
			_workflowDefinitionLinkDisplayContext
		).getDefaultWorkflowDefinitionLabel(
			_CLASS_NAME
		);

		Mockito.doReturn(
			Collections.emptyList()
		).when(
			_workflowDefinitionLinkDisplayContext
		).getWorkflowDefinitions();

		Mockito.doReturn(
			true
		).when(
			_workflowDefinitionLinkDisplayContext
		).isControlPanelPortlet();
	}

	@After
	public void tearDown() {
		_workflowDefinitionManagerUtilMockedStatic.close();
	}

	@Test
	public void testCreateWorkflowDefinitionLinkSearchEntry() throws Exception {
		String script = "'\"></option><img onerror=alert(123) src=x>";

		Mockito.when(
			_workflowHandler.getType(Mockito.any(Locale.class))
		).thenReturn(
			script
		);

		WorkflowDefinitionLinkSearchEntry workflowDefinitionLinkSearchEntry =
			_workflowDefinitionLinkDisplayContext.
				createWorkflowDefinitionLinkSearchEntry(
					_workflowHandler, LocaleUtil.US);

		Assert.assertEquals(
			HtmlUtil.escapeAttribute(script),
			workflowDefinitionLinkSearchEntry.getResource());
	}

	@Test
	public void testGetWorkflowDefinitionLabel() throws Exception {
		String title = RandomTestUtil.randomString();

		Mockito.when(
			_workflowDefinition.getTitle("en_US")
		).thenReturn(
			title
		);

		Mockito.when(
			_workflowDefinition.isActive()
		).thenReturn(
			false
		);

		Assert.assertEquals(
			title,
			_workflowDefinitionLinkDisplayContext.getWorkflowDefinitionLabel(
				_workflowHandler));

		Mockito.when(
			_workflowDefinition.isActive()
		).thenReturn(
			true
		);

		Assert.assertEquals(
			_DEFAULT_WORKFLOW_DEFINITION_LABEL,
			_workflowDefinitionLinkDisplayContext.getWorkflowDefinitionLabel(
				_workflowHandler));
	}

	private void _setUpHttpServletRequest() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.getLanguageId(LocaleUtil.US)
		).thenReturn(
			"en_US"
		);

		languageUtil.setLanguage(language);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getHttpServletRequest(_renderRequest)
		).thenReturn(
			_httpServletRequest
		);

		Mockito.when(
			portal.getLiferayPortletRequest(_renderRequest)
		).thenReturn(
			Mockito.mock(LiferayPortletRequest.class)
		);

		Mockito.when(
			portal.getLiferayPortletResponse(_renderResponse)
		).thenReturn(
			Mockito.mock(LiferayPortletResponse.class)
		);

		portalUtil.setPortal(portal);
	}

	private void _setUpWorkflowDefinitionLink() {
		Mockito.when(
			_workflowDefinitionLink.getWorkflowDefinitionName()
		).thenReturn(
			_WORKFLOW_DEFINITION_NAME
		);

		Mockito.when(
			_workflowDefinitionLink.getWorkflowDefinitionVersion()
		).thenReturn(
			_WORKFLOW_DEFINITION_VERSION
		);
	}

	private void _setUpWorkflowDefinitionLinkLocalService() {
		Mockito.when(
			_workflowDefinitionLinkLocalService.
				fetchDefaultWorkflowDefinitionLink(_COMPANY_ID, _CLASS_NAME)
		).thenReturn(
			_workflowDefinitionLink
		);
	}

	private void _setUpWorkflowDefinitionManagerUtil() {
		_workflowDefinitionManagerUtilMockedStatic.when(
			() -> WorkflowDefinitionManagerUtil.liberalGetWorkflowDefinition(
				_COMPANY_ID, _WORKFLOW_DEFINITION_NAME,
				_WORKFLOW_DEFINITION_VERSION)
		).thenReturn(
			_workflowDefinition
		);
	}

	private void _setUpWorkflowHandler() {
		Mockito.when(
			_workflowHandler.getClassName()
		).thenReturn(
			_CLASS_NAME
		);
	}

	private static final String _CLASS_NAME = RandomTestUtil.randomString();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _DEFAULT_WORKFLOW_DEFINITION_LABEL =
		RandomTestUtil.randomString();

	private static final String _WORKFLOW_DEFINITION_NAME =
		RandomTestUtil.randomString();

	private static final int _WORKFLOW_DEFINITION_VERSION =
		RandomTestUtil.randomInt();

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);
	private final WorkflowDefinition _workflowDefinition = Mockito.mock(
		WorkflowDefinition.class);
	private final WorkflowDefinitionLink _workflowDefinitionLink = Mockito.mock(
		WorkflowDefinitionLink.class);
	private WorkflowDefinitionLinkDisplayContext
		_workflowDefinitionLinkDisplayContext;
	private final WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService = Mockito.mock(
			WorkflowDefinitionLinkLocalService.class);
	private final MockedStatic<WorkflowDefinitionManagerUtil>
		_workflowDefinitionManagerUtilMockedStatic = Mockito.mockStatic(
			WorkflowDefinitionManagerUtil.class);
	private final WorkflowHandler<?> _workflowHandler = Mockito.mock(
		WorkflowHandler.class);

}