/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.service;

import com.liferay.layout.content.creator.LayoutContentVersionCreator;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutLocalServiceWrapperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_originalLayoutContentVersionCreatorSnapshot =
			ReflectionTestUtil.getAndSetFieldValue(
				LayoutLocalServiceWrapper.class,
				"_layoutContentVersionCreatorSnapshot",
				_layoutContentVersionCreatorSnapshot);

		_originalLog = ReflectionTestUtil.getAndSetFieldValue(
			LayoutLocalServiceWrapper.class, "_log", _log);
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			LayoutLocalServiceWrapper.class,
			"_layoutContentVersionCreatorSnapshot",
			_originalLayoutContentVersionCreatorSnapshot);

		ReflectionTestUtil.setFieldValue(
			LayoutLocalServiceWrapper.class, "_log", _originalLog);
	}

	@Test
	@TestInfo("LPD-98095")
	public void testCreateLayoutContentVersion() {
		Layout sourceLayout = Mockito.mock(Layout.class);

		ReflectionTestUtil.invoke(
			_layoutLocalServiceWrapper, "_createLayoutContentVersion",
			new Class<?>[] {Layout.class}, sourceLayout);

		Mockito.verify(
			_log
		).isDebugEnabled();

		Mockito.verify(
			_log, Mockito.never()
		).debug(
			Mockito.anyString()
		);

		Mockito.when(
			_log.isDebugEnabled()
		).thenReturn(
			true
		);

		ReflectionTestUtil.invoke(
			_layoutLocalServiceWrapper, "_createLayoutContentVersion",
			new Class<?>[] {Layout.class}, sourceLayout);

		Mockito.verify(
			_log
		).debug(
			"Layout content version creator is null"
		);

		Mockito.reset(_layoutContentVersionCreatorSnapshot, _log);

		LayoutContentVersionCreator layoutContentVersionCreator = Mockito.mock(
			LayoutContentVersionCreator.class);

		Mockito.when(
			_layoutContentVersionCreatorSnapshot.get()
		).thenReturn(
			layoutContentVersionCreator
		);

		ReflectionTestUtil.invoke(
			_layoutLocalServiceWrapper, "_createLayoutContentVersion",
			new Class<?>[] {Layout.class}, sourceLayout);

		Mockito.verify(
			layoutContentVersionCreator
		).createLayoutContentVersion(
			sourceLayout
		);

		Mockito.verify(
			_log, Mockito.never()
		).isDebugEnabled();

		Mockito.verify(
			_log, Mockito.never()
		).debug(
			Mockito.anyString()
		);
	}

	private static final Log _log = Mockito.mock(Log.class);

	private final Snapshot<LayoutContentVersionCreator>
		_layoutContentVersionCreatorSnapshot = Mockito.mock(Snapshot.class);
	private final LayoutLocalServiceWrapper _layoutLocalServiceWrapper =
		new LayoutLocalServiceWrapper();
	private Object _originalLayoutContentVersionCreatorSnapshot;
	private Object _originalLog;

}