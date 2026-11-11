/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.settings.PortletPreferencesSettings;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener.test.BaseModelListenerTestCase;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Lucas Miranda
 */
@RunWith(Arquillian.class)
public class AuditPortletPreferencesLocalServiceWrapperTest
	extends BaseModelListenerTestCase {

	@Test
	public void testUpdatePreferences() throws Exception {
		String portletId = RandomTestUtil.randomString();

		PortletPreferencesSettings portletPreferencesSettings =
			new PortletPreferencesSettings(
				_portletPreferencesLocalService.getStrictPreferences(
					TestPropsValues.getCompanyId(),
					TestPropsValues.getCompanyId(),
					PortletKeys.PREFS_OWNER_TYPE_COMPANY, 0, portletId));

		String clientSecret = RandomTestUtil.randomString();

		portletPreferencesSettings.setValue("clientSecret", clientSecret);

		portletPreferencesSettings.store();

		_portletPreferences =
			_portletPreferencesLocalService.fetchPortletPreferences(
				TestPropsValues.getCompanyId(),
				PortletKeys.PREFS_OWNER_TYPE_COMPANY, 0, portletId);

		Assert.assertEquals(auditMessages.toString(), 1, auditMessages.size());

		AuditMessage auditMessage = fetchAuditMessage(
			PortletPreferences.class.getName(), EventTypes.UPDATE);

		Assert.assertEquals(
			TestPropsValues.getCompanyId(), auditMessage.getCompanyId());

		String additionalInfoJSON = JSONUtil.put(
			"attributes",
			JSONUtil.putAll(
				JSONUtil.put(
					"name", "clientSecret"
				).put(
					"newValue", ""
				).put(
					"oldValue", ""
				))
		).put(
			"portletId", portletId
		).toString();

		JSONAssert.assertEquals(
			additionalInfoJSON,
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.NON_EXTENSIBLE);

		Assert.assertFalse(
			String.valueOf(
				auditMessage.toJSONObject()
			).contains(
				clientSecret
			));

		portletPreferencesSettings.store();

		Assert.assertEquals(auditMessages.toString(), 1, auditMessages.size());

		String xml = StringBundler.concat(
			"<portlet-preferences><preference><name>clientSecret</name>",
			"<value>", RandomTestUtil.randomString(),
			"</value></preference></portlet-preferences>");

		_portletPreferencesLocalService.updatePreferences(
			TestPropsValues.getCompanyId(),
			PortletKeys.PREFS_OWNER_TYPE_COMPANY, 0, portletId, xml);

		Assert.assertEquals(auditMessages.toString(), 2, auditMessages.size());

		auditMessage = auditMessages.get(1);

		JSONAssert.assertEquals(
			additionalInfoJSON,
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.NON_EXTENSIBLE);

		_portletPreferencesLocalService.deletePortletPreferences(
			TestPropsValues.getCompanyId(),
			PortletKeys.PREFS_OWNER_TYPE_COMPANY, 0, portletId);

		Assert.assertEquals(auditMessages.toString(), 3, auditMessages.size());

		auditMessage = auditMessages.get(2);

		JSONAssert.assertEquals(
			additionalInfoJSON,
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.NON_EXTENSIBLE);

		_portletPreferencesLocalService.updatePreferences(
			TestPropsValues.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
			PortletKeys.PREFS_PLID_SHARED, portletId, xml);

		_portletPreferencesLocalService.deletePortletPreferences(
			TestPropsValues.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
			PortletKeys.PREFS_PLID_SHARED, portletId);

		Assert.assertEquals(auditMessages.toString(), 3, auditMessages.size());
	}

	@DeleteAfterTestRun
	private PortletPreferences _portletPreferences;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}