/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.impl;

import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBTemplate;
import com.liferay.knowledge.base.model.KBTemplateSearchDisplay;
import com.liferay.knowledge.base.model.impl.KBTemplateSearchDisplayImpl;
import com.liferay.knowledge.base.service.base.KBTemplateServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Shin
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=kb",
		"json.web.service.context.path=KBTemplate"
	},
	service = AopService.class
)
public class KBTemplateServiceImpl extends KBTemplateServiceBaseImpl {

	@Override
	public KBTemplate addKBTemplate(
			String portletId, String title, String content,
			ServiceContext serviceContext)
		throws PortalException {

		if (portletId.equals(KBPortletKeys.KNOWLEDGE_BASE_ADMIN)) {
			_adminPortletResourcePermission.check(
				getPermissionChecker(), serviceContext.getScopeGroupId(),
				KBActionKeys.ADD_KB_TEMPLATE);
		}
		else if (portletId.equals(KBPortletKeys.KNOWLEDGE_BASE_DISPLAY)) {
			_displayPortletResourcePermission.check(
				getPermissionChecker(), serviceContext.getScopeGroupId(),
				KBActionKeys.ADD_KB_TEMPLATE);
		}

		return kbTemplateLocalService.addKBTemplate(
			getUserId(), title, content, serviceContext);
	}

	@Override
	public KBTemplate deleteKBTemplate(long kbTemplateId)
		throws PortalException {

		_kbTemplateModelResourcePermission.check(
			getPermissionChecker(), kbTemplateId, KBActionKeys.DELETE);

		return kbTemplateLocalService.deleteKBTemplate(kbTemplateId);
	}

	@Override
	public void deleteKBTemplates(long groupId, long[] kbTemplateIds)
		throws PortalException {

		_adminPortletResourcePermission.check(
			getPermissionChecker(), groupId, KBActionKeys.DELETE_KB_TEMPLATES);

		kbTemplateLocalService.deleteKBTemplates(kbTemplateIds);
	}

	@Override
	public List<KBTemplate> getGroupKBTemplates(
		long groupId, int start, int end,
		OrderByComparator<KBTemplate> orderByComparator) {

		return kbTemplatePersistence.filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public int getGroupKBTemplatesCount(long groupId) {
		return kbTemplatePersistence.filterCountByGroupId(groupId);
	}

	@Override
	public KBTemplate getKBTemplate(long kbTemplateId) throws PortalException {
		_kbTemplateModelResourcePermission.check(
			getPermissionChecker(), kbTemplateId, KBActionKeys.VIEW);

		return kbTemplatePersistence.findByPrimaryKey(kbTemplateId);
	}

	@Override
	public KBTemplateSearchDisplay getKBTemplateSearchDisplay(
			long groupId, String title, String content, Date startDate,
			Date endDate, boolean andOperator, int[] curStartValues, int cur,
			int delta, OrderByComparator<KBTemplate> orderByComparator)
		throws PortalException {

		// See LPS-9546

		int start = 0;

		if (curStartValues.length > (cur - SearchContainer.DEFAULT_CUR)) {
			start = curStartValues[cur - SearchContainer.DEFAULT_CUR];

			curStartValues = ArrayUtil.subset(
				curStartValues, 0, cur - SearchContainer.DEFAULT_CUR + 1);
		}
		else {
			cur = SearchContainer.DEFAULT_CUR;

			curStartValues = new int[] {0};
		}

		int end = start + _INTERVAL;

		List<KBTemplate> kbTemplates = new ArrayList<>();

		int curStartValue = 0;

		while (curStartValue == 0) {
			List<KBTemplate> curKBTemplates = kbTemplateLocalService.search(
				groupId, title, content, startDate, endDate, andOperator, start,
				end, orderByComparator);

			if (curKBTemplates.isEmpty()) {
				break;
			}

			for (int i = 0; i < curKBTemplates.size(); i++) {
				KBTemplate curKBTemplate = curKBTemplates.get(i);

				if (!_kbTemplateModelResourcePermission.contains(
						getPermissionChecker(), curKBTemplate,
						KBActionKeys.VIEW)) {

					continue;
				}

				if (kbTemplates.size() == delta) {
					curStartValue = start + i;

					break;
				}

				kbTemplates.add(curKBTemplate);
			}

			start = start + _INTERVAL;

			end = start + _INTERVAL;
		}

		int total = ((cur - 1) * delta) + kbTemplates.size();

		if (curStartValue > 0) {
			curStartValues = ArrayUtil.append(curStartValues, curStartValue);

			total = total + 1;
		}

		return new KBTemplateSearchDisplayImpl(
			kbTemplates, total, curStartValues);
	}

	@Override
	public KBTemplate updateKBTemplate(
			long kbTemplateId, String title, String content,
			ServiceContext serviceContext)
		throws PortalException {

		_kbTemplateModelResourcePermission.check(
			getPermissionChecker(), kbTemplateId, KBActionKeys.UPDATE);

		return kbTemplateLocalService.updateKBTemplate(
			kbTemplateId, title, content, serviceContext);
	}

	private static final int _INTERVAL = 200;

	@Reference(
		target = "(resource.name=" + KBConstants.RESOURCE_NAME_ADMIN + ")"
	)
	private PortletResourcePermission _adminPortletResourcePermission;

	@Reference(
		target = "(resource.name=" + KBConstants.RESOURCE_NAME_DISPLAY + ")"
	)
	private PortletResourcePermission _displayPortletResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBTemplate)"
	)
	private ModelResourcePermission<KBTemplate>
		_kbTemplateModelResourcePermission;

}