/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.display.context;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.exception.CPDefinitionDisplayDateException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.servlet.taglib.ui.constants.CPDefinitionScreenNavigationConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.CustomAttributesUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CPDefinitionOptionValueRelDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public CPDefinitionOptionValueRelDisplayContext(
		ActionHelper actionHelper,
		CommerceCatalogLocalService commerceCatalogLocalService,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		HttpServletRequest httpServletRequest, Portal portal) {

		super(actionHelper, httpServletRequest);

		_commerceCatalogLocalService = commerceCatalogLocalService;
		_commerceCurrencyLocalService = commerceCurrencyLocalService;
		_portal = portal;
	}

	public CPInstance fetchCPInstance() throws PortalException {
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			getCPDefinitionOptionValueRel();

		if (cpDefinitionOptionValueRel == null) {
			return null;
		}

		return cpDefinitionOptionValueRel.fetchCPInstance();
	}

	public Calendar getCalendar() throws PortalException {
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			getCPDefinitionOptionValueRel();

		if (cpDefinitionOptionValueRel == null) {
			return CalendarFactoryUtil.getCalendar();
		}

		String cpDefinitionOptionValueRelKey =
			cpDefinitionOptionValueRel.getKey();

		String[] splits = cpDefinitionOptionValueRelKey.split(StringPool.DASH);

		Integer month = Integer.valueOf(splits[0]);
		Integer day = Integer.valueOf(splits[1]);
		Integer year = Integer.valueOf(splits[2]);
		Integer hour = Integer.valueOf(splits[3]);
		Integer minute = Integer.valueOf(splits[4]);

		TimeZone timeZone = TimeZoneUtil.getTimeZone(_getTimeZone(splits));

		Date optionValueDate = _portal.getDate(
			month - 1, day, year, hour, minute, timeZone,
			CPDefinitionDisplayDateException.class);

		return CalendarFactoryUtil.getCalendar(
			optionValueDate.getTime(), timeZone);
	}

	public CommerceCurrency getCommerceCurrency() throws PortalException {
		CPDefinition cpDefinition = getCPDefinition();

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(
				cpDefinition.getGroupId());

		return _commerceCurrencyLocalService.getCommerceCurrency(
			commerceCatalog.getCompanyId(),
			commerceCatalog.getCommerceCurrencyCode());
	}

	public String getComposedCPInstanceId(
		CPInstance cpInstance, String unitOfMeasureKey) {

		if (Validator.isNotNull(unitOfMeasureKey)) {
			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
				cpInstance.fetchCPInstanceUnitOfMeasure(unitOfMeasureKey);

			if (cpInstanceUnitOfMeasure != null) {
				return StringBundler.concat(
					cpInstance.getCPInstanceId(), StringPool.DASH,
					cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId());
			}
		}

		return String.valueOf(cpInstance.getCPInstanceId());
	}

	public CPDefinitionOptionRel getCPDefinitionOptionRel()
		throws PortalException {

		if (_cpDefinitionOptionRel != null) {
			return _cpDefinitionOptionRel;
		}

		_cpDefinitionOptionRel = actionHelper.getCPDefinitionOptionRel(
			cpRequestHelper.getRenderRequest());

		return _cpDefinitionOptionRel;
	}

	public long getCPDefinitionOptionRelId() throws PortalException {
		CPDefinitionOptionRel cpDefinitionOptionRel =
			getCPDefinitionOptionRel();

		if (cpDefinitionOptionRel == null) {
			return 0;
		}

		return cpDefinitionOptionRel.getCPDefinitionOptionRelId();
	}

	public CPDefinitionOptionValueRel getCPDefinitionOptionValueRel()
		throws PortalException {

		return actionHelper.getCPDefinitionOptionValueRel(
			cpRequestHelper.getRenderRequest());
	}

	public long getCPDefinitionOptionValueRelId() throws PortalException {
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			getCPDefinitionOptionValueRel();

		if (cpDefinitionOptionValueRel == null) {
			return 0;
		}

		return cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId();
	}

	public int getDuration() throws PortalException {
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			getCPDefinitionOptionValueRel();

		if (cpDefinitionOptionValueRel == null) {
			return 0;
		}

		String cpDefinitionOptionValueRelKey =
			cpDefinitionOptionValueRel.getKey();

		String[] splits = cpDefinitionOptionValueRelKey.split(StringPool.DASH);

		return Integer.valueOf(splits[5]);
	}

	public String getDurationType() throws PortalException {
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			getCPDefinitionOptionValueRel();

		if (cpDefinitionOptionValueRel == null) {
			return CPConstants.HOURS_DURATION_TYPE;
		}

		String cpDefinitionOptionValueRelKey =
			cpDefinitionOptionValueRel.getKey();

		String[] splits = cpDefinitionOptionValueRelKey.split(StringPool.DASH);

		return splits[6];
	}

	public String getRemoveSkuUrl(String redirect) throws PortalException {
		return PortletURLBuilder.createActionURL(
			liferayPortletResponse
		).setActionName(
			"/cp_definitions/edit_cp_definition_option_value_rel"
		).setCMD(
			"deleteSku"
		).setRedirect(
			redirect
		).setParameter(
			"cpDefinitionOptionValueRelId", getCPDefinitionOptionValueRelId()
		).buildString();
	}

	@Override
	public String getScreenNavigationCategoryKey() {
		return CPDefinitionScreenNavigationConstants.CATEGORY_KEY_OPTIONS;
	}

	public TimeZone getTimeZone() throws PortalException {
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			getCPDefinitionOptionValueRel();

		if (cpDefinitionOptionValueRel == null) {
			return TimeZoneUtil.getDefault();
		}

		String cpDefinitionOptionValueRelKey =
			cpDefinitionOptionValueRel.getKey();

		String[] splits = cpDefinitionOptionValueRelKey.split(StringPool.DASH);

		return TimeZoneUtil.getTimeZone(_getTimeZone(splits));
	}

	public boolean hasCustomAttributesAvailable() throws Exception {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return CustomAttributesUtil.hasCustomAttributes(
			themeDisplay.getCompanyId(),
			CPDefinitionOptionValueRel.class.getName(),
			getCPDefinitionOptionValueRelId(), null);
	}

	public boolean isCPDefinitionOptionRelSelectDate() throws PortalException {
		CPDefinitionOptionRel cpDefinitionOptionRel =
			getCPDefinitionOptionRel();

		return Objects.equals(
			CPConstants.PRODUCT_OPTION_SELECT_DATE_KEY,
			cpDefinitionOptionRel.getCommerceOptionTypeKey());
	}

	private String _getTimeZone(String[] splits) {
		if (splits.length <= 8) {
			return splits[7].toUpperCase();
		}

		String timeZone = StringBundler.concat(
			StringUtil.upperCaseFirstLetter(splits[7]),
			StringPool.FORWARD_SLASH,
			StringUtil.upperCaseFirstLetter(splits[8]));

		if ((splits.length > 9) && Validator.isNotNull(splits[9])) {
			return StringBundler.concat(
				timeZone, StringPool.UNDERLINE,
				StringUtil.upperCaseFirstLetter(splits[9]));
		}

		return timeZone;
	}

	private final CommerceCatalogLocalService _commerceCatalogLocalService;
	private final CommerceCurrencyLocalService _commerceCurrencyLocalService;
	private CPDefinitionOptionRel _cpDefinitionOptionRel;
	private final Portal _portal;

}