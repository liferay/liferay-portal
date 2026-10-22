/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.url;

import com.liferay.adaptive.media.AMURIResolver;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.internal.configuration.AMImageConfigurationEntryImpl;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.net.URI;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alejandro Tardín
 * @author Daniel Sanz
 */
@FeatureFlag("LPD-94784")
public class AMImageURLFactoryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpFileVersion();

		_setUpPortalUtil();

		_setUpPreviewURL(_ORIGINAL_URL);

		Mockito.when(
			_amURIResolver.resolveURI(Mockito.any(URI.class))
		).thenAnswer(
			invocation -> URI.create(_URI_PREFIX + invocation.getArguments()[0])
		);

		ReflectionTestUtil.setFieldValue(
			_amImageURLFactoryImpl, "_amURIResolver", _amURIResolver);
		ReflectionTestUtil.setFieldValue(
			_amImageURLFactoryImpl, "_dlURLHelper", _dlURLHelper);
		ReflectionTestUtil.setFieldValue(
			_amImageURLFactoryImpl, "_portal", _portal);
	}

	@Test
	public void testCreateFileEntryURLAdaptationURLParameters() {
		Assert.assertEquals(
			_appendQueryString(_QUERY_STRINGS[0], _ORIGINAL_URL),
			String.valueOf(
				_amImageURLFactoryImpl.createFileEntryURL(
					_fileVersion,
					_createAMImageConfigurationEntry(
						_QUERY_STRINGS[0], "true"))));

		Assert.assertEquals(
			_URI_PREFIX + _getFileEntryRelativeURL(),
			String.valueOf(
				_amImageURLFactoryImpl.createFileEntryURL(
					_fileVersion,
					_createAMImageConfigurationEntry(
						_QUERY_STRINGS[1], "false"))));
	}

	@Test
	public void testCreateFileEntryURLCompanyCDNHost() throws Exception {
		Mockito.when(
			_portal.getCDNHostHttp(_COMPANY_ID)
		).thenReturn(
			_CDN_HOST
		);

		Assert.assertEquals(
			_appendQueryString(_QUERY_STRINGS[1], _CDN_HOST + _ORIGINAL_URL),
			String.valueOf(
				_amImageURLFactoryImpl.createFileEntryURL(
					_fileVersion,
					_createAMImageConfigurationEntry(
						_QUERY_STRINGS[1], "true"))));

		Assert.assertEquals(
			_URI_PREFIX + _getFileEntryRelativeURL(),
			String.valueOf(
				_amImageURLFactoryImpl.createFileEntryURL(
					_fileVersion,
					_createAMImageConfigurationEntry(null, "false"))));

		Mockito.when(
			_portal.getCDNHostHttps(_COMPANY_ID)
		).thenReturn(
			_CDN_HOST + "/"
		);

		AMImageConfigurationEntry amImageConfigurationEntry =
			_createAMImageConfigurationEntry(_QUERY_STRINGS[0], "true");

		Assert.assertEquals(
			_appendQueryString(_QUERY_STRINGS[0], _CDN_HOST + _ORIGINAL_URL),
			String.valueOf(
				_amImageURLFactoryImpl.createFileEntryURL(
					_fileVersion, amImageConfigurationEntry)));

		String[] urls = {
			_addPreviewURLParameters(
				StringBundler.concat(
					"//", RandomTestUtil.randomString(), "/",
					RandomTestUtil.randomString())),
			_addPreviewURLParameters(
				StringBundler.concat(
					"http://", RandomTestUtil.randomString(), "/",
					RandomTestUtil.randomString())),
			_addPreviewURLParameters(
				StringBundler.concat(
					"https://", RandomTestUtil.randomString(), "/",
					RandomTestUtil.randomString()))
		};

		for (String url : urls) {
			_setUpPreviewURL(url);

			Assert.assertEquals(
				_appendQueryString(_QUERY_STRINGS[0], url),
				String.valueOf(
					_amImageURLFactoryImpl.createFileEntryURL(
						_fileVersion, amImageConfigurationEntry)));
		}
	}

	@FeatureFlag(enable = false, value = "LPD-94784")
	@Test
	public void testCreateFileEntryURLFeatureFlagDisabled() {
		Assert.assertEquals(
			_URI_PREFIX + _getFileEntryRelativeURL(),
			String.valueOf(
				_amImageURLFactoryImpl.createFileEntryURL(
					_fileVersion,
					_createAMImageConfigurationEntry(
						_QUERY_STRINGS[0], "true"))));
	}

	@Test
	public void testCreateFileVersionURL() throws Exception {
		Assert.assertEquals(
			_appendQueryString(_QUERY_STRINGS[0], _ORIGINAL_URL),
			String.valueOf(
				_amImageURLFactoryImpl.createFileVersionURL(
					_fileVersion,
					_createAMImageConfigurationEntry(
						_QUERY_STRINGS[0], "true"))));

		Mockito.verify(
			_dlURLHelper
		).getPreviewURL(
			Mockito.eq(_fileEntry), Mockito.eq(_fileVersion), Mockito.isNull(),
			Mockito.anyString(), Mockito.eq(true), Mockito.anyBoolean()
		);

		Assert.assertEquals(
			StringBundler.concat(
				_URI_PREFIX, "image/", _FILE_ENTRY_ID, "/", _FILE_VERSION_ID,
				"/", _UUID, "/", _FILE_NAME),
			String.valueOf(
				_amImageURLFactoryImpl.createFileVersionURL(
					_fileVersion,
					_createAMImageConfigurationEntry(null, null))));
	}

	private static String _addPreviewURLParameters(String url) {
		return StringBundler.concat(
			url, "?version=", RandomTestUtil.randomDouble(), "&t=",
			RandomTestUtil.randomLong());
	}

	private String _appendQueryString(String queryString, String url) {
		return StringBundler.concat(url, "&", queryString);
	}

	private AMImageConfigurationEntry _createAMImageConfigurationEntry(
		String adaptationURLParameters, String delegateAdaptation) {

		return new AMImageConfigurationEntryImpl(
			RandomTestUtil.randomString(), _UUID,
			HashMapBuilder.put(
				"adaptation-url-parameters", () -> adaptationURLParameters
			).put(
				"delegate-adaptation", () -> delegateAdaptation
			).build());
	}

	private String _getFileEntryRelativeURL() {
		return StringBundler.concat(
			"image/", _FILE_ENTRY_ID, "/", _UUID, "/", _FILE_NAME, "?t=",
			_modifiedDate.getTime());
	}

	private void _setUpFileVersion() throws Exception {
		Mockito.when(
			_fileVersion.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			_fileVersion.getFileEntry()
		).thenReturn(
			_fileEntry
		);

		Mockito.when(
			_fileVersion.getFileEntryId()
		).thenReturn(
			_FILE_ENTRY_ID
		);

		Mockito.when(
			_fileVersion.getFileName()
		).thenReturn(
			_FILE_NAME
		);

		Mockito.when(
			_fileVersion.getFileVersionId()
		).thenReturn(
			_FILE_VERSION_ID
		);

		Mockito.when(
			_fileVersion.getModifiedDate()
		).thenReturn(
			_modifiedDate
		);
	}

	private void _setUpPortalUtil() {
		Mockito.when(
			_portal.stripURLAnchor(Mockito.anyString(), Mockito.anyString())
		).thenAnswer(
			invocation -> new String[] {
				(String)invocation.getArguments()[0], ""
			}
		);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);
	}

	private void _setUpPreviewURL(String url) throws Exception {
		Mockito.when(
			_dlURLHelper.getPreviewURL(
				Mockito.eq(_fileEntry), Mockito.eq(_fileVersion),
				Mockito.isNull(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.anyBoolean())
		).thenReturn(
			url
		);
	}

	private static final String _CDN_HOST =
		"https://" + RandomTestUtil.randomString();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _FILE_ENTRY_ID = RandomTestUtil.randomLong();

	private static final String _FILE_NAME = RandomTestUtil.randomString();

	private static final long _FILE_VERSION_ID = RandomTestUtil.randomLong();

	private static final String _ORIGINAL_URL = _addPreviewURLParameters(
		"/" + RandomTestUtil.randomString());

	private static final String[] _QUERY_STRINGS = {
		StringBundler.concat(
			RandomTestUtil.randomString(), "=", RandomTestUtil.randomString()),
		StringBundler.concat(
			RandomTestUtil.randomString(), "=", RandomTestUtil.randomString(),
			"&", RandomTestUtil.randomString(), "=",
			RandomTestUtil.randomString())
	};

	private static final String _URI_PREFIX =
		RandomTestUtil.randomString() + "/";

	private static final String _UUID = RandomTestUtil.randomString();

	private final AMImageURLFactoryImpl _amImageURLFactoryImpl =
		new AMImageURLFactoryImpl();
	private final AMURIResolver _amURIResolver = Mockito.mock(
		AMURIResolver.class);
	private final DLURLHelper _dlURLHelper = Mockito.mock(DLURLHelper.class);
	private final FileEntry _fileEntry = Mockito.mock(FileEntry.class);
	private final FileVersion _fileVersion = Mockito.mock(FileVersion.class);
	private final Date _modifiedDate = new Date();
	private final Portal _portal = Mockito.mock(Portal.class);

}