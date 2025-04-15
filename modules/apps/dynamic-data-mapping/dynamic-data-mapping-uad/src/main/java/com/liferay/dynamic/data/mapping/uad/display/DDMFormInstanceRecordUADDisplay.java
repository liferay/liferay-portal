/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.uad.display;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.uad.util.DDMUADUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.user.associated.data.display.UADDisplay;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = UADDisplay.class)
public class DDMFormInstanceRecordUADDisplay
	extends BaseDDMFormInstanceRecordUADDisplay {

	@Override
	public String getEditURL(
			DDMFormInstanceRecord ddmFormInstanceRecord,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		HttpServletRequest httpServletRequest =
			liferayPortletRequest.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String portletNamespace = _portal.getPortletNamespace(
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM);

		DDMFormValues ddmFormValues = ddmFormInstanceRecord.getDDMFormValues();

		return _portal.getSiteAdminURL(
			themeDisplay, DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM,
			HashMapBuilder.put(
				portletNamespace.concat("defaultLanguageId"),
				new String[] {
					LocaleUtil.toLanguageId(ddmFormValues.getDefaultLocale())
				}
			).put(
				portletNamespace.concat("formInstanceId"),
				new String[] {
					String.valueOf(ddmFormInstanceRecord.getFormInstanceId())
				}
			).put(
				portletNamespace.concat("formInstanceRecordId"),
				new String[] {
					String.valueOf(
						ddmFormInstanceRecord.getFormInstanceRecordId())
				}
			).put(
				portletNamespace.concat("mvcPath"),
				new String[] {"/display/edit_form_instance_record.jsp"}
			).put(
				portletNamespace.concat("redirect"),
				new String[] {_portal.getCurrentURL(httpServletRequest)}
			).put(
				portletNamespace.concat("title"),
				new String[] {
					StringBundler.concat(
						ddmFormInstanceRecord.getUserName(), " - ",
						_language.get(
							httpServletRequest, "personal-data-erasure"))
				}
			).build());
	}

	@Override
	public Map<String, Object> getFieldValues(
		DDMFormInstanceRecord ddmFormInstanceRecord, String[] fieldNames,
		Locale locale) {

		Map<String, Object> fieldValues = super.getFieldValues(
			ddmFormInstanceRecord, fieldNames, locale);

		DDMUADUtil.formatCreateDate(fieldValues);

		return fieldValues;
	}

	@Override
	public String getName(
		DDMFormInstanceRecord ddmFormInstanceRecord, Locale locale) {

		try {
			StringBundler sb = new StringBundler(6);

			DDMFormInstance ddmFormInstance =
				ddmFormInstanceRecord.getFormInstance();

			sb.append(DDMUADUtil.getFormattedName(ddmFormInstance));

			sb.append(StringPool.SPACE);
			sb.append(_language.get(locale, "record"));
			sb.append(StringPool.SPACE);
			sb.append(StringPool.POUND);
			sb.append(_getIndex(ddmFormInstanceRecord) + 1);

			return sb.toString();
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return StringPool.BLANK;
	}

	@Override
	public Class<?> getParentContainerClass() {
		return DDMFormInstance.class;
	}

	@Override
	public Serializable getParentContainerId(
		DDMFormInstanceRecord ddmFormInstanceRecord) {

		return ddmFormInstanceRecord.getFormInstanceId();
	}

	@Override
	public boolean isUserOwned(
		DDMFormInstanceRecord ddmFormInstanceRecord, long userId) {

		if (ddmFormInstanceRecord.getUserId() == userId) {
			return true;
		}

		return false;
	}

	@Override
	public List<DDMFormInstanceRecord> search(
		long userId, long[] groupIds, String keywords, String orderByField,
		String orderByType, int start, int end) {

		List<DDMFormInstanceRecord> ddmFormInstanceRecords = new ArrayList<>();

		ddmFormInstanceRecords.addAll(
			super.search(
				userId, groupIds, StringPool.BLANK, orderByField, orderByType,
				start, end));

		if (Validator.isNull(keywords)) {
			return ddmFormInstanceRecords;
		}

		return ListUtil.filter(
			ddmFormInstanceRecords,
			ddmFormInstanceRecord -> {
				String lowerCaseFormattedName = StringUtil.toLowerCase(
					getName(
						ddmFormInstanceRecord,
						LocaleThreadLocal.getThemeDisplayLocale()));

				return lowerCaseFormattedName.contains(
					StringUtil.toLowerCase(keywords));
			});
	}

	private List<DDMFormInstanceRecord> _getDDMFormInstanceRecords(
		long formInstanceId, long userId) {

		DDMFormInstanceRecordUADUserCache ddmFormInstanceRecordUADUserCache =
			_ddmFormInstanceRecordUADUserCacheMap.get(formInstanceId);

		if (ddmFormInstanceRecordUADUserCache == null) {
			ddmFormInstanceRecordUADUserCache =
				new DDMFormInstanceRecordUADUserCache(formInstanceId);

			ddmFormInstanceRecordUADUserCache.putDDMFormInstanceRecords(userId);

			_ddmFormInstanceRecordUADUserCacheMap.put(
				formInstanceId, ddmFormInstanceRecordUADUserCache);
		}

		return ddmFormInstanceRecordUADUserCache.getDDMFormInstanceRecords(
			userId);
	}

	private int _getIndex(DDMFormInstanceRecord ddmFormInstanceRecord) {
		int index = 0;

		List<DDMFormInstanceRecord> ddmFormInstanceRecords =
			_getDDMFormInstanceRecords(
				ddmFormInstanceRecord.getFormInstanceId(),
				ddmFormInstanceRecord.getUserId());

		for (DDMFormInstanceRecord currentDDMFormInstanceRecord :
				ddmFormInstanceRecords) {

			if (currentDDMFormInstanceRecord.getFormInstanceRecordId() ==
					ddmFormInstanceRecord.getFormInstanceRecordId()) {

				return index;
			}

			index++;
		}

		return -1;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormInstanceRecordUADDisplay.class);

	private final Map<Long, DDMFormInstanceRecordUADUserCache>
		_ddmFormInstanceRecordUADUserCacheMap = new HashMap<>();

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private class DDMFormInstanceRecordUADUserCache {

		public DDMFormInstanceRecordUADUserCache(long formInstanceId) {
			_formInstanceId = formInstanceId;
		}

		public List<DDMFormInstanceRecord> getDDMFormInstanceRecords(
			long userId) {

			if (_ddmFormInstanceRecordUADUserMap.get(userId) == null) {
				putDDMFormInstanceRecords(userId);
			}

			return _ddmFormInstanceRecordUADUserMap.get(userId);
		}

		public void putDDMFormInstanceRecords(long userId) {
			_ddmFormInstanceRecordUADUserMap.put(
				userId,
				ListUtil.sort(
					ddmFormInstanceRecordLocalService.getFormInstanceRecords(
						_formInstanceId, userId, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS, null),
					Comparator.comparing(
						DDMFormInstanceRecord::getCreateDate)));
		}

		private final Map<Long, List<DDMFormInstanceRecord>>
			_ddmFormInstanceRecordUADUserMap = new HashMap<>();
		private final long _formInstanceId;

	}

}