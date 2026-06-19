/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.internal.jaxrs.writer.interceptor;

import com.liferay.headless.data.masking.engine.DataMaskingEngine;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import java.io.OutputStream;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jose Luis Navarro
 */
public class DataMaskingWriterInterceptorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_dataMaskingWriterInterceptor, "_dataMaskingEngine",
			_dataMaskingEngine);
		ReflectionTestUtil.setFieldValue(
			_dataMaskingWriterInterceptor, "_httpServletRequest",
			_httpServletRequest);
		ReflectionTestUtil.setFieldValue(
			_dataMaskingWriterInterceptor, "_portal", _portal);
	}

	@Test
	public void testAroundWriteTo() throws Exception {
		Mockito.when(
			_httpServletRequest.getHeader("X-Liferay-Data-Masks")
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_writerInterceptorContext.getMediaType()
		).thenReturn(
			MediaType.APPLICATION_OCTET_STREAM_TYPE
		);

		_dataMaskingWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Mockito.when(
			_httpServletRequest.getHeader("X-Liferay-Data-Masks")
		).thenReturn(
			null
		);

		Mockito.when(
			_writerInterceptorContext.getMediaType()
		).thenReturn(
			MediaType.APPLICATION_JSON_TYPE
		);

		_dataMaskingWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Mockito.verify(
			_writerInterceptorContext, Mockito.times(2)
		).proceed();

		Mockito.verify(
			_writerInterceptorContext, Mockito.never()
		).setOutputStream(
			Mockito.any(OutputStream.class)
		);

		Mockito.verify(
			_dataMaskingEngine, Mockito.never()
		).redact(
			Mockito.anyLong(), Mockito.anyList(), Mockito.anyString()
		);
	}

	private final DataMaskingEngine _dataMaskingEngine = Mockito.mock(
		DataMaskingEngine.class);
	private final DataMaskingWriterInterceptor _dataMaskingWriterInterceptor =
		new DataMaskingWriterInterceptor();
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final WriterInterceptorContext _writerInterceptorContext =
		Mockito.mock(WriterInterceptorContext.class);

}