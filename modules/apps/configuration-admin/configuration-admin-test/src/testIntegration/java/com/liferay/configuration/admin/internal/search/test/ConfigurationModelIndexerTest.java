/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.internal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osgi.util.service.OSGiServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition.Scope;
import com.liferay.portal.configuration.metatype.definitions.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.definitions.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class ConfigurationModelIndexerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_bundle = FrameworkUtil.getBundle(ConfigurationModelIndexerTest.class);

		_bundleContext = _bundle.getBundleContext();

		Bundle configAdminWebBundle = BundleUtil.getBundle(
			_bundleContext, "com.liferay.configuration.admin.web");

		Class<?> configurationModelClass = configAdminWebBundle.loadClass(
			"com.liferay.configuration.admin.web.internal.model." +
				"ConfigurationModel");

		_configurationModelConstructor = configurationModelClass.getConstructor(
			String.class, String.class, Configuration.class,
			ExtendedObjectClassDefinition.class, boolean.class);
	}

	@After
	public void tearDown() throws SearchException {
		for (Document document : _documents) {
			_indexWriterHelper.deleteDocument(
				CompanyConstants.SYSTEM, document.getUID(), true);
		}

		_documents.clear();
	}

	@Test
	public void testLocalizedAttributes() throws Exception {
		Map<String, String> extensionAttributes = HashMapBuilder.put(
			"factoryInstanceLabelAttribute", "companyId"
		).put(
			"scope", Scope.COMPANY.toString()
		).build();

		ExtendedAttributeDefinition[] extendedAttributeDefinitions = {
			new SimpleExtendedAttributeDefinition(
				"com.liferay.configuration.admin.web.test.attribute." +
					"description",
				"com.liferay.configuration.admin.web.test.attribute.name")
		};

		ExtendedObjectClassDefinition extendedObjectClassDefinition =
			new SimpleExtendedObjectClassDefinition(
				extendedAttributeDefinitions, extensionAttributes);

		Object configurationModel = _configurationModelConstructor.newInstance(
			StringPool.QUESTION, "com.liferay.configuration.admin.web", null,
			extendedObjectClassDefinition, true);

		Document document = _indexer.getDocument(configurationModel);

		String[] attributeDescriptions = document.getValues(
			"configurationModelAttributeDescription_en_US");

		Assert.assertEquals(
			"Test_Attribute_Description", attributeDescriptions[0]);

		String[] attributeNames = document.getValues(
			"configurationModelAttributeName_en_US");

		Assert.assertEquals("Test_Attribute_Name", attributeNames[0]);
	}

	@Test
	public void testSearchAfterReindex() throws Exception {
		_addCompanyFactoryConfiguration();

		_assertSearchResults();

		_addCompanyFactoryConfiguration();

		_assertSearchResults();
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private Configuration _addCompanyFactoryConfiguration() throws Exception {
		Configuration configuration = OSGiServiceUtil.callService(
			_bundleContext, ConfigurationAdmin.class,
			configurationAdmin -> configurationAdmin.createFactoryConfiguration(
				_PID, StringPool.QUESTION));

		ExtendedObjectClassDefinition extendedObjectClassDefinition =
			new SimpleExtendedObjectClassDefinition(
				configuration,
				HashMapBuilder.put(
					"factoryInstanceLabelAttribute", "companyId"
				).put(
					"scope", Scope.COMPANY.toString()
				).build());

		Object configurationModel = _configurationModelConstructor.newInstance(
			StringPool.QUESTION, _bundle.getSymbolicName(), configuration,
			extendedObjectClassDefinition, true);

		Document document = _indexer.getDocument(configurationModel);

		_documents.add(document);

		_indexWriterHelper.addDocument(CompanyConstants.SYSTEM, document, true);

		return configuration;
	}

	private void _assertSearchResults() throws Exception {
		SearchContext searchContext = new SearchContext();

		searchContext.setAndSearch(false);
		searchContext.setCompanyId(CompanyConstants.SYSTEM);
		searchContext.setKeywords(_PID);
		searchContext.setLocale(LocaleUtil.US);

		Hits hits = _indexer.search(searchContext);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());
	}

	private static final String _PID = RandomTestUtil.randomString(50);

	private Bundle _bundle;
	private BundleContext _bundleContext;
	private Constructor<?> _configurationModelConstructor;
	private final List<Document> _documents = new ArrayList<>();

	@Inject
	private IndexWriterHelper _indexWriterHelper;

	@Inject(
		filter = "component.name=com.liferay.configuration.admin.web.internal.search.ConfigurationModelIndexer"
	)
	private Indexer<Object> _indexer;

	private class SimpleExtendedAttributeDefinition
		implements ExtendedAttributeDefinition {

		public SimpleExtendedAttributeDefinition(
			String description, String name) {

			_description = description;
			_name = name;
		}

		@Override
		public int getCardinality() {
			return 0;
		}

		@Override
		public String[] getDefaultValue() {
			return new String[0];
		}

		@Override
		public String getDescription() {
			return _description;
		}

		@Override
		public Map<String, String> getExtensionAttributes(String uri) {
			return null;
		}

		@Override
		public Set<String> getExtensionUris() {
			return null;
		}

		@Override
		public String getID() {
			return StringPool.BLANK;
		}

		@Override
		public String getName() {
			return _name;
		}

		@Override
		public String[] getOptionLabels() {
			return new String[0];
		}

		@Override
		public String[] getOptionValues() {
			return new String[0];
		}

		@Override
		public int getType() {
			return 0;
		}

		@Override
		public String validate(String s) {
			return null;
		}

		private final String _description;
		private final String _name;

	}

	private class SimpleExtendedObjectClassDefinition
		implements ExtendedObjectClassDefinition {

		public SimpleExtendedObjectClassDefinition(
			Configuration configuration,
			Map<String, String> extensionAttributes) {

			this(new ExtendedAttributeDefinition[0], extensionAttributes);
		}

		public SimpleExtendedObjectClassDefinition(
			ExtendedAttributeDefinition[] extendedAttributeDefinitions,
			Map<String, String> extensionAttributes) {

			_extendedAttributeDefinitions = extendedAttributeDefinitions;
			_extensionAttributes = extensionAttributes;
		}

		@Override
		public ExtendedAttributeDefinition[] getAttributeDefinitions(
			int filter) {

			return _extendedAttributeDefinitions;
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public Map<String, String> getExtensionAttributes(String uri) {
			return _extensionAttributes;
		}

		@Override
		public Set<String> getExtensionUris() {
			return null;
		}

		@Override
		public String getID() {
			return _PID;
		}

		@Override
		public InputStream getIcon(int size) throws IOException {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		private final ExtendedAttributeDefinition[]
			_extendedAttributeDefinitions;
		private final Map<String, String> _extensionAttributes;

	}

}