/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Option;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class OptionResourceTest extends BaseOptionResourceTestCase {

	@Ignore
	@Override
	@Test
	public void testDeleteOption() throws Exception {
		super.testDeleteOption();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteOptionByExternalReferenceCode() throws Exception {
		super.testDeleteOptionByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteOption() throws Exception {
		super.testGraphQLDeleteOption();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteOptionByExternalReferenceCode()
		throws Exception {

		super.testGraphQLDeleteOptionByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetOption() throws Exception {
		super.testGraphQLGetOption();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetOptionByExternalReferenceCode() throws Exception {
		super.testGraphQLGetOptionByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetOptionByExternalReferenceCodeNotFound()
		throws Exception {

		super.testGraphQLGetOptionByExternalReferenceCodeNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetOptionsPage() throws Exception {
		super.testGraphQLGetOptionsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostOption() throws Exception {
		super.testGraphQLPostOption();
	}

	@Override
	@Test
	public void testPatchOption() throws Exception {
		Option randomOption = randomOption();

		Option postOption = testPostOption_addOption(randomOption);

		postOption.setKey(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		optionResource.patchOption(postOption.getId(), postOption);

		Option patchOption = optionResource.getOption(postOption.getId());

		Assert.assertNotEquals(randomOption.getKey(), patchOption.getKey());

		assertValid(postOption);
	}

	@Override
	@Test
	public void testPatchOptionByExternalReferenceCode() throws Exception {
		Option randomOption = randomOption();

		Option postOption = testPostOption_addOption(randomOption);

		postOption.setKey(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		optionResource.patchOptionByExternalReferenceCode(
			postOption.getExternalReferenceCode(), postOption);

		Option patchOption = optionResource.getOptionByExternalReferenceCode(
			postOption.getExternalReferenceCode());

		Assert.assertNotEquals(randomOption.getKey(), patchOption.getKey());

		assertValid(postOption);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"description", "facetable", "fieldType", "key", "name", "required",
			"skuContributor"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"fieldType", "name"};
	}

	@Override
	protected Option randomOption() throws Exception {
		return new Option() {
			{
				description = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				facetable = RandomTestUtil.randomBoolean();
				fieldType = FieldType.SELECT;
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				required = RandomTestUtil.randomBoolean();
				skuContributor = RandomTestUtil.randomBoolean();
			}
		};
	}

	@Override
	protected Option testDeleteOption_addOption() throws Exception {
		return _addOption(randomOption());
	}

	@Override
	protected Option testDeleteOptionByExternalReferenceCode_addOption()
		throws Exception {

		return _addOption(randomOption());
	}

	@Override
	protected Option testGetOption_addOption() throws Exception {
		return _addOption(randomOption());
	}

	@Override
	protected Option testGetOptionByExternalReferenceCode_addOption()
		throws Exception {

		return _addOption(randomOption());
	}

	@Override
	protected Option testGetOptionPermissionsPage_addOption() throws Exception {
		return _addOption(randomOption());
	}

	@Override
	protected Option testGetOptionsPage_addOption(Option option)
		throws Exception {

		return _addOption(option);
	}

	@Override
	protected Option testGraphQLGetOptionPermissionsPage_addOption()
		throws Exception {

		return _addOption(randomOption());
	}

	@Override
	protected Option testPostOption_addOption(Option option) throws Exception {
		return _addOption(option);
	}

	@Override
	protected Option testPostOption_addPermissionsOption(Option option)
		throws Exception {

		return permissionsOptionResource.postOption(option);
	}

	@Override
	protected Option testPutOptionByExternalReferenceCode_addOption()
		throws Exception {

		return _addOption(randomOption());
	}

	@Override
	protected Option testPutOptionPermissionsPage_addOption() throws Exception {
		return _addOption(randomOption());
	}

	private Option _addOption(Option option) throws Exception {
		Option postOption = optionResource.postOption(option);

		_cpOptions.add(_cpOptionLocalService.getCPOption(postOption.getId()));

		return postOption;
	}

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

	@DeleteAfterTestRun
	private final List<CPOption> _cpOptions = new ArrayList<>();

}