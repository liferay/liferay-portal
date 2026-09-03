/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.resource.v1_0;

import com.liferay.commerce.exception.NoSuchOrderAttachmentException;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.order.CommerceOrderAttachmentURLProvider;
import com.liferay.commerce.service.CommerceOrderAttachmentService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.Attachment;
import com.liferay.headless.commerce.admin.order.internal.odata.entity.v1_0.AttachmentEntityModel;
import com.liferay.headless.commerce.admin.order.resource.v1_0.AttachmentResource;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
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
	public void deleteOrderAttachment(Long orderId, Long attachmentId)
		throws Exception {

		_commerceOrderService.getCommerceOrder(orderId);

		_commerceOrderAttachmentService.deleteCommerceOrderAttachment(
			attachmentId);
	}

	@Override
	public void
			deleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode(
				String externalReferenceCode,
				String attachmentExternalReferenceCode)
		throws Exception {

		CommerceOrder commerceOrder = _getCommerceOrderByExternalReferenceCode(
			externalReferenceCode);

		CommerceOrderAttachment commerceOrderAttachment =
			_getCommerceOrderAttachmentByExternalReferenceCode(
				attachmentExternalReferenceCode);

		deleteOrderAttachment(
			commerceOrder.getCommerceOrderId(),
			commerceOrderAttachment.getCommerceOrderAttachmentId());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public Attachment getOrderAttachment(Long orderId, Long attachmentId)
		throws Exception {

		_commerceOrderService.getCommerceOrder(orderId);

		return _toAttachment(
			_commerceOrderAttachmentService.getCommerceOrderAttachment(
				attachmentId));
	}

	@Override
	public Page<Attachment> getOrderAttachmentsPage(
			Long orderId, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			orderId);

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
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public Attachment
			getOrderByExternalReferenceCodeAttachmentByExternalReferenceCode(
				String externalReferenceCode,
				String attachmentExternalReferenceCode)
		throws Exception {

		CommerceOrder commerceOrder = _getCommerceOrderByExternalReferenceCode(
			externalReferenceCode);

		CommerceOrderAttachment commerceOrderAttachment =
			_getCommerceOrderAttachmentByExternalReferenceCode(
				attachmentExternalReferenceCode);

		return getOrderAttachment(
			commerceOrder.getCommerceOrderId(),
			commerceOrderAttachment.getCommerceOrderAttachmentId());
	}

	@Override
	public Page<Attachment> getOrderByExternalReferenceCodeAttachmentsPage(
			String externalReferenceCode, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		CommerceOrder commerceOrder = _getCommerceOrderByExternalReferenceCode(
			externalReferenceCode);

		return getOrderAttachmentsPage(
			commerceOrder.getCommerceOrderId(), search, filter, pagination,
			sorts);
	}

	@Override
	public Attachment patchOrderAttachment(
			Long orderId, Long attachmentId, Attachment attachment)
		throws Exception {

		_commerceOrderService.getCommerceOrder(orderId);

		CommerceOrderAttachment commerceOrderAttachment =
			_commerceOrderAttachmentService.getCommerceOrderAttachment(
				attachmentId);

		return _toAttachment(
			_commerceOrderAttachmentService.updateCommerceOrderAttachment(
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				GetterUtil.get(
					attachment.getPriority(),
					commerceOrderAttachment.getPriority()),
				GetterUtil.get(
					attachment.getRestricted(),
					commerceOrderAttachment.isRestricted()),
				GetterUtil.get(
					attachment.getTitle(), commerceOrderAttachment.getTitle()),
				GetterUtil.get(
					attachment.getType(), commerceOrderAttachment.getType())));
	}

	@Override
	public Attachment
			patchOrderByExternalReferenceCodeAttachmentByExternalReferenceCode(
				String externalReferenceCode,
				String attachmentExternalReferenceCode, Attachment attachment)
		throws Exception {

		CommerceOrder commerceOrder = _getCommerceOrderByExternalReferenceCode(
			externalReferenceCode);

		CommerceOrderAttachment commerceOrderAttachment =
			_getCommerceOrderAttachmentByExternalReferenceCode(
				attachmentExternalReferenceCode);

		return patchOrderAttachment(
			commerceOrder.getCommerceOrderId(),
			commerceOrderAttachment.getCommerceOrderAttachmentId(), attachment);
	}

	@Override
	public Attachment postOrderAttachment(Long orderId, Attachment attachment)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			orderId);

		return _toAttachment(
			_commerceOrderAttachmentService.addCommerceOrderAttachment(
				commerceOrder.getCommerceOrderId(),
				GetterUtil.get(attachment.getPriority(), 0.0),
				GetterUtil.get(attachment.getRestricted(), false),
				attachment.getTitle(), attachment.getType(),
				GetterUtil.get(attachment.getFileName(), attachment.getTitle()),
				new ByteArrayInputStream(
					Base64.decode(attachment.getAttachment()))));
	}

	@Override
	public Attachment postOrderByExternalReferenceCodeAttachment(
			String externalReferenceCode, Attachment attachment)
		throws Exception {

		CommerceOrder commerceOrder = _getCommerceOrderByExternalReferenceCode(
			externalReferenceCode);

		return postOrderAttachment(
			commerceOrder.getCommerceOrderId(), attachment);
	}

	private Map<String, String> _getActionURL(
		Map<String, String> action, long commerceOrderId, long attachmentId) {

		if (action == null) {
			return null;
		}

		action.put(
			"href",
			StringUtil.replace(
				action.get("href"),
				new String[] {"{attachmentId}", "{orderId}"},
				new String[] {
					String.valueOf(attachmentId),
					String.valueOf(commerceOrderId)
				}));

		return action;
	}

	private CommerceOrderAttachment
			_getCommerceOrderAttachmentByExternalReferenceCode(
				String externalReferenceCode)
		throws Exception {

		CommerceOrderAttachment commerceOrderAttachment =
			_commerceOrderAttachmentService.
				fetchCommerceOrderAttachmentByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrderAttachment == null) {
			throw new NoSuchOrderAttachmentException(
				"Unable to find commerce order attachment with external " +
					"reference code " + externalReferenceCode);
		}

		return commerceOrderAttachment;
	}

	private CommerceOrder _getCommerceOrderByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find commerce order with external reference code " +
					externalReferenceCode);
		}

		return commerceOrder;
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

		long attachmentId =
			commerceOrderAttachment.getCommerceOrderAttachmentId();
		long commerceOrderId = commerceOrderAttachment.getCommerceOrderId();
		FileEntry fileEntry = _dlAppLocalService.fetchFileEntry(
			commerceOrderAttachment.getFileEntryId());

		return new Attachment() {
			{
				setActions(
					() -> HashMapBuilder.<String, Map<String, String>>put(
						"delete",
						_getActionURL(
							ActionUtil.addAction(
								"DELETE", AttachmentResourceImpl.class,
								attachmentId, "deleteOrderAttachment",
								_commerceOrderAttachmentModelResourcePermission,
								commerceOrderId, contextUriInfo),
							commerceOrderId, attachmentId)
					).put(
						"update",
						_getActionURL(
							ActionUtil.addAction(
								"UPDATE", AttachmentResourceImpl.class,
								attachmentId, "patchOrderAttachment",
								_commerceOrderAttachmentModelResourcePermission,
								commerceOrderId, contextUriInfo),
							commerceOrderId, attachmentId)
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
				setFileName(
					() -> {
						if (fileEntry == null) {
							return null;
						}

						return fileEntry.getFileName();
					});
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
								attachmentId, contextHttpServletRequest);
					});
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