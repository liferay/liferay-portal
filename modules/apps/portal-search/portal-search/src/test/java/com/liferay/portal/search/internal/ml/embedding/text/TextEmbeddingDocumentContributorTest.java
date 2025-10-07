/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.ml.embedding.text;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.configuration.SemanticSearchConfiguration;
import com.liferay.portal.search.configuration.SemanticSearchConfigurationProvider;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.ml.embedding.text.TextEmbeddingRetriever;
import com.liferay.portal.search.rest.dto.v1_0.EmbeddingProviderConfiguration;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 */
@FeatureFlag("LPS-122920")
public class TextEmbeddingDocumentContributorTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setSemanticSearchConfiguration(
			new String[] {LocaleUtil.toLanguageId(LocaleUtil.US)},
			new String[] {BlogsEntry.class.getName()}, true);
		_setUpTextEmbeddingDocumentContributorImpl();
	}

	@Test
	public void testContribute() throws Exception {
		Document document = Mockito.mock(Document.class);

		_textEmbeddingDocumentContributorImpl.contribute(
			document, _getBlogsEntry(), RandomTestUtil.randomString());

		Mockito.verify(
			document
		).add(
			Mockito.any()
		);
	}

	@Test
	public void testContributeWithLanguageId() throws Exception {
		Document document = Mockito.mock(Document.class);

		_textEmbeddingDocumentContributorImpl.contribute(
			document, LocaleUtil.toLanguageId(LocaleUtil.US), _getBlogsEntry(),
			RandomTestUtil.randomString());

		Mockito.verify(
			document
		).add(
			Mockito.any()
		);
	}

	@Test
	public void testContributeWithNotConfiguredLanguage() throws Exception {
		Document document = Mockito.mock(Document.class);

		_textEmbeddingDocumentContributorImpl.contribute(
			document, LocaleUtil.toLanguageId(LocaleUtil.FRENCH),
			_getBlogsEntry(), RandomTestUtil.randomString());

		Mockito.verifyNoInteractions(document);
	}

	@Test
	public void testGetEmbeddingProviderConfiguration() throws Exception {
		Assert.assertNotNull(
			_textEmbeddingDocumentContributorImpl.
				getEmbeddingProviderConfiguration(_getBlogsEntry()));
	}

	@Test
	public void testGetEmbeddingProviderConfigurationWithNotConfiguredModelClass()
		throws Exception {

		_setSemanticSearchConfiguration(
			new String[] {LocaleUtil.toLanguageId(LocaleUtil.US)},
			new String[] {DLFileEntry.class.getName()}, false);

		Assert.assertNull(
			_textEmbeddingDocumentContributorImpl.
				getEmbeddingProviderConfiguration(_getBlogsEntry()));
	}

	@Test
	public void testGetEmbeddingProviderConfigurationWithObjectEntry()
		throws Exception {

		String objectDefinitionClassName1 = RandomTestUtil.randomString();

		_setSemanticSearchConfiguration(
			new String[] {LocaleUtil.toLanguageId(LocaleUtil.US)},
			new String[] {objectDefinitionClassName1}, true);

		String objectDefinitionClassName2 = RandomTestUtil.randomString();

		Assert.assertNotNull(
			_textEmbeddingDocumentContributorImpl.
				getEmbeddingProviderConfiguration(
					_getObjectEntry(objectDefinitionClassName1)));

		Assert.assertNull(
			_textEmbeddingDocumentContributorImpl.
				getEmbeddingProviderConfiguration(
					_getObjectEntry(objectDefinitionClassName2)));
	}

	@Test
	public void testGetEmbeddingProviderConfigurationWithTextEmbeddingsDisabled()
		throws Exception {

		_setSemanticSearchConfiguration(
			new String[] {LocaleUtil.toLanguageId(LocaleUtil.US)},
			new String[] {DLFileEntry.class.getName()}, false);

		Assert.assertNull(
			_textEmbeddingDocumentContributorImpl.
				getEmbeddingProviderConfiguration(_getBlogsEntry()));
	}

	@Test
	public void testGetTextEmbeddingFieldName() throws Exception {
		int dimensions = RandomTestUtil.randomInt();

		Assert.assertEquals(
			"text_embedding_" + dimensions + "_en_US",
			_textEmbeddingDocumentContributorImpl.getTextEmbeddingFieldName(
				dimensions, LocaleUtil.toLanguageId(LocaleUtil.US)));
	}

	@Test
	public void testIsIndexableStatus() throws Exception {
		BlogsEntry blogsEntry = _getBlogsEntry();

		Assert.assertTrue(
			_textEmbeddingDocumentContributorImpl.isIndexableStatus(
				blogsEntry));

		Mockito.when(
			blogsEntry.getStatus()
		).thenReturn(
			WorkflowConstants.STATUS_IN_TRASH
		);

		Assert.assertFalse(
			_textEmbeddingDocumentContributorImpl.isIndexableStatus(
				blogsEntry));
	}

	@Test
	public void testIsIndexableStatusWithNotWorkflowableModel()
		throws Exception {

		Assert.assertTrue(
			_textEmbeddingDocumentContributorImpl.isIndexableStatus(
				Mockito.mock(DLFileEntry.class)));
	}

	private SemanticSearchConfiguration _createSemanticSearchConfiguration(
		String[] embeddingProviderLanguageIds,
		String[] embeddingProviderModelClassNames, boolean enabled) {

		SemanticSearchConfiguration semanticSearchConfiguration = Mockito.mock(
			SemanticSearchConfiguration.class);

		Mockito.when(
			semanticSearchConfiguration.
				textEmbeddingProviderConfigurationJSONs()
		).thenReturn(
			new String[] {
				new EmbeddingProviderConfiguration(
				) {

					{
						setLanguageIds(embeddingProviderLanguageIds);
						setModelClassNames(embeddingProviderModelClassNames);
						setProviderName(RandomTestUtil.randomString());
					}
				}.toString()
			}
		);

		Mockito.when(
			semanticSearchConfiguration.textEmbeddingsEnabled()
		).thenReturn(
			enabled
		);

		return semanticSearchConfiguration;
	}

	private BlogsEntry _getBlogsEntry() {
		BlogsEntry blogsEntry = Mockito.mock(BlogsEntry.class);

		Mockito.doReturn(
			RandomTestUtil.randomLong()
		).when(
			blogsEntry
		).getCompanyId();

		Mockito.doReturn(
			BlogsEntry.class
		).when(
			blogsEntry
		).getModelClass();

		Mockito.doReturn(
			WorkflowConstants.STATUS_APPROVED
		).when(
			blogsEntry
		).getStatus();

		return blogsEntry;
	}

	private ObjectEntry _getObjectEntry(String objectDefinitionClassName)
		throws Exception {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.doReturn(
			RandomTestUtil.randomLong()
		).when(
			objectEntry
		).getCompanyId();

		Mockito.doReturn(
			ObjectEntry.class
		).when(
			objectEntry
		).getModelClass();

		Mockito.doReturn(
			objectDefinitionClassName
		).when(
			objectEntry
		).getModelClassName();

		return objectEntry;
	}

	private void _setSemanticSearchConfiguration(
		String[] embeddingProviderLanguageIds,
		String[] embeddingProviderModelClassNames, boolean enabled) {

		SemanticSearchConfiguration semanticSearchConfiguration =
			_createSemanticSearchConfiguration(
				embeddingProviderLanguageIds, embeddingProviderModelClassNames,
				enabled);

		Mockito.when(
			_semanticSearchConfigurationProvider.getCompanyConfiguration(
				Mockito.anyLong())
		).thenReturn(
			semanticSearchConfiguration
		);
	}

	private void _setUpTextEmbeddingDocumentContributorImpl() {
		_textEmbeddingDocumentContributorImpl =
			new TextEmbeddingDocumentContributorImpl();

		ReflectionTestUtil.setFieldValue(
			_textEmbeddingDocumentContributorImpl,
			"semanticSearchConfigurationProvider",
			_semanticSearchConfigurationProvider);

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.getAvailableLocales(Mockito.anyLong())
		).thenReturn(
			SetUtil.fromArray(new Locale[] {LocaleUtil.US, LocaleUtil.GERMAN})
		);

		ReflectionTestUtil.setFieldValue(
			_textEmbeddingDocumentContributorImpl, "_language", language);

		SearchEngineInformation searchEngineInformation = Mockito.mock(
			SearchEngineInformation.class);

		Mockito.when(
			searchEngineInformation.getVendorString()
		).thenReturn(
			"Elasticsearch"
		);

		ReflectionTestUtil.setFieldValue(
			_textEmbeddingDocumentContributorImpl, "_searchEngineInformation",
			searchEngineInformation);

		TextEmbeddingRetriever textEmbeddingRetriever = Mockito.mock(
			TextEmbeddingRetriever.class);

		Mockito.when(
			textEmbeddingRetriever.getTextEmbedding(
				Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			new Double[768]
		);

		ReflectionTestUtil.setFieldValue(
			_textEmbeddingDocumentContributorImpl, "_textEmbeddingRetriever",
			textEmbeddingRetriever);
	}

	private final SemanticSearchConfigurationProvider
		_semanticSearchConfigurationProvider = Mockito.mock(
			SemanticSearchConfigurationProvider.class);
	private TextEmbeddingDocumentContributorImpl
		_textEmbeddingDocumentContributorImpl;

}