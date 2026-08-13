/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.resource.v1_0.test;

import com.liferay.analytics.settings.rest.client.dto.v1_0.Field;
import com.liferay.analytics.settings.rest.client.pagination.Page;
import com.liferay.analytics.settings.rest.client.pagination.Pagination;
import com.liferay.analytics.settings.rest.constants.FieldAccountConstants;
import com.liferay.analytics.settings.rest.constants.FieldPeopleConstants;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.rule.Inject;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Riccardo Ferrari
 */
@RunWith(Arquillian.class)
public class FieldResourceTest extends BaseFieldResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		try {
			_analyticsSettingsManager.deleteCompanyConfiguration(
				TestPropsValues.getCompanyId());
		}
		catch (Exception exception) {
		}
	}

	@Override
	@Test
	public void testGetFieldsAccountsPage() throws Exception {
		Page<Field> totalPage = fieldResource.getFieldsAccountsPage(
			null, null, null);

		int totalCount = GetterUtil.getInteger(totalPage.getTotalCount());

		Assert.assertEquals(
			FieldAccountConstants.FIELD_ACCOUNT_NAMES.length, totalCount);
	}

	@Ignore
	@Override
	@Test
	public void testGetFieldsAccountsPageWithPagination() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetFieldsAccountsPageWithSortDateTime() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetFieldsAccountsPageWithSortDouble() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetFieldsAccountsPageWithSortInteger() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetFieldsAccountsPageWithSortString() throws Exception {
	}

	@Override
	@Test
	public void testGetFieldsPeoplePage() throws Exception {
		Page<Field> totalPage = fieldResource.getFieldsPeoplePage(
			null, null, null);

		int totalCount = GetterUtil.getInteger(totalPage.getTotalCount());

		Assert.assertEquals(
			FieldPeopleConstants.FIELD_CONTACT_NAMES.length +
				FieldPeopleConstants.FIELD_USER_NAMES.length,
			totalCount);
	}

	@Ignore
	@Override
	@Test
	public void testGetFieldsPeoplePageWithPagination() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetFieldsPeoplePageWithSortDateTime() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetFieldsPeoplePageWithSortDouble() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetFieldsPeoplePageWithSortInteger() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetFieldsPeoplePageWithSortString() throws Exception {
	}

	@Override
	@Test
	public void testPatchFieldAccount() throws Exception {
		fieldResource.patchFieldAccount(
			new Field[] {_getField("externalReferenceCode", true, "account")});

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> {
				try {
					Page<Field> fieldsAccountsPage =
						fieldResource.getFieldsAccountsPage(null, null, null);

					int selectedCount = 0;

					for (Field field : fieldsAccountsPage.getItems()) {
						if (field.getSelected()) {
							selectedCount += 1;
						}
					}

					Assert.assertEquals(
						FieldAccountConstants.FIELD_ACCOUNT_REQUIRED_NAMES.
							length + 1,
						selectedCount);
				}
				catch (Exception exception) {
					throw new RuntimeException(exception);
				}

				return null;
			});

		fieldResource.patchFieldAccount(
			_getFields(
				FieldAccountConstants.FIELD_ACCOUNT_NAMES, true, "account"));

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> {
				try {
					Page<Field> fieldsAccountsPage =
						fieldResource.getFieldsAccountsPage(
							null, Pagination.of(1, 100), null);

					int selectedCount = 0;

					for (Field field : fieldsAccountsPage.getItems()) {
						if (field.getSelected()) {
							selectedCount += 1;
						}
					}

					Assert.assertEquals(
						FieldAccountConstants.FIELD_ACCOUNT_NAMES.length,
						selectedCount);
				}
				catch (Exception exception) {
					throw new RuntimeException(exception);
				}

				return null;
			});

		fieldResource.patchFieldAccount(
			_getFields(
				FieldAccountConstants.FIELD_ACCOUNT_NAMES, false, "account"));

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> {
				try {
					Page<Field> fieldsAccountsPage =
						fieldResource.getFieldsAccountsPage(
							null, Pagination.of(1, 100), null);

					int selectedCount = 0;

					for (Field field : fieldsAccountsPage.getItems()) {
						if (field.getSelected()) {
							selectedCount += 1;
						}
					}

					Assert.assertEquals(
						FieldAccountConstants.FIELD_ACCOUNT_REQUIRED_NAMES.
							length,
						selectedCount);
				}
				catch (Exception exception) {
					throw new RuntimeException(exception);
				}

				return null;
			});
	}

	@Override
	@Test
	public void testPatchFieldPeople() throws Exception {
		fieldResource.patchFieldPeople(
			new Field[] {
				_getField("jobClass", true, "contact"),
				_getField("externalReferenceCode", true, "user")
			});

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> {
				try {
					Page<Field> fieldsPeoplePage =
						fieldResource.getFieldsPeoplePage(
							null, Pagination.of(1, 100), null);

					int selectedCount = 0;

					for (Field field : fieldsPeoplePage.getItems()) {
						if (field.getSelected()) {
							selectedCount += 1;
						}
					}

					Assert.assertEquals(
						FieldPeopleConstants.FIELD_CONTACT_REQUIRED_NAMES.
							length +
								FieldPeopleConstants.FIELD_USER_REQUIRED_NAMES.
									length + 2,
						selectedCount);
				}
				catch (Exception exception) {
					throw new RuntimeException(exception);
				}

				return null;
			});

		fieldResource.patchFieldPeople(
			ArrayUtil.append(
				_getFields(
					FieldPeopleConstants.FIELD_CONTACT_NAMES, true, "contact"),
				_getFields(
					FieldPeopleConstants.FIELD_USER_NAMES, true, "user")));

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> {
				try {
					Page<Field> fieldsPeoplePage =
						fieldResource.getFieldsPeoplePage(
							null, Pagination.of(1, 100), null);

					int selectedCount = 0;

					for (Field field : fieldsPeoplePage.getItems()) {
						if (field.getSelected()) {
							selectedCount += 1;
						}
					}

					Assert.assertEquals(
						FieldPeopleConstants.FIELD_CONTACT_NAMES.length +
							FieldPeopleConstants.FIELD_USER_NAMES.length,
						selectedCount);
				}
				catch (Exception exception) {
					throw new RuntimeException(exception);
				}

				return null;
			});

		fieldResource.patchFieldPeople(
			ArrayUtil.append(
				_getFields(
					FieldPeopleConstants.FIELD_CONTACT_NAMES, false, "contact"),
				_getFields(
					FieldPeopleConstants.FIELD_USER_NAMES, false, "user")));

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> {
				try {
					Page<Field> fieldsPeoplePage =
						fieldResource.getFieldsPeoplePage(
							null, Pagination.of(1, 100), null);

					int selectedCount = 0;

					for (Field field : fieldsPeoplePage.getItems()) {
						if (field.getSelected()) {
							selectedCount += 1;
						}
					}

					Assert.assertEquals(
						FieldPeopleConstants.FIELD_CONTACT_REQUIRED_NAMES.
							length +
								FieldPeopleConstants.FIELD_USER_REQUIRED_NAMES.
									length,
						selectedCount);
				}
				catch (Exception exception) {
					throw new RuntimeException(exception);
				}

				return null;
			});
	}

	private Field _getField(String name, boolean selected, String source) {
		Field field = new Field();

		field.setName(name);
		field.setSelected(selected);
		field.setSource(source);

		return field;
	}

	private Field[] _getFields(
		String[] names, boolean selected, String source) {

		return TransformUtil.transform(
			names, name -> _getField(name, selected, source), Field.class);
	}

	@Inject
	private AnalyticsSettingsManager _analyticsSettingsManager;

}