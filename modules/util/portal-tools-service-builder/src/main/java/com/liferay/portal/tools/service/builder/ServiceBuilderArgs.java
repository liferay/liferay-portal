/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder;

import com.liferay.petra.string.StringPool;

/**
 * @author Raymond Augé
 * @author Andrea Di Giorgi
 */
public class ServiceBuilderArgs {

	public static final String[] MODEL_HINTS_CONFIGS = {
		"classpath*:META-INF/portal-model-hints.xml",
		"META-INF/portal-model-hints.xml",
		"classpath*:META-INF/ext-model-hints.xml",
		"classpath*:META-INF/portlet-model-hints.xml"
	};

	public static final String OUTPUT_KEY_MODIFIED_FILES =
		"service.builder.modified.files";

	public static final String[] READ_ONLY_PREFIXES = {
		"dslQuery", "dynamicQuery", "fetch", "get", "has", "is", "load",
		"reindex", "search"
	};

	public static final String[] RESOURCE_ACTION_CONFIGS = {
		"META-INF/resource-actions/default.xml", "resource-actions/default.xml"
	};

	public String getApiDirName() {
		return _apiDirName;
	}

	public String getBeanLocatorUtil() {
		return _beanLocatorUtil;
	}

	public long getBuildNumber() {
		return _buildNumber;
	}

	public int getDatabaseNameMaxLength() {
		return _databaseNameMaxLength;
	}

	public String getHbmFileName() {
		return _hbmFileName;
	}

	public String getImplDirName() {
		return _implDirName;
	}

	public String[] getIncubationFeatures() {
		return _incubationFeatures;
	}

	public String getInputFileName() {
		return _inputFileName;
	}

	public String[] getModelHintsConfigs() {
		return _modelHintsConfigs;
	}

	public String getModelHintsFileName() {
		return _modelHintsFileName;
	}

	public String getPluginName() {
		return _pluginName;
	}

	public String getPropsUtil() {
		return _propsUtil;
	}

	public String[] getReadOnlyPrefixes() {
		return _readOnlyPrefixes;
	}

	public String[] getResourceActionsConfigs() {
		return _resourceActionsConfigs;
	}

	public String getResourcesDirName() {
		return _resourcesDirName;
	}

	public String getSpringFileName() {
		return _springFileName;
	}

	public String[] getSpringNamespaces() {
		return _springNamespaces;
	}

	public String getSqlDirName() {
		return _sqlDirName;
	}

	public String getSqlFileName() {
		return _sqlFileName;
	}

	public String getSqlIndexesFileName() {
		return _sqlIndexesFileName;
	}

	public String getSqlSequencesFileName() {
		return _sqlSequencesFileName;
	}

	public String getTargetEntityName() {
		return _targetEntityName;
	}

	public String getTestDirName() {
		return _testDirName;
	}

	public String getUADDirName() {
		return _uadDirName;
	}

	public String getUADTestIntegrationDirName() {
		return _uadTestIntegrationDirName;
	}

	public boolean isAutoImportDefaultReferences() {
		return _autoImportDefaultReferences;
	}

	public boolean isAutoNamespaceTables() {
		return _autoNamespaceTables;
	}

	public boolean isBuildNumberIncrement() {
		return _buildNumberIncrement;
	}

	public boolean isOsgiModule() {
		return _osgiModule;
	}

	public void setApiDirName(String apiDirName) {
		_apiDirName = apiDirName;
	}

	public void setAutoImportDefaultReferences(
		boolean autoImportDefaultReferences) {

		_autoImportDefaultReferences = autoImportDefaultReferences;
	}

	public void setAutoNamespaceTables(boolean autoNamespaceTables) {
		_autoNamespaceTables = autoNamespaceTables;
	}

	public void setBeanLocatorUtil(String beanLocatorUtil) {
		_beanLocatorUtil = beanLocatorUtil;
	}

	public void setBuildNumber(long buildNumber) {
		_buildNumber = buildNumber;
	}

	public void setBuildNumberIncrement(boolean buildNumberIncrement) {
		_buildNumberIncrement = buildNumberIncrement;
	}

	public void setDatabaseNameMaxLength(int databaseNameMaxLength) {
		_databaseNameMaxLength = databaseNameMaxLength;
	}

	public void setHbmFileName(String hbmFileName) {
		_hbmFileName = hbmFileName;
	}

	public void setImplDirName(String implDirName) {
		_implDirName = implDirName;
	}

	public void setIncubationFeatures(String incubationFeatures) {
		setIncubationFeatures(_split(incubationFeatures));
	}

	public void setIncubationFeatures(String[] incubationFeatures) {
		_incubationFeatures = incubationFeatures;
	}

	public void setInputFileName(String inputFileName) {
		_inputFileName = inputFileName;
	}

	public void setMergeModelHintsConfigs(String mergeModelHintsConfigs) {
		setMergeModelHintsConfigs(_split(mergeModelHintsConfigs));
	}

	public void setMergeModelHintsConfigs(String[] mergeModelHintsConfigs) {
		_setModelHintsConfigs(
			_append(_modelHintsConfigs, mergeModelHintsConfigs));
	}

	public void setMergeReadOnlyPrefixes(String mergeReadOnlyPrefixes) {
		setMergeReadOnlyPrefixes(_split(mergeReadOnlyPrefixes));
	}

	public void setMergeReadOnlyPrefixes(String[] mergeReadOnlyPrefixes) {
		_setReadOnlyPrefixes(_append(_readOnlyPrefixes, mergeReadOnlyPrefixes));
	}

	public void setMergeResourceActionsConfigs(
		String mergeResourceActionsConfigs) {

		setMergeResourceActionsConfigs(_split(mergeResourceActionsConfigs));
	}

	public void setMergeResourceActionsConfigs(
		String[] mergeResourceActionsConfigs) {

		_setResourceActionsConfigs(
			_append(_resourceActionsConfigs, mergeResourceActionsConfigs));
	}

	public void setModelHintsConfigs(String modelHintsConfigs) {
		setModelHintsConfigs(_split(modelHintsConfigs));
	}

	public void setModelHintsConfigs(String[] modelHintsConfigs) {
		_setModelHintsConfigs(modelHintsConfigs);
	}

	public void setModelHintsFileName(String modelHintsFileName) {
		_modelHintsFileName = modelHintsFileName;
	}

	public void setOsgiModule(boolean osgiModule) {
		_osgiModule = osgiModule;
	}

	public void setPluginName(String pluginName) {
		_pluginName = pluginName;
	}

	public void setPropsUtil(String propsUtil) {
		_propsUtil = propsUtil;
	}

	public void setReadOnlyPrefixes(String readOnlyPrefixes) {
		setReadOnlyPrefixes(_split(readOnlyPrefixes));
	}

	public void setReadOnlyPrefixes(String[] readOnlyPrefixes) {
		_setReadOnlyPrefixes(readOnlyPrefixes);
	}

	public void setResourceActionsConfigs(String resourceActionsConfigs) {
		setResourceActionsConfigs(_split(resourceActionsConfigs));
	}

	public void setResourceActionsConfigs(String[] resourceActionsConfigs) {
		_setResourceActionsConfigs(resourceActionsConfigs);
	}

	public void setResourcesDirName(String resourcesDirName) {
		_resourcesDirName = resourcesDirName;
	}

	public void setSpringFileName(String springFileName) {
		_springFileName = springFileName;
	}

	public void setSpringNamespaces(String springNamespaces) {
		setSpringNamespaces(_split(springNamespaces));
	}

	public void setSpringNamespaces(String[] springNamespaces) {
		_springNamespaces = springNamespaces;
	}

	public void setSqlDirName(String sqlDirName) {
		_sqlDirName = sqlDirName;
	}

	public void setSqlFileName(String sqlFileName) {
		_sqlFileName = sqlFileName;
	}

	public void setSqlIndexesFileName(String sqlIndexesFileName) {
		_sqlIndexesFileName = sqlIndexesFileName;
	}

	public void setSqlSequencesFileName(String sqlSequencesFileName) {
		_sqlSequencesFileName = sqlSequencesFileName;
	}

	public void setTargetEntityName(String targetEntityName) {
		_targetEntityName = targetEntityName;
	}

	public void setTestDirName(String testDirName) {
		_testDirName = testDirName;
	}

	public void setUADDirName(String uadDirName) {
		_uadDirName = uadDirName;
	}

	public void setUADTestIntegrationDirName(String uadTestIntegrationDirName) {
		_uadTestIntegrationDirName = uadTestIntegrationDirName;
	}

	private String[] _append(String[] array1, String[] array2) {
		String[] newArray = new String[array1.length + array2.length];

		System.arraycopy(array1, 0, newArray, 0, array1.length);

		System.arraycopy(array2, 0, newArray, array1.length, array2.length);

		return newArray;
	}

	private void _setModelHintsConfigs(String[] modelHintsConfigs) {
		if (_modelHintsConfigsSet) {
			throw new IllegalStateException(
				"Unable to call both setMergeModelHintsConfigs and " +
					"setModelHintsConfigs");
		}

		_modelHintsConfigsSet = true;

		_modelHintsConfigs = modelHintsConfigs;
	}

	private void _setReadOnlyPrefixes(String[] readOnlyPrefixes) {
		if (_readOnlyPrefixesSet) {
			throw new IllegalStateException(
				"Unable to call both setMergeReadOnlyPrefixes and " +
					"setReadOnlyPrefixes");
		}

		_readOnlyPrefixesSet = true;

		_readOnlyPrefixes = readOnlyPrefixes;
	}

	private void _setResourceActionsConfigs(String[] resourceActionsConfigs) {
		if (_resourceActionsConfigsSet) {
			throw new IllegalStateException(
				"Unable to call both setMergeResourceActionsConfigs and " +
					"setResourceActionsConfigs");
		}

		_resourceActionsConfigsSet = true;

		_resourceActionsConfigs = resourceActionsConfigs;
	}

	private String[] _split(String s) {
		return s.split(",");
	}

	private String _apiDirName = "../portal-kernel/src";
	private boolean _autoImportDefaultReferences = true;
	private boolean _autoNamespaceTables;
	private String _beanLocatorUtil =
		"com.liferay.portal.kernel.bean.PortalBeanLocatorUtil";
	private long _buildNumber = 1;
	private boolean _buildNumberIncrement = true;
	private int _databaseNameMaxLength = 30;
	private String _hbmFileName = "src/META-INF/portal-hbm.xml";
	private String _implDirName = "src";
	private String[] _incubationFeatures = {};
	private String _inputFileName = "service.xml";
	private String[] _modelHintsConfigs = MODEL_HINTS_CONFIGS;
	private boolean _modelHintsConfigsSet;
	private String _modelHintsFileName = "src/META-INF/portal-model-hints.xml";
	private boolean _osgiModule;
	private String _pluginName;
	private String _propsUtil = "com.liferay.portal.kernel.util.PropsUtil";
	private String[] _readOnlyPrefixes = READ_ONLY_PREFIXES;
	private boolean _readOnlyPrefixesSet;
	private String[] _resourceActionsConfigs = RESOURCE_ACTION_CONFIGS;
	private boolean _resourceActionsConfigsSet;
	private String _resourcesDirName = "src";
	private String _springFileName = "src/META-INF/portal-spring.xml";
	private String[] _springNamespaces = {"beans"};
	private String _sqlDirName = "../sql";
	private String _sqlFileName = "portal-tables.sql";
	private String _sqlIndexesFileName = "indexes.sql";
	private String _sqlSequencesFileName = "sequences.sql";
	private String _targetEntityName;
	private String _testDirName = "test/integration";
	private String _uadDirName = StringPool.BLANK;
	private String _uadTestIntegrationDirName = StringPool.BLANK;

}