/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.security.audit.constants.AuditEventConstants;
import com.liferay.portal.security.audit.storage.exception.AuditEventPseudonymFieldValueException;
import com.liferay.portal.security.audit.storage.model.AuditEventPseudonymField;
import com.liferay.portal.security.audit.storage.service.AuditEventPseudonymFieldLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class AuditEventPseudonymFieldLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.security.audit.storage.service"));

	@Test
	public void testAddAuditEventPseudonymField() throws Exception {
		_testAddAuditEventPseudonymField(
			AuditEventConstants.CONTEXT_NAME_INSTANCE);
		_testAddAuditEventPseudonymField(StringPool.BLANK);
		_testAddAuditEventPseudonymField(null);

		String contextName = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		AuditEventPseudonymField auditEventPseudonymField =
			_auditEventPseudonymFieldLocalService.addAuditEventPseudonymField(
				TestPropsValues.getCompanyId(), contextName, _NAME, value);

		Assert.assertEquals(
			contextName, auditEventPseudonymField.getContextName());

		AuditEventPseudonymField curAuditEventPseudonymField =
			_auditEventPseudonymFieldLocalService.addAuditEventPseudonymField(
				TestPropsValues.getCompanyId(), contextName, _NAME, value);

		Assert.assertEquals(
			auditEventPseudonymField.getAuditEventPseudonymFieldId(),
			curAuditEventPseudonymField.getAuditEventPseudonymFieldId());

		_addAuditEventPseudonymField(value);

		DynamicQuery dynamicQuery =
			_auditEventPseudonymFieldLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"companyId", TestPropsValues.getCompanyId()));
		dynamicQuery.add(RestrictionsFactoryUtil.eq("name", _NAME));
		dynamicQuery.add(RestrictionsFactoryUtil.eq("value", value));

		Assert.assertEquals(
			2,
			_auditEventPseudonymFieldLocalService.dynamicQueryCount(
				dynamicQuery));

		_testAddAuditEventPseudonymFieldWithInvalidValue(
			"Maximum length of value exceeded",
			RandomTestUtil.randomString(256));
		_testAddAuditEventPseudonymFieldWithInvalidValue(
			"Value is blank", StringPool.BLANK);
		_testAddAuditEventPseudonymFieldWithInvalidValue(
			"Value is blank", null);

		_testAddAuditEventPseudonymFieldWithSimilarValues("CASEY");
		_testAddAuditEventPseudonymFieldWithSimilarValues("Casey");
		_testAddAuditEventPseudonymFieldWithSimilarValues("Jose");
		_testAddAuditEventPseudonymFieldWithSimilarValues("José");
		_testAddAuditEventPseudonymFieldWithSimilarValues("casey");
		_testAddAuditEventPseudonymFieldWithSimilarValues("casey ");
	}

	private AuditEventPseudonymField _addAuditEventPseudonymField(String value)
		throws Exception {

		return _auditEventPseudonymFieldLocalService.
			addAuditEventPseudonymField(
				TestPropsValues.getCompanyId(), null, _NAME, value);
	}

	private void _testAddAuditEventPseudonymField(String contextName)
		throws Exception {

		String value = RandomTestUtil.randomString();

		AuditEventPseudonymField auditEventPseudonymField =
			_auditEventPseudonymFieldLocalService.addAuditEventPseudonymField(
				TestPropsValues.getCompanyId(), contextName, _NAME, value);

		Assert.assertEquals(
			AuditEventConstants.CONTEXT_NAME_INSTANCE,
			auditEventPseudonymField.getContextName());
		Assert.assertEquals(
			DigesterUtil.digestHex(DigesterUtil.SHA_256, value),
			auditEventPseudonymField.getValueHash());

		AuditEventPseudonymField curAuditEventPseudonymField =
			_addAuditEventPseudonymField(value);

		Assert.assertEquals(
			auditEventPseudonymField.getAuditEventPseudonymFieldId(),
			curAuditEventPseudonymField.getAuditEventPseudonymFieldId());
	}

	private void _testAddAuditEventPseudonymFieldWithInvalidValue(
		String expectedMessage, String value) {

		AssertUtils.assertFailure(
			AuditEventPseudonymFieldValueException.class, expectedMessage,
			() -> _addAuditEventPseudonymField(value));
	}

	private void _testAddAuditEventPseudonymFieldWithSimilarValues(String value)
		throws Exception {

		AuditEventPseudonymField auditEventPseudonymField =
			_addAuditEventPseudonymField(value);

		Assert.assertEquals(value, auditEventPseudonymField.getValue());
	}

	private static final String _NAME = RandomTestUtil.randomString();

	@Inject
	private AuditEventPseudonymFieldLocalService
		_auditEventPseudonymFieldLocalService;

}