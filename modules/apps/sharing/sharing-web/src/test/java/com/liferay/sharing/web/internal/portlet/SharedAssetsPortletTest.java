/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCCommandCache;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.sharing.web.internal.display.context.ViewSharedAssetsDisplayContext;
import com.liferay.sharing.web.internal.display.context.ViewSharedAssetsDisplayContextFactory;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Brian Wulbern
 */
public class SharedAssetsPortletTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testRender() throws Exception {
		_testRender("mvcPath", false);
		_testRender("mvcRenderCommandName", false);
		_testRender("mvcRenderCommandName", true);
	}

	private void _testRender(String parameterName, boolean mvcRenderCommand)
		throws Exception {

		ViewSharedAssetsDisplayContextFactory
			viewSharedAssetsDisplayContextFactory = Mockito.mock(
				ViewSharedAssetsDisplayContextFactory.class);

		ViewSharedAssetsDisplayContext viewSharedAssetsDisplayContext =
			Mockito.mock(ViewSharedAssetsDisplayContext.class);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();
		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		Mockito.when(
			viewSharedAssetsDisplayContextFactory.
				getViewSharedAssetsDisplayContext(
					mockLiferayPortletRenderRequest,
					mockLiferayPortletRenderResponse)
		).thenReturn(
			viewSharedAssetsDisplayContext
		);

		mockLiferayPortletRenderRequest.setParameter(
			parameterName, RandomTestUtil.randomString());

		AtomicReference<Object> viewSharedAssetsDisplayContextAtomicReference =
			new AtomicReference<>();

		SharedAssetsPortlet sharedAssetsPortlet = new SharedAssetsPortlet() {

			@Override
			protected String getTitle(RenderRequest renderRequest) {
				return RandomTestUtil.randomString();
			}

			@Override
			protected void include(
				String path, RenderRequest renderRequest,
				RenderResponse renderResponse) {

				viewSharedAssetsDisplayContextAtomicReference.set(
					renderRequest.getAttribute(
						ViewSharedAssetsDisplayContext.class.getName()));
			}

		};

		MVCCommandCache<MVCRenderCommand> mvcCommandCache = Mockito.mock(
			MVCCommandCache.class);

		MVCRenderCommand cachedMVCRenderCommand = MVCRenderCommand.EMPTY;

		if (mvcRenderCommand) {
			cachedMVCRenderCommand = (renderRequest, renderResponse) -> {
				viewSharedAssetsDisplayContextAtomicReference.set(
					renderRequest.getAttribute(
						ViewSharedAssetsDisplayContext.class.getName()));

				return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
			};
		}

		Mockito.when(
			mvcCommandCache.getMVCCommand(Mockito.anyString())
		).thenReturn(
			cachedMVCRenderCommand
		);

		ReflectionTestUtil.setFieldValue(
			sharedAssetsPortlet, "_renderMVCCommandCache", mvcCommandCache);

		ReflectionTestUtil.setFieldValue(
			sharedAssetsPortlet, "_viewSharedAssetsDisplayContextFactory",
			viewSharedAssetsDisplayContextFactory);

		sharedAssetsPortlet.render(
			mockLiferayPortletRenderRequest, mockLiferayPortletRenderResponse);

		Assert.assertSame(
			viewSharedAssetsDisplayContext,
			viewSharedAssetsDisplayContextAtomicReference.get());
	}

}