/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.product.type.virtual.test.util.VirtualCPTypeTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.SkuVirtualSettings;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Collections;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class SkuVirtualSettingsResourceTest
	extends BaseSkuVirtualSettingsResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser();

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			RandomTestUtil.randomString(), 0, RandomTestUtil.randomString(),
			"USD", "en_US", false,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), _user.getUserId()));

		_cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME);

		_cpInstance = CPTestUtil.addCPDefinitionCPInstance(
			_cpDefinition.getCPDefinitionId(), Collections.emptyMap());

		VirtualCPTypeTestUtil.addCPDefinitionVirtualSetting(
			_commerceCatalog.getGroupId(), CPInstance.class.getName(),
			_cpInstance.getCPInstanceId(), 0,
			CommerceOrderConstants.ORDER_STATUS_COMPLETED, 1, 0, 0);
	}

	@Override
	protected SkuVirtualSettings
			testGetSkuByExternalReferenceCodeSkuVirtualSettings_addSkuVirtualSettings()
		throws Exception {

		return skuVirtualSettingsResource.
			getSkuByExternalReferenceCodeSkuVirtualSettings(
				_cpInstance.getExternalReferenceCode());
	}

	@Override
	protected String
			testGetSkuByExternalReferenceCodeSkuVirtualSettings_getExternalReferenceCode()
		throws Exception {

		return _cpInstance.getExternalReferenceCode();
	}

	@Override
	protected SkuVirtualSettings
			testGetSkuIdSkuVirtualSettings_addSkuVirtualSettings()
		throws Exception {

		return skuVirtualSettingsResource.getSkuIdSkuVirtualSettings(
			_cpInstance.getCPInstanceId());
	}

	@Override
	protected Long testGetSkuIdSkuVirtualSettings_getId(
			SkuVirtualSettings skuVirtualSettings)
		throws Exception {

		return _cpInstance.getCPInstanceId();
	}

	@Override
	protected String
			testGraphQLGetSkuByExternalReferenceCodeSkuVirtualSettings_getExternalReferenceCode()
		throws Exception {

		return _cpInstance.getExternalReferenceCode();
	}

	@Override
	protected Long testGraphQLGetSkuIdSkuVirtualSettings_getId(
			SkuVirtualSettings skuVirtualSettings)
		throws Exception {

		return _cpInstance.getCPInstanceId();
	}

	@Override
	protected SkuVirtualSettings
			testGraphQLSkuSkuVirtualSettings_addSkuVirtualSettings()
		throws Exception {

		return skuVirtualSettingsResource.getSkuIdSkuVirtualSettings(
			_cpInstance.getCPInstanceId());
	}

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private CPDefinition _cpDefinition;

	@DeleteAfterTestRun
	private CPInstance _cpInstance;

	@DeleteAfterTestRun
	private User _user;

}