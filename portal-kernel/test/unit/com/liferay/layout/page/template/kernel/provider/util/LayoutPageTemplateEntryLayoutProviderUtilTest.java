/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.kernel.provider.util;

import com.liferay.layout.page.template.kernel.provider.LayoutPageTemplateEntryLayoutProvider;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutPageTemplateEntryLayoutProviderUtilTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Before
	public void setUp() {
		_originalLayoutPageTemplateEntryLayoutProviderSnapshot =
			ReflectionTestUtil.getAndSetFieldValue(
				LayoutPageTemplateEntryLayoutProviderUtil.class,
				"_layoutPageTemplateEntryLayoutProviderSnapshot",
				_layoutPageTemplateEntryLayoutProviderSnapshot);

		_originalLog = ReflectionTestUtil.getAndSetFieldValue(
			LayoutPageTemplateEntryLayoutProviderUtil.class, "_log", _log);
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			LayoutPageTemplateEntryLayoutProviderUtil.class,
			"_layoutPageTemplateEntryLayoutProviderSnapshot",
			_originalLayoutPageTemplateEntryLayoutProviderSnapshot);

		ReflectionTestUtil.setFieldValue(
			LayoutPageTemplateEntryLayoutProviderUtil.class, "_log",
			_originalLog);
	}

	@Test
	public void testConstructor() {
		new LayoutPageTemplateEntryLayoutProviderUtil();
	}

	@Test
	@TestInfo("LPD-99345")
	public void testGetLayoutPageTemplateEntryLayout() throws Throwable {
		long groupId = RandomTestUtil.randomLong();
		String externalReferenceCode = RandomTestUtil.randomString();
		long plid = RandomTestUtil.randomLong();

		Layout layout = Mockito.mock(Layout.class);

		LayoutPageTemplateEntryLayoutProvider
			layoutPageTemplateEntryLayoutProvider = Mockito.mock(
				LayoutPageTemplateEntryLayoutProvider.class);

		Mockito.when(
			layoutPageTemplateEntryLayoutProvider.
				getLayoutPageTemplateEntryLayout(
					groupId, externalReferenceCode, plid)
		).thenReturn(
			layout
		);

		Mockito.when(
			_layoutPageTemplateEntryLayoutProviderSnapshot.get()
		).thenReturn(
			layoutPageTemplateEntryLayoutProvider
		);

		Assert.assertSame(
			layout,
			LayoutPageTemplateEntryLayoutProviderUtil.
				getLayoutPageTemplateEntryLayout(
					groupId, externalReferenceCode, plid));

		_testLogsDebugMessageWhenProviderIsUnavailable(
			() ->
				LayoutPageTemplateEntryLayoutProviderUtil.
					getLayoutPageTemplateEntryLayout(
						groupId, externalReferenceCode, plid));
	}

	@Test
	@TestInfo("LPD-99345")
	public void testGetLayoutPageTemplateEntryLayoutPrototype()
		throws Throwable {

		long companyId = RandomTestUtil.randomLong();
		String externalReferenceCode = RandomTestUtil.randomString();
		String layoutPageTemplateEntryScopeERC = RandomTestUtil.randomString();
		long scopeGroupId = RandomTestUtil.randomLong();

		LayoutPrototype layoutPrototype = Mockito.mock(LayoutPrototype.class);

		LayoutPageTemplateEntryLayoutProvider
			layoutPageTemplateEntryLayoutProvider = Mockito.mock(
				LayoutPageTemplateEntryLayoutProvider.class);

		Mockito.when(
			layoutPageTemplateEntryLayoutProvider.
				getLayoutPageTemplateEntryLayoutPrototype(
					companyId, externalReferenceCode,
					layoutPageTemplateEntryScopeERC, scopeGroupId)
		).thenReturn(
			layoutPrototype
		);

		Mockito.when(
			_layoutPageTemplateEntryLayoutProviderSnapshot.get()
		).thenReturn(
			layoutPageTemplateEntryLayoutProvider
		);

		Assert.assertSame(
			layoutPrototype,
			LayoutPageTemplateEntryLayoutProviderUtil.
				getLayoutPageTemplateEntryLayoutPrototype(
					companyId, externalReferenceCode,
					layoutPageTemplateEntryScopeERC, scopeGroupId));

		_testLogsDebugMessageWhenProviderIsUnavailable(
			() ->
				LayoutPageTemplateEntryLayoutProviderUtil.
					getLayoutPageTemplateEntryLayoutPrototype(
						companyId, externalReferenceCode,
						layoutPageTemplateEntryScopeERC, scopeGroupId));
	}

	private <T> void _testLogsDebugMessageWhenProviderIsUnavailable(
			UnsafeSupplier<T, Exception> unsafeSupplier)
		throws Throwable {

		Mockito.reset(_log, _layoutPageTemplateEntryLayoutProviderSnapshot);

		Assert.assertNull(unsafeSupplier.get());

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

		Assert.assertNull(unsafeSupplier.get());

		Mockito.verify(
			_log
		).debug(
			"Layout page template entry layout provider is null"
		);
	}

	private static final Log _log = Mockito.mock(Log.class);

	private final Snapshot<LayoutPageTemplateEntryLayoutProvider>
		_layoutPageTemplateEntryLayoutProviderSnapshot = Mockito.mock(
			Snapshot.class);
	private Object _originalLayoutPageTemplateEntryLayoutProviderSnapshot;
	private Object _originalLog;

}