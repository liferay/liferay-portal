/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.resource.v1_0;

import com.liferay.account.exception.NoSuchEntryException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Attachment;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.AttachmentDTOConverterContext;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.AttachmentResource;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/attachment.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = AttachmentResource.class
)
@CTAware
public class AttachmentResourceImpl extends BaseAttachmentResourceImpl {

	@NestedField(parentClass = Product.class, value = "attachments")
	@Override
	public Page<Attachment> getChannelProductAttachmentsPage(
			Long channelId, @NestedFieldId("productId") Long productId,
			Long accountId, Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.fetchCPDefinitionByCProductId(
				productId, true);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + productId);
		}

		return _getAttachmentPage(
			cpDefinition,
			_getAccountId(
				accountId,
				_commerceChannelLocalService.getCommerceChannel(channelId)),
			CPAttachmentFileEntryConstants.TYPE_OTHER, pagination);
	}

	@NestedField(parentClass = Product.class, value = "images")
	@Override
	public Page<Attachment> getChannelProductImagesPage(
			Long channelId, @NestedFieldId("productId") Long productId,
			Long accountId, Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.fetchCPDefinitionByCProductId(
				productId, true);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + productId);
		}

		return _getAttachmentPage(
			cpDefinition,
			_getAccountId(
				accountId,
				_commerceChannelLocalService.getCommerceChannel(channelId)),
			CPAttachmentFileEntryConstants.TYPE_IMAGE, pagination);
	}

	private Long _getAccountId(Long accountId, CommerceChannel commerceChannel)
		throws Exception {

		if ((accountId != null) && (accountId > 0)) {
			AccountEntry accountEntry = _accountEntryService.fetchAccountEntry(
				accountId);

			if (accountEntry != null) {
				return accountEntry.getAccountEntryId();
			}
		}

		int countUserAccounts =
			_commerceAccountHelper.countUserCommerceAccounts(
				contextUser.getUserId(), commerceChannel.getGroupId());

		if (countUserAccounts > 1) {
			if (accountId == null) {
				throw new NoSuchEntryException();
			}
		}
		else {
			long[] accountIds =
				_commerceAccountHelper.getUserCommerceAccountIds(
					contextUser.getUserId(), commerceChannel.getGroupId());

			if (accountIds.length == 0) {
				AccountEntry accountEntry =
					_accountEntryLocalService.getGuestAccountEntry(
						contextCompany.getCompanyId());

				accountIds = new long[] {accountEntry.getAccountEntryId()};
			}

			return accountIds[0];
		}

		return accountId;
	}

	private Page<Attachment> _getAttachmentPage(
			CPDefinition cpDefinition, long accountId, int type,
			Pagination pagination)
		throws Exception {

		return Page.of(
			transform(
				_cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
					_classNameLocalService.getClassNameId(
						cpDefinition.getModelClass()),
					cpDefinition.getCPDefinitionId(), type,
					WorkflowConstants.STATUS_APPROVED,
					pagination.getStartPosition(), pagination.getEndPosition()),
				cpAttachmentFileEntry -> _toAttachment(
					accountId, cpAttachmentFileEntry)),
			pagination,
			_cpAttachmentFileEntryLocalService.getCPAttachmentFileEntriesCount(
				_classNameLocalService.getClassNameId(
					cpDefinition.getModelClass()),
				cpDefinition.getCPDefinitionId(), type,
				WorkflowConstants.STATUS_APPROVED));
	}

	private Attachment _toAttachment(
			long accountId, CPAttachmentFileEntry cpAttachmentFileEntry)
		throws Exception {

		return _attachmentDTOConverter.toDTO(
			new AttachmentDTOConverterContext(
				cpAttachmentFileEntry.getCPAttachmentFileEntryId(),
				contextAcceptLanguage.getPreferredLocale(), accountId));
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.AttachmentDTOConverter)"
	)
	private DTOConverter<CPAttachmentFileEntry, Attachment>
		_attachmentDTOConverter;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

}