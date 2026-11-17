/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.index.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
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
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.ActionRequest;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tibor Lipusz
 * @author Petteri Karttunen
 */
@RunWith(Arquillian.class)
public class SynonymSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_companyId = TestPropsValues.getCompanyId();
		_originalName = PrincipalThreadLocal.getName();

		_user = UserTestUtil.getAdminUser(_companyId);

		PrincipalThreadLocal.setName(_user.getUserId());

		_group = GroupTestUtil.addGroup(
			_companyId, _user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				_user.getUserId(), true);

		_originalPortalPreferencesXML = PortletPreferencesFactoryUtil.toXML(
			portalPreferences);

		portalPreferences.setValue(
			"", "locales",
			"ar_SA,ca_ES,zh_CN,nl_NL,en_US,pt_PT,fi_FI,fr_FR,de_DE,hu_HU," +
				"it_IT,ja_JP,pt_BR,es_ES,sv_SE");

		PortalPreferencesLocalServiceUtil.updatePreferences(
			_companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			PortletPreferencesFactoryUtil.toXML(portalPreferences));

		LanguageUtil.init();

		_addSynonymSets();

		_addJournalArticles();
	}

	@AfterClass
	public static void tearDownClass() {
		_deleteSynonymSets();

		PrincipalThreadLocal.setName(_originalName);

		PortalPreferencesLocalServiceUtil.updatePreferences(
			_companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			_originalPortalPreferencesXML);
	}

	@Test
	public void testSearchOnLocalesWithDefaultSynonymFilters() {
		for (Map.Entry<Locale, String> entry : _synonymsMap.entrySet()) {
			String[] parts = StringUtil.split(entry.getValue());

			_assertSearch(2, parts[0], entry.getKey());
		}
	}

	@Test
	public void testSearchQuotedPhrase() {
		_assertSearch(
			1, "\"Ministério Público de Santa Catarina\"", LocaleUtil.BRAZIL);
		_assertSearch(
			1, "\"denunciado pelo Ministério Público\"", LocaleUtil.BRAZIL);
		_assertSearch(1, "\"perseguido pelo bichano\"", LocaleUtil.BRAZIL);
		_assertSearch(1, "\"perseguido pelo gato\"", LocaleUtil.BRAZIL);
		_assertSearch(2, "\"Tribunal de Justiça\"", LocaleUtil.BRAZIL);
	}

	private static void _addJournalArticle(Locale locale, String title)
		throws Exception {

		Map<Locale, String> localeStringMap = HashMapBuilder.put(
			locale, title
		).build();

		JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class), localeStringMap,
			null, localeStringMap, LocaleUtil.getSiteDefault(), false, true,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));
	}

	private static void _addJournalArticles() throws Exception {
		for (Map.Entry<Locale, String> entry : _synonymsMap.entrySet()) {
			String[] parts = StringUtil.split(entry.getValue());

			_addJournalArticle(entry.getKey(), parts[0]);
			_addJournalArticle(entry.getKey(), parts[1]);
		}

		_addJournalArticle(
			LocaleUtil.BRAZIL, "Decisão do Tribunal de Justiça hoje");
		_addJournalArticle(
			LocaleUtil.BRAZIL,
			"Ministério Público de Santa Catarina abre vaga");
		_addJournalArticle(
			LocaleUtil.BRAZIL, "Promotor denunciado pelo MP ontem");
		_addJournalArticle(
			LocaleUtil.BRAZIL, "Rato perseguido pelo gato fugiu");
		_addJournalArticle(LocaleUtil.BRAZIL, "Recurso ao TJ negado");
	}

	private static void _addSynonymSets() {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.COMPANY_ID, _companyId);

		Collection<String> values = _synonymsMap.values();

		ReflectionTestUtil.invoke(
			_editSynonymSetsMVCActionCommand, "updateSynonymSets",
			new Class<?>[] {ActionRequest.class, String[].class},
			mockLiferayPortletActionRequest,
			ArrayUtil.append(
				values.toArray(new String[0]),
				new String[] {
					"Ministério Público,MP", "Tribunal de Justiça,TJ",
					"gato,bichano"
				}));
	}

	private static void _deleteSynonymSets() {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"deleteAllSynonymSets", "true");
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.COMPANY_ID, _companyId);

		ReflectionTestUtil.invoke(
			_deleteSynonymSetsMVCActionCommand, "deleteSynonymSets",
			new Class<?>[] {ActionRequest.class},
			mockLiferayPortletActionRequest);
	}

	private void _assertSearch(
		int expectedCount, String keyword, Locale locale) {

		String localizedFieldName = Field.getLocalizedName(locale, Field.TITLE);

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				_companyId
			).entryClassNames(
				JournalArticle.class.getName()
			).groupIds(
				_group.getGroupId()
			).queryString(
				keyword
			);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		List<Document> documents = searchResponse.getDocuments71();

		DocumentsAssert.assertCount(
			searchResponse.getRequestString(),
			documents.toArray(new Document[0]), localizedFieldName,
			expectedCount);
	}

	private static final Locale _ARABIC_LOCALE = new Locale("ar", "SA");

	private static final Locale _CATALAN_LOCALE = new Locale("ca", "ES");

	private static final Locale _FINNISH_LOCALE = new Locale("fi", "FI");

	private static final Locale _SWEDISH_LOCALE = new Locale("sv", "SE");

	private static Long _companyId;

	@Inject(
		filter = "mvc.command.name=/synonyms/delete_synonym_sets",
		type = MVCActionCommand.class
	)
	private static MVCActionCommand _deleteSynonymSetsMVCActionCommand;

	@Inject(
		filter = "mvc.command.name=/synonyms/edit_synonym_sets",
		type = MVCActionCommand.class
	)
	private static MVCActionCommand _editSynonymSetsMVCActionCommand;

	private static Group _group;
	private static String _originalName;
	private static String _originalPortalPreferencesXML;
	private static final Map<Locale, String> _synonymsMap = HashMapBuilder.put(
		_ARABIC_LOCALE, "فعال,منتج"
	).put(
		_CATALAN_LOCALE, "feliç,satisfet"
	).put(
		_FINNISH_LOCALE, "tehokas,tuottava"
	).put(
		_SWEDISH_LOCALE, "lycklig,nöjd"
	).put(
		LocaleUtil.BRAZIL, "feliz,alegre"
	).put(
		LocaleUtil.CHINA, "有效的,富有成效的"
	).put(
		LocaleUtil.FRANCE, "maison,logement"
	).put(
		LocaleUtil.GERMANY, "glücklich,heiter"
	).put(
		LocaleUtil.HUNGARY, "hatékony,produktív"
	).put(
		LocaleUtil.ITALY, "contento,soddisfatto"
	).put(
		LocaleUtil.JAPAN, "効果的,生産的な"
	).put(
		LocaleUtil.NETHERLANDS, "effectief,productief"
	).put(
		LocaleUtil.PORTUGAL, "carro,automovel"
	).put(
		LocaleUtil.SPAIN, "efectivo,productivo"
	).put(
		LocaleUtil.US, "dxp,portal"
	).build();
	private static User _user;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

}