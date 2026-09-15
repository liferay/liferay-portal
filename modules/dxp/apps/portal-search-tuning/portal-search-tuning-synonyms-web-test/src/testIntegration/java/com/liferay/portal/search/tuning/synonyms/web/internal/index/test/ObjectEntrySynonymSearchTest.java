/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.index.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.PortalPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.portlet.ActionRequest;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Felipe Lorenz
 */
@RunWith(Arquillian.class)
public class ObjectEntrySynonymSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testSearch() throws Exception {
		String originalName = PrincipalThreadLocal.getName();

		long companyId = TestPropsValues.getCompanyId();

		User user = UserTestUtil.getAdminUser(companyId);

		PrincipalThreadLocal.setName(user.getUserId());

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				user.getUserId(), true);

		String originalPortalPreferencesXML =
			PortletPreferencesFactoryUtil.toXML(portalPreferences);

		portalPreferences.setValue("", "locales", "en_US,pt_BR");

		PortalPreferencesLocalServiceUtil.updatePreferences(
			companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			PortletPreferencesFactoryUtil.toXML(portalPreferences));

		ObjectDefinition objectDefinition = _addObjectDefinition(user);

		_addObjectEntry("PD initiative", objectDefinition, user);
		_addObjectEntry("product delivery Initiative", objectDefinition, user);
		_addObjectEntry("Query Builder Initiative", objectDefinition, user);
		_addObjectEntry("UQB Initiative", objectDefinition, user);

		_updateSynonymSets(companyId);

		_testSearch(companyId, "PD", objectDefinition);
		_testSearch(companyId, "product delivery", objectDefinition);
		_testSearch(companyId, "query", objectDefinition);
		_testSearch(companyId, "uqb", objectDefinition);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_deleteSynonymSets(companyId);

		PrincipalThreadLocal.setName(originalName);

		PortalPreferencesLocalServiceUtil.updatePreferences(
			companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			originalPortalPreferencesXML);
	}

	private ObjectDefinition _addObjectDefinition(User user) throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, user.getUserId(), 0, null, null, true, false, true, false,
				true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(
					ObjectDefinitionTestUtil.getRandomName()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(
					ObjectDefinitionTestUtil.getRandomName()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					new TextObjectFieldBuilder(
					).indexed(
						true
					).labelMap(
						LocalizedMapUtil.getLocalizedMap("Content")
					).localized(
						true
					).name(
						Field.CONTENT
					).build(),
					new TextObjectFieldBuilder(
					).indexed(
						true
					).labelMap(
						LocalizedMapUtil.getLocalizedMap("Title")
					).name(
						"title"
					).build()),
				Collections.emptyList(),
				ServiceContextTestUtil.getServiceContext());

		_objectDefinitionLocalService.updateTitleObjectFieldId(
			objectDefinition.getObjectDefinitionId(),
			objectDefinition.getObjectFieldBag(
			).getObjectField(
				"title"
			).getObjectFieldId());

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			user.getUserId(), objectDefinition.getObjectDefinitionId());
	}

	private void _addObjectEntry(
			String content, ObjectDefinition objectDefinition, User user)
		throws Exception {

		_objectEntryLocalService.addObjectEntry(
			0, user.getUserId(), objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				Field.CONTENT + "_i18n",
				HashMapBuilder.put(
					LocaleUtil.toLanguageId(LocaleUtil.US), content
				).build()
			).put(
				"title", "title"
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _deleteSynonymSets(long companyId) {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"deleteAllSynonymSets", "true");
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.COMPANY_ID, companyId);

		ReflectionTestUtil.invoke(
			_deleteSynonymSetsMVCActionCommand, "deleteSynonymSets",
			new Class<?>[] {ActionRequest.class},
			mockLiferayPortletActionRequest);
	}

	private void _testSearch(
		long companyId, String keyword, ObjectDefinition objectDefinition) {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				companyId
			).entryClassNames(
				objectDefinition.getClassName()
			).queryString(
				keyword
			);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		List<Document> documents = searchResponse.getDocuments71();

		Assert.assertEquals(
			searchResponse.getRequestString(), 2, documents.size());
	}

	private void _updateSynonymSets(long companyId) {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.COMPANY_ID, companyId);

		ReflectionTestUtil.invoke(
			_editSynonymSetsMVCActionCommand, "updateSynonymSets",
			new Class<?>[] {ActionRequest.class, String[].class},
			mockLiferayPortletActionRequest,
			new String[] {"product delivery,PD", "query,uqb"});
	}

	@Inject(
		filter = "mvc.command.name=/synonyms/delete_synonym_sets",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _deleteSynonymSetsMVCActionCommand;

	@Inject(
		filter = "mvc.command.name=/synonyms/edit_synonym_sets",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _editSynonymSetsMVCActionCommand;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

}