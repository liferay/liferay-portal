/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.petra.string.StringBundler;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.check.JSONUpgradeLiferayThemePackageJSONCheck;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author Kevin Lee
 */
public class UpgradeSourceProcessorTest extends BaseSourceProcessorTestCase {

	@Test
	public void testGradleUpgradeReleaseDxpCheck() throws Exception {
		test("upgrade/GradleUpgradeReleaseDxpCheck.testgradle");
	}

	@Test
	public void testJSONUpgradeLiferayThemePackageJSONCheck() throws Exception {
		JSONUpgradeLiferayThemePackageJSONCheck.setTestMode(true);

		test(
			"upgrade/json-upgrade-liferay-theme-package-json-check/package." +
				"testjson");
	}

	@Test
	public void testPropertiesUpgradeLiferayPluginPackageFileCheck()
		throws Exception {

		test("upgrade/liferay-plugin-package.testproperties");
	}

	@Test
	public void testUpgradeBNDDeclarativeServicesCheck() throws Exception {
		test("upgrade/upgrade-declarative-services-check/bnd.testbnd");
		test("upgrade/upgrade-declarative-services-replace-check/bnd.testbnd");
	}

	@Test
	public void testUpgradeBNDIncludeResourceCheck() throws Exception {
		test("upgrade/upgrade-include-resource-check/bnd.testbnd");
	}

	@Test
	public void testUpgradeGradleIncludeResourceCheck() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"upgrade/upgrade-include-resource-check/build.testgradle"
			).addDependentFileName(
				"upgrade/upgrade-include-resource-check/bnd.testbnd"
			));
	}

	@Test
	public void testUpgradeImportsCheck() throws Exception {
		test("upgrade/UpgradeImportsCheck.testftl");
		test("upgrade/UpgradeImportsCheck.testjava");
		test("upgrade/UpgradeImportsCheck.testjsp");
	}

	@Test
	public void testUpgradeJavaAssetEntryAssetCategoriesCheck()
		throws Exception {

		test("upgrade/UpgradeJavaAssetEntryAssetCategoriesCheck.testjava");
	}

	@Test
	public void testUpgradeJavaBaseFragmentCollectionContributorExtendedClassesCheck()
		throws Exception {

		test(
			"upgrade/UpgradeJavaBaseFragmentCollectionContributor" +
				"ExtendedClassesCheck.testjava");
	}

	@Test
	public void testUpgradeJavaBaseModelListenerCheck() throws Exception {
		test("upgrade/UpgradeJavaBaseModelListenerCheck.testjava");
	}

	@Test
	public void testUpgradeJavaDDMFormValuesSerializerTrackerCheck()
		throws Exception {

		test("upgrade/UpgradeJavaDDMFormValuesSerializerTrackerCheck.testjava");
	}

	@Test
	public void testUpgradeJavaDisplayPageInfoItemCapabilityCheck()
		throws Exception {

		test("upgrade/UpgradeJavaDisplayPageInfoItemCapabilityCheck.testjava");
	}

	@Test
	public void testUpgradeJavaFacetedSearcherCheck() throws Exception {
		test("upgrade/UpgradeJavaFacetedSearcherCheck.testjava");
	}

	@Test
	public void testUpgradeJavaFDSActionProviderCheck() throws Exception {
		test("upgrade/UpgradeJavaFDSActionProviderCheck.testjava");
	}

	@Test
	public void testUpgradeJavaFDSDataProviderCheck() throws Exception {
		test("upgrade/UpgradeJavaCommerceDataSetDataProviderCheck.testjava");
		test("upgrade/UpgradeJavaClayDataSetDataProviderCheck.testjava");
	}

	@Test
	public void testUpgradeJavaFinderImplCheck() throws Exception {
		test(
			"upgrade/src/service/persistence/impl/UpgradeJavaFinderImplCheck." +
				"testjava");
	}

	@Test
	public void testUpgradeJavaGetFDSTableSchemaParameterCheck()
		throws Exception {

		test("upgrade/UpgradeJavaGetFDSTableSchemaParameterCheck.testjava");
	}

	@Test
	public void testUpgradeJavaGetFileMethodCheck() throws Exception {
		test("upgrade/UpgradeJavaGetFileMethodCheck.testjava");
	}

	@Test
	public void testUpgradeJavaGetLayoutDisplayPageObjectProviderCheck()
		throws Exception {

		test(
			"upgrade/UpgradeJavaGetLayoutDisplayPageObjectProviderCheck." +
				"testjava",
			StringBundler.concat(
				"Unable to resolve variable className for new ",
				"InfoItemReference(). Replace \"TO_BE_REPLACED_FOR_",
				"CLASSNAME\" with the correct type"));
	}

	@Test
	public void testUpgradeJavaGetLayoutDisplayPageProviderCheck()
		throws Exception {

		test("upgrade/UpgradeJavaGetLayoutDisplayPageProviderCheck.testjava");
	}

	@Test
	public void testUpgradeJavaLocalServiceImplCheck() throws Exception {
		test(
			"upgrade/src/service/impl" +
				"/UpgradeJavaLocalServiceImplCheck.testjava");
	}

	@Test
	public void testUpgradeJavaModelPermissionsCheck() throws Exception {
		test("upgrade/UpgradeJavaModelPermissionsCheck.testjava");
	}

	@Test
	public void testUpgradeJavaMultiVMPoolUtilCheck() throws Exception {
		test(
			"upgrade/UpgradeJavaMultiVMPoolUtilCheck.testjava",
			"Unable to resolve types for MultiVMPool.getPortalCache(). " +
				"Replace \"TO_BE_REPLACED\" with the correct type");
	}

	@Test
	public void testUpgradeJavaPortletIdMethodCheck() throws Exception {
		test("upgrade/UpgradeJavaPortletIdMethodCheck.testjava");
	}

	@Test
	public void testUpgradeJavaPortletSharedSearchSettingsCheck()
		throws Exception {

		test("upgrade/UpgradeJavaPortletSharedSearchSettingsCheck.testjava");
	}

	@Test
	public void testUpgradeJavaProductDTOConverterReferenceCheck()
		throws Exception {

		test("upgrade/UpgradeJavaProductDTOConverterReferenceCheck.testjava");
	}

	@Test
	public void testUpgradeJavaSchedulerEntryImplConstructorCheck()
		throws Exception {

		test("upgrade/UpgradeJavaSchedulerEntryImplConstructorCheck.testjava");
	}

	@Test
	public void testUpgradeJavaScreenContributorClassCheck() throws Exception {
		test("upgrade/UpgradeJavaScreenContributorClassCheck.testjava");
	}

	@Test
	public void testUpgradeJavaServiceImplCheck() throws Exception {
		test("upgrade/src/service/impl/UpgradeJavaServiceImplCheck.testjava");
	}

	@Test
	public void testUpgradeJavaServiceReferenceAnnotationCheck()
		throws Exception {

		test("upgrade/UpgradeJavaServiceReferenceAnnotationCheck.testjava");
	}

	@Test
	public void testUpgradeJavaSortFieldNameTranslatorCheck() throws Exception {
		test("upgrade/UpgradeJavaSortFieldNameTranslatorCheck.testjava");
	}

	@Test
	public void testUpgradeJavaStorageTypeAwareCheck() throws Exception {
		test("upgrade/UpgradeJavaStorageTypeAwareCheck.testjava");
	}

	@Test
	public void testUpgradeJSPFieldSetGroupCheck() throws Exception {
		test("upgrade/UpgradeJSPFieldSetGroupCheck.testjsp");
	}

	@Test
	public void testUpgradePortletFTLCheck() throws Exception {
		test("upgrade/UpgradeFTLPortletFTLCheck.testftl");
	}

	@Test
	public void testUpgradeRejectedExecutionHandlerCheck() throws Exception {
		test("upgrade/UpgradeRejectedExecutionHandlerCheck.testjava");
	}

	@Test
	public void testUpgradeSCSSMixinsCheck() throws Exception {
		test(
			"upgrade/UpgradeSCSSMixinsCheck.testscss",
			StringBundler.concat(
				"Do not use \"media-query\" mixing, replace with its ",
				"equivalent (e.g., media-breakpoint-up, media-breakpoint-",
				"only, media-breakpoint-down, etc.), see LPS-194507."));
	}

	@Test
	public void testUpgradeSCSSNodeSassPatternsCheck() throws Exception {
		test("upgrade/UpgradeSCSSNodeSassPatternsCheck.testscss");
	}

	@Test
	public void testUpgradeSetResultsSetTotalMethodCheck() throws Exception {
		test("upgrade/UpgradeJavaSetResultsSetTotalMethodCheck.testjava");
		test("upgrade/UpgradeJSPSetResultsSetTotalMethodCheck.testjsp");
		test("upgrade/UpgradeJSPFSetResultsSetTotalMethodCheck.testjspf");
	}

	@Test
	public void testUpgradeVelocityMigrationCheck() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"upgrade/UpgradeVelocityMigrationCheck.testvm"
			).setExpectedFileName(
				"upgrade/migrated/UpgradeVelocityMigrationCheck.testftl"
			));
	}

	@Test
	public void testXMLUpgradeCompatibilityVersionCheck() throws Exception {
		test("upgrade/XMLUpgradeCompatibilityVersionCheck.testxml");
	}

	@Test
	public void testXMLUpgradeDTDVersionCheck() throws Exception {
		test("upgrade/XMLUpgradeDTDVersionCheck.testxml");
	}

	@Test
	public void testXMLUpgradeServiceDeclarativeServicesCheck()
		throws Exception {

		test("upgrade/upgrade-declarative-services-check/service.testxml");
		test(
			"upgrade/upgrade-declarative-services-replace-check" +
				"/service.testxml");
	}

	@Override
	protected SourceFormatterArgs getSourceFormatterArgs() {
		List<String> checkCategoryNames = new ArrayList<>();

		checkCategoryNames.add("Upgrade");

		List<String> sourceFormatterProperties = new ArrayList<>();

		sourceFormatterProperties.add(
			"upgrade.to.liferay.version=" + _UPGRADE_TO_LIFERAY_VERSION);
		sourceFormatterProperties.add(
			"upgrade.to.release.version=" + _UPGRADE_TO_RELEASE_VERSION);

		SourceFormatterArgs sourceFormatterArgs =
			super.getSourceFormatterArgs();

		sourceFormatterArgs.setCheckCategoryNames(checkCategoryNames);
		sourceFormatterArgs.setJavaParserEnabled(false);
		sourceFormatterArgs.setSourceFormatterProperties(
			sourceFormatterProperties);

		return sourceFormatterArgs;
	}

	private static final String _UPGRADE_TO_LIFERAY_VERSION = "7.4.13.u27";

	private static final String _UPGRADE_TO_RELEASE_VERSION = "2024.q1.1";

}