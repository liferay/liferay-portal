/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.bulk.selection.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceReport;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceReportLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceRecordTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Akhash Ramprakash
 */
@RunWith(Arquillian.class)
public class DeleteDDMFormInstanceRecordBulkSelectionActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testExecute() throws Exception {

		// Failed deletions

		DDMFormInstance ddmFormInstance = _addDDMFormInstance();

		DDMFormInstanceRecord ddmFormInstanceRecord1 =
			_addDDMFormInstanceRecord(ddmFormInstance, "1");
		DDMFormInstanceRecord ddmFormInstanceRecord2 =
			_addDDMFormInstanceRecord(ddmFormInstance, "2");
		DDMFormInstanceRecord ddmFormInstanceRecord3 =
			_addDDMFormInstanceRecord(ddmFormInstance, "3");
		DDMFormInstanceRecord ddmFormInstanceRecord4 =
			_addDDMFormInstanceRecord(ddmFormInstance, "4");
		DDMFormInstanceRecord ddmFormInstanceRecord5 =
			_addDDMFormInstanceRecord(ddmFormInstance, "5");

		Bundle bundle = FrameworkUtil.getBundle(
			DeleteDDMFormInstanceRecordBulkSelectionActionTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			(Class<ModelListener<DDMFormInstanceRecord>>)
				(Class<?>)ModelListener.class,
			new BaseModelListener<DDMFormInstanceRecord>() {

				@Override
				public void onBeforeRemove(
						DDMFormInstanceRecord ddmFormInstanceRecord)
					throws ModelListenerException {

					long formInstanceRecordId =
						ddmFormInstanceRecord.getFormInstanceRecordId();

					if ((formInstanceRecordId ==
							ddmFormInstanceRecord2.getFormInstanceRecordId()) ||
						(formInstanceRecordId ==
							ddmFormInstanceRecord4.getFormInstanceRecordId())) {

						throw new ModelListenerException();
					}
				}

			},
			null);

		_execute(ddmFormInstance);

		Assert.assertNull(
			_ddmFormInstanceRecordLocalService.fetchFormInstanceRecord(
				ddmFormInstanceRecord1.getFormInstanceRecordId()));
		Assert.assertNotNull(
			_ddmFormInstanceRecordLocalService.fetchFormInstanceRecord(
				ddmFormInstanceRecord2.getFormInstanceRecordId()));
		Assert.assertNull(
			_ddmFormInstanceRecordLocalService.fetchFormInstanceRecord(
				ddmFormInstanceRecord3.getFormInstanceRecordId()));
		Assert.assertNotNull(
			_ddmFormInstanceRecordLocalService.fetchFormInstanceRecord(
				ddmFormInstanceRecord4.getFormInstanceRecordId()));
		Assert.assertNull(
			_ddmFormInstanceRecordLocalService.fetchFormInstanceRecord(
				ddmFormInstanceRecord5.getFormInstanceRecordId()));

		JSONObject ddmFormInstanceReportDataJSONObject =
			_getDDMFormInstanceReportDataJSONObject(ddmFormInstance);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"number",
				JSONUtil.put(
					"summary",
					JSONUtil.put(
						"max", "4"
					).put(
						"maxCount", 1
					).put(
						"min", "2"
					).put(
						"minCount", 1
					).put(
						"sum", "6"
					)
				).put(
					"totalEntries", 2
				)
			).put(
				"totalItems", 2
			).toString(),
			ddmFormInstanceReportDataJSONObject.toString(), false);

		// Successful deletions

		ddmFormInstance = _addDDMFormInstance();

		_addDDMFormInstanceRecord(
			ddmFormInstance, String.valueOf(RandomTestUtil.randomInt()));
		_addDDMFormInstanceRecord(
			ddmFormInstance, String.valueOf(RandomTestUtil.randomInt()));
		_addDDMFormInstanceRecord(
			ddmFormInstance, String.valueOf(RandomTestUtil.randomInt()));

		_execute(ddmFormInstance);

		Assert.assertEquals(
			0,
			_ddmFormInstanceRecordLocalService.getFormInstanceRecordsCount(
				ddmFormInstance.getFormInstanceId()));

		ddmFormInstanceReportDataJSONObject =
			_getDDMFormInstanceReportDataJSONObject(ddmFormInstance);

		Assert.assertEquals(
			0, ddmFormInstanceReportDataJSONObject.getInt("totalItems"));

		JSONObject numberFieldJSONObject =
			ddmFormInstanceReportDataJSONObject.getJSONObject("number");

		Assert.assertFalse(numberFieldJSONObject.has("summary"));
		Assert.assertEquals(0, numberFieldJSONObject.getInt("totalEntries"));
	}

	private DDMFormInstance _addDDMFormInstance() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createNumericDDMFormField(
				"number", RandomTestUtil.randomString(), "integer", false,
				false, false, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));

		return DDMFormInstanceTestUtil.addDDMFormInstance(
			ddmForm, _group, TestPropsValues.getUserId());
	}

	private DDMFormInstanceRecord _addDDMFormInstanceRecord(
			DDMFormInstance ddmFormInstance, String value)
		throws Exception {

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmFormInstance.getDDMForm());

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"number", value));

		return DDMFormInstanceRecordTestUtil.addDDMFormInstanceRecord(
			ddmFormInstance, ddmFormValues, _group,
			TestPropsValues.getUserId());
	}

	private void _execute(DDMFormInstance ddmFormInstance) throws Exception {
		_deleteDDMFormInstanceRecordBulkSelectionAction.execute(
			TestPropsValues.getUser(),
			_ddmFormInstanceRecordBulkSelectionFactory.create(
				HashMapBuilder.put(
					"createDate",
					new String[] {String.valueOf(System.currentTimeMillis())}
				).put(
					"formInstanceId",
					new String[] {
						String.valueOf(ddmFormInstance.getFormInstanceId())
					}
				).build()),
			new HashMap<>());
	}

	private JSONObject _getDDMFormInstanceReportDataJSONObject(
			DDMFormInstance ddmFormInstance)
		throws Exception {

		DDMFormInstanceReport ddmFormInstanceReport =
			_ddmFormInstanceReportLocalService.
				getFormInstanceReportByFormInstanceId(
					ddmFormInstance.getFormInstanceId());

		return JSONFactoryUtil.createJSONObject(
			ddmFormInstanceReport.getData());
	}

	@Inject(
		filter = "model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord"
	)
	private BulkSelectionFactory<DDMFormInstanceRecord>
		_ddmFormInstanceRecordBulkSelectionFactory;

	@Inject
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	@Inject
	private DDMFormInstanceReportLocalService
		_ddmFormInstanceReportLocalService;

	@Inject(
		filter = "bulk.selection.action.key=delete.ddm.form.instance.record"
	)
	private BulkSelectionAction<DDMFormInstanceRecord>
		_deleteDDMFormInstanceRecordBulkSelectionAction;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceRegistration<ModelListener<DDMFormInstanceRecord>>
		_serviceRegistration;

}