/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.search.experiences.rest.client.dto.v1_0.SXPBlueprint;
import com.liferay.search.experiences.rest.client.http.HttpInvoker;
import com.liferay.search.experiences.rest.client.pagination.Page;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 */
@RunWith(Arquillian.class)
public class SXPBlueprintResourceTest extends BaseSXPBlueprintResourceTestCase {

	@Override
	@Test
	public void testGetSXPBlueprintExport() throws Exception {
		SXPBlueprint sxpBlueprint = randomSXPBlueprint();

		SXPBlueprint postSXPBlueprint = testPostSXPBlueprint_addSXPBlueprint(
			sxpBlueprint);

		HttpInvoker.HttpResponse httpResponse =
			sxpBlueprintResource.getSXPBlueprintExportHttpResponse(
				postSXPBlueprint.getId());

		Assert.assertTrue(
			JSONUtil.equals(
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()),
				JSONUtil.put(
					"configuration", JSONFactoryUtil.createJSONObject()
				).put(
					"description_i18n",
					JSONUtil.put("en_US", sxpBlueprint.getDescription())
				).put(
					"elementInstances", JSONFactoryUtil.createJSONArray()
				).put(
					"externalReferenceCode",
					sxpBlueprint.getExternalReferenceCode()
				).put(
					"schemaVersion", postSXPBlueprint.getSchemaVersion()
				).put(
					"title_i18n", JSONUtil.put("en_US", sxpBlueprint.getTitle())
				)));
	}

	@Override
	@Test
	public void testGetSXPBlueprintsPageWithFilterDateTimeEquals()
		throws Exception {

		_deleteSXPBlueprints();

		super.testGetSXPBlueprintsPageWithFilterDateTimeEquals();
	}

	@Override
	@Test
	public void testGetSXPBlueprintsPageWithSortInteger() throws Exception {
		testGetSXPBlueprintsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, sxpBlueprint1, sxpBlueprint2) -> {
				if (BeanTestUtil.hasProperty(
						sxpBlueprint1, entityField.getName())) {

					BeanTestUtil.setProperty(
						sxpBlueprint1, entityField.getName(), 0);
				}

				if (BeanTestUtil.hasProperty(
						sxpBlueprint2, entityField.getName())) {

					BeanTestUtil.setProperty(
						sxpBlueprint2, entityField.getName(), 1);
				}
			});
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSXPBlueprint() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSXPBlueprintByExternalReferenceCode()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSXPBlueprintByExternalReferenceCodeNotFound()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSXPBlueprintNotFound() throws Exception {
	}

	@Override
	@Test
	public void testPostSXPBlueprint() throws Exception {
		super.testPostSXPBlueprint();

		SXPBlueprint sxpBlueprint = SXPBlueprint.toDTO(
			JSONUtil.put(
				"description", RandomTestUtil.randomString()
			).put(
				"title", RandomTestUtil.randomString()
			).toString());

		SXPBlueprint postSXPBlueprint = testPostSXPBlueprint_addSXPBlueprint(
			sxpBlueprint);

		sxpBlueprint.setCollectionProviderTypeName(StringPool.BLANK);
		sxpBlueprint.setCollectionProviderSubtypeName(StringPool.BLANK);
		sxpBlueprint.setCreateDate(postSXPBlueprint.getCreateDate());
		sxpBlueprint.setExternalReferenceCode(
			postSXPBlueprint.getExternalReferenceCode());
		sxpBlueprint.setId(postSXPBlueprint.getId());
		sxpBlueprint.setModifiedDate(postSXPBlueprint.getModifiedDate());
		sxpBlueprint.setSchemaVersion(postSXPBlueprint.getSchemaVersion());
		sxpBlueprint.setUserName(postSXPBlueprint.getUserName());
		sxpBlueprint.setVersion(postSXPBlueprint.getVersion());

		Assert.assertEquals(
			sxpBlueprint.toString(), postSXPBlueprint.toString());

		assertValid(postSXPBlueprint);
	}

	@Override
	@Test
	public void testPostSXPBlueprintValidate() throws Exception {
		sxpBlueprintResource.postSXPBlueprintValidate("{}");
	}

	@Override
	protected SXPBlueprint randomSXPBlueprint() throws Exception {
		SXPBlueprint sxpBlueprint = super.randomSXPBlueprint();

		sxpBlueprint.setDescription_i18n(
			Collections.singletonMap("en_US", sxpBlueprint.getDescription()));
		sxpBlueprint.setTitle(_TITLE_PREFIX + sxpBlueprint.getTitle());
		sxpBlueprint.setTitle_i18n(
			Collections.singletonMap("en_US", sxpBlueprint.getTitle()));

		return sxpBlueprint;
	}

	@Override
	protected SXPBlueprint testDeleteSXPBlueprint_addSXPBlueprint()
		throws Exception {

		return _addSXPBlueprint(randomSXPBlueprint());
	}

	@Override
	protected SXPBlueprint testGetSXPBlueprint_addSXPBlueprint()
		throws Exception {

		return _addSXPBlueprint(randomSXPBlueprint());
	}

	@Override
	protected SXPBlueprint
			testGetSXPBlueprintByExternalReferenceCode_addSXPBlueprint()
		throws Exception {

		return _addSXPBlueprint(randomSXPBlueprint());
	}

	@Override
	protected SXPBlueprint testGetSXPBlueprintsPage_addSXPBlueprint(
			SXPBlueprint sxpBlueprint)
		throws Exception {

		return _addSXPBlueprint(sxpBlueprint);
	}

	@Override
	protected SXPBlueprint testGraphQLSXPBlueprint_addSXPBlueprint()
		throws Exception {

		return _addSXPBlueprint(randomSXPBlueprint());
	}

	@Override
	protected SXPBlueprint testPatchSXPBlueprint_addSXPBlueprint()
		throws Exception {

		return _addSXPBlueprint(randomSXPBlueprint());
	}

	@Override
	protected SXPBlueprint testPostSXPBlueprint_addSXPBlueprint(
			SXPBlueprint sxpBlueprint)
		throws Exception {

		return _addSXPBlueprint(sxpBlueprint);
	}

	@Override
	protected SXPBlueprint testPostSXPBlueprintCopy_addSXPBlueprint(
			SXPBlueprint sxpBlueprint)
		throws Exception {

		return _addSXPBlueprint(sxpBlueprint);
	}

	@Override
	protected SXPBlueprint testPutSXPBlueprint_addSXPBlueprint()
		throws Exception {

		return _addSXPBlueprint(randomSXPBlueprint());
	}

	@Override
	protected SXPBlueprint
			testPutSXPBlueprintByExternalReferenceCode_addSXPBlueprint()
		throws Exception {

		return _addSXPBlueprint(randomSXPBlueprint());
	}

	@Override
	protected SXPBlueprint
			testPutSXPBlueprintByExternalReferenceCode_createSXPBlueprint()
		throws Exception {

		return _addSXPBlueprint(randomSXPBlueprint());
	}

	private SXPBlueprint _addSXPBlueprint(SXPBlueprint sxpBlueprint)
		throws Exception {

		return sxpBlueprintResource.postSXPBlueprint(sxpBlueprint);
	}

	private void _deleteSXPBlueprints() throws Exception {
		Page<SXPBlueprint> page = sxpBlueprintResource.getSXPBlueprintsPage(
			null, null, null, null);

		for (SXPBlueprint sxpBlueprint : page.getItems()) {
			String title = sxpBlueprint.getTitle();

			if (!title.startsWith(_TITLE_PREFIX)) {
				continue;
			}

			sxpBlueprintResource.deleteSXPBlueprint(sxpBlueprint.getId());
		}
	}

	private static final String _TITLE_PREFIX = "SXPBRT";

}