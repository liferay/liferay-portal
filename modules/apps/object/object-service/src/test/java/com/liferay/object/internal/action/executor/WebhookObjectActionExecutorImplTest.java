/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.action.executor;

import com.liferay.object.model.ObjectAction;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
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
public class WebhookObjectActionExecutorImplTest {

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
	public void testExecute() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		long objectActionId = RandomTestUtil.randomLong();

		String keyReferenceString = _getKeyReferenceString(
			companyId, objectActionId);

		String secret = RandomTestUtil.randomString();

		Mockito.when(
			_secretResolver.resolve(companyId, keyReferenceString)
		).thenReturn(
			secret
		);

		_testExecute(companyId, secret, objectActionId, keyReferenceString);

		keyReferenceString = _getKeyReferenceString(
			companyId, objectActionId + 1);

		_testExecute(
			companyId, keyReferenceString, objectActionId, keyReferenceString);
	}

	private String _getKeyReferenceString(long companyId, long objectActionId) {
		return KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				StringBundler.concat(
					ObjectAction.class.getSimpleName(), StringPool.SLASH,
					companyId, StringPool.SLASH, objectActionId, "/secret"),
				RandomTestUtil.randomString(), KeyReference.Type.SECRET));
	}

	private void _testExecute(
			long companyId, String expectedSecret, long objectActionId,
			String secret)
		throws Exception {

		WebhookObjectActionExecutorImpl webhookObjectActionExecutorImpl =
			new WebhookObjectActionExecutorImpl();

		Http http = Mockito.mock(Http.class);

		ReflectionTestUtil.setFieldValue(
			webhookObjectActionExecutorImpl, "_http", http);

		webhookObjectActionExecutorImpl.execute(
			companyId, objectActionId,
			UnicodePropertiesBuilder.put(
				"secret", secret
			).put(
				"url", "https://" + RandomTestUtil.randomString()
			).put(
				"urlLocalNetworkAccessEnabled", "true"
			).build(),
			new JSONObjectImpl(), RandomTestUtil.randomLong());

		ArgumentCaptor<Http.Options> argumentCaptor = ArgumentCaptor.forClass(
			Http.Options.class);

		Mockito.verify(
			http
		).URLtoString(
			argumentCaptor.capture()
		);

		Http.Options options = argumentCaptor.getValue();

		Map<String, String> headers = options.getHeaders();

		Assert.assertEquals(expectedSecret, headers.get("x-api-key"));
	}

	private final SecretResolver _secretResolver = Mockito.mock(
		SecretResolver.class);
	private Snapshot<SecretResolver> _secretResolverSnapshot;

}