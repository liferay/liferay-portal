/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.web.internal.constants.ConfigurationAdminWebKeys;
import com.liferay.configuration.admin.web.internal.display.ConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContext;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContextFactory;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryIterator;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryRetriever;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.test.portlet.MockRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockRenderResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletException;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Anderson Luiz
 */
public class SearchResultsMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testRender() throws PortletException, SearchException {
		Indexer<ConfigurationModel> indexer = Mockito.mock(Indexer.class);

		Mockito.when(
			_indexerRegistry.nullSafeGetIndexer(ConfigurationModel.class)
		).thenReturn(
			indexer
		);

		Hits hits = Mockito.mock(Hits.class);

		Mockito.when(
			hits.getDocs()
		).thenReturn(
			new Document[0]
		);

		Mockito.when(
			indexer.search(Mockito.any(SearchContext.class))
		).thenReturn(
			hits
		);

		Mockito.when(
			_language.get(Mockito.any(Locale.class), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1, String.class)
		);

		try (MockedStatic<ConfigurationScopeDisplayContextFactory>
				configurationScopeDisplayContextFactoryMockedStatic =
					Mockito.mockStatic(
						ConfigurationScopeDisplayContextFactory.class)) {

			ConfigurationScopeDisplayContext configurationScopeDisplayContext =
				Mockito.mock(ConfigurationScopeDisplayContext.class);

			Mockito.when(
				configurationScopeDisplayContext.getScope()
			).thenReturn(
				ExtendedObjectClassDefinition.Scope.COMPANY
			);

			Mockito.when(
				configurationScopeDisplayContext.getScopePK()
			).thenReturn(
				RandomTestUtil.randomLong()
			);

			configurationScopeDisplayContextFactoryMockedStatic.when(
				() -> ConfigurationScopeDisplayContextFactory.create(
					Mockito.any())
			).thenReturn(
				configurationScopeDisplayContext
			);

			String keyword1 = RandomTestUtil.randomString();

			ConfigurationScreen configurationScreen1 = _getConfigurationScreen(
				RandomTestUtil.randomString(), keyword1,
				RandomTestUtil.randomString());

			String keyword2 = RandomTestUtil.randomString();

			ConfigurationScreen configurationScreen2 = _getConfigurationScreen(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				keyword2);

			String keyword3 =
				RandomTestUtil.randomString() + " " +
					RandomTestUtil.randomString();

			ConfigurationScreen configurationScreen3 = _getConfigurationScreen(
				keyword3, RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

			Mockito.when(
				_configurationEntryRetriever.getAllConfigurationScreens()
			).thenReturn(
				ListUtil.fromArray(
					configurationScreen1, configurationScreen2,
					configurationScreen3)
			);

			_assertConfigurationEntry(configurationScreen1.getKey(), keyword1);
			_assertConfigurationEntry(configurationScreen2.getKey(), keyword2);
			_assertConfigurationEntry(configurationScreen3.getKey(), keyword3);
		}
	}

	private void _assertConfigurationEntry(String key, String keywords)
		throws PortletException {

		MockRenderRequest mockRenderRequest = new MockRenderRequest();

		mockRenderRequest.setParameter("keywords", keywords);

		_searchResultsMVCRenderCommand.render(
			mockRenderRequest, new MockRenderResponse());

		ConfigurationEntryIterator configurationEntryIterator =
			(ConfigurationEntryIterator)mockRenderRequest.getAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_ENTRY_ITERATOR);

		Assert.assertEquals(1, configurationEntryIterator.getTotal());

		List<ConfigurationEntry> results =
			configurationEntryIterator.getResults();

		ConfigurationEntry configurationEntry = results.get(0);

		Assert.assertEquals(key, configurationEntry.getKey());
	}

	private ConfigurationScreen _getConfigurationScreen(
		String categoryKey, String key, String name) {

		ConfigurationScreen configurationScreen = Mockito.mock(
			ConfigurationScreen.class);

		Mockito.when(
			configurationScreen.getCategoryKey()
		).thenReturn(
			categoryKey
		);

		Mockito.when(
			configurationScreen.getKey()
		).thenReturn(
			key
		);

		Mockito.when(
			configurationScreen.getName(Mockito.any())
		).thenReturn(
			name
		);

		Mockito.when(
			configurationScreen.getScope()
		).thenReturn(
			String.valueOf(ExtendedObjectClassDefinition.Scope.COMPANY)
		);

		Mockito.when(
			configurationScreen.isVisible()
		).thenReturn(
			true
		);

		return configurationScreen;
	}

	@Mock
	private ConfigurationEntryRetriever _configurationEntryRetriever;

	@Mock
	private ConfigurationModelRetriever _configurationModelRetriever;

	@Mock
	private IndexerRegistry _indexerRegistry;

	@Mock
	private Language _language;

	@InjectMocks
	private SearchResultsMVCRenderCommand _searchResultsMVCRenderCommand =
		new SearchResultsMVCRenderCommand();

}