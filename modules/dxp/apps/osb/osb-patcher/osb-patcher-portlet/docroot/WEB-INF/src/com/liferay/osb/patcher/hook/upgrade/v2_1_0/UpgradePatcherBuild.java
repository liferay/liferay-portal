/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.hook.upgrade.v2_1_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherBuild();
	}

	protected void updatePatcherBuild() throws Exception {
		for (long patcherBuildId : _PATCHER_BUILD_IDS_DELETE) {
			runSQL(
				"delete from OSB_PatcherBuild where patcherBuildId = " +
					patcherBuildId);

			runSQL(
				"delete from OSB_PatcherBuilds_PatcherFixes where " +
					"patcherBuildId = " + patcherBuildId);
		}

		for (int i = 0; i < _PATCHER_BUILD_IDS.length; i++) {
			runSQL(
				"update OSB_PatcherBuild set name = '" +
					_PATCHER_BUILD_NAMES[i] + "' where patcherBuildId = " +
						_PATCHER_BUILD_IDS[i]);

			runSQL(
				"update OSB_PatcherBuild set patcherBuildKey = '" +
					_PATCHER_BUILD_KEYS[i] + "' where patcherBuildId = " +
						_PATCHER_BUILD_IDS[i]);
		}
	}

	private static final long[] _PATCHER_BUILD_IDS = {
		88614, 88871, 105832, 125914, 148509, 160032, 160500, 163147, 198572,
		206317, 218671, 227091, 242456, 249889, 282564, 322504, 329467
	};

	private static final long[] _PATCHER_BUILD_IDS_DELETE = {
		149413, 323740, 431292, 759896
	};

	private static final String[] _PATCHER_BUILD_KEYS = {
		"d7355d83c5b7ac791d3258e4a5dfca6003c1c7c3",
		"363595296cf4d3c18c9d7afa7dc32509c80d9936",
		"de46457fb97b192241646a105fe8107a65101ee1",
		"c0ae447448dd8f7e7af2ea273ca3b2d3d6c3bfcc",
		"bb308191e884d18a37d73d6ecf1479fffb99fdaf",
		"242c21e07d8692bd8098c332c465d48928f98a95",
		"65cc9be5343b66a12effcd4edd4ccc4ed321f2b3",
		"c310c04366b29a05724211af8339eececc0c254f",
		"b31f835b16b3ed13a5684a75032e4ea692c092dd",
		"6c97ce7519387375bad0f464d1939de3c8f514ac",
		"0357cae5e57e3b0f0563dab338e8ac9796dfb80e",
		"1b37cef28a444aae97bfc240112d19b140933b15",
		"75fe3cd725df170e84d7bbd28d250fca0d3bb65a",
		"20f2e45e39096070872bf44a95e7d19e7931faa0",
		"1290373d4f809870409d9da65c023c2a4d0c7ccf",
		"5a00686265e13639bf8b87364f7e52b06062cae8",
		"2dc80bdecade2e056459568b9a67d2458f11d6fa"
	};

	private static final String[] _PATCHER_BUILD_NAMES = {
		"LPE-7689,LPE-7965,LPE-9162,LPE-9445,LPE-9446,LPE-9447,LPE-9477",
		"LPE-9150,LPE-9152,LPE-9442,LPE-9456,LPE-9517,LPE-9569",
		"LPE-9694,LPE-9695,LPE-9696", "LPE-9788,LPE-9796",
		"LPE-9150,LPE-9152,LPE-9442,LPE-9456,LPE-9517,LPE-9821",
		"LPE-9150,LPE-9480,LPE-9788,LPE-9796",
		"LPE-9343,LPE-9344,LPE-9400,LPE-9464,LPE-9569",
		"LPE-9150,LPE-9152,LPE-9442,LPE-9456,LPE-9517,LPE-9569,LPE-9821",
		"LPE-8740,LPE-9289", "LPE-8740,LPE-9593,LPE-9889",
		"LPE-9150,LPE-9480,LPE-9788,LPE-9796,LPE-9879",
		"LPE-9150,LPE-9152,LPE-9442,LPE-9456,LPE-9517,LPE-9569,LPE-9821," +
			"LPP-9057",
		"LPE-8829,LPE-8984,LPE-9148,LPE-9150,LPE-9162,LPE-9285,LPE-9289," +
			"LPE-9343,LPE-9344,LPE-9353,LPE-9364,LPE-9366,LPE-9371,LPE-9377," +
				"LPE-9397,LPE-9400,LPE-9401,LPE-9402,LPE-9403,LPE-9410,LPE-9426," +
					"LPE-9429,LPE-9431,LPE-9435,LPE-9442,LPE-9448,LPE-9449,LPE-9456," +
						"LPE-9458,LPE-9459,LPE-9460,LPE-9464,LPE-9469,LPE-9473,LPE-9474," +
							"LPE-9477,LPE-9500,LPE-9508,LPE-9512,LPE-9514,LPE-9517,LPE-9526," +
								"LPE-9534,LPE-9537,LPE-9556,LPE-9569,LPE-9579,LPE-9584,LPE-9586," +
									"LPE-9590,LPE-9613,LPE-9754",
		"LPE-10028,LPE-9545,LPE-9788,LPE-9796,LPE-9927,LPP-9005", "LPE-9429",
		"LPE-7689,LPE-8829,LPE-9150,LPE-9152,LPE-9353,LPE-9366,LPE-9371," +
			"LPE-9401,LPE-9402,LPE-9429,LPE-9431,LPE-9435,LPE-9442,LPE-9445," +
				"LPE-9446,LPE-9447,LPE-9448,LPE-9449,LPE-9456,LPE-9458,LPE-9459," +
					"LPE-9477,LPE-9500,LPE-9508,LPE-9512,LPE-9514,LPE-9517,LPE-9526," +
						"LPE-9534,LPE-9537,LPE-9556,LPE-9569,LPE-9586,LPE-9613,LPE-9811," +
							"LPE-9821,LPE-9865,LPP-9057",
		"LPE-10163,LPE-7689,LPE-8829,LPE-9150,LPE-9152,LPE-9353,LPE-9366," +
			"LPE-9371,LPE-9401,LPE-9402,LPE-9429,LPE-9431,LPE-9435,LPE-9442," +
				"LPE-9445,LPE-9446,LPE-9447,LPE-9448,LPE-9449,LPE-9456,LPE-9458," +
					"LPE-9459,LPE-9477,LPE-9500,LPE-9508,LPE-9512,LPE-9514,LPE-9517," +
						"LPE-9526,LPE-9534,LPE-9537,LPE-9556,LPE-9569,LPE-9586,LPE-9613," +
							"LPE-9811,LPE-9821,LPE-9865,LPP-9057"
	};

}