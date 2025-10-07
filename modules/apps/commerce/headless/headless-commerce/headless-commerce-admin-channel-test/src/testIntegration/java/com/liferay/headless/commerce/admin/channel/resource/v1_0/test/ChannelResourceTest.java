/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.Channel;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class ChannelResourceTest extends BaseChannelResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testGroup.getCompanyId());

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetChannelsPage() throws Exception {
		super.testGraphQLGetChannelsPage();
	}

	@Override
	@Test
	public void testPatchChannel() throws Exception {
		super.testPatchChannel();

		_testPatchChannelWithAccountExternalReferenceCode();
	}

	@Override
	@Test
	public void testPatchChannelByExternalReferenceCode() throws Exception {
		super.testPatchChannelByExternalReferenceCode();

		_testPatchChannelByExternalReferenceCodeWithAccountExternalReferenceCode();
	}

	@Override
	@Test
	public void testPostChannel() throws Exception {
		super.testPostChannel();

		_testPostChannelWithAccountExternalReferenceCode();
	}

	@Override
	@Test
	public void testPutChannel() throws Exception {
		super.testPutChannel();

		_testPutChannelWithAccountExternalReferenceCode();
	}

	@Override
	@Test
	public void testPutChannelByExternalReferenceCode() throws Exception {
		super.testPutChannelByExternalReferenceCode();

		_testPutChannelByExternalReferenceCodeWithAccountExternalReferenceCode();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"currencyCode", "name", "type"};
	}

	@Override
	protected Channel randomChannel() throws Exception {
		return new Channel() {
			{
				accountId = AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT;
				currencyCode = _commerceCurrency.getCode();
				currencyExternalReferenceCode =
					_commerceCurrency.getExternalReferenceCode();
				currencyId = _commerceCurrency.getCommerceCurrencyId();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				siteGroupId = RandomTestUtil.randomLong();
				type = CommerceChannelConstants.CHANNEL_TYPE_SITE;
			}
		};
	}

	@Override
	protected Channel randomPatchChannel() throws Exception {
		return new Channel() {
			{
				accountId = AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT;
				currencyCode = _commerceCurrency.getCode();
				currencyExternalReferenceCode =
					_commerceCurrency.getExternalReferenceCode();
				currencyId = _commerceCurrency.getCommerceCurrencyId();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = CommerceChannelConstants.CHANNEL_TYPE_SITE;
			}
		};
	}

	@Override
	protected Channel testDeleteChannel_addChannel() throws Exception {
		return channelResource.postChannel(randomChannel());
	}

	@Override
	protected Channel testDeleteChannelByExternalReferenceCode_addChannel()
		throws Exception {

		return channelResource.postChannel(randomChannel());
	}

	@Override
	protected Channel testGetAccountAddressChannelChannel_addChannel()
		throws Exception {

		if (_commerceChannelRel == null) {
			_getCommerceChannelRelId();
		}

		return _toChannel(
			_commerceChannelLocalService.getCommerceChannel(
				_commerceChannelRel.getCommerceChannelId()));
	}

	@Override
	protected Long
			testGetAccountAddressChannelChannel_getAccountAddressChannelId()
		throws Exception {

		return _getCommerceChannelRelId();
	}

	@Override
	protected Channel testGetChannel_addChannel() throws Exception {
		return channelResource.postChannel(randomChannel());
	}

	@Override
	protected Channel testGetChannelByExternalReferenceCode_addChannel()
		throws Exception {

		return channelResource.postChannel(randomChannel());
	}

	@Override
	protected Channel testGetChannelsPage_addChannel(Channel channel)
		throws Exception {

		return channelResource.postChannel(channel);
	}

	@Override
	protected Channel testGraphQLChannel_addChannel() throws Exception {
		return channelResource.postChannel(randomChannel());
	}

	@Override
	protected Channel testGraphQLChannel_addChannel(Channel channel)
		throws Exception {

		return channelResource.postChannel(channel);
	}

	@Override
	protected Channel testGraphQLGetAccountAddressChannelChannel_addChannel()
		throws Exception {

		return testGetAccountAddressChannelChannel_addChannel();
	}

	@Override
	protected Long
			testGraphQLGetAccountAddressChannelChannel_getAccountAddressChannelId()
		throws Exception {

		return _getCommerceChannelRelId();
	}

	@Override
	protected Channel testPatchChannel_addChannel() throws Exception {
		return channelResource.postChannel(randomChannel());
	}

	@Override
	protected Channel testPatchChannelByExternalReferenceCode_addChannel()
		throws Exception {

		return channelResource.postChannel(randomChannel());
	}

	@Override
	protected Channel testPostChannel_addChannel(Channel channel)
		throws Exception {

		return channelResource.postChannel(channel);
	}

	@Override
	protected Channel testPutChannel_addChannel() throws Exception {
		return channelResource.postChannel(randomChannel());
	}

	@Override
	protected Channel testPutChannelByExternalReferenceCode_addChannel()
		throws Exception {

		return channelResource.postChannel(randomChannel());
	}

	private AccountEntry _addAccountEntry(String type) throws Exception {
		return _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(), type,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	private long _getCommerceChannelRelId() throws Exception {
		if (_accountEntry == null) {
			_accountEntry = _addAccountEntry(
				AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);
		}

		if (_address == null) {
			_address = _addressLocalService.addAddress(
				RandomTestUtil.randomString(), _user.getUserId(),
				AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
				0, 0, 0, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), false,
				RandomTestUtil.randomString(), false,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_serviceContext);
		}

		Channel channel = channelResource.postChannel(randomChannel());

		if (_commerceChannelRel == null) {
			_commerceChannelRel =
				_commerceChannelRelLocalService.addCommerceChannelRel(
					Address.class.getName(), _address.getAddressId(),
					channel.getId(), _serviceContext);
		}

		return _commerceChannelRel.getCommerceChannelRelId();
	}

	private void _testPatchChannelByExternalReferenceCodeWithAccountExternalReferenceCode()
		throws Exception {

		Channel postChannel =
			testPatchChannelByExternalReferenceCode_addChannel();

		Channel randomPatchChannel = randomPatchChannel();

		AccountEntry accountEntry = _addAccountEntry(
			AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER);

		randomPatchChannel.setAccountId(0L);
		randomPatchChannel.setAccountExternalReferenceCode(
			accountEntry.getExternalReferenceCode());

		Channel patchChannel =
			channelResource.patchChannelByExternalReferenceCode(
				postChannel.getExternalReferenceCode(), randomPatchChannel);

		randomPatchChannel.setAccountId(accountEntry.getAccountEntryId());

		Assert.assertEquals(
			accountEntry.getAccountEntryId(),
			GetterUtil.getLong(patchChannel.getAccountId()));
		Assert.assertEquals(
			accountEntry.getExternalReferenceCode(),
			patchChannel.getAccountExternalReferenceCode());

		Channel expectedPatchChannel = postChannel.clone();

		BeanTestUtil.copyProperties(randomPatchChannel, expectedPatchChannel);

		Channel getChannel = channelResource.getChannelByExternalReferenceCode(
			patchChannel.getExternalReferenceCode());

		assertEquals(expectedPatchChannel, getChannel);
		assertValid(getChannel);
		Assert.assertEquals(
			accountEntry.getAccountEntryId(),
			GetterUtil.getLong(getChannel.getAccountId()));
		Assert.assertEquals(
			accountEntry.getExternalReferenceCode(),
			getChannel.getAccountExternalReferenceCode());
	}

	private void _testPatchChannelWithAccountExternalReferenceCode()
		throws Exception {

		Channel postChannel = testPatchChannel_addChannel();

		Channel randomPatchChannel = randomPatchChannel();

		AccountEntry accountEntry = _addAccountEntry(
			AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER);

		randomPatchChannel.setAccountId(0L);
		randomPatchChannel.setAccountExternalReferenceCode(
			accountEntry.getExternalReferenceCode());

		Channel patchChannel = channelResource.patchChannel(
			postChannel.getId(), randomPatchChannel);

		randomPatchChannel.setAccountId(accountEntry.getAccountEntryId());

		Assert.assertEquals(
			accountEntry.getAccountEntryId(),
			GetterUtil.getLong(patchChannel.getAccountId()));
		Assert.assertEquals(
			accountEntry.getExternalReferenceCode(),
			patchChannel.getAccountExternalReferenceCode());

		Channel expectedPatchChannel = postChannel.clone();

		BeanTestUtil.copyProperties(randomPatchChannel, expectedPatchChannel);

		Channel getChannel = channelResource.getChannel(patchChannel.getId());

		assertEquals(expectedPatchChannel, getChannel);
		assertValid(getChannel);
		Assert.assertEquals(
			accountEntry.getAccountEntryId(),
			GetterUtil.getLong(getChannel.getAccountId()));
		Assert.assertEquals(
			accountEntry.getExternalReferenceCode(),
			getChannel.getAccountExternalReferenceCode());
	}

	private void _testPostChannelWithAccountExternalReferenceCode()
		throws Exception {

		Channel randomChannel = randomChannel();

		AccountEntry accountEntry = _addAccountEntry(
			AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER);

		randomChannel.setAccountId(0L);
		randomChannel.setAccountExternalReferenceCode(
			accountEntry.getExternalReferenceCode());

		Channel postChannel = testPostChannel_addChannel(randomChannel);

		randomChannel.setAccountId(accountEntry.getAccountEntryId());

		assertEquals(randomChannel, postChannel);
		assertValid(postChannel);
		Assert.assertEquals(
			accountEntry.getAccountEntryId(),
			GetterUtil.getLong(postChannel.getAccountId()));
		Assert.assertEquals(
			accountEntry.getExternalReferenceCode(),
			postChannel.getAccountExternalReferenceCode());
	}

	private void _testPutChannelByExternalReferenceCodeWithAccountExternalReferenceCode()
		throws Exception {

		Channel postChannel =
			testPutChannelByExternalReferenceCode_addChannel();

		Channel randomChannel = randomChannel();

		AccountEntry accountEntry = _addAccountEntry(
			AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER);

		randomChannel.setAccountId(0L);
		randomChannel.setAccountExternalReferenceCode(
			accountEntry.getExternalReferenceCode());

		Channel putChannel = channelResource.putChannelByExternalReferenceCode(
			postChannel.getExternalReferenceCode(), randomChannel);

		randomChannel.setAccountId(accountEntry.getAccountEntryId());

		assertEquals(randomChannel, putChannel);
		assertValid(putChannel);
		Assert.assertEquals(
			accountEntry.getAccountEntryId(),
			GetterUtil.getLong(putChannel.getAccountId()));
		Assert.assertEquals(
			accountEntry.getExternalReferenceCode(),
			putChannel.getAccountExternalReferenceCode());

		Channel getChannel = channelResource.getChannelByExternalReferenceCode(
			putChannel.getExternalReferenceCode());

		assertEquals(randomChannel, getChannel);
		assertValid(getChannel);
		Assert.assertEquals(
			accountEntry.getAccountEntryId(),
			GetterUtil.getLong(getChannel.getAccountId()));
		Assert.assertEquals(
			accountEntry.getExternalReferenceCode(),
			getChannel.getAccountExternalReferenceCode());

		Channel newChannel =
			testPutChannelByExternalReferenceCode_createChannel();

		accountEntry = _addAccountEntry(
			AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER);

		newChannel.setAccountId(0L);
		newChannel.setAccountExternalReferenceCode(
			accountEntry.getExternalReferenceCode());

		putChannel = channelResource.putChannelByExternalReferenceCode(
			newChannel.getExternalReferenceCode(), newChannel);

		newChannel.setAccountId(accountEntry.getAccountEntryId());

		assertEquals(newChannel, putChannel);
		assertValid(putChannel);
		Assert.assertEquals(
			accountEntry.getAccountEntryId(),
			GetterUtil.getLong(putChannel.getAccountId()));
		Assert.assertEquals(
			accountEntry.getExternalReferenceCode(),
			putChannel.getAccountExternalReferenceCode());

		getChannel = channelResource.getChannelByExternalReferenceCode(
			putChannel.getExternalReferenceCode());

		assertEquals(newChannel, getChannel);

		Assert.assertEquals(
			accountEntry.getAccountEntryId(),
			GetterUtil.getLong(getChannel.getAccountId()));
		Assert.assertEquals(
			accountEntry.getExternalReferenceCode(),
			getChannel.getAccountExternalReferenceCode());

		Assert.assertEquals(
			newChannel.getExternalReferenceCode(),
			putChannel.getExternalReferenceCode());
	}

	private void _testPutChannelWithAccountExternalReferenceCode()
		throws Exception {

		Channel postChannel = testPutChannel_addChannel();

		Channel randomChannel = randomChannel();

		AccountEntry accountEntry = _addAccountEntry(
			AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER);

		randomChannel.setAccountId(0L);
		randomChannel.setAccountExternalReferenceCode(
			accountEntry.getExternalReferenceCode());

		Channel putChannel = channelResource.putChannel(
			postChannel.getId(), randomChannel);

		randomChannel.setAccountId(accountEntry.getAccountEntryId());

		assertEquals(randomChannel, putChannel);
		assertValid(putChannel);
		Assert.assertEquals(
			accountEntry.getAccountEntryId(),
			GetterUtil.getLong(putChannel.getAccountId()));
		Assert.assertEquals(
			accountEntry.getExternalReferenceCode(),
			putChannel.getAccountExternalReferenceCode());

		Channel getChannel = channelResource.getChannel(putChannel.getId());

		assertEquals(randomChannel, getChannel);
		assertValid(getChannel);
		Assert.assertEquals(
			accountEntry.getAccountEntryId(),
			GetterUtil.getLong(getChannel.getAccountId()));
		Assert.assertEquals(
			accountEntry.getExternalReferenceCode(),
			getChannel.getAccountExternalReferenceCode());
	}

	private Channel _toChannel(CommerceChannel commerceChannel) {
		return new Channel() {
			{
				accountId = commerceChannel.getAccountEntryId();
				currencyCode = commerceChannel.getCommerceCurrencyCode();
				externalReferenceCode =
					commerceChannel.getExternalReferenceCode();
				id = commerceChannel.getCommerceChannelId();
				name = commerceChannel.getName();
				siteGroupId = commerceChannel.getSiteGroupId();
				type = commerceChannel.getType();
			}
		};
	}

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@DeleteAfterTestRun
	private Address _address;

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@DeleteAfterTestRun
	private CommerceChannelRel _commerceChannelRel;

	@Inject
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	private CommerceCurrency _commerceCurrency;
	private ServiceContext _serviceContext;
	private User _user;

}