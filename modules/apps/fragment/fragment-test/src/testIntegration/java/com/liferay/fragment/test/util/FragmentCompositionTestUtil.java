/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.test.util;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.fragment.service.FragmentCompositionServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Lourdes Fernández Besada
 */
public class FragmentCompositionTestUtil {

	public static FragmentComposition addFragmentComposition(
			long fragmentCollectionId, String name)
		throws PortalException {

		FragmentCollection fragmentCollection =
			FragmentCollectionLocalServiceUtil.getFragmentCollection(
				fragmentCollectionId);

		return FragmentCompositionServiceUtil.addFragmentComposition(
			null, fragmentCollection.getGroupId(),
			fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), name, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), 0, WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				fragmentCollection.getGroupId()));
	}

}