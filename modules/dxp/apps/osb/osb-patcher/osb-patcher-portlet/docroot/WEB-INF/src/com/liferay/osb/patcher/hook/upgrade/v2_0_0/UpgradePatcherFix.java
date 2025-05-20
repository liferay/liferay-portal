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

package com.liferay.osb.patcher.hook.upgrade.v2_0_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherFix extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFix();
	}

	protected void updatePatcherFix() throws Exception {
		for (long patcherFixId : _PATCHER_FIX_IDS_DELETE) {
			runSQL(
				"delete from OSB_PatcherFix where patcherFixId = " +
					patcherFixId);
		}

		runSQL(
			"update OSB_PatcherFix set patcherPortalVersionId = 221718 " +
				"where patcherFixId = 837236");

		runSQL(
			"update OSB_PatcherFix set patcherPortalVersionId = 10549 " +
				"where patcherFixId = 888823");
	}

	private static final long[] _PATCHER_FIX_IDS_DELETE = {
		345991, 346472, 346474, 346476, 346478, 346480, 346482, 346484, 346486,
		346488, 346490, 346492, 346494, 346496, 346498, 346500, 346502, 346504,
		346506, 346508, 346510, 346512, 346514, 346516, 346518, 346520, 346522,
		346524, 346526, 346528, 346530, 346532, 346534, 346536, 346538, 346540,
		346542, 346544, 346546, 346548, 346550, 346552, 346554, 346556, 346558,
		346560, 346562, 346564, 346566, 346568, 346570, 346572, 346574, 346576,
		346578, 346580, 346582, 346584, 346586, 346588, 346590, 346592, 346594,
		346596, 346598, 346600, 346602, 346604, 346606, 346608, 346610, 346612,
		346614, 346616, 346618, 346620, 346622, 346624, 346626, 346628, 346630,
		346632, 346634, 346636, 346638, 346640, 346642, 346644, 346646, 346648,
		346650, 346652, 346654, 346656, 346658, 346660, 346662, 346664, 346666,
		346668, 346670, 346672, 346674, 346676, 346678, 346680, 346682, 346684,
		346686, 346688, 346690, 346692, 346694, 346696, 346698, 346700, 346702,
		346704, 346706, 346708, 346710, 346712, 346714, 346716, 346718, 346720,
		346722, 346724, 346726, 346728, 346730, 346732, 346734, 346736, 346738,
		346740, 346742, 346744, 346746, 346748, 346750, 346752, 346754, 346756,
		346758, 346760, 346762, 346764, 346766, 346768, 346770, 346772, 346774,
		346776, 346778, 346780, 346782, 346784, 346786, 346788, 346790, 346792,
		346794, 346796, 346798, 346800, 346802, 346804, 346806, 346808, 346810,
		346812
	};

}