/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.impl;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollectionTemplate;
import com.liferay.change.tracking.model.CTCollectionTemplateTable;
import com.liferay.change.tracking.service.base.CTCollectionTemplateServiceBaseImpl;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=ct",
		"json.web.service.context.path=CTCollectionTemplate"
	},
	service = AopService.class
)
public class CTCollectionTemplateServiceImpl
	extends CTCollectionTemplateServiceBaseImpl {

	@Override
	public CTCollectionTemplate addCTCollectionTemplate(
			String name, String description, String json)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null, CTActionKeys.ADD_PUBLICATION);

		return ctCollectionTemplateLocalService.addCTCollectionTemplate(
			getUserId(), name, description, json);
	}

	@Override
	public List<CTCollectionTemplate> getCTCollectionTemplates(
		String keywords, int start, int end,
		OrderByComparator<CTCollectionTemplate> orderByComparator) {

		String[] keywordsArray = _customSQL.keywords(
			keywords, true, WildcardMode.SURROUND);

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			CTCollectionTemplateTable.INSTANCE
		).from(
			CTCollectionTemplateTable.INSTANCE
		).where(
			CTCollectionTemplateTable.INSTANCE.companyId.eq(
				CompanyThreadLocal.getCompanyId()
			).and(
				Predicate.or(
					_customSQL.getKeywordsPredicate(
						DSLFunctionFactoryUtil.lower(
							CTCollectionTemplateTable.INSTANCE.name),
						keywordsArray),
					_customSQL.getKeywordsPredicate(
						DSLFunctionFactoryUtil.lower(
							CTCollectionTemplateTable.INSTANCE.description),
						keywordsArray))
			)
		).orderBy(
			CTCollectionTemplateTable.INSTANCE, orderByComparator
		).limit(
			start, end
		);

		return ctCollectionTemplatePersistence.dslQuery(dslQuery);
	}

	@Override
	public int getCTCollectionTemplatesCount(String keywords) {
		String[] keywordsArray = _customSQL.keywords(
			keywords, true, WildcardMode.SURROUND);

		DSLQuery dslQuery = DSLQueryFactoryUtil.count(
		).from(
			CTCollectionTemplateTable.INSTANCE
		).where(
			CTCollectionTemplateTable.INSTANCE.companyId.eq(
				CompanyThreadLocal.getCompanyId()
			).and(
				Predicate.or(
					_customSQL.getKeywordsPredicate(
						DSLFunctionFactoryUtil.lower(
							CTCollectionTemplateTable.INSTANCE.name),
						keywordsArray),
					_customSQL.getKeywordsPredicate(
						DSLFunctionFactoryUtil.lower(
							CTCollectionTemplateTable.INSTANCE.description),
						keywordsArray))
			)
		);

		return ctCollectionTemplatePersistence.dslQueryCount(dslQuery);
	}

	@Override
	public CTCollectionTemplate updateCTCollectionTemplate(
			long ctCollectionTemplateId, String name, String description,
			String json)
		throws PortalException {

		_ctCollectionTemplateModelResourcePermission.check(
			getPermissionChecker(), ctCollectionTemplateId, ActionKeys.UPDATE);

		return ctCollectionTemplateLocalService.updateCTCollectionTemplate(
			ctCollectionTemplateId, name, description, json);
	}

	@Reference(
		target = "(model.class.name=com.liferay.change.tracking.model.CTCollectionTemplate)"
	)
	private ModelResourcePermission<CTCollectionTemplate>
		_ctCollectionTemplateModelResourcePermission;

	@Reference
	private CustomSQL _customSQL;

	@Reference(target = "(resource.name=" + CTConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

}