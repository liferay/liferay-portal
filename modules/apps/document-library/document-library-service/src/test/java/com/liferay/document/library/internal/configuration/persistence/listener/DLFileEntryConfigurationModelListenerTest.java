/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.configuration.persistence.listener;

import com.liferay.document.library.configuration.DLFileEntryConfigurationProvider;
import com.liferay.document.library.constants.DLFileEntryConfigurationConstants;
import com.liferay.document.library.internal.util.DLFileEntryConfigurationModelListenerThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class DLFileEntryConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_dlFileEntryConfigurationModelListener,
			"_dlFileEntryConfigurationProvider",
			_dlFileEntryConfigurationProvider);
		ReflectionTestUtil.setFieldValue(
			_dlFileEntryConfigurationModelListener, "_language", _language);
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testOnBeforeSaveWithCompanyMaxNumberOfPagesGreaterThanSystemValue()
		throws Exception {

		Mockito.when(
			_dlFileEntryConfigurationProvider.getMaxNumberOfPagesLimit(
				ExtendedObjectClassDefinition.Scope.COMPANY, _COMPANY_ID)
		).thenReturn(
			10
		);

		_dlFileEntryConfigurationModelListener.onBeforeSave(
			RandomTestUtil.randomString(),
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", _COMPANY_ID
			).put(
				"maxNumberOfPages", 20
			).put(
				"previewableProcessorMaxSize", 1000L
			).build());
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testOnBeforeSaveWithCompanyPreviewableProcessorMaxSizeGreaterThanSystemValue()
		throws Exception {

		Mockito.when(
			_dlFileEntryConfigurationProvider.getMaxNumberOfPagesLimit(
				ExtendedObjectClassDefinition.Scope.COMPANY, _COMPANY_ID)
		).thenReturn(
			10
		);

		Mockito.when(
			_dlFileEntryConfigurationProvider.
				getPreviewableProcessorMaxSizeLimit(
					ExtendedObjectClassDefinition.Scope.COMPANY, _COMPANY_ID)
		).thenReturn(
			1000L
		);

		_dlFileEntryConfigurationModelListener.onBeforeSave(
			RandomTestUtil.randomString(),
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", _COMPANY_ID
			).put(
				"maxNumberOfPages", 5
			).put(
				"previewableProcessorMaxSize", 2000L
			).build());
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testOnBeforeSaveWithCompanyUnlimitedMaxNumberOfPages()
		throws Exception {

		Mockito.when(
			_dlFileEntryConfigurationProvider.getMaxNumberOfPagesLimit(
				ExtendedObjectClassDefinition.Scope.COMPANY, _COMPANY_ID)
		).thenReturn(
			5
		);

		_dlFileEntryConfigurationModelListener.onBeforeSave(
			RandomTestUtil.randomString(),
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", _COMPANY_ID
			).put(
				"maxNumberOfPages",
				DLFileEntryConfigurationConstants.MAX_NUMBER_OF_PAGES_UNLIMITED
			).put(
				"previewableProcessorMaxSize", 1000L
			).build());
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testOnBeforeSaveWithCompanyUnlimitedPreviewableProcessorMaxSize()
		throws Exception {

		Mockito.when(
			_dlFileEntryConfigurationProvider.getMaxNumberOfPagesLimit(
				ExtendedObjectClassDefinition.Scope.COMPANY, _COMPANY_ID)
		).thenReturn(
			10
		);

		Mockito.when(
			_dlFileEntryConfigurationProvider.
				getPreviewableProcessorMaxSizeLimit(
					ExtendedObjectClassDefinition.Scope.COMPANY, _COMPANY_ID)
		).thenReturn(
			1000L
		);

		_dlFileEntryConfigurationModelListener.onBeforeSave(
			RandomTestUtil.randomString(),
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", _COMPANY_ID
			).put(
				"maxNumberOfPages", 5
			).put(
				"previewableProcessorMaxSize",
				DLFileEntryConfigurationConstants.
					PREVIEWABLE_PROCESSOR_MAX_SIZE_UNLIMITED
			).build());
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testOnBeforeSaveWithGroupMaxNumberOfPagesGreaterThanCompanyValue()
		throws Exception {

		Mockito.when(
			_dlFileEntryConfigurationProvider.getMaxNumberOfPagesLimit(
				ExtendedObjectClassDefinition.Scope.GROUP, _GROUP_ID)
		).thenReturn(
			5
		);

		_dlFileEntryConfigurationModelListener.onBeforeSave(
			RandomTestUtil.randomString(),
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", _COMPANY_ID
			).put(
				"groupId", _GROUP_ID
			).put(
				"maxNumberOfPages", 8
			).put(
				"previewableProcessorMaxSize", 10L
			).build());
	}

	@Test
	public void testOnBeforeSaveWithSystemScope() throws Exception {
		_dlFileEntryConfigurationModelListener.onBeforeSave(
			RandomTestUtil.randomString(),
			HashMapDictionaryBuilder.<String, Object>put(
				"maxNumberOfPages", 20
			).put(
				"previewableProcessorMaxSize", 2000L
			).build());
	}

	@Test
	public void testOnBeforeSaveWithValidationDisabled() throws Exception {
		try (SafeCloseable safeCloseable =
				DLFileEntryConfigurationModelListenerThreadLocal.
					setValidationEnabledWithSafeCloseable(false)) {

			_dlFileEntryConfigurationModelListener.onBeforeSave(
				RandomTestUtil.randomString(),
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", _COMPANY_ID
				).put(
					"maxNumberOfPages", 20
				).put(
					"previewableProcessorMaxSize", 1000L
				).build());
		}
	}

	@Test
	public void testOnBeforeSaveWithValidCompanyValues() throws Exception {
		Mockito.when(
			_dlFileEntryConfigurationProvider.getMaxNumberOfPagesLimit(
				ExtendedObjectClassDefinition.Scope.COMPANY, _COMPANY_ID)
		).thenReturn(
			10
		);

		Mockito.when(
			_dlFileEntryConfigurationProvider.
				getPreviewableProcessorMaxSizeLimit(
					ExtendedObjectClassDefinition.Scope.COMPANY, _COMPANY_ID)
		).thenReturn(
			1000L
		);

		_dlFileEntryConfigurationModelListener.onBeforeSave(
			RandomTestUtil.randomString(),
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", _COMPANY_ID
			).put(
				"maxNumberOfPages", 5
			).put(
				"previewableProcessorMaxSize", 500L
			).build());
	}

	@Test
	public void testOnBeforeSaveWithValidGroupValues() throws Exception {
		Mockito.when(
			_dlFileEntryConfigurationProvider.getMaxNumberOfPagesLimit(
				ExtendedObjectClassDefinition.Scope.GROUP, _GROUP_ID)
		).thenReturn(
			10
		);

		Mockito.when(
			_dlFileEntryConfigurationProvider.
				getPreviewableProcessorMaxSizeLimit(
					ExtendedObjectClassDefinition.Scope.GROUP, _GROUP_ID)
		).thenReturn(
			1000L
		);

		_dlFileEntryConfigurationModelListener.onBeforeSave(
			RandomTestUtil.randomString(),
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", _COMPANY_ID
			).put(
				"groupId", _GROUP_ID
			).put(
				"maxNumberOfPages", 5
			).put(
				"previewableProcessorMaxSize", 500L
			).build());
	}

	private static final long _COMPANY_ID = 12345L;

	private static final long _GROUP_ID = 67890L;

	private final DLFileEntryConfigurationModelListener
		_dlFileEntryConfigurationModelListener =
			new DLFileEntryConfigurationModelListener();
	private final DLFileEntryConfigurationProvider
		_dlFileEntryConfigurationProvider = Mockito.mock(
			DLFileEntryConfigurationProvider.class);
	private final Language _language = Mockito.mock(Language.class);

}