/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.version;

import com.liferay.petra.string.StringPool;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Luis Ortiz
 */
public class VersionTest {

	@Test
	public void testCompareEqualFinalVersions() {
		Version version1 = Version.parseVersion("1.2.3");
		Version version2 = Version.parseVersion("1.2.3");

		Assert.assertTrue(version1.compareTo(version2) == 0);
	}

	@Test
	public void testCompareEqualStepVersions() {
		Version version1 = Version.parseVersion("1.2.3.step-3");
		Version version2 = Version.parseVersion("1.2.3.step-3");

		Assert.assertTrue(version1.compareTo(version2) == 0);
	}

	@Test
	public void testCompareFinalToStepVersion() {
		Version version1 = Version.parseVersion("1.2.3");
		Version version2 = Version.parseVersion("1.2.3.step-2");

		Assert.assertTrue(version1.compareTo(version2) > 0);
	}

	@Test
	public void testCompareStepToFinalVersion() {
		Version version1 = Version.parseVersion("1.2.3.step-2");
		Version version2 = Version.parseVersion("1.2.3");

		Assert.assertTrue(version1.compareTo(version2) < 0);
	}

	@Test
	public void testCompareSteps() {
		Version version1 = Version.parseVersion("1.2.3.step-1");
		Version version2 = Version.parseVersion("1.2.3.step-2");

		Assert.assertTrue(version1.compareTo(version2) < 0);
	}

	@Test
	public void testEqualFinalVersions() {
		Version version1 = Version.parseVersion("1.2.3");
		Version version2 = Version.parseVersion("1.2.3");

		Assert.assertTrue(version1.equals(version2));
	}

	@Test
	public void testEqualStepVersions() {
		Version version1 = Version.parseVersion("1.2.3.step-1");
		Version version2 = Version.parseVersion("1.2.3.step-1");

		Assert.assertTrue(version1.equals(version2));
	}

	@Test
	public void testNotEqualVersions() {
		Version version1 = Version.parseVersion("1.2.3");
		Version version2 = Version.parseVersion("1.2.3.step-4");

		Assert.assertTrue(!version1.equals(version2));
	}

	@Test
	public void testParseEmptyVersion() {
		Version version = Version.parseVersion("");

		Assert.assertEquals(0, version.getMajor());
		Assert.assertEquals(0, version.getMinor());
		Assert.assertEquals(0, version.getMicro());
		Assert.assertEquals(StringPool.BLANK, version.getQualifier());
	}

	@Test
	public void testParseNullVersion() {
		Version version = Version.parseVersion(null);

		Assert.assertEquals(0, version.getMajor());
		Assert.assertEquals(0, version.getMinor());
		Assert.assertEquals(0, version.getMicro());
		Assert.assertEquals(StringPool.BLANK, version.getQualifier());
	}

	@Test
	public void testParseStepVersion() {
		Version version = Version.parseVersion("1.2.3.step-4");

		Assert.assertEquals(1, version.getMajor());
		Assert.assertEquals(2, version.getMinor());
		Assert.assertEquals(3, version.getMicro());
		Assert.assertEquals("step-4", version.getQualifier());
	}

	@Test
	public void testParseVersion() {
		Version version = Version.parseVersion("1.2.3");

		Assert.assertEquals(1, version.getMajor());
		Assert.assertEquals(2, version.getMinor());
		Assert.assertEquals(3, version.getMicro());
		Assert.assertEquals(StringPool.BLANK, version.getQualifier());
	}

	@Test
	public void testParseWrongFormatVersion() {
		Version version = Version.parseVersion("V1.4.3");

		Assert.assertEquals(0, version.getMajor());
		Assert.assertEquals(0, version.getMinor());
		Assert.assertEquals(0, version.getMicro());
		Assert.assertEquals(StringPool.BLANK, version.getQualifier());
	}

	@Test
	public void testToStringFinalVersion() {
		Version version = Version.parseVersion("1.2.3");

		Assert.assertEquals("1.2.3", version.toString());
	}

	@Test
	public void testToStringStepVersion() {
		Version version = Version.parseVersion("1.2.3.step-4");

		Assert.assertEquals("1.2.3.step-4", version.toString());
	}

}