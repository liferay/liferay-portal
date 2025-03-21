/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.portlet.PortletLayoutListenerException;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import jakarta.portlet.PortletPreferences;

import java.util.LinkedHashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
@Component(
	property = "jakarta.portlet.name=" + JournalContentPortletKeys.JOURNAL_CONTENT,
	service = PortletLayoutListener.class
)
public class JournalContentPortletLayoutListener
	implements PortletLayoutListener {

	@Override
	public void onAddToLayout(String portletId, long plid)
		throws PortletLayoutListenerException {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat("Add ", portletId, " to layout ", plid));
		}

		try {
			Layout layout = _layoutLocalService.getLayout(plid);

			JournalArticle article = _getArticle(layout, portletId);

			if (article == null) {
				return;
			}

			_addLayoutClassedModelUsage(layout, portletId, article);
		}
		catch (Exception exception) {
			throw new PortletLayoutListenerException(exception);
		}
	}

	@Override
	public void onMoveInLayout(String portletId, long plid)
		throws PortletLayoutListenerException {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat("Move ", portletId, " from in ", plid));
		}
	}

	@Override
	public void onRemoveFromLayout(String portletId, long plid)
		throws PortletLayoutListenerException {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Remove ", portletId, " from layout ", plid));
		}

		try {
			Layout layout = _layoutLocalService.getLayout(plid);

			JournalArticle article = _getArticle(layout, portletId);

			if (article == null) {
				return;
			}

			_layoutClassedModelUsageLocalService.deleteLayoutClassedModelUsages(
				portletId, _portal.getClassNameId(Portlet.class), plid);

			String[] runtimePortletIds = _getRuntimePortletIds(
				layout.getCompanyId(), layout.getGroupId(),
				article.getArticleId());

			if (runtimePortletIds.length > 0) {
				_portletLocalService.deletePortlets(
					layout.getCompanyId(), runtimePortletIds, layout.getPlid());
			}
		}
		catch (Exception exception) {
			throw new PortletLayoutListenerException(exception);
		}
	}

	@Override
	public void onSetup(String portletId, long plid)
		throws PortletLayoutListenerException {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Setup ", portletId, " from layout ", plid));
		}

		try {
			Layout layout = _layoutLocalService.getLayout(plid);

			_layoutClassedModelUsageLocalService.deleteLayoutClassedModelUsages(
				portletId, _portal.getClassNameId(Portlet.class), plid);

			JournalArticle article = _getArticle(layout, portletId);

			if (article != null) {
				_addLayoutClassedModelUsage(layout, portletId, article);
			}
		}
		catch (Exception exception) {
			throw new PortletLayoutListenerException(exception);
		}
	}

	@Override
	public void updatePropertiesOnRemoveFromLayout(
			String portletId, UnicodeProperties typeSettingsUnicodeProperties)
		throws PortletLayoutListenerException {
	}

	private void _addLayoutClassedModelUsage(
		Layout layout, String portletId, JournalArticle article) {

		LayoutClassedModelUsage layoutClassedModelUsage =
			_layoutClassedModelUsageLocalService.fetchLayoutClassedModelUsage(
				layout.getGroupId(),
				_portal.getClassNameId(JournalArticle.class),
				article.getResourcePrimKey(), StringPool.BLANK, portletId,
				_portal.getClassNameId(Portlet.class), layout.getPlid());

		if (layoutClassedModelUsage != null) {
			return;
		}

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			layout.getGroupId(), _portal.getClassNameId(JournalArticle.class),
			article.getResourcePrimKey(), StringPool.BLANK, portletId,
			_portal.getClassNameId(Portlet.class), layout.getPlid(),
			ServiceContextThreadLocal.getServiceContext());
	}

	private JournalArticle _getArticle(Layout layout, String portletId) {
		PortletPreferences portletPreferences = null;

		if (layout.isPortletEmbedded(portletId, layout.getGroupId())) {
			portletPreferences =
				PortletPreferencesFactoryUtil.getLayoutPortletSetup(
					layout.getCompanyId(), layout.getGroupId(),
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
					PortletKeys.PREFS_PLID_SHARED, portletId, null);
		}
		else {
			portletPreferences = PortletPreferencesFactoryUtil.getPortletSetup(
				layout, portletId, StringPool.BLANK);
		}

		if (portletPreferences == null) {
			return null;
		}

		String groupExternalReferenceCode = GetterUtil.getString(
			portletPreferences.getValue("groupExternalReferenceCode", null));

		if (Validator.isNull(groupExternalReferenceCode)) {
			return null;
		}

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			groupExternalReferenceCode, layout.getCompanyId());

		long groupId = 0;

		if (group != null) {
			groupId = group.getGroupId();
		}

		if (groupId <= 0) {
			return null;
		}

		String articleExternalReferenceCode = portletPreferences.getValue(
			"articleExternalReferenceCode", null);

		if (articleExternalReferenceCode == null) {
			return null;
		}

		return _journalArticleLocalService.
			fetchLatestArticleByExternalReferenceCode(
				groupId, articleExternalReferenceCode);
	}

	private String _getRuntimePortletId(String xml) throws Exception {
		Document document = SAXReaderUtil.read(xml);

		Element rootElement = document.getRootElement();

		String portletName = rootElement.attributeValue("name");
		String instanceId = rootElement.attributeValue("instance");

		return PortletIdCodec.encode(portletName, 0, instanceId);
	}

	private String[] _getRuntimePortletIds(
			long companyId, long scopeGroupId, String articleId)
		throws Exception {

		JournalArticle article =
			_journalArticleLocalService.fetchDisplayArticle(
				scopeGroupId, articleId);

		if (article == null) {
			Group group = _groupLocalService.fetchGroup(companyId);

			if (group == null) {
				return new String[0];
			}

			article = _journalArticleLocalService.fetchDisplayArticle(
				group.getGroupId(), articleId);

			if (article == null) {
				return new String[0];
			}
		}

		Set<String> portletIds = _getRuntimePortletIds(article.getContent());

		if (Validator.isNotNull(article.getDDMTemplateKey())) {
			DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
				scopeGroupId, _portal.getClassNameId(DDMStructure.class),
				article.getDDMTemplateKey(), true);

			if (ddmTemplate != null) {
				portletIds.addAll(
					_getRuntimePortletIds(ddmTemplate.getScript()));
			}
		}

		return portletIds.toArray(new String[0]);
	}

	private Set<String> _getRuntimePortletIds(String content) throws Exception {
		Set<String> portletIds = new LinkedHashSet<>();

		for (int index = 0;;) {
			index = content.indexOf(_OPEN_TAG, index);

			if (index == -1) {
				break;
			}

			int close1 = content.indexOf(_CLOSE_1_TAG, index);
			int close2 = content.indexOf(_CLOSE_2_TAG, index);

			int closeIndex = -1;

			if ((close2 == -1) || ((close1 != -1) && (close1 < close2))) {
				closeIndex = close1 + _CLOSE_1_TAG.length();
			}
			else {
				closeIndex = close2 + _CLOSE_2_TAG.length();
			}

			if (closeIndex == -1) {
				break;
			}

			portletIds.add(
				_getRuntimePortletId(content.substring(index, closeIndex)));

			index = closeIndex;
		}

		return portletIds;
	}

	private static final String _CLOSE_1_TAG = "</runtime-portlet>";

	private static final String _CLOSE_2_TAG = "/>";

	private static final String _OPEN_TAG = "<runtime-portlet";

	private static final Log _log = LogFactoryUtil.getLog(
		JournalContentPortletLayoutListener.class);

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

}