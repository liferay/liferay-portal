/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.commerce.admin.site.setting.client.dto.v1_0.MeasurementUnit;
import com.liferay.headless.commerce.admin.site.setting.client.pagination.Page;
import com.liferay.headless.commerce.admin.site.setting.client.pagination.Pagination;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 */
@RunWith(Arquillian.class)
public class MeasurementUnitResourceTest
	extends BaseMeasurementUnitResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		Page<MeasurementUnit> page =
			measurementUnitResource.getMeasurementUnitsPage(
				null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (totalCount > 0) {
			for (MeasurementUnit measurementUnit : page.getItems()) {
				measurementUnitResource.deleteMeasurementUnit(
					measurementUnit.getId());
			}
		}
	}

	@Ignore
	@Override
	@Test
	public void testGetMeasurementUnitsByTypeWithSortDateTime() {
	}

	@Ignore
	@Override
	@Test
	public void testGetMeasurementUnitsByTypeWithSortDouble() {
	}

	@Ignore
	@Override
	@Test
	public void testGetMeasurementUnitsByTypeWithSortInteger() {
	}

	@Ignore
	@Override
	@Test
	public void testGetMeasurementUnitsByTypeWithSortString() {
	}

	@Ignore
	@Override
	@Test
	public void testGetMeasurementUnitsPageWithFilterDateTimeEquals() {
	}

	@Ignore
	@Override
	@Test
	public void testGetMeasurementUnitsPageWithFilterDoubleEquals() {
	}

	@Ignore
	@Override
	@Test
	public void testGetMeasurementUnitsPageWithFilterStringEquals() {
	}

	@Ignore
	@Override
	@Test
	public void testGetMeasurementUnitsPageWithSortDateTime() {
	}

	@Ignore
	@Override
	@Test
	public void testGetMeasurementUnitsPageWithSortDouble() {
	}

	@Ignore
	@Override
	@Test
	public void testGetMeasurementUnitsPageWithSortInteger() {
	}

	@Ignore
	@Override
	@Test
	public void testGetMeasurementUnitsPageWithSortString() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetMeasurementUnitsByType() {
	}

	@Override
	@Test
	public void testPatchMeasurementUnit() throws Exception {
		MeasurementUnit measurementUnit = _postMeasurementUnit(
			randomMeasurementUnit());

		measurementUnit.setExternalReferenceCode("externalReferenceCode1");
		measurementUnit.setPriority(Double.valueOf("9.0"));
		measurementUnit.setRate(Double.valueOf("1.0"));

		measurementUnitResource.patchMeasurementUnit(
			measurementUnit.getId(), measurementUnit);

		MeasurementUnit getMeasurementUnit =
			measurementUnitResource.getMeasurementUnit(measurementUnit.getId());

		Assert.assertTrue(equals(measurementUnit, getMeasurementUnit));
	}

	@Override
	@Test
	public void testPatchMeasurementUnitByExternalReferenceCode()
		throws Exception {

		MeasurementUnit measurementUnit = _postMeasurementUnit(
			randomMeasurementUnit());

		String oldExternalReferenceCode =
			measurementUnit.getExternalReferenceCode();

		measurementUnit.setExternalReferenceCode("externalReferenceCode1");
		measurementUnit.setPriority(Double.valueOf("9.0"));
		measurementUnit.setRate(Double.valueOf("1.0"));

		measurementUnitResource.patchMeasurementUnitByExternalReferenceCode(
			oldExternalReferenceCode, measurementUnit);

		MeasurementUnit getMeasurementUnit =
			measurementUnitResource.getMeasurementUnit(measurementUnit.getId());

		Assert.assertTrue(equals(measurementUnit, getMeasurementUnit));
	}

	@Override
	@Test
	public void testPatchMeasurementUnitByKey() throws Exception {
		MeasurementUnit measurementUnit = _postMeasurementUnit(
			randomMeasurementUnit());

		measurementUnit.setExternalReferenceCode("externalReferenceCode1");
		measurementUnit.setPriority(Double.valueOf("9.0"));
		measurementUnit.setRate(Double.valueOf("1.0"));

		measurementUnitResource.patchMeasurementUnitByKey(
			measurementUnit.getKey(), measurementUnit);

		MeasurementUnit getMeasurementUnit =
			measurementUnitResource.getMeasurementUnit(measurementUnit.getId());

		Assert.assertTrue(equals(measurementUnit, getMeasurementUnit));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"companyId", "externalReferenceCode", "key", "name", "priority",
			"rate", "type"
		};
	}

	@Override
	protected MeasurementUnit randomMeasurementUnit() {
		return new MeasurementUnit() {
			{
				companyId = testCompany.getCompanyId();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = HashMapBuilder.put(
					LocaleUtil.US.toString(), RandomTestUtil.randomString()
				).build();
				primary = true;
				priority = RandomTestUtil.randomDouble();
				rate = Double.valueOf("1.0");
				type = _types.get(
					RandomTestUtil.randomInt(0, _types.size() - 1));
			}
		};
	}

	@Override
	protected MeasurementUnit testDeleteMeasurementUnit_addMeasurementUnit()
		throws Exception {

		return _postMeasurementUnit(randomMeasurementUnit());
	}

	@Override
	protected MeasurementUnit
			testDeleteMeasurementUnitByExternalReferenceCode_addMeasurementUnit()
		throws Exception {

		return _postMeasurementUnit(randomMeasurementUnit());
	}

	@Override
	protected MeasurementUnit
			testDeleteMeasurementUnitByKey_addMeasurementUnit()
		throws Exception {

		return _postMeasurementUnit(randomMeasurementUnit());
	}

	@Override
	protected MeasurementUnit testGetMeasurementUnit_addMeasurementUnit()
		throws Exception {

		return _postMeasurementUnit(randomMeasurementUnit());
	}

	@Override
	protected MeasurementUnit
			testGetMeasurementUnitByExternalReferenceCode_addMeasurementUnit()
		throws Exception {

		return _postMeasurementUnit(randomMeasurementUnit());
	}

	@Override
	protected MeasurementUnit testGetMeasurementUnitByKey_addMeasurementUnit()
		throws Exception {

		return _postMeasurementUnit(randomMeasurementUnit());
	}

	@Override
	protected MeasurementUnit testGetMeasurementUnitsByType_addMeasurementUnit(
			String measurementUnitType, MeasurementUnit measurementUnit)
		throws Exception {

		measurementUnit.setType(measurementUnitType);

		return _postMeasurementUnit(measurementUnit);
	}

	@Override
	protected String testGetMeasurementUnitsByType_getMeasurementUnitType() {
		return _types.get(RandomTestUtil.randomInt(0, _types.size() - 1));
	}

	@Override
	protected MeasurementUnit testGetMeasurementUnitsPage_addMeasurementUnit(
			MeasurementUnit measurementUnit)
		throws Exception {

		return _postMeasurementUnit(randomMeasurementUnit());
	}

	@Override
	protected MeasurementUnit
			testGraphQLDeleteMeasurementUnit_addMeasurementUnit()
		throws Exception {

		return _postMeasurementUnit(randomMeasurementUnit());
	}

	@Override
	protected MeasurementUnit testGraphQLGetMeasurementUnit_addMeasurementUnit()
		throws Exception {

		return _postMeasurementUnit(randomMeasurementUnit());
	}

	@Override
	protected MeasurementUnit
			testGraphQLGetMeasurementUnitByExternalReferenceCode_addMeasurementUnit()
		throws Exception {

		return _postMeasurementUnit(randomMeasurementUnit());
	}

	@Override
	protected MeasurementUnit
			testGraphQLGetMeasurementUnitByKey_addMeasurementUnit()
		throws Exception {

		return _postMeasurementUnit(randomMeasurementUnit());
	}

	@Override
	protected MeasurementUnit testGraphQLMeasurementUnit_addMeasurementUnit()
		throws Exception {

		return _postMeasurementUnit(randomMeasurementUnit());
	}

	@Override
	protected MeasurementUnit testGraphQLMeasurementUnit_addMeasurementUnit(
			MeasurementUnit measurementUnit)
		throws Exception {

		return _postMeasurementUnit(measurementUnit);
	}

	@Override
	protected MeasurementUnit testPostMeasurementUnit_addMeasurementUnit(
			MeasurementUnit measurementUnit)
		throws Exception {

		return _postMeasurementUnit(measurementUnit);
	}

	@Override
	protected MeasurementUnit
			testPutMeasurementUnitByExternalReferenceCode_addMeasurementUnit()
		throws Exception {

		return _postMeasurementUnit(randomMeasurementUnit());
	}

	@Override
	protected MeasurementUnit
			testPutMeasurementUnitByExternalReferenceCode_createMeasurementUnit()
		throws Exception {

		return randomMeasurementUnit();
	}

	private MeasurementUnit _postMeasurementUnit(
			MeasurementUnit randomMeasurementUnit)
		throws Exception {

		return measurementUnitResource.postMeasurementUnit(
			randomMeasurementUnit);
	}

	private static final List<String> _types = Collections.unmodifiableList(
		ListUtil.fromArray("Dimensions", "Unit", "Weight"));

}