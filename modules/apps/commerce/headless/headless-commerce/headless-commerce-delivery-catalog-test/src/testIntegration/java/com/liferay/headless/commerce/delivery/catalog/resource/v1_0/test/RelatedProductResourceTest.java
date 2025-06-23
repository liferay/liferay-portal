/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLinkLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.RelatedProduct;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class RelatedProductResourceTest
	extends BaseRelatedProductResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), RandomTestUtil.randomString());
		_cpDefinition1 = CPTestUtil.addCPDefinition(
			testGroup.getGroupId(), "simple", true, false);
		_cpDefinition2 = CPTestUtil.addCPDefinition(
			testGroup.getGroupId(), "simple", true, false);
	}

	@Override
	protected RelatedProduct randomRelatedProduct() throws Exception {
		return new RelatedProduct() {
			{
				id = RandomTestUtil.randomLong();
				priority = RandomTestUtil.randomDouble();
				productId = _cpDefinition2.getCProductId();
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	@Override
	protected RelatedProduct
			testGetChannelProductRelatedProductsPage_addRelatedProduct(
				Long channelId, Long productId, RelatedProduct relatedProduct)
		throws Exception {

		return _addCPDefinitionLinkByCProductId(
			_cpDefinition1.getCPDefinitionId(), relatedProduct);
	}

	@Override
	protected Long testGetChannelProductRelatedProductsPage_getChannelId()
		throws Exception {

		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected Long testGetChannelProductRelatedProductsPage_getProductId()
		throws Exception {

		return _cpDefinition1.getCProductId();
	}

	private RelatedProduct _addCPDefinitionLinkByCProductId(
			Long cpDefinitionId, RelatedProduct relatedProduct)
		throws Exception {

		CPDefinitionLink cpDefinitionLink =
			_cpDefinitionLinkLocalService.addCPDefinitionLinkByCProductId(
				cpDefinitionId, relatedProduct.getProductId(), 1, 1, 2023, 1, 1,
				2, 2, 2023, 2, 2, true, relatedProduct.getPriority(),
				relatedProduct.getType(), _serviceContext);

		_cpDefinitionLink.add(cpDefinitionLink);

		return new RelatedProduct() {
			{
				id = cpDefinitionLink.getCPDefinitionLinkId();
				priority = cpDefinitionLink.getPriority();
				productId = cpDefinitionLink.getCProductId();
				type = cpDefinitionLink.getType();
			}
		};
	}

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CPDefinition _cpDefinition1;

	@DeleteAfterTestRun
	private CPDefinition _cpDefinition2;

	@DeleteAfterTestRun
	private final List<CPDefinitionLink> _cpDefinitionLink = new ArrayList<>();

	@Inject
	private CPDefinitionLinkLocalService _cpDefinitionLinkLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}