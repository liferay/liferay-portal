/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPSubscriptionInfo;
import com.liferay.commerce.product.service.CPInstanceLocalServiceUtil;
import com.liferay.commerce.product.util.CPSubscriptionType;
import com.liferay.commerce.product.util.CPSubscriptionTypeRegistry;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Alessio Antonio Rendina
 * @author Luca Pellizzon
 */
public class ProductSubscriptionInfoTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		try {
			CPInstance cpInstance = CPInstanceLocalServiceUtil.fetchCPInstance(
				_cpInstanceId);

			if (cpInstance == null) {
				return SKIP_BODY;
			}

			CPSubscriptionInfo cpSubscriptionInfo =
				cpInstance.getCPSubscriptionInfo();

			if (cpSubscriptionInfo == null) {
				return SKIP_BODY;
			}

			CPDefinition cpDefinition = cpInstance.getCPDefinition();

			if (cpDefinition.isSubscriptionEnabled() ||
				cpInstance.isSubscriptionEnabled()) {

				_length = cpSubscriptionInfo.getSubscriptionLength();

				_duration =
					_length * cpSubscriptionInfo.getMaxSubscriptionCycles();

				String subscriptionType =
					cpSubscriptionInfo.getSubscriptionType();

				String period = StringPool.BLANK;

				CPSubscriptionType cpSubscriptionType =
					cpSubscriptionTypeRegistry.getCPSubscriptionType(
						subscriptionType);

				if (cpSubscriptionType != null) {
					period = cpSubscriptionType.getLabel(LocaleUtil.US);
				}

				_subscriptionPeriodKey = _getPeriodKey(period, _length != 1);

				_durationPeriodKey = _getPeriodKey(period, _duration != 1);
			}

			if (cpDefinition.isDeliverySubscriptionEnabled() ||
				cpInstance.isDeliverySubscriptionEnabled()) {

				_deliveryLength =
					cpSubscriptionInfo.getDeliverySubscriptionLength();

				_deliveryDuration =
					_deliveryLength *
						cpSubscriptionInfo.getDeliveryMaxSubscriptionCycles();

				String deliverySubscriptionType =
					cpSubscriptionInfo.getDeliverySubscriptionType();

				String deliveryPeriod = StringPool.BLANK;

				CPSubscriptionType cpDeliverySubscriptionType =
					cpSubscriptionTypeRegistry.getCPSubscriptionType(
						deliverySubscriptionType);

				if (cpDeliverySubscriptionType != null) {
					deliveryPeriod = cpDeliverySubscriptionType.getLabel(
						LocaleUtil.US);
				}

				_deliverySubscriptionPeriodKey = _getPeriodKey(
					deliveryPeriod, _deliveryLength != 1);

				_deliveryDurationPeriodKey = _getPeriodKey(
					deliveryPeriod, _deliveryDuration != 1);
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return SKIP_BODY;
		}

		HttpServletRequest httpServletRequest = getRequest();

		if (_showDuration && (_duration > 0)) {
			_durationPeriod = LanguageUtil.format(
				httpServletRequest, "duration-x-x",
				new Object[] {_duration, _durationPeriodKey});
		}

		if (_deliveryShowDuration && (_deliveryDuration > 0)) {
			_deliveryDurationPeriod = LanguageUtil.format(
				httpServletRequest, "duration-x-x",
				new Object[] {_deliveryDuration, _deliveryDurationPeriodKey});
		}

		if ((_length > 0) && Validator.isNotNull(_subscriptionPeriodKey)) {
			_subscriptionPeriod = LanguageUtil.format(
				httpServletRequest, "every-x-x",
				new Object[] {_length, _subscriptionPeriodKey});
		}

		if ((_deliveryLength > 0) &&
			Validator.isNotNull(_deliverySubscriptionPeriodKey)) {

			_deliverySubscriptionPeriod = LanguageUtil.format(
				httpServletRequest, "every-x-x",
				new Object[] {_deliveryLength, _deliverySubscriptionPeriodKey});
		}

		return super.doStartTag();
	}

	public long getCPInstanceId() {
		return _cpInstanceId;
	}

	public boolean isShowDuration() {
		return _showDuration;
	}

	public void setCPInstanceId(long cpInstanceId) {
		_cpInstanceId = cpInstanceId;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());

		cpSubscriptionTypeRegistry =
			ServletContextUtil.getCPSubscriptionTypeRegistry();
	}

	public void setShowDuration(boolean showDuration) {
		_showDuration = showDuration;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cpInstanceId = 0;
		_deliveryDuration = 0;
		_deliveryDurationPeriod = null;
		_deliveryDurationPeriodKey = null;
		_deliveryLength = 0;
		_deliveryShowDuration = true;
		_deliverySubscriptionPeriod = null;
		_deliverySubscriptionPeriodKey = null;
		_duration = 0;
		_durationPeriod = null;
		_durationPeriodKey = null;
		_length = 0;
		_showDuration = true;
		_subscriptionPeriod = null;
		_subscriptionPeriodKey = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-commerce:subscription-info:deliveryDurationPeriod",
			_deliveryDurationPeriod);
		httpServletRequest.setAttribute(
			"liferay-commerce:subscription-info:deliverySubscriptionPeriod",
			_deliverySubscriptionPeriod);
		httpServletRequest.setAttribute(
			"liferay-commerce:subscription-info:durationPeriod",
			_durationPeriod);
		httpServletRequest.setAttribute(
			"liferay-commerce:subscription-info:subscriptionPeriod",
			_subscriptionPeriod);
	}

	protected CPSubscriptionTypeRegistry cpSubscriptionTypeRegistry;

	private String _getPeriodKey(String period, boolean plural) {
		HttpServletRequest httpServletRequest = getRequest();

		if (plural) {
			return LanguageUtil.get(
				httpServletRequest,
				StringUtil.toLowerCase(period + CharPool.LOWER_CASE_S));
		}

		return LanguageUtil.get(httpServletRequest, period);
	}

	private static final String _PAGE = "/subscription_info/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		ProductSubscriptionInfoTag.class);

	private long _cpInstanceId;
	private long _deliveryDuration;
	private String _deliveryDurationPeriod;
	private String _deliveryDurationPeriodKey;
	private int _deliveryLength;
	private boolean _deliveryShowDuration = true;
	private String _deliverySubscriptionPeriod;
	private String _deliverySubscriptionPeriodKey;
	private long _duration;
	private String _durationPeriod;
	private String _durationPeriodKey;
	private long _length;
	private boolean _showDuration = true;
	private String _subscriptionPeriod;
	private String _subscriptionPeriodKey;

}