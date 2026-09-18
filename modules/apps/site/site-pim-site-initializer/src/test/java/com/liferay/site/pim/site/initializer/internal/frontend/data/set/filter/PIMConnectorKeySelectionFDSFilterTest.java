/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
 */
public class PIMConnectorKeySelectionFDSFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_pimConnectorKeySelectionFDSFilter, "_pimConnectorRegistry",
			_pimConnectorRegistry);

		CompanyThreadLocal.setCompanyId(_COMPANY_ID);
	}

	@Test
	public void testGetEntityFieldType() {
		Assert.assertEquals(
			FDSEntityFieldTypes.STRING,
			_pimConnectorKeySelectionFDSFilter.getEntityFieldType());
	}

	@Test
	public void testGetId() {
		Assert.assertEquals("key", _pimConnectorKeySelectionFDSFilter.getId());
	}

	@Test
	public void testGetLabel() {
		Assert.assertEquals(
			"connector", _pimConnectorKeySelectionFDSFilter.getLabel());
	}

	@Test
	public void testGetSelectionFDSFilterItems() {
		PIMConnector pimConnector = _mockPIMConnector(
			"Liferay Commerce", "liferay-commerce");

		Mockito.when(
			_pimConnectorRegistry.getPIMConnectors(_COMPANY_ID)
		).thenReturn(
			ListUtil.fromArray(pimConnector)
		);

		List<SelectionFDSFilterItem> selectionFDSFilterItems =
			_pimConnectorKeySelectionFDSFilter.getSelectionFDSFilterItems(
				LocaleUtil.US);

		Assert.assertEquals(
			selectionFDSFilterItems.toString(), 1,
			selectionFDSFilterItems.size());

		SelectionFDSFilterItem selectionFDSFilterItem =
			selectionFDSFilterItems.get(0);

		Assert.assertEquals(
			"Liferay Commerce", selectionFDSFilterItem.getLabel());
		Assert.assertEquals(
			"liferay-commerce", selectionFDSFilterItem.getValue());
	}

	@Test
	public void testGetSelectionFDSFilterItemsWithoutPIMConnectors() {
		Mockito.when(
			_pimConnectorRegistry.getPIMConnectors(_COMPANY_ID)
		).thenReturn(
			ListUtil.fromArray(new PIMConnector[0])
		);

		List<SelectionFDSFilterItem> selectionFDSFilterItems =
			_pimConnectorKeySelectionFDSFilter.getSelectionFDSFilterItems(
				LocaleUtil.US);

		Assert.assertTrue(
			selectionFDSFilterItems.toString(),
			selectionFDSFilterItems.isEmpty());
	}

	private PIMConnector _mockPIMConnector(String name, String key) {
		PIMConnector pimConnector = Mockito.mock(PIMConnector.class);

		Mockito.when(
			pimConnector.getKey()
		).thenReturn(
			key
		);

		Mockito.when(
			pimConnector.getName(LocaleUtil.US)
		).thenReturn(
			name
		);

		return pimConnector;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private final PIMConnectorKeySelectionFDSFilter
		_pimConnectorKeySelectionFDSFilter =
			new PIMConnectorKeySelectionFDSFilter();
	private final PIMConnectorRegistry _pimConnectorRegistry = Mockito.mock(
		PIMConnectorRegistry.class);

}