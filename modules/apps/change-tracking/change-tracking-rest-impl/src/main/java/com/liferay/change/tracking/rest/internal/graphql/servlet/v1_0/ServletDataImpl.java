/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.internal.graphql.servlet.v1_0;

import com.liferay.change.tracking.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.change.tracking.rest.internal.graphql.query.v1_0.Query;
import com.liferay.change.tracking.rest.internal.resource.v1_0.CTCollectionResourceImpl;
import com.liferay.change.tracking.rest.internal.resource.v1_0.CTEntryResourceImpl;
import com.liferay.change.tracking.rest.internal.resource.v1_0.CTProcessResourceImpl;
import com.liferay.change.tracking.rest.internal.resource.v1_0.CTRemoteResourceImpl;
import com.liferay.change.tracking.rest.resource.v1_0.CTCollectionResource;
import com.liferay.change.tracking.rest.resource.v1_0.CTEntryResource;
import com.liferay.change.tracking.rest.resource.v1_0.CTProcessResource;
import com.liferay.change.tracking.rest.resource.v1_0.CTRemoteResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Generated;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author David Truong
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setCTCollectionResourceComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects);
		Mutation.setCTProcessResourceComponentServiceObjects(
			_ctProcessResourceComponentServiceObjects);
		Mutation.setCTRemoteResourceComponentServiceObjects(
			_ctRemoteResourceComponentServiceObjects);

		Query.setCTCollectionResourceComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects);
		Query.setCTEntryResourceComponentServiceObjects(
			_ctEntryResourceComponentServiceObjects);
		Query.setCTProcessResourceComponentServiceObjects(
			_ctProcessResourceComponentServiceObjects);
		Query.setCTRemoteResourceComponentServiceObjects(
			_ctRemoteResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Change.Tracking.REST";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/change-tracking-rest-graphql/v1_0";
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
						"mutation#createCTCollectionsPageExportBatch",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"postCTCollectionsPageExportBatch"));
					put(
						"mutation#createCTCollection",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"postCTCollection"));
					put(
						"mutation#createCTCollectionBatch",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"postCTCollectionBatch"));
					put(
						"mutation#deleteCTCollectionByExternalReferenceCode",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"deleteCTCollectionByExternalReferenceCode"));
					put(
						"mutation#patchCTCollectionByExternalReferenceCode",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"patchCTCollectionByExternalReferenceCode"));
					put(
						"mutation#createCTCollectionByExternalReferenceCodePublish",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"postCTCollectionByExternalReferenceCodePublish"));
					put(
						"mutation#createCTCollectionByExternalReferenceCodeSchedulePublish",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"postCTCollectionByExternalReferenceCodeSchedulePublish"));
					put(
						"mutation#deleteCTCollection",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"deleteCTCollection"));
					put(
						"mutation#deleteCTCollectionBatch",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"deleteCTCollectionBatch"));
					put(
						"mutation#patchCTCollection",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"patchCTCollection"));
					put(
						"mutation#updateCTCollection",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class, "putCTCollection"));
					put(
						"mutation#updateCTCollectionBatch",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"putCTCollectionBatch"));
					put(
						"mutation#createCTCollectionCheckout",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"postCTCollectionCheckout"));
					put(
						"mutation#createCTCollectionPublish",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"postCTCollectionPublish"));
					put(
						"mutation#createCTCollectionSchedulePublish",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"postCTCollectionSchedulePublish"));
					put(
						"mutation#createCTProcessesPageExportBatch",
						new ObjectValuePair<>(
							CTProcessResourceImpl.class,
							"postCTProcessesPageExportBatch"));
					put(
						"mutation#deleteCTProcess",
						new ObjectValuePair<>(
							CTProcessResourceImpl.class, "deleteCTProcess"));
					put(
						"mutation#deleteCTProcessBatch",
						new ObjectValuePair<>(
							CTProcessResourceImpl.class,
							"deleteCTProcessBatch"));
					put(
						"mutation#createCTProcessRevert",
						new ObjectValuePair<>(
							CTProcessResourceImpl.class,
							"postCTProcessRevert"));
					put(
						"mutation#createCTRemotesPageExportBatch",
						new ObjectValuePair<>(
							CTRemoteResourceImpl.class,
							"postCTRemotesPageExportBatch"));
					put(
						"mutation#createCTRemote",
						new ObjectValuePair<>(
							CTRemoteResourceImpl.class, "postCTRemote"));
					put(
						"mutation#createCTRemoteBatch",
						new ObjectValuePair<>(
							CTRemoteResourceImpl.class, "postCTRemoteBatch"));
					put(
						"mutation#deleteCTRemote",
						new ObjectValuePair<>(
							CTRemoteResourceImpl.class, "deleteCTRemote"));
					put(
						"mutation#deleteCTRemoteBatch",
						new ObjectValuePair<>(
							CTRemoteResourceImpl.class, "deleteCTRemoteBatch"));
					put(
						"mutation#patchCTRemote",
						new ObjectValuePair<>(
							CTRemoteResourceImpl.class, "patchCTRemote"));
					put(
						"mutation#updateCTRemote",
						new ObjectValuePair<>(
							CTRemoteResourceImpl.class, "putCTRemote"));
					put(
						"mutation#updateCTRemoteBatch",
						new ObjectValuePair<>(
							CTRemoteResourceImpl.class, "putCTRemoteBatch"));

					put(
						"query#cTCollections",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"getCTCollectionsPage"));
					put(
						"query#cTCollectionByExternalReferenceCode",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"getCTCollectionByExternalReferenceCode"));
					put(
						"query#cTCollectionByExternalReferenceCodeShareLink",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"getCTCollectionByExternalReferenceCodeShareLink"));
					put(
						"query#cTCollectionShareLink",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"getCTCollectionShareLink"));
					put(
						"query#cTCollection",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class, "getCTCollection"));
					put(
						"query#ctCollectionCTEntries",
						new ObjectValuePair<>(
							CTEntryResourceImpl.class,
							"getCtCollectionCTEntriesPage"));
					put(
						"query#ctCollectionCTEntryByModelClassNameByModelClassPkModelClassPK",
						new ObjectValuePair<>(
							CTEntryResourceImpl.class,
							"getCtCollectionCTEntryByModelClassNameByModelClassPkModelClassPK"));
					put(
						"query#cTEntriesHistory",
						new ObjectValuePair<>(
							CTEntryResourceImpl.class,
							"getCTEntriesHistoryPage"));
					put(
						"query#cTEntry",
						new ObjectValuePair<>(
							CTEntryResourceImpl.class, "getCTEntry"));
					put(
						"query#cTProcesses",
						new ObjectValuePair<>(
							CTProcessResourceImpl.class, "getCTProcessesPage"));
					put(
						"query#cTProcess",
						new ObjectValuePair<>(
							CTProcessResourceImpl.class, "getCTProcess"));
					put(
						"query#cTRemotes",
						new ObjectValuePair<>(
							CTRemoteResourceImpl.class, "getCTRemotesPage"));
					put(
						"query#cTRemote",
						new ObjectValuePair<>(
							CTRemoteResourceImpl.class, "getCTRemote"));

					put(
						"query#CTCollection.shareLink",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"getCTCollectionShareLink"));
					put(
						"query#CTCollection.byExternalReferenceCodeShareLink",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class,
							"getCTCollectionByExternalReferenceCodeShareLink"));
					put(
						"query#CTProcess.cTCollection",
						new ObjectValuePair<>(
							CTCollectionResourceImpl.class, "getCTCollection"));
					put(
						"query#CTCollection.ctCollectionCTEntries",
						new ObjectValuePair<>(
							CTEntryResourceImpl.class,
							"getCtCollectionCTEntriesPage"));
					put(
						"query#CTCollection.ctCollectionCTEntryByModelClassNameByModelClassPkModelClassPK",
						new ObjectValuePair<>(
							CTEntryResourceImpl.class,
							"getCtCollectionCTEntryByModelClassNameByModelClassPkModelClassPK"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<CTCollectionResource>
		_ctCollectionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<CTProcessResource>
		_ctProcessResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<CTRemoteResource>
		_ctRemoteResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<CTEntryResource>
		_ctEntryResourceComponentServiceObjects;

}