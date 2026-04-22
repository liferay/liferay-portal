/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.configuration.CProductVersionConfiguration;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Attachment;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class AttachmentResourceTest extends BaseAttachmentResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_cpDefinition = CPTestUtil.addCPDefinition(
			testGroup.getGroupId(), "simple", true, false);

		_cProduct = _cpDefinition.getCProduct();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetAttachmentByExternalReferenceCode()
		throws Exception {

		super.testGraphQLGetAttachmentByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetAttachmentByExternalReferenceCodeNotFound()
		throws Exception {

		super.testGraphQLGetAttachmentByExternalReferenceCodeNotFound();
	}

	@Override
	@Test
	public void testPatchAttachmentByExternalReferenceCode() throws Exception {
		super.testPatchAttachmentByExternalReferenceCode();

		_testPatchAttachmentByExternalReferenceCodeWithFileEntryExternalReferenceCode();
	}

	@Override
	@Test
	public void testPostProductByExternalReferenceCodeAttachment()
		throws Exception {

		super.testPostProductByExternalReferenceCodeAttachment();

		_testPostProductByExternalReferenceCodeAttachmentWithFileEntryExternalReferenceCode();
	}

	@Override
	@Test
	public void testPostProductByExternalReferenceCodeImage() throws Exception {
		Attachment randomAttachment = _randomImageAttachment();

		Attachment postAttachment =
			testPostProductByExternalReferenceCodeImage_addAttachment(
				randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);

		_testPostProductByExternalReferenceCodeImageWithFileEntryExternalReferenceCode();
	}

	@Override
	@Test
	public void testPostProductIdAttachment() throws Exception {
		super.testPostProductIdAttachment();

		_testPostProductIdAttachmentWithFileEntryExternalReferenceCode();
	}

	@Test
	public void testPostProductIdAttachmentProductVersioning()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						CProductVersionConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			CommerceCatalog commerceCatalog =
				CPTestUtil.getSystemCommerceCatalog(testCompany.getCompanyId());

			CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
				commerceCatalog.getGroupId(), "simple", null, null, true, true,
				WorkflowConstants.STATUS_APPROVED);

			_cpDefinitions.add(cpDefinition1);

			Assert.assertEquals(
				1,
				_cpDefinitionLocalService.getCProductCPDefinitions(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS
				).size());

			long classNameId = _classNameLocalService.getClassNameId(
				CPDefinition.class.getName());

			Assert.assertEquals(
				0,
				_cpAttachmentFileEntryLocalService.
					getCPAttachmentFileEntriesCount(
						classNameId, cpDefinition1.getCPDefinitionId(),
						CPAttachmentFileEntryConstants.TYPE_OTHER,
						WorkflowConstants.STATUS_ANY));

			Attachment attachment1 = randomAttachment();

			attachment1.setType(CPAttachmentFileEntryConstants.TYPE_OTHER);

			attachmentResource.postProductIdAttachment(
				cpDefinition1.getCProductId(), attachment1);

			Assert.assertEquals(
				1,
				_cpDefinitionLocalService.getCProductCPDefinitions(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS
				).size());

			List<CPDefinition> draftDefinitions =
				_cpDefinitionLocalService.getCProductCPDefinitions(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_DRAFT, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			Assert.assertEquals(
				draftDefinitions.toString(), 1, draftDefinitions.size());

			CPDefinition cpDefinition2 = draftDefinitions.get(0);

			_cpDefinitions.add(cpDefinition2);

			Assert.assertEquals(
				0,
				_cpAttachmentFileEntryLocalService.
					getCPAttachmentFileEntriesCount(
						classNameId, cpDefinition1.getCPDefinitionId(),
						CPAttachmentFileEntryConstants.TYPE_OTHER,
						WorkflowConstants.STATUS_ANY));

			Assert.assertEquals(
				1,
				_cpAttachmentFileEntryLocalService.
					getCPAttachmentFileEntriesCount(
						classNameId, cpDefinition2.getCPDefinitionId(),
						CPAttachmentFileEntryConstants.TYPE_OTHER,
						WorkflowConstants.STATUS_ANY));
		}
	}

	@Override
	@Test
	public void testPostProductIdImage() throws Exception {
		Attachment randomAttachment = _randomImageAttachment();

		Attachment postAttachment = testPostProductIdImage_addAttachment(
			randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);

		_testPostProductIdImageWithFileEntryExternalReferenceCode();
	}

	@Override
	@Test
	public void testPutAttachmentByExternalReferenceCode() throws Exception {
		testPatchAttachmentByExternalReferenceCode();
	}

	@Override
	protected Attachment randomAttachment() throws PortalException {
		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			TempFileEntryUtil.getTempFileName(
				RandomTestUtil.randomString() + ".txt"),
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			new ByteArrayInputStream(RandomTestUtil.randomBytes()), 0, null,
			null, null, _serviceContext);

		return new Attachment() {
			{
				cdnEnabled = false;
				cdnURL = RandomTestUtil.randomString();
				externalReferenceCode = RandomTestUtil.randomString();
				fileEntryGroupExternalReferenceCode =
					testGroup.getExternalReferenceCode();
				fileEntryId = fileEntry.getFileEntryId();
				galleryEnabled = true;
				neverExpire = true;
				priority = RandomTestUtil.randomDouble();
				title = HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString(5)
				).build();
				type = RandomTestUtil.randomInt();
			}
		};
	}

	@Override
	protected Attachment testDeleteAttachment_addAttachment() throws Exception {
		return attachmentResource.postProductIdAttachment(
			_cProduct.getCProductId(), randomAttachment());
	}

	@Override
	protected Attachment
			testDeleteAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		return attachmentResource.postProductByExternalReferenceCodeAttachment(
			_cProduct.getExternalReferenceCode(), randomAttachment());
	}

	@Override
	protected Attachment
			testGetAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		return attachmentResource.postProductByExternalReferenceCodeAttachment(
			_cProduct.getExternalReferenceCode(), randomAttachment());
	}

	@Override
	protected Attachment
			testGetProductByExternalReferenceCodeAttachmentsPage_addAttachment(
				String externalReferenceCode, Attachment attachment)
		throws Exception {

		return attachmentResource.postProductByExternalReferenceCodeAttachment(
			externalReferenceCode, attachment);
	}

	@Override
	protected String
			testGetProductByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode()
		throws Exception {

		return _cProduct.getExternalReferenceCode();
	}

	@Override
	protected Attachment
			testGetProductByExternalReferenceCodeImagesPage_addAttachment(
				String externalReferenceCode, Attachment attachment)
		throws Exception {

		return attachmentResource.postProductByExternalReferenceCodeImage(
			externalReferenceCode, attachment);
	}

	@Override
	protected String
			testGetProductByExternalReferenceCodeImagesPage_getExternalReferenceCode()
		throws Exception {

		return _cProduct.getExternalReferenceCode();
	}

	@Override
	protected Attachment testGetProductIdAttachmentsPage_addAttachment(
			Long id, Attachment attachment)
		throws Exception {

		return attachmentResource.postProductIdAttachment(id, attachment);
	}

	@Override
	protected Long testGetProductIdAttachmentsPage_getId() throws Exception {
		return _cProduct.getCProductId();
	}

	@Override
	protected Attachment testGetProductIdImagesPage_addAttachment(
			Long id, Attachment attachment)
		throws Exception {

		return attachmentResource.postProductIdImage(id, attachment);
	}

	@Override
	protected Long testGetProductIdImagesPage_getId() throws Exception {
		return _cProduct.getCProductId();
	}

	@Override
	protected Attachment testGraphQLAttachment_addAttachment()
		throws Exception {

		return attachmentResource.postProductIdAttachment(
			_cProduct.getCProductId(), randomAttachment());
	}

	@Override
	protected Attachment
			testPatchAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		return attachmentResource.postProductByExternalReferenceCodeAttachment(
			_cProduct.getExternalReferenceCode(), randomAttachment());
	}

	@Override
	protected Attachment
			testPostProductByExternalReferenceCodeAttachment_addAttachment(
				Attachment attachment)
		throws Exception {

		return attachmentResource.postProductByExternalReferenceCodeAttachment(
			_cProduct.getExternalReferenceCode(), attachment);
	}

	@Override
	protected Attachment
			testPostProductByExternalReferenceCodeAttachmentByBase64_addAttachment(
				Attachment attachment)
		throws Exception {

		return attachmentResource.postProductIdAttachment(
			_cProduct.getCProductId(), attachment);
	}

	@Override
	protected Attachment
			testPostProductByExternalReferenceCodeAttachmentByUrl_addAttachment(
				Attachment attachment)
		throws Exception {

		return attachmentResource.postProductIdAttachment(
			_cProduct.getCProductId(), attachment);
	}

	@Override
	protected Attachment
			testPostProductByExternalReferenceCodeImage_addAttachment(
				Attachment attachment)
		throws Exception {

		return attachmentResource.postProductByExternalReferenceCodeImage(
			_cProduct.getExternalReferenceCode(), attachment);
	}

	@Override
	protected Attachment
			testPostProductByExternalReferenceCodeImageByBase64_addAttachment(
				Attachment attachment)
		throws Exception {

		return attachmentResource.postProductIdImage(
			_cProduct.getCProductId(), attachment);
	}

	@Override
	protected Attachment
			testPostProductByExternalReferenceCodeImageByUrl_addAttachment(
				Attachment attachment)
		throws Exception {

		return attachmentResource.postProductIdImage(
			_cProduct.getCProductId(), attachment);
	}

	@Override
	protected Attachment testPostProductIdAttachment_addAttachment(
			Attachment attachment)
		throws Exception {

		return attachmentResource.postProductIdAttachment(
			_cProduct.getCProductId(), attachment);
	}

	@Override
	protected Attachment testPostProductIdAttachmentByBase64_addAttachment(
			Attachment attachment)
		throws Exception {

		return attachmentResource.postProductIdAttachment(
			_cProduct.getCProductId(), attachment);
	}

	@Override
	protected Attachment testPostProductIdAttachmentByUrl_addAttachment(
			Attachment attachment)
		throws Exception {

		return attachmentResource.postProductIdAttachment(
			_cProduct.getCProductId(), attachment);
	}

	@Override
	protected Attachment testPostProductIdImage_addAttachment(
			Attachment attachment)
		throws Exception {

		return attachmentResource.postProductIdImage(
			_cpDefinition.getCProductId(), attachment);
	}

	@Override
	protected Attachment testPostProductIdImageByBase64_addAttachment(
			Attachment attachment)
		throws Exception {

		return attachmentResource.postProductIdImage(
			_cProduct.getCProductId(), attachment);
	}

	@Override
	protected Attachment testPostProductIdImageByUrl_addAttachment(
			Attachment attachment)
		throws Exception {

		return attachmentResource.postProductIdImage(
			_cProduct.getCProductId(), attachment);
	}

	@Override
	protected Attachment
			testPutAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		return attachmentResource.postProductByExternalReferenceCodeAttachment(
			_cProduct.getExternalReferenceCode(), randomAttachment());
	}

	private Attachment _randomImageAttachment() throws Exception {
		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			TempFileEntryUtil.getTempFileName(
				RandomTestUtil.randomString() + ".jpg"),
			ContentTypes.IMAGE_JPEG, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			new ByteArrayInputStream(RandomTestUtil.randomBytes()), 0, null,
			null, null, _serviceContext);

		return new Attachment() {
			{
				cdnEnabled = false;
				cdnURL = RandomTestUtil.randomString();
				externalReferenceCode = RandomTestUtil.randomString();
				fileEntryGroupExternalReferenceCode =
					testGroup.getExternalReferenceCode();
				fileEntryId = fileEntry.getFileEntryId();
				galleryEnabled = true;
				neverExpire = true;
				priority = RandomTestUtil.randomDouble();
				title = HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString(5)
				).build();
				type = RandomTestUtil.randomInt();
			}
		};
	}

	private void _testPatchAttachmentByExternalReferenceCodeWithFileEntryExternalReferenceCode()
		throws Exception {

		Attachment postAttachment =
			testPatchAttachmentByExternalReferenceCode_addAttachment();

		Attachment randomPatchAttachment = randomPatchAttachment();

		long randomPatchAttachmentFileEntryId = GetterUtil.getLong(
			randomPatchAttachment.getFileEntryId());

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(
			randomPatchAttachmentFileEntryId);

		randomPatchAttachment.setFileEntryExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomPatchAttachment.setFileEntryId(0L);

		Attachment patchAttachment =
			attachmentResource.patchAttachmentByExternalReferenceCode(
				postAttachment.getExternalReferenceCode(),
				randomPatchAttachment);

		randomPatchAttachment.setFileEntryId(randomPatchAttachmentFileEntryId);

		Attachment expectedPatchAttachment = postAttachment.clone();

		BeanTestUtil.copyProperties(
			randomPatchAttachment, expectedPatchAttachment);

		Attachment getAttachment =
			attachmentResource.getAttachmentByExternalReferenceCode(
				patchAttachment.getExternalReferenceCode());

		assertEquals(expectedPatchAttachment, getAttachment);
		assertValid(getAttachment);
		Assert.assertEquals(
			randomPatchAttachment.getFileEntryExternalReferenceCode(),
			getAttachment.getFileEntryExternalReferenceCode());
		Assert.assertEquals(
			randomPatchAttachmentFileEntryId,
			GetterUtil.getLong(getAttachment.getFileEntryId()));
	}

	private void _testPostProductByExternalReferenceCodeAttachmentWithFileEntryExternalReferenceCode()
		throws Exception {

		Attachment randomAttachment = randomAttachment();

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(
			randomAttachment.getFileEntryId());

		randomAttachment.setFileEntryExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomAttachment.setFileEntryId(0L);

		Attachment postAttachment =
			attachmentResource.postProductByExternalReferenceCodeAttachment(
				_cProduct.getExternalReferenceCode(), randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
		Assert.assertEquals(
			fileEntry.getExternalReferenceCode(),
			postAttachment.getFileEntryExternalReferenceCode());
		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			GetterUtil.getLong(postAttachment.getFileEntryId()));
	}

	private void _testPostProductByExternalReferenceCodeImageWithFileEntryExternalReferenceCode()
		throws Exception {

		Attachment randomAttachment = _randomImageAttachment();

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(
			randomAttachment.getFileEntryId());

		randomAttachment.setFileEntryExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomAttachment.setFileEntryId(0L);

		Attachment postAttachment =
			attachmentResource.postProductByExternalReferenceCodeImage(
				_cProduct.getExternalReferenceCode(), randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
		Assert.assertEquals(
			fileEntry.getExternalReferenceCode(),
			postAttachment.getFileEntryExternalReferenceCode());
		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			GetterUtil.getLong(postAttachment.getFileEntryId()));
	}

	private void _testPostProductIdAttachmentWithFileEntryExternalReferenceCode()
		throws Exception {

		Attachment randomAttachment = randomAttachment();

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(
			randomAttachment.getFileEntryId());

		randomAttachment.setFileEntryExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomAttachment.setFileEntryId(0L);

		Attachment postAttachment = attachmentResource.postProductIdAttachment(
			_cProduct.getCProductId(), randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
		Assert.assertEquals(
			fileEntry.getExternalReferenceCode(),
			postAttachment.getFileEntryExternalReferenceCode());
		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			GetterUtil.getLong(postAttachment.getFileEntryId()));
	}

	private void _testPostProductIdImageWithFileEntryExternalReferenceCode()
		throws Exception {

		Attachment randomAttachment = _randomImageAttachment();

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(
			randomAttachment.getFileEntryId());

		randomAttachment.setFileEntryExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomAttachment.setFileEntryId(0L);

		Attachment postAttachment = attachmentResource.postProductIdImage(
			_cProduct.getCProductId(), randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
		Assert.assertEquals(
			fileEntry.getExternalReferenceCode(),
			postAttachment.getFileEntryExternalReferenceCode());
		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			GetterUtil.getLong(postAttachment.getFileEntryId()));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@DeleteAfterTestRun
	private CPDefinition _cpDefinition;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@DeleteAfterTestRun
	private List<CPDefinition> _cpDefinitions = new ArrayList<>();

	@Inject
	private CPDefinitionService _cpDefinitionService;

	@DeleteAfterTestRun
	private CProduct _cProduct;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private ServiceContextHelper _serviceContextHelper;

	@DeleteAfterTestRun
	private User _user;

}