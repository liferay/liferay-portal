/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.internal.graphql.servlet.v1_0;

import com.liferay.headless.form.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.form.internal.graphql.query.v1_0.Query;
import com.liferay.headless.form.internal.resource.v1_0.FormDocumentResourceImpl;
import com.liferay.headless.form.internal.resource.v1_0.FormRecordResourceImpl;
import com.liferay.headless.form.internal.resource.v1_0.FormResourceImpl;
import com.liferay.headless.form.internal.resource.v1_0.FormStructureResourceImpl;
import com.liferay.headless.form.resource.v1_0.FormDocumentResource;
import com.liferay.headless.form.resource.v1_0.FormRecordResource;
import com.liferay.headless.form.resource.v1_0.FormResource;
import com.liferay.headless.form.resource.v1_0.FormStructureResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Javier Gamarra
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setFormResourceComponentServiceObjects(
			_formResourceComponentServiceObjects);
		Mutation.setFormDocumentResourceComponentServiceObjects(
			_formDocumentResourceComponentServiceObjects);
		Mutation.setFormRecordResourceComponentServiceObjects(
			_formRecordResourceComponentServiceObjects);
		Mutation.setFormStructureResourceComponentServiceObjects(
			_formStructureResourceComponentServiceObjects);

		Query.setFormResourceComponentServiceObjects(
			_formResourceComponentServiceObjects);
		Query.setFormDocumentResourceComponentServiceObjects(
			_formDocumentResourceComponentServiceObjects);
		Query.setFormRecordResourceComponentServiceObjects(
			_formRecordResourceComponentServiceObjects);
		Query.setFormStructureResourceComponentServiceObjects(
			_formStructureResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Form";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-form-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#createFormEvaluateContext",
						new ObjectValuePair<>(
							FormResourceImpl.class, "postFormEvaluateContext"));
					put(
						"mutation#createFormFormDocument",
						new ObjectValuePair<>(
							FormResourceImpl.class, "postFormFormDocument"));
					put(
						"mutation#createSiteFormsPageExportBatch",
						new ObjectValuePair<>(
							FormResourceImpl.class,
							"postSiteFormsPageExportBatch"));
					put(
						"mutation#deleteFormDocument",
						new ObjectValuePair<>(
							FormDocumentResourceImpl.class,
							"deleteFormDocument"));
					put(
						"mutation#deleteFormDocumentBatch",
						new ObjectValuePair<>(
							FormDocumentResourceImpl.class,
							"deleteFormDocumentBatch"));
					put(
						"mutation#updateFormRecord",
						new ObjectValuePair<>(
							FormRecordResourceImpl.class, "putFormRecord"));
					put(
						"mutation#updateFormRecordBatch",
						new ObjectValuePair<>(
							FormRecordResourceImpl.class,
							"putFormRecordBatch"));
					put(
						"mutation#createFormFormRecordsPageExportBatch",
						new ObjectValuePair<>(
							FormRecordResourceImpl.class,
							"postFormFormRecordsPageExportBatch"));
					put(
						"mutation#createFormFormRecord",
						new ObjectValuePair<>(
							FormRecordResourceImpl.class,
							"postFormFormRecord"));
					put(
						"mutation#createFormFormRecordBatch",
						new ObjectValuePair<>(
							FormRecordResourceImpl.class,
							"postFormFormRecordBatch"));
					put(
						"mutation#createSiteFormStructuresPageExportBatch",
						new ObjectValuePair<>(
							FormStructureResourceImpl.class,
							"postSiteFormStructuresPageExportBatch"));

					put(
						"query#form",
						new ObjectValuePair<>(
							FormResourceImpl.class, "getForm"));
					put(
						"query#forms",
						new ObjectValuePair<>(
							FormResourceImpl.class, "getSiteFormsPage"));
					put(
						"query#formDocument",
						new ObjectValuePair<>(
							FormDocumentResourceImpl.class, "getFormDocument"));
					put(
						"query#formRecord",
						new ObjectValuePair<>(
							FormRecordResourceImpl.class, "getFormRecord"));
					put(
						"query#formFormRecords",
						new ObjectValuePair<>(
							FormRecordResourceImpl.class,
							"getFormFormRecordsPage"));
					put(
						"query#formFormRecordByLatestDraft",
						new ObjectValuePair<>(
							FormRecordResourceImpl.class,
							"getFormFormRecordByLatestDraft"));
					put(
						"query#formStructure",
						new ObjectValuePair<>(
							FormStructureResourceImpl.class,
							"getFormStructure"));
					put(
						"query#formStructures",
						new ObjectValuePair<>(
							FormStructureResourceImpl.class,
							"getSiteFormStructuresPage"));

					put(
						"query#Form.formRecordByLatestDraft",
						new ObjectValuePair<>(
							FormRecordResourceImpl.class,
							"getFormFormRecordByLatestDraft"));
					put(
						"query#FormRecord.form",
						new ObjectValuePair<>(
							FormResourceImpl.class, "getForm"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<FormResource>
		_formResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<FormDocumentResource>
		_formDocumentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<FormRecordResource>
		_formRecordResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<FormStructureResource>
		_formStructureResourceComponentServiceObjects;

}