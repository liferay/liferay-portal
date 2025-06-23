/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.model.CommerceTermEntryRel;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.commerce.term.service.CommerceTermEntryRelLocalService;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.TermOrderType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 * @author Stefano Motta
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class TermOrderTypeResourceTest
	extends BaseTermOrderTypeResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_commerceTermEntry =
			_commerceTermEntryLocalService.addCommerceTermEntry(
				RandomTestUtil.randomString(), _user.getUserId(),
				RandomTestUtil.randomBoolean(),
				RandomTestUtil.randomLocaleStringMap(), 1, 1, 2022, 12, 0, 0, 0,
				0, 0, 0, true, RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomString(), RandomTestUtil.nextDouble(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				_serviceContext);
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteTermOrderType() throws Exception {
		super.testDeleteTermOrderType();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteTermOrderTypeBatch() throws Exception {
		super.testDeleteTermOrderTypeBatch();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteTermOrderType() throws Exception {
		super.testGraphQLDeleteTermOrderType();
	}

	@Override
	protected TermOrderType randomTermOrderType() throws Exception {
		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.addCommerceOrderType(
				RandomTestUtil.randomString(), _user.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean(), 1, 1, 2022, 12, 0, 0, 0, 0, 0,
				0, 0, true, _serviceContext);

		return new TermOrderType() {
			{
				orderTypeExternalReferenceCode =
					commerceOrderType.getExternalReferenceCode();
				orderTypeId = commerceOrderType.getCommerceOrderTypeId();
				termExternalReferenceCode =
					_commerceTermEntry.getExternalReferenceCode();
				termId = _commerceTermEntry.getCommerceTermEntryId();
				termOrderTypeId = RandomTestUtil.randomLong();
			}
		};
	}

	@Override
	protected TermOrderType
			testGetTermByExternalReferenceCodeTermOrderTypesPage_addTermOrderType(
				String externalReferenceCode, TermOrderType termOrderType)
		throws Exception {

		return _addTermOrderType(termOrderType);
	}

	@Override
	protected String
			testGetTermByExternalReferenceCodeTermOrderTypesPage_getExternalReferenceCode()
		throws Exception {

		return _commerceTermEntry.getExternalReferenceCode();
	}

	@Override
	protected TermOrderType testGetTermIdTermOrderTypesPage_addTermOrderType(
			Long id, TermOrderType termOrderType)
		throws Exception {

		return _addTermOrderType(termOrderType);
	}

	@Override
	protected Long testGetTermIdTermOrderTypesPage_getId() throws Exception {
		return _commerceTermEntry.getCommerceTermEntryId();
	}

	@Override
	protected TermOrderType
			testPostTermByExternalReferenceCodeTermOrderType_addTermOrderType(
				TermOrderType termOrderType)
		throws Exception {

		return _addTermOrderType(termOrderType);
	}

	@Override
	protected TermOrderType testPostTermIdTermOrderType_addTermOrderType(
			TermOrderType termOrderType)
		throws Exception {

		return _addTermOrderType(termOrderType);
	}

	private TermOrderType _addTermOrderType(TermOrderType termOrderType)
		throws Exception {

		return _toTermOrderType(
			_commerceTermEntryRelLocalService.addCommerceTermEntryRel(
				_user.getUserId(), CommerceOrderType.class.getName(),
				termOrderType.getOrderTypeId(), termOrderType.getTermId()));
	}

	private TermOrderType _toTermOrderType(
			CommerceTermEntryRel commerceTermEntryRel)
		throws Exception {

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.getCommerceOrderType(
				commerceTermEntryRel.getClassPK());
		CommerceTermEntry commerceTermEntry =
			_commerceTermEntryLocalService.getCommerceTermEntry(
				commerceTermEntryRel.getCommerceTermEntryId());

		return new TermOrderType() {
			{
				orderTypeExternalReferenceCode =
					commerceOrderType.getExternalReferenceCode();
				orderTypeId = commerceOrderType.getCommerceOrderTypeId();
				termExternalReferenceCode =
					commerceTermEntry.getExternalReferenceCode();
				termId = commerceTermEntry.getCommerceTermEntryId();
				termOrderTypeId =
					commerceTermEntryRel.getCommerceTermEntryRelId();
			}
		};
	}

	@Inject
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	private CommerceTermEntry _commerceTermEntry;

	@Inject
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	@Inject
	private CommerceTermEntryRelLocalService _commerceTermEntryRelLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}