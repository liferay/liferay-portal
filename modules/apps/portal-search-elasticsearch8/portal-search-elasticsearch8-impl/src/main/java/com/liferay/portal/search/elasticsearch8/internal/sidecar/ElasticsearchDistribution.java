/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.sidecar;

import com.liferay.petra.string.StringBundler;

import java.util.Arrays;
import java.util.List;

/**
 * @author Bryan Engler
 */
public class ElasticsearchDistribution implements Distribution {

	public static final String VERSION = "9.5.7";

	@Override
	public Distributable getElasticsearchDistributable() {
		return new DistributableImpl(
			StringBundler.concat(
				"https://artifacts.elastic.co/downloads/elasticsearch",
				"/elasticsearch-", VERSION, "-linux-x86_64.tar.gz"),
			_ELASTICSEARCH_CHECKSUM);
	}

	@Override
	public List<Distributable> getPluginDistributables() {
		return Arrays.asList(
			new DistributableImpl(
				_getDownloadURLString("analysis-icu"), _ICU_CHECKSUM),
			new DistributableImpl(
				_getDownloadURLString("analysis-kuromoji"), _KUROMOJI_CHECKSUM),
			new DistributableImpl(
				_getDownloadURLString("analysis-smartcn"), _SMARTCN_CHECKSUM),
			new DistributableImpl(
				_getDownloadURLString("analysis-stempel"), _STEMPEL_CHECKSUM));
	}

	private String _getDownloadURLString(String plugin) {
		return StringBundler.concat(
			"https://artifacts.elastic.co/downloads/elasticsearch-plugins/",
			plugin, "/", plugin, "-", VERSION, ".zip");
	}

	private static final String _ELASTICSEARCH_CHECKSUM =
		"0f43479a7456c41eb0f3879adf959e023e5f6d68e969bd9d6fca791d2e84ca063e77" +
			"18cc8fc19e66363b9e312b575b51bf9874c607625b9ac3ca9e82cdb6bfbb";

	private static final String _ICU_CHECKSUM =
		"9466f544f7aa0cff454ea052d2d8f9657cb37106e9d17626a524f35fc0981cb34e2c" +
			"7ed1e4dd8b4640ae8c6c67e6e44ef140ef8311a2ad3719f4777946f5464e";

	private static final String _KUROMOJI_CHECKSUM =
		"9c46b4522cad84b902eeaba418fd97f7728c302ab811df3846ca6bc3bd44b071f37f" +
			"c338d3a98546ae4089e6bd2251ecac6bb78d5bee9740c1553e7fa0446af0";

	private static final String _SMARTCN_CHECKSUM =
		"2b3768240436343990ea72c8c89747d1c7aecf62fb0bee95428d467e4832250e516d" +
			"daef7c50a848af49538dbfa906c4369ff924e626cfb358c993df75dfe9b0";

	private static final String _STEMPEL_CHECKSUM =
		"8bc75eb24694d70811c42d9311f1fa52bc6ce21e901d01efdc53e02523c226d57748" +
			"f2d8b848002e7b233d75be7f27a6010c8ccdf5595c7af4cef20abb880c21";

}