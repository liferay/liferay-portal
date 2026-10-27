/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.configuration.CProductVersionConfiguration;
import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Attachment;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.AttachmentBase64;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.AttachmentUrl;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.problem.Problem;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.AttachmentResource;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portlet.documentlibrary.constants.DLConstants;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.ByteArrayInputStream;

import java.net.URI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

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

	@Override
	@Test
	public void testGetProductByExternalReferenceCodeAttachmentsPage()
		throws Exception {

		super.testGetProductByExternalReferenceCodeAttachmentsPage();

		_testGetProductByExternalReferenceCodeAttachmentsPageWithBinaries();
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
		_testPostProductByExternalReferenceCodeImageWithOptions();
	}

	@Override
	@Test
	public void testPostProductIdAttachment() throws Exception {
		super.testPostProductIdAttachment();

		_testPostProductIdAttachmentProductVersioning();
		_testPostProductIdAttachmentWithFileEntryExternalReferenceCode();
		_testPostProductIdAttachmentWithLocalNetworkURL();
		_testPostProductIdAttachmentWithUnsupportedProtocol();
		_testPostProductIdAttachmentWithUpdatePermission();
	}

	@Override
	@Test
	public void testPostProductIdAttachmentByUrl() throws Exception {
		super.testPostProductIdAttachmentByUrl();

		_testPostProductIdAttachmentByUrlWithLocalNetworkURL();
		_testPostProductIdAttachmentByUrlWithUpdatePermission();
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
		_testPostProductIdImageWithUpdatePermission();
	}

	@Override
	@Test
	public void testPostProductIdImageByUrl() throws Exception {
		super.testPostProductIdImageByUrl();

		_testPostProductIdImageByUrlWithUpdatePermission();
	}

	@Override
	@Test
	public void testPutAttachmentByExternalReferenceCode() throws Exception {
		testPatchAttachmentByExternalReferenceCode();
	}

	public static class TestApplication extends Application {

		public int getCount() {
			return _count.get();
		}

		@GET
		@Path("/image.png")
		@Produces(MediaType.APPLICATION_OCTET_STREAM)
		public byte[] getImage() {
			_count.incrementAndGet();

			return RandomTestUtil.randomBytes();
		}

		@GET
		@Path("/redirect")
		public Response getRedirect() {
			_count.incrementAndGet();

			return Response.status(
				Response.Status.FOUND
			).location(
				URI.create("image.png")
			).build();
		}

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(this);
		}

		private final AtomicInteger _count = new AtomicInteger();

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

	private void _assertAllowedAndFailedURL(
		TestApplication testApplication, String url) {

		int count = testApplication.getCount();

		_assertBadRequest(
			() -> attachmentResource.postProductIdAttachmentByUrl(
				_cProduct.getCProductId(), _randomAttachmentUrl(url)));

		Assert.assertEquals(count + 1, testApplication.getCount());
	}

	private void _assertAllowedURL(TestApplication testApplication, String url)
		throws Exception {

		int count = testApplication.getCount();

		Attachment attachment = attachmentResource.postProductIdAttachmentByUrl(
			_cProduct.getCProductId(), _randomAttachmentUrl(url));

		Assert.assertNotNull(attachment.getId());

		Assert.assertEquals(count + 1, testApplication.getCount());
	}

	private void _assertBadRequest(ThrowingRunnable throwingRunnable) {
		Problem.ProblemException problemException = Assert.assertThrows(
			Problem.ProblemException.class, throwingRunnable);

		Problem problem = problemException.getProblem();

		Assert.assertEquals("BAD_REQUEST", problem.getStatus());
	}

	private void _assertDisallowedURL(
		TestApplication testApplication, String url) {

		int count = testApplication.getCount();

		_assertBadRequest(
			() -> attachmentResource.postProductIdAttachmentByUrl(
				_cProduct.getCProductId(), _randomAttachmentUrl(url)));

		Assert.assertEquals(count, testApplication.getCount());
	}

	private AttachmentResource _getAttachmentResource(Role role)
		throws Exception {

		User user = UserTestUtil.addUser(testCompany, _PASSWORD);

		Role orderAdministratorRole = _roleLocalService.getRole(
			testCompany.getCompanyId(),
			AccountRoleConstants.ROLE_NAME_ORDER_ADMINISTRATOR);

		_roleLocalService.addUserRoles(
			user.getUserId(),
			new long[] {orderAdministratorRole.getRoleId(), role.getRoleId()});

		return AttachmentResource.builder(
		).authentication(
			user.getEmailAddress(), _PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private String _getURL(String path) {
		return StringBundler.concat(
			"http://localhost:", PortalUtil.getPortalServerPort(false),
			"/o/test", path);
	}

	private Attachment _randomAttachment(String url) {
		return new Attachment() {
			{
				src = url;
				title = HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build();
			}
		};
	}

	private AttachmentUrl _randomAttachmentUrl(String url) {
		return new AttachmentUrl() {
			{
				src = url;
				title = HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build();
			}
		};
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
				type = CPAttachmentFileEntryConstants.TYPE_IMAGE;
			}
		};
	}

	private ServiceRegistration<Application> _registerTestApplication(
		TestApplication testApplication) {

		Bundle bundle = FrameworkUtil.getBundle(AttachmentResourceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			Application.class, testApplication,
			HashMapDictionaryBuilder.<String, Object>put(
				"liferay.access.control.disable", true
			).put(
				"liferay.auth.verifier", false
			).put(
				"liferay.oauth2", false
			).put(
				"osgi.jaxrs.application.base", "/test"
			).put(
				"osgi.jaxrs.name",
				"Liferay.Headless.Commerce.Admin.Catalog.Test"
			).build());
	}

	private void _testGetProductByExternalReferenceCodeAttachmentsPageWithBinaries()
		throws Exception {

		String base64EncodedContent = Base64.encode(
			RandomTestUtil.randomBytes());
		String productExternalReferenceCode =
			_cProduct.getExternalReferenceCode();

		Attachment postAttachment =
			attachmentResource.
				postProductByExternalReferenceCodeAttachmentByBase64(
					productExternalReferenceCode,
					new AttachmentBase64() {
						{
							attachment = base64EncodedContent;
							contentType = ContentTypes.TEXT_PLAIN;
							externalReferenceCode =
								RandomTestUtil.randomString();
							galleryEnabled = true;
							neverExpire = true;
							title = HashMapBuilder.put(
								"en_US", RandomTestUtil.randomString(5)
							).build();
							type = CPAttachmentFileEntryConstants.TYPE_OTHER;
						}
					});

		Attachment getAttachment =
			attachmentResource.getAttachmentByExternalReferenceCode(
				postAttachment.getExternalReferenceCode());

		Assert.assertEquals(
			base64EncodedContent, getAttachment.getAttachment());

		Page<Attachment> attachmentsPage =
			attachmentResource.getProductByExternalReferenceCodeAttachmentsPage(
				productExternalReferenceCode, Pagination.of(1, 10));

		String getBase64EncodedContent = null;

		for (Attachment attachment : attachmentsPage.getItems()) {
			if (!Objects.equals(
					postAttachment.getExternalReferenceCode(),
					attachment.getExternalReferenceCode())) {

				continue;
			}

			getBase64EncodedContent = attachment.getAttachment();
		}

		Assert.assertEquals(base64EncodedContent, getBase64EncodedContent);
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

	private void _testPostProductByExternalReferenceCodeImageWithOptions()
		throws Exception {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			CPTestUtil.addCPDefinitionOptionRel(
				testGroup.getGroupId(), _cpDefinition.getCPDefinitionId(), true,
				1);

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			cpDefinitionOptionRel.getCPDefinitionOptionValueRels();

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRels.get(0);

		Attachment randomAttachment = _randomImageAttachment();

		randomAttachment.setOptions(
			HashMapBuilder.put(
				cpDefinitionOptionRel.getKey(),
				cpDefinitionOptionValueRel.getKey()
			).build());

		Attachment postAttachment =
			attachmentResource.postProductByExternalReferenceCodeImage(
				_cProduct.getExternalReferenceCode(), randomAttachment);

		Map<String, String> options = postAttachment.getOptions();

		Assert.assertEquals(
			JSONUtil.put(
				cpDefinitionOptionValueRel.getKey()
			).toString(),
			options.get(cpDefinitionOptionRel.getKey()));
	}

	private void _testPostProductIdAttachmentByUrlWithLocalNetworkURL()
		throws Exception {

		TestApplication testApplication = new TestApplication();

		ServiceRegistration<Application> serviceRegistration =
			_registerTestApplication(testApplication);

		try {
			String url = _getURL("/image.png");

			_assertDisallowedURL(testApplication, url);

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							testCompany.getCompanyId(),
							_URL_FETCH_SECURITY_COMPANY_CONFIGURATION_PID,
							HashMapDictionaryBuilder.<String, Object>put(
								"urlLocalNetworkAccessEnabled", true
							).build())) {

				_assertAllowedAndFailedURL(
					testApplication, _getURL("/redirect"));
				_assertAllowedURL(testApplication, url);
			}

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							testCompany.getCompanyId(),
							_URL_FETCH_SECURITY_COMPANY_CONFIGURATION_PID,
							HashMapDictionaryBuilder.<String, Object>put(
								"urlHostsAllowed", new String[] {"localhost"}
							).build())) {

				_assertDisallowedURL(testApplication, url);
			}

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							testCompany.getCompanyId(),
							_URL_FETCH_SECURITY_COMPANY_CONFIGURATION_PID,
							HashMapDictionaryBuilder.<String, Object>put(
								"urlHostsAllowed", new String[] {"localhost"}
							).put(
								"urlLocalNetworkAccessEnabled", true
							).build())) {

				_assertAllowedURL(testApplication, url);
			}

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							testCompany.getCompanyId(),
							_URL_FETCH_SECURITY_COMPANY_CONFIGURATION_PID,
							HashMapDictionaryBuilder.<String, Object>put(
								"urlHostsAllowed",
								new String[] {RandomTestUtil.randomString()}
							).put(
								"urlLocalNetworkAccessEnabled", true
							).build())) {

				_assertDisallowedURL(testApplication, url);
			}
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _testPostProductIdAttachmentByUrlWithUpdatePermission()
		throws Exception {

		_testUpdatePermission(
			CPActionKeys.MANAGE_COMMERCE_PRODUCT_ATTACHMENTS,
			(attachmentResource, url) ->
				attachmentResource.postProductIdAttachmentByUrl(
					_cProduct.getCProductId(), _randomAttachmentUrl(url)));
	}

	private void _testPostProductIdAttachmentProductVersioning()
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
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					commerceCatalog.getGroupId()));

			_cpDefinitions.add(cpDefinition1);

			Assert.assertEquals(
				1,
				_cpDefinitionLocalService.getCProductCPDefinitionsCount(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_APPROVED));

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
				0,
				_cpAttachmentFileEntryLocalService.
					getCPAttachmentFileEntriesCount(
						classNameId, cpDefinition1.getCPDefinitionId(),
						CPAttachmentFileEntryConstants.TYPE_OTHER,
						WorkflowConstants.STATUS_ANY));

			Assert.assertEquals(
				1,
				_cpDefinitionLocalService.getCProductCPDefinitionsCount(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_APPROVED));

			List<CPDefinition> cpDefinitions =
				_cpDefinitionLocalService.getCProductCPDefinitions(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_DRAFT, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			Assert.assertEquals(
				cpDefinitions.toString(), 1, cpDefinitions.size());

			CPDefinition cpDefinition2 = cpDefinitions.get(0);

			_cpDefinitions.add(cpDefinition2);

			Assert.assertEquals(
				1,
				_cpAttachmentFileEntryLocalService.
					getCPAttachmentFileEntriesCount(
						classNameId, cpDefinition2.getCPDefinitionId(),
						CPAttachmentFileEntryConstants.TYPE_OTHER,
						WorkflowConstants.STATUS_ANY));
		}
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

	private void _testPostProductIdAttachmentWithLocalNetworkURL()
		throws Exception {

		_testProblemWithoutOutboundRequest(
			"BAD_REQUEST",
			url -> attachmentResource.postProductIdAttachment(
				_cProduct.getCProductId(), _randomAttachment(url)));
	}

	private void _testPostProductIdAttachmentWithUnsupportedProtocol() {
		_assertBadRequest(
			() -> attachmentResource.postProductIdAttachment(
				_cProduct.getCProductId(),
				_randomAttachment("file:///" + RandomTestUtil.randomString())));
		_assertBadRequest(
			() -> attachmentResource.postProductIdAttachment(
				_cProduct.getCProductId(),
				_randomAttachment(
					StringBundler.concat(
						"jar:file:///", RandomTestUtil.randomString(), "!/",
						RandomTestUtil.randomString()))));
	}

	private void _testPostProductIdAttachmentWithUpdatePermission()
		throws Exception {

		_testUpdatePermission(
			CPActionKeys.MANAGE_COMMERCE_PRODUCT_ATTACHMENTS,
			(attachmentResource, url) ->
				attachmentResource.postProductIdAttachment(
					_cProduct.getCProductId(), _randomAttachment(url)));
	}

	private void _testPostProductIdImageByUrlWithUpdatePermission()
		throws Exception {

		_testUpdatePermission(
			CPActionKeys.MANAGE_COMMERCE_PRODUCT_IMAGES,
			(attachmentResource, url) ->
				attachmentResource.postProductIdImageByUrl(
					_cProduct.getCProductId(), _randomAttachmentUrl(url)));
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

	private void _testPostProductIdImageWithUpdatePermission()
		throws Exception {

		_testUpdatePermission(
			CPActionKeys.MANAGE_COMMERCE_PRODUCT_IMAGES,
			(attachmentResource, url) -> attachmentResource.postProductIdImage(
				_cProduct.getCProductId(), _randomAttachment(url)));
	}

	private void _testProblemWithoutOutboundRequest(
			String status, UnsafeConsumer<String, Exception> unsafeConsumer)
		throws Exception {

		TestApplication testApplication = new TestApplication();

		ServiceRegistration<Application> serviceRegistration =
			_registerTestApplication(testApplication);

		try {
			Problem.ProblemException problemException = Assert.assertThrows(
				Problem.ProblemException.class,
				() -> unsafeConsumer.accept(_getURL("/image.png")));

			Assert.assertEquals(0, testApplication.getCount());

			Problem problem = problemException.getProblem();

			Assert.assertEquals(status, problem.getStatus());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _testUpdatePermission(
			String actionId,
			UnsafeBiConsumer<AttachmentResource, String, Exception>
				unsafeBiConsumer)
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		AttachmentResource attachmentResource = _getAttachmentResource(role);

		_testProblemWithoutOutboundRequest(
			"FORBIDDEN",
			url -> unsafeBiConsumer.accept(attachmentResource, url));

		String primKey = String.valueOf(testCompany.getCompanyId());

		RoleTestUtil.addResourcePermission(
			role, CommerceCatalog.class.getName(),
			ResourceConstants.SCOPE_COMPANY, primKey, ActionKeys.UPDATE);
		RoleTestUtil.addResourcePermission(
			role, CPConstants.RESOURCE_NAME_PRODUCT,
			ResourceConstants.SCOPE_COMPANY, primKey, actionId);
		RoleTestUtil.addResourcePermission(
			role, DLConstants.RESOURCE_NAME, ResourceConstants.SCOPE_COMPANY,
			primKey, ActionKeys.ADD_DOCUMENT);

		TestApplication testApplication = new TestApplication();

		ServiceRegistration<Application> serviceRegistration =
			_registerTestApplication(testApplication);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						_URL_FETCH_SECURITY_COMPANY_CONFIGURATION_PID,
						HashMapDictionaryBuilder.<String, Object>put(
							"urlLocalNetworkAccessEnabled", true
						).build())) {

			unsafeBiConsumer.accept(attachmentResource, _getURL("/image.png"));

			Assert.assertEquals(1, testApplication.getCount());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private static final String _PASSWORD = RandomTestUtil.randomString();

	private static final String _URL_FETCH_SECURITY_COMPANY_CONFIGURATION_PID =
		"com.liferay.portal.security.configuration." +
			"URLFetchSecurityCompanyConfiguration";

	@DeleteAfterTestRun
	private CProduct _cProduct;

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
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}