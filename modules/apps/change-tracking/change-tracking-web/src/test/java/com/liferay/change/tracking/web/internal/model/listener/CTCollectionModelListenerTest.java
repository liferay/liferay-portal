/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.model.listener;

import com.liferay.change.tracking.model.CTRemote;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Caio Farias
 */
public class CTCollectionModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_secretResolverSnapshot = ReflectionTestUtil.getAndSetFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			new Snapshot<SecretResolver>(
				SecretResolverUtil.class, SecretResolver.class) {

				@Override
				public SecretResolver get() {
					return _secretResolver;
				}

			});
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			_secretResolverSnapshot);
	}

	@Test
	public void testGetToken() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		long ctRemoteId = RandomTestUtil.randomLong();

		String keyReferenceString = _getKeyReferenceString(
			companyId, ctRemoteId);

		String clientSecret = RandomTestUtil.randomString();

		_secretResolver = Mockito.mock(SecretResolver.class);

		Mockito.when(
			_secretResolver.resolve(companyId, keyReferenceString)
		).thenReturn(
			clientSecret
		);

		_testGetToken(keyReferenceString, companyId, ctRemoteId, clientSecret);

		keyReferenceString = _getKeyReferenceString(companyId, ctRemoteId + 1);

		_testGetToken(
			keyReferenceString, companyId, ctRemoteId, keyReferenceString);
	}

	private String _getKeyReferenceString(long companyId, long ctRemoteId) {
		return KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				StringBundler.concat(
					CTRemote.class.getSimpleName(), StringPool.SLASH, companyId,
					StringPool.SLASH, ctRemoteId),
				RandomTestUtil.randomString(), KeyReference.Type.SECRET));
	}

	private void _testGetToken(
			String clientSecret, long companyId, long ctRemoteId,
			String expectedClientSecret)
		throws Exception {

		CTRemote ctRemote = Mockito.mock(CTRemote.class);

		Mockito.when(
			ctRemote.getClientSecret()
		).thenReturn(
			clientSecret
		);

		Mockito.when(
			ctRemote.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			ctRemote.getCtRemoteId()
		).thenReturn(
			ctRemoteId
		);

		CTCollectionModelListener ctCollectionModelListener =
			new CTCollectionModelListener();

		Http http = Mockito.mock(Http.class);

		Mockito.when(
			http.URLtoString(Mockito.any(Http.Options.class))
		).thenReturn(
			"{}"
		);

		ReflectionTestUtil.setFieldValue(
			ctCollectionModelListener, "_http", http);

		ReflectionTestUtil.setFieldValue(
			ctCollectionModelListener, "_jsonFactory", new JSONFactoryImpl());

		ReflectionTestUtil.invoke(
			ctCollectionModelListener, "_getToken",
			new Class<?>[] {CTRemote.class}, ctRemote);

		ArgumentCaptor<Http.Options> argumentCaptor = ArgumentCaptor.forClass(
			Http.Options.class);

		Mockito.verify(
			http
		).URLtoString(
			argumentCaptor.capture()
		);

		Http.Options options = argumentCaptor.getValue();

		Map<String, String> parts = options.getParts();

		Assert.assertEquals(expectedClientSecret, parts.get("client_secret"));
	}

	private SecretResolver _secretResolver;
	private Snapshot<SecretResolver> _secretResolverSnapshot;

}