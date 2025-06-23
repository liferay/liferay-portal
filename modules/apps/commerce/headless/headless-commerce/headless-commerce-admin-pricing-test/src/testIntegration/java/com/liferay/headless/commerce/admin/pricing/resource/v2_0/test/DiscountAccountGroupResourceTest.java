/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v2_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalServiceUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountCommerceAccountGroupRel;
import com.liferay.commerce.discount.service.CommerceDiscountCommerceAccountGroupRelLocalServiceUtil;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountLocalServiceUtil;
import com.liferay.commerce.test.util.CommerceAccountGroupTestUtil;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountAccountGroup;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class DiscountAccountGroupResourceTest
	extends BaseDiscountAccountGroupResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		Iterator<CommerceDiscountCommerceAccountGroupRel> iterator =
			_commerceDiscountCommerceAccountGroupRels.iterator();

		while (iterator.hasNext()) {
			CommerceDiscountCommerceAccountGroupRel
				commerceDiscountCommerceAccountGroupRelIterator =
					iterator.next();

			CommerceDiscountCommerceAccountGroupRel
				commerceDiscountCommerceAccountGroupRel =
					CommerceDiscountCommerceAccountGroupRelLocalServiceUtil.
						fetchCommerceDiscountCommerceAccountGroupRel(
							commerceDiscountCommerceAccountGroupRelIterator.
								getCommerceDiscountId(),
							commerceDiscountCommerceAccountGroupRelIterator.
								getCommerceAccountGroupId());

			if (commerceDiscountCommerceAccountGroupRel != null) {
				CommerceDiscountCommerceAccountGroupRelLocalServiceUtil.
					deleteCommerceDiscountCommerceAccountGroupRel(
						commerceDiscountCommerceAccountGroupRel.
							getCommerceDiscountCommerceAccountGroupRelId());
				AccountGroupLocalServiceUtil.deleteAccountGroup(
					commerceDiscountCommerceAccountGroupRelIterator.
						getCommerceAccountGroupId());
				CommerceDiscountLocalServiceUtil.deleteCommerceDiscount(
					commerceDiscountCommerceAccountGroupRelIterator.
						getCommerceDiscountId());
			}

			iterator.remove();
		}

		CommerceDiscount commerceDiscount =
			_commerceDiscountLocalService.
				fetchCommerceDiscountByExternalReferenceCode(
					"external-reference-code-test", testCompany.getCompanyId());

		if (commerceDiscount != null) {
			_commerceDiscountLocalService.deleteCommerceDiscount(
				commerceDiscount.getCommerceDiscountId());
		}
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		Iterator<CommerceDiscountCommerceAccountGroupRel> iterator =
			_commerceDiscountCommerceAccountGroupRels.iterator();

		while (iterator.hasNext()) {
			CommerceDiscountCommerceAccountGroupRel
				commerceDiscountCommerceAccountGroupRelIterator =
					iterator.next();

			CommerceDiscountCommerceAccountGroupRel
				commerceDiscountCommerceAccountGroupRel =
					CommerceDiscountCommerceAccountGroupRelLocalServiceUtil.
						fetchCommerceDiscountCommerceAccountGroupRel(
							commerceDiscountCommerceAccountGroupRelIterator.
								getCommerceDiscountId(),
							commerceDiscountCommerceAccountGroupRelIterator.
								getCommerceAccountGroupId());

			if (commerceDiscountCommerceAccountGroupRel != null) {
				CommerceDiscountCommerceAccountGroupRelLocalServiceUtil.
					deleteCommerceDiscountCommerceAccountGroupRel(
						commerceDiscountCommerceAccountGroupRel.
							getCommerceDiscountCommerceAccountGroupRelId());
				AccountGroupLocalServiceUtil.deleteAccountGroup(
					commerceDiscountCommerceAccountGroupRelIterator.
						getCommerceAccountGroupId());
				CommerceDiscountLocalServiceUtil.deleteCommerceDiscount(
					commerceDiscountCommerceAccountGroupRelIterator.
						getCommerceDiscountId());
			}

			iterator.remove();
		}

		CommerceDiscount commerceDiscount =
			_commerceDiscountLocalService.
				fetchCommerceDiscountByExternalReferenceCode(
					"external-reference-code-test", testCompany.getCompanyId());

		if (commerceDiscount != null) {
			_commerceDiscountLocalService.deleteCommerceDiscount(
				commerceDiscount.getCommerceDiscountId());
		}
	}

	@Override
	@Test
	public void testDeleteDiscountAccountGroup() throws Exception {
		DiscountAccountGroup discountAccountGroup = _addDiscountAccountGroup(
			randomDiscountAccountGroup());

		assertHttpResponseStatusCode(
			204,
			discountAccountGroupResource.deleteDiscountAccountGroupHttpResponse(
				discountAccountGroup.getDiscountAccountGroupId()));
	}

	@Override
	@Test
	public void testGetDiscountIdDiscountAccountGroupsPageWithFilterDateTimeEquals()
		throws Exception {
	}

	@Override
	@Test
	public void testGetDiscountIdDiscountAccountGroupsPageWithFilterDoubleEquals()
		throws Exception {
	}

	@Override
	@Test
	public void testGetDiscountIdDiscountAccountGroupsPageWithFilterStringEquals()
		throws Exception {
	}

	@Override
	@Test
	public void testGetDiscountIdDiscountAccountGroupsPageWithSortDateTime()
		throws Exception {
	}

	@Override
	@Test
	public void testGetDiscountIdDiscountAccountGroupsPageWithSortDouble()
		throws Exception {
	}

	@Override
	@Test
	public void testGetDiscountIdDiscountAccountGroupsPageWithSortInteger()
		throws Exception {
	}

	@Override
	@Test
	public void testGetDiscountIdDiscountAccountGroupsPageWithSortString()
		throws Exception {
	}

	@Override
	@Test
	public void testGraphQLDeleteDiscountAccountGroup() throws Exception {
		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDiscountAccountGroup",
						HashMapBuilder.<String, Object>put(
							"discountAccountGroupId",
							() -> {
								DiscountAccountGroup discountAccountGroup =
									_addDiscountAccountGroup(
										randomDiscountAccountGroup());

								return discountAccountGroup.
									getDiscountAccountGroupId();
							}
						).build())),
				"JSONObject/data", "Object/deleteDiscountAccountGroup"));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"accountGroupId", "discountId"};
	}

	@Override
	protected DiscountAccountGroup randomDiscountAccountGroup()
		throws Exception {

		Calendar calendar = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		CommerceDiscount commerceDiscount =
			_commerceDiscountLocalService.addOrUpdateCommerceDiscount(
				RandomTestUtil.randomString(), _user.getUserId(), 0,
				RandomTestUtil.randomString(),
				CommerceDiscountConstants.TARGET_PRODUCTS, false, null, false,
				BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO,
				BigDecimal.ZERO, BigDecimal.ZERO,
				CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED, 0, true,
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), true, _serviceContext);

		AccountGroup discountAccountGroup =
			CommerceAccountGroupTestUtil.addAccountGroup(
				_serviceContext.getScopeGroupId());

		return new DiscountAccountGroup() {
			{
				accountGroupExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountGroupId = discountAccountGroup.getAccountGroupId();
				discountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				discountId = commerceDiscount.getCommerceDiscountId();
			}
		};
	}

	@Override
	protected DiscountAccountGroup
			testBatchEngineDeleteImportTask_addDiscountAccountGroup()
		throws Exception {

		return _addDiscountAccountGroup(randomDiscountAccountGroup());
	}

	@Override
	protected DiscountAccountGroup
			testDeleteDiscountAccountGroupBatch_addDiscountAccountGroup()
		throws Exception {

		return _addDiscountAccountGroup(randomDiscountAccountGroup());
	}

	@Override
	protected DiscountAccountGroup
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_addDiscountAccountGroup(
				String externalReferenceCode,
				DiscountAccountGroup discountAccountGroup)
		throws Exception {

		CommerceDiscount commerceDiscount =
			_commerceDiscountLocalService.
				fetchCommerceDiscountByExternalReferenceCode(
					externalReferenceCode, testCompany.getCompanyId());

		CommerceDiscountCommerceAccountGroupRel
			commerceDiscountCommerceAccountGroupRel =
				CommerceDiscountCommerceAccountGroupRelLocalServiceUtil.
					addCommerceDiscountCommerceAccountGroupRel(
						_serviceContext.getUserId(),
						commerceDiscount.getCommerceDiscountId(),
						discountAccountGroup.getAccountGroupId(),
						_serviceContext);

		_commerceDiscountCommerceAccountGroupRels.add(
			commerceDiscountCommerceAccountGroupRel);

		return _toDiscountAccountGroup(commerceDiscountCommerceAccountGroupRel);
	}

	@Override
	protected String
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_getExternalReferenceCode()
		throws Exception {

		if (_commerceDiscount == null) {
			Calendar calendar = CalendarFactoryUtil.getCalendar(
				_user.getTimeZone());

			_commerceDiscount =
				_commerceDiscountLocalService.addOrUpdateCommerceDiscount(
					"external-reference-code-test", _user.getUserId(), 0,
					RandomTestUtil.randomString(),
					CommerceDiscountConstants.TARGET_PRODUCTS, false, null,
					false, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO,
					CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED, 0,
					true, calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.YEAR),
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.YEAR),
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), true, _serviceContext);
		}

		return _commerceDiscount.getExternalReferenceCode();
	}

	@Override
	protected DiscountAccountGroup
			testGetDiscountIdDiscountAccountGroupsPage_addDiscountAccountGroup(
				Long id, DiscountAccountGroup discountAccountGroup)
		throws Exception {

		AccountGroup accountGroup =
			AccountGroupLocalServiceUtil.addAccountGroup(
				StringPool.BLANK, _serviceContext.getUserId(), null,
				RandomTestUtil.randomString(), _serviceContext);

		accountGroup.setDefaultAccountGroup(false);
		accountGroup.setType(AccountConstants.ACCOUNT_GROUP_TYPE_STATIC);
		accountGroup.setExpandoBridgeAttributes(_serviceContext);

		accountGroup = AccountGroupLocalServiceUtil.updateAccountGroup(
			accountGroup);

		discountAccountGroup.setAccountGroupId(
			accountGroup.getAccountGroupId());

		discountAccountGroup.setDiscountId(id);

		return _addDiscountAccountGroup(discountAccountGroup);
	}

	@Override
	protected Long testGetDiscountIdDiscountAccountGroupsPage_getId()
		throws Exception {

		if (_commerceDiscount == null) {
			Calendar calendar = CalendarFactoryUtil.getCalendar(
				_user.getTimeZone());

			_commerceDiscount =
				_commerceDiscountLocalService.addOrUpdateCommerceDiscount(
					"external-reference-code-test", _user.getUserId(), 0,
					RandomTestUtil.randomString(),
					CommerceDiscountConstants.TARGET_PRODUCTS, false, null,
					false, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO,
					CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED, 0,
					true, calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.YEAR),
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.YEAR),
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), true, _serviceContext);
		}

		return _commerceDiscount.getCommerceDiscountId();
	}

	@Override
	protected Long testGetDiscountIdDiscountAccountGroupsPage_getIrrelevantId()
		throws Exception {

		return super.
			testGetDiscountIdDiscountAccountGroupsPage_getIrrelevantId();
	}

	@Override
	protected DiscountAccountGroup
			testPostDiscountByExternalReferenceCodeDiscountAccountGroup_addDiscountAccountGroup(
				DiscountAccountGroup discountAccountGroup)
		throws Exception {

		return _addDiscountAccountGroup(discountAccountGroup);
	}

	@Override
	protected DiscountAccountGroup
			testPostDiscountIdDiscountAccountGroup_addDiscountAccountGroup(
				DiscountAccountGroup discountAccountGroup)
		throws Exception {

		return _addDiscountAccountGroup(discountAccountGroup);
	}

	private DiscountAccountGroup _addDiscountAccountGroup(
			DiscountAccountGroup discountAccountGroup)
		throws Exception {

		CommerceDiscountCommerceAccountGroupRel
			commerceDiscountCommerceAccountGroupRel =
				CommerceDiscountCommerceAccountGroupRelLocalServiceUtil.
					addCommerceDiscountCommerceAccountGroupRel(
						_serviceContext.getUserId(),
						discountAccountGroup.getDiscountId(),
						discountAccountGroup.getAccountGroupId(),
						_serviceContext);

		_commerceDiscountCommerceAccountGroupRels.add(
			commerceDiscountCommerceAccountGroupRel);

		return _toDiscountAccountGroup(commerceDiscountCommerceAccountGroupRel);
	}

	private DiscountAccountGroup _toDiscountAccountGroup(
			CommerceDiscountCommerceAccountGroupRel
				commerceDiscountCommerceAccountGroupRel)
		throws Exception {

		AccountGroup discountAccountGroup =
			commerceDiscountCommerceAccountGroupRel.getAccountGroup();

		CommerceDiscount commerceDiscount =
			commerceDiscountCommerceAccountGroupRel.getCommerceDiscount();

		return new DiscountAccountGroup() {
			{
				accountGroupExternalReferenceCode =
					discountAccountGroup.getExternalReferenceCode();
				accountGroupId = discountAccountGroup.getAccountGroupId();
				discountAccountGroupId =
					commerceDiscountCommerceAccountGroupRel.
						getCommerceDiscountCommerceAccountGroupRelId();
				discountExternalReferenceCode =
					commerceDiscount.getExternalReferenceCode();
				discountId = commerceDiscount.getCommerceDiscountId();
			}
		};
	}

	private CommerceDiscount _commerceDiscount;
	private final List<CommerceDiscountCommerceAccountGroupRel>
		_commerceDiscountCommerceAccountGroupRels = new ArrayList<>();

	@Inject
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}