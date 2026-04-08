/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.exception.NoSuchCPAttachmentFileEntryException;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPOptionService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Attachment;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.AttachmentBase64;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.AttachmentUrl;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.AttachmentUtil;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.ProductUtil;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.AttachmentResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.custom.field.CustomField;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.upload.UniqueFileNameProvider;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Zoltán Takács
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/attachment.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = AttachmentResource.class
)
@CTAware
public class AttachmentResourceImpl extends BaseAttachmentResourceImpl {

	@Override
	public void deleteAttachment(Long id) throws Exception {
		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpAttachmentFileEntryService.fetchCPAttachmentFileEntry(id);

		if (cpAttachmentFileEntry == null) {
			throw new NoSuchCPAttachmentFileEntryException(
				"Unable to find attachment " + id);
		}

		_cpAttachmentFileEntryService.deleteCPAttachmentFileEntry(id);
	}

	@Override
	public void deleteAttachmentByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpAttachmentFileEntryService.
				fetchCPAttachmentFileEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (cpAttachmentFileEntry == null) {
			throw new NoSuchCPAttachmentFileEntryException(
				"Unable to find attachment with external reference code " +
					externalReferenceCode);
		}

		_cpAttachmentFileEntryService.deleteCPAttachmentFileEntry(
			cpAttachmentFileEntry.getCPAttachmentFileEntryId());
	}

	@Override
	public Attachment getAttachmentByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpAttachmentFileEntryService.
				fetchCPAttachmentFileEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (cpAttachmentFileEntry == null) {
			throw new NoSuchCPAttachmentFileEntryException(
				"Unable to find attachment with external reference code " +
					externalReferenceCode);
		}

		return _toAttachment(
			cpAttachmentFileEntry.getCPAttachmentFileEntryId());
	}

	@Override
	public Page<Attachment> getProductByExternalReferenceCodeAttachmentsPage(
			String externalReferenceCode, Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId(),
					false);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		return _getAttachmentPage(
			cpDefinition, CPAttachmentFileEntryConstants.TYPE_OTHER,
			pagination);
	}

	@Override
	public Page<Attachment> getProductByExternalReferenceCodeImagesPage(
			String externalReferenceCode, Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId(),
					false);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		return _getAttachmentPage(
			cpDefinition, CPAttachmentFileEntryConstants.TYPE_IMAGE,
			pagination);
	}

	@NestedField(parentClass = Product.class, value = "attachments")
	@Override
	public Page<Attachment> getProductIdAttachmentsPage(
			@NestedFieldId(value = "productId") Long id, Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id, false);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _getAttachmentPage(
			cpDefinition, CPAttachmentFileEntryConstants.TYPE_OTHER,
			pagination);
	}

	@NestedField(parentClass = Product.class, value = "images")
	@Override
	public Page<Attachment> getProductIdImagesPage(
			@NestedFieldId(value = "productId") Long id, Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id, false);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _getAttachmentPage(
			cpDefinition, CPAttachmentFileEntryConstants.TYPE_IMAGE,
			pagination);
	}

	@Override
	public Attachment patchAttachmentByExternalReferenceCode(
			String externalReferenceCode, Attachment attachment)
		throws Exception {

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpAttachmentFileEntryService.
				fetchCPAttachmentFileEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (cpAttachmentFileEntry == null) {
			throw new NoSuchCPAttachmentFileEntryException(
				"Unable to find attachment with external reference code " +
					externalReferenceCode);
		}

		return _updateCPAttachmentFileEntry(
			attachment, cpAttachmentFileEntry,
			CPAttachmentFileEntryConstants.TYPE_OTHER);
	}

	@Override
	public Attachment postProductByExternalReferenceCodeAttachment(
			String externalReferenceCode, Attachment attachment)
		throws Exception {

		CPDefinition cpDefinition =
			ProductUtil.fetchCPDefinitionByCProductExternalReferenceCode(
				_cpDefinitionService, externalReferenceCode,
				contextCompany.getCompanyId());

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		return _addOrUpdateProductAttachment(cpDefinition, attachment);
	}

	@Override
	public Attachment postProductByExternalReferenceCodeAttachmentByBase64(
			String externalReferenceCode, AttachmentBase64 attachmentBase64)
		throws Exception {

		CPDefinition cpDefinition =
			ProductUtil.fetchCPDefinitionByCProductExternalReferenceCode(
				_cpDefinitionService, externalReferenceCode,
				contextCompany.getCompanyId());

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		return _addOrUpdateProductAttachment(cpDefinition, attachmentBase64);
	}

	@Override
	public Attachment postProductByExternalReferenceCodeAttachmentByUrl(
			String externalReferenceCode, AttachmentUrl attachmentUrl)
		throws Exception {

		CPDefinition cpDefinition =
			ProductUtil.fetchCPDefinitionByCProductExternalReferenceCode(
				_cpDefinitionService, externalReferenceCode,
				contextCompany.getCompanyId());

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		return _addOrUpdateProductAttachment(cpDefinition, attachmentUrl);
	}

	@Override
	public Attachment postProductByExternalReferenceCodeImage(
			String externalReferenceCode, Attachment attachment)
		throws Exception {

		CPDefinition cpDefinition =
			ProductUtil.fetchCPDefinitionByCProductExternalReferenceCode(
				_cpDefinitionService, externalReferenceCode,
				contextCompany.getCompanyId());

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		return _addOrUpdateProductImage(cpDefinition, attachment);
	}

	@Override
	public Attachment postProductByExternalReferenceCodeImageByBase64(
			String externalReferenceCode, AttachmentBase64 attachmentBase64)
		throws Exception {

		CPDefinition cpDefinition =
			ProductUtil.fetchCPDefinitionByCProductExternalReferenceCode(
				_cpDefinitionService, externalReferenceCode,
				contextCompany.getCompanyId());

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		return _addOrUpdateProductImage(cpDefinition, attachmentBase64);
	}

	@Override
	public Attachment postProductByExternalReferenceCodeImageByUrl(
			String externalReferenceCode, AttachmentUrl attachmentUrl)
		throws Exception {

		CPDefinition cpDefinition =
			ProductUtil.fetchCPDefinitionByCProductExternalReferenceCode(
				_cpDefinitionService, externalReferenceCode,
				contextCompany.getCompanyId());

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		return _addOrUpdateProductImage(cpDefinition, attachmentUrl);
	}

	@Override
	public Attachment postProductIdAttachment(Long id, Attachment attachment)
		throws Exception {

		CPDefinition cpDefinition = ProductUtil.fetchCPDefinitionByCProductId(
			_cpDefinitionService, id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _addOrUpdateProductAttachment(cpDefinition, attachment);
	}

	@Override
	public Attachment postProductIdAttachmentByBase64(
			Long id, AttachmentBase64 attachmentBase64)
		throws Exception {

		CPDefinition cpDefinition = ProductUtil.fetchCPDefinitionByCProductId(
			_cpDefinitionService, id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _addOrUpdateProductAttachment(cpDefinition, attachmentBase64);
	}

	@Override
	public Attachment postProductIdAttachmentByUrl(
			Long id, AttachmentUrl attachmentUrl)
		throws Exception {

		CPDefinition cpDefinition = ProductUtil.fetchCPDefinitionByCProductId(
			_cpDefinitionService, id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _addOrUpdateProductAttachment(cpDefinition, attachmentUrl);
	}

	@Override
	public Attachment postProductIdImage(Long id, Attachment attachment)
		throws Exception {

		CPDefinition cpDefinition = ProductUtil.fetchCPDefinitionByCProductId(
			_cpDefinitionService, id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _addOrUpdateProductImage(cpDefinition, attachment);
	}

	@Override
	public Attachment postProductIdImageByBase64(
			Long id, AttachmentBase64 attachmentBase64)
		throws Exception {

		CPDefinition cpDefinition = ProductUtil.fetchCPDefinitionByCProductId(
			_cpDefinitionService, id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _addOrUpdateProductImage(cpDefinition, attachmentBase64);
	}

	@Override
	public Attachment postProductIdImageByUrl(
			Long id, AttachmentUrl attachmentUrl)
		throws Exception {

		CPDefinition cpDefinition = ProductUtil.fetchCPDefinitionByCProductId(
			_cpDefinitionService, id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _addOrUpdateProductImage(cpDefinition, attachmentUrl);
	}

	@Override
	public Attachment putAttachmentByExternalReferenceCode(
			String externalReferenceCode, Attachment attachment)
		throws Exception {

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpAttachmentFileEntryService.
				fetchCPAttachmentFileEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (cpAttachmentFileEntry == null) {
			throw new NoSuchCPAttachmentFileEntryException(
				"Unable to find attachment with external reference code " +
					externalReferenceCode);
		}

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			cpAttachmentFileEntry.getGroupId());

		cpAttachmentFileEntry = AttachmentUtil.updateCPAttachmentFileEntry(
			cpAttachmentFileEntry, _cpAttachmentFileEntryService,
			_cpDefinitionOptionRelService, _cpDefinitionOptionValueRelService,
			_cpOptionService, _dlAppLocalService,
			_dlFileEntryModelResourcePermission, groupLocalService, attachment,
			cpAttachmentFileEntry.getClassPK(),
			CPAttachmentFileEntryConstants.TYPE_OTHER, serviceContext);

		return _toAttachment(
			cpAttachmentFileEntry.getCPAttachmentFileEntryId());
	}

	@Override
	public Page<Attachment> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	private Attachment _addOrUpdateAttachment(
			CPDefinition cpDefinition, int type, Attachment attachment)
		throws Exception {

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			cpDefinition.getGroupId());

		if (attachment.getTags() != null) {
			serviceContext.setAssetTagNames(attachment.getTags());
		}

		serviceContext.setExpandoBridgeAttributes(
			_getExpandoBridgeAttributes(
				CPAttachmentFileEntry.class.getName(),
				attachment.getCustomFields()));

		CPAttachmentFileEntry cpAttachmentFileEntry =
			AttachmentUtil.addOrUpdateCPAttachmentFileEntry(
				cpDefinition.getGroupId(), _cpAttachmentFileEntryService,
				_cpDefinitionOptionRelService,
				_cpDefinitionOptionValueRelService, _cpOptionService,
				_dlAppLocalService, _dlFileEntryModelResourcePermission,
				_groupLocalService, _uniqueFileNameProvider, attachment,
				_classNameLocalService.getClassNameId(
					cpDefinition.getModelClassName()),
				cpDefinition.getCPDefinitionId(), type, serviceContext);

		return _toAttachment(
			cpAttachmentFileEntry.getCPAttachmentFileEntryId());
	}

	private Attachment _addOrUpdateAttachment(
			CPDefinition cpDefinition, int type,
			AttachmentBase64 attachmentBase64)
		throws Exception {

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			cpDefinition.getGroupId());

		if (attachmentBase64.getTags() != null) {
			serviceContext.setAssetTagNames(attachmentBase64.getTags());
		}

		serviceContext.setExpandoBridgeAttributes(
			_getExpandoBridgeAttributes(
				CPAttachmentFileEntry.class.getName(),
				attachmentBase64.getCustomFields()));

		CPAttachmentFileEntry cpAttachmentFileEntry =
			AttachmentUtil.addOrUpdateCPAttachmentFileEntry(
				_cpAttachmentFileEntryService, _cpDefinitionOptionRelService,
				_cpDefinitionOptionValueRelService, _cpOptionService,
				_uniqueFileNameProvider, attachmentBase64,
				_classNameLocalService.getClassNameId(
					cpDefinition.getModelClassName()),
				cpDefinition.getCPDefinitionId(), type, serviceContext);

		return _toAttachment(
			cpAttachmentFileEntry.getCPAttachmentFileEntryId());
	}

	private Attachment _addOrUpdateAttachment(
			CPDefinition cpDefinition, int type, AttachmentUrl attachmentUrl)
		throws Exception {

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			cpDefinition.getGroupId());

		if (attachmentUrl.getTags() != null) {
			serviceContext.setAssetTagNames(attachmentUrl.getTags());
		}

		serviceContext.setExpandoBridgeAttributes(
			_getExpandoBridgeAttributes(
				CPAttachmentFileEntry.class.getName(),
				attachmentUrl.getCustomFields()));

		CPAttachmentFileEntry cpAttachmentFileEntry =
			AttachmentUtil.addOrUpdateCPAttachmentFileEntry(
				_cpAttachmentFileEntryService, _cpDefinitionOptionRelService,
				_cpDefinitionOptionValueRelService, _cpOptionService,
				_uniqueFileNameProvider, attachmentUrl,
				_classNameLocalService.getClassNameId(
					cpDefinition.getModelClassName()),
				cpDefinition.getCPDefinitionId(), type, serviceContext);

		return _toAttachment(
			cpAttachmentFileEntry.getCPAttachmentFileEntryId());
	}

	private Attachment _addOrUpdateProductAttachment(
			CPDefinition cpDefinition, Attachment attachment)
		throws Exception {

		return _addOrUpdateAttachment(
			cpDefinition, CPAttachmentFileEntryConstants.TYPE_OTHER,
			attachment);
	}

	private Attachment _addOrUpdateProductAttachment(
			CPDefinition cpDefinition, AttachmentBase64 attachment)
		throws Exception {

		return _addOrUpdateAttachment(
			cpDefinition, CPAttachmentFileEntryConstants.TYPE_OTHER,
			attachment);
	}

	private Attachment _addOrUpdateProductAttachment(
			CPDefinition cpDefinition, AttachmentUrl attachment)
		throws Exception {

		return _addOrUpdateAttachment(
			cpDefinition, CPAttachmentFileEntryConstants.TYPE_OTHER,
			attachment);
	}

	private Attachment _addOrUpdateProductImage(
			CPDefinition cpDefinition, Attachment attachment)
		throws Exception {

		return _addOrUpdateAttachment(
			cpDefinition, CPAttachmentFileEntryConstants.TYPE_IMAGE,
			attachment);
	}

	private Attachment _addOrUpdateProductImage(
			CPDefinition cpDefinition, AttachmentBase64 attachment)
		throws Exception {

		return _addOrUpdateAttachment(
			cpDefinition, CPAttachmentFileEntryConstants.TYPE_IMAGE,
			attachment);
	}

	private Attachment _addOrUpdateProductImage(
			CPDefinition cpDefinition, AttachmentUrl attachment)
		throws Exception {

		return _addOrUpdateAttachment(
			cpDefinition, CPAttachmentFileEntryConstants.TYPE_IMAGE,
			attachment);
	}

	private Page<Attachment> _getAttachmentPage(
			CPDefinition cpDefinition, int type, Pagination pagination)
		throws Exception {

		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			_cpAttachmentFileEntryService.getCPAttachmentFileEntries(
				_classNameLocalService.getClassNameId(
					cpDefinition.getModelClass()),
				cpDefinition.getCPDefinitionId(), type,
				WorkflowConstants.STATUS_APPROVED,
				pagination.getStartPosition(), pagination.getEndPosition());

		int totalCount =
			_cpAttachmentFileEntryService.getCPAttachmentFileEntriesCount(
				_classNameLocalService.getClassNameId(
					cpDefinition.getModelClass()),
				cpDefinition.getCPDefinitionId(), type,
				WorkflowConstants.STATUS_APPROVED);

		return Page.of(
			_toAttachments(cpAttachmentFileEntries), pagination, totalCount);
	}

	private Map<String, Serializable> _getExpandoBridgeAttributes(
		String className, CustomField[] customFields) {

		Map<String, Serializable> expandoBridgeAttributes =
			CustomFieldsUtil.toMap(
				className, contextCompany.getCompanyId(), customFields,
				contextAcceptLanguage.getPreferredLocale());

		if (expandoBridgeAttributes == null) {
			expandoBridgeAttributes = new HashMap<>();
		}

		return expandoBridgeAttributes;
	}

	private Attachment _toAttachment(Long cpAttachmentFileEntryId)
		throws Exception {

		return _attachmentDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				cpAttachmentFileEntryId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<Attachment> _toAttachments(
			List<CPAttachmentFileEntry> cpAttachmentFileEntries)
		throws Exception {

		return transform(
			cpAttachmentFileEntries,
			cpAttachmentFileEntry -> _toAttachment(
				cpAttachmentFileEntry.getCPAttachmentFileEntryId()));
	}

	private Attachment _updateCPAttachmentFileEntry(
			Attachment attachment, CPAttachmentFileEntry cpAttachmentFileEntry,
			int type)
		throws Exception {

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			cpAttachmentFileEntry.getGroupId());

		if (attachment.getTags() != null) {
			serviceContext.setAssetTagNames(attachment.getTags());
		}

		serviceContext.setExpandoBridgeAttributes(
			_getExpandoBridgeAttributes(
				CPAttachmentFileEntry.class.getName(),
				attachment.getCustomFields()));

		cpAttachmentFileEntry = AttachmentUtil.updateCPAttachmentFileEntry(
			cpAttachmentFileEntry, _cpAttachmentFileEntryService,
			_cpDefinitionOptionRelService, _cpDefinitionOptionValueRelService,
			_cpOptionService, _dlAppLocalService,
			_dlFileEntryModelResourcePermission, groupLocalService, attachment,
			cpAttachmentFileEntry.getClassPK(), type, serviceContext);

		return _toAttachment(
			cpAttachmentFileEntry.getCPAttachmentFileEntryId());
	}

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.AttachmentDTOConverter)"
	)
	private DTOConverter<CPAttachmentFileEntry, Attachment>
		_attachmentDTOConverter;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CPAttachmentFileEntryService _cpAttachmentFileEntryService;

	@Reference
	private CPDefinitionOptionRelService _cpDefinitionOptionRelService;

	@Reference
	private CPDefinitionOptionValueRelService
		_cpDefinitionOptionValueRelService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private CPOptionService _cpOptionService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.document.library.kernel.model.DLFileEntry)"
	)
	private ModelResourcePermission<DLFileEntry>
		_dlFileEntryModelResourcePermission;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

}