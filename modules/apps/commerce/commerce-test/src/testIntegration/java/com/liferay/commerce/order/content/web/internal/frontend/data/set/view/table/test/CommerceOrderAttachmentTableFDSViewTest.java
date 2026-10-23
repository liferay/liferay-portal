/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.view.table.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.FDSViewRegistry;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaField;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tancredi Covioli
 */
@FeatureFlag("LPD-69290")
@RunWith(Arquillian.class)
public class CommerceOrderAttachmentTableFDSViewTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetFDSTableSchemaField() throws Exception {
		_testGetFDSTableSchemaField(
			"commerceOrderAttachmentRestrictedDataRenderer", "restricted",
			"restricted", false);
		_testGetFDSTableSchemaField(
			"commerceOrderAttachmentTitleDataRenderer", "title", "title", true);
		_testGetFDSTableSchemaField(
			"dateTime", "modified-date", "dateModified", true);
		_testGetFDSTableSchemaField(null, "extension", "extension", false);
		_testGetFDSTableSchemaField(null, "priority", "priority", true);
		_testGetFDSTableSchemaField(null, "type", "typeLabel", false);
		_testGetFDSTableSchemaFieldWithDigitalSignatureEnabled();
	}

	private CompanyConfigurationTemporarySwapper
			_getCompanyConfigurationTemporarySwapper(boolean enabled)
		throws Exception {

		return new CompanyConfigurationTemporarySwapper(
			TestPropsValues.getCompanyId(),
			DigitalSignatureConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"embeddedViewEnabled", true
			).put(
				"enabled", enabled
			).put(
				"siteSettingsStrategy", "always-inherit"
			).build());
	}

	private Map<String, FDSTableSchemaField> _getFDSTableSchemaFieldsMap() {
		List<FDSView> fdsViews = _fdsViewRegistry.getFDSViews(
			CommercePortletKeys.COMMERCE_ORDER + "-attachments");

		FDSView fdsView = fdsViews.get(0);

		FDSTableSchema fdsTableSchema = fdsView.getFDSTableSchema(
			LocaleUtil.US);

		return fdsTableSchema.getFDSTableSchemaFieldsMap();
	}

	private void _testGetFDSTableSchemaField(
		String expectedContentRenderer, String expectedLabel, String fieldName,
		boolean sortable) {

		Map<String, FDSTableSchemaField> fdsTableSchemaFieldsMap =
			_getFDSTableSchemaFieldsMap();

		FDSTableSchemaField fdsTableSchemaField = fdsTableSchemaFieldsMap.get(
			fieldName);

		Assert.assertEquals(
			expectedContentRenderer, fdsTableSchemaField.getContentRenderer());
		Assert.assertEquals(expectedLabel, fdsTableSchemaField.getLabel());
		Assert.assertEquals(sortable, fdsTableSchemaField.isSortable());
	}

	private void _testGetFDSTableSchemaFieldWithDigitalSignatureEnabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(false)) {

			Map<String, FDSTableSchemaField> fdsTableSchemaFieldsMap =
				_getFDSTableSchemaFieldsMap();

			Assert.assertNull(fdsTableSchemaFieldsMap.get("dsRequestStatus"));
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(true)) {

			_testGetFDSTableSchemaField(
				"dsRequestStatusDataRenderer", "signature-status",
				"dsRequestStatus", false);
		}
	}

	@Inject
	private FDSViewRegistry _fdsViewRegistry;

}