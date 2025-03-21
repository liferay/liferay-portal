/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.resource.v1_0;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.product.exception.NoSuchChannelException;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.AccountAddressChannel;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.Channel;
import com.liferay.headless.commerce.admin.channel.internal.odata.entity.v1_0.ChannelEntityModel;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ChannelResource;
import com.liferay.headless.commerce.core.util.CommerceCurrencyUtil;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/channel.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ChannelResource.class
)
public class ChannelResourceImpl extends BaseChannelResourceImpl {

	@Override
	public void deleteChannel(Long channelId) throws Exception {
		_commerceChannelService.deleteCommerceChannel(channelId);
	}

	@Override
	public void deleteChannelByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelService.fetchCommerceChannelByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceChannel == null) {
			throw new NoSuchChannelException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		_commerceChannelService.deleteCommerceChannel(
			commerceChannel.getCommerceChannelId());
	}

	@NestedField(parentClass = AccountAddressChannel.class, value = "channel")
	@Override
	public Channel getAccountAddressChannelChannel(Long id) throws Exception {
		CommerceChannelRel commerceChannelRel =
			_commerceChannelRelService.getCommerceChannelRel(id);

		return _channelDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceChannelRel.getCommerceChannelId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	@Override
	public Channel getChannel(Long channelId) throws Exception {
		CommerceChannel commerceChannel =
			_commerceChannelService.fetchCommerceChannel(channelId);

		if (commerceChannel == null) {
			throw new NoSuchChannelException();
		}

		return _toChannel(commerceChannel);
	}

	@Override
	public Channel getChannelByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelService.fetchCommerceChannelByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceChannel == null) {
			throw new NoSuchChannelException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		return _toChannel(commerceChannel);
	}

	@Override
	public Page<Channel> getChannelsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CommerceChannel.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toChannel(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Channel patchChannel(Long channelId, Channel channel)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(channelId);

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.getCommerceCurrency(
				contextCompany.getCompanyId(),
				commerceChannel.getCommerceCurrencyCode());

		try {
			commerceCurrency = CommerceCurrencyUtil.getCommerceCurrency(
				contextCompany.getCompanyId(), channel.getCurrencyCode(),
				channel.getCurrencyExternalReferenceCode(),
				GetterUtil.getLong(channel.getCurrencyId()));
		}
		catch (NoSuchCurrencyException noSuchCurrencyException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchCurrencyException);
			}
		}

		return _toChannel(
			_commerceChannelService.updateCommerceChannel(
				channelId,
				_getAccountEntryId(
					channel, commerceChannel.getAccountEntryId()),
				commerceChannel.getSiteGroupId(),
				GetterUtil.getString(
					channel.getName(), commerceChannel.getName()),
				GetterUtil.getString(
					channel.getType(), commerceChannel.getType()),
				commerceChannel.getTypeSettingsUnicodeProperties(),
				commerceCurrency.getCode(),
				commerceChannel.getPriceDisplayType(),
				commerceChannel.isDiscountsTargetNetPrice()));
	}

	@Override
	public Channel patchChannelByExternalReferenceCode(
			String externalReferenceCode, Channel channel)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelService.fetchCommerceChannelByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceChannel == null) {
			throw new NoSuchChannelException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.getCommerceCurrency(
				contextCompany.getCompanyId(),
				commerceChannel.getCommerceCurrencyCode());

		try {
			commerceCurrency = CommerceCurrencyUtil.getCommerceCurrency(
				contextCompany.getCompanyId(), channel.getCurrencyCode(),
				channel.getCurrencyExternalReferenceCode(),
				GetterUtil.getLong(channel.getCurrencyId()));
		}
		catch (NoSuchCurrencyException noSuchCurrencyException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchCurrencyException);
			}
		}

		return _toChannel(
			_commerceChannelService.updateCommerceChannel(
				commerceChannel.getCommerceChannelId(),
				_getAccountEntryId(
					channel, commerceChannel.getAccountEntryId()),
				commerceChannel.getSiteGroupId(),
				GetterUtil.getString(
					channel.getName(), commerceChannel.getName()),
				GetterUtil.getString(
					channel.getType(), commerceChannel.getType()),
				commerceChannel.getTypeSettingsUnicodeProperties(),
				commerceCurrency.getCode(),
				commerceChannel.getPriceDisplayType(),
				commerceChannel.isDiscountsTargetNetPrice()));
	}

	@Override
	public Channel postChannel(Channel channel) throws Exception {
		CommerceCurrency commerceCurrency =
			CommerceCurrencyUtil.getCommerceCurrency(
				contextCompany.getCompanyId(), channel.getCurrencyCode(),
				channel.getCurrencyExternalReferenceCode(),
				GetterUtil.getLong(channel.getCurrencyId()));

		return _toChannel(
			_commerceChannelService.addCommerceChannel(
				channel.getExternalReferenceCode(),
				_getAccountEntryId(
					channel, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT),
				GetterUtil.get(channel.getSiteGroupId(), 0), channel.getName(),
				channel.getType(), null, commerceCurrency.getCode(),
				_serviceContextHelper.getServiceContext(contextUser)));
	}

	@Override
	public Channel putChannel(Long channelId, Channel channel)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelService.fetchCommerceChannel(channelId);

		if (commerceChannel == null) {
			return postChannel(channel);
		}

		CommerceCurrency commerceCurrency =
			CommerceCurrencyUtil.getCommerceCurrency(
				contextCompany.getCompanyId(), channel.getCurrencyCode(),
				channel.getCurrencyExternalReferenceCode(),
				GetterUtil.getLong(channel.getCurrencyId()));

		return _toChannel(
			_commerceChannelService.updateCommerceChannel(
				channelId,
				_getAccountEntryId(
					channel, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT),
				channel.getSiteGroupId(), channel.getName(), channel.getType(),
				null, commerceCurrency.getCode(), null, false));
	}

	@Override
	public Channel putChannelByExternalReferenceCode(
			String externalReferenceCode, Channel channel)
		throws Exception {

		CommerceCurrency commerceCurrency =
			CommerceCurrencyUtil.getCommerceCurrency(
				contextCompany.getCompanyId(), channel.getCurrencyCode(),
				channel.getCurrencyExternalReferenceCode(),
				GetterUtil.getLong(channel.getCurrencyId()));

		return _toChannel(
			_commerceChannelService.addOrUpdateCommerceChannel(
				externalReferenceCode,
				_getAccountEntryId(
					channel, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT),
				GetterUtil.getLong(channel.getSiteGroupId()), channel.getName(),
				channel.getType(), null, commerceCurrency.getCode(),
				_serviceContextHelper.getServiceContext()));
	}

	private long _getAccountEntryId(Channel channel, long defaultAccountEntryId)
		throws Exception {

		long accountEntryId = GetterUtil.getLong(channel.getAccountId());

		if (accountEntryId > 0) {
			return accountEntryId;
		}

		AccountEntry accountEntry =
			_accountEntryService.fetchAccountEntryByExternalReferenceCode(
				GetterUtil.getString(channel.getAccountExternalReferenceCode()),
				contextCompany.getCompanyId());

		if (accountEntry != null) {
			return accountEntry.getAccountEntryId();
		}

		return defaultAccountEntryId;
	}

	private Channel _toChannel(CommerceChannel commerceChannel)
		throws Exception {

		return _toChannel(commerceChannel.getCommerceChannelId());
	}

	private Channel _toChannel(Long commerceChannelId) throws Exception {
		return _channelDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceChannelId, contextAcceptLanguage.getPreferredLocale()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ChannelResourceImpl.class);

	private static final EntityModel _entityModel = new ChannelEntityModel();

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.channel.internal.dto.v1_0.converter.ChannelDTOConverter)"
	)
	private DTOConverter<CommerceChannel, Channel> _channelDTOConverter;

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}