/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.model.DSDocument;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 */
@RunWith(Arquillian.class)
public class DSEnvelopeManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_configurationProvider.saveCompanyConfiguration(
			DigitalSignatureConfiguration.class, TestPropsValues.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"accountBaseURI",
				TestPropsUtil.get("digital.signature.account.base.uri")
			).put(
				"apiAccountId",
				TestPropsUtil.get("digital.signature.api.accountId")
			).put(
				"apiUsername",
				TestPropsUtil.get("digital.signature.api.username")
			).put(
				"enabled", true
			).put(
				"integrationKey",
				TestPropsUtil.get("digital.signature.integration.key")
			).put(
				"rsaPrivateKey",
				TestPropsUtil.get("digital.signature.rsa.private.key")
			).put(
				"siteSettingsStrategy",
				TestPropsUtil.get("digital.signature.site.settings.strategy")
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_configurationProvider.saveCompanyConfiguration(
			DigitalSignatureConfiguration.class, TestPropsValues.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"accountBaseURI", ""
			).put(
				"apiAccountId", ""
			).put(
				"apiUsername", ""
			).put(
				"enabled", false
			).put(
				"integrationKey", ""
			).put(
				"rsaPrivateKey", ""
			).build());
	}

	@Test
	public void testAddDSEnvelope() throws Exception {
		DSEnvelope dsEnvelope = _dsEnvelopeManager.addDSEnvelope(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			new DSEnvelope() {
				{
					dsDocuments = Collections.singletonList(
						new DSDocument() {
							{
								data = Base64.encode(
									FileUtil.getBytes(
										getClass(),
										"dependencies/Document.pdf"));
								dsDocumentId = "1";
								name = RandomTestUtil.randomString();
							}
						});
					dsRecipients = Collections.singletonList(
						new DSRecipient() {
							{
								dsRecipientId = "1";
								emailAddress =
									RandomTestUtil.randomString() +
										"@liferay.com";
								name = RandomTestUtil.randomString();
							}
						});
					emailBlurb = RandomTestUtil.randomString();
					emailSubject = RandomTestUtil.randomString();
					name = RandomTestUtil.randomString();
					senderEmailAddress =
						RandomTestUtil.randomString() + "@liferay.com";
					status = "sent";
				}
			});

		Assert.assertTrue(Validator.isNotNull(dsEnvelope.getDSEnvelopeId()));

		_dsEnvelopeManager.deleteDSEnvelopes(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			dsEnvelope.getDSEnvelopeId());
	}

	@Test
	public void testGetDSEnvelope() throws Exception {
		String expectedEmailSubject = RandomTestUtil.randomString();

		DSEnvelope dsEnvelope = _dsEnvelopeManager.addDSEnvelope(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			new DSEnvelope() {
				{
					emailSubject = expectedEmailSubject;
					name = RandomTestUtil.randomString();
					status = "created";
				}
			});

		dsEnvelope = _dsEnvelopeManager.getDSEnvelope(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			dsEnvelope.getDSEnvelopeId());

		Assert.assertEquals(expectedEmailSubject, dsEnvelope.getEmailSubject());

		_dsEnvelopeManager.deleteDSEnvelopes(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			dsEnvelope.getDSEnvelopeId());
	}

	@Test
	public void testGetDSEnvelopesPage() throws Exception {

		// Set up

		DSEnvelope dsEnvelope1 = _dsEnvelopeManager.addDSEnvelope(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			new DSEnvelope() {
				{
					dsDocuments = Collections.singletonList(
						new DSDocument() {
							{
								data = Base64.encode(
									FileUtil.getBytes(
										getClass(),
										"dependencies/Document.pdf"));
								dsDocumentId = "1";
								name = RandomTestUtil.randomString();
							}
						});
					dsRecipients = Collections.singletonList(
						new DSRecipient() {
							{
								dsRecipientId = "1";
								emailAddress =
									RandomTestUtil.randomString() +
										"@liferay.com";
								name = RandomTestUtil.randomString();
							}
						});
					emailBlurb = RandomTestUtil.randomString();
					emailSubject = RandomTestUtil.randomString();
					name = RandomTestUtil.randomString();
					senderEmailAddress =
						RandomTestUtil.randomString() + "@liferay.com";
					status = "sent";
				}
			});

		DSEnvelope dsEnvelope2 = _dsEnvelopeManager.addDSEnvelope(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			new DSEnvelope() {
				{
					dsDocuments = Collections.singletonList(
						new DSDocument() {
							{
								data = Base64.encode(
									FileUtil.getBytes(
										getClass(),
										"dependencies/Document.pdf"));
								dsDocumentId = "1";
								name = RandomTestUtil.randomString();
							}
						});
					dsRecipients = Collections.singletonList(
						new DSRecipient() {
							{
								dsRecipientId = "1";
								emailAddress =
									RandomTestUtil.randomString() +
										"@liferay.com";
								name = RandomTestUtil.randomString();
							}
						});
					emailBlurb = RandomTestUtil.randomString();
					emailSubject = RandomTestUtil.randomString();
					name = dsEnvelope1.getName() + RandomTestUtil.randomInt();
					senderEmailAddress =
						RandomTestUtil.randomString() + "@liferay.com";
					status = "sent";
				}
			});

		// DocuSign search is asynchronous and may not be ready immediately
		// after adding envelopes

		IdempotentRetryAssert.retryAssert(
			10, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> _assertPage(
				dsEnvelope1.getName(), "asc", 2, "",
				dsEnvelopes -> {
				}));

		// Assert digital signature envelope ID

		_assertPage(
			dsEnvelope1.getDSEnvelopeId(), "desc", 1, "",
			dsEnvelopes -> _assertEquals(dsEnvelope1, dsEnvelopes.get(0)));

		// Assert digital signature recipient

		List<DSRecipient> dsRecipients = dsEnvelope1.getDSRecipients();

		DSRecipient dsRecipient = dsRecipients.get(0);

		_assertPage(
			dsRecipient.getEmailAddress(), "desc", 1, "",
			dsEnvelopes -> _assertEquals(dsEnvelope1, dsEnvelopes.get(0)));

		// Assert email subject

		_assertPage(
			dsEnvelope1.getEmailSubject(), "desc", 1, "",
			dsEnvelopes -> _assertEquals(dsEnvelope1, dsEnvelopes.get(0)));

		// Asert name and order

		_assertPage(
			dsEnvelope1.getName(), "asc", 2, "",
			dsEnvelopes -> {
				_assertEquals(dsEnvelope1, dsEnvelopes.get(0));
				_assertEquals(dsEnvelope2, dsEnvelopes.get(1));
			});
		_assertPage(
			dsEnvelope1.getName(), "desc", 2, "",
			dsEnvelopes -> {
				_assertEquals(dsEnvelope2, dsEnvelopes.get(0));
				_assertEquals(dsEnvelope1, dsEnvelopes.get(1));
			});
		_assertPage(
			dsEnvelope2.getName(), "desc", 1, "",
			dsEnvelopes -> _assertEquals(dsEnvelope2, dsEnvelopes.get(0)));

		// Assert sender email address

		_assertPage(
			dsEnvelope1.getSenderEmailAddress(), "desc", 1, "",
			dsEnvelopes -> _assertEquals(dsEnvelope1, dsEnvelopes.get(0)));

		// Clean up

		_dsEnvelopeManager.deleteDSEnvelopes(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			new String[] {
				dsEnvelope1.getDSEnvelopeId(), dsEnvelope2.getDSEnvelopeId()
			});
	}

	private void _assertEquals(
		DSEnvelope expectedDSEnvelope, DSEnvelope actualDSEnvelope) {

		Assert.assertEquals(
			expectedDSEnvelope.getCreatedLocalDateTime(),
			actualDSEnvelope.getCreatedLocalDateTime());
		Assert.assertEquals(
			expectedDSEnvelope.getDSEnvelopeId(),
			actualDSEnvelope.getDSEnvelopeId());
		Assert.assertEquals(
			expectedDSEnvelope.getEmailSubject(),
			actualDSEnvelope.getEmailSubject());
		Assert.assertEquals(
			expectedDSEnvelope.getName(), actualDSEnvelope.getName());
		Assert.assertEquals(
			expectedDSEnvelope.getSenderEmailAddress(),
			actualDSEnvelope.getSenderEmailAddress());
		Assert.assertTrue(
			StringUtil.equals(
				expectedDSEnvelope.getStatus(), actualDSEnvelope.getStatus()) ||
			StringUtil.equals("correct", actualDSEnvelope.getStatus()));
	}

	private Void _assertPage(
			String keywords, String order, int expectedTotalCount,
			String status,
			UnsafeConsumer<List<DSEnvelope>, Exception> unsafeConsumer)
		throws Exception {

		Page<DSEnvelope> page = _dsEnvelopeManager.getDSEnvelopesPage(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			"2021-01-01", true, keywords, order, Pagination.of(1, 2), status);

		Assert.assertEquals(expectedTotalCount, page.getTotalCount());

		List<DSEnvelope> dsEnvelopes = (List<DSEnvelope>)page.getItems();

		Assert.assertEquals(
			dsEnvelopes.toString(), expectedTotalCount, dsEnvelopes.size());

		unsafeConsumer.accept(dsEnvelopes);

		return null;
	}

	@Inject
	private static ConfigurationProvider _configurationProvider;

	@Inject
	private DSEnvelopeManager _dsEnvelopeManager;

}