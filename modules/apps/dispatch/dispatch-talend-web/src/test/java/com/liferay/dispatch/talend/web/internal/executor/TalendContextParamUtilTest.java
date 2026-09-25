/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.talend.web.internal.executor;

import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Caio Farias
 */
public class TalendContextParamUtilTest {

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
	public void testGetValue() {
		long companyId = RandomTestUtil.randomLong();
		long dispatchTriggerId = RandomTestUtil.randomLong();
		String name = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		_secretResolver = Mockito.mock(SecretResolver.class);

		Mockito.when(
			_secretResolver.resolve(Mockito.eq(companyId), Mockito.anyString())
		).thenReturn(
			value
		);

		Assert.assertEquals(
			value,
			TalendContextParamUtil.getValue(
				companyId, dispatchTriggerId, name,
				_getKeyReferenceString(companyId, dispatchTriggerId, name)));
		Assert.assertEquals(
			value,
			TalendContextParamUtil.getValue(
				companyId, dispatchTriggerId, name, value));

		String keyReferenceString = _getKeyReferenceString(
			companyId, dispatchTriggerId, "JAVA_OPTS");

		Assert.assertEquals(
			keyReferenceString,
			TalendContextParamUtil.getValue(
				companyId, dispatchTriggerId, "JAVA_OPTS", keyReferenceString));

		keyReferenceString = _getKeyReferenceString(
			companyId + 1, dispatchTriggerId, name);

		Assert.assertEquals(
			keyReferenceString,
			TalendContextParamUtil.getValue(
				companyId, dispatchTriggerId, name, keyReferenceString));

		keyReferenceString = _getKeyReferenceString(
			companyId, dispatchTriggerId + 1, name);

		Assert.assertEquals(
			keyReferenceString,
			TalendContextParamUtil.getValue(
				companyId, dispatchTriggerId, name, keyReferenceString));

		keyReferenceString = _getKeyReferenceString(
			companyId, dispatchTriggerId, RandomTestUtil.randomString());

		Assert.assertEquals(
			keyReferenceString,
			TalendContextParamUtil.getValue(
				companyId, dispatchTriggerId, name, keyReferenceString));
	}

	private String _getKeyReferenceString(
		long companyId, long dispatchTriggerId, String name) {

		return KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				StringBundler.concat(
					DispatchTrigger.class.getSimpleName(), StringPool.SLASH,
					companyId, StringPool.SLASH, dispatchTriggerId,
					StringPool.SLASH, name),
				RandomTestUtil.randomString(), KeyReference.Type.SECRET));
	}

	private SecretResolver _secretResolver;
	private Snapshot<SecretResolver> _secretResolverSnapshot;

}