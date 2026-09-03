/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.internal.resource.v1_0;

import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.order.CommerceOrderAttachmentURLProvider;
import com.liferay.commerce.service.CommerceOrderAttachmentService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.Attachment;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.AttachmentBase64;
import com.liferay.headless.commerce.delivery.order.internal.odata.entity.v1_0.AttachmentEntityModel;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.AttachmentResource;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.io.ByteArrayInputStream;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Stefano Motta
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/attachment.properties",
	scope = ServiceScope.PROTOTYPE, service = AttachmentResource.class
)
public class AttachmentResourceImpl extends BaseAttachmentResourceImpl {

	@Override
	public void deletePlacedOrderAttachment(
			Long attachmentId, Long placedOrderId)
		throws Exception {

		if (FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-6252")) {

			_commerceOrderAttachmentService.deleteCommerceOrderAttachment(
				attachmentId);

			return;
		}

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			placedOrderId);

		_commerceOrderService.deleteAttachmentFileEntry(
			attachmentId, commerceOrder.getCommerceOrderId());
	}

	@Override
	public void
			deletePlacedOrderByExternalReferenceCodeAttachmentByExternalReferenceCodeAttachmentExternalReferenceCode(
				String attachmentExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		if (FeatureFlagManagerUtil.isEnabled(
				commerceOrder.getCompanyId(), "LPD-6252")) {

			CommerceOrderAttachment commerceOrderAttachment =
				_commerceOrderAttachmentService.
					fetchCommerceOrderAttachmentByExternalReferenceCode(
						attachmentExternalReferenceCode,
						contextCompany.getCompanyId());

			if (commerceOrderAttachment != null) {
				_commerceOrderAttachmentService.deleteCommerceOrderAttachment(
					commerceOrderAttachment.getCommerceOrderAttachmentId());
			}

			return;
		}

		LocalRepository localRepository = commerceOrder.getLocalRepository();

		FileEntry fileEntry =
			localRepository.getFileEntryByExternalReferenceCode(
				attachmentExternalReferenceCode);

		deletePlacedOrderAttachment(
			fileEntry.getFileEntryId(), commerceOrder.getCommerceOrderId());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public Page<Attachment> getPlacedOrderAttachmentsPage(
			Long placedOrderId, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			placedOrderId);

		if (FeatureFlagManagerUtil.isEnabled(
				commerceOrder.getCompanyId(), "LPD-6252")) {

			return SearchUtil.search(
				Collections.emptyMap(),
				booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
				CommerceOrderAttachment.class.getName(), search, pagination,
				queryConfig -> queryConfig.setSelectedFieldNames(
					Field.ENTRY_CLASS_PK),
				searchContext -> {
					searchContext.setAttribute(
						"commerceOrderId", commerceOrder.getCommerceOrderId());
					searchContext.setCompanyId(contextCompany.getCompanyId());
				},
				sorts,
				document -> _toAttachment(
					_commerceOrderAttachmentService.getCommerceOrderAttachment(
						GetterUtil.getLong(
							document.get(Field.ENTRY_CLASS_PK)))));
		}

		return Page.of(
			transform(
				commerceOrder.getAttachmentFileEntries(
					pagination.getStartPosition(), pagination.getEndPosition()),
				this::_toAttachment),
			pagination, commerceOrder.getAttachmentFileEntriesCount());
	}

	@Override
	public Page<Attachment>
			getPlacedOrderByExternalReferenceCodeAttachmentsPage(
				String externalReferenceCode, String search, Filter filter,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		return getPlacedOrderAttachmentsPage(
			commerceOrder.getCommerceOrderId(), search, filter, pagination,
			sorts);
	}

	@Override
	public Attachment postPlacedOrderAttachmentByBase64(
			Long placedOrderId, AttachmentBase64 attachmentBase64)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			placedOrderId);

		if (FeatureFlagManagerUtil.isEnabled(
				commerceOrder.getCompanyId(), "LPD-6252")) {

			return _toAttachment(
				_commerceOrderAttachmentService.addCommerceOrderAttachment(
					commerceOrder.getCommerceOrderId(),
					GetterUtil.getDouble(attachmentBase64.getPriority()),
					GetterUtil.getBoolean(attachmentBase64.getRestricted()),
					attachmentBase64.getTitle(), attachmentBase64.getType(),
					attachmentBase64.getTitle(),
					new ByteArrayInputStream(
						Base64.decode(attachmentBase64.getAttachment()))));
		}

		return _toAttachment(
			_commerceOrderService.addAttachmentFileEntry(
				attachmentBase64.getExternalReferenceCode(),
				contextUser.getUserId(), commerceOrder.getCommerceOrderId(),
				attachmentBase64.getTitle(),
				new ByteArrayInputStream(
					Base64.decode(attachmentBase64.getAttachment()))));
	}

	@Override
	public Attachment postPlacedOrderByExternalReferenceCodeAttachmentByBase64(
			String externalReferenceCode, AttachmentBase64 attachmentBase64)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		return postPlacedOrderAttachmentByBase64(
			commerceOrder.getCommerceOrderId(), attachmentBase64);
	}

	private Map<String, String> _addDeleteAction(
		long commerceOrderId, long commerceOrderAttachmentId) {

		Map<String, String> action = ActionUtil.addAction(
			ActionKeys.DELETE, AttachmentResourceImpl.class,
			commerceOrderAttachmentId, "deletePlacedOrderAttachment",
			_commerceOrderAttachmentModelResourcePermission, commerceOrderId,
			contextUriInfo);

		if (action == null) {
			return null;
		}

		action.put(
			"href",
			StringUtil.replace(
				action.get("href"), "{attachmentId}",
				String.valueOf(commerceOrderAttachmentId)));

		return action;
	}

	private String _getTypeLabel(String type) {
		if (Validator.isNull(type)) {
			return type;
		}

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					"L_COMMERCE_ORDER_ATTACHMENT_TYPES",
					contextCompany.getCompanyId());

		if (listTypeDefinition == null) {
			return type;
		}

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.fetchListTypeEntry(
				listTypeDefinition.getListTypeDefinitionId(), type);

		if (listTypeEntry == null) {
			return type;
		}

		return listTypeEntry.getName(
			contextAcceptLanguage.getPreferredLocale());
	}

	private Attachment _toAttachment(
			CommerceOrderAttachment commerceOrderAttachment)
		throws PortalException {

		FileEntry fileEntry = _dlAppLocalService.fetchFileEntry(
			commerceOrderAttachment.getFileEntryId());

		return new Attachment() {
			{
				setActions(
					() -> HashMapBuilder.<String, Map<String, String>>put(
						"delete",
						_addDeleteAction(
							commerceOrderAttachment.getCommerceOrderId(),
							commerceOrderAttachment.
								getCommerceOrderAttachmentId())
					).build());
				setDateModified(commerceOrderAttachment::getModifiedDate);
				setExtension(
					() -> {
						if (fileEntry == null) {
							return null;
						}

						return fileEntry.getExtension();
					});
				setExternalReferenceCode(
					commerceOrderAttachment::getExternalReferenceCode);
				setId(commerceOrderAttachment::getCommerceOrderAttachmentId);
				setPriority(commerceOrderAttachment::getPriority);
				setRestricted(commerceOrderAttachment::isRestricted);
				setTitle(commerceOrderAttachment::getTitle);
				setType(commerceOrderAttachment::getType);
				setTypeLabel(
					() -> _getTypeLabel(commerceOrderAttachment.getType()));
				setUrl(
					() -> {
						if (fileEntry == null) {
							return null;
						}

						return _commerceOrderAttachmentURLProvider.
							getDownloadURL(
								commerceOrderAttachment.
									getCommerceOrderAttachmentId(),
								contextHttpServletRequest);
					});
			}
		};
	}

	private Attachment _toAttachment(FileEntry fileEntry) {
		return new Attachment() {
			{
				setExternalReferenceCode(fileEntry::getExternalReferenceCode);
				setId(fileEntry::getFileEntryId);
				setTitle(fileEntry::getTitle);
				setUrl(
					() -> DLURLHelperUtil.getDownloadURL(
						fileEntry, fileEntry.getLatestFileVersion(), null,
						StringPool.BLANK, true, true));
			}
		};
	}

	private static final EntityModel _entityModel = new AttachmentEntityModel();

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrderAttachment)"
	)
	private ModelResourcePermission<CommerceOrderAttachment>
		_commerceOrderAttachmentModelResourcePermission;

	@Reference
	private CommerceOrderAttachmentService _commerceOrderAttachmentService;

	@Reference
	private CommerceOrderAttachmentURLProvider
		_commerceOrderAttachmentURLProvider;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

}