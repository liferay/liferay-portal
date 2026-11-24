/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.web.internal.info.item.provider.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.util.CommerceContextThreadLocal;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CSDiagramEntryInfoItemPermissionProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-117098")
	public void testHasPermission() throws Exception {
		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				TestPropsValues.getCompanyId());

		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), commerceCurrency.getCode());

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CSDiagramEntry csDiagramEntry =
			_csDiagramEntryLocalService.addCSDiagramEntry(
				TestPropsValues.getUserId(), cpInstance.getCPDefinitionId(),
				cpInstance.getCPInstanceId(), cpDefinition.getCProductId(),
				false, 0, null, cpInstance.getSku(),
				ServiceContextTestUtil.getServiceContext(
					commerceCatalog.getGroupId(), TestPropsValues.getUserId()));

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

		Assert.assertTrue(
			_infoItemPermissionProvider.hasPermission(
				permissionChecker, csDiagramEntry, ActionKeys.DELETE));
		Assert.assertTrue(
			_infoItemPermissionProvider.hasPermission(
				permissionChecker, csDiagramEntry, ActionKeys.UPDATE));

		InfoItemReference infoItemReference = new InfoItemReference(
			CSDiagramEntry.class.getName(),
			csDiagramEntry.getCSDiagramEntryId());

		Assert.assertTrue(
			_infoItemPermissionProvider.hasPermission(
				permissionChecker, infoItemReference, ActionKeys.DELETE));
		Assert.assertTrue(
			_infoItemPermissionProvider.hasPermission(
				permissionChecker, infoItemReference, ActionKeys.UPDATE));

		User user = UserTestUtil.addUser();

		AccountEntry accountEntry =
			CommerceAccountTestUtil.getPersonAccountEntry(user.getUserId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			TestPropsValues.getGroupId(), commerceCurrency.getCode());

		permissionChecker = PermissionCheckerFactoryUtil.create(user);

		try (SafeCloseable safeCloseable =
				CommerceContextThreadLocal.setCommerceContextWithSafeCloseable(
					_commerceContextFactory.create(
						accountEntry.getAccountEntryId(),
						commerceChannel.getGroupId(),
						commerceCurrency.getCode(), 0,
						TestPropsValues.getCompanyId()))) {

			Assert.assertFalse(
				_infoItemPermissionProvider.hasPermission(
					permissionChecker, csDiagramEntry, ActionKeys.DELETE));
			Assert.assertFalse(
				_infoItemPermissionProvider.hasPermission(
					permissionChecker, csDiagramEntry, ActionKeys.UPDATE));
			Assert.assertTrue(
				_infoItemPermissionProvider.hasPermission(
					permissionChecker, csDiagramEntry, ActionKeys.VIEW));
			Assert.assertFalse(
				_infoItemPermissionProvider.hasPermission(
					permissionChecker, infoItemReference, ActionKeys.DELETE));
			Assert.assertFalse(
				_infoItemPermissionProvider.hasPermission(
					permissionChecker, infoItemReference, ActionKeys.UPDATE));
			Assert.assertTrue(
				_infoItemPermissionProvider.hasPermission(
					permissionChecker, infoItemReference, ActionKeys.VIEW));
		}
	}

	@Inject
	private CommerceContextFactory _commerceContextFactory;

	@Inject
	private CSDiagramEntryLocalService _csDiagramEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.commerce.shop.by.diagram.web.internal.info.item.provider.CSDiagramEntryInfoItemPermissionProvider"
	)
	private InfoItemPermissionProvider<CSDiagramEntry>
		_infoItemPermissionProvider;

}