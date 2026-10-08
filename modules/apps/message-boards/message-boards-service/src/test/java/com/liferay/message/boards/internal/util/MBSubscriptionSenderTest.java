/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.util;

import com.liferay.mail.kernel.model.SMTPAccount;
import com.liferay.message.boards.model.MBMailingList;
import com.liferay.message.boards.service.MBMailingListLocalService;
import com.liferay.message.boards.service.MBMailingListLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jorge García Jiménez
 */
public class MBSubscriptionSenderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_mbMailingListLocalServiceSnapshot =
			ReflectionTestUtil.getAndSetFieldValue(
				MBMailingListLocalServiceUtil.class, "_serviceSnapshot",
				new Snapshot<MBMailingListLocalService>(
					MBMailingListLocalServiceUtil.class,
					MBMailingListLocalService.class) {

					@Override
					public MBMailingListLocalService get() {
						return _mbMailingListLocalService;
					}

				});
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
			MBMailingListLocalServiceUtil.class, "_serviceSnapshot",
			_mbMailingListLocalServiceSnapshot);
		ReflectionTestUtil.setFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			_secretResolverSnapshot);
	}

	@Test
	public void testAddMailingListSubscriber() {
		MBMailingList mbMailingList = Mockito.mock(MBMailingList.class);

		long categoryId = RandomTestUtil.randomLong();

		Mockito.when(
			mbMailingList.getCategoryId()
		).thenReturn(
			categoryId
		);

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			mbMailingList.getCompanyId()
		).thenReturn(
			companyId
		);

		long mailingListId = RandomTestUtil.randomLong();

		Mockito.when(
			mbMailingList.getMailingListId()
		).thenReturn(
			mailingListId
		);

		String keyReferenceString = _getKeyReferenceString(
			categoryId, companyId);

		Mockito.when(
			mbMailingList.getOutPassword()
		).thenReturn(
			keyReferenceString
		);

		Mockito.when(
			mbMailingList.isActive()
		).thenReturn(
			true
		);

		Mockito.when(
			mbMailingList.isOutCustom()
		).thenReturn(
			true
		);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			_mbMailingListLocalService.fetchCategoryMailingList(
				groupId, categoryId)
		).thenReturn(
			mbMailingList
		);

		String password = RandomTestUtil.randomString();

		Mockito.when(
			_secretResolver.resolve(companyId, keyReferenceString)
		).thenReturn(
			password
		);

		MBSubscriptionSender mbSubscriptionSender = new MBSubscriptionSender();

		mbSubscriptionSender.addMailingListSubscriber(groupId, categoryId);

		SMTPAccount smtpAccount = ReflectionTestUtil.getFieldValue(
			mbSubscriptionSender, "smtpAccount");

		Assert.assertEquals(password, smtpAccount.getPassword());

		Assert.assertTrue(mbSubscriptionSender.hasSubscribers());

		Mockito.when(
			_secretResolver.resolve(companyId, keyReferenceString)
		).thenAnswer(
			invocation -> {
				throw new SecretException();
			}
		);

		mbSubscriptionSender = new MBSubscriptionSender();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				MBSubscriptionSender.class.getName(), LoggerTestUtil.ERROR)) {

			mbSubscriptionSender.addMailingListSubscriber(groupId, categoryId);

			List<String> messages = logCapture.getMessages();

			Assert.assertEquals(messages.toString(), 1, messages.size());

			Assert.assertEquals(
				"Unable to resolve the outgoing mail server credential for " +
					"mailing list ID " + mailingListId,
				messages.get(0));
		}

		Assert.assertFalse(mbSubscriptionSender.hasSubscribers());
	}

	private String _getKeyReferenceString(long categoryId, long companyId) {
		return KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				StringBundler.concat(
					MBMailingList.class.getSimpleName(), StringPool.SLASH,
					companyId, StringPool.SLASH, categoryId, "/outPassword"),
				RandomTestUtil.randomString(), KeyReference.Type.SECRET));
	}

	private final MBMailingListLocalService _mbMailingListLocalService =
		Mockito.mock(MBMailingListLocalService.class);
	private Snapshot<MBMailingListLocalService>
		_mbMailingListLocalServiceSnapshot;
	private final SecretResolver _secretResolver = Mockito.mock(
		SecretResolver.class);
	private Snapshot<SecretResolver> _secretResolverSnapshot;

}