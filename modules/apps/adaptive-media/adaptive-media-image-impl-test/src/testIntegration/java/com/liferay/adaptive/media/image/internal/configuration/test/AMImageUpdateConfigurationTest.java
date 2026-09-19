/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.configuration.test;

import com.liferay.adaptive.media.exception.AMImageConfigurationException;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class AMImageUpdateConfigurationTest
	extends BaseAMImageConfigurationTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpdateConfigurationEntryWithAlphanumericCharactersUuid()
		throws Exception {

		Map<String, String> properties = HashMapBuilder.put(
			"max-height", "100"
		).put(
			"max-width", "100"
		).build();

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1", properties);

		String uuid = "one-2-three_four";

		AMImageConfigurationEntry amImageConfigurationEntry =
			_amImageConfigurationHelper.updateAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1", "one", "desc", uuid,
				properties);

		Assert.assertEquals(uuid, amImageConfigurationEntry.getUUID());
	}

	@Test
	public void testUpdateConfigurationEntryWithBlankDescription()
		throws Exception {

		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "one", "desc", "1",
				HashMapBuilder.put(
					"max-height", "100"
				).put(
					"max-width", "100"
				).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", StringPool.BLANK, "1",
			amImageConfigurationEntry1.getProperties());

		AMImageConfigurationEntry amImageConfigurationEntry2 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		Assert.assertNotNull(amImageConfigurationEntry2);
		Assert.assertEquals(
			StringPool.BLANK, amImageConfigurationEntry2.getDescription());
	}

	@Test
	public void testUpdateConfigurationEntryWithBlankMaxHeight()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", ""
			).put(
				"max-width", "200"
			).build());

		AMImageConfigurationEntry amImageConfigurationEntry =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		Assert.assertNotNull(amImageConfigurationEntry);

		Map<String, String> actualProperties =
			amImageConfigurationEntry.getProperties();

		Assert.assertEquals("0", actualProperties.get("max-height"));
		Assert.assertEquals("200", actualProperties.get("max-width"));
	}

	@Test(
		expected = AMImageConfigurationException.RequiredWidthOrHeightException.class
	)
	public void testUpdateConfigurationEntryWithBlankMaxHeightOnly()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", ""
			).build());
	}

	@Test
	public void testUpdateConfigurationEntryWithBlankMaxWidth()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "200"
			).put(
				"max-width", ""
			).build());

		AMImageConfigurationEntry amImageConfigurationEntry =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		Assert.assertNotNull(amImageConfigurationEntry);

		Map<String, String> actualProperties =
			amImageConfigurationEntry.getProperties();

		Assert.assertEquals("200", actualProperties.get("max-height"));
		Assert.assertEquals("0", actualProperties.get("max-width"));
	}

	@Test(
		expected = AMImageConfigurationException.RequiredWidthOrHeightException.class
	)
	public void testUpdateConfigurationEntryWithBlankMaxWidthAndBlankMaxHeight()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", ""
			).put(
				"max-width", ""
			).build());
	}

	@Test(
		expected = AMImageConfigurationException.RequiredWidthOrHeightException.class
	)
	public void testUpdateConfigurationEntryWithBlankMaxWidthAndZeroMaxHeight()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", ""
			).put(
				"max-width", "0"
			).build());
	}

	@Test(
		expected = AMImageConfigurationException.RequiredWidthOrHeightException.class
	)
	public void testUpdateConfigurationEntryWithBlankMaxWidthOnly()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-width", ""
			).build());
	}

	@Test(expected = AMImageConfigurationException.InvalidNameException.class)
	public void testUpdateConfigurationEntryWithBlankName() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "one", "desc", "1",
				HashMapBuilder.put(
					"max-height", "100"
				).put(
					"max-width", "100"
				).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", StringPool.BLANK, "desc", "1",
			amImageConfigurationEntry1.getProperties());
	}

	@Test(expected = AMImageConfigurationException.InvalidUuidException.class)
	public void testUpdateConfigurationEntryWithBlankUuid() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "one", "desc", "1",
				HashMapBuilder.put(
					"max-height", "100"
				).put(
					"max-width", "100"
				).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "two", "desc",
			StringPool.BLANK, amImageConfigurationEntry1.getProperties());
	}

	@Test
	public void testUpdateConfigurationEntryWithColonSemicolonDescription()
		throws Exception {

		Map<String, String> properties = HashMapBuilder.put(
			"max-height", "100"
		).put(
			"max-width", "100"
		).build();

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1", properties);

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc:;desc", "1",
			properties);

		AMImageConfigurationEntry amImageConfigurationEntry =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		Assert.assertNotNull(amImageConfigurationEntry);
		Assert.assertEquals(
			"desc:;desc", amImageConfigurationEntry.getDescription());
	}

	@Test
	public void testUpdateConfigurationEntryWithColonSemicolonName()
		throws Exception {

		Map<String, String> properties = HashMapBuilder.put(
			"max-height", "100"
		).put(
			"max-width", "100"
		).build();

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1", properties);

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one:;one", "desc", "1",
			properties);

		AMImageConfigurationEntry amImageConfigurationEntry =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		Assert.assertNotNull(amImageConfigurationEntry);
		Assert.assertEquals("one:;one", amImageConfigurationEntry.getName());
	}

	@Test
	public void testUpdateConfigurationEntryWithMaxHeightOnly()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "200"
			).build());

		AMImageConfigurationEntry amImageConfigurationEntry =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		Assert.assertNotNull(amImageConfigurationEntry);

		Map<String, String> actualProperties =
			amImageConfigurationEntry.getProperties();

		Assert.assertEquals("200", actualProperties.get("max-height"));
		Assert.assertEquals("0", actualProperties.get("max-width"));
	}

	@Test
	public void testUpdateConfigurationEntryWithMaxWidthOnly()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-width", "200"
			).build());

		AMImageConfigurationEntry amImageConfigurationEntry =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		Assert.assertNotNull(amImageConfigurationEntry);

		Map<String, String> actualProperties =
			amImageConfigurationEntry.getProperties();

		Assert.assertEquals("0", actualProperties.get("max-height"));
		Assert.assertEquals("200", actualProperties.get("max-width"));
	}

	@Test(expected = AMImageConfigurationException.InvalidHeightException.class)
	public void testUpdateConfigurationEntryWithNegativeNumberMaxHeight()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "-10"
			).build());
	}

	@Test(expected = AMImageConfigurationException.InvalidWidthException.class)
	public void testUpdateConfigurationEntryWithNegativeNumberMaxWidth()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-width", "-10"
			).build());
	}

	@Test(expected = AMImageConfigurationException.InvalidUuidException.class)
	public void testUpdateConfigurationEntryWithNonalphanumericCharactersUuid()
		throws Exception {

		Map<String, String> properties = HashMapBuilder.put(
			"max-height", "100"
		).put(
			"max-width", "100"
		).build();

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1", properties);

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "a3%&!2",
			properties);
	}

	@Test(expected = AMImageConfigurationException.InvalidHeightException.class)
	public void testUpdateConfigurationEntryWithNotNumberMaxHeight()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "Invalid"
			).build());
	}

	@Test(expected = AMImageConfigurationException.InvalidWidthException.class)
	public void testUpdateConfigurationEntryWithNotNumberMaxWidth()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-width", "Invalid"
			).build());
	}

	@Test
	public void testUpdateConfigurationEntryWithZeroMaxHeight()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "0"
			).put(
				"max-width", "200"
			).build());

		AMImageConfigurationEntry amImageConfigurationEntry =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		Assert.assertNotNull(amImageConfigurationEntry);

		Map<String, String> actualProperties =
			amImageConfigurationEntry.getProperties();

		Assert.assertEquals("0", actualProperties.get("max-height"));
		Assert.assertEquals("200", actualProperties.get("max-width"));
	}

	@Test(
		expected = AMImageConfigurationException.RequiredWidthOrHeightException.class
	)
	public void testUpdateConfigurationEntryWithZeroMaxHeightOnly()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "0"
			).build());
	}

	@Test
	public void testUpdateConfigurationEntryWithZeroMaxWidth()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "200"
			).put(
				"max-width", "0"
			).build());

		AMImageConfigurationEntry amImageConfigurationEntry =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		Assert.assertNotNull(amImageConfigurationEntry);

		Map<String, String> actualProperties =
			amImageConfigurationEntry.getProperties();

		Assert.assertEquals("200", actualProperties.get("max-height"));
		Assert.assertEquals("0", actualProperties.get("max-width"));
	}

	@Test(
		expected = AMImageConfigurationException.RequiredWidthOrHeightException.class
	)
	public void testUpdateConfigurationEntryWithZeroMaxWidthAndBlankMaxHeight()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "0"
			).put(
				"max-width", ""
			).build());
	}

	@Test(
		expected = AMImageConfigurationException.RequiredWidthOrHeightException.class
	)
	public void testUpdateConfigurationEntryWithZeroMaxWidthAndZeroMaxHeight()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "0"
			).put(
				"max-width", "0"
			).build());
	}

	@Test(
		expected = AMImageConfigurationException.RequiredWidthOrHeightException.class
	)
	public void testUpdateConfigurationEntryWithZeroMaxWidthOnly()
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			HashMapBuilder.put(
				"max-width", "0"
			).build());
	}

	@Test(
		expected = AMImageConfigurationException.RequiredWidthOrHeightException.class
	)
	public void testUpdateConfigurationEntryWithoutMaxHeightNorMaxWidth()
		throws Exception {

		Map<String, String> properties = HashMapBuilder.put(
			"max-height", "100"
		).put(
			"max-width", "100"
		).build();

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "desc", "1", properties);

		properties = new HashMap<>();

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one", "desc", "1",
			properties);
	}

	@Test
	public void testUpdateDisabledConfigurationEntry() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "one", "desc", "1",
				HashMapBuilder.put(
					"max-height", "100"
				).put(
					"max-width", "100"
				).build());

		_amImageConfigurationHelper.disableAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(),
			amImageConfigurationEntry1.getUUID());

		AMImageConfigurationEntry amImageConfigurationEntry2 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		assertDisabled(amImageConfigurationEntry2);

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one-bis", "desc-bis", "1-bis",
			amImageConfigurationEntry1.getProperties());

		amImageConfigurationEntry2 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1-bis");

		assertDisabled(amImageConfigurationEntry2);

		Assert.assertEquals("one-bis", amImageConfigurationEntry2.getName());
		Assert.assertEquals(
			"desc-bis", amImageConfigurationEntry2.getDescription());

		Map<String, String> actualConfigurationEntry1Properties =
			amImageConfigurationEntry2.getProperties();

		Assert.assertEquals(
			"100", actualConfigurationEntry1Properties.get("max-height"));
		Assert.assertEquals(
			"100", actualConfigurationEntry1Properties.get("max-width"));
	}

	@Test(
		expected = AMImageConfigurationException.DuplicateAMImageConfigurationNameException.class
	)
	public void testUpdateDuplicateConfigurationEntryName() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "one", "onedesc", "1",
				HashMapBuilder.put(
					"max-height", "100"
				).put(
					"max-width", "100"
				).build());

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "two", "twodesc", "2",
			HashMapBuilder.put(
				"max-height", "200"
			).put(
				"max-width", "200"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "two", "twodesc", "1",
			amImageConfigurationEntry1.getProperties());
	}

	@Test(
		expected = AMImageConfigurationException.DuplicateAMImageConfigurationUuidException.class
	)
	public void testUpdateDuplicateConfigurationEntryUuid() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "one", "onedesc", "1",
				HashMapBuilder.put(
					"max-height", "100"
				).put(
					"max-width", "100"
				).build());

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "two", "twodesc", "2",
			HashMapBuilder.put(
				"max-height", "200"
			).put(
				"max-width", "200"
			).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "two-bis", "twodesc", "2",
			amImageConfigurationEntry1.getProperties());
	}

	@Test
	public void testUpdateFirstConfigurationEntryName() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "one", "onedesc", "1",
				HashMapBuilder.put(
					"max-height", "100"
				).put(
					"max-width", "100"
				).build());

		AMImageConfigurationEntry amImageConfigurationEntry2 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "two", "twodesc", "2",
				HashMapBuilder.put(
					"max-height", "200"
				).put(
					"max-width", "200"
				).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1", "one-bis", "onedesc-bis", "1",
			amImageConfigurationEntry1.getProperties());

		AMImageConfigurationEntry actualAMImageConfigurationEntry1 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		assertEnabled(actualAMImageConfigurationEntry1);

		Assert.assertEquals(
			"one-bis", actualAMImageConfigurationEntry1.getName());
		Assert.assertEquals(
			"onedesc-bis", actualAMImageConfigurationEntry1.getDescription());

		Map<String, String> actualConfigurationEntry1Properties =
			actualAMImageConfigurationEntry1.getProperties();

		Assert.assertEquals(
			"100", actualConfigurationEntry1Properties.get("max-height"));
		Assert.assertEquals(
			"100", actualConfigurationEntry1Properties.get("max-width"));

		AMImageConfigurationEntry actualAMImageConfigurationEntry2 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "2");

		assertEnabled(actualAMImageConfigurationEntry2);

		Assert.assertEquals(
			amImageConfigurationEntry2.getName(),
			actualAMImageConfigurationEntry2.getName());
		Assert.assertEquals(
			amImageConfigurationEntry2.getDescription(),
			actualAMImageConfigurationEntry2.getDescription());

		Map<String, String> actualConfigurationEntry2Properties =
			actualAMImageConfigurationEntry2.getProperties();

		Assert.assertEquals(
			"200", actualConfigurationEntry2Properties.get("max-height"));
		Assert.assertEquals(
			"200", actualConfigurationEntry2Properties.get("max-width"));
	}

	@Test
	public void testUpdateFirstConfigurationEntryProperties() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "one", "onedesc", "1",
				HashMapBuilder.put(
					"max-height", "100"
				).put(
					"max-width", "100"
				).build());

		AMImageConfigurationEntry amImageConfigurationEntry2 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "two", "twodesc", "2",
				HashMapBuilder.put(
					"max-height", "200"
				).put(
					"max-width", "200"
				).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(),
			amImageConfigurationEntry1.getUUID(),
			amImageConfigurationEntry1.getName(),
			amImageConfigurationEntry1.getDescription(),
			amImageConfigurationEntry1.getUUID(),
			HashMapBuilder.put(
				"max-height", "500"
			).put(
				"max-width", "800"
			).build());

		AMImageConfigurationEntry actualAMImageConfigurationEntry1 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		assertEnabled(actualAMImageConfigurationEntry1);

		Assert.assertEquals(
			amImageConfigurationEntry1.getName(),
			actualAMImageConfigurationEntry1.getName());
		Assert.assertEquals(
			amImageConfigurationEntry1.getDescription(),
			actualAMImageConfigurationEntry1.getDescription());

		Map<String, String> actualConfigurationEntry1Properties =
			actualAMImageConfigurationEntry1.getProperties();

		Assert.assertEquals(
			"500", actualConfigurationEntry1Properties.get("max-height"));
		Assert.assertEquals(
			"800", actualConfigurationEntry1Properties.get("max-width"));

		AMImageConfigurationEntry actualAMImageConfigurationEntry2 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "2");

		assertEnabled(actualAMImageConfigurationEntry2);

		Assert.assertEquals(
			amImageConfigurationEntry2.getName(),
			actualAMImageConfigurationEntry2.getName());
		Assert.assertEquals(
			amImageConfigurationEntry2.getDescription(),
			actualAMImageConfigurationEntry2.getDescription());

		Map<String, String> actualConfigurationEntry2Properties =
			actualAMImageConfigurationEntry2.getProperties();

		Assert.assertEquals(
			"200", actualConfigurationEntry2Properties.get("max-height"));
		Assert.assertEquals(
			"200", actualConfigurationEntry2Properties.get("max-width"));
	}

	@Test
	public void testUpdateFirstConfigurationEntryUuid() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "one", "onedesc", "1",
				HashMapBuilder.put(
					"max-height", "100"
				).put(
					"max-width", "100"
				).build());

		AMImageConfigurationEntry amImageConfigurationEntry2 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "two", "twodesc", "2",
				HashMapBuilder.put(
					"max-height", "200"
				).put(
					"max-width", "200"
				).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "1",
			amImageConfigurationEntry1.getName(),
			amImageConfigurationEntry1.getDescription(), "1-bis",
			amImageConfigurationEntry1.getProperties());

		AMImageConfigurationEntry nonexistentAMImageConfigurationEntry1 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		Assert.assertNull(nonexistentAMImageConfigurationEntry1);

		AMImageConfigurationEntry actualAMImageConfigurationEntry1 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1-bis");

		assertEnabled(actualAMImageConfigurationEntry1);

		Assert.assertEquals(
			amImageConfigurationEntry1.getName(),
			actualAMImageConfigurationEntry1.getName());
		Assert.assertEquals(
			amImageConfigurationEntry1.getDescription(),
			actualAMImageConfigurationEntry1.getDescription());

		Map<String, String> actualConfigurationEntry1Properties =
			actualAMImageConfigurationEntry1.getProperties();

		Assert.assertEquals(
			"100", actualConfigurationEntry1Properties.get("max-height"));
		Assert.assertEquals(
			"100", actualConfigurationEntry1Properties.get("max-width"));

		AMImageConfigurationEntry actualAMImageConfigurationEntry2 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "2");

		assertEnabled(actualAMImageConfigurationEntry2);

		Assert.assertEquals(
			amImageConfigurationEntry2.getName(),
			actualAMImageConfigurationEntry2.getName());
		Assert.assertEquals(
			amImageConfigurationEntry2.getDescription(),
			actualAMImageConfigurationEntry2.getDescription());

		Map<String, String> actualConfigurationEntry2Properties =
			actualAMImageConfigurationEntry2.getProperties();

		Assert.assertEquals(
			"200", actualConfigurationEntry2Properties.get("max-height"));
		Assert.assertEquals(
			"200", actualConfigurationEntry2Properties.get("max-width"));
	}

	@Test(
		expected = AMImageConfigurationException.NoSuchAMImageConfigurationException.class
	)
	public void testUpdateNonexistingConfiguration() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "one", "onedesc", "1",
				HashMapBuilder.put(
					"max-height", "100"
				).put(
					"max-width", "100"
				).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "2", "two", "twodesc", "2",
			amImageConfigurationEntry1.getProperties());
	}

	@Test
	public void testUpdateSecondConfigurationEntryName() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "one", "onedesc", "1",
				HashMapBuilder.put(
					"max-height", "100"
				).put(
					"max-width", "100"
				).build());

		AMImageConfigurationEntry amImageConfigurationEntry2 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "two", "twodesc", "2",
				HashMapBuilder.put(
					"max-height", "200"
				).put(
					"max-width", "200"
				).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "2", "two-bis", "twodesc-bis", "2",
			amImageConfigurationEntry2.getProperties());

		AMImageConfigurationEntry actualAMImageConfigurationEntry2 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "2");

		assertEnabled(actualAMImageConfigurationEntry2);

		Assert.assertEquals(
			"two-bis", actualAMImageConfigurationEntry2.getName());
		Assert.assertEquals(
			"twodesc-bis", actualAMImageConfigurationEntry2.getDescription());

		Map<String, String> actualConfigurationEntry2Properties =
			actualAMImageConfigurationEntry2.getProperties();

		Assert.assertEquals(
			"200", actualConfigurationEntry2Properties.get("max-height"));
		Assert.assertEquals(
			"200", actualConfigurationEntry2Properties.get("max-width"));

		AMImageConfigurationEntry actualAMImageConfigurationEntry1 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		assertEnabled(actualAMImageConfigurationEntry1);

		Assert.assertEquals(
			amImageConfigurationEntry1.getName(),
			actualAMImageConfigurationEntry1.getName());
		Assert.assertEquals(
			amImageConfigurationEntry1.getDescription(),
			actualAMImageConfigurationEntry1.getDescription());

		Map<String, String> actualConfigurationEntry1Properties =
			actualAMImageConfigurationEntry1.getProperties();

		Assert.assertEquals(
			"100", actualConfigurationEntry1Properties.get("max-height"));
		Assert.assertEquals(
			"100", actualConfigurationEntry1Properties.get("max-width"));
	}

	@Test
	public void testUpdateSecondConfigurationEntryProperties()
		throws Exception {

		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "one", "onedesc", "1",
				HashMapBuilder.put(
					"max-height", "100"
				).put(
					"max-width", "100"
				).build());

		AMImageConfigurationEntry amImageConfigurationEntry2 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "two", "twodesc", "2",
				HashMapBuilder.put(
					"max-height", "200"
				).put(
					"max-width", "200"
				).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(),
			amImageConfigurationEntry2.getUUID(),
			amImageConfigurationEntry2.getName(),
			amImageConfigurationEntry2.getDescription(),
			amImageConfigurationEntry2.getUUID(),
			HashMapBuilder.put(
				"max-height", "500"
			).put(
				"max-width", "800"
			).build());

		AMImageConfigurationEntry actualAMImageConfigurationEntry2 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "2");

		assertEnabled(actualAMImageConfigurationEntry2);

		Assert.assertEquals(
			amImageConfigurationEntry2.getName(),
			actualAMImageConfigurationEntry2.getName());
		Assert.assertEquals(
			amImageConfigurationEntry2.getDescription(),
			actualAMImageConfigurationEntry2.getDescription());

		Map<String, String> actualConfigurationEntry2Properties =
			actualAMImageConfigurationEntry2.getProperties();

		Assert.assertEquals(
			"500", actualConfigurationEntry2Properties.get("max-height"));
		Assert.assertEquals(
			"800", actualConfigurationEntry2Properties.get("max-width"));

		AMImageConfigurationEntry actualAMImageConfigurationEntry1 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		assertEnabled(actualAMImageConfigurationEntry1);

		Assert.assertEquals(
			amImageConfigurationEntry1.getName(),
			actualAMImageConfigurationEntry1.getName());
		Assert.assertEquals(
			amImageConfigurationEntry1.getDescription(),
			actualAMImageConfigurationEntry1.getDescription());

		Map<String, String> actualConfigurationEntry1Properties =
			actualAMImageConfigurationEntry1.getProperties();

		Assert.assertEquals(
			"100", actualConfigurationEntry1Properties.get("max-height"));
		Assert.assertEquals(
			"100", actualConfigurationEntry1Properties.get("max-width"));
	}

	@Test
	public void testUpdateSecondConfigurationEntryUuid() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "one", "onedesc", "1",
				HashMapBuilder.put(
					"max-height", "100"
				).put(
					"max-width", "100"
				).build());

		AMImageConfigurationEntry amImageConfigurationEntry2 =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "two", "twodesc", "2",
				HashMapBuilder.put(
					"max-height", "200"
				).put(
					"max-width", "200"
				).build());

		_amImageConfigurationHelper.updateAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "2",
			amImageConfigurationEntry2.getName(),
			amImageConfigurationEntry2.getDescription(), "2-bis",
			amImageConfigurationEntry2.getProperties());

		AMImageConfigurationEntry nonexistentAMImageConfigurationEntry2 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "2");

		Assert.assertNull(nonexistentAMImageConfigurationEntry2);

		AMImageConfigurationEntry actualAMImageConfigurationEntry2 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "2-bis");

		assertEnabled(actualAMImageConfigurationEntry2);

		Assert.assertEquals(
			amImageConfigurationEntry2.getName(),
			actualAMImageConfigurationEntry2.getName());
		Assert.assertEquals(
			amImageConfigurationEntry2.getDescription(),
			actualAMImageConfigurationEntry2.getDescription());

		Map<String, String> actualConfigurationEntry2Properties =
			actualAMImageConfigurationEntry2.getProperties();

		Assert.assertEquals(
			"200", actualConfigurationEntry2Properties.get("max-height"));
		Assert.assertEquals(
			"200", actualConfigurationEntry2Properties.get("max-width"));

		AMImageConfigurationEntry actualAMImageConfigurationEntry1 =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		assertEnabled(actualAMImageConfigurationEntry1);

		Assert.assertEquals(
			amImageConfigurationEntry1.getName(),
			actualAMImageConfigurationEntry1.getName());
		Assert.assertEquals(
			amImageConfigurationEntry1.getDescription(),
			actualAMImageConfigurationEntry1.getDescription());

		Map<String, String> actualConfigurationEntry1Properties =
			actualAMImageConfigurationEntry1.getProperties();

		Assert.assertEquals(
			"100", actualConfigurationEntry1Properties.get("max-height"));
		Assert.assertEquals(
			"100", actualConfigurationEntry1Properties.get("max-width"));
	}

	@Override
	protected AMImageConfigurationHelper getAMImageConfigurationHelper() {
		return _amImageConfigurationHelper;
	}

	@Inject
	private AMImageConfigurationHelper _amImageConfigurationHelper;

}