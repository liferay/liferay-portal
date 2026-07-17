/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.criteria.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.audiences.criteria.AudiencesCriteria;
import com.liferay.audiences.criteria.AudiencesCriteriaProvider;
import com.liferay.audiences.criteria.AudiencesCriteriaType;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class AudiencesCriteriaProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetBrowserAttributesAudiencesCriteriaType()
		throws Exception {

		List<AudiencesCriteriaType> audiencesCriteriaTypes =
			_audiencesCriteriaProvider.getAudiencesCriteriaTypes(
				TestPropsValues.getCompanyId(), LocaleUtil.getDefault());

		AudiencesCriteriaType audiencesCriteriaType =
			audiencesCriteriaTypes.get(0);

		List<AudiencesCriteria> audiencesCriterias =
			audiencesCriteriaType.getAudiencesCriterias();

		Assert.assertEquals(
			audiencesCriterias.toString(), 15, audiencesCriterias.size());

		AudiencesCriteria audiencesCriteria = _getAudiencesCriteria(
			audiencesCriterias, "url");

		Assert.assertEquals(
			AudiencesCriteria.Type.STRING, audiencesCriteria.getType());
	}

	@Test
	public void testGetCustomAudiencesCriteriaTypeWithCustomAttribute()
		throws Exception {

		String name = RandomTestUtil.randomString();
		String symbol = RandomTestUtil.randomString();

		_addClientExtensionEntry(name, symbol);

		List<AudiencesCriteriaType> audiencesCriteriaTypes =
			_audiencesCriteriaProvider.getAudiencesCriteriaTypes(
				TestPropsValues.getCompanyId(), LocaleUtil.getDefault());

		AudiencesCriteriaType audiencesCriteriaType =
			audiencesCriteriaTypes.get(2);

		List<AudiencesCriteria> audiencesCriterias =
			audiencesCriteriaType.getAudiencesCriterias();

		Assert.assertEquals(
			audiencesCriterias.toString(), 1, audiencesCriterias.size());

		AudiencesCriteria audiencesCriteria = audiencesCriterias.get(0);

		Assert.assertEquals(name, audiencesCriteria.getLabel());
		Assert.assertEquals(
			AudiencesCriteria.Type.STRING, audiencesCriteria.getType());

		String key = audiencesCriteria.getKey();

		Assert.assertTrue(key, key.endsWith(StringPool.POUND + symbol));
	}

	@Test
	public void testGetCustomAudiencesCriteriaTypeWithSegment()
		throws Exception {

		SegmentsEntry segmentsEntry = _addSegmentsEntry();

		List<AudiencesCriteriaType> audiencesCriteriaTypes =
			_audiencesCriteriaProvider.getAudiencesCriteriaTypes(
				TestPropsValues.getCompanyId(), LocaleUtil.getDefault());

		AudiencesCriteriaType audiencesCriteriaType =
			audiencesCriteriaTypes.get(2);

		AudiencesCriteria audiencesCriteria = _getAudiencesCriteria(
			audiencesCriteriaType.getAudiencesCriterias(), "segments");

		Assert.assertEquals(
			AudiencesCriteria.InputType.SELECT,
			audiencesCriteria.getInputType());
		Assert.assertEquals(
			AudiencesCriteria.Type.STRING, audiencesCriteria.getType());

		AudiencesCriteria.Option option = _getOption(
			audiencesCriteria.getOptions(),
			segmentsEntry.getSegmentsEntryKey());

		Assert.assertEquals(
			segmentsEntry.getName(LocaleUtil.getDefault()), option.getLabel());
	}

	@Test
	public void testGetGeneralAttributesAudiencesCriteriaType()
		throws Exception {

		List<AudiencesCriteriaType> audiencesCriteriaTypes =
			_audiencesCriteriaProvider.getAudiencesCriteriaTypes(
				TestPropsValues.getCompanyId(), LocaleUtil.getDefault());

		AudiencesCriteriaType audiencesCriteriaType =
			audiencesCriteriaTypes.get(1);

		List<AudiencesCriteria> audiencesCriterias =
			audiencesCriteriaType.getAudiencesCriterias();

		Assert.assertEquals(
			audiencesCriterias.toString(), 2, audiencesCriterias.size());

		AudiencesCriteria authenticationAudiencesCriteria =
			_getAudiencesCriteria(audiencesCriterias, "user_authentication");

		Assert.assertEquals(
			AudiencesCriteria.Type.BOOLEAN,
			authenticationAudiencesCriteria.getType());
		Assert.assertNull(authenticationAudiencesCriteria.getOptions());

		AudiencesCriteria languageAudiencesCriteria = _getAudiencesCriteria(
			audiencesCriterias, "user_language");

		Assert.assertEquals(
			AudiencesCriteria.Type.STRING, languageAudiencesCriteria.getType());

		List<AudiencesCriteria.Option> options =
			languageAudiencesCriteria.getOptions();

		Assert.assertFalse(options.toString(), options.isEmpty());
	}

	private void _addClientExtensionEntry(String name, String symbol)
		throws Exception {

		_clientExtensionEntries.add(
			_clientExtensionEntryLocalService.addClientExtensionEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				StringPool.BLANK,
				Collections.singletonMap(LocaleUtil.getDefault(), name),
				StringPool.BLANK, StringPool.BLANK,
				ClientExtensionEntryConstants.TYPE_AUDIENCES_CUSTOM_ATTRIBUTES,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"names", name
				).put(
					"symbols", symbol
				).put(
					"types", "string"
				).put(
					"url", "http://" + RandomTestUtil.randomString() + ".com"
				).buildString()));
	}

	private SegmentsEntry _addSegmentsEntry() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			CriteriaSerializer.serialize(new Criteria()),
			SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));

		_segmentsEntries.add(segmentsEntry);

		return segmentsEntry;
	}

	private AudiencesCriteria _getAudiencesCriteria(
		List<AudiencesCriteria> audiencesCriterias, String key) {

		for (AudiencesCriteria audiencesCriteria : audiencesCriterias) {
			if (key.equals(audiencesCriteria.getKey())) {
				return audiencesCriteria;
			}
		}

		return null;
	}

	private AudiencesCriteria.Option _getOption(
		List<AudiencesCriteria.Option> options, String value) {

		for (AudiencesCriteria.Option option : options) {
			if (value.equals(option.getValue())) {
				return option;
			}
		}

		return null;
	}

	@Inject
	private AudiencesCriteriaProvider _audiencesCriteriaProvider;

	@DeleteAfterTestRun
	private final List<ClientExtensionEntry> _clientExtensionEntries =
		new ArrayList<>();

	@Inject
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	@DeleteAfterTestRun
	private final List<SegmentsEntry> _segmentsEntries = new ArrayList<>();

}