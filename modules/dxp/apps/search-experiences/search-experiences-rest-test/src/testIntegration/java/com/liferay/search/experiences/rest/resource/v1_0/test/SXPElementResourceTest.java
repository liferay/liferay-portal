/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.search.experiences.rest.client.dto.v1_0.ElementDefinition;
import com.liferay.search.experiences.rest.client.dto.v1_0.SXPElement;
import com.liferay.search.experiences.rest.client.http.HttpInvoker;
import com.liferay.search.experiences.rest.client.pagination.Page;
import com.liferay.search.experiences.rest.client.serdes.v1_0.ElementDefinitionSerDes;
import com.liferay.search.experiences.service.SXPElementLocalService;

import java.io.IOException;
import java.io.InputStream;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 */
@RunWith(Arquillian.class)
public class SXPElementResourceTest extends BaseSXPElementResourceTestCase {

	@Override
	@Test
	public void testGetSXPElementExport() throws Exception {
		SXPElement sxpElement = randomSXPElement();

		SXPElement postSXPElement = testPostSXPElement_addSXPElement(
			sxpElement);

		HttpInvoker.HttpResponse httpResponse =
			sxpElementResource.getSXPElementExportHttpResponse(
				postSXPElement.getId());

		Assert.assertTrue(
			JSONUtil.equals(
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()),
				JSONUtil.put(
					"description_i18n",
					JSONUtil.put("en_US", sxpElement.getDescription())
				).put(
					"elementDefinition", JSONFactoryUtil.createJSONObject()
				).put(
					"externalReferenceCode",
					sxpElement.getExternalReferenceCode()
				).put(
					"fallbackDescription", sxpElement.getFallbackDescription()
				).put(
					"fallbackTitle", sxpElement.getFallbackTitle()
				).put(
					"schemaVersion", postSXPElement.getSchemaVersion()
				).put(
					"title_i18n", JSONUtil.put("en_US", sxpElement.getTitle())
				).put(
					"type", postSXPElement.getType()
				)));
	}

	@Override
	@Test
	public void testGetSXPElementsPageWithFilterDateTimeEquals()
		throws Exception {

		_deleteSXPElements();

		super.testGetSXPElementsPageWithFilterDateTimeEquals();
	}

	@Override
	@Test
	public void testGetSXPElementsPageWithSortDateTime() throws Exception {
	}

	@Override
	@Test
	public void testGetSXPElementsPageWithSortString() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteSXPElement() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSXPElement() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSXPElementByExternalReferenceCode()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSXPElementByExternalReferenceCodeNotFound()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSXPElementNotFound() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSXPElementsPage() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostSXPElement() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostSXPElementCopy() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostSXPElementPreview() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostSXPElementValidate() throws Exception {
	}

	@Override
	@Test
	public void testPostSXPElement() throws Exception {
		super.testPostSXPElement();

		String description = RandomTestUtil.randomString();
		String title = RandomTestUtil.randomString();

		SXPElement sxpElement = SXPElement.toDTO(
			JSONUtil.put(
				"description", description
			).put(
				"description_i18n",
				Collections.singletonMap(
					LocaleUtil.toLanguageId(LocaleUtil.US), description)
			).put(
				"elementDefinition", "{}"
			).put(
				"title", title
			).put(
				"title_i18n",
				Collections.singletonMap(
					LocaleUtil.toLanguageId(LocaleUtil.US), title)
			).toString());

		SXPElement postSXPElement = testPostSXPElement_addSXPElement(
			sxpElement);

		sxpElement.setCreateDate(postSXPElement.getCreateDate());

		sxpElement.setDescription_i18n(
			Collections.singletonMap(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US), description));
		sxpElement.setExternalReferenceCode(
			postSXPElement.getExternalReferenceCode());
		sxpElement.setId(postSXPElement.getId());
		sxpElement.setModifiedDate(postSXPElement.getModifiedDate());
		sxpElement.setReadOnly(false);
		sxpElement.setSchemaVersion(postSXPElement.getSchemaVersion());
		sxpElement.setTitle_i18n(
			Collections.singletonMap(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US), title));
		sxpElement.setType(0);
		sxpElement.setUserName(postSXPElement.getUserName());
		sxpElement.setVersion(postSXPElement.getVersion());

		Assert.assertEquals(sxpElement.toString(), postSXPElement.toString());

		assertValid(postSXPElement);
	}

	@Override
	@Test
	public void testPostSXPElementPreview() throws Exception {
		SXPElement randomSXPElement = randomSXPElement();

		randomSXPElement.setDescription(StringPool.BLANK);
		randomSXPElement.setDescription_i18n(
			Collections.singletonMap(
				"en_US", "text-match-over-multiple-fields-description"));
		randomSXPElement.setElementDefinition(
			ElementDefinitionSerDes.toDTO(_getElementDefinition("1")));
		randomSXPElement.setTitle(StringPool.BLANK);
		randomSXPElement.setTitle_i18n(
			Collections.singletonMap(
				"en_US", "text-match-over-multiple-fields"));

		SXPElement postSXPElement = testPostSXPElementPreview_addSXPElement(
			randomSXPElement);

		randomSXPElement.setDescription(
			_getUSLocalization(randomSXPElement.getDescription_i18n()));
		randomSXPElement.setElementDefinition(
			ElementDefinitionSerDes.toDTO(_getElementDefinition("2")));
		randomSXPElement.setTitle(
			_getUSLocalization(randomSXPElement.getTitle_i18n()));

		assertEquals(randomSXPElement, postSXPElement);
		assertValid(postSXPElement);
	}

	@Override
	@Test
	public void testPostSXPElementValidate() throws Exception {
		sxpElementResource.postSXPElementValidate("{}");
	}

	protected static String getResourceAsString(
		Class<?> clazz, String resourceName) {

		try (InputStream inputStream = clazz.getResourceAsStream(
				resourceName)) {

			return StringUtil.read(inputStream);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to load resource: " + resourceName, ioException);
		}
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "elementDefinition", "title"};
	}

	@Override
	protected SXPElement randomSXPElement() throws Exception {
		SXPElement sxpElement = super.randomSXPElement();

		sxpElement.setDescription_i18n(
			Collections.singletonMap("en_US", sxpElement.getDescription()));
		sxpElement.setElementDefinition(ElementDefinition.toDTO("{}"));
		sxpElement.setTitle(_TITLE_PREFIX + sxpElement.getTitle());
		sxpElement.setTitle_i18n(
			Collections.singletonMap("en_US", sxpElement.getTitle()));

		return sxpElement;
	}

	@Override
	protected SXPElement testDeleteSXPElement_addSXPElement() throws Exception {
		return _addSXPElement(randomSXPElement());
	}

	@Override
	protected SXPElement testGetSXPElement_addSXPElement() throws Exception {
		return _addSXPElement(randomSXPElement());
	}

	@Override
	protected SXPElement
			testGetSXPElementByExternalReferenceCode_addSXPElement()
		throws Exception {

		return _addSXPElement(randomSXPElement());
	}

	@Override
	protected SXPElement testGetSXPElementsPage_addSXPElement(
			SXPElement sxpElement)
		throws Exception {

		return _addSXPElement(sxpElement);
	}

	@Override
	protected SXPElement testGraphQLSXPElement_addSXPElement()
		throws Exception {

		return _addSXPElement(randomSXPElement());
	}

	@Override
	protected SXPElement testPatchSXPElement_addSXPElement() throws Exception {
		return _addSXPElement(randomSXPElement());
	}

	@Override
	protected SXPElement testPostSXPElement_addSXPElement(SXPElement sxpElement)
		throws Exception {

		return _addSXPElement(sxpElement);
	}

	@Override
	protected SXPElement testPostSXPElementCopy_addSXPElement(
			SXPElement sxpElement)
		throws Exception {

		return _addSXPElement(sxpElement);
	}

	@Override
	protected SXPElement testPostSXPElementPreview_addSXPElement(
			SXPElement sxpElement)
		throws Exception {

		return sxpElementResource.postSXPElementPreview(sxpElement);
	}

	@Override
	protected SXPElement testPutSXPElement_addSXPElement() throws Exception {
		return _addSXPElement(randomSXPElement());
	}

	@Override
	protected SXPElement
			testPutSXPElementByExternalReferenceCode_addSXPElement()
		throws Exception {

		return _addSXPElement(randomSXPElement());
	}

	@Override
	protected SXPElement
			testPutSXPElementByExternalReferenceCode_createSXPElement()
		throws Exception {

		return _addSXPElement(randomSXPElement());
	}

	private SXPElement _addSXPElement(SXPElement sxpElement) throws Exception {
		return sxpElementResource.postSXPElement(sxpElement);
	}

	private void _deleteSXPElements() throws Exception {
		Page<SXPElement> page = sxpElementResource.getSXPElementsPage(
			null, null, null, null);

		for (SXPElement sxpElement : page.getItems()) {
			String title = sxpElement.getTitle();

			if (title.startsWith(_TITLE_PREFIX)) {
				sxpElementResource.deleteSXPElement(sxpElement.getId());
			}
		}
	}

	private String _getElementDefinition(String suffix) {
		return getResourceAsString(
			getClass(),
			"dependencies/SXPElementResourceTest._testPostSXPElementPreview" +
				"_elementDefinition" + suffix + ".json");
	}

	private String _getUSLocalization(Map<String, String> localizationMap) {
		return _language.get(LocaleUtil.US, localizationMap.get("en_US"));
	}

	private static final String _TITLE_PREFIX = "SXPERT";

	@Inject
	private Language _language;

	@Inject
	private SXPElementLocalService _sxpElementLocalService;

}