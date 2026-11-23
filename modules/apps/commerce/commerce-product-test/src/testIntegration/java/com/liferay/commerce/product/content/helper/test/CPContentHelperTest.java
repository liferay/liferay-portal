/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.helper.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.content.helper.CPContentHelper;
import com.liferay.commerce.product.content.util.CPMedia;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Calendar;
import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CPContentHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetImages() throws Exception {
		frutillaRule.scenario(
			"Get the gallery images of a product"
		).given(
			"A product with a CDN image whose title and URL contain HTML " +
				"special characters"
		).when(
			"The gallery images are requested"
		).then(
			"The title and the URL are escaped in the image tag"
		);

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				TestPropsValues.getCompanyId());

		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), commerceCurrency.getCode());

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		Calendar calendar = CalendarFactoryUtil.getCalendar();
		String cdnURL = StringBundler.concat(
			"http://", RandomTestUtil.randomString(), "?",
			RandomTestUtil.randomString(), "&", RandomTestUtil.randomString());
		String title = StringBundler.concat(
			RandomTestUtil.randomString(), "\"", RandomTestUtil.randomString());

		_cpAttachmentFileEntryLocalService.addCPAttachmentFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			commerceCatalog.getGroupId(),
			_classNameLocalService.getClassNameId(CPDefinition.class),
			cpDefinition.getCPDefinitionId(), 0, true, cdnURL,
			calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
			calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
			calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, true,
			HashMapBuilder.put(
				LocaleUtil.US, title
			).build(),
			null, 0, CPAttachmentFileEntryConstants.TYPE_IMAGE,
			ServiceContextTestUtil.getServiceContext(
				commerceCatalog.getGroupId()));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addPreferredLocale(LocaleUtil.US);

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLanguageId(LocaleUtil.toLanguageId(LocaleUtil.US));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setRequest(mockHttpServletRequest);

		List<CPMedia> cpMedias = _cpContentHelper.getImages(
			cpDefinition.getCPDefinitionId(), true, themeDisplay);

		Assert.assertEquals(cpMedias.toString(), 1, cpMedias.size());

		String adaptiveMediaImageHTMLTag = ReflectionTestUtil.invoke(
			cpMedias.get(0), "getAdaptiveMediaImageHTMLTag", new Class<?>[0]);

		Assert.assertTrue(
			adaptiveMediaImageHTMLTag.contains(
				"aria-label=\"" + HtmlUtil.escapeAttribute(title) + "\""));
		Assert.assertTrue(
			adaptiveMediaImageHTMLTag.contains(
				"src=\"" + HtmlUtil.escapeAttribute(cdnURL) + "\""));
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Inject
	private CPContentHelper _cpContentHelper;

}