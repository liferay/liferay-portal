/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.web.internal.display.context.util.MockHttpServletRequestBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.bean.BeanPropertiesImpl;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portlet.internal.PortalContextImpl;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Map;

import org.assertj.core.api.AbstractUriAssert;
import org.assertj.core.api.Assertions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
public class DLEditFileShortcutDisplayContextTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		BeanPropertiesUtil beanPropertiesUtil = new BeanPropertiesUtil();

		beanPropertiesUtil.setBeanProperties(new BeanPropertiesImpl());

		_dlAppService = Mockito.mock(DLAppService.class);

		_itemSelector = Mockito.mock(ItemSelector.class);

		_language = Mockito.mock(Language.class);
	}

	@Test
	public void testGetEditFileShortcutURLWithAttribute()
		throws URISyntaxException {

		FileShortcut fileShortcut = _addRandomFileShortcut();

		DLEditFileShortcutDisplayContext dlEditFileShortcutDisplayContext =
			_getDLEditFileShortcutDisplayContext(
				new MockHttpServletRequestBuilder(
				).withAttribute(
					WebKeys.DOCUMENT_LIBRARY_FILE_SHORTCUT, fileShortcut
				).build());

		AbstractUriAssert<?> abstractUriAssert = Assertions.assertThat(
			new URI(dlEditFileShortcutDisplayContext.getEditFileShortcutURL()));

		abstractUriAssert.hasParameter(Constants.CMD, Constants.UPDATE);
	}

	@Test
	public void testGetEditFileShortcutURLWithoutAttribute()
		throws URISyntaxException {

		DLEditFileShortcutDisplayContext dlEditFileShortcutDisplayContext =
			_getDLEditFileShortcutDisplayContext(new MockHttpServletRequest());

		AbstractUriAssert<?> abstractUriAssert = Assertions.assertThat(
			new URI(dlEditFileShortcutDisplayContext.getEditFileShortcutURL()));

		abstractUriAssert.hasParameter(Constants.CMD, Constants.ADD);
	}

	@Test
	public void testGetFieldsWithoutParametersAndWithAttributes() {
		FileShortcut fileShortcut = _addRandomFileShortcut();

		DLEditFileShortcutDisplayContext dlEditFileShortcutDisplayContext =
			_getDLEditFileShortcutDisplayContext(
				new MockHttpServletRequestBuilder(
				).withAttribute(
					WebKeys.DOCUMENT_LIBRARY_FILE_SHORTCUT, fileShortcut
				).build());

		Assert.assertEquals(
			fileShortcut.getFileShortcutId(),
			dlEditFileShortcutDisplayContext.getFileShortcutId());
		Assert.assertEquals(
			fileShortcut.getFolderId(),
			dlEditFileShortcutDisplayContext.getFolderId());
		Assert.assertEquals(
			fileShortcut.getRepositoryId(),
			dlEditFileShortcutDisplayContext.getRepositoryId());
		Assert.assertEquals(
			fileShortcut.getToFileEntryId(),
			dlEditFileShortcutDisplayContext.getToFileEntryId());
	}

	@Test
	public void testGetFieldsWithoutParametersAndWithoutAttributes() {
		DLEditFileShortcutDisplayContext dlEditFileShortcutDisplayContext =
			_getDLEditFileShortcutDisplayContext(new MockHttpServletRequest());

		Assert.assertEquals(
			0, dlEditFileShortcutDisplayContext.getFileShortcutId());
		Assert.assertEquals(0, dlEditFileShortcutDisplayContext.getFolderId());
		Assert.assertEquals(
			0, dlEditFileShortcutDisplayContext.getRepositoryId());
		Assert.assertEquals(
			0, dlEditFileShortcutDisplayContext.getToFileEntryId());
	}

	@Test
	public void testGetFieldsWithParametersAndWithAttributes() {
		FileShortcut fileShortcut = _addRandomFileShortcut();

		DLEditFileShortcutDisplayContext dlEditFileShortcutDisplayContext =
			_getDLEditFileShortcutDisplayContext(
				new MockHttpServletRequestBuilder(
				).withAttribute(
					WebKeys.DOCUMENT_LIBRARY_FILE_SHORTCUT, fileShortcut
				).withParameter(
					"fileShortcutId", "12356"
				).withParameter(
					"folderId", "23567"
				).withParameter(
					"repositoryId", "35678"
				).withParameter(
					"toFileEntryId", "56789"
				).build());

		Assert.assertEquals(
			12356L, dlEditFileShortcutDisplayContext.getFileShortcutId());
		Assert.assertEquals(
			23567L, dlEditFileShortcutDisplayContext.getFolderId());
		Assert.assertEquals(
			35678L, dlEditFileShortcutDisplayContext.getRepositoryId());
		Assert.assertEquals(
			56789L, dlEditFileShortcutDisplayContext.getToFileEntryId());
	}

	@Test
	public void testGetFieldsWithParametersAndWithoutAttributes() {
		DLEditFileShortcutDisplayContext dlEditFileShortcutDisplayContext =
			_getDLEditFileShortcutDisplayContext(
				new MockHttpServletRequestBuilder(
				).withParameter(
					"fileShortcutId", "12356"
				).withParameter(
					"folderId", "23567"
				).withParameter(
					"repositoryId", "35678"
				).withParameter(
					"toFileEntryId", "56789"
				).build());

		Assert.assertEquals(
			12356L, dlEditFileShortcutDisplayContext.getFileShortcutId());
		Assert.assertEquals(
			23567L, dlEditFileShortcutDisplayContext.getFolderId());
		Assert.assertEquals(
			35678L, dlEditFileShortcutDisplayContext.getRepositoryId());
		Assert.assertEquals(
			56789L, dlEditFileShortcutDisplayContext.getToFileEntryId());
	}

	@Test
	public void testGetTitleWithAttribute() {
		FileShortcut fileShortcut = _addRandomFileShortcut();

		DLEditFileShortcutDisplayContext dlEditFileShortcutDisplayContext =
			_getDLEditFileShortcutDisplayContext(
				new MockHttpServletRequestBuilder(
				).withAttribute(
					WebKeys.DOCUMENT_LIBRARY_FILE_SHORTCUT, fileShortcut
				).build());

		Mockito.when(
			_language.format(
				Mockito.nullable(HttpServletRequest.class),
				Mockito.eq("shortcut-to-x"), Mockito.nullable(String.class),
				Mockito.anyBoolean())
		).thenReturn(
			"Short Cut To X"
		);

		Assert.assertEquals(
			"Short Cut To X", dlEditFileShortcutDisplayContext.getTitle());
	}

	@Test
	public void testGetTitleWithoutAttribute() {
		DLEditFileShortcutDisplayContext dlEditFileShortcutDisplayContext =
			_getDLEditFileShortcutDisplayContext(new MockHttpServletRequest());

		Mockito.when(
			_language.get(
				Mockito.any(HttpServletRequest.class),
				Mockito.eq("new-file-shortcut"))
		).thenReturn(
			"New"
		);

		Assert.assertEquals("New", dlEditFileShortcutDisplayContext.getTitle());
	}

	private FileShortcut _addRandomFileShortcut() {
		FileShortcut fileShortcut = Mockito.mock(FileShortcut.class);

		long randomLong = RandomTestUtil.randomLong();

		Mockito.when(
			fileShortcut.getFileShortcutId()
		).thenReturn(
			randomLong
		);

		Mockito.when(
			fileShortcut.getFolderId()
		).thenReturn(
			randomLong + 1
		);

		Mockito.when(
			fileShortcut.getRepositoryId()
		).thenReturn(
			randomLong + 2
		);

		Mockito.when(
			fileShortcut.getToFileEntryId()
		).thenReturn(
			randomLong + 3
		);

		return fileShortcut;
	}

	private DLEditFileShortcutDisplayContext
		_getDLEditFileShortcutDisplayContext(
			HttpServletRequest httpServletRequest) {

		return new DLEditFileShortcutDisplayContext(
			_dlAppService, _itemSelector, _language,
			new MockPortletRenderRequest(httpServletRequest),
			new MockPortletRenderResponse());
	}

	private DLAppService _dlAppService;
	private ItemSelector _itemSelector;
	private Language _language;

	private class MockActionPortletURL extends MockPortletURL {

		public MockActionPortletURL() {
			super(new PortalContextImpl(), "urlType");
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			Map<String, String[]> parameterMap = getParameterMap();

			for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue()[0]);
				sb.append("&");
			}

			sb.setLength(sb.length() - 1);

			return "http://localhost/mockportlet?" + sb.toString();
		}

	}

	private class MockPortletRenderRequest
		extends MockLiferayPortletRenderRequest {

		public MockPortletRenderRequest(HttpServletRequest httpServletRequest) {
			_httpServletRequest = httpServletRequest;
		}

		@Override
		public Object getAttribute(String name) {
			return _httpServletRequest.getAttribute(name);
		}

		@Override
		public HttpServletRequest getHttpServletRequest() {
			return _httpServletRequest;
		}

		@Override
		public String getParameter(String name) {
			return _httpServletRequest.getParameter(name);
		}

		private final HttpServletRequest _httpServletRequest;

	}

	private class MockPortletRenderResponse
		extends MockLiferayPortletRenderResponse {

		@Override
		public PortletURL createActionURL() {
			return new MockActionPortletURL();
		}

	}

}